/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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


package soot;

import soot.util.*;


/**
 *   A class that models Java's array types. ArrayTypes are parametrized by a Type and 
 *   and an integer representing the array's dimension count..
 *   Two ArrayType are 'equal' if they are parametrized equally.
 *
 *
 *
 */
@SuppressWarnings("serial")
public class ArrayType extends RefLikeType
{
    /**
     * baseType can be any type except for an array type, null and void
     *
     * What is the base type of the array? That is, for an array of type
     * A[][][], how do I find out what the A is? The accepted way of
     * doing this has always been to look at the public field baseType
     * in ArrayType, ever since the very beginning of Soot.
     */
    public final Type baseType;
    
    /** dimension count for the array type*/
    public final int numDimensions;

   
    private ArrayType(Type baseType, int numDimensions)
    {
        if( !( baseType instanceof PrimType || baseType instanceof RefType
        		|| baseType instanceof NullType ) )
            throw new RuntimeException( "oops,  base type must be PrimType or RefType but not '"+ baseType +"'" );
        if( numDimensions < 1 ) throw new RuntimeException( "attempt to create array with "+numDimensions+" dimensions" );
        this.baseType = baseType;
        this.numDimensions = numDimensions;
    }

     /** 
     *  Creates an ArrayType  parametrized by a given Type and dimension count.
     *  @param baseType a Type to parametrize the ArrayType
     *  @param numDimensions the dimension count to parametrize the ArrayType.
     *  @return an ArrayType parametrized accrodingly.
     */
    public static ArrayType v(Type baseType, int numDimensions)
    {
    	if (numDimensions < 0)
        	throw new RuntimeException("Invalid number of array dimensions: " + numDimensions);
    	
    	int orgDimensions = numDimensions;
    	Type elementType = baseType;
    	while (numDimensions > 0) {
            ArrayType ret = elementType.getArrayType();
            if( ret == null ) {
                ret = new ArrayType(baseType, orgDimensions - numDimensions + 1);
                elementType.setArrayType( ret );
            }
            elementType = ret;
            numDimensions--;
    	}
    	
        return (ArrayType) elementType;
    }

    /**
     *  Two ArrayType are 'equal' if they are parametrized identically.
     * (ie have same Type and dimension count.
     * @param t object to test for equality
     * @return true if t is an ArrayType and is parametrized identically to this.
     */
    public boolean equals(Object t)
    {
        return t == this;
        /*
        if(t instanceof ArrayType)
        {
            ArrayType arrayType = (ArrayType) t;

            return this.numDimensions == arrayType.numDimensions &&
                this.baseType.equals(arrayType.baseType);
        }
        else
            return false;
            */
    }

    public void toString(UnitPrinter up)
    {
        up.type( baseType );

        for(int i = 0; i < numDimensions; i++)
            up.literal("[]");
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append(baseType.toString());

        for(int i = 0; i < numDimensions; i++)
            buffer.append("[]");

        return buffer.toString();
    }

    public int hashCode()
    {
        return baseType.hashCode() + 0x432E0341 * numDimensions;
    }

    public void apply(Switch sw)
    {
        ((TypeSwitch) sw).caseArrayType(this);
    }

    /**
     * If I have a variable x of declared type t, what is a good
     * declared type for the expression ((Object[]) x)[i]? The
     * getArrayElementType() method in RefLikeType was introduced to
     * answer this question for all classes implementing RefLikeType. If
     * t is an array, then the answer is the same as getElementType().
     * But t could also be Object, Serializable, or Cloneable, which can
     * all hold any array, so then the answer is Object.
     */
    public Type getArrayElementType() {
	return getElementType();
    }
    /**
     * If I get an element of the array, what will be its type? That
     * is, if I have an array a of type A[][][], what is the type of
     * a[] (it's A[][])? The getElementType() method in ArrayType was
     * introduced to answer this question.
     */
    public Type getElementType() {
	if( numDimensions > 1 ) {
	    return ArrayType.v( baseType, numDimensions-1 );
	} else {
	    return baseType;
	}
    }
    public ArrayType makeArrayType() {
        return ArrayType.v( baseType, numDimensions+1 );
    }
    
    public boolean isAllowedInFinalCode() {
    	return true;
    }

}

