/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Navindra Umanee <navindra@cs.mcgill.ca>
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
import soot.shimple.toolkits.graph.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.toolkits.pointer.*;
import soot.jimple.toolkits.scalar.UnreachableCodeEliminator;
import soot.toolkits.graph.*;

/**
 * @author Navindra Umanee
 **/
public class DefaultShimpleFactory implements ShimpleFactory
{
    protected Body body;
    protected BlockGraph bg;
    protected UnitGraph ug;
    protected DominatorsFinder dFinder;
    protected DominatorTree dTree;
    protected DominanceFrontier dFrontier;
    protected PointsToAnalysis pta;
    protected CallGraph cg;
    protected SideEffectAnalysis sea;
    protected GlobalValueNumberer gvn;

    protected ReversibleGraph rbg;
    protected DominatorTree rdTree;
    protected DominanceFrontier rdFrontier;
    protected DominatorsFinder rdFinder;
    
    public DefaultShimpleFactory()
    {
    }
    
    public void clearCache()
    {
        bg = null;
        ug = null;
        dFinder = null;
        dTree = null;
        dFrontier = null;
        pta = null;
        cg = null;
        sea = null;
        gvn = null;
        rbg = null;
        rdTree = null;
        rdFinder = null;
        rdFrontier = null;
    }
    
    public void setBody(Body body)
    {
        this.body = body;
        clearCache();        
    }

    public Body getBody()
    {
        if(body == null)
            throw new RuntimeException("Assertion failed: Call setBody() first.");

        return body;
    }

    public ReversibleGraph getReverseBlockGraph()
    {
        if(rbg != null)
            return rbg;
        
        BlockGraph bg = getBlockGraph();
        rbg = new HashReversibleGraph(bg);
        rbg.reverse();
        return rbg;
    }

    public DominatorsFinder getReverseDominatorsFinder()
    {
        if(rdFinder != null)
            return rdFinder;

        rdFinder = new SimpleDominatorsFinder(getReverseBlockGraph());
        return rdFinder;
    }

    public DominatorTree getReverseDominatorTree()
    {
        if(rdTree != null)
            return rdTree;

        rdTree = new DominatorTree(getReverseDominatorsFinder());
        return rdTree;
    }

    public DominanceFrontier getReverseDominanceFrontier()
    {
        if(rdFrontier != null)
            return rdFrontier;

        rdFrontier = new CytronDominanceFrontier(getReverseDominatorTree());
        return rdFrontier;
    }
    
    public BlockGraph getBlockGraph()
    {
        if(bg != null)
            return bg;

        bg = new ExceptionalBlockGraph((ExceptionalUnitGraph)getUnitGraph());
        BlockGraphConverter.addStartStopNodesTo(bg);
        return bg;
    }

    public UnitGraph getUnitGraph()
    {
        if(ug != null)
            return ug;
        
        UnreachableCodeEliminator.v().transform(getBody());

        ug = new ExceptionalUnitGraph(getBody());
        return ug;
    }
    
    public DominatorsFinder getDominatorsFinder()
    {
        if(dFinder != null)
            return dFinder;

        dFinder = new SimpleDominatorsFinder(getBlockGraph());
        return dFinder;
    }

    public DominatorTree getDominatorTree()
    {
        if(dTree != null)
            return dTree;

        dTree = new DominatorTree(getDominatorsFinder());
        return dTree;
    }
    
    public DominanceFrontier getDominanceFrontier()
    {
        if(dFrontier != null)
            return dFrontier;

        dFrontier = new CytronDominanceFrontier(getDominatorTree());
        return dFrontier;
    }

    public GlobalValueNumberer getGlobalValueNumberer()
    {
        if(gvn != null)
            return gvn;
        
        gvn = new SimpleGlobalValueNumberer(getBlockGraph());
        return gvn;
    }
}
