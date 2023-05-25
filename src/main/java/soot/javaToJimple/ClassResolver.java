package soot.javaToJimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import polyglot.ast.Block;
import polyglot.ast.FieldDecl;
import polyglot.ast.Node;
import polyglot.types.Type;
import polyglot.util.IdentityKey;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.options.Options;
import soot.tagkit.DoubleConstantValueTag;
import soot.tagkit.EnclosingTag;
import soot.tagkit.FloatConstantValueTag;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.LongConstantValueTag;
import soot.tagkit.QualifyingTag;
import soot.tagkit.SourceFileTag;
import soot.tagkit.StringConstantValueTag;
import soot.tagkit.SyntheticTag;

public class ClassResolver {
  private static final Logger logger = LoggerFactory.getLogger(ClassResolver.class);

  private ArrayList<FieldDecl> staticFieldInits;
  private ArrayList<FieldDecl> fieldInits;
  private ArrayList<Block> initializerBlocks;
  private ArrayList<Block> staticInitializerBlocks;

  /**
   * adds source file tag to each sootclass
   */
  protected void addSourceFileTag(soot.SootClass sc) {
    SourceFileTag tag = (SourceFileTag) sc.getTag(SourceFileTag.NAME);
    if (tag == null) {
      tag = new SourceFileTag();
      sc.addTag(tag);
    }

    String name = Util.getSourceFileOfClass(sc);
    if (InitialResolver.v().classToSourceMap() != null) {
      if (InitialResolver.v().classToSourceMap().containsKey(name)) {
        name = InitialResolver.v().classToSourceMap().get(name);
      }
    }

    // the pkg is not included in the tag for some unknown reason
    // I think in this case windows uses the same slash - may cause
    // windows problems though
    int slashIndex = name.lastIndexOf('/');
    if (slashIndex != -1) {
      name = name.substring(slashIndex + 1);
    }
    tag.setSourceFile(name);
    // sc.addTag(new SourceFileTag(name));
  }

  /**
   * Class Declaration Creation
   */
  private void createClassDecl(polyglot.ast.ClassDecl cDecl) {

    // add outer class tag if neccessary (if class is not top-level)
    if (!cDecl.type().isTopLevel()) {
      SootClass outerClass = ((soot.RefType) Util.getSootType(cDecl.type().outer())).getSootClass();

      if (InitialResolver.v().getInnerClassInfoMap() == null) {
        InitialResolver.v().setInnerClassInfoMap(new HashMap<SootClass, InnerClassInfo>());
      }
      InitialResolver.v().getInnerClassInfoMap().put(sootClass,
          new InnerClassInfo(outerClass, cDecl.name(), InnerClassInfo.NESTED));
      sootClass.setOuterClass(outerClass);
    }

    // modifiers
    polyglot.types.Flags flags = cDecl.flags();
    addModifiers(flags, cDecl);

    // super class
    if (cDecl.superClass() == null) {
      soot.SootClass superClass = soot.Scene.v().getSootClass(Scene.v().getObjectType().toString());
      sootClass.setSuperclass(superClass);
    } else {
      sootClass.setSuperclass(((soot.RefType) Util.getSootType(cDecl.superClass().type())).getSootClass());
      if (((polyglot.types.ClassType) cDecl.superClass().type()).isNested()) {
        polyglot.types.ClassType superType = (polyglot.types.ClassType) cDecl.superClass().type();
        // add inner clas tag

        Util.addInnerClassTag(sootClass, sootClass.getName(),
            ((soot.RefType) Util.getSootType(superType.outer())).toString(), superType.name(),
            Util.getModifier(superType.flags()));
      }

    }

    // implements
    for (Iterator interfacesIt = cDecl.interfaces().iterator(); interfacesIt.hasNext();) {
      polyglot.ast.TypeNode next = (polyglot.ast.TypeNode) interfacesIt.next();
      sootClass.addInterface(((soot.RefType) Util.getSootType(next.type())).getSootClass());
    }

    findReferences(cDecl);
    createClassBody(cDecl.body());

    // handle initialization of fields
    // static fields init in clinit
    // other fields init in init
    handleFieldInits();

    if ((staticFieldInits != null) || (staticInitializerBlocks != null)) {
      soot.SootMethod clinitMethod;
      if (!sootClass.declaresMethod("<clinit>", new ArrayList(), soot.VoidType.v())) {
        clinitMethod = Scene.v().makeSootMethod("<clinit>", new ArrayList(), soot.VoidType.v(), soot.Modifier.STATIC,
            new ArrayList<SootClass>());

        sootClass.addMethod(clinitMethod);
        PolyglotMethodSource mSource = new PolyglotMethodSource();
        mSource.setJBB(InitialResolver.v().getJBBFactory().createJimpleBodyBuilder());
        clinitMethod.setSource(mSource);
      } else {
        clinitMethod = sootClass.getMethod("<clinit>", new ArrayList(), soot.VoidType.v());

      }
      ((PolyglotMethodSource) clinitMethod.getSource()).setStaticFieldInits(staticFieldInits);
      ((PolyglotMethodSource) clinitMethod.getSource()).setStaticInitializerBlocks(staticInitializerBlocks);

    }

    // add final locals to local inner classes inits
    if (cDecl.type().isLocal()) {
      AnonLocalClassInfo info = InitialResolver.v().finalLocalInfo().get(new polyglot.util.IdentityKey(cDecl.type()));
      ArrayList<SootField> finalsList = addFinalLocals(cDecl.body(), info.finalLocalsAvail(), cDecl.type(), info);
      for (soot.SootMethod meth : sootClass.getMethods()) {
        if (meth.getName().equals("<init>")) {
          ((PolyglotMethodSource) meth.getSource()).setFinalsList(finalsList);
        }
      }
      if (!info.inStaticMethod()) {
        polyglot.types.ClassType outerType = cDecl.type().outer();
        addOuterClassThisRefToInit(outerType);
        addOuterClassThisRefField(outerType);
      }
    }

    // add outer class ref to constructors of inner classes
    // and out class field ref (only for non-static inner classes
    else if (cDecl.type().isNested() && !cDecl.flags().isStatic()) {
      polyglot.types.ClassType outerType = cDecl.type().outer();
      addOuterClassThisRefToInit(outerType);
      addOuterClassThisRefField(outerType);
    }

    Util.addLnPosTags(sootClass, cDecl.position());
  }

