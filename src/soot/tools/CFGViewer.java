/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Sable Research Group
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.tools;

import soot.util.dot.*;
import soot.*;
import soot.toolkits.graph.*;
import soot.util.*;
import java.util.*;
import soot.shimple.*;

/**
 * A utility class for generating dot graph file for a control flow graph
 *
 * @author Feng Qian
 */
public class CFGViewer {

  /* make all control fields public, allow other soot class dump 
   * the graph in the middle */
  
  public final static int UNITGRAPH  = 0;
  public final static int BLOCKGRAPH = 1;
  public final static int ARRAYBLOCK = 2;

  public int graphtype = UNITGRAPH;

  public String clsname;
  public String methname;

  public boolean isBrief      = false;
 
  private int meth_count = 0;

  /* in one page or several pages of 8.5x11 */
  public boolean onepage      = true;

  /* build CFG for Shimple */
  public static boolean shimple = false;
  /* build CFG for Jimple->Shimple->Jimple */
  public static boolean viaShimple = false;

  /* build complete CFG graphs */
  public static boolean complete = false;
    
    
  public static void main(String[] args) {
      new CFGViewer().run( args );
  }

  public void run(String[] args) {

    /* check the arguments */
    if (args.length ==0) {
      usage();
      return;
    }

    /* process options */
    parse_options(args);

    /* load and support classes manually */
    SootClass cls = Scene.v().loadClassAndSupport(clsname);
    cls.setApplicationClass();
    
    /* iterate each method and call print_cfg */
    Iterator methodIt = cls.methodIterator();
    while (methodIt.hasNext()) {
      SootMethod meth = (SootMethod)methodIt.next();
      
      if ((methname == null) 
	  || (methname.equals(meth.getName()))) {
	if (meth.isConcrete()) {
	  Body body = meth.retrieveActiveBody();

	  if(shimple || viaShimple)
	     body = Shimple.v().newBody(body);

	  if(viaShimple && !shimple)
             body = Shimple.v().newJimpleBody((ShimpleBody)body);

	  print_cfg(body);
	}
      }
    }
  }

  private void usage(){
      G.v().out.println("Usage:");
      G.v().out.println("   java soot.util.CFGViewer [options] class[:method]");
      G.v().out.println("   options:");
      G.v().out.println("       --unit|block|array : produces the unit(default)/block graph.");
      G.v().out.println("       --brief : uses the unit/block index as the label.");
      G.v().out.println("       --soot-classpath PATHs : specifies the soot class pathes.");
      G.v().out.println("       --multipages : produces the dot file sets multi pages (8.5x11).");
      G.v().out.println("                      By default, the graph is in one page.");
  }


  private void parse_options(String[] args){
    for (int i=0, n=args.length; i<n; i++) {
      if (args[i].equals("--unit")) {
	graphtype = UNITGRAPH;
      } else if (args[i].equals("--block")) {
	graphtype = BLOCKGRAPH;
      } else if (args[i].equals("--array")) {
	graphtype = ARRAYBLOCK;
      } else if (args[i].equals("--brief")) {
	isBrief = true;
      } else if (args[i].equals("--soot-classpath")) {
	Scene.v().setSootClassPath(args[++i]);
      } else if (args[i].equals("--multipages")) {
	onepage = false;
      } else if (args[i].equals("--shimple")) {
	shimple = true;
      } else if (args[i].equals("--via-shimple")) {
	viaShimple = true;
      } else if (args[i].equals("--complete")) {
        complete = true;
      } else {
	int smpos = args[i].indexOf(':');
	if (smpos == -1) {
	  clsname = args[i]; 
	} else {
	  clsname  = args[i].substring(0, smpos);
	  methname = args[i].substring(smpos+1);
	}
      }
    }
  }

  protected void print_cfg(Body body) {
    SootMethod method = body.getMethod();
    SootClass  sclass = method.getDeclaringClass();

    DirectedGraph graph = null;

    switch (graphtype) {
    case UNITGRAPH:
      graph = new UnitGraph(body, complete);
      break;
    case BLOCKGRAPH:
      if(complete)
        graph = new BlockGraph(body, BlockGraph.COMPLETE);
      else
        graph = new BlockGraph(body, BlockGraph.BRIEF);
      break;
    case ARRAYBLOCK:
      graph = new BlockGraph(body, BlockGraph.ARRAYREF);
      break;
    }

    String methodname = method.getName()+"-"+meth_count++;
    String graphname = sclass.getName()+":"+method.getName();
    toDotFile(methodname, graph, graphname); 
  }


  private int nodecount = 0;

  /**
   * Generates a dot format file for a DirectedGraph
   * @param methodname, the name of generated dot file
   * @param graph, a directed control flow graph (UnitGraph, BlockGraph ...)
   * @param graphname, the title of the graph
   */
  public void toDotFile(String methodname, 
				DirectedGraph graph, 
				String graphname) {

    // this makes the node name unique
    nodecount = 0; // reset node counter first.
    Hashtable nodeindex = new Hashtable(graph.size());

    // file name is the method name + .dot
    DotGraph canvas = new DotGraph(methodname);

    if (!onepage) {
      canvas.setPageSize(8.5, 11.0);
    }

    if (isBrief) {
      canvas.setNodeShape(DotGraphConstants.NODE_SHAPE_CIRCLE);
    } else {
      canvas.setNodeShape(DotGraphConstants.NODE_SHAPE_BOX);
    }
    canvas.setGraphLabel(graphname);

    Iterator nodesIt = graph.iterator();
    while (nodesIt.hasNext()) {
      Object node = nodesIt.next();

      Iterator succsIt = graph.getSuccsOf(node).iterator();
      while (succsIt.hasNext()) {
        Object succ = succsIt.next();

        canvas.drawEdge(makeNodeName(getNodeOrder(nodeindex, node)), 
			makeNodeName(getNodeOrder(nodeindex, succ)));
      }
    }

    // make the entry and exit node filled.
    Iterator headsIt = graph.getHeads().iterator();
    while (headsIt.hasNext()) {
      Object head = headsIt.next();
      DotGraphNode headNode = canvas.getNode(makeNodeName(getNodeOrder(nodeindex, head)));
      headNode.setStyle(DotGraphConstants.NODE_STYLE_FILLED);
    }

    Iterator tailsIt = graph.getTails().iterator();
    while (tailsIt.hasNext()) {
      Object tail = tailsIt.next();
      DotGraphNode tailNode = canvas.getNode(makeNodeName(getNodeOrder(nodeindex, tail)));
      tailNode.setStyle(DotGraphConstants.NODE_STYLE_FILLED);
    }

    // set node label
    if (!isBrief) {
      nodesIt = nodeindex.keySet().iterator();
      while (nodesIt.hasNext()) {
	Object node = nodesIt.next();
	String nodename = makeNodeName(getNodeOrder(nodeindex, node));
	DotGraphNode dotnode = canvas.getNode(nodename);
	dotnode.setLabel(node.toString());
      }
    }

    canvas.plot();
  } 

  private int getNodeOrder(Hashtable nodeindex, Object node){
    Integer index = (Integer)nodeindex.get(node);
    if (index == null) {
      index = new Integer(nodecount++);
      nodeindex.put(node, index);
    }
    return index.intValue();
  }

  private String makeNodeName(int index){
    return "N"+index;
  }
}
