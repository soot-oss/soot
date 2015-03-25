/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Nomair A. Naeem
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
 * Maintained by Nomair A. Naeem
 */

/*
 * CHANGE LOG:   23rd November, 2005: Added explicit check for DVariableDeclarationStmt in checking stmts
 *                       This is essential because to get complete code coverage the traversal routine needs
 *                       to go into the DVariableDeclarationStmt and invoke applies on the defs or local
 *                       being declared in there. 
 */
package soot.dava.toolkits.base.AST.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.*;
import soot.jimple.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.asg.*;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.dava.internal.javaRep.*;
/*
 * CHANGE LOG: 18th MArch 2006: Need a reference to the ValueBox holding a BinOp for SimplifyExpressions
 *              Need to create a level of indirection i.e. instead of retrieving Values e.g. from stmts retrieve the valueBox
 *              and then apply on the value inside the valuebox
 */
public class DepthFirstAdapter extends AnalysisAdapter {

	private static final Logger logger =LoggerFactory.getLogger(DepthFirstAdapter.class);



	public DepthFirstAdapter() {
	}

	public DepthFirstAdapter(boolean verbose) {
	}

	public void inASTMethodNode(ASTMethodNode node) {
		 
			logger.trace("inASTMethodNode");
	}

	public void outASTMethodNode(ASTMethodNode node) {
		 
			logger.trace("outASTMethodNode");
	}

	public void caseASTMethodNode(ASTMethodNode node) {
		inASTMethodNode(node);
		normalRetrieving(node);
		outASTMethodNode(node);
	}

	public void inASTSynchronizedBlockNode(ASTSynchronizedBlockNode node) {
		 
			logger.trace("inASTSynchronizedBlockNode");
	}

	public void outASTSynchronizedBlockNode(ASTSynchronizedBlockNode node) {
		 
			logger.trace("outASTSynchronizedBlockNode");
	}

	public void caseASTSynchronizedBlockNode(ASTSynchronizedBlockNode node) {
		inASTSynchronizedBlockNode(node);

		/*
		 apply on the local on which synchronization is done
		 MArch 18th, 2006: since getLocal returns a local always dont need a valuebox for this
		 */
		Value local = node.getLocal();
		
		decideCaseExprOrRef(local);

		/*
		 apply on the body of the synch block
		 */
		normalRetrieving(node);

		outASTSynchronizedBlockNode(node);
	}

	public void inASTLabeledBlockNode(ASTLabeledBlockNode node) {
		 
			logger.trace("inASTLabeledBlockNode");
	}

	public void outASTLabeledBlockNode(ASTLabeledBlockNode node) {
		 
			logger.trace("outASTLabeledBlockNode");
	}

	public void caseASTLabeledBlockNode(ASTLabeledBlockNode node) {
		inASTLabeledBlockNode(node);
		normalRetrieving(node);
		outASTLabeledBlockNode(node);
	}

	public void inASTUnconditionalLoopNode(ASTUnconditionalLoopNode node) {
		 
			logger.trace("inASTUnconditionalWhileNode");
	}

	public void outASTUnconditionalLoopNode(ASTUnconditionalLoopNode node) {
		 
			logger.trace("outASTUnconditionalWhileNode");
	}

	public void caseASTUnconditionalLoopNode(ASTUnconditionalLoopNode node) {
		inASTUnconditionalLoopNode(node);
		normalRetrieving(node);
		outASTUnconditionalLoopNode(node);
	}

	public void inASTSwitchNode(ASTSwitchNode node) {
		 
			logger.trace("inASTSwitchNode");
	}

	public void outASTSwitchNode(ASTSwitchNode node) {
		 
			logger.trace("outASTSwitchNode");
	}

	public void caseASTSwitchNode(ASTSwitchNode node) {
		inASTSwitchNode(node);

		/*
		 apply on key of switchStatement
		 */
		/*
		 * March 18th 2006, added level of indirection to have access to value box
		 * Value key = node.get_Key();
		 * decideCaseExprOrRef(key);
		 */
		caseExprOrRefValueBox(node.getKeyBox());
				
		/*
		 Apply on bodies of switch cases
		 */
		normalRetrieving(node);
		outASTSwitchNode(node);
	}

