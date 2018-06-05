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

import java.io.UnsupportedEncodingException;

import soot.SootClass;

public class OuterClassTag implements Tag {
  SootClass outerClass;
  String simpleName;
  boolean anon;

  public OuterClassTag(SootClass outer, String simpleName, boolean anon) {
    this.outerClass = outer;
    this.simpleName = simpleName;
    this.anon = anon;
  }

  public String getName() {
    return "OuterClassTag";
  }

  /**
   */
  public byte[] getValue() {
    try {
      return outerClass.getName().getBytes("UTF8");
    } catch (UnsupportedEncodingException e) {
      return new byte[0];
    }
  }

  public SootClass getOuterClass() {
    return outerClass;
  }

  public String getSimpleName() {
    return simpleName;
  }

  public boolean isAnon() {
    return anon;
  }

  public String toString() {
    return "[outer class=" + outerClass.getName() + "]";
  }
}
