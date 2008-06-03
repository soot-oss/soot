package dk.brics.soot.intermediate.translation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import soot.ArrayType;
import soot.Hierarchy;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.toolkits.graph.CompleteUnitGraph;
import dk.brics.soot.intermediate.representation.Method;
import dk.brics.soot.intermediate.representation.Statement;
import dk.brics.soot.intermediate.representation.Variable;

@SuppressWarnings("unchecked") 
public class JavaTranslator {
	
	private Hierarchy h;
	private List<Method> methods = new LinkedList<Method>();
	StmtTranslator st;
	Map<String,Method> methodsignaturToMethod = new HashMap<String, Method>();
	
	public static final String FOOQUALIFIEDNAME = "dk.brics.soot.intermediate.foo.Foo";
	
	public Method[] translateApplicationClasses() {
		h = new Hierarchy();
		makeMethods();
		translate();
		return methods.toArray(new Method[0]);
	}
	
	private void translate() {
		st = new StmtTranslator(this);
		Collection<SootClass> app = Scene.v().getApplicationClasses();
		for (SootClass ac: app) {
			st.setCurrentClass(ac);
			List<SootMethod> sootMethods = ac.getMethods();
			for (SootMethod sm: sootMethods) {
				if (sm.isConcrete()) {
					Method method = methodsignaturToMethod.get(sm.getSignature());
					st.setCurrentMethod(sm);
				
					CompleteUnitGraph cug = new CompleteUnitGraph(sm.retrieveActiveBody());
					Iterator si = cug.iterator();
					
					while (si.hasNext()) {
						Stmt stmt = (Stmt)si.next();
						st.translateStmt(stmt);
					}
					//System.err.println("Setting up link between entry and first stmt of the method...");
					si = cug.getHeads().iterator();
					while (si.hasNext()) {
						Stmt stmt = (Stmt)si.next();						
						method.getEntry().addSucc(st.getFirst(stmt));
					}
					si = cug.iterator();
					//System.err.println("Setting up link between the last statement and the first statement...");
					while (si.hasNext()) {
						Stmt stmt = (Stmt)si.next();
						Iterator si2 = cug.getSuccsOf(stmt).iterator();
						while (si2.hasNext()) {
							Stmt stmt2 = (Stmt)si2.next();
							st.getLast(stmt).addSucc(st.getFirst(stmt2));
						}
					}
//					System.err.println("!!!!!!!!!!!!!!!!!!"+sm.getName()+"!!!!!!!!!!!!!!!!!!!!");
//					si = cug.iterator();
//					while (si.hasNext()) {
//						Stmt stmt = (Stmt)si.next();
//						System.err.println("The stmt: "+stmt+" translates to "+st.getFirst(stmt));
//						//System.err.println(st.getFirst(stmt).getSuccs());
//					}
//					System.err.println("######################################");
				}
			}
		}
	}

	void makeMethods() {
		Collection<SootClass> app = Scene.v().getApplicationClasses();
		for(SootClass ac: app) {
			List<SootMethod> sootMethods = ac.getMethods();
			for (SootMethod sm: sootMethods) {
				List<Variable> vars = new LinkedList<Variable>();
				List<Type> params = sm.getParameterTypes();
				for (Type pt: params) {
					Variable v = makeVariable(pt);
					vars.add(v);
				}			
				Variable[] var_array = (Variable[])vars.toArray(new Variable[0]);
				Method m = new Method(sm.getName(), var_array);
				methods.add(m);
				methodsignaturToMethod.put(sm.getSignature(), m);
			}
		}
	}
	
    Variable makeVariable(Value v) {
    	return makeVariable(v.getType());
    }
    
	Variable makeVariable(Type t) {
		Variable.Type type = getType(t);
		return new Variable(type);
	}
	
	public boolean isFooType(Value v) {
		return isFooType(v.getType());
	}
	
	public boolean isFooType(Type t) {
		return getType(t) != Variable.Type.OTHER;
	}
	
	public List<SootMethod> getTargetsOf(InvokeExpr expr) {
		if (expr instanceof InstanceInvokeExpr) {
			return getTargetsOf(((InstanceInvokeExpr)expr).getBase(), expr.getMethod());
		}
		List<SootMethod> targets = new ArrayList(1);
		targets.add(expr.getMethod());
		return targets;
	}
	
	public List<SootMethod> getTargetsOf(Value v, SootMethod m) {
		SootClass rc;
		Type t = v.getType();
		if (t instanceof ArrayType) {
			rc = Scene.v().getSootClass("java.lang.Object");
		} else {
			rc = ((RefType)v.getType()).getSootClass();
		}
		List<SootMethod> targets = h.resolveAbstractDispatch(rc, m);
		return targets;
	}
	
	private Variable.Type getType(Type t) {
		if (t instanceof RefType) {
			if (((RefType)t).getSootClass().getName().equals(FOOQUALIFIEDNAME)) {
				return Variable.Type.FOO;
			}
		}
		if (t instanceof ArrayType) {
			return Variable.Type.OTHER;
		}
		return Variable.Type.OTHER;
	}
	
    boolean isApplicationClass(SootClass c) {
    	Iterator aci = Scene.v().getApplicationClasses().iterator();
    	while (aci.hasNext()) {
    	    SootClass ac = (SootClass)aci.next();
    	    if (c.getName().equals(ac.getName())) {
    		return true;
    	    }
    	}
    	return false;
    }
    
	public void notSupported(String msg) {
		System.err.println(msg);
		System.exit(5);
	}
}
