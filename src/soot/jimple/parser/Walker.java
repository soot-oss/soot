/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrice Pominville
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.jimple.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.G;
import soot.Immediate;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.Modifier;
import soot.NullType;
import soot.RefType;
import soot.Scene;
import soot.ShortType;
import soot.SootClass;
import soot.SootField;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.SootResolver;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.UnitBox;
import soot.UnknownType;
import soot.Value;
import soot.VoidType;
import soot.jimple.BinopExpr;
import soot.jimple.ClassConstant;
import soot.jimple.DoubleConstant;
import soot.jimple.Expr;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.UnopExpr;
import soot.jimple.parser.analysis.DepthFirstAdapter;
import soot.jimple.parser.node.*;
import soot.util.StringTools;

/*Modified By Marc Berndl 17th May */

public class Walker extends DepthFirstAdapter {
	boolean debug = false;
	LinkedList mProductions = new LinkedList();
	SootClass mSootClass = null;
	Map<String, Local> mLocals = null;
	Value mValue = IntConstant.v(1);

	Map<Object, Unit> mLabelToStmtMap; // maps a label to the stmt following it
										// in the jimple source
	Map<String, List> mLabelToPatchList; // maps a label to the a list of stmts
											// that refer to the label (ie goto
											// lableX)

	protected final SootResolver mResolver;

	public Walker(SootResolver resolver) {
		mResolver = resolver;
		if (debug) {
			mProductions = new LinkedList() {
				public Object removeLast() {
					Object o = super.removeLast();
					if (debug)
						G.v().out.println("popped: " + o);
					return o;
				}
			};
		}
	}

	public Walker(SootClass sc, SootResolver resolver) {
		mSootClass = sc;
		mResolver = resolver;
	}

	public void outStart(Start node) {
		SootClass c = (SootClass) mProductions.removeLast();
	}

	public SootClass getSootClass() {
		if (mSootClass == null)
			throw new RuntimeException("did not parse class yet....");

		return mSootClass;
	}

	/*
	 * file = modifier* file_type class_name extends_clause? implements_clause?
	 * file_body;
	 */
	public void inAFile(AFile node) {
		if (debug)
			G.v().out.println("reading class " + node.getClassName());
	}

	public void caseAFile(AFile node) {
		inAFile(node);
		{
			Object temp[] = node.getModifier().toArray();
			for (Object element : temp) {
				((PModifier) element).apply(this);
			}
		}
		if (node.getFileType() != null) {
			node.getFileType().apply(this);
		}
		if (node.getClassName() != null) {
			node.getClassName().apply(this);
		}

		String className = (String) mProductions.removeLast();

		if (mSootClass == null) {
			mSootClass = new SootClass(className);
			mSootClass.setResolvingLevel(SootClass.BODIES);
		} else {
			if (!mSootClass.getName().equals(className))
				throw new RuntimeException(
						"Invalid SootClass for this JimpleAST. The SootClass provided is of type: >"
								+ mSootClass.getName()
								+ "< whereas this parse tree is for type: >"
								+ className + "<");
		}

		if (node.getExtendsClause() != null) {
			node.getExtendsClause().apply(this);
		}
		if (node.getImplementsClause() != null) {
			node.getImplementsClause().apply(this);
		}
		if (node.getFileBody() != null) {
			node.getFileBody().apply(this);
		}
		outAFile(node);
	}

	public void outAFile(AFile node) {
		// not not pop members; they have been taken care of.
		List implementsList = null;
		String superClass = null;

		String classType = null;

		if (node.getImplementsClause() != null) {
			implementsList = (List) mProductions.removeLast();
		}
		if (node.getExtendsClause() != null) {
			superClass = (String) mProductions.removeLast();
		}

		classType = (String) mProductions.removeLast();

		int modifierCount = node.getModifier().size();

		int modifierFlags = processModifiers(node.getModifier());

		if (classType.equals("interface"))
			modifierFlags |= Modifier.INTERFACE;

		mSootClass.setModifiers(modifierFlags);

		if (superClass != null) {
			mSootClass.setSuperclass(mResolver.makeClassRef(superClass));
		}

		if (implementsList != null) {
			Iterator implIt = implementsList.iterator();
			while (implIt.hasNext()) {
				SootClass interfaceClass = mResolver
						.makeClassRef((String) implIt.next());
				mSootClass.addInterface(interfaceClass);
			}
		}

		mProductions.addLast(mSootClass);
	}

	/*
	 * member = {field} modifier* type name semicolon | {method} modifier* type
	 * name l_paren parameter_list? r_paren throws_clause? method_body;
	 */
	public void outAFieldMember(AFieldMember node) {
		int modifier = 0;
		Type type = null;
		String name = null;

		name = (String) mProductions.removeLast();
		type = (Type) mProductions.removeLast();

		modifier = processModifiers(node.getModifier());

		SootField f = new SootField(name, type, modifier);
		mSootClass.addField(f);
	}

	public void outAMethodMember(AMethodMember node) {
		int modifier = 0;
		Type type;
		String name;
		List parameterList = null;
		List<SootClass> throwsClause = null;
		JimpleBody methodBody = null;

		if (node.getMethodBody() instanceof AFullMethodBody)
			methodBody = (JimpleBody) mProductions.removeLast();

		if (node.getThrowsClause() != null)
			throwsClause = (List<SootClass>) mProductions.removeLast();

		if (node.getParameterList() != null) {
			parameterList = (List) mProductions.removeLast();
		} else {
			parameterList = new ArrayList();
		}

		Object o = mProductions.removeLast();

		name = (String) o;
		type = (Type) mProductions.removeLast();
		modifier = processModifiers(node.getModifier());

		SootMethod method;

		if (throwsClause != null)
			method = new SootMethod(name, parameterList, type, modifier,
					throwsClause);
		else
			method = new SootMethod(name, parameterList, type, modifier);

		mSootClass.addMethod(method);

		if (method.isConcrete()) {
			methodBody.setMethod(method);
			method.setActiveBody(methodBody);

		} else if (node.getMethodBody() instanceof AFullMethodBody)
			throw new RuntimeException("Impossible: !concrete => ! instanceof");

	}

