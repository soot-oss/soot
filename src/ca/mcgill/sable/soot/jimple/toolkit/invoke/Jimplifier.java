// package ca.mcgill.sable.soot.sideEffect;

package ca.mcgill.sable.soot.jimple.toolkit.invoke;

import java.io.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import java.util.StringTokenizer;
/**
* Maintains a map of all jimplified methods
*/
public class Jimplifier{
  //DEBUG 
  static public int jimplifiedNum = 0;
  static public int benchjimplifiedNum = 0;
  static int removedNum = 0;
  static boolean CLGB = false;

  public static boolean NOLIB = true;

  /**
  * MethodName to JimpleBody
  */
  static Map MethodNameToJimpleBody = new HashMap();
  static private CallGraphBuilder callGB;
  // static private BuildBody buildAndStoreBody;

  static private Body buildAndStoreBody;
  public static int THREESHOLD = 1;
  public static HashMap sizeHT = new HashMap();
  public static int totnativebenchnummethods = 0;
  public static int totabstractbenchnummethods = 0;
  public static int totnumbenchmethods = 0;
  public static int totnativenummethods = 0;
  public static int totabstractnummethods = 0;
  public static int totnummethods = 0;
  public static int numstmts = 0;
  public static int benchnumstmts = 0;
  public static int cgnumstmts = 0;
  public static int cgbenchnumstmts = 0;
  static public void setCallGraphBuilder( CallGraphBuilder callgb ){
    callGB = callgb;
  }


  static public void removeall() { MethodNameToJimpleBody = null; }


