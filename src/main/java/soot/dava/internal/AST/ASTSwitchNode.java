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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.dava.internal.SET.SETNodeLabel;
import soot.dava.toolkits.base.AST.ASTAnalysis;
import soot.dava.toolkits.base.AST.ASTWalker;
import soot.dava.toolkits.base.AST.TryContentsFinder;
import soot.dava.toolkits.base.AST.analysis.Analysis;
import soot.jimple.Jimple;

public class ASTSwitchNode extends ASTLabeledNode {
  private ValueBox keyBox;
  private List<Object> indexList;
  private Map<Object, List<Object>> index2BodyList;

  public ASTSwitchNode(SETNodeLabel label, Value key, List<Object> indexList, Map<Object, List<Object>> index2BodyList) {
    super(label);

    this.keyBox = Jimple.v().newRValueBox(key);
    this.indexList = indexList;
    this.index2BodyList = index2BodyList;

    Iterator<Object> it = indexList.iterator();
    while (it.hasNext()) {
      List body = index2BodyList.get(it.next());

      if (body != null) {
        subBodies.add(body);
      }
    }
  }

  /*
   * Nomair A. Naeem 22-FEB-2005 Added for ASTCleaner
   */
  public List<Object> getIndexList() {
    return indexList;
  }

  public Map<Object, List<Object>> getIndex2BodyList() {
    return index2BodyList;
  }

  public void replaceIndex2BodyList(Map<Object, List<Object>> index2BodyList) {
    this.index2BodyList = index2BodyList;

    subBodies = new ArrayList<Object>();
    Iterator<Object> it = indexList.iterator();
    while (it.hasNext()) {
      List body = index2BodyList.get(it.next());

      if (body != null) {
        subBodies.add(body);
      }
    }
  }

  public ValueBox getKeyBox() {
    return keyBox;
  }

  public Value get_Key() {
    return keyBox.getValue();
  }

  public void set_Key(Value key) {
    this.keyBox = Jimple.v().newRValueBox(key);
  }

  public Object clone() {
    return new ASTSwitchNode(get_Label(), get_Key(), indexList, index2BodyList);
  }

  public void perform_Analysis(ASTAnalysis a) {
    ASTWalker.v().walk_value(a, get_Key());

    if (a instanceof TryContentsFinder) {
      TryContentsFinder.v().add_ExceptionSet(this, TryContentsFinder.v().remove_CurExceptionSet());
    }

    perform_AnalysisOnSubBodies(a);
  }

  public void toString(UnitPrinter up) {
    label_toString(up);

    up.literal("switch");
    up.literal(" ");
    up.literal("(");
    keyBox.toString(up);
    up.literal(")");
    up.newline();

    up.literal("{");
    up.newline();

    Iterator<Object> it = indexList.iterator();
    while (it.hasNext()) {

      Object index = it.next();

      up.incIndent();

      if (index instanceof String) {
        up.literal("default");
      } else {
        up.literal("case");
        up.literal(" ");
        up.literal(index.toString());
      }

      up.literal(":");
      up.newline();

      List<Object> subBody = index2BodyList.get(index);

      if (subBody != null) {
        up.incIndent();
        body_toString(up, subBody);

        if (it.hasNext()) {
          up.newline();
        }
        up.decIndent();
      }
      up.decIndent();
    }

    up.literal("}");
    up.newline();
  }

  public String toString() {
    StringBuffer b = new StringBuffer();

    b.append(label_toString());

    b.append("switch (");
    b.append(get_Key());
    b.append(")");
    b.append(NEWLINE);

    b.append("{");
    b.append(NEWLINE);

    Iterator<Object> it = indexList.iterator();
    while (it.hasNext()) {

      Object index = it.next();

      b.append(TAB);

      if (index instanceof String) {
        b.append("default");
      } else {
        b.append("case ");
        b.append(((Integer) index).toString());
      }

      b.append(":");
      b.append(NEWLINE);

      List<Object> subBody = index2BodyList.get(index);

      if (subBody != null) {
        b.append(body_toString(subBody));

        if (it.hasNext()) {
          b.append(NEWLINE);
        }
      }
    }

    b.append("}");
    b.append(NEWLINE);

    return b.toString();
  }

  /*
   * Nomair A. Naeem, 7-FEB-05 Part of Visitor Design Implementation for AST See: soot.dava.toolkits.base.AST.analysis For
   * details
   */
  public void apply(Analysis a) {
    a.caseASTSwitchNode(this);
  }
}