	public void inASTIfNode(ASTIfNode node) {
		 
			logger.trace("inASTIfNode");
	}

	public void outASTIfNode(ASTIfNode node) {
		 
			logger.trace("outASTIfNode");
	}

	public void caseASTIfNode(ASTIfNode node) {
		inASTIfNode(node);

		/*
		 apply on the ASTCondition
		 */
		ASTCondition condition = node.get_Condition();
		condition.apply(this);

		/*
		 Apply on the if body
		 */
		normalRetrieving(node);
		outASTIfNode(node);
	}

	public void inASTIfElseNode(ASTIfElseNode node) {
		 
			logger.trace("inASTIfElseNode");
	}

	public void outASTIfElseNode(ASTIfElseNode node) {
		 
			logger.trace("outASTIfElseNode");
	}

	public void caseASTIfElseNode(ASTIfElseNode node) {
		inASTIfElseNode(node);

		/*
		 apply on the ASTCondition
		 */
		ASTCondition condition = node.get_Condition();
		condition.apply(this);

		/*
		 Apply on the if body followed by the else body
		 */
		normalRetrieving(node);

		outASTIfElseNode(node);
	}

	public void inASTWhileNode(ASTWhileNode node) {
		 
			logger.trace("inASTWhileNode");
	}

	public void outASTWhileNode(ASTWhileNode node) {
		 
			logger.trace("outASTWhileNode");
	}

	public void caseASTWhileNode(ASTWhileNode node) {
		inASTWhileNode(node);

		/*
		 apply on the ASTCondition
		 */
		ASTCondition condition = node.get_Condition();
		condition.apply(this);

		/*
		 Apply on the while body
		 */
		normalRetrieving(node);
		outASTWhileNode(node);
	}

	public void inASTForLoopNode(ASTForLoopNode node) {
		 
			logger.trace("inASTForLoopNode");
	}

	public void outASTForLoopNode(ASTForLoopNode node) {
		 
			logger.trace("outASTForLoopNode");
	}

	public void caseASTForLoopNode(ASTForLoopNode node) {
		inASTForLoopNode(node);

		/*
		 Apply on init
		 */
		List<Object> init = node.getInit();
		Iterator<Object> it = init.iterator();
		while (it.hasNext()) {
			AugmentedStmt as = (AugmentedStmt) it.next();
			Stmt s = as.get_Stmt();
			if (s instanceof DefinitionStmt)
				caseDefinitionStmt((DefinitionStmt) s);
			else if (s instanceof ReturnStmt)
				caseReturnStmt((ReturnStmt) s);
			else if (s instanceof InvokeStmt)
				caseInvokeStmt((InvokeStmt) s);
			else if (s instanceof ThrowStmt)
				caseThrowStmt((ThrowStmt) s);
			else
				caseStmt(s);
		}

		/*
		 apply on the ASTCondition
		 */
		ASTCondition condition = node.get_Condition();
		condition.apply(this);

		/*
		 Apply on update
		 */
		List<Object> update = node.getUpdate();
		it = update.iterator();
		while (it.hasNext()) {
			AugmentedStmt as = (AugmentedStmt) it.next();
			Stmt s = as.get_Stmt();

			if (s instanceof DefinitionStmt)
				caseDefinitionStmt((DefinitionStmt) s);
			else if (s instanceof ReturnStmt)
				caseReturnStmt((ReturnStmt) s);
			else if (s instanceof InvokeStmt)
				caseInvokeStmt((InvokeStmt) s);
			else if (s instanceof ThrowStmt)
				caseThrowStmt((ThrowStmt) s);
			else
				caseStmt(s);
		}

		/*
		 Apply on the for body
		 */
		normalRetrieving(node);
		outASTForLoopNode(node);
	}

	public void inASTDoWhileNode(ASTDoWhileNode node) {
		 
			logger.trace("inASTDoWhileNode");
	}

