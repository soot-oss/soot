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

import java.io.PrintWriter;

public class Key {

  private final int red;
  private final int green;
  private final int blue;
  private final String key;
  private String aType;

  public Key(int r, int g, int b, String k) {
    red = r;
    green = g;
    blue = b;
    key = k;
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

  public String key() {
    return key;
  }

  public void print(PrintWriter writerOut) {
    writerOut.println("<key red=\"" + red() + "\" green=\"" + green() + "\" blue=\"" + blue() + "\" key=\"" + key()
        + "\" aType=\"" + aType() + "\"/>");
  }

  public String aType() {
    return aType;
  }

  public void aType(String s) {
    aType = s;
  }
}
