package soot.dava.internal.javaRep;

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

import soot.UnitPrinter;
import soot.dava.internal.SET.SETNodeLabel;
import soot.jimple.internal.AbstractStmt;

public class DAbruptStmt extends AbstractStmt {
  private String command;
  private SETNodeLabel label;

  public boolean surpressDestinationLabel;

  public DAbruptStmt(String command, SETNodeLabel label) {
    this.command = command;
    this.label = label;

    label.set_Name();
    surpressDestinationLabel = false;
  }

  public boolean fallsThrough() {
    return false;
  }

  public boolean branches() {
    return false;
  }

  public Object clone() {
    return new DAbruptStmt(command, label);
  }

  public String toString() {
    StringBuffer b = new StringBuffer();

    b.append(command);

    if ((surpressDestinationLabel == false) && (label.toString() != null)) {
      b.append(" ");
      b.append(label.toString());
    }

    return b.toString();
  }

  public void toString(UnitPrinter up) {
    up.literal(command);
    if ((surpressDestinationLabel == false) && (label.toString() != null)) {
      up.literal(" ");
      up.literal(label.toString());
    }
  }

  public boolean is_Continue() {
    return command.equals("continue");
  }

  public boolean is_Break() {
    return command.equals("break");
  }

  /*
   * Nomair A. Naeem 20-FEB-2005 getter and setter methods for the label are needed for the aggregators of the AST
   * conditionals
   */
  public void setLabel(SETNodeLabel label) {
    this.label = label;
  }

  public SETNodeLabel getLabel() {
    return label;
  }
}
