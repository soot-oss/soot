// package ca.mcgill.sable.soot.sideEffect;
package ca.mcgill.sable.soot.jimple.toolkit.invoke;

import ca.mcgill.sable.soot.jimple.*;
 
import ca.mcgill.sable.util.*;
import java.util.*;

/**
 * represents a call site in the code and contains links to all possible methods that this call site  may reach.
 */
public class CallSite{

  InvokeExpr invokeExpr;
  
  String invokeExprId;

  public void setCallerID ( String s ) {

   callerID = s;

 }

 String callerID;

 public String getCallerID () { return callerID; }

  
  /**
   * contains possible MethodNodes reached by this call site
   */
  Set possibleMethods = new HashSet();

  //  public CallSite( InvokeExpr site ){
  //invokeExpr = site;
  //}

  public CallSite( String invokeExprId ){
    this.invokeExprId = invokeExprId;
  }


  public CallSite( String invokeExprId , InvokeExpr invokeExpr ){
    this.invokeExprId = invokeExprId;
    this.invokeExpr = invokeExpr;
  }


  // called only by a SCCnode.
  // Therefore notice that SCCNode doesn't have an invokeExpr
  public CallSite(){
  }


  Integer integer;

  public void setInteger(Integer i) { this.integer = i; }

  public Integer getInteger() { return integer; }

  
  public List getMethodsAsList() {

   List sortedmethods = new /* Array */ LinkedList();

   Object[] methodsarray = possibleMethods.toArray();

   Arrays.sort ( methodsarray, new StringComparator() );

   for (int i=0;i<methodsarray.length;i++)
   sortedmethods.add ( (MethodNode) methodsarray[i] );

   return sortedmethods;

  }





  public Set getMethods(){
    return possibleMethods;
  }

  public void setMethods( Set methods ) {

    possibleMethods = methods;

  }


  public String getInvokeExprId(){
    // null when it's a SCCnode
    if( invokeExprId == null )
      throw new NoInvokeExprException();

    return invokeExprId;
  }

  public InvokeExpr getInvokeExpr(){
    // null when it's a SCCnode
    if( invokeExpr == null )
      throw new NoInvokeExprException();

    return invokeExpr;
  }

  public void addMethod( MethodNode mNode ){
    possibleMethods.add( mNode );
  }

  public void removeMethod( MethodNode mNode ){
    possibleMethods.remove( mNode );
  }

  public void prepareForGC(){
    possibleMethods = null;
  }

/*
  protected void finalize() throws Throwable{
    System.out.println( "GC:  CallSite " + invokeExpr );

  }
*/

}






