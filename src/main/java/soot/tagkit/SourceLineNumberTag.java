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

public class SourceLineNumberTag implements Tag {
  /* it is a value representing line number. */
  protected int startLineNumber;
  protected int endLineNumber;

  public SourceLineNumberTag(int ln) {
    startLineNumber = ln;
    endLineNumber = ln;
  }

  public SourceLineNumberTag(int startLn, int endLn) {
    startLineNumber = startLn;
    endLineNumber = endLn;
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

  public void setLineNumber(int value) {
    this.startLineNumber = value;
    this.endLineNumber = value;
  }

  public void setStartLineNumber(int value) {
    this.startLineNumber = value;
  }

  public void setEndLineNumber(int value) {
    this.endLineNumber = value;
  }

  public String getName() {
    return "SourceLineNumberTag";
  }

  public byte[] getValue() {
    byte[] v = new byte[2];
    return v;
  }

  public String toString() {
    return String.valueOf(startLineNumber);
  }

}
