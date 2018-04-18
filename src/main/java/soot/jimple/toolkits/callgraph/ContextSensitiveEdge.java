/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Ondrej Lhotak
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

package soot.jimple.toolkits.callgraph;
import soot.*;
import soot.jimple.*;

/** Represents a single context-sensitive edge in a call graph.
 * @author Ondrej Lhotak
 */
public interface ContextSensitiveEdge 
{ 
    /** The context at the source of the call.
     */
    public Context srcCtxt();

    /** The method in which the call occurs; may be null for calls not
     * occurring in a specific method (eg. implicit calls by the VM)
     */
    public SootMethod src();

    /** The unit at which the call occurs; may be null for calls not
     * occurring at a specific statement (eg. calls in native code)
     */
    public Unit srcUnit();
    public Stmt srcStmt();
    
    /** The context at the target of the call.
     */
    public Context tgtCtxt();

    /** The target method of the call edge. */
    public SootMethod tgt();

    /** The kind of edge. Note: kind should not be tested by other classes;
     *  instead, accessors such as isExplicit() should be added.
     **/
    public Kind kind();
}

