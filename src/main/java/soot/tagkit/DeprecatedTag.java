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
 * Represents the deprecated attribute used by fields, methods and classes
 * 
 * The two attributes <code>forRemoval</code> and <code>since</code> were introduced with Java 9.
 */
public class DeprecatedTag implements Tag {

  public static final String NAME = "DeprecatedTag";

  private final Boolean forRemoval;

  private final String since;

  public DeprecatedTag() {
    forRemoval = null;
    since = null;
  }

  public DeprecatedTag(Boolean forRemoval, String since) {
    super();
    this.forRemoval = forRemoval;
    this.since = since;
  }

  @Override
  public String toString() {
    return "Deprecated";
  }

  @Override
  public String getName() {
    return NAME;
  }

  public String getInfo() {
    return "Deprecated";
  }

  public Boolean getForRemoval() {
    return forRemoval;
  }

  public String getSince() {
    return since;
  }

  @Override
  public byte[] getValue() {
    throw new RuntimeException("DeprecatedTag has no value for bytecode");
  }
}
