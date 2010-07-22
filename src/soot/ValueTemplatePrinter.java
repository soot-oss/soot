package soot;

import java.util.List;

import soot.jimple.AddExpr;
import soot.jimple.AndExpr;
import soot.jimple.ArrayRef;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ClassConstant;
import soot.jimple.CmpExpr;
import soot.jimple.CmpgExpr;
import soot.jimple.CmplExpr;
import soot.jimple.DivExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.EqExpr;
import soot.jimple.FloatConstant;
import soot.jimple.GeExpr;
import soot.jimple.GtExpr;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.JimpleValueSwitch;
import soot.jimple.LeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.LongConstant;
import soot.jimple.LtExpr;
import soot.jimple.MulExpr;
import soot.jimple.NeExpr;
import soot.jimple.NegExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NullConstant;
import soot.jimple.OrExpr;
import soot.jimple.ParameterRef;
import soot.jimple.RemExpr;
import soot.jimple.ShlExpr;
import soot.jimple.ShrExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.StringConstant;
import soot.jimple.SubExpr;
import soot.jimple.ThisRef;
import soot.jimple.UshrExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.XorExpr;

public class ValueTemplatePrinter implements JimpleValueSwitch {

	private final TemplatePrinter p;
	private final TypeTemplatePrinter ttp;
	private String varName;
	
	public void setVariableName(String name) {
		this.varName = name;		
	}

	public ValueTemplatePrinter(TemplatePrinter p) {
		this.p = p;
		ttp = new TypeTemplatePrinter(p);
		// TODO Auto-generated constructor stub
	}

	public void caseDoubleConstant(DoubleConstant v) {
		p.print("Value "+varName+" = DoubleConstant.v("+v.value+");");
	}

	public void caseFloatConstant(FloatConstant v) {
		
		p.print("Value "+varName+" = FloatConstant.v("+v.value+");");
	

	}

	public void caseIntConstant(IntConstant v) {
		
		p.print("Value "+varName+" = IntConstant.v("+v.value+");");

	}

	public void caseLongConstant(LongConstant v) {
		p.print("Value "+varName+" = LongConstant.v("+v.value+");");
		

	}

	public void caseNullConstant(NullConstant v) {
	
		p.print("Value "+varName+" = NullConstant.v();");
	

	}

	public void caseStringConstant(StringConstant v) {
				
		p.print("Value "+varName+" = StringConstant.v("+v.value+");");
		

	}

	public void caseClassConstant(ClassConstant v) {
		p.print("Value "+varName+" = ClassConstant.v("+v.value+");");
		
	}

	public void defaultCase(Object object) {
		// TODO Auto-generated method stub

	}

	public void caseAddExpr(AddExpr v) {
		String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newAddExpr(left,right);");
		varName = oldName;
	}

	public void caseAndExpr(AndExpr v) {
		String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newAndExpr(left,right);");
		varName = oldName;

	}

	public void caseCmpExpr(CmpExpr v) {
	String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newCmpExpr(left,right);");
		varName = oldName;


	}

	public void caseCmpgExpr(CmpgExpr v) {
	String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newCmpgExpr(left,right);");
		varName = oldName;

	}

	public void caseCmplExpr(CmplExpr v) {
	String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newCmplExpr(left,right);");
		varName = oldName;

	}

	public void caseDivExpr(DivExpr v) {
	String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newDivExpr(left,right);");
		varName = oldName;

	}

	public void caseEqExpr(EqExpr v) {
	String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newEqExpr(left,right);");
		varName = oldName;

	}

	public void caseNeExpr(NeExpr v) {
	String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newNeExpr(left,right);");
		varName = oldName;

	}

	public void caseGeExpr(GeExpr v) {
		String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newGeExpr(left,right);");
		varName = oldName;


	}

	public void caseGtExpr(GtExpr v) {
	String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newGtExpr(left,right);");
		varName = oldName;


	}

	public void caseLeExpr(LeExpr v) {
	String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newLeExpr(left,right);");
		varName = oldName;
		

	}

	public void caseLtExpr(LtExpr v) {
		String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newLtExpr(left,right);");
		varName = oldName;
	}

	public void caseMulExpr(MulExpr v) {
		String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newMulExpr(left,right);");
		varName = oldName;

	}

	public void caseOrExpr(OrExpr v) {
		String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newOrExpr(left,right);");
		varName = oldName;
		
	}

	public void caseRemExpr(RemExpr v) {
		String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newRemExpr(left,right);");
		varName = oldName;
	}

	public void caseShlExpr(ShlExpr v) {
		String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newShlExpr(left,right);");
		varName = oldName;

	}

	public void caseShrExpr(ShrExpr v) {
		String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newShrExpr(left,right);");
		varName = oldName;
		
	}

	public void caseUshrExpr(UshrExpr v) {
		String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newUshrExpr(left,right);");
		varName = oldName;

	}

	public void caseSubExpr(SubExpr v) {
		String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newSubExpr(left,right);");
		varName = oldName;
	}

