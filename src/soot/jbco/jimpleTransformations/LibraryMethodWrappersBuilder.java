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

import java.util.*;

import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.jbco.IJbcoTransform;
import soot.jbco.util.*;

/**
 * @author Michael Batchelder
 * 
 * Created on 7-Feb-2006
 */
public class LibraryMethodWrappersBuilder extends SceneTransformer  implements IJbcoTransform {

  public static String dependancies[] = new String[] { "wjtp.jbco_blbc" };

  public String[] getDependancies() {
    return dependancies;
  }
  
  public static String name = "wjtp.jbco_blbc";
  
  public String getName() {
    return name;
  }
  
  private static int newmethods = 0;
  private static int methodcalls = 0;
  
  public void outputSummary() {
    out.println("New Methods Created: "+newmethods);
    out.println("Method Calls Replaced: "+methodcalls);
  }
  
  private static final HashMap<SootClass, HashMap> libClassesToMethods = new HashMap<SootClass, HashMap>();

  private static final Scene scene = G.v().soot_Scene();

  public static ArrayList<SootMethod> builtByMe = new ArrayList<SootMethod>();

  protected void internalTransform(String phaseName, Map options) {
    if (output)
      out.println("Building Library Wrapper Methods...");
    soot.jbco.util.BodyBuilder.retrieveAllBodies();
    // iterate through application classes to find library calls
    Iterator it = scene.getApplicationClasses().snapshotIterator();
    while (it.hasNext()) {
      SootClass c = (SootClass) it.next();
        
      if (output) out.println("\r\tProcessing " + c.getName()+"\r");

      List mList = c.getMethods();
      for (int midx = 0; midx < mList.size(); midx++) {
        SootMethod m = (SootMethod) mList.get(midx);
        if (!m.isConcrete() || builtByMe.contains(m))
          continue;

        Body body = null;
        try {
          body = m.getActiveBody();
        } catch (Exception exc) {
          body = m.retrieveActiveBody();
        }
        if (body == null)
          continue;

        int localName = 0;
        Chain locals = body.getLocals();
        PatchingChain units = body.getUnits();
        
        Unit first = null;
        Iterator uIt = units.snapshotIterator();
        while (uIt.hasNext()) {
          Unit unit = (Unit)uIt.next();
          if (unit instanceof IdentityStmt)
            continue;
          first = unit;
          break;
        }
        
        uIt = units.snapshotIterator();
        while (uIt.hasNext()) {
          Unit unit = (Unit) uIt.next();
          List uses = unit.getUseBoxes();
          for (int i = 0; i < uses.size(); i++) {
            ValueBox vb = (ValueBox) uses.get(i);
            Value v = vb.getValue();
            if (!(v instanceof InvokeExpr))
              continue;

            InvokeExpr ie = (InvokeExpr) v;
            SootMethod sm = null;
            try {
              sm = ie.getMethod();
            } catch (RuntimeException exc) {
            }
            SootClass dc = sm.getDeclaringClass();
            if (sm.getName().endsWith("init>") || !dc.isLibraryClass())
              continue;

            if (output) out.print("\t\t\tChanging " + sm.getSignature());
            
            SootMethodRef smr = getNewMethodRef(dc, sm);
            if (smr == null) {
              try {
                smr = buildNewMethod(c, dc, sm, ie);
                setNewMethodRef(dc, sm, smr);
                newmethods++;
              } catch(Exception exc) {
                smr = null;
              }
            }
            if (smr == null)
              continue;

            if (output) out.println(" to " + smr.getSignature() + "\tUnit: " + unit);
            
            List args = ie.getArgs();
            List parms = smr.parameterTypes();
            int argsCount = args.size();
            int paramCount = parms.size();

            if (ie instanceof StaticInvokeExpr) {
              while (argsCount < paramCount) {
                Type pType = (Type) parms.get(argsCount);
                Local newLocal = Jimple.v().newLocal("newLocal" + localName++,
                    pType);
                locals.add(newLocal);
                units.insertBeforeNoRedirect(Jimple.v().newAssignStmt(newLocal,
                    getConstantType(pType)),first);
                args.add(newLocal);
                argsCount++;
              }
              vb.setValue(Jimple.v().newStaticInvokeExpr(smr, args));
            } else if (ie instanceof InstanceInvokeExpr) {
              argsCount++;
              args.add(((InstanceInvokeExpr) ie).getBase());

              while (argsCount < paramCount) {
                Type pType = (Type) parms.get(argsCount);
                Local newLocal = Jimple.v().newLocal("newLocal" + localName++,
                    pType);
                locals.add(newLocal);
                units.insertBeforeNoRedirect(Jimple.v().newAssignStmt(newLocal,
                    getConstantType(pType)),first);
                args.add(newLocal);
                argsCount++;
              }
              vb.setValue(Jimple.v().newStaticInvokeExpr(smr, args));
            }
            methodcalls++;
          }
        }
      }
    }
    
    scene.releaseActiveHierarchy();
    scene.getActiveHierarchy();
    scene.setFastHierarchy(new FastHierarchy());
  }

  private SootMethodRef getNewMethodRef(SootClass libClass, SootMethod sm) {
    HashMap methods = libClassesToMethods.get(libClass);
    if (methods == null) {
      libClassesToMethods.put(libClass, new HashMap());
      return null;
    }

    return (SootMethodRef) methods.get(sm);
  }

