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
import soot.jimple.internal.*;
import soot.shimple.internal.*;
import soot.toolkits.scalar.*;
import soot.util.*;
import java.util.*;

/**
 * Implementation of the Body class for the Shimple IR (SSA Jimple).
 * This class provides methods for maintaining SSA form as well as
 * elimininating SSA form.
 *
 * <p> We decided to hide all the intelligence in
 * internal.ShimpleBodyBuilder for clarity of API.
 *
 * @author Navindra Umanee
 * @see soot.shimple.internal.ShimpleBodyBuilder
 * @see <a
 * href="http://citeseer.nj.nec.com/cytron91efficiently.html">Efficiently
 * Computing Static Single Assignment Form and the Control Dependence
 * Graph</a>
**/
public class ShimpleBody extends StmtBody
{
    /**
     * Holds our options map...
     **/
    protected Map options;

    /**
     * Construct an empty ShimpleBody associated with m.
     **/
    ShimpleBody(SootMethod m)
    {
        super(m);
        options = Scene.v().getPhaseOptions("shimple");
        setIsSSA(true);
    }

    /**
     * Constructs a ShimpleBody from the given Body and options.
     *
     * <p> Currently available option is "naive-phi-elimination",
     * typically in the "shimple" phase (eg, -p shimple
     * naive-phi-elimination) which can be useful for understanding
     * the effect of analyses.
     **/
    ShimpleBody(Body body, Map options)
    {
        super(body.getMethod());

        if (!(body instanceof JimpleBody || body instanceof ShimpleBody))
            throw new RuntimeException("Cannot construct ShimpleBody from given Body type.");

        if(soot.Main.isVerbose)
            System.out.println("[" + getMethod().getName() + "] Constructing ShimpleBody...");

        this.options = options;
        
        importBodyContentsFrom(body);
        
        // Shimplise body
        rebuild(false);
    }

    /**
     * Recompute SSA form.
     *
     * <p> Note: assumes presence of PHI nodes in body. If you *know*
     * there are no PHI nodes present, you may prefer to use
     * rebuild(false) in order to skip some transformations during the
     * PHI elimination process.
     **/
    public void rebuild()
    {
        rebuild(true);
    }

    /**
     * Rebuild SSA form.
     *
     * <p> If there are PHI nodes already present in the body, it is
     * imperative that we specify this so that the algorithm can
     * eliminate them before rebuilding SSA.
     * 
     * <p> The eliminate PHI nodes stage is harmless, but if you
     * *know* that no PHI nodes are present and you wish to avoid the
     * transformations involved in eliminating PHI nodes, use
     * rebuild(false).
     **/
    public void rebuild(boolean hasPhiNodes)
    {
        // *** TODO: Consider possible rebuilding options: minimal,
        // *** pruned, etc.
        
        new ShimpleBodyBuilder(this, options, hasPhiNodes);
        setIsSSA(true);
    }
    
    /**
     * Returns an equivalent unbacked JimpleBody of the current Body
     * by eliminating the PHI nodes.
     *
     * <p> Currently available option is "naive-phi-elimination",
     * typically specified in the "shimple" phase (eg, -p shimple
     * naive-phi-elimination) which skips the dead code elimination
     * and register allocation phase before eliminating PHI nodes.
     * This can be useful for understanding the effect of analyses.
     *
     * <p> Remember to setActiveBody() if necessary in your
     * SootMethod.
     *
     * @see #eliminatePhiNodes()
     **/
    public JimpleBody toJimpleBody()
    {
        ShimpleBody sBody = (ShimpleBody) this.clone();
        sBody.eliminatePhiNodes();
        JimpleBody jBody = Jimple.v().newBody(sBody.getMethod());
        jBody.importBodyContentsFrom(sBody);
        return jBody;
    }
    
    /**
     * Remove PHI-functions from body. SSA form is no longer a given
     * once done.
     *
     * <p> Currently available option is "naive-phi-elimination",
     * typically specified in the "shimple" phase (eg, -p shimple
     * naive-phi-elimination) which skips the dead code elimination
     * and register allocation phase before eliminating PHI nodes.
     * This can be useful for understanding the effect of analyses.
     *
     * @see #toJimpleBody()
     **/
    public void eliminatePhiNodes()
    {
        ShimpleBodyBuilder.eliminatePhiNodes(this, options);
        setIsSSA(false);
    }

    /**
     * Returns a copy of the current ShimpleBody.
     **/
    public Object clone()
    {
        Body b = Shimple.v().newBody(getMethod());
        b.importBodyContentsFrom(this);
        return b;
    }

    /**
     * Set isSSA boolean to indicate whether a ShimpleBody is still in SSA
     * form or not.   Could be useful for book-keeping purposes.
     **/
    protected boolean isSSA = false;

    /**
     * Sets a flag that indicates whether ShimpleBody is still in SSA
     * form after a transformation or not.  It is often up to the user
     * to indicate if a body is no longer in SSA form.  Could be useful
     * for book-keeping purposes.
     **/
    public void setIsSSA(boolean isSSA)
    {
        this.isSSA = isSSA;
    }

    /**
     * Returns value of, optional, user-maintained SSA boolean.
     *
     * @see #setIsSSA(boolean)
     **/
    public boolean getIsSSA()
    {
        return isSSA;
    }
}
