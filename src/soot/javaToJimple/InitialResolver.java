package soot.javaToJimple;
import soot.*;
import java.util.*;

public class InitialResolver {

    private polyglot.ast.Node astNode;
    private soot.SootClass sootClass;
    private ArrayList staticFieldInits;
    private ArrayList fieldInits;
    private HashMap fieldMap;
    private HashMap sourceToClassMap;
    private ArrayList initializerBlocks;
    private ArrayList staticInitializerBlocks;
	private polyglot.frontend.Compiler compiler;
    private polyglot.util.Position currentClassDeclPos;
    private BiMap anonClassMap;   // maps New to SootClass (name)
    private HashMap anonTypeMap;    //maps polyglot types to soot types
    private BiMap localClassMap;  // maps LocalClassDecl to SootClass (name)
    private HashMap localTypeMap;   // maps polyglot types to soot types
    private int privateAccessCounter = 0;
    private HashMap privateAccessMap;
    //private HashMap finalsMap;
    private HashMap finalLocalInfo; // new or lcd mapped to list of final locals avail in current meth and the whether its static
    private HashMap newToOuterMap;    
   
    
    public InitialResolver(soot.Singletons.Global g){}
    public static InitialResolver v() {
        return soot.G.v().soot_javaToJimple_InitialResolver();
    }
    

    
    /**
     * Invokes polyglot and gets the AST for the source given in fullPath
     */
    public void formAst(String fullPath, List locations){
    
        //System.out.println("full source path: "+fullPath);
        JavaToJimple jtj = new JavaToJimple();
        polyglot.frontend.ExtensionInfo extInfo = jtj.initExtInfo(fullPath, locations);
        // only have one compiler - for memory issues
        if (compiler == null) {
            //System.out.println("make new polyglot compiler");
		    compiler = new polyglot.frontend.Compiler(extInfo);
        }
        // build ast
        astNode = jtj.compile(compiler, fullPath, extInfo);
    }

    public void setAst(polyglot.ast.Node ast) {
	astNode = ast;
    }
   
    private void addSourceFileTag(soot.SootClass sc){
        if (sc.getTag("SourceFileTag") != null) return;
        String name = Util.getSourceFileOfClass(sc);


        // all classes may be in map 
        if (soot.SourceLocator.v().getSourceToClassMap() != null) {
            if (soot.SourceLocator.v().getSourceToClassMap().get(name) != null) {
                name = (String)soot.SourceLocator.v().getSourceToClassMap().get(name);
            }
        }

        // add file extension
        name += ".java";
        sc.addTag(new soot.tagkit.SourceFileTag(name));
        //System.out.println("added to sc: "+sc.getName()+" sourcefiletag: "+name);
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
        
        // find and resolve anonymous classes
        resolveAnonClasses();
        // find and resolve local classes
        resolveLocalClasses();

        Iterator it = typeListBuilder.getList().iterator();
        while (it.hasNext()) {
            polyglot.types.Type type = (polyglot.types.Type)it.next();
            
            // ignore primitives
            if (type.isPrimitive()) continue;
              
            // ignore non class types
            if (!type.isClass()) continue;
                
               
            polyglot.types.ClassType classType = (polyglot.types.ClassType)type;
            
            resolveClassType(classType);
            /*soot.Type sootClassType;
            
            // will find these separately
            if (classType.isLocal()) continue;
                 
            if (classType.isAnonymous()) continue;

            // fix class names of inner member classes
            if (classType.isNested()){
                
                className = classType.fullName();
                
                while (classType.isNested()){
                    SootResolver.v().assertResolvedClass(fixInnerClassName(classType.outer()));
                    StringBuffer sb = new StringBuffer(className);
                    
                    int lastDot = className.lastIndexOf(".");
                    if (lastDot != -1) {
                        sb.replace(lastDot, lastDot+1, "$");
                        className = sb.toString();
                    }
                    classType = classType.outer();
                }
                    
            }
                
            else {
                className = classType.fullName();
            }
            
            if (!className.equals("java.lang.String[]")) {
                    
                //System.out.println("Will resolve class: "+className);    
                SootResolver.v().assertResolvedClass(className);
            }*/
            
        }
        
        // resolve Object, StrungBuffer and inner classes
        SootResolver.v().assertResolvedClass("java.lang.Object");
        SootResolver.v().assertResolvedClass("java.lang.StringBuffer");
        SootResolver.v().assertResolvedClass("java.lang.Throwable");
        
    }
    
