// package ca.mcgill.sable.soot.virtualCalls; 

package ca.mcgill.sable.soot.jimple.toolkit.invoke;

import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.util.*;
import java.util.*;
// import ca.mcgill.sable.soot.sideEffect.*;
import ca.mcgill.sable.soot.*;
import java.util.*; 

public class TypeNode extends DfsNode {



  String typename;

//  List forwardnodes = new ArrayList();

//  Set backwardnodes = new VectorSet();

  Set AdjacentNodes;

//  Set instancetypes = new VectorSet();

  boolean ready = false, done = false;

  // Map forwardHT = new HashMap( 10, 0.7f );

  // Map backwardHT =  new HashMap( 10, 0.7f );

  // BitSet forwardSet = new BitSet();

  // BitSet backwardSet = new BitSet();

  LinkedNode forwardList = null;

  LinkedNode backwardList = null;

  // Map instancesHT;
/*  =  new HashMap( 15, 0.7f ); */

  BitSet instanceSet = new BitSet();
  

  Map bitmap;

  Map inversebitmap;

  // Map nodesbitmap;

  // Map nodesinversebitmap;





  public TypeNode(){}



  public void prepareForGC() {

   ready = false;

   done = false;

//   forwardHT = new HashMap( 10, 0.7f );

//   backwardHT = new HashMap( 10, 0.7f );

   // instancesHT = new HashMap( 15, 0.7f );

   // forwardSet = new BitSet();

   // backwardSet = new BitSet();

   forwardList = null;

   backwardList = null;

   instanceSet = new BitSet();

   AdjacentNodes = null;

  }




  public TypeNode( String typeName, HashMap bitmap, HashMap inversebitmap /* ,HashMap nodesbitmap, HashMap nodesinversebitmap */ ){

   this.typename = typeName;

   this.bitmap = bitmap;

   this.inversebitmap = inversebitmap;

   // this.nodesbitmap = nodesbitmap;

   // this.nodesinversebitmap = nodesinversebitmap;

  }
  



  public String getTypeName(){

   return typename;

  }

/*
  protected void finalize() throws Throwable{

    System.out.println( "GC:  TypeNode " + typename );

  }
*/







  public List getForwardNodes(){

   ArrayList al = new ArrayList();    

   // for ( int i =0; i < forwardSet.size(); i++ )

   LinkedNode temp = forwardList;

   while ( temp != null )
   {

    // if ( forwardSet.get(i) )
    al.add ( temp.node );

    temp = temp.next;

   }



//   al.addAll( forwardHT.values() );

   return al;

  }

/*
  public void setForwardNodes( List forwardNodes ){

//   forwardnodes = new ArrayList();

   Iterator iter = forwardNodes.iterator();
     
   while ( iter.hasNext() )
   {

    TypeNode tnode = (TypeNode) iter.next();

    if ( ((TypeNode)forwardHT.get(tnode.getTypeName())) == null)
    {

     forwardHT.put(tnode.getTypeName(),tnode);

  //   forwardnodes.add(tnode);

    }

   }

  }

*/
    
  public void addForwardNode( TypeNode tn ){

   if ( tn != null )
   {

   LinkedNode temp = forwardList;

   // int index = ( ( Integer ) nodesbitmap.get ( tn ) ).intValue();

   LinkedNode prev = null;

   while ( temp != null )
   {

    if ( temp.node.getTypeName().equals ( tn.getTypeName() ) )
    return;

    prev = temp;

    temp = temp.next;

   }

   if ( prev != null )
   {

   prev.next = new LinkedNode();    

   prev.next.node = tn;

   prev.next.next = null;
   }
   else
   {

    forwardList = new LinkedNode();

    forwardList.node = tn;

    forwardList.next = null;

   }


  }

/*
    if ( ((TypeNode)forwardHT.get(tn.getTypeName())) == null)
    {

     forwardHT.put(tn.getTypeName(),tn);

  //   forwardnodes.add( tn );

    } 
*/

  }



