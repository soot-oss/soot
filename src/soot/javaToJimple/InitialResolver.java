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

public class InitialResolver {

    private polyglot.ast.Node astNode;  // source node
    private ArrayList staticFieldInits; 
    private ArrayList fieldInits;
    private HashMap fieldMap;  // maps field instances to soot fields
    //private HashMap sourceToClassMap; // is used 
    private ArrayList initializerBlocks;
    private ArrayList staticInitializerBlocks;
    private polyglot.frontend.Compiler compiler; 
    private polyglot.util.Position currentClassDeclPos;
    private BiMap anonClassMap;   // maps New to SootClass (name)
    private HashMap anonTypeMap;    //maps polyglot types to soot types
    private BiMap localClassMap;  // maps LocalClassDecl to SootClass (name)
    private HashMap localTypeMap;   // maps polyglot types to soot types
    private int privateAccessCounter = 0; // global for whole program because
                                          // the methods created are static 
    private HashMap privateAccessMap;
    private HashMap finalLocalInfo; // new or lcd mapped to list of final locals avail in current meth and the whether its static
    private HashMap newToOuterMap;    
  
    private HashMap sootNameToAST = null;
    private ArrayList hasOuterRefInInit; // list of sootclass types that need an outer class this param in for init
   
    private HashMap classToSourceMap;
    private HashMap specialAnonMap;
   
    private FastHierarchy hierarchy;
    
    /**
     * returns true if there is an AST avail for given soot class
     */
    public boolean hasASTForSootName(String name){
       if (sootNameToAST == null) return false;
       if (sootNameToAST.containsKey(name)) return true;
       return false;
    }
    
    /**
     * sets AST for given soot class if possible
     */
    public void setASTForSootName(String name){
        if (!hasASTForSootName(name)) {
            throw new RuntimeException("Can only set AST for name if it exists. You should probably not be calling this method unless you know what you're doing!");
        }
        setAst((polyglot.ast.Node)sootNameToAST.get(name));
    }
   
    public InitialResolver(soot.Singletons.Global g){}
    public static InitialResolver v() {
        return soot.G.v().soot_javaToJimple_InitialResolver();
    }
    

    
    /**
     * Invokes polyglot and gets the AST for the source given in fullPath
     */
    public void formAst(String fullPath, List locations){
    
        JavaToJimple jtj = new JavaToJimple();
        polyglot.frontend.ExtensionInfo extInfo = jtj.initExtInfo(fullPath, locations);
        // only have one compiler - for memory issues
        if (compiler == null) {
            compiler = new polyglot.frontend.Compiler(extInfo);
        }
        // build ast
        astNode = jtj.compile(compiler, fullPath, extInfo);
    }

    /**
     * if you have a special AST set it here then call resolveFormJavaFile
     * on the soot class
     */ 
    public void setAst(polyglot.ast.Node ast) {
        astNode = ast;
    }
   
    /**
     *  adds source file tag to each sootclass
     */
    private void addSourceFileTag(soot.SootClass sc){
        if (sc.getTag("SourceFileTag") != null) return;
        String name = Util.getSourceFileOfClass(sc);


        if (classToSourceMap != null){
            if (classToSourceMap.containsKey(name)){
                name = (String)classToSourceMap.get(name);
            }
        }

        // the pkg is not included in the tag for some unknown reason
        // I think in this case windows uses the same slash - may cause 
        // windows problems though
        //System.out.println("source name: "+name);
        int slashIndex = name.indexOf("/");
        if (slashIndex != -1){
            name = name.substring(slashIndex+1);
        }
        //System.out.println("source name after: "+name);
        sc.addTag(new soot.tagkit.SourceFileTag(name));
    }

    
    /* get types and resolve them in the Scene
     * use a polyglot visitor to find all types in AST
     * for nested inner classes fix names (. -> $)
     * for local and anon find, invent names and resolve those
     */
    private void resolveTypes(){
        
        // get types and resolve them in the Scene
        TypeListBuilder typeListBuilder = new TypeListBuilder();
        
        astNode.visit(typeListBuilder);
        

        Iterator it = typeListBuilder.getList().iterator();
        while (it.hasNext()) {
            polyglot.types.Type type = (polyglot.types.Type)it.next();
            
            // ignore primitives
            if (type.isPrimitive()) continue;
              
            // ignore non class types
            if (!type.isClass()) continue;
                
               
            polyglot.types.ClassType classType = (polyglot.types.ClassType)type;
            
            resolveClassType(classType);
            
        }
        
        
        // resolve Object, StringBuffer(used for string concat) and Throwable
        // (used for finally)
        // Shouldn't be needed here since they're in base classes list
        //SootResolver.v().resolveClass("java.lang.Object");
        //SootResolver.v().resolveClass("java.lang.StringBuffer");
        //SootResolver.v().resolveClass("java.lang.Throwable");
        
    }

