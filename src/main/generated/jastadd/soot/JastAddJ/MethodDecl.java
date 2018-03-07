/* This file was generated with JastAdd2 (http://jastadd.org) version R20130212 (r1031) */
package soot.JastAddJ;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import soot.Scene;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;

/**
 * @production MethodDecl : {@link MemberDecl} ::=
 *             <span class="component">{@link Modifiers}</span>
 *             <span class="component">TypeAccess:{@link Access}</span>
 *             <span class="component">&lt;ID:String&gt;</span> <span class=
 *             "component">Parameter:{@link ParameterDeclaration}*</span>
 *             <span class="component">Exception:{@link Access}*</span>
 *             <span class="component">[{@link Block}]</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:95
 */
public class MethodDecl extends MemberDecl implements Cloneable, SimpleSet, Iterator {
	/**
	 * @apilevel low-level
	 */
	public void flushCache() {
		super.flushCache();
		accessibleFrom_TypeDecl_values = null;
		throwsException_TypeDecl_values = null;
		signature_computed = false;
		signature_value = null;
		moreSpecificThan_MethodDecl_values = null;
		overrides_MethodDecl_values = null;
		hides_MethodDecl_values = null;
		parameterDeclaration_String_values = null;
		type_computed = false;
		type_value = null;
		usesTypeVariable_computed = false;
		sourceMethodDecl_computed = false;
		sourceMethodDecl_value = null;
		sootMethod_computed = false;
		sootMethod_value = null;
		sootRef_computed = false;
		sootRef_value = null;
		offsetBeforeParameters_computed = false;
		offsetAfterParameters_computed = false;
		handlesException_TypeDecl_values = null;
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
	public MethodDecl clone() throws CloneNotSupportedException {
		MethodDecl node = (MethodDecl) super.clone();
		node.accessibleFrom_TypeDecl_values = null;
		node.throwsException_TypeDecl_values = null;
		node.signature_computed = false;
		node.signature_value = null;
		node.moreSpecificThan_MethodDecl_values = null;
		node.overrides_MethodDecl_values = null;
		node.hides_MethodDecl_values = null;
		node.parameterDeclaration_String_values = null;
		node.type_computed = false;
		node.type_value = null;
		node.usesTypeVariable_computed = false;
		node.sourceMethodDecl_computed = false;
		node.sourceMethodDecl_value = null;
		node.sootMethod_computed = false;
		node.sootMethod_value = null;
		node.sootRef_computed = false;
		node.sootRef_value = null;
		node.offsetBeforeParameters_computed = false;
		node.offsetAfterParameters_computed = false;
		node.handlesException_TypeDecl_values = null;
		node.in$Circle(false);
		node.is$Final(false);
		return node;
	}

	/**
	 * @apilevel internal
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public MethodDecl copy() {
		try {
			MethodDecl node = (MethodDecl) clone();
			node.parent = null;
			if (children != null)
				node.children = (ASTNode[]) children.clone();
			return node;
		} catch (CloneNotSupportedException e) {
			throw new Error("Error: clone not supported for " + getClass().getName());
		}
	}

	/**
	 * Create a deep copy of the AST subtree at this node. The copy is dangling,
	 * i.e. has no parent.
	 * 
	 * @return dangling copy of the subtree at this node
	 * @apilevel low-level
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public MethodDecl fullCopy() {
		MethodDecl tree = (MethodDecl) copy();
		if (children != null) {
			for (int i = 0; i < children.length; ++i) {
				ASTNode child = (ASTNode) children[i];
				if (child != null) {
					child = child.fullCopy();
					tree.setChild(child, i);
				}
			}
		}
		return tree;
	}

	/**
	 * @ast method
	 * @aspect BoundNames
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BoundNames.jrag:77
	 */
	public Access createBoundAccess(List args) {
		if (isStatic()) {
			return hostType().createQualifiedAccess().qualifiesAccess(new BoundMethodAccess(name(), args, this));
		}
		return new BoundMethodAccess(name(), args, this);
	}

	/**
	 * @ast method
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:154
	 */
	public SimpleSet add(Object o) {
		return new SimpleSetImpl().add(this).add(o);
	}

	/**
	 * @ast method
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:158
	 */
	public boolean isSingleton() {
		return true;
	}

	/**
	 * @ast method
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:159
	 */
	public boolean isSingleton(Object o) {
		return contains(o);
	}

	/**
	 * @ast method
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:162
	 */

	private MethodDecl iterElem;

	/**
	 * @ast method
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:163
	 */
	public Iterator iterator() {
		iterElem = this;
		return this;
	}

	/**
	 * @ast method
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:164
	 */
	public boolean hasNext() {
		return iterElem != null;
	}

	/**
	 * @ast method
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:165
	 */
	public Object next() {
		Object o = iterElem;
		iterElem = null;
		return o;
	}

	/**
	 * @ast method
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:166
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @ast method
	 * @aspect NameCheck
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:101
	 */
	public void nameCheck() {
		// 8.4
		// 8.4.2
		if (!hostType().methodsSignature(signature()).contains(this))
			error("method with signature " + signature() + " is multiply declared in type " + hostType().typeName());
		// 8.4.3.4
		if (isNative() && hasBlock())
			error("native methods must have an empty semicolon body");
		// 8.4.5
		if (isAbstract() && hasBlock())
			error("abstract methods must have an empty semicolon body");
		// 8.4.5
		if (!hasBlock() && !(isNative() || isAbstract()))
			error("only abstract and native methods may have an empty semicolon body");
	}

	/**
	 * @ast method
	 * @aspect PrettyPrint
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:176
	 */
	public void toString(StringBuffer s) {
		s.append(indent());
		getModifiers().toString(s);
		getTypeAccess().toString(s);
		s.append(" " + name() + "(");
		if (getNumParameter() > 0) {
			getParameter(0).toString(s);
			for (int i = 1; i < getNumParameter(); i++) {
				s.append(", ");
				getParameter(i).toString(s);
			}
		}
		s.append(")");
		if (getNumException() > 0) {
			s.append(" throws ");
			getException(0).toString(s);
			for (int i = 1; i < getNumException(); i++) {
				s.append(", ");
				getException(i).toString(s);
			}
		}
		if (hasBlock()) {
			s.append(" ");
			getBlock().toString(s);
		} else {
			s.append(";");
		}
	}

	/**
	 * @ast method
	 * @aspect TypeCheck
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:386
	 */
	public void typeCheck() {
		// Thrown vs super class method see MethodDecl.nameCheck
		// 8.4.4
		TypeDecl exceptionType = typeThrowable();
		for (int i = 0; i < getNumException(); i++) {
			TypeDecl typeDecl = getException(i).type();
			if (!typeDecl.instanceOf(exceptionType))
				error(signature() + " throws non throwable type " + typeDecl.fullName());
		}

		// check returns
		if (!isVoid() && hasBlock() && getBlock().canCompleteNormally())
			error("the body of a non void method may not complete normally");

	}

	/**
	 * @ast method
	 * @aspect LookupParTypeDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1242
	 */
	public BodyDecl substitutedBodyDecl(Parameterization parTypeDecl) {
		// System.out.println("Begin substituting " + signature() + " in " +
		// hostType().typeName() + " with " + parTypeDecl.typeSignature());
		MethodDecl m = new MethodDeclSubstituted((Modifiers) getModifiers().fullCopy(),
				getTypeAccess().type().substituteReturnType(parTypeDecl), getID(),
				getParameterList().substitute(parTypeDecl), getExceptionList().substitute(parTypeDecl),
				substituteBody(parTypeDecl), this);
		// System.out.println("End substituting " + signature());
		return m;
	}

	/**
	 * @ast method
	 * @aspect LookupParTypeDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1257
	 */
	public Opt substituteBody(Parameterization parTypeDecl) {
		return new Opt();
	}

	/**
	 * @ast method
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:199
	 */
	public MethodDecl createAccessor(TypeDecl methodQualifier) {
		MethodDecl m = (MethodDecl) methodQualifier.getAccessor(this, "method");
		if (m != null)
			return m;

		int accessorIndex = methodQualifier.accessorCounter++;

		List parameterList = new List();
		for (int i = 0; i < getNumParameter(); i++)
			parameterList.add(new ParameterDeclaration(
					// We don't need to create a qualified access to the type
					// here
					// since there can be no ambiguity concerning unqualified
					// type names in an inner/enclosing class
					// Jesper 2012-05-04
					// FALSE! We need to create a qualified access in case the
					// method we are generating an access for is not declared
					// in the methodQualifier type
					getParameter(i).type().createQualifiedAccess(), getParameter(i).name()));
		List exceptionList = new List();
		for (int i = 0; i < getNumException(); i++)
			exceptionList.add((Access) getException(i).fullCopy());

		// add synthetic flag to modifiers
		Modifiers modifiers = new Modifiers(new List());
		if (getModifiers().isStatic())
			modifiers.addModifier(new Modifier("static"));
		modifiers.addModifier(new Modifier("synthetic"));
		modifiers.addModifier(new Modifier("public"));
		// build accessor declaration
		m = new MethodDecl(modifiers, getTypeAccess().type().createQualifiedAccess(),
				name() + "$access$" + accessorIndex, parameterList, exceptionList,
				new Opt(new Block(new List().add(createAccessorStmt()))));
		m = methodQualifier.addMemberMethod(m);
		methodQualifier.addAccessor(this, "method", m);
		return m;
	}

	/**
	 * @ast method
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:247
	 */
	private Stmt createAccessorStmt() {
		List argumentList = new List();
		for (int i = 0; i < getNumParameter(); i++)
			argumentList.add(new VarAccess(getParameter(i).name()));
		Access access = new BoundMethodAccess(name(), argumentList, this);
		if (!isStatic())
			access = new ThisAccess("this").qualifiesAccess(access);
		return isVoid() ? (Stmt) new ExprStmt(access) : new ReturnStmt(new Opt(access));
	}

	/**
	 * @ast method
	 * @aspect InnerClasses
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:257
	 */
	public MethodDecl createSuperAccessor(TypeDecl methodQualifier) {
		MethodDecl m = (MethodDecl) methodQualifier.getAccessor(this, "method_super");
		if (m != null)
			return m;

		int accessorIndex = methodQualifier.accessorCounter++;
		List parameters = new List();
		List args = new List();
		for (int i = 0; i < getNumParameter(); i++) {
			parameters.add(new ParameterDeclaration(getParameter(i).type(), getParameter(i).name()));
			args.add(new VarAccess(getParameter(i).name()));
		}
		Stmt stmt;
		if (type().isVoid())
			stmt = new ExprStmt(new SuperAccess("super").qualifiesAccess(new MethodAccess(name(), args)));
		else
			stmt = new ReturnStmt(new Opt(new SuperAccess("super").qualifiesAccess(new MethodAccess(name(), args))));
		m = new MethodDecl(new Modifiers(new List().add(new Modifier("synthetic"))), type().createQualifiedAccess(),
				name() + "$access$" + accessorIndex, parameters, new List(), new Opt(new Block(new List().add(stmt))));
		m = methodQualifier.addMemberMethod(m);
		methodQualifier.addAccessor(this, "method_super", m);
		return m;
	}

	/**
	 * @ast method
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:210
	 */
	public void jimplify1phase2() {
		String name = name();
		ArrayList parameters = new ArrayList();
		ArrayList paramnames = new ArrayList();
		for (int i = 0; i < getNumParameter(); i++) {
			parameters.add(getParameter(i).type().getSootType());
			paramnames.add(getParameter(i).name());
		}
		soot.Type returnType = type().getSootType();
		int modifiers = sootTypeModifiers();
		ArrayList throwtypes = new ArrayList();
		for (int i = 0; i < getNumException(); i++)
			throwtypes.add(getException(i).type().getSootClassDecl());
		String signature = SootMethod.getSubSignature(name, parameters, returnType);
		if (!hostType().getSootClassDecl().declaresMethod(signature)) {
			SootMethod m = Scene.v().makeSootMethod(name, parameters, returnType, modifiers, throwtypes);
			hostType().getSootClassDecl().addMethod(m);
			m.addTag(new soot.tagkit.ParamNamesTag(paramnames));
			sootMethod = m;
		} else {
			sootMethod = hostType().getSootClassDecl().getMethod(signature);
		}
		addAttributes();
	}

	/**
	 * @ast method
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:268
	 */

	public SootMethod sootMethod;

	/**
	 * @ast method
	 * @aspect AnnotationsCodegen
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:43
	 */
	public void addAttributes() {
		super.addAttributes();
		ArrayList c = new ArrayList();
		getModifiers().addRuntimeVisibleAnnotationsAttribute(c);
		getModifiers().addRuntimeInvisibleAnnotationsAttribute(c);
		addRuntimeVisibleParameterAnnotationsAttribute(c);
		addRuntimeInvisibleParameterAnnotationsAttribute(c);
		addSourceLevelParameterAnnotationsAttribute(c);
		getModifiers().addSourceOnlyAnnotations(c);
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			soot.tagkit.Tag tag = (soot.tagkit.Tag) iter.next();
			sootMethod.addTag(tag);
		}
	}

