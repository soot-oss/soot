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
public class AugHierarchy implements IHierarchy
{	
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
		{
			if ( a instanceof Integer1Type )
				return Collections.<Type>singletonList(b);
			else if ( b instanceof Integer1Type )
				return Collections.<Type>singletonList(a);
			else if ( a instanceof BooleanType || b instanceof BooleanType )
				return Collections.<Type>emptyList();
			else if ( (a instanceof ByteType && b instanceof Integer32767Type)
				|| (b instanceof ByteType && a instanceof Integer32767Type) )
				return Collections.<Type>singletonList(ShortType.v());
			else if ( (a instanceof CharType && (b instanceof ShortType
				|| b instanceof ByteType)) || (b instanceof CharType
				&& (a instanceof ShortType || a instanceof ByteType)) )
				return Collections.<Type>singletonList(IntType.v());
			else if ( ancestor_(a, b) )
				return Collections.<Type>singletonList(a);
			else return Collections.<Type>singletonList(b);
		}
		else if ( a instanceof IntegerType || b instanceof IntegerType )
			return Collections.<Type>emptyList();
		else return BytecodeHierarchy.lcas_(a, b);
	}
	
	public boolean ancestor(Type ancestor, Type child)
	{
		return ancestor_(ancestor, child);
	}
	
	public static boolean ancestor_(Type ancestor, Type child)
	{
		if ( TypeResolver.typesEqual(ancestor, child) )
			return true;
		else if ( ancestor instanceof Integer1Type )
		{
			if ( child instanceof BottomType )
				return true;
			else return false;
		}
		else if ( ancestor instanceof BooleanType )
		{
			if ( child instanceof BottomType
				|| child instanceof Integer1Type )
				return true;
			else return false;
		}
		else if ( ancestor instanceof Integer127Type )
		{
			if ( child instanceof BottomType
				|| child instanceof Integer1Type )
				return true;
			else return false;
		}
		else if ( ancestor instanceof ByteType
			|| ancestor instanceof Integer32767Type )
		{
			if ( child instanceof BottomType
				|| child instanceof Integer1Type
				|| child instanceof Integer127Type )
				return true;
			else return false;
		}
		else if ( ancestor instanceof CharType )
		{
			if ( child instanceof BottomType
				|| child instanceof Integer1Type
				|| child instanceof Integer127Type
				|| child instanceof Integer32767Type )
				return true;
			else return false;
		}
		else if ( ancestor instanceof ShortType )
		{
			if ( child instanceof BottomType
				|| child instanceof Integer1Type
				|| child instanceof Integer127Type
				|| child instanceof Integer32767Type
				|| child instanceof ByteType )
				return true;
			else return false;
		}
		else if ( ancestor instanceof IntType )
		{
			if ( child instanceof BottomType
				|| child instanceof Integer1Type
				|| child instanceof Integer127Type
				|| child instanceof Integer32767Type
				|| child instanceof ByteType
				|| child instanceof CharType
				|| child instanceof ShortType )
				return true;
			else return false;
		}
		else if ( child instanceof IntegerType )
			return false;
		else return BytecodeHierarchy.ancestor_(ancestor, child);
	}
}