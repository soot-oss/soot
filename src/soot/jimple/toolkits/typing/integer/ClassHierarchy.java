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


package soot.jimple.toolkits.typing.integer;

import soot.*;
import java.util.*;

/**
 * This class encapsulates the integer type hierarchy.
 *
 * <P> This class is primarily used by the TypeResolver class, to optimize its computation.
 **/
public class ClassHierarchy
{
    public ClassHierarchy( Singletons.Global g )
  {
    typeNodeMap.put(BooleanType.v(), BOOLEAN);
    typeNodeMap.put(ByteType.v(), BYTE);
    typeNodeMap.put(ShortType.v(), SHORT);
    typeNodeMap.put(CharType.v(), CHAR);
    typeNodeMap.put(IntType.v(), INT);
  }

    public static ClassHierarchy v() { return G.v().soot_jimple_toolkits_typing_integer_ClassHierarchy(); }
  public final TypeNode BOOLEAN = new TypeNode(0, BooleanType.v());
  public final TypeNode BYTE = new TypeNode(1, ByteType.v());
  public final TypeNode SHORT = new TypeNode(2, ShortType.v());
  public final TypeNode CHAR = new TypeNode(3, CharType.v());
  public final TypeNode INT = new TypeNode(4, IntType.v());
  public final TypeNode TOP = new TypeNode(5, null);
  public final TypeNode R0_1 = new TypeNode(6, null); 		//eventually becomes boolean
  public final TypeNode R0_127 = new TypeNode(7, null); 	//eventually becomes byte
  public final TypeNode R0_32767 = new TypeNode(8, null);	//eventually becomes short 

  private final boolean[][] ancestors_1 =
  {
    { false, false, false, false, false,  true, false, false, false, },
    { false, false,  true, false,  true,  true, false, false, false, },
    { false, false, false, false,  true,  true, false, false, false, },
    { false, false, false, false,  true,  true, false, false, false, },
    { false, false, false, false, false,  true, false, false, false, },
    { false, false, false, false, false, false, false, false, false, },
    {  true,  true,  true,  true,  true,  true, false,  true,  true, },
    { false,  true,  true,  true,  true,  true, false, false,  true, },
    { false, false,  true,  true,  true,  true, false, false, false, },
  };

  private final boolean[][] ancestors_2 =
  {
    { false,  true,  true,  true,  true, false, false,  true,  true, },
    { false, false,  true, false,  true, false, false, false, false, },
    { false, false, false, false,  true, false, false, false, false, },
    { false, false, false, false,  true, false, false, false, false, },
    { false, false, false, false, false, false, false, false, false, },
    { },
    { },
    { false,  true,  true,  true,  true, false, false, false,  true, },
    { false, false,  true,  true,  true, false, false, false, false, },
  };

  private final boolean[][] descendants_1 =
  {
    { false, false, false, false, false, false,  true, false, false, },
    { false, false, false, false, false, false,  true,  true, false, },
    { false,  true, false, false, false, false,  true,  true,  true, },
    { false, false, false, false, false, false,  true,  true,  true, },
    { false,  true,  true,  true, false, false,  true,  true,  true, },
    {  true,  true,  true,  true,  true, false,  true,  true,  true, },
    { false, false, false, false, false, false, false, false, false, },
    { false, false, false, false, false, false,  true, false, false, },
    { false, false, false, false, false, false,  true,  true, false, },
  };

  private final boolean[][] descendants_2 =
  {
    { false, false, false, false, false, false, false, false, false, },
    {  true, false, false, false, false, false, false,  true, false, },
    {  true,  true, false, false, false, false, false,  true,  true, },
    {  true, false, false, false, false, false, false,  true,  true, },
    {  true,  true,  true,  true, false, false, false,  true,  true, },
    { },
    { },
    {  true, false, false, false, false, false, false, false, false, },
    {  true, false, false, false, false, false, false,  true, false, },
  };

  private final TypeNode[][] lca_1 =
  {
    {  BOOLEAN,      TOP,      TOP,      TOP,      TOP,      TOP,  BOOLEAN,      TOP,      TOP, },
    {      TOP,     BYTE,    SHORT,      INT,      INT,      TOP,     BYTE,     BYTE,    SHORT, },
    {      TOP,    SHORT,    SHORT,      INT,      INT,      TOP,    SHORT,    SHORT,    SHORT, },
    {      TOP,      INT,      INT,     CHAR,      INT,      TOP,     CHAR,     CHAR,     CHAR, },
    {      TOP,      INT,      INT,      INT,      INT,      TOP,      INT,      INT,      INT, },
    {      TOP,      TOP,      TOP,      TOP,      TOP,      TOP,      TOP,      TOP,      TOP, },
    {  BOOLEAN,     BYTE,    SHORT,     CHAR,      INT,      TOP,     R0_1,   R0_127, R0_32767, },
    {      TOP,     BYTE,    SHORT,     CHAR,      INT,      TOP,   R0_127,   R0_127, R0_32767, },
    {      TOP,    SHORT,    SHORT,     CHAR,      INT,      TOP, R0_32767, R0_32767, R0_32767, },
  };
  
