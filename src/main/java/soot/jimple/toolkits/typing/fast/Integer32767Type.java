package soot.jimple.toolkits.typing.fast;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2008 Ben Bellamy 
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

import soot.G;
import soot.IntegerType;
import soot.PrimType;
import soot.RefType;
import soot.Singletons;

/**
 * @author Ben Bellamy
 */
public class Integer32767Type extends PrimType implements IntegerType {

  public static Integer32767Type v() {
    return G.v().soot_jimple_toolkits_typing_fast_Integer32767Type();
  }

  public Integer32767Type(Singletons.Global g) {
  }

  public String toString() {
    return "[0..32767]";
  }

  public boolean equals(Object t) {
    return this == t;
  }

  @Override
  public RefType boxedType() {
    return RefType.v("java.lang.Integer");
  }

  @Override
  public boolean isAllowedInFinalCode() {
    return false;
  }
}