  private void findReferences(polyglot.ast.Node node) {
    TypeListBuilder typeListBuilder = new TypeListBuilder();

    node.visit(typeListBuilder);

    for (Type type : typeListBuilder.getList()) {

      if (type.isPrimitive() || !type.isClass()) {
        continue;
      }
      polyglot.types.ClassType classType = (polyglot.types.ClassType) type;
      soot.Type sootClassType = Util.getSootType(classType);
      references.add(sootClassType);
    }
  }

  /**
   * Class Body Creation
   */
  private void createClassBody(polyglot.ast.ClassBody classBody) {

    // reinit static lists
    staticFieldInits = null;
    fieldInits = null;
    initializerBlocks = null;
    staticInitializerBlocks = null;

    // handle members
    for (Iterator it = classBody.members().iterator(); it.hasNext();) {
      Object next = it.next();
      if (next instanceof polyglot.ast.MethodDecl) {
        createMethodDecl((polyglot.ast.MethodDecl) next);
      } else if (next instanceof polyglot.ast.FieldDecl) {
        createFieldDecl((polyglot.ast.FieldDecl) next);
      } else if (next instanceof polyglot.ast.ConstructorDecl) {
        createConstructorDecl((polyglot.ast.ConstructorDecl) next);
      } else if (next instanceof polyglot.ast.ClassDecl) {
        // this handles inner class tags for immediately enclosed
        // normal nested classes
        Util.addInnerClassTag(sootClass, Util.getSootType(((polyglot.ast.ClassDecl) next).type()).toString(),
            sootClass.getName(), ((polyglot.ast.ClassDecl) next).name().toString(),
            Util.getModifier(((polyglot.ast.ClassDecl) next).flags()));
      } else if (next instanceof polyglot.ast.Initializer) {
        createInitializer((polyglot.ast.Initializer) next);
      } else if (Options.v().verbose()) {
        logger.debug("Class Body Member not implemented for type " + next.getClass().getName());
      }
    }
    handleInnerClassTags(classBody);
    handleClassLiteral(classBody);
    handleAssert(classBody);
  }

  private void addOuterClassThisRefField(polyglot.types.Type outerType) {
    soot.Type outerSootType = Util.getSootType(outerType);
    soot.SootField field = Scene.v().makeSootField("this$0", outerSootType, soot.Modifier.PRIVATE | soot.Modifier.FINAL);
    sootClass.addField(field);
    field.addTag(new SyntheticTag());
  }

  private void addOuterClassThisRefToInit(polyglot.types.Type outerType) {
    soot.Type outerSootType = Util.getSootType(outerType);
    for (soot.SootMethod meth : sootClass.getMethods()) {
      if (meth.getName().equals("<init>")) {
        List<soot.Type> newParams = new ArrayList<soot.Type>();
        newParams.add(outerSootType);
        newParams.addAll(meth.getParameterTypes());
        meth.setParameterTypes(newParams);
        meth.addTag(new EnclosingTag());
        if (InitialResolver.v().getHasOuterRefInInit() == null) {
          InitialResolver.v().setHasOuterRefInInit(new ArrayList());
        }
        InitialResolver.v().getHasOuterRefInInit().add(meth.getDeclaringClass().getType());
      }
    }
  }

  private void addFinals(polyglot.types.LocalInstance li, ArrayList<SootField> finalFields) {
    // add as param for init
    for (SootMethod meth : sootClass.getMethods()) {
      if (meth.getName().equals("<init>")) {
        List<soot.Type> newParams = new ArrayList<soot.Type>();
        newParams.addAll(meth.getParameterTypes());
        newParams.add(Util.getSootType(li.type()));
        meth.setParameterTypes(newParams);
      }
    }

    // add field
    soot.SootField sf = Scene.v().makeSootField("val$" + li.name(), Util.getSootType(li.type()),
        soot.Modifier.FINAL | soot.Modifier.PRIVATE);
    sootClass.addField(sf);
    finalFields.add(sf);
    sf.addTag(new SyntheticTag());
  }