	/*
	 * type = {void} void | {novoid} nonvoid_type;
	 */

	public void outAVoidType(AVoidType node) {
		mProductions.addLast(VoidType.v());
	}

	/*
	 * nonvoid_type = {base} base_type_no_name array_brackets*; {quoted}
	 * quoted_name array_brackets* | {ident} identifier array_brackets* |
	 * {full_ident} full_identifier array_brackets*;
	 */
	public void outABaseNonvoidType(ABaseNonvoidType node) {
		Type t = (Type) mProductions.removeLast();
		int dim = node.getArrayBrackets().size();
		if (dim > 0)
			t = ArrayType.v(t, dim);
		mProductions.addLast(t);
	}

	public void outAQuotedNonvoidType(AQuotedNonvoidType node) {
		String typeName = (String) mProductions.removeLast();
		Type t = RefType.v(typeName);

		int dim = node.getArrayBrackets().size();
		if (dim > 0)
			t = ArrayType.v(t, dim);
		mProductions.addLast(t);
	}

	public void outAIdentNonvoidType(AIdentNonvoidType node) {
		String typeName = (String) mProductions.removeLast();
		Type t = RefType.v(typeName);
		int dim = node.getArrayBrackets().size();
		if (dim > 0)
			t = ArrayType.v(t, dim);
		mProductions.addLast(t);
	}

	public void outAFullIdentNonvoidType(AFullIdentNonvoidType node) {
		String typeName = (String) mProductions.removeLast();
		Type t = RefType.v(typeName);
		int dim = node.getArrayBrackets().size();
		if (dim > 0)
			t = ArrayType.v(t, dim);
		mProductions.addLast(t);
	}

	/*
	 * base_type_no_name = {boolean} boolean | {byte} byte | {char} char |
	 * {short} short | {int} int | {long} long | {float} float | {double} double
	 * | {null} null_type;
	 */

	public void outABooleanBaseTypeNoName(ABooleanBaseTypeNoName node) {
		mProductions.addLast(BooleanType.v());
	}

	public void outAByteBaseTypeNoName(AByteBaseTypeNoName node) {
		mProductions.addLast(ByteType.v());
	}

	public void outACharBaseTypeNoName(ACharBaseTypeNoName node) {
		mProductions.addLast(CharType.v());
	}

	public void outAShortBaseTypeNoName(AShortBaseTypeNoName node) {
		mProductions.addLast(ShortType.v());
	}

	public void outAIntBaseTypeNoName(AIntBaseTypeNoName node) {
		mProductions.addLast(IntType.v());
	}

	public void outALongBaseTypeNoName(ALongBaseTypeNoName node) {
		mProductions.addLast(LongType.v());
	}

	public void outAFloatBaseTypeNoName(AFloatBaseTypeNoName node) {
		mProductions.addLast(FloatType.v());
	}

	public void outADoubleBaseTypeNoName(ADoubleBaseTypeNoName node) {
		mProductions.addLast(DoubleType.v());
	}

	public void outANullBaseTypeNoName(ANullBaseTypeNoName node) {
		mProductions.addLast(NullType.v());
	}

	/*
	 * base_type = {boolean} boolean | {byte} byte | {char} char | {short} short
	 * | {int} int | {long} long | {float} float | {double} double | {null}
	 * null_type | {class_name} class_name;
	 */

	public void outABooleanBaseType(ABooleanBaseType node) {
		mProductions.addLast(BooleanType.v());
	}

	public void outAByteBaseType(AByteBaseType node) {
		mProductions.addLast(ByteType.v());
	}

	public void outACharBaseType(ACharBaseType node) {
		mProductions.addLast(CharType.v());
	}

	public void outAShortBaseType(AShortBaseType node) {
		mProductions.addLast(ShortType.v());
	}

	public void outAIntBaseType(AIntBaseType node) {
		mProductions.addLast(IntType.v());
	}

	public void outALongBaseType(ALongBaseType node) {
		mProductions.addLast(LongType.v());
	}

	public void outAFloatBaseType(AFloatBaseType node) {
		mProductions.addLast(FloatType.v());
	}

	public void outADoubleBaseType(ADoubleBaseType node) {
		mProductions.addLast(DoubleType.v());
	}

	public void outANullBaseType(ANullBaseType node) {
		mProductions.addLast(NullType.v());
	}

	public void outAClassNameBaseType(AClassNameBaseType node) {
		String type = (String) mProductions.removeLast();
		if (type.equals("int"))
			throw new RuntimeException();
		mProductions.addLast(RefType.v(type));
	}

	/*
	 * method_body = {empty} semicolon | {full} l_brace declaration* statement*
	 * catch_clause* r_brace;
	 */

	public void inAFullMethodBody(AFullMethodBody node) {
		mLocals = new HashMap<String, Local>();
		mLabelToStmtMap = new HashMap<Object, Unit>();
		mLabelToPatchList = new HashMap<String, List>();
	}

