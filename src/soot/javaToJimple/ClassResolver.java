/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

package soot.javaToJimple;
import soot.*;
import java.util.*;

public class ClassResolver {

    private ArrayList staticFieldInits; 
    private ArrayList fieldInits;
    private ArrayList initializerBlocks;
    private ArrayList staticInitializerBlocks;
   
    /**
     *  adds source file tag to each sootclass
     */
    protected void addSourceFileTag(soot.SootClass sc){
        if (sc.getTag("SourceFileTag") != null) return;
        String name = Util.getSourceFileOfClass(sc);


        if (InitialResolver.v().classToSourceMap() != null){
            if (InitialResolver.v().classToSourceMap().containsKey(name)){
                name = (String)InitialResolver.v().classToSourceMap().get(name);
            }
        }

        // the pkg is not included in the tag for some unknown reason
        // I think in this case windows uses the same slash - may cause 
        // windows problems though
        int slashIndex = name.indexOf("/");
        if (slashIndex != -1){
            name = name.substring(slashIndex+1);
        }
        sc.addTag(new soot.tagkit.SourceFileTag(name));
    }
    
    /**
     * Class Declaration Creation
     */
    private void createClassDecl(polyglot.ast.ClassDecl cDecl){
    
        //add outer class tag if neccessary (if class is not top-level)
        if (!cDecl.type().isTopLevel()){
            SootClass outerClass = ((soot.RefType)Util.getSootType(cDecl.type().outer())).getSootClass();
            
            if (InitialResolver.v().getInnerClassInfoMap() == null){
                InitialResolver.v().setInnerClassInfoMap(new HashMap());
            }
            InitialResolver.v().getInnerClassInfoMap().put(sootClass, new InnerClassInfo(outerClass, cDecl.name(), InnerClassInfo.NESTED));
            sootClass.setOuterClass(outerClass);
        }
    
        // modifiers
        polyglot.types.Flags flags = cDecl.flags();
        addModifiers(flags, cDecl);
        
        // super class
        if (cDecl.superClass() == null) {
            soot.SootClass superClass = soot.Scene.v().getSootClass ("java.lang.Object"); 
            sootClass.setSuperclass(superClass);
        }
        else {
    
            sootClass.setSuperclass(((soot.RefType)Util.getSootType(cDecl.superClass().type())).getSootClass());
            if (((polyglot.types.ClassType)cDecl.superClass().type()).isNested()){
                polyglot.types.ClassType superType = (polyglot.types.ClassType)cDecl.superClass().type();
                // add inner clas tag
                
                Util.addInnerClassTag(sootClass, sootClass.getName(), ((soot.RefType)Util.getSootType(superType.outer())).toString(), superType.name(), Util.getModifier(superType.flags()));
            }
        
        }
    
    
        // implements 
        Iterator interfacesIt = cDecl.interfaces().iterator();
        while (interfacesIt.hasNext()) {
            polyglot.ast.TypeNode next = (polyglot.ast.TypeNode)interfacesIt.next();
            sootClass.addInterface(((soot.RefType)Util.getSootType(next.type())).getSootClass());
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
                clinitMethod = new soot.SootMethod("<clinit>", new ArrayList(), soot.VoidType.v(), soot.Modifier.STATIC, new ArrayList());
                
                sootClass.addMethod(clinitMethod);
                PolyglotMethodSource mSource = new PolyglotMethodSource();
                mSource.setJBB(InitialResolver.v().getJBBFactory().createJimpleBodyBuilder());
                clinitMethod.setSource(mSource);
            }
            else {
                clinitMethod = sootClass.getMethod("<clinit>", new ArrayList(), soot.VoidType.v());
            
            }
            ((PolyglotMethodSource)clinitMethod.getSource()).setStaticFieldInits(staticFieldInits);
            ((PolyglotMethodSource)clinitMethod.getSource()).setStaticInitializerBlocks(staticInitializerBlocks);
    
        }
    
    
        // add final locals to local inner classes inits
        if (cDecl.type().isLocal()) {
            AnonLocalClassInfo info = (AnonLocalClassInfo)InitialResolver.v().finalLocalInfo().get(new polyglot.util.IdentityKey(cDecl.type()));
                ArrayList finalsList = addFinalLocals(cDecl.body(), info.finalLocals(), cDecl.type(), info); 
                Iterator it = sootClass.getMethods().iterator();
                while (it.hasNext()){
                    soot.SootMethod meth = (soot.SootMethod)it.next();
                    if (meth.getName().equals("<init>")){
                        ((PolyglotMethodSource)meth.getSource()).setFinalsList(finalsList);
                    }
                }
            if (!info.inStaticMethod()){
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

        for( Iterator typeIt = typeListBuilder.getList().iterator(); typeIt.hasNext(); ) {

            final polyglot.types.Type type = (polyglot.types.Type) typeIt.next();
            if (type.isPrimitive()) continue;
            if (!type.isClass()) continue;
            polyglot.types.ClassType classType = (polyglot.types.ClassType)type;
            soot.Type sootClassType = Util.getSootType(classType);
            references.add(sootClassType);
        }
    }

    /**
     * Class Body Creation
     */
    private void createClassBody(polyglot.ast.ClassBody classBody){
        

        // reinit static lists
        staticFieldInits = null;
        fieldInits = null;
        initializerBlocks = null;
        staticInitializerBlocks = null;
    
        
        // handle members
        Iterator it = classBody.members().iterator();
        while (it.hasNext()){
            Object next = it.next();
            
            if (next instanceof polyglot.ast.MethodDecl) {
                createMethodDecl((polyglot.ast.MethodDecl)next);
            }
            else if (next instanceof polyglot.ast.FieldDecl) {
                createFieldDecl((polyglot.ast.FieldDecl)next);
            }
            else if (next instanceof polyglot.ast.ConstructorDecl){
                createConstructorDecl((polyglot.ast.ConstructorDecl)next);
            }
            else if (next instanceof polyglot.ast.ClassDecl){
                // this handles inner class tags for immediately enclosed
                // normal nested classes 
                Util.addInnerClassTag(sootClass, Util.getSootType(((polyglot.ast.ClassDecl)next).type()).toString(), sootClass.getName(), ((polyglot.ast.ClassDecl)next).name().toString(), Util.getModifier(((polyglot.ast.ClassDecl)next).flags()));
            }
            else if (next instanceof polyglot.ast.Initializer) {
                createInitializer((polyglot.ast.Initializer)next);
            }
            else {
                throw new RuntimeException("Class Body Member not implemented");
            }
        }
        handleInnerClassTags(classBody);
        handleClassLiteral(classBody);
        handleAssert(classBody);
    }

    private void addOuterClassThisRefField(polyglot.types.Type outerType){
        soot.Type outerSootType = Util.getSootType(outerType);
        soot.SootField field = new soot.SootField("this$0", outerSootType, soot.Modifier.PRIVATE | soot.Modifier.FINAL);
        sootClass.addField(field);
    }

    private void addOuterClassThisRefToInit(polyglot.types.Type outerType){
        soot.Type outerSootType = Util.getSootType(outerType);
        Iterator it = sootClass.getMethods().iterator();
        while (it.hasNext()){
            soot.SootMethod meth = (soot.SootMethod)it.next();
            if (meth.getName().equals("<init>")){
                List newParams = new ArrayList();
                newParams.add(outerSootType);
                newParams.addAll(meth.getParameterTypes());
                meth.setParameterTypes(newParams);
                if (InitialResolver.v().getHasOuterRefInInit() == null){
                    InitialResolver.v().setHasOuterRefInInit(new ArrayList());
                }
                InitialResolver.v().getHasOuterRefInInit().add(meth.getDeclaringClass().getType());
            }
        }
    }
    private void addFinals(polyglot.types.LocalInstance li, ArrayList finalFields){
        // add as param for init
        Iterator it = sootClass.getMethods().iterator();
        while (it.hasNext()){
            soot.SootMethod meth = (soot.SootMethod)it.next();
            if (meth.getName().equals("<init>")){
                List newParams = new ArrayList();
                newParams.addAll(meth.getParameterTypes());
                newParams.add(Util.getSootType(li.type()));
                meth.setParameterTypes(newParams);
            }
        }
                
        // add field
        //System.out.println("add field: val$"+li.name()+" to: "+sootClass.getName());
        soot.SootField sf = new soot.SootField("val$"+li.name(), Util.getSootType(li.type()), soot.Modifier.FINAL | soot.Modifier.PRIVATE);
        sootClass.addField(sf);
        finalFields.add(sf);
               
    }
    private ArrayList addFinalLocals(polyglot.ast.ClassBody cBody, ArrayList finalLocals, polyglot.types.ClassType nodeKeyType, AnonLocalClassInfo info){
        ArrayList finalFields = new ArrayList();
        
        LocalUsesChecker luc = new LocalUsesChecker();
        cBody.visit(luc);
        Iterator localsNeededIt = luc.getLocals().iterator();
        ArrayList localsUsed = new ArrayList();
        while (localsNeededIt.hasNext()){
            polyglot.types.LocalInstance li = (polyglot.types.LocalInstance)((polyglot.util.IdentityKey)localsNeededIt.next()).object();
            //System.out.println("testing class: "+Util.getSootType(nodeKeyType));
            //System.out.println("for local inst: "+li);
            //System.out.println("luc localdecls: "+luc.getLocalDecls());
            //if (luc.getLocalDecls().contains(new polyglot.util.IdentityKey(li))){
              //  System.out.println("contains decl"+li);
            //}
            //else {
              //  System.out.println("doesn't contain decl: "+li);
            //}
            if (finalLocals.contains(new polyglot.util.IdentityKey(li)) && !luc.getLocalDecls().contains(new polyglot.util.IdentityKey(li))){
               
                //System.out.println("how does it get here??");
                addFinals(li,finalFields);
                
                localsUsed.add(new polyglot.util.IdentityKey(li));
            }
        }
        Iterator newsIt = luc.getNews().iterator();
        while (newsIt.hasNext()){
            polyglot.ast.New tempNew = (polyglot.ast.New)newsIt.next();
            polyglot.types.ClassType tempNewType = (polyglot.types.ClassType)tempNew.objectType().type();
            //System.out.println("checking new of class: "+Util.getSootType(tempNewType));
            if (InitialResolver.v().finalLocalInfo().containsKey(new polyglot.util.IdentityKey(tempNewType))){
                AnonLocalClassInfo lInfo = (AnonLocalClassInfo)InitialResolver.v().finalLocalInfo().get(new polyglot.util.IdentityKey(tempNewType));
                Iterator it = lInfo.finalLocals().iterator();
                while (it.hasNext()){
                    polyglot.types.LocalInstance li2 = (polyglot.types.LocalInstance)((polyglot.util.IdentityKey)it.next()).object();
                    if (!sootClass.declaresField("val$"+li2.name(), Util.getSootType(li2.type()))){
                        if (!luc.getLocalDecls().contains(new polyglot.util.IdentityKey(li2))){
                            addFinals(li2, finalFields);
                            localsUsed.add(new polyglot.util.IdentityKey(li2));
                        }
                    }
                }
            }
        }
    
        info.finalLocals(localsUsed);
        InitialResolver.v().finalLocalInfo().put(new polyglot.util.IdentityKey(nodeKeyType), info);
        return finalFields;
    }
        /**
         * creates the Jimple for an anon class - in the AST there is no class 
         * decl for anon classes - the revelant fields and methods are 
         * created 
         */
        private void createAnonClassDecl(polyglot.ast.New aNew) {
            
            SootClass outerClass = ((soot.RefType)Util.getSootType(aNew.anonType().outer())).getSootClass();
            if (InitialResolver.v().getInnerClassInfoMap() == null){
                InitialResolver.v().setInnerClassInfoMap(new HashMap());
            }
            InitialResolver.v().getInnerClassInfoMap().put(sootClass, new InnerClassInfo(outerClass, "0", InnerClassInfo.ANON));
            sootClass.setOuterClass(outerClass);
        
            soot.SootClass typeClass = ((soot.RefType)Util.getSootType(aNew.objectType().type())).getSootClass();
           
            // set superclass
            if (((polyglot.types.ClassType)aNew.objectType().type()).flags().isInterface()){
                sootClass.addInterface(typeClass);
                sootClass.setSuperclass(soot.Scene.v().getSootClass("java.lang.Object"));
            }
            else {
                sootClass.setSuperclass(typeClass);
                if (((polyglot.types.ClassType)aNew.objectType().type()).isNested()){
                    polyglot.types.ClassType superType = (polyglot.types.ClassType)aNew.objectType().type();
                    // add inner clas tag
                    Util.addInnerClassTag(sootClass, typeClass.getName(), ((soot.RefType)Util.getSootType(superType.outer())).toString(), superType.name(), Util.getModifier(superType.flags()));
    
                }
            }
    
            // needs to be done for local also
            ArrayList params = new ArrayList();
                
            soot.SootMethod method;
            // if interface there are no extra params
            if (((polyglot.types.ClassType)aNew.objectType().type()).flags().isInterface()){
                method = new soot.SootMethod("<init>", params, soot.VoidType.v());
            }
            else {
                Iterator aIt = aNew.arguments().iterator();
                while (aIt.hasNext()){
                    polyglot.types.Type pType = ((polyglot.ast.Expr)aIt.next()).type();
                    params.add(Util.getSootType(pType));
                }
                method = new soot.SootMethod("<init>", params, soot.VoidType.v());
            }
            
            AnonClassInitMethodSource src = new AnonClassInitMethodSource();
            method.setSource(src);
            sootClass.addMethod(method);
       
            AnonLocalClassInfo info = (AnonLocalClassInfo)InitialResolver.v().finalLocalInfo().get(new polyglot.util.IdentityKey(aNew.anonType()));
           
            if (aNew.qualifier() != null) {
                // add qualifier ref - do this first to get right order
                addQualifierRefToInit(aNew.qualifier().type());
                src.hasQualifier(true);
            }
            if (!info.inStaticMethod()){
                addOuterClassThisRefToInit(aNew.anonType().outer());
                addOuterClassThisRefField(aNew.anonType().outer());
                src.thisOuterType(Util.getSootType(aNew.anonType().outer()));
                src.hasOuterRef(true);
            }
            
            src.inStaticMethod(info.inStaticMethod());
            if (info != null){
                src.setFinalsList(addFinalLocals(aNew.body(), info.finalLocals(), (polyglot.types.ClassType)aNew.anonType(), info));
            }
            src.outerClassType(Util.getSootType(aNew.anonType().outer()));
            if (((polyglot.types.ClassType)aNew.objectType().type()).isNested()){
                src.superOuterType(Util.getSootType(((polyglot.types.ClassType)aNew.objectType().type()).outer()));
                src.isSubType(Util.isSubType(aNew.anonType().outer(), ((polyglot.types.ClassType)aNew.objectType().type()).outer())); 
            }
        }

        /**
         * adds modifiers
         */
        private void addModifiers(polyglot.types.Flags flags, polyglot.ast.ClassDecl cDecl){
            int modifiers = 0;
            if (cDecl.type().isNested()){
                if (flags.isPublic() || flags.isProtected() || flags.isPrivate()){
                    modifiers = soot.Modifier.PUBLIC;
                }
                if (flags.isInterface()){
                    modifiers = modifiers | soot.Modifier.INTERFACE;
                }
                if (flags.isAbstract()){
                    modifiers = modifiers | soot.Modifier.ABSTRACT;
                }
                // if inner classes are declared in an interface they need to be
                // given public access but I have no idea why
                // if inner classes are declared in an interface the are 
                // implicitly static and public (jls9.5)
                if (cDecl.type().outer().flags().isInterface()){
                    modifiers = modifiers | soot.Modifier.PUBLIC;
                }
            }
            else {
                modifiers = Util.getModifier(flags);
            }
            sootClass.setModifiers(modifiers);
        }
    /**
     * Handling for assert stmts - extra fields and methods are needed
     * in the Jimple 
     */
    private void handleAssert(polyglot.ast.ClassBody cBody){
        AssertStmtChecker asc = new AssertStmtChecker();
        cBody.visit(asc);
        if (!asc.isHasAssert()) return;
        // two extra fields
        if (!sootClass.declaresField("$assertionsDisabled", soot.BooleanType.v())){
            sootClass.addField(new soot.SootField("$assertionsDisabled", soot.BooleanType.v(), soot.Modifier.STATIC | soot.Modifier.FINAL));
        }
        soot.SootClass addClassToClass = sootClass;
        while ((InitialResolver.v().getInnerClassInfoMap() != null) && (InitialResolver.v().getInnerClassInfoMap().containsKey(addClassToClass))){
            addClassToClass = ((InnerClassInfo)InitialResolver.v().getInnerClassInfoMap().get(addClassToClass)).getOuterClass();
        }
        if (!addClassToClass.declaresField("class$"+addClassToClass.getName(), soot.RefType.v("java.lang.Class"))){
            addClassToClass.addField(new soot.SootField("class$"+addClassToClass.getName(), soot.RefType.v("java.lang.Class"), soot.Modifier.STATIC));
        }
        // two extra methods
        String methodName = "class$";
        soot.Type methodRetType = soot.RefType.v("java.lang.Class");
        ArrayList paramTypes = new ArrayList();
        paramTypes.add(soot.RefType.v("java.lang.String"));
        if (!addClassToClass.declaresMethod(methodName, paramTypes, methodRetType)){
            soot.SootMethod sootMethod = new soot.SootMethod(methodName, paramTypes, methodRetType, soot.Modifier.STATIC);
            AssertClassMethodSource mSrc = new AssertClassMethodSource();
            sootMethod.setSource(mSrc);
            addClassToClass.addMethod(sootMethod);
        }
        methodName = "<clinit>";
        methodRetType = soot.VoidType.v();
        paramTypes = new ArrayList();
        if (!sootClass.declaresMethod(methodName, paramTypes, methodRetType)){
            soot.SootMethod sootMethod = new soot.SootMethod(methodName, paramTypes, methodRetType, soot.Modifier.STATIC);
            PolyglotMethodSource mSrc = new PolyglotMethodSource();
            mSrc.setJBB(InitialResolver.v().getJBBFactory().createJimpleBodyBuilder());
            mSrc.hasAssert(true);
            sootMethod.setSource(mSrc);
            sootClass.addMethod(sootMethod);
        }
        else {
            ((soot.javaToJimple.PolyglotMethodSource)sootClass.getMethod(methodName, paramTypes, methodRetType).getSource()).hasAssert(true);
        }
    }
    /**
     * Constructor Declaration Creation
     */
    private void createConstructorDecl(polyglot.ast.ConstructorDecl constructor){
        String name = "<init>";
    
        ArrayList parameters = createParameters(constructor);
    
        ArrayList exceptions = createExceptions(constructor);
    
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
        ArrayList exceptions = createExceptions(method);
    
        soot.SootMethod sootMethod = createSootMethod(name, method.flags(), method.returnType().type(), parameters, exceptions);
    
        finishProcedure(method, sootMethod);
    }
    /**
     * looks after pos tags for methods and constructors
     */
    private void finishProcedure(polyglot.ast.ProcedureDecl procedure, soot.SootMethod sootMethod){
        
        addProcedureToClass(sootMethod);
    
        if (procedure.position() != null){
                if (procedure.body() != null) {
                    if (procedure.body().position() != null) {
                        Util.addLnPosTags(sootMethod, procedure.position().line(), procedure.body().position().endLine(), procedure.position().column(), procedure.body().position().endColumn());
                    }
                }
                else {
                    Util.addLnPosTags(sootMethod, procedure.position());
                }
                
        }
    
    
        PolyglotMethodSource mSrc = new PolyglotMethodSource(procedure.body(), procedure.formals());
        mSrc.setJBB(InitialResolver.v().getJBBFactory().createJimpleBodyBuilder()); 
        
        sootMethod.setSource(mSrc);
        
    }

    private void handleFieldInits(){
        if ((fieldInits != null) || (initializerBlocks != null)) {
            Iterator methodsIt = sootClass.getMethods().iterator();
            while (methodsIt.hasNext()) {
                soot.SootMethod next = (soot.SootMethod)methodsIt.next();
                if (next.getName().equals("<init>")){
               
                        soot.javaToJimple.PolyglotMethodSource src = (soot.javaToJimple.PolyglotMethodSource)next.getSource();
                        src.setInitializerBlocks(initializerBlocks);
                        src.setFieldInits(fieldInits);
          
                }
            }
        }
        
    }
    private void handleClassLiteral(polyglot.ast.ClassBody cBody){
    
        ClassLiteralChecker classLitChecker = new ClassLiteralChecker();
        cBody.visit(classLitChecker);
        ArrayList classLitList = classLitChecker.getList();
        String specialClassName = null;    
        if (!classLitList.isEmpty()) {
            String methodName = "class$";
            soot.Type methodRetType = soot.RefType.v("java.lang.Class");
            ArrayList paramTypes = new ArrayList();
            paramTypes.add(soot.RefType.v("java.lang.String"));
            soot.SootMethod sootMethod = new soot.SootMethod(methodName, paramTypes, methodRetType, soot.Modifier.STATIC);
            ClassLiteralMethodSource mSrc = new ClassLiteralMethodSource();
            sootMethod.setSource(mSrc);
            if (sootClass.isInterface()) {
                // have to create a I$1 class
                
                specialClassName = sootClass.getName()+"$"+InitialResolver.v().getNextAnonNum();    
                InitialResolver.v().addNameToAST(specialClassName);
                references.add(specialClassName);    
                // add meth to newly created class not this current one
                soot.SootClass specialClass = soot.Scene.v().getSootClass(specialClassName);
                if (InitialResolver.v().specialAnonMap() == null){
                    InitialResolver.v().setSpecialAnonMap(new HashMap());
                }
                InitialResolver.v().specialAnonMap().put(sootClass, specialClass);
                
                if (!specialClass.declaresMethod(methodName, paramTypes, methodRetType)){
                    specialClass.addMethod(sootMethod);
                }
            
            }
            else {
                if (!sootClass.declaresMethod(methodName, paramTypes, methodRetType)){
                    sootClass.addMethod(sootMethod);
                }
            }
        }
        Iterator classLitIt = classLitList.iterator();
        while (classLitIt.hasNext()) {
            polyglot.ast.ClassLit classLit = (polyglot.ast.ClassLit)classLitIt.next();
    
            // field
            String fieldName = Util.getFieldNameForClassLit(classLit.typeNode().type());
            soot.Type fieldType = soot.RefType.v("java.lang.Class");
            soot.SootField sootField = new soot.SootField(fieldName, fieldType, soot.Modifier.STATIC);
            if (sootClass.isInterface()){
                soot.SootClass specialClass = soot.Scene.v().getSootClass(specialClassName);
                if (!specialClass.declaresField(fieldName, fieldType)){
                    specialClass.addField(sootField);
                }
            }
            else {
                if (!sootClass.declaresField(fieldName, fieldType)){
                    sootClass.addField(sootField);
                }
            }
    
        }
    
    }
    /**
     * returns the name of the class without the package part
     */
    private String getSimpleClassName(){
        String name = sootClass.getName();
        if (sootClass.getPackageName() != null){
            name = name.substring(name.lastIndexOf(".")+1, name.length());
        }
        return name;
    }
    /**
     * Source Creation 
     */
    protected void createSource(polyglot.ast.SourceFile source){
    
        String simpleName = sootClass.getName();
        
        Iterator declsIt = source.decls().iterator();
        boolean found = false;
    
        // first look in top-level decls
        while (declsIt.hasNext()){
            Object next = declsIt.next();
            if (next instanceof polyglot.ast.ClassDecl) {
                polyglot.types.ClassType nextType = ((polyglot.ast.ClassDecl)next).type();
                if (Util.getSootType(nextType).equals(sootClass.getType())){
                    createClassDecl((polyglot.ast.ClassDecl)next);
                    found = true;
                }
            }
        }
    
        // if the class wasn't a top level then its nested, local or anon
        if (!found) {
            NestedClassListBuilder nestedClassBuilder = new NestedClassListBuilder();
            source.visit(nestedClassBuilder);
            
            Iterator nestedDeclsIt = nestedClassBuilder.getClassDeclsList().iterator();
            while (nestedDeclsIt.hasNext() && !found){
                
                polyglot.ast.ClassDecl nextDecl = (polyglot.ast.ClassDecl)nestedDeclsIt.next();
                polyglot.types.ClassType type = (polyglot.types.ClassType)nextDecl.type();
                if (type.isLocal() && !type.isAnonymous()) {
                   
                    if (InitialResolver.v().getLocalClassMap().containsVal(simpleName)){
                        createClassDecl(((polyglot.ast.LocalClassDecl)InitialResolver.v().getLocalClassMap().getKey(simpleName)).decl());
                        found = true;
                    }
                }
                else {
               
                    if (Util.getSootType(type).equals(sootClass.getType())){
                        createClassDecl(nextDecl);
                        found = true;
                    }
                }
            }
    
            if (!found) {
                // assume its anon class (only option left) 
                //
                if ((InitialResolver.v().getAnonClassMap() != null) && InitialResolver.v().getAnonClassMap().containsVal(simpleName)){
                    
                    polyglot.ast.New aNew = (polyglot.ast.New)InitialResolver.v().getAnonClassMap().getKey(simpleName);
                    createAnonClassDecl(aNew);
                    findReferences(aNew.body());
                    createClassBody(aNew.body());
                    handleFieldInits();
    
                }                    
                else {
                    // could be an anon class that was created out of thin air 
                    // for handling class lits in interfaces
                    sootClass.setSuperclass(soot.Scene.v().getSootClass("java.lang.Object"));
                }
            }
        }
    
        
    }

    private void handleInnerClassTags(polyglot.ast.ClassBody classBody){
        // if this class is an inner class add self
        if ((InitialResolver.v().getInnerClassInfoMap() != null) && (InitialResolver.v().getInnerClassInfoMap().containsKey(sootClass))){
            //hasTag("OuterClassTag")){
            
            InnerClassInfo tag = (InnerClassInfo)InitialResolver.v().getInnerClassInfoMap().get(sootClass);
            Util.addInnerClassTag(sootClass, sootClass.getName(), tag.getInnerType() == InnerClassInfo.ANON ? null : tag.getOuterClass().getName(), tag.getInnerType() == InnerClassInfo.ANON ? null : tag.getSimpleName(), soot.Modifier.isInterface(tag.getOuterClass().getModifiers()) ? soot.Modifier.STATIC | soot.Modifier.PUBLIC : sootClass.getModifiers());
            // if this class is an inner class and enclosing class is also
            // an inner class add enclsing class
            SootClass outerClass = tag.getOuterClass();
            while (InitialResolver.v().getInnerClassInfoMap().containsKey(outerClass)){
                InnerClassInfo tag2 = (InnerClassInfo)InitialResolver.v().getInnerClassInfoMap().get(outerClass);
                Util.addInnerClassTag(sootClass, outerClass.getName(), tag2.getInnerType() == InnerClassInfo.ANON ? null : tag2.getOuterClass().getName(), tag2.getInnerType() == InnerClassInfo.ANON ? null : tag2.getSimpleName(), tag2.getInnerType() == InnerClassInfo.ANON && soot.Modifier.isInterface(tag2.getOuterClass().getModifiers()) ? soot.Modifier.STATIC | soot.Modifier.PUBLIC : outerClass.getModifiers());
                outerClass = tag2.getOuterClass();
            }
        }
    
    }
    private void addQualifierRefToInit(polyglot.types.Type type){
        soot.Type sootType = Util.getSootType(type);
        Iterator it = sootClass.getMethods().iterator();
        while (it.hasNext()){
            soot.SootMethod meth = (soot.SootMethod)it.next();
            if (meth.getName().equals("<init>")){
                List newParams = new ArrayList();
                newParams.add(sootType);
                newParams.addAll(meth.getParameterTypes());
                meth.setParameterTypes(newParams);
            }
        }
    }
    private void addProcedureToClass(soot.SootMethod method) {
        sootClass.addMethod(method);
    }
    /**
     * Field Declaration Creation
     */
    private void createFieldDecl(polyglot.ast.FieldDecl field){
    
        int modifiers = Util.getModifier(field.fieldInstance().flags());
        String name = field.fieldInstance().name();
        soot.Type sootType = Util.getSootType(field.fieldInstance().type());
        soot.SootField sootField = new soot.SootField(name, sootType, modifiers);
        sootClass.addField(sootField);
    
        
        if (field.fieldInstance().flags().isStatic()) {
            if (field.init() != null) {
                if (staticFieldInits == null) {
                    staticFieldInits = new ArrayList();
                }
                staticFieldInits.add(field);
            }
        }
        else {
            if (field.init() != null) {
                if (fieldInits == null) {
                    fieldInits = new ArrayList();
                }
                fieldInits.add(field);
            }
        }
    
    
        Util.addLnPosTags(sootField, field.position());
    }
    ClassResolver( SootClass sootClass, List references ) {
        this.sootClass = sootClass;
        this.references = references;
    }
    private final SootClass sootClass;
    private final List references;

    
    /**
     * Procedure Declaration Helper Methods
     * creates procedure name
     */
    private String createName(polyglot.ast.ProcedureDecl procedure) {
        return procedure.name();
    }

    /**
     * creates soot params from polyglot formals
     */
    private ArrayList createParameters(polyglot.ast.ProcedureDecl procedure) {
        ArrayList parameters = new ArrayList();
        Iterator formalsIt = procedure.formals().iterator();
        while (formalsIt.hasNext()){
            polyglot.ast.Formal next = (polyglot.ast.Formal)formalsIt.next();
            parameters.add(Util.getSootType(next.type().type()));
        }
        return parameters;
    }
    
    /**
     * creates soot exceptions from polyglot throws
     */
    private ArrayList createExceptions(polyglot.ast.ProcedureDecl procedure) {
        ArrayList exceptions = new ArrayList();
        Iterator throwsIt = procedure.throwTypes().iterator();
        while (throwsIt.hasNext()){
            polyglot.types.Type throwType = ((polyglot.ast.TypeNode)throwsIt.next()).type();
            exceptions.add(((soot.RefType)Util.getSootType(throwType)).getSootClass());
        }
        return exceptions; 
    }
    
    
    private soot.SootMethod createSootMethod(String name, polyglot.types.Flags flags , polyglot.types.Type returnType, ArrayList parameters, ArrayList exceptions){
        
        int modifier = Util.getModifier(flags);
        soot.Type sootReturnType = Util.getSootType(returnType);

        soot.SootMethod method = new soot.SootMethod(name, parameters, sootReturnType, modifier, exceptions);
        return method;
    }
    
    /**
     * Initializer Creation
     */
    private void createInitializer(polyglot.ast.Initializer initializer) {
        if (initializer.flags().isStatic()) {
            if (staticInitializerBlocks == null) {
                staticInitializerBlocks = new ArrayList();
            }
            staticInitializerBlocks.add(initializer.body());
        }
        else {
            if (initializerBlocks == null) {
                initializerBlocks = new ArrayList();
            }
            initializerBlocks.add(initializer.body());
        }
    }
    
    private soot.SootMethod createSootConstructor(String name, polyglot.types.Flags flags, ArrayList parameters, ArrayList exceptions) {
        
        int modifier = Util.getModifier(flags);

        soot.SootMethod method = new soot.SootMethod(name, parameters, soot.VoidType.v(), modifier, exceptions);

        return method;
    }
    
}

