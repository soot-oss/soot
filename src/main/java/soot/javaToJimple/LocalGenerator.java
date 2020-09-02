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

import soot.Body;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.RefLikeType;
import soot.ShortType;
import soot.Type;
import soot.UnknownType;
import soot.VoidType;
import soot.jimple.Jimple;
import soot.jimple.toolkits.typing.fast.Integer127Type;
import soot.jimple.toolkits.typing.fast.Integer1Type;
import soot.jimple.toolkits.typing.fast.Integer32767Type;
import soot.util.Chain;

public class LocalGenerator {

  protected final Chain<Local> locals;

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

  public LocalGenerator(Body b) {
    this.locals = b.getLocals();
  }

  /** generates a new soot local given the type */
  public Local generateLocal(Type type) {

    // store local names for enhanced performance
    HashSet<String> localNames = new HashSet<String>();
    for (Local l : locals) {
      localNames.add(l.getName());
    }

    String name;
    if (type instanceof IntType || type instanceof Integer1Type || type instanceof Integer127Type
        || type instanceof Integer32767Type) {
      do {
        name = nextIntName();
      } while (localNames.contains(name));
    } else if (type instanceof ByteType) {
      do {
        name = nextByteName();
      } while (localNames.contains(name));
    } else if (type instanceof ShortType) {
      do {
        name = nextShortName();
      } while (localNames.contains(name));
    } else if (type instanceof BooleanType) {
      do {
        name = nextBooleanName();
      } while (localNames.contains(name));
    } else if (type instanceof VoidType) {
      do {
        name = nextVoidName();
      } while (localNames.contains(name));
    } else if (type instanceof CharType) {
      do {
        name = nextCharName();
      } while (localNames.contains(name));
    } else if (type instanceof DoubleType) {
      do {
        name = nextDoubleName();
      } while (localNames.contains(name));
    } else if (type instanceof FloatType) {
      do {
        name = nextFloatName();
      } while (localNames.contains(name));
    } else if (type instanceof LongType) {
      do {
        name = nextLongName();
      } while (localNames.contains(name));
    } else if (type instanceof RefLikeType) {
      do {
        name = nextRefLikeTypeName();
      } while (localNames.contains(name));
    } else if (type instanceof UnknownType) {
      do {
        name = nextUnknownTypeName();
      } while (localNames.contains(name));
    } else {
      throw new RuntimeException(
          String.format("Unhandled Type %s of Local variable to Generate - Not Implemented", type.getClass().getName()));
    }

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
    Local sootLocal = Jimple.v().newLocal(name, sootType);
    locals.add(sootLocal);
    return sootLocal;
  }
}