	public void outAFullMethodBody(AFullMethodBody node) {
		JimpleBody jBody = Jimple.v().newBody();

		if (node.getCatchClause() != null) {
			int size = node.getCatchClause().size();
			for (int i = 0; i < size; i++)
				jBody.getTraps().addFirst((Trap) mProductions.removeLast());
		}

		if (node.getStatement() != null) {
			int size = node.getStatement().size();
			Unit lastStmt = null;
			for (int i = 0; i < size; i++) {
				Object o = mProductions.removeLast();
				if (o instanceof Unit) {
					jBody.getUnits().addFirst((Unit) o);
					lastStmt = (Unit) o;
				} else if (o instanceof String) {
					if (lastStmt == null)
						throw new RuntimeException("impossible");
					mLabelToStmtMap.put(o, lastStmt);
				} else
					throw new RuntimeException("impossible");
			}
		}

		if (node.getDeclaration() != null) {
			int size = node.getDeclaration().size();
			for (int i = 0; i < size; i++) {
				List<Local> localList = (List<Local>) mProductions.removeLast();

				jBody.getLocals().addAll(localList);
			}
		}

		Iterator<String> it = mLabelToPatchList.keySet().iterator();
		while (it.hasNext()) {
			String label = it.next();
			Unit target = mLabelToStmtMap.get(label);

			Iterator patchIt = mLabelToPatchList.get(label).iterator();
			while (patchIt.hasNext()) {
				UnitBox box = (UnitBox) patchIt.next();
				box.setUnit(target);
			}
		}

		/*
		 * Iterator it = mLabelToStmtMap.keySet().iterator();
		 * while(it.hasNext()) { String label = (String) it.next(); Unit target
		 * = (Unit) mLabelToStmtMap.get(label);
		 * 
		 * List l = (List) mLabelToPatchList.get(label); if(l != null) {
		 * Iterator patchIt = l.iterator(); while(patchIt.hasNext()) { UnitBox
		 * box = (UnitBox) patchIt.next(); box.setUnit(target); } } }
		 */

		mProductions.addLast(jBody);
	}

	public void outANovoidType(ANovoidType node) {
	}

	/*
	 * parameter_list = {single} parameter | {multi} parameter comma
	 * parameter_list;
	 */

	public void outASingleParameterList(ASingleParameterList node) {
		List<Type> l = new ArrayList<Type>();
		l.add((Type) mProductions.removeLast());
		mProductions.addLast(l);
	}

	public void outAMultiParameterList(AMultiParameterList node) {
		List<Type> l = (List<Type>) mProductions.removeLast();
		l.add(0, (Type) mProductions.removeLast());
		mProductions.addLast(l);
	}

	/*
	 * arg_list = {single} immediate | {multi} immediate comma arg_list;
	 */
	public void outASingleArgList(ASingleArgList node) {
		List<Value> l = new ArrayList<Value>();

		l.add((Value) mProductions.removeLast());
		mProductions.addLast(l);
	}

	public void outAMultiArgList(AMultiArgList node) {
		List<Value> l = (List<Value>) mProductions.removeLast();
		l.add(0, (Value) mProductions.removeLast());
		mProductions.addLast(l);
	}

	/*
	 * class_name_list = {class_name_single} class_name | {class_name_multi}
	 * class_name comma class_name_list;
	 */

	public void outAClassNameSingleClassNameList(
			AClassNameSingleClassNameList node) {
		List<String> l = new ArrayList<String>();
		l.add((String) mProductions.removeLast());
		mProductions.addLast(l);
	}

	public void outAClassNameMultiClassNameList(
			AClassNameMultiClassNameList node) {
		List<String> l = (List<String>) mProductions.removeLast();
		l.add(0, (String) mProductions.removeLast());
		mProductions.addLast(l);
	}

	/*
	 * file_type = {class} [theclass]:class | {interface} interface;
	 */

	public void outAClassFileType(AClassFileType node) {
		mProductions.addLast("class");
	}

	public void outAInterfaceFileType(AInterfaceFileType node) {
		mProductions.addLast("interface");
	}

	/*
	 * catch_clause = catch [name]:class_name from [from_label]:label_name to
	 * [to_label]:label_name with [with_label]:label_name semicolon;
	 */

	// public void caseACatchClause(ACatchClause node){}

	public void outACatchClause(ACatchClause node) {
		String exceptionName;
		UnitBox withUnit, fromUnit, toUnit;

		withUnit = Jimple.v().newStmtBox(null);
		addBoxToPatch((String) mProductions.removeLast(), withUnit);

		toUnit = Jimple.v().newStmtBox(null);
		addBoxToPatch((String) mProductions.removeLast(), toUnit);

		fromUnit = Jimple.v().newStmtBox(null);
		addBoxToPatch((String) mProductions.removeLast(), fromUnit);

		exceptionName = (String) mProductions.removeLast();

		Trap trap = Jimple.v().newTrap(mResolver.makeClassRef(exceptionName),
				fromUnit, toUnit, withUnit);
		mProductions.addLast(trap);
	}

	/*
	 * declaration = jimple_type local_name_list semicolon;
	 */

	public void outADeclaration(ADeclaration node) {
		List localNameList = (List) mProductions.removeLast();
		Type type = (Type) mProductions.removeLast();
		Iterator it = localNameList.iterator();
		List<Local> localList = new ArrayList<Local>();

		while (it.hasNext()) {
			Local l = Jimple.v().newLocal((String) it.next(), type);
			mLocals.put(l.getName(), l);
			localList.add(l);
		}
		mProductions.addLast(localList);
	}

	/*
	 * jimple_type = {unknown} unknown | {nonvoid} nonvoid_type;
	 */
	public void outAUnknownJimpleType(AUnknownJimpleType node) {
		mProductions.addLast(UnknownType.v());
	}

	/*
	 * local_name_list = {single} local_name | {multi} local_name comma
	 * local_name_list;
	 */

	public void outASingleLocalNameList(ASingleLocalNameList node) {
		List<String> l = new ArrayList<String>();
		l.add((String) mProductions.removeLast());
		mProductions.addLast(l);
	}

