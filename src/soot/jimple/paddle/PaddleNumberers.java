/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002, 2003 Ondrej Lhotak
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

/** Numberers for Paddle nodes.
 * @author Ondrej Lhotak
 */

public class PaddleNumberers {
    public PaddleNumberers( Singletons.Global g ) {}
    public static PaddleNumberers v() { return G.v().soot_jimple_paddle_PaddleNumberers(); }

    private ArrayNumberer allocNodeNumberer = new ArrayNumberer();
    public ArrayNumberer allocNodeNumberer() { return allocNodeNumberer; }
    private ArrayNumberer varNodeNumberer = new ArrayNumberer();
    public ArrayNumberer varNodeNumberer() { return varNodeNumberer; }
    private ArrayNumberer fieldRefNodeNumberer = new ArrayNumberer();
    public ArrayNumberer fieldRefNodeNumberer() { return fieldRefNodeNumberer; }
    private ArrayNumberer allocDotFieldNumberer = new ArrayNumberer();
    public ArrayNumberer allocDotFieldNumberer() { return allocDotFieldNumberer; }
    private ArrayNumberer contextVarNodeNumberer = new ArrayNumberer();
    public ArrayNumberer contextVarNodeNumberer() { return contextVarNodeNumberer; }
    private ArrayNumberer contextAllocNodeNumberer = new ArrayNumberer();
    public ArrayNumberer contextAllocNodeNumberer() { return contextAllocNodeNumberer; }
}