  public void removeForwardNode( TypeNode tn ){

   if ( tn != null )
   {

   LinkedNode temp = forwardList;

   // int index = ( ( Integer ) nodesbitmap.get ( tn ) ).intValue();

   LinkedNode prev = null;

   while ( temp != null )
   {

    if ( temp.node.getTypeName().equals ( tn.getTypeName() ) )
    {

     if ( prev == null )
     forwardList = temp.next;
     else    
     {    

      prev.next = temp.next;
      return;    

     }

    }

    prev = temp;

    temp = temp.next;

   }
 
  }



//   forwardSet.clear ( ( ( Integer ) nodesbitmap.get ( tn ) ).intValue() );
/*

   if( !(( (TypeNode) forwardHT.get(tn.getTypeName() )) == null)  )
   {

   //   forwardnodes.remove( tn );

      forwardHT.remove( tn.getTypeName() );

    }
  
*/
  }



/*

  public TypeNode getForwardNode( String tn ){

   if ( !( ( (TypeNode) forwardHT.get(tn) ) == null ) )
   return ( (TypeNode) forwardHT.get(tn) ); 
 
   return null;

  }


*/



  public void addBackwardNode( TypeNode bn ){

   if ( bn != null )
   {


   LinkedNode temp = backwardList;

   // int index = ( ( Integer ) nodesbitmap.get ( bn ) ).intValue();

   LinkedNode prev = null;

   while ( temp != null )
   {

    if ( temp.node.getTypeName().equals ( bn.getTypeName() ) )
    return;
  
    prev = temp;

    temp = temp.next;

   }

   if ( prev != null )
   {

   prev.next = new LinkedNode();    

   prev.next.node = bn;

   prev.next.next = null;
   }
   else
   {

    backwardList = new LinkedNode();

    backwardList.node = bn;

    backwardList.next = null;

   }

   outstanding++;


  }

/*
   if ( ! backwardSet.get ( ( ( Integer ) nodesbitmap.get ( bn ) ).intValue() ) )
   {
 
    backwardSet.set ( ( ( Integer ) nodesbitmap.get ( bn ) ).intValue() );

    outstanding++;

   }

*/

/*

   if ( ((TypeNode)backwardHT.get(bn.getTypeName())) == null)
   {

     backwardHT.put(bn.getTypeName(),bn);

     // workHT.put(bn.getTypeName(),bn);

     outstanding++;

//     backwardnodes.add( bn );
    }

*/

  }


  public void removeBackwardNode( TypeNode bn ){

  if ( bn != null )
  {

   LinkedNode temp = backwardList;

   // int index = ( ( Integer ) nodesbitmap.get ( bn ) ).intValue();

   LinkedNode prev = null;

   while ( temp != null )
   {

    if ( temp.node.getTypeName().equals ( bn.getTypeName() ) )
    {

     outstanding--;

     if ( prev == null )
     backwardList = temp.next;
     else    
     {    

      prev.next = temp.next;
      return;    

     }

    }

    prev = temp;

    temp = temp.next;

   }

   // outstanding--;
/*

   if ( backwardSet.get ( ( ( Integer ) nodesbitmap.get ( bn ) ).intValue() ) )
   {
 
    backwardSet.clear ( ( ( Integer ) nodesbitmap.get ( bn ) ).intValue() );

    outstanding--;

   }

*/

/*


   if( ! ( ( (TypeNode) backwardHT.get(bn.getTypeName()) ) == null ) )
   {

 //   backwardnodes.remove( bn );

    backwardHT.remove( bn.getTypeName() );

    outstanding--;

    // workHT.remove( bn.getTypeName() );

   }

   */

  }

  }








  public Set getBackwardNodes(){

//   VectorSet vs = new VectorSet();

   HashSet vs = new HashSet();

   LinkedNode temp = backwardList;

   while ( temp != null )
   {

    // if ( forwardSet.get(i) )
    vs.add ( temp.node );

    temp = temp.next;

   }

/*


   for ( int i =0; i < backwardSet.size(); i++ )
   {

    if ( backwardSet.get(i) )
    vs.add ( ( TypeNode ) nodesinversebitmap.get ( new Integer ( i ) ) );

   }
     
//   vs.addAll( backwardHT.values() );
  
*/
 
   return vs;
  
  }






/*
  public void setBackwardNodes( Set bn ){
 
//   backwardnodes = new VectorSet();
  
   Iterator iter = bn.iterator();
  
   while ( iter.hasNext() )
   {

    TypeNode tnode = (TypeNode) iter.next();   
    
    if ( ((TypeNode)backwardHT.get(tnode.getTypeName())) == null)
    {

     backwardHT.put(tnode.getTypeName(),tnode);

     // workHT.put(tnode.getTypeName(),tnode);

   //  backwardnodes.add(tnode);

    }

   }

  }



  public TypeNode getBackwardNode( String tn ){  

   if ( !( ( (TypeNode) backwardHT.get(tn) ) == null ) )
   return ( (TypeNode) backwardHT.get(tn) );
   
   return null;

  }

*/