	public void outAMultiLocalNameList(AMultiLocalNameList node) {
		List<String> l = (List<String>) mProductions.removeLast();
		l.add(0, (String) mProductions.removeLast());
		mProductions.addLast(l);
	}

	/*
	 * statement = {label} label_name colon | {breakpoint} breakpoint semicolon
	 * | {entermonitor} entermonitor immediate semicolon | {exitmonitor}
	 * exitmonitor immediate semicolon | {switch} switch l_paren immediate
	 * r_paren l_brace case_stmt+ r_brace semicolon | {identity} local_name
	 * colon_equals at_identifier type semicolon | {identity_no_type} local_name
	 * colon_equals at_identifier semicolon | {assign} variable equals
	 * expression semicolon | {if} if bool_expr goto_stmt | {goto} goto_stmt |
	 * {nop} nop semicolon | {ret} ret immediate? semicolon | {return} return
	 * immediate? semicolon | {throw} throw immediate semicolon | {invoke}
	 * invoke_expr semicolon;
	 */

	public void outALabelStatement(ALabelStatement node) {
	}

	public void outABreakpointStatement(ABreakpointStatement node) {
		Unit u = Jimple.v().newBreakpointStmt();
		mProductions.addLast(u);
	}

	public void outAEntermonitorStatement(AEntermonitorStatement node) {
		Value op = (Value) mProductions.removeLast();

		Unit u = Jimple.v().newEnterMonitorStmt(op);
		mProductions.addLast(u);
	}

	public void outAExitmonitorStatement(AExitmonitorStatement node) {
		Value op = (Value) mProductions.removeLast();

		Unit u = Jimple.v().newExitMonitorStmt(op);
		mProductions.addLast(u);
	}

	/*
	 * case_label = {constant} case minus? integer_constant | {default} default;
	 */
	/*
	 * case_stmt = case_label colon goto_stmt;
	 */
	public void outACaseStmt(ACaseStmt node) {
		String labelName = (String) mProductions.removeLast();
		UnitBox box = Jimple.v().newStmtBox(null);

		addBoxToPatch(labelName, box);

		Value labelValue = null;
		if (node.getCaseLabel() instanceof AConstantCaseLabel)
			labelValue = (Value) mProductions.removeLast();

		// if labelValue == null, this is the default label.
		if (labelValue == null)
			mProductions.addLast(box);
		else {
			Object[] valueTargetPair = new Object[2];
			valueTargetPair[0] = labelValue;
			valueTargetPair[1] = box;
			mProductions.addLast(valueTargetPair);
		}
	}

	public void outATableswitchStatement(ATableswitchStatement node) {
		List<UnitBox> targets = new ArrayList<UnitBox>();
		UnitBox defaultTarget = null;

		int lowIndex = 0, highIndex = 0;

		if (node.getCaseStmt() != null) {
			int size = node.getCaseStmt().size();

			for (int i = 0; i < size; i++) {
				Object valueTargetPair = mProductions.removeLast();
				if (valueTargetPair instanceof UnitBox) {
					if (defaultTarget != null)
						throw new RuntimeException(
								"error: can't ;have more than 1 default stmt");

					defaultTarget = (UnitBox) valueTargetPair;
				} else {
					Object[] pair = (Object[]) valueTargetPair;

					if ((i == 0 && defaultTarget == null)
							|| (i == 1 && defaultTarget != null))
						highIndex = ((IntConstant) pair[0]).value;
					if (i == (size - 1))
						lowIndex = ((IntConstant) pair[0]).value;

					targets.add(0, (UnitBox) pair[1]);
				}
			}
		} else {
			throw new RuntimeException("error: switch stmt has no case stmts");
		}

		Value key = (Value) mProductions.removeLast();
		Unit switchStmt = Jimple.v().newTableSwitchStmt(key, lowIndex,
				highIndex, targets, defaultTarget);

		mProductions.addLast(switchStmt);
	}

	public void outALookupswitchStatement(ALookupswitchStatement node) {
		List<IntConstant> lookupValues = new ArrayList<IntConstant>();
		List<UnitBox> targets = new ArrayList<UnitBox>();
		UnitBox defaultTarget = null;

		if (node.getCaseStmt() != null) {
			int size = node.getCaseStmt().size();

			for (int i = 0; i < size; i++) {
				Object valueTargetPair = mProductions.removeLast();
				if (valueTargetPair instanceof UnitBox) {
					if (defaultTarget != null)
						throw new RuntimeException(
								"error: can't ;have more than 1 default stmt");

					defaultTarget = (UnitBox) valueTargetPair;
				} else {
					Object[] pair = (Object[]) valueTargetPair;

					lookupValues.add(0, (IntConstant) pair[0]);
					targets.add(0, (UnitBox) pair[1]);
				}
			}
		} else {
			throw new RuntimeException("error: switch stmt has no case stmts");
		}

		Value key = (Value) mProductions.removeLast();
		Unit switchStmt = Jimple.v().newLookupSwitchStmt(key, lookupValues,
				targets, defaultTarget);

		mProductions.addLast(switchStmt);
	}

	public void outAIdentityStatement(AIdentityStatement node) {
		Type identityRefType = (Type) mProductions.removeLast();
		String atClause = (String) mProductions.removeLast();
		Value local = mLocals.get(mProductions.removeLast()); // the local ref
																// from it's
																// identifier

		Value ref = null;
		if (atClause.startsWith("@this")) {
			ref = Jimple.v().newThisRef((RefType) identityRefType);
		} else if (atClause.startsWith("@parameter")) {
			int index = Integer.parseInt(atClause.substring(10,
					atClause.length() - 1));

			ref = Jimple.v().newParameterRef(identityRefType, index);
		} else
			throw new RuntimeException(
					"shouldn't @caughtexception be handled by outAIdentityNoTypeStatement: got"
							+ atClause);

		Unit u = Jimple.v().newIdentityStmt(local, ref);
		mProductions.addLast(u);
	}

