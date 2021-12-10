package soot.jimple.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import soot.BodyTransformer;
import soot.RefType;
import soot.SootClass;
import soot.Type;

/**
 * Abstract base class for all transformers that fix wrong code that declares something as static, but uses it like an
 * instance or vice versa.
 *
 * @author Steven Arzt
 */
public abstract class AbstractStaticnessCorrector extends BodyTransformer {

  protected static boolean isClassLoaded(SootClass sc) {
    return sc.resolvingLevel() >= SootClass.SIGNATURES;
  }

  protected static boolean isTypeLoaded(Type tp) {
    if (tp instanceof RefType) {
      RefType rt = (RefType) tp;
      if (rt.hasSootClass()) {
        return isClassLoaded(rt.getSootClass());
      }
    }
    return false;
  }
}