  // public Map workHT =  new HashMap( 10, 0.7f );







  public void done() {

   done = true;

  }


  public boolean isDone() {

   return done;

  }




  public boolean isSource() {

  // if ( backwardHT.keySet().toArray().length == 0 )

  return ( backwardList == null );

/*
   for ( int i =0; i < backwardSet.size(); i++ )
   {

    if ( backwardSet.get(i) )
    return false;

   }
   
*/  
 // return true;

  }





  public boolean isSink() {

   return ( forwardList == null );

/*
   for ( int i =0; i < forwardSet.size(); i++ )
   {

    if ( forwardSet.get(i) )
    return false;

   }
   
   return true;
*/
//   if ( forwardHT.keySet().toArray().length == 0 )
//   return true;
  
//   return false;

  }





  
  public boolean isReady() {
  
//   return ( workHT.keySet().toArray().length == 0 );

   return ( outstanding == 0 );

  }








 public void solveNode(Map allclassHT , Map instanceHT )
 {

  // if ( !(allclassHT.get( this.getTypeName() ) == null) )
  // System.out.println("");

  boolean flag = false;

//  boolean printing = false;

  List actualnodes = new ArrayList();

  if ( this instanceof CGSCCNode )
  {

   List nodes = ((CGSCCNode) this).getCGSCCElements();

   Iterator it = nodes.iterator();

   while ( it.hasNext() )
   { 

    TypeNode tn = (TypeNode) it.next();

/*
    if  ( ( ( tn.getTypeName().startsWith ( "ca.mcgill.sable.soot.jimple.LocalDefsFlowAnalysis.<init>(ca.mcgill.sable.soot.jimple.StmtGraph):void" ) ) || ( tn.getTypeName().startsWith("locals") ) ) || ( tn.getTypeName().equals ( "return_ca.mcgill.sable.so


ot.jimple.JimpleBody.getLocals():ca.mcgill.sable.util.List" ) ) )
    printing = true;

*/

    if ( !(Helper.IsRedundantNode ( new String("return_"), tn.getTypeName() )) )
    {
    
     actualnodes.add(tn);

     flag = true;

    }

   } // WHILE

  } // IF THIS INSTANCEOF CGSCCNODE
  else 
  {

//    if ( this.getTypeName().startsWith ( "ca.mcgill.sable.soot.jimple.LocalDefsFlowAnalysis.<init>(ca.mcgill.sable.soot.jimple.StmtGraph):void" ) )

/*
    if  ( ( ( this.getTypeName().startsWith ( "ca.mcgill.sable.soot.jimple.LocalDefsFlowAnalysis.<init>(ca.mcgill.sable.soot.jimple.StmtGraph):void" ) )|| ( this.getTypeName().startsWith("locals") ) )|| ( this.getTypeName().equals ( "return_ca.mcgill.sabl


e.soot.jimple.JimpleBody.getLocals():ca.mcgill.sable.util.List" ) ) )
    printing = true;

*/

   if ( !(Helper.IsRedundantNode ( new String("return_"), this.getTypeName() ))) 
   {

    actualnodes.add(this);

    flag = true;

   }

  }

  //  System.out.println ( "B = "+this.getBackwardNodes().size()+" F = "+this.getForwardNodes().size() );


//  Iterator it = this.getBackwardNodes().iterator();

  try {

   LinkedNode temp = backwardList;

   while ( temp != null )
   {

     TypeNode tn = temp.node;

     unionInstanceTypes( tn );

     temp = temp.next;
   }


/*
   for ( int i = 0; i < backwardSet.size(); i++ )
   {

    if ( backwardSet.get ( i ) )
    {

     TypeNode tn = ( TypeNode ) nodesinversebitmap.get ( new Integer ( i ) );

     unionInstanceTypes ( tn );  

    }

   }

*/

/*
   while ( it.hasNext() )
   {

    TypeNode tn = (TypeNode) it.next();

    unionInstanceTypes( tn );

   }  

*/

  } catch (RuntimeException e ) {}

  done();

/*

  if ( printing ) 
  {
  
  System.out.println ( "NODE : "+this.getTypeName() );

  Iterator iter = this.getInstanceTypes().iterator();

  while ( iter.hasNext() )
  {

   String s = (String) iter.next();

   // if ( flag )
   // System.out.println("INSTANCE TYPE : "+s);

   // if ( printing ) 
   System.out.println ("INSTANCE TYPE : "+s );


  }

  }

*/

  Iterator actualit = actualnodes.iterator();

  while ( actualit.hasNext() )
  {

   TypeNode actualtn = (TypeNode) actualit.next();

   instanceHT.put( actualtn.getTypeName(), this.instanceSet ); 

  }


   LinkedNode temp = forwardList;

   while ( temp != null )
   {

     TypeNode fwdtn = temp.node;

     fwdtn.outstanding--;

     temp = temp.next;
   }



/*


   for ( int i = 0; i < forwardSet.size(); i++ )
   {

    if ( forwardSet.get ( i ) )
    {

     TypeNode fwdtn = ( TypeNode ) nodesinversebitmap.get ( new Integer ( i ) );

     fwdtn.outstanding--;
     
    }

   }

*/

/*

  Iterator fwdit = this.getForwardNodes().iterator();

  while ( fwdit.hasNext() )
  {

   TypeNode fwdtn = (TypeNode) fwdit.next();

   // fwdtn.workHT.remove(this.getTypeName());

   fwdtn.outstanding--;

  }
*/

 }





