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
import soot.jimple.*;
import soot.shimple.internal.*;
import soot.util.*;
import java.util.*;
import java.io.*;

/**
 * Contains the constructors for the components of the Shimple (SSA
 * Jimple) grammar.  Methods are available to construct Shimple from
 * Jimple/Shimple, create PHI expressions, and converting back from
 * Shimple to Jimple.
 *
 * <p> We did not replicate those elements already available from
 * Jimple.v().
 *
 * @author Navindra Umanee
 * @see soot.shimple.Shimple
 * @see <a
 * href="http://citeseer.nj.nec.com/cytron91efficiently.html">Efficiently
 * Computing Static Single Assignment Form and the Control Dependence
 * Graph</a>
**/
public class Shimple
{
    private static Shimple shimpleRepresentation = new Shimple();

    protected Shimple()
    {
    }

    public static Shimple v()
    {
        return shimpleRepresentation;
    }

    /**
     * Returns an empty ShimpleBody associated with method m.
     **/
    public ShimpleBody newBody(SootMethod m)
    {
        return new ShimpleBody(m);
    }

    /**
     * Returns a ShimpleBody constructed from b.
     **/
    public ShimpleBody newBody(Body b, String phase)
    {
        Map options = Scene.v().getPhaseOptions(phase);
        return new ShimpleBody(b, options);
    }

    /**
     * Returns a ShimpleBody constructed from b with given options.
     *
     * <p> Currently available option is "naive-phi-elimination",
     * typically specified in the "shimple" phase (eg, -p shimple
     * naive-phi-elimination) which skips the dead code elimination
     * and register allocation phase before eliminating PHI nodes.
     * This can be useful for understanding the effect of analyses.
     **/
    public ShimpleBody newBody(Body b, String phase, String optionsString)
    {
        Map options = Scene.v().computePhaseOptions(phase, optionsString);
        return new ShimpleBody(b, options);
    }

    /**
     * Create a trivial PHI-function, where numberOfControlPreds
     * determines the number of leftLocal arguments required.
     **/
    public PhiExpr newPhiExpr(Local leftLocal, int numberOfControlPreds)
    {
        return new SPhiExpr(leftLocal, numberOfControlPreds);
    }

    /**
     * Create a PHI-function with the provided list of Values (Locals
     * or Constants).
     **/
    public PhiExpr newPhiExpr(List args)
    {
        return new SPhiExpr(args);
    }

    /**
     * Constructs a JimpleBody from a ShimpleBody.
     *
     * <p> Currently available option is "naive-phi-elimination",
     * typically specified in the "shimple" phase (eg, -p shimple
     * naive-phi-elimination) which skips the dead code elimination
     * and register allocation phase before eliminating PHI nodes.
     * This can be useful for understanding the effect of analyses.
     **/
    public JimpleBody newJimpleBody(ShimpleBody body)
    {
        return body.toJimpleBody();
    }
}
