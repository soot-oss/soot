/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1998, 1999 Etienne Gagnon (gagnon@sable.mcgill.ca). *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Raja Vallee-Rai (rvallerai@sable.mcgill.ca) are  *
 * Copyright (C) 1998 Raja Vallee-Rai.  All rights reserved.         *
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
 - Modified on January 20, 1999 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Fixed a fixed a basic type array typing problem.

 - Modified on January 15, 1999 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Fixed typing bug in null assignment to array variables.

 - Modified on November 13, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Added type information for @caughtexception

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on October 14, 1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Implemented fast typing algorithm for arrays.

 - Modified on October 1, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Fixed the inference of <<, >> and >>>.

 - Modified on 2-Sep-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   getBaseType() on NewArrayExpr does not always yield a BaseType.

 - Modified on 2-Sep-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Applied Etienne's patch.

 - Modified on July 29, 1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Initial version.

*/

package soot.jimple.toolkits.typing;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;
import java.util.*;

/**
 * This class resolves the type of local variables.
 **/
public class TypeAssigner extends BodyTransformer
{
    private static TypeAssigner instance = new TypeAssigner();
    private TypeAssigner() {}

    public static TypeAssigner v() { return instance; }

    /** Assign types to local variables. **/
    protected void internalTransform(Body b, Map options)
    {
        new TypeResolver((JimpleBody)b);
    }
}

