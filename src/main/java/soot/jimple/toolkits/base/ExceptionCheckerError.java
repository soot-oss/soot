package soot.jimple.toolkits.base;

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

import soot.SootClass;
import soot.SootMethod;
import soot.jimple.Stmt;
import soot.tagkit.SourceLnPosTag;

public class ExceptionCheckerError extends Exception {

  public ExceptionCheckerError(SootMethod m, SootClass sc, Stmt s, SourceLnPosTag pos) {
    method(m);
    excType(sc);
    throwing(s);
    position(pos);
  }

  private SootMethod method;
  private SootClass excType;
  private Stmt throwing;
  private SourceLnPosTag position;

  public SootMethod method() {
    return method;
  }

  public void method(SootMethod sm) {
    method = sm;
  }

  public SootClass excType() {
    return excType;
  }

  public void excType(SootClass sc) {
    excType = sc;
  }

  public Stmt throwing() {
    return throwing;
  }

  public void throwing(Stmt s) {
    throwing = s;
  }

  public SourceLnPosTag position() {
    return position;
  }

  public void position(SourceLnPosTag pos) {
    position = pos;
  }

}
