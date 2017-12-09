/* This file was generated with JastAdd2 (http://jastadd.org) version R20130212 (r1031) */
package soot.JastAddJ;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.SootResolver;
import soot.Type;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;

/**
 * @production TypeDecl : {@link ASTNode} ::=
 *             <span class="component">{@link Modifiers}</span>
 *             <span class="component">&lt;ID:String&gt;</span>
 *             <span class="component">{@link BodyDecl}*</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:41
 */
public abstract class TypeDecl extends ASTNode<ASTNode> implements Cloneable, SimpleSet, Iterator, VariableScope {
	/**
	 * @apilevel low-level
	 */
	public void flushCache() {
		super.flushCache();
		accessibleFromPackage_String_values = null;
		accessibleFromExtend_TypeDecl_values = null;
		accessibleFrom_TypeDecl_values = null;
		dimension_computed = false;
		elementType_computed = false;
		elementType_value = null;
		arrayType_computed = false;
		arrayType_value = null;
		isException_computed = false;
		isCheckedException_computed = false;
		isUncheckedException_computed = false;
		mayCatch_TypeDecl_values = null;
		constructors_computed = false;
		constructors_value = null;
		unqualifiedLookupMethod_String_values = null;
		methodsNameMap_computed = false;
		methodsNameMap_value = null;
		localMethodsSignatureMap_computed = false;
		localMethodsSignatureMap_value = null;
		methodsSignatureMap_computed = false;
		methodsSignatureMap_value = null;
		ancestorMethods_String_values = null;
		localTypeDecls_String_values = null;
		memberTypes_String_values = null;
		localFields_String_values = null;
		localFieldsMap_computed = false;
		localFieldsMap_value = null;
		memberFieldsMap_computed = false;
		memberFieldsMap_value = null;
		memberFields_String_values = null;
		hasAbstract_computed = false;
		unimplementedMethods_computed = false;
		unimplementedMethods_value = null;
		isPublic_computed = false;
		isStatic_computed = false;
		fullName_computed = false;
		fullName_value = null;
		typeName_computed = false;
		typeName_value = null;
		narrowingConversionTo_TypeDecl_values = null;
		methodInvocationConversionTo_TypeDecl_values = null;
		castingConversionTo_TypeDecl_values = null;
		isString_computed = false;
		isObject_computed = false;
		instanceOf_TypeDecl_values = null;
		isCircular_visited = -1;
		isCircular_computed = false;
		isCircular_initialized = false;
		boxed_computed = false;
		boxed_value = null;
		unboxed_computed = false;
		unboxed_value = null;
		isIterable_computed = false;
		involvesTypeParameters_visited = -1;
		involvesTypeParameters_computed = false;
		involvesTypeParameters_initialized = false;
		erasure_computed = false;
		erasure_value = null;
		implementedInterfaces_computed = false;
		implementedInterfaces_value = null;
		usesTypeVariable_visited = -1;
		usesTypeVariable_computed = false;
		usesTypeVariable_initialized = false;
		sourceTypeDecl_computed = false;
		sourceTypeDecl_value = null;
		containedIn_TypeDecl_values = null;
		sameStructure_TypeDecl_values = null;
		subtype_TypeDecl_values = null;
		enclosingVariables_computed = false;
		enclosingVariables_value = null;
		uniqueIndex_computed = false;
		jvmName_computed = false;
		jvmName_value = null;
		getSootClassDecl_computed = false;
		getSootClassDecl_value = null;
		getSootType_computed = false;
		getSootType_value = null;
		sootClass_computed = false;
		sootClass_value = null;
		needsClinit_computed = false;
		innerClassesAttributeEntries_computed = false;
		innerClassesAttributeEntries_value = null;
		getSootField_String_TypeDecl_values = null;
		createEnumMethod_TypeDecl_values = null;
		createEnumIndex_EnumConstant_values = null;
		createEnumArray_TypeDecl_values = null;
		componentType_computed = false;
		componentType_value = null;
		isDAbefore_Variable_values = null;
		isDUbefore_Variable_values = null;
		typeException_computed = false;
		typeException_value = null;
		typeRuntimeException_computed = false;
		typeRuntimeException_value = null;
		typeError_computed = false;
		typeError_value = null;
		lookupMethod_String_values = null;
		typeObject_computed = false;
		typeObject_value = null;
		lookupType_String_values = null;
		lookupVariable_String_values = null;
		packageName_computed = false;
		packageName_value = null;
		isAnonymous_computed = false;
		unknownType_computed = false;
		unknownType_value = null;
		inExplicitConstructorInvocation_computed = false;
		inStaticContext_computed = false;
	}

	/**
	 * @apilevel internal
	 */
	public void flushCollectionCache() {
		super.flushCollectionCache();
	}

	/**
	 * @apilevel internal
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl clone() throws CloneNotSupportedException {
		TypeDecl node = (TypeDecl) super.clone();
		node.accessibleFromPackage_String_values = null;
		node.accessibleFromExtend_TypeDecl_values = null;
		node.accessibleFrom_TypeDecl_values = null;
		node.dimension_computed = false;
		node.elementType_computed = false;
		node.elementType_value = null;
		node.arrayType_computed = false;
		node.arrayType_value = null;
		node.isException_computed = false;
		node.isCheckedException_computed = false;
		node.isUncheckedException_computed = false;
		node.mayCatch_TypeDecl_values = null;
		node.constructors_computed = false;
		node.constructors_value = null;
		node.unqualifiedLookupMethod_String_values = null;
		node.methodsNameMap_computed = false;
		node.methodsNameMap_value = null;
		node.localMethodsSignatureMap_computed = false;
		node.localMethodsSignatureMap_value = null;
		node.methodsSignatureMap_computed = false;
		node.methodsSignatureMap_value = null;
		node.ancestorMethods_String_values = null;
		node.localTypeDecls_String_values = null;
		node.memberTypes_String_values = null;
		node.localFields_String_values = null;
		node.localFieldsMap_computed = false;
		node.localFieldsMap_value = null;
		node.memberFieldsMap_computed = false;
		node.memberFieldsMap_value = null;
		node.memberFields_String_values = null;
		node.hasAbstract_computed = false;
		node.unimplementedMethods_computed = false;
		node.unimplementedMethods_value = null;
		node.isPublic_computed = false;
		node.isStatic_computed = false;
		node.fullName_computed = false;
		node.fullName_value = null;
		node.typeName_computed = false;
		node.typeName_value = null;
		node.narrowingConversionTo_TypeDecl_values = null;
		node.methodInvocationConversionTo_TypeDecl_values = null;
		node.castingConversionTo_TypeDecl_values = null;
		node.isString_computed = false;
		node.isObject_computed = false;
		node.instanceOf_TypeDecl_values = null;
		node.isCircular_visited = -1;
		node.isCircular_computed = false;
		node.isCircular_initialized = false;
		node.boxed_computed = false;
		node.boxed_value = null;
		node.unboxed_computed = false;
		node.unboxed_value = null;
		node.isIterable_computed = false;
		node.involvesTypeParameters_visited = -1;
		node.involvesTypeParameters_computed = false;
		node.involvesTypeParameters_initialized = false;
		node.erasure_computed = false;
		node.erasure_value = null;
		node.implementedInterfaces_computed = false;
		node.implementedInterfaces_value = null;
		node.usesTypeVariable_visited = -1;
		node.usesTypeVariable_computed = false;
		node.usesTypeVariable_initialized = false;
		node.sourceTypeDecl_computed = false;
		node.sourceTypeDecl_value = null;
		node.containedIn_TypeDecl_values = null;
		node.sameStructure_TypeDecl_values = null;
		node.subtype_TypeDecl_values = null;
		node.enclosingVariables_computed = false;
		node.enclosingVariables_value = null;
		node.uniqueIndex_computed = false;
		node.jvmName_computed = false;
		node.jvmName_value = null;
		node.getSootClassDecl_computed = false;
		node.getSootClassDecl_value = null;
		node.getSootType_computed = false;
		node.getSootType_value = null;
		node.sootClass_computed = false;
		node.sootClass_value = null;
		node.needsClinit_computed = false;
		node.innerClassesAttributeEntries_computed = false;
		node.innerClassesAttributeEntries_value = null;
		node.getSootField_String_TypeDecl_values = null;
		node.createEnumMethod_TypeDecl_values = null;
		node.createEnumIndex_EnumConstant_values = null;
		node.createEnumArray_TypeDecl_values = null;
		node.componentType_computed = false;
		node.componentType_value = null;
		node.isDAbefore_Variable_values = null;
		node.isDUbefore_Variable_values = null;
		node.typeException_computed = false;
		node.typeException_value = null;
		node.typeRuntimeException_computed = false;
		node.typeRuntimeException_value = null;
		node.typeError_computed = false;
		node.typeError_value = null;
		node.lookupMethod_String_values = null;
		node.typeObject_computed = false;
		node.typeObject_value = null;
		node.lookupType_String_values = null;
		node.lookupVariable_String_values = null;
		node.packageName_computed = false;
		node.packageName_value = null;
		node.isAnonymous_computed = false;
		node.unknownType_computed = false;
		node.unknownType_value = null;
		node.inExplicitConstructorInvocation_computed = false;
		node.inStaticContext_computed = false;
		node.in$Circle(false);
		node.is$Final(false);
		return node;
	}

	/**
	 * @ast method
	 * @aspect AnonymousClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag:28
	 */

	public int anonymousIndex = 0;

	/**
	 * @ast method
	 * @aspect AnonymousClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag:45
	 */
	public int nextAnonymousIndex() {
		if (isNestedType())
			return enclosingType().nextAnonymousIndex();
		return anonymousIndex++;
	}

	/**
	 * @ast method
	 * @aspect BoundNames
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BoundNames.jrag:24
	 */
	public MethodDecl addMemberMethod(MethodDecl m) {
		addBodyDecl(m);
		return (MethodDecl) getBodyDecl(getNumBodyDecl() - 1);
		/*
		 * HashMap map = methodsNameMap(); ArrayList list =
		 * (ArrayList)map.get(m.name()); if(list == null) { list = new
		 * ArrayList(4); map.put(m.name(), list); } list.add(m);
		 * if(!memberMethods(m.name()).contains(m)) throw new
		 * Error("The method " + m.signature() + " added to " + typeName() +
		 * " can not be found using lookupMemberMethod");
		 */
	}

	/**
	 * @ast method
	 * @aspect BoundNames
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BoundNames.jrag:40
	 */
	public ConstructorDecl addConstructor(ConstructorDecl c) {
		addBodyDecl(c);
		return (ConstructorDecl) getBodyDecl(getNumBodyDecl() - 1);
	}

	/**
	 * @ast method
	 * @aspect BoundNames
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BoundNames.jrag:45
	 */
	public ClassDecl addMemberClass(ClassDecl c) {
		addBodyDecl(new MemberClassDecl(c));
		return ((MemberClassDecl) getBodyDecl(getNumBodyDecl() - 1)).getClassDecl();
	}

	/**
	 * @ast method
	 * @aspect BoundNames
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BoundNames.jrag:52
	 */
	public FieldDeclaration addMemberField(FieldDeclaration f) {
		addBodyDecl(f);
		return (FieldDeclaration) getBodyDecl(getNumBodyDecl() - 1);
		// if(!memberFields(f.name()).contains(f))
		// throw new Error("The field " + f.name() + " added to " + typeName() +
		// " can not be found using lookupMemberField");
	}

	/**
	 * @ast method
	 * @aspect BoundNames
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BoundNames.jrag:90
	 */
	public TypeAccess createBoundAccess() {
		return new BoundTypeAccess("", name(), this);
	}

	/**
	 * @ast method
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:136
	 */
	public SimpleSet add(Object o) {
		return new SimpleSetImpl().add(this).add(o);
	}

	/**
	 * @ast method
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:140
	 */
	public boolean isSingleton() {
		return true;
	}

	/**
	 * @ast method
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:141
	 */
	public boolean isSingleton(Object o) {
		return contains(o);
	}

	/**
	 * @ast method
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:144
	 */

	private TypeDecl iterElem;

	/**
	 * @ast method
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:145
	 */
	public Iterator iterator() {
		iterElem = this;
		return this;
	}

	/**
	 * @ast method
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:146
	 */
	public boolean hasNext() {
		return iterElem != null;
	}

	/**
	 * @ast method
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:147
	 */
	public Object next() {
		Object o = iterElem;
		iterElem = null;
		return o;
	}

	/**
	 * @ast method
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:148
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @ast method
	 * @aspect DeclareBeforeUse
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DeclareBeforeUse.jrag:41
	 */
	public boolean declaredBeforeUse(Variable decl, ASTNode use) {
		int indexDecl = ((ASTNode) decl).varChildIndex(this);
		int indexUse = use.varChildIndex(this);
		return indexDecl < indexUse;
	}

	/**
	 * @ast method
	 * @aspect DeclareBeforeUse
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DeclareBeforeUse.jrag:46
	 */
	public boolean declaredBeforeUse(Variable decl, int indexUse) {
		int indexDecl = ((ASTNode) decl).varChildIndex(this);
		return indexDecl < indexUse;
	}

	/**
	 * @ast method
	 * @aspect ConstructorLookup
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:88
	 */
	public ConstructorDecl lookupConstructor(ConstructorDecl signature) {
		for (Iterator iter = constructors().iterator(); iter.hasNext();) {
			ConstructorDecl decl = (ConstructorDecl) iter.next();
			if (decl.sameSignature(signature)) {
				return decl;
			}
		}
		return null;
	}

	/**
	 * @return true if the method access may access the method
	 * @ast method
	 * @aspect MethodDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:189
	 */
	public boolean mayAccess(MethodAccess access, MethodDecl method) {
		if (instanceOf(method.hostType()) && access.qualifier().type().instanceOf(this))
			return true;

		if (isNestedType())
			return enclosingType().mayAccess(access, method);
		else
			return false;
	}