    /**
     * resolve class types - recursively resolving outer class if nec.
     */
    private void resolveClassType(polyglot.types.ClassType classType){
        
        soot.Type sootClassType;
            

        // fix class names of inner member classes
        if (classType.isNested()){
       
            resolveClassType(classType.outer());
        }
        
        if (classType.isLocal()) {
            resolveLocalClass(classType);
        }
                 
        if (classType.isAnonymous()) {
            resolveAnonClass(classType);
        }

        
        sootClassType = Util.getSootType(classType);
       
        if (classTypesFound == null){
            classTypesFound = new ArrayList();
        }
        classTypesFound.add(classType);
        //SootResolver.v().referencedType(sootClassType);
    }

    private ArrayList classTypesFound;
    private void makeASTMap(){
        ClassDeclFinder finder = new ClassDeclFinder();
        finder.typesToFind(classTypesFound);
        astNode.visit(finder);
        Iterator it = finder.declsFound().iterator();
        while (it.hasNext()){
            addNameToAST(Util.getSootType(((polyglot.ast.ClassDecl)it.next()).type()).toString());
        }
    }
    
    /**
     * add name to AST to map - used mostly for inner and non public
     * top-level classes
     */
    private void addNameToAST(String name){
        if (sootNameToAST == null){
            sootNameToAST = new HashMap();
        }   
        sootNameToAST.put(name, astNode);
    }
    
    // resolves all types and deals with .class literals and asserts
    public List resolveFromJavaFile(soot.SootClass sc) {
        List references = new ArrayList();
        ClassResolver cr = new ClassResolver(sc, references);
        
        // get types and resolve them in the Scene
        resolveTypes();

        makeASTMap();
        
        // create class to source map first 
        // create source file
        if (astNode instanceof polyglot.ast.SourceFile) {
            createClassToSourceMap((polyglot.ast.SourceFile)astNode);
            cr.createSource((polyglot.ast.SourceFile)astNode);
        }
        
        addSourceFileTag(sc);
        
        return references;
    }
    
    class ClassResolver {
        /**
         * Class Declaration Creation
         */
        private void createClassDecl(polyglot.ast.ClassDecl cDecl){
        
            //add outer class tag if neccessary (if class is not top-level)
            if (!cDecl.type().isTopLevel()){
                SootClass outerClass = ((soot.RefType)Util.getSootType(cDecl.type().outer())).getSootClass();
                
                if (innerClassInfoMap == null){
                    innerClassInfoMap = new HashMap();
                }
                innerClassInfoMap.put(sootClass, new InnerClassInfo(outerClass, cDecl.name(), InnerClassInfo.NESTED));
                sootClass.setOuterClass(outerClass);
                //sootClass.addTag(new soot.tagkit.OuterClassTag(outerClass, cDecl.name(), false));
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
                    //System.out.println("super type: "+superType);
                    // add inner clas tag
                    
                    Util.addInnerClassTag(sootClass, sootClass.getName(), ((soot.RefType)Util.getSootType(superType.outer())).toString(), superType.name(), Util.getModifier(superType.flags()));
                    /*sootClass.addTag(new soot.tagkit.InnerClassTag(
                    sootClass.getName(),
                    ((soot.RefType)Util.getSootType(superType.outer())).toString(), 
                    superType.name(),
                    Util.getModifier(superType.flags())));*/
                }
            
            }
        
        
            // implements 
            Iterator interfacesIt = cDecl.interfaces().iterator();
            while (interfacesIt.hasNext()) {
                polyglot.ast.TypeNode next = (polyglot.ast.TypeNode)interfacesIt.next();
                sootClass.addInterface(((soot.RefType)Util.getSootType(next.type())).getSootClass());
            }
            
            currentClassDeclPos = cDecl.position();
            findReferences(cDecl);
            createClassBody(cDecl.body());
        
            // handle initialization of fields 
            // static fields init in clinit
            // other fields init in init
            handleFieldInits();
            
