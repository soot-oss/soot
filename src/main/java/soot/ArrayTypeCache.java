package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import heros.solver.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import soot.Singletons.Global;

/**
 * Caches array types.
 * 
 * @author Marc Miltenberger
 */
public class ArrayTypeCache {
  protected final Map<Pair<Type, Integer>, ArrayType> cache = new ConcurrentHashMap<>();

  protected final Function<Pair<Type, Integer>, ArrayType> createNewArrayType
      = new Function<Pair<Type, Integer>, ArrayType>() {

        @Override
        public ArrayType apply(Pair<Type, Integer> t) {
          return new ArrayType(t.getO1(), t.getO2());
        }

      };

  public ArrayTypeCache(Global g) {
  }

  /**
   * Returns a potentially cached array type of the given dimensions
   * 
   * @param baseType
   *          the base type (array element type)
   * @param numDimensions
   *          the number of dimensions
   * @return the array type
   */
  // We are doing this synchronized now to ensure correctness:
  // Already creating a new ArrayType adds it to the type numberer, so we must not create
  // the same array type twice. Furthermore, the ConcurrentHashMap's computeIfAbsent
  // method does not allow the update of other keys in while a value is computed.
  public ArrayType getArrayType(Type baseType, int numDimensions) {
    if (numDimensions < 1) {
      throw new IllegalArgumentException(
          String.format("Number of dimensions has to be at least 1, but was %d", numDimensions));
    }

    final Pair<Type, Integer> pairSearch = new Pair<>(baseType, numDimensions);
    final ArrayType result = cache.get(pairSearch);
    if (result != null) {
      return result;
    }

    // Slight performance improvement by eortega-pjr
    Type elementType = baseType;
    for (int i = 1; i <= numDimensions; i++) {
      final ArrayType ret = cache.computeIfAbsent(new Pair<>(baseType, i), createNewArrayType);
      elementType.setArrayType(ret);
      elementType = ret;
    }

    return (ArrayType) elementType;

  }

}
