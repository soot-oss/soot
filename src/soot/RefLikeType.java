/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
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


package soot;

/** Abstract class for Soot classes that model subtypes of java.lang.Object
 * (ie. object references and arrays)
 * @author Ondrej Lhotak
 */

public abstract class RefLikeType extends Type
{
    /**
     * If I have a variable x of declared type t, what is a good        
     * declared type for the expression ((Object[]) x)[i]? The          
     * getArrayElementType() method in RefLikeType was introduced even  
     * later to answer this question for all classes implementing       
     * RefLikeType. If t is an array, then the answer is the same as    
     * getElementType(). But t could also be Object, Serializable, or   
     * Cloneable, which can all hold any array, so then the answer is   
     * Object.                                                          
     */
    public abstract Type getArrayElementType();
}
