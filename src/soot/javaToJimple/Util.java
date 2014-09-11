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

import java.util.*;

public class Util {

    public static void addInnerClassTag(soot.SootClass sc, String innerName, String outerName, String simpleName, int access){
        // maybe need file sep here - may break windows

        innerName = innerName.replaceAll(".", "/");
        if (outerName != null){
            outerName = outerName.replaceAll(".", "/");
        }
        sc.addTag(new soot.tagkit.InnerClassTag(
            innerName,
            outerName, 
            simpleName,
            access));
    
    }
    
    public static String getParamNameForClassLit(polyglot.types.Type type){
        String name = "";
        if (type.isArray()){
            int dims = ((polyglot.types.ArrayType)type).dims();
            polyglot.types.Type arrType = ((polyglot.types.ArrayType)type).base();
            while (arrType instanceof polyglot.types.ArrayType) {
              arrType = ((polyglot.types.ArrayType)arrType).base();
            }
            String fieldName = "";
            if (arrType.isBoolean()){
                fieldName = "Z";
            }
            else if (arrType.isByte()){
                fieldName = "B";
            }
            else if (arrType.isChar()){
                fieldName = "C";
            }
            else if (arrType.isDouble()){
                fieldName = "D";
            }
            else if (arrType.isFloat()){
                fieldName = "F";
            }
            else if (arrType.isInt()){
                fieldName = "I";
            }
            else if (arrType.isLong()){
                fieldName = "J";
            }
            else if (arrType.isShort()){
                fieldName = "S";
            }
            else {
                String typeSt = getSootType(arrType).toString();
                fieldName = "L"+typeSt;
            }
            
            for (int i = 0; i < dims; i++){
                name += "[";
            }
            name += fieldName;
            if (!arrType.isPrimitive()){
                name += ";";
            }
        }
        else {
            name = getSootType(type).toString();
        }
        return name;
    }
    
    public static String getFieldNameForClassLit(polyglot.types.Type type){
        String fieldName = "";
        if (type.isArray()){
            int dims = ((polyglot.types.ArrayType)type).dims();
            polyglot.types.Type arrType = ((polyglot.types.ArrayType)type).base();
            while (arrType instanceof polyglot.types.ArrayType) {
              arrType = ((polyglot.types.ArrayType)arrType).base();
            }
            fieldName = "array$";
            for (int i = 0; i < (dims - 1); i++){
                fieldName += "$";
            }
            if (arrType.isBoolean()){
                fieldName += "Z";
            }
            else if (arrType.isByte()){
                fieldName += "B";
            }
            else if (arrType.isChar()){
                fieldName += "C";
            }
            else if (arrType.isDouble()){
                fieldName += "D";
            }
            else if (arrType.isFloat()){
                fieldName += "F";
            }
            else if (arrType.isInt()){
                fieldName += "I";
            }
            else if (arrType.isLong()){
                fieldName += "J";
            }
            else if (arrType.isShort()){
                fieldName += "S";
            }
            else {
                String typeSt = getSootType(arrType).toString();
                typeSt = typeSt.replaceAll(".", "$");

                fieldName = fieldName+"L"+typeSt;
            }
       }
       else {
            fieldName = "class$";
            String typeSt = getSootType(type).toString();
            typeSt = typeSt.replaceAll(".", "$");
            fieldName = fieldName+typeSt;
       }
       
       return fieldName;
    }
    
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
            if (soot.options.Options.v().keep_line_number()){
            	if(pos.file()!=null) {
	                host.addTag(
	                		new soot.tagkit.SourceLnNamePosTag(
	                				pos.file(),pos.line(), pos.endLine(), pos.column(), pos.endColumn()
	                		)
	                );
            	} else {
	                host.addTag(
	                		new soot.tagkit.SourceLnPosTag(
	                				pos.line(), pos.endLine(), pos.column(), pos.endColumn()
	                		)
	                );
            	}
            }
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
    
    
    
