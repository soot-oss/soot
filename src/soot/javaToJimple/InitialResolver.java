package soot.javaToJimple;
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
    private HashMap anonClassMap;
    private HashMap localClassMap;
    private int privateAccessCounter = 0;
    private HashMap privateAccessMap;
    
    
    public InitialResolver(){
    }

    public void formAst(String fullPath, List locations){
   
        JavaToJimple jtj = new JavaToJimple();
        polyglot.frontend.ExtensionInfo extInfo = jtj.initExtInfo(fullPath, locations);
        // only have one compiler - for memory issues
        if (compiler == null) {
		    compiler = new polyglot.frontend.Compiler(extInfo);
        }
        // build ast
        astNode = jtj.compile(compiler, fullPath, extInfo);
        //System.out.println("astNode computed");
    }
    
    public void resolveFromJavaFile(soot.SootClass sc, soot.SootResolver resolver) {
        //System.out.println("Creating Class: "+sc.getName());
        sootClass = sc;

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
            String className;
            
            // will find these separately
            if (classType.isLocal()) continue;
                 
            if (classType.isAnonymous()) continue;

            // fix class names of inner member classes
            if (classType.isNested()){
                System.out.println("class is nested");
                System.out.println(classType.kind());
                className = classType.fullName();
                
                while (classType.isNested()){
                //System.out.println("classType:"+classType);    
                    System.out.println("want to resolve: "+classType.outer().toString());
                    resolver.assertResolvedClass(classType.outer().toString());
                    //System.out.println("resolved: "+classType.outer().toString());
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
            
            //System.out.println("className: "+className);
            if (!className.equals("java.lang.String[]")) {
                    
                System.out.println("want to resolve: "+className);
                    
                resolver.assertResolvedClass(className);
                //System.out.println("Soot resolved className: "+className);
            }
            
        }
        
        // resolve Object, StrungBuffer and inner classes
        resolver.assertResolvedClass("java.lang.Object");
        resolver.assertResolvedClass("java.lang.StringBuffer");
        resolver.assertResolvedClass("java.lang.Throwable");
        
        // find and resolve anonymous classes
        resolveAnonClasses(resolver);
        // find and resolve local classes
        resolveLocalClasses(resolver);
        
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
            //System.out.println("assert? : "+((soot.javaToJimple.PolyglotMethodSource)sootMethod.getSource()).hasAssert());
        }
        else {
            ((soot.javaToJimple.PolyglotMethodSource)sootClass.getMethod(methodName, paramTypes, methodRetType).getSource()).hasAssert(true);
            //System.out.println("assert? : "+((soot.javaToJimple.PolyglotMethodSource)sootClass.getMethod(methodName, paramTypes, methodRetType).getSource()).hasAssert());
        }
        //System.out.println("handled assert"); 
    }
		
    /**
     * Resolve Anonymous Inner Classes
     */
    private void resolveAnonClasses(soot.SootResolver resolver){
        AnonClassChecker anonClassChecker = new AnonClassChecker();
        astNode.visit(anonClassChecker);
        Iterator keysIt = anonClassChecker.getMap().keySet().iterator();
        while (keysIt.hasNext()) {
            polyglot.types.ClassType outer = (polyglot.types.ClassType)keysIt.next();
            for (int i = 0; i < ((Integer)anonClassChecker.getMap().get(outer)).intValue(); i++) {
                String className = outer.toString()+"$"+(i+1);
                System.out.println("want to resolve: "+className+" for anon class");
                resolver.assertResolvedClass(className);
                //System.out.println("resolved class: "+className);
            }
            
        }
        anonClassMap = anonClassChecker.getBodyNameMap();
    }
    
    /**
     * Resolve Local Inner Classes
     */
    private void resolveLocalClasses(soot.SootResolver resolver) {
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
                for (int i = 0; i < count; i++) {
                    String className = outer.toString()+"$"+count+"$"+innerName;
                    System.out.println("want to resolve: "+className);
                    resolver.assertResolvedClass(className);
                    //System.out.println("resolved class: "+className);
                }
            }
        }
        localClassMap = localClassChecker.getClassMap();
    }

    /**
     * Source Creation
     */
    private void createSource(polyglot.ast.SourceFile source){
			
		
        //System.out.println("name: "+sootClass.getName());
        //System.out.println("package: "+sootClass.getPackageName());
        
        String simpleName = sootClass.getName();
        if (sootClass.getPackageName() != null) {
            simpleName = simpleName.substring(simpleName.lastIndexOf(".")+1, simpleName.length());
        }
        
        Iterator declsIt = source.decls().iterator();
       
        boolean found = false;

		while (declsIt.hasNext()){
			Object next = declsIt.next();
			if (next instanceof polyglot.ast.ClassDecl) {
                //System.out.println("polyglot name: "+((polyglot.ast.ClassDecl)next).name());
                if (((polyglot.ast.ClassDecl)next).name().equals(simpleName)){
				    createClassDecl((polyglot.ast.ClassDecl)next);
                    found = true;
                }
                else {
                    // if not already there put cdecl name in class to source file map
                    addToClassToSourceMap(((polyglot.ast.ClassDecl)next).name(), sootClass.getName());                
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
                //System.out.println("next decl: "+nextDecl.name());
                polyglot.types.ClassType type = (polyglot.types.ClassType)nextDecl.type();
                //System.out.println("type kind: "+type.kind());
                //System.out.println("type: "+type);
                //System.out.println("soot class name: "+sootClass.getName());
                //System.out.println("local class map: "+localClassMap);
                if (type.isLocal() && !type.isAnonymous()) {
                    /*if (nextDecl.name().equals(sootClass.getName())){
                        createClassDecl(nextDecl);
                        found = true;
                    }*/
                   
                    if (localClassMap.containsKey(sootClass.getName())){
                        //System.out.println("local class decl: "+localClassMap.get(sootClass.getName()));
                        createClassDecl((polyglot.ast.ClassDecl)localClassMap.get(sootClass.getName()));
                        found = true;
                    }
                }
                else {
                
                    String outerName = type.outer().toString();
                    while (type.outer().isNested() || type.outer().isInnerClass()){
                        outerName = type.outer().outer().toString()+"$"+outerName;
                        type = type.outer();
                    }
                    String realName = outerName+"$"+nextDecl.name();
                    //System.out.println("Real Name Nested or Local: "+realName);
                    //System.out.println("Nested or Local: "+nextDecl.type().fullName());
                    //System.out.println("Nested class: "+sootClass.getName());
                    if (realName.equals(sootClass.getName())){
                        createClassDecl(nextDecl);
                        found = true;
                    }
                }
            }

            if (!found) {
                // assume its anon class 
                int index = sootClass.getName().indexOf("$");
                int length = sootClass.getName().length();
                int count = (new Integer(sootClass.getName().substring(index+1, length))).intValue();
               
                String outerName = sootClass.getName().substring(0, index);
                //System.out.println("anon outer name: "+outerName);
                int counter = 1;

                Iterator anonIt = nestedClassBuilder.getAnonClassBodyList().iterator();
                while (anonIt.hasNext()) {
                    polyglot.ast.New next = (polyglot.ast.New)anonIt.next();
                    //System.out.println("Next New Anon Class: "+next);
                    if (counter == count) {
                        //System.out.println("Anon class found");
                        // anon class found!
                        createAnonClassDecl(next.objectType().type(), outerName, next);
                        createClassBody(next.body());

                        // need to create field for outer ref 
                        // need to create constructor
                    }
                    counter++;
                }
            }
        }

    }

    private void addToClassToSourceMap(String className, String sourceName) {
            
        if (sourceToClassMap == null) {
            sourceToClassMap = new HashMap();
        }
            
        if (soot.util.SourceLocator.v().getSourceToClassMap() == null) {
            soot.util.SourceLocator.v().setSourceToClassMap(sourceToClassMap);
        }
            
        if (!soot.util.SourceLocator.v().getSourceToClassMap().containsKey(className)) {
            soot.util.SourceLocator.v().addToSourceToClassMap(className, sourceName);
        }
    }
    
    private void createAnonClassDecl(polyglot.types.Type type, String outerName, polyglot.ast.New next) {
        System.out.println("create anon class: type: "+type);
        soot.SootClass typeClass = soot.Scene.v().getSootClass(type.toString());
        if (typeClass.isInterface()){
            sootClass.addInterface(typeClass);
            sootClass.setSuperclass(soot.Scene.v().getSootClass("java.lang.Object"));
        }
        else {
            sootClass.setSuperclass(typeClass);
        }
        soot.SootField field = new soot.SootField("this$0", soot.Scene.v().getSootClass(outerName).getType(), soot.Modifier.FINAL | soot.Modifier.PRIVATE);
        sootClass.addField(field);
        //System.out.println("this$0 type: "+ Util.getSootType(type));
        
        ArrayList params = new ArrayList();
        params.add(soot.Scene.v().getSootClass(outerName).getType());
       
        if (typeClass.isInterface()){
            soot.SootMethod method = new soot.SootMethod("<init>", params, soot.VoidType.v(), soot.Modifier.PUBLIC);
            AnonClassInitMethodSource src = new AnonClassInitMethodSource();
            src.outerClassType(soot.Scene.v().getSootClass(outerName).getType());
            method.setSource(src);
            sootClass.addMethod(method);
        }
        else {
            ArrayList allParams = new ArrayList();
            Iterator aIt = next.arguments().iterator();
            while (aIt.hasNext()){
                polyglot.types.Type pType = ((polyglot.ast.Expr)aIt.next()).type();
                allParams.add(Util.getSootType(pType));
            }
            allParams.add(soot.Scene.v().getSootClass(outerName).getType());
            soot.SootMethod method = new soot.SootMethod("<init>", allParams, soot.VoidType.v(), soot.Modifier.PUBLIC);
        
            AnonClassInitMethodSource src = new AnonClassInitMethodSource();
            src.outerClassType(soot.Scene.v().getSootClass(outerName).getType());
            method.setSource(src);
            sootClass.addMethod(method);
                    
        }
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

            String superClassName = cDecl.superClass().toString();
            if (((polyglot.types.ClassType)cDecl.superClass().type()).isNested()){
                superClassName = fixInnerClassName((polyglot.types.ClassType)cDecl.superClass().type());        
            }
			soot.SootClass superClass = soot.Scene.v().getSootClass (superClassName); 
            sootClass.setSuperclass(superClass);
		
		}

       
        // implements 
        Iterator interfacesIt = cDecl.interfaces().iterator();
        while (interfacesIt.hasNext()) {
            polyglot.ast.TypeNode next = (polyglot.ast.TypeNode)interfacesIt.next();
            //System.out.println("next interface: "+next);
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

        // add outer class ref to constructors of inner classes
        // and out class field ref (only for non-static inner classes
        if (cDecl.type().isNested() && !cDecl.flags().isStatic()) {
            polyglot.types.ClassType outerType = cDecl.type().outer();
            soot.Type outerSootType = Util.getSootType(outerType);
            Iterator it = sootClass.getMethods().iterator();
            while (it.hasNext()){
                soot.SootMethod meth = (soot.SootMethod)it.next();
                if (meth.getName().equals("<init>")){
                    meth.getParameterTypes().add(outerSootType);
                }
            }
            soot.SootField field = new soot.SootField("this$0", outerSootType, soot.Modifier.PRIVATE | soot.Modifier.FINAL);
            sootClass.addField(field);
        }
        
        Util.addLineTag(sootClass, cDecl);
	}
    

    private String fixInnerClassName(polyglot.types.ClassType innerClass){
                
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
    }
    
	private void addModifiers(polyglot.types.Flags flags){
        //System.out.println("flags: "+flags);
		int modifiers = Util.getModifier(flags);
        //System.out.println("modifiers: "+modifiers+" for class: "+sootClass.getName());
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
        
        Iterator it = classBody.members().iterator();
		while (it.hasNext()){
			Object next = it.next();
			
			if (next instanceof polyglot.ast.MethodDecl) {
                //System.out.println("Method");
				createMethodDecl((polyglot.ast.MethodDecl)next);
			}
			else if (next instanceof polyglot.ast.FieldDecl) {
                //System.out.println("Field");
				createFieldDecl((polyglot.ast.FieldDecl)next);
            }
			else if (next instanceof polyglot.ast.ConstructorDecl){
                //System.out.println("Constructor");
                createConstructorDecl((polyglot.ast.ConstructorDecl)next);
			}
			else if (next instanceof polyglot.ast.ClassDecl){
                //System.out.println("Class Decl in Class Body - has inner class");
                // determine and create acces methods for used private access
                // in inner classes
                handlePrivateAccessors((polyglot.ast.ClassDecl)next);
			}
            else if (next instanceof polyglot.ast.Initializer) {
                //System.out.println("Initializer in Class Body");
                createInitializer((polyglot.ast.Initializer)next);
            }
            else {
                throw new RuntimeException("Class Body Member no implemented");
			}
        }
    }
    
    private void handlePrivateAccessors(polyglot.ast.ClassDecl cDecl) {
        // determine and create acces methods for used private access
        // in inner classes
        PrivateAccessChecker privateAccessChecker = new PrivateAccessChecker();
        cDecl.visit(privateAccessChecker);
               
        ArrayList privateAccessList = new ArrayList();
               
        Iterator listIt = privateAccessChecker.getList().iterator();
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
                pfams.setFieldInst((polyglot.types.FieldInstance)inst);
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
     */
    private String createName(polyglot.ast.ProcedureDecl procedure) {
        return procedure.name();
    }

    private ArrayList createParameters(polyglot.ast.ProcedureDecl procedure) {
		ArrayList parameters = new ArrayList();
		Iterator formalsIt = procedure.formals().iterator();
		while (formalsIt.hasNext()){
			polyglot.ast.Formal next = (polyglot.ast.Formal)formalsIt.next();
            parameters.add(Util.getSootType(next.type().type()));
		}
        return parameters;
    }
    
    private ArrayList createExceptions(polyglot.ast.ProcedureDecl procedure) {
		ArrayList exceptions = new ArrayList();
		Iterator throwsIt = procedure.throwTypes().iterator();
		while (throwsIt.hasNext()){
            polyglot.types.Type throwType = ((polyglot.ast.TypeNode)throwsIt.next()).type();
            String nextException = throwType.toString();
            if (((polyglot.types.ClassType)throwType).isNested()){
                nextException = fixInnerClassName((polyglot.types.ClassType)throwType);
            }
            exceptions.add(soot.Scene.v().getSootClass(nextException));
		}
        return exceptions; 
    }
    
    
    private void finishProcedure(polyglot.ast.ProcedureDecl procedure, soot.SootMethod sootMethod){
        
		addProcedureToClass(sootMethod);
	
        if (procedure.position() != null){
            if (procedure.position() instanceof soot.javaToJimple.jj.DPosition){
                soot.javaToJimple.jj.DPosition dpos = (soot.javaToJimple.jj.DPosition)procedure.position();
                //System.out.println("method header: "+dpos);
        
                if (procedure.body() != null) {
                    if (procedure.body().position() != null) {
                        if (procedure.body().position() instanceof soot.javaToJimple.jj.DPosition){
                        soot.javaToJimple.jj.DPosition bodyDpos = (soot.javaToJimple.jj.DPosition)procedure.body().position();
                        //System.out.println("body: "+bodyDpos);
                        
                        //Util.addMethodPosTag(sootMethod, dpos.column(), bodyDpos.endCol());
                        //Util.addMethodLineTag(sootMethod, dpos.line(), bodyDpos.endLine());
                        Util.addLnPosTags(sootMethod, dpos.line(), bodyDpos.endLine(), dpos.column(), bodyDpos.endCol());
                        }
                    }
                }
            }
            else {
                //System.out.println(procedure.position());
            }
        }
        else {
            //System.out.println("procedure.position() is null");
        }

        PolyglotMethodSource mSrc = new PolyglotMethodSource(procedure.body(), procedure.formals());
        mSrc.setPrivateAccessMap(privateAccessMap);
        if (localClassMap != null) {
            mSrc.setLocalClassMap(localClassMap);
        }
        if (anonClassMap != null) {
            //System.out.println("adding anon class map to method source: "+anonClassMap);
            mSrc.setAnonClassMap(anonClassMap);
        }
        
        sootMethod.setSource(mSrc);
        
	}

    private void addProcedureToClass(soot.SootMethod method) {
        sootClass.addMethod(method);
    }
    

    /**
     * Method Declaration Creation
     */
    private void createMethodDecl(polyglot.ast.MethodDecl method) {

        //System.out.println("method: "+method);
        String name = createName(method);
            
        // parameters
        ArrayList parameters = createParameters(method);
                  
        // exceptions
        ArrayList exceptions = createExceptions(method);
    
	    soot.SootMethod sootMethod = createSootMethod(name, method.flags(), method.returnType().type(), parameters, exceptions);
       
        finishProcedure(method, sootMethod);
    }
    
    
	private soot.SootMethod createSootMethod(String name, polyglot.types.Flags flags , polyglot.types.Type returnType, ArrayList parameters, ArrayList exceptions){
		int modifier = Util.getModifier(flags);
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
        //Util.addLineTag(sootField, field);
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
        //System.out.println("Creating COnstructor for: "+constructor.name());
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

    
}
