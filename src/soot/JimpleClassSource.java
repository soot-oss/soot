/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
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

package soot;
import soot.options.*;
import soot.jimple.*;
import java.io.*;
import java.util.*;

/** A class source for resolving from .jimple files using the Jimple parser.
 */
public class JimpleClassSource extends ClassSource
{
    public JimpleClassSource( String className, InputStream classFile ) {
        super( className );
        this.classFile = classFile;
    }
    public List resolve( SootClass sc ) {
        if(Options.v().verbose())
            G.v().out.println("resolving [from .jimple]: " + className );
        
        soot.jimple.parser.JimpleAST jimpAST =
            new soot.jimple.parser.JimpleAST(classFile);                
        jimpAST.getSkeleton(sc);
        JimpleMethodSource mtdSrc = new JimpleMethodSource(jimpAST);

        Iterator mtdIt = sc.methodIterator();
        while(mtdIt.hasNext()) {
            SootMethod sm = (SootMethod) mtdIt.next();
            sm.setSource(mtdSrc);
        }
        
        List ret = new ArrayList(jimpAST.getCstPool());

        try {
            classFile.close();
        } catch (IOException e) { throw new RuntimeException("!?"); }
        return ret;
    }
    protected InputStream classFile;
}

