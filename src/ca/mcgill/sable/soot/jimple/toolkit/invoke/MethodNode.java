// package ca.mcgill.sable.soot.sideEffect;

package ca.mcgill.sable.soot.jimple.toolkit.invoke;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;
// OLD version with the old SCCDetector
//public class MethodNode implements GraphNode{

class MethodNode extends DfsNode{
   HashSet ImportantInvokeExprs = new HashSet();
   List invokeExprs = new ArrayList();
   int code;
   int incomingedges = 0;
   int numloops = 0;
   boolean alreadyInlined = false;
   boolean isRedundant = false;
   SootMethod method;
   //List CallSites = new ArrayList();

   Map CallSites = new HashMap();
   Map CallSitesHT = new HashMap();
   /**
   * all methodNodes pointing to this methodNode
   */
   Set CalledBy = new HashSet();
   /**
   * either points to the 'CalledBy' List when the callgraph is "transposed"
   * or a list of methodNodes pointed to by ALL callSites.
   * By default points to CallSites.
   */
   Set AdjacentNodes;
   List Invokers = new ArrayList();
   MethodNode(){
   }


   // the method signature
   String name ;
   String className;
   String methodName;
   List params;
   Type returnType;
   MethodNode( SootMethod method ){
      //    if( Main.SPEED )

      this.method = method;
      // to speed up things by avoid reloading method each time we need
      // the following info
      className = method.getDeclaringClass().getName();
      methodName = method.getName();
      params = method.getParameterTypes();
      returnType = method.getReturnType();
      name = method.getSignature();
   }


   /**
   * name is the method signature
   */
   String getName(){
      //return getMethod().getSignature();    
      return name;
   }


   String getClassName(){
      return className;
   }


   String getMethodName(){
      return methodName;
   }


   Type getReturnType(){
      return returnType;
   }


   Collection getCallSites(){
      return CallSites.values();
   }


   SootMethod getMethod(){
      //    if( method != null )

      return method;
      //    return new SootClassManager().getClass( className ).
      //      getMethod( methodName, params );

      /*if( method == null ){
         method = new SootClassManager().getClass( className ).
         getMethod( methodName, params );
      }

      return method;
      */
   }


   //DEBUG
   void setMethodToNull(){
      method = null;
   }


   void setInvokeExprs(List invokeExprs) {
      this.invokeExprs = invokeExprs;
   }


   void addInvokeExpr( InvokeExpr ie ) {
      invokeExprs.add(ie);
   }


   void removeInvokeExpr( InvokeExpr ie ) {
      invokeExprs.remove(ie);
   }


   List getInvokeExprs() {
      return invokeExprs;
   }


   void addCallSite( CallSite cs, Integer integer ){
      // VIJAY ON MARCH 1 CallSites.put( cs.getInvokeExprId() , cs );
      CallSites.put ( cs.getInvokeExpr(), cs );
      CallSitesHT.put ( integer, cs );
      cs.setInteger(integer);
   }


   void removeCallSite( CallSite cs ) {
      CallSites.remove ( cs.getInvokeExpr() );
      CallSitesHT.remove ( cs.getInteger() );
   }


   CallSite getCallSite( String invokeExprId ){
      CallSite callSite = (CallSite)CallSites.get( invokeExprId );
      if( callSite == null )
      throw new NoSuchCallSiteException( "The invoke expression "+invokeExprId + " was not found in the method " +
      this.getName()+" in the invoke graph" );
      return callSite;
   }


   CallSite getCallSite ( InvokeExpr invokeexpr ) {
      CallSite callSite = (CallSite) CallSites.get( invokeexpr );
      if( callSite == null )
      throw new NoSuchCallSiteException( "The invoke expression "+invokeexpr + " was not found in the method " +
      this.getName()+" in the invoke graph" );
      return callSite;
   }


