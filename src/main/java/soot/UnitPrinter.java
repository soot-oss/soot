package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import soot.jimple.Constant;
import soot.jimple.IdentityRef;

/**
 * Interface for different methods of printing out a Unit.
 */
public interface UnitPrinter {
  public void startUnit(Unit u);

  public void endUnit(Unit u);

  public void startUnitBox(UnitBox u);

  public void endUnitBox(UnitBox u);

  public void startValueBox(ValueBox u);

  public void endValueBox(ValueBox u);

  public void incIndent();

  public void decIndent();

  public void noIndent();

  public void setIndent(String newIndent);

  public String getIndent();

  public void literal(String s);

  public void newline();

  public void local(Local l);

  public void type(Type t);

  public void methodRef(SootMethodRef m);

  public void constant(Constant c);

  public void fieldRef(SootFieldRef f);

  public void unitRef(Unit u, boolean branchTarget);

  public void identityRef(IdentityRef r);

  public void setPositionTagger(AttributesUnitPrinter pt);

  public AttributesUnitPrinter getPositionTagger();

  public StringBuffer output();
}