	public void outAIdentityNoTypeStatement(AIdentityNoTypeStatement node) {
		mProductions.removeLast(); // get rid of @caughtexception string
									// presently on top of the stack
		Value local = mLocals.get(mProductions.removeLast()); // the local ref
																// from it's
																// identifier

		Unit u = Jimple.v().newIdentityStmt(local,
				Jimple.v().newCaughtExceptionRef());
		mProductions.addLast(u);
	}

	public void outAAssignStatement(AAssignStatement node) {
		Object removeLast = mProductions.removeLast();
		Value rvalue = (Value) removeLast;
		Value variable = (Value) mProductions.removeLast();

		Unit u = Jimple.v().newAssignStmt(variable, rvalue);
		mProductions.addLast(u);
	}

	public void outAIfStatement(AIfStatement node) {
		String targetLabel = (String) mProductions.removeLast();
		Value condition = (Value) mProductions.removeLast();

		UnitBox box = Jimple.v().newStmtBox(null);
		Unit u = Jimple.v().newIfStmt(condition, box);

		addBoxToPatch(targetLabel, box);

		mProductions.addLast(u);
	}

	public void outAReturnStatement(AReturnStatement node) {
		Immediate v;
		Stmt s = null;
		if (node.getImmediate() != null) {
			v = (Immediate) mProductions.removeLast();
			s = Jimple.v().newReturnStmt(v);
		} else {
			s = Jimple.v().newReturnVoidStmt();
		}

		mProductions.addLast(s);
	}

	public void outAGotoStatement(AGotoStatement node) {
		String targetLabel = (String) mProductions.removeLast();

		UnitBox box = Jimple.v().newStmtBox(null);
		Unit branch = Jimple.v().newGotoStmt(box);

		addBoxToPatch(targetLabel, box);

		mProductions.addLast(branch);
	}

	public void outANopStatement(ANopStatement node) {
		Unit u = Jimple.v().newNopStmt();
		mProductions.addLast(u);
	}

	public void outARetStatement(ARetStatement node) {
		throw new RuntimeException("ret not yet implemented.");
	}

	public void outAThrowStatement(AThrowStatement node) {
		Value op = (Value) mProductions.removeLast();

		Unit u = Jimple.v().newThrowStmt(op);
		mProductions.addLast(u);
	}

	public void outAInvokeStatement(AInvokeStatement node) {
		Value op = (Value) mProductions.removeLast();

		Unit u = Jimple.v().newInvokeStmt(op);

		mProductions.addLast(u);
	}

	/*
	 * case_label = {constant} case minus? integer_constant | {default} default;
	 */
	public void outAConstantCaseLabel(AConstantCaseLabel node) {
		String s = (String) mProductions.removeLast();
		int sign = 1;
		if (node.getMinus() != null)
			sign = -1;

		if (s.endsWith("L")) {
			mProductions.addLast(LongConstant.v(sign
					* Long.parseLong(s.substring(0, s.length() - 1))));
		} else if (s.equals("2147483648"))
			mProductions.addLast(IntConstant.v(sign * Integer.MIN_VALUE));
		else
			mProductions.addLast(IntConstant.v(sign * Integer.parseInt(s)));
	}

	/*
	 * immediate = {local} local_name | {constant} constant;
	 */

	public void outALocalImmediate(ALocalImmediate node) {
		String local = (String) mProductions.removeLast();

		Local l = mLocals.get(local);
		if (l == null)
			throw new RuntimeException("did not find local: " + local);
		mProductions.addLast(l);
	}

	/*
	 * constant = {integer} minus? integer_constant | {float} minus?
	 * float_constant | {string} string_constant | {null} null;
	 */

	public void outANullConstant(ANullConstant node) {
		mProductions.addLast(NullConstant.v());
	}

	public void outAIntegerConstant(AIntegerConstant node) {
		String s = (String) mProductions.removeLast();

		StringBuffer buf = new StringBuffer();
		if (node.getMinus() != null)
			buf.append('-');
		buf.append(s);

		s = buf.toString();
		if (s.endsWith("L")) {
			mProductions.addLast(LongConstant.v(Long.parseLong(s.substring(0,
					s.length() - 1))));
		} else if (s.equals("2147483648"))
			mProductions.addLast(IntConstant.v(Integer.MIN_VALUE));
		else
			mProductions.addLast(IntConstant.v(Integer.parseInt(s)));
	}

	public void outAStringConstant(AStringConstant node) {
		String s = (String) mProductions.removeLast();
		mProductions.addLast(StringConstant.v(s));
		/*
		 * try { String t = StringTools.getUnEscapedStringOf(s);
		 * 
		 * mProductions.push(StringConstant.v(t)); } catch(RuntimeException e) {
		 * G.v().out.println(s); throw e; }
		 */
	}

	public void outAClzzConstant(AClzzConstant node) {
		String s = (String) mProductions.removeLast();
		mProductions.addLast(ClassConstant.v(s));
	}

	/* ('#' (('-'? 'Infinity') | 'NaN') ('f' | 'F')? ) ; */
	public void outAFloatConstant(AFloatConstant node) {
		String s = (String) mProductions.removeLast();

		boolean isDouble = true;
		float value = 0;
		double dvalue = 0;

		if (s.endsWith("f") || s.endsWith("F"))
			isDouble = false;

		if (s.charAt(0) == '#') {
			if (s.charAt(1) == '-') {
				if (isDouble)
					dvalue = Double.NEGATIVE_INFINITY;
				else
					value = Float.NEGATIVE_INFINITY;
			} else if (s.charAt(1) == 'I') {
				if (isDouble)
					dvalue = Double.POSITIVE_INFINITY;
				else
					value = Float.POSITIVE_INFINITY;
			} else {
				if (isDouble)
					dvalue = Double.NaN;
				else
					value = Float.NaN;
			}
		} else {
			StringBuffer buf = new StringBuffer();
			if (node.getMinus() != null)
				buf.append('-');
			buf.append(s);
			s = buf.toString();

			if (isDouble)
				dvalue = Double.parseDouble(s);
			else
				value = Float.parseFloat(s);
		}

		Object res;
		if (isDouble)
			res = DoubleConstant.v(dvalue);
		else
			res = FloatConstant.v(value);

		mProductions.addLast(res);
	}

