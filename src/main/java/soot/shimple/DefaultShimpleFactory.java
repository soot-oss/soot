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
    protected final Body body;
    protected BlockGraph bg;
    protected UnitGraph ug;
    protected DominatorsFinder<Block> dFinder;
    protected DominatorTree<Block> dTree;
    protected DominanceFrontier<Block> dFrontier;
    protected PointsToAnalysis pta;
    protected CallGraph cg;
    protected SideEffectAnalysis sea;
    protected GlobalValueNumberer gvn;

    protected ReversibleGraph<Block> rbg;
    protected DominatorTree<Block> rdTree;
    protected DominanceFrontier<Block> rdFrontier;
    protected DominatorsFinder<Block> rdFinder;
    
    public DefaultShimpleFactory(Body body)
    {
    	this.body = body;
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

    public Body getBody()
    {
        if(body == null)
            throw new RuntimeException("Assertion failed: Call setBody() first.");

        return body;
    }

    public ReversibleGraph<Block> getReverseBlockGraph()
    {
        if(rbg != null)
            return rbg;
        
        BlockGraph bg = getBlockGraph();
        rbg = new HashReversibleGraph<Block>(bg);
        rbg.reverse();
        return rbg;
    }

    public DominatorsFinder<Block> getReverseDominatorsFinder()
    {
        if(rdFinder != null)
            return rdFinder;

        rdFinder = new SimpleDominatorsFinder<Block>(getReverseBlockGraph());
        return rdFinder;
    }

    public DominatorTree<Block> getReverseDominatorTree()
    {
        if(rdTree != null)
            return rdTree;

        rdTree = new DominatorTree<Block>(getReverseDominatorsFinder());
        return rdTree;
    }

    public DominanceFrontier<Block> getReverseDominanceFrontier()
    {
        if(rdFrontier != null)
            return rdFrontier;

        rdFrontier = new CytronDominanceFrontier<Block>(getReverseDominatorTree());
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
    
    public DominatorsFinder<Block> getDominatorsFinder()
    {
        if(dFinder != null)
            return dFinder;

        dFinder = new SimpleDominatorsFinder<Block>(getBlockGraph());
        return dFinder;
    }

    public DominatorTree<Block> getDominatorTree()
    {
        if(dTree != null)
            return dTree;

        dTree = new DominatorTree<Block>(getDominatorsFinder());
        return dTree;
    }
    
    public DominanceFrontier<Block> getDominanceFrontier()
    {
        if(dFrontier != null)
            return dFrontier;

        dFrontier = new CytronDominanceFrontier<Block>(getDominatorTree());
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
