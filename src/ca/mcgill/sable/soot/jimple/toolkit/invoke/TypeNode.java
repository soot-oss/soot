// package ca.mcgill.sable.soot.virtualCalls; 

package ca.mcgill.sable.soot.jimple.toolkit.invoke;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.util.*;
import java.util.*;
// import ca.mcgill.sable.soot.sideEffect.*;
import ca.mcgill.sable.soot.*;
import java.util.*;
class TypeNode extends DfsNode {
   String typename;
   Set AdjacentNodes;
   boolean ready = false, done = false;
   LinkedNode forwardList = null;
   LinkedNode backwardList = null;
   BitSet instanceSet = new BitSet();
   Map bitmap;
   Map inversebitmap;

   TypeNode(){}

   void prepareForGC() {
      ready = false;
      done = false;
      forwardList = null;
      backwardList = null;
      instanceSet = new BitSet();
      AdjacentNodes = null;
   }


   TypeNode( String typeName, HashMap bitmap, HashMap inversebitmap /* ,HashMap nodesbitmap, HashMap nodesinversebitmap */ ){
      this.typename = typeName;
      this.bitmap = bitmap;
      this.inversebitmap = inversebitmap;
   }


   String getTypeName(){
      return typename;
   }


   List getForwardNodes(){
      ArrayList al = new ArrayList();
      LinkedNode temp = forwardList;
      while ( temp != null )
      {
         al.add ( temp.node );
         temp = temp.next;
      }

      return al;
   }


   void addForwardNode( TypeNode tn ){
      if ( tn != null )
      {
         LinkedNode temp = forwardList;
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

   }


   void removeForwardNode( TypeNode tn ){
      if ( tn != null )
      {
         LinkedNode temp = forwardList;
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

   }


   void addBackwardNode( TypeNode bn ){
      if ( bn != null )
      {
         LinkedNode temp = backwardList;
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

   }


   void removeBackwardNode( TypeNode bn ){
      if ( bn != null )
      {
         LinkedNode temp = backwardList;
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

      }

   }


   Set getBackwardNodes(){
      HashSet vs = new HashSet();
      LinkedNode temp = backwardList;
      while ( temp != null )
      {
         // if ( forwardSet.get(i) )
         vs.add ( temp.node );
         temp = temp.next;
      }

      return vs;
   }


   void done() {
      done = true;
   }


   boolean isDone() {
      return done;
   }


   boolean isSource() {
      return ( backwardList == null );
   }


   boolean isSink() {
      return ( forwardList == null );
   }


   boolean isReady() {
      return ( outstanding == 0 );
   }


   void solveNode(Map allclassHT , Map instanceHT )
   {
      boolean flag = false;
      List actualnodes = new ArrayList();
      if ( this instanceof CGSCCNode )
      {
         List nodes = ((CGSCCNode) this).getCGSCCElements();
         Iterator it = nodes.iterator();
         while ( it.hasNext() )
         {
            TypeNode tn = (TypeNode) it.next();
            if ( !(Helper.IsRedundantNode ( new String("return_"), tn.getTypeName() )) )
            {
               actualnodes.add(tn);
               flag = true;
            }

         }
         // WHILE

      }
      // IF THIS INSTANCEOF CGSCCNODE
      else
      {
         if ( !(Helper.IsRedundantNode ( new String("return_"), this.getTypeName() )))
         {
            actualnodes.add(this);
            flag = true;
         }

      }

      try {
         LinkedNode temp = backwardList;
         while ( temp != null )
         {
            TypeNode tn = temp.node;
            unionInstanceTypes( tn );
            temp = temp.next;
         }

      }
      catch (RuntimeException e ) {}

      done();

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

   }


   int outstanding = 0;
   boolean containsInstanceType( String s ){
      return instanceSet.get ( ( ( Integer ) bitmap.get ( s ) ).intValue() );
   }


   void addInstanceType( String it ){
      Integer index = ( Integer ) bitmap.get ( it );
      instanceSet.set ( index.intValue() );
   }


   /*  YET TO BE DONE
     public void removeInstanceType( String it ){
       if( backwardnodes.contains(bn) )
         backwardnodes.remove( bn );
     }
   */

   Map getInstancesHT () {
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


   Set getInstanceTypes(){
      HashSet vs = new HashSet();
      for ( int i = 0; i < instanceSet.size(); i++ )
      {
         if ( instanceSet.get ( i ) )
         vs.add ( ( String ) inversebitmap.get ( new Integer ( i ) ) );
      }

      return vs;
   }


   // DO THIS 

   void unionInstanceTypes( TypeNode bn ){
      instanceSet.or ( bn.instanceSet );
   }


   public List getAdjacentNodes(){
      return this.getForwardNodes();
   }


}




