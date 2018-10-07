package soot.tagkit;

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

public class SourceLnPosTag implements Tag {

  private final int startLn;
  private final int endLn;
  private final int startPos;
  private final int endPos;

  public SourceLnPosTag(int sline, int eline, int spos, int epos) {
    startLn = sline;
    endLn = eline;
    startPos = spos;
    endPos = epos;
  }

  public int startLn() {
    return startLn;
  }

  public int endLn() {
    return endLn;
  }

  public int startPos() {
    return startPos;
  }

  public int endPos() {
    return endPos;
  }

  public String getName() {
    return "SourceLnPosTag";
  }

  public byte[] getValue() {
    byte[] v = new byte[2];
    v[0] = (byte) (startLn / 256);
    v[1] = (byte) (startLn % 256);
    return v;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("Source Line Pos Tag: ");
    sb.append("sline: ");
    sb.append(startLn);
    sb.append(" eline: ");
    sb.append(endLn);
    sb.append(" spos: ");
    sb.append(startPos);
    sb.append(" epos: ");
    sb.append(endPos);
    return sb.toString();
  }
}