	public void outASTDoWhileNode(ASTDoWhileNode node) {
		 
			logger.trace("outASTDoWhileNode");
	}

	public void caseASTDoWhileNode(ASTDoWhileNode node) {
		inASTDoWhileNode(node);

		/*
		 apply on the ASTCondition
		 */
		ASTCondition condition = node.get_Condition();
		condition.apply(this);

		/*
		 Apply on the while body
		 */
		normalRetrieving(node);
		outASTDoWhileNode(node);
	}

	public void inASTTryNode(ASTTryNode node) {
		 
			logger.trace("inASTTryNode");
	}

	public void outASTTryNode(ASTTryNode node) {
		 
			logger.trace("outASTTryNode");
	}

	public void caseASTTryNode(ASTTryNode node) {
		inASTTryNode(node);

		//get try body 
		List<Object> tryBody = node.get_TryBody();
		Iterator<Object> it = tryBody.iterator();

		//go over the ASTNodes in this tryBody and apply
		while (it.hasNext())
			((ASTNode) it.next()).apply(this);

		Map<Object, Object> exceptionMap = node.get_ExceptionMap();
		Map<Object, Object> paramMap = node.get_ParamMap();
		//get catch list and apply on the following
		// a, type of exception caught
		// b, local of exception
		// c, catchBody
		List<Object> catchList = node.get_CatchList();
		Iterator itBody = null;
		it = catchList.iterator();
		while (it.hasNext()) {
			ASTTryNode.container catchBody = (ASTTryNode.container) it.next();

			SootClass sootClass = ((SootClass) exceptionMap.get(catchBody));
			Type type = sootClass.getType();

			//apply on type of exception
			caseType(type);

			//apply on local of exception
			Local local = (Local) paramMap.get(catchBody);
			/*
			 * March 18th, 2006, Since these are always locals we dont have access to ValueBox
			 */
			decideCaseExprOrRef(local);

			//apply on catchBody
			List body = (List) catchBody.o;
			itBody = body.iterator();
			while (itBody.hasNext()) {
				((ASTNode) itBody.next()).apply(this);
			}
		}
		outASTTryNode(node);
	}

	public void inASTUnaryCondition(ASTUnaryCondition uc) {
		 
			logger.trace("inASTUnaryCondition");
	}

	public void outASTUnaryCondition(ASTUnaryCondition uc) {
		 
			logger.trace("outASTUnaryCondition");
	}

	public void caseASTUnaryCondition(ASTUnaryCondition uc) {
		inASTUnaryCondition(uc);
		//apply on the value
		decideCaseExprOrRef(uc.getValue());
		outASTUnaryCondition(uc);
	}

	public void inASTBinaryCondition(ASTBinaryCondition bc) {
		 
			logger.trace("inASTBinaryCondition");
	}

	public void outASTBinaryCondition(ASTBinaryCondition bc) {
		 
			logger.trace("outASTBinaryCondition");
	}

	public void caseASTBinaryCondition(ASTBinaryCondition bc) {
		inASTBinaryCondition(bc);

		ConditionExpr condition = bc.getConditionExpr();
		//calling decideCaseExprOrRef although we know for sure this is an Expr but doesnt matter
		
		decideCaseExprOrRef(condition);

		outASTBinaryCondition(bc);
	}

	public void inASTAndCondition(ASTAndCondition ac) {
		 
			logger.trace("inASTAndCondition");
	}

	public void outASTAndCondition(ASTAndCondition ac) {
		 
			logger.trace("outASTAndCondition");
	}

	public void caseASTAndCondition(ASTAndCondition ac) {
		inASTAndCondition(ac);

		((ac.getLeftOp())).apply(this);
		((ac.getRightOp())).apply(this);

		outASTAndCondition(ac);
	}

	public void inASTOrCondition(ASTOrCondition oc) {
		 
			logger.trace("inASTOrCondition");
	}

	public void outASTOrCondition(ASTOrCondition oc) {
		 
			logger.trace("outASTOrCondition");
	}

