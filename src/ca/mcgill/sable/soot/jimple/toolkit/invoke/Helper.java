
package ca.mcgill.sable.soot.jimple.toolkit.invoke;

// package ca.mcgill.sable.soot.sideEffect;

import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import java.util.*;

public class Helper{

  public static String getFullMethodName( SootMethod method ){
    return method.getSignature(); // the sig. is full.
  }
  
  /**
   *  
   */
  // Should be implemented in SootClass.
  public static SootMethod getMethod( SootClass bClass , String methodName , List paramAsString ) throws ca.mcgill.sable.soot.NoSuchMethodException{
    
    Iterator methodIt = bClass.getMethods().iterator();
    
    while(methodIt.hasNext()){
      
      SootMethod method = (SootMethod) methodIt.next();
      
      List methodParamAsString = new ArrayList();

      for( Iterator methParamIter = method.getParameterTypes().iterator();
	   methParamIter.hasNext() ; ){
    	methodParamAsString.add( ((Type)methParamIter.next()).toString() );
      }
      /*
      System.out.println ( "IN CLASS "+bClass.getName() );
      System.out.println ( "CHECKING FOR MTHD "+methodName );
      System.out.println ( method.getName().equals( methodName ) );
      System.out.println ( methodParamAsString.equals( paramAsString ) );
      */
      if( method.getName().equals( methodName ) && 
	  methodParamAsString.equals( paramAsString ) )
	  return method;
    }

    throw new ca.mcgill.sable.soot.NoSuchMethodException( methodName);
  }


  public static String getLocalId( MethodNode methodNode , Local local ){
    StringBuffer buffer = new StringBuffer();

    /*buffer.append("[").append( local.getType().toString() ).append( "]" );
    buffer.append("{").
      append(methodNode.getName()).
      append( "}" ).append( local.getName() );
      */

    buffer.append("[").append(methodNode.getName()).append("]").
      append( local.getName() );
      
    return buffer.toString();
  }

  
  public static String getInstanceFieldId(  SootField field ){
    StringBuffer buffer = new StringBuffer();

    //buffer.append("[").append( field.getType().toString() ).append( "]" );
    //buffer.append( field.getDeclaringClass().getName() ).append( "." );
    buffer.append( field.getName() );

    return buffer.toString();
  }


  public static String getStaticFieldId( SootField field ){
    StringBuffer buffer = new StringBuffer();
    
    buffer.append("[").append( field.getType().toString() ).append( "]" );
    buffer.append( field.getDeclaringClass().getName() ).append( "." ).
      append( field.getName() );
   
    return buffer.toString();
  }


  public static String getInvokeExprId( int invokeExprNum , SootMethod method ){
    StringBuffer buffer = new StringBuffer( Integer.toString(invokeExprNum) ).
      append( method.getSignature() );
    
    return buffer.toString();
  }


  public static String getStmtId( int stmtNum , Stmt stmt ){
    StringBuffer buffer = new StringBuffer( Integer.toString(stmtNum) ).
      append( stmt.toString() );
    
    return buffer.toString();
  }


  public static String getArrayElemId(){
    return "ArrayElem";
  }


  public static String getThisId( MethodNode methodNode ){
    StringBuffer buffer = new StringBuffer();

    buffer.append("[").append(methodNode.getName()).append("]").
      append(".this");
      
    return buffer.toString();

    /*
    //TEST: return [className].this
    buffer.append( methodNode.getClassName() ).append(".this");

    return buffer.toString();
    */
  }    






public static ArrayList CNHT2VL ( Map hashtable ) {

 ArrayList vectorlist = new ArrayList(); 

 Object[] keys = hashtable.keySet().toArray();
    for ( int i = 0 ; i < keys.length ; i++ )
    vectorlist.add( ((ClassNode)hashtable.get( keys[i] )) );

return vectorlist;

}



  /* 

public static ArrayList MNHT2VL ( Map hashtable ) {

ArrayList vectorlist = new ArrayList();

 Object[] keys = hashtable.keySet().toArray();

    for ( int i = 0 ; i < keys.length ; i++ )
    vectorlist.add( ((MethodNode)hashtable.get( keys[i] )) );

return vectorlist;

}


public static ArrayList TNHT2VL ( Map hashtable ) {

ArrayList vectorlist = new ArrayList();

 Object[] keys = hashtable.keySet().toArray();
    for ( int i = 0 ; i < keys.length ; i++ )
    vectorlist.add( ((TypeNode)hashtable.get( keys[i] )) );

return vectorlist;

}

*/



 public static ArrayList cnode2bclass(ArrayList cnodeV )
 {
    ArrayList bclassV = new ArrayList();

    Iterator iter = cnodeV.iterator();
    while ( iter.hasNext() )
    {
     bclassV.add(((ClassNode)iter.next()).getSootClass());
    }

    return bclassV;
 }



  /*


 public static ArrayList mnode2method(ArrayList mnodeV )
 {
    ArrayList methodV = new ArrayList();
 
    Iterator iter = mnodeV.iterator();
    while ( iter.hasNext() )
    {
     methodV.add(((MethodNode)iter.next()).getMethod());
    }
 
    return methodV;
 }


 public static ArrayList CSAL2VL(ArrayList CSAL )
 {
    ArrayList CSVL = new ArrayList();

    Iterator iter = CSAL.iterator();
    while ( iter.hasNext() )
    {
     CSVL.add(((CallSite)iter.next()));
    }
    
    return CSVL;
 }

 

 public static Set TNAL2VS(ArrayList TNAL )
 {
   VectorSet TNVS = new VectorSet();

    Iterator iter = TNAL.iterator();

    while ( iter.hasNext() )
    {
     TypeNode tn = (TypeNode)iter.next();

     TNVS.add(tn);
    }

    return TNVS;
 }

 */ 

 public static boolean IsRedundantNode ( String s1, String s2 )
 {
  int len = s1.length();


  if ( !( s1.length() > s2.length() ) )   
  {
  String s3 = s2.substring(0,len);

//  System.out.println("DEBUG -- "+s1+" "+s3);


  if ( s1.compareTo(s3) == 0 )
  return true;
  else 
  return false;
  }
  else return false; 

 }   

















}



