    public static soot.Local getThis(soot.Type sootType, soot.Body body, HashMap getThisMap, LocalGenerator lg){

        if (InitialResolver.v().hierarchy() == null){
            InitialResolver.v().hierarchy(new soot.FastHierarchy());
        }
        
        soot.FastHierarchy fh = InitialResolver.v().hierarchy();
       
        //System.out.println("getting this for type: "+sootType);
        // if this for type already created return it from map
        //if (getThisMap.containsKey(sootType)){
        //    return (soot.Local)getThisMap.get(sootType);
        //}
        soot.Local specialThisLocal = body.getThisLocal();
        // if need this just return it
        if (specialThisLocal.getType().equals(sootType)) {
           
            getThisMap.put(sootType, specialThisLocal);
            return specialThisLocal;
        }
       
        // check to see if this method has a local of the correct type (it will
        // if its an initializer - then ust use it)
        // here we need an exact type I think
        if (bodyHasLocal(body, sootType)){
            soot.Local l = getLocalOfType(body, sootType);
            getThisMap.put(sootType, l);
            return l;
        }
        
        // otherwise get this$0 for one level up
        soot.SootClass classToInvoke = ((soot.RefType)specialThisLocal.getType()).getSootClass();
        soot.SootField outerThisField = classToInvoke.getFieldByName("this$0");
        soot.Local t1 = lg.generateLocal(outerThisField.getType());
        
        soot.jimple.FieldRef fieldRef = soot.jimple.Jimple.v().newInstanceFieldRef(specialThisLocal, outerThisField.makeRef());
        soot.jimple.AssignStmt fieldAssignStmt = soot.jimple.Jimple.v().newAssignStmt(t1, fieldRef);
        body.getUnits().add(fieldAssignStmt);
         
        if (fh.canStoreType(t1.getType(), sootType)){
            getThisMap.put(sootType, t1);
            return t1;            
        }
        
        // check to see if this method has a local of the correct type (it will
        // if its an initializer - then ust use it)
        // here we need an exact type I think
        /*if (bodyHasLocal(body, sootType)){
            soot.Local l = getLocalOfType(body, sootType);
            getThisMap.put(sootType, l);
            return l;
        }*/
        
        // otherwise make a new access method
        soot.Local t2 = t1;

        return getThisGivenOuter(sootType, getThisMap, body, lg, t2);
    }

    private static soot.Local getLocalOfType(soot.Body body, soot.Type type) {
        soot.FastHierarchy fh = InitialResolver.v().hierarchy();
        Iterator stmtsIt = body.getUnits().iterator();
        soot.Local correctLocal = null;
        while (stmtsIt.hasNext()){
            soot.jimple.Stmt s = (soot.jimple.Stmt)stmtsIt.next();
            if (s instanceof soot.jimple.IdentityStmt && (s.hasTag("EnclosingTag") || s.hasTag("QualifyingTag"))){
                Iterator it = s.getDefBoxes().iterator();
                while (it.hasNext()){
                    soot.ValueBox vb = (soot.ValueBox)it.next();
                    if ((vb.getValue() instanceof soot.Local) && (fh.canStoreType(type, vb.getValue().getType()))){//(vb.getValue().getType().equals(type))){
                        correctLocal = (soot.Local)vb.getValue();
                    }
                }
            }
        }
        return correctLocal;
    }
    