   CallSite getCallSite ( Integer integer ) {
      CallSite callSite = (CallSite) CallSitesHT.get( integer );
      if( callSite == null )
      throw new NoSuchCallSiteException( "The "+integer+"th invoke expression was not found in the method " +
      this.getName()+" in the invoke graph" );
      return callSite;
   }


   //public void setCallSite( InvokeExpr invokeExpr , CallSite csite ){
   // CallSites.put( invokeExpr , csite );
   //}

   void addInvokingSite( CallSite cs ) {
      Invokers.add ( cs );
   }


   void removeInvokingSite( CallSite cs ) {
      if( Invokers.contains(cs) )
      Invokers.remove( cs );
   }


   List getInvokingSites() {
      return Invokers;
   }


   void addCaller( MethodNode caller ){
      CalledBy.add( caller );
   }


   void removeCaller( MethodNode caller ){
      if( CalledBy.contains(caller) )
      CalledBy.remove( caller );
   }


   Set getCallers(){
      return CalledBy;
   }


   void setCallers( Set callers ){
      CalledBy = callers;
   }


   /**
   * used to transpose the callgraph. ( needed for str. conn. comp.)
   */
   //public void transposeNode( boolean bool ){
   //if ( bool == true )
   //  AdjacentNodes = CalledBy;
   //else
   //  setAdjNodesToCallSites();      
   //}

   private void setAdjNodesToCallSites(){
      AdjacentNodes = new HashSet();
      Iterator iter = CallSites.values().iterator();
      while( iter.hasNext() ){
         CallSite callSite = (CallSite)iter.next();
         // methods here are MethodNodes
         Object[] methods = callSite.getMethods().toArray();
         for ( int m = 0 ; m < methods.length ; m++ )
         AdjacentNodes.add( methods[m] );
      }

   }


   // for GraphNode Interface
   public List getAdjacentNodes(){
      if ( AdjacentNodes == null )
      setAdjNodesToCallSites();
      ArrayList al = new ArrayList();
      al.addAll ( AdjacentNodes );
      return al;
   }


   /**
   * for all callSites collect all methods possibly reached
   */
   Set getAllPossibleMethods(){
      Set allMethods = new HashSet();
      // for all callSites collect all methods possibly reached
      //Object[] keys = CallSites.keySet().toArray();
      //for ( int i = 0 ; i < keys.length ; i++ ){
      // CallSite callSite = (CallSite)CallSites.get( keys[i] );

      // Iterator iter = CallSites.values().iterator();

      Iterator iter = getInvokeExprs().iterator();
      while( iter.hasNext() ){
         CallSite callSite = getCallSite ( (InvokeExpr)iter.next());
         Object[] callSMethods = callSite.getMethodsAsList().toArray();
         for ( int c = 0 ; c < callSMethods.length ; c++ )
         allMethods.add( callSMethods[c] );
      }

      return allMethods;
   }


   List getAllPossibleMethodsAsList() {
      List sortedmethods = new /* Array */ LinkedList();
      Object[] methodsarray = getAllPossibleMethods().toArray();
      Arrays.sort ( methodsarray, new StringComparator() );
      for (int i=0;i<methodsarray.length;i++)
      sortedmethods.add ( (MethodNode) methodsarray[i] );
      return sortedmethods;
   }


   /**
   *
   */
   void prepareForGC(){
      if( CallSites != null ){
         //CallSite callSite;

         //	    Object[] keys = CallSites.keySet().toArray();
         //for ( int i = 0 ; i < keys.length ; i++ ){
         //callSite = (CallSite)CallSites.get( keys[i] );

         Iterator iter = CallSites.values().iterator();
         while( iter.hasNext() ){
            CallSite callSite = (CallSite)iter.next();
            callSite.prepareForGC();
         }

      }

      CalledBy = null;
      method = null;
   }


   protected void finalize() throws Throwable {
      //    System.out.println( "GC:  MethodNode " + getName() );

   }


}




