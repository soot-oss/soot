package soot.jimple.spark.ondemand.pautil;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2007 Manu Sridharan
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

import java.util.HashSet;
import java.util.Set;

import soot.AnySubType;
import soot.ArrayType;
import soot.RefType;
import soot.Scene;
import soot.SootMethod;
import soot.Type;
import soot.jimple.spark.internal.TypeManager;
import soot.jimple.spark.ondemand.genericutil.ImmutableStack;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.pag.VarNode;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.jimple.toolkits.callgraph.VirtualCalls;
import soot.util.NumberedString;

/**
 * Interface for handler for when an allocation site is encountered in a pointer analysis query.
 * 
 * @author manu
 * 
 * 
 */
public interface AllocationSiteHandler {

  /**
   * handle a particular allocation site
   * 
   * @param allocNode
   *          the abstract location node
   * @param callStack
   *          for context-sensitive analysis, the call site; might be null
   * @return true if analysis should be terminated; false otherwise
   */
  public boolean handleAllocationSite(AllocNode allocNode, ImmutableStack<Integer> callStack);

  public void resetState();

  public static class PointsToSetHandler implements AllocationSiteHandler {

    private PointsToSetInternal p2set;

    /*
     * (non-Javadoc)
     * 
     * @see AAA.algs.AllocationSiteHandler#handleAllocationSite(soot.jimple.spark.pag.AllocNode, java.lang.Integer)
     */
    public boolean handleAllocationSite(AllocNode allocNode, ImmutableStack<Integer> callStack) {
      p2set.add(allocNode);
      return false;
    }

    public PointsToSetInternal getP2set() {
      return p2set;
    }

    public void setP2set(PointsToSetInternal p2set) {
      this.p2set = p2set;
    }

    public void resetState() {
      // TODO support this
      throw new RuntimeException();
    }

    public boolean shouldHandle(VarNode dst) {
      // TODO Auto-generated method stub
      return false;
    }
  }

  public static class CastCheckHandler implements AllocationSiteHandler {

    private Type type;

    private TypeManager manager;

    private boolean castFailed = false;

    /*
     * (non-Javadoc)
     * 
     * @see AAA.algs.AllocationSiteHandler#handleAllocationSite(soot.jimple.spark.pag.AllocNode, java.lang.Integer)
     */
    public boolean handleAllocationSite(AllocNode allocNode, ImmutableStack<Integer> callStack) {
      castFailed = !manager.castNeverFails(allocNode.getType(), type);
      return castFailed;
    }

    public void setManager(TypeManager manager) {
      this.manager = manager;
    }

    public void setType(Type type) {
      this.type = type;
    }

    public void resetState() {
      throw new RuntimeException();
    }

    public boolean shouldHandle(VarNode dst) {
      // TODO Auto-generated method stub
      P2SetVisitor v = new P2SetVisitor() {

        @Override
        public void visit(Node n) {
          if (!returnValue) {
            returnValue = !manager.castNeverFails(n.getType(), type);
          }
        }

      };
      dst.getP2Set().forall(v);
      return v.getReturnValue();
    }
  }

  public static class VirtualCallHandler implements AllocationSiteHandler {

    public PAG pag;

    public Type receiverType;

    public NumberedString methodStr;

    public Set<SootMethod> possibleMethods = new HashSet<SootMethod>();

    /**
     * @param pag
     * @param receiverType
     * @param methodName
     * @param parameterTypes
     * @param returnType
     */
    public VirtualCallHandler(PAG pag, Type receiverType, NumberedString methodStr) {
      super();
      this.pag = pag;
      this.receiverType = receiverType;
      this.methodStr = methodStr;
    }

    /*
     * (non-Javadoc)
     * 
     * @see AAA.algs.AllocationSiteHandler#handleAllocationSite(soot.jimple.spark.pag.AllocNode, AAA.algs.MethodContext)
     */
    public boolean handleAllocationSite(AllocNode allocNode, ImmutableStack<Integer> callStack) {
      Type type = allocNode.getType();
      if (!pag.getTypeManager().castNeverFails(type, receiverType)) {
        return false;
      }
      if (type instanceof AnySubType) {
        AnySubType any = (AnySubType) type;
        RefType refType = any.getBase();
        if (pag.getTypeManager().getFastHierarchy().canStoreType(receiverType, refType)
            || pag.getTypeManager().getFastHierarchy().canStoreType(refType, receiverType)) {
          return true;
        }
        return false;
      }
      if (type instanceof ArrayType) {
        // we'll invoke the java.lang.Object method in this
        // case
        // Assert.chk(varNodeType.toString().equals("java.lang.Object"));
        type = Scene.v().getSootClass("java.lang.Object").getType();
      }
      RefType refType = (RefType) type;
      SootMethod targetMethod = null;
      targetMethod = VirtualCalls.v().resolveNonSpecial(refType, methodStr);
      if (!possibleMethods.contains(targetMethod)) {
        possibleMethods.add(targetMethod);
        if (possibleMethods.size() > 1) {
          return true;
        }
      }
      return false;
    }

    public void resetState() {
      possibleMethods.clear();
    }

    public boolean shouldHandle(VarNode dst) {
      // TODO Auto-generated method stub
      return false;
    }
  }

  public boolean shouldHandle(VarNode dst);
}
