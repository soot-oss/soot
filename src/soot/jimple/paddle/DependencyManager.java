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

package soot.jimple.paddle;
import soot.*;
import soot.jimple.paddle.queue.*;
import java.util.*;
import soot.util.*;

/** This class keeps track of dependencies between queues and Paddle modules,
 * and schedules which modules need to be updated.
 * @author Ondrej Lhotak
 */
public class DependencyManager 
{ 
    private static final boolean DEBUG = false;
    private LinkedList worklist = new LinkedList();
    private Set workset = new HashSet();
    private MultiMap deps = new HashMultiMap();
    private MultiMap precs = new HashMultiMap();
    public DependencyManager() {
    }
    public void invalidate( DepItem item ) {
        if( workset.add(item) ) {
            worklist.addFirst(item);
        }
    }
    public void invalidateAtEnd( DepItem item ) {
        if( workset.add(item) ) {
            worklist.addLast(item);
        }
    }
    private boolean incflow = false;
    public void update() {
        if(incflow) return;
        incflow = true;
worklist:
        while( !worklist.isEmpty() ) {
            DepItem item = (DepItem) worklist.removeFirst();
            workset.remove(item);
            if( !checkPrec(item) ) {
                if(DEBUG) System.out.println( "not updating "+item.getClass() );
                invalidateAtEnd(item);
            } else {
                if(DEBUG) System.out.println( "updating "+item.getClass() );
                if( item.update() ) {
                    for( Iterator depIt = deps.get(item).iterator(); depIt.hasNext(); ) {
                        final DepItem dep = (DepItem) depIt.next();
                        invalidate(dep);
                    }
                }
                if(DEBUG) System.out.println( "done updating "+item.getClass() );
            }
        }
        incflow = false;
    }
    public boolean checkPrec( DepItem item ) {
        for( Iterator precIt = precs.get(item).iterator(); precIt.hasNext(); ) {
            final DepItem prec = (DepItem) precIt.next();
            if( workset.contains(prec) ) {
                return false;
            }
        }
        return true;
    }
    public void addDep( DepItem from, DepItem to ) {
        deps.put(from, to);
    }
    public void addPrec( DepItem from, DepItem to ) {
        precs.put(from, to);
    }
}
