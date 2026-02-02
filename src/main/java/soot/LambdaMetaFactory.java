package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2017 Brian Alliet Initial implementation
 * Copyright (C) 2018 Manuel Benz Bug fixes and improvements
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.asm.AsmUtil;
import soot.jimple.ClassConstant;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.MethodHandle;
import soot.jimple.MethodType;
import soot.jimple.toolkits.scalar.LocalNameStandardizer;
import soot.tagkit.ArtificialEntityTag;
import soot.util.Chain;
import soot.util.HashChain;

public class LambdaMetaFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(LambdaMetaFactory.class);

  private final Wrapper wrapper;
  private int uniq;

  public LambdaMetaFactory(Singletons.Global g) {
    uniq = 0;
    wrapper = new Wrapper();
  }

  public static LambdaMetaFactory v() {
    return G.v().soot_LambdaMetaFactory();
  }

  /**
   * @param bootstrapArgs
   * @param tag
   * @param name
   * @param invokedType
   *          types of captured arguments, the last element is always the type of the FunctionalInterface
   * @param enclosingClass
   * @return
   */
  public SootMethodRef makeLambdaHelper(List<? extends Value> bootstrapArgs, int tag, String name, Type[] invokedType,
      SootClass enclosingClass) {
    final int argsSize = bootstrapArgs.size();
    if (argsSize < 3 || !(bootstrapArgs.get(0) instanceof MethodType) || !(bootstrapArgs.get(1) instanceof MethodHandle)
        || !(bootstrapArgs.get(2) instanceof MethodType)
        || (argsSize > 3 && !(bootstrapArgs.get(3) instanceof IntConstant))) {
      LOGGER.warn("LambdaMetaFactory: unexpected arguments for LambdaMetaFactory.metaFactory: {}", bootstrapArgs);
      return null;
    }
    /** implemented method type */
    MethodType samMethodType = ((MethodType) bootstrapArgs.get(0));

    /** the MethodHandle providing the implementation */
    MethodHandle implMethod = ((MethodHandle) bootstrapArgs.get(1));
    // we might not have seen types used in the handle elsewhere yet, so let's try to resolve them
    // now
    resolveHandle(implMethod);

    /** allows restrictions on invocation */
    MethodType instantiatedMethodType = ((MethodType) bootstrapArgs.get(2));

    int flags = 0;
    if (argsSize > 3) {
      flags = ((IntConstant) bootstrapArgs.get(3)).value;
    }

    boolean serializable = (flags & 1 /* FLAGS_SERIALIZABLE */) != 0;
    List<ClassConstant> markerInterfaces = new ArrayList<ClassConstant>();
    List<MethodType> bridges = new ArrayList<MethodType>();

    int va = 4;
    if ((flags & 2 /* FLAG_MARKERS */) != 0) {
      if (va == argsSize || !(bootstrapArgs.get(va) instanceof IntConstant)) {
        LOGGER.warn("LambdaMetaFactory: unexpected arguments for LambdaMetaFactory.altMetaFactory");
        return null;
      }
      int count = ((IntConstant) bootstrapArgs.get(va++)).value;
      for (int i = 0; i < count; i++) {
        if (va >= argsSize) {
          LOGGER.warn("LambdaMetaFactory: unexpected arguments for LambdaMetaFactory.altMetaFactory");
          return null;
        }
        Value v = bootstrapArgs.get(va++);
        if (!(v instanceof ClassConstant)) {
          LOGGER.warn("LambdaMetaFactory: unexpected arguments for LambdaMetaFactory.altMetaFactory");
          return null;
        }
        markerInterfaces.add((ClassConstant) v);
      }
    }

    if ((flags & 4 /* FLAG_BRIDGES */) != 0) {
      if (va == argsSize || !(bootstrapArgs.get(va) instanceof IntConstant)) {
        LOGGER.warn("LambdaMetaFactory: unexpected arguments for LambdaMetaFactory.altMetaFactory");
        return null;
      }
      int count = ((IntConstant) bootstrapArgs.get(va++)).value;
      for (int i = 0; i < count; i++) {
        if (va >= argsSize) {
          LOGGER.warn("LambdaMetaFactory: unexpected arguments for LambdaMetaFactory.altMetaFactory");
          return null;
        }
        Value v = bootstrapArgs.get(va++);
        if (!(v instanceof MethodType)) {
          LOGGER.warn("LambdaMetaFactory: unexpected arguments for LambdaMetaFactory.altMetaFactory");
          return null;
        }
        bridges.add((MethodType) v);
      }
    }

    List<Type> capTypes = Arrays.asList(invokedType).subList(0, invokedType.length - 1);
    if (!(invokedType[invokedType.length - 1] instanceof RefType)) {
      LOGGER.warn("unexpected interface type: " + invokedType[invokedType.length - 1]);
      return null;
    }

    // Our thunk class implements the functional interface
    SootClass functionalInterfaceToImplement = ((RefType) invokedType[invokedType.length - 1]).getSootClass();

    final String enclosingClassname = enclosingClass.getName();

    String className;
    final boolean readableClassnames = true;
    if (readableClassnames) {
      // class names cannot contain <>
      String implMethodName = implMethod.getMethodRef().getName();
      String dummyName = "<init>".equals(implMethodName) ? "init" : implMethodName;
      // XXX: $ causes confusion in inner class inference; remove for now
      dummyName = dummyName.replace('$', '_');
      String prefix
          = (enclosingClassname == null || enclosingClassname.isEmpty()) ? "soot.dummy." : enclosingClassname + "$";
      className = prefix + dummyName + "__" + uniqSupply();
    } else {
      className = "soot.dummy.lambda" + uniqSupply();
    }
    SootClass tclass = Scene.v().makeSootClass(className);
    tclass.setModifiers(Modifier.PUBLIC | Modifier.FINAL);
    tclass.setSuperclass(Scene.v().getObjectType().getSootClass());
    tclass.addInterface(functionalInterfaceToImplement);
    tclass.addTag(new ArtificialEntityTag());

    // additions from altMetafactory
    if (serializable) {
      tclass.addInterface(RefType.v("java.io.Serializable").getSootClass());
    }
    for (int i = 0; i < markerInterfaces.size(); i++) {
      String internal = markerInterfaces.get(i).getValue();
      RefType refType = ((RefType) AsmUtil.toBaseType(internal, Optional.fromNullable(tclass.moduleName)));
      SootClass interfaceClass = refType.getSootClass();
      if (!interfaceClass.equals(functionalInterfaceToImplement)) {
        tclass.addInterface(interfaceClass);
      }
    }

    // It contains fields for all the captures in the lambda
    List<SootField> capFields = new ArrayList<SootField>(capTypes.size());
    for (int i = 0, e = capTypes.size(); i < e; i++) {
      SootField f = Scene.v().makeSootField("cap" + i, capTypes.get(i), 0);
      capFields.add(f);
      tclass.addField(f);
    }

    // if the implMethod is a new private static in the enclosing class, make it public access so
    // it can be invoked from the thunk class
    if (MethodHandle.Kind.REF_INVOKE_STATIC.getValue() == implMethod.getKind()) {
      SootClass declClass = implMethod.getMethodRef().getDeclaringClass();
      if (declClass.getName().equals(enclosingClassname)) {
        SootMethod method = implMethod.getMethodRef().resolve();
        method.setModifiers((method.getModifiers() & ~Modifier.PRIVATE) | Modifier.PUBLIC);
      }
    }

    MethodSource ms = new ThunkMethodSource(capFields, samMethodType, implMethod, instantiatedMethodType);

    // Bootstrap method creates a new instance of this class
    SootMethod tboot = Scene.v().makeSootMethod("bootstrap$", capTypes, functionalInterfaceToImplement.getType(),
        Modifier.PUBLIC | Modifier.STATIC);
    tclass.addMethod(tboot);
    tboot.setSource(ms);

    // Constructor just copies the captures
    SootMethod tctor = Scene.v().makeSootMethod("<init>", capTypes, VoidType.v(), Modifier.PUBLIC);
    tclass.addMethod(tctor);
    tctor.setSource(ms);

    // Dispatch runs the 'real' method implementing the body of the lambda
    addDispatch(name, tclass, samMethodType, instantiatedMethodType, capFields, implMethod);

    // For each bridge MethodType, add another dispatch method which calls the 'real' method
    for (MethodType bridgeType : bridges) {
      addDispatch(name, tclass, bridgeType, instantiatedMethodType, capFields, implMethod);
    }

    for (SootMethod m : tclass.getMethods()) {
      // There is no reason not to load the bodies directly. After all,
      // we are introducing new classes while loading bodies.
      m.retrieveActiveBody();
    }

    addClassAndInvalidateHierarchy(tclass);
    if (enclosingClass.isApplicationClass()) {
      tclass.setApplicationClass();
    }

    return tboot.makeRef();
  }

  /**
   * Invalidates the class hierarchy due to some newly added class.
   *
   * @param tclass
   */
  protected void addClassAndInvalidateHierarchy(SootClass tclass) {
    Scene.v().addClass(tclass);
    // The hierarchy has to be rebuilt after adding the MetaFactory implementation.
    // soot.FastHierarchy.canStoreClass will otherwise fail due to not having an interval set for
    // the class. This eventually
    // leads to the MetaFactory not being accepted as implementation of the functional interface it
    // actually implements.
    // This, in turn, leads to missing edges in the call graph.
    Scene.v().releaseFastHierarchy();
  }

  /**
   * Makes sure all types used in the implMethod signature are properly resolved. The method has to be synchronized since
   * body creation happens in parallel and the SootResolver is not thread-safe
   *
   * @param implMethod
   */
  private synchronized void resolveHandle(MethodHandle implMethod) {
    Scene scene = Scene.v();

    SootMethodRef methodRef = implMethod.getMethodRef();
    scene.forceResolve(methodRef.getDeclaringClass().getName(), SootClass.HIERARCHY);

    Stream.concat(Stream.of(methodRef.getReturnType()), methodRef.getParameterTypes().stream())
        .filter(t -> t instanceof RefType)
        .forEach(t -> scene.forceResolve(((RefType) t).getSootClass().getName(), SootClass.HIERARCHY));
  }

  private void addDispatch(String name, SootClass tclass, MethodType implMethodType, MethodType instantiatedMethodType,
      List<SootField> capFields, MethodHandle implMethod) {
    ThunkMethodSource ms = new ThunkMethodSource(capFields, implMethodType, implMethod, instantiatedMethodType);
    SootMethod m = Scene.v().makeSootMethod(name, implMethodType.getParameterTypes(), implMethodType.getReturnType(),
        Modifier.PUBLIC);
    tclass.addMethod(m);
    m.setSource(ms);
  }

  private synchronized long uniqSupply() {
    return ++uniq;
  }

  private static class Wrapper {

    final Map<RefType, PrimType> wrapperTypes;
    final Map<PrimType, RefType> primitiveTypes;
    /** valueOf(primitive) method signature */
    final Map<PrimType, SootMethod> valueOf;
    /** primitiveValue() method signature */
    final Map<RefType, SootMethod> primitiveValue;

    public Wrapper() {
      final Scene sc = Scene.v();
      Map<RefType, PrimType> wrapperTypesTmp = new HashMap<>();
      Map<PrimType, RefType> primitiveTypesTmp = new HashMap<>();
      Map<PrimType, SootMethod> valueOfTmp = new HashMap<>();
      Map<RefType, SootMethod> primitiveValueTmp = new HashMap<>();

      PrimType[] primTypes = { BooleanType.v(), ByteType.v(), CharType.v(), DoubleType.v(), FloatType.v(), IntType.v(),
          LongType.v(), ShortType.v() };
      for (PrimType primTy : primTypes) {
        RefType wrapTy = primTy.boxedType();

        wrapperTypesTmp.put(wrapTy, primTy);
        primitiveTypesTmp.put(primTy, wrapTy);

        SootClass wrapCls = wrapTy.getSootClass();
        valueOfTmp.put(primTy,
            sc.makeMethodRef(wrapCls, "valueOf", Collections.singletonList(primTy), wrapTy, true).resolve());

        primitiveValueTmp.put(wrapTy,
            sc.makeMethodRef(wrapCls, primTy.toString() + "Value", Collections.emptyList(), primTy, false).resolve());
      }
      this.wrapperTypes = Collections.unmodifiableMap(wrapperTypesTmp);
      this.primitiveTypes = Collections.unmodifiableMap(primitiveTypesTmp);
      this.valueOf = Collections.unmodifiableMap(valueOfTmp);
      this.primitiveValue = Collections.unmodifiableMap(primitiveValueTmp);
    }
  }

  private class ThunkMethodSource implements MethodSource {
    /**
     * fields storing capture variables, in the order they appear in invokedType; to be prepended at target invocation site
     */
    private final List<SootField> capFields;
    /**
     * MethodType of method to implemented by function object; either samMethodType or bridgeMethodType *
     */
    private final MethodType implMethodType;
    /** implMethod - the MethodHandle providing the implementation */
    private final MethodHandle implMethod;
    /** allows restrictions on invocation */
    private final MethodType instantiatedMethodType;

    public ThunkMethodSource(List<SootField> capFields, MethodType implMethodType, MethodHandle implMethod,
        MethodType instantiatedMethodType) {
      this.capFields = capFields;
      this.implMethodType = implMethodType;
      this.implMethod = implMethod;
      this.instantiatedMethodType = instantiatedMethodType;
    }

    @Override
    public Body getBody(SootMethod m, String phaseName) {
      if (!"jb".equals(phaseName)) {
        throw new Error("unsupported body type: " + phaseName);
      }

      SootClass tclass = m.getDeclaringClass();
      JimpleBody jb = Jimple.v().newBody(m);

      if (null != m.getName()) {
        switch (m.getName()) {
          case "<init>":
            getInitBody(tclass, jb);
            break;
          case "bootstrap$":
            getBootstrapBody(tclass, jb);
            break;
          default:
            getInvokeBody(tclass, jb);
            break;
        }
      }

      // rename locals consistent with JimpleBodyPack, at least when it is activated
      LocalNameStandardizer.v().transform(jb, "jb.lns", PhaseOptions.v().getPhaseOptions("jb.lns"));
      return jb;
    }

    /**
     * Thunk class init (constructor)
     *
     * @param tclass
     *          thunk class
     * @param jb
     */
    private void getInitBody(SootClass tclass, JimpleBody jb) {
      final Jimple jimp = Jimple.v();
      PatchingChain<Unit> us = jb.getUnits();
      LocalGenerator lc = Scene.v().createLocalGenerator(jb);

      // @this
      Local l = lc.generateLocal(tclass.getType());
      us.add(jimp.newIdentityStmt(l, jimp.newThisRef(tclass.getType())));

      // @parameters
      Chain<Local> capLocals = new HashChain<>();
      int i = 0;
      for (SootField f : capFields) {
        Local l2 = lc.generateLocal(f.getType());
        us.add(jimp.newIdentityStmt(l2, jimp.newParameterRef(f.getType(), i)));
        capLocals.add(l2);
        i++;
      }

      // super java.lang.Object.<init>
      us.add(jimp.newInvokeStmt(jimp.newSpecialInvokeExpr(l,
          Scene.v().makeConstructorRef(Scene.v().getObjectType().getSootClass(), Collections.emptyList()),
          Collections.emptyList())));

      // assign parameters to fields
      Iterator<Local> localItr = capLocals.iterator();
      for (SootField f : capFields) {
        Local l2 = localItr.next();
        us.add(jimp.newAssignStmt(jimp.newInstanceFieldRef(l, f.makeRef()), l2));
      }

      us.add(jimp.newReturnVoidStmt());
    }

    private void getBootstrapBody(SootClass tclass, JimpleBody jb) {
      final Jimple jimp = Jimple.v();
      PatchingChain<Unit> us = jb.getUnits();
      LocalGenerator lc = Scene.v().createLocalGenerator(jb);

      List<Value> capValues = new ArrayList<Value>();
      List<Type> capTypes = new ArrayList<Type>();
      int i = 0;
      for (SootField capField : capFields) {
        Type type = capField.getType();
        capTypes.add(type);
        Local p = lc.generateLocal(type);
        us.add(jimp.newIdentityStmt(p, jimp.newParameterRef(type, i)));
        capValues.add(p);
        i++;
      }
      Local l = lc.generateLocal(tclass.getType());
      us.add(jimp.newAssignStmt(l, jimp.newNewExpr(tclass.getType())));
      us.add(jimp.newInvokeStmt(jimp.newSpecialInvokeExpr(l, Scene.v().makeConstructorRef(tclass, capTypes), capValues)));
      us.add(jimp.newReturnStmt(l));
    }

    /**
     * Adds method which implements functional interface and invokes target implementation.
     *
     * @param tclass
     * @param jb
     */
    private void getInvokeBody(SootClass tclass, JimpleBody jb) {
      final Jimple jimp = Jimple.v();
      PatchingChain<Unit> us = jb.getUnits();
      LocalGenerator lc = Scene.v().createLocalGenerator(jb);

      // @this
      Local this_ = lc.generateLocal(tclass.getType());
      us.add(jimp.newIdentityStmt(this_, jimp.newThisRef(tclass.getType())));

      // @parameter for direct arguments
      Chain<Local> samParamLocals = new HashChain<>();
      int i = 0;
      for (Type ty : implMethodType.getParameterTypes()) {
        Local l = lc.generateLocal(ty);
        us.add(jimp.newIdentityStmt(l, jimp.newParameterRef(ty, i)));
        samParamLocals.add(l);
        i++;
      }

      // narrowing casts to match instantiatedMethodType
      Iterator<Type> iptItr = instantiatedMethodType.getParameterTypes().iterator();
      Chain<Local> instParamLocals = new HashChain<>();
      for (Local l : samParamLocals) {
        Type ipt = iptItr.next();
        instParamLocals.add(narrowingReferenceConversion(l, ipt, jb, us, lc));
      }

      // captured arguments
      List<Local> args = new ArrayList<Local>();
      for (SootField f : capFields) {
        Local l = lc.generateLocal(f.getType());
        us.add(jimp.newAssignStmt(l, jimp.newInstanceFieldRef(this_, f.makeRef())));
        args.add(l);
      }

      // direct arguments

      // The MethodHandle's first argument is the receiver, if it has one.
      // If there are no captured arguments, use the first parameter as the receiver.
      final int kind = implMethod.getKind();
      boolean needsReceiver = false;
      if (MethodHandle.Kind.REF_INVOKE_INTERFACE.getValue() == kind
          || MethodHandle.Kind.REF_INVOKE_VIRTUAL.getValue() == kind
          || MethodHandle.Kind.REF_INVOKE_SPECIAL.getValue() == kind) {
        // NOTE: for a method reference to a constructor, the receiver is not needed because it's
        // the new object
        needsReceiver = true;
      }
      Iterator<Local> iplItr = instParamLocals.iterator();
      if (capFields.isEmpty() && iplItr.hasNext() && needsReceiver) {
        args.add(adapt(iplItr.next(), implMethod.getMethodRef().getDeclaringClass().getType(), jb, us, lc));
      }

      int j = args.size();
      if (needsReceiver) {
        // assert: if there is a receiver, it is already filled, but the alignment to parameters is
        // off by 1
        j = args.size() - 1;
      }
      while (iplItr.hasNext()) {
        Local pl = iplItr.next();
        args.add(adapt(pl, implMethod.getMethodRef().getParameterType(j), jb, us, lc));
        j++;
      }

      invokeImplMethod(jb, us, lc, args);
    }

    private Local adapt(Local fromLocal, Type to, JimpleBody jb, PatchingChain<Unit> us, LocalGenerator lc) {
      Type from = fromLocal.getType();

      // Implements JLS 5.3 Method Invocation Context for adapting arguments from lambda expression
      // to formal arguments of target implementation

      // an identity conversion (§5.1.1)
      if (from.equals(to)) {
        return fromLocal;
      }

      if ((from instanceof ArrayType) || (from instanceof RefType && to instanceof RefType)) {
        return wideningReferenceConversion(fromLocal);
      }

      if (from instanceof PrimType) {
        if (to instanceof PrimType) {
          // a widening primitive conversion (§5.1.2)
          return wideningPrimitiveConversion(fromLocal, to, jb, us, lc);
        } else {
          // a boxing conversion (§5.1.7)
          // a boxing conversion followed by widening reference conversion

          // from is PrimType
          // to is RefType
          return wideningReferenceConversion(box(fromLocal, jb, us, lc));
        }
      } else {
        // an unboxing conversion (§5.1.8)
        // an unboxing conversion followed by a widening primitive conversion

        // from is RefType
        // to is PrimType
        if (!(to instanceof PrimType)) {
          throw new IllegalArgumentException("Expected 'to' to be a PrimType");
        }

        // In some cases, the wrapper type is "java.lang.Object" and we first need to cast it to a
        // type that can be unboxed. Java, e.g., seems to accept filter predicates on boxed Boolean
        // types specified through generics.
        // Code Example:
        // Map<String, Boolean> map = new HashMap<>();
        // map.entrySet().stream().filter(Map.Entry::getValue)
        // In the example, the map values are of type Object because of generic erasure, but we're
        // still dealing with
        // booleans semantically.
        // Actually, the wrapper type could be also be other types if wirdcards (?) are used in generic code.
        if (wrapper.wrapperTypes.get(from) == null) {
          // Insert the cast
          RefType boxedType = wrapper.primitiveTypes.get((PrimType) to);
          Local castLocal = lc.generateLocal(boxedType);
          us.add(Jimple.v().newAssignStmt(castLocal, Jimple.v().newCastExpr(fromLocal, boxedType)));
          fromLocal = castLocal;
        }

        return wideningPrimitiveConversion(unbox(fromLocal, jb, us, lc), to, jb, us, lc);
      }
    }

    /**
     * P box = P.valueOf(fromLocal);
     *
     * @param fromLocal
     *          primitive
     * @param jb
     * @param us
     * @return
     */
    private Local box(Local fromLocal, JimpleBody jb, PatchingChain<Unit> us, LocalGenerator lc) {
      PrimType primitiveType = (PrimType) fromLocal.getType();
      SootMethod valueOfMethod = wrapper.valueOf.get(primitiveType);

      Local lBox = lc.generateLocal(primitiveType.boxedType());
      if (lBox == null || valueOfMethod == null || us == null) {
        throw new NullPointerException(String.format("%s,%s,%s,%s", valueOfMethod, primitiveType, wrapper.valueOf.entrySet(),
            wrapper.valueOf.get(primitiveType)));
      }
      us.add(Jimple.v().newAssignStmt(lBox, Jimple.v().newStaticInvokeExpr(valueOfMethod.makeRef(), fromLocal)));

      return lBox;
    }

    /**
     * p unbox = fromLocal.pValue();
     *
     * @param fromLocal
     *          boxed
     * @param jb
     * @param us
     * @return
     */
    private Local unbox(Local fromLocal, JimpleBody jb, PatchingChain<Unit> us, LocalGenerator lc) {
      RefType wrapperType = (RefType) fromLocal.getType();

      Local lUnbox = lc.generateLocal(wrapper.wrapperTypes.get(wrapperType));
      us.add(Jimple.v().newAssignStmt(lUnbox,
          Jimple.v().newVirtualInvokeExpr(fromLocal, wrapper.primitiveValue.get(wrapperType).makeRef())));

      return lUnbox;
    }

    private Local wideningReferenceConversion(Local fromLocal) {
      // a widening reference conversion (JLS §5.1.5)
      // TODO: confirm that 'from' is a subtype of 'to'
      return fromLocal;
    }

    /**
     * T t = (T) fromLocal;
     *
     * @param fromLocal
     * @param to
     * @param jb
     * @param us
     * @return
     */
    private Local narrowingReferenceConversion(Local fromLocal, Type to, JimpleBody jb, PatchingChain<Unit> us,
        LocalGenerator lc) {
      Type fromTy = fromLocal.getType();
      // throw new IllegalArgumentException("Expected source to have reference type");
      if (fromTy.equals(to) || !(fromTy instanceof RefType || fromTy instanceof ArrayType)
          || !(to instanceof RefType || to instanceof ArrayType)) {
        return fromLocal;
        // throw new IllegalArgumentException("Expected target to have reference type");
      }

      Local l2 = lc.generateLocal(to);
      us.add(Jimple.v().newAssignStmt(l2, Jimple.v().newCastExpr(fromLocal, to)));
      return l2;
    }

    /**
     * T t = (T) fromLocal;
     *
     * @param fromLocal
     * @param to
     * @param jb
     * @param us
     * @return
     */
    private Local wideningPrimitiveConversion(Local fromLocal, Type to, JimpleBody jb, PatchingChain<Unit> us,
        LocalGenerator lc) {
      if (!(fromLocal.getType() instanceof PrimType)) {
        throw new IllegalArgumentException("Expected source to have primitive type");
      }
      if (!(to instanceof PrimType)) {
        throw new IllegalArgumentException("Expected target to have primitive type");
      }

      Local l2 = lc.generateLocal(to);
      us.add(Jimple.v().newAssignStmt(l2, Jimple.v().newCastExpr(fromLocal, to)));
      return l2;
    }

    /**
     * Invocation of target implementation method.
     *
     * @param jb
     * @param us
     * @param args
     */
    private void invokeImplMethod(JimpleBody jb, PatchingChain<Unit> us, LocalGenerator lc, List<Local> args) {
      Value value = _invokeImplMethod(jb, us, lc, args);

      if (VoidType.v().equals(implMethodType.getReturnType())) {
        // dispatch method is void
        if (value instanceof InvokeExpr) {
          us.add(Jimple.v().newInvokeStmt(value));
        }
        us.add(Jimple.v().newReturnVoidStmt());
      } else {
        // Handle special case of a constructor method-ref. The dispatch method is void <init>(),
        // but the created object should still be returned.
        // See (src/systemTest/targets/soot/lambdaMetaFactory/Issue1367.java)
        if (VoidType.v().equals(implMethod.getMethodRef().getReturnType())) {
          us.add(Jimple.v().newReturnStmt(value));
        } else {
          // neither is void, must pass through return value
          Local ret = lc.generateLocal(value.getType());
          us.add(Jimple.v().newAssignStmt(ret, value));

          // adapt return value
          us.add(Jimple.v().newReturnStmt(adapt(ret, implMethodType.getReturnType(), jb, us, lc)));
        }
      }
    }

    private Value _invokeImplMethod(JimpleBody jb, PatchingChain<Unit> us, LocalGenerator lc, List<Local> args) {
      // A lambda capturing 'this' may be implemented by a private instance method.
      // A method reference to an instance method may be implemented by the instance method itself.
      // To use the correct invocation style, resolve the method and determine how the compiler
      // implemented the lambda or method reference.

      SootMethodRef methodRef = implMethod.getMethodRef();
      switch (MethodHandle.Kind.getKind(implMethod.getKind())) {
        case REF_INVOKE_STATIC:
          return Jimple.v().newStaticInvokeExpr(methodRef, args);
        case REF_INVOKE_INTERFACE:
          return Jimple.v().newInterfaceInvokeExpr(args.get(0), methodRef, rest(args));
        case REF_INVOKE_VIRTUAL:
          return Jimple.v().newVirtualInvokeExpr(args.get(0), methodRef, rest(args));
        case REF_INVOKE_SPECIAL:
          final SootClass currentClass = jb.getMethod().getDeclaringClass();
          final SootClass calledClass = methodRef.getDeclaringClass();
          // It can be the case that the method is not in the same class (or a super class).
          // As such, we need a virtual call in these cases.
          if (currentClass == calledClass
              || Scene.v().getOrMakeFastHierarchy().canStoreClass(currentClass.getSuperclass(), calledClass)) {
            return Jimple.v().newSpecialInvokeExpr(args.get(0), methodRef, rest(args));
          } else {
            SootMethod m = implMethod.getMethodRef().resolve();
            if (!m.isPublic()) {
              // make sure the method is public
              int mod = Modifier.PUBLIC | m.getModifiers();
              mod &= ~Modifier.PRIVATE;
              mod &= ~Modifier.PROTECTED;
              m.setModifiers(mod);
            }
            // In some versions of the (Open)JDK, we seem to have an interface instead of a class
            // for some reason
            if (methodRef.getDeclaringClass().isInterface()) {
              return Jimple.v().newInterfaceInvokeExpr(args.get(0), methodRef, rest(args));
            } else {
              return Jimple.v().newVirtualInvokeExpr(args.get(0), methodRef, rest(args));
            }
          }
        case REF_INVOKE_CONSTRUCTOR:
          final Jimple jimp = Jimple.v();
          RefType type = methodRef.getDeclaringClass().getType();

          Local newLocal = lc.generateLocal(type);
          us.add(jimp.newAssignStmt(newLocal, jimp.newNewExpr(type)));
          // NOTE: args does not include the receiver
          us.add(jimp.newInvokeStmt(jimp.newSpecialInvokeExpr(newLocal, methodRef, args)));

          return newLocal;
        case REF_GET_FIELD:
        case REF_GET_FIELD_STATIC:
        case REF_PUT_FIELD:
        case REF_PUT_FIELD_STATIC:
        default:
      }
      throw new IllegalArgumentException("Unexpected MethodHandle.Kind " + implMethod.getKind());
    }

    private List<Local> rest(List<Local> args) {
      int first = 1;
      int last = args.size();
      if (last < first) {
        return Collections.emptyList();
      } else {
        return args.subList(first, last);
      }
    }
  }
}
