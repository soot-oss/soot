/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Navindra Umanee
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

package soot.shimple;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.jimple.*;

/**
 * A PHI expression is a PHI function with a list of Values (Locals or
 * Constants) as args.  The semantics are as described in "Efficiently
 * Computing Static Single Assignment Form and the Control Dependence
 * Graph" by Cytron et al., TOPLAS Oct. 91.
 *
 * <p> The function names are fairly self-documenting.
 *
 * @author Navindra Umanee
 * @see <a
 * href="http://citeseer.nj.nec.com/cytron91efficiently.html">Efficiently
 * Computing Static Single Assignment Form and the Control Dependence
 * Graph</a>
 **/
public interface PhiExpr extends Expr
{
    public Value getArg(int index);
    public List getArgs();
    public int getArgCount();
    public void setArg(int index, Value arg);
    public Value removeArg(int index);
    public ValueBox getArgBox(int index);
    public String toString();
    public String toBriefString();
    public List getUseBoxes();
    public void apply(Switch sw);
}
