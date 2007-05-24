/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2000 Etienne Gagnon.  All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.jimple.toolkits.typing;

import soot.*;
import soot.options.Options;

import java.util.*;

/**
 * This class encapsulates the typing class hierarchy, as well as non-reference types.
 *
 * <P> This class is primarily used by the TypeResolver class, to optimize its computation.
 **/
public class ClassHierarchy
{
  /** Map: Scene -> ClassHierarchy **/
  
  public final TypeNode OBJECT;
  public final TypeNode CLONEABLE;
  public final TypeNode SERIALIZABLE;
  public final TypeNode NULL;
  public final TypeNode INT;
  //public final TypeNode UNKNOWN;
  //public final TypeNode ERROR;

  /** All type node instances **/
  private final List<TypeNode> typeNodeList = new ArrayList<TypeNode>();
  
  /** Map: Type -> TypeNode **/
  private final HashMap<Type, TypeNode> typeNodeMap = new HashMap<Type, TypeNode>();
  
  /** Used to transform boolean, byte, short and char to int **/
  private final ToInt transform = new ToInt();
  
  /** Used to create TypeNode instances **/
  private final ConstructorChooser make = new ConstructorChooser();
  
  private ClassHierarchy(Scene scene)
  {
    if(scene == null)
      {
	throw new InternalTypingException();
      }

    G.v().ClassHierarchy_classHierarchyMap.put(scene, this);

    NULL = typeNode(NullType.v());
    OBJECT = typeNode(RefType.v("java.lang.Object"));

    // hack for J2ME library which does not have Cloneable and Serializable
    // reported by Stephen Chen
    if (!Options.v().j2me()) {
      CLONEABLE = typeNode(RefType.v("java.lang.Cloneable"));
      SERIALIZABLE = typeNode(RefType.v("java.io.Serializable"));
    } else {
      CLONEABLE = null;
      SERIALIZABLE = null;
    }

    INT = typeNode(IntType.v());
  }

  /** Get the class hierarchy for the given scene. **/
  public static ClassHierarchy classHierarchy(Scene scene)
  {
    if(scene == null)
      {
	throw new InternalTypingException();
      }
    
    ClassHierarchy classHierarchy =
      G.v().ClassHierarchy_classHierarchyMap.get(scene);

    if(classHierarchy == null)
      {
	classHierarchy = new ClassHierarchy(scene);
      }
    
    return classHierarchy;
  }

  /** Get the type node for the given type. **/
  public TypeNode typeNode(Type type)
  {
    if(type == null) 
      {
	throw new InternalTypingException();
      }
    
    type = transform.toInt(type);
    TypeNode typeNode = typeNodeMap.get(type);

    if(typeNode == null)
      {
	int id = typeNodeList.size();
	typeNodeList.add(null);

	typeNode = make.typeNode(id, type, this);

	typeNodeList.set(id, typeNode);
	typeNodeMap.put(type, typeNode);
      }

    return typeNode;
  }

  /** Returns a string representation of this object **/
  public String toString ()
  {
    StringBuffer s = new StringBuffer();
    boolean colon = false;

    s.append("ClassHierarchy:{");
    for (TypeNode typeNode : typeNodeList) {
	if(colon)
	  {
	    s.append(",");
	  }
	else
	  {
	    colon = true;
	  }

	s.append(typeNode);
      }
    s.append("}");

    return s.toString();
  }

  /**
   * Transforms boolean, byte, short and char into int.
   **/
  private static class ToInt extends TypeSwitch
  {
    private Type result;
    private final Type intType = IntType.v();

    private ToInt()
    {
    }

    /** Transform boolean, byte, short and char into int. **/
    Type toInt(Type type)
    {
      type.apply(this);
      return result;
    }

    public void caseBooleanType(BooleanType type)
    {
      result = intType;
    }

    public void caseByteType(ByteType type)
    {
      result = intType;
    }

    public void caseShortType(ShortType type)
    {
      result = intType;
    }

    public void caseCharType(CharType type)
    {
      result = intType;
    }

    public void defaultCase(Type type)
    {
      result = type;
    }
  }

  /**
   * Creates new TypeNode instances usign the appropriate constructor.
   **/
  private static class ConstructorChooser extends TypeSwitch
  {
    private int id;
    private ClassHierarchy hierarchy;

    private TypeNode result;
    
    ConstructorChooser()
    {
    }
    
    /** Create a new TypeNode instance for the type parameter. **/
    TypeNode typeNode(int id, Type type, ClassHierarchy hierarchy)
    {
      if(type == null || hierarchy == null)
	{
	  throw new InternalTypingException();
	}
      
      this.id = id;
      this.hierarchy = hierarchy;
      
      type.apply(this);
      
      return result;
    }

    public void caseRefType(RefType type)
    {
      result = new TypeNode(id, type, hierarchy);
    }

    public void caseArrayType(ArrayType type)
    {
      result = new TypeNode(id, type, hierarchy);
    }

    public void defaultCase(Type type)
    {
      result = new TypeNode(id, type, hierarchy);
    }
  }
}
