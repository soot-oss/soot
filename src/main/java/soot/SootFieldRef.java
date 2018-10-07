package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Ondrej Lhotak
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
 * Representation of a reference to a field as it appears in a class file. Note that the field directly referred to may not
 * actually exist; the actual target of the reference is determined according to the resolution procedure in the Java Virtual
 * Machine Specification, 2nd ed, section 5.4.3.2.
 */

public interface SootFieldRef {
  public SootClass declaringClass();

  public String name();

  public Type type();

  public boolean isStatic();

  public String getSignature();

  public SootField resolve();
}
