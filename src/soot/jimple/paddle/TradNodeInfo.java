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
import soot.util.*;
import soot.jimple.paddle.queue.*;
import java.util.*;

/** Keeps track of the type and method of each node.
 * @author Ondrej Lhotak
 */
public class TradNodeInfo extends AbsNodeInfo
{ 
    public TradNodeInfo(
        Rvar_method_type locals,
        Rvar_type globals,
        Robj_method_type localallocs,
        Robj_type globalallocs
        )
    {
        super(locals, globals, localallocs, globalallocs);
    }

    public boolean update() {
        boolean ret = false;
        for( Iterator tIt = locals.iterator(); tIt.hasNext(); ) {
            final Rvar_method_type.Tuple t = (Rvar_method_type.Tuple) tIt.next();
            if( localMap.put(t.var(), t.method()) ) ret = true;
        }
        for( Iterator tIt = globals.iterator(); tIt.hasNext(); ) {
            final Rvar_type.Tuple t = (Rvar_type.Tuple) tIt.next();
            if( globalSet.add(t.var()) ) ret = true;
        }
        for( Iterator tIt = localallocs.iterator(); tIt.hasNext(); ) {
            final Robj_method_type.Tuple t = (Robj_method_type.Tuple) tIt.next();
            if( localallocMap.put(t.obj(), t.method()) ) ret = true;
        }
        for( Iterator tIt = globalallocs.iterator(); tIt.hasNext(); ) {
            final Robj_type.Tuple t = (Robj_type.Tuple) tIt.next();
            if( globalallocSet.add(t.obj()) ) ret = true;
        }
        return ret;
    }

    public SootMethod method(VarNode v) {
        SootMethod ret = (SootMethod) localMap.get(v);
        if( ret == null ) throw new RuntimeException("no method: "+v );
        return ret;
    }
    public boolean global(VarNode v) {
        return globalSet.contains(v);
    }

    public SootMethod method(AllocNode v) {
        SootMethod ret = (SootMethod) localallocMap.get(v);
        if( ret == null ) throw new RuntimeException("no method: "+v );
        return ret;
    }
    public boolean global(AllocNode v) {
        return globalallocSet.contains(v);
    }

    private LargeNumberedMap localMap =
        new LargeNumberedMap(PaddleNumberers.v().varNodeNumberer());
    private NumberedSet globalSet =
        new NumberedSet(PaddleNumberers.v().varNodeNumberer());
    private LargeNumberedMap localallocMap =
        new LargeNumberedMap(PaddleNumberers.v().allocNodeNumberer());
    private NumberedSet globalallocSet =
        new NumberedSet(PaddleNumberers.v().allocNodeNumberer());

}

