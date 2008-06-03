package dk.brics.soot.intermediate.translation;

import java.util.HashMap;
import java.util.Map;

import soot.Local;
import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.IdentityStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import dk.brics.soot.intermediate.representation.Method;
import dk.brics.soot.intermediate.representation.Nop;
import dk.brics.soot.intermediate.representation.Return;
import dk.brics.soot.intermediate.representation.Statement;
import dk.brics.soot.intermediate.representation.Variable;
import dk.brics.soot.intermediate.representation.Variable.Type;

public class StmtTranslator extends AbstractStmtSwitch {
	
	JavaTranslator jt;
	private SootClass currentClass;
	private SootMethod currentMethod;
	private Map<Stmt, Statement> firstStmtToStatement;
	private Map<Stmt, Statement> lastStmtToStatement;
    Map<Local,Variable> local_var = new HashMap<Local,Variable>();
	private Statement first_statement;
	private Statement last_statement;
	private ExprTranslator et;
	
	public StmtTranslator(JavaTranslator translator) {
		jt = translator;
		firstStmtToStatement = new HashMap<Stmt, Statement>();
		lastStmtToStatement = new HashMap<Stmt, Statement>();
		et = new ExprTranslator(jt, this);
	}
	
	public void setCurrentClass(SootClass sc) {
		this.currentClass = sc;
	}
	
	public void setCurrentMethod(SootMethod sm) {
		this.currentMethod = sm;
	}
	
	public void translateStmt(Stmt stmt) {
		first_statement = null;
		last_statement = null;
		stmt.apply(this);
		if (first_statement == null) {
			addStatement(new Nop());
		}
		firstStmtToStatement.put(stmt, first_statement);
		lastStmtToStatement.put(stmt, last_statement);
	}
	
	void addStatement(Statement s) {
		((Method)jt.methodsignaturToMethod.get(currentMethod.getSignature())).addStatement(s);
		if (first_statement == null) {
			first_statement = s;
		} else {
			last_statement.addSucc(s);
		}
		last_statement = s;
	}
	
	public Statement getFirst(Stmt stmt) {
		return firstStmtToStatement.get(stmt);
	}
	
	public Statement getLast(Stmt stmt) {
		return lastStmtToStatement.get(stmt);
	}
	
	Variable getLocalVariable(Local l) {
		if (local_var.containsKey(l)) {
			return (Variable)local_var.get(l);
		}
		Variable var = jt.makeVariable(l);
		local_var.put(l, var);
		return var;
	}
	
	public void caseInvokeStmt(InvokeStmt stmt) {
		InvokeExpr expr = stmt.getInvokeExpr();
		Variable lvar = jt.makeVariable(expr);
		et.translateExpr(lvar, stmt.getInvokeExprBox());
	}
	
	public void caseAssignStmt(AssignStmt stmt) {
		handleAssign(stmt);
	}
	
	public void caseIdentityStmt(IdentityStmt stmt) {
		handleAssign(stmt);			
	}
	
	public void caseReturnStmt(ReturnStmt stmt) {
		Variable rvar = jt.makeVariable(stmt.getOp());
		Return r = new Return();
		r.setAssignmentTarget(rvar);
		addStatement(r);
	}
	
	public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
		Return r = new Return();
		r.setAssignmentTarget(null);
		addStatement(r);
	}
	
	
	public void defaultCase(Stmt stmt) {
		addStatement(new Nop());
	}
	
	void handleAssign(DefinitionStmt stmt) {
		Value lval = stmt.getLeftOp();
		Value rval = stmt.getRightOp();
		Variable rvar;
		if (lval instanceof Local) {
			rvar = getLocalVariable((Local)lval);
		} else {
			rvar = jt.makeVariable(rval);
		}
		et.translateExpr(rvar, stmt.getRightOpBox());
		if (lval instanceof ArrayRef) {
			notSupported("We do not support arrays");
		} else if (lval instanceof FieldRef) {
			notSupported("We do not support field references");
		}
	}


	private void notSupported(String msg) {
		System.err.println(msg);
		System.exit(5);		
	}

}
