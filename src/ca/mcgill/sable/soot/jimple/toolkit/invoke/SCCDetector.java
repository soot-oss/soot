// package ca.mcgill.sable.soot.sideEffect;

package ca.mcgill.sable.soot.jimple.toolkit.invoke;

//import ca.mcgill.sable.soot.jimple.*;
//import ca.mcgill.sable.soot.baf.Method;
import ca.mcgill.sable.util.*;
import java.util.*;

/**
 * An implementation of a Strongly Connected Components Detector proposed
 * by Tarjan in SIAM journal of computing Volume 1 (2), 1972.
 */
public class SCCDetector{

  Collection dfsNodes;

  int counter = 0;

  public int Originalnodes = 0, Originaledges = 0;

  LinkedList stackOfPoints = new LinkedList();

  HashMap stackAsMap = new HashMap();

  HashMap removedElementsMap = new HashMap();


  // a list of list of dfsNodes
  List SCCs = new ArrayList();

  /**
   * computes str. conn. comps  we get from node 'dfsNode' and add these components to the SCCs list. 
   */
  private void computeSCCs( DfsNode dfsNode ){
    
    dfsNode.setLowLink( counter );
    dfsNode.setNumber( counter );
    counter++;

    stackOfPoints.addFirst( dfsNode );


    
    stackAsMap.put ( dfsNode, dfsNode );



    List adjnodes = dfsNode.getAdjacentNodes();

    Originaledges = Originaledges + adjnodes.size();
    
    Iterator iter = adjnodes.iterator();

    while( iter.hasNext() ){
      DfsNode adjacentNode = (DfsNode)iter.next();

      if( adjacentNode.getNumber() == 0 ){
	// recursion

	computeSCCs( adjacentNode );

	dfsNode.setLowLink( java.lang.Math.min(dfsNode.getLowLink() ,
					       adjacentNode.getLowLink()) );
      }
      else if( adjacentNode.getNumber() < dfsNode.getNumber() ){

//	if( stackOfPoints.contains( adjacentNode ) )
          
          if ( ( stackAsMap.get ( adjacentNode ) != null ) && ( removedElementsMap.get ( adjacentNode ) == null ) )
	  dfsNode.setLowLink( java.lang.Math.min(dfsNode.getLowLink() ,
						 adjacentNode.getNumber()) );
      }
    }

    // the root of a new strongly conn. comp. is detected
    if( dfsNode.getLowLink() == dfsNode.getNumber() ){
      Collection aScc = new ArrayList();
      
      DfsNode nodeFromStack;
      do {
	nodeFromStack = (DfsNode)stackOfPoints.removeFirst();

        removedElementsMap.put ( nodeFromStack, nodeFromStack );

	aScc.add( nodeFromStack );
      }
      //while( nodeFromStack.getNumber() >= dfsNode.getNumber() );
      while( nodeFromStack != dfsNode );

      // add a new str. conn. comp. to the list of all SCCs.
      SCCs.add( aScc );
    }
  }


  /**
   * computes SCCs of the dfsNodes given as argument and returns the result as list of list of DfsNodes.
   */
  public List computeSCCs( Collection dfsNodes ){
    this.dfsNodes = dfsNodes;

    Originalnodes = dfsNodes.size();


    // make sure dfsNodes are initialized properly

    System.out.println();  

    System.out.print ("Computing Strongly Connected Components in the constraint graph....." );

    Iterator  iter = dfsNodes.iterator();


    while( iter.hasNext() ){
      DfsNode dfsNode = (DfsNode)iter.next();
      dfsNode.setNumber(0);
      dfsNode.setLowLink(0);
    }


    counter = 0;
    // stackOfPoints.clear();

    stackOfPoints = new LinkedList();

    stackAsMap = new HashMap();

    removedElementsMap = new HashMap();

    // SCCs.clear();

    SCCs = new ArrayList();

    iter = dfsNodes.iterator();

    while( iter.hasNext() ){
      DfsNode dfsNode = (DfsNode)iter.next();
      
      if( dfsNode.getNumber() == 0 ) 
	computeSCCs(dfsNode);
    }
    
    System.out.println ("Done" );

    return SCCs;
  }


  /**
   * returns the SCCs of dfsNodes. (Computes them if necessary). 
   */
  public List getSCCs( Collection dfsNodes ){
    if( SCCs.size() > 0 )
      return SCCs;

    return computeSCCs( dfsNodes );
  }
}











