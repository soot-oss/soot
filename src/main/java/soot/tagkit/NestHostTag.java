package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Soot contributors
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
 * Represents the {@code NestHost} class-file attribute introduced by JEP 181 (Nest-Based Access Control). It records the
 * host of the nest that the annotated class belongs to.
 */
public class NestHostTag implements Tag {

  public static final String NAME = "NestHostTag";

  private final String host;

  public NestHostTag(String host) {
    this.host = host;
  }

  /**
   * Returns the internal name of the nest host.
   * 
   * @return the nest host
   */
  public String getHost() {
    return this.host;
  }

  @Override
  public String toString() {
    return "Nest host: " + host;
  }

  @Override
  public String getName() {
    return NAME;
  }
}
