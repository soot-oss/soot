package soot.toolkits.graph;

import soot.*;
import java.util.*;
import soot.options.*;

public class CFGPrinter extends BodyTransformer {

    public CFGPrinter(Singletons.Global g) {}
    public static CFGPrinter v() { return G.v().CFGPrinter();}

    private DirectedGraph graph;
    
    // can handle options here
    // and display in eclipse 
    // and print to dot file also if needed
    protected void internalTransform(Body b, String phaseName, Map options){
        
        CFGOutputOptions opts = new CFGOutputOptions(options);
        if (opts.graph_type() == CFGOutputOptions.graph_type_complete_block_graph){
            graph = new BlockGraph(b, BlockGraph.COMPLETE);   
        }
        else if (opts.graph_type() == CFGOutputOptions.graph_type_brief_block_graph){
            graph = new BlockGraph(b, BlockGraph.BRIEF);   
        }
        else if (opts.graph_type() == CFGOutputOptions.graph_type_array_block_graph){
            graph = new BlockGraph(b, BlockGraph.ARRAYREF);   
        }
        else if (opts.graph_type() == CFGOutputOptions.graph_type_complete_unit_graph){
            graph = new UnitGraph(b, true);   
        }
        else{
        // for now just make complete block graph (but use options to 
        // change this later)
        //G.v().out.println("running cfg printer");
            graph = new UnitGraph(b, false);
        }
        //graph = new BlockGraph(b, BlockGraph.COMPLETE);
        soot.Scene.v().cfgList.add(graph);
    }

    private void notifyPlugin(){
        // ask the scene for a registered listener ? to notify
    }

    public Iterator getNodes(){
        return graph.iterator();
    }

    public Iterator getSuccessors(Object node){
        return graph.getSuccsOf(node).iterator();
    }

    public Iterator getHeads(){
        return graph.getHeads().iterator();
    }

    public Iterator getTails(){
        return graph.getTails().iterator();
    }
}