    private void resolveClassType(polyglot.types.ClassType classType){
        
        soot.Type sootClassType;
            
        // will find these separately prior to this loop
        if (classType.isLocal()) return;
                 
        if (classType.isAnonymous()) return;

        // fix class names of inner member classes
        if (classType.isNested()){
       
            resolveClassType(classType.outer());
        }

        
        sootClassType = Util.getSootType(classType);
            
        //System.out.println("will resolve: "+sootClassType);            
        SootResolver.v().assertResolvedClassForType(sootClassType);
    }
    
    // resolves all types and deals with .class literals and asserts
    public void resolveFromJavaFile(soot.SootClass sc) {
        sootClass = sc;

        // add sourcefile tag to Soot class
        addSourceFileTag(sc);
        
        // get types and resolve them in the Scene
        resolveTypes();
        /*TypeListBuilder typeListBuilder = new TypeListBuilder();
        
        astNode.visit(typeListBuilder);

        Iterator it = typeListBuilder.getList().iterator();
        while (it.hasNext()) {
            polyglot.types.Type type = (polyglot.types.Type)it.next();
            
            // ignore primitives
            if (type.isPrimitive()) continue;
              
            // ignore non class types
            if (!type.isClass()) continue;
                
               
            polyglot.types.ClassType classType = (polyglot.types.ClassType)type;
            String className;
            
            // will find these separately
            if (classType.isLocal()) continue;
                 
            if (classType.isAnonymous()) continue;

            // fix class names of inner member classes
            if (classType.isNested()){
                className = classType.fullName();
                
                while (classType.isNested()){
                    SootResolver.v().assertResolvedClass(fixInnerClassName(classType.outer()));
                    StringBuffer sb = new StringBuffer(className);
                    
                    int lastDot = className.lastIndexOf(".");
                    if (lastDot != -1) {
                        sb.replace(lastDot, lastDot+1, "$");
                        className = sb.toString();
                    }
                    classType = classType.outer();
                }
                    
            }
                
            else {
                className = classType.fullName();
            }
            
            if (!className.equals("java.lang.String[]")) {
                    
                //System.out.println("Will resolve class: "+className);    
                SootResolver.v().assertResolvedClass(className);
            }
            
        }
        
        // resolve Object, StrungBuffer and inner classes
        SootResolver.v().assertResolvedClass("java.lang.Object");
        SootResolver.v().assertResolvedClass("java.lang.StringBuffer");
        SootResolver.v().assertResolvedClass("java.lang.Throwable");
        
        // find and resolve anonymous classes
        resolveAnonClasses();
        // find and resolvelocal classes
        resolveLocalClasses();
        */

        // determine is ".class" literal is used
        ClassLiteralChecker classLitChecker = new ClassLiteralChecker();
        astNode.visit(classLitChecker);
        ArrayList classLitList = classLitChecker.getList();
        if (!classLitList.isEmpty()) {
            String methodName = "class$";
            soot.Type methodRetType = soot.RefType.v("java.lang.Class");
            ArrayList paramTypes = new ArrayList();
            paramTypes.add(soot.RefType.v("java.lang.String"));
            if (!sc.declaresMethod(methodName, paramTypes, methodRetType)){
                soot.SootMethod sootMethod = new soot.SootMethod(methodName, paramTypes, methodRetType, soot.Modifier.STATIC);
                ClassLiteralMethodSource mSrc = new ClassLiteralMethodSource();
                sootMethod.setSource(mSrc);
                sc.addMethod(sootMethod);
            }
        }
        Iterator classLitIt = classLitList.iterator();
        while (classLitIt.hasNext()) {
            polyglot.ast.Field classLitField = (polyglot.ast.Field)classLitIt.next();
            // field
            polyglot.ast.Receiver receiver = classLitField.target();
            String fieldName = "class$";
            if (receiver instanceof polyglot.ast.TypeNode) {
                String type = ((polyglot.ast.TypeNode)receiver).type().toString();
                type = type.replace('.', '$');
                fieldName = fieldName+type;
            }
            else {
                throw new RuntimeException("class literal only valid on type nodes");
            }
            soot.Type fieldType = soot.RefType.v("java.lang.Class");
            if (!sc.declaresField(fieldName, fieldType)){
                soot.SootField sootField = new soot.SootField(fieldName, fieldType, soot.Modifier.STATIC);
                sc.addField(sootField);
            }

        }
     
        // determine if assert is used
        AssertStmtChecker asc = new AssertStmtChecker();
        astNode.visit(asc);

        if (asc.isHasAssert()){
            handleAssert();
        }
        
        // create class to source map first 
        // create source file
        if (astNode instanceof polyglot.ast.SourceFile) {
            createSource((polyglot.ast.SourceFile)astNode);
        }
        

    }