	public void caseASTOrCondition(ASTOrCondition oc) {
		inASTOrCondition(oc);

		((oc.getLeftOp())).apply(this);
		((oc.getRightOp())).apply(this);

		outASTOrCondition(oc);
	}

	public void inType(Type t) {
		 
			logger.trace("inType");
	}

	public void outType(Type t) {
		 
			logger.trace("outType");
	}

	public void caseType(Type t) {
		inType(t);
		outType(t);
	}

	public void normalRetrieving(ASTNode node) {
		//from the Node get the subBodes
		Iterator<Object> sbit = node.get_SubBodies().iterator();
		while (sbit.hasNext()) {
			Object subBody = sbit.next();
			Iterator it = ((List) subBody).iterator();

			//go over the ASTNodes in this subBody and apply
			while (it.hasNext()) {
				ASTNode temp = (ASTNode) it.next();

				temp.apply(this);
			}
		}//end of going over subBodies
	}

	public void inASTStatementSequenceNode(ASTStatementSequenceNode node) {
		 
			logger.trace("inASTStatementSequenceNode");
	}

	public void outASTStatementSequenceNode(ASTStatementSequenceNode node) {
		 
			logger.trace("outASTStatementSequenceNode");
	}

	public void caseASTStatementSequenceNode(ASTStatementSequenceNode node) {
		inASTStatementSequenceNode(node);
		Iterator<Object> it = node.getStatements().iterator();
		while (it.hasNext()) {
			AugmentedStmt as = (AugmentedStmt) it.next();
			Stmt s = as.get_Stmt();
			/*
			 Do a case by case check of possible statements and invoke
			 the case methods from within this method.
			 
			 cant use apply since the Statements are defined in some other
			 package and dont want to change code all over the place
			 */

			if (s instanceof DefinitionStmt)
				caseDefinitionStmt((DefinitionStmt) s);
			else if (s instanceof ReturnStmt)
				caseReturnStmt((ReturnStmt) s);
			else if (s instanceof InvokeStmt)
				caseInvokeStmt((InvokeStmt) s);
			else if (s instanceof ThrowStmt)
				caseThrowStmt((ThrowStmt) s);
			else if (s instanceof DVariableDeclarationStmt)
				caseDVariableDeclarationStmt((DVariableDeclarationStmt) s);
			else
				caseStmt(s);

		}//end of while going through the statement sequence
		outASTStatementSequenceNode(node);
	}

	public void inDefinitionStmt(DefinitionStmt s) {
		 
			logger.trace("inDefinitionStmt" + s);
	}

	public void outDefinitionStmt(DefinitionStmt s) {
		 
			logger.trace("outDefinitionStmt");
	}

	public void caseDefinitionStmt(DefinitionStmt s) {
		inDefinitionStmt(s);
		
		/*
		 * March 18th, 2006 introducing level of indirection
		 * decideCaseExprOrRef(s.getRightOp());
		 * decideCaseExprOrRef(s.getLeftOp());
		 */
		caseExprOrRefValueBox(s.getRightOpBox());
		caseExprOrRefValueBox(s.getLeftOpBox());
		

		outDefinitionStmt(s);
	}

	public void inReturnStmt(ReturnStmt s) {
		 
			logger.trace("inReturnStmt");
		//	logger.info("Return Stmt:"+s);
	}

	public void outReturnStmt(ReturnStmt s) {
		 
			logger.trace("outReturnStmt");
	}

	public void caseReturnStmt(ReturnStmt s) {
		inReturnStmt(s);

		/*
		 * MArch 18th 2006
		 * decideCaseExprOrRef(s.getOp());
		 */
		caseExprOrRefValueBox(s.getOpBox());

		outReturnStmt(s);
	}

	public void inInvokeStmt(InvokeStmt s) {
		 
			logger.trace("inInvokeStmt");
	}

	public void outInvokeStmt(InvokeStmt s) {
		 
			logger.trace("outInvokeStmt");
	}

