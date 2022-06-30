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

  public static final String NAME = "SourceLnPosTag";

  private final int startLn;
  private final int endLn;
  private final int startPos;
  private final int endPos;

  public SourceLnPosTag(int sline, int eline, int spos, int epos) {
    this.startLn = sline;
    this.endLn = eline;
    this.startPos = spos;
    this.endPos = epos;
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

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public byte[] getValue() {
    byte[] v = new byte[2];
    v[0] = (byte) (startLn / 256);
    v[1] = (byte) (startLn % 256);
    return v;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Source Line Pos Tag: ");
    sb.append("sline: ").append(startLn);
    sb.append(" eline: ").append(endLn);
    sb.append(" spos: ").append(startPos);
    sb.append(" epos: ").append(endPos);
    return sb.toString();
  }
}