  private final TypeNode[][] lca_2 =
  {
    {  BOOLEAN,     BYTE,    SHORT,     CHAR,      INT,     null,     null,   R0_127, R0_32767, },
    {     BYTE,     BYTE,    SHORT,      INT,      INT,     null,     null,     BYTE,    SHORT, },
    {    SHORT,    SHORT,    SHORT,      INT,      INT,     null,     null,    SHORT,    SHORT, },
    {     CHAR,      INT,      INT,     CHAR,      INT,     null,     null,     CHAR,     CHAR, },
    {      INT,      INT,      INT,      INT,      INT,     null,     null,      INT,      INT, },
    { },
    { },
    {   R0_127,     BYTE,    SHORT,     CHAR,      INT,     null,     null,   R0_127, R0_32767, },
    { R0_32767,    SHORT,    SHORT,     CHAR,      INT,     null,     null, R0_32767, R0_32767, },
  };
  
  private final TypeNode[][] gcd_1 =
  {
    {  BOOLEAN,     R0_1,     R0_1,     R0_1,     R0_1,  BOOLEAN,     R0_1,     R0_1,     R0_1, },
    {     R0_1,     BYTE,     BYTE,   R0_127,     BYTE,     BYTE,     R0_1,   R0_127,   R0_127, },
    {     R0_1,     BYTE,    SHORT, R0_32767,    SHORT,    SHORT,     R0_1,   R0_127, R0_32767, },
    {     R0_1,   R0_127, R0_32767,     CHAR,     CHAR,     CHAR,     R0_1,   R0_127, R0_32767, },
    {     R0_1,     BYTE,    SHORT,     CHAR,      INT,      INT,     R0_1,   R0_127, R0_32767, },
    {  BOOLEAN,     BYTE,    SHORT,     CHAR,      INT,      TOP,     R0_1,   R0_127, R0_32767, },
    {     R0_1,     R0_1,     R0_1,     R0_1,     R0_1,     R0_1,     R0_1,     R0_1,     R0_1, },
    {     R0_1,   R0_127,   R0_127,   R0_127,   R0_127,   R0_127,     R0_1,   R0_127,   R0_127, },
    {     R0_1,   R0_127, R0_32767, R0_32767, R0_32767, R0_32767,     R0_1,   R0_127, R0_32767, },
  };
  
  private final TypeNode[][] gcd_2 =
  {
    {  BOOLEAN,  BOOLEAN,  BOOLEAN,  BOOLEAN,  BOOLEAN,     null,     null,  BOOLEAN,  BOOLEAN, },
    {  BOOLEAN,     BYTE,     BYTE,   R0_127,     BYTE,     null,     null,   R0_127,   R0_127, },
    {  BOOLEAN,     BYTE,    SHORT, R0_32767,    SHORT,     null,     null,   R0_127, R0_32767, },
    {  BOOLEAN,   R0_127, R0_32767,     CHAR,     CHAR,     null,     null,   R0_127, R0_32767, },
    {  BOOLEAN,     BYTE,    SHORT,     CHAR,      INT,     null,     null,   R0_127, R0_32767, },
    { },
    { },
    {  BOOLEAN,   R0_127,   R0_127,   R0_127,   R0_127,     null,     null,   R0_127,   R0_127, },
    {  BOOLEAN,   R0_127, R0_32767, R0_32767, R0_32767,     null,     null,   R0_127, R0_32767, },
  };
  
  /** Map: Type -> TypeNode **/
  private final HashMap<Type,TypeNode> typeNodeMap = new HashMap();
  
  /** Get the type node for the given type. **/
  public TypeNode typeNode(Type type)
  {
    if(type == null || !(type instanceof PrimType || type instanceof RefType))
      {
	throw new InternalTypingException(type);
      }
    
    TypeNode typeNode = typeNodeMap.get(type);

    if(typeNode == null)
      {
	throw new InternalTypingException();
      }

    return typeNode;
  }

  public boolean hasAncestor_1(int t1, int t2)
  {
    return ancestors_1[t1][t2];
  }

  public boolean hasAncestor_2(int t1, int t2)
  {
    return ancestors_2[t1][t2];
  }

  public boolean hasDescendant_1(int t1, int t2)
  {
    return descendants_1[t1][t2];
  }

  public boolean hasDescendant_2(int t1, int t2)
  {
    return descendants_2[t1][t2];
  }

  public TypeNode lca_1(int t1, int t2)
  {
    return lca_1[t1][t2];
  }

  private int convert(int n)
  {
    switch(n)
      {
      case 5: return 4;
      case 6: return 0;
      default: return n;
      }
  }

  public TypeNode lca_2(int t1, int t2)
  {
    return lca_2[convert(t1)][convert(t2)];
  }

  public TypeNode gcd_1(int t1, int t2)
  {
    return gcd_1[t1][t2];
  }

  public TypeNode gcd_2(int t1, int t2)
  {
    return gcd_2[convert(t1)][convert(t2)];
  }
}