	public void caseInvokeStmt(InvokeStmt s) {
		inInvokeStmt(s);

		caseExprOrRefValueBox(s.getInvokeExprBox());
		//decideCaseExprOrRef(s.getInvokeExpr());

		outInvokeStmt(s);
	}

	public void inThrowStmt(ThrowStmt s) {
		 
			logger.trace("\n\ninThrowStmt\n\n");
	}

	public void outThrowStmt(ThrowStmt s) {
		 
			logger.trace("outThrowStmt");
	}

	public void caseThrowStmt(ThrowStmt s) {
		inThrowStmt(s);
		caseExprOrRefValueBox(s.getOpBox());
		//decideCaseExprOrRef(s.getOp());

		outThrowStmt(s);
	}

	public void inDVariableDeclarationStmt(DVariableDeclarationStmt s) {
		 
			logger.trace("\n\ninDVariableDeclarationStmt\n\n" + s);
	}

	public void outDVariableDeclarationStmt(DVariableDeclarationStmt s) {
		 
			logger.trace("outDVariableDeclarationStmt");
	}

	public void caseDVariableDeclarationStmt(DVariableDeclarationStmt s) {
		inDVariableDeclarationStmt(s);

		//a variableDeclarationStmt has a type followed by a list of locals
		Type type = s.getType();
		caseType(type);

		List listDeclared = s.getDeclarations();
		Iterator it = listDeclared.iterator();
		while (it.hasNext()) {
			Local declared = (Local) it.next();
			decideCaseExprOrRef(declared);
		}

		outDVariableDeclarationStmt(s);
	}

	public void inStmt(Stmt s) {
		 
			logger.trace("inStmt: " + s);

		/*
		 if(s instanceof DAbruptStmt)
		 logger.info("DAbruptStmt: "+s);
		 if(s instanceof ReturnVoidStmt)
		 logger.info("ReturnVoidStmt: "+s);
		 */
	}

	public void outStmt(Stmt s) {
		 
			logger.trace("outStmt");
	}

	public void caseStmt(Stmt s) {
		inStmt(s);
		outStmt(s);
	}

	
	/*
	 * March 18th 2006, Adding new indirection
	 */
	public void caseExprOrRefValueBox(ValueBox vb){
		inExprOrRefValueBox(vb);
		decideCaseExprOrRef(vb.getValue());
		outExprOrRefValueBox(vb);
	}
	
	public void inExprOrRefValueBox(ValueBox vb){
		  
			logger.trace("inExprOrRefValueBox" + vb);
		
	}

		
	public void outExprOrRefValueBox(ValueBox vb){
		  
			logger.trace("outExprOrRefValueBox" + vb);
			
	}

	
	
	public void decideCaseExprOrRef(Value v) {
		if (v instanceof Expr)
			caseExpr((Expr) v);
		else if (v instanceof Ref)
			caseRef((Ref) v);
		else
			caseValue(v);
	}

	public void inValue(Value v) {
		  {
			logger.trace("inValue" + v);

			if (v instanceof DThisRef)
				logger.trace("DTHISREF.................");
			else if (v instanceof Immediate) {
				logger.trace("\tIMMEDIATE");
				if (v instanceof soot.jimple.internal.JimpleLocal) {
					logger.trace("\t\tJimpleLocal...................."
							+ v);

				} else if (v instanceof Constant) {
					logger.trace("\t\tconstant....................");
					if (v instanceof IntConstant)
						logger.trace("\t\t INTconstant....................");
				} else if (v instanceof soot.baf.internal.BafLocal) {
					logger.trace("\t\tBafLocal....................");
				} else
					logger.trace("\t\telse!!!!!!!!!!!!");
			} else {
				logger.trace("NEITHER................");
			}
		}
	}

	public void outValue(Value v) {
		 
			logger.trace("outValue");
	}

	public void caseValue(Value v) {
		inValue(v);
		outValue(v);
	}

	public void inExpr(Expr e) {
		 
			logger.trace("inExpr");
	}

	public void outExpr(Expr e) {
		 
			logger.trace("outExpr");
	}

