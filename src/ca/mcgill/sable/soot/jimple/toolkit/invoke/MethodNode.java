// package ca.mcgill.sable.soot.sideEffect;

package ca.mcgill.sable.soot.jimple.toolkit.invoke;

import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;
 
// OLD version with the old SCCDetector
//public class MethodNode implements GraphNode{

public class MethodNode extends DfsNode{

  public HashSet ImportantInvokeExprs = new HashSet();

  public int code;

  public int incomingedges = 0;

  public int numloops = 0;

  public boolean alreadyInlined = false;

  public boolean isRedundant = false;

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





  public MethodNode(){
  }


  // the method signature
  String name ;

  String className;
  String methodName;
  List params;
  Type returnType;




  public MethodNode( SootMethod method ){
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
  public String getName(){
    //return getMethod().getSignature();    
    return name;
  }


  public String getClassName(){
    return className;
  }


  public String getMethodName(){
    return methodName;
  }


  public Type getReturnType(){
    return returnType;
  }


  public Collection getCallSites(){
    return CallSites.values();
  }
  

  public SootMethod getMethod(){
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
  public void setMethodToNull(){
    method = null;
  }

  
  public void addCallSite( CallSite cs, Integer integer ){
    // VIJAY ON MARCH 1 CallSites.put( cs.getInvokeExprId() , cs );
    CallSites.put ( cs.getInvokeExpr(), cs );

    CallSitesHT.put ( integer, cs );

  }

  
  public CallSite getCallSite( String invokeExprId ){
    CallSite callSite = (CallSite)CallSites.get( invokeExprId );
    
    if( callSite == null )
      throw new NoSuchCallSiteException( invokeExprId  + "  IN  " +
					 this.getName() );
    return callSite;
  }


  public CallSite getCallSite ( InvokeExpr invokeexpr ) {

    CallSite callSite = (CallSite) CallSites.get( invokeexpr );

   if( callSite == null )
      throw new NoSuchCallSiteException( invokeexpr  + "  IN  " +
                                         this.getName() );
    return callSite;
  }


   public CallSite getCallSite ( Integer integer ) {

    CallSite callSite = (CallSite) CallSitesHT.get( integer );

     if( callSite == null )
      throw new NoSuchCallSiteException( integer  + "  IN  " +
                                         this.getName() );
    return callSite;
  }




  //public void setCallSite( InvokeExpr invokeExpr , CallSite csite ){
  // CallSites.put( invokeExpr , csite );
  //}



  public void addInvokingSite( CallSite cs ) {
   Invokers.add ( cs );
  }

  public void removeInvokingSite( CallSite cs ) {
   if( Invokers.contains(cs) )
    Invokers.remove( cs );
  }


  public List getInvokingSites() {
   return Invokers;
  }



  public void addCaller( MethodNode caller ){
    CalledBy.add( caller );
  }

  public void removeCaller( MethodNode caller ){
    if( CalledBy.contains(caller) )
      CalledBy.remove( caller );
  }

  public Set getCallers(){
    return CalledBy;
  }


  public void setCallers( Set callers ){
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
  public Set getAllPossibleMethods(){
    Set allMethods = new HashSet();

    // for all callSites collect all methods possibly reached
    //Object[] keys = CallSites.keySet().toArray();
    //for ( int i = 0 ; i < keys.length ; i++ ){
    // CallSite callSite = (CallSite)CallSites.get( keys[i] );
    
    Iterator iter = CallSites.values().iterator();
    while( iter.hasNext() ){
      CallSite callSite = (CallSite)iter.next();
      Object[] callSMethods = callSite.getMethods().toArray();
      for ( int c = 0 ; c < callSMethods.length ; c++ )
	allMethods.add( callSMethods[c] );
    }

    return allMethods;
  }


    /**
     *
     */
    public void prepareForGC(){

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











