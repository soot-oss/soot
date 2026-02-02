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

import java.util.Collection;
import java.util.Collections;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.IntType;
import soot.IntegerType;
import soot.ShortType;
import soot.Type;

/**
 * @author Ben Bellamy
 */
public class AugHierarchy implements IHierarchy {

  public static Collection<Type> lcas_(Type a, Type b, boolean useWeakObjectType) {
    if (TypeResolver.typesEqual(a, b)) {
      return Collections.<Type>singletonList(a);
    } else if (a instanceof BottomType) {
      return Collections.<Type>singletonList(b);
    } else if (b instanceof BottomType) {
      return Collections.<Type>singletonList(a);
    } else if (a instanceof WeakObjectType) {
      return Collections.<Type>singletonList(b);
    } else if (b instanceof WeakObjectType) {
      return Collections.<Type>singletonList(a);
    } else if (a instanceof IntegerType && b instanceof IntegerType) {
      if (a instanceof Integer1Type) {
        return Collections.<Type>singletonList(b);
      } else if (b instanceof Integer1Type) {
        return Collections.<Type>singletonList(a);
      } else if (a instanceof BooleanType || b instanceof BooleanType) {
        return Collections.<Type>emptyList();
      } else if ((a instanceof ByteType && b instanceof Integer32767Type)
          || (b instanceof ByteType && a instanceof Integer32767Type)) {
        return Collections.<Type>singletonList(ShortType.v());
      } else if ((a instanceof CharType && (b instanceof ShortType || b instanceof ByteType))
          || (b instanceof CharType && (a instanceof ShortType || a instanceof ByteType))) {
        return Collections.<Type>singletonList(IntType.v());
      } else if (ancestor_(a, b)) {
        return Collections.<Type>singletonList(a);
      } else {
        return Collections.<Type>singletonList(b);
      }
    } else if (a instanceof IntegerType || b instanceof IntegerType) {
      return Collections.<Type>emptyList();
    } else {
      return BytecodeHierarchy.lcas_(a, b, useWeakObjectType);
    }
  }

  public static boolean ancestor_(Type ancestor, Type child) {
    if (TypeResolver.typesEqual(ancestor, child)) {
      return true;
    } else if (ancestor instanceof ArrayType && child instanceof ArrayType) {
      // Arrays are not covariant. However, we may have intermediate types that will later be replaced with actual types. In
      // that case, we consider the temporary type as compatible with a final type of sufficient size. Note that these checks
      // are more strict than the non-arrays checks on Integer types below.
      Type at = ((ArrayType) ancestor).getElementType();
      Type ct = ((ArrayType) child).getElementType();
      if (at instanceof Integer1Type) {
        return ct instanceof BottomType;
      } else if (at instanceof BooleanType) {
        return ct instanceof BottomType || ct instanceof Integer1Type;
      } else if (at instanceof Integer127Type) {
        return ct instanceof BottomType || ct instanceof Integer1Type;
      } else if (at instanceof ByteType || at instanceof Integer32767Type) {
        return ct instanceof BottomType || ct instanceof Integer1Type || ct instanceof Integer127Type;
      } else if (at instanceof CharType) {
        return ct instanceof BottomType || ct instanceof Integer1Type || ct instanceof Integer127Type
            || ct instanceof Integer32767Type;
      } else if (ancestor instanceof ShortType) {
        return ct instanceof BottomType || ct instanceof Integer1Type || ct instanceof Integer127Type
            || ct instanceof Integer32767Type;
      } else if (at instanceof IntType) {
        return ct instanceof BottomType || ct instanceof Integer1Type || ct instanceof Integer127Type
            || ct instanceof Integer32767Type;
      } else if (ct instanceof IntegerType) {
        return false;
      } else {
        return BytecodeHierarchy.ancestor_(ancestor, child);
      }
    } else if (ancestor instanceof IntegerType && child instanceof IntegerType) {
      return IntUtils.getMaxValue((IntegerType) ancestor) >= IntUtils.getMaxValue((IntegerType) child);
    } else if (ancestor instanceof Integer1Type) {
      return child instanceof BottomType;
    } else if (ancestor instanceof BooleanType) {
      return child instanceof BottomType || child instanceof Integer1Type;
    } else if (ancestor instanceof Integer127Type) {
      return child instanceof BottomType || child instanceof Integer1Type;
    } else if (ancestor instanceof ByteType || ancestor instanceof Integer32767Type) {
      return child instanceof BottomType || child instanceof Integer1Type || child instanceof Integer127Type;
    } else if (ancestor instanceof CharType) {
      return child instanceof BottomType || child instanceof Integer1Type || child instanceof Integer127Type
          || child instanceof Integer32767Type;
    } else if (ancestor instanceof ShortType) {
      return child instanceof BottomType || child instanceof Integer1Type || child instanceof Integer127Type
          || child instanceof Integer32767Type || child instanceof ByteType;
    } else if (ancestor instanceof IntType) {
      return child instanceof BottomType || child instanceof Integer1Type || child instanceof Integer127Type
          || child instanceof Integer32767Type || child instanceof ByteType || child instanceof CharType
          || child instanceof ShortType;
    } else if (child instanceof IntegerType) {
      return false;
    } else {
      return BytecodeHierarchy.ancestor_(ancestor, child);
    }
  }

  @Override
  public Collection<Type> lcas(Type a, Type b, boolean useWeakObjectType) {
    return lcas_(a, b, useWeakObjectType);
  }

  @Override
  public boolean ancestor(Type ancestor, Type child) {
    return ancestor_(ancestor, child);
  }
}
