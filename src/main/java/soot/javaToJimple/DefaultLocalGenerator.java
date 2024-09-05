package soot.javaToJimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import soot.Body;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DecimalType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LocalGenerator;
import soot.LongType;
import soot.RefLikeType;
import soot.ShortType;
import soot.Type;
import soot.UByteType;
import soot.UIntType;
import soot.ULongType;
import soot.UShortType;
import soot.UnknownType;
import soot.VoidType;
import soot.jimple.Jimple;
import soot.jimple.toolkits.typing.fast.Integer127Type;
import soot.jimple.toolkits.typing.fast.Integer1Type;
import soot.jimple.toolkits.typing.fast.Integer32767Type;
import soot.util.Chain;

public class DefaultLocalGenerator extends LocalGenerator {

  protected final Chain<Local> locals;
  protected Set<String> names;
  protected long expectedModCount;

  private int tempInt = -1;
  private int tempVoid = -1;
  private int tempBoolean = -1;
  private int tempLong = -1;
  private int tempDouble = -1;
  private int tempFloat = -1;
  private int tempRefLikeType = -1;
  private int tempByte = -1;
  private int tempShort = -1;
  private int tempChar = -1;
  private int tempUnknownType = -1;

  public DefaultLocalGenerator(Body b) {
    this.locals = b.getLocals();
    this.names = null;
    this.expectedModCount = -1;// init with invalid mod count
  }

  public Local generateLocal(Type type) {
    Supplier<String> nameGen;
    if (type instanceof IntType || type instanceof Integer1Type || type instanceof Integer127Type
        || type instanceof Integer32767Type || type instanceof UIntType) {
      nameGen = this::nextIntName;
    } else if (type instanceof ByteType) {
      nameGen = this::nextByteName;
    } else if (type instanceof ShortType) {
      nameGen = this::nextShortName;
    } else if (type instanceof BooleanType) {
      nameGen = this::nextBooleanName;
    } else if (type instanceof VoidType) {
      nameGen = this::nextVoidName;
    } else if (type instanceof CharType) {
      nameGen = this::nextCharName;
    } else if (type instanceof DoubleType || type instanceof DecimalType) {
      nameGen = this::nextDoubleName;
    } else if (type instanceof FloatType) {
      nameGen = this::nextFloatName;
    } else if (type instanceof LongType) {
      nameGen = this::nextLongName;
    } else if (type instanceof ULongType) {
      nameGen = this::nextLongName;
    } else if (type instanceof UByteType) {
      nameGen = this::nextByteName;
    } else if (type instanceof UShortType) {
      nameGen = this::nextShortName;
    } else if (type instanceof RefLikeType) {
      nameGen = this::nextRefLikeTypeName;
    } else if (type instanceof UnknownType) {
      nameGen = this::nextUnknownTypeName;
    } else {
      throw new RuntimeException(
          String.format("Unhandled Type %s of Local variable to Generate - Not Implemented", type.getClass().getName()));
    }

    // Ensure the 'names' set is up to date with the local chain.
    Set<String> localNames = this.names;
    {
      Chain<Local> locs = this.locals;
      long modCount = locs.getModificationCount();
      if (this.expectedModCount != modCount) {
        this.expectedModCount = modCount;
        this.names = localNames = new HashSet<>(locs.size());
        for (Local l : locs) {
          localNames.add(l.getName());
        }
      }
      assert (localNames != null);
    }

    String name;
    do {
      name = nameGen.get();
    } while (localNames.contains(name));

    return createLocal(name, type);
  }

  private String nextIntName() {
    return "$i" + (++tempInt);
  }

  private String nextCharName() {
    return "$c" + (++tempChar);
  }

  private String nextVoidName() {
    return "$v" + (++tempVoid);
  }

  private String nextByteName() {
    return "$b" + (++tempByte);
  }

  private String nextShortName() {
    return "$s" + (++tempShort);
  }

  private String nextBooleanName() {
    return "$z" + (++tempBoolean);
  }

  private String nextDoubleName() {
    return "$d" + (++tempDouble);
  }

  private String nextFloatName() {
    return "$f" + (++tempFloat);
  }

  private String nextLongName() {
    return "$l" + (++tempLong);
  }

  private String nextRefLikeTypeName() {
    return "$r" + (++tempRefLikeType);
  }

  private String nextUnknownTypeName() {
    return "$u" + (++tempUnknownType);
  }

  // this should be used for generated locals only
  protected Local createLocal(String name, Type sootType) {
    assert (expectedModCount == locals.getModificationCount());// pre-condition
    assert (!names.contains(name));// pre-condition

    Local sootLocal = Jimple.v().newLocal(name, sootType);
    locals.add(sootLocal);
    expectedModCount++;
    names.add(name);

    assert (expectedModCount == locals.getModificationCount());// post-condition
    assert (names.contains(name));// post-condition

    return sootLocal;
  }
}
