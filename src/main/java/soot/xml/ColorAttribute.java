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

public class ColorAttribute {

  private int red;
  private int green;
  private int blue;
  private int fg;
  private final String analysisType;

  public ColorAttribute(int red, int green, int blue, boolean fg, String type) {
    this.red = red;
    this.green = green;
    this.blue = blue;
    if (fg) {
      this.fg = 1;
    } else {
      this.fg = 0;
    }
    analysisType = type;
  }

  public int red() {
    return red;
  }

  public int green() {
    return green;
  }

  public int blue() {
    return blue;
  }

  public int fg() {
    return fg;
  }

  public String analysisType() {
    return analysisType;
  }

}
