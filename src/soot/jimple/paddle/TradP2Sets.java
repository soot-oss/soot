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

/** Manages the points-to sets for nodes.
 * @author Ondrej Lhotak
 */
public class TradP2Sets extends AbsP2Sets
{ 
    private P2SetMap vnToSet = new P2SetMap( PaddleNumberers.v().varNodeNumberer() );
    private P2SetMap adfToSet = new P2SetMap( PaddleNumberers.v().allocDotFieldNumberer() );
    public PointsToSetReadOnly get( VarNode v ) {
        return vnToSet.get(v);
    }
    public PointsToSetReadOnly get( AllocDotField adf ) {
        return adfToSet.get(adf);
    }
    public PointsToSetInternal make( VarNode v ) {
        return vnToSet.make(v);
    }
    public PointsToSetInternal make( AllocDotField adf ) {
        return adfToSet.make(adf);
    }
}

