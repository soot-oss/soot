package soot.javaToJimple;

public class Util {
    
    /**
     * Position Tag Adder
     */
    public static void addPosTag(soot.tagkit.Host vb, polyglot.util.Position pos) {
        if (pos != null) {
            addPosTag(vb, pos.line(), pos.column());
        }
        /*if (pos != null) {
            vb.addTag(new soot.tagkit.StringTag("Java Info: "+vb.getValue().toString()+" line: "+pos.line()+" column: "+pos.column()+" length: "+vb.getValue().toString().length())); 
            //System.out.println("Java Info: "+vb.getValue().toString()+" line: "+pos.line()+" column: "+pos.column()+" length: "+vb.getValue().toString().length()); 
            vb.addTag(new soot.tagkit.SourcePositionTag(pos.column(), (pos.column()+vb.getValue().toString().length())));
        }*/
    }

    public static void addMethodPosTag(soot.tagkit.Host meth, int start, int length){
        meth.addTag(new soot.tagkit.SourcePositionTag(start, start+length));
    }
    
    /**
     * Position Tag Adder
     */
    public static void addPosTag(soot.tagkit.Host vb, int line, int column) {
        //if (pos != null) {
        if (vb instanceof soot.ValueBox) {
            soot.ValueBox vBox = (soot.ValueBox)vb;
            vBox.addTag(new soot.tagkit.StringTag("Java Info: "+vBox.getValue().toString()+" line: "+line+" column: "+column+" length: "+vBox.getValue().toString().length())); 
            ////System.out.println("Java Info: "+vBox.getValue().toString()+" line: "+line+" column: "+column+" length: "+vBox.getValue().toString().length()); 
            vBox.addTag(new soot.tagkit.SourcePositionTag(column, column+vBox.getValue().toString().length()));
        
        }
        else if (vb instanceof soot.SootField) {
            soot.SootField sField = (soot.SootField)vb;
            sField.addTag(new soot.tagkit.StringTag("Java Info: "+sField.getName()+" line: "+line+" column: "+column+" length: "+sField.getName().length()));
            sField.addTag(new soot.tagkit.SourcePositionTag(column, column+sField.getName().length()));
        }
        else if (vb instanceof soot.jimple.Stmt) {
            soot.jimple.Stmt stmt = (soot.jimple.Stmt)vb;
            stmt.addTag(new soot.tagkit.StringTag("Java Info: "+stmt.toString()+" line: "+line+" column: "+column+" length: "+stmt.toString().length()));
            stmt.addTag(new soot.tagkit.SourcePositionTag(column, column+stmt.toString().length()));
        } 
        else {
            //System.out.println("Adding pos tag to unhandled Host");
            throw new RuntimeException("Adding pos tag to unhandled Host");
        }
    }
   
    /**
     * Line Tag Adder
     */
    public static void addLineTag(soot.tagkit.Host host, polyglot.ast.Node node) {

        if (soot.options.Options.v().keep_line_number()){
            if (node.position() != null) {
                host.addTag(new soot.tagkit.LineNumberTag(node.position().line())); 
            }
        }
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

            //System.out.println("original polyglot type: "+type);
            polyglot.types.Type polyglotBase = ((polyglot.types.ArrayType)type).base();
            while (polyglotBase instanceof polyglot.types.ArrayType) {
            //for (int i = 0; i < ((polyglot.types.ArrayType)type).dims(); i++) {
              polyglotBase = ((polyglot.types.ArrayType)polyglotBase).base();
            }
			soot.Type baseType = getSootType(polyglotBase);
			int dims = ((polyglot.types.ArrayType)type).dims();

            //System.out.println("Getting Type: baseType: "+baseType+" dims: "+dims);
            // do something here if baseType is still an array
            //System.out.println("baseType: "+baseType);
			sootType = soot.ArrayType.v(baseType, dims);
            //System.out.println("returning from getType: "+sootType.getClass()+" name: "+sootType.toString());
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
                ////System.out.println("resolveing types className : "+ className);
                int lastDot = className.lastIndexOf(".");
                sb.replace(lastDot, lastDot+1, "$");
                className = sb.toString();
                
            }
            else {
			    className = classType.fullName();
            }
			sootType = soot.RefType.v(className);
		}
		else{
			throw new RuntimeException("Unknown Type");
            ////System.out.println("Uknown Type Encountered");
            //sootType = soot.UnknownType.v();
		}
		return sootType;
    }    
    
    /*public static boolean isPrimitiveType(polyglot.types.Type type) {
        if (type instanceof polyglot.types.PrimitiveType) return true;
        else return false;
    }*/
    
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
