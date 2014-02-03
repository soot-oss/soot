/* Soot - a J*va Optimization Framework
 * Copyright (C) 2008 Ben Bellamy 
 * 
 * All rights reserved.
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
package soot.jimple.toolkits.typing.fast;

import java.util.*;
import soot.*;

/**
 * @author Ben Bellamy
 */
public class BytecodeHierarchy implements IHierarchy
{
	private static class AncestryTreeNode
	{
		public final AncestryTreeNode next;
		public final RefType type;
		
		public AncestryTreeNode(AncestryTreeNode next, RefType type)
		{
			this.next = next;
			this.type = type;
		}
	}
	
	/* Returns a collection of nodes, each with type Object, each at the leaf
	end of a different path from root to Object. */
	private static Collection<AncestryTreeNode> buildAncestryTree(RefType root)
	{
		LinkedList<AncestryTreeNode> leafs = new LinkedList<AncestryTreeNode>();
		leafs.add(new AncestryTreeNode(null, root));
		
		LinkedList<AncestryTreeNode> r = new LinkedList<AncestryTreeNode>();
		while ( !leafs.isEmpty() )
		{
			AncestryTreeNode node = leafs.remove();
			if ( TypeResolver.typesEqual(
				node.type, RefType.v("java.lang.Object")) )
				r.add(node);
			else
			{
				SootClass sc = node.type.getSootClass();
				
				for ( Iterator<SootClass> i = sc.getInterfaces().iterator(); i.hasNext(); )
					leafs.add(new AncestryTreeNode(
						node, (i.next()).getType()));
				
				// The superclass of all interfaces is Object
				// -- try to discard phantom interfaces.
				if ( ( !sc.isInterface() || sc.getInterfaceCount() == 0 ) && !sc.isPhantom())
					leafs.add(new AncestryTreeNode(
						node, sc.getSuperclass().getType()));
				
			}
		}
		return r;
	}
	
	private static RefType leastCommonNode(
		AncestryTreeNode a, AncestryTreeNode b)
	{
		RefType r = null;
		while ( a != null && b != null
			&& TypeResolver.typesEqual(a.type, b.type) )
		{
			r = a.type;
			a = a.next;
			b = b.next;
		}
		return r;
	}
	
	public Collection<Type> lcas(Type a, Type b)
	{
		return lcas_(a, b);
	}
	
	public static Collection<Type> lcas_(Type a, Type b)
	{
		if ( TypeResolver.typesEqual(a, b) )
			return Collections.<Type>singletonList(a);
		else if ( a instanceof BottomType )
			return Collections.<Type>singletonList(b);
		else if ( b instanceof BottomType )
			return Collections.<Type>singletonList(a);
		else if ( a instanceof IntegerType && b instanceof IntegerType )
			return Collections.<Type>singletonList(IntType.v());
		else if ( a instanceof PrimType || b instanceof PrimType )
			return Collections.<Type>emptyList();
		else if ( a instanceof NullType )
			return Collections.<Type>singletonList(b);
		else if ( b instanceof NullType )
			return Collections.<Type>singletonList(a);
		// a and b are both ArrayType or RefType
		else if ( a instanceof ArrayType && b instanceof ArrayType )
		{
			Type eta = ((ArrayType)a).getElementType(),
				etb = ((ArrayType)b).getElementType();
			Collection<Type> ts;
			
			// Primitive arrays are not covariant but all other arrays are
			if ( eta instanceof PrimType || eta instanceof PrimType )
				ts = Collections.<Type>emptyList();
			else
				ts = lcas_(eta, etb);
			
			LinkedList<Type> r = new LinkedList<Type>();
			if ( ts.isEmpty() )
			{
				r.add(RefType.v("java.io.Serializable"));
				r.add(RefType.v("java.lang.Cloneable"));
			}
			else
				for ( Type t : ts )
					r.add(t.makeArrayType());
			return r;
		}
		else if ( a instanceof ArrayType || b instanceof ArrayType )
		{
			Type rt;
			if ( a instanceof ArrayType )
				rt = b;
			else
				rt = a;
			
			/* If the reference type implements Serializable or Cloneable then 
			these are the least common supertypes, otherwise the only one is 
			Object. */
			
			LinkedList<Type> r = new LinkedList<Type>();
			/* Do not consider Object to be a subtype of Serializable or Cloneable
			(it can appear this way if phantom-refs is enabled and rt.jar is not
			available) otherwise an infinite loop can result. */
			if (!TypeResolver.typesEqual(RefType.v("java.lang.Object"), rt)) {
			    if ( ancestor_(RefType.v("java.io.Serializable"), rt) )
			        r.add(RefType.v("java.io.Serializable"));
			    if ( ancestor_(RefType.v("java.lang.Cloneable"), rt) )
			        r.add(RefType.v("java.lang.Cloneable"));
			}
			
			if ( r.isEmpty() )
				r.add(RefType.v("java.lang.Object"));
			return r;
		}
		// a and b are both RefType
		else
		{
			Collection<AncestryTreeNode> treea = buildAncestryTree((RefType)a),
				treeb = buildAncestryTree((RefType)b);
			
			LinkedList<Type> r = new LinkedList<Type>();
			for ( AncestryTreeNode nodea : treea )
				for ( AncestryTreeNode nodeb : treeb )
				{
					RefType t = leastCommonNode(nodea, nodeb);
					
					boolean least = true;
					for ( ListIterator<Type> i = r.listIterator(); i.hasNext(); )
					{
						Type t_ = i.next();
						
						if ( ancestor_(t, t_) )
						{
							least = false;
							break;
						}
						
						if ( ancestor_(t_, t) )
							i.remove();
					}
					
					if ( least )
						r.add(t);
				}
			
			//in case of phantom classes that screw up type resolution here,
			//default to only possible common reftype, java.lang.Object
			//kludge on a kludge on a kludge...
			//syed - 05/06/2009
			if ( r.isEmpty() )
				r.add(RefType.v("java.lang.Object"));
			return r;
		}
	}
	
	public boolean ancestor(Type ancestor, Type child)
	{
		return ancestor_(ancestor, child);
	}
	
	public static boolean ancestor_(Type ancestor, Type child)
	{
		if ( TypeResolver.typesEqual(ancestor, child) )
			return true;
		else if ( child instanceof BottomType )
			return true;
		else if ( ancestor instanceof BottomType )
			return false;
		else if ( ancestor instanceof IntegerType
			&& child instanceof IntegerType )
			return true;
		else if ( ancestor instanceof PrimType || child instanceof PrimType )
			return false;
		else if ( child instanceof NullType )
			return true;
		else if ( ancestor instanceof NullType )
			return false;
		else return Scene.v().getOrMakeFastHierarchy().canStoreType(
			child, ancestor);
	}
	
	private static LinkedList<RefType> superclassPath(RefType t)
	{
		LinkedList<RefType> r = new LinkedList<RefType>();
		r.addFirst(t);
		
		SootClass sc = t.getSootClass();
		while ( sc.hasSuperclass() )
		{
			sc = sc.getSuperclass();
			r.addFirst((RefType)sc.getType());
		}
		
		return r;
	}
	
	public static RefType lcsc(RefType a, RefType b)
	{
		LinkedList<RefType> pathA = superclassPath(a),
			pathB = superclassPath(b);
		RefType r = null;
		while ( !(pathA.isEmpty() || pathB.isEmpty()) 
			&& TypeResolver.typesEqual(pathA.getFirst(), pathB.getFirst()) )
		{
			r = pathA.removeFirst();
			pathB.removeFirst();
		}
		return r;
	}
}