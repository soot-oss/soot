package soot.jimple.spark.builder;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
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

import soot.ArrayType;
import soot.Local;
import soot.PointsToAnalysis;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Value;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ClassConstant;
import soot.jimple.Expr;
import soot.jimple.IdentityRef;
import soot.jimple.IdentityStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NullConstant;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.ThisRef;
import soot.jimple.ThrowStmt;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.spark.internal.ClientAccessibilityOracle;
import soot.jimple.spark.internal.SparkLibraryHelper;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.ArrayElement;
import soot.jimple.spark.pag.MethodPAG;
import soot.jimple.spark.pag.NewInstanceNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.pag.Parm;
import soot.jimple.spark.pag.VarNode;
import soot.options.CGOptions;
import soot.shimple.AbstractShimpleValueSwitch;
import soot.shimple.PhiExpr;
import soot.toolkits.scalar.Pair;

/**
 * Class implementing builder parameters (this decides what kinds of nodes should be built for each kind of Soot value).
 *
 * @author Ondrej Lhotak
 */
public class MethodNodeFactory extends AbstractShimpleValueSwitch {
  public MethodNodeFactory(PAG pag, MethodPAG mpag) {
    this.pag = pag;
    this.mpag = mpag;
    setCurrentMethod(mpag.getMethod());
  }

  /** Sets the method for which a graph is currently being built. */
  private void setCurrentMethod(SootMethod m) {
    method = m;
    if (!m.isStatic()) {
      SootClass c = m.getDeclaringClass();
      if (c == null) {
        throw new RuntimeException("Method " + m + " has no declaring class");
      }
      caseThis();
    }
    for (int i = 0; i < m.getParameterCount(); i++) {
      if (m.getParameterType(i) instanceof RefLikeType) {
        caseParm(i);
      }
    }
    Type retType = m.getReturnType();
    if (retType instanceof RefLikeType) {
      caseRet();
    }
  }

  public Node getNode(Value v) {
    v.apply(this);
    return getNode();
  }

  /** Adds the edges required for this statement to the graph. */
  final public void handleStmt(Stmt s) {
    // We only consider reflective class creation when it is enabled
    if (s.containsInvokeExpr()) {
      if (!pag.getCGOpts().types_for_invoke()) {
        return;
      }

      InvokeExpr iexpr = s.getInvokeExpr();
      if (iexpr instanceof VirtualInvokeExpr) {
        if (!isReflectionNewInstance(iexpr)) {
          return;
        }
      } else if (!(iexpr instanceof StaticInvokeExpr)) {
        return;
      }
    }

    s.apply(new AbstractStmtSwitch() {
      @Override
      final public void caseAssignStmt(AssignStmt as) {
        Value l = as.getLeftOp();
        Value r = as.getRightOp();
        if (!(l.getType() instanceof RefLikeType)) {
          return;
        }
        assert r.getType() instanceof RefLikeType : "Type mismatch in assignment " + as + " in method "
            + method.getSignature();
        l.apply(MethodNodeFactory.this);
        Node dest = getNode();
        r.apply(MethodNodeFactory.this);
        Node src = getNode();
        if (l instanceof InstanceFieldRef) {
          ((InstanceFieldRef) l).getBase().apply(MethodNodeFactory.this);
          pag.addDereference((VarNode) getNode());
        }
        if (r instanceof InstanceFieldRef) {
          ((InstanceFieldRef) r).getBase().apply(MethodNodeFactory.this);
          pag.addDereference((VarNode) getNode());
        } else if (r instanceof StaticFieldRef) {
          StaticFieldRef sfr = (StaticFieldRef) r;
          SootFieldRef s = sfr.getFieldRef();
          if (pag.getOpts().empties_as_allocs()) {
            if (s.declaringClass().getName().equals("java.util.Collections")) {
              if (s.name().equals("EMPTY_SET")) {
                src = pag.makeAllocNode(RefType.v("java.util.HashSet"), RefType.v("java.util.HashSet"), method);
              } else if (s.name().equals("EMPTY_MAP")) {
                src = pag.makeAllocNode(RefType.v("java.util.HashMap"), RefType.v("java.util.HashMap"), method);
              } else if (s.name().equals("EMPTY_LIST")) {
                src = pag.makeAllocNode(RefType.v("java.util.LinkedList"), RefType.v("java.util.LinkedList"), method);
              }
            } else if (s.declaringClass().getName().equals("java.util.Hashtable")) {
              if (s.name().equals("emptyIterator")) {
                src = pag.makeAllocNode(RefType.v("java.util.Hashtable$EmptyIterator"),
                    RefType.v("java.util.Hashtable$EmptyIterator"), method);
              } else if (s.name().equals("emptyEnumerator")) {
                src = pag.makeAllocNode(RefType.v("java.util.Hashtable$EmptyEnumerator"),
                    RefType.v("java.util.Hashtable$EmptyEnumerator"), method);
              }
            }
          }
        }
        mpag.addInternalEdge(src, dest);
      }

      @Override
      final public void caseReturnStmt(ReturnStmt rs) {
        if (!(rs.getOp().getType() instanceof RefLikeType)) {
          return;
        }
        rs.getOp().apply(MethodNodeFactory.this);
        Node retNode = getNode();
        mpag.addInternalEdge(retNode, caseRet());
      }

      @Override
      final public void caseIdentityStmt(IdentityStmt is) {
        if (!(is.getLeftOp().getType() instanceof RefLikeType)) {
          return;
        }
        Value leftOp = is.getLeftOp();
        Value rightOp = is.getRightOp();
        leftOp.apply(MethodNodeFactory.this);
        Node dest = getNode();
        rightOp.apply(MethodNodeFactory.this);
        Node src = getNode();
        mpag.addInternalEdge(src, dest);

        // in case library mode is activated add allocations to any
        // possible type of this local and
        // parameters of accessible methods
        int libOption = pag.getCGOpts().library();
        if (libOption != CGOptions.library_disabled && (accessibilityOracle.isAccessible(method))) {
          if (rightOp instanceof IdentityRef) {
            Type rt = rightOp.getType();
            rt.apply(new SparkLibraryHelper(pag, src, method));
          }
        }

      }

      @Override
      final public void caseThrowStmt(ThrowStmt ts) {
        ts.getOp().apply(MethodNodeFactory.this);
        mpag.addOutEdge(getNode(), pag.nodeFactory().caseThrow());
      }
    });
  }