	/**
	 * @ast method
	 * @aspect AnnotationsCodegen
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:163
	 */
	public void addRuntimeVisibleParameterAnnotationsAttribute(Collection c) {
		boolean foundVisibleAnnotations = false;
		Collection annotations = new ArrayList(getNumParameter());
		for (int i = 0; i < getNumParameter(); i++) {
			Collection a = getParameter(i).getModifiers().runtimeVisibleAnnotations();
			if (!a.isEmpty())
				foundVisibleAnnotations = true;
			soot.tagkit.VisibilityAnnotationTag tag = new soot.tagkit.VisibilityAnnotationTag(
					soot.tagkit.AnnotationConstants.RUNTIME_VISIBLE);
			for (Iterator iter = a.iterator(); iter.hasNext();) {
				Annotation annotation = (Annotation) iter.next();
				ArrayList elements = new ArrayList(1);
				annotation.appendAsAttributeTo(elements);
				tag.addAnnotation((soot.tagkit.AnnotationTag) elements.get(0));
			}
			annotations.add(tag);
		}
		if (foundVisibleAnnotations) {
			soot.tagkit.VisibilityParameterAnnotationTag tag = new soot.tagkit.VisibilityParameterAnnotationTag(
					annotations.size(), soot.tagkit.AnnotationConstants.RUNTIME_VISIBLE);
			for (Iterator iter = annotations.iterator(); iter.hasNext();) {
				tag.addVisibilityAnnotation((soot.tagkit.VisibilityAnnotationTag) iter.next());
			}
			c.add(tag);
		}
	}

