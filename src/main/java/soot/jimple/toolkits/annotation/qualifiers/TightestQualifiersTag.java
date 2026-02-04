package soot.jimple.toolkits.annotation.qualifiers;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Marc Miltenberger
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
import soot.tagkit.Tag;

/**
 * Contains information about possible tighter qualifiers
 */
public class TightestQualifiersTag implements Tag {

  public static enum AccessLevel {
    PUBLIC("Public"), PROTECTED("Protected"), PRIVATE("Private"), PACKAGE_PROTECTED("Package");

    private String toString;

    AccessLevel(String str) {
      this.toString = str;
    }

    @Override
    public String toString() {
      return toString;
    }
  }

  private static final String NAME = "TightestQualifiers";
  private final AccessLevel actual;
  private final AccessLevel tightest;

  /**
   * Create a new tightest qualifier tag
   * 
   * @param actual
   *          the actual access level
   * @param tightest
   *          the tightest possible level
   */
  public TightestQualifiersTag(AccessLevel actual, AccessLevel tightest) {
    this.actual = actual;
    this.tightest = tightest;
  }

  /**
   * Returns the actual access level
   * 
   * @return the actual access level
   */
  public AccessLevel getActualAccessLevel() {
    return actual;
  }

  /**
   * Returns the computed tightest possible access level
   * 
   * @return the tightest possible access level
   */
  public AccessLevel getTightestAccessLevel() {
    return tightest;
  }

  @Override
  public String getName() {
    return NAME;
  }

  public static Tag v(AccessLevel actual, AccessLevel tightest) {
    return new TightestQualifiersTag(actual, tightest);
  }

}
