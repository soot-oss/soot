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
        System.out.println("astNode computed");
    }
    
    public void resolveFromJavaFile(soot.SootClass sc, soot.SootResolver resolver) {
        System.out.println("Creating Class: "+sc.getName());
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
                  
            // fix class names of inner member classes
            if (classType.isNested()){
                    
                className = classType.fullName();
                
                while (classType.isNested()){
                    
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
            
            System.out.println("className: "+className);
            if (!className.equals("java.lang.String[]")) {
                    
                resolver.assertResolvedClass(className);
                System.out.println("Soot resolved className: "+className);
            }
            
        }
        // resolve java.lang.Object
        resolver.assertResolvedClass("java.lang.Object");
        ////System.out.println("resolved Object");
        // resolve java.util.StringBuffer - for String Concat
        resolver.assertResolvedClass("java.lang.StringBuffer");
        ////System.out.println("resolved StringBuffer");
        
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
                //System.out.println("adding class$ method");
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
      
        // create class to source map first 
        // create source file
        if (astNode instanceof polyglot.ast.SourceFile) {
            ////System.out.println("about to create Source file");
            createSource((polyglot.ast.SourceFile)astNode);
            ////System.out.println("source created (meths, fields, constrs"); 
        }
        

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
                resolver.assertResolvedClass(className);
                System.out.println("resolved class: "+className);
            }
            
        }
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
                    resolver.assertResolvedClass(className);
                    System.out.println("resolved class: "+className);
                }
            }
        }
    }

    private void findClassDeclForSootClass(polyglot.ast.SourceFile source) {
       
        boolean found = false;
        Iterator declsIt = source.decls().iterator();
        
		while (declsIt.hasNext()){
            Object next = declsIt.next();
			
            if (next instanceof polyglot.ast.ClassDecl) {
				    
                
                if (((polyglot.ast.ClassDecl)next).name().equals(sootClass.getName())){
				    createClassDecl((polyglot.ast.ClassDecl)next);
                    found = true;
                }
                else {
                
                    polyglot.ast.ClassBody cBody = ((polyglot.ast.ClassDecl)next).body();
                    Iterator cBodyIt = cBody.members().iterator();
                    while (cBodyIt.hasNext()) {
                        Object nextMem = cBodyIt.next();
                        if (nextMem instanceof polyglot.ast.ClassDecl){
                            if (((polyglot.ast.ClassDecl)next).name().equals(sootClass.getName())){
				                createClassDecl((polyglot.ast.ClassDecl)next);
                                found = true;
                            }
                            else {
                            
                            }
                        
                        }
                    }
                }
            
            }
        }

        if (!found){
            //while 
        }
 
    }

    /**
     * Source Creation
     */
    private void createSource(polyglot.ast.SourceFile source){
			
        polyglot.ast.PackageNode pkg = source.package_();
		Iterator importsIt  = source.imports().iterator();		
		Iterator declsIt = source.decls().iterator();
        

        ////System.out.println("Num cDecls: "+source.decls().size());
		while (declsIt.hasNext()){
			Object next = declsIt.next();
			////System.out.println(next.getClass().toString());
			if (next instanceof polyglot.ast.ClassDecl) {
                //System.out.println("about to create class Decl: "+((polyglot.ast.ClassDecl)next).name());
                if (((polyglot.ast.ClassDecl)next).name().equals(sootClass.getName())){
				    createClassDecl((polyglot.ast.ClassDecl)next);
                }
		    }
		}

        polyglot.types.ImportTable importTable = source.importTable();
    }

    /**
     * Class Declaration Creation
     */
    private void createClassDecl(polyglot.ast.ClassDecl cDecl){
       
        System.out.println("cDecl position: "+cDecl.position()); 
        ////System.out.println("Want to create for: "+sootClass.getName());
        String nameToMatch = cDecl.type().toString();
        if (cDecl.type().isNested()) {
            // dealing with putting in sourceToClassMap an inner class
            String outerName = cDecl.type().outer().toString();
            polyglot.types.ClassType ct = cDecl.type().outer();
            while (ct.isNested()) {
                StringBuffer sb = new StringBuffer(outerName);
                    
                int lastDot = outerName.lastIndexOf(".");
                if (lastDot != -1) {
                    sb.replace(lastDot, lastDot+1, "$");
                    outerName = sb.toString();
                }
                ct = ct.outer();
            }
            nameToMatch = outerName + "$" + cDecl.name();
            System.out.println("name to match: "+nameToMatch);
        }

        if (sootClass.getName().indexOf("$") != -1) {

            if (sootClass.getName().equals(nameToMatch)){
            }
            else {
                // dealing with resolving an inner class
                String outer = sootClass.getName().substring(0, sootClass.getName().lastIndexOf("$"));
                System.out.println("Soot class in inner and outer name is: "+outer);
                System.out.println("Soot class in inner and name to match is: "+nameToMatch);
                if (outer.equals(nameToMatch)){
                    Iterator it = cDecl.body().members().iterator();
                    while (it.hasNext()) {
                        Object next = it.next();
                        if (next instanceof polyglot.ast.ClassDecl) {
                            createClassDecl((polyglot.ast.ClassDecl)next);
                        }
                    }
                }
                else {
                    return;
                }
            }
        }

        ////System.out.println("cDecl Name: "+cDecl.name()+" kind: "+cDecl.type().kind());
        ////System.out.println("Will create cDecl for nameToMatch: "+nameToMatch);
        ////System.out.println("Will create cDecl for classType: "+cDecl.type().toString());
        ////System.out.println("Will create sc for : "+sootClass.getName());
        if (!sootClass.getName().equals(nameToMatch)) {
       
            if (sourceToClassMap == null) {
                sourceToClassMap = new HashMap();
            }
            if (soot.util.SourceLocator.v().getSourceToClassMap() == null) {
                soot.util.SourceLocator.v().setSourceToClassMap(sourceToClassMap);
                ////System.out.println(soot.util.SourceLocator.v().getSourceToClassMap());
            }
            ////System.out.println(soot.util.SourceLocator.v().getSourceToClassMap());
            if (soot.util.SourceLocator.v().getSourceToClassMap().get(nameToMatch) == null) {
                ////System.out.println("adding to SCMAp: "+nameToMatch+","+sootClass.getName());
                String toFile = sootClass.getName();
                if (soot.util.SourceLocator.v().getSourceToClassMap().get(sootClass.getName()) != null){
                    toFile = (String)soot.util.SourceLocator.v().getSourceToClassMap().get(sootClass.getName());
                }
                //sourceToClassMap.put(nameToMatch, sootClass.getName());
                soot.util.SourceLocator.v().addToSourceToClassMap(nameToMatch, toFile);
            }
            return;
            
        }
        ////System.out.println("Will create sc for : "+sootClass.getName());
        //System.out.println("Will actually create for cDecl : "+cDecl.name());
        polyglot.types.Flags flags = cDecl.flags();
		////System.out.println("ClassDecl: Flags: "+flags.toString());
			
	    //Iterator interfaceIt = cDecl.interfaces().iterator();
		//while (interfaceIt.hasNext()){
			////System.out.println("ClassDecl: Interface: "+interfaceIt.next().toString());	
		//}
		////System.out.println("ClassDecl: Name: "+cDecl.name());
			
		//createSootClass(cDecl.name(), flags);
        addModifiers(flags);
    	////System.out.println("ClassDecl: SuperClass: "+cDecl.superClass());
	    if (cDecl.superClass() == null) {
			//probably Object
			//soot.SootClass superClass = new soot.SootClass ("java.lang.Object"); 
			soot.SootClass superClass = soot.Scene.v().getSootClass ("java.lang.Object"); 
			sootClass.setSuperclass(superClass);
		}
		else {

            String superClassName = cDecl.superClass().toString();
            if (((polyglot.types.ClassType)cDecl.superClass().type()).isNested()){
                superClassName = fixInnerClassName((polyglot.types.ClassType)cDecl.superClass().type());        
            }
			// this needs work
			soot.SootClass superClass = soot.Scene.v().getSootClass (superClassName); 
			////System.out.println("superClass: "+superClass);
            sootClass.setSuperclass(superClass);
		
		}

       
        // implements 
        Iterator interfacesIt = cDecl.interfaces().iterator();
        while (interfacesIt.hasNext()) {
            sootClass.addInterface(soot.Scene.v().getSootClass(((polyglot.ast.TypeNode)interfacesIt.next()).toString()));
        }
        // handle throws
	    
        currentClassDeclPos = cDecl.position();
        //System.out.println("about to create classBody");
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
            }
            else {
                clinitMethod = sootClass.getMethod("<clinit>", new ArrayList(), soot.VoidType.v());
            
            }
            clinitMethod.setSource(new soot.javaToJimple.PolyglotMethodSource());
            ((PolyglotMethodSource)clinitMethod.getSource()).setStaticFieldInits(staticFieldInits);
            ((PolyglotMethodSource)clinitMethod.getSource()).setStaticInitializerBlocks(staticInitializerBlocks);

        }

        // add outer class ref to constructors of inner classes
        // and out class field ref
        if (cDecl.type().isNested()) {
            polyglot.types.ClassType outerType = cDecl.type().outer();
            soot.Type outerSootType = Util.getSootType(outerType);
            Iterator it = sootClass.getMethods().iterator();
            while (it.hasNext()){
                soot.SootMethod meth = (soot.SootMethod)it.next();
                if (meth.getName().equals("<init>")){
                    meth.getParameterTypes().add(outerSootType);
                }
                /*System.out.println("sc: "+sootClass+" meth: "+meth);
                Iterator paramsIt = meth.getParameterTypes().iterator();
                while (paramsIt.hasNext()) {
                    System.out.println("param: "+paramsIt.next());
                }*/
            }
            soot.SootField field = new soot.SootField("this$0", outerSootType, soot.Modifier.PRIVATE | soot.Modifier.FINAL);
            sootClass.addField(field);
        }
        
        /*if (fieldMap != null) {
            
        }

        
        if (!soot.Scene.v().containsClass(sootClass.getName())) { 
            soot.Scene.v().addClass(sootClass); 
            //System.out.println("adding class: "+sootClass.getName()+" to scene");
        }*/
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
		int modifiers = Util.getModifier(flags);
		////System.out.println(modifiers);
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
			
			////System.out.println(next.getClass().toString());
			
			if (next instanceof polyglot.ast.MethodDecl) {
                System.out.println("Method");
				createMethodDecl((polyglot.ast.MethodDecl)next);
			}
			else if (next instanceof polyglot.ast.FieldDecl) {
                System.out.println("Field");
				createFieldDecl((polyglot.ast.FieldDecl)next);
            }
			else if (next instanceof polyglot.ast.ConstructorDecl){
                System.out.println("Constructor");
                createConstructorDecl((polyglot.ast.ConstructorDecl)next);
			}
			else if (next instanceof polyglot.ast.ClassDecl){
                System.out.println("Class Decl in Class Body - has inner class");
                // determine and create acces methods for used private access
                // in inner classes
                handlePrivateAccessors((polyglot.ast.ClassDecl)next);
                createClassDecl((polyglot.ast.ClassDecl)next);
			}
            else if (next instanceof polyglot.ast.Initializer) {
                System.out.println("Initializer in Class Body");
                createInitializer((polyglot.ast.Initializer)next);
            }
            else {
				//System.out.println("Unhandled Class Body Member - Please Implement.");
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
            String nextException = throwsIt.next().toString();
            ////System.out.println("Next method throw: "+nextException);
            exceptions.add(soot.Scene.v().getSootClass(nextException));
		}
        return exceptions; 
    }
    
    
    private void finishProcedure(polyglot.ast.ProcedureDecl procedure, soot.SootMethod sootMethod){
        
        Util.addLineTag(sootMethod, procedure);
		addProcedureToClass(sootMethod);
	
        if (!procedure.position().equals(currentClassDeclPos)){
            if (procedure.body() != null) {
                Util.addMethodPosTag(sootMethod, procedure.body().position().column(), procedure.body().toString().length());
            }
        }
        //System.out.println("procedure len: "+procedure.toString().length());
        //System.out.println("procedure toString: "+procedure.toString());
        //System.out.println("body len: "+procedure.body().toString().length());
        //System.out.println("procedure postion: "+procedure.position());

        //Iterator it = procedure.formals().iterator();
        //while (it.hasNext()) {
            //polyglot.ast.Formal f = (polyglot.ast.Formal)it.next();
            //System.out.println("formal len: "+f.toString().length());
            //System.out.println("formal toString: "+f.toString());
            //System.out.println("formal inst len: "+f.localInstance().toString().length());
            //System.out.println("formal inst toString: "+f.localInstance().toString());
        //}

        //System.out.println("procedure inst len: "+procedure.procedureInstance().toString().length());
        //System.out.println("procedure inst toString: "+procedure.procedureInstance().toString());
        //System.out.println("procedure inst column: "+procedure.procedureInstance().position().column());
        
        PolyglotMethodSource mSrc = new PolyglotMethodSource(procedure.body(), procedure.formals());
        mSrc.setPrivateAccessMap(privateAccessMap);
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
		//System.out.println("Making Method: "+name);	
		int modifier = Util.getModifier(flags);
		soot.Type sootReturnType = Util.getSootType(returnType);

		soot.SootMethod method = new soot.SootMethod(name, parameters, sootReturnType, modifier, exceptions);
		////System.out.println("Made Method: "+name);
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
        //System.out.println("Adding Field to SootClass: "+sootField.getName()+" to: "+sootClass.getName());
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
        Util.addLineTag(sootField, field);
        //System.out.println("field toString: "+field.toString());
        //Util.addPosTag(sootField, field.position().line(), field.position().column()+field.toString().lastIndexOf(' ')+1);
        Util.addPosTag(sootField, field.position());
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
        //System.out.println("Constructor Name: "+constructor.name());    		
	    ////System.out.println("Constr body: "+constructor.body().toString());
       
        //System.out.println("Constructor Formals: ");
        //Iterator it = constructor.formals().iterator();
        //while (it.hasNext()) {
            //System.out.println("Formal: "+it.next());
        //}
        System.out.println("Creating COnstructor for: "+constructor.name());
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
