/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Patrick Lam (plam@sable.mcgill.ca) are           *
 * Copyright (C) 1999 Patrick Lam.  All rights reserved.             *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
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

 - Modified on April 19, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Added a conserve locals option.
   Changed NO_PACKING to USE_PACKING.
   
 - Modified on February 3, 1999 by Patrick Lam (plam@sable.mcgill.ca) (*)
   Added changes in support of the Grimp intermediate
   representation (with aggregated-expressions).

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package soot.jimple;

import soot.*;
import soot.util.*;
import java.util.*;
import java.io.*;

public class BuildJimpleBodyOption
{
    public static final int NO_TYPING               = 0x0001,
                            NO_RENAMING             = 0x0002,
                            NO_SPLITTING            = 0x0004,
                            USE_PACKING             = 0x0010,
                            NO_AGGREGATING          = 0x0020,
                            USE_ORIGINAL_NAMES      = 0x0040,
                            AGGRESSIVE_AGGREGATING  = 0x0008;

    public static boolean noTyping(int m)
    {
        return (m & NO_TYPING) != 0;
    }

    public static boolean noRenaming(int m)
    {
        return (m & NO_RENAMING) != 0;
    }

    public static boolean noSplitting(int m)
    {
        return (m & NO_SPLITTING) != 0;
    }

    public static boolean usePacking(int m)
    {
        return (m & USE_PACKING) != 0;
    }

    public static boolean noAggregating(int m)
    {
        return (m & NO_AGGREGATING) != 0;
    }

    public static boolean aggressiveAggregating(int m)
    {
        return (m & AGGRESSIVE_AGGREGATING) != 0;
    }
    
    public static boolean useOriginalNames(int m)
    {
        return (m & USE_ORIGINAL_NAMES) != 0;
    }
}