	/**
	 * @ast method
	 * @aspect MemberMethods
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:231
	 */
	public Iterator localMethodsIterator() {
		return new Iterator() {
			private Iterator outer = localMethodsSignatureMap().values().iterator();
			private Iterator inner = null;

			public boolean hasNext() {
				if ((inner == null || !inner.hasNext()) && outer.hasNext())
					inner = ((SimpleSet) outer.next()).iterator();
				return inner == null ? false : inner.hasNext();
			}

			public Object next() {
				return inner.next();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		// return localMethodsSignatureMap().values().iterator();
	}

	/**
	 * @ast method
	 * @aspect MemberMethods
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:299
	 */
	public Iterator methodsIterator() {
		return new Iterator() {
			private Iterator outer = methodsSignatureMap().values().iterator();
			private Iterator inner = null;

			public boolean hasNext() {
				if ((inner == null || !inner.hasNext()) && outer.hasNext())
					inner = ((SimpleSet) outer.next()).iterator();
				return inner != null ? inner.hasNext() : false;
			}

			public Object next() {
				return inner.next();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * @ast method
	 * @aspect MemberMethods
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:364
	 */
	protected boolean allMethodsAbstract(SimpleSet set) {
		if (set == null)
			return true;
		for (Iterator iter = set.iterator(); iter.hasNext();) {
			MethodDecl m = (MethodDecl) iter.next();
			if (!m.isAbstract())
				return false;
		}
		return true;
	}

	/**
	 * @return true if the expression may access the field
	 * @ast method
	 * @aspect VariableScope
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:213
	 */
	public boolean mayAccess(Expr expr, FieldDeclaration field) {
		if (instanceOf(field.hostType())) {
			if (!field.isInstanceVariable() || expr.isSuperAccess() || expr.type().instanceOf(this))
				return true;
		}

		if (isNestedType()) {
			return enclosingType().mayAccess(expr, field);
		} else {
			return false;
		}
	}

	/**
	 * @ast method
	 * @aspect Fields
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:315
	 */
	public Iterator fieldsIterator() {
		return new Iterator() {
			private Iterator outer = memberFieldsMap().values().iterator();
			private Iterator inner = null;

			public boolean hasNext() {
				if ((inner == null || !inner.hasNext()) && outer.hasNext())
					inner = ((SimpleSet) outer.next()).iterator();
				return inner != null ? inner.hasNext() : false;
			}

			public Object next() {
				return inner.next();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * @ast method
	 * @aspect Modifiers
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:68
	 */
	public void checkModifiers() {
		super.checkModifiers();
		// 8.1.1
		if (isPublic() && !isTopLevelType() && !isMemberType())
			error("public pertains only to top level types and member types");

		// 8.1.1
		if ((isProtected() || isPrivate()) && !(isMemberType() && enclosingType().isClassDecl()))
			error("protected and private may only be used on member types within a directly enclosing class declaration");

		// 8.1.1
		if (isStatic() && !isMemberType())
			error("static pertains only to member types");

		// 8.4.3.1
		// 8.1.1.1
		if (!isAbstract() && hasAbstract()) {
			StringBuffer s = new StringBuffer();
			s.append("" + name() + " is not declared abstract but contains abstract members: \n");
			for (Iterator iter = unimplementedMethods().iterator(); iter.hasNext();) {
				MethodDecl m = (MethodDecl) iter.next();
				s.append("  " + m.signature() + " in " + m.hostType().typeName() + "\n");
			}
			error(s.toString());
		}
	}

	/**
	 * @ast method
	 * @aspect NameCheck
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:251
	 */
	public void nameCheck() {
		if (isTopLevelType() && lookupType(packageName(), name()) != this)
			error("duplicate type " + name() + " in package " + packageName());

		if (!isTopLevelType() && !isAnonymous() && !isLocalClass()
				&& extractSingleType(enclosingType().memberTypes(name())) != this)
			error("duplicate member type " + name() + " in type " + enclosingType().typeName());

		// 14.3
		if (isLocalClass()) {
			TypeDecl typeDecl = extractSingleType(lookupType(name()));
			if (typeDecl != null && typeDecl != this && typeDecl.isLocalClass()
					&& enclosingBlock() == typeDecl.enclosingBlock())
				error("local class named " + name() + " may not be redeclared as a local class in the same block");
		}

		if (!packageName().equals("") && hasPackage(fullName()))
			error("type name conflicts with a package using the same name: " + name());

		// 8.1 & 9.1
		if (hasEnclosingTypeDecl(name())) {
			error("type may not have the same simple name as an enclosing type declaration");
		}
	}

	/**
	 * @ast method
	 * @aspect PrettyPrint
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:62
	 */
	protected void ppBodyDecls(StringBuffer s) {
		s.append(" {");
		for (int i = 0; i < getNumBodyDecl(); i++) {
			getBodyDecl(i).toString(s);
		}
		s.append(indent() + "}");
	}

	/**
	 * @ast method
	 * @aspect CreateQualifiedAccesses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:96
	 */
	public Access createQualifiedAccess() {
		if (isLocalClass() || isAnonymous()) {
			return new TypeAccess(name());
		} else if (!isTopLevelType()) {
			return enclosingType().createQualifiedAccess().qualifiesAccess(new TypeAccess(name()));
		} else {
			return new TypeAccess(packageName(), name());
		}
	}

	/**
	 * @ast method
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:234
	 */
	public FieldDeclaration findSingleVariable(String name) {
		return (FieldDeclaration) memberFields(name).iterator().next();
	}

	/**
	 * @ast method
	 * @aspect TypeHierarchyCheck
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:157
	 */
	public void refined_TypeHierarchyCheck_TypeDecl_typeCheck() {
		// 8.4.6.4 & 9.4.1
		for (Iterator iter1 = localMethodsIterator(); iter1.hasNext();) {
			MethodDecl m = (MethodDecl) iter1.next();
			ASTNode target = m.hostType() == this ? (ASTNode) m : (ASTNode) this;

			// for(Iterator i2 = overrides(m).iterator(); i2.hasNext(); ) {
			for (Iterator i2 = ancestorMethods(m.signature()).iterator(); i2.hasNext();) {
				MethodDecl decl = (MethodDecl) i2.next();
				if (m.overrides(decl)) {
					// 8.4.6.1
					if (!m.isStatic() && decl.isStatic())
						target.error("an instance method may not override a static method");

					// regardless of overriding
					// 8.4.6.3
					if (!m.mayOverrideReturn(decl))
						target.error("the return type of method " + m.signature() + " in " + m.hostType().typeName()
								+ " does not match the return type of method " + decl.signature() + " in "
								+ decl.hostType().typeName() + " and may thus not be overriden");

					// regardless of overriding
					// 8.4.4
					for (int i = 0; i < m.getNumException(); i++) {
						Access e = m.getException(i);
						boolean found = false;
						for (int j = 0; !found && j < decl.getNumException(); j++) {
							if (e.type().instanceOf(decl.getException(j).type()))
								found = true;
						}
						if (!found && e.type().isUncheckedException())
							target.error(m.signature() + " in " + m.hostType().typeName()
									+ " may not throw more checked exceptions than overridden method "
									+ decl.signature() + " in " + decl.hostType().typeName());
					}
					// 8.4.6.3
					if (decl.isPublic() && !m.isPublic())
						target.error("overriding access modifier error");
					// 8.4.6.3
					if (decl.isProtected() && !(m.isPublic() || m.isProtected()))
						target.error("overriding access modifier error");
					// 8.4.6.3
					if ((!decl.isPrivate() && !decl.isProtected() && !decl.isPublic()) && m.isPrivate())
						target.error("overriding access modifier error");

					// regardless of overriding
					if (decl.isFinal())
						target.error("method " + m.signature() + " in " + hostType().typeName()
								+ " can not override final method " + decl.signature() + " in "
								+ decl.hostType().typeName());
				}
				if (m.hides(decl)) {
					// 8.4.6.2
					if (m.isStatic() && !decl.isStatic())
						target.error("a static method may not hide an instance method");
					// 8.4.6.3
					if (!m.mayOverrideReturn(decl))
						target.error("can not hide a method with a different return type");
					// 8.4.4
					for (int i = 0; i < m.getNumException(); i++) {
						Access e = m.getException(i);
						boolean found = false;
						for (int j = 0; !found && j < decl.getNumException(); j++) {
							if (e.type().instanceOf(decl.getException(j).type()))
								found = true;
						}
						if (!found)
							target.error("may not throw more checked exceptions than hidden method");
					}
					// 8.4.6.3
					if (decl.isPublic() && !m.isPublic())
						target.error("hiding access modifier error: public method " + decl.signature() + " in "
								+ decl.hostType().typeName() + " is hidden by non public method " + m.signature()
								+ " in " + m.hostType().typeName());
					// 8.4.6.3
					if (decl.isProtected() && !(m.isPublic() || m.isProtected()))
						target.error("hiding access modifier error: protected method " + decl.signature() + " in "
								+ decl.hostType().typeName() + " is hidden by non (public|protected) method "
								+ m.signature() + " in " + m.hostType().typeName());
					// 8.4.6.3
					if ((!decl.isPrivate() && !decl.isProtected() && !decl.isPublic()) && m.isPrivate())
						target.error("hiding access modifier error: default method " + decl.signature() + " in "
								+ decl.hostType().typeName() + " is hidden by private method " + m.signature() + " in "
								+ m.hostType().typeName());
					if (decl.isFinal())
						target.error("method " + m.signature() + " in " + hostType().typeName()
								+ " can not hide final method " + decl.signature() + " in "
								+ decl.hostType().typeName());
				}
			}
		}
	}

	/**
	 * @ast method
	 * @aspect Generics
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:184
	 */
	public TypeDecl makeGeneric(Signatures.ClassSignature s) {
		return this;
	}

	/**
	 * @ast method
	 * @aspect LookupParTypeDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:835
	 */
	public TypeDecl substitute(TypeVariable typeVariable) {
		if (isTopLevelType())
			return typeVariable;
		return enclosingType().substitute(typeVariable);
	}

	/**
	 * @ast method
	 * @aspect LookupParTypeDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:873
	 */
	public Access substitute(Parameterization parTypeDecl) {
		if (parTypeDecl instanceof ParTypeDecl && ((ParTypeDecl) parTypeDecl).genericDecl() == this)
			return ((TypeDecl) parTypeDecl).createBoundAccess();
		if (isTopLevelType())
			return createBoundAccess();
		return enclosingType().substitute(parTypeDecl).qualifiesAccess(new TypeAccess(name()));
	}

	/**
	 * @ast method
	 * @aspect LookupParTypeDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:913
	 */
	public Access substituteReturnType(Parameterization parTypeDecl) {
		return substitute(parTypeDecl);
	}

	/**
	 * @ast method
	 * @aspect LookupParTypeDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:957
	 */
	public Access substituteParameterType(Parameterization parTypeDecl) {
		return substitute(parTypeDecl);
	}

	/**
	 * @ast method
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:12
	 */
	public boolean hasField(String name) {
		if (!memberFields(name).isEmpty())
			return true;
		for (int i = 0; i < getNumBodyDecl(); i++) {
			if (getBodyDecl(i) instanceof FieldDeclaration) {
				FieldDeclaration decl = (FieldDeclaration) getBodyDecl(i);
				if (decl.name().equals(name))
					return true;
			}
		}
		return false;
	}

	/**
	 * @ast method
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:36
	 */
	public boolean hasMethod(String id) {
		if (!memberMethods(id).isEmpty())
			return true;
		for (int i = 0; i < getNumBodyDecl(); i++) {
			if (getBodyDecl(i) instanceof MethodDecl) {
				MethodDecl decl = (MethodDecl) getBodyDecl(i);
				if (decl.name().equals(id))
					return true;
			}
		}
		return false;
	}

	/**
	 * @ast method
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:124
	 */

	// The set of TypeDecls that has this TypeDecl as their directly enclosing
	// TypeDecl.
	// I.e., NestedTypes, InnerTypes, AnonymousClasses, LocalClasses.
	private Collection nestedTypes;

	/**
	 * @ast method
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:125
	 */
	public Collection nestedTypes() {
		return nestedTypes != null ? nestedTypes : new HashSet();
	}

	/**
	 * @ast method
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:128
	 */
	public void addNestedType(TypeDecl typeDecl) {
		if (nestedTypes == null)
			nestedTypes = new HashSet();
		if (typeDecl != this)
			nestedTypes.add(typeDecl);
	}

	/**
	 * @ast method
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:135
	 */

	// The set of nested TypeDecls that are accessed in this TypeDecl
	private Collection usedNestedTypes;

	/**
	 * @ast method
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:136
	 */
	public Collection usedNestedTypes() {
		return usedNestedTypes != null ? usedNestedTypes : new HashSet();
	}

	/**
	 * @ast method
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:139
	 */
	public void addUsedNestedType(TypeDecl typeDecl) {
		if (usedNestedTypes == null)
			usedNestedTypes = new HashSet();
		usedNestedTypes.add(typeDecl);
	}

	/**
	 * @ast method
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:170
	 */

	public int accessorCounter = 0;
	/**
	 * @ast method
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:172
	 */

	private HashMap accessorMap = null;

	/**
	 * @ast method
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:173
	 */
	public ASTNode getAccessor(ASTNode source, String name) {
		ArrayList key = new ArrayList(2);
		key.add(source);
		key.add(name);
		if (accessorMap == null || !accessorMap.containsKey(key))
			return null;
		return (ASTNode) accessorMap.get(key);
	}

	/**
	 * @ast method
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:181
	 */
	public void addAccessor(ASTNode source, String name, ASTNode accessor) {
		ArrayList key = new ArrayList(2);
		key.add(source);
		key.add(name);
		if (accessorMap == null)
			accessorMap = new HashMap();
		accessorMap.put(key, accessor);
	}

	/**
	 * @ast method
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:189
	 */
	public ASTNode getAccessorSource(ASTNode accessor) {
		Iterator i = accessorMap.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			if (entry.getValue() == accessor)
				return (ASTNode) ((ArrayList) entry.getKey()).get(0);
		}
		return null;
	}

	/**
	 * @ast method
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:442
	 */

	// add val$name as fields to the class
	private boolean addEnclosingVariables = true;

	/**
	 * @ast method
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:443
	 */
	public void addEnclosingVariables() {
		if (!addEnclosingVariables)
			return;
		addEnclosingVariables = false;
		for (Iterator iter = enclosingVariables().iterator(); iter.hasNext();) {
			Variable v = (Variable) iter.next();
			Modifiers m = new Modifiers();
			m.addModifier(new Modifier("public"));
			m.addModifier(new Modifier("synthetic"));
			m.addModifier(new Modifier("final"));
			addMemberField(new FieldDeclaration(m, v.type().createQualifiedAccess(), "val$" + v.name(), new Opt()));
		}
	}

	/**
	 * @ast method
	 * @aspect Java2Rewrites
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:11
	 */

	int uniqueIndexCounter = 1;
	/**
	 * @ast method
	 * @aspect Java2Rewrites
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:65
	 */

	// lazily build a static field for assertionsDisabled
	private FieldDeclaration createAssertionsDisabled = null;

	/**
	 * @ast method
	 * @aspect Java2Rewrites
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:66
	 */
	public FieldDeclaration createAssertionsDisabled() {
		if (createAssertionsDisabled != null)
			return createAssertionsDisabled;
		// static final boolean $assertionsDisabled =
		// !TypeName.class.desiredAssertionStatus();
		createAssertionsDisabled = new FieldDeclaration(
				new Modifiers(
						new List().add(new Modifier("public")).add(new Modifier("static")).add(new Modifier("final"))),
				new PrimitiveTypeAccess("boolean"), "$assertionsDisabled",
				new Opt(new LogNotExpr(topLevelType().createQualifiedAccess().qualifiesAccess(
						new ClassAccess().qualifiesAccess(new MethodAccess("desiredAssertionStatus", new List()))))));
		getBodyDeclList().insertChild(createAssertionsDisabled, 0);
		// explicit read to trigger possible rewrites
		createAssertionsDisabled = (FieldDeclaration) getBodyDeclList().getChild(0);
		// transform the generated initalization, e.g., the ClassAccess
		// construct
		createAssertionsDisabled.transformation();
		return createAssertionsDisabled;
	}

	/**
	 * @ast method
	 * @aspect Java2Rewrites
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:119
	 */

	// lazily build a static field for each typename used in a .class expression
	private HashMap createStaticClassField = null;

	/**
	 * @ast method
	 * @aspect Java2Rewrites
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:120
	 */
	public FieldDeclaration createStaticClassField(String name) {
		if (createStaticClassField == null)
			createStaticClassField = new HashMap();
		if (createStaticClassField.containsKey(name))
			return (FieldDeclaration) createStaticClassField.get(name);
		// static synthetic Class class$java$lang$String;
		FieldDeclaration f = new FieldDeclaration(
				new Modifiers(new List().add(new Modifier("public")).add(new Modifier("static"))),
				lookupType("java.lang", "Class").createQualifiedAccess(), name, new Opt()) {
			public boolean isConstant() {
				return true;
			}
		};
		createStaticClassField.put(name, f);
		return addMemberField(f);
	}

	/**
	 * @ast method
	 * @aspect Java2Rewrites
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:141
	 */

	// lazily build a static class$ method in this type declaration
	private MethodDecl createStaticClassMethod = null;

	/**
	 * @ast method
	 * @aspect Java2Rewrites
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:142
	 */
	public MethodDecl createStaticClassMethod() {
		if (createStaticClassMethod != null)
			return createStaticClassMethod;
		// static synthetic Class class$(String name) {
		// try {
		// return java.lang.Class.forName(name);
		// } catch(java.lang.ClassNotFoundException e) {
		// throw new java.lang.NoClassDefFoundError(e.getMessage());
		// }
		// }
		createStaticClassMethod = new MethodDecl(
				new Modifiers(new List().add(new Modifier("public")).add(new Modifier("static"))),
				lookupType("java.lang", "Class").createQualifiedAccess(), "class$",
				new List().add(new ParameterDeclaration(new Modifiers(new List()),
						lookupType("java.lang", "String").createQualifiedAccess(), "name")),
				new List(),
				new Opt(new Block(
						new List().add(new TryStmt(
								new Block(new List().add(
										new ReturnStmt(new Opt(lookupType("java.lang", "Class").createQualifiedAccess()
												.qualifiesAccess(new MethodAccess("forName",
														new List().add(new VarAccess("name")))))))),
								new List().add(new BasicCatch(
										new ParameterDeclaration(
												new Modifiers(new List()),
												lookupType("java.lang", "ClassNotFoundException")
														.createQualifiedAccess(),
												"e"),
										new Block(new List().add(new ThrowStmt(new ClassInstanceExpr(
												lookupType("java.lang", "NoClassDefFoundError").createQualifiedAccess(),
												new List().add(new VarAccess("e")
														.qualifiesAccess(new MethodAccess("getMessage", new List()))),
												new Opt())))))),
								new Opt()))))) {
			public boolean isConstant() {
				return true;
			}
		};
		return addMemberMethod(createStaticClassMethod);
	}

	/**
	 * @ast method
	 * @aspect Transformations
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Transformations.jrag:27
	 */
	public void transformation() {
		addEnclosingVariables();
		super.transformation();
		if (isNestedType())
			enclosingType().addNestedType(this);
	}

	/**
	 * @ast method
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:143
	 */

	public SootMethod clinit = null;

	/**
	 * @ast method
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:145
	 */
	public void jimplify1phase2() {
		if (needsClinit() && !getSootClassDecl().declaresMethod("<clinit>", new ArrayList())) {
			clinit = Scene.v().makeSootMethod("<clinit>", new ArrayList(), soot.VoidType.v(), soot.Modifier.STATIC,
					new ArrayList());
			getSootClassDecl().addMethod(clinit);
		}

		for (Iterator iter = nestedTypes().iterator(); iter.hasNext();) {
			TypeDecl typeDecl = (TypeDecl) iter.next();
			typeDecl.jimplify1phase2();
		}
		for (int i = 0; i < getNumBodyDecl(); i++)
			if (getBodyDecl(i).generate())
				getBodyDecl(i).jimplify1phase2();
		addAttributes();
	}

	/**
	 * @ast method
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:434
	 */
	public soot.Value emitCastTo(Body b, soot.Value v, TypeDecl type, ASTNode location) {
		if (this == type)
			return v;
		if (isReferenceType() && type.isReferenceType() && instanceOf(type))
			return v;
		if ((isLong() || this instanceof FloatingPointType) && type.isIntegralType()) {
			v = b.newCastExpr(asImmediate(b, v), typeInt().getSootType(), location);
			return typeInt().emitCastTo(b, v, type, location);
		}

		return b.newCastExpr(asImmediate(b, v), type.getSootType(), location);
	}

	/**
	 * @ast method
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:903
	 */
	public void jimplify2clinit() {
		SootMethod m = clinit;
		JimpleBody body = Jimple.v().newBody(m);
		m.setActiveBody(body);
		Body b = new Body(this, body, this);
		for (int i = 0; i < getNumBodyDecl(); i++) {
			BodyDecl bodyDecl = getBodyDecl(i);
			if (bodyDecl instanceof FieldDeclaration && bodyDecl.generate()) {
				FieldDeclaration f = (FieldDeclaration) bodyDecl;
				if (f.isStatic() && f.hasInit()) {
					Local l = asLocal(b, f.getInit().type().emitCastTo(b, f.getInit(), f.type()), // AssignConversion
							f.type().getSootType());
					b.setLine(f);
					b.add(b.newAssignStmt(b.newStaticFieldRef(f.sootRef(), f), l, f));
				}
			} else if (bodyDecl instanceof StaticInitializer && bodyDecl.generate()) {
				bodyDecl.jimplify2(b);
			}
		}
		b.add(b.newReturnVoidStmt(null));
	}

	/**
	 * @ast method
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:932
	 */
	public void jimplify2() {
		super.jimplify2();
		if (clinit != null)
			jimplify2clinit();
		for (Iterator iter = nestedTypes().iterator(); iter.hasNext();) {
			TypeDecl typeDecl = (TypeDecl) iter.next();
			typeDecl.jimplify2();
		}
		// add inner class attribute
		ArrayList tags = new ArrayList();
		for (Iterator iter = innerClassesAttributeEntries().iterator(); iter.hasNext();) {
			TypeDecl type = (TypeDecl) iter.next();
			tags.add(new soot.tagkit.InnerClassTag(type.jvmName().replace('.', '/'),
					type.isMemberType() ? type.enclosingType().jvmName().replace('.', '/') : null,
					type.isAnonymous() ? null : type.name(), type.sootTypeModifiers()));
		}
		if (!tags.isEmpty())
			getSootClassDecl().addTag(new soot.tagkit.InnerClassAttribute(tags));
		addAttributes();
		getSootClassDecl().setResolvingLevel(SootClass.BODIES);
	}

	/**
	 * @ast method
	 * @aspect AnnotationsCodegen
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:20
	 */
	public void addAttributes() {
		super.addAttributes();
		ArrayList c = new ArrayList();
		getModifiers().addRuntimeVisibleAnnotationsAttribute(c);
		getModifiers().addRuntimeInvisibleAnnotationsAttribute(c);
		getModifiers().addSourceOnlyAnnotations(c);
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			soot.tagkit.Tag tag = (soot.tagkit.Tag) iter.next();
			getSootClassDecl().addTag(tag);
		}
	}

	/**
	 * @ast method
	 * @aspect AutoBoxingCodegen
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AutoBoxingCodegen.jrag:57
	 */
	protected soot.Value emitBoxingOperation(Body b, soot.Value v, ASTNode location) {
		// Box the value on the stack into this Reference type
		ArrayList parameters = new ArrayList();
		parameters.add(unboxed().getSootType());
		SootMethodRef ref = Scene.v().makeMethodRef(getSootClassDecl(), "valueOf", parameters, getSootType(), true);
		ArrayList args = new ArrayList();
		args.add(asLocal(b, v));
		return b.newStaticInvokeExpr(ref, args, location);
	}

	/**
	 * @ast method
	 * @aspect AutoBoxingCodegen
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AutoBoxingCodegen.jrag:83
	 */
	protected soot.Value emitUnboxingOperation(Body b, soot.Value v, ASTNode location) {
		// Unbox the value on the stack from this Reference type
		SootMethodRef ref = Scene.v().makeMethodRef(getSootClassDecl(), unboxed().name() + "Value", new ArrayList(),
				unboxed().getSootType(), false);
		return b.newVirtualInvokeExpr(asLocal(b, v), ref, new ArrayList(), location);
	}

	/**
	 * @ast method
	 * @aspect EnumsCodegen
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/EnumsCodegen.jrag:85
	 */

	// compute index of enum constants
	private HashMap createEnumIndexMap = null;

	/**
	 * @ast method
	 * 
	 */
	public TypeDecl() {
		super();

	}

	/**
	 * Initializes the child array to the correct size. Initializes List and Opt
	 * nta children.
	 * 
	 * @apilevel internal
	 * @ast method
	 * @ast method
	 * 
	 */
	public void init$Children() {
		children = new ASTNode[2];
		setChild(new List(), 1);
	}

	/**
	 * @ast method
	 * 
	 */
	public TypeDecl(Modifiers p0, String p1, List<BodyDecl> p2) {
		setChild(p0, 0);
		setID(p1);
		setChild(p2, 1);
	}

	/**
	 * @ast method
	 * 
	 */
	public TypeDecl(Modifiers p0, beaver.Symbol p1, List<BodyDecl> p2) {
		setChild(p0, 0);
		setID(p1);
		setChild(p2, 1);
	}

	/**
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	protected int numChildren() {
		return 2;
	}

	/**
	 * @apilevel internal
	 * @ast method
	 * 
	 */
	public boolean mayHaveRewrite() {
		return false;
	}

	/**
	 * Replaces the Modifiers child.
	 * 
	 * @param node
	 *            The new node to replace the Modifiers child.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public void setModifiers(Modifiers node) {
		setChild(node, 0);
	}

	/**
	 * Retrieves the Modifiers child.
	 * 
	 * @return The current node used as the Modifiers child.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public Modifiers getModifiers() {
		return (Modifiers) getChild(0);
	}

	/**
	 * Retrieves the Modifiers child.
	 * <p>
	 * <em>This method does not invoke AST transformations.</em>
	 * </p>
	 * 
	 * @return The current node used as the Modifiers child.
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	public Modifiers getModifiersNoTransform() {
		return (Modifiers) getChildNoTransform(0);
	}

	/**
	 * Replaces the lexeme ID.
	 * 
	 * @param value
	 *            The new value for the lexeme ID.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public void setID(String value) {
		tokenString_ID = value;
	}

	/**
	 * @apilevel internal
	 * @ast method
	 * 
	 */

	/**
	 * @apilevel internal
	 */
	protected String tokenString_ID;
	/**
	 * @ast method
	 * 
	 */

	public int IDstart;
	/**
	 * @ast method
	 * 
	 */

	public int IDend;

	/**
	 * JastAdd-internal setter for lexeme ID using the Beaver parser.
	 * 
	 * @apilevel internal
	 * @ast method
	 * 
	 */
	public void setID(beaver.Symbol symbol) {
		if (symbol.value != null && !(symbol.value instanceof String))
			throw new UnsupportedOperationException("setID is only valid for String lexemes");
		tokenString_ID = (String) symbol.value;
		IDstart = symbol.getStart();
		IDend = symbol.getEnd();
	}

	/**
	 * Retrieves the value for the lexeme ID.
	 * 
	 * @return The value for the lexeme ID.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public String getID() {
		return tokenString_ID != null ? tokenString_ID : "";
	}

	/**
	 * Replaces the BodyDecl list.
	 * 
	 * @param list
	 *            The new list node to be used as the BodyDecl list.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public void setBodyDeclList(List<BodyDecl> list) {
		setChild(list, 1);
	}

	/**
	 * Retrieves the number of children in the BodyDecl list.
	 * 
	 * @return Number of children in the BodyDecl list.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public int getNumBodyDecl() {
		return getBodyDeclList().getNumChild();
	}

	/**
	 * Retrieves the number of children in the BodyDecl list. Calling this
	 * method will not trigger rewrites..
	 * 
	 * @return Number of children in the BodyDecl list.
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	public int getNumBodyDeclNoTransform() {
		return getBodyDeclListNoTransform().getNumChildNoTransform();
	}

	/**
	 * Retrieves the element at index {@code i} in the BodyDecl list..
	 * 
	 * @param i
	 *            Index of the element to return.
	 * @return The element at position {@code i} in the BodyDecl list.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public BodyDecl getBodyDecl(int i) {
		return (BodyDecl) getBodyDeclList().getChild(i);
	}

	/**
	 * Append an element to the BodyDecl list.
	 * 
	 * @param node
	 *            The element to append to the BodyDecl list.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public void addBodyDecl(BodyDecl node) {
		List<BodyDecl> list = (parent == null || state == null) ? getBodyDeclListNoTransform() : getBodyDeclList();
		list.addChild(node);
	}

	/**
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	public void addBodyDeclNoTransform(BodyDecl node) {
		List<BodyDecl> list = getBodyDeclListNoTransform();
		list.addChild(node);
	}

	/**
	 * Replaces the BodyDecl list element at index {@code i} with the new node
	 * {@code node}.
	 * 
	 * @param node
	 *            The new node to replace the old list element.
	 * @param i
	 *            The list index of the node to be replaced.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public void setBodyDecl(BodyDecl node, int i) {
		List<BodyDecl> list = getBodyDeclList();
		list.setChild(node, i);
	}

	/**
	 * Retrieves the BodyDecl list.
	 * 
	 * @return The node representing the BodyDecl list.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public List<BodyDecl> getBodyDecls() {
		return getBodyDeclList();
	}

	/**
	 * Retrieves the BodyDecl list.
	 * <p>
	 * <em>This method does not invoke AST transformations.</em>
	 * </p>
	 * 
	 * @return The node representing the BodyDecl list.
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	public List<BodyDecl> getBodyDeclsNoTransform() {
		return getBodyDeclListNoTransform();
	}

	/**
	 * Retrieves the BodyDecl list.
	 * 
	 * @return The node representing the BodyDecl list.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public List<BodyDecl> getBodyDeclList() {
		List<BodyDecl> list = (List<BodyDecl>) getChild(1);
		list.getNumChild();
		return list;
	}

	/**
	 * Retrieves the BodyDecl list.
	 * <p>
	 * <em>This method does not invoke AST transformations.</em>
	 * </p>
	 * 
	 * @return The node representing the BodyDecl list.
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public List<BodyDecl> getBodyDeclListNoTransform() {
		return (List<BodyDecl>) getChildNoTransform(1);
	}

	/**
	 * @ast method
	 * @aspect GenericsTypeCheck
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:359
	 */
	public void typeCheck() {
		refined_TypeHierarchyCheck_TypeDecl_typeCheck();
		ArrayList list = new ArrayList();
		list.addAll(implementedInterfaces());
		for (int i = 0; i < list.size(); i++) {
			InterfaceDecl decl = (InterfaceDecl) list.get(i);
			if (decl instanceof ParInterfaceDecl) {
				ParInterfaceDecl p = (ParInterfaceDecl) decl;
				for (Iterator i2 = list.listIterator(i); i2.hasNext();) {
					InterfaceDecl decl2 = (InterfaceDecl) i2.next();
					if (decl2 instanceof ParInterfaceDecl) {
						ParInterfaceDecl q = (ParInterfaceDecl) decl2;
						if (p != q && p.genericDecl() == q.genericDecl() && !p.sameArgument(q))
							error(p.genericDecl().name() + " cannot be inherited with different arguments: "
									+ p.typeName() + " and " + q.typeName());
					}
				}
			}
		}
	}

	/**
	 * @ast method
	 * @aspect AutoBoxingCodegen
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AutoBoxingCodegen.jrag:42
	 */
	public soot.Value emitCastTo(Body b, Expr expr, TypeDecl type) {
		if (type instanceof LUBType || type instanceof GLBType || type instanceof AbstractWildcardType)
			type = typeObject();
		else if (expr.isConstant() && isPrimitive() && type.isReferenceType())
			return boxed().emitBoxingOperation(b, emitConstant(cast(expr.constant())), expr);
		else if (expr.isConstant() && !expr.type().isEnumDecl()) {
			if (type.isPrimitive())
				return emitConstant(type.cast(expr.constant()));
			else
				return emitConstant(expr.constant());
		}
		return emitCastTo(b, expr.eval(b), type, expr);
	}

	/**
	 * @ast method
	 * @aspect TypeConversion
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:59
	 */
	private boolean refined_TypeConversion_TypeDecl_assignConversionTo_TypeDecl_Expr(TypeDecl type, Expr expr) {
		// System.out.println("@@@ " + fullName() + " assign conversion to " +
		// type.fullName() + ", expr: " + expr);
		boolean sourceIsConstant = expr != null ? expr.isConstant() : false;
		// System.out.println("@@@ sourceIsConstant: " + sourceIsConstant);
		if (identityConversionTo(type) || wideningConversionTo(type))
			return true;
		// System.out.println("@@@ narrowing conversion needed");
		// System.out.println("@@@ value: " + expr.value());
		if (sourceIsConstant && (isInt() || isChar() || isShort() || isByte())
				&& (type.isByte() || type.isShort() || type.isChar()) && narrowingConversionTo(type)
				&& expr.representableIn(type))
			return true;
		// System.out.println("@@@ false");
		return false;
	}

	/**
	 * @ast method
	 * @aspect TypeConversion
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:76
	 */
	private boolean refined_TypeConversion_TypeDecl_methodInvocationConversionTo_TypeDecl(TypeDecl type) {
		return identityConversionTo(type) || wideningConversionTo(type);
	}

	/**
	 * @ast method
	 * @aspect TypeConversion
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:81
	 */
	private boolean refined_TypeConversion_TypeDecl_castingConversionTo_TypeDecl(TypeDecl type) {
		return identityConversionTo(type) || wideningConversionTo(type) || narrowingConversionTo(type);
	}

	/**
	 * @ast method
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:32
	 */
	private SootClass refined_EmitJimple_TypeDecl_getSootClassDecl() {
		if (compilationUnit().fromSource()) {
			return sootClass();
		} else {
			if (options().verbose())
				System.out.println("Loading .class file " + jvmName());
			SootClass sc = Scene.v().loadClass(jvmName(), SootClass.SIGNATURES);
			sc.setLibraryClass();
			return sc;
		}
	}

	/**
	 * @ast method
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:46
	 */
	private Type refined_EmitJimple_TypeDecl_getSootType() {
		return getSootClassDecl().getType();
	}

	/**
	 * @ast method
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:65
	 */
	private SootClass refined_EmitJimple_TypeDecl_sootClass() {
		return null;
	}

	/**
	 * @attribute syn
	 * @aspect ConstantExpression
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:160
	 */
	public Constant cast(Constant c) {
		ASTNode$State state = state();
		try {
			throw new UnsupportedOperationException(
					"ConstantExpression operation cast" + " not supported for type " + getClass().getName());
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect ConstantExpression
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:174
	 */
	public Constant plus(Constant c) {
		ASTNode$State state = state();
		try {
			throw new UnsupportedOperationException(
					"ConstantExpression operation plus" + " not supported for type " + getClass().getName());
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect ConstantExpression
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:183
	 */
	public Constant minus(Constant c) {
		ASTNode$State state = state();
		try {
			throw new UnsupportedOperationException(
					"ConstantExpression operation minus" + " not supported for type " + getClass().getName());
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect ConstantExpression
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:192
	 */
	public Constant bitNot(Constant c) {
		ASTNode$State state = state();
		try {
			throw new UnsupportedOperationException(
					"ConstantExpression operation bitNot" + " not supported for type " + getClass().getName());
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect ConstantExpression
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:199
	 */
	public Constant mul(Constant c1, Constant c2) {
		ASTNode$State state = state();
		try {
			throw new UnsupportedOperationException(
					"ConstantExpression operation mul" + " not supported for type " + getClass().getName());
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect ConstantExpression
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:208
	 */
	public Constant div(Constant c1, Constant c2) {
		ASTNode$State state = state();
		try {
			throw new UnsupportedOperationException(
					"ConstantExpression operation div" + " not supported for type " + getClass().getName());
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect ConstantExpression
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:217
	 */
	public Constant mod(Constant c1, Constant c2) {
		ASTNode$State state = state();
		try {
			throw new UnsupportedOperationException(
					"ConstantExpression operation mod" + " not supported for type " + getClass().getName());
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect ConstantExpression
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:226
	 */
	public Constant add(Constant c1, Constant c2) {
		ASTNode$State state = state();
		try {
			throw new UnsupportedOperationException(
					"ConstantExpression operation add" + " not supported for type " + getClass().getName());
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect ConstantExpression
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:236
	 */
	public Constant sub(Constant c1, Constant c2) {
		ASTNode$State state = state();
		try {
			throw new UnsupportedOperationException(
					"ConstantExpression operation sub" + " not supported for type " + getClass().getName());
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect ConstantExpression
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:245
	 */
	public Constant lshift(Constant c1, Constant c2) {
		ASTNode$State state = state();
		try {
			throw new UnsupportedOperationException(
					"ConstantExpression operation lshift" + " not supported for type " + getClass().getName());
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect ConstantExpression
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:252
	 */
	public Constant rshift(Constant c1, Constant c2) {
		ASTNode$State state = state();
		try {
			throw new UnsupportedOperationException(
					"ConstantExpression operation rshift" + " not supported for type " + getClass().getName());
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect ConstantExpression
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:259
	 */
	public Constant urshift(Constant c1, Constant c2) {
		ASTNode$State state = state();
		try {
			throw new UnsupportedOperationException(
					"ConstantExpression operation urshift" + " not supported for type " + getClass().getName());
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect ConstantExpression
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:266
	 */
	public Constant andBitwise(Constant c1, Constant c2) {
		ASTNode$State state = state();
		try {
			throw new UnsupportedOperationException(
					"ConstantExpression operation andBitwise" + " not supported for type " + getClass().getName());
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect ConstantExpression
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:274
	 */
	public Constant xorBitwise(Constant c1, Constant c2) {
		ASTNode$State state = state();
		try {
			throw new UnsupportedOperationException(
					"ConstantExpression operation xorBitwise" + " not supported for type " + getClass().getName());
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect ConstantExpression
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:282
	 */
	public Constant orBitwise(Constant c1, Constant c2) {
		ASTNode$State state = state();
		try {
			throw new UnsupportedOperationException(
					"ConstantExpression operation orBitwise" + " not supported for type " + getClass().getName());
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect ConstantExpression
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:290
	 */
	public Constant questionColon(Constant cond, Constant c1, Constant c2) {
		ASTNode$State state = state();
		try {
			throw new UnsupportedOperationException(
					"ConstantExpression operation questionColon" + " not supported for type " + getClass().getName());
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect ConstantExpression
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:394
	 */
	public boolean eqIsTrue(Expr left, Expr right) {
		ASTNode$State state = state();
		try {
			System.err.println("Evaluation eqIsTrue for unknown type: " + getClass().getName());
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect ConstantExpression
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:405
	 */
	public boolean ltIsTrue(Expr left, Expr right) {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect ConstantExpression
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:411
	 */
	public boolean leIsTrue(Expr left, Expr right) {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	protected java.util.Map accessibleFromPackage_String_values;

	/**
	 * @attribute syn
	 * @aspect AccessControl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AccessControl.jrag:15
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean accessibleFromPackage(String packageName) {
		Object _parameters = packageName;
		if (accessibleFromPackage_String_values == null)
			accessibleFromPackage_String_values = new java.util.HashMap(4);
		if (accessibleFromPackage_String_values.containsKey(_parameters)) {
			return ((Boolean) accessibleFromPackage_String_values.get(_parameters)).booleanValue();
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		boolean accessibleFromPackage_String_value = accessibleFromPackage_compute(packageName);
		if (isFinal && num == state().boundariesCrossed)
			accessibleFromPackage_String_values.put(_parameters, Boolean.valueOf(accessibleFromPackage_String_value));
		return accessibleFromPackage_String_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean accessibleFromPackage_compute(String packageName) {
		return !isPrivate() && (isPublic() || hostPackage().equals(packageName));
	}

	protected java.util.Map accessibleFromExtend_TypeDecl_values;

	/**
	 * @attribute syn
	 * @aspect AccessControl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AccessControl.jrag:18
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean accessibleFromExtend(TypeDecl type) {
		Object _parameters = type;
		if (accessibleFromExtend_TypeDecl_values == null)
			accessibleFromExtend_TypeDecl_values = new java.util.HashMap(4);
		if (accessibleFromExtend_TypeDecl_values.containsKey(_parameters)) {
			return ((Boolean) accessibleFromExtend_TypeDecl_values.get(_parameters)).booleanValue();
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		boolean accessibleFromExtend_TypeDecl_value = accessibleFromExtend_compute(type);
		if (isFinal && num == state().boundariesCrossed)
			accessibleFromExtend_TypeDecl_values.put(_parameters, Boolean.valueOf(accessibleFromExtend_TypeDecl_value));
		return accessibleFromExtend_TypeDecl_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean accessibleFromExtend_compute(TypeDecl type) {
		if (type == this)
			return true;
		if (isInnerType()) {
			if (!enclosingType().accessibleFrom(type)) {
				return false;
			}
		}
		if (isPublic())
			return true;
		else if (isProtected()) {
			// isProtected implies a nested type
			if (hostPackage().equals(type.hostPackage())) {
				return true;
			}
			if (type.isNestedType() && type.enclosingType().withinBodyThatSubclasses(enclosingType()) != null)
				return true;
			return false;
		} else if (isPrivate()) {
			return topLevelType() == type.topLevelType();
		} else
			return hostPackage().equals(type.hostPackage());
	}

	protected java.util.Map accessibleFrom_TypeDecl_values;

	/**
	 * @attribute syn
	 * @aspect AccessControl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AccessControl.jrag:44
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean accessibleFrom(TypeDecl type) {
		Object _parameters = type;
		if (accessibleFrom_TypeDecl_values == null)
			accessibleFrom_TypeDecl_values = new java.util.HashMap(4);
		if (accessibleFrom_TypeDecl_values.containsKey(_parameters)) {
			return ((Boolean) accessibleFrom_TypeDecl_values.get(_parameters)).booleanValue();
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		boolean accessibleFrom_TypeDecl_value = accessibleFrom_compute(type);
		if (isFinal && num == state().boundariesCrossed)
			accessibleFrom_TypeDecl_values.put(_parameters, Boolean.valueOf(accessibleFrom_TypeDecl_value));
		return accessibleFrom_TypeDecl_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean accessibleFrom_compute(TypeDecl type) {
		if (type == this)
			return true;
		if (isInnerType()) {
			if (!enclosingType().accessibleFrom(type)) {
				return false;
			}
		}
		if (isPublic()) {
			return true;
		} else if (isProtected()) {
			if (hostPackage().equals(type.hostPackage())) {
				return true;
			}
			if (isMemberType()) {
				TypeDecl typeDecl = type;
				while (typeDecl != null && !typeDecl.instanceOf(enclosingType()))
					typeDecl = typeDecl.enclosingType();
				if (typeDecl != null) {
					return true;
				}
			}
			return false;
		} else if (isPrivate()) {
			return topLevelType() == type.topLevelType();
		} else {
			return hostPackage().equals(type.hostPackage());
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean dimension_computed = false;
	/**
	 * @apilevel internal
	 */
	protected int dimension_value;

	/**
	 * @attribute syn
	 * @aspect Arrays
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Arrays.jrag:11
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public int dimension() {
		if (dimension_computed) {
			return dimension_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		dimension_value = dimension_compute();
		if (isFinal && num == state().boundariesCrossed)
			dimension_computed = true;
		return dimension_value;
	}

	/**
	 * @apilevel internal
	 */
	private int dimension_compute() {
		return 0;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean elementType_computed = false;
	/**
	 * @apilevel internal
	 */
	protected TypeDecl elementType_value;

	/**
	 * @attribute syn
	 * @aspect Arrays
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Arrays.jrag:15
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl elementType() {
		if (elementType_computed) {
			return elementType_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		elementType_value = elementType_compute();
		if (isFinal && num == state().boundariesCrossed)
			elementType_computed = true;
		return elementType_value;
	}

	/**
	 * @apilevel internal
	 */
	private TypeDecl elementType_compute() {
		return this;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean arrayType_computed = false;
	/**
	 * @apilevel internal
	 */
	protected TypeDecl arrayType_value;

	/**
	 * @attribute syn
	 * @aspect GenericsArrays
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsArrays.jrag:11
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl arrayType() {
		if (arrayType_computed) {
			return arrayType_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		arrayType_value = arrayType_compute();
		arrayType_value.setParent(this);
		arrayType_value.is$Final = true;
		if (true)
			arrayType_computed = true;
		return arrayType_value;
	}

	/**
	 * @apilevel internal
	 */
	private TypeDecl arrayType_compute() {
		String name = name() + "[]";

		List body = new List();
		body.add(new FieldDeclaration(new Modifiers(new List().add(new Modifier("public")).add(new Modifier("final"))),
				new PrimitiveTypeAccess("int"), "length", new Opt() // [Init:Expr]
		));
		MethodDecl clone = null;
		TypeDecl typeObject = typeObject();
		for (int i = 0; clone == null && i < typeObject.getNumBodyDecl(); i++) {
			if (typeObject.getBodyDecl(i) instanceof MethodDecl) {
				MethodDecl m = (MethodDecl) typeObject.getBodyDecl(i);
				if (m.name().equals("clone"))
					clone = m;
			}
		}
		if (clone != null) {
			body.add(
					// we create a substituted method that substitutes the clone
					// method in object
					// this has the following two consequences: the return value
					// will be cast to the
					// expected return type rather than object, and the invoked
					// method will be the
					// method in object rather in the array
					new MethodDeclSubstituted(new Modifiers(new List().add(new Modifier("public"))),
							new ArrayTypeAccess(createQualifiedAccess()), "clone", new List(), new List(),
							new Opt(new Block()), (MethodDecl) typeObject().memberMethods("clone").iterator().next()));
		}
		TypeDecl typeDecl = new ArrayDecl(new Modifiers(new List().add(new Modifier("public"))), name,
				new Opt(typeObject().createQualifiedAccess()), // [SuperClassAccess]
				new List().add(typeCloneable().createQualifiedAccess()).add(typeSerializable().createQualifiedAccess()), // Implements*
				body // BodyDecl*
		);
		return typeDecl;
	}

	/**
	 * @attribute syn
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:134
	 */
	public int size() {
		ASTNode$State state = state();
		try {
			return 1;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:135
	 */
	public boolean isEmpty() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:139
	 */
	public boolean contains(Object o) {
		ASTNode$State state = state();
		try {
			return this == o;
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean isException_computed = false;
	/**
	 * @apilevel internal
	 */
	protected boolean isException_value;

	/**
	 * @attribute syn
	 * @aspect ExceptionHandling
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:24
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean isException() {
		if (isException_computed) {
			return isException_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		isException_value = isException_compute();
		if (isFinal && num == state().boundariesCrossed)
			isException_computed = true;
		return isException_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean isException_compute() {
		return instanceOf(typeException());
	}

	/**
	 * @apilevel internal
	 */
	protected boolean isCheckedException_computed = false;
	/**
	 * @apilevel internal
	 */
	protected boolean isCheckedException_value;

	/**
	 * Unfortunately the concept of checked vs. unchecked exceptions has been
	 * inverted in JastAddJ compared to the Java specification. This is a
	 * slightly unfortunate design flaw which we cannot change at this time.
	 * 
	 * @attribute syn
	 * @aspect ExceptionHandling
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:32
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean isCheckedException() {
		if (isCheckedException_computed) {
			return isCheckedException_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		isCheckedException_value = isCheckedException_compute();
		if (isFinal && num == state().boundariesCrossed)
			isCheckedException_computed = true;
		return isCheckedException_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean isCheckedException_compute() {
		return isException() && (instanceOf(typeRuntimeException()) || instanceOf(typeError()));
	}

	/**
	 * @apilevel internal
	 */
	protected boolean isUncheckedException_computed = false;
	/**
	 * @apilevel internal
	 */
	protected boolean isUncheckedException_value;

	/**
	 * Unfortunately the concept of checked vs. unchecked exceptions has been
	 * inverted in JastAddJ compared to the Java specification. This is a
	 * slightly unfortunate design flaw which we cannot change at this time.
	 * 
	 * @attribute syn
	 * @aspect ExceptionHandling
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:41
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean isUncheckedException() {
		if (isUncheckedException_computed) {
			return isUncheckedException_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		isUncheckedException_value = isUncheckedException_compute();
		if (isFinal && num == state().boundariesCrossed)
			isUncheckedException_computed = true;
		return isUncheckedException_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean isUncheckedException_compute() {
		return isException() && !isCheckedException();
	}

	protected java.util.Map mayCatch_TypeDecl_values;

	/**
	 * @attribute syn
	 * @aspect ExceptionHandling
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:250
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean mayCatch(TypeDecl thrownType) {
		Object _parameters = thrownType;
		if (mayCatch_TypeDecl_values == null)
			mayCatch_TypeDecl_values = new java.util.HashMap(4);
		if (mayCatch_TypeDecl_values.containsKey(_parameters)) {
			return ((Boolean) mayCatch_TypeDecl_values.get(_parameters)).booleanValue();
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		boolean mayCatch_TypeDecl_value = mayCatch_compute(thrownType);
		if (isFinal && num == state().boundariesCrossed)
			mayCatch_TypeDecl_values.put(_parameters, Boolean.valueOf(mayCatch_TypeDecl_value));
		return mayCatch_TypeDecl_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean mayCatch_compute(TypeDecl thrownType) {
		return thrownType.instanceOf(this) || this.instanceOf(thrownType);
	}

	/**
	 * @attribute syn
	 * @aspect ConstructScope
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:21
	 */
	public Collection lookupSuperConstructor() {
		ASTNode$State state = state();
		try {
			return Collections.EMPTY_LIST;
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean constructors_computed = false;
	/**
	 * @apilevel internal
	 */
	protected Collection constructors_value;

	/**
	 * @attribute syn
	 * @aspect ConstructorLookup
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:98
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public Collection constructors() {
		if (constructors_computed) {
			return constructors_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		constructors_value = constructors_compute();
		if (isFinal && num == state().boundariesCrossed)
			constructors_computed = true;
		return constructors_value;
	}

	/**
	 * @apilevel internal
	 */
	private Collection constructors_compute() {
		Collection c = new ArrayList();
		for (int i = 0; i < getNumBodyDecl(); i++) {
			if (getBodyDecl(i) instanceof ConstructorDecl) {
				c.add(getBodyDecl(i));
			}
		}
		return c;
	}

	protected java.util.Map unqualifiedLookupMethod_String_values;

	/**
	 * @attribute syn
	 * @aspect LookupMethod
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:36
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public Collection unqualifiedLookupMethod(String name) {
		Object _parameters = name;
		if (unqualifiedLookupMethod_String_values == null)
			unqualifiedLookupMethod_String_values = new java.util.HashMap(4);
		if (unqualifiedLookupMethod_String_values.containsKey(_parameters)) {
			return (Collection) unqualifiedLookupMethod_String_values.get(_parameters);
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		Collection unqualifiedLookupMethod_String_value = unqualifiedLookupMethod_compute(name);
		if (isFinal && num == state().boundariesCrossed)
			unqualifiedLookupMethod_String_values.put(_parameters, unqualifiedLookupMethod_String_value);
		return unqualifiedLookupMethod_String_value;
	}

	/**
	 * @apilevel internal
	 */
	private Collection unqualifiedLookupMethod_compute(String name) {
		Collection c = memberMethods(name);
		if (!c.isEmpty())
			return c;
		if (isInnerType())
			return lookupMethod(name);
		return removeInstanceMethods(lookupMethod(name));
	}

	/**
	 * @attribute syn
	 * @aspect MemberMethods
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:210
	 */
	public Collection memberMethods(String name) {
		ASTNode$State state = state();
		try {
			Collection c = (Collection) methodsNameMap().get(name);
			if (c != null)
				return c;
			return Collections.EMPTY_LIST;
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean methodsNameMap_computed = false;
	/**
	 * @apilevel internal
	 */
	protected HashMap methodsNameMap_value;

	/**
	 * @attribute syn
	 * @aspect MemberMethods
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:216
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public HashMap methodsNameMap() {
		if (methodsNameMap_computed) {
			return methodsNameMap_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		methodsNameMap_value = methodsNameMap_compute();
		if (isFinal && num == state().boundariesCrossed)
			methodsNameMap_computed = true;
		return methodsNameMap_value;
	}

	/**
	 * @apilevel internal
	 */
	private HashMap methodsNameMap_compute() {
		HashMap map = new HashMap();
		for (Iterator iter = methodsIterator(); iter.hasNext();) {
			MethodDecl m = (MethodDecl) iter.next();
			ArrayList list = (ArrayList) map.get(m.name());
			if (list == null) {
				list = new ArrayList(4);
				map.put(m.name(), list);
			}
			list.add(m);
		}
		return map;
	}

	/**
	 * @attribute syn
	 * @aspect MemberMethods
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:247
	 */
	public SimpleSet localMethodsSignature(String signature) {
		ASTNode$State state = state();
		try {
			SimpleSet set = (SimpleSet) localMethodsSignatureMap().get(signature);
			if (set != null)
				return set;
			return SimpleSet.emptySet;
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean localMethodsSignatureMap_computed = false;
	/**
	 * @apilevel internal
	 */
	protected HashMap localMethodsSignatureMap_value;

	/**
	 * @attribute syn
	 * @aspect MemberMethods
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:253
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public HashMap localMethodsSignatureMap() {
		if (localMethodsSignatureMap_computed) {
			return localMethodsSignatureMap_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		localMethodsSignatureMap_value = localMethodsSignatureMap_compute();
		if (isFinal && num == state().boundariesCrossed)
			localMethodsSignatureMap_computed = true;
		return localMethodsSignatureMap_value;
	}

	/**
	 * @apilevel internal
	 */
	private HashMap localMethodsSignatureMap_compute() {
		HashMap map = new HashMap(getNumBodyDecl());
		for (int i = 0; i < getNumBodyDecl(); i++) {
			if (getBodyDecl(i) instanceof MethodDecl) {
				MethodDecl decl = (MethodDecl) getBodyDecl(i);
				map.put(decl.signature(), decl);
			}
		}
		return map;
	}

	/**
	 * @attribute syn
	 * @aspect MemberMethods
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:315
	 */
	public SimpleSet methodsSignature(String signature) {
		ASTNode$State state = state();
		try {
			SimpleSet set = (SimpleSet) methodsSignatureMap().get(signature);
			if (set != null)
				return set;
			return SimpleSet.emptySet;
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean methodsSignatureMap_computed = false;
	/**
	 * @apilevel internal
	 */
	protected HashMap methodsSignatureMap_value;

	/**
	 * @attribute syn
	 * @aspect MemberMethods
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:321
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public HashMap methodsSignatureMap() {
		if (methodsSignatureMap_computed) {
			return methodsSignatureMap_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		methodsSignatureMap_value = methodsSignatureMap_compute();
		if (isFinal && num == state().boundariesCrossed)
			methodsSignatureMap_computed = true;
		return methodsSignatureMap_value;
	}

	/**
	 * @apilevel internal
	 */
	private HashMap methodsSignatureMap_compute() {
		return localMethodsSignatureMap();
	}

	protected java.util.Map ancestorMethods_String_values;

	/**
	 * @attribute syn
	 * @aspect AncestorMethods
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:378
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public SimpleSet ancestorMethods(String signature) {
		Object _parameters = signature;
		if (ancestorMethods_String_values == null)
			ancestorMethods_String_values = new java.util.HashMap(4);
		if (ancestorMethods_String_values.containsKey(_parameters)) {
			return (SimpleSet) ancestorMethods_String_values.get(_parameters);
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		SimpleSet ancestorMethods_String_value = ancestorMethods_compute(signature);
		if (isFinal && num == state().boundariesCrossed)
			ancestorMethods_String_values.put(_parameters, ancestorMethods_String_value);
		return ancestorMethods_String_value;
	}

	/**
	 * @apilevel internal
	 */
	private SimpleSet ancestorMethods_compute(String signature) {
		return SimpleSet.emptySet;
	}

	/**
	 * @attribute syn
	 * @aspect TypeScopePropagation
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:478
	 */
	public boolean hasType(String name) {
		ASTNode$State state = state();
		try {
			return !memberTypes(name).isEmpty();
		} finally {
		}
	}

	protected java.util.Map localTypeDecls_String_values;

	/**
	 * @attribute syn
	 * @aspect TypeScopePropagation
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:489
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public SimpleSet localTypeDecls(String name) {
		Object _parameters = name;
		if (localTypeDecls_String_values == null)
			localTypeDecls_String_values = new java.util.HashMap(4);
		if (localTypeDecls_String_values.containsKey(_parameters)) {
			return (SimpleSet) localTypeDecls_String_values.get(_parameters);
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		SimpleSet localTypeDecls_String_value = localTypeDecls_compute(name);
		if (isFinal && num == state().boundariesCrossed)
			localTypeDecls_String_values.put(_parameters, localTypeDecls_String_value);
		return localTypeDecls_String_value;
	}

	/**
	 * @apilevel internal
	 */
	private SimpleSet localTypeDecls_compute(String name) {
		SimpleSet set = SimpleSet.emptySet;
		for (int i = 0; i < getNumBodyDecl(); i++)
			if (getBodyDecl(i).declaresType(name))
				set = set.add(getBodyDecl(i).type(name));
		return set;
	}

	protected java.util.Map memberTypes_String_values;

	/**
	 * @attribute syn
	 * @aspect TypeScopePropagation
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:497
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public SimpleSet memberTypes(String name) {
		Object _parameters = name;
		if (memberTypes_String_values == null)
			memberTypes_String_values = new java.util.HashMap(4);
		if (memberTypes_String_values.containsKey(_parameters)) {
			return (SimpleSet) memberTypes_String_values.get(_parameters);
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		SimpleSet memberTypes_String_value = memberTypes_compute(name);
		if (isFinal && num == state().boundariesCrossed)
			memberTypes_String_values.put(_parameters, memberTypes_String_value);
		return memberTypes_String_value;
	}

	/**
	 * @apilevel internal
	 */
	private SimpleSet memberTypes_compute(String name) {
		return SimpleSet.emptySet;
	}

	protected java.util.Map localFields_String_values;

	/**
	 * @attribute syn
	 * @aspect Fields
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:266
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public SimpleSet localFields(String name) {
		Object _parameters = name;
		if (localFields_String_values == null)
			localFields_String_values = new java.util.HashMap(4);
		if (localFields_String_values.containsKey(_parameters)) {
			return (SimpleSet) localFields_String_values.get(_parameters);
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		SimpleSet localFields_String_value = localFields_compute(name);
		if (isFinal && num == state().boundariesCrossed)
			localFields_String_values.put(_parameters, localFields_String_value);
		return localFields_String_value;
	}

	/**
	 * @apilevel internal
	 */
	private SimpleSet localFields_compute(String name) {
		return localFieldsMap().containsKey(name) ? (SimpleSet) localFieldsMap().get(name) : SimpleSet.emptySet;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean localFieldsMap_computed = false;
	/**
	 * @apilevel internal
	 */
	protected HashMap localFieldsMap_value;

	/**
	 * @attribute syn
	 * @aspect Fields
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:269
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public HashMap localFieldsMap() {
		if (localFieldsMap_computed) {
			return localFieldsMap_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		localFieldsMap_value = localFieldsMap_compute();
		if (isFinal && num == state().boundariesCrossed)
			localFieldsMap_computed = true;
		return localFieldsMap_value;
	}

	/**
	 * @apilevel internal
	 */
	private HashMap localFieldsMap_compute() {
		HashMap map = new HashMap();
		for (int i = 0; i < getNumBodyDecl(); i++) {
			if (getBodyDecl(i) instanceof FieldDeclaration) {
				FieldDeclaration decl = (FieldDeclaration) getBodyDecl(i);
				SimpleSet fields = (SimpleSet) map.get(decl.name());
				if (fields == null)
					fields = SimpleSet.emptySet;
				fields = fields.add(decl);
				map.put(decl.name(), fields);
			}
		}
		return map;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean memberFieldsMap_computed = false;
	/**
	 * @apilevel internal
	 */
	protected HashMap memberFieldsMap_value;

	/**
	 * @attribute syn
	 * @aspect Fields
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:282
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public HashMap memberFieldsMap() {
		if (memberFieldsMap_computed) {
			return memberFieldsMap_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		memberFieldsMap_value = memberFieldsMap_compute();
		if (isFinal && num == state().boundariesCrossed)
			memberFieldsMap_computed = true;
		return memberFieldsMap_value;
	}

	/**
	 * @apilevel internal
	 */
	private HashMap memberFieldsMap_compute() {
		return localFieldsMap();
	}

	protected java.util.Map memberFields_String_values;

	/**
	 * @attribute syn
	 * @aspect Fields
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:331
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public SimpleSet memberFields(String name) {
		Object _parameters = name;
		if (memberFields_String_values == null)
			memberFields_String_values = new java.util.HashMap(4);
		if (memberFields_String_values.containsKey(_parameters)) {
			return (SimpleSet) memberFields_String_values.get(_parameters);
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		SimpleSet memberFields_String_value = memberFields_compute(name);
		if (isFinal && num == state().boundariesCrossed)
			memberFields_String_values.put(_parameters, memberFields_String_value);
		return memberFields_String_value;
	}

	/**
	 * @apilevel internal
	 */
	private SimpleSet memberFields_compute(String name) {
		return localFields(name);
	}

	/**
	 * @apilevel internal
	 */
	protected boolean hasAbstract_computed = false;
	/**
	 * @apilevel internal
	 */
	protected boolean hasAbstract_value;

	/**
	 * @attribute syn
	 * @aspect Modifiers
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:14
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean hasAbstract() {
		if (hasAbstract_computed) {
			return hasAbstract_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		hasAbstract_value = hasAbstract_compute();
		if (isFinal && num == state().boundariesCrossed)
			hasAbstract_computed = true;
		return hasAbstract_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean hasAbstract_compute() {
		return false;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean unimplementedMethods_computed = false;
	/**
	 * @apilevel internal
	 */
	protected Collection unimplementedMethods_value;

	/**
	 * @attribute syn
	 * @aspect Modifiers
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:16
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public Collection unimplementedMethods() {
		if (unimplementedMethods_computed) {
			return unimplementedMethods_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		unimplementedMethods_value = unimplementedMethods_compute();
		if (isFinal && num == state().boundariesCrossed)
			unimplementedMethods_computed = true;
		return unimplementedMethods_value;
	}

	/**
	 * @apilevel internal
	 */
	private Collection unimplementedMethods_compute() {
		return Collections.EMPTY_LIST;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean isPublic_computed = false;
	/**
	 * @apilevel internal
	 */
	protected boolean isPublic_value;

	/**
	 * @attribute syn
	 * @aspect Modifiers
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:200
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean isPublic() {
		if (isPublic_computed) {
			return isPublic_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		isPublic_value = isPublic_compute();
		if (isFinal && num == state().boundariesCrossed)
			isPublic_computed = true;
		return isPublic_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean isPublic_compute() {
		return getModifiers().isPublic() || isMemberType() && enclosingType().isInterfaceDecl();
	}

	/**
	 * @attribute syn
	 * @aspect Modifiers
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:202
	 */
	public boolean isPrivate() {
		ASTNode$State state = state();
		try {
			return getModifiers().isPrivate();
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect Modifiers
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:203
	 */
	public boolean isProtected() {
		ASTNode$State state = state();
		try {
			return getModifiers().isProtected();
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect Modifiers
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:204
	 */
	public boolean isAbstract() {
		ASTNode$State state = state();
		try {
			return getModifiers().isAbstract();
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean isStatic_computed = false;
	/**
	 * @apilevel internal
	 */
	protected boolean isStatic_value;

	/**
	 * @attribute syn
	 * @aspect Modifiers
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:206
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean isStatic() {
		if (isStatic_computed) {
			return isStatic_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		isStatic_value = isStatic_compute();
		if (isFinal && num == state().boundariesCrossed)
			isStatic_computed = true;
		return isStatic_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean isStatic_compute() {
		return getModifiers().isStatic() || isMemberType() && enclosingType().isInterfaceDecl();
	}

	/**
	 * @attribute syn
	 * @aspect Modifiers
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:209
	 */
	public boolean isFinal() {
		ASTNode$State state = state();
		try {
			return getModifiers().isFinal();
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect Modifiers
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:210
	 */
	public boolean isStrictfp() {
		ASTNode$State state = state();
		try {
			return getModifiers().isStrictfp();
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect Modifiers
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:212
	 */
	public boolean isSynthetic() {
		ASTNode$State state = state();
		try {
			return getModifiers().isSynthetic();
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect NameCheck
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:274
	 */
	public boolean hasEnclosingTypeDecl(String name) {
		ASTNode$State state = state();
		try {
			TypeDecl enclosingType = enclosingType();
			if (enclosingType != null) {
				return enclosingType.name().equals(name) || enclosingType.hasEnclosingTypeDecl(name);
			}
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect NameCheck
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:427
	 */
	public boolean assignableToInt() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect PrettyPrint
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:758
	 */
	public boolean addsIndentationLevel() {
		ASTNode$State state = state();
		try {
			return true;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect PrettyPrint
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:800
	 */
	public String dumpString() {
		ASTNode$State state = state();
		try {
			return getClass().getName() + " [" + getID() + "]";
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeName
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:68
	 */
	public String name() {
		ASTNode$State state = state();
		try {
			return getID();
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean fullName_computed = false;
	/**
	 * @apilevel internal
	 */
	protected String fullName_value;

	/**
	 * @attribute syn
	 * @aspect TypeName
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:70
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public String fullName() {
		if (fullName_computed) {
			return fullName_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		fullName_value = fullName_compute();
		if (isFinal && num == state().boundariesCrossed)
			fullName_computed = true;
		return fullName_value;
	}

	/**
	 * @apilevel internal
	 */
	private String fullName_compute() {
		if (isNestedType())
			return enclosingType().fullName() + "." + name();
		String packageName = packageName();
		if (packageName.equals(""))
			return name();
		return packageName + "." + name();
	}

	/**
	 * @apilevel internal
	 */
	protected boolean typeName_computed = false;
	/**
	 * @apilevel internal
	 */
	protected String typeName_value;

	/**
	 * @attribute syn
	 * @aspect TypeName
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:79
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public String typeName() {
		if (typeName_computed) {
			return typeName_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		typeName_value = typeName_compute();
		if (isFinal && num == state().boundariesCrossed)
			typeName_computed = true;
		return typeName_value;
	}

	/**
	 * @apilevel internal
	 */
	private String typeName_compute() {
		if (isNestedType())
			return enclosingType().typeName() + "." + name();
		String packageName = packageName();
		if (packageName.equals("") || packageName.equals(PRIMITIVE_PACKAGE_NAME))
			return name();
		return packageName + "." + name();
	}

	/**
	 * @attribute syn
	 * @aspect TypeConversion
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:15
	 */
	public boolean identityConversionTo(TypeDecl type) {
		ASTNode$State state = state();
		try {
			return this == type;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeConversion
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:17
	 */
	public boolean wideningConversionTo(TypeDecl type) {
		ASTNode$State state = state();
		try {
			return instanceOf(type);
		} finally {
		}
	}

	protected java.util.Map narrowingConversionTo_TypeDecl_values;

	/**
	 * @attribute syn
	 * @aspect TypeConversion
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:18
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean narrowingConversionTo(TypeDecl type) {
		Object _parameters = type;
		if (narrowingConversionTo_TypeDecl_values == null)
			narrowingConversionTo_TypeDecl_values = new java.util.HashMap(4);
		if (narrowingConversionTo_TypeDecl_values.containsKey(_parameters)) {
			return ((Boolean) narrowingConversionTo_TypeDecl_values.get(_parameters)).booleanValue();
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		boolean narrowingConversionTo_TypeDecl_value = narrowingConversionTo_compute(type);
		if (isFinal && num == state().boundariesCrossed)
			narrowingConversionTo_TypeDecl_values.put(_parameters,
					Boolean.valueOf(narrowingConversionTo_TypeDecl_value));
		return narrowingConversionTo_TypeDecl_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean narrowingConversionTo_compute(TypeDecl type) {
		return instanceOf(type);
	}

	/**
	 * @attribute syn
	 * @aspect TypeConversion
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:55
	 */
	public boolean stringConversion() {
		ASTNode$State state = state();
		try {
			return true;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeConversion
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:59
	 */
	public boolean assignConversionTo(TypeDecl type, Expr expr) {
		ASTNode$State state = state();
		try {
			if (refined_TypeConversion_TypeDecl_assignConversionTo_TypeDecl_Expr(type, expr))
				return true;
			boolean canBoxThis = this instanceof PrimitiveType;
			boolean canBoxType = type instanceof PrimitiveType;
			boolean canUnboxThis = !unboxed().isUnknown();
			boolean canUnboxType = !type.unboxed().isUnknown();
			TypeDecl t = !canUnboxThis && canUnboxType ? type.unboxed() : type;
			boolean sourceIsConstant = expr != null ? expr.isConstant() : false;
			if (sourceIsConstant && (isInt() || isChar() || isShort() || isByte())
					&& (t.isByte() || t.isShort() || t.isChar()) && narrowingConversionTo(t) && expr.representableIn(t))
				return true;
			if (canBoxThis && !canBoxType && boxed().wideningConversionTo(type))
				return true;
			else if (canUnboxThis && !canUnboxType && unboxed().wideningConversionTo(type))
				return true;

			return false;
		} finally {
		}
	}

	protected java.util.Map methodInvocationConversionTo_TypeDecl_values;

	/**
	 * @attribute syn
	 * @aspect AutoBoxing
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/AutoBoxing.jrag:99
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean methodInvocationConversionTo(TypeDecl type) {
		Object _parameters = type;
		if (methodInvocationConversionTo_TypeDecl_values == null)
			methodInvocationConversionTo_TypeDecl_values = new java.util.HashMap(4);
		if (methodInvocationConversionTo_TypeDecl_values.containsKey(_parameters)) {
			return ((Boolean) methodInvocationConversionTo_TypeDecl_values.get(_parameters)).booleanValue();
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		boolean methodInvocationConversionTo_TypeDecl_value = methodInvocationConversionTo_compute(type);
		if (isFinal && num == state().boundariesCrossed)
			methodInvocationConversionTo_TypeDecl_values.put(_parameters,
					Boolean.valueOf(methodInvocationConversionTo_TypeDecl_value));
		return methodInvocationConversionTo_TypeDecl_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean methodInvocationConversionTo_compute(TypeDecl type) {
		if (refined_TypeConversion_TypeDecl_methodInvocationConversionTo_TypeDecl(type))
			return true;
		boolean canBoxThis = this instanceof PrimitiveType;
		boolean canBoxType = type instanceof PrimitiveType;
		boolean canUnboxThis = !unboxed().isUnknown();
		boolean canUnboxType = !type.unboxed().isUnknown();
		if (canBoxThis && !canBoxType)
			return boxed().wideningConversionTo(type);
		else if (canUnboxThis && !canUnboxType)
			return unboxed().wideningConversionTo(type);
		return false;
	}

	protected java.util.Map castingConversionTo_TypeDecl_values;

	/**
	 * @attribute syn
	 * @aspect AutoBoxing
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/AutoBoxing.jrag:114
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean castingConversionTo(TypeDecl type) {
		Object _parameters = type;
		if (castingConversionTo_TypeDecl_values == null)
			castingConversionTo_TypeDecl_values = new java.util.HashMap(4);
		if (castingConversionTo_TypeDecl_values.containsKey(_parameters)) {
			return ((Boolean) castingConversionTo_TypeDecl_values.get(_parameters)).booleanValue();
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		boolean castingConversionTo_TypeDecl_value = castingConversionTo_compute(type);
		if (isFinal && num == state().boundariesCrossed)
			castingConversionTo_TypeDecl_values.put(_parameters, Boolean.valueOf(castingConversionTo_TypeDecl_value));
		return castingConversionTo_TypeDecl_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean castingConversionTo_compute(TypeDecl type) {
		if (refined_TypeConversion_TypeDecl_castingConversionTo_TypeDecl(type))
			return true;
		boolean canBoxThis = this instanceof PrimitiveType;
		boolean canBoxType = type instanceof PrimitiveType;
		boolean canUnboxThis = !unboxed().isUnknown();
		boolean canUnboxType = !type.unboxed().isUnknown();
		if (canBoxThis && !canBoxType)
			return boxed().wideningConversionTo(type);
		else if (canUnboxThis && !canUnboxType)
			return unboxed().wideningConversionTo(type);
		return false;
		/*
		 * else if(boxingConversionTo(type)) return true; else
		 * if(unboxingConversionTo(type)) return true; return false;
		 */
	}

	/**
	 * @attribute syn
	 * @aspect NumericPromotion
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:146
	 */
	public TypeDecl unaryNumericPromotion() {
		ASTNode$State state = state();
		try {
			return this;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect NumericPromotion
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:154
	 */
	public TypeDecl binaryNumericPromotion(TypeDecl type) {
		ASTNode$State state = state();
		try {
			return unknownType();
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:165
	 */
	public boolean isReferenceType() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:168
	 */
	public boolean isPrimitiveType() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:173
	 */
	public boolean isNumericType() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:177
	 */
	public boolean isIntegralType() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:181
	 */
	public boolean isBoolean() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:185
	 */
	public boolean isByte() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:187
	 */
	public boolean isChar() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:189
	 */
	public boolean isShort() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:191
	 */
	public boolean isInt() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:195
	 */
	public boolean isFloat() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:197
	 */
	public boolean isLong() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:199
	 */
	public boolean isDouble() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:202
	 */
	public boolean isVoid() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:205
	 */
	public boolean isNull() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:209
	 */
	public boolean isClassDecl() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:211
	 */
	public boolean isInterfaceDecl() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:213
	 */
	public boolean isArrayDecl() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:221
	 */
	public boolean isPrimitive() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean isString_computed = false;
	/**
	 * @apilevel internal
	 */
	protected boolean isString_value;

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:224
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean isString() {
		if (isString_computed) {
			return isString_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		isString_value = isString_compute();
		if (isFinal && num == state().boundariesCrossed)
			isString_computed = true;
		return isString_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean isString_compute() {
		return false;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean isObject_computed = false;
	/**
	 * @apilevel internal
	 */
	protected boolean isObject_value;

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:227
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean isObject() {
		if (isObject_computed) {
			return isObject_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		isObject_value = isObject_compute();
		if (isFinal && num == state().boundariesCrossed)
			isObject_computed = true;
		return isObject_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean isObject_compute() {
		return false;
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:230
	 */
	public boolean isUnknown() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	protected java.util.Map instanceOf_TypeDecl_values;

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:386
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean instanceOf(TypeDecl type) {
		Object _parameters = type;
		if (instanceOf_TypeDecl_values == null)
			instanceOf_TypeDecl_values = new java.util.HashMap(4);
		if (instanceOf_TypeDecl_values.containsKey(_parameters)) {
			return ((Boolean) instanceOf_TypeDecl_values.get(_parameters)).booleanValue();
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		boolean instanceOf_TypeDecl_value = instanceOf_compute(type);
		if (isFinal && num == state().boundariesCrossed)
			instanceOf_TypeDecl_values.put(_parameters, Boolean.valueOf(instanceOf_TypeDecl_value));
		return instanceOf_TypeDecl_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean instanceOf_compute(TypeDecl type) {
		return subtype(type);
	}

	/**
	 * @attribute syn
	 * @aspect TypeWideningAndIdentity
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:423
	 */
	public boolean isSupertypeOfClassDecl(ClassDecl type) {
		ASTNode$State state = state();
		try {
			return type == this;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeWideningAndIdentity
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:440
	 */
	public boolean isSupertypeOfInterfaceDecl(InterfaceDecl type) {
		ASTNode$State state = state();
		try {
			return type == this;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeWideningAndIdentity
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:453
	 */
	public boolean isSupertypeOfArrayDecl(ArrayDecl type) {
		ASTNode$State state = state();
		try {
			return this == type;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeWideningAndIdentity
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:475
	 */
	public boolean isSupertypeOfPrimitiveType(PrimitiveType type) {
		ASTNode$State state = state();
		try {
			return type == this;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeWideningAndIdentity
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:482
	 */
	public boolean isSupertypeOfNullType(NullType type) {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeWideningAndIdentity
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:486
	 */
	public boolean isSupertypeOfVoidType(VoidType type) {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect NestedTypes
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:497
	 */
	public TypeDecl topLevelType() {
		ASTNode$State state = state();
		try {
			if (isTopLevelType())
				return this;
			return enclosingType().topLevelType();
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect NestedTypes
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:523
	 */
	public boolean isTopLevelType() {
		ASTNode$State state = state();
		try {
			return !isNestedType();
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect NestedTypes
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:534
	 */
	public boolean isInnerClass() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect NestedTypes
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:536
	 */
	public boolean isInnerType() {
		ASTNode$State state = state();
		try {
			return (isLocalClass() || isAnonymous() || (isMemberType() && !isStatic())) && !inStaticContext();
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect NestedTypes
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:538
	 */
	public boolean isInnerTypeOf(TypeDecl typeDecl) {
		ASTNode$State state = state();
		try {
			return typeDecl == this || (isInnerType() && enclosingType().isInnerTypeOf(typeDecl));
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect NestedTypes
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:545
	 */
	public TypeDecl withinBodyThatSubclasses(TypeDecl type) {
		ASTNode$State state = state();
		try {
			if (instanceOf(type))
				return this;
			if (!isTopLevelType())
				return enclosingType().withinBodyThatSubclasses(type);
			return null;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect NestedTypes
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:553
	 */
	public boolean encloses(TypeDecl type) {
		ASTNode$State state = state();
		try {
			return type.enclosedBy(this);
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect NestedTypes
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:555
	 */
	public boolean enclosedBy(TypeDecl type) {
		ASTNode$State state = state();
		try {
			if (this == type)
				return true;
			if (isTopLevelType())
				return false;
			return enclosingType().enclosedBy(type);
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect NestedTypes
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:569
	 */
	public TypeDecl hostType() {
		ASTNode$State state = state();
		try {
			return this;
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected int isCircular_visited = -1;
	/**
	 * @apilevel internal
	 */
	protected boolean isCircular_computed = false;
	/**
	 * @apilevel internal
	 */
	protected boolean isCircular_initialized = false;
	/**
	 * @apilevel internal
	 */
	protected boolean isCircular_value;

	/**
	 * @attribute syn
	 * @aspect Circularity
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:676
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean isCircular() {
		if (isCircular_computed) {
			return isCircular_value;
		}
		ASTNode$State state = state();
		if (!isCircular_initialized) {
			isCircular_initialized = true;
			isCircular_value = true;
		}
		if (!state.IN_CIRCLE) {
			state.IN_CIRCLE = true;
			int num = state.boundariesCrossed;
			boolean isFinal = this.is$Final();
			do {
				isCircular_visited = state.CIRCLE_INDEX;
				state.CHANGE = false;
				boolean new_isCircular_value = isCircular_compute();
				if (new_isCircular_value != isCircular_value)
					state.CHANGE = true;
				isCircular_value = new_isCircular_value;
				state.CIRCLE_INDEX++;
			} while (state.CHANGE);
			if (isFinal && num == state().boundariesCrossed) {
				isCircular_computed = true;
			} else {
				state.RESET_CYCLE = true;
				isCircular_compute();
				state.RESET_CYCLE = false;
				isCircular_computed = false;
				isCircular_initialized = false;
			}
			state.IN_CIRCLE = false;
			return isCircular_value;
		}
		if (isCircular_visited != state.CIRCLE_INDEX) {
			isCircular_visited = state.CIRCLE_INDEX;
			if (state.RESET_CYCLE) {
				isCircular_computed = false;
				isCircular_initialized = false;
				isCircular_visited = -1;
				return isCircular_value;
			}
			boolean new_isCircular_value = isCircular_compute();
			if (new_isCircular_value != isCircular_value)
				state.CHANGE = true;
			isCircular_value = new_isCircular_value;
			return isCircular_value;
		}
		return isCircular_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean isCircular_compute() {
		return false;
	}

	/*
	 * It is a compile-time error if the return type of a method declared in an
	 * annotation type is any type other than one of the following: one of the
	 * primitive types, String, Class and any invocation of Class, an enum type
	 * (\ufffd8.9), an annotation type, or an array (\ufffd10) of one of the
	 * preceding types.* @attribute syn
	 * 
	 * @aspect Annotations
	 * 
	 * @declaredat
	 * /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/
	 * Annotations.jrag:121
	 */
	public boolean isValidAnnotationMethodReturnType() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect Annotations
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:225
	 */
	public Annotation annotation(TypeDecl typeDecl) {
		ASTNode$State state = state();
		try {
			return getModifiers().annotation(typeDecl);
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect Annotations
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:282
	 */
	public boolean hasAnnotationSuppressWarnings(String s) {
		ASTNode$State state = state();
		try {
			return getModifiers().hasAnnotationSuppressWarnings(s);
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect Annotations
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:321
	 */
	public boolean isDeprecated() {
		ASTNode$State state = state();
		try {
			return getModifiers().hasDeprecatedAnnotation();
		} finally {
		}
	}

	/*
	 * An element type T is commensurate with an element value V if and only if
	 * one of the following conditions is true: T is an array type E[] and
	 * either: o V is an ElementValueArrayInitializer and each
	 * ElementValueInitializer (analogous to a variable initializer in an array
	 * initializer) in V is commensurate with E. Or o V is an ElementValue that
	 * is commensurate with T. The type of V is assignment compatible
	 * (\ufffd5.2) with T and, furthermore: o If T is a primitive type or
	 * String, V is a constant expression (\ufffd15.28). o V is not null. o if T
	 * is Class, or an invocation of Class, and V is a class literal
	 * (\ufffd15.8.2). o If T is an enum type, and V is an enum constant.
	 * * @attribute syn
	 * 
	 * @aspect Annotations
	 * 
	 * @declaredat
	 * /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/
	 * Annotations.jrag:474
	 */
	public boolean commensurateWith(ElementValue value) {
		ASTNode$State state = state();
		try {
			return value.commensurateWithTypeDecl(this);
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect Annotations
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:545
	 */
	public boolean isAnnotationDecl() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/*
	 * NumericTypes, BooleanTypes TypeChecking (ensure that an expression of a
	 * certain type is valid in a particular context) TypeComputation (compute
	 * the type of an expression) CodeGeneration (output code including implicit
	 * type conversions and promotions)
	 * 
	 * NumericTypes: binaryNumericPromotion, unaryNumericPromotion,
	 * assignmentConversion, methodInvocationConversion, castingConversion
	 * numeric operations that do not use these kinds of conversions and
	 * promotions explicitly need to be refined BooleanTypes:
	 * assignmentConversion, methodInvocationConversion, castingConversion
	 * 
	 * @attribute syn
	 * 
	 * @aspect AutoBoxing
	 * 
	 * @declaredat
	 * /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/
	 * AutoBoxing.jrag:31
	 */
	public boolean boxingConversionTo(TypeDecl typeDecl) {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean boxed_computed = false;
	/**
	 * @apilevel internal
	 */
	protected TypeDecl boxed_value;

	/**
	 * @attribute syn
	 * @aspect AutoBoxing
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/AutoBoxing.jrag:35
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl boxed() {
		if (boxed_computed) {
			return boxed_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		boxed_value = boxed_compute();
		if (isFinal && num == state().boundariesCrossed)
			boxed_computed = true;
		return boxed_value;
	}

	/**
	 * @apilevel internal
	 */
	private TypeDecl boxed_compute() {
		return unknownType();
	}

	/**
	 * @attribute syn
	 * @aspect AutoBoxing
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/AutoBoxing.jrag:47
	 */
	public boolean unboxingConversionTo(TypeDecl typeDecl) {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean unboxed_computed = false;
	/**
	 * @apilevel internal
	 */
	protected TypeDecl unboxed_value;

	/**
	 * @attribute syn
	 * @aspect AutoBoxing
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/AutoBoxing.jrag:51
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl unboxed() {
		if (unboxed_computed) {
			return unboxed_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		unboxed_value = unboxed_compute();
		if (isFinal && num == state().boundariesCrossed)
			unboxed_computed = true;
		return unboxed_value;
	}

	/**
	 * @apilevel internal
	 */
	private TypeDecl unboxed_compute() {
		return unknownType();
	}

	/**
	 * @apilevel internal
	 */
	protected boolean isIterable_computed = false;
	/**
	 * @apilevel internal
	 */
	protected boolean isIterable_value;

	/**
	 * True if type is java.lang.Iterable or subtype As long as we use the 1.4
	 * API we check for java.util.Collection instead.
	 * 
	 * @attribute syn
	 * @aspect EnhancedFor
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/EnhancedFor.jrag:35
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean isIterable() {
		if (isIterable_computed) {
			return isIterable_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		isIterable_value = isIterable_compute();
		if (isFinal && num == state().boundariesCrossed)
			isIterable_computed = true;
		return isIterable_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean isIterable_compute() {
		return instanceOf(lookupType("java.lang", "Iterable"));
	}

	/*
	 * 1) It is a compile-time error to attempt to explicitly instantiate an
	 * enum type (\ufffd\ufffd\ufffd15.9.1).
	 * 
	 * @attribute syn
	 * 
	 * @aspect Enums
	 * 
	 * @declaredat
	 * /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/
	 * Enums.jrag:16
	 */
	public boolean isEnumDecl() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericMethodsInference
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethodsInference.jrag:13
	 */
	public boolean isUnboxedPrimitive() {
		ASTNode$State state = state();
		try {
			return this instanceof PrimitiveType && isPrimitive();
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected int involvesTypeParameters_visited = -1;
	/**
	 * @apilevel internal
	 */
	protected boolean involvesTypeParameters_computed = false;
	/**
	 * @apilevel internal
	 */
	protected boolean involvesTypeParameters_initialized = false;
	/**
	 * @apilevel internal
	 */
	protected boolean involvesTypeParameters_value;

	/**
	 * @attribute syn
	 * @aspect GenericMethodsInference
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethodsInference.jrag:15
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean involvesTypeParameters() {
		if (involvesTypeParameters_computed) {
			return involvesTypeParameters_value;
		}
		ASTNode$State state = state();
		if (!involvesTypeParameters_initialized) {
			involvesTypeParameters_initialized = true;
			involvesTypeParameters_value = false;
		}
		if (!state.IN_CIRCLE) {
			state.IN_CIRCLE = true;
			int num = state.boundariesCrossed;
			boolean isFinal = this.is$Final();
			do {
				involvesTypeParameters_visited = state.CIRCLE_INDEX;
				state.CHANGE = false;
				boolean new_involvesTypeParameters_value = involvesTypeParameters_compute();
				if (new_involvesTypeParameters_value != involvesTypeParameters_value)
					state.CHANGE = true;
				involvesTypeParameters_value = new_involvesTypeParameters_value;
				state.CIRCLE_INDEX++;
			} while (state.CHANGE);
			if (isFinal && num == state().boundariesCrossed) {
				involvesTypeParameters_computed = true;
			} else {
				state.RESET_CYCLE = true;
				involvesTypeParameters_compute();
				state.RESET_CYCLE = false;
				involvesTypeParameters_computed = false;
				involvesTypeParameters_initialized = false;
			}
			state.IN_CIRCLE = false;
			return involvesTypeParameters_value;
		}
		if (involvesTypeParameters_visited != state.CIRCLE_INDEX) {
			involvesTypeParameters_visited = state.CIRCLE_INDEX;
			if (state.RESET_CYCLE) {
				involvesTypeParameters_computed = false;
				involvesTypeParameters_initialized = false;
				involvesTypeParameters_visited = -1;
				return involvesTypeParameters_value;
			}
			boolean new_involvesTypeParameters_value = involvesTypeParameters_compute();
			if (new_involvesTypeParameters_value != involvesTypeParameters_value)
				state.CHANGE = true;
			involvesTypeParameters_value = new_involvesTypeParameters_value;
			return involvesTypeParameters_value;
		}
		return involvesTypeParameters_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean involvesTypeParameters_compute() {
		return false;
	}

	/**
	 * @attribute syn
	 * @aspect Generics
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:179
	 */
	public boolean isGenericType() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect Generics
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:253
	 */
	public boolean isParameterizedType() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect Generics
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:256
	 */
	public boolean isRawType() {
		ASTNode$State state = state();
		try {
			return isNestedType() && enclosingType().isRawType();
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean erasure_computed = false;
	/**
	 * @apilevel internal
	 */
	protected TypeDecl erasure_value;

	/**
	 * @attribute syn
	 * @aspect GenericsErasure
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:343
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl erasure() {
		if (erasure_computed) {
			return erasure_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		erasure_value = erasure_compute();
		if (isFinal && num == state().boundariesCrossed)
			erasure_computed = true;
		return erasure_value;
	}

	/**
	 * @apilevel internal
	 */
	private TypeDecl erasure_compute() {
		if (isAnonymous() || isLocalClass())
			return this;
		if (!isNestedType())
			return this;
		return extractSingleType(enclosingType().erasure().memberTypes(name()));
	}

	/**
	 * @apilevel internal
	 */
	protected boolean implementedInterfaces_computed = false;
	/**
	 * @apilevel internal
	 */
	protected HashSet implementedInterfaces_value;

	/**
	 * @attribute syn
	 * @aspect GenericsTypeCheck
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:399
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public HashSet implementedInterfaces() {
		if (implementedInterfaces_computed) {
			return implementedInterfaces_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		implementedInterfaces_value = implementedInterfaces_compute();
		if (isFinal && num == state().boundariesCrossed)
			implementedInterfaces_computed = true;
		return implementedInterfaces_value;
	}

	/**
	 * @apilevel internal
	 */
	private HashSet implementedInterfaces_compute() {
		return new HashSet();
	}

	/**
	 * @attribute syn
	 * @aspect LookupParTypeDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:571
	 */
	public boolean sameSignature(Access a) {
		ASTNode$State state = state();
		try {
			if (a instanceof ParTypeAccess)
				return false;
			if (a instanceof AbstractWildcard)
				return false;
			return this == a.type();
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected int usesTypeVariable_visited = -1;
	/**
	 * @apilevel internal
	 */
	protected boolean usesTypeVariable_computed = false;
	/**
	 * @apilevel internal
	 */
	protected boolean usesTypeVariable_initialized = false;
	/**
	 * @apilevel internal
	 */
	protected boolean usesTypeVariable_value;

	/**
	 * @attribute syn
	 * @aspect LookupParTypeDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1068
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean usesTypeVariable() {
		if (usesTypeVariable_computed) {
			return usesTypeVariable_value;
		}
		ASTNode$State state = state();
		if (!usesTypeVariable_initialized) {
			usesTypeVariable_initialized = true;
			usesTypeVariable_value = false;
		}
		if (!state.IN_CIRCLE) {
			state.IN_CIRCLE = true;
			int num = state.boundariesCrossed;
			boolean isFinal = this.is$Final();
			do {
				usesTypeVariable_visited = state.CIRCLE_INDEX;
				state.CHANGE = false;
				boolean new_usesTypeVariable_value = usesTypeVariable_compute();
				if (new_usesTypeVariable_value != usesTypeVariable_value)
					state.CHANGE = true;
				usesTypeVariable_value = new_usesTypeVariable_value;
				state.CIRCLE_INDEX++;
			} while (state.CHANGE);
			if (isFinal && num == state().boundariesCrossed) {
				usesTypeVariable_computed = true;
			} else {
				state.RESET_CYCLE = true;
				usesTypeVariable_compute();
				state.RESET_CYCLE = false;
				usesTypeVariable_computed = false;
				usesTypeVariable_initialized = false;
			}
			state.IN_CIRCLE = false;
			return usesTypeVariable_value;
		}
		if (usesTypeVariable_visited != state.CIRCLE_INDEX) {
			usesTypeVariable_visited = state.CIRCLE_INDEX;
			if (state.RESET_CYCLE) {
				usesTypeVariable_computed = false;
				usesTypeVariable_initialized = false;
				usesTypeVariable_visited = -1;
				return usesTypeVariable_value;
			}
			boolean new_usesTypeVariable_value = usesTypeVariable_compute();
			if (new_usesTypeVariable_value != usesTypeVariable_value)
				state.CHANGE = true;
			usesTypeVariable_value = new_usesTypeVariable_value;
			return usesTypeVariable_value;
		}
		return usesTypeVariable_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean usesTypeVariable_compute() {
		return isNestedType() && enclosingType().usesTypeVariable();
	}

	/**
	 * @attribute syn
	 * @aspect LookupParTypeDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1306
	 */
	public TypeDecl original() {
		ASTNode$State state = state();
		try {
			return this;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect LookupParTypeDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1398
	 */
	public TypeDecl asWildcardExtends() {
		ASTNode$State state = state();
		try {
			return lookupWildcardExtends(this);
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect LookupParTypeDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1411
	 */
	public TypeDecl asWildcardSuper() {
		ASTNode$State state = state();
		try {
			return lookupWildcardSuper(this);
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean sourceTypeDecl_computed = false;
	/**
	 * @apilevel internal
	 */
	protected TypeDecl sourceTypeDecl_value;

	/**
	 * @attribute syn
	 * @aspect SourceDeclarations
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1504
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl sourceTypeDecl() {
		if (sourceTypeDecl_computed) {
			return sourceTypeDecl_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		sourceTypeDecl_value = sourceTypeDecl_compute();
		if (isFinal && num == state().boundariesCrossed)
			sourceTypeDecl_computed = true;
		return sourceTypeDecl_value;
	}

	/**
	 * @apilevel internal
	 */
	private TypeDecl sourceTypeDecl_compute() {
		return this;
	}

	/**
	 * @attribute syn
	 * @aspect GenericsParTypeDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsParTypeDecl.jrag:73
	 */
	public boolean isTypeVariable() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:14
	 */
	public boolean supertypeGenericClassDecl(GenericClassDecl type) {
		ASTNode$State state = state();
		try {
			return supertypeClassDecl(type);
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:20
	 */
	public boolean supertypeGenericInterfaceDecl(GenericInterfaceDecl type) {
		ASTNode$State state = state();
		try {
			return this == type || supertypeInterfaceDecl(type);
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:26
	 */
	public boolean supertypeRawClassDecl(RawClassDecl type) {
		ASTNode$State state = state();
		try {
			return supertypeParClassDecl(type);
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:30
	 */
	public boolean supertypeRawInterfaceDecl(RawInterfaceDecl type) {
		ASTNode$State state = state();
		try {
			return supertypeParInterfaceDecl(type);
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:46
	 */
	public boolean supertypeWildcard(WildcardType type) {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:57
	 */
	public boolean supertypeWildcardExtends(WildcardExtendsType type) {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:66
	 */
	public boolean supertypeWildcardSuper(WildcardSuperType type) {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:102
	 */
	public boolean isWildcard() {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:125
	 */
	public boolean supertypeParClassDecl(ParClassDecl type) {
		ASTNode$State state = state();
		try {
			return supertypeClassDecl(type);
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:129
	 */
	public boolean supertypeParInterfaceDecl(ParInterfaceDecl type) {
		ASTNode$State state = state();
		try {
			return supertypeInterfaceDecl(type);
		} finally {
		}
	}

	protected java.util.Map containedIn_TypeDecl_values;

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:141
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean containedIn(TypeDecl type) {
		Object _parameters = type;
		if (containedIn_TypeDecl_values == null)
			containedIn_TypeDecl_values = new java.util.HashMap(4);
		ASTNode$State.CircularValue _value;
		if (containedIn_TypeDecl_values.containsKey(_parameters)) {
			Object _o = containedIn_TypeDecl_values.get(_parameters);
			if (!(_o instanceof ASTNode$State.CircularValue)) {
				return ((Boolean) _o).booleanValue();
			} else
				_value = (ASTNode$State.CircularValue) _o;
		} else {
			_value = new ASTNode$State.CircularValue();
			containedIn_TypeDecl_values.put(_parameters, _value);
			_value.value = Boolean.valueOf(true);
		}
		ASTNode$State state = state();
		if (!state.IN_CIRCLE) {
			state.IN_CIRCLE = true;
			int num = state.boundariesCrossed;
			boolean isFinal = this.is$Final();
			boolean new_containedIn_TypeDecl_value;
			do {
				_value.visited = new Integer(state.CIRCLE_INDEX);
				state.CHANGE = false;
				new_containedIn_TypeDecl_value = containedIn_compute(type);
				if (new_containedIn_TypeDecl_value != ((Boolean) _value.value).booleanValue()) {
					state.CHANGE = true;
					_value.value = Boolean.valueOf(new_containedIn_TypeDecl_value);
				}
				state.CIRCLE_INDEX++;
			} while (state.CHANGE);
			if (isFinal && num == state().boundariesCrossed) {
				containedIn_TypeDecl_values.put(_parameters, new_containedIn_TypeDecl_value);
			} else {
				containedIn_TypeDecl_values.remove(_parameters);
				state.RESET_CYCLE = true;
				containedIn_compute(type);
				state.RESET_CYCLE = false;
			}
			state.IN_CIRCLE = false;
			return new_containedIn_TypeDecl_value;
		}
		if (!new Integer(state.CIRCLE_INDEX).equals(_value.visited)) {
			_value.visited = new Integer(state.CIRCLE_INDEX);
			boolean new_containedIn_TypeDecl_value = containedIn_compute(type);
			if (state.RESET_CYCLE) {
				containedIn_TypeDecl_values.remove(_parameters);
			} else if (new_containedIn_TypeDecl_value != ((Boolean) _value.value).booleanValue()) {
				state.CHANGE = true;
				_value.value = new_containedIn_TypeDecl_value;
			}
			return new_containedIn_TypeDecl_value;
		}
		return ((Boolean) _value.value).booleanValue();
	}

	/**
	 * @apilevel internal
	 */
	private boolean containedIn_compute(TypeDecl type) {
		if (type == this || type instanceof WildcardType)
			return true;
		else if (type instanceof WildcardExtendsType)
			return this.subtype(((WildcardExtendsType) type).extendsType());
		else if (type instanceof WildcardSuperType)
			return ((WildcardSuperType) type).superType().subtype(this);
		else if (type instanceof TypeVariable)
			return subtype(type);
		return sameStructure(type);
		// return false;
	}

	protected java.util.Map sameStructure_TypeDecl_values;

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:178
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean sameStructure(TypeDecl t) {
		Object _parameters = t;
		if (sameStructure_TypeDecl_values == null)
			sameStructure_TypeDecl_values = new java.util.HashMap(4);
		ASTNode$State.CircularValue _value;
		if (sameStructure_TypeDecl_values.containsKey(_parameters)) {
			Object _o = sameStructure_TypeDecl_values.get(_parameters);
			if (!(_o instanceof ASTNode$State.CircularValue)) {
				return ((Boolean) _o).booleanValue();
			} else
				_value = (ASTNode$State.CircularValue) _o;
		} else {
			_value = new ASTNode$State.CircularValue();
			sameStructure_TypeDecl_values.put(_parameters, _value);
			_value.value = Boolean.valueOf(true);
		}
		ASTNode$State state = state();
		if (!state.IN_CIRCLE) {
			state.IN_CIRCLE = true;
			int num = state.boundariesCrossed;
			boolean isFinal = this.is$Final();
			boolean new_sameStructure_TypeDecl_value;
			do {
				_value.visited = new Integer(state.CIRCLE_INDEX);
				state.CHANGE = false;
				new_sameStructure_TypeDecl_value = sameStructure_compute(t);
				if (new_sameStructure_TypeDecl_value != ((Boolean) _value.value).booleanValue()) {
					state.CHANGE = true;
					_value.value = Boolean.valueOf(new_sameStructure_TypeDecl_value);
				}
				state.CIRCLE_INDEX++;
			} while (state.CHANGE);
			if (isFinal && num == state().boundariesCrossed) {
				sameStructure_TypeDecl_values.put(_parameters, new_sameStructure_TypeDecl_value);
			} else {
				sameStructure_TypeDecl_values.remove(_parameters);
				state.RESET_CYCLE = true;
				sameStructure_compute(t);
				state.RESET_CYCLE = false;
			}
			state.IN_CIRCLE = false;
			return new_sameStructure_TypeDecl_value;
		}
		if (!new Integer(state.CIRCLE_INDEX).equals(_value.visited)) {
			_value.visited = new Integer(state.CIRCLE_INDEX);
			boolean new_sameStructure_TypeDecl_value = sameStructure_compute(t);
			if (state.RESET_CYCLE) {
				sameStructure_TypeDecl_values.remove(_parameters);
			} else if (new_sameStructure_TypeDecl_value != ((Boolean) _value.value).booleanValue()) {
				state.CHANGE = true;
				_value.value = new_sameStructure_TypeDecl_value;
			}
			return new_sameStructure_TypeDecl_value;
		}
		return ((Boolean) _value.value).booleanValue();
	}

	/**
	 * @apilevel internal
	 */
	private boolean sameStructure_compute(TypeDecl t) {
		return t == this;
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:291
	 */
	public boolean supertypeTypeVariable(TypeVariable type) {
		ASTNode$State state = state();
		try {
			if (type == this)
				return true;
			for (int i = 0; i < type.getNumTypeBound(); i++)
				if (type.getTypeBound(i).type().subtype(this))
					return true;
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:347
	 */
	public boolean supertypeLUBType(LUBType type) {
		ASTNode$State state = state();
		try {
			for (int i = 0; i < type.getNumTypeBound(); i++)
				if (!type.getTypeBound(i).type().subtype(this))
					return false;
			return true;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:366
	 */
	public boolean supertypeGLBType(GLBType type) {
		ASTNode$State state = state();
		try {
			// T1 && .. && Tn <: this, if exists 0 < i <= n Ti <: this
			for (int i = 0; i < type.getNumTypeBound(); i++)
				if (type.getTypeBound(i).type().subtype(this))
					return true;
			return false;
		} finally {
		}
	}

	protected java.util.Map subtype_TypeDecl_values;

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:405
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean subtype(TypeDecl type) {
		Object _parameters = type;
		if (subtype_TypeDecl_values == null)
			subtype_TypeDecl_values = new java.util.HashMap(4);
		ASTNode$State.CircularValue _value;
		if (subtype_TypeDecl_values.containsKey(_parameters)) {
			Object _o = subtype_TypeDecl_values.get(_parameters);
			if (!(_o instanceof ASTNode$State.CircularValue)) {
				return ((Boolean) _o).booleanValue();
			} else
				_value = (ASTNode$State.CircularValue) _o;
		} else {
			_value = new ASTNode$State.CircularValue();
			subtype_TypeDecl_values.put(_parameters, _value);
			_value.value = Boolean.valueOf(true);
		}
		ASTNode$State state = state();
		if (!state.IN_CIRCLE) {
			state.IN_CIRCLE = true;
			int num = state.boundariesCrossed;
			boolean isFinal = this.is$Final();
			boolean new_subtype_TypeDecl_value;
			do {
				_value.visited = new Integer(state.CIRCLE_INDEX);
				state.CHANGE = false;
				new_subtype_TypeDecl_value = subtype_compute(type);
				if (new_subtype_TypeDecl_value != ((Boolean) _value.value).booleanValue()) {
					state.CHANGE = true;
					_value.value = Boolean.valueOf(new_subtype_TypeDecl_value);
				}
				state.CIRCLE_INDEX++;
			} while (state.CHANGE);
			if (isFinal && num == state().boundariesCrossed) {
				subtype_TypeDecl_values.put(_parameters, new_subtype_TypeDecl_value);
			} else {
				subtype_TypeDecl_values.remove(_parameters);
				state.RESET_CYCLE = true;
				subtype_compute(type);
				state.RESET_CYCLE = false;
			}
			state.IN_CIRCLE = false;
			return new_subtype_TypeDecl_value;
		}
		if (!new Integer(state.CIRCLE_INDEX).equals(_value.visited)) {
			_value.visited = new Integer(state.CIRCLE_INDEX);
			boolean new_subtype_TypeDecl_value = subtype_compute(type);
			if (state.RESET_CYCLE) {
				subtype_TypeDecl_values.remove(_parameters);
			} else if (new_subtype_TypeDecl_value != ((Boolean) _value.value).booleanValue()) {
				state.CHANGE = true;
				_value.value = new_subtype_TypeDecl_value;
			}
			return new_subtype_TypeDecl_value;
		}
		return ((Boolean) _value.value).booleanValue();
	}

	/**
	 * @apilevel internal
	 */
	private boolean subtype_compute(TypeDecl type) {
		return type == this;
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:421
	 */
	public boolean supertypeClassDecl(ClassDecl type) {
		ASTNode$State state = state();
		try {
			return type == this;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:437
	 */
	public boolean supertypeInterfaceDecl(InterfaceDecl type) {
		ASTNode$State state = state();
		try {
			return type == this;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:450
	 */
	public boolean supertypeArrayDecl(ArrayDecl type) {
		ASTNode$State state = state();
		try {
			return this == type;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:472
	 */
	public boolean supertypePrimitiveType(PrimitiveType type) {
		ASTNode$State state = state();
		try {
			return type == this;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:479
	 */
	public boolean supertypeNullType(NullType type) {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:483
	 */
	public boolean supertypeVoidType(VoidType type) {
		ASTNode$State state = state();
		try {
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:493
	 */
	public boolean supertypeClassDeclSubstituted(ClassDeclSubstituted type) {
		ASTNode$State state = state();
		try {
			return type.original() == this || supertypeClassDecl(type);
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:503
	 */
	public boolean supertypeInterfaceDeclSubstituted(InterfaceDeclSubstituted type) {
		ASTNode$State state = state();
		try {
			return type.original() == this || supertypeInterfaceDecl(type);
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:513
	 */
	public boolean supertypeGenericClassDeclSubstituted(GenericClassDeclSubstituted type) {
		ASTNode$State state = state();
		try {
			return type.original() == this || supertypeGenericClassDecl(type);
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect GenericsSubtype
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:523
	 */
	public boolean supertypeGenericInterfaceDeclSubstituted(GenericInterfaceDeclSubstituted type) {
		ASTNode$State state = state();
		try {
			return type.original() == this || supertypeGenericInterfaceDecl(type);
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:82
	 */
	public TypeDecl stringPromotion() {
		ASTNode$State state = state();
		try {
			return this;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:94
	 */
	public MethodDecl methodWithArgs(String name, TypeDecl[] args) {
		ASTNode$State state = state();
		try {
			for (Iterator iter = memberMethods(name).iterator(); iter.hasNext();) {
				MethodDecl m = (MethodDecl) iter.next();
				if (m.getNumParameter() == args.length) {
					for (int i = 0; i < args.length; i++)
						if (m.getParameter(i).type() == args[i])
							return m;
				}
			}
			return null;
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean enclosingVariables_computed = false;
	/**
	 * @apilevel internal
	 */
	protected Collection enclosingVariables_value;

	/**
	 * @attribute syn
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:145
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public Collection enclosingVariables() {
		if (enclosingVariables_computed) {
			return enclosingVariables_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		enclosingVariables_value = enclosingVariables_compute();
		if (isFinal && num == state().boundariesCrossed)
			enclosingVariables_computed = true;
		return enclosingVariables_value;
	}

	/**
	 * @apilevel internal
	 */
	private Collection enclosingVariables_compute() {
		HashSet set = new HashSet();
		for (TypeDecl e = this; e != null; e = e.enclosingType())
			if (e.isLocalClass() || e.isAnonymous())
				collectEnclosingVariables(set, e.enclosingType());
		if (isClassDecl()) {
			ClassDecl classDecl = (ClassDecl) this;
			if (classDecl.isNestedType() && classDecl.hasSuperclass())
				set.addAll(classDecl.superclass().enclosingVariables());
		}
		return set;
	}

	/**
	 * @attribute syn
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:394
	 */
	public boolean isAnonymousInNonStaticContext() {
		ASTNode$State state = state();
		try {
			return isAnonymous() && !((ClassInstanceExpr) getParent().getParent()).unqualifiedScope().inStaticContext()
					&& (!inExplicitConstructorInvocation() || enclosingBodyDecl().hostType().isInnerType());
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:400
	 */
	public boolean needsEnclosing() {
		ASTNode$State state = state();
		try {
			if (isAnonymous())
				return isAnonymousInNonStaticContext();
			else if (isLocalClass())
				return !inStaticContext();
			else if (isInnerType())
				return true;
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:410
	 */
	public boolean needsSuperEnclosing() {
		ASTNode$State state = state();
		try {
			if (!isAnonymous())
				return false;
			TypeDecl superClass = ((ClassDecl) this).superclass();
			if (superClass.isLocalClass())
				return !superClass.inStaticContext();
			else if (superClass.isInnerType())
				return true;
			if (needsEnclosing() && enclosing() == superEnclosing())
				return false;
			return false;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:422
	 */
	public TypeDecl enclosing() {
		ASTNode$State state = state();
		try {
			if (!needsEnclosing())
				return null;
			TypeDecl typeDecl = enclosingType();
			if (isAnonymous() && inExplicitConstructorInvocation())
				typeDecl = typeDecl.enclosingType();
			return typeDecl;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:430
	 */
	public TypeDecl superEnclosing() {
		ASTNode$State state = state();
		try {
			return null;
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean uniqueIndex_computed = false;
	/**
	 * @apilevel internal
	 */
	protected int uniqueIndex_value;

	/**
	 * @attribute syn
	 * @aspect Java2Rewrites
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:12
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public int uniqueIndex() {
		if (uniqueIndex_computed) {
			return uniqueIndex_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		uniqueIndex_value = uniqueIndex_compute();
		if (isFinal && num == state().boundariesCrossed)
			uniqueIndex_computed = true;
		return uniqueIndex_value;
	}

	/**
	 * @apilevel internal
	 */
	private int uniqueIndex_compute() {
		return topLevelType().uniqueIndexCounter++;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean jvmName_computed = false;
	/**
	 * @apilevel internal
	 */
	protected String jvmName_value;

	/**
	 * @attribute syn
	 * @aspect Java2Rewrites
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:15
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public String jvmName() {
		if (jvmName_computed) {
			return jvmName_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		jvmName_value = jvmName_compute();
		if (isFinal && num == state().boundariesCrossed)
			jvmName_computed = true;
		return jvmName_value;
	}

	/**
	 * @apilevel internal
	 */
	private String jvmName_compute() {
		throw new Error("Jvm name only supported for reference types and not " + getClass().getName());
	}

	/**
	 * @attribute syn
	 * @aspect Java2Rewrites
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:44
	 */
	public String primitiveClassName() {
		ASTNode$State state = state();
		try {
			throw new Error("primitiveClassName not supported for " + name() + " of type " + getClass().getName());
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect Java2Rewrites
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:57
	 */
	public String referenceClassFieldName() {
		ASTNode$State state = state();
		try {
			throw new Error("referenceClassFieldName not supported for " + name() + " of type " + getClass().getName());
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean getSootClassDecl_computed = false;
	/**
	 * @apilevel internal
	 */
	protected SootClass getSootClassDecl_value;

	/**
	 * @attribute syn
	 * @aspect EmitJimpleRefinements
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/EmitJimpleRefinements.jrag:63
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public SootClass getSootClassDecl() {
		if (getSootClassDecl_computed) {
			return getSootClassDecl_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		getSootClassDecl_value = getSootClassDecl_compute();
		if (isFinal && num == state().boundariesCrossed)
			getSootClassDecl_computed = true;
		return getSootClassDecl_value;
	}

	/**
	 * @apilevel internal
	 */
	private SootClass getSootClassDecl_compute() {
		if (erasure() != this)
			return erasure().getSootClassDecl();
		if (compilationUnit().fromSource()) {
			return sootClass();
		} else {
			if (options().verbose())
				System.out.println("Loading .class file " + jvmName());
			return SootResolver.v().makeClassRef(jvmName());
			/*
			 * 
			 * RefType type = (RefType) Scene.v().getRefType(jvmName());
			 * SootClass toReturn = null; if( type != null ) toReturn =
			 * type.getSootClass(); if(toReturn != null) { return toReturn; }
			 * SootClass c = new SootClass(jvmName()); c.setPhantom(true);
			 * Scene.v().addClass(c); return c;
			 */

			// return Scene.v().getSootClass(jvmName());
			/*
			 * SootClass sc = Scene.v().loadClass(jvmName(),
			 * SootClass.SIGNATURES); sc.setLibraryClass(); return sc;
			 */
		}

	}

	/**
	 * @apilevel internal
	 */
	protected boolean getSootType_computed = false;
	/**
	 * @apilevel internal
	 */
	protected Type getSootType_value;

	/**
	 * @attribute syn
	 * @aspect EmitJimpleRefinements
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/EmitJimpleRefinements.jrag:20
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public Type getSootType() {
		if (getSootType_computed) {
			return getSootType_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		getSootType_value = getSootType_compute();
		if (isFinal && num == state().boundariesCrossed)
			getSootType_computed = true;
		return getSootType_value;
	}

	/**
	 * @apilevel internal
	 */
	private Type getSootType_compute() {
		return RefType.v(erasure().jvmName());
	}

	/**
	 * @attribute syn
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:58
	 */
	public soot.RefType sootRef() {
		ASTNode$State state = state();
		try {
			return (soot.RefType) getSootType();
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean sootClass_computed = false;
	/**
	 * @apilevel internal
	 */
	protected SootClass sootClass_value;

	/**
	 * @attribute syn
	 * @aspect GenericsCodegen
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:413
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public SootClass sootClass() {
		if (sootClass_computed) {
			return sootClass_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		sootClass_value = sootClass_compute();
		if (isFinal && num == state().boundariesCrossed)
			sootClass_computed = true;
		return sootClass_value;
	}

	/**
	 * @apilevel internal
	 */
	private SootClass sootClass_compute() {
		return erasure() != this ? erasure().sootClass() : refined_EmitJimple_TypeDecl_sootClass();
	}

	/**
	 * @attribute syn
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:85
	 */
	public String sourceNameWithoutPath() {
		ASTNode$State state = state();
		try {
			String s = sourceFile();
			return s != null ? s.substring(s.lastIndexOf(java.io.File.separatorChar) + 1) : "Unknown";
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:90
	 */
	public int sootTypeModifiers() {
		ASTNode$State state = state();
		try {
			int result = 0;
			if (isNestedType()) {
				result |= soot.Modifier.PUBLIC;
			} else {
				if (isPublic())
					result |= soot.Modifier.PUBLIC;
				if (isProtected())
					result |= soot.Modifier.PROTECTED;
				if (isPrivate())
					result |= soot.Modifier.PRIVATE;
			}
			if (isFinal())
				result |= soot.Modifier.FINAL;
			if (isStatic())
				result |= soot.Modifier.STATIC;
			if (isAbstract())
				result |= soot.Modifier.ABSTRACT;
			return result;
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean needsClinit_computed = false;
	/**
	 * @apilevel internal
	 */
	protected boolean needsClinit_value;

	/**
	 * @attribute syn
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:887
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean needsClinit() {
		if (needsClinit_computed) {
			return needsClinit_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		needsClinit_value = needsClinit_compute();
		if (isFinal && num == state().boundariesCrossed)
			needsClinit_computed = true;
		return needsClinit_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean needsClinit_compute() {
		for (int i = 0; i < getNumBodyDecl(); i++) {
			BodyDecl b = getBodyDecl(i);
			if (b instanceof FieldDeclaration) {
				FieldDeclaration f = (FieldDeclaration) b;
				if (f.isStatic() && f.hasInit() && f.generate()) {
					return true;
				}
			} else if (b instanceof StaticInitializer && b.generate()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean innerClassesAttributeEntries_computed = false;
	/**
	 * @apilevel internal
	 */
	protected Collection innerClassesAttributeEntries_value;

	/**
	 * @attribute syn
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:962
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public Collection innerClassesAttributeEntries() {
		if (innerClassesAttributeEntries_computed) {
			return innerClassesAttributeEntries_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		innerClassesAttributeEntries_value = innerClassesAttributeEntries_compute();
		if (isFinal && num == state().boundariesCrossed)
			innerClassesAttributeEntries_computed = true;
		return innerClassesAttributeEntries_value;
	}

	/**
	 * @apilevel internal
	 */
	private Collection innerClassesAttributeEntries_compute() {
		HashSet list = new HashSet();
		if (isNestedType())
			list.add(this);
		for (Iterator iter = nestedTypes().iterator(); iter.hasNext();)
			list.add(iter.next());
		for (Iterator iter = usedNestedTypes().iterator(); iter.hasNext();)
			list.add(iter.next());
		return list;
	}

	protected java.util.Map getSootField_String_TypeDecl_values;

	/**
	 * @attribute syn
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:996
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public SootField getSootField(String name, TypeDecl type) {
		java.util.List _parameters = new java.util.ArrayList(2);
		_parameters.add(name);
		_parameters.add(type);
		if (getSootField_String_TypeDecl_values == null)
			getSootField_String_TypeDecl_values = new java.util.HashMap(4);
		if (getSootField_String_TypeDecl_values.containsKey(_parameters)) {
			return (SootField) getSootField_String_TypeDecl_values.get(_parameters);
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		SootField getSootField_String_TypeDecl_value = getSootField_compute(name, type);
		if (isFinal && num == state().boundariesCrossed)
			getSootField_String_TypeDecl_values.put(_parameters, getSootField_String_TypeDecl_value);
		return getSootField_String_TypeDecl_value;
	}

	/**
	 * @apilevel internal
	 */
	private SootField getSootField_compute(String name, TypeDecl type) {
		SootField f = Scene.v().makeSootField(name, type.getSootType(), 0);
		getSootClassDecl().addField(f);
		return f;
	}

	/**
	 * @attribute syn
	 * @aspect LocalNum
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/LocalNum.jrag:52
	 */
	public int variableSize() {
		ASTNode$State state = state();
		try {
			return 1;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect AnnotationsCodegen
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:322
	 */
	public String typeDescriptor() {
		ASTNode$State state = state();
		try {
			return jvmName();
		} finally {
		}
	}

	protected java.util.Map createEnumMethod_TypeDecl_values;

	/**
	 * @attribute syn
	 * @aspect EnumsCodegen
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/EnumsCodegen.jrag:42
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public MethodDecl createEnumMethod(TypeDecl enumDecl) {
		Object _parameters = enumDecl;
		if (createEnumMethod_TypeDecl_values == null)
			createEnumMethod_TypeDecl_values = new java.util.HashMap(4);
		if (createEnumMethod_TypeDecl_values.containsKey(_parameters)) {
			return (MethodDecl) createEnumMethod_TypeDecl_values.get(_parameters);
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		MethodDecl createEnumMethod_TypeDecl_value = createEnumMethod_compute(enumDecl);
		if (isFinal && num == state().boundariesCrossed)
			createEnumMethod_TypeDecl_values.put(_parameters, createEnumMethod_TypeDecl_value);
		return createEnumMethod_TypeDecl_value;
	}

	/**
	 * @apilevel internal
	 */
	private MethodDecl createEnumMethod_compute(TypeDecl enumDecl) {
		MethodDecl m = new MethodDecl(
				new Modifiers(
						new List().add(new Modifier("static")).add(new Modifier("final")).add(new Modifier("private"))),
				typeInt().arrayType()
						.createQualifiedAccess(),
				"$SwitchMap$"
						+ enumDecl.fullName().replace('.',
								'$'),
				new List(),
				new List(), new Opt(
						new Block(
								new List()
										.add(new IfStmt(new EQExpr(
												createEnumArray(enumDecl).createBoundFieldAccess(),
												new NullLiteral("null")),
												AssignExpr.asStmt(createEnumArray(enumDecl)
														.createBoundFieldAccess(),
														new ArrayCreationExpr(
																new ArrayTypeWithSizeAccess(
																		typeInt().createQualifiedAccess(),
																		enumDecl.createQualifiedAccess()
																				.qualifiesAccess(new MethodAccess(
																						"values", new List()))
																				.qualifiesAccess(
																						new VarAccess("length"))),
																new Opt())),
												new Opt()))
										.add(new ReturnStmt(createEnumArray(enumDecl).createBoundFieldAccess())))));
		// add method declaration as a body declaration
		getBodyDeclList().insertChild(m, 1);
		// trigger possible rewrites
		return (MethodDecl) getBodyDeclList().getChild(1);
	}

	protected java.util.Map createEnumIndex_EnumConstant_values;

	/**
	 * @attribute syn
	 * @aspect EnumsCodegen
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/EnumsCodegen.jrag:86
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public int createEnumIndex(EnumConstant e) {
		Object _parameters = e;
		if (createEnumIndex_EnumConstant_values == null)
			createEnumIndex_EnumConstant_values = new java.util.HashMap(4);
		if (createEnumIndex_EnumConstant_values.containsKey(_parameters)) {
			return ((Integer) createEnumIndex_EnumConstant_values.get(_parameters)).intValue();
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		int createEnumIndex_EnumConstant_value = createEnumIndex_compute(e);
		if (isFinal && num == state().boundariesCrossed)
			createEnumIndex_EnumConstant_values.put(_parameters, Integer.valueOf(createEnumIndex_EnumConstant_value));
		return createEnumIndex_EnumConstant_value;
	}

	/**
	 * @apilevel internal
	 */
	private int createEnumIndex_compute(EnumConstant e) {
		if (createEnumIndexMap == null)
			createEnumIndexMap = new HashMap();
		if (!createEnumIndexMap.containsKey(e.hostType()))
			createEnumIndexMap.put(e.hostType(), new Integer(0));
		Integer i = (Integer) createEnumIndexMap.get(e.hostType());
		i = new Integer(i.intValue() + 1);
		createEnumIndexMap.put(e.hostType(), i);

		MethodDecl m = createEnumMethod(e.hostType());
		List list = m.getBlock().getStmtList();
		list.insertChild(new TryStmt(
				new Block(new List().add(AssignExpr.asStmt(
						createEnumArray(e.hostType()).createBoundFieldAccess()
								.qualifiesAccess(new ArrayAccess(e.createBoundFieldAccess()
										.qualifiesAccess(new MethodAccess("ordinal", new List())))),
						new IntegerLiteral(i.toString())))),
				new List()
						.add(new BasicCatch(
								new ParameterDeclaration(
										lookupType("java.lang", "NoSuchFieldError").createQualifiedAccess(), "e"),
								new Block(new List()))),
				new Opt()), list.getNumChild() - 1);
		return i.intValue();
	}

	protected java.util.Map createEnumArray_TypeDecl_values;

	/**
	 * @attribute syn
	 * @aspect EnumsCodegen
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/EnumsCodegen.jrag:129
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public FieldDeclaration createEnumArray(TypeDecl enumDecl) {
		Object _parameters = enumDecl;
		if (createEnumArray_TypeDecl_values == null)
			createEnumArray_TypeDecl_values = new java.util.HashMap(4);
		if (createEnumArray_TypeDecl_values.containsKey(_parameters)) {
			return (FieldDeclaration) createEnumArray_TypeDecl_values.get(_parameters);
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		FieldDeclaration createEnumArray_TypeDecl_value = createEnumArray_compute(enumDecl);
		if (isFinal && num == state().boundariesCrossed)
			createEnumArray_TypeDecl_values.put(_parameters, createEnumArray_TypeDecl_value);
		return createEnumArray_TypeDecl_value;
	}

	/**
	 * @apilevel internal
	 */
	private FieldDeclaration createEnumArray_compute(TypeDecl enumDecl) {
		FieldDeclaration f = new FieldDeclaration(
				new Modifiers(
						new List().add(new Modifier("static")).add(new Modifier("final")).add(new Modifier("private"))),
				typeInt().arrayType().createQualifiedAccess(), "$SwitchMap$" + enumDecl.fullName().replace('.', '$'),
				new Opt());
		// add field declaration as a body declaration
		getBodyDeclList().insertChild(f, 0);
		// trigger possible rewrites
		return (FieldDeclaration) getBodyDeclList().getChild(0);
	}

	/**
	 * @attribute syn
	 * @aspect GenericsCodegen
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:333
	 */
	public SimpleSet bridgeCandidates(String signature) {
		ASTNode$State state = state();
		try {
			return SimpleSet.emptySet;
		} finally {
		}
	}

	/**
	 * @return true if the modifier list includes the SafeVarargs annotation
	 * @attribute syn
	 * @aspect SafeVarargs
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/SafeVarargs.jrag:14
	 */
	public boolean hasAnnotationSafeVarargs() {
		ASTNode$State state = state();
		try {
			return getModifiers().hasAnnotationSafeVarargs();
		} finally {
		}
	}

	/**
	 * A type is reifiable if it either refers to a non-parameterized type, is a
	 * raw type, is a parameterized type with only unbound wildcard parameters
	 * or is an array type with a reifiable type parameter.
	 *
	 * @see "JLSv3 &sect;4.7"
	 * @attribute syn
	 * @aspect SafeVarargs
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/SafeVarargs.jrag:106
	 */
	public boolean isReifiable() {
		ASTNode$State state = state();
		try {
			return true;
		} finally {
		}
	}

	/**
	 * An unchecked conversion occurs when converting from a raw type G to a
	 * generic type G<T1, ..., Tn>.
	 * 
	 * @attribute syn
	 * @aspect UncheckedConversion
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/UncheckedConversion.jrag:50
	 */
	public boolean isUncheckedConversionTo(TypeDecl dest) {
		ASTNode$State state = state();
		try {
			return (!dest.isRawType()) && this.isRawType();
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean componentType_computed = false;
	/**
	 * @apilevel internal
	 */
	protected TypeDecl componentType_value;

	/**
	 * @attribute inh
	 * @aspect Arrays
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Arrays.jrag:21
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl componentType() {
		if (componentType_computed) {
			return componentType_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		componentType_value = getParent().Define_TypeDecl_componentType(this, null);
		if (isFinal && num == state().boundariesCrossed)
			componentType_computed = true;
		return componentType_value;
	}

	/**
	 * @attribute inh
	 * @aspect Arrays
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Arrays.jrag:50
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl typeCloneable() {
		ASTNode$State state = state();
		TypeDecl typeCloneable_value = getParent().Define_TypeDecl_typeCloneable(this, null);
		return typeCloneable_value;
	}

	/**
	 * @attribute inh
	 * @aspect Arrays
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Arrays.jrag:51
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl typeSerializable() {
		ASTNode$State state = state();
		TypeDecl typeSerializable_value = getParent().Define_TypeDecl_typeSerializable(this, null);
		return typeSerializable_value;
	}

	/**
	 * @attribute inh
	 * @aspect ClassPath
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:31
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public CompilationUnit compilationUnit() {
		ASTNode$State state = state();
		CompilationUnit compilationUnit_value = getParent().Define_CompilationUnit_compilationUnit(this, null);
		return compilationUnit_value;
	}

	protected java.util.Map isDAbefore_Variable_values;

	/**
	 * @attribute inh
	 * @aspect DA
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:240
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean isDAbefore(Variable v) {
		Object _parameters = v;
		if (isDAbefore_Variable_values == null)
			isDAbefore_Variable_values = new java.util.HashMap(4);
		if (isDAbefore_Variable_values.containsKey(_parameters)) {
			return ((Boolean) isDAbefore_Variable_values.get(_parameters)).booleanValue();
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		boolean isDAbefore_Variable_value = getParent().Define_boolean_isDAbefore(this, null, v);
		if (isFinal && num == state().boundariesCrossed)
			isDAbefore_Variable_values.put(_parameters, Boolean.valueOf(isDAbefore_Variable_value));
		return isDAbefore_Variable_value;
	}

	protected java.util.Map isDUbefore_Variable_values;

	/**
	 * @attribute inh
	 * @aspect DU
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:705
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean isDUbefore(Variable v) {
		Object _parameters = v;
		if (isDUbefore_Variable_values == null)
			isDUbefore_Variable_values = new java.util.HashMap(4);
		if (isDUbefore_Variable_values.containsKey(_parameters)) {
			return ((Boolean) isDUbefore_Variable_values.get(_parameters)).booleanValue();
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		boolean isDUbefore_Variable_value = getParent().Define_boolean_isDUbefore(this, null, v);
		if (isFinal && num == state().boundariesCrossed)
			isDUbefore_Variable_values.put(_parameters, Boolean.valueOf(isDUbefore_Variable_value));
		return isDUbefore_Variable_value;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean typeException_computed = false;
	/**
	 * @apilevel internal
	 */
	protected TypeDecl typeException_value;

	/**
	 * @attribute inh
	 * @aspect ExceptionHandling
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:14
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl typeException() {
		if (typeException_computed) {
			return typeException_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		typeException_value = getParent().Define_TypeDecl_typeException(this, null);
		if (isFinal && num == state().boundariesCrossed)
			typeException_computed = true;
		return typeException_value;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean typeRuntimeException_computed = false;
	/**
	 * @apilevel internal
	 */
	protected TypeDecl typeRuntimeException_value;

	/**
	 * @attribute inh
	 * @aspect ExceptionHandling
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:16
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl typeRuntimeException() {
		if (typeRuntimeException_computed) {
			return typeRuntimeException_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		typeRuntimeException_value = getParent().Define_TypeDecl_typeRuntimeException(this, null);
		if (isFinal && num == state().boundariesCrossed)
			typeRuntimeException_computed = true;
		return typeRuntimeException_value;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean typeError_computed = false;
	/**
	 * @apilevel internal
	 */
	protected TypeDecl typeError_value;

	/**
	 * @attribute inh
	 * @aspect ExceptionHandling
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:18
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl typeError() {
		if (typeError_computed) {
			return typeError_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		typeError_value = getParent().Define_TypeDecl_typeError(this, null);
		if (isFinal && num == state().boundariesCrossed)
			typeError_computed = true;
		return typeError_value;
	}

	protected java.util.Map lookupMethod_String_values;

	/**
	 * @attribute inh
	 * @aspect LookupMethod
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:26
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public Collection lookupMethod(String name) {
		Object _parameters = name;
		if (lookupMethod_String_values == null)
			lookupMethod_String_values = new java.util.HashMap(4);
		if (lookupMethod_String_values.containsKey(_parameters)) {
			return (Collection) lookupMethod_String_values.get(_parameters);
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		Collection lookupMethod_String_value = getParent().Define_Collection_lookupMethod(this, null, name);
		if (isFinal && num == state().boundariesCrossed)
			lookupMethod_String_values.put(_parameters, lookupMethod_String_value);
		return lookupMethod_String_value;
	}

	/**
	 * @attribute inh
	 * @aspect SpecialClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:62
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl typeInt() {
		ASTNode$State state = state();
		TypeDecl typeInt_value = getParent().Define_TypeDecl_typeInt(this, null);
		return typeInt_value;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean typeObject_computed = false;
	/**
	 * @apilevel internal
	 */
	protected TypeDecl typeObject_value;

	/**
	 * @attribute inh
	 * @aspect SpecialClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:65
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl typeObject() {
		if (typeObject_computed) {
			return typeObject_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		typeObject_value = getParent().Define_TypeDecl_typeObject(this, null);
		if (isFinal && num == state().boundariesCrossed)
			typeObject_computed = true;
		return typeObject_value;
	}

	/**
	 * @attribute inh
	 * @aspect LookupFullyQualifiedTypes
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:98
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl lookupType(String packageName, String typeName) {
		ASTNode$State state = state();
		TypeDecl lookupType_String_String_value = getParent().Define_TypeDecl_lookupType(this, null, packageName,
				typeName);
		return lookupType_String_String_value;
	}

	protected java.util.Map lookupType_String_values;

	/**
	 * @attribute inh
	 * @aspect TypeScopePropagation
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:260
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public SimpleSet lookupType(String name) {
		Object _parameters = name;
		if (lookupType_String_values == null)
			lookupType_String_values = new java.util.HashMap(4);
		if (lookupType_String_values.containsKey(_parameters)) {
			return (SimpleSet) lookupType_String_values.get(_parameters);
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		SimpleSet lookupType_String_value = getParent().Define_SimpleSet_lookupType(this, null, name);
		if (isFinal && num == state().boundariesCrossed)
			lookupType_String_values.put(_parameters, lookupType_String_value);
		return lookupType_String_value;
	}

	protected java.util.Map lookupVariable_String_values;

	/**
	 * @attribute inh
	 * @aspect VariableScope
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:14
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public SimpleSet lookupVariable(String name) {
		Object _parameters = name;
		if (lookupVariable_String_values == null)
			lookupVariable_String_values = new java.util.HashMap(4);
		if (lookupVariable_String_values.containsKey(_parameters)) {
			return (SimpleSet) lookupVariable_String_values.get(_parameters);
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		SimpleSet lookupVariable_String_value = getParent().Define_SimpleSet_lookupVariable(this, null, name);
		if (isFinal && num == state().boundariesCrossed)
			lookupVariable_String_values.put(_parameters, lookupVariable_String_value);
		return lookupVariable_String_value;
	}

	/**
	 * @attribute inh
	 * @aspect NameCheck
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:242
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean hasPackage(String packageName) {
		ASTNode$State state = state();
		boolean hasPackage_String_value = getParent().Define_boolean_hasPackage(this, null, packageName);
		return hasPackage_String_value;
	}

	/**
	 * @attribute inh
	 * @aspect NameCheck
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:245
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public ASTNode enclosingBlock() {
		ASTNode$State state = state();
		ASTNode enclosingBlock_value = getParent().Define_ASTNode_enclosingBlock(this, null);
		return enclosingBlock_value;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean packageName_computed = false;
	/**
	 * @apilevel internal
	 */
	protected String packageName_value;

	/**
	 * @attribute inh
	 * @aspect TypeName
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:89
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public String packageName() {
		if (packageName_computed) {
			return packageName_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		packageName_value = getParent().Define_String_packageName(this, null);
		if (isFinal && num == state().boundariesCrossed)
			packageName_computed = true;
		return packageName_value;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean isAnonymous_computed = false;
	/**
	 * @apilevel internal
	 */
	protected boolean isAnonymous_value;

	/**
	 * @attribute inh
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:216
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean isAnonymous() {
		if (isAnonymous_computed) {
			return isAnonymous_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		isAnonymous_value = getParent().Define_boolean_isAnonymous(this, null);
		if (isFinal && num == state().boundariesCrossed)
			isAnonymous_computed = true;
		return isAnonymous_value;
	}

	/**
	 * @attribute inh
	 * @aspect NestedTypes
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:496
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl enclosingType() {
		ASTNode$State state = state();
		TypeDecl enclosingType_value = getParent().Define_TypeDecl_enclosingType(this, null);
		return enclosingType_value;
	}

	/**
	 * @attribute inh
	 * @aspect NestedTypes
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:512
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public BodyDecl enclosingBodyDecl() {
		ASTNode$State state = state();
		BodyDecl enclosingBodyDecl_value = getParent().Define_BodyDecl_enclosingBodyDecl(this, null);
		return enclosingBodyDecl_value;
	}

	/**
	 * @attribute inh
	 * @aspect NestedTypes
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:518
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean isNestedType() {
		ASTNode$State state = state();
		boolean isNestedType_value = getParent().Define_boolean_isNestedType(this, null);
		return isNestedType_value;
	}

	/**
	 * @attribute inh
	 * @aspect NestedTypes
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:526
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean isMemberType() {
		ASTNode$State state = state();
		boolean isMemberType_value = getParent().Define_boolean_isMemberType(this, null);
		return isMemberType_value;
	}

	/**
	 * @attribute inh
	 * @aspect NestedTypes
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:540
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean isLocalClass() {
		ASTNode$State state = state();
		boolean isLocalClass_value = getParent().Define_boolean_isLocalClass(this, null);
		return isLocalClass_value;
	}

	/**
	 * @attribute inh
	 * @aspect NestedTypes
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:565
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public String hostPackage() {
		ASTNode$State state = state();
		String hostPackage_value = getParent().Define_String_hostPackage(this, null);
		return hostPackage_value;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean unknownType_computed = false;
	/**
	 * @apilevel internal
	 */
	protected TypeDecl unknownType_value;

	/**
	 * @attribute inh
	 * @aspect Circularity
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:675
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl unknownType() {
		if (unknownType_computed) {
			return unknownType_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		unknownType_value = getParent().Define_TypeDecl_unknownType(this, null);
		if (isFinal && num == state().boundariesCrossed)
			unknownType_computed = true;
		return unknownType_value;
	}

	/**
	 * @attribute inh
	 * @aspect TypeCheck
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:402
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl typeVoid() {
		ASTNode$State state = state();
		TypeDecl typeVoid_value = getParent().Define_TypeDecl_typeVoid(this, null);
		return typeVoid_value;
	}

	/**
	 * @attribute inh
	 * @aspect TypeCheck
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:505
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl enclosingInstance() {
		ASTNode$State state = state();
		TypeDecl enclosingInstance_value = getParent().Define_TypeDecl_enclosingInstance(this, null);
		return enclosingInstance_value;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean inExplicitConstructorInvocation_computed = false;
	/**
	 * @apilevel internal
	 */
	protected boolean inExplicitConstructorInvocation_value;

	/**
	 * @attribute inh
	 * @aspect TypeHierarchyCheck
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:127
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean inExplicitConstructorInvocation() {
		if (inExplicitConstructorInvocation_computed) {
			return inExplicitConstructorInvocation_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		inExplicitConstructorInvocation_value = getParent().Define_boolean_inExplicitConstructorInvocation(this, null);
		if (isFinal && num == state().boundariesCrossed)
			inExplicitConstructorInvocation_computed = true;
		return inExplicitConstructorInvocation_value;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean inStaticContext_computed = false;
	/**
	 * @apilevel internal
	 */
	protected boolean inStaticContext_value;

	/**
	 * @attribute inh
	 * @aspect TypeHierarchyCheck
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:135
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean inStaticContext() {
		if (inStaticContext_computed) {
			return inStaticContext_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		inStaticContext_value = getParent().Define_boolean_inStaticContext(this, null);
		if (isFinal && num == state().boundariesCrossed)
			inStaticContext_computed = true;
		return inStaticContext_value;
	}

	/**
	 * @attribute inh
	 * @aspect Annotations
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:280
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean withinSuppressWarnings(String s) {
		ASTNode$State state = state();
		boolean withinSuppressWarnings_String_value = getParent().Define_boolean_withinSuppressWarnings(this, null, s);
		return withinSuppressWarnings_String_value;
	}

	/**
	 * @attribute inh
	 * @aspect Annotations
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:379
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean withinDeprecatedAnnotation() {
		ASTNode$State state = state();
		boolean withinDeprecatedAnnotation_value = getParent().Define_boolean_withinDeprecatedAnnotation(this, null);
		return withinDeprecatedAnnotation_value;
	}

	/**
	 * @attribute inh
	 * @aspect LookupParTypeDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1384
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl typeWildcard() {
		ASTNode$State state = state();
		TypeDecl typeWildcard_value = getParent().Define_TypeDecl_typeWildcard(this, null);
		return typeWildcard_value;
	}

	/**
	 * @attribute inh
	 * @aspect LookupParTypeDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1397
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl lookupWildcardExtends(TypeDecl typeDecl) {
		ASTNode$State state = state();
		TypeDecl lookupWildcardExtends_TypeDecl_value = getParent().Define_TypeDecl_lookupWildcardExtends(this, null,
				typeDecl);
		return lookupWildcardExtends_TypeDecl_value;
	}

	/**
	 * @attribute inh
	 * @aspect LookupParTypeDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1410
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl lookupWildcardSuper(TypeDecl typeDecl) {
		ASTNode$State state = state();
		TypeDecl lookupWildcardSuper_TypeDecl_value = getParent().Define_TypeDecl_lookupWildcardSuper(this, null,
				typeDecl);
		return lookupWildcardSuper_TypeDecl_value;
	}

	/**
	 * @attribute inh
	 * @aspect LookupParTypeDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1430
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public LUBType lookupLUBType(Collection bounds) {
		ASTNode$State state = state();
		LUBType lookupLUBType_Collection_value = getParent().Define_LUBType_lookupLUBType(this, null, bounds);
		return lookupLUBType_Collection_value;
	}

	/**
	 * @attribute inh
	 * @aspect LookupParTypeDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1468
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public GLBType lookupGLBType(ArrayList bounds) {
		ASTNode$State state = state();
		GLBType lookupGLBType_ArrayList_value = getParent().Define_GLBType_lookupGLBType(this, null, bounds);
		return lookupGLBType_ArrayList_value;
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Arrays.jrag:20
	 * @apilevel internal
	 */
	public TypeDecl Define_TypeDecl_componentType(ASTNode caller, ASTNode child) {
		if (caller == arrayType_value) {
			return this;
		} else {
			return getParent().Define_TypeDecl_componentType(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:20
	 * @apilevel internal
	 */
	public boolean Define_boolean_isDest(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return false;
		} else {
			return getParent().Define_boolean_isDest(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:30
	 * @apilevel internal
	 */
	public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return true;
		} else {
			return getParent().Define_boolean_isSource(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:245
	 * @apilevel internal
	 */
	public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			{
				BodyDecl b = getBodyDecl(childIndex);
				// if(b instanceof MethodDecl || b instanceof MemberTypeDecl) {
				if (!v.isInstanceVariable() && !v.isClassVariable()) {
					if (v.hostType() != this)
						return isDAbefore(v);
					return false;
				}
				if (b instanceof FieldDeclaration && !((FieldDeclaration) b).isStatic() && v.isClassVariable())
					return true;

				if (b instanceof MethodDecl) {
					return true;
				}
				if (b instanceof MemberTypeDecl && v.isBlank() && v.isFinal() && v.hostType() == this)
					return true;
				if (v.isClassVariable() || v.isInstanceVariable()) {
					if (v.isFinal() && v.hostType() != this && instanceOf(v.hostType()))
						return true;
					int index = childIndex - 1;
					if (b instanceof ConstructorDecl)
						index = getNumBodyDecl() - 1;

					for (int i = index; i >= 0; i--) {
						b = getBodyDecl(i);
						if (b instanceof FieldDeclaration) {
							FieldDeclaration f = (FieldDeclaration) b;
							if ((v.isClassVariable() && f.isStatic()) || (v.isInstanceVariable() && !f.isStatic())) {
								boolean c = f.isDAafter(v);
								// System.err.println("DefiniteAssignment: is "
								// + v.name() + " DA after index " + i + ", " +
								// f + ": " + c);
								return c;
								// return f.isDAafter(v);
							}
						} else if (b instanceof StaticInitializer && v.isClassVariable()) {
							StaticInitializer si = (StaticInitializer) b;
							return si.isDAafter(v);
						} else if (b instanceof InstanceInitializer && v.isInstanceVariable()) {
							InstanceInitializer ii = (InstanceInitializer) b;
							return ii.isDAafter(v);
						}
					}
				}
				return isDAbefore(v);
			}
		} else {
			return getParent().Define_boolean_isDAbefore(this, caller, v);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:712
	 * @apilevel internal
	 */
	public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			{
				BodyDecl b = getBodyDecl(childIndex);
				if (b instanceof MethodDecl || b instanceof MemberTypeDecl) {
					return false;
				}
				if (v.isClassVariable() || v.isInstanceVariable()) {
					int index = childIndex - 1;
					if (b instanceof ConstructorDecl)
						index = getNumBodyDecl() - 1;

					for (int i = index; i >= 0; i--) {
						b = getBodyDecl(i);
						if (b instanceof FieldDeclaration) {
							FieldDeclaration f = (FieldDeclaration) b;
							// System.err.println(" working on field " +
							// f.name() + " which is child " + i);
							if (f == v)
								return !f.hasInit();
							if ((v.isClassVariable() && f.isStatic()) || (v.isInstanceVariable() && !f.isStatic()))
								return f.isDUafter(v);
							// System.err.println(" field " + f.name() + " can
							// not affect " + v.name());
						} else if (b instanceof StaticInitializer && v.isClassVariable()) {
							StaticInitializer si = (StaticInitializer) b;
							// System.err.println(" working on static
							// initializer which is child " + i);
							return si.isDUafter(v);
						} else if (b instanceof InstanceInitializer && v.isInstanceVariable()) {
							InstanceInitializer ii = (InstanceInitializer) b;
							// System.err.println(" working on instance
							// initializer which is child " + i);
							return ii.isDUafter(v);
						}
					}
				}
				// System.err.println("Reached TypeDecl when searching for DU
				// for variable");
				return isDUbefore(v);
			}
		} else {
			return getParent().Define_boolean_isDUbefore(this, caller, v);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:16
	 * @apilevel internal
	 */
	public Collection Define_Collection_lookupConstructor(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return constructors();
		} else {
			return getParent().Define_Collection_lookupConstructor(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:20
	 * @apilevel internal
	 */
	public Collection Define_Collection_lookupSuperConstructor(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return lookupSuperConstructor();
		} else {
			return getParent().Define_Collection_lookupSuperConstructor(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:34
	 * @apilevel internal
	 */
	public Collection Define_Collection_lookupMethod(ASTNode caller, ASTNode child, String name) {
		if (caller == getBodyDeclListNoTransform()) {
			int i = caller.getIndexOfChild(child);
			return unqualifiedLookupMethod(name);
		} else {
			return getParent().Define_Collection_lookupMethod(this, caller, name);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:358
	 * @apilevel internal
	 */
	public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			{
				SimpleSet c = memberTypes(name);
				if (!c.isEmpty())
					return c;
				if (name().equals(name))
					return SimpleSet.emptySet.add(this);

				c = lookupType(name);
				// 8.5.2
				if (isClassDecl() && isStatic() && !isTopLevelType()) {
					SimpleSet newSet = SimpleSet.emptySet;
					for (Iterator iter = c.iterator(); iter.hasNext();) {
						TypeDecl d = (TypeDecl) iter.next();
						// if(d.isStatic() || d.isTopLevelType() ||
						// this.instanceOf(d.enclosingType())) {
						newSet = newSet.add(d);
						// }
					}
					c = newSet;
				}
				return c;
			}
		} else {
			return getParent().Define_SimpleSet_lookupType(this, caller, name);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:27
	 * @apilevel internal
	 */
	public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
		if (caller == getBodyDeclListNoTransform()) {
			int i = caller.getIndexOfChild(child);
			{
				SimpleSet list = memberFields(name);
				if (!list.isEmpty())
					return list;
				list = lookupVariable(name);
				if (inStaticContext() || isStatic())
					list = removeInstanceVariables(list);
				return list;
			}
		} else {
			return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:301
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBePublic(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return false;
		} else if (caller == getModifiersNoTransform()) {
			return true;
		} else {
			return getParent().Define_boolean_mayBePublic(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:302
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBeProtected(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return false;
		} else if (caller == getModifiersNoTransform()) {
			return true;
		} else {
			return getParent().Define_boolean_mayBeProtected(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:303
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBePrivate(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return false;
		} else if (caller == getModifiersNoTransform()) {
			return true;
		} else {
			return getParent().Define_boolean_mayBePrivate(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:306
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBeAbstract(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return false;
		} else if (caller == getModifiersNoTransform()) {
			return true;
		} else {
			return getParent().Define_boolean_mayBeAbstract(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:304
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBeStatic(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return false;
		} else if (caller == getModifiersNoTransform()) {
			return true;
		} else {
			return getParent().Define_boolean_mayBeStatic(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:309
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBeStrictfp(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return false;
		} else if (caller == getModifiersNoTransform()) {
			return true;
		} else {
			return getParent().Define_boolean_mayBeStrictfp(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:305
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBeFinal(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return false;
		} else {
			return getParent().Define_boolean_mayBeFinal(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:307
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBeVolatile(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return false;
		} else {
			return getParent().Define_boolean_mayBeVolatile(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:308
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBeTransient(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return false;
		} else {
			return getParent().Define_boolean_mayBeTransient(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:310
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBeSynchronized(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return false;
		} else {
			return getParent().Define_boolean_mayBeSynchronized(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:311
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBeNative(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return false;
		} else {
			return getParent().Define_boolean_mayBeNative(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:297
	 * @apilevel internal
	 */
	public VariableScope Define_VariableScope_outerScope(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return this;
		} else {
			return getParent().Define_VariableScope_outerScope(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:369
	 * @apilevel internal
	 */
	public boolean Define_boolean_insideLoop(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int i = caller.getIndexOfChild(child);
			return false;
		} else {
			return getParent().Define_boolean_insideLoop(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:376
	 * @apilevel internal
	 */
	public boolean Define_boolean_insideSwitch(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int i = caller.getIndexOfChild(child);
			return false;
		} else {
			return getParent().Define_boolean_insideSwitch(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:118
	 * @apilevel internal
	 */
	public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return NameType.EXPRESSION_NAME;
		} else {
			return getParent().Define_NameType_nameType(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:218
	 * @apilevel internal
	 */
	public boolean Define_boolean_isAnonymous(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return false;
		} else {
			return getParent().Define_boolean_isAnonymous(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:494
	 * @apilevel internal
	 */
	public TypeDecl Define_TypeDecl_enclosingType(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return this;
		} else {
			return getParent().Define_TypeDecl_enclosingType(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:520
	 * @apilevel internal
	 */
	public boolean Define_boolean_isNestedType(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return true;
		} else {
			return getParent().Define_boolean_isNestedType(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:542
	 * @apilevel internal
	 */
	public boolean Define_boolean_isLocalClass(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return false;
		} else {
			return getParent().Define_boolean_isLocalClass(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:575
	 * @apilevel internal
	 */
	public TypeDecl Define_TypeDecl_hostType(ASTNode caller, ASTNode child) {
		if (caller == getModifiersNoTransform()) {
			return hostType();
		} else if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return hostType();
		} else {
			int childIndex = this.getIndexOfChild(caller);
			return hostType();
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:404
	 * @apilevel internal
	 */
	public TypeDecl Define_TypeDecl_returnType(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return typeVoid();
		} else {
			return getParent().Define_TypeDecl_returnType(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:509
	 * @apilevel internal
	 */
	public TypeDecl Define_TypeDecl_enclosingInstance(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			{
				if (getBodyDecl(childIndex) instanceof MemberTypeDecl
						&& !((MemberTypeDecl) getBodyDecl(childIndex)).typeDecl().isInnerType())
					return null;
				if (getBodyDecl(childIndex) instanceof ConstructorDecl)
					return enclosingInstance();
				return this;
			}
		} else {
			return getParent().Define_TypeDecl_enclosingInstance(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:12
	 * @apilevel internal
	 */
	public String Define_String_methodHost(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return typeName();
		} else {
			return getParent().Define_String_methodHost(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:138
	 * @apilevel internal
	 */
	public boolean Define_boolean_inStaticContext(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return isStatic() || inStaticContext();
		} else {
			return getParent().Define_boolean_inStaticContext(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:159
	 * @apilevel internal
	 */
	public boolean Define_boolean_reportUnreachable(ASTNode caller, ASTNode child) {
		{
			int childIndex = this.getIndexOfChild(caller);
			return true;
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:74
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayUseAnnotationTarget(ASTNode caller, ASTNode child, String name) {
		if (caller == getModifiersNoTransform()) {
			return name.equals("TYPE");
		} else {
			return getParent().Define_boolean_mayUseAnnotationTarget(this, caller, name);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:271
	 * @apilevel internal
	 */
	public boolean Define_boolean_withinSuppressWarnings(ASTNode caller, ASTNode child, String s) {
		if (caller == getBodyDeclListNoTransform()) {
			int i = caller.getIndexOfChild(child);
			return getBodyDecl(i).hasAnnotationSuppressWarnings(s) || hasAnnotationSuppressWarnings(s)
					|| withinSuppressWarnings(s);
		} else {
			return getParent().Define_boolean_withinSuppressWarnings(this, caller, s);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:374
	 * @apilevel internal
	 */
	public boolean Define_boolean_withinDeprecatedAnnotation(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int i = caller.getIndexOfChild(child);
			return getBodyDecl(i).isDeprecated() || isDeprecated() || withinDeprecatedAnnotation();
		} else {
			return getParent().Define_boolean_withinDeprecatedAnnotation(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:350
	 * @apilevel internal
	 */
	public boolean Define_boolean_enclosedByExceptionHandler(ASTNode caller, ASTNode child) {
		if (caller == getBodyDeclListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return false;
		} else {
			return getParent().Define_boolean_enclosedByExceptionHandler(this, caller);
		}
	}

	/**
	 * @apilevel internal
	 */
	public ASTNode rewriteTo() {
		return super.rewriteTo();
	}
}