  // throws an exception if something wrong.
  public static JimpleBody jimplify( SootMethod method ){

    buildAndStoreBody = null;  
    JimpleBody jimpleBody = null;

    if ( NOLIB )
    { 

     if ( method.getDeclaringClass().getName().startsWith ( "java." ) || method.getDeclaringClass().getName().startsWith ("sun.") )
     return null;
  
    } 

    // will add sootclasses that are referenced by the method into cm.
    // Avoid java.lang.Character.<clinit>
    //if ((method.getSignature().equals("java.lang.Character.<clinit>():void")))
    //throw new JimplificationException( method.getSignature());

    SootClass sootClass = method.getDeclaringClass();
    if( /* sootClass.getName().equals("java.lang.Character") || */
    sootClass.getName().equals("sun.tools.asm.Instruction") ){
      throw new JimplificationException( method.getSignature());
    }

    try{
      /*
            StringBuffer buffer = 
      	new StringBuffer( "\t HT size / Jimplified / Removed / GC:  ").
      	append( MethodNameToJimpleBody.size() ).append( " / " ).
      	append( jimplifiedNum++ ).append( " / " ).
      	append( removedNum ).append( " / " ).
      	append( JimpleBody.gcNum );

            System.out.println( buffer.toString() );
            */
      // if more than X (threeshold) methods are stored in the HT, 
      // then make some room
      // by removing X/2 methods.
      /*if( MethodNameToJimpleBody.size() >= THREESHOLD ){
        int i = 0;
        for( Iterator HTIter = MethodNameToJimpleBody.values().iterator();
        HTIter.hasNext() && i <= THREESHOLD/2 ; i++ ){
          // this will allow a GC.
          JimpleBody listBody = (JimpleBody)HTIter.next();
          SootMethod methodToRemove = listBody.getMethod();
          String methodName = methodToRemove.getSignature();
          // when the jimplification order comes from the callgraph
          // if want to be sure that the stmtlistbodt gets GC
          // we must put methodNode's method to null;
          if( callGB != null ){
            // it might happen that a jimplified method doesn't 
            // exist in MethodNOdeHT of the callGraph because that
            // jimplified method appears in THIS HT because the
            // classGraph ask it to be built.
            try{
              MethodNode mNode = callGB.getNode( methodName );
              mNode.setMethodToNull();
            }
            catch( NoSuchMethodNodeException e ){ }

          }

          remove( methodToRemove );
     

        }

      }

      */
      String methodName = Helper.getFullMethodName( method );
      if( !Main.NOPRINT ){
        StringBuffer buffer=new StringBuffer("\t soon In jimplified HT --- ").
        append(jimplifiedNum).append(" ").append( methodName );
        System.out.println( buffer.toString() );
      }

      //buildAndStoreBody should be set at Main, but if not then
      // use a stdrd one.

      Jimple jimple = Jimple.v();
      //   BodyExpr storedclass = new StoredBody ( ClassFile.v() );

      // ClassFileBody clbd = ( ClassFileBody ) storedclass.resolveFor ( method );

      ClassFileBody clbd = new ClassFileBody ( method );
      if ( ( buildAndStoreBody == null ) && ( CLGB ) ) {
        // buildAndStoreBody = new BuildBody( jimple, storedclass );
        buildAndStoreBody = new JimpleBody ( clbd, BuildJimpleBodyOption.USE_PACKING );
      }

      if ( CLGB )
      {
        // jimpleBody =(JimpleBody) buildAndStoreBody.resolveFor(method);
        /*
        jimpleBody = ( JimpleBody ) buildAndStoreBody;
        totnummethods++;
        if ( Modifier.isNative ( method.getModifiers() ) )
        totnativenummethods++;
        else if ( Modifier.isAbstract ( method.getModifiers() ) )
        totabstractnummethods++;
        boolean isJava = ClassGraphBuilder.isLibraryNode("java.",method.getDeclaringClass().getName());
        boolean isSun = ClassGraphBuilder.isLibraryNode("sun.",method.getDeclaringClass().getName());
        boolean isSunw = ClassGraphBuilder.isLibraryNode("sunw.",method.getDeclaringClass().getName());
        sizeHT.put ( method.getSignature(), new Integer ( jimpleBody.getStmtList().size() ) );
        numstmts = numstmts + jimpleBody.getStmtList().size();
        if ( ! ( isJava || isSun || isSunw ) )
        {
          totnumbenchmethods++;
          if ( Modifier.isNative ( method.getModifiers() ) )
          totnativebenchnummethods++;
          else if ( Modifier.isAbstract ( method.getModifiers() ) )
          totabstractbenchnummethods++;
          benchnumstmts = benchnumstmts + jimpleBody.getStmtList().size();
        }

        */

      }

      // insert the jimplified method into HT

      if ( ! CLGB )
      {
        //        System.out.println ( "BUILDING NEW JIMP FORM FOR "+method.getSignature() );
//        BuildAndStoreBody basb = null;
        Body basb = null;
        // if ( ( ( Integer ) sizeHT.get ( method.getSignature() ) ).intValue() < 200 ) 
        // if ( ! method.getName().equals ( "generateJimple" ) )  
//        basb = new BuildAndStoreBody( Jimple.v(), new StoredBody( ClassFile.v() ) , BuildJimpleBodyOption.NO_PACKING );
          basb = new JimpleBody( clbd, 0 );

        //       else
        //       basb = new BuildAndStoreBody( Jimple.v(), new StoredBody( ClassFile.v() ) /* , BuildJimpleBodyOption.NO_PACKING  */ );

//        jimpleBody = ( JimpleBody ) basb.resolveFor ( method );

        method.setActiveBody(basb);

        jimpleBody = ( JimpleBody ) basb;
        MethodNameToJimpleBody.put( methodName , jimpleBody );
        /*
        if ( method.getName().equals("toString") && method.getDeclaringClass().getName().equals("java.lang.Throwable") )
        { 

         Iterator stmtit = jimpleBody.getStmtList().iterator();

         while ( stmtit.hasNext() )
         System.out.println ( stmtit.next() );

         PrintWriter out = new PrintWriter(System.out, true);

         jimpleBody.printTo( out );

        }
        */
        jimplifiedNum++;
        boolean isJava = ClassGraphBuilder.isLibraryNode("java.",method.getDeclaringClass().getName());
        boolean isSun = ClassGraphBuilder.isLibraryNode("sun.",method.getDeclaringClass().getName());
        boolean isSunw = ClassGraphBuilder.isLibraryNode("sunw.",method.getDeclaringClass().getName());
        cgnumstmts = cgnumstmts + jimpleBody.getStmtList().size();
        if ( ! ( isJava || isSun || isSunw ) )
        {
          benchjimplifiedNum++;
          cgbenchnumstmts = cgbenchnumstmts + jimpleBody.getStmtList().size();
          totnumbenchmethods++;

          // System.out.println ( "Benchmark method number "+totnumbenchmethods+" is "+method.getSignature() );

          if ( Modifier.isNative ( method.getModifiers() ) )
          totnativebenchnummethods++;
          else if ( Modifier.isAbstract ( method.getModifiers() ) )
          totabstractbenchnummethods++;
          benchnumstmts = benchnumstmts + jimpleBody.getStmtList().size();
        }

        totnummethods++;
        if ( Modifier.isNative ( method.getModifiers() ) )
        totnativenummethods++;
        else if ( Modifier.isAbstract ( method.getModifiers() ) )
        totabstractnummethods++;

        sizeHT.put ( method.getSignature(), new Integer ( jimpleBody.getStmtList().size() ) );
        numstmts = numstmts + jimpleBody.getStmtList().size();


        // clbd.coffiMethod.cfg.reconstructInstructions();

        // clbd.coffiMethod.cfg = null;

        // System.out.println ( "BUILT NEW JIMP FORM FOR NODE NUMBER "+jimplifiedNum);
      }

      else
      {
        // clbd.coffiMethod.cfg.listBody = null;

        //         clbd.coffiMethod = null;

        //         clbd.coffiClass = null;

        //         clbd = null;

        // System.out.println ( " NULL 1 " );

/*
        if ( ! ( jimpleBody == null ) )
        {
          if ( jimpleBody.getStmtList() != null )
          {
            jimpleBody.getStmtList().setBodyToNull();
            jimpleBody.setStmtListToNull();
          }

        }

*/
        if ( ! ( clbd.coffiMethod.cfg == null ) )
        {
          clbd.coffiMethod.cfg.reconstructInstructions();
          clbd.coffiMethod.cfg = null;
          // System.out.println ( "THROW AWAY "+method.getDeclaringClass().getName() );
          jimpleBody = null;

        }

      }
      /*
      if ( Modifier.isNative ( method.getModifiers() ) )
      System.out.println ( "ADD NATIVE METHOD "+method.getSignature() );
      */

    }
    catch ( java.lang.NullPointerException e ){
      //e.printStackTrace( System.out );

      throw new JimplificationException( method.getSignature() +
      " because:\n\t" + e );
    }
    catch ( java.lang.RuntimeException e ){
      //e.printStackTrace( System.out );

      throw new JimplificationException( method.getSignature() +
      " because:\n\t" + e );
    }

    return jimpleBody;
  }