	/**
	 * @ast method
	 * @aspect AnnotationsCodegen
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:219
	 */
	public void addRuntimeInvisibleParameterAnnotationsAttribute(Collection c) {
		boolean foundVisibleAnnotations = false;
		Collection annotations = new ArrayList(getNumParameter());
		for (int i = 0; i < getNumParameter(); i++) {
			Collection a = getParameter(i).getModifiers().runtimeInvisibleAnnotations();
			if (!a.isEmpty())
				foundVisibleAnnotations = true;
			soot.tagkit.VisibilityAnnotationTag tag = new soot.tagkit.VisibilityAnnotationTag(
					soot.tagkit.AnnotationConstants.RUNTIME_INVISIBLE);
			for (Iterator iter = a.iterator(); iter.hasNext();) {
				Annotation annotation = (Annotation) iter.next();
				ArrayList elements = new ArrayList(1);
				annotation.appendAsAttributeTo(elements);
				tag.addAnnotation((soot.tagkit.AnnotationTag) elements.get(0));
			}
			annotations.add(tag);
		}
		if (foundVisibleAnnotations) {
			soot.tagkit.VisibilityParameterAnnotationTag tag = new soot.tagkit.VisibilityParameterAnnotationTag(
					annotations.size(), soot.tagkit.AnnotationConstants.RUNTIME_INVISIBLE);
			for (Iterator iter = annotations.iterator(); iter.hasNext();) {
				tag.addVisibilityAnnotation((soot.tagkit.VisibilityAnnotationTag) iter.next());
			}
			c.add(tag);
		}
	}

	/**
	 * @ast method
	 * @aspect AnnotationsCodegen
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:274
	 */
	public void addSourceLevelParameterAnnotationsAttribute(Collection c) {
		boolean foundVisibleAnnotations = false;
		Collection annotations = new ArrayList(getNumParameter());
		for (int i = 0; i < getNumParameter(); i++) {
			getParameter(i).getModifiers().addSourceOnlyAnnotations(c);
		}
	}

	/**
	 * @ast method
	 * @aspect GenericsCodegen
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:342
	 */
	public void transformation() {
		super.transformation();
		HashSet processed = new HashSet();
		for (Iterator iter = hostType().bridgeCandidates(signature()).iterator(); iter.hasNext();) {
			MethodDecl m = (MethodDecl) iter.next();
			if (this.overrides(m)) {
				MethodDecl erased = m.erasedMethod();
				if (!erased.signature().equals(signature()) || erased.type().erasure() != type().erasure()) {
					StringBuffer keyBuffer = new StringBuffer();
					for (int i = 0; i < getNumParameter(); i++) {
						keyBuffer.append(erased.getParameter(i).type().erasure().fullName());
					}
					keyBuffer.append(erased.type().erasure().fullName());
					String key = keyBuffer.toString();
					if (!processed.contains(key)) {
						processed.add(key);

						List args = new List();
						List parameters = new List();
						for (int i = 0; i < getNumParameter(); i++) {
							args.add(new CastExpr(getParameter(i).type().erasure().createBoundAccess(),
									new VarAccess("p" + i)));
							parameters.add(new ParameterDeclaration(erased.getParameter(i).type().erasure(), "p" + i));
						}
						Stmt stmt;
						if (type().isVoid()) {
							stmt = new ExprStmt(createBoundAccess(args));
						} else {
							stmt = new ReturnStmt(createBoundAccess(args));
						}
						List modifiersList = new List();
						if (isPublic())
							modifiersList.add(new Modifier("public"));
						else if (isProtected())
							modifiersList.add(new Modifier("protected"));
						else if (isPrivate())
							modifiersList.add(new Modifier("private"));
						MethodDecl bridge = new BridgeMethodDecl(new Modifiers(modifiersList),
								erased.type().erasure().createBoundAccess(), erased.name(), parameters,
								(List) getExceptionList().fullCopy(), new Opt(new Block(new List().add(stmt))));
						hostType().addBodyDecl(bridge);
					}
				}
			}
		}
	}

	/**
	 * Check if the method is missing a SafeVarargs annotation.
	 * 
	 * @ast method
	 * @aspect SafeVarargs
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/SafeVarargs.jrag:151
	 */
	public void checkWarnings() {
		// check for illegal use of @SafeVarargs
		super.checkWarnings();

		if (!suppressWarnings("unchecked") && !hasAnnotationSafeVarargs() && isVariableArity()
				&& !getParameter(getNumParameter() - 1).type().isReifiable())
			warning("possible heap pollution for " + "variable arity parameter");
	}

	/**
	 * @ast method
	 * 
	 */
	public MethodDecl() {
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
		children = new ASTNode[5];
		setChild(new List(), 2);
		setChild(new List(), 3);
		setChild(new Opt(), 4);
	}

	/**
	 * @ast method
	 * 
	 */
	public MethodDecl(Modifiers p0, Access p1, String p2, List<ParameterDeclaration> p3, List<Access> p4,
			Opt<Block> p5) {
		setChild(p0, 0);
		setChild(p1, 1);
		setID(p2);
		setChild(p3, 2);
		setChild(p4, 3);
		setChild(p5, 4);
	}

	/**
	 * @ast method
	 * 
	 */
	public MethodDecl(Modifiers p0, Access p1, beaver.Symbol p2, List<ParameterDeclaration> p3, List<Access> p4,
			Opt<Block> p5) {
		setChild(p0, 0);
		setChild(p1, 1);
		setID(p2);
		setChild(p3, 2);
		setChild(p4, 3);
		setChild(p5, 4);
	}

