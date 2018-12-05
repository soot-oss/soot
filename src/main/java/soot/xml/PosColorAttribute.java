package soot.xml;

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

public class PosColorAttribute {

  private ColorAttribute color;
  private int jimpleStartPos;
  private int jimpleEndPos;
  private int javaStartPos;
  private int javaEndPos;
  private int javaStartLn;
  private int javaEndLn;
  private int jimpleStartLn;
  private int jimpleEndLn;

  public PosColorAttribute() {
  }

  public ColorAttribute color() {
    return color;
  }

  public void color(ColorAttribute c) {
    color = c;
  }

  public int jimpleStartPos() {
    return jimpleStartPos;
  }

  public void jimpleStartPos(int x) {
    jimpleStartPos = x;
  }

  public int jimpleEndPos() {
    return jimpleEndPos;
  }

  public void jimpleEndPos(int x) {
    jimpleEndPos = x;
  }

  public int javaStartPos() {
    return javaStartPos;
  }

  public void javaStartPos(int x) {
    javaStartPos = x;
  }

  public int javaEndPos() {
    return javaEndPos;
  }

  public void javaEndPos(int x) {
    javaEndPos = x;
  }

  public int jimpleStartLn() {
    return jimpleStartLn;
  }

  public void jimpleStartLn(int x) {
    jimpleStartLn = x;
  }

  public int jimpleEndLn() {
    return jimpleEndLn;
  }

  public void jimpleEndLn(int x) {
    jimpleEndLn = x;
  }

  public int javaStartLn() {
    return javaStartLn;
  }

  public void javaStartLn(int x) {
    javaStartLn = x;
  }

  public int javaEndLn() {
    return javaEndLn;
  }

  public void javaEndLn(int x) {
    javaEndLn = x;
  }

  public boolean hasColor() {
    if (color() != null) {
      return true;
    } else {
      return false;
    }
  }
}
