package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import java.io.Serializable;
import java.util.List;

import soot.tagkit.Host;
import soot.util.Chain;
import soot.util.Switchable;

/**
 * A code fragment (eg Stmt or Inst), used within Body classes. Intermediate representations must use an implementation of
 * Unit for their code. In general, a unit denotes some sort of unit for execution.
 */
public interface Unit extends Switchable, Host, Serializable, Context {
  /** Returns a list of Boxes containing Values used in this Unit. */
  public List<ValueBox> getUseBoxes();

  /** Returns a list of Boxes containing Values defined in this Unit. */
  public List<ValueBox> getDefBoxes();

  /**
   * Returns a list of Boxes containing Units defined in this Unit; typically branch targets.
   */
  public List<UnitBox> getUnitBoxes();

  /** Returns a list of Boxes pointing to this Unit. */
  public List<UnitBox> getBoxesPointingToThis();

  /** Adds a box to the list returned by getBoxesPointingToThis. */
  public void addBoxPointingToThis(UnitBox b);

  /** Removes a box from the list returned by getBoxesPointingToThis. */
  public void removeBoxPointingToThis(UnitBox b);

  /** Clears any pointers to and from this Unit's UnitBoxes. */
  public void clearUnitBoxes();

  /**
   * Returns a list of Boxes containing any Value either used or defined in this Unit.
   */
  public List<ValueBox> getUseAndDefBoxes();

  public Object clone();

  /**
   * Returns true if execution after this statement may continue at the following statement. GotoStmt will return false but
   * IfStmt will return true.
   */
  public boolean fallsThrough();

  /**
   * Returns true if execution after this statement does not necessarily continue at the following statement. GotoStmt and
   * IfStmt will both return true.
   */
  public boolean branches();

  public void toString(UnitPrinter up);

  /**
   * Redirects jumps to this Unit to newLocation. In general, you shouldn't have to use this directly.
   *
   * @see PatchingChain#getNonPatchingChain()
   * @see soot.shimple.Shimple#redirectToPreds(Chain, Unit)
   * @see soot.shimple.Shimple#redirectPointers(Unit, Unit)
   **/
  public void redirectJumpsToThisTo(Unit newLocation);
}
