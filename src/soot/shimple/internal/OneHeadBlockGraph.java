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

package soot.shimple.internal;

import soot.*;
import soot.toolkits.graph.*;
import soot.util.*;
import java.util.*;

/**
 * Utility class to convert a multi-headed BlockGraph to a single-headed
 * block graph.  This is done by inserting a fake head node in graph, if
 * necessary, and patching the required successor/predecessor pointers.
 *
 * @author Navindra Umanee
 **/
public class OneHeadBlockGraph
{
    /**
     * Transforms a multi-headed BlockGraph to a single-headed BlockGraph
     * by inserting a dummy start node.
     **/
    public static void convert(BlockGraph graph)
    {
        List heads = graph.getHeads();
        List blocks = graph.getBlocks();

        if(heads.size() > 1){
            Block head = new HeadBlock(heads);
            heads.clear();
            heads.add(head);
            
            {
                Iterator blocksIt = blocks.iterator();
                while(blocksIt.hasNext()){
                    Block block = (Block) blocksIt.next();
                    block.setIndexInMethod(block.getIndexInMethod() + 1);
                }
            }
                
            blocks.add(0, head);
        }
    }
}

/**
 * Dummy start node.
 *
 * @author Navindra Umanee
 **/
class HeadBlock extends Block
{
    HeadBlock(List oldHeads)
    {
        super(null, null, null, 0, 0, null);
        setPreds(Collections.EMPTY_LIST);
        setSuccs(oldHeads);

        Iterator headsIt = oldHeads.iterator();
        while(headsIt.hasNext()){
            Block oldHead = (Block) headsIt.next();
            List oldPreds = oldHead.getPreds();
            if(oldPreds == null)
                oldHead.setPreds(new SingletonList(this));
            else
                oldPreds.add(0, this);
        }
    }
    
    public Iterator iterator()
    {
        return Collections.EMPTY_LIST.iterator();
    }
}