  private ArrayList<SootField> addFinalLocals(polyglot.ast.ClassBody cBody, ArrayList<IdentityKey> finalLocalsAvail,
      polyglot.types.ClassType nodeKeyType, AnonLocalClassInfo info) {
    ArrayList<SootField> finalFields = new ArrayList<SootField>();

    LocalUsesChecker luc = new LocalUsesChecker();
    cBody.visit(luc);
    /* Iterator localsNeededIt = luc.getLocals().iterator(); */
    ArrayList<IdentityKey> localsUsed = new ArrayList<IdentityKey>();
    /*
     * while (localsNeededIt.hasNext()){ polyglot.types.LocalInstance li =
     * (polyglot.types.LocalInstance)((polyglot.util.IdentityKey) localsNeededIt.next()).object(); //if
     * (luc.getLocalDecls().contains(new polyglot.util.IdentityKey(li))){ //} //else { //} if (finalLocalsAvail.contains(new
     * polyglot.util.IdentityKey(li)) && !luc.getLocalDecls().contains(new polyglot.util.IdentityKey(li))){
     *
     * addFinals(li,finalFields);
     *
     * localsUsed.add(new polyglot.util.IdentityKey(li)); } }
     */
    for (IdentityKey next : finalLocalsAvail) {
      polyglot.types.LocalInstance li = (polyglot.types.LocalInstance) next.object();
      if (!luc.getLocalDecls().contains(new polyglot.util.IdentityKey(li))) {
        localsUsed.add(new polyglot.util.IdentityKey(li));
        addFinals(li, finalFields);
      }
    }
    // this part is broken it adds all final locals available for the new
    // not just the ones used (which is a problem)
    for (Node next : luc.getNews()) {
      polyglot.ast.New tempNew = (polyglot.ast.New) next;
      polyglot.types.ClassType tempNewType = (polyglot.types.ClassType) tempNew.objectType().type();
      if (InitialResolver.v().finalLocalInfo().containsKey(new polyglot.util.IdentityKey(tempNewType))) {
        AnonLocalClassInfo lInfo = InitialResolver.v().finalLocalInfo().get(new polyglot.util.IdentityKey(tempNewType));
        for (Iterator<IdentityKey> it = lInfo.finalLocalsAvail().iterator(); it.hasNext();) {
          polyglot.types.LocalInstance li2 = (polyglot.types.LocalInstance) it.next().object();
          if (!sootClass.declaresField("val$" + li2.name(), Util.getSootType(li2.type()))) {
            if (!luc.getLocalDecls().contains(new polyglot.util.IdentityKey(li2))) {
              addFinals(li2, finalFields);
              localsUsed.add(new polyglot.util.IdentityKey(li2));
            }
          }
        }
      }
    }
    // also need to add them if any super class all the way up needs one
    // because the super() will be made in init and it will require
    // possibly eventually to send in the finals

    polyglot.types.ClassType superType = (polyglot.types.ClassType) nodeKeyType.superType();
    while (!Util.getSootType(superType)
        .equals(soot.Scene.v().getSootClass(Scene.v().getObjectType().toString()).getType())) {
      if (InitialResolver.v().finalLocalInfo().containsKey(new polyglot.util.IdentityKey(superType))) {
        AnonLocalClassInfo lInfo = InitialResolver.v().finalLocalInfo().get(new polyglot.util.IdentityKey(superType));
        for (IdentityKey next : lInfo.finalLocalsAvail()) {
          polyglot.types.LocalInstance li2 = (polyglot.types.LocalInstance) next.object();
          if (!sootClass.declaresField("val$" + li2.name(), Util.getSootType(li2.type()))) {
            if (!luc.getLocalDecls().contains(new polyglot.util.IdentityKey(li2))) {
              addFinals(li2, finalFields);
              localsUsed.add(new polyglot.util.IdentityKey(li2));
            }
          }
        }
      }
      superType = (polyglot.types.ClassType) superType.superType();
    }
    info.finalLocalsUsed(localsUsed);
    InitialResolver.v().finalLocalInfo().put(new polyglot.util.IdentityKey(nodeKeyType), info);
    return finalFields;
  }