  private void setNewMethodRef(SootClass libClass, SootMethod sm,
      SootMethodRef smr) {
    HashMap<SootMethod, SootMethodRef> methods = libClassesToMethods.get(libClass);
    if (methods == null) {
      libClassesToMethods.put(libClass, new HashMap());
    }

    methods.put(sm, smr);
  }

  private SootMethodRef buildNewMethod(SootClass fromC, SootClass libClass,
      SootMethod sm, InvokeExpr origIE) 
  {
    SootClass randClass;
    List methods;
    SootMethod randMethod;
    String newName;

    Vector<SootClass> availClasses = new Vector<SootClass>();
    Iterator aIt = scene.getApplicationClasses().iterator();
    while (aIt.hasNext()) {
      SootClass c = (SootClass)aIt.next();
      if (c.isConcrete() && !c.isInterface() && c.isPublic())
        availClasses.add(c);
    }
    
    int classCount = availClasses.size();
    if (classCount==0)
        throw new RuntimeException("There appears to be no public non-interface Application classes!");
    
    do {
      int index = Rand.getInt(classCount);
      if ((randClass = availClasses.get(index)) == fromC && classCount > 1) {
        index = Rand.getInt(classCount);
        randClass = availClasses.get(index);
      }

      methods = randClass.getMethods();
      index = Rand.getInt(methods.size());
      randMethod = (SootMethod) methods.get(index);
      newName = randMethod.getName();
    } while (newName.endsWith("init>"));

    List smParamTypes = sm.getParameterTypes();
    ArrayList tmp = new ArrayList();
    if (!sm.isStatic()) {
      for (int i = 0; i < smParamTypes.size(); i++)
        tmp.add(smParamTypes.get(i));
      tmp.add(libClass.getType());
      smParamTypes = tmp;
    } else {
      tmp.addAll(smParamTypes);
      smParamTypes = tmp;
    }

    // add random class params until we don't match any other method
    int extraParams = 0;
    SootMethod similarM;
    do {
      similarM = null;
      try {
        similarM = randClass.getMethod(newName, smParamTypes);
      } catch (RuntimeException rexc) {
      }

      if (similarM != null) {
        int rtmp = Rand.getInt(classCount + 7);
        if (rtmp >= classCount) {
          rtmp -= classCount;
          smParamTypes.add(getPrimType(rtmp));
        } else {
          smParamTypes.add(availClasses.get(rtmp).getType());
        }
        extraParams++;
      }
    } while (similarM != null);

    int mods = ((((sm.getModifiers() | Modifier.STATIC | Modifier.PUBLIC)
        & (Modifier.ABSTRACT ^ 0xFFFF)) & (Modifier.NATIVE ^ 0xFFFF)) 
        & (Modifier.SYNCHRONIZED ^ 0xFFFF));
    SootMethod newMethod = new SootMethod(newName, smParamTypes, sm
        .getReturnType(), mods);
    randClass.addMethod(newMethod);

    JimpleBody body = Jimple.v().newBody(newMethod);
    newMethod.setActiveBody(body);
    PatchingChain units = body.getUnits();
    Chain locals = body.getLocals();

    InvokeExpr ie = null;
    List args = BodyBuilder.buildParameterLocals(units, locals, smParamTypes);
    while (extraParams-- > 0)
      args.remove(args.size() - 1);
    
    if (sm.isStatic()) {
      ie = Jimple.v().newStaticInvokeExpr(sm.makeRef(), args);
    } else {
      Local libObj = (Local) args.remove(args.size() - 1);
      if (origIE instanceof InterfaceInvokeExpr)
        ie = Jimple.v().newInterfaceInvokeExpr(libObj, sm.makeRef(), args);
      else if (origIE instanceof SpecialInvokeExpr)
        ie = Jimple.v().newSpecialInvokeExpr(libObj, sm.makeRef(), args);
      else if (origIE instanceof VirtualInvokeExpr)
        ie = Jimple.v().newVirtualInvokeExpr(libObj, sm.makeRef(), args);
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

    if (output)
      out.println("\r"+sm.getName()+" was replaced by \r\t"+newMethod.getName()+" which calls \r\t\t"+ie);

    if (units.size()<2) 
      out.println("\r\rTHERE AREN'T MANY UNITS IN THIS METHOD "+units);
    
    builtByMe.add(newMethod);
    
    return newMethod.makeRef();
  }
  
  private static Type getPrimType(int idx) {
    switch (idx) {
    	case 0: return IntType.v();
    	case 1: return CharType.v();
    	case 2: return ByteType.v();
    	case 3: return LongType.v();
    	case 4: return BooleanType.v();
    	case 5: return DoubleType.v();
    	case 6: return FloatType.v();
    	default: return IntType.v();
    }
  }
  
  private static Value getConstantType(Type t) {
    if (t instanceof BooleanType)
      return IntConstant.v(Rand.getInt(1));
    if (t instanceof IntType)
      return IntConstant.v(Rand.getInt());
    if (t instanceof CharType)
      return Jimple.v().newCastExpr(IntConstant.v(Rand.getInt()),CharType.v());
    if (t instanceof ByteType)
      return Jimple.v().newCastExpr(IntConstant.v(Rand.getInt()),ByteType.v());
    if (t instanceof LongType)
      return LongConstant.v(Rand.getLong());
    if (t instanceof FloatType)
      return FloatConstant.v(Rand.getFloat());
    if (t instanceof DoubleType)
      return DoubleConstant.v(Rand.getDouble());
    
    return Jimple.v().newCastExpr(NullConstant.v(),t);
  }
}