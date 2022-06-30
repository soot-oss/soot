package soot.dava.internal.AST;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
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

import java.util.ArrayList;
import java.util.List;

import soot.Local;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.dava.internal.SET.SETNodeLabel;
import soot.dava.toolkits.base.AST.analysis.Analysis;
import soot.jimple.Jimple;

public class ASTSynchronizedBlockNode extends ASTLabeledNode {
  private List<Object> body;
  private ValueBox localBox;

  public ASTSynchronizedBlockNode(SETNodeLabel label, List<Object> body, Value local) {
    super(label);
    this.body = body;
    this.localBox = Jimple.v().newLocalBox(local);

    subBodies.add(body);
  }

  /*
   * Nomair A Naeem 21-FEB-2005 Used by UselessLabeledBlockRemove to update a body
   */
  public void replaceBody(List<Object> body) {
    this.body = body;
    subBodies = new ArrayList<Object>();
    subBodies.add(body);
  }

  public int size() {
    return body.size();
  }

  public Local getLocal() {
    return (Local) localBox.getValue();
  }

  public void setLocal(Local local) {
    this.localBox = Jimple.v().newLocalBox(local);
  }

  public Object clone() {
    return new ASTSynchronizedBlockNode(get_Label(), body, getLocal());
  }

  public void toString(UnitPrinter up) {
    label_toString(up);

    /*
     * up.literal( "synchronized" ); up.literal( " " ); up.literal( "(" );
     */
    up.literal("synchronized (");
    localBox.toString(up);
    up.literal(")");
    up.newline();

    up.literal("{");
    up.newline();

    up.incIndent();
    body_toString(up, body);
    up.decIndent();

    up.literal("}");
    up.newline();
  }

  public String toString() {
    StringBuffer b = new StringBuffer();

    b.append(label_toString());

    b.append("synchronized (");
    b.append(getLocal());
    b.append(")");
    b.append(NEWLINE);

    b.append("{");
    b.append(NEWLINE);

    b.append(body_toString(body));

    b.append("}");
    b.append(NEWLINE);

    return b.toString();
  }

  /*
   * Nomair A. Naeem, 7-FEB-05 Part of Visitor Design Implementation for AST See: soot.dava.toolkits.base.AST.analysis For
   * details
   */
  public void apply(Analysis a) {
    a.caseASTSynchronizedBlockNode(this);
  }
}
