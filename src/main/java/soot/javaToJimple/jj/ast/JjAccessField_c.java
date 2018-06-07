package soot.javaToJimple.jj.ast;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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

import java.util.List;

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Expr_c;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;

public class JjAccessField_c extends Expr_c implements Expr {

  private Call getMeth;
  private Call setMeth;
  private Field field;

  public JjAccessField_c(Position pos, Call getMeth, Call setMeth, Field field) {
    super(pos);
    this.getMeth = getMeth;
    this.setMeth = setMeth;
    this.field = field;
  }

  public Call getMeth() {
    return getMeth;
  }

  public Call setMeth() {
    return setMeth;
  }

  public Field field() {
    return field;
  }

  public String toString() {
    return field + " " + getMeth + " " + setMeth;
  }

  public List acceptCFG(CFGBuilder v, List succs) {
    return succs;
  }

  public Term entry() {
    return field.entry();
  }

  public Node visitChildren(NodeVisitor v) {
    visitChild(field, v);
    visitChild(getMeth, v);
    visitChild(setMeth, v);
    return this;
  }
}