  /**
   * Checks whether the given invocation is for Class.newInstance()
   *
   * @param iexpr
   *          The invocation to check
   * @return True if the given invocation is for Class.newInstance(), otherwise false
   */
  private boolean isReflectionNewInstance(InvokeExpr iexpr) {
    if (iexpr instanceof VirtualInvokeExpr) {
      VirtualInvokeExpr vie = (VirtualInvokeExpr) iexpr;
      if (vie.getBase().getType() instanceof RefType) {
        RefType rt = (RefType) vie.getBase().getType();
        if (rt.getSootClass().getName().equals("java.lang.Class")) {
          if (vie.getMethodRef().name().equals("newInstance") && vie.getMethodRef().parameterTypes().size() == 0) {
            return true;
          }
        }
      }
    }
    return false;
  }

  final public Node getNode() {
    return (Node) getResult();
  }

  final public Node caseThis() {
    VarNode ret = pag.makeLocalVarNode(new Pair<SootMethod, String>(method, PointsToAnalysis.THIS_NODE),
        method.getDeclaringClass().getType(), method);
    ret.setInterProcTarget();
    return ret;
  }

  final public Node caseParm(int index) {
    VarNode ret = pag.makeLocalVarNode(new Pair<SootMethod, Integer>(method, new Integer(index)),
        method.getParameterType(index), method);
    ret.setInterProcTarget();
    return ret;
  }

  @Override
  final public void casePhiExpr(PhiExpr e) {
    Pair<Expr, String> phiPair = new Pair<Expr, String>(e, PointsToAnalysis.PHI_NODE);
    Node phiNode = pag.makeLocalVarNode(phiPair, e.getType(), method);
    for (Value op : e.getValues()) {
      op.apply(MethodNodeFactory.this);
      Node opNode = getNode();
      mpag.addInternalEdge(opNode, phiNode);
    }
    setResult(phiNode);
  }

  final public Node caseRet() {
    VarNode ret = pag.makeLocalVarNode(Parm.v(method, PointsToAnalysis.RETURN_NODE), method.getReturnType(), method);
    ret.setInterProcSource();
    return ret;
  }

  final public Node caseArray(VarNode base) {
    return pag.makeFieldRefNode(base, ArrayElement.v());
  }
  /* End of public methods. */
  /* End of package methods. */

  // OK, these ones are public, but they really shouldn't be; it's just
  // that Java requires them to be, because they override those other
  // public methods.
  @Override
  final public void caseArrayRef(ArrayRef ar) {
    caseLocal((Local) ar.getBase());
    setResult(caseArray((VarNode) getNode()));
  }

  @Override
  final public void caseCastExpr(CastExpr ce) {
    Pair<Expr, String> castPair = new Pair<Expr, String>(ce, PointsToAnalysis.CAST_NODE);
    ce.getOp().apply(this);
    Node opNode = getNode();
    Node castNode = pag.makeLocalVarNode(castPair, ce.getCastType(), method);
    mpag.addInternalEdge(opNode, castNode);
    setResult(castNode);
  }

  @Override
  final public void caseCaughtExceptionRef(CaughtExceptionRef cer) {
    setResult(pag.nodeFactory().caseThrow());
  }

  @Override
  final public void caseInstanceFieldRef(InstanceFieldRef ifr) {
    if (pag.getOpts().field_based() || pag.getOpts().vta()) {
      setResult(pag.makeGlobalVarNode(ifr.getField(), ifr.getField().getType()));
    } else {
      setResult(pag.makeLocalFieldRefNode(ifr.getBase(), ifr.getBase().getType(), ifr.getField(), method));
    }
  }

