// package ca.mcgill.sable.soot.sideEffect;
package ca.mcgill.sable.soot.jimple.toolkit.invoke;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;
/**
* represents a call site in the code and contains links to all possible methods that this call site may reach.
*/
class CallSite{
   InvokeExpr invokeExpr;
   String invokeExprId;
   void setCallerID ( String s ) {
      callerID = s;
   }


   String callerID;
   String getCallerID () { return callerID; }


   /**
   * contains possible MethodNodes reached by this call site
   */
   Set possibleMethods = new HashSet();
   //  public CallSite( InvokeExpr site ){
   //invokeExpr = site;
   //}

   CallSite( String invokeExprId ){
      this.invokeExprId = invokeExprId;
   }


   CallSite( String invokeExprId , InvokeExpr invokeExpr ){
      this.invokeExprId = invokeExprId;
      this.invokeExpr = invokeExpr;
   }


   // called only by a SCCnode.
   // Therefore notice that SCCNode doesn't have an invokeExpr
   CallSite(){
   }


   Integer integer;
   void setInteger(Integer i) { this.integer = i; }


   Integer getInteger() { return integer; }


   List getMethodsAsList() {
      List sortedmethods = new /* Array */ LinkedList();
      Object[] methodsarray = possibleMethods.toArray();
      Arrays.sort ( methodsarray, new StringComparator() );
      for (int i=0;i<methodsarray.length;i++)
      sortedmethods.add ( (MethodNode) methodsarray[i] );
      return sortedmethods;
   }


   Set getMethods(){
      return possibleMethods;
   }


   void setMethods( Set methods ) {
      possibleMethods = methods;
   }


   String getInvokeExprId(){
      // null when it's a SCCnode
      if( invokeExprId == null )
      throw new NoInvokeExprException();
      return invokeExprId;
   }


   InvokeExpr getInvokeExpr(){
      // null when it's a SCCnode
      if( invokeExpr == null )
      throw new NoInvokeExprException();
      return invokeExpr;
   }


   void addMethod( MethodNode mNode ){
      possibleMethods.add( mNode );
   }


   void removeMethod( MethodNode mNode ){
      possibleMethods.remove( mNode );
   }


   void prepareForGC(){
      possibleMethods = null;
   }


   /*
     protected void finalize() throws Throwable{
       System.out.println( "GC:  CallSite " + invokeExpr );

     }
   */
}