            if ((staticFieldInits != null) || (staticInitializerBlocks != null)) {
                soot.SootMethod clinitMethod;
                if (!sootClass.XdeclaresMethod("<clinit>", new ArrayList(), soot.VoidType.v())) {
                    clinitMethod = new soot.SootMethod("<clinit>", new ArrayList(), soot.VoidType.v(), soot.Modifier.STATIC, new ArrayList());
                    
                    sootClass.addMethod(clinitMethod);
                    clinitMethod.setSource(new soot.javaToJimple.PolyglotMethodSource());
                }
                else {
                    clinitMethod = sootClass.XgetMethod("<clinit>", new ArrayList(), soot.VoidType.v());
                
                }
                ((PolyglotMethodSource)clinitMethod.getSource()).setStaticFieldInits(staticFieldInits);
                ((PolyglotMethodSource)clinitMethod.getSource()).setStaticInitializerBlocks(staticInitializerBlocks);
        
            }
        
        
            // add final locals to local inner classes inits
            if (cDecl.type().isLocal()) {
                AnonLocalClassInfo info = (AnonLocalClassInfo)finalLocalInfo.get(new polyglot.util.IdentityKey(cDecl.type()));
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
            
            Util.addLineTag(sootClass, cDecl);
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
        
            //handleClassLiteral(classBody);
            //handleAssert(classBody);
            
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
            handlePrivateAccessors(classBody);
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
                    //System.out.println("meth in add outer ref: "+meth);
                    if (hasOuterRefInInit == null){
                        hasOuterRefInInit = new ArrayList();
                    }
                    hasOuterRefInInit.add(meth.getDeclaringClass().getType());
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
                if (finalLocals.contains(new polyglot.util.IdentityKey(li))){
                    
                    addFinals(li,finalFields);
                    
                    localsUsed.add(new polyglot.util.IdentityKey(li));
                }
            }
            Iterator newsIt = luc.getNews().iterator();
            while (newsIt.hasNext()){
                polyglot.ast.New tempNew = (polyglot.ast.New)newsIt.next();
                //System.out.println("new used: "+tempNew);
                //System.out.println("finalLocalInfo: "+finalLocalInfo);
                polyglot.types.ClassType tempNewType = (polyglot.types.ClassType)tempNew.objectType().type();
                if (finalLocalInfo.containsKey(new polyglot.util.IdentityKey(tempNewType))){
                    AnonLocalClassInfo lInfo = (AnonLocalClassInfo)finalLocalInfo.get(new polyglot.util.IdentityKey(tempNewType));
                    //System.out.println("anon local class info: "+lInfo); 
                    Iterator it = lInfo.finalLocals().iterator();
                    while (it.hasNext()){
                        polyglot.types.LocalInstance li2 = (polyglot.types.LocalInstance)((polyglot.util.IdentityKey)it.next()).object();
                        //System.out.println("li for new: "+li2);
                        if (!sootClass.XdeclaresField("val$"+li2.name(), Util.getSootType(li2.type()))){
                            addFinals(li2, finalFields);
                            localsUsed.add(new polyglot.util.IdentityKey(li2));
                        }
                    }
                }
            }
        
