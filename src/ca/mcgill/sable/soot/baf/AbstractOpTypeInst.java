/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Baf, a Java(TM) bytecode analyzer framework.                      *
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

 - Modified on February 3, 1999 by Patrick Lam (plam@sable.mcgill.ca) (*)
   Added changes in support of the Grimp intermediate
   representation (with aggregated-expressions).

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;

public abstract class AbstractOpTypeInst extends AbstractInst
{
    protected Type opType;

    protected AbstractOpTypeInst(Type opType)
    {
	if(opType instanceof NullType || opType instanceof ArrayType || opType instanceof RefType)
	    opType = RefType.v();
	
        this.opType = opType;
    }
    
    public Type getOpType()
    {
        return opType;
    }
    
    public void setOpType(Type t)
    {
        opType = t;
	if(opType instanceof NullType || opType instanceof ArrayType || opType instanceof RefType)
	    opType = RefType.v();
    }

    private static String bafDescriptorOf(Type type)
    {
        TypeSwitch sw;

        type.apply(sw = new TypeSwitch()
        {
            public void caseBooleanType(BooleanType t)
            {
                setResult("b");
            }

            public void caseByteType(ByteType t)
            {
                setResult("b");
            }

            public void caseCharType(CharType t)
            {
                setResult("c");
            }

            public void caseDoubleType(DoubleType t)
            {
                setResult("d");
            }

            public void caseFloatType(FloatType t)
            {
                setResult("f");
            }

            public void caseIntType(IntType t)
            {
                setResult("i");
            }

            public void caseLongType(LongType t)
            {
                setResult("l");
            }

            public void caseShortType(ShortType t)
            {
                setResult("s");
            }

	    
	    public void defaultCase(Type t)
	    {
                throw new RuntimeException("Invalid type: " + t);
	    }

            public void caseRefType(RefType t)
            {
                setResult("r");
            }


        });

        return (String) sw.getResult();

    }

    /* override AbstractInst's toString with our own, including types */
    protected String toString(boolean isBrief, Map unitToName, String indentation)
    {
        return indentation + getName() + "." + 
          Baf.bafDescriptorOf(opType) + getParameters(isBrief, unitToName);
    }
}
