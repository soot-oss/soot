package soot.dava.toolkits.base.AST.analysis;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Nomair A. Naeem
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Immediate;
import soot.Local;
import soot.SootClass;
import soot.Type;
import soot.Value;
import soot.ValueBox;
import soot.dava.internal.AST.ASTAndCondition;
import soot.dava.internal.AST.ASTBinaryCondition;
import soot.dava.internal.AST.ASTCondition;
import soot.dava.internal.AST.ASTDoWhileNode;
import soot.dava.internal.AST.ASTForLoopNode;
import soot.dava.internal.AST.ASTIfElseNode;
import soot.dava.internal.AST.ASTIfNode;
import soot.dava.internal.AST.ASTLabeledBlockNode;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTOrCondition;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.AST.ASTSwitchNode;
import soot.dava.internal.AST.ASTSynchronizedBlockNode;
import soot.dava.internal.AST.ASTTryNode;
import soot.dava.internal.AST.ASTUnaryCondition;
import soot.dava.internal.AST.ASTUnconditionalLoopNode;
import soot.dava.internal.AST.ASTWhileNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.javaRep.DInstanceFieldRef;
import soot.dava.internal.javaRep.DThisRef;
import soot.dava.internal.javaRep.DVariableDeclarationStmt;
import soot.jimple.ArrayRef;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.ConditionExpr;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.Expr;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.Ref;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;
import soot.jimple.UnopExpr;

/*
 * CHANGE LOG: 18th MArch 2006: Need a reference to the ValueBox holding a BinOp for SimplifyExpressions
 *              Need to create a level of indirection i.e. instead of retrieving Values e.g. from stmts retrieve the valueBox
 *              and then apply on the value inside the valuebox
 */
public class DepthFirstAdapter extends AnalysisAdapter {

  public boolean DEBUG = false;

  boolean verbose = false;

  public DepthFirstAdapter() {
  }

  public DepthFirstAdapter(boolean verbose) {
    this.verbose = verbose;
  }

  public void inASTMethodNode(ASTMethodNode node) {
    if (verbose) {
      System.out.println("inASTMethodNode");
    }
  }

  public void outASTMethodNode(ASTMethodNode node) {
    if (verbose) {
      System.out.println("outASTMethodNode");
    }
  }

  public void caseASTMethodNode(ASTMethodNode node) {
    inASTMethodNode(node);
    normalRetrieving(node);
    outASTMethodNode(node);
  }

  public void inASTSynchronizedBlockNode(ASTSynchronizedBlockNode node) {
    if (verbose) {
      System.out.println("inASTSynchronizedBlockNode");
    }
  }

  public void outASTSynchronizedBlockNode(ASTSynchronizedBlockNode node) {
    if (verbose) {
      System.out.println("outASTSynchronizedBlockNode");
    }
  }

  public void caseASTSynchronizedBlockNode(ASTSynchronizedBlockNode node) {
    inASTSynchronizedBlockNode(node);

    /*
     * apply on the local on which synchronization is done MArch 18th, 2006: since getLocal returns a local always dont need
     * a valuebox for this
     */
    Value local = node.getLocal();

    decideCaseExprOrRef(local);

    /*
     * apply on the body of the synch block
     */
    normalRetrieving(node);

    outASTSynchronizedBlockNode(node);
  }

  public void inASTLabeledBlockNode(ASTLabeledBlockNode node) {
    if (verbose) {
      System.out.println("inASTLabeledBlockNode");
    }
  }

  public void outASTLabeledBlockNode(ASTLabeledBlockNode node) {
    if (verbose) {
      System.out.println("outASTLabeledBlockNode");
    }
  }

  public void caseASTLabeledBlockNode(ASTLabeledBlockNode node) {
    inASTLabeledBlockNode(node);
    normalRetrieving(node);
    outASTLabeledBlockNode(node);
  }

  public void inASTUnconditionalLoopNode(ASTUnconditionalLoopNode node) {
    if (verbose) {
      System.out.println("inASTUnconditionalWhileNode");
    }
  }

  public void outASTUnconditionalLoopNode(ASTUnconditionalLoopNode node) {
    if (verbose) {
      System.out.println("outASTUnconditionalWhileNode");
    }
  }

