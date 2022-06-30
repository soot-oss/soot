package soot.util.dot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Sable Research Group
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
 * A class for specifying Dot graph attributes.
 *
 * @author Feng Qian
 */
public class DotGraphAttribute {

  private final String id;
  private final String value;

  public DotGraphAttribute(String id, String v) {
    this.id = id;
    this.value = v;
  }

  @Override
  public String toString() {
    return this.id + '=' + this.value;
  }

  public String getID() {
    return this.id;
  }

  public String getValue() {
    return this.value;
  }
}
