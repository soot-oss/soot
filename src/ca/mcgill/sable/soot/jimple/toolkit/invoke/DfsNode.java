package ca.mcgill.sable.soot.jimple.toolkit.invoke;

// package ca.mcgill.sable.soot.sideEffect;

import ca.mcgill.sable.util.*;
import java.util.*;

public abstract class DfsNode implements GraphNode{

  // time of the node discovery
  int number = 0;

  // used by the SCCDetector. 
  int lowLink = 0;


  public int getNumber() { 
    return number; 
  }

  public void setNumber( int i ){ 
    number = i; 
  }

  public int getLowLink(){
    return lowLink;
  }

  public void setLowLink( int i ){
    lowLink = i;
  }
  
  //public abstract List getAdjacentNodes();
}



// OLD version, used with the Old version of the SCC detector.
/*
public class DFSNode{
  
  String color;

  GraphNode graphNode;

  DFSNode( GraphNode gNode ){
    
    graphNode = gNode;

  }

  public GraphNode getGraphNode(){
    return graphNode;
  }

  public Set getAdjacentNodes(){
    return graphNode.getAdjacentNodes();
  }

}
*/