  /**
   * creates the Jimple for an anon class - in the AST there is no class decl for anon classes - the revelant fields and
   * methods are created
   */
  private void createAnonClassDecl(polyglot.ast.New aNew) {

    SootClass outerClass = ((soot.RefType) Util.getSootType(aNew.anonType().outer())).getSootClass();
    if (InitialResolver.v().getInnerClassInfoMap() == null) {
      InitialResolver.v().setInnerClassInfoMap(new HashMap<SootClass, InnerClassInfo>());
    }
    InitialResolver.v().getInnerClassInfoMap().put(sootClass, new InnerClassInfo(outerClass, "0", InnerClassInfo.ANON));
    sootClass.setOuterClass(outerClass);

    soot.SootClass typeClass = ((soot.RefType) Util.getSootType(aNew.objectType().type())).getSootClass();

    // set superclass
    if (((polyglot.types.ClassType) aNew.objectType().type()).flags().isInterface()) {
      sootClass.addInterface(typeClass);
      sootClass.setSuperclass(soot.Scene.v().getSootClass(Scene.v().getObjectType().toString()));
    } else {
      sootClass.setSuperclass(typeClass);
      if (((polyglot.types.ClassType) aNew.objectType().type()).isNested()) {
        polyglot.types.ClassType superType = (polyglot.types.ClassType) aNew.objectType().type();
        // add inner clas tag
        Util.addInnerClassTag(sootClass, typeClass.getName(),
            ((soot.RefType) Util.getSootType(superType.outer())).toString(), superType.name(),
            Util.getModifier(superType.flags()));

      }
    }

    // needs to be done for local also
    ArrayList params = new ArrayList();

    soot.SootMethod method;
    // if interface there are no extra params
    if (((polyglot.types.ClassType) aNew.objectType().type()).flags().isInterface()) {
      method = Scene.v().makeSootMethod("<init>", params, soot.VoidType.v());
    } else {
      if (!aNew.arguments().isEmpty()) {
        polyglot.types.ConstructorInstance ci = InitialResolver.v().getConstructorForAnon(aNew);
        for (Iterator aIt = ci.formalTypes().iterator(); aIt.hasNext();) {
          polyglot.types.Type pType = (polyglot.types.Type) aIt.next();
          params.add(Util.getSootType(pType));
        }
      }
      /*
       * Iterator aIt = aNew.arguments().iterator(); while (aIt.hasNext()){ polyglot.types.Type pType =
       * ((polyglot.ast.Expr)aIt.next()).type(); params.add(Util.getSootType(pType)); }
       */
      method = Scene.v().makeSootMethod("<init>", params, soot.VoidType.v());
    }

    AnonClassInitMethodSource src = new AnonClassInitMethodSource();
    method.setSource(src);
    sootClass.addMethod(method);

    AnonLocalClassInfo info = InitialResolver.v().finalLocalInfo().get(new polyglot.util.IdentityKey(aNew.anonType()));

    if (aNew.qualifier() != null) {
      // && (!(aNew.qualifier() instanceof
      // polyglot.ast.Special &&
      // ((polyglot.ast.Special)aNew.qualifier()).kind()
      // == polyglot.ast.Special.THIS)) ){
      // if (aNew.qualifier() != null ) {
      // add qualifier ref - do this first to get right order
      addQualifierRefToInit(aNew.qualifier().type());
      src.hasQualifier(true);
    }
    if (info != null) {
      src.inStaticMethod(info.inStaticMethod());
      if (!info.inStaticMethod()) {
        if (!InitialResolver.v().isAnonInCCall(aNew.anonType())) {
          addOuterClassThisRefToInit(aNew.anonType().outer());
          addOuterClassThisRefField(aNew.anonType().outer());
          src.thisOuterType(Util.getSootType(aNew.anonType().outer()));
          src.hasOuterRef(true);
        }
      }
    }
    src.polyglotType((polyglot.types.ClassType) aNew.anonType().superType());
    src.anonType(aNew.anonType());
    if (info != null) {
      src.setFinalsList(addFinalLocals(aNew.body(), info.finalLocalsAvail(), aNew.anonType(), info));
    }
    src.outerClassType(Util.getSootType(aNew.anonType().outer()));
    if (((polyglot.types.ClassType) aNew.objectType().type()).isNested()) {
      src.superOuterType(Util.getSootType(((polyglot.types.ClassType) aNew.objectType().type()).outer()));
      src.isSubType(Util.isSubType(aNew.anonType().outer(), ((polyglot.types.ClassType) aNew.objectType().type()).outer()));
    }

    Util.addLnPosTags(sootClass, aNew.position().line(), aNew.body().position().endLine(), aNew.position().column(),
        aNew.body().position().endColumn());
  }

  public int getModifiers(polyglot.types.Flags flags) {
    return Util.getModifier(flags);
  }

  /**
   * adds modifiers
   */
  private void addModifiers(polyglot.types.Flags flags, polyglot.ast.ClassDecl cDecl) {
    int modifiers = 0;
    if (cDecl.type().isNested()) {
      if (flags.isPublic() || flags.isProtected() || flags.isPrivate()) {
        modifiers = soot.Modifier.PUBLIC;
      }
      if (flags.isInterface()) {
        modifiers = modifiers | soot.Modifier.INTERFACE;
      }
      if (flags.isAbstract()) {
        modifiers = modifiers | soot.Modifier.ABSTRACT;
      }
      // if inner classes are declared in an interface they need to be
      // given public access but I have no idea why
      // if inner classes are declared in an interface the are
      // implicitly static and public (jls9.5)
      if (cDecl.type().outer().flags().isInterface()) {
        modifiers = modifiers | soot.Modifier.PUBLIC;
      }
    } else {
      modifiers = getModifiers(flags);
    }
    sootClass.setModifiers(modifiers);
  }

