/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
  
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Coffi, a bytecode parser for the Java(TM) language.               *
 * Copyright (C) 1996, 1997 Clark Verbrugge (clump@sable.mcgill.ca). *
 * All rights reserved.                                              *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.
 The reference version is: $CoffiVersion: 1.1 $
                           $SootVersion$

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on September 3, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Improved the error message on merge failure.

 - Modified on July 23, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Added a clone method.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.coffi;

import java.io.*;

import ca.mcgill.sable.soot.*;

/*
 * A less resource hungry implementation of the TypeStack would just have pointers to
 * 'sub-stacks' instead of copying the entire array around.
 */
 
class TypeStack implements ca.mcgill.sable.util.ValueObject
{
    private static SootClassManager cm;
    
    private Type[] types;
    
    private TypeStack() 
    {
        // no constructor
    }  
    
    public static void setClassManager(SootClassManager cm)
    {
        TypeStack.cm = cm;
    }

    public Object clone()
    {
        TypeStack newTypeStack = new TypeStack();
        
        newTypeStack.cm = this.cm;
        newTypeStack.types = (Type[]) types.clone();
        
        return newTypeStack;
    }
        
    /**
     * Returns an empty stack.
     */
     
    public static TypeStack v()
    {
        TypeStack typeStack = new TypeStack();
        
        typeStack.types = new Type[0];
        
        return typeStack;
    }
    
    public TypeStack pop()
    {
        TypeStack newStack = new TypeStack();
        
        newStack.types = new Type[types.length - 1];
        System.arraycopy(types, 0, newStack.types, 0, types.length - 1);
        
        return newStack;
    }
    
    public TypeStack push(Type type)
    {
        TypeStack newStack = new TypeStack();
        
        newStack.types = new Type[types.length + 1];
        System.arraycopy(types, 0, newStack.types, 0, types.length);
        
        newStack.types[types.length] = type;
         
        return newStack;
    }
    
    public Type get(int index)
    {
        return types[index];
    }
    
    public int topIndex()
    {
        return types.length - 1;
    }
    
    public Type top()
    {
        if(types.length == 0)
            throw new RuntimeException("TypeStack is empty");
        else
            return types[types.length - 1];
    }
    
    public boolean equals(Object object)
    {
        if(object instanceof TypeStack)
        {
            TypeStack otherStack = (TypeStack) object;
             
            if(otherStack.types.length != types.length)
                return false;
                
            for(int i = 0; i < types.length; i++)
                if(!types[i].equals(otherStack.types[i]))
                    return false;
                    
            return true;
        }
        else
            return false;
    }
    
    public TypeStack merge(TypeStack other)
    {
        
        if(types.length != other.types.length)
            throw new RuntimeException("TypeStack merging failed; unequal " +
            "stack lengths: " + types.length + " and " + other.types.length);
            
        TypeStack newStack = new TypeStack();
        
        newStack.types = new Type[other.types.length];
        
        for(int i = 0; i < types.length; i++)
            if(types[i].equals(other.types[i]))
                newStack.types[i] = types[i];
            else {
                if(!(types[i] instanceof RefType) || !(other.types[i] instanceof 
                    RefType))
                {
                    throw new RuntimeException("TypeStack merging failed; incompatible types " + types[i] + " and " + other.types[i]);
                } 
                
                // System.out.println("Merging: " + types[i] + " with " + other.types[i]);
                
                newStack.types[i] = RefType.v("java.lang.Object");
            }
            
        return newStack;
    }
    
    public void print(PrintStream out)
    {
        for(int i = types.length - 1; i >= 0; i--)
            out.println(i + ": " + types[i].toString());
            
        if(types.length == 0)
            out.println("<empty>");
    }
}
