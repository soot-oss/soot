/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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


package soot;
import soot.coffi.*;
import soot.util.*;
import java.util.*;
import java.io.*;
import soot.jimple.parser.*;

/** A MethodSource for methods that don't know where to get Body's from. 
 * @see soot.jimple.JimpleMethodSource
 * @see soot.coffi.CoffiMethodSource */
public class UnknownMethodSource implements MethodSource
{
    UnknownMethodSource()
    {
    }

    public Body getBody(SootMethod m, String phaseName)
    {  
        // we ignore options here.
        // actually we should have default option verbatim,
        // and apply phase options.
        // in fact we probably want to allow different
        // phase options depending on app vs. lib.

        throw new RuntimeException("Can't get body for unknown source!");

//          InputStream classFileStream;

//          try {
//              classFileStream = SourceLocator.getInputStreamOf(m.getDeclaringClass().toString());
//          } 
//          catch(ClassNotFoundException e) {
//              throw new RuntimeException("Can't find jimple file: " + e);                    
//          }
                
//          Parser.parse(classFileStream,  m.getDeclaringClass());
    }
}

