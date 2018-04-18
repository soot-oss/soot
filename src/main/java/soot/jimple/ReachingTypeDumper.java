/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
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

package soot.jimple;
import java.util.*;

import soot.*;

import java.io.*;

/** Dumps the reaching types of each local variable to a file in a format that
 * can be easily compared with results of other analyses, such as VTA.
 * @author Ondrej Lhotak
 */
public class ReachingTypeDumper {
    private static class StringComparator<T> implements Comparator<T> {
        public int compare( T o1, T o2 ) {
            return o1.toString().compareTo( o2.toString() );
        }
    }
    
    public ReachingTypeDumper( PointsToAnalysis pa, String output_dir ) {
        this.pa = pa;
        this.output_dir = output_dir;
    }
    
    public void dump() {
        try {
            PrintWriter file = new PrintWriter(
                new FileOutputStream( new File(output_dir, "types") ) );
            for (SootClass cls : Scene.v().getApplicationClasses()) {
            	handleClass( file, cls );
            }
            for (SootClass cls : Scene.v().getLibraryClasses()) {
            	handleClass( file, cls );
            }
            file.close();
        } catch( IOException e ) {
            throw new RuntimeException( "Couldn't dump reaching types."+e );
        }
    }


    /* End of public methods. */
    /* End of package methods. */

    protected PointsToAnalysis pa;
    protected String output_dir;

    protected void handleClass( PrintWriter out, SootClass c ) {
    	for (SootMethod m : c.getMethods()) {            
            if( !m.isConcrete() ) continue;
            Body b = m.retrieveActiveBody();
                        
            Local[] sortedLocals = b.getLocals().toArray(new Local[b.getLocalCount()]);
            Arrays.sort(sortedLocals, new StringComparator<Local>());
            
            for (Local l : sortedLocals) {
                out.println( "V "+m+l );
                if( l.getType() instanceof RefLikeType ) {
                    Set<Type> types = pa.reachingObjects( l ).possibleTypes();
                    
                    Type[] sortedTypes = types.toArray(new Type[types.size()]);
                    Arrays.sort(sortedTypes, new StringComparator<Type>());
                    
                    for (Type type : sortedTypes) {
                        out.println("T " + type);
                    }
                }
            }
        }
    }
}

