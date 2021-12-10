package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jennifer Lhotak
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

public class JimpleLineNumberTag implements Tag {

  public static final String NAME = "JimpleLineNumberTag";

  /* it is a value representing line number. */
  private final int startLineNumber;
  private final int endLineNumber;

  public JimpleLineNumberTag(int ln) {
    this.startLineNumber = ln;
    this.endLineNumber = ln;
  }

  public JimpleLineNumberTag(int startLn, int endLn) {
    this.startLineNumber = startLn;
    this.endLineNumber = endLn;
  }

  public int getLineNumber() {
    return startLineNumber;
  }

  public int getStartLineNumber() {
    return startLineNumber;
  }

  public int getEndLineNumber() {
    return endLineNumber;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public byte[] getValue() {
    return new byte[2];
  }

  @Override
  public String toString() {
    return "Jimple Line Tag: " + startLineNumber;
  }
}
