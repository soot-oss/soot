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
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;

/**
 * A Phi node is a function with a list of Values (Locals or
 * Constants) as args.
 *
 * <p> The semantics are as described in the referenced paper by
 * Cytron et al., TOPLAS Oct. 91.  A Phi node such as "x_1 = Phi(x_2,
 * x_3)" is eliminated by respectively adding the statements "x_1 =
 * x_2" and "x_1 = x_3" at the end of the corresponding control flow
 * predecessor.
 *
 * @author Navindra Umanee
 * @see
 <a
 * href="http://citeseer.nj.nec.com/cytron91efficiently.html">Efficiently
 * Computing Static Single Assignment Form and the Control Dependence
 * Graph</a>
 **/
public interface PhiExpr extends Expr, UnitBoxOwner
{
    public Value getValueArg(int index);

    /**
     * Get the Phi argument corresponding to the given control flow
     * predecessor, returns null if not available.
     **/
    public Value getValueArg(Block pred);

    /**
     * Get the Phi argument corresponding to the given control flow
     * predecessor, returns null if not available.
     **/
    public Value getValueArg(Unit predTailUnit);

    
    public List getValueArgs();
    public Unit getPredArg(int index);
    public List getPredArgs();

    /**
     * Returns -1 if there is no Phi argument for the given control
     * flow predecessor.
     **/
    public int getArgIndex(Block pred);

    /**
     * Returns -1 if there is no Phi argument for the given control
     * flow predecessor.
     **/
    public int getArgIndex(Unit predTailUnit);

    public ValueUnitPair getArgBox(int index);
    public int getArgCount();
    public List getArgs();
    public boolean setArg(int index, Value arg, Block pred);
    public boolean setArg(int index, Value arg, Unit predTailUnit);

    /**
     * @see #setPredArg(int, Block)
     * @see #setPredArg(int, Unit)
     **/
    public boolean setValueArg(int index, Value arg);

    /**
     * A fully defined PhiExpr requires a link to the matching control
     * flow predecessor for each argument.
     * 
     * <p> If a PhiExpr is not fully defined, then the algorithm for
     * eliminating Phi nodes may fail.
     **/
    public boolean setPredArg(int index, Block pred);
    
    /**
     * A fully defined PhiExpr requires a link to the last Unit in
     * the matching control flow predecessor for each argument.
     * 
     * <p> If a PhiExpr is not fully defined, then the algorithm for
     * eliminating Phi nodes may fail.
     **/
    public boolean setPredArg(int index, Unit predTailUnit);

    public boolean removeArg(int index);
    public boolean removeArg(ValueUnitPair arg);
    public void addArg(Value arg, Block pred);
    public void addArg(Value arg, Unit predTailUnit);

    /**
     * The type of the PhiExpr is usually the type of its arguments.
     **/
    public Type getType();

    public void apply(Switch sw);
}