	/**
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	protected int numChildren() {
		return 5;
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
	 * Replaces the TypeAccess child.
	 * 
	 * @param node
	 *            The new node to replace the TypeAccess child.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public void setTypeAccess(Access node) {
		setChild(node, 1);
	}

	/**
	 * Retrieves the TypeAccess child.
	 * 
	 * @return The current node used as the TypeAccess child.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public Access getTypeAccess() {
		return (Access) getChild(1);
	}

	/**
	 * Retrieves the TypeAccess child.
	 * <p>
	 * <em>This method does not invoke AST transformations.</em>
	 * </p>
	 * 
	 * @return The current node used as the TypeAccess child.
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	public Access getTypeAccessNoTransform() {
		return (Access) getChildNoTransform(1);
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
	 * Replaces the Parameter list.
	 * 
	 * @param list
	 *            The new list node to be used as the Parameter list.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public void setParameterList(List<ParameterDeclaration> list) {
		setChild(list, 2);
	}

	/**
	 * Retrieves the number of children in the Parameter list.
	 * 
	 * @return Number of children in the Parameter list.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public int getNumParameter() {
		return getParameterList().getNumChild();
	}

	/**
	 * Retrieves the number of children in the Parameter list. Calling this
	 * method will not trigger rewrites..
	 * 
	 * @return Number of children in the Parameter list.
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	public int getNumParameterNoTransform() {
		return getParameterListNoTransform().getNumChildNoTransform();
	}

	/**
	 * Retrieves the element at index {@code i} in the Parameter list..
	 * 
	 * @param i
	 *            Index of the element to return.
	 * @return The element at position {@code i} in the Parameter list.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public ParameterDeclaration getParameter(int i) {
		return (ParameterDeclaration) getParameterList().getChild(i);
	}

	/**
	 * Append an element to the Parameter list.
	 * 
	 * @param node
	 *            The element to append to the Parameter list.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public void addParameter(ParameterDeclaration node) {
		List<ParameterDeclaration> list = (parent == null || state == null) ? getParameterListNoTransform()
				: getParameterList();
		list.addChild(node);
	}

	/**
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	public void addParameterNoTransform(ParameterDeclaration node) {
		List<ParameterDeclaration> list = getParameterListNoTransform();
		list.addChild(node);
	}

	/**
	 * Replaces the Parameter list element at index {@code i} with the new node
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
	public void setParameter(ParameterDeclaration node, int i) {
		List<ParameterDeclaration> list = getParameterList();
		list.setChild(node, i);
	}

	/**
	 * Retrieves the Parameter list.
	 * 
	 * @return The node representing the Parameter list.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public List<ParameterDeclaration> getParameters() {
		return getParameterList();
	}

	/**
	 * Retrieves the Parameter list.
	 * <p>
	 * <em>This method does not invoke AST transformations.</em>
	 * </p>
	 * 
	 * @return The node representing the Parameter list.
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	public List<ParameterDeclaration> getParametersNoTransform() {
		return getParameterListNoTransform();
	}

	/**
	 * Retrieves the Parameter list.
	 * 
	 * @return The node representing the Parameter list.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public List<ParameterDeclaration> getParameterList() {
		List<ParameterDeclaration> list = (List<ParameterDeclaration>) getChild(2);
		list.getNumChild();
		return list;
	}

	/**
	 * Retrieves the Parameter list.
	 * <p>
	 * <em>This method does not invoke AST transformations.</em>
	 * </p>
	 * 
	 * @return The node representing the Parameter list.
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public List<ParameterDeclaration> getParameterListNoTransform() {
		return (List<ParameterDeclaration>) getChildNoTransform(2);
	}

	/**
	 * Replaces the Exception list.
	 * 
	 * @param list
	 *            The new list node to be used as the Exception list.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public void setExceptionList(List<Access> list) {
		setChild(list, 3);
	}

	/**
	 * Retrieves the number of children in the Exception list.
	 * 
	 * @return Number of children in the Exception list.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public int getNumException() {
		return getExceptionList().getNumChild();
	}

	/**
	 * Retrieves the number of children in the Exception list. Calling this
	 * method will not trigger rewrites..
	 * 
	 * @return Number of children in the Exception list.
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	public int getNumExceptionNoTransform() {
		return getExceptionListNoTransform().getNumChildNoTransform();
	}

	/**
	 * Retrieves the element at index {@code i} in the Exception list..
	 * 
	 * @param i
	 *            Index of the element to return.
	 * @return The element at position {@code i} in the Exception list.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public Access getException(int i) {
		return (Access) getExceptionList().getChild(i);
	}

	/**
	 * Append an element to the Exception list.
	 * 
	 * @param node
	 *            The element to append to the Exception list.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public void addException(Access node) {
		List<Access> list = (parent == null || state == null) ? getExceptionListNoTransform() : getExceptionList();
		list.addChild(node);
	}

	/**
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	public void addExceptionNoTransform(Access node) {
		List<Access> list = getExceptionListNoTransform();
		list.addChild(node);
	}

	/**
	 * Replaces the Exception list element at index {@code i} with the new node
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
	public void setException(Access node, int i) {
		List<Access> list = getExceptionList();
		list.setChild(node, i);
	}

	/**
	 * Retrieves the Exception list.
	 * 
	 * @return The node representing the Exception list.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public List<Access> getExceptions() {
		return getExceptionList();
	}

	/**
	 * Retrieves the Exception list.
	 * <p>
	 * <em>This method does not invoke AST transformations.</em>
	 * </p>
	 * 
	 * @return The node representing the Exception list.
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	public List<Access> getExceptionsNoTransform() {
		return getExceptionListNoTransform();
	}

	/**
	 * Retrieves the Exception list.
	 * 
	 * @return The node representing the Exception list.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public List<Access> getExceptionList() {
		List<Access> list = (List<Access>) getChild(3);
		list.getNumChild();
		return list;
	}

	/**
	 * Retrieves the Exception list.
	 * <p>
	 * <em>This method does not invoke AST transformations.</em>
	 * </p>
	 * 
	 * @return The node representing the Exception list.
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public List<Access> getExceptionListNoTransform() {
		return (List<Access>) getChildNoTransform(3);
	}

	/**
	 * Replaces the optional node for the Block child. This is the {@code Opt}
	 * node containing the child Block, not the actual child!
	 * 
	 * @param opt
	 *            The new node to be used as the optional node for the Block
	 *            child.
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	public void setBlockOpt(Opt<Block> opt) {
		setChild(opt, 4);
	}

	/**
	 * Check whether the optional Block child exists.
	 * 
	 * @return {@code true} if the optional Block child exists, {@code false} if
	 *         it does not.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public boolean hasBlock() {
		return getBlockOpt().getNumChild() != 0;
	}

	/**
	 * Retrieves the (optional) Block child.
	 * 
	 * @return The Block child, if it exists. Returns {@code null} otherwise.
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public Block getBlock() {
		return (Block) getBlockOpt().getChild(0);
	}

	/**
	 * Replaces the (optional) Block child.
	 * 
	 * @param node
	 *            The new node to be used as the Block child.
	 * @apilevel high-level
	 * @ast method
	 * 
	 */
	public void setBlock(Block node) {
		getBlockOpt().setChild(node, 0);
	}

	/**
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public Opt<Block> getBlockOpt() {
		return (Opt<Block>) getChild(4);
	}

	/**
	 * Retrieves the optional node for child Block. This is the {@code Opt} node
	 * containing the child Block, not the actual child!
	 * <p>
	 * <em>This method does not invoke AST transformations.</em>
	 * </p>
	 * 
	 * @return The optional node for child Block.
	 * @apilevel low-level
	 * @ast method
	 * 
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public Opt<Block> getBlockOptNoTransform() {
		return (Opt<Block>) getChildNoTransform(4);
	}

	/**
	 * @ast method
	 * @aspect Enums
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:717
	 */

	public void checkModifiers() {
		super.checkModifiers();
		if (hostType().isClassDecl()) {
			// 8.4.3.1
			if (!hostType().isEnumDecl() && isAbstract() && !hostType().isAbstract())
				error("class must be abstract to include abstract methods");
			// 8.4.3.1
			if (isAbstract() && isPrivate())
				error("method may not be abstract and private");
			// 8.4.3.1
			// 8.4.3.2
			if (isAbstract() && isStatic())
				error("method may not be abstract and static");
			if (isAbstract() && isSynchronized())
				error("method may not be abstract and synchronized");
			// 8.4.3.4
			if (isAbstract() && isNative())
				error("method may not be abstract and native");
			if (isAbstract() && isStrictfp())
				error("method may not be abstract and strictfp");
			if (isNative() && isStrictfp())
				error("method may not be native and strictfp");
		}
		if (hostType().isInterfaceDecl()) {
			// 9.4
			if (isStatic())
				error("interface method " + signature() + " in " + hostType().typeName() + " may not be static");
			if (isStrictfp())
				error("interface method " + signature() + " in " + hostType().typeName() + " may not be strictfp");
			if (isNative())
				error("interface method " + signature() + " in " + hostType().typeName() + " may not be native");
			if (isSynchronized())
				error("interface method " + signature() + " in " + hostType().typeName() + " may not be synchronized");
			if (isProtected())
				error("interface method " + signature() + " in " + hostType().typeName() + " may not be protected");
			if (isPrivate())
				error("interface method " + signature() + " in " + hostType().typeName() + " may not be private");
			else if (isFinal())
				error("interface method " + signature() + " in " + hostType().typeName() + " may not be final");
		}
	}

