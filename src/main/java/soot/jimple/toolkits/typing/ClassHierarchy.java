package soot.jimple.toolkits.typing;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2000 Etienne Gagnon.  All rights reserved.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.G;
import soot.IntType;
import soot.NullType;
import soot.RefType;
import soot.Scene;
import soot.ShortType;
import soot.Type;
import soot.TypeSwitch;
import soot.options.Options;

/**
 * This class encapsulates the typing class hierarchy, as well as non-reference types.
 *
 * <P>
 * This class is primarily used by the TypeResolver class, to optimize its computation.
 **/
public class ClassHierarchy {
  /** Map: Scene -> ClassHierarchy **/

  public final TypeNode OBJECT;
  public final TypeNode CLONEABLE;
  public final TypeNode SERIALIZABLE;
  public final TypeNode NULL;
  public final TypeNode INT;
  // public final TypeNode UNKNOWN;
  // public final TypeNode ERROR;

  /** All type node instances **/
  private final List<TypeNode> typeNodeList = new ArrayList<TypeNode>();

  /** Map: Type -> TypeNode **/
  private final HashMap<Type, TypeNode> typeNodeMap = new HashMap<Type, TypeNode>();

  /** Used to transform boolean, byte, short and char to int **/
  private final ToInt transform = new ToInt();

  /** Used to create TypeNode instances **/
  private final ConstructorChooser make = new ConstructorChooser();

  private ClassHierarchy(Scene scene) {
    if (scene == null) {
      throw new InternalTypingException();
    }

    G.v().ClassHierarchy_classHierarchyMap.put(scene, this);

    this.NULL = typeNode(NullType.v());
    this.OBJECT = typeNode(Scene.v().getObjectType());

    // hack for J2ME library which does not have Cloneable and Serializable
    // reported by Stephen Chen
    if (!Options.v().j2me() && Options.v().src_prec() != Options.src_prec_dotnet) {
      this.CLONEABLE = typeNode(RefType.v("java.lang.Cloneable"));
      this.SERIALIZABLE = typeNode(RefType.v("java.io.Serializable"));
    } else {
      this.CLONEABLE = null;
      this.SERIALIZABLE = null;
    }

    this.INT = typeNode(IntType.v());
  }

  /** Get the class hierarchy for the given scene. **/
  public static ClassHierarchy classHierarchy(Scene scene) {
    if (scene == null) {
      throw new InternalTypingException();
    }

    ClassHierarchy classHierarchy = G.v().ClassHierarchy_classHierarchyMap.get(scene);

    if (classHierarchy == null) {
      classHierarchy = new ClassHierarchy(scene);
    }

    return classHierarchy;
  }

  /** Get the type node for the given type. **/
  public TypeNode typeNode(Type type) {
    if (type == null) {
      throw new InternalTypingException();
    }

    type = transform.toInt(type);
    TypeNode typeNode = typeNodeMap.get(type);
    if (typeNode == null) {
      int id = typeNodeList.size();
      typeNodeList.add(null);

      typeNode = make.typeNode(id, type, this);

      typeNodeList.set(id, typeNode);
      typeNodeMap.put(type, typeNode);
    }

    return typeNode;
  }

  /** Returns a string representation of this object **/
  @Override
  public String toString() {
    StringBuilder s = new StringBuilder("ClassHierarchy:{");

    boolean colon = false;
    for (TypeNode typeNode : typeNodeList) {
      if (colon) {
        s.append(',');
      } else {
        colon = true;
      }
      s.append(typeNode);
    }
    s.append('}');

    return s.toString();
  }

  /**
   * Transforms boolean, byte, short and char into int.
   **/
  private static class ToInt extends TypeSwitch<Type> {
    private final Type intType = IntType.v();

    /** Transform boolean, byte, short and char into int. **/
    public Type toInt(Type type) {
      type.apply(this);
      return getResult();
    }

    @Override
    public void caseBooleanType(BooleanType type) {
      setResult(intType);
    }

    @Override
    public void caseByteType(ByteType type) {
      setResult(intType);
    }

    @Override
    public void caseShortType(ShortType type) {
      setResult(intType);
    }

    @Override
    public void caseCharType(CharType type) {
      setResult(intType);
    }

    @Override
    public void defaultCase(Type type) {
      setResult(type);
    }
  }

  /**
   * Creates new TypeNode instances usign the appropriate constructor.
   **/
  private static class ConstructorChooser extends TypeSwitch<TypeNode> {
    private int id;
    private ClassHierarchy hierarchy;

    /** Create a new TypeNode instance for the type parameter. **/
    public TypeNode typeNode(int id, Type type, ClassHierarchy hierarchy) {
      if (type == null || hierarchy == null) {
        throw new InternalTypingException();
      }
      this.id = id;
      this.hierarchy = hierarchy;

      type.apply(this);

      return getResult();
    }

    @Override
    public void caseRefType(RefType type) {
      setResult(new TypeNode(id, type, hierarchy));
    }

    @Override
    public void caseArrayType(ArrayType type) {
      setResult(new TypeNode(id, type, hierarchy));
    }

    @Override
    public void defaultCase(Type type) {
      setResult(new TypeNode(id, type, hierarchy));
    }
  }
}
