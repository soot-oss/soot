package soot.sootify;

import java.util.List;

import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.BreakpointStmt;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.NopStmt;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.StmtSwitch;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;

class StmtTemplatePrinter implements StmtSwitch {
	private final TemplatePrinter p;
	
	private final ValueTemplatePrinter vtp; //text for expression

	/**
	 * @param templatePrinter
	 */
	StmtTemplatePrinter(TemplatePrinter templatePrinter) {
		this.p = templatePrinter;
		this.vtp = new ValueTemplatePrinter(p);
	}

	public void defaultCase(Object obj) {
		// ignore this method
		
	}

	public void caseThrowStmt(ThrowStmt stmt) {
		Value value = stmt.getOp();
		vtp.setVariableName("op");
		value.apply(vtp);
		p.println("units.add(Jimple.v().newThrowStmt(op));");
	}

	public void caseTableSwitchStmt(TableSwitchStmt stmt) {
		Value key= stmt.getKey();
		vtp.setVariableName("key");
		key.apply(vtp);
		
		int lowIndex= stmt.getLowIndex();
		p.println("int lowIndex=" + lowIndex + ";");
		
	
		int highIndex= stmt.getHighIndex();
		p.println("int highIndex=" + highIndex + ";");
		
		int i=0;
		for(Stmt s: (List<Stmt>)stmt.getTargets()) {
			//TODO must print references to statements
			vtp.setVariableName("target"+i);
			s.apply(vtp);
			i++;
			
			p.println("targets.add(target"+i+");");
		}
		
		
		Unit defaultTarget = stmt.getDefaultTarget();
		p.println("Unit defaultTarget=" + defaultTarget.toString() + ";");
		
		
		
		p.println("units.add(Jimple.v().newTableSwitchStmt(key, lowIndex, highIndex, targets, defaultTarget));");

		
	}

	public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
		p.println("units.add(Jimple.v().newReturnVoidStmt());");
		
	}

	public void caseReturnStmt(ReturnStmt stmt) {
		Value value = stmt.getOp();
		vtp.setVariableName("retVal");
		value.apply(vtp);
		p.println("units.add(Jimple.v().newReturnStmt(retVal));");		
	}

	public void caseRetStmt(RetStmt stmt) {

		Value stmtAddress= stmt.getStmtAddress();
		vtp.setVariableName("stmtAddress");
		stmtAddress.apply(vtp);
		p.println("units.add(Jimple.v().newRetStmt(stmtAddress));");
	
		
	}

	public void caseNopStmt(NopStmt stmt) {
		p.println("units.add(Jimple.v().newNopStmt());");
		
		
	}

	public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
		Value key= stmt.getKey();
		vtp.setVariableName("key");
		key.apply(vtp);
		
		p.println("List<IntConstant> lookupValues = new LinkedList<IntConstant>();");
		int i=0;
		for(IntConstant c: (List<IntConstant>)stmt.getLookupValues()) {
			vtp.setVariableName("lookupValue"+i);
			c.apply(vtp);
			i++;
			
			p.println("lookupValues.add(lookupValue"+i+");");
		}
		
		p.println("List<Stmt> targets = new LinkedList<Stmt>();");
		i=0;
		for(Stmt s: (List<Stmt>)stmt.getTargets()) {
			//TODO must print references to statements
			vtp.setVariableName("target"+i);
			s.apply(vtp);
			i++;
			
			p.println("targets.add(target"+i+");");
		}
		
		Unit defaultTarget = stmt.getDefaultTarget();
		p.println("Unit defaultTarget=" + defaultTarget.toString() + ";");
				
		p.println("units.add(Jimple.v().newLookupSwitchStmt(key, lookupValues, targets, defaultTarget));");
		}

	public void caseInvokeStmt(InvokeStmt stmt) {
		Value op = stmt.getInvokeExpr(); 
		vtp.setVariableName("op");
		op.apply(vtp);
		p.println("units.add(Jimple.v().newInvokeStmt(op));");
		
		
	}

	public void caseIfStmt(IfStmt stmt) {
		Value condition = stmt.getCondition();
		vtp.setVariableName("condition");
		condition.apply(vtp);
		
		Unit target = stmt.getTarget();
		p.println("Unit target=" + target.toString() + ";");
		
		p.println("units.add(Jimple.v().newIfStmt(condition, target));");
		
		
	}

	public void caseIdentityStmt(IdentityStmt stmt) {
		

	Value local = stmt.getLeftOp();
	vtp.setVariableName("local");
		local.apply(vtp);
		
		Value identityRef = stmt.getRightOp();
		vtp.setVariableName("identityRef");
		identityRef.apply(vtp);
			
		p.println("units.add(Jimple.v().newIdentityStmt(local, identityRef));");
		
	}

	public void caseGotoStmt(GotoStmt stmt) {
		Unit target = stmt.getTarget();
		p.println("Unit target=" + target.toString() + ";");
		
		p.println("units.add(Jimple.v().newGotoStmt(target));");
		
	}

	public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
		Value op = stmt.getOp();
		vtp.setVariableName("op");
		op.apply(vtp);
		
		p.println("units.add(Jimple.v().newExitMonitorStmt(op));");
		
	}

	public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
		
		Value op = stmt.getOp();
		vtp.setVariableName("op");
		op.apply(vtp);
		
		p.println("units.add(Jimple.v().newEnterMonitorStmt(op));");
		
	}

	public void caseBreakpointStmt(BreakpointStmt stmt) {
		
		p.println("units.add(Jimple.v().newBreakpointStmt());");
		
	}

	public void caseAssignStmt(AssignStmt stmt) {
		
		Value variable = stmt.getLeftOp();
		vtp.setVariableName("variable");
		variable.apply(vtp);
		
		Value rvalue = stmt.getRightOp();
		vtp.setVariableName("rvalue");
		rvalue.apply(vtp);
		
		p.println("units.add(Jimple.v().newAssignStmt(variable, rvalue));");
		

		
	}
}