package soot.javaToJimple;

public class Util {
   
    /*public static String getOuterClassName(soot.SootClass sc){
        return getOuterClass(sc).getName();
    }

    public static soot.SootClass getOuterClass(soot.SootClass sc){
        
    }
    
    public static boolean isAnonInnerClass(soot.SootClass sc){
        if (sc.indexOf("$") != -1
    }

    public static boolean isLocalInnerClass(soot.SootClass sc){
    }

    public static boolean isNested(soot.SootClass sc){
    }

    public static boolean isStaticInner(soot.SootClass sc){
    }

    public static boolean isDeeplyNested(soot.SootClass sc){
    }*/

    public static String getSourceFileOfClass(soot.SootClass sootClass){
        String name = sootClass.getName();
        int index = name.indexOf("$");
        
        // inner classes are found in the very outer class
        if (index != -1){
            name = name.substring(0, index);
        }
        return name;
    }
    
    public static void addLnPosTags(soot.tagkit.Host host, polyglot.util.Position pos) {
        if (pos != null) {
            addLnPosTags(host, pos.line(), pos.endLine(), pos.column(), pos.endColumn()); 
        }
    }
    
    public static void addLnPosTags(soot.tagkit.Host host, int sline, int eline, int spos, int epos) {
        if (soot.options.Options.v().keep_line_number()){
            host.addTag(new soot.tagkit.SourceLnPosTag(sline, eline, spos, epos));
        }
    }
    
    /**
     * Position Tag Adder
     */
    public static void addPosTag(soot.tagkit.Host host, polyglot.util.Position pos) {
        if (pos != null) {
            addPosTag(host, pos.column(), pos.endColumn());
        }
    }

    public static void addMethodPosTag(soot.tagkit.Host meth, int start, int end){
    
        meth.addTag(new soot.tagkit.SourcePositionTag(start, end));
    }
    
    /**
     * Position Tag Adder
     */
    public static void addPosTag(soot.tagkit.Host host, int sc, int ec) {

        host.addTag(new soot.tagkit.SourcePositionTag(sc, ec));
    }

    public static void addMethodLineTag(soot.tagkit.Host host, int sline, int eline){
        if (soot.options.Options.v().keep_line_number()){
            host.addTag(new soot.tagkit.SourceLineNumberTag(sline, eline));    
        }
    }
    
    /**
     * Line Tag Adder
     */
    public static void addLineTag(soot.tagkit.Host host, polyglot.ast.Node node) {

        if (soot.options.Options.v().keep_line_number()){
            if (node.position() != null) {
                host.addTag(new soot.tagkit.SourceLineNumberTag(node.position().line(), node.position().line()));
                
            }
        }
    }
    
    /**
     * Line Tag Adder
     */
    public static void addLineTag(soot.tagkit.Host host, int sLine, int eLine) {

        host.addTag(new soot.tagkit.SourceLineNumberTag(sLine, eLine));
                    
    }
    
    /**
     * Type handling
     */
    public static soot.Type getSootType(polyglot.types.Type type) {
		
		soot.Type sootType = null;
	
		if (type.isInt()){
			sootType = soot.IntType.v();
		}
		else if (type.isArray()){

            polyglot.types.Type polyglotBase = ((polyglot.types.ArrayType)type).base();
            while (polyglotBase instanceof polyglot.types.ArrayType) {
              polyglotBase = ((polyglot.types.ArrayType)polyglotBase).base();
            }
			soot.Type baseType = getSootType(polyglotBase);
			int dims = ((polyglot.types.ArrayType)type).dims();

            // do something here if baseType is still an array
			sootType = soot.ArrayType.v(baseType, dims);
		}
		else if (type.isBoolean()){
			sootType = soot.BooleanType.v();
		}
		else if (type.isByte()){
			sootType = soot.ByteType.v();
		}
		else if (type.isChar()){
			sootType = soot.CharType.v();
		}
		else if (type.isDouble()){
			sootType = soot.DoubleType.v();
		}
		else if (type.isFloat()){
			sootType = soot.FloatType.v();
		}
		else if (type.isLong()){
			sootType = soot.LongType.v();
		}
		else if (type.isShort()){
			sootType = soot.ShortType.v();
		}
		else if (type.isNull()){
			sootType = soot.NullType.v();
		}
		else if (type.isVoid()){
			sootType = soot.VoidType.v();
		}
		else if (type.isClass()){
            //System.out.println("type: "+type);
            polyglot.types.ClassType classType = (polyglot.types.ClassType)type;
            String className;
            /*if(soot.javaToJimple.InitialResolver.v().getAnonTypeMap().containsKey(new polyglot.util.IdentityKey(classType))){
                
                className = (String)soot.javaToJimple.InitialResolver.v().getAnonTypeMap().get(new polyglot.util.IdentityKey(classType));   
                System.out.println("clas name in anon map: "+className);
            }*/
            if (classType.isNested()) {
                //System.out.println("is class type anon: "+classType.isAnonymous());
                if (classType.isAnonymous()) {
                    className = (String)soot.javaToJimple.InitialResolver.v().getAnonTypeMap().get(new polyglot.util.IdentityKey(classType));   
                    //System.out.println("anon type className: "+className);
                }
                else if (classType.isLocal()) {
                    //System.out.println("type is local");
                    className = (String)soot.javaToJimple.InitialResolver.v().getLocalTypeMap().get(new polyglot.util.IdentityKey(classType));    
                }
                else {
                    String fullName = classType.fullName();
                    String pkgName = "";
                    if (classType.package_() != null){
                        pkgName = classType.package_().fullName();
                    }
                    //System.out.println("pkgName: "+pkgName);
                    className = classType.name();
                    
                    if (classType.outer().isAnonymous() || classType.outer().isLocal()){
                        className = getSootType(classType.outer()).toString()+"$"+className;
                    }
                    else {
                        while (classType.outer() != null){
                            className = classType.outer().name()+"$"+className;
                            classType = classType.outer();
                        }

                        if (!pkgName.equals("")){
                            className = pkgName+"."+className;
                        }
                    }
                }
            }
            else {
			    className = classType.fullName();
            }
            
            //System.out.println("className for type: "+className);
			sootType = soot.RefType.v(className);
		}
		else{
			throw new RuntimeException("Unknown Type");
		}
		return sootType;
    }    
    
    
    /**
     * Modifier Creation
     */
	public static int getModifier(polyglot.types.Flags flags) {

		int modifier = 0;
		
		if (flags.isPublic()){
			modifier = modifier | soot.Modifier.PUBLIC;	
		}
		if (flags.isPrivate()){
			modifier = modifier | soot.Modifier.PRIVATE;
		}
		if (flags.isProtected()){
			modifier = modifier | soot.Modifier.PROTECTED;
		}
		if (flags.isFinal()){
			modifier = modifier | soot.Modifier.FINAL;
		}
		if (flags.isStatic()){
			modifier = modifier | soot.Modifier.STATIC;
		}
		if (flags.isNative()){
			modifier = modifier | soot.Modifier.NATIVE;
		}
		if (flags.isAbstract()){
			modifier = modifier | soot.Modifier.ABSTRACT;
		}
		if (flags.isVolatile()){
			modifier = modifier | soot.Modifier.VOLATILE;
		}
		if (flags.isTransient()){
			modifier = modifier | soot.Modifier.TRANSIENT;
		}
		if (flags.isSynchronized()){
			modifier = modifier | soot.Modifier.SYNCHRONIZED;
		}
		if (flags.isInterface()){
			modifier = modifier | soot.Modifier.INTERFACE;
		}

		return modifier;
	}
}
