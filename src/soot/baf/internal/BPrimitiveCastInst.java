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

package soot.baf.internal;

import soot.*;
import soot.baf.*;
import soot.util.*;
import java.util.*;

public class BPrimitiveCastInst extends AbstractInst 
                            implements PrimitiveCastInst
{
    Type fromType;
    
    protected Type toType;

    public int getInCount()
    {
        return 1;
    }

    public int getInMachineCount()
    {
        return JasminClass.sizeOfType(fromType);
    }
    
    public int getOutCount()
    {
        return 1;
    }

    public int getOutMachineCount()
    {
        return JasminClass.sizeOfType(toType);
    }

    
    public BPrimitiveCastInst(Type fromType, Type toType) 
    { 
        
        this.fromType = fromType;
        this.toType = toType;
    }

    
    public Object clone() 
    {
        return new BPrimitiveCastInst(getFromType(), toType);
    }



    // after changing the types, use getName to check validity
    public Type getFromType() { return fromType; }
    public void setFromType(Type t) { fromType = t;}
    
    public Type getToType() { return toType; }
    public void setToType(Type t) { toType = t;}

    final public String getName() 
    {
        TypeSwitch sw;

        fromType.apply(sw = new TypeSwitch()
        {
            public void defaultCase(Type ty)
                {
                    throw new RuntimeException("invalid fromType " + fromType);
                }

            public void caseDoubleType(DoubleType ty)
                {
                    if(toType.equals(IntType.v()))
                        setResult("d2i");
                    else if(toType.equals(LongType.v()))
                        setResult("d2l");
                    else if(toType.equals(FloatType.v()))
                        setResult("d2f");
                    else
                        throw new RuntimeException
                            ("invalid toType from double: " + toType);
                }

            public void caseFloatType(FloatType ty)
                {
                    if(toType.equals(IntType.v()))
                        setResult("f2i");
                    else if(toType.equals(LongType.v()))
                        setResult("f2l");
                    else if(toType.equals(DoubleType.v()))
                        setResult("f2d");
                    else
                        throw new RuntimeException
                            ("invalid toType from float: " + toType);
                }

            public void caseIntType(IntType ty)
                {
                    emitIntToTypeCast();
                }

            public void caseBooleanType(BooleanType ty)
                {
                    emitIntToTypeCast();
                }

            public void caseByteType(ByteType ty)
                {
                    emitIntToTypeCast();
                }

            public void caseCharType(CharType ty)
                {
                    emitIntToTypeCast();
                }
            
            public void caseShortType(ShortType ty)
                {
                    emitIntToTypeCast();
                }

            private void emitIntToTypeCast()
                {
                    if(toType.equals(ByteType.v()))
                        setResult("i2b");
                    else if(toType.equals(CharType.v()))
                        setResult("i2c");
                    else if(toType.equals(ShortType.v()))
                        setResult("i2s");
                    else if(toType.equals(FloatType.v()))
                        setResult("i2f");
                    else if(toType.equals(LongType.v()))
                        setResult("i2l");
                    else if(toType.equals(DoubleType.v()))
                        setResult("i2d");
                    else if(toType.equals(IntType.v()))
                            ;  // this shouldn't happen?
                    else
                        throw new RuntimeException
                            ("invalid toType from int: " + toType);
                }

            public void caseLongType(LongType ty)
                {
                    if(toType.equals(IntType.v()))
                        setResult("l2i");
                    else if(toType.equals(FloatType.v()))
                        setResult("l2f");
                    else if(toType.equals(DoubleType.v()))
                        setResult("l2d");
                    else
                        throw new RuntimeException
                              ("invalid toType from long: " + toType);
                            
                }
        });
        return (String)sw.getResult();
    }

    /* override toString with our own, *not* including types */
    protected String toString(boolean isBrief, Map unitToName, String indentation)
    {
        return indentation + getName() + 
            getParameters(isBrief, unitToName);
    }

    public void apply(Switch sw)
    {
        ((InstSwitch) sw).casePrimitiveCastInst(this);
    }   
}