	/*
	 * binop_expr = [left]:immediate binop [right]:immediate;
	 */

	public void outABinopExpr(ABinopExpr node) {
		Value right = (Value) mProductions.removeLast();
		BinopExpr expr = (BinopExpr) mProductions.removeLast();
		Value left = (Value) mProductions.removeLast();

		expr.setOp1(left);
		expr.setOp2(right);
		mProductions.addLast(expr);
	}

	public void outABinopBoolExpr(ABinopBoolExpr node) {
	}

	public void outAUnopExpression(AUnopExpression node) {
	}

	/*
	 * binop = {and} and | {or} or | {xor} xor | {mod} mod |
	 * 
	 * {cmp} cmp | {cmpg} cmpg | {cmpl} cmpl | {cmpeq} cmpeq |
	 * 
	 * {cmpne} cmpne | {cmpgt} cmpgt | {cmpge} cmpge | {cmplt} cmplt |
	 * 
	 * {cmple} cmple | {shl} shl | {shr} shr | {ushr} ushr |
	 * 
	 * {plus} plus | {minus} minus | {mult} mult | {div} div;
	 */

	public void outAAndBinop(AAndBinop node) {
		mProductions.addLast(Jimple.v().newAndExpr(mValue, mValue));
	}

	public void outAOrBinop(AOrBinop node) {
		mProductions.addLast(Jimple.v().newOrExpr(mValue, mValue));
	}

	public void outAXorBinop(AXorBinop node) {
		mProductions.addLast(Jimple.v().newXorExpr(mValue, mValue));
	}

	public void outAModBinop(AModBinop node) {
		mProductions.addLast(Jimple.v().newRemExpr(mValue, mValue));
	}

	public void outACmpBinop(ACmpBinop node) {
		mProductions.addLast(Jimple.v().newCmpExpr(mValue, mValue));
	}

	public void outACmpgBinop(ACmpgBinop node) {
		mProductions.addLast(Jimple.v().newCmpgExpr(mValue, mValue));
	}

	public void outACmplBinop(ACmplBinop node) {
		mProductions.addLast(Jimple.v().newCmplExpr(mValue, mValue));
	}

	public void outACmpeqBinop(ACmpeqBinop node) {
		mProductions.addLast(Jimple.v().newEqExpr(mValue, mValue));
	}

	public void outACmpneBinop(ACmpneBinop node) {
		mProductions.addLast(Jimple.v().newNeExpr(mValue, mValue));
	}

	public void outACmpgtBinop(ACmpgtBinop node) {
		mProductions.addLast(Jimple.v().newGtExpr(mValue, mValue));
	}

	public void outACmpgeBinop(ACmpgeBinop node) {
		mProductions.addLast(Jimple.v().newGeExpr(mValue, mValue));
	}

	public void outACmpltBinop(ACmpltBinop node) {
		mProductions.addLast(Jimple.v().newLtExpr(mValue, mValue));
	}

	public void outACmpleBinop(ACmpleBinop node) {
		mProductions.addLast(Jimple.v().newLeExpr(mValue, mValue));
	}

	public void outAShlBinop(AShlBinop node) {
		mProductions.addLast(Jimple.v().newShlExpr(mValue, mValue));
	}

	public void outAShrBinop(AShrBinop node) {
		mProductions.addLast(Jimple.v().newShrExpr(mValue, mValue));
	}

	public void outAUshrBinop(AUshrBinop node) {
		mProductions.addLast(Jimple.v().newUshrExpr(mValue, mValue));
	}

	public void outAPlusBinop(APlusBinop node) {
		mProductions.addLast(Jimple.v().newAddExpr(mValue, mValue));
	}

	public void outAMinusBinop(AMinusBinop node) {
		mProductions.addLast(Jimple.v().newSubExpr(mValue, mValue));
	}

	public void outAMultBinop(AMultBinop node) {
		mProductions.addLast(Jimple.v().newMulExpr(mValue, mValue));
	}

	public void outADivBinop(ADivBinop node) {
		mProductions.addLast(Jimple.v().newDivExpr(mValue, mValue));
	}

	/*
	 * throws_clause = throws class_name_list;
	 */
	public void outAThrowsClause(AThrowsClause node) {
		List l = (List) mProductions.removeLast();

		Iterator it = l.iterator();
		List<SootClass> exceptionClasses = new ArrayList<SootClass>(l.size());

		while (it.hasNext()) {
			String className = (String) it.next();

			exceptionClasses.add(mResolver.makeClassRef(className));
		}

		mProductions.addLast(exceptionClasses);
	}

	/*
	 * variable = {reference} reference | {local} local_name;
	 */

	public void outALocalVariable(ALocalVariable node) {
		String local = (String) mProductions.removeLast();

		Local l = mLocals.get(local);
		if (l == null)
			throw new RuntimeException("did not find local: " + local);
		mProductions.addLast(l);
	}

	/*
	 * public void caseAReferenceVariable(AReferenceVariable node) { }
	 */

	/*
	 * array_ref = identifier fixed_array_descriptor;
	 */