	public void caseXorExpr(XorExpr v) {
		String oldName = varName;
		
		Value left = v.getOp1();
		setVariableName("left");
		left.apply(this);

		Value right = v.getOp2();
		setVariableName("right");
		right.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newXorExpr(left,right);");
		varName = oldName;

	}

	public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {
	String oldName = varName;
		
	Local base= (Local)v.getBase();
	p.println("Local base=" + base.getName()+ ";");
		
		SootMethodRef method = v.getMethodRef();
		SootMethod m = method.resolve();
		
		p.println("List<Type> parameterTypes = new LinkedList<Type>();");
		int i=0;
		for(Type t: (List<Type>)m.getParameterTypes()) {
			ttp.setVariableName("type"+i);
			t.apply(ttp);
			p.println("parameterTypes.add(type"+i+");");
			i++;
		}
		
		ttp.setVariableName("returnType");
		m.getReturnType().apply(ttp);
		
		p.print("SootMethodRef methodRef = ");
		p.print("Scene.v().makeMethodRef(");
		String className = m.getDeclaringClass().getName();
		p.print("Scene.v().getSootClass(\""+className+"\",");
		p.print(m.getName()+",");
		p.print("parameterTypes,");	
		p.print("returnType,");		
		p.print(m.isStatic()+");");		
		
		p.println("SootMethod methode=" + method.getSignature()+ ";");
		
		p.println("Value "+oldName+" = Jimple.v().newInterfaceInvokeExpr(base, method);");
		varName = oldName;

	}

	public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {
	
		String oldName = varName;
		
		Local base= (Local)v.getBase();
		p.println("Local base=" + base.getName()+ ";");
		
		SootMethodRef method = v.getMethodRef();
		SootMethod m = method.resolve();
		
		p.println("List<Type> parameterTypes = new LinkedList<Type>();");
		int i=0;
		for(Type t: (List<Type>)m.getParameterTypes()) {
			ttp.setVariableName("type"+i);
			t.apply(ttp);
			p.println("parameterTypes.add(type"+i+");");
			i++;
		}
		
		ttp.setVariableName("returnType");
		m.getReturnType().apply(ttp);
		
		p.print("SootMethodRef methodRef = ");
		p.print("Scene.v().makeMethodRef(");
		String className = m.getDeclaringClass().getName();
		p.print("Scene.v().getSootClass(\""+className+"\",");
		p.print(m.getName()+",");
		p.print("parameterTypes,");	
		p.print("returnType,");		
		p.print(m.isStatic()+");");		

		//Scene.v().makeMethodRef(declaringClass, name, parameterTypes, returnType, isStatic)
		
		p.println("Value "+oldName+" = Jimple.v().newSpecialInvokeExpr(base, methodRef);");
		varName = oldName;

	}


	public void caseStaticInvokeExpr(StaticInvokeExpr v) {
		String oldName = varName;
		
		SootMethodRef method = v.getMethodRef();
		SootMethod m = method.resolve();
		
		p.println("List<Type> parameterTypes = new LinkedList<Type>();");
		int i=0;
		for(Type t: (List<Type>)m.getParameterTypes()) {
			ttp.setVariableName("type"+i);
			t.apply(ttp);
			p.println("parameterTypes.add(type"+i+");");
			i++;
		}
		
		ttp.setVariableName("returnType");
		m.getReturnType().apply(ttp);
		
		p.print("SootMethodRef methodRef = ");
		p.print("Scene.v().makeMethodRef(");
		String className = m.getDeclaringClass().getName();
		p.print("Scene.v().getSootClass(\""+className+"\",");
		p.print(m.getName()+",");
		p.print("parameterTypes,");	
		p.print("returnType,");		
		p.print(m.isStatic()+");");	
		
		p.println("Value "+oldName+" = Jimple.v().newStaticInvokeExpr(method);");
		varName = oldName;
	
	}

	public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
		
		String oldName = varName;
		
		Local base= (Local)v.getBase();
		p.println("Local base=" + base.getName()+ ";");
		
		SootMethodRef method = v.getMethodRef();
		SootMethod m = method.resolve();
		
		p.println("List<Type> parameterTypes = new LinkedList<Type>();");
		int i=0;
		for(Type t: (List<Type>)m.getParameterTypes()) {
			ttp.setVariableName("type"+i);
			t.apply(ttp);
			p.println("parameterTypes.add(type"+i+");");
			i++;
		}
		
		ttp.setVariableName("returnType");
		m.getReturnType().apply(ttp);
		
		p.print("SootMethodRef methodRef = ");
		p.print("Scene.v().makeMethodRef(");
		String className = m.getDeclaringClass().getName();
		p.print("Scene.v().getSootClass(\""+className+"\",");
		p.print(m.getName()+",");
		p.print("parameterTypes,");	
		p.print("returnType,");		
		p.print(m.isStatic()+");");	
		
		
		//Scene.v().makeMethodRef(declaringClass, name, parameterTypes, returnType, isStatic)
		
