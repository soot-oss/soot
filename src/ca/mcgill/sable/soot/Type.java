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

import ca.mcgill.sable.util.*;

public abstract class Type implements ca.mcgill.sable.util.ValueObject, Switchable, ToBriefString
{
    public abstract String toString();
    
    public String toBriefString()
    {
        return toString();
    }
    
    public static Type toMachineType(Type t)
    {
        if(t.equals(ShortType.v()) || t.equals(ByteType.v()) ||
            t.equals(BooleanType.v()) || t.equals(CharType.v()))
        {
            return IntType.v();
        }
        else
            return t;
    }

    public Type merge(Type other, SootClassManager cm)
    {
        if(this.equals(UnknownType.v()))
            return other;
        else if(other.equals(UnknownType.v()))
            return this;
        else if(this.equals(other))
            return this;
        else if(this instanceof RefType && other instanceof RefType)
        {
            // Return least common superclass

            SootClass thisClass = cm.getClass(((RefType) this).className);
            SootClass otherClass = cm.getClass(((RefType) other).className);
            SootClass javalangObject = cm.getClass("java.lang.Object");

            LinkedList thisHierarchy = new LinkedList();
            LinkedList otherHierarchy = new LinkedList();

            // Build thisHierarchy
            {
                SootClass SootClass = thisClass;

                for(;;)
                {
                    thisHierarchy.addFirst(SootClass);

                    if(SootClass == javalangObject)
                        break;

                    SootClass = SootClass.getSuperClass();
                }
            }

            // Build otherHierarchy
            {
                SootClass SootClass = otherClass;

                for(;;)
                {
                    otherHierarchy.addFirst(SootClass);

                    if(SootClass == javalangObject)
                        break;

                    SootClass = SootClass.getSuperClass();
                }
            }

            // Find least common superclass
            {
                SootClass commonClass = null;

                while(!otherHierarchy.isEmpty() && !thisHierarchy.isEmpty() &&
                    otherHierarchy.getFirst() == thisHierarchy.getFirst())
                {
                    commonClass = (SootClass) otherHierarchy.removeFirst();
                    thisHierarchy.removeFirst();
                }

                return RefType.v(commonClass.getName());
            }
        }
        else
            throw new IllegalTypeMergeException(this + " and " + other);
    }

    public void apply(Switch sw)
    {
    }

}