	public void outAArrayReference(AArrayReference node) {
		Value immediate = (Value) mProductions.removeLast();
		String identifier = (String) mProductions.removeLast();

		Local l = mLocals.get(identifier);
		if (l == null)
			throw new RuntimeException("did not find local: " + identifier);

		mProductions.addLast(Jimple.v().newArrayRef(l, immediate));

	}

	/*
	 * field_ref = {local} local_name dot field_signature | {sig}
	 * field_signature;
	 */

	public void outALocalFieldRef(ALocalFieldRef node) {
		SootFieldRef field = (SootFieldRef) mProductions.removeLast();

		String local = (String) mProductions.removeLast();

		Local l = mLocals.get(local);
		if (l == null)
			throw new RuntimeException("did not find local: " + local);

		mProductions.addLast(Jimple.v().newInstanceFieldRef(l, field));
	}

	public void outASigFieldRef(ASigFieldRef node) {
		SootFieldRef field = (SootFieldRef) mProductions.removeLast();
		field = Scene.v().makeFieldRef(field.declaringClass(), field.name(),
				field.type(), true);
		mProductions.addLast(Jimple.v().newStaticFieldRef(field));
	}

	/*
	 * field_signature = cmplt [class_name]:class_name [first]:colon type
	 * [field_name]:name cmpgt;
	 */

	public void outAFieldSignature(AFieldSignature node) {
		String className, fieldName;
		Type t;

		fieldName = (String) mProductions.removeLast();
		t = (Type) mProductions.removeLast();
		className = (String) mProductions.removeLast();

		SootClass cl = mResolver.makeClassRef(className);
		SootFieldRef field = Scene.v().makeFieldRef(cl, fieldName, t, false);

		mProductions.addLast(field);
	}

	/*
	 * expression = {new} new_expr | {cast} l_paren nonvoid_type r_paren
	 * local_name | {instanceof} immediate instanceof nonvoid_type | {invoke}
	 * invoke_expr |
	 * 
	 * {reference} reference | {binop} binop_expr | {unop} unop_expr |
	 * {immediate} immediate;
	 */
	public void outACastExpression(ACastExpression node) {
		Value val = (Value) mProductions.removeLast();

		Type type = (Type) mProductions.removeLast();
		mProductions.addLast(Jimple.v().newCastExpr(val, type));
	}

	public void outAInstanceofExpression(AInstanceofExpression node) {
		Type nonvoidType = (Type) mProductions.removeLast();
		Value immediate = (Value) mProductions.removeLast();
		mProductions.addLast(Jimple.v().newInstanceOfExpr(immediate,
				nonvoidType));

	}

	/*
	 * unop_expr = unop immediate;
	 */
	public void outAUnopExpr(AUnopExpr node) {
		Value v = (Value) mProductions.removeLast();
		UnopExpr expr = (UnopExpr) mProductions.removeLast();
		expr.setOp(v);
		mProductions.addLast(expr);
	}

	/*
	 * unop = {lengthof} lengthof | {neg} neg;
	 */
	public void outALengthofUnop(ALengthofUnop node) {
		mProductions.addLast(Jimple.v().newLengthExpr(mValue));
	}

	public void outANegUnop(ANegUnop node) {
		mProductions.addLast(Jimple.v().newNegExpr(mValue));
	}

	/*
	 * invoke_expr = {nonstatic} nonstatic_invoke local_name dot
	 * method_signature l_paren arg_list? r_paren | {static} staticinvoke
	 * method_signature l_paren arg_list? r_paren;
	 */

	public void outANonstaticInvokeExpr(ANonstaticInvokeExpr node) {
		List args;

		if (node.getArgList() != null)
			args = (List) mProductions.removeLast();
		else
			args = new ArrayList();

		SootMethodRef method = (SootMethodRef) mProductions.removeLast();

		String local = (String) mProductions.removeLast();

		Local l = mLocals.get(local);
		if (l == null)
			throw new RuntimeException("did not find local: " + local);

		Node invokeType = node.getNonstaticInvoke();
		Expr invokeExpr;

		if (invokeType instanceof ASpecialNonstaticInvoke) {
			invokeExpr = Jimple.v().newSpecialInvokeExpr(l, method, args);
		} else if (invokeType instanceof AVirtualNonstaticInvoke) {
			invokeExpr = Jimple.v().newVirtualInvokeExpr(l, method, args);
		} else {
			if (debug)
				if (!(invokeType instanceof AInterfaceNonstaticInvoke))
					throw new RuntimeException("expected interface invoke.");
			invokeExpr = Jimple.v().newInterfaceInvokeExpr(l, method, args);
		}

		mProductions.addLast(invokeExpr);

	}

	public void outAStaticInvokeExpr(AStaticInvokeExpr node) {
		List args;

		if (node.getArgList() != null)
			args = (List) mProductions.removeLast();
		else
			args = new ArrayList();

		SootMethodRef method = (SootMethodRef) mProductions.removeLast();
		method = Scene.v().makeMethodRef(method.declaringClass(),
				method.name(), method.parameterTypes(), method.returnType(),
				true);

		mProductions.addLast(Jimple.v().newStaticInvokeExpr(method, args));
	}

	public void outADynamicInvokeExpr(ADynamicInvokeExpr node) {
		List<Value> bsmArgs;
		if (node.getStaticargs() != null)
			bsmArgs = (List) mProductions.removeLast();
		else
			bsmArgs = Collections.emptyList();

		SootMethodRef bsmMethodRef = (SootMethodRef) mProductions.removeLast();

		List<Value> dynArgs;
		if (node.getDynargs() != null)
			dynArgs = (List) mProductions.removeLast();
		else
			dynArgs = Collections.emptyList();

		SootMethodRef dynMethodRef = (SootMethodRef) mProductions.removeLast();

		mProductions.addLast(Jimple.v().newDynamicInvokeExpr(bsmMethodRef,
				bsmArgs, dynMethodRef, dynArgs));
	}