		p.println("Value "+oldName+" = Jimple.v().newVirtualInvokeExpr(base, method);");
		varName = oldName;

	}

	public void caseCastExpr(CastExpr v) {
		String oldName = varName;
		
		Value op1 = v.getOp();
		setVariableName("op1");
		op1.apply(this);

		Type type= v.getType();
		setVariableName("type");
		type.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newCastExpr(op1, type);");
		varName = oldName;
	}

	public void caseInstanceOfExpr(InstanceOfExpr v) {
	String oldName = varName;
		
		Value op1 = v.getOp();
		setVariableName("op1");
		op1.apply(this);

		Type type= v.getType();
		setVariableName("type");
		type.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newInstanceOfExpr(op1, type);");
		
		varName = oldName; 
	}

	public void caseNewArrayExpr(NewArrayExpr v) {
		String oldName = varName;
		
		Value size = v.getSize();
		setVariableName("size");
		size.apply(this);

		Type type= v.getType();
		ttp.setVariableName("type");
		type.apply(ttp);
		
		p.println("Value "+oldName+" = Jimple.v().newNewArrayExpr(type, size);");
		varName = oldName;
	}

	public void caseNewMultiArrayExpr(NewMultiArrayExpr v) {
		
		String oldName = varName; 
		
		ttp.setVariableName("arrayType");
		v.getType().apply(ttp);
		
		p.println("List<IntConstant> sizes = new LinkedList<IntConstant>();");
		int i=0;
		for(IntConstant s: (List<IntConstant>)v.getSizes()) {
			this.setVariableName("size"+i);
			s.apply(this);
			i++;
			
			p.println("sizes.add(sizes"+i+");");
		}
		
		p.println("Value "+oldName+" = Jimple.v().newNewMultiArrayExpr(arrayType, sizes);");
		varName = oldName;
	}

	public void caseNewExpr(NewExpr v) {
		String oldName = varName;
		
		RefType type= v.getBaseType();
		setVariableName("type");
		type.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newNewExpr(type);");
		varName = oldName;

	}

	public void caseLengthExpr(LengthExpr v) {
		String oldName = varName;
		
		Value op1 = v.getOp();
		setVariableName("op1");
		op1.apply(this);
		p.println("Value "+oldName+" = Jimple.v().newLengthExpr(op);");
		varName = oldName;
	}

	public void caseNegExpr(NegExpr v) {
	String oldName = varName;
		
		Value op1 = v.getOp();
		setVariableName("op1");
		op1.apply(this);
		p.println("Value "+oldName+" = Jimple.v().newNegExpr(op);");
		varName = oldName;
	}

	public void caseArrayRef(ArrayRef v) {
		String oldName = varName;
		
		Value base = v.getBase();
		setVariableName("base");
		base.apply(this);
		
		Value index = v.getIndex();
		setVariableName("index");
		index.apply(this);
		
		p.println("Value "+oldName+" = Jimple.v().newArrayRef(base, index);");
		varName = oldName;
	}

	public void caseStaticFieldRef(StaticFieldRef v) {
		String oldName = varName;
		
		SootFieldRef op1 = v.getFieldRef();
		p.println("SootFieldRef op1=" + op1.name()+ ";");
		
		p.println("Value "+oldName+" = Jimple.v().newStaticFieldRef(f);");
		varName = oldName;

	}

	public void caseInstanceFieldRef(InstanceFieldRef v) {
		String oldName = varName;
		
		Value base = v.getBase();
		setVariableName("base");
		base.apply(this);
		
		SootFieldRef op1 = v.getFieldRef();
		p.println("Unit op1=" + op1.name()+ ";"); //is that true?
		
		p.println("Value "+oldName+" = Jimple.v().newInstanceFieldRef(base, f);");
		varName = oldName;

	}

	public void caseParameterRef(ParameterRef v) {
		String oldName = varName;
		
		Type paramType= v.getType();
		setVariableName("paramType");
		paramType.apply(ttp);
		
		int number = v.getIndex();
		p.println("int number=" + number+ ";");
		
		p.println("Value "+oldName+" = Jimple.v().newParameterRef(paramType, number);");
		varName = oldName;
	}

	public void caseCaughtExceptionRef(CaughtExceptionRef v) {
		String oldName = varName;
		p.println("Value "+oldName+" = Jimple.v().newCaughtExceptionRef();");
		varName = oldName;

	}

	public void caseThisRef(ThisRef v) {
		
		String oldName = varName;
		
		RefType type= (RefType)v.getType();
		setVariableName("type");
		type.apply(ttp);
		
		p.println("Value "+oldName+" = Jimple.v().newThisRef(type);");
		varName = oldName;
		
	}

	public void caseLocal(Local l) {
		String oldName = varName;
		
		String name = l.getName();
		p.println("String name=" + name.toString()+ ";");
		
		Type type= l.getType();
		setVariableName("type");
		type.apply(ttp);
		
		p.println("Value "+oldName+" = Jimple.v().newLocal(name, t);");
		varName = oldName;

	}
	

}
