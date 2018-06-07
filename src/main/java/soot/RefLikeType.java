package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
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
 * Abstract class for Soot classes that model subtypes of java.lang.Object (ie. object references and arrays)
 *
 * @author Ondrej Lhotak
 */

@SuppressWarnings("serial")
public abstract class RefLikeType extends Type {
  /**
   * If I have a variable x of declared type t, what is a good declared type for the expression ((Object[]) x)[i]? The
   * getArrayElementType() method in RefLikeType was introduced even later to answer this question for all classes
   * implementing RefLikeType. If t is an array, then the answer is the same as getElementType(). But t could also be Object,
   * Serializable, or Cloneable, which can all hold any array, so then the answer is Object.
   */
  public abstract Type getArrayElementType();
}
