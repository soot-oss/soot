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
class Jimplifier{
   //DEBUG 
   static int jimplifiedNum = 0;
   static int benchjimplifiedNum = 0;
   static int removedNum = 0;
   static boolean CLGB = false;
   static public boolean NOLIB = true;
   /**
   * MethodName to JimpleBody
   */
   static Map MethodNameToJimpleBody = new HashMap();
   static private CallGraphBuilder callGB;
   static private Body buildAndStoreBody;
   static HashMap sizeHT = new HashMap();
   static int totnativebenchnummethods = 0;
   static int totabstractbenchnummethods = 0;
   static int totnumbenchmethods = 0;
   static int totnativenummethods = 0;
   static int totabstractnummethods = 0;
   static int totnummethods = 0;
   static int numstmts = 0;
   static int benchnumstmts = 0;
   static int cgnumstmts = 0;
   static int cgbenchnumstmts = 0;
   static List classesToAnalyze;
   static void setCallGraphBuilder( CallGraphBuilder callgb ){
      callGB = callgb;
   }


   static void removeall() { MethodNameToJimpleBody = null; }


   // throws an exception if something wrong.
   static JimpleBody jimplify( SootMethod method ){
      buildAndStoreBody = null;
      JimpleBody jimpleBody = null;
      if ( ! ( classesToAnalyze == null ) )
      {
         if ( ! classesToAnalyze.contains(method.getDeclaringClass().getName()) )
         return null;
      }

      if ( NOLIB )
      {
         if ( method.getDeclaringClass().getName().startsWith ( "java." ) || method.getDeclaringClass().getName().startsWith ("sun.") || method.getDeclaringClass().getName().startsWith("sunw.") || method.getDeclaringClass().getName().startsWith("javax.") || method.getDeclaringClass().getName().startsWith("com.") || method.getDeclaringClass().getName().startsWith("org.") )
         return null;
      }

      try{
         String methodName = Helper.getFullMethodName( method );
         if( !Main.NOPRINT ){
            StringBuffer buffer=new StringBuffer("\t soon In jimplified HT --- ").
            append(jimplifiedNum).append(" ").append( methodName );
            System.out.println( buffer.toString() );
         }

         Jimple jimple = Jimple.v();
         ClassFileBody clbd = new ClassFileBody ( method );
         // if ( ( buildAndStoreBody == null ) && ( CLGB ) ) 
         // buildAndStoreBody = new JimpleBody ( clbd, BuildJimpleBodyOption.USE_PACKING );

         if ( ! CLGB )
         {
            Body basb = null;
            basb = new JimpleBody( clbd, 0 );
            method.setActiveBody(basb);
            jimpleBody = ( JimpleBody ) basb;
            MethodNameToJimpleBody.put( methodName , jimpleBody );
            jimplifiedNum++;
            boolean isJava = ClassGraphBuilder.isLibraryNode("java.",method.getDeclaringClass().getName());
            boolean isSun = ClassGraphBuilder.isLibraryNode("sun.",method.getDeclaringClass().getName());
            boolean isSunw = ClassGraphBuilder.isLibraryNode("sunw.",method.getDeclaringClass().getName());
            boolean isJavax = ClassGraphBuilder.isLibraryNode("javax.",method.getDeclaringClass().getName());
            boolean isOrg = ClassGraphBuilder.isLibraryNode("org.",method.getDeclaringClass().getName());
            boolean isCom = ClassGraphBuilder.isLibraryNode("com.",method.getDeclaringClass().getName());
            cgnumstmts = cgnumstmts + jimpleBody.getStmtList().size();
            if ( ! ( isJava || isSun || isSunw || isJavax || isOrg || isCom ) )
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
         }

         else
         {
            if ( ! ( clbd.coffiMethod.cfg == null ) )
            {
               clbd.coffiMethod.cfg.reconstructInstructions();
               clbd.coffiMethod.cfg = null;
               // System.out.println ( "THROW AWAY "+method.getDeclaringClass().getName() );
               jimpleBody = null;
            }

         }

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
   static void remove( SootMethod method ){
      MethodNameToJimpleBody.remove( Helper.getFullMethodName(method) );
      removedNum++;
      //System.out.println("\t Removed from HT --- " + method.getSignature() ); 
   }


   /**
   * returns method's stmtlistbody. (Jimplify first if necessary)
   */
   static JimpleBody getJimpleBody( SootMethod method ){
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
   static JimpleBody getJimpleBody( String fullMethodName ){
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


   static Map getMethodNameToJimpleBody() {
      return MethodNameToJimpleBody;
   }


}




