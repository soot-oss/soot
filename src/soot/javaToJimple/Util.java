package soot.javaToJimple;

public class Util {
    
    /**
     * Position Tag Adder
     */
    public static void addPosTag(soot.tagkit.Host host, polyglot.util.Position pos) {
        if (pos != null) {
            if (pos instanceof soot.javaToJimple.jj.DPosition){
                soot.javaToJimple.jj.DPosition dpos = (soot.javaToJimple.jj.DPosition)pos;
                /*if (host instanceof soot.jimple.Stmt) {
                    System.out.println("host is a stmt and adding SourcePosTag: "+host.toString());
                }
                System.out.println("adding pos tag: "+dpos+" to host: "+host.getClass());*/
                addPosTag(host, dpos.column(), dpos.endCol());
            }
            else {
                System.out.println("not a dpos");
            }
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
        host.addTag(new soot.tagkit.SourceLineNumberTag(sline, eline));    
    }
    
    /**
     * Line Tag Adder
     */
    public static void addLineTag(soot.tagkit.Host host, polyglot.ast.Node node) {

        if (soot.options.Options.v().keep_line_number()){
            if (node.position() != null) {
                if (node.position() instanceof soot.javaToJimple.jj.DPosition){
                    soot.javaToJimple.jj.DPosition dpos = (soot.javaToJimple.jj.DPosition)node.position();
                    host.addTag(new soot.tagkit.SourceLineNumberTag(dpos.line(), dpos.line()));
                    
                }
                
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
            polyglot.types.ClassType classType = (polyglot.types.ClassType)type;
            String className;
            if (classType.isNested()) {
                if (classType.isAnonymous()) {}
                
                className = classType.fullName();
                StringBuffer sb = new StringBuffer(className);
                int lastDot = className.lastIndexOf(".");
                if (lastDot != -1){
                    sb.replace(lastDot, lastDot+1, "$");
                    className = sb.toString();
                }
                
            }
            else {
			    className = classType.fullName();
            }
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
