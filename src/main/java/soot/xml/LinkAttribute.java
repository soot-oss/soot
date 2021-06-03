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

public class LinkAttribute {

  private final String info;
  private final int jimpleLink;
  private final int javaLink;
  private final String className;
  private final boolean isJimpleLink;
  private final boolean isJavaLink;
  private final String analysisType;

  public LinkAttribute(String info, int jimpleLink, int javaLink, String className, String type) {
    this.info = info;
    this.jimpleLink = jimpleLink;
    this.javaLink = javaLink;
    this.className = className;
    this.isJimpleLink = true;
    this.isJavaLink = true;
    this.analysisType = type;
  }

  public String info() {
    return this.info;
  }

  public int jimpleLink() {
    return this.jimpleLink;
  }

  public int javaLink() {
    return this.javaLink;
  }

  public String className() {
    return this.className;
  }

  public boolean isJimpleLink() {
    return this.isJimpleLink;
  }

  public boolean isJavaLink() {
    return this.isJavaLink;
  }

  public String analysisType() {
    return this.analysisType;
  }
}
