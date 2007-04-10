package dk.brics.soot.intermediate.translation;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import soot.IntType;
import soot.Local;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ArrayRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.NewExpr;
import soot.jimple.NullConstant;
import soot.jimple.ParameterRef;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.StringConstant;
import soot.jimple.VirtualInvokeExpr;
import dk.brics.soot.intermediate.representation.FooAssignment;
import dk.brics.soot.intermediate.representation.FooInit;
import dk.brics.soot.intermediate.representation.FooMethodCall;
import dk.brics.soot.intermediate.representation.Method;
import dk.brics.soot.intermediate.representation.SomeMethodCall;
import dk.brics.soot.intermediate.representation.Nop;
import dk.brics.soot.intermediate.representation.Statement;
import dk.brics.soot.intermediate.representation.Variable;



public class ExprTranslator extends soot.jimple.AbstractJimpleValueSwitch {
	
	private JavaTranslator jt;
	private StmtTranslator st;
	private Variable res_var;
	
	public ExprTranslator(JavaTranslator jt, StmtTranslator st) {
		this.jt = jt;
		this.st = st;
	}
	
	// Called for any type of value
	public void translateExpr(Variable var, ValueBox box) {
		Value val = box.getValue();
		Variable temp = res_var;
		res_var = var;
		val.apply(this);
		res_var = temp;
	}
	
	public void caseLocal(Local expr) {
		assign(res_var, st.getLocalVariable(expr));
	}
	
	public void caseArrayRef(ArrayRef expr) {
		jt.notSupported("Array references are not supported");
	}
	
	public void caseInstanceFieldRef(InstanceFieldRef expr) {
		jt.notSupported("Instance field references are not supported");
	}
	
	public void caseStaticFieldRef(StaticFieldRef expr) {
	    st.addStatement(new Nop());
	}
	
	public void caseParameterRef(ParameterRef expr) {
		st.addStatement(new Nop());
	}
	
	public void caseNullConstant(NullConstant expr) {
		jt.notSupported("The null constant is not supported");
	}
	
	public void caseStringConstant(StringConstant expr) {
		jt.notSupported("String constants are not supported");
	}
		
	public void caseNewExpr(NewExpr expr) {
		st.addStatement(new Nop());
	}
	
	public void caseSpecialInvokeExpr(SpecialInvokeExpr expr) {
		// Constructor calls, maybe
		Variable bvar = st.getLocalVariable((Local)expr.getBase());
		SootMethod m = expr.getMethod();
		if (m.getName().equals("<init>")) {
			SootClass dc = m.getDeclaringClass();
			if (isFoo(dc)) {
				FooInit fi = new FooInit();
				fi.setAssignmentTarget(bvar);
				st.addStatement(fi);
				return;
			}
		}
		handleCall(expr, expr.getMethod());
	}
	
	public void caseStaticInvokeExpr(StaticInvokeExpr expr) {
		handleCall(expr, expr.getMethod());
	}
	
	public void caseInterfaceInvokeExpr(InterfaceInvokeExpr expr) {
		caseInstanceInvokeExpr(expr);
	}
	
	public void caseVirtualInvokeExpr(VirtualInvokeExpr expr) {
		caseInstanceInvokeExpr(expr);
	}
	
	void caseInstanceInvokeExpr(InstanceInvokeExpr expr) {
		List<SootMethod> targets = jt.getTargetsOf(expr);
		if (targets.isEmpty()) {
		    // This is a call to an interface method or abstract method
		    // with no available implementations.
		    // You could use instruction target as target.
			jt.notSupported("We don't support abstract methods");
		}
		for (SootMethod target: targets) {
		    boolean special = handleSpecialCall(expr, target);
		    if (!special) {
		    	handleCall(expr, target);
		    }
		}
	}
	
	void assign(Variable lvar, Variable rvar) {
		switch (lvar.type) {
		case FOO:
			FooAssignment fa = new FooAssignment();
			fa.setAssignmentTarget(lvar);
			st.addStatement(fa);
			return;
		}
	}

	boolean isString(Type t) {
		return t.equals(RefType.v(JavaTranslator.FOOQUALIFIEDNAME));
	}
	
	boolean isFoo(SootClass c) {
		return c.getName().equals(JavaTranslator.FOOQUALIFIEDNAME);
	}
	
	boolean isStringBuffer(Type t) {
		return t.equals(RefType.v(JavaTranslator.FOOQUALIFIEDNAME));
	}
	
	boolean isStringBuffer(SootClass c) {
		return c.getName().equals(JavaTranslator.FOOQUALIFIEDNAME);
	}
		
	// For a single target, foo-type or not, non-special
	void handleCall(InvokeExpr expr, SootMethod target) {
		SootClass dc = target.getDeclaringClass();
		if (jt.isApplicationClass(dc)) {
			if (!dc.isInterface()) {
				// Target in an application class.
				// Setup call to translated method.
				Method method = jt.methodsignaturToMethod.get(target.getSignature());
				SomeMethodCall smc = new SomeMethodCall(method, getArgs(target));
				st.addStatement(smc);
			} else {
				// Call to interface method or abstract method as target.
				// Only occurs if no implementions are found.
				// Arguments are evaluated and nothing is returned.
				jt.notSupported("We don't support interface calls");
			}
		}
		else {
			// Target in non-application class.
			// We should Escape all arguments and corrupt result.
			// So this might not be what we want.
			Method method = new Method(target.getName(), getArgs(target));
			SomeMethodCall smc = new SomeMethodCall(method, getArgs(target));
			st.addStatement(smc);
			return;
		}
	}

	// any type
	boolean handleSpecialCall(InstanceInvokeExpr expr, SootMethod target) {
		String mn = target.getName();
		int np = target.getParameterCount();
		SootClass dc = target.getDeclaringClass();
		
		// Is it a method in a Foo object?
		if (isFoo(dc)) {
			if (mn.equals("foo") && np == 1){
				Variable lvar = new Variable(Variable.Type.FOO);			
				Method foo = new Method("foo", new Variable[0]);
				FooMethodCall fmc = new FooMethodCall(foo);
				fmc.setAssignmentTarget(lvar);
				st.addStatement(fmc);
				return true;
			}
			// Unknown Foo method
			return false;
		}
		// Not a special call
		return false;
	}
	
	public Variable[] getArgs(SootMethod target) {
		Variable[] vars = new Variable[target.getParameterCount()];
		for (int i= 0; i<target.getParameterCount(); i++) {
			vars[i] = jt.makeVariable(target.getParameterType(i));
		}
		return vars;
	}
	
}
