package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2001 Feng Qian
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

public class LineNumberTag implements Tag {
  /* it is a u2 value representing line number. */
  protected int line_number;

  public LineNumberTag(int ln) {
    line_number = ln;
  }

  public String getName() {
    return "LineNumberTag";
  }

  public byte[] getValue() {
    byte[] v = new byte[2];
    v[0] = (byte) (line_number / 256);
    v[1] = (byte) (line_number % 256);
    return v;
  }

  public int getLineNumber() {
    return line_number;
  }

  public void setLineNumber(int value) {
    line_number = value;
  }

  public String toString() {
    return String.valueOf(line_number);
  }

}
