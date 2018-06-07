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

import soot.jimple.spark.pag.VarNode;

/**
 * @author manu
 */
public final class AssignEdge {

  private static final int PARAM_MASK = 0x00000001;

  private static final int RETURN_MASK = 0x00000002;

  private static final int CALL_MASK = PARAM_MASK | RETURN_MASK;

  private Integer callSite = null;

  private final VarNode src;

  private int scratch;

  private final VarNode dst;

  /**
   * @param from
   * @param to
   */
  public AssignEdge(final VarNode from, final VarNode to) {
    this.src = from;
    this.dst = to;
  }

  public boolean isParamEdge() {
    return (scratch & PARAM_MASK) != 0;
  }

  public void setParamEdge() {
    scratch |= PARAM_MASK;
  }

  public boolean isReturnEdge() {
    return (scratch & RETURN_MASK) != 0;
  }

  public void setReturnEdge() {
    scratch |= RETURN_MASK;
  }

  public boolean isCallEdge() {
    return (scratch & CALL_MASK) != 0;
  }

  public void clearCallEdge() {
    scratch = 0;
  }

  /**
   * @return
   */
  public Integer getCallSite() {
    assert callSite != null : this + " is not a call edge!";
    return callSite;
  }

  /**
   * @param i
   */
  public void setCallSite(Integer i) {
    callSite = i;
  }

  public String toString() {
    String ret = src + " -> " + dst;
    if (isReturnEdge()) {
      ret += "(* return" + callSite + " *)";
    } else if (isParamEdge()) {
      ret += "(* param" + callSite + " *)";

    }
    return ret;
  }

  public VarNode getSrc() {
    return src;
  }

  public VarNode getDst() {
    return dst;
  }
}
