/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Soot, a Java(TM) classfile optimization framework.                *
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

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.
 The reference version is: $SootVersion$

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

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/
 
package ca.mcgill.sable.soot;

// Incomplete class

public class Modifier
{
    public static final int ABSTRACT =     0x0400;
    public static final int FINAL =        0x0010;
    public static final int INTERFACE =    0x0200;
    public static final int NATIVE =       0x0100;
    public static final int PRIVATE =      0x0002;
    public static final int PROTECTED =    0x0004;
    public static final int PUBLIC =       0x0001;
    public static final int STATIC =       0x0008;
    public static final int SYNCHRONIZED = 0x0020;
    public static final int TRANSIENT =    0x0080;
    public static final int VOLATILE =     0x0040;

    private Modifier()
    {
    }

    public static boolean isAbstract(int m)
    {
        return (m & ABSTRACT) != 0;
    }
    
    public static boolean isFinal(int m )
    {
        return (m & FINAL) != 0;
    }
    
    public static boolean isInterface(int m)
    {
        return (m & INTERFACE) != 0;
    }
    
    public static boolean isNative(int m)
    {
        return (m & NATIVE) != 0;
    }
    
    public static boolean isPrivate(int m)
    {
        return (m & PRIVATE) != 0;
    }
    
    public static boolean isProtected(int m)
    {
        return (m & PROTECTED) != 0;
    }
    
    public static boolean isPublic(int m)
    {
        return (m & PUBLIC) != 0;
    }
    
    public static boolean isStatic(int m)
    {
        return (m & STATIC) != 0;
    }
    
    public static boolean isSynchronized(int m)
    {
        return (m & SYNCHRONIZED) != 0;
    }
    
    public static boolean isTransient(int m )
    {
        return (m & TRANSIENT) != 0;
    }
    
    public static boolean isVolatile(int m)
    {
        return (m & VOLATILE) != 0;
    }
    
    /**
     * Converts the given modifiers to their string representation, in canonical form.  
     */
     
    public static String toString(int m)
    {
        StringBuffer buffer = new StringBuffer();
        
        if(isPublic(m))
            buffer.append("public "); 
        else if(isPrivate(m))
            buffer.append("private ");
        else if(isProtected(m))
            buffer.append("protected ");
            
        if(isAbstract(m))
            buffer.append("abstract ");

        if(isInterface(m))
            buffer.append("interface ");
                        
        if(isStatic(m))
            buffer.append("static ");
            
        if(isFinal(m))
            buffer.append("final ");
            
        if(isSynchronized(m))
            buffer.append("synchronized ");
            
        if(isNative(m))
            buffer.append("native ");
        
        if(isTransient(m))
            buffer.append("transient ");
    
        if(isVolatile(m))
            buffer.append("volatile ");
                        
        return (buffer.toString()).trim();
    }
}