	public void caseExpr(Expr e) {
		inExpr(e);
		decideCaseExpr(e);
		outExpr(e);
	}

	public void inRef(Ref r) {
		 
			logger.trace("inRef");
	}

	public void outRef(Ref r) {
		 
			logger.trace("outRef");
	}

	public void caseRef(Ref r) {
		inRef(r);
		decideCaseRef(r);
		outRef(r);
	}

	public void decideCaseExpr(Expr e) {
		if (e instanceof BinopExpr)
			caseBinopExpr((BinopExpr) e);
		else if (e instanceof UnopExpr)
			caseUnopExpr((UnopExpr) e);
		else if (e instanceof NewArrayExpr)
			caseNewArrayExpr((NewArrayExpr) e);
		else if (e instanceof NewMultiArrayExpr)
			caseNewMultiArrayExpr((NewMultiArrayExpr) e);
		else if (e instanceof InstanceOfExpr)
			caseInstanceOfExpr((InstanceOfExpr) e);
		else if (e instanceof InvokeExpr)
			caseInvokeExpr((InvokeExpr) e);
		else if (e instanceof CastExpr)
			caseCastExpr((CastExpr) e);
	}

	public void inBinopExpr(BinopExpr be) {
		 
			logger.trace("inBinopExpr");
	}

	public void outBinopExpr(BinopExpr be) {
		 
			logger.trace("outBinopExpr");
	}

	public void caseBinopExpr(BinopExpr be) {
		inBinopExpr(be);

		caseExprOrRefValueBox(be.getOp1Box());
		caseExprOrRefValueBox(be.getOp2Box());
		//decideCaseExprOrRef(be.getOp1());
		//decideCaseExprOrRef(be.getOp2());

		outBinopExpr(be);
	}

	public void inUnopExpr(UnopExpr ue) {
		 
			logger.trace("inUnopExpr");
	}

	public void outUnopExpr(UnopExpr ue) {
		 
			logger.trace("outUnopExpr");
	}

	public void caseUnopExpr(UnopExpr ue) {
		inUnopExpr(ue);

		caseExprOrRefValueBox(ue.getOpBox());
		//decideCaseExprOrRef(ue.getOp());

		outUnopExpr(ue);
	}

	public void inNewArrayExpr(NewArrayExpr nae) {
		 
			logger.trace("inNewArrayExpr");
	}

	public void outNewArrayExpr(NewArrayExpr nae) {
		 
			logger.trace("outNewArrayExpr");
	}

	public void caseNewArrayExpr(NewArrayExpr nae) {
		inNewArrayExpr(nae);

		caseExprOrRefValueBox(nae.getSizeBox());
		//decideCaseExprOrRef(nae.getSize());

		outNewArrayExpr(nae);
	}

	public void inNewMultiArrayExpr(NewMultiArrayExpr nmae) {
		 
			logger.trace("inNewMultiArrayExpr");
	}

	public void outNewMultiArrayExpr(NewMultiArrayExpr nmae) {
		 
			logger.trace("outNewMultiArrayExpr");
	}

	public void caseNewMultiArrayExpr(NewMultiArrayExpr nmae) {
		inNewMultiArrayExpr(nmae);

		for (int i = 0; i < nmae.getSizeCount(); i++){
			caseExprOrRefValueBox(nmae.getSizeBox(i));
			//decideCaseExprOrRef(nmae.getSize(i));
		}
		outNewMultiArrayExpr(nmae);
	}

	public void inInstanceOfExpr(InstanceOfExpr ioe) {
		 
			logger.trace("inInstanceOfExpr");
	}

	public void outInstanceOfExpr(InstanceOfExpr ioe) {
		 
			logger.trace("outInstanceOfExpr");
	}

	public void caseInstanceOfExpr(InstanceOfExpr ioe) {
		inInstanceOfExpr(ioe);

		caseExprOrRefValueBox(ioe.getOpBox());
		//decideCaseExprOrRef(ioe.getOp());

		outInstanceOfExpr(ioe);
	}

