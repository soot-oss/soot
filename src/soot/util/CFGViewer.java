/* A utility class for generating dot graph file for a control flow graph
 *
 * @author Feng Qian
 */

package soot.util;

import soot.*;
import soot.toolkits.graph.*;
import soot.util.*;
import java.util.*;

public class CFGViewer extends BodyTransformer{

  private String clsname;
  private String methname;

  private static boolean useUnitGraph = true;
  private static boolean isBrief      = false;

  public static void main(String[] args) {

    /* check the arguments */
    if (args.length ==0) {
      usage();
      System.exit(0);
    }

    /* add a phase to transformer pack by call Pack.add */
    Pack jtp = Scene.v().getPack("jtp");
    CFGViewer viewer = new CFGViewer();
    jtp.add(new Transform("jtp.cfgviewer", 
			  viewer));

    /* process options */
    args = parseoptions(viewer, args);
        
    soot.Main.setTargetRep(soot.Main.NO_OUTPUT);

    soot.Main.main(args);
  }

  private static void usage(){
      System.err.println("Usage: java soot.util.CFGViewer [--unit|--block] [--brief] class[:method]");
      System.err.println("       --unit  : (default) uses the unit graph.");
      System.err.println("       --block : uses the block graph.");
      System.err.println("       --brief : uses the unit/block index as the label.");
  }


  private static String[] parseoptions(CFGViewer viewer, String[] args){
    for (int i=0, n=args.length; i<n; i++) {
      if (args[i].equals("--unit")) {
	useUnitGraph = true;
      } else if (args[i].equals("--block")) {
	useUnitGraph = false;
      } else if (args[i].equals("--brief")) {
	isBrief = true;
      } else {
	int smpos = args[i].indexOf(':');
	if (smpos == -1) {
	  viewer.clsname = args[i]; 
	} else {
	  viewer.clsname  = args[i].substring(0, smpos);
	  viewer.methname = args[i].substring(smpos+1);
	}

	args = new String[1];
	args[0] = viewer.clsname;
      }
    }

    return args;
  }

  protected void internalTransform(Body body, String phase, Map options) {
    SootMethod method = body.getMethod();
    SootClass  sclass = method.getDeclaringClass();

    if (!sclass.getName().equals(clsname)) {
      return;
    }

    if (methname != null && !method.getName().equals(methname)) {
      return;
    }

    DirectedGraph graph = null;
    
    if (useUnitGraph) {
      graph = new UnitGraph(body, false);
    } else {
      graph = new BlockGraph(body, BlockGraph.BRIEF);
    }

    String methodname = method.getName();
    String graphname = sclass.getName()+"."+method.getName();
    toDotFile(methodname, graph, graphname); 
  }


  private static int nodecount = 0;

  /* generating dot format for plotting */
  private static void toDotFile(String methodname, 
				DirectedGraph graph, 
				String graphname) {

    // this makes the node name unique
    nodecount = 0; // reset node counter first.
    Hashtable nodeindex = new Hashtable(graph.size());

    // file name is the method name + .dot
    DotGraph canvas = new DotGraph(methodname);

    canvas.setPageSize(8.5, 11.0);

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

  private static int getNodeOrder(Hashtable nodeindex, Object node){
    Integer index = (Integer)nodeindex.get(node);
    if (index == null) {
      index = new Integer(nodecount++);
      nodeindex.put(node, index);
    }
    return index.intValue();
  }

  private static String makeNodeName(int index){
    return "N"+index;
  }
}