	/**
	 * @ast method
	 * @aspect EmitJimpleRefinements
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/EmitJimpleRefinements.jrag:100
	 */
	public void jimplify2() {
		if (!generate() || sootMethod().hasActiveBody() || (sootMethod().getSource() != null
				&& (sootMethod().getSource() instanceof soot.coffi.CoffiMethodSource)))
			return;
		try {
			if (hasBlock() && !(hostType().isInterfaceDecl())) {
				JimpleBody body = Jimple.v().newBody(sootMethod());
				sootMethod().setActiveBody(body);
				Body b = new Body(hostType(), body, this);
				b.setLine(this);
				for (int i = 0; i < getNumParameter(); i++)
					getParameter(i).jimplify2(b);
				getBlock().jimplify2(b);
				if (type() instanceof VoidType)
					b.add(Jimple.v().newReturnVoidStmt());
			}
		} catch (RuntimeException e) {
			System.err.println("Error generating " + hostType().typeName() + ": " + this);
			throw e;
		}
	}

	/**
	 * @ast method
	 * @aspect MethodDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:148
	 */
	private boolean refined_MethodDecl_MethodDecl_moreSpecificThan_MethodDecl(MethodDecl m) {
		if (getNumParameter() == 0)
			return false;
		for (int i = 0; i < getNumParameter(); i++) {
			if (!getParameter(i).type().instanceOf(m.getParameter(i).type()))
				return false;
		}
		return true;
	}

	/**
	 * @ast method
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:107
	 */
	private int refined_EmitJimple_MethodDecl_sootTypeModifiers() {
		int result = 0;
		if (isPublic())
			result |= soot.Modifier.PUBLIC;
		if (isProtected())
			result |= soot.Modifier.PROTECTED;
		if (isPrivate())
			result |= soot.Modifier.PRIVATE;
		if (isFinal())
			result |= soot.Modifier.FINAL;
		if (isStatic())
			result |= soot.Modifier.STATIC;
		if (isAbstract())
			result |= soot.Modifier.ABSTRACT;
		if (isSynchronized())
			result |= soot.Modifier.SYNCHRONIZED;
		if (isStrictfp())
			result |= soot.Modifier.STRICTFP;
		if (isNative())
			result |= soot.Modifier.NATIVE;
		return result;
	}

	protected java.util.Map accessibleFrom_TypeDecl_values;

