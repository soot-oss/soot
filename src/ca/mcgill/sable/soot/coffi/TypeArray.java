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
   Relaxed the type merge conditions.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.coffi;

import java.io.*;
import ca.mcgill.sable.soot.*;

class TypeArray implements ca.mcgill.sable.util.ValueObject
{
    private static Scene cm;

    private Type[] types;

    public static void setClassManager(Scene cm)
    {
        TypeArray.cm = cm;
    }

    private TypeArray()
    {
    }

    /**
     * Returns an empty array of types.
     *
     */

    public static TypeArray v(int size)
    {
        TypeArray newArray = new TypeArray();

        newArray.types = new Type[size];

        for(int i =  0; i < size; i++)
            newArray.types[i] = UnusuableType.v();

        return newArray;
    }

    public Type get(int index)
    {
        return types[index];
    }

    public TypeArray set(int index, Type type)
    {
        TypeArray newArray = new TypeArray();

        newArray.types = (Type[]) types.clone();
        newArray.types[index] = type;

        return newArray;
    }

    public boolean equals(Object obj)
    {
        if(obj instanceof TypeArray)
        {
            TypeArray other = (TypeArray) obj;

            if(types.length != other.types.length)
                return false;

            for(int i = 0; i < types.length; i++)
                if(!types[i].equals(other.types[i]))
                    return false;

            return true;
        }
        else
            return false;
    }

    public TypeArray merge(TypeArray otherArray)
    {
        TypeArray newArray = new TypeArray();

        if(types.length != otherArray.types.length)
            throw new RuntimeException("Merging of type arrays failed; unequal array length");

        newArray.types = new Type[types.length];

        for(int i = 0; i < types.length; i++)
        {
            if(types[i].equals(otherArray.types[i]))
                newArray.types[i] = types[i];
            else if((types[i] instanceof ArrayType ||
                types[i] instanceof RefType) &&
                (otherArray.types[i] instanceof ArrayType
                    || otherArray.types[i] instanceof RefType))
            {
                // This type merge does not need to be accurate, because it is not really used

                newArray.types[i] = RefType.v("java.lang.Object");
            }
            else {
                newArray.types[i] = UnusuableType.v();
            }
        }
        return newArray;
    }

    public void print(PrintStream out)
    {
        for(int i = 0; i < types.length; i++)
            out.println(i + ": " + types[i].toString());
    }
}


