// package ca.mcgill.sable.soot.virtualCalls;

package ca.mcgill.sable.soot.jimple.toolkit.invoke;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.util.*;
import java.util.*;
// import ca.mcgill.sable.soot.sideEffect.*;
import ca.mcgill.sable.soot.*;
class CGSCCNode extends TypeNode{
   List CGSCC;
   CallSite callSite;
   String name ;
   CGSCCNode( List cgscc ){
      super();
      this.CGSCC = cgscc;
      StringBuffer nameSB = new StringBuffer();
      for( int i = 0 ; i < cgscc.size() ; i++ )
      nameSB.append( ((TypeNode)cgscc.get(i)).getTypeName() + "+" );
      this.bitmap = ( ( TypeNode ) cgscc.get(0) ).bitmap;
      this.inversebitmap = ( ( TypeNode ) cgscc.get(0) ).inversebitmap;
      this.name = nameSB.toString();
   }


   String getTypeName(){
      return name;
   }


   List getCGSCCElements(){
      return CGSCC;
   }


   void printContents(){
      System.out.println( "\n CGSCC Node: " + getTypeName() );
      for ( int i = 0 ; i < CGSCC.size() ; i++ ){
         System.out.print( " - " + ((TypeNode)CGSCC.get(i)).getTypeName() );
      }

      System.out.println("\n");
   }


   void updateLinks( Map dtHT ){
      HashMap tempMap = new HashMap();
      for ( int i = 0 ; i < CGSCC.size() ; i++ )
      {
         TypeNode CGSCCElem = (TypeNode)CGSCC.get(i);
         tempMap.put ( CGSCCElem.getTypeName(), CGSCCElem );
      }

      for ( int i = 0 ; i < CGSCC.size() ; i++ ){
         TypeNode CGSCCElem = (TypeNode)CGSCC.get(i);
         Object[] pFNodes = CGSCCElem.getForwardNodes().toArray();
         for ( int p = 0 ; p < pFNodes.length ; p++ )
         {
            if ( tempMap.get ( ( ( TypeNode ) pFNodes[p] ).getTypeName() ) == null )
            {
               TypeNode pfnode = (TypeNode)pFNodes[p];
               CGSCCElem.removeForwardNode(pfnode);
               pfnode.removeBackwardNode(CGSCCElem);
               this.addForwardNode(pfnode);
               pfnode.addBackwardNode(this);
            }

         }

         Object[] pBNodes = CGSCCElem.getBackwardNodes().toArray();
         for ( int p = 0 ; p < pBNodes.length ; p++ )
         {
            if ( tempMap.get ( ( ( TypeNode ) pBNodes[p] ).getTypeName() ) == null )
            {
               TypeNode pbnode = (TypeNode)pBNodes[p];
               CGSCCElem.removeBackwardNode(pbnode);
               pbnode.removeForwardNode(CGSCCElem);
               this.addBackwardNode(pbnode);
               pbnode.addForwardNode(this);
            }

         }

         this.unionInstanceTypes( CGSCCElem );
         dtHT.remove(CGSCCElem.getTypeName());
      }

   }


}




