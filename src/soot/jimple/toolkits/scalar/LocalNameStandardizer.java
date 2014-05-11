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






package soot.jimple.toolkits.scalar;

import soot.*;

import java.util.*;

public class LocalNameStandardizer extends BodyTransformer
{
    public LocalNameStandardizer( Singletons.Global g ) {}
    public static LocalNameStandardizer v() { return G.v().soot_jimple_toolkits_scalar_LocalNameStandardizer(); }

    protected void internalTransform(Body body, String phaseName, Map<String,String> options)
    {
        boolean onlyStackName = PhaseOptions.getBoolean(options, "only-stack-locals");

        // Change the names to the standard forms now.
        {
            int objectCount = 0;
            int intCount = 0;
            int longCount = 0;
            int floatCount = 0;
            int doubleCount = 0;
            int addressCount = 0;
            int errorCount = 0;
            int nullCount = 0;

            Iterator<Local> localIt = body.getLocals().iterator();

            while(localIt.hasNext())
            {
                Local l = localIt.next();
                String prefix = "";
                
                if(l.getName().startsWith("$"))
                    prefix = "$";
                else 
                {
                    if (onlyStackName)
                        continue;
                }
                    
                if(l.getType().equals(BooleanType.v()))
                    l.setName(prefix + "z" + intCount++);
                else if(l.getType().equals(ByteType.v()))
                    l.setName(prefix + "b" + longCount++);
                else if(l.getType().equals(ShortType.v()))
                    l.setName(prefix + "s" + longCount++);
                else if(l.getType().equals(CharType.v()))
                    l.setName(prefix + "c" + longCount++);
                else if(l.getType().equals(IntType.v()))
                    l.setName(prefix + "i" + longCount++);
                else if(l.getType().equals(LongType.v()))
                    l.setName(prefix + "l" + longCount++);
                else if(l.getType().equals(DoubleType.v()))
                    l.setName(prefix + "d" + doubleCount++);
                else if(l.getType().equals(FloatType.v()))
                    l.setName(prefix + "f" + floatCount++);
                else if(l.getType().equals(StmtAddressType.v()))
                    l.setName(prefix + "a" + addressCount++);
                else if(l.getType().equals(ErroneousType.v()) ||
                    l.getType().equals(UnknownType.v()))
                {
                    l.setName(prefix + "e" + errorCount++);
                }
                else if(l.getType().equals(NullType.v()))
                    l.setName(prefix + "n" + nullCount++);
                else
                    l.setName(prefix + "r" + objectCount++);
            }
        }
    }
}