  private soot.SootClass getSpecialInterfaceAnonClass(soot.SootClass addToClass) {
    // check to see if there is already a special anon class for this
    // interface
    if ((InitialResolver.v().specialAnonMap() != null) && (InitialResolver.v().specialAnonMap().containsKey(addToClass))) {
      return InitialResolver.v().specialAnonMap().get(addToClass);
    } else {
      String specialClassName = addToClass.getName() + "$" + InitialResolver.v().getNextAnonNum();
      // add class to scene and other maps and lists as needed
      soot.SootClass specialClass = new soot.SootClass(specialClassName);
      soot.Scene.v().addClass(specialClass);
      specialClass.setApplicationClass();
      specialClass.addTag(new SyntheticTag());
      specialClass.setSuperclass(soot.Scene.v().getSootClass(Scene.v().getObjectType().toString()));
      Util.addInnerClassTag(addToClass, specialClass.getName(), addToClass.getName(), null, soot.Modifier.STATIC);
      Util.addInnerClassTag(specialClass, specialClass.getName(), addToClass.getName(), null, soot.Modifier.STATIC);
      InitialResolver.v().addNameToAST(specialClassName);
      references.add(RefType.v(specialClassName));
      if (InitialResolver.v().specialAnonMap() == null) {
        InitialResolver.v().setSpecialAnonMap(new HashMap<SootClass, SootClass>());
      }
      InitialResolver.v().specialAnonMap().put(addToClass, specialClass);
      return specialClass;
    }
  }

  /**
   * Handling for assert stmts - extra fields and methods are needed in the Jimple
   */
  private void handleAssert(polyglot.ast.ClassBody cBody) {

    // find any asserts in class body but not in inner class bodies
    AssertStmtChecker asc = new AssertStmtChecker();
    cBody.visit(asc);
    if (!asc.isHasAssert()) {
      return;
    }

    // two extra fields

    // $assertionsDisabled field is added to the actual class where the
    // assert is found (even if its an inner class - interfaces cannot
    // have asserts stmts directly contained within them)
    String fieldName = "$assertionsDisabled";
    soot.Type fieldType = soot.BooleanType.v();
    if (!sootClass.declaresField(fieldName, fieldType)) {
      soot.SootField assertionsDisabledField
          = Scene.v().makeSootField(fieldName, fieldType, soot.Modifier.STATIC | soot.Modifier.FINAL);
      sootClass.addField(assertionsDisabledField);
      assertionsDisabledField.addTag(new SyntheticTag());
    }

    // class$ field is added to the outer most class if sootClass
    // containing the assert is inner - if the outer most class is
    // an interface - add instead to special interface anon class
    soot.SootClass addToClass = sootClass;
    while ((InitialResolver.v().getInnerClassInfoMap() != null)
        && (InitialResolver.v().getInnerClassInfoMap().containsKey(addToClass))) {
      addToClass = InitialResolver.v().getInnerClassInfoMap().get(addToClass).getOuterClass();
    }

    // this field is named after the outer class even if the outer
    // class is an interface and will be actually added to the
    // special interface anon class
    fieldName = "class$" + addToClass.getName().replaceAll(".", "$");
    if ((InitialResolver.v().getInterfacesList() != null)
        && (InitialResolver.v().getInterfacesList().contains(addToClass.getName()))) {
      addToClass = getSpecialInterfaceAnonClass(addToClass);
    }

    fieldType = soot.RefType.v("java.lang.Class");

    if (!addToClass.declaresField(fieldName, fieldType)) {
      soot.SootField classField = Scene.v().makeSootField(fieldName, fieldType, soot.Modifier.STATIC);
      addToClass.addField(classField);
      classField.addTag(new SyntheticTag());
    }

    // two extra methods

    // class$ method is added to the outer most class if sootClass
    // containing the assert is inner - if the outer most class is
    // an interface - add instead to special interface anon class
    String methodName = "class$";
    soot.Type methodRetType = soot.RefType.v("java.lang.Class");
    ArrayList paramTypes = new ArrayList();
    paramTypes.add(soot.RefType.v("java.lang.String"));

    // make meth
    soot.SootMethod sootMethod = Scene.v().makeSootMethod(methodName, paramTypes, methodRetType, soot.Modifier.STATIC);
    AssertClassMethodSource assertMSrc = new AssertClassMethodSource();
    sootMethod.setSource(assertMSrc);

    if (!addToClass.declaresMethod(methodName, paramTypes, methodRetType)) {
      addToClass.addMethod(sootMethod);
      sootMethod.addTag(new SyntheticTag());
    }

    // clinit method is added to actual class where assert is found
    // if the class already has a clinit method its method source is
    // informed of an assert
    methodName = "<clinit>";
    methodRetType = soot.VoidType.v();
    paramTypes = new ArrayList();

    // make meth
    sootMethod = Scene.v().makeSootMethod(methodName, paramTypes, methodRetType, soot.Modifier.STATIC);
    PolyglotMethodSource mSrc = new PolyglotMethodSource();
    mSrc.setJBB(InitialResolver.v().getJBBFactory().createJimpleBodyBuilder());
    mSrc.hasAssert(true);
    sootMethod.setSource(mSrc);

    if (!sootClass.declaresMethod(methodName, paramTypes, methodRetType)) {
      sootClass.addMethod(sootMethod);
    } else {
      ((soot.javaToJimple.PolyglotMethodSource) sootClass.getMethod(methodName, paramTypes, methodRetType).getSource())
          .hasAssert(true);
    }
  }

