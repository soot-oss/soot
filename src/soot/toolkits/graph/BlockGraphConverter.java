/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
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

package soot.toolkits.graph;

import soot.*;
import soot.toolkits.graph.*;
import soot.util.*;
import java.util.*;

/**
 * This utility class can convert any BlockGraph to a single-headed
 * and single-tailed graph by inserting appropriate Start or Stop
 * nodes.  It can also fully reverse the graph, something that might
 * be useful e.g. when computing control dependences with a dominators
 * algorithm.
 *
 * <p>
 * Note: This class may be retracted in a future release when a suitable
 * replacement becomes available.
 * </p>
 *
 * @author Navindra Umanee
 **/
public class BlockGraphConverter
{
    /**
     * Transforms a multi-headed and/or multi-tailed BlockGraph to a
     * single-headed singled-tailed BlockGraph by inserting a dummy
     * start and stop nodes.
     **/
    public static void addStartStopNodesTo(BlockGraph graph)
    {
        ADDSTART:
        {
            List heads = graph.getHeads();

            if(heads.size() == 0)
                break ADDSTART;
            
            if((heads.size() == 1) && (heads.get(0) instanceof DummyBlock))
                break ADDSTART;

            List blocks = graph.getBlocks();
            DummyBlock head = new DummyBlock(graph.getBody(), 0);
            head.makeHeadBlock(heads);

            graph.mHeads = new SingletonList(head);
            
            {
                Iterator blocksIt = blocks.iterator();
                while(blocksIt.hasNext()){
                    Block block = (Block) blocksIt.next();
                    block.setIndexInMethod(block.getIndexInMethod() + 1);
                }
            }
            
	    List newBlocks = new ArrayList();
	    newBlocks.add(head);
	    newBlocks.addAll(blocks);
            graph.mBlocks = newBlocks;
        }

        ADDSTOP:
        {
            List tails = graph.getTails();

            if(tails.size() == 0)
                break ADDSTOP;
            
            if((tails.size() == 1) && (tails.get(0) instanceof DummyBlock))
                break ADDSTOP;

            List blocks = graph.getBlocks();
            DummyBlock tail = new DummyBlock(graph.getBody(), blocks.size());
            tail.makeTailBlock(tails);

            graph.mTails = new SingletonList(tail);

            blocks.add(tail);
        }
    }

    /**
     * Reverses a BlockGraph by making the heads tails, the tails
     * heads and reversing the edges.  It does not change the ordering
     * of Units in individual blocks, nor does it change the Block
     * labels.  This utility could be useful when calculating control
     * dependences with a dominators algorithm.
     **/
    public static void reverse(BlockGraph graph)
    {
        // Issue: Do we change indexInMethod?  No...
        // Issue: Do we reverse the Units list in the Block?
        // Issue: Do we need to implement an equals method in Block?
        //        When are two Blocks from two different BlockGraphs
        //        equal?

        for(Iterator blocksIt = graph.getBlocks().iterator(); blocksIt.hasNext();){
            Block block = (Block) blocksIt.next();
            List succs = block.getSuccs();
            List preds = block.getPreds();
            block.setSuccs(preds);
            block.setPreds(succs);
        }

        List heads = graph.getHeads();
        List tails = graph.getTails();

        graph.mHeads = new ArrayList(tails);
        graph.mTails = new ArrayList(heads);
    }

    public static void main(String[] args)
    {
        // assumes 2 args:  Class + Method
        
        Scene.v().loadClassAndSupport(args[0]);
        SootClass sc = Scene.v().getSootClass(args[0]);
        SootMethod sm = sc.getMethod(args[1]);
        Body b = sm.retrieveActiveBody();
        CompleteBlockGraph cfg = new CompleteBlockGraph(b);
        System.out.println(cfg);
        BlockGraphConverter.addStartStopNodesTo(cfg);
        System.out.println(cfg);
        BlockGraphConverter.reverse(cfg);
        System.out.println(cfg);
    }
    
}

/**
 * Represents Start or Stop node in the graph.
 *
 * @author Navindra Umanee
 **/
class DummyBlock extends Block
{
    DummyBlock(Body body, int indexInMethod)
    {
        super(null, null, body, indexInMethod, 0, null);
    }

    void makeHeadBlock(List oldHeads)
    {
        setPreds(new ArrayList());
        setSuccs(new ArrayList(oldHeads));

        Iterator headsIt = oldHeads.iterator();
        while(headsIt.hasNext()){
            Block oldHead = (Block) headsIt.next();

            List newPreds = new ArrayList();
            newPreds.add(this);

            List oldPreds = oldHead.getPreds();
            if(oldPreds != null)
                newPreds.addAll(oldPreds);
            
            oldHead.setPreds(newPreds);
        }
    }

    void makeTailBlock(List oldTails)
    {
        setSuccs(new ArrayList());
        setPreds(new ArrayList(oldTails));

        Iterator tailsIt = oldTails.iterator();
        while(tailsIt.hasNext()){
            Block oldTail = (Block) tailsIt.next();

            List newSuccs = new ArrayList();
            newSuccs.add(this);

            List oldSuccs = oldTail.getSuccs();
            if(oldSuccs != null)
                newSuccs.addAll(oldSuccs);

            oldTail.setSuccs(newSuccs);
        }
    }    

    public Iterator iterator()
    {
        return Collections.EMPTY_LIST.iterator();
    }
}