  /**
  * remove method from the ht.
  */
  public static void remove( SootMethod method ){
    MethodNameToJimpleBody.remove( Helper.getFullMethodName(method) );
    removedNum++;
    //System.out.println("\t Removed from HT --- " + method.getSignature() ); 
  }


  /**
  * returns method's stmtlistbody. (Jimplify first if necessary)
  */
  public static JimpleBody getJimpleBody( SootMethod method ){
    String methodName = Helper.getFullMethodName( method );
    JimpleBody jimpleBody = (JimpleBody)MethodNameToJimpleBody.
    get( methodName );
    if( jimpleBody == null )
    jimpleBody = jimplify( method );
    return jimpleBody;
  }


  /**
  * returns method's stmtlistbody if method has already been jimplified.
  */
  public static JimpleBody getJimpleBody( String fullMethodName ){
    JimpleBody jimpleBody = (JimpleBody)MethodNameToJimpleBody.
    get( fullMethodName );
    if( jimpleBody == null )
    throw new RuntimeException( " Not yet jimplified : " + fullMethodName );
    return jimpleBody;
  }


  /**
  * sets the BuildAndStoreBody object that will be used during
  * methods jimplification.
  */
  static void setBuildAndStoreBody( Body bsBody ){
    buildAndStoreBody = bsBody;
  }


  public static Map getMethodNameToJimpleBody() {
    return MethodNameToJimpleBody;
  }


}














