	public void outAUnnamedMethodSignature(AUnnamedMethodSignature node) {
		String className, methodName;
		List parameterList = new ArrayList();
		if (node.getParameterList() != null)
			parameterList = (List) mProductions.removeLast();

		Type type = (Type) mProductions.removeLast();
		String name = (String) mProductions.removeLast();

		SootClass sootClass = mResolver
				.makeClassRef(SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME);
		SootMethodRef sootMethod = Scene.v().makeMethodRef(sootClass, name,
				parameterList, type, false);

		mProductions.addLast(sootMethod);
	}

	/*
	 * method_signature = cmplt [class_name]:class_name [first]:colon type
	 * [method_name]:name l_paren parameter_list? r_paren cmpgt;
	 */
	public void outAMethodSignature(AMethodSignature node) {
		String className, methodName;
		List parameterList = new ArrayList();
		if (node.getParameterList() != null)
			parameterList = (List) mProductions.removeLast();

		methodName = (String) mProductions.removeLast();
		Type type = (Type) mProductions.removeLast();
		className = (String) mProductions.removeLast();

		SootClass sootClass = mResolver.makeClassRef(className);
		SootMethodRef sootMethod = Scene.v().makeMethodRef(sootClass,
				methodName, parameterList, type, false);

		mProductions.addLast(sootMethod);
	}

	/*
	 * new_expr = {simple} new base_type | {array} newarray l_paren nonvoid_type
	 * r_paren fixed_array_descriptor | {multi} newmultiarray l_paren base_type
	 * r_paren array_descriptor+;
	 */
	public void outASimpleNewExpr(ASimpleNewExpr node) {
		mProductions.addLast(Jimple.v().newNewExpr(
				(RefType) mProductions.removeLast()));
	}

	public void outAArrayNewExpr(AArrayNewExpr node) {
		Value size = (Value) mProductions.removeLast();
		Type type = (Type) mProductions.removeLast();
		mProductions.addLast(Jimple.v().newNewArrayExpr(type, size));
	}

	public void outAMultiNewExpr(AMultiNewExpr node) {

		LinkedList arrayDesc = node.getArrayDescriptor();

		int descCnt = arrayDesc.size();
		List sizes = new LinkedList();

		Iterator it = arrayDesc.iterator();
		while (it.hasNext()) {
			AArrayDescriptor o = (AArrayDescriptor) it.next();
			if (o.getImmediate() != null)
				sizes.add(0, mProductions.removeLast());
			else
				break;
		}

		Type type = (Type) mProductions.removeLast();
		ArrayType arrayType = ArrayType.v(type, descCnt);

		mProductions.addLast(Jimple.v().newNewMultiArrayExpr(arrayType, sizes));
	}

	public void defaultCase(Node node) {
		if (node instanceof TQuotedName || node instanceof TFullIdentifier
				|| node instanceof TIdentifier
				|| node instanceof TStringConstant ||

				node instanceof TIntegerConstant
				|| node instanceof TFloatConstant
				|| node instanceof TAtIdentifier

		) {
			if (debug)
				G.v().out.println("Default case -pushing token:"
						+ ((Token) node).getText());
			String tokenString = ((Token) node).getText();
			if (node instanceof TStringConstant || node instanceof TQuotedName) {
				tokenString = tokenString
						.substring(1, tokenString.length() - 1);
			}

			if (node instanceof TIdentifier || node instanceof TFullIdentifier
					|| node instanceof TQuotedName
					|| node instanceof TStringConstant) {
				try {
					tokenString = StringTools.getUnEscapedStringOf(tokenString);

				} catch (RuntimeException e) {
					G.v().out.println("Invalid escaped string: " + tokenString);
					// just used the unescaped string, better than nothing
				}
			}

			mProductions.addLast(tokenString);
		}
	}

	protected int processModifiers(List l) {
		int modifier = 0;
		Iterator it = l.iterator();

		while (it.hasNext()) {
			Object t = it.next();
			if (t instanceof AAbstractModifier)
				modifier |= Modifier.ABSTRACT;
			else if (t instanceof AFinalModifier)
				modifier |= Modifier.FINAL;
			else if (t instanceof ANativeModifier)
				modifier |= Modifier.NATIVE;
			else if (t instanceof APublicModifier)
				modifier |= Modifier.PUBLIC;
			else if (t instanceof AProtectedModifier)
				modifier |= Modifier.PROTECTED;
			else if (t instanceof APrivateModifier)
				modifier |= Modifier.PRIVATE;
			else if (t instanceof AStaticModifier)
				modifier |= Modifier.STATIC;
			else if (t instanceof ASynchronizedModifier)
				modifier |= Modifier.SYNCHRONIZED;
			else if (t instanceof ATransientModifier)
				modifier |= Modifier.TRANSIENT;
			else if (t instanceof AVolatileModifier)
				modifier |= Modifier.VOLATILE;
			else if (t instanceof AStrictfpModifier)
				modifier |= Modifier.STRICTFP;
			else if (t instanceof AEnumModifier)
				modifier |= Modifier.ENUM;
			else if (t instanceof AAnnotationModifier)
				modifier |= Modifier.ANNOTATION;
			else
				throw new RuntimeException(
						"Impossible: modifier unknown - Have you added a new modifier and not updated this file?");
		}

		return modifier;
	}

	private void addBoxToPatch(String aLabelName, UnitBox aUnitBox) {
		List<UnitBox> patchList = mLabelToPatchList.get(aLabelName);
		if (patchList == null) {
			patchList = new ArrayList<UnitBox>();
			mLabelToPatchList.put(aLabelName, patchList);
		}

		patchList.add(aUnitBox);
	}

}
