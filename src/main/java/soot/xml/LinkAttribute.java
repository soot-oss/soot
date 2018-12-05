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

  private String info;
  private int jimpleLink;
  private int javaLink;
  private String className;
  private final boolean isJimpleLink;
  private final boolean isJavaLink;
  private final String analysisType;

  public LinkAttribute(String info, int jimpleLink, int javaLink, String className, String type) {
    this.info = info;
    this.jimpleLink = jimpleLink;
    this.javaLink = javaLink;
    this.className = className;
    isJimpleLink = true;
    isJavaLink = true;
    analysisType = type;
  }

  public String info() {
    return info;
  }

  public int jimpleLink() {
    return jimpleLink;
  }

  public int javaLink() {
    return javaLink;
  }

  public String className() {
    return className;
  }

  public boolean isJimpleLink() {
    return isJimpleLink;
  }

  public boolean isJavaLink() {
    return isJavaLink;
  }

  public String analysisType() {
    return analysisType;
  }
}
