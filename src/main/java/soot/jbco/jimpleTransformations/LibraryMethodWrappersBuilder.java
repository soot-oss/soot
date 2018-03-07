/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jbco.jimpleTransformations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.Body;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FastHierarchy;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.Modifier;
import soot.PatchingChain;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.jbco.IJbcoTransform;
import soot.jbco.util.BodyBuilder;
import soot.jbco.util.Rand;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IdentityStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.util.Chain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Batchelder
 * <p>
 * Created on 7-Feb-2006
 */
public class LibraryMethodWrappersBuilder extends SceneTransformer implements IJbcoTransform {

    private static final Logger logger = LoggerFactory.getLogger(LibraryMethodWrappersBuilder.class);

    public static final String name = "wjtp.jbco_blbc";
    public static final String dependencies[] = new String[]{"wjtp.jbco_blbc"};

    private static final Map<SootClass, Map<SootMethod, SootMethodRef>> libClassesToMethods = new HashMap<>();
    public static List<SootMethod> builtByMe = new ArrayList<>();

    private int newmethods = 0;
    private int methodcalls = 0;

    @Override
    public String[] getDependencies() {
        return Arrays.copyOf(dependencies, dependencies.length);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void outputSummary() {
        logger.info("Created {} new methods. Replaced {} method calls.", newmethods, methodcalls);
    }

    protected void internalTransform(String phaseName, Map<String, String> options) {
        if (isVerbose()) {
            logger.info("Building Library Wrapper Methods...");
        }

        BodyBuilder.retrieveAllBodies();
        // iterate through application classes to find library calls
        Iterator<SootClass> it = Scene.v().getApplicationClasses().snapshotIterator();
        while (it.hasNext()) {
            SootClass sc = it.next();

            if (isVerbose()) {
                logger.info("\tProcessing class {}", sc.getName());
            }

            List<SootMethod> methods = sc.getMethods();
            // do not replace with foreach loop as it will cause java.util.ConcurrentModificationException
            for (int i = 0; i < methods.size(); i++) {
                SootMethod method = methods.get(i);
                if (!method.isConcrete() || builtByMe.contains(method)) {
                    continue;
                }

                Body body;
                try {
                    body = method.getActiveBody();
                } catch (Exception exc) {
                    body = method.retrieveActiveBody();
                }
                if (body == null) {
                    continue;
                }

                int localName = 0;
                Chain<Local> locals = body.getLocals();
                PatchingChain<Unit> units = body.getUnits();

                Unit first = null;
                Iterator<Unit> uIt = units.snapshotIterator();
                while (uIt.hasNext()) {
                    Unit unit = uIt.next();
                    if (unit instanceof IdentityStmt) {
                        continue;
                    }
                    first = unit;
                    break;
                }

                uIt = units.snapshotIterator();
                while (uIt.hasNext()) {
                    Unit unit = uIt.next();
                    List<ValueBox> uses = unit.getUseBoxes();
                    for (ValueBox valueBox : uses) {
                        Value value = valueBox.getValue();
                        // skip calls to 'super' as they cannot be called from static method and/or on object from the
                        // outside (this is prohibited on language level as that would violate encapsulation)
                        if (!(value instanceof InvokeExpr) || value instanceof SpecialInvokeExpr) {
                            continue;
                        }

                        InvokeExpr invokeExpr = (InvokeExpr) value;
                        SootMethod invokedMethod;
                        try {
                            invokedMethod = invokeExpr.getMethod();
                        } catch (RuntimeException exc) {
                            continue;
                        }
                        SootClass invokedMethodClass = invokedMethod.getDeclaringClass();
                        if (invokedMethod.getName().endsWith("init>") || !invokedMethodClass.isLibraryClass()) {
                            continue;
                        }

                        SootMethodRef invokedMethodRef = getNewMethodRef(invokedMethodClass, invokedMethod);
                        if (invokedMethodRef == null) {
                            try {
                                invokedMethodRef = buildNewMethod(sc, invokedMethodClass, invokedMethod, invokeExpr);
                                setNewMethodRef(invokedMethodClass, invokedMethod, invokedMethodRef);
                                newmethods++;
                            } catch (Exception e) {
                                continue;
                            }
                        }

                        if (isVerbose()) {
                            logger.info("\t\t\tChanging {} to {}\tUnit: ",
                                    invokedMethod.getSignature(), invokedMethodRef.getSignature(), unit);
                        }

                        List<Value> args = invokeExpr.getArgs();
                        List<Type> parameterTypes = invokedMethodRef.parameterTypes();
                        int argsCount = args.size();
                        int paramCount = parameterTypes.size();

                        if (invokeExpr instanceof InstanceInvokeExpr || invokeExpr instanceof StaticInvokeExpr) {
                            if (invokeExpr instanceof InstanceInvokeExpr) {
                                argsCount++;
                                args.add(((InstanceInvokeExpr) invokeExpr).getBase());
                            }

                            while (argsCount < paramCount) {
                                Type pType = parameterTypes.get(argsCount);
                                Local newLocal = Jimple.v().newLocal("newLocal" + localName++, pType);
                                locals.add(newLocal);
                                units.insertBeforeNoRedirect(Jimple.v().newAssignStmt(newLocal, getConstantType(pType)),
                                        first);
                                args.add(newLocal);
                                argsCount++;
                            }
                            valueBox.setValue(Jimple.v().newStaticInvokeExpr(invokedMethodRef, args));
                        }
                        methodcalls++;
                    }
                }
            }
        }

        Scene.v().releaseActiveHierarchy();
        Scene.v().getActiveHierarchy();
        Scene.v().setFastHierarchy(new FastHierarchy());
    }

    private SootMethodRef getNewMethodRef(SootClass libClass, SootMethod sm) {
        Map<SootMethod, SootMethodRef> methods = libClassesToMethods.computeIfAbsent(libClass, key -> new HashMap<>());
        return methods.get(sm);
    }

    private void setNewMethodRef(SootClass libClass, SootMethod sm, SootMethodRef smr) {
        Map<SootMethod, SootMethodRef> methods = libClassesToMethods.computeIfAbsent(libClass, key -> new HashMap<>());
        methods.put(sm, smr);
    }

    private SootMethodRef buildNewMethod(SootClass fromC, SootClass libClass, SootMethod sm, InvokeExpr origIE) {
        SootClass randClass;
        List<SootMethod> methods;
        SootMethod randMethod;
        String newName;

        List<SootClass> availableClasses = new ArrayList<>();
        for (SootClass c : Scene.v().getApplicationClasses()) {
            if (c.isConcrete() && !c.isInterface() && c.isPublic() && Scene.v().getActiveHierarchy().isVisible(c, sm)) {
                availableClasses.add(c);
            }
        }

        int classCount = availableClasses.size();
        if (classCount == 0) {
            throw new RuntimeException("There appears to be no public non-interface Application classes!");
        }

        do {
            int index = Rand.getInt(classCount);
            if ((randClass = availableClasses.get(index)) == fromC && classCount > 1) {
                index = Rand.getInt(classCount);
                randClass = availableClasses.get(index);
            }

            methods = randClass.getMethods();
            index = Rand.getInt(methods.size());
            randMethod = methods.get(index);
            newName = randMethod.getName();
        } while (newName.endsWith("init>"));

        List<Type> smParamTypes = sm.getParameterTypes();
        List<Type> tmp = new ArrayList<>();
        if (!sm.isStatic()) {
            tmp.addAll(smParamTypes);
            tmp.add(libClass.getType());
            smParamTypes = tmp;
        } else {
            tmp.addAll(smParamTypes);
            smParamTypes = tmp;
        }

        // add random class params until we don't match any other method
        int extraParams = 0;
        if (randClass.declaresMethod(newName, smParamTypes)) {
            int rtmp = Rand.getInt(classCount + 7);
            if (rtmp >= classCount) {
                rtmp -= classCount;
                smParamTypes.add(getPrimType(rtmp));
            } else {
                smParamTypes.add(availableClasses.get(rtmp).getType());
            }
            extraParams++;
        }

        int mods = ((((sm.getModifiers() | Modifier.STATIC | Modifier.PUBLIC) & (Modifier.ABSTRACT ^ 0xFFFF))
                & (Modifier.NATIVE ^ 0xFFFF)) & (Modifier.SYNCHRONIZED ^ 0xFFFF));
        SootMethod newMethod = Scene.v().makeSootMethod(newName, smParamTypes, sm.getReturnType(), mods);
        randClass.addMethod(newMethod);

        JimpleBody body = Jimple.v().newBody(newMethod);
        newMethod.setActiveBody(body);
        Chain<Local> locals = body.getLocals();
        PatchingChain<Unit> units = body.getUnits();

        InvokeExpr ie = null;
        List<Local> args = BodyBuilder.buildParameterLocals(units, locals, smParamTypes);
        while (extraParams-- > 0) {
            args.remove(args.size() - 1);
        }

        if (sm.isStatic()) {
            ie = Jimple.v().newStaticInvokeExpr(sm.makeRef(), args);
        } else {
            Local libObj = args.remove(args.size() - 1);
            if (origIE instanceof InterfaceInvokeExpr) {
                ie = Jimple.v().newInterfaceInvokeExpr(libObj, sm.makeRef(), args);
            } else if (origIE instanceof VirtualInvokeExpr) {
                ie = Jimple.v().newVirtualInvokeExpr(libObj, sm.makeRef(), args);
            }
        }
        if (sm.getReturnType() instanceof VoidType) {
            units.add(Jimple.v().newInvokeStmt(ie));
            units.add(Jimple.v().newReturnVoidStmt());
        } else {
            Local assign = Jimple.v().newLocal("returnValue", sm.getReturnType());
            locals.add(assign);
            units.add(Jimple.v().newAssignStmt(assign, ie));
            units.add(Jimple.v().newReturnStmt(assign));
        }

        if (isVerbose()) {
            logger.info("{} was replaced by {} which calls {}", sm.getName(), newMethod.getName(), ie);
        }

        if (units.size() < 2) {
            logger.warn("THERE AREN'T MANY UNITS IN THIS METHOD {}", units);
        }

        builtByMe.add(newMethod);

        return newMethod.makeRef();
    }

    private static Type getPrimType(int idx) {
        switch (idx) {
            case 0:
                return IntType.v();
            case 1:
                return CharType.v();
            case 2:
                return ByteType.v();
            case 3:
                return LongType.v();
            case 4:
                return BooleanType.v();
            case 5:
                return DoubleType.v();
            case 6:
                return FloatType.v();
            default:
                return IntType.v();
        }
    }

    private static Value getConstantType(Type t) {
        if (t instanceof BooleanType)
            return IntConstant.v(Rand.getInt(1));
        if (t instanceof IntType)
            return IntConstant.v(Rand.getInt());
        if (t instanceof CharType)
            return Jimple.v().newCastExpr(IntConstant.v(Rand.getInt()), CharType.v());
        if (t instanceof ByteType)
            return Jimple.v().newCastExpr(IntConstant.v(Rand.getInt()), ByteType.v());
        if (t instanceof LongType)
            return LongConstant.v(Rand.getLong());
        if (t instanceof FloatType)
            return FloatConstant.v(Rand.getFloat());
        if (t instanceof DoubleType)
            return DoubleConstant.v(Rand.getDouble());

        return Jimple.v().newCastExpr(NullConstant.v(), t);
    }
}