  public void caseASTUnconditionalLoopNode(ASTUnconditionalLoopNode node) {
    inASTUnconditionalLoopNode(node);
    normalRetrieving(node);
    outASTUnconditionalLoopNode(node);
  }

  public void inASTSwitchNode(ASTSwitchNode node) {
    if (verbose) {
      System.out.println("inASTSwitchNode");
    }
  }

  public void outASTSwitchNode(ASTSwitchNode node) {
    if (verbose) {
      System.out.println("outASTSwitchNode");
    }
  }

  public void caseASTSwitchNode(ASTSwitchNode node) {
    inASTSwitchNode(node);

    /*
     * apply on key of switchStatement
     */
    /*
     * March 18th 2006, added level of indirection to have access to value box Value key = node.get_Key();
     * decideCaseExprOrRef(key);
     */
    caseExprOrRefValueBox(node.getKeyBox());

    /*
     * Apply on bodies of switch cases
     */
    normalRetrieving(node);
    outASTSwitchNode(node);
  }

  public void inASTIfNode(ASTIfNode node) {
    if (verbose) {
      System.out.println("inASTIfNode");
    }
  }

  public void outASTIfNode(ASTIfNode node) {
    if (verbose) {
      System.out.println("outASTIfNode");
    }
  }

  public void caseASTIfNode(ASTIfNode node) {
    inASTIfNode(node);

    /*
     * apply on the ASTCondition
     */
    ASTCondition condition = node.get_Condition();
    condition.apply(this);

    /*
     * Apply on the if body
     */
    normalRetrieving(node);
    outASTIfNode(node);
  }

  public void inASTIfElseNode(ASTIfElseNode node) {
    if (verbose) {
      System.out.println("inASTIfElseNode");
    }
  }

  public void outASTIfElseNode(ASTIfElseNode node) {
    if (verbose) {
      System.out.println("outASTIfElseNode");
    }
  }

  public void caseASTIfElseNode(ASTIfElseNode node) {
    inASTIfElseNode(node);

    /*
     * apply on the ASTCondition
     */
    ASTCondition condition = node.get_Condition();
    condition.apply(this);

    /*
     * Apply on the if body followed by the else body
     */
    normalRetrieving(node);

    outASTIfElseNode(node);
  }

  public void inASTWhileNode(ASTWhileNode node) {
    if (verbose) {
      System.out.println("inASTWhileNode");
    }
  }

  public void outASTWhileNode(ASTWhileNode node) {
    if (verbose) {
      System.out.println("outASTWhileNode");
    }
  }

  public void caseASTWhileNode(ASTWhileNode node) {
    inASTWhileNode(node);

    /*
     * apply on the ASTCondition
     */
    ASTCondition condition = node.get_Condition();
    condition.apply(this);

    /*
     * Apply on the while body
     */
    normalRetrieving(node);
    outASTWhileNode(node);
  }

  public void inASTForLoopNode(ASTForLoopNode node) {
    if (verbose) {
      System.out.println("inASTForLoopNode");
    }
  }

  public void outASTForLoopNode(ASTForLoopNode node) {
    if (verbose) {
      System.out.println("outASTForLoopNode");
    }
  }

  public void caseASTForLoopNode(ASTForLoopNode node) {
    inASTForLoopNode(node);

    /*
     * Apply on init
     */
    for (AugmentedStmt as : node.getInit()) {
      Stmt s = as.get_Stmt();
      if (s instanceof DefinitionStmt) {
        caseDefinitionStmt((DefinitionStmt) s);
      } else if (s instanceof ReturnStmt) {
        caseReturnStmt((ReturnStmt) s);
      } else if (s instanceof InvokeStmt) {
        caseInvokeStmt((InvokeStmt) s);
      } else if (s instanceof ThrowStmt) {
        caseThrowStmt((ThrowStmt) s);
      } else {
        caseStmt(s);
      }
    }

    /*
     * apply on the ASTCondition
     */
    ASTCondition condition = node.get_Condition();
    condition.apply(this);

    /*
     * Apply on update
     */
    for (AugmentedStmt as : node.getUpdate()) {
      Stmt s = as.get_Stmt();

      if (s instanceof DefinitionStmt) {
        caseDefinitionStmt((DefinitionStmt) s);
      } else if (s instanceof ReturnStmt) {
        caseReturnStmt((ReturnStmt) s);
      } else if (s instanceof InvokeStmt) {
        caseInvokeStmt((InvokeStmt) s);
      } else if (s instanceof ThrowStmt) {
        caseThrowStmt((ThrowStmt) s);
      } else {
        caseStmt(s);
      }
    }

    /*
     * Apply on the for body
     */
    normalRetrieving(node);
    outASTForLoopNode(node);
  }

