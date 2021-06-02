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
    return this.color;
  }

  public void color(ColorAttribute c) {
    this.color = c;
  }

  public int jimpleStartPos() {
    return this.jimpleStartPos;
  }

  public void jimpleStartPos(int x) {
    this.jimpleStartPos = x;
  }

  public int jimpleEndPos() {
    return this.jimpleEndPos;
  }

  public void jimpleEndPos(int x) {
    this.jimpleEndPos = x;
  }

  public int javaStartPos() {
    return this.javaStartPos;
  }

  public void javaStartPos(int x) {
    this.javaStartPos = x;
  }

  public int javaEndPos() {
    return this.javaEndPos;
  }

  public void javaEndPos(int x) {
    this.javaEndPos = x;
  }

  public int jimpleStartLn() {
    return this.jimpleStartLn;
  }

  public void jimpleStartLn(int x) {
    this.jimpleStartLn = x;
  }

  public int jimpleEndLn() {
    return this.jimpleEndLn;
  }

  public void jimpleEndLn(int x) {
    this.jimpleEndLn = x;
  }

  public int javaStartLn() {
    return this.javaStartLn;
  }

  public void javaStartLn(int x) {
    this.javaStartLn = x;
  }

  public int javaEndLn() {
    return this.javaEndLn;
  }

  public void javaEndLn(int x) {
    this.javaEndLn = x;
  }

  public boolean hasColor() {
    return color() != null;
  }
}