  /**
   * Constructor Declaration Creation
   */
  private void createConstructorDecl(polyglot.ast.ConstructorDecl constructor) {
    String name = "<init>";

    ArrayList parameters = createParameters(constructor);

    ArrayList<SootClass> exceptions = createExceptions(constructor);

    soot.SootMethod sootMethod = createSootConstructor(name, constructor.flags(), parameters, exceptions);

    finishProcedure(constructor, sootMethod);
  }

  /**
   * Method Declaration Creation
   */
  private void createMethodDecl(polyglot.ast.MethodDecl method) {

    String name = createName(method);

    // parameters
    ArrayList parameters = createParameters(method);

    // exceptions
    ArrayList<SootClass> exceptions = createExceptions(method);

    soot.SootMethod sootMethod = createSootMethod(name, method.flags(), method.returnType().type(), parameters, exceptions);

    finishProcedure(method, sootMethod);
  }

  /**
   * looks after pos tags for methods and constructors
   */
  private void finishProcedure(polyglot.ast.ProcedureDecl procedure, soot.SootMethod sootMethod) {

    addProcedureToClass(sootMethod);

    if (procedure.position() != null) {
      Util.addLnPosTags(sootMethod, procedure.position());
    }

    PolyglotMethodSource mSrc = new PolyglotMethodSource(procedure.body(), procedure.formals());
    mSrc.setJBB(InitialResolver.v().getJBBFactory().createJimpleBodyBuilder());

    sootMethod.setSource(mSrc);

  }

  private void handleFieldInits() {
    if ((fieldInits != null) || (initializerBlocks != null)) {
      for (soot.SootMethod next : sootClass.getMethods()) {
        if (next.getName().equals("<init>")) {
          soot.javaToJimple.PolyglotMethodSource src = (soot.javaToJimple.PolyglotMethodSource) next.getSource();
          src.setInitializerBlocks(initializerBlocks);
          src.setFieldInits(fieldInits);
        }
      }
    }
  }

  private void handleClassLiteral(polyglot.ast.ClassBody cBody) {

    // check for class lits whose type is not primitive
    ClassLiteralChecker classLitChecker = new ClassLiteralChecker();
    cBody.visit(classLitChecker);
    ArrayList<Node> classLitList = classLitChecker.getList();

    if (!classLitList.isEmpty()) {

      soot.SootClass addToClass = sootClass;
      if (addToClass.isInterface()) {
        addToClass = getSpecialInterfaceAnonClass(addToClass);
      }

      // add class$ meth
      String methodName = "class$";
      soot.Type methodRetType = soot.RefType.v("java.lang.Class");
      ArrayList paramTypes = new ArrayList();
      paramTypes.add(soot.RefType.v("java.lang.String"));
      soot.SootMethod sootMethod = Scene.v().makeSootMethod(methodName, paramTypes, methodRetType, soot.Modifier.STATIC);
      ClassLiteralMethodSource mSrc = new ClassLiteralMethodSource();
      sootMethod.setSource(mSrc);

      if (!addToClass.declaresMethod(methodName, paramTypes, methodRetType)) {
        addToClass.addMethod(sootMethod);
        sootMethod.addTag(new SyntheticTag());
      }

      // add fields for all non prim class lits
      for (Iterator<Node> classLitIt = classLitList.iterator(); classLitIt.hasNext();) {
        polyglot.ast.ClassLit classLit = (polyglot.ast.ClassLit) classLitIt.next();

        // field
        String fieldName = Util.getFieldNameForClassLit(classLit.typeNode().type());
        soot.Type fieldType = soot.RefType.v("java.lang.Class");

        soot.SootField sootField = Scene.v().makeSootField(fieldName, fieldType, soot.Modifier.STATIC);
        if (!addToClass.declaresField(fieldName, fieldType)) {
          addToClass.addField(sootField);
          sootField.addTag(new SyntheticTag());
        }
      }
    }
  }

