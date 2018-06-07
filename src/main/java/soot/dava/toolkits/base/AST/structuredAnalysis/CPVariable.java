package soot.dava.toolkits.base.AST.structuredAnalysis;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import soot.Local;
import soot.PrimType;
import soot.SootField;
import soot.dava.DavaFlowAnalysisException;

/*
 * Needed since we want to track locals and SootFields (not FieldRefs)
 */
public class CPVariable {

  private Local local;
  private SootField field;

  public CPVariable(SootField field) {
    this.field = field;
    this.local = null;

    if (!(field.getType() instanceof PrimType)) {
      throw new DavaFlowAnalysisException("Variables managed for CP should only be primitives");
    }
  }

  public CPVariable(Local local) {
    this.field = null;
    this.local = local;

    if (!(local.getType() instanceof PrimType)) {
      throw new DavaFlowAnalysisException("Variables managed for CP should only be primitives");
    }

  }

  public boolean containsLocal() {
    return (local != null);
  }

  public boolean containsSootField() {
    return (field != null);
  }

  public SootField getSootField() {
    if (containsSootField()) {
      return field;
    } else {
      throw new DavaFlowAnalysisException("getsootField invoked when variable is not a sootfield!!!");
    }
  }

  public Local getLocal() {
    if (containsLocal()) {
      return local;
    } else {
      throw new DavaFlowAnalysisException("getLocal invoked when variable is not a local");
    }
  }

  /*
   * VERY IMPORTANT METHOD: invoked from ConstantPropagationTuple equals method which is invoked from the main merge
   * intersection method of CPFlowSet
   */
  public boolean equals(CPVariable var) {
    // check they have the same type Local or SootField
    if (this.containsLocal() && var.containsLocal()) {
      // both locals and same name
      if (this.getLocal().getName().equals(var.getLocal().getName())) {
        return true;
      }
    }
    if (this.containsSootField() && var.containsSootField()) {
      // both SootFields check they have same name
      if (this.getSootField().getName().equals(var.getSootField().getName())) {
        return true;
      }
    }

    return false;
  }

  public String toString() {
    if (containsLocal()) {
      return "Local: " + getLocal().getName();
    } else if (containsSootField()) {
      return "SootField: " + getSootField().getName();
    } else {
      return "UNKNOWN CONSTANT_PROPAGATION_VARIABLE";
    }
  }
}