    /**
     * Handling for assert stmts - extra fields and methods are needed
     * in the Jimple 
     */
    private void handleAssert(){
        // two extra fields
        sootClass.addField(new soot.SootField("$assertionsDisabled", soot.BooleanType.v(), soot.Modifier.STATIC | soot.Modifier.FINAL));
        sootClass.addField(new soot.SootField("class$"+sootClass.getName(), soot.RefType.v("java.lang.Class"), soot.Modifier.STATIC));
        // two extra methods
        String methodName = "class$";
        soot.Type methodRetType = soot.RefType.v("java.lang.Class");
        ArrayList paramTypes = new ArrayList();
        paramTypes.add(soot.RefType.v("java.lang.String"));
        if (!sootClass.declaresMethod(methodName, paramTypes, methodRetType)){
            soot.SootMethod sootMethod = new soot.SootMethod(methodName, paramTypes, methodRetType, soot.Modifier.STATIC);
            AssertClassMethodSource mSrc = new AssertClassMethodSource();
            sootMethod.setSource(mSrc);
            sootClass.addMethod(sootMethod);
        }
        methodName = "<clinit>";
        methodRetType = soot.VoidType.v();
        paramTypes = new ArrayList();
        if (!sootClass.declaresMethod(methodName, paramTypes, methodRetType)){
            soot.SootMethod sootMethod = new soot.SootMethod(methodName, paramTypes, methodRetType, soot.Modifier.STATIC);
            PolyglotMethodSource mSrc = new PolyglotMethodSource();
            mSrc.hasAssert(true);
            sootMethod.setSource(mSrc);
            sootClass.addMethod(sootMethod);
        }
        else {
            ((soot.javaToJimple.PolyglotMethodSource)sootClass.getMethod(methodName, paramTypes, methodRetType).getSource()).hasAssert(true);
        }
    }
		
    /**
     * Resolve Anonymous Inner Classes - neeed a map between the
     * Anon class bodies and their assigned names
     */
    private void resolveAnonClasses(){
        AnonClassChecker anonClassChecker = new AnonClassChecker();
        astNode.visit(anonClassChecker);
        Iterator keysIt = anonClassChecker.getMap().keySet().iterator();
        while (keysIt.hasNext()) {
            polyglot.types.ClassType outer = (polyglot.types.ClassType)keysIt.next();
            for (int i = 0; i < ((Integer)anonClassChecker.getMap().get(outer)).intValue(); i++) {
                String className = outer.toString()+"$"+(i+1);
                soot.SootResolver.v().assertResolvedClass(className);

                polyglot.ast.New aNew = (polyglot.ast.New)anonClassChecker.getBodyNameMap().getKey(className);
                if (anonTypeMap == null){
                    anonTypeMap = new HashMap();
                }
                //System.out.println("put in anon tyoe map: "+aNew.anonType());
                anonTypeMap.put(new polyglot.util.IdentityKey(aNew.anonType()), className);
            }
            
        }
        anonClassMap = anonClassChecker.getBodyNameMap();
    }
    
