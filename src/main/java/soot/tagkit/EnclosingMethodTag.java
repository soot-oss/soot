package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Jennifer Lhotak
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

/**
 * Represents the enclosing method attribute attatched to anon and inner classes to indicate the class and method it is
 * declared in for Java 1.5.
 */

public class EnclosingMethodTag implements Tag {

  private String enclosingClass;
  private String enclosingMethod;
  private String enclosingMethodSig;

  public EnclosingMethodTag(String c, String m, String s) {
    this.enclosingClass = c;
    this.enclosingMethod = m;
    this.enclosingMethodSig = s;
  }

  public String toString() {
    return "Enclosing Class: " + enclosingClass + " Enclosing Method: " + enclosingMethod + " Sig: " + enclosingMethodSig;
  }

  /** Returns the tag name. */
  public String getName() {
    return "EnclosingMethodTag";
  }

  public String getInfo() {
    return "EnclosingMethod";
  }

  public String getEnclosingClass() {
    return enclosingClass;
  }

  public String getEnclosingMethod() {
    return enclosingMethod;
  }

  public String getEnclosingMethodSig() {
    return enclosingMethodSig;
  }

  /** Returns the tag raw data. */
  public byte[] getValue() {
    throw new RuntimeException("EnclosingMethodTag has no value for bytecode");
  }
}