  @Override
  final public void caseLocal(Local l) {
    setResult(pag.makeLocalVarNode(l, l.getType(), method));
  }

  @Override
  final public void caseNewArrayExpr(NewArrayExpr nae) {
    setResult(pag.makeAllocNode(nae, nae.getType(), method));
  }

  private boolean isStringBuffer(Type t) {
    if (!(t instanceof RefType)) {
      return false;
    }
    RefType rt = (RefType) t;
    String s = rt.toString();
    if (s.equals("java.lang.StringBuffer")) {
      return true;
    }
    if (s.equals("java.lang.StringBuilder")) {
      return true;
    }
    return false;
  }

  @Override
  final public void caseNewExpr(NewExpr ne) {
    if (pag.getOpts().merge_stringbuffer() && isStringBuffer(ne.getType())) {
      setResult(pag.makeAllocNode(ne.getType(), ne.getType(), null));
    } else {
      setResult(pag.makeAllocNode(ne, ne.getType(), method));
    }
  }

  @Override
  final public void caseNewMultiArrayExpr(NewMultiArrayExpr nmae) {
    ArrayType type = (ArrayType) nmae.getType();
    AllocNode prevAn = pag.makeAllocNode(new Pair<Expr, Integer>(nmae, new Integer(type.numDimensions)), type, method);
    VarNode prevVn = pag.makeLocalVarNode(prevAn, prevAn.getType(), method);
    mpag.addInternalEdge(prevAn, prevVn);
    setResult(prevAn);
    while (true) {
      Type t = type.getElementType();
      if (!(t instanceof ArrayType)) {
        break;
      }
      type = (ArrayType) t;
      AllocNode an = pag.makeAllocNode(new Pair<Expr, Integer>(nmae, new Integer(type.numDimensions)), type, method);
      VarNode vn = pag.makeLocalVarNode(an, an.getType(), method);
      mpag.addInternalEdge(an, vn);
      mpag.addInternalEdge(vn, pag.makeFieldRefNode(prevVn, ArrayElement.v()));
      prevAn = an;
      prevVn = vn;
    }
  }

  @Override
  final public void caseParameterRef(ParameterRef pr) {
    setResult(caseParm(pr.getIndex()));
  }

  @Override
  final public void caseStaticFieldRef(StaticFieldRef sfr) {
    setResult(pag.makeGlobalVarNode(sfr.getField(), sfr.getField().getType()));
  }

  @Override
  final public void caseStringConstant(StringConstant sc) {
    AllocNode stringConstant;
    if (pag.getOpts().string_constants() || Scene.v().containsClass(sc.value)
        || (sc.value.length() > 0 && sc.value.charAt(0) == '[')) {
      stringConstant = pag.makeStringConstantNode(sc.value);
    } else {
      stringConstant = pag.makeAllocNode(PointsToAnalysis.STRING_NODE, RefType.v("java.lang.String"), null);
    }
    VarNode stringConstantLocal = pag.makeGlobalVarNode(stringConstant, RefType.v("java.lang.String"));
    pag.addEdge(stringConstant, stringConstantLocal);
    setResult(stringConstantLocal);
  }

  @Override
  final public void caseThisRef(ThisRef tr) {
    setResult(caseThis());
  }

  @Override
  final public void caseNullConstant(NullConstant nr) {
    setResult(null);
  }

  @Override
  final public void caseClassConstant(ClassConstant cc) {
    AllocNode classConstant = pag.makeClassConstantNode(cc);
    VarNode classConstantLocal = pag.makeGlobalVarNode(classConstant, RefType.v("java.lang.Class"));
    pag.addEdge(classConstant, classConstantLocal);
    setResult(classConstantLocal);
  }

  @Override
  final public void defaultCase(Object v) {
    throw new RuntimeException("failed to handle " + v);
  }

  @Override
  public void caseStaticInvokeExpr(StaticInvokeExpr v) {
    SootMethodRef ref = v.getMethodRef();
    if (v.getArgCount() == 1 && v.getArg(0) instanceof StringConstant && ref.name().equals("forName")
        && ref.declaringClass().getName().equals("java.lang.Class") && ref.parameterTypes().size() == 1) {
      // This is a call to Class.forName
      StringConstant classNameConst = (StringConstant) v.getArg(0);
      caseClassConstant(ClassConstant.v("L" + classNameConst.value.replaceAll("\\.", "/") + ";"));
    }
  }

  @Override
  public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
    if (isReflectionNewInstance(v)) {
      NewInstanceNode newInstanceNode = pag.makeNewInstanceNode(v, Scene.v().getObjectType(), method);

      v.getBase().apply(this);
      Node srcNode = getNode();
      mpag.addInternalEdge(srcNode, newInstanceNode);

      setResult(newInstanceNode);
    } else {
      throw new RuntimeException("Unhandled case of VirtualInvokeExpr");
    }
  }

  protected final PAG pag;
  protected final MethodPAG mpag;
  protected SootMethod method;
  protected ClientAccessibilityOracle accessibilityOracle = Scene.v().getClientAccessibilityOracle();
}