            info.finalLocals(localsUsed);
            finalLocalInfo.put(new polyglot.util.IdentityKey(nodeKeyType), info);
            return finalFields;
        }
            /**
             * creates the Jimple for an anon class - in the AST there is no class 
             * decl for anon classes - the revelant fields and methods are 
             * created 
             */
            private void createAnonClassDecl(polyglot.ast.New aNew) {
                
                SootClass outerClass = ((soot.RefType)Util.getSootType(aNew.anonType().outer())).getSootClass();
                if (innerClassInfoMap == null){
                    innerClassInfoMap = new HashMap();
                }
                innerClassInfoMap.put(sootClass, new InnerClassInfo(outerClass, "0", InnerClassInfo.ANON));
                sootClass.setOuterClass(outerClass);
                //sootClass.addTag(new soot.tagkit.OuterClassTag(outerClass, "0", true));
            
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
                        //System.out.println("super type: "+superType);
                        // add inner clas tag
                        Util.addInnerClassTag(sootClass, typeClass.getName(), ((soot.RefType)Util.getSootType(superType.outer())).toString(), superType.name(), Util.getModifier(superType.flags()));
        
                        /*String innerName = typeClass.getName();
                        // maybe replace dots with separator??
                        innerName = soot.Util.replaceAll(innerName, ".", "/");
                        sootClass.addTag(new soot.tagkit.InnerClassTag(
                        innerName,
                        ((soot.RefType)Util.getSootType(superType.outer())).toString(), 
                        superType.name(),
                        Util.getModifier(superType.flags())));
        */
                    }
                }
        
                // needs to be done for local also
                ArrayList params = new ArrayList();
                    
                soot.SootMethod method;
                // if interface there are no extra params
                if (((polyglot.types.ClassType)aNew.objectType().type()).flags().isInterface()){
                //if (typeClass.isInterface()){
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
           
                AnonLocalClassInfo info = (AnonLocalClassInfo)finalLocalInfo.get(new polyglot.util.IdentityKey(aNew.anonType()));
               
                //System.out.println("new : "+aNew.anonType());
                //System.out.println("new : "+aNew.anonType().outer());
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
                    //System.out.println("aNew: "+aNew);
                    //System.out.println("aNew anon: "+aNew.anonType());
                    //System.out.println("aNew object: "+aNew.objectType().type());
                    //System.out.println("aNew object outer: "+((polyglot.types.ClassType)aNew.objectType().type()).outer());
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
            if (!sootClass.XdeclaresField("$assertionsDisabled", soot.BooleanType.v())){
                sootClass.addField(new soot.SootField("$assertionsDisabled", soot.BooleanType.v(), soot.Modifier.STATIC | soot.Modifier.FINAL));
            }
            soot.SootClass addClassToClass = sootClass;
            while ((innerClassInfoMap != null) && (innerClassInfoMap.containsKey(addClassToClass))){
                addClassToClass = ((InnerClassInfo)innerClassInfoMap.get(addClassToClass)).getOuterClass();
            }
            /*if (!sootClass.declaresField("class$"+sootClass.getName(), soot.RefType.v("java.lang.Class"))){
                sootClass.addField(new soot.SootField("class$"+sootClass.getName(), soot.RefType.v("java.lang.Class"), soot.Modifier.STATIC));
            }*/
            if (!addClassToClass.XdeclaresField("class$"+addClassToClass.getName(), soot.RefType.v("java.lang.Class"))){
                addClassToClass.addField(new soot.SootField("class$"+addClassToClass.getName(), soot.RefType.v("java.lang.Class"), soot.Modifier.STATIC));
            }
            // two extra methods
            String methodName = "class$";
            soot.Type methodRetType = soot.RefType.v("java.lang.Class");
            ArrayList paramTypes = new ArrayList();
            paramTypes.add(soot.RefType.v("java.lang.String"));
            if (!addClassToClass.XdeclaresMethod(methodName, paramTypes, methodRetType)){
                soot.SootMethod sootMethod = new soot.SootMethod(methodName, paramTypes, methodRetType, soot.Modifier.STATIC);
                AssertClassMethodSource mSrc = new AssertClassMethodSource();
                sootMethod.setSource(mSrc);
                addClassToClass.addMethod(sootMethod);
            }
            methodName = "<clinit>";
            methodRetType = soot.VoidType.v();
            paramTypes = new ArrayList();
            if (!sootClass.XdeclaresMethod(methodName, paramTypes, methodRetType)){
                soot.SootMethod sootMethod = new soot.SootMethod(methodName, paramTypes, methodRetType, soot.Modifier.STATIC);
                PolyglotMethodSource mSrc = new PolyglotMethodSource();
                mSrc.hasAssert(true);
                sootMethod.setSource(mSrc);
                sootClass.addMethod(sootMethod);
            }
            else {
                ((soot.javaToJimple.PolyglotMethodSource)sootClass.XgetMethod(methodName, paramTypes, methodRetType).getSource()).hasAssert(true);
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
                    
            }
        
            //handle final local map for local and anon classes
            handleFinalLocals(procedure);
        
            PolyglotMethodSource mSrc = new PolyglotMethodSource(procedure.body(), procedure.formals());
            mSrc.setPrivateAccessMap(privateAccessMap);
        
            
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
                    
                    specialClassName = sootClass.getName()+"$"+getNextAnonNum();    
                    addNameToAST(specialClassName);
                    references.add(specialClassName);    
                    // add meth to newly created class not this current one
                    soot.SootClass specialClass = soot.Scene.v().getSootClass(specialClassName);
                    if (specialAnonMap == null){
                        specialAnonMap = new HashMap();
                    }
                    specialAnonMap.put(sootClass, specialClass);
                    
                    if (!specialClass.XdeclaresMethod(methodName, paramTypes, methodRetType)){
                        specialClass.addMethod(sootMethod);
                    }
                
                }
                else {
                    if (!sootClass.XdeclaresMethod(methodName, paramTypes, methodRetType)){
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
                    if (!specialClass.XdeclaresField(fieldName, fieldType)){
                        specialClass.addField(sootField);
                    }
                }
                else {
                    if (!sootClass.XdeclaresField(fieldName, fieldType)){
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
        private void createSource(polyglot.ast.SourceFile source){
        
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
                       
                        if (localClassMap.containsVal(simpleName)){
                            createClassDecl(((polyglot.ast.LocalClassDecl)localClassMap.getKey(simpleName)).decl());
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
                    if ((anonClassMap != null) && anonClassMap.containsVal(simpleName)){
                        
                        polyglot.ast.New aNew = (polyglot.ast.New)anonClassMap.getKey(simpleName);
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
            if ((innerClassInfoMap != null) && (innerClassInfoMap.containsKey(sootClass))){
                //hasTag("OuterClassTag")){
                
                InnerClassInfo tag = (InnerClassInfo)innerClassInfoMap.get(sootClass);
                //soot.tagkit.OuterClassTag tag = (soot.tagkit.OuterClassTag)sootClass.getTag("OuterClassTag");
                Util.addInnerClassTag(sootClass, sootClass.getName(), tag.getInnerType() == InnerClassInfo.ANON ? null : tag.getOuterClass().getName(), tag.getInnerType() == InnerClassInfo.ANON ? null : tag.getSimpleName(), soot.Modifier.isInterface(tag.getOuterClass().getModifiers()) ? soot.Modifier.STATIC | soot.Modifier.PUBLIC : sootClass.getModifiers());
                // if this class is an inner class and enclosing class is also
                // an inner class add enclsing class
                SootClass outerClass = tag.getOuterClass();
                while (innerClassInfoMap.containsKey(outerClass)){//outerClass.hasTag("OuterClassTag")){
                    InnerClassInfo tag2 = (InnerClassInfo)innerClassInfoMap.get(outerClass);
                    //soot.tagkit.OuterClassTag tag2 = (soot.tagkit.OuterClassTag)outerClass.getTag("OuterClassTag");
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
                    //System.out.println("meth in add qualifier: "+meth);
                }
            }
        }
        /**
         * inner classes can access private fields and methods of the
         * outer class and special methods are created in order
         * to make this possible
         */
        private void handlePrivateAccessors(polyglot.ast.ClassBody cBody) {
            // determine and create acces methods for used private access
            // in inner classes
                   
            ArrayList privateAccessList = new ArrayList();
            ArrayList uses = new ArrayList();
        
            // look through body for private field and procedure decls
            PrivateInstancesAvailable privateInsts = new PrivateInstancesAvailable();
            cBody.visit(privateInsts);
        
        
            // look through body again for all inner class bodies
            InnerClassBodies icb = new InnerClassBodies();
            cBody.visit(icb);
            
                   
            // look through each body and see if they use a private instance
            Iterator cbIt = icb.getList().iterator();
            while (cbIt.hasNext()){
                polyglot.ast.ClassBody cb = (polyglot.ast.ClassBody)cbIt.next();
                PrivateAccessUses pau = new PrivateAccessUses();
                pau.avail(privateInsts.getList());
                cb.visit(pau);
        
                uses.addAll(pau.getList());
            }
               
            Iterator listIt = uses.iterator();
            while (listIt.hasNext()) {
                Object nextInst = ((polyglot.util.IdentityKey)listIt.next()).object();
                if (nextInst instanceof polyglot.types.FieldInstance) {
                    if (Util.getSootType(((polyglot.types.FieldInstance)nextInst).container()).equals(sootClass.getType())){
                        privateAccessList.add(nextInst);
                    }
                }
                if (nextInst instanceof polyglot.types.MethodInstance) {
                    if (Util.getSootType(((polyglot.types.MethodInstance)nextInst).container()).equals(sootClass.getType())){
                        privateAccessList.add(nextInst);
                    }
                          
                }
            }
        
            Iterator it = privateAccessList.iterator();
            while (it.hasNext()) {
                polyglot.types.MemberInstance inst = (polyglot.types.MemberInstance)it.next();
                String name = "access$"+privateAccessCounter+"00";
                
                ArrayList paramTypesList = new ArrayList();
                if (inst instanceof polyglot.types.MethodInstance) {
                    Iterator paramsIt = ((polyglot.types.MethodInstance)inst).formalTypes().iterator();
                    while (paramsIt.hasNext()) {
                        paramTypesList.add(Util.getSootType((polyglot.types.Type)paramsIt.next()));
                    }
                }
                if (!inst.flags().isStatic()) {
                    paramTypesList.add(sootClass.getType());
                }
                
                
                soot.Type returnType = null;
                if (inst instanceof polyglot.types.MethodInstance) {
                    returnType = Util.getSootType(((polyglot.types.MethodInstance)inst).returnType());    
                }
                else {
                    returnType = Util.getSootType(((polyglot.types.FieldInstance)inst).type());
                }
                
                soot.SootMethod accessMeth = new soot.SootMethod(name, paramTypesList, returnType, soot.Modifier.STATIC);
        
                if (inst instanceof polyglot.types.MethodInstance) {
                    PrivateMethodAccMethodSource pmams = new PrivateMethodAccMethodSource();
                    pmams.setMethodInst((polyglot.types.MethodInstance)inst);
                    accessMeth.setSource(pmams);
                }
                else {
                    PrivateFieldAccMethodSource pfams = new PrivateFieldAccMethodSource(
                    Util.getSootType(((polyglot.types.FieldInstance)inst).type()),
                    ((polyglot.types.FieldInstance)inst).name(),
                    ((polyglot.types.FieldInstance)inst).flags().isStatic(),
                    ((soot.RefType)Util.getSootType(((polyglot.types.FieldInstance)inst).container())).getSootClass()
                    );
                    accessMeth.setSource(pfams);
                }
        
                sootClass.addMethod(accessMeth);
                if (privateAccessMap == null){
                    privateAccessMap = new HashMap();
                }
                privateAccessMap.put(inst, accessMeth);
                privateAccessCounter++;
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
        
            if (fieldMap == null) {
                fieldMap = new HashMap();
            }
        
            fieldMap.put(field.fieldInstance(), sootField);
            
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
        
            handleFinalLocals(field);
        
            Util.addLnPosTags(sootField, field.position());
        }
        ClassResolver( SootClass sootClass, List references ) {
            this.sootClass = sootClass;
            this.references = references;
        }
        private final SootClass sootClass;
        private final List references;
    }

    private void createClassToSourceMap(polyglot.ast.SourceFile src){
       
        String srcName = src.source().path();
        //System.out.println("srcName: "+srcName);
        String srcFileName = null;
        if (src.package_() != null){
            String slashedPkg = soot.util.StringTools.replaceAll(src.package_().package_().fullName(), ".", System.getProperty("file.separator"));
            srcFileName = srcName.substring(srcName.lastIndexOf(slashedPkg));
        }
        else {
            srcFileName = srcName.substring(srcName.lastIndexOf(System.getProperty("file.separator"))+1);
        }

        ArrayList list = new ArrayList();
        Iterator it = src.decls().iterator();
        while (it.hasNext()){
            polyglot.ast.ClassDecl nextDecl = (polyglot.ast.ClassDecl)it.next();
            addToClassToSourceMap(Util.getSootType(nextDecl.type()).toString(), srcFileName); 
        }

    }


    private int getNextAnonNum(){
        if (anonTypeMap == null) return 1;
        else return anonTypeMap.size()+1;
    }
    
    private void resolveAnonClass(polyglot.types.ClassType type){
        NewFinder finder = new NewFinder();
        finder.typeToFind(type);
        astNode.visit(finder);
        if (finder.newFound() == null){
            throw new RuntimeException("Couldn't find New corresponding to the anon class type: "+type);
        }
        // maybe this anon has already been resolved
        if (anonClassMap == null){
            anonClassMap = new BiMap();
        }
        if (anonTypeMap == null){
            anonTypeMap = new HashMap();
        }
        if (!anonClassMap.containsKey(finder.newFound())){
            int nextAvailNum = 1;
            polyglot.types.ClassType outerToMatch = type.outer();
            while (outerToMatch.isNested()){
                outerToMatch = outerToMatch.outer();
            }

            if (!anonTypeMap.isEmpty()){
                Iterator matchIt = anonTypeMap.keySet().iterator();
                while (matchIt.hasNext()){
                    polyglot.types.ClassType pType = (polyglot.types.ClassType)((polyglot.util.IdentityKey)matchIt.next()).object();
                    polyglot.types.ClassType outerMatch = pType.outer();
                    while (outerMatch.isNested()){
                        outerMatch = outerMatch.outer();
                    }
                    if (outerMatch.equals(outerToMatch)){
                        int numFound = getAnonClassNum((String)anonTypeMap.get(new polyglot.util.IdentityKey(pType)));
                        if (numFound >= nextAvailNum){
                            nextAvailNum = numFound+1;
                        }
                    }
                }
            }
            
            String realName = outerToMatch.fullName()+"$"+nextAvailNum;
            anonClassMap.put(finder.newFound(), realName);
            anonTypeMap.put(new polyglot.util.IdentityKey(type), realName);
            addNameToAST(realName);
            //soot.SootResolver.v().referencedClass(realName);
            
        }
    }
    
    private void resolveLocalClass(polyglot.types.ClassType type){
        LocalClassDeclFinder finder = new LocalClassDeclFinder();
        finder.typeToFind(type);
        astNode.visit(finder);
        if (finder.declFound() == null) {
            throw new RuntimeException("Couldn't find LocalClassDecl corresponding to the local class type: "+type);
        }
        // maybe this localdecl has already been resolved 
        if (localClassMap == null){
            localClassMap = new BiMap();
        }
        if (localTypeMap == null){
            localTypeMap = new HashMap();
        }
        
        if (!localClassMap.containsKey(finder.declFound())){
            int nextAvailNum = 1;
            polyglot.types.ClassType outerToMatch = type.outer();
            while (outerToMatch.isNested()){
                outerToMatch = outerToMatch.outer();
            }

            if (!localTypeMap.isEmpty()){
                Iterator matchIt = localTypeMap.keySet().iterator();
                while (matchIt.hasNext()){
                    polyglot.types.ClassType pType = (polyglot.types.ClassType)((polyglot.util.IdentityKey)matchIt.next()).object();
                    polyglot.types.ClassType outerMatch = pType.outer();
                    while (outerMatch.isNested()){
                        outerMatch = outerMatch.outer();
                    }
                    if (outerMatch.equals(outerToMatch)){
                        int numFound = getLocalClassNum((String)localTypeMap.get(new polyglot.util.IdentityKey(pType)), finder.declFound().decl().name());
                        if (numFound >= nextAvailNum){
                            nextAvailNum = numFound+1;
                        }
                    }
                }
            }

            String realName = outerToMatch.fullName()+"$"+nextAvailNum+finder.declFound().decl().name();
            localClassMap.put(finder.declFound(), realName);
            localTypeMap.put(new polyglot.util.IdentityKey(type), realName);
            addNameToAST(realName);
            //soot.SootResolver.v().referencedClass(realName);
        }
    }

    private static final int NO_MATCH = 0;
    
    private int getLocalClassNum(String realName, String simpleName){
        // a local inner class is named outer$NsimpleName where outer 
        // is the very outer most class
        int dIndex = realName.indexOf("$");
        int nIndex = realName.indexOf(simpleName, dIndex);
        if (nIndex == -1) return NO_MATCH;
        if (dIndex == -1) {
            throw new RuntimeException("Matching an incorrectly named local inner class: "+realName);
        }
        return (new Integer(realName.substring(dIndex+1, nIndex))).intValue();
    }
    
    private int getAnonClassNum(String realName){
        // a anon inner class is named outer$N where outer 
        // is the very outer most class
        int dIndex = realName.indexOf("$");
        if (dIndex == -1) {
            throw new RuntimeException("Matching an incorrectly named anon inner class: "+realName);
        }
        return (new Integer(realName.substring(dIndex+1))).intValue();
    }
    

    /**
     * ClassToSourceMap is for classes whos names don't match the source file
     * name - ex: multiple top level classes in a single file
     */
    private void addToClassToSourceMap(String className, String sourceName) {
            
        if (classToSourceMap == null){
            classToSourceMap = new HashMap();
        }
        classToSourceMap.put(className, sourceName);
    }
    

    public boolean hasClassInnerTag(soot.SootClass sc, String innerName){
        Iterator it = sc.getTags().iterator();
        while (it.hasNext()){
            soot.tagkit.Tag t = (soot.tagkit.Tag)it.next();
            if (t instanceof soot.tagkit.InnerClassTag) {
                soot.tagkit.InnerClassTag tag = (soot.tagkit.InnerClassTag)t;
                if (tag.getInnerClass().equals(innerName)) return true;
            }
        }
        return false;
    }
    
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
    
    private void handleFinalLocals(polyglot.ast.ClassMember member){
        MethodFinalsChecker mfc = new MethodFinalsChecker();
        member.visit(mfc);
        AnonLocalClassInfo alci = new AnonLocalClassInfo();
        if (member instanceof polyglot.ast.ProcedureDecl){
            polyglot.ast.ProcedureDecl procedure = (polyglot.ast.ProcedureDecl)member;
            //alci.finalLocals(getFinalLocalsAvail(procedure));
            // not sure if this will break deep nesting
            alci.finalLocals(mfc.finalLocals());
            if (procedure.flags().isStatic()){
                alci.inStaticMethod(true);
            }
        }
        else if (member instanceof polyglot.ast.FieldDecl){
            alci.finalLocals(new ArrayList());
            if (((polyglot.ast.FieldDecl)member).flags().isStatic()){
                alci.inStaticMethod(true);
            }
        }
        else if (member instanceof polyglot.ast.Initializer){
            // for now don't make final locals avail in init blocks
            // need to test this
            alci.finalLocals(mfc.finalLocals());
            if (((polyglot.ast.Initializer)member).flags().isStatic()){
                alci.inStaticMethod(true);
            }
        }
        if (finalLocalInfo == null){
            finalLocalInfo = new HashMap();
        }
        Iterator it = mfc.inners().iterator();
        while (it.hasNext()){
            
            polyglot.types.ClassType cType = (polyglot.types.ClassType)((polyglot.util.IdentityKey)it.next()).object();
            AnonLocalClassInfo info = new AnonLocalClassInfo();
            info.inStaticMethod(alci.inStaticMethod());
            info.finalLocals(alci.finalLocals());
            finalLocalInfo.put(new polyglot.util.IdentityKey(cType), info);
        }
    }

    private ArrayList getFinalLocalsAvail(polyglot.ast.ClassMember member){
        ArrayList finalsAvail = new ArrayList();
        if (member instanceof polyglot.ast.ProcedureDecl){
            polyglot.ast.ProcedureDecl proc = (polyglot.ast.ProcedureDecl)member;
            if (proc.formals() != null){
                Iterator formalsIt = proc.formals().iterator();
                while (formalsIt.hasNext()){
                    polyglot.ast.Formal formal = (polyglot.ast.Formal)formalsIt.next();
                    if (formal.localInstance().flags().isFinal()){
                        finalsAvail.add(new polyglot.util.IdentityKey(formal.localInstance()));
                    }
                }
                
            }
        }
        polyglot.ast.Block fBody;
        if (member instanceof polyglot.ast.ProcedureDecl){
            polyglot.ast.ProcedureDecl proc = (polyglot.ast.ProcedureDecl)member;
            fBody = proc.body();
        }
        else if (member instanceof polyglot.ast.Initializer){
            polyglot.ast.Initializer initializer = (polyglot.ast.Initializer)member;
            fBody = initializer.body();
        
        }
        else {
            throw new RuntimeException("Only handle final locals for procedures and initializers!");
        }
        if ((fBody != null) && (fBody.statements() != null)){
            Iterator stmtsIt = fBody.statements().iterator();
            while (stmtsIt.hasNext()){
                Object next = stmtsIt.next();
                if (next instanceof polyglot.ast.LocalDecl){
                    polyglot.ast.LocalDecl decl = (polyglot.ast.LocalDecl)next;
                    if (decl.localInstance().flags().isFinal()){
                        finalsAvail.add(new polyglot.util.IdentityKey(decl.localInstance()));
                    }
                }
            }
        }
        return finalsAvail;
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
        handleFinalLocals(initializer);
    }
    
    private soot.SootMethod createSootConstructor(String name, polyglot.types.Flags flags, ArrayList parameters, ArrayList exceptions) {
        
        int modifier = Util.getModifier(flags);

        soot.SootMethod method = new soot.SootMethod(name, parameters, soot.VoidType.v(), modifier, exceptions);

        return method;
    }
    
    public BiMap getAnonClassMap(){
        return anonClassMap;
    }

    public BiMap getLocalClassMap(){
        return localClassMap;
    }
    
    public HashMap getAnonTypeMap(){
        return anonTypeMap;
    }

    public HashMap getLocalTypeMap(){
        return localTypeMap;
    }
  
    public HashMap finalLocalInfo(){
        return finalLocalInfo;
    }

    public int getNextPrivateAccessCounter(){
        int res = privateAccessCounter;
        privateAccessCounter++;
        return res;
    }

    public ArrayList getHasOuterRefInInit(){
        return hasOuterRefInInit;
    }

    public HashMap specialAnonMap(){
        return specialAnonMap;
    }

    public void hierarchy(soot.FastHierarchy fh){
        hierarchy = fh;
    }
    
    public soot.FastHierarchy hierarchy(){
        return hierarchy;
    }

    private HashMap innerClassInfoMap;
   
    public HashMap getInnerClassInfoMap(){
        return innerClassInfoMap;
    }
    
}

