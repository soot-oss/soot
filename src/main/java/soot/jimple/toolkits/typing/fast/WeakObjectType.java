package soot.jimple.toolkits.typing.fast;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2020 Steven Arzt
 *
 * All rights reserved.
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

import soot.RefType;
import soot.Singletons.Global;

/**
 * A weak variant of an object type (Object, Serializable, Cloneable). The "java.lang.Object" type can be used as a
 * placeholder for all reference and array types. Assume the following code (without the casts, because they don't need to be
 * explicit in Dalvik):
 * 
 * <pre>
 * java.lang.Object a;
 * int[] b;
 * 
 * b = ((Object[]) a)[42];
 * b[0] = 42;
 * </pre>
 * 
 * If we reconstruct types for this code, the array element from "a" is of type "java.lang.Object", because the array was of
 * type "java.lang.Object[]". Still, this typing is not required, we rather take it, because we have no better guess as to
 * what the original type was. Later on, when we learn that we write an "int" into "b", we can conclude, that "b" should have
 * been of type "int[]". In other words, we need to drop the requirement of "b" being of type "java.lang.Object", which was
 * our initial assumption.
 * 
 * @author Steven Arzt
 */
public class WeakObjectType extends RefType {

  public WeakObjectType(Global g) {
    super(g);
  }

  public WeakObjectType(String className) {
    super(className);
  }

}