	/**
	 * @attribute syn
	 * @aspect AccessControl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AccessControl.jrag:77
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
		if (isPublic()) {
			return true;
		} else if (isProtected()) {
			if (hostPackage().equals(type.hostPackage()))
				return true;
			if (type.withinBodyThatSubclasses(hostType()) != null)
				return true;
			return false;
		} else if (isPrivate())
			return hostType().topLevelType() == type.topLevelType();
		else
			return hostPackage().equals(type.hostPackage());
	}

	/**
	 * @attribute syn
	 * @aspect DataStructures
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:152
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
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:153
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
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:157
	 */
	public boolean contains(Object o) {
		ASTNode$State state = state();
		try {
			return this == o;
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect ErrorCheck
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:22
	 */
	public int lineNumber() {
		ASTNode$State state = state();
		try {
			return getLine(IDstart);
		} finally {
		}
	}

	protected java.util.Map throwsException_TypeDecl_values;

	/**
	 * @attribute syn
	 * @aspect ExceptionHandling
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:146
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean throwsException(TypeDecl exceptionType) {
		Object _parameters = exceptionType;
		if (throwsException_TypeDecl_values == null)
			throwsException_TypeDecl_values = new java.util.HashMap(4);
		if (throwsException_TypeDecl_values.containsKey(_parameters)) {
			return ((Boolean) throwsException_TypeDecl_values.get(_parameters)).booleanValue();
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		boolean throwsException_TypeDecl_value = throwsException_compute(exceptionType);
		if (isFinal && num == state().boundariesCrossed)
			throwsException_TypeDecl_values.put(_parameters, Boolean.valueOf(throwsException_TypeDecl_value));
		return throwsException_TypeDecl_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean throwsException_compute(TypeDecl exceptionType) {
		for (int i = 0; i < getNumException(); i++)
			if (exceptionType.instanceOf(getException(i).type()))
				return true;
		return false;
	}

	/**
	 * @attribute syn
	 * @aspect MethodDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:131
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
	protected boolean signature_computed = false;
	/**
	 * @apilevel internal
	 */
	protected String signature_value;

	/**
	 * @attribute syn
	 * @aspect MethodSignature15
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:347
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public String signature() {
		if (signature_computed) {
			return signature_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		signature_value = signature_compute();
		if (isFinal && num == state().boundariesCrossed)
			signature_computed = true;
		return signature_value;
	}

	/**
	 * @apilevel internal
	 */
	private String signature_compute() {
		StringBuffer s = new StringBuffer();
		s.append(name() + "(");
		for (int i = 0; i < getNumParameter(); i++) {
			if (i != 0)
				s.append(", ");
			s.append(getParameter(i).type().erasure().typeName());
		}
		s.append(")");
		return s.toString();

	}

	/**
	 * @attribute syn
	 * @aspect MethodDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:146
	 */
	public boolean sameSignature(MethodDecl m) {
		ASTNode$State state = state();
		try {
			return signature().equals(m.signature());
		} finally {
		}
	}

	protected java.util.Map moreSpecificThan_MethodDecl_values;

	/**
	 * @attribute syn
	 * @aspect MethodSignature15
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:155
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean moreSpecificThan(MethodDecl m) {
		Object _parameters = m;
		if (moreSpecificThan_MethodDecl_values == null)
			moreSpecificThan_MethodDecl_values = new java.util.HashMap(4);
		if (moreSpecificThan_MethodDecl_values.containsKey(_parameters)) {
			return ((Boolean) moreSpecificThan_MethodDecl_values.get(_parameters)).booleanValue();
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		boolean moreSpecificThan_MethodDecl_value = moreSpecificThan_compute(m);
		if (isFinal && num == state().boundariesCrossed)
			moreSpecificThan_MethodDecl_values.put(_parameters, Boolean.valueOf(moreSpecificThan_MethodDecl_value));
		return moreSpecificThan_MethodDecl_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean moreSpecificThan_compute(MethodDecl m) {
		if (!isVariableArity() && !m.isVariableArity())
			return refined_MethodDecl_MethodDecl_moreSpecificThan_MethodDecl(m);
		int num = Math.max(getNumParameter(), m.getNumParameter());
		for (int i = 0; i < num; i++) {
			TypeDecl t1 = i < getNumParameter() - 1 ? getParameter(i).type()
					: getParameter(getNumParameter() - 1).type().componentType();
			TypeDecl t2 = i < m.getNumParameter() - 1 ? m.getParameter(i).type()
					: m.getParameter(m.getNumParameter() - 1).type().componentType();
			if (!t1.instanceOf(t2))
				return false;
		}
		return true;
	}

	protected java.util.Map overrides_MethodDecl_values;

	/**
	 * @attribute syn
	 * @aspect MethodDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:200
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean overrides(MethodDecl m) {
		Object _parameters = m;
		if (overrides_MethodDecl_values == null)
			overrides_MethodDecl_values = new java.util.HashMap(4);
		if (overrides_MethodDecl_values.containsKey(_parameters)) {
			return ((Boolean) overrides_MethodDecl_values.get(_parameters)).booleanValue();
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		boolean overrides_MethodDecl_value = overrides_compute(m);
		if (isFinal && num == state().boundariesCrossed)
			overrides_MethodDecl_values.put(_parameters, Boolean.valueOf(overrides_MethodDecl_value));
		return overrides_MethodDecl_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean overrides_compute(MethodDecl m) {
		return !isStatic() && !m.isPrivate() && m.accessibleFrom(hostType()) && hostType().instanceOf(m.hostType())
				&& m.signature().equals(signature());
	}

	protected java.util.Map hides_MethodDecl_values;

	/**
	 * @attribute syn
	 * @aspect MethodDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:204
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean hides(MethodDecl m) {
		Object _parameters = m;
		if (hides_MethodDecl_values == null)
			hides_MethodDecl_values = new java.util.HashMap(4);
		if (hides_MethodDecl_values.containsKey(_parameters)) {
			return ((Boolean) hides_MethodDecl_values.get(_parameters)).booleanValue();
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		boolean hides_MethodDecl_value = hides_compute(m);
		if (isFinal && num == state().boundariesCrossed)
			hides_MethodDecl_values.put(_parameters, Boolean.valueOf(hides_MethodDecl_value));
		return hides_MethodDecl_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean hides_compute(MethodDecl m) {
		return isStatic() && !m.isPrivate() && m.accessibleFrom(hostType()) && hostType().instanceOf(m.hostType())
				&& m.signature().equals(signature());
	}

	protected java.util.Map parameterDeclaration_String_values;

	/**
	 * @attribute syn
	 * @aspect VariableScope
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:99
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public SimpleSet parameterDeclaration(String name) {
		Object _parameters = name;
		if (parameterDeclaration_String_values == null)
			parameterDeclaration_String_values = new java.util.HashMap(4);
		if (parameterDeclaration_String_values.containsKey(_parameters)) {
			return (SimpleSet) parameterDeclaration_String_values.get(_parameters);
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		SimpleSet parameterDeclaration_String_value = parameterDeclaration_compute(name);
		if (isFinal && num == state().boundariesCrossed)
			parameterDeclaration_String_values.put(_parameters, parameterDeclaration_String_value);
		return parameterDeclaration_String_value;
	}

	/**
	 * @apilevel internal
	 */
	private SimpleSet parameterDeclaration_compute(String name) {
		for (int i = 0; i < getNumParameter(); i++)
			if (getParameter(i).name().equals(name))
				return (ParameterDeclaration) getParameter(i);
		return SimpleSet.emptySet;
	}

	/**
	 * @attribute syn
	 * @aspect Modifiers
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:214
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
	 * @aspect Modifiers
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:224
	 */
	public boolean isPublic() {
		ASTNode$State state = state();
		try {
			return getModifiers().isPublic() || hostType().isInterfaceDecl();
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect Modifiers
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:225
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
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:226
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
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:227
	 */
	public boolean isAbstract() {
		ASTNode$State state = state();
		try {
			return getModifiers().isAbstract() || hostType().isInterfaceDecl();
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect Modifiers
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:228
	 */
	public boolean isStatic() {
		ASTNode$State state = state();
		try {
			return getModifiers().isStatic();
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect Modifiers
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:230
	 */
	public boolean isFinal() {
		ASTNode$State state = state();
		try {
			return getModifiers().isFinal() || hostType().isFinal() || isPrivate();
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect Modifiers
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:231
	 */
	public boolean isSynchronized() {
		ASTNode$State state = state();
		try {
			return getModifiers().isSynchronized();
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect Modifiers
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:232
	 */
	public boolean isNative() {
		ASTNode$State state = state();
		try {
			return getModifiers().isNative();
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect Modifiers
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:233
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
	 * @apilevel internal
	 */
	protected boolean type_computed = false;
	/**
	 * @apilevel internal
	 */
	protected TypeDecl type_value;

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:269
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl type() {
		if (type_computed) {
			return type_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		type_value = type_compute();
		if (isFinal && num == state().boundariesCrossed)
			type_computed = true;
		return type_value;
	}

	/**
	 * @apilevel internal
	 */
	private TypeDecl type_compute() {
		return getTypeAccess().type();
	}

	/**
	 * @attribute syn
	 * @aspect TypeAnalysis
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:271
	 */
	public boolean isVoid() {
		ASTNode$State state = state();
		try {
			return type().isVoid();
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect TypeHierarchyCheck
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:237
	 */
	public boolean mayOverrideReturn(MethodDecl m) {
		ASTNode$State state = state();
		try {
			return type().instanceOf(m.type());
		} finally {
		}
	}

	/*
	 * It is also a compile-time error if any method declared in an annotation
	 * type has a signature that is override-equivalent to that of any public or
	 * protected method declared in class Object or in the interface
	 * annotation.Annotation* @attribute syn
	 * 
	 * @aspect Annotations
	 * 
	 * @declaredat
	 * /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/
	 * Annotations.jrag:139
	 */
	public boolean annotationMethodOverride() {
		ASTNode$State state = state();
		try {
			return !hostType().ancestorMethods(signature()).isEmpty();
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect Annotations
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:283
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
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:323
	 */
	public boolean isDeprecated() {
		ASTNode$State state = state();
		try {
			return getModifiers().hasDeprecatedAnnotation();
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean usesTypeVariable_computed = false;
	/**
	 * @apilevel internal
	 */
	protected boolean usesTypeVariable_value;

	/**
	 * @attribute syn
	 * @aspect LookupParTypeDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1062
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean usesTypeVariable() {
		if (usesTypeVariable_computed) {
			return usesTypeVariable_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		usesTypeVariable_value = usesTypeVariable_compute();
		if (isFinal && num == state().boundariesCrossed)
			usesTypeVariable_computed = true;
		return usesTypeVariable_value;
	}

	/**
	 * @apilevel internal
	 */
	private boolean usesTypeVariable_compute() {
		return getModifiers().usesTypeVariable() || getTypeAccess().usesTypeVariable()
				|| getParameterList().usesTypeVariable() || getExceptionList().usesTypeVariable();
	}

	/**
	 * @apilevel internal
	 */
	protected boolean sourceMethodDecl_computed = false;
	/**
	 * @apilevel internal
	 */
	protected MethodDecl sourceMethodDecl_value;

	/**
	 * @attribute syn
	 * @aspect SourceDeclarations
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1511
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public MethodDecl sourceMethodDecl() {
		if (sourceMethodDecl_computed) {
			return sourceMethodDecl_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		sourceMethodDecl_value = sourceMethodDecl_compute();
		if (isFinal && num == state().boundariesCrossed)
			sourceMethodDecl_computed = true;
		return sourceMethodDecl_value;
	}

	/**
	 * @apilevel internal
	 */
	private MethodDecl sourceMethodDecl_compute() {
		return this;
	}

	/**
	 * @attribute syn
	 * @aspect GenericsParTypeDecl
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsParTypeDecl.jrag:67
	 */
	public boolean visibleTypeParameters() {
		ASTNode$State state = state();
		try {
			return !isStatic();
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect MethodSignature15
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:284
	 */
	public int arity() {
		ASTNode$State state = state();
		try {
			return getNumParameter();
		} finally {
		}
	}

	/*
	 * The method is then a variable arity method. Otherwise, it is a fixed
	 * arity method.* @attribute syn
	 * 
	 * @aspect VariableArityParameters
	 * 
	 * @declaredat
	 * /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/
	 * VariableArityParameters.jrag:33
	 */
	public boolean isVariableArity() {
		ASTNode$State state = state();
		try {
			return getNumParameter() == 0 ? false : getParameter(getNumParameter() - 1).isVariableArity();
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect VariableArityParameters
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/VariableArityParameters.jrag:38
	 */
	public ParameterDeclaration lastParameter() {
		ASTNode$State state = state();
		try {
			return getParameter(getNumParameter() - 1);
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:107
	 */
	public int sootTypeModifiers() {
		ASTNode$State state = state();
		try {
			int res = refined_EmitJimple_MethodDecl_sootTypeModifiers();
			if (isVariableArity())
				res |= Modifiers.ACC_VARARGS;
			return res;
		} finally {
		}
	}

	/**
	 * @apilevel internal
	 */
	protected boolean sootMethod_computed = false;
	/**
	 * @apilevel internal
	 */
	protected SootMethod sootMethod_value;

	/**
	 * @attribute syn
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:269
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public SootMethod sootMethod() {
		if (sootMethod_computed) {
			return sootMethod_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		sootMethod_value = sootMethod_compute();
		if (isFinal && num == state().boundariesCrossed)
			sootMethod_computed = true;
		return sootMethod_value;
	}

	/**
	 * @apilevel internal
	 */
	private SootMethod sootMethod_compute() {
		ArrayList list = new ArrayList();
		for (int i = 0; i < getNumParameter(); i++)
			list.add(getParameter(i).type().getSootType());
		if (hostType().isArrayDecl())
			return typeObject().getSootClassDecl().getMethod(name(), list, type().getSootType());
		return hostType().getSootClassDecl().getMethod(name(), list, type().getSootType());
	}

	/**
	 * @apilevel internal
	 */
	protected boolean sootRef_computed = false;
	/**
	 * @apilevel internal
	 */
	protected SootMethodRef sootRef_value;

	/**
	 * @attribute syn
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:279
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public SootMethodRef sootRef() {
		if (sootRef_computed) {
			return sootRef_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		sootRef_value = sootRef_compute();
		if (isFinal && num == state().boundariesCrossed)
			sootRef_computed = true;
		return sootRef_value;
	}

	/**
	 * @apilevel internal
	 */
	private SootMethodRef sootRef_compute() {
		ArrayList parameters = new ArrayList();
		for (int i = 0; i < getNumParameter(); i++)
			parameters.add(getParameter(i).type().getSootType());
		SootMethodRef ref = Scene.v().makeMethodRef(hostType().getSootClassDecl(), name(), parameters,
				type().getSootType(), isStatic());
		return ref;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean offsetBeforeParameters_computed = false;
	/**
	 * @apilevel internal
	 */
	protected int offsetBeforeParameters_value;

	/**
	 * @attribute syn
	 * @aspect LocalNum
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/LocalNum.jrag:17
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public int offsetBeforeParameters() {
		if (offsetBeforeParameters_computed) {
			return offsetBeforeParameters_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		offsetBeforeParameters_value = offsetBeforeParameters_compute();
		if (isFinal && num == state().boundariesCrossed)
			offsetBeforeParameters_computed = true;
		return offsetBeforeParameters_value;
	}

	/**
	 * @apilevel internal
	 */
	private int offsetBeforeParameters_compute() {
		return 0;
	}

	/**
	 * @apilevel internal
	 */
	protected boolean offsetAfterParameters_computed = false;
	/**
	 * @apilevel internal
	 */
	protected int offsetAfterParameters_value;

	/**
	 * @attribute syn
	 * @aspect LocalNum
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/LocalNum.jrag:19
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public int offsetAfterParameters() {
		if (offsetAfterParameters_computed) {
			return offsetAfterParameters_value;
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		offsetAfterParameters_value = offsetAfterParameters_compute();
		if (isFinal && num == state().boundariesCrossed)
			offsetAfterParameters_computed = true;
		return offsetAfterParameters_value;
	}

	/**
	 * @apilevel internal
	 */
	private int offsetAfterParameters_compute() {
		if (getNumParameter() == 0)
			return offsetBeforeParameters();
		return getParameter(getNumParameter() - 1).localNum()
				+ getParameter(getNumParameter() - 1).type().variableSize();
	}

	/**
	 * @attribute syn
	 * @aspect GenericsCodegen
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:34
	 */
	public MethodDecl erasedMethod() {
		ASTNode$State state = state();
		try {
			return this;
		} finally {
		}
	}

	/**
	 * @return true if the modifier list includes the SafeVarargs annotation
	 * @attribute syn
	 * @aspect SafeVarargs
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/SafeVarargs.jrag:20
	 */
	public boolean hasAnnotationSafeVarargs() {
		ASTNode$State state = state();
		try {
			return getModifiers().hasAnnotationSafeVarargs();
		} finally {
		}
	}

	/**
	 * It is an error if the SafeVarargs annotation is used on something that is
	 * not a variable arity method or constructor.
	 * 
	 * @attribute syn
	 * @aspect SafeVarargs
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/SafeVarargs.jrag:56
	 */
	public boolean hasIllegalAnnotationSafeVarargs() {
		ASTNode$State state = state();
		try {
			return hasAnnotationSafeVarargs() && (!isVariableArity() || (!isFinal() && !isStatic()));
		} finally {
		}
	}

	/**
	 * @attribute syn
	 * @aspect SuppressWarnings
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/SuppressWarnings.jrag:24
	 */
	public boolean suppressWarnings(String type) {
		ASTNode$State state = state();
		try {
			return hasAnnotationSuppressWarnings(type) || withinSuppressWarnings(type);
		} finally {
		}
	}

	protected java.util.Map handlesException_TypeDecl_values;

	/**
	 * @attribute inh
	 * @aspect ExceptionHandling
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:51
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean handlesException(TypeDecl exceptionType) {
		Object _parameters = exceptionType;
		if (handlesException_TypeDecl_values == null)
			handlesException_TypeDecl_values = new java.util.HashMap(4);
		if (handlesException_TypeDecl_values.containsKey(_parameters)) {
			return ((Boolean) handlesException_TypeDecl_values.get(_parameters)).booleanValue();
		}
		ASTNode$State state = state();
		int num = state.boundariesCrossed;
		boolean isFinal = this.is$Final();
		boolean handlesException_TypeDecl_value = getParent().Define_boolean_handlesException(this, null,
				exceptionType);
		if (isFinal && num == state().boundariesCrossed)
			handlesException_TypeDecl_values.put(_parameters, Boolean.valueOf(handlesException_TypeDecl_value));
		return handlesException_TypeDecl_value;
	}

	/**
	 * @attribute inh
	 * @aspect LookupMethod
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:14
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public MethodDecl unknownMethod() {
		ASTNode$State state = state();
		MethodDecl unknownMethod_value = getParent().Define_MethodDecl_unknownMethod(this, null);
		return unknownMethod_value;
	}

	/**
	 * @attribute inh
	 * @aspect EmitJimple
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:277
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public TypeDecl typeObject() {
		ASTNode$State state = state();
		TypeDecl typeObject_value = getParent().Define_TypeDecl_typeObject(this, null);
		return typeObject_value;
	}

	/**
	 * @attribute inh
	 * @aspect SuppressWarnings
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/SuppressWarnings.jrag:17
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public boolean withinSuppressWarnings(String s) {
		ASTNode$State state = state();
		boolean withinSuppressWarnings_String_value = getParent().Define_boolean_withinSuppressWarnings(this, null, s);
		return withinSuppressWarnings_String_value;
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:437
	 * @apilevel internal
	 */
	public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
		if (caller == getBlockOptNoTransform()) {
			return v.isFinal() && (v.isClassVariable() || v.isInstanceVariable()) ? true : isDAbefore(v);
		} else {
			return getParent().Define_boolean_isDAbefore(this, caller, v);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:868
	 * @apilevel internal
	 */
	public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
		if (caller == getBlockOptNoTransform()) {
			return v.isFinal() && (v.isClassVariable() || v.isInstanceVariable()) ? false : true;
		} else {
			return getParent().Define_boolean_isDUbefore(this, caller, v);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:143
	 * @apilevel internal
	 */
	public boolean Define_boolean_handlesException(ASTNode caller, ASTNode child, TypeDecl exceptionType) {
		if (caller == getBlockOptNoTransform()) {
			return throwsException(exceptionType) || handlesException(exceptionType);
		} else {
			return getParent().Define_boolean_handlesException(this, caller, exceptionType);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:46
	 * @apilevel internal
	 */
	public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
		if (caller == getParameterListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return parameterDeclaration(name);
		} else if (caller == getBlockOptNoTransform()) {
			SimpleSet set = parameterDeclaration(name);
			// A declaration of a method parameter name shadows any other
			// variable declarations
			if (!set.isEmpty())
				return set;
			// Delegate to other declarations in scope
			return lookupVariable(name);
		} else {
			return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:271
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBePublic(ASTNode caller, ASTNode child) {
		if (caller == getModifiersNoTransform()) {
			return true;
		} else {
			return getParent().Define_boolean_mayBePublic(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:272
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBeProtected(ASTNode caller, ASTNode child) {
		if (caller == getModifiersNoTransform()) {
			return true;
		} else {
			return getParent().Define_boolean_mayBeProtected(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:273
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBePrivate(ASTNode caller, ASTNode child) {
		if (caller == getModifiersNoTransform()) {
			return true;
		} else {
			return getParent().Define_boolean_mayBePrivate(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:274
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBeAbstract(ASTNode caller, ASTNode child) {
		if (caller == getModifiersNoTransform()) {
			return true;
		} else {
			return getParent().Define_boolean_mayBeAbstract(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:275
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBeStatic(ASTNode caller, ASTNode child) {
		if (caller == getModifiersNoTransform()) {
			return true;
		} else {
			return getParent().Define_boolean_mayBeStatic(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:276
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBeFinal(ASTNode caller, ASTNode child) {
		if (caller == getModifiersNoTransform()) {
			return true;
		} else {
			return getParent().Define_boolean_mayBeFinal(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:277
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBeSynchronized(ASTNode caller, ASTNode child) {
		if (caller == getModifiersNoTransform()) {
			return true;
		} else {
			return getParent().Define_boolean_mayBeSynchronized(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:278
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBeNative(ASTNode caller, ASTNode child) {
		if (caller == getModifiersNoTransform()) {
			return true;
		} else {
			return getParent().Define_boolean_mayBeNative(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:279
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayBeStrictfp(ASTNode caller, ASTNode child) {
		if (caller == getModifiersNoTransform()) {
			return true;
		} else {
			return getParent().Define_boolean_mayBeStrictfp(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:246
	 * @apilevel internal
	 */
	public ASTNode Define_ASTNode_enclosingBlock(ASTNode caller, ASTNode child) {
		if (caller == getBlockOptNoTransform()) {
			return this;
		} else {
			return getParent().Define_ASTNode_enclosingBlock(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:82
	 * @apilevel internal
	 */
	public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
		if (caller == getExceptionListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return NameType.TYPE_NAME;
		} else if (caller == getParameterListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return NameType.TYPE_NAME;
		} else if (caller == getTypeAccessNoTransform()) {
			return NameType.TYPE_NAME;
		} else {
			return getParent().Define_NameType_nameType(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:405
	 * @apilevel internal
	 */
	public TypeDecl Define_TypeDecl_returnType(ASTNode caller, ASTNode child) {
		if (caller == getBlockOptNoTransform()) {
			return type();
		} else {
			return getParent().Define_TypeDecl_returnType(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:142
	 * @apilevel internal
	 */
	public boolean Define_boolean_inStaticContext(ASTNode caller, ASTNode child) {
		if (caller == getBlockOptNoTransform()) {
			return isStatic();
		} else {
			return getParent().Define_boolean_inStaticContext(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:33
	 * @apilevel internal
	 */
	public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
		if (caller == getBlockOptNoTransform()) {
			return true;
		} else {
			return getParent().Define_boolean_reachable(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:61
	 * @apilevel internal
	 */
	public boolean Define_boolean_isMethodParameter(ASTNode caller, ASTNode child) {
		if (caller == getParameterListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return true;
		} else {
			return getParent().Define_boolean_isMethodParameter(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:62
	 * @apilevel internal
	 */
	public boolean Define_boolean_isConstructorParameter(ASTNode caller, ASTNode child) {
		if (caller == getParameterListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return false;
		} else {
			return getParent().Define_boolean_isConstructorParameter(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:63
	 * @apilevel internal
	 */
	public boolean Define_boolean_isExceptionHandlerParameter(ASTNode caller, ASTNode child) {
		if (caller == getParameterListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return false;
		} else {
			return getParent().Define_boolean_isExceptionHandlerParameter(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:86
	 * @apilevel internal
	 */
	public boolean Define_boolean_mayUseAnnotationTarget(ASTNode caller, ASTNode child, String name) {
		if (caller == getModifiersNoTransform()) {
			return name.equals("METHOD");
		} else {
			return getParent().Define_boolean_mayUseAnnotationTarget(this, caller, name);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/VariableArityParameters.jrag:22
	 * @apilevel internal
	 */
	public boolean Define_boolean_variableArityValid(ASTNode caller, ASTNode child) {
		if (caller == getParameterListNoTransform()) {
			int i = caller.getIndexOfChild(child);
			return i == getNumParameter() - 1;
		} else {
			return getParent().Define_boolean_variableArityValid(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/LocalNum.jrag:26
	 * @apilevel internal
	 */
	public int Define_int_localNum(ASTNode caller, ASTNode child) {
		if (caller == getParameterListNoTransform()) {
			int index = caller.getIndexOfChild(child);
			{
				if (index == 0)
					return offsetBeforeParameters();
				return getParameter(index - 1).localNum() + getParameter(index - 1).type().variableSize();
			}
		} else {
			return getParent().Define_int_localNum(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:351
	 * @apilevel internal
	 */
	public boolean Define_boolean_enclosedByExceptionHandler(ASTNode caller, ASTNode child) {
		if (caller == getBlockOptNoTransform()) {
			return getNumException() != 0;
		} else {
			return getParent().Define_boolean_enclosedByExceptionHandler(this, caller);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/PreciseRethrow.jrag:50
	 * @apilevel internal
	 */
	public boolean Define_boolean_inhModifiedInScope(ASTNode caller, ASTNode child, Variable var) {
		if (caller == getParameterListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return getBlock().modifiedInScope(var);
		} else {
			return getParent().Define_boolean_inhModifiedInScope(this, caller, var);
		}
	}

	/**
	 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/PreciseRethrow.jrag:124
	 * @apilevel internal
	 */
	public boolean Define_boolean_isCatchParam(ASTNode caller, ASTNode child) {
		if (caller == getParameterListNoTransform()) {
			int childIndex = caller.getIndexOfChild(child);
			return false;
		} else {
			return getParent().Define_boolean_isCatchParam(this, caller);
		}
	}

	/**
	 * @apilevel internal
	 */
	public ASTNode rewriteTo() {
		return super.rewriteTo();
	}
}