  /**
   * Source Creation
   */
  protected void createSource(polyglot.ast.SourceFile source) {
    // add absolute path to sourceFileTag
    SourceFileTag t = (SourceFileTag) sootClass.getTag(SourceFileTag.NAME);
    if (t != null) {
      /*
       * System.out.println("source: "+source); System.out.println("source.source(): "+source.source()); System.out.println(
       * "source path: "+source.source().path()); System.out.println("source name: "+source.source().name());
       */
      t.setAbsolutePath(source.source().path());
    } else {
      t = new SourceFileTag();
      /*
       * System.out.println("source: "+source); System.out.println("source.source(): "+source.source()); System.out.println(
       * "source path: "+source.source().path()); System.out.println("source name: "+source.source().name());
       */
      t.setAbsolutePath(source.source().path());
      sootClass.addTag(t);
    }

    String simpleName = sootClass.getName();

    boolean found = false;

    // first look in top-level decls
    for (Iterator declsIt = source.decls().iterator(); declsIt.hasNext();) {
      Object next = declsIt.next();
      if (next instanceof polyglot.ast.ClassDecl) {
        polyglot.types.ClassType nextType = ((polyglot.ast.ClassDecl) next).type();
        if (Util.getSootType(nextType).equals(sootClass.getType())) {
          createClassDecl((polyglot.ast.ClassDecl) next);
          found = true;
        }
      }
    }

    // if the class wasn't a top level then its nested, local or anon
    if (!found) {
      NestedClassListBuilder nestedClassBuilder = new NestedClassListBuilder();
      source.visit(nestedClassBuilder);

      Iterator<Node> nestedDeclsIt = nestedClassBuilder.getClassDeclsList().iterator();
      while (nestedDeclsIt.hasNext() && !found) {

        polyglot.ast.ClassDecl nextDecl = (polyglot.ast.ClassDecl) nestedDeclsIt.next();
        polyglot.types.ClassType type = nextDecl.type();
        if (type.isLocal() && !type.isAnonymous()) {

          if (InitialResolver.v().getLocalClassMap().containsVal(simpleName)) {
            createClassDecl(
                ((polyglot.ast.LocalClassDecl) InitialResolver.v().getLocalClassMap().getKey(simpleName)).decl());
            found = true;
          }
        } else {

          if (Util.getSootType(type).equals(sootClass.getType())) {
            createClassDecl(nextDecl);
            found = true;
          }
        }
      }

      if (!found) {
        // assume its anon class (only option left)
        //
        if ((InitialResolver.v().getAnonClassMap() != null)
            && InitialResolver.v().getAnonClassMap().containsVal(simpleName)) {
          polyglot.ast.New aNew = (polyglot.ast.New) InitialResolver.v().getAnonClassMap().getKey(simpleName);
          if (aNew == null) {
            throw new RuntimeException("Could resolve class: " + simpleName);
          }

          createAnonClassDecl(aNew);
          findReferences(aNew.body());
          createClassBody(aNew.body());
          handleFieldInits();

        } else {
          // could be an anon class that was created out of thin air
          // for handling class lits (and asserts) in interfaces
          // this is now done on creation of this special class
          // sootClass.setSuperclass(soot.Scene.v().getSootClass("java.lang.Object"));
        }
      }
    }

  }

  private void handleInnerClassTags(polyglot.ast.ClassBody classBody) {
    // if this class is an inner class add self
    if ((InitialResolver.v().getInnerClassInfoMap() != null)
        && (InitialResolver.v().getInnerClassInfoMap().containsKey(sootClass))) {
      // hasTag(OuterClassTag.NAME)){

      InnerClassInfo tag = InitialResolver.v().getInnerClassInfoMap().get(sootClass);
      Util.addInnerClassTag(sootClass, sootClass.getName(),
          tag.getInnerType() == InnerClassInfo.ANON ? null : tag.getOuterClass().getName(),
          tag.getInnerType() == InnerClassInfo.ANON ? null : tag.getSimpleName(),
          soot.Modifier.isInterface(tag.getOuterClass().getModifiers()) ? soot.Modifier.STATIC | soot.Modifier.PUBLIC
              : sootClass.getModifiers());
      // if this class is an inner class and enclosing class is also
      // an inner class add enclsing class
      SootClass outerClass = tag.getOuterClass();
      while (InitialResolver.v().getInnerClassInfoMap().containsKey(outerClass)) {
        InnerClassInfo tag2 = InitialResolver.v().getInnerClassInfoMap().get(outerClass);
        Util.addInnerClassTag(sootClass, outerClass.getName(),
            tag2.getInnerType() == InnerClassInfo.ANON ? null : tag2.getOuterClass().getName(),
            tag2.getInnerType() == InnerClassInfo.ANON ? null : tag2.getSimpleName(),
            tag2.getInnerType() == InnerClassInfo.ANON && soot.Modifier.isInterface(tag2.getOuterClass().getModifiers())
                ? soot.Modifier.STATIC | soot.Modifier.PUBLIC
                : outerClass.getModifiers());
        outerClass = tag2.getOuterClass();
      }
    }

  }

  private void addQualifierRefToInit(polyglot.types.Type type) {
    soot.Type sootType = Util.getSootType(type);
    for (soot.SootMethod meth : sootClass.getMethods()) {
      if (meth.getName().equals("<init>")) {
        List<soot.Type> newParams = new ArrayList<soot.Type>();
        newParams.add(sootType);
        newParams.addAll(meth.getParameterTypes());
        meth.setParameterTypes(newParams);
        meth.addTag(new QualifyingTag());
      }
    }
  }

  private void addProcedureToClass(soot.SootMethod method) {
    sootClass.addMethod(method);
  }

