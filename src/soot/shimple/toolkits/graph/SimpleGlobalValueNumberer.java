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

package soot.shimple.toolkits.graph;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import soot.shimple.*;
import soot.toolkits.graph.*;
import java.util.*;
import soot.shimple.toolkits.graph.ValueGraph.Node;

public class SimpleGlobalValueNumberer implements GlobalValueNumberer
{
    protected BlockGraph cfg;
    protected ValueGraph vg;
    protected Set partitions;
    protected Map nodeToPartition;

    protected int currentPartitionNumber;
    
    public SimpleGlobalValueNumberer(BlockGraph cfg)
    {
        this.cfg = cfg;
        vg = new ValueGraph(cfg);
        partitions = new HashSet(); // not deterministic
        nodeToPartition = new HashMap();
        currentPartitionNumber = 0;
        
        initPartition();
        iterPartition();
    }

    // testing
    public static void main(String[] args)
    {
        // assumes 2 args:  Class + Method

        Scene.v().loadClassAndSupport(args[0]);
        SootClass sc = Scene.v().getSootClass(args[0]);
        SootMethod sm = sc.getMethod(args[1]);
        Body b = sm.retrieveActiveBody();
        ShimpleBody sb = Shimple.v().newBody(b);
        CompleteBlockGraph cfg = new CompleteBlockGraph(sb);
        SimpleGlobalValueNumberer sgvn = new SimpleGlobalValueNumberer(cfg);
        System.out.println(sgvn);
    }
    
    public int getGlobalValueNumber(Local local)
    {
        Node node = vg.getNode(local);
        return ((Partition)nodeToPartition.get(node)).getPartitionNumber();
    }
    
    public boolean areEqual(Local local1, Local local2)
    {
        Node node1 = vg.getNode(local1);
        Node node2 = vg.getNode(local2);

        return (nodeToPartition.get(node1) == nodeToPartition.get(node2));
    }
    
    protected void initPartition()
    {
        Map labelToPartition = new HashMap();
        
        Iterator topNodesIt = vg.getTopNodes().iterator();
        while(topNodesIt.hasNext()){
            Node node = (Node) topNodesIt.next();
            String label = node.getLabel();
            
            Partition partition = (Partition) labelToPartition.get(label);
            if(partition == null){
                partition = new Partition();
                partitions.add(partition);
                labelToPartition.put(label, partition);
            }

            partition.add(node);
            nodeToPartition.put(node, partition);
        }
    }

    protected List newPartitions;
    
    protected void iterPartition()
    {
        newPartitions = new ArrayList();

        Iterator partitionsIt = partitions.iterator();
        while(partitionsIt.hasNext()){
            Partition partition = (Partition) partitionsIt.next();
            processPartition(partition);
        }

        partitions.addAll(newPartitions);
    }

    protected void processPartition(Partition partition)
    {
        int size = partition.size();

        if(size == 0)
            return;

        Node firstNode = (Node) partition.get(0);
        Partition newPartition = new Partition();
        boolean processNewPartition = false;
        
        for(int i = 1; i < size; i++){
            Node node = (Node) partition.get(i);

            if(!childrenAreInSamePartition(firstNode, node)){
                partition.remove(i);
                size--;
                newPartition.add(node);
                newPartitions.add(newPartition);
                nodeToPartition.put(node, newPartition);
                processNewPartition = true;
            }
        }

        if(processNewPartition)
            processPartition(newPartition);
    }
    
    protected boolean childrenAreInSamePartition(Node node1, Node node2)
    {
        boolean ordered = node1.isOrdered();
        if(ordered != node2.isOrdered())
            throw new RuntimeException("Assertion failed.");

        List children1 = node1.getChildren();
        List children2 = node2.getChildren();

        int numberOfChildren = children1.size();
        if(children2.size() != numberOfChildren)
            return false;
        
        if(ordered){
            for(int i = 0; i < numberOfChildren; i++){
                Node child1 = (Node) children1.get(i);
                Node child2 = (Node) children2.get(i);
                if(nodeToPartition.get(child1) != nodeToPartition.get(child2))
                    return false;
            }
        }
        else{
            for(int i = 0; i < numberOfChildren; i++){
                Node child1 = (Node) children1.get(i);
                for(int j = i; j < numberOfChildren; j++){
                    Node child2 = (Node) children2.get(j);

                    if(nodeToPartition.get(child1) ==
                       nodeToPartition.get(child2)){
                        if(j != i){
                            children2.remove(j);
                            children2.add(i, child2);
                        }
                        break;
                    }

                    // last iteration, no match
                    if((j + 1) == numberOfChildren)
                        return false;
                }
            }
        }

        return true;
    }

    public String toString()
    {
        StringBuffer tmp = new StringBuffer();

        Body b = cfg.getBody();
        Iterator localsIt = b.getLocals().iterator();

        while(localsIt.hasNext()){
            Local local = (Local)localsIt.next();
            tmp.append(local + "\t" + getGlobalValueNumber(local) + "\n");
        }

        /*
        Iterator partitionsIt = partitions.iterator();
        while(partitionsIt.hasNext()){
            Partition partition = (Partition) partitionsIt.next();
            int partitionNumber = partition.getPartitionNumber();
            
            Iterator partitionIt = partition.iterator();
            while(partitionIt.hasNext()){
                Local local = vg.getLocal((Node)partitionIt.next());
                if(local == null)
                    continue;
                tmp.append(local + "\t" + partitionNumber + "\n");
            }
        }
        */
        
        return tmp.toString();
    }
    
    protected class Partition extends ArrayList
    {
        protected int partitionNumber;
        
        protected Partition()
        {
            super();
            partitionNumber = currentPartitionNumber++;
        }

        protected int getPartitionNumber()
        {
            return partitionNumber;
        }
    }
}