    /**
     * Resolve Local Inner Classes - need a map between the 
     * assigned name and Local class decl
     */
    private void resolveLocalClasses() {
        LocalClassChecker localClassChecker = new LocalClassChecker();
        astNode.visit(localClassChecker);
        Iterator keysIt = localClassChecker.getMap().keySet().iterator();
        while (keysIt.hasNext()) {
            polyglot.types.ClassType outer = (polyglot.types.ClassType)keysIt.next();
            HashMap innerMap = (HashMap)localClassChecker.getMap().get(outer);
            Iterator valsIt = innerMap.keySet().iterator();
            while (valsIt.hasNext()) {
                String innerName = (String)valsIt.next();
                int count = ((Integer)innerMap.get(innerName)).intValue();
                for (int i = 1; i <= count; i++) {
                    String className = outer.toString()+"$"+i+"$"+innerName;
                    soot.SootResolver.v().assertResolvedClass(className);
                    //System.out.println("class name: "+className);
                    polyglot.ast.LocalClassDecl lcd = (polyglot.ast.LocalClassDecl)localClassChecker.getClassMap().getKey(className);
                
                    if (localTypeMap == null){
                        localTypeMap = new HashMap();
                    }
                    //System.out.println("adding to localTypeMap: "+lcd.decl().type()+" and className: "+className);
                    localTypeMap.put(new polyglot.util.IdentityKey(lcd.decl().type()), className);
                }
            }
        }
        localClassMap = localClassChecker.getClassMap();
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
        //System.out.println("trying to create source for: "+simpleName);
        /*if (sootClass.getPackageName() != null) {
            simpleName = simpleName.substring(simpleName.lastIndexOf(".")+1, simpleName.length());
        }
        */
        //System.out.println("soot class name: " +simpleName);
        
        Iterator declsIt = source.decls().iterator();
        boolean found = false;

        // first look in top-level decls
		while (declsIt.hasNext()){
			Object next = declsIt.next();
            //System.out.println("next top-level decl: "+next);
			if (next instanceof polyglot.ast.ClassDecl) {
                //System.out.println("source decl is class decl: "+next);
                polyglot.types.ClassType nextType = ((polyglot.ast.ClassDecl)next).type();
                if (Util.getSootType(nextType).equals(sootClass.getType())){
                    //((polyglot.ast.ClassDecl)next).name().equals(simpleName)){
				    createClassDecl((polyglot.ast.ClassDecl)next);
                    found = true;
                }
                else {
                    // if not already there put cdecl name in class to source file map
                    // its actually a map from class names to the corresponding source file
                    if (((polyglot.ast.ClassDecl)next).type().isTopLevel() && !((polyglot.ast.ClassDecl)next).flags().isPublic()){                    
                        //System.out.println("class to src map: "+((polyglot.ast.ClassDecl)next).name()+" and "+sootClass.getName());
                        addToClassToSourceMap(((polyglot.ast.ClassDecl)next).name(), sootClass.getName());                
                    }
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
                //System.out.println("nextDecl: nested decl: "+nextDecl);
                polyglot.types.ClassType type = (polyglot.types.ClassType)nextDecl.type();
                //System.out.println("nested class type: "+type);
                if (type.isLocal() && !type.isAnonymous()) {
                   
                    if (localClassMap.containsVal(simpleName)){
                        createClassDecl(((polyglot.ast.LocalClassDecl)localClassMap.getKey(simpleName)).decl());
                        found = true;
                    }
                }
                else {
               
                    //String realName = fixInnerClassName(type);
                    //System.out.println("real name fixed: "+realName);
                    /*String outerName = type.outer().toString();
                    while (type.outer().isNested() || type.outer().isInnerClass()){
                        outerName = type.outer().outer().toString()+"$"+outerName;
                        type = type.outer();
                    }
                    String realName = outerName+"$"+nextDecl.name();
                    System.out.println("realName: "+realName);*/
                    if (Util.getSootType(type).equals(sootClass.getType())){
                        //realName.equals(sootClass.getName())){
                        createClassDecl(nextDecl);
                        found = true;
                    }
                }
            }

            if (!found) {
                // assume its anon class (only option left) 
                //System.out.println("assuming anon inner sootClass name: "+sootClass.getName());
                //
                if (anonClassMap.containsVal(simpleName)){
                    
                    polyglot.ast.New aNew = (polyglot.ast.New)anonClassMap.getKey(simpleName);
                    createAnonClassDecl(aNew);
                    createClassBody(aNew.body());
                }                    
                    
                /*int index = sootClass.getName().indexOf("$");
                int length = sootClass.getName().length();
                int count = 0;
                try {
                    count = (new Integer(sootClass.getName().substring(index+1, length))).intValue();
               
                }
                catch(NumberFormatException e){
                    throw new RuntimeException("Trying to process: "+sootClass.getName()+" as an anonymous inner class and it fails!");
                }
                int counter = 1;

                Iterator anonIt = nestedClassBuilder.getAnonClassBodyList().iterator();
                while (anonIt.hasNext()) {
                    polyglot.ast.New next = (polyglot.ast.New)anonIt.next();
                    String outerName = (String)newToOuterMap.get(next);
                    if (counter == count) {
                        
                        createAnonClassDecl(next.objectType().type(), outerName, next);
                        createClassBody(next.body());

                    }
                    counter++;
                }*/
            }
        }

    }

    /**
     * ClassToSourceMap is for classes whos names don't match the source file
     * name - ex: multiple top level classes in a single file
     */
    private void addToClassToSourceMap(String className, String sourceName) {
            
        if (sourceToClassMap == null) {
            sourceToClassMap = new HashMap();
        }
            
        if (soot.SourceLocator.v().getSourceToClassMap() == null) {
            soot.SourceLocator.v().setSourceToClassMap(sourceToClassMap);
        }
            
        if (!soot.SourceLocator.v().getSourceToClassMap().containsKey(className)) {
            //System.out.println("adding to classSource map className: "+className+" and source: "+sourceName);
            soot.SourceLocator.v().addToSourceToClassMap(className, sourceName);
        }
    }
    
    /**
     * creates the Jimple for an anon class - in the AST there is no class 
     * decl for anon classes - the revelant fields and methods are 
     * created 
     */
    private void createAnonClassDecl(polyglot.ast.New aNew) {
        
        soot.SootClass typeClass = ((soot.RefType)Util.getSootType(aNew.objectType().type())).getSootClass();
       
        //String outerName = Util.getSootType(aNew.anonType().outer()).toString();
        //System.out.println("outer name for anon: "+outerName);
        // set superclass
        if (typeClass.isInterface()){
        //if (aNew.objectType().type().flags().isInterface()){
            sootClass.addInterface(typeClass);
            sootClass.setSuperclass(soot.Scene.v().getSootClass("java.lang.Object"));
        }
        else {
            sootClass.setSuperclass(typeClass);
        }

        // needs to be done for local also
        //ArrayList finalLocalsFields = new ArrayList();
        ArrayList params = new ArrayList();
        //ArrayList allParams = new ArrayList();
        /*AnonLocalClassInfo info = null;
        if (finalsMap != null){
            if (finalsMap.containsKey(aNew)){
                info = (AnonLocalClassInfo)finalsMap.get(aNew);
            }
        }
                
        // add this field
        if (!info.inStaticMethod()){ 
            //only if enclosing method is not static
            soot.SootField field = new soot.SootField("this$0", soot.Scene.v().getSootClass(outerName).getType(), soot.Modifier.FINAL | soot.Modifier.PRIVATE);
            sootClass.addField(field);
        }
        
        // anon classes need to be able to access locals from 
        // the outer methods
        // in which they are declared
        Iterator fIt = info.finalFields().iterator();
        while (fIt.hasNext()){
            polyglot.types.LocalInstance li = (polyglot.types.LocalInstance)((polyglot.util.IdentityKey)fIt.next()).object();
                    
            soot.SootField sf = new soot.SootField("val$"+li.name(), Util.getSootType(li.type()), soot.Modifier.FINAL | soot.Modifier.PRIVATE);
            finalLocalsFields.add(sf);
            sootClass.addField(sf);
        }
       
        // handle parameters
        if (!info.inStaticMethod()){
            params.add(soot.Scene.v().getSootClass(outerName).getType());
        }
        ArrayList finalLocals = info.finalFields();
        Iterator fIt2 = finalLocals.iterator();
        while (fIt2.hasNext()){
            params.add(Util.getSootType(((polyglot.types.LocalInstance)((polyglot.util.IdentityKey)fIt2.next()).object()).type()));
        }
       */
            
        soot.SootMethod method;
        // if interface there are no extra params
        //if (aNew.objectType().type().flags().isInterface()){
        if (typeClass.isInterface()){
            method = new soot.SootMethod("<init>", params, soot.VoidType.v());
            //AnonClassInitMethodSource src = new AnonClassInitMethodSource();
            //src.outerClassType(soot.Scene.v().getSootClass(outerName).getType());
            //System.out.println("should be ?? setting finals  in interface");
            //src.setFieldList(finalLocalsFields);
            //src.inStaticMethod(info.inStaticMethod());
            //method.setSource(src);
            //sootClass.addMethod(method);
        }
        else {
            // otherwise add outer class param if not in static meth
            //if (!info.inStaticMethod()){
            //    allParams.add(soot.Scene.v().getSootClass(outerName).getType());
            //}
            Iterator aIt = aNew.arguments().iterator();
            while (aIt.hasNext()){
                polyglot.types.Type pType = ((polyglot.ast.Expr)aIt.next()).type();
                params.add(Util.getSootType(pType));
            }
            //ArrayList finalLocals2 = info.finalFields();

                    
            /*Iterator fIt3 = finalLocals2.iterator();
            while (fIt3.hasNext()){
                allParams.add(Util.getSootType(((polyglot.types.LocalInstance)((polyglot.util.IdentityKey)fIt3.next()).object()).type()));
            }*/
            method = new soot.SootMethod("<init>", params, soot.VoidType.v());
        }
        
        AnonClassInitMethodSource src = new AnonClassInitMethodSource();
            //src.outerClassType(soot.Scene.v().getSootClass(outerName).getType());
            //System.out.println("setting finals not in interface");
            //src.setFieldList(finalLocalsFields);
            //System.out.println("in static: "+info.inStaticMethod());
            //src.inStaticMethod(info.inStaticMethod());
        method.setSource(src);
        sootClass.addMethod(method);
   
        AnonLocalClassInfo info = (AnonLocalClassInfo)finalLocalInfo.get(aNew);
        //System.out.println("in static meth: "+info.inStaticMethod());
        if (!info.inStaticMethod()){
            //System.out.println("is nested: "+aNew.anonType().isNested());
            //System.out.println("anon not in static method");
            addOuterClassThisRefToInit(aNew.anonType().outer());
            addOuterClassThisRefField(aNew.anonType().outer());
        }
        src.inStaticMethod(info.inStaticMethod());
        src.setFieldList(addFinalLocals(aNew.body(), info.finalLocals(), aNew, info));
        src.outerClassType(Util.getSootType(aNew.anonType().outer()));
        //}
    }
        
    private ArrayList addFinalLocals(polyglot.ast.ClassBody cBody, ArrayList finalLocals, polyglot.ast.Node nodeKey, AnonLocalClassInfo info){
        ArrayList finalFields = new ArrayList();
        
        LocalUsesChecker luc = new LocalUsesChecker();
        cBody.visit(luc);
        Iterator localsNeededIt = luc.getLocals().iterator();
        ArrayList localsUsed = new ArrayList();
        while (localsNeededIt.hasNext()){
            polyglot.types.LocalInstance li = (polyglot.types.LocalInstance)((polyglot.util.IdentityKey)localsNeededIt.next()).object();
            if (finalLocals.contains(new polyglot.util.IdentityKey(li))){
                
                // add as param for init
                Iterator it = sootClass.getMethods().iterator();
                while (it.hasNext()){
                    soot.SootMethod meth = (soot.SootMethod)it.next();
                    if (meth.getName().equals("<init>")){
                        //System.out.println("adding final local type: "+li.type());
                        meth.getParameterTypes().add(Util.getSootType(li.type()));
                    }
                }
                
                // add field
                soot.SootField sf = new soot.SootField("val$"+li.name(), Util.getSootType(li.type()), soot.Modifier.FINAL | soot.Modifier.PRIVATE);
                sootClass.addField(sf);
                finalFields.add(sf);
                
                localsUsed.add(new polyglot.util.IdentityKey(li));
            }
        }

        //System.out.println("final locals replaced: "+localsUsed);
        info.finalLocals(localsUsed);
        finalLocalInfo.put(nodeKey, info);
        return finalFields;
    }
    
    /**
     * Class Declaration Creation
     */
    private void createClassDecl(polyglot.ast.ClassDecl cDecl){
       
        // modifiers
        polyglot.types.Flags flags = cDecl.flags();
        addModifiers(flags);
	    
        // super class
        if (cDecl.superClass() == null) {
			soot.SootClass superClass = soot.Scene.v().getSootClass ("java.lang.Object"); 
			sootClass.setSuperclass(superClass);
		}
		else {

            /*String superClassName = cDecl.superClass().toString();
            if (((polyglot.types.ClassType)cDecl.superClass().type()).isNested()){
                superClassName = fixInnerClassName((polyglot.types.ClassType)cDecl.superClass().type());        
            }
			soot.SootClass superClass = soot.Scene.v().getSootClass (superClassName); 
            sootClass.setSuperclass(superClass);*/
            sootClass.setSuperclass(((soot.RefType)Util.getSootType(cDecl.superClass().type())).getSootClass());
		
		}

       
        // implements 
        Iterator interfacesIt = cDecl.interfaces().iterator();
        while (interfacesIt.hasNext()) {
            polyglot.ast.TypeNode next = (polyglot.ast.TypeNode)interfacesIt.next();
            sootClass.addInterface(soot.Scene.v().getSootClass(next.toString()));
        }
	    
        currentClassDeclPos = cDecl.position();
		createClassBody(cDecl.body());

        // handle initialization of fields 
        // static fields init in clinit
        // other fields init in init
        if ((fieldInits != null) || (initializerBlocks != null)) {
            Iterator methodsIt = sootClass.getMethods().iterator();
            while (methodsIt.hasNext()) {
                soot.SootMethod next = (soot.SootMethod)methodsIt.next();
                if (next.getName().equals("<init>")){
                
                    ((soot.javaToJimple.PolyglotMethodSource)next.getSource()).setInitializerBlocks(initializerBlocks);
                    ((soot.javaToJimple.PolyglotMethodSource)next.getSource()).setFieldInits(fieldInits);
                }
            }
        }
        
        if ((staticFieldInits != null) || (staticInitializerBlocks != null)) {
            soot.SootMethod clinitMethod;
            if (!sootClass.declaresMethod("<clinit>", new ArrayList(), soot.VoidType.v())) {
                clinitMethod = new soot.SootMethod("<clinit>", new ArrayList(), soot.VoidType.v(), soot.Modifier.STATIC, new ArrayList());
                
                sootClass.addMethod(clinitMethod);
                clinitMethod.setSource(new soot.javaToJimple.PolyglotMethodSource());
            }
            else {
                clinitMethod = sootClass.getMethod("<clinit>", new ArrayList(), soot.VoidType.v());
            
            }
            ((PolyglotMethodSource)clinitMethod.getSource()).setStaticFieldInits(staticFieldInits);
            ((PolyglotMethodSource)clinitMethod.getSource()).setStaticInitializerBlocks(staticInitializerBlocks);

        }

       
        // add final locals to local inner classes inits
        if (cDecl.type().isLocal()) {
            AnonLocalClassInfo info = (AnonLocalClassInfo)finalLocalInfo.get(cDecl);
            ArrayList finalsList = addFinalLocals(cDecl.body(), info.finalLocals(), cDecl, info); 
            Iterator it = sootClass.getMethods().iterator();
            while (it.hasNext()){
                soot.SootMethod meth = (soot.SootMethod)it.next();
                if (meth.getName().equals("<init>")){
                    //System.out.println("set finals list for local: "+finalsList);
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
   
    private void addOuterClassThisRefToInit(polyglot.types.Type outerType){
        soot.Type outerSootType = Util.getSootType(outerType);
        Iterator it = sootClass.getMethods().iterator();
        while (it.hasNext()){
            soot.SootMethod meth = (soot.SootMethod)it.next();
            if (meth.getName().equals("<init>")){
                meth.getParameterTypes().add(0, outerSootType);
            }
        }
    }
    
    private void addOuterClassThisRefField(polyglot.types.Type outerType){
        soot.Type outerSootType = Util.getSootType(outerType);
        soot.SootField field = new soot.SootField("this$0", outerSootType, soot.Modifier.PRIVATE | soot.Modifier.FINAL);
        sootClass.addField(field);
        //System.out.println("added this$0 to : "+sootClass.getName());
    }
    
    /**
     * changes inner class names to make the last "."'s be a $'s
     */
    /*private String fixInnerClassName(polyglot.types.ClassType innerClass){
                
        String fullName = innerClass.fullName();
                
        while (innerClass.isNested()){
                    
            StringBuffer sb = new StringBuffer(fullName);
                    
            int lastDot = fullName.lastIndexOf(".");
            if (lastDot != -1) {
                sb.replace(lastDot, lastDot+1, "$");
                fullName = sb.toString();
            }
            innerClass = innerClass.outer();
        }

        return fullName;
    }*/
   
    /**
     * adds modifiers
     */
	private void addModifiers(polyglot.types.Flags flags){
		int modifiers = Util.getModifier(flags);
		sootClass.setModifiers(modifiers);
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
                // determine and create acces methods for used private access
                // in inner classes
                //System.out.println("will handle private accessors for: "+next);
                //handlePrivateAccessors((polyglot.ast.ClassDecl)next);
                // will do this any way to make it possible for local and anon
			}
            else if (next instanceof polyglot.ast.Initializer) {
                createInitializer((polyglot.ast.Initializer)next);
            }
            else {
                throw new RuntimeException("Class Body Member not implemented");
			}
        }
        handlePrivateAccessors(classBody);
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
           
        //System.out.println("uses of private access: "+uses);
        Iterator listIt = uses.iterator();
        while (listIt.hasNext()) {
            Object nextInst = listIt.next();
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
                PrivateFieldAccMethodSource pfams = new PrivateFieldAccMethodSource();
                //pfams.setFieldInst((polyglot.types.FieldInstance)inst);
                pfams.fieldName(((polyglot.types.FieldInstance)inst).name());
                pfams.fieldType(Util.getSootType(((polyglot.types.FieldInstance)inst).type()));
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
            /*String nextException = throwType.toString();
            if (((polyglot.types.ClassType)throwType).isNested()){
                nextException = fixInnerClassName((polyglot.types.ClassType)throwType);
            }*/
            //exceptions.add(soot.Scene.v().getSootClass(nextException));
            exceptions.add(((soot.RefType)Util.getSootType(throwType)).getSootClass());
		}
        return exceptions; 
    }
    
    /**
     * looks after pos tags for methods and constructors
     */
    private void finishProcedure(polyglot.ast.ProcedureDecl procedure, soot.SootMethod sootMethod){
        
		addProcedureToClass(sootMethod);
	
        if (procedure.position() != null){
            /*if (procedure.position() instanceof soot.javaToJimple.jj.DPosition){
                soot.javaToJimple.jj.DPosition dpos = (soot.javaToJimple.jj.DPosition)procedure.position();
        
                if (procedure.body() != null) {
                    if (procedure.body().position() != null) {
                        if (procedure.body().position() instanceof soot.javaToJimple.jj.DPosition){
                        soot.javaToJimple.jj.DPosition bodyDpos = (soot.javaToJimple.jj.DPosition)procedure.body().position();
                        Util.addLnPosTags(sootMethod, dpos.line(), bodyDpos.endLine(), dpos.column(), bodyDpos.endCol());
                        }
                    }
                }*/
                if (procedure.body() != null) {
                    if (procedure.body().position() != null) {
                        Util.addLnPosTags(sootMethod, procedure.position().line(), procedure.body().position().endLine(), procedure.position().column(), procedure.body().position().endColumn());
                    }
                }
                
        }

        //handle final local map for local and anon classes
        MethodFinalsChecker mfc = new MethodFinalsChecker();
        procedure.visit(mfc);
        AnonLocalClassInfo alci = new AnonLocalClassInfo();
        alci.finalLocals(mfc.finalLocals());
        if (soot.Modifier.isStatic(sootMethod.getModifiers())){
            //System.out.println("method is static");
            alci.inStaticMethod(true);
        }
        Iterator it = mfc.inners().iterator();
        while (it.hasNext()){
            if (finalLocalInfo == null){
                finalLocalInfo = new HashMap();
            }
            finalLocalInfo.put(it.next(), alci);
        }
        
        /*mfc.setCurrentSootClass(sootClass.getName());
        if (procedure.body() != null){
            procedure.body().visit(mfc);
        }
        if (newToOuterMap == null){
            newToOuterMap = new HashMap();
        }
        if (mfc.getNewToOuter().keySet() != null) {
            Iterator newIt = mfc.getNewToOuter().keySet().iterator();
            while (newIt.hasNext()){
                Object next = newIt.next();
                newToOuterMap.put(next, mfc.getNewToOuter().get(next));
            }
        }
        if ((mfc.getLocals() != null) && (mfc.getClassNames() != null)){
            if (finalsMap == null) {// this should maybe always be made new here
                finalsMap = new HashMap();
            }
            
            Iterator it = mfc.getClassNames().iterator();
            while (it.hasNext()){
                AnonLocalClassInfo alci = new AnonLocalClassInfo();
                ArrayList finalLocals = new ArrayList();
                // add as first param indictator of method being static
                if (soot.Modifier.isStatic(sootMethod.getModifiers())){
                    alci.inStaticMethod(true);
                }
                polyglot.ast.New key = (polyglot.ast.New)it.next();
                LocalUsesChecker luc = new LocalUsesChecker();
                key.body().visit(luc);
                Iterator localsIt = luc.getLocals().iterator();
                while (localsIt.hasNext()){
                    polyglot.types.LocalInstance testLocal = (polyglot.types.LocalInstance)((polyglot.util.IdentityKey)localsIt.next()).object();
                    if (!luc.getLocalDecls().contains(new polyglot.util.IdentityKey(testLocal))){
                        finalLocals.add(new polyglot.util.IdentityKey(testLocal));
                    }
                }
                alci.finalFields(finalLocals);
                finalsMap.put(key, alci);
            }
        }*/

        PolyglotMethodSource mSrc = new PolyglotMethodSource(procedure.body(), procedure.formals());
        //mSrc.setFinalsMap(finalsMap);
        //mSrc.setNewToOuterMap(newToOuterMap);
        mSrc.setPrivateAccessMap(privateAccessMap);

        // will no longer add anon/local maps to methodsource
        /*if (localClassMap != null) {
            mSrc.setLocalClassMap(localClassMap);
        }
        if (anonClassMap != null) {
            mSrc.setAnonClassMap(anonClassMap);
        }*/
        
        sootMethod.setSource(mSrc);
        
	}

    private void addProcedureToClass(soot.SootMethod method) {
        sootClass.addMethod(method);
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
    
    
	private soot.SootMethod createSootMethod(String name, polyglot.types.Flags flags , polyglot.types.Type returnType, ArrayList parameters, ArrayList exceptions){
        //System.out.println("making soot meth: "+name+" modifiers: "+flags);
        
		int modifier = Util.getModifier(flags);
        //System.out.println("soot modifier: "+modifier);
		soot.Type sootReturnType = Util.getSootType(returnType);

		soot.SootMethod method = new soot.SootMethod(name, parameters, sootReturnType, modifier, exceptions);
		return method;
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
        Util.addLnPosTags(sootField, field.position());
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

    private soot.SootMethod createSootConstructor(String name, polyglot.types.Flags flags, ArrayList parameters, ArrayList exceptions) {
        
        int modifier = Util.getModifier(flags);

        soot.SootMethod method = new soot.SootMethod(name, parameters, soot.VoidType.v(), modifier);

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
}