  private void addConstValTag(polyglot.ast.FieldDecl field, soot.SootField sootField) {
    // logger.debug("adding constantval tag to field: "+field);
    if (field.fieldInstance().constantValue() instanceof Integer) {
      sootField.addTag(new IntegerConstantValueTag(((Integer) field.fieldInstance().constantValue())));
    } else if (field.fieldInstance().constantValue() instanceof Character) {
      sootField.addTag(new IntegerConstantValueTag(((Character) field.fieldInstance().constantValue())));
    } else if (field.fieldInstance().constantValue() instanceof Short) {
      sootField.addTag(new IntegerConstantValueTag(((Short) field.fieldInstance().constantValue())));
    } else if (field.fieldInstance().constantValue() instanceof Byte) {
      sootField.addTag(new IntegerConstantValueTag(((Byte) field.fieldInstance().constantValue())));
    } else if (field.fieldInstance().constantValue() instanceof Boolean) {
      boolean b = ((Boolean) field.fieldInstance().constantValue());
      sootField.addTag(new IntegerConstantValueTag(b ? 1 : 0));
    } else if (field.fieldInstance().constantValue() instanceof Long) {
      sootField.addTag(new LongConstantValueTag(((Long) field.fieldInstance().constantValue())));
    } else if (field.fieldInstance().constantValue() instanceof Double) {
      // System.out.println("const val:
      // "+field.fieldInstance().constantValue());
      sootField.addTag(new DoubleConstantValueTag((long) ((Double) field.fieldInstance().constantValue()).doubleValue()));
      // System.out.println(((Double)field.fieldInstance().constantValue()).doubleValue());
      DoubleConstantValueTag tag = (DoubleConstantValueTag) sootField.getTag(DoubleConstantValueTag.NAME);
      // System.out.println("tag: "+tag);
    } else if (field.fieldInstance().constantValue() instanceof Float) {
      sootField.addTag(new FloatConstantValueTag(((Float) field.fieldInstance().constantValue())));
    } else if (field.fieldInstance().constantValue() instanceof String) {
      sootField.addTag(new StringConstantValueTag((String) field.fieldInstance().constantValue()));
    } else {
      throw new RuntimeException("Expecting static final field to have a constant value! For field: " + field + " of type: "
          + field.fieldInstance().constantValue().getClass());
    }
  }

  /**
   * Field Declaration Creation
   */
  private void createFieldDecl(polyglot.ast.FieldDecl field) {

    // System.out.println("field decl: "+field);
    int modifiers = Util.getModifier(field.fieldInstance().flags());
    String name = field.fieldInstance().name();
    soot.Type sootType = Util.getSootType(field.fieldInstance().type());
    soot.SootField sootField = Scene.v().makeSootField(name, sootType, modifiers);
    sootClass.addField(sootField);

    if (field.fieldInstance().flags().isStatic()) {
      if (field.init() != null) {
        if (field.flags().isFinal()
            && (field.type().type().isPrimitive() || (field.type().type().toString().equals("java.lang.String")))
            && field.fieldInstance().isConstant()) {
          // System.out.println("adding constantValtag: to field:
          // "+sootField);
          addConstValTag(field, sootField);
        } else {
          if (staticFieldInits == null) {
            staticFieldInits = new ArrayList<FieldDecl>();
          }
          staticFieldInits.add(field);
        }
      }
    } else {
      if (field.init() != null) {
        if (fieldInits == null) {
          fieldInits = new ArrayList<FieldDecl>();
        }
        fieldInits.add(field);
      }
    }

    Util.addLnPosTags(sootField, field.position());
  }

  public ClassResolver(SootClass sootClass, Set<soot.Type> set) {
    this.sootClass = sootClass;
    this.references = set;
  }

  private final SootClass sootClass;
  private final Collection<soot.Type> references;

  /**
   * Procedure Declaration Helper Methods creates procedure name
   */
  private String createName(polyglot.ast.ProcedureDecl procedure) {
    return procedure.name();
  }

  /**
   * creates soot params from polyglot formals
   */
  private ArrayList createParameters(polyglot.ast.ProcedureDecl procedure) {
    ArrayList parameters = new ArrayList();
    for (Iterator formalsIt = procedure.formals().iterator(); formalsIt.hasNext();) {
      polyglot.ast.Formal next = (polyglot.ast.Formal) formalsIt.next();
      parameters.add(Util.getSootType(next.type().type()));
    }
    return parameters;
  }

  /**
   * creates soot exceptions from polyglot throws
   */
  private ArrayList<SootClass> createExceptions(polyglot.ast.ProcedureDecl procedure) {
    ArrayList<SootClass> exceptions = new ArrayList<SootClass>();
    for (Iterator throwsIt = procedure.throwTypes().iterator(); throwsIt.hasNext();) {
      polyglot.types.Type throwType = ((polyglot.ast.TypeNode) throwsIt.next()).type();
      exceptions.add(((soot.RefType) Util.getSootType(throwType)).getSootClass());
    }
    return exceptions;
  }

  private soot.SootMethod createSootMethod(String name, polyglot.types.Flags flags, polyglot.types.Type returnType,
      ArrayList parameters, ArrayList<SootClass> exceptions) {

    int modifier = Util.getModifier(flags);
    soot.Type sootReturnType = Util.getSootType(returnType);

    return Scene.v().makeSootMethod(name, parameters, sootReturnType, modifier, exceptions);
  }

  /**
   * Initializer Creation
   */
  private void createInitializer(polyglot.ast.Initializer initializer) {
    if (initializer.flags().isStatic()) {
      if (staticInitializerBlocks == null) {
        staticInitializerBlocks = new ArrayList<Block>();
      }
      staticInitializerBlocks.add(initializer.body());
    } else {
      if (initializerBlocks == null) {
        initializerBlocks = new ArrayList<Block>();
      }
      initializerBlocks.add(initializer.body());
    }
  }

  private soot.SootMethod createSootConstructor(String name, polyglot.types.Flags flags, ArrayList parameters,
      ArrayList<SootClass> exceptions) {
    int modifier = Util.getModifier(flags);
    return Scene.v().makeSootMethod(name, parameters, soot.VoidType.v(), modifier, exceptions);
  }
}