  int outstanding = 0;




  public boolean containsInstanceType( String s ){

/*
   if ( ((String)instancesHT.get(s)) == null)
   return false;
*/

   return instanceSet.get ( ( ( Integer ) bitmap.get ( s ) ).intValue() ); 

   // return true;

  }
  









 
  public void addInstanceType( String it ){

   Integer index = ( Integer ) bitmap.get ( it ); 

   instanceSet.set ( index.intValue() );

/*

   if ( ((String) instancesHT.get(it)) == null )   
   {

     instancesHT.put(it,it); 

   //  instancetypes.add( it );

    }

*/

  }




 /*  YET TO BE DONE
  public void removeInstanceType( String it ){
    if( backwardnodes.contains(bn) )
      backwardnodes.remove( bn );
  }
*/ 


  public Map getInstancesHT () {

   Map instHT = new HashMap(15, 0.7f);

   for ( int i = 0; i < instanceSet.size(); i++ )
   {

    if ( instanceSet.get ( i ) )
    {

      String s = ( String ) inversebitmap.get ( new Integer ( i ) );

      instHT.put ( s , s );
    }
   
   }

   return instHT;

  }




  
  public Set getInstanceTypes(){

   // VectorSet vs = new VectorSet();

   HashSet vs = new HashSet();

   for ( int i = 0; i < instanceSet.size(); i++ )
   {

    if ( instanceSet.get ( i ) )
    vs.add ( ( String ) inversebitmap.get ( new Integer ( i ) ) );

   }

//    vs.addAll( instancesHT.values() );
   
   return vs;

  }






// DO THIS 
    
      
  public void unionInstanceTypes( TypeNode bn ){

   instanceSet.or ( bn.instanceSet );
    

/*
    Iterator iter = s.iterator();

    try {

     while ( iter.hasNext() )
     {
  
      String nextname = (String) iter.next();

      String tname = new String();
   
      if ( (tname = ( String) instancesHT.get( nextname )) == null )
      {
      
       instancesHT.put(nextname,nextname);

    //   instancetypes.add(nextname);

      } 

     }

    } catch ( java.lang.RuntimeException e){}

*/

  }








    
  // public Set getAdjacentNodes(){


  public List getAdjacentNodes(){

   return this.getForwardNodes();

/*


   if ( AdjacentNodes == null )
   {

    ArrayList AdjacentNodesAL = ( ArrayList) this.getForwardNodes();

//    AdjacentNodes = Helper.TNAL2VS( AdjacentNodesAL );

    HashSet adjnodes = new HashSet();

    Iterator adjit = AdjacentNodesAL.iterator();

    while ( adjit.hasNext() )
    adjnodes.add ( ( TypeNode ) adjit.next() );

    AdjacentNodes = adjnodes;

   }

   return AdjacentNodes;
 
*/

  }






}









