    private static boolean bodyHasLocal(soot.Body body, soot.Type type) {
        soot.FastHierarchy fh = InitialResolver.v().hierarchy();
        Iterator stmtsIt = body.getUnits().iterator();
        while (stmtsIt.hasNext()){
            soot.jimple.Stmt s = (soot.jimple.Stmt)stmtsIt.next();
            if (s instanceof soot.jimple.IdentityStmt && (s.hasTag("EnclosingTag") || s.hasTag("QualifyingTag"))){
                Iterator it = s.getDefBoxes().iterator();
                while (it.hasNext()){
                    soot.ValueBox vb = (soot.ValueBox)it.next();
                    if ((vb.getValue() instanceof soot.Local) && (fh.canStoreType(type, vb.getValue().getType()))){//(vb.getValue().getType().equals(type))){
                        return true;
                    }
                }
            }
        }
        return false;
        /*soot.FastHierarchy fh = InitialResolver.v().hierarchy();
        Iterator it = body.getDefBoxes().iterator();
        while (it.hasNext()){
            soot.ValueBox vb = (soot.ValueBox)it.next();
            if ((vb.getValue() instanceof soot.Local) && (fh.canStoreType(type, vb.getValue().getType()))){//(vb.getValue().getType().equals(type))){
                return true;
            }
        }
        return false;*/
    }

    
    public static soot.Local getThisGivenOuter(soot.Type sootType, HashMap getThisMap, soot.Body body, LocalGenerator lg, soot.Local t2){
       
        if (InitialResolver.v().hierarchy() == null){
            InitialResolver.v().hierarchy(new soot.FastHierarchy());
        }
        
        soot.FastHierarchy fh = InitialResolver.v().hierarchy();
        
        while (!fh.canStoreType(t2.getType(),sootType)){
            soot.SootClass classToInvoke = ((soot.RefType)t2.getType()).getSootClass();
            // make an access method and add it to that class for accessing 
            // its private this$0 field
            soot.SootMethod methToInvoke = makeOuterThisAccessMethod(classToInvoke);
            
            // generate a local that corresponds to the invoke of that meth
            soot.Local t3 = lg.generateLocal(methToInvoke.getReturnType());
            ArrayList methParams = new ArrayList();
            methParams.add(t2);
            soot.Local res = getPrivateAccessFieldInvoke(methToInvoke.makeRef(), methParams, body, lg);
            soot.jimple.AssignStmt assign = soot.jimple.Jimple.v().newAssignStmt(t3, res);
            body.getUnits().add(assign);
            t2 = t3;
        }
            
        getThisMap.put(sootType, t2);

        return t2;        
    }
    

    private static soot.SootMethod makeOuterThisAccessMethod(soot.SootClass classToInvoke){
        String name = "access$"+soot.javaToJimple.InitialResolver.v().getNextPrivateAccessCounter()+"00";
        ArrayList paramTypes = new ArrayList();
        paramTypes.add(classToInvoke.getType());
        
        soot.SootMethod meth = new soot.SootMethod(name, paramTypes, classToInvoke.getFieldByName("this$0").getType(), soot.Modifier.STATIC);

        classToInvoke.addMethod(meth);
        PrivateFieldAccMethodSource src = new PrivateFieldAccMethodSource(
            classToInvoke.getFieldByName("this$0").getType(),
            "this$0",
            classToInvoke.getFieldByName("this$0").isStatic(),
            classToInvoke
            );
        meth.setActiveBody(src.getBody(meth, null));
        meth.addTag(new soot.tagkit.SyntheticTag());
        return meth;
    }
    
    public static soot.Local getPrivateAccessFieldInvoke(soot.SootMethodRef toInvoke, ArrayList params, soot.Body body, LocalGenerator lg){
        soot.jimple.InvokeExpr invoke = soot.jimple.Jimple.v().newStaticInvokeExpr(toInvoke, params);

        soot.Local retLocal = lg.generateLocal(toInvoke.returnType());

        soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, invoke);
        body.getUnits().add(stmt);

        return retLocal;
    }

    public static boolean isSubType(polyglot.types.ClassType type, polyglot.types.ClassType superType){
        if (type.equals(superType)) return true;
        if (type.superType() == null) return false;
        return isSubType((polyglot.types.ClassType)type.superType(), superType);
    }
    
    /**
     * Type handling
     */
    public static soot.Type getSootType(polyglot.types.Type type) {

        if (type == null){
            throw new RuntimeException("Trying to get soot type for null polyglot type");
        }
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
                if (classType.isAnonymous() && (soot.javaToJimple.InitialResolver.v().getAnonTypeMap() != null) && soot.javaToJimple.InitialResolver.v().getAnonTypeMap().containsKey(new polyglot.util.IdentityKey(classType))){
                    className = soot.javaToJimple.InitialResolver.v().getAnonTypeMap().get(new polyglot.util.IdentityKey(classType));   
                }
                else if (classType.isLocal() && (soot.javaToJimple.InitialResolver.v().getLocalTypeMap() != null) && soot.javaToJimple.InitialResolver.v().getLocalTypeMap().containsKey(new polyglot.util.IdentityKey(classType))) {
                    className = soot.javaToJimple.InitialResolver.v().getLocalTypeMap().get(new polyglot.util.IdentityKey(classType));    
                }
                else {
                    String pkgName = "";
                    if (classType.package_() != null){
                        pkgName = classType.package_().fullName();
                    }
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
        if (flags.isStrictFP()) {
            modifier = modifier | soot.Modifier.STRICTFP;
        }
		return modifier;
	}
}