  public void inASTDoWhileNode(ASTDoWhileNode node) {
    if (verbose) {
      System.out.println("inASTDoWhileNode");
    }
  }

  public void outASTDoWhileNode(ASTDoWhileNode node) {
    if (verbose) {
      System.out.println("outASTDoWhileNode");
    }
  }

  public void caseASTDoWhileNode(ASTDoWhileNode node) {
    inASTDoWhileNode(node);

    /*
     * apply on the ASTCondition
     */
    ASTCondition condition = node.get_Condition();
    condition.apply(this);

    /*
     * Apply on the while body
     */
    normalRetrieving(node);
    outASTDoWhileNode(node);
  }

  public void inASTTryNode(ASTTryNode node) {
    if (verbose) {
      System.out.println("inASTTryNode");
    }
  }

  public void outASTTryNode(ASTTryNode node) {
    if (verbose) {
      System.out.println("outASTTryNode");
    }
  }

  public void caseASTTryNode(ASTTryNode node) {
    inASTTryNode(node);

    // get try body
    List<Object> tryBody = node.get_TryBody();
    Iterator<Object> it = tryBody.iterator();

    // go over the ASTNodes in this tryBody and apply
    while (it.hasNext()) {
      ((ASTNode) it.next()).apply(this);
    }

    Map<Object, Object> exceptionMap = node.get_ExceptionMap();
    Map<Object, Object> paramMap = node.get_ParamMap();
    // get catch list and apply on the following
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

      // apply on type of exception
      caseType(type);

      // apply on local of exception
      Local local = (Local) paramMap.get(catchBody);
      /*
       * March 18th, 2006, Since these are always locals we dont have access to ValueBox
       */
      decideCaseExprOrRef(local);

      // apply on catchBody
      List body = (List) catchBody.o;
      itBody = body.iterator();
      while (itBody.hasNext()) {
        ((ASTNode) itBody.next()).apply(this);
      }
    }
    outASTTryNode(node);
  }

  public void inASTUnaryCondition(ASTUnaryCondition uc) {
    if (verbose) {
      System.out.println("inASTUnaryCondition");
    }
  }

  public void outASTUnaryCondition(ASTUnaryCondition uc) {
    if (verbose) {
      System.out.println("outASTUnaryCondition");
    }
  }

  public void caseASTUnaryCondition(ASTUnaryCondition uc) {
    inASTUnaryCondition(uc);
    // apply on the value
    decideCaseExprOrRef(uc.getValue());
    outASTUnaryCondition(uc);
  }

  public void inASTBinaryCondition(ASTBinaryCondition bc) {
    if (verbose) {
      System.out.println("inASTBinaryCondition");
    }
  }

  public void outASTBinaryCondition(ASTBinaryCondition bc) {
    if (verbose) {
      System.out.println("outASTBinaryCondition");
    }
  }

  public void caseASTBinaryCondition(ASTBinaryCondition bc) {
    inASTBinaryCondition(bc);

    ConditionExpr condition = bc.getConditionExpr();
    // calling decideCaseExprOrRef although we know for sure this is an Expr but doesnt matter

    decideCaseExprOrRef(condition);

    outASTBinaryCondition(bc);
  }

  public void inASTAndCondition(ASTAndCondition ac) {
    if (verbose) {
      System.out.println("inASTAndCondition");
    }
  }

  public void outASTAndCondition(ASTAndCondition ac) {
    if (verbose) {
      System.out.println("outASTAndCondition");
    }
  }

  public void caseASTAndCondition(ASTAndCondition ac) {
    inASTAndCondition(ac);

    ((ac.getLeftOp())).apply(this);
    ((ac.getRightOp())).apply(this);

    outASTAndCondition(ac);
  }

  public void inASTOrCondition(ASTOrCondition oc) {
    if (verbose) {
      System.out.println("inASTOrCondition");
    }
  }

  public void outASTOrCondition(ASTOrCondition oc) {
    if (verbose) {
      System.out.println("outASTOrCondition");
    }
  }

  public void caseASTOrCondition(ASTOrCondition oc) {
    inASTOrCondition(oc);

    ((oc.getLeftOp())).apply(this);
    ((oc.getRightOp())).apply(this);

    outASTOrCondition(oc);
  }

  public void inType(Type t) {
    if (verbose) {
      System.out.println("inType");
    }
  }

  public void outType(Type t) {
    if (verbose) {
      System.out.println("outType");
    }
  }

  public void caseType(Type t) {
    inType(t);
    outType(t);
  }

  public void normalRetrieving(ASTNode node) {
    // from the Node get the subBodes
    Iterator<Object> sbit = node.get_SubBodies().iterator();
    while (sbit.hasNext()) {
      Object subBody = sbit.next();
      Iterator it = ((List) subBody).iterator();

      // go over the ASTNodes in this subBody and apply
      while (it.hasNext()) {
        ASTNode temp = (ASTNode) it.next();

        temp.apply(this);
      }
    } // end of going over subBodies
  }

  public void inASTStatementSequenceNode(ASTStatementSequenceNode node) {
    if (verbose) {
      System.out.println("inASTStatementSequenceNode");
    }
  }

  public void outASTStatementSequenceNode(ASTStatementSequenceNode node) {
    if (verbose) {
      System.out.println("outASTStatementSequenceNode");
    }
  }

  public void caseASTStatementSequenceNode(ASTStatementSequenceNode node) {
    inASTStatementSequenceNode(node);
    for (AugmentedStmt as : node.getStatements()) {
      Stmt s = as.get_Stmt();
      /*
       * Do a case by case check of possible statements and invoke the case methods from within this method.
       *
       * cant use apply since the Statements are defined in some other package and dont want to change code all over the
       * place
       */

      if (s instanceof DefinitionStmt) {
        caseDefinitionStmt((DefinitionStmt) s);
      } else if (s instanceof ReturnStmt) {
        caseReturnStmt((ReturnStmt) s);
      } else if (s instanceof InvokeStmt) {
        caseInvokeStmt((InvokeStmt) s);
      } else if (s instanceof ThrowStmt) {
        caseThrowStmt((ThrowStmt) s);
      } else if (s instanceof DVariableDeclarationStmt) {
        caseDVariableDeclarationStmt((DVariableDeclarationStmt) s);
      } else {
        caseStmt(s);
      }

    } // end of while going through the statement sequence
    outASTStatementSequenceNode(node);
  }

  public void inDefinitionStmt(DefinitionStmt s) {
    if (verbose) {
      System.out.println("inDefinitionStmt" + s);
    }
  }

  public void outDefinitionStmt(DefinitionStmt s) {
    if (verbose) {
      System.out.println("outDefinitionStmt");
    }
  }

  public void caseDefinitionStmt(DefinitionStmt s) {
    inDefinitionStmt(s);

    /*
     * March 18th, 2006 introducing level of indirection decideCaseExprOrRef(s.getRightOp());
     * decideCaseExprOrRef(s.getLeftOp());
     */
    caseExprOrRefValueBox(s.getRightOpBox());
    caseExprOrRefValueBox(s.getLeftOpBox());

    outDefinitionStmt(s);
  }

  public void inReturnStmt(ReturnStmt s) {
    if (verbose) {
      System.out.println("inReturnStmt");
      // System.out.println("Return Stmt:"+s);
    }
  }

  public void outReturnStmt(ReturnStmt s) {
    if (verbose) {
      System.out.println("outReturnStmt");
    }
  }

  public void caseReturnStmt(ReturnStmt s) {
    inReturnStmt(s);

    /*
     * MArch 18th 2006 decideCaseExprOrRef(s.getOp());
     */
    caseExprOrRefValueBox(s.getOpBox());

    outReturnStmt(s);
  }

  public void inInvokeStmt(InvokeStmt s) {
    if (verbose) {
      System.out.println("inInvokeStmt");
    }
  }

  public void outInvokeStmt(InvokeStmt s) {
    if (verbose) {
      System.out.println("outInvokeStmt");
    }
  }

  public void caseInvokeStmt(InvokeStmt s) {
    inInvokeStmt(s);

    caseExprOrRefValueBox(s.getInvokeExprBox());
    // decideCaseExprOrRef(s.getInvokeExpr());

    outInvokeStmt(s);
  }

  public void inThrowStmt(ThrowStmt s) {
    if (verbose) {
      System.out.println("\n\ninThrowStmt\n\n");
    }
  }

  public void outThrowStmt(ThrowStmt s) {
    if (verbose) {
      System.out.println("outThrowStmt");
    }
  }

  public void caseThrowStmt(ThrowStmt s) {
    inThrowStmt(s);
    caseExprOrRefValueBox(s.getOpBox());
    // decideCaseExprOrRef(s.getOp());

    outThrowStmt(s);
  }

  public void inDVariableDeclarationStmt(DVariableDeclarationStmt s) {
    if (verbose) {
      System.out.println("\n\ninDVariableDeclarationStmt\n\n" + s);
    }
  }

  public void outDVariableDeclarationStmt(DVariableDeclarationStmt s) {
    if (verbose) {
      System.out.println("outDVariableDeclarationStmt");
    }
  }

  public void caseDVariableDeclarationStmt(DVariableDeclarationStmt s) {
    inDVariableDeclarationStmt(s);

    // a variableDeclarationStmt has a type followed by a list of locals
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
    if (verbose) {
      System.out.println("inStmt: " + s);
    }

    /*
     * if(s instanceof DAbruptStmt) System.out.println("DAbruptStmt: "+s); if(s instanceof ReturnVoidStmt)
     * System.out.println("ReturnVoidStmt: "+s);
     */
  }

  public void outStmt(Stmt s) {
    if (verbose) {
      System.out.println("outStmt");
    }
  }

  public void caseStmt(Stmt s) {
    inStmt(s);
    outStmt(s);
  }

  /*
   * March 18th 2006, Adding new indirection
   */
  public void caseExprOrRefValueBox(ValueBox vb) {
    inExprOrRefValueBox(vb);
    decideCaseExprOrRef(vb.getValue());
    outExprOrRefValueBox(vb);
  }

  public void inExprOrRefValueBox(ValueBox vb) {
    if (verbose) {
      System.out.println("inExprOrRefValueBox" + vb);
    }

  }

  public void outExprOrRefValueBox(ValueBox vb) {
    if (verbose) {
      System.out.println("outExprOrRefValueBox" + vb);
    }

  }

  public void decideCaseExprOrRef(Value v) {
    if (v instanceof Expr) {
      caseExpr((Expr) v);
    } else if (v instanceof Ref) {
      caseRef((Ref) v);
    } else {
      caseValue(v);
    }
  }

  public void inValue(Value v) {
    if (verbose) {
      System.out.println("inValue" + v);

      if (v instanceof DThisRef) {
        System.out.println("DTHISREF.................");
      } else if (v instanceof Immediate) {
        System.out.println("\tIMMEDIATE");
        if (v instanceof soot.jimple.internal.JimpleLocal) {
          System.out.println("\t\tJimpleLocal...................." + v);

        } else if (v instanceof Constant) {
          System.out.println("\t\tconstant....................");
          if (v instanceof IntConstant) {
            System.out.println("\t\t INTconstant....................");
          }
        } else if (v instanceof soot.baf.internal.BafLocal) {
          System.out.println("\t\tBafLocal....................");
        } else {
          System.out.println("\t\telse!!!!!!!!!!!!");
        }
      } else {
        System.out.println("NEITHER................");
      }
    }
  }

  public void outValue(Value v) {
    if (verbose) {
      System.out.println("outValue");
    }
  }

  public void caseValue(Value v) {
    inValue(v);
    outValue(v);
  }

  public void inExpr(Expr e) {
    if (verbose) {
      System.out.println("inExpr");
    }
  }

  public void outExpr(Expr e) {
    if (verbose) {
      System.out.println("outExpr");
    }
  }

  public void caseExpr(Expr e) {
    inExpr(e);
    decideCaseExpr(e);
    outExpr(e);
  }

  public void inRef(Ref r) {
    if (verbose) {
      System.out.println("inRef");
    }
  }

  public void outRef(Ref r) {
    if (verbose) {
      System.out.println("outRef");
    }
  }

  public void caseRef(Ref r) {
    inRef(r);
    decideCaseRef(r);
    outRef(r);
  }

  public void decideCaseExpr(Expr e) {
    if (e instanceof BinopExpr) {
      caseBinopExpr((BinopExpr) e);
    } else if (e instanceof UnopExpr) {
      caseUnopExpr((UnopExpr) e);
    } else if (e instanceof NewArrayExpr) {
      caseNewArrayExpr((NewArrayExpr) e);
    } else if (e instanceof NewMultiArrayExpr) {
      caseNewMultiArrayExpr((NewMultiArrayExpr) e);
    } else if (e instanceof InstanceOfExpr) {
      caseInstanceOfExpr((InstanceOfExpr) e);
    } else if (e instanceof InvokeExpr) {
      caseInvokeExpr((InvokeExpr) e);
    } else if (e instanceof CastExpr) {
      caseCastExpr((CastExpr) e);
    }
  }

  public void inBinopExpr(BinopExpr be) {
    if (verbose) {
      System.out.println("inBinopExpr");
    }
  }

  public void outBinopExpr(BinopExpr be) {
    if (verbose) {
      System.out.println("outBinopExpr");
    }
  }

  public void caseBinopExpr(BinopExpr be) {
    inBinopExpr(be);

    caseExprOrRefValueBox(be.getOp1Box());
    caseExprOrRefValueBox(be.getOp2Box());
    // decideCaseExprOrRef(be.getOp1());
    // decideCaseExprOrRef(be.getOp2());

    outBinopExpr(be);
  }

  public void inUnopExpr(UnopExpr ue) {
    if (verbose) {
      System.out.println("inUnopExpr");
    }
  }

  public void outUnopExpr(UnopExpr ue) {
    if (verbose) {
      System.out.println("outUnopExpr");
    }
  }

  public void caseUnopExpr(UnopExpr ue) {
    inUnopExpr(ue);

    caseExprOrRefValueBox(ue.getOpBox());
    // decideCaseExprOrRef(ue.getOp());

    outUnopExpr(ue);
  }

  public void inNewArrayExpr(NewArrayExpr nae) {
    if (verbose) {
      System.out.println("inNewArrayExpr");
    }
  }

  public void outNewArrayExpr(NewArrayExpr nae) {
    if (verbose) {
      System.out.println("outNewArrayExpr");
    }
  }

  public void caseNewArrayExpr(NewArrayExpr nae) {
    inNewArrayExpr(nae);

    caseExprOrRefValueBox(nae.getSizeBox());
    // decideCaseExprOrRef(nae.getSize());

    outNewArrayExpr(nae);
  }

  public void inNewMultiArrayExpr(NewMultiArrayExpr nmae) {
    if (verbose) {
      System.out.println("inNewMultiArrayExpr");
    }
  }

  public void outNewMultiArrayExpr(NewMultiArrayExpr nmae) {
    if (verbose) {
      System.out.println("outNewMultiArrayExpr");
    }
  }

  public void caseNewMultiArrayExpr(NewMultiArrayExpr nmae) {
    inNewMultiArrayExpr(nmae);

    for (int i = 0; i < nmae.getSizeCount(); i++) {
      caseExprOrRefValueBox(nmae.getSizeBox(i));
      // decideCaseExprOrRef(nmae.getSize(i));
    }
    outNewMultiArrayExpr(nmae);
  }

  public void inInstanceOfExpr(InstanceOfExpr ioe) {
    if (verbose) {
      System.out.println("inInstanceOfExpr");
    }
  }

  public void outInstanceOfExpr(InstanceOfExpr ioe) {
    if (verbose) {
      System.out.println("outInstanceOfExpr");
    }
  }

  public void caseInstanceOfExpr(InstanceOfExpr ioe) {
    inInstanceOfExpr(ioe);

    caseExprOrRefValueBox(ioe.getOpBox());
    // decideCaseExprOrRef(ioe.getOp());

    outInstanceOfExpr(ioe);
  }

  public void inInvokeExpr(InvokeExpr ie) {
    if (verbose) {
      System.out.println("inInvokeExpr");
    }
  }

  public void outInvokeExpr(InvokeExpr ie) {
    if (verbose) {
      System.out.println("outInvokeExpr");
    }
  }

  public void caseInvokeExpr(InvokeExpr ie) {
    inInvokeExpr(ie);

    for (int i = 0; i < ie.getArgCount(); i++) {
      caseExprOrRefValueBox(ie.getArgBox(i));
      // decideCaseExprOrRef(ie.getArg(i));
    }
    if (ie instanceof InstanceInvokeExpr) {
      caseInstanceInvokeExpr((InstanceInvokeExpr) ie);
    }

    outInvokeExpr(ie);
  }

  public void inInstanceInvokeExpr(InstanceInvokeExpr iie) {
    if (verbose) {
      System.out.println("inInstanceInvokeExpr");
    }
  }

  public void outInstanceInvokeExpr(InstanceInvokeExpr iie) {
    if (verbose) {
      System.out.println("outInstanceInvokeExpr");
    }
  }

  public void caseInstanceInvokeExpr(InstanceInvokeExpr iie) {
    inInstanceInvokeExpr(iie);

    caseExprOrRefValueBox(iie.getBaseBox());
    // decideCaseExprOrRef(iie.getBase());

    outInstanceInvokeExpr(iie);
  }

  public void inCastExpr(CastExpr ce) {
    if (verbose) {
      System.out.println("inCastExpr");
    }
  }

  public void outCastExpr(CastExpr ce) {
    if (verbose) {
      System.out.println("outCastExpr");
    }
  }

  public void caseCastExpr(CastExpr ce) {
    inCastExpr(ce);
    Type type = ce.getCastType();
    caseType(type);

    caseExprOrRefValueBox(ce.getOpBox());
    // Value op = ce.getOp();
    // decideCaseExprOrRef(op);

    outCastExpr(ce);
  }

  public void decideCaseRef(Ref r) {
    if (r instanceof ArrayRef) {
      caseArrayRef((ArrayRef) r);
    } else if (r instanceof InstanceFieldRef) {
      caseInstanceFieldRef((InstanceFieldRef) r);
    } else if (r instanceof StaticFieldRef) {
      caseStaticFieldRef((StaticFieldRef) r);
    }
  }

  public void inArrayRef(ArrayRef ar) {
    if (verbose) {
      System.out.println("inArrayRef");
    }
  }

  public void outArrayRef(ArrayRef ar) {
    if (verbose) {
      System.out.println("outArrayRef");
    }
  }

  public void caseArrayRef(ArrayRef ar) {
    inArrayRef(ar);
    caseExprOrRefValueBox(ar.getBaseBox());
    caseExprOrRefValueBox(ar.getIndexBox());
    // decideCaseExprOrRef(ar.getBase());
    // decideCaseExprOrRef(ar.getIndex());
    outArrayRef(ar);
  }

  public void inInstanceFieldRef(InstanceFieldRef ifr) {
    if (verbose) {
      System.out.println("inInstanceFieldRef");

      if (ifr instanceof DInstanceFieldRef) {
        System.out.println("...........DINSTANCEFIELDREF");

      }
    }
  }

  public void outInstanceFieldRef(InstanceFieldRef ifr) {
    if (verbose) {
      System.out.println("outInstanceFieldRef");
    }
  }

  public void caseInstanceFieldRef(InstanceFieldRef ifr) {
    inInstanceFieldRef(ifr);
    caseExprOrRefValueBox(ifr.getBaseBox());
    // decideCaseExprOrRef(ifr.getBase());
    outInstanceFieldRef(ifr);
  }

  public void inStaticFieldRef(StaticFieldRef sfr) {
    if (verbose) {
      System.out.println("inStaticFieldRef");
    }
  }

  public void outStaticFieldRef(StaticFieldRef sfr) {
    if (verbose) {
      System.out.println("outStaticFieldRef");
    }
  }

  public void caseStaticFieldRef(StaticFieldRef sfr) {
    inStaticFieldRef(sfr);

    outStaticFieldRef(sfr);
  }

  public void debug(String className, String methodName, String debug) {
    if (DEBUG) {
      System.out.println("Analysis" + className + "..Method:" + methodName + "    DEBUG: " + debug);
    }
  }

}
