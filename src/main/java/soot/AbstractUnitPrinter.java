package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 - 2004 Ondrej Lhotak
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

import soot.jimple.Constant;
import soot.jimple.IdentityRef;
import soot.jimple.Jimple;

/**
 * Partial default UnitPrinter implementation.
 */
public abstract class AbstractUnitPrinter implements UnitPrinter {
  public void setPositionTagger(AttributesUnitPrinter pt) {
    this.pt = pt;
    pt.setUnitPrinter(this);
  }

  public AttributesUnitPrinter getPositionTagger() {
    return pt;
  }

  public void startUnit(Unit u) {
    handleIndent();
    if (pt != null) {
      pt.startUnit(u);
    }
  }

  public void endUnit(Unit u) {
    if (pt != null) {
      pt.endUnit(u);
    }
  }

  public void startUnitBox(UnitBox ub) {
    handleIndent();
  }

  public void endUnitBox(UnitBox ub) {
  }

  public void startValueBox(ValueBox vb) {
    handleIndent();
    if (pt != null) {
      pt.startValueBox(vb);
    }
  }

  public void endValueBox(ValueBox vb) {
    if (pt != null) {
      pt.endValueBox(vb);
    }
  }

  public void noIndent() {
    startOfLine = false;
  }

  public void incIndent() {
    indent = indent + "    ";
  }

  public void decIndent() {
    if (indent.length() >= 4) {
      indent = indent.substring(4);
    }
  }

  public void setIndent(String indent) {
    this.indent = indent;
  }

  public String getIndent() {
    return indent;
  }

  public abstract void literal(String s);

  public abstract void type(Type t);

  public abstract void methodRef(SootMethodRef m);

  public abstract void fieldRef(SootFieldRef f);

  public abstract void identityRef(IdentityRef r);

  public abstract void unitRef(Unit u, boolean branchTarget);

  public void newline() {
    output.append("\n");
    startOfLine = true;
    if (pt != null) {
      pt.newline();
    }
  }

  public void local(Local l) {
    handleIndent();
    if (quotableLocals == null) {
      initializeQuotableLocals();
    }
    if (quotableLocals.contains(l.getName())) {
      output.append("'" + l.getName() + "'");
    } else {
      output.append(l.getName());
    }
  }

  public void constant(Constant c) {
    handleIndent();
    output.append(c.toString());
  }

  public String toString() {
    String ret = output.toString();
    output = new StringBuffer();
    return ret;
  }

  public StringBuffer output() {
    return output;
  }

  protected void handleIndent() {
    if (startOfLine) {
      output.append(indent);
    }
    startOfLine = false;
  }

  protected void initializeQuotableLocals() {
    quotableLocals = new HashSet<String>();
    quotableLocals.addAll(Jimple.jimpleKeywordList());
  }

  protected boolean startOfLine = true;
  protected String indent = "        ";
  protected StringBuffer output = new StringBuffer();
  protected AttributesUnitPrinter pt;
  protected HashSet<String> quotableLocals;
}
