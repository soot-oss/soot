package soot.jimple.toolkits.callgraph;

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

import soot.Kind;
import soot.SootMethod;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;
import soot.util.NumberedString;

/**
 * Holds relevant information about a particular virtual call site.
 * 
 * @author Ondrej Lhotak
 */
public class VirtualCallSite {
  private InstanceInvokeExpr iie;
  private Stmt stmt;
  private SootMethod container;
  private NumberedString subSig;
  Kind kind;

  public VirtualCallSite(Stmt stmt, SootMethod container, InstanceInvokeExpr iie, NumberedString subSig, Kind kind) {
    this.stmt = stmt;
    this.container = container;
    this.iie = iie;
    this.subSig = subSig;
    this.kind = kind;
  }

  public Stmt stmt() {
    return stmt;
  }

  public SootMethod container() {
    return container;
  }

  public InstanceInvokeExpr iie() {
    return iie;
  }

  public NumberedString subSig() {
    return subSig;
  }

  public Kind kind() {
    return kind;
  }
}
