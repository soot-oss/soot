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
    public ReachingTypeDumper( PointsToAnalysis pa, String output_dir ) {
        this.pa = pa;
        this.output_dir = output_dir;
    }
    public void dump() {
        try {
            PrintWriter file = new PrintWriter(
                new FileOutputStream( new File(output_dir, "types") ) );
            for( Iterator it = Scene.v().getApplicationClasses().iterator();
                    it.hasNext(); ) {
                handleClass( file, (SootClass) it.next() );
            }
            for( Iterator it = Scene.v().getLibraryClasses().iterator();
                    it.hasNext(); ) {
                handleClass( file, (SootClass) it.next() );
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
        for( Iterator mIt = c.methodIterator(); mIt.hasNext(); ) {
            final SootMethod m = (SootMethod) mIt.next();
            if( !m.isConcrete() ) continue;
            Body b = m.retrieveActiveBody();
            TreeSet sortedLocals = new TreeSet( new StringComparator() );
            sortedLocals.addAll( b.getLocals() );
            for( Iterator lIt = sortedLocals.iterator(); lIt.hasNext(); ) {
                final Local l = (Local) lIt.next();
                out.println( "V "+m+l );
                if( l.getType() instanceof RefLikeType ) {
                    Set types = pa.reachingObjects( l ).possibleTypes();
                    TreeSet sortedTypes = new TreeSet( new StringComparator() );
                    sortedTypes.addAll( types );
                    for( Iterator tIt = sortedTypes.iterator(); tIt.hasNext(); ) {
                        out.println( "T "+tIt.next() );
                    }
                }
            }
        }
    }
    class StringComparator implements Comparator {
        public int compare( Object o1, Object o2 ) {
            return o1.toString().compareTo( o2.toString() );
        }
    }
}

