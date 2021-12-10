package soot.jimple.toolkits.annotation.nullcheck;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Janus
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

import soot.EquivalentValue;

/**
 * @deprecated only used by deprecated type {@link BranchedRefVarsAnalysis}; flagged for future deletion
 */
@Deprecated
public class RefIntPair {

  private final EquivalentValue _ref;
  private final int _val;

  // constructor is not public so that people go throught the ref pair constants factory on the analysis
  RefIntPair(EquivalentValue r, int v, BranchedRefVarsAnalysis brva) {
    this._ref = r;
    this._val = v;
  }

  public EquivalentValue ref() {
    return this._ref;
  }

  public int val() {
    return this._val;
  }

  @Override
  public String toString() {
    String prefix = "(" + _ref + ", ";
    switch (_val) {
      case BranchedRefVarsAnalysis.kNull:
        return prefix + "null)";
      case BranchedRefVarsAnalysis.kNonNull:
        return prefix + "non-null)";
      case BranchedRefVarsAnalysis.kTop:
        return prefix + "top)";
      case BranchedRefVarsAnalysis.kBottom:
        return prefix + "bottom)";
      default:
        return prefix + _val + ")";
    }
  }
}