	public void inInvokeExpr(InvokeExpr ie) {
		 
			logger.trace("inInvokeExpr");
	}

	public void outInvokeExpr(InvokeExpr ie) {
		 
			logger.trace("outInvokeExpr");
	}

	public void caseInvokeExpr(InvokeExpr ie) {
		inInvokeExpr(ie);

		for (int i = 0; i < ie.getArgCount(); i++){
			caseExprOrRefValueBox(ie.getArgBox(i));
			//decideCaseExprOrRef(ie.getArg(i));
		}
		if (ie instanceof InstanceInvokeExpr)
			caseInstanceInvokeExpr((InstanceInvokeExpr) ie);

		outInvokeExpr(ie);
	}

	public void inInstanceInvokeExpr(InstanceInvokeExpr iie) {
		 
			logger.trace("inInstanceInvokeExpr");
	}

	public void outInstanceInvokeExpr(InstanceInvokeExpr iie) {
		 
			logger.trace("outInstanceInvokeExpr");
	}

	public void caseInstanceInvokeExpr(InstanceInvokeExpr iie) {
		inInstanceInvokeExpr(iie);

		caseExprOrRefValueBox(iie.getBaseBox());
		//decideCaseExprOrRef(iie.getBase());

		outInstanceInvokeExpr(iie);
	}

	public void inCastExpr(CastExpr ce) {
		 
			logger.trace("inCastExpr");
	}

	public void outCastExpr(CastExpr ce) {
		 
			logger.trace("outCastExpr");
	}

	public void caseCastExpr(CastExpr ce) {
		inCastExpr(ce);
		Type type = ce.getCastType();
		caseType(type);

		caseExprOrRefValueBox(ce.getOpBox());
		//Value op = ce.getOp();
		//decideCaseExprOrRef(op);

		outCastExpr(ce);
	}

	public void decideCaseRef(Ref r) {
		if (r instanceof ArrayRef)
			caseArrayRef((ArrayRef) r);
		else if (r instanceof InstanceFieldRef)
			caseInstanceFieldRef((InstanceFieldRef) r);
		else if (r instanceof StaticFieldRef)
			caseStaticFieldRef((StaticFieldRef) r);
	}

	public void inArrayRef(ArrayRef ar) {
		 
			logger.trace("inArrayRef");
	}

	public void outArrayRef(ArrayRef ar) {
		 
			logger.trace("outArrayRef");
	}

	public void caseArrayRef(ArrayRef ar) {
		inArrayRef(ar);
		caseExprOrRefValueBox(ar.getBaseBox());
		caseExprOrRefValueBox(ar.getIndexBox());
		//decideCaseExprOrRef(ar.getBase());
		//decideCaseExprOrRef(ar.getIndex());
		outArrayRef(ar);
	}

	public void inInstanceFieldRef(InstanceFieldRef ifr) {
		  {
			logger.trace("inInstanceFieldRef");

			if (ifr instanceof DInstanceFieldRef) {
				logger.trace("...........DINSTANCEFIELDREF");

			}
		}
	}

	public void outInstanceFieldRef(InstanceFieldRef ifr) {
		 
			logger.trace("outInstanceFieldRef");
	}

	public void caseInstanceFieldRef(InstanceFieldRef ifr) {
		inInstanceFieldRef(ifr);
		caseExprOrRefValueBox(ifr.getBaseBox());
		//decideCaseExprOrRef(ifr.getBase());
		outInstanceFieldRef(ifr);
	}

	public void inStaticFieldRef(StaticFieldRef sfr) {
		 
			logger.trace("inStaticFieldRef");
	}

	public void outStaticFieldRef(StaticFieldRef sfr) {
		 
			logger.trace("outStaticFieldRef");
	}

	public void caseStaticFieldRef(StaticFieldRef sfr) {
		inStaticFieldRef(sfr);

		outStaticFieldRef(sfr);
	}

	public void debug(String className, String methodName, String debug) {
			logger.debug("Analysis" + className + "..Method:"
					+ methodName + "    DEBUG: " + debug);
	}

}
