package ca.mcgill.sable.soot.jimple.toolkit.invoke;
import java.io.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.soot.grimp.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.toolkit.invoke.*;
class optRTA {
   private static boolean assignflag, invokeflag;
   private static Value returnvariable;
   private static InvokeExpr currInvokeExpr;
   // private static List processedmethods;

   private static List unprocessedmethods;
   private static Map processedmethodsHT;
   private static Map instancetypesHT = new HashMap();
   private static List newmethodsiniteration;
   private static List newclassesiniteration;
   private static Map pendinglistsHT = new HashMap();
   private static Map pendingtargetsHT = new HashMap();
   private static ClassGraphBuilder classgraphbuilder;
   private static Scene manager;
   private static InvokeGraph callgraph;
   private static InvokeGraph rtacallgraph;
   private static boolean arraycreated = true;
   private static int CHAnodes = 0;
   private static int CHAtotal = 0;
   private static int CHAmono = 0;
   private static int CHApoly = 0;
   private static int CHAedges = 0;
   private static int CHAmonoedges = 0;
   private static int CHApolyedges = 0;
   private static int CHAbenchnodes = 0;
   private static int CHAbenchtotal = 0;
   private static int CHAbenchmono = 0;
   private static int CHAbenchpoly = 0;
   private static int CHAbenchedges = 0;
   private static int CHAbenchmonoedges = 0;
   private static int CHAbenchpolyedges = 0;
   private static boolean CHA = false;
   static void main ( String[] args ) {
      ca.mcgill.sable.soot.Main.sootClassPath = "/home/profs/hendren/JavaBench/EiffelSuite/benchmarks/illness/classes:/home/acaps/u2/vijay/PIZZA/pizza/classes:/home/profs/hendren/JavaBench/AdaSuite/kalman/classes:/home/profs/hendren/JavaBench/MLSuite/benchmarks/lexgen/classes:/home/profs/hendren/JavaBench/AdaSuite/rudstone/classes:/home/profs/hendren/JavaBench/MLSuite/benchmarks/nucleic/classes:home/profs/hendren/JavaBench/SchemeSuite:/tmp/sablecc-2.9:/home/profs/hendren/JavaBench/FromML/boyer/Classes:/home/acaps/u2/vijay/local/ADABENCH/Dhrystone:/home/acaps/u2/vijay/local/ADALIB:/home/profs/hendren/JavaBench/EiffelSuite/benchmarks/compile_to_c/classes:/home/acaps/u2/vijay/JDKCLASSES:/home/acaps/u2/vijay/local/PUZZLE/puzzle/classes:/home/acaps/u2/vijay/BENCH/BENCH/jvm98:/home/acaps/u2/vijay:/home/acaps/u2/vijay/LATESTUTIL/sableUtil-1.11/classes:/home/acaps/u2/vijay/SOOT2/src:/home/acaps/u2/vijay:/tmp";
      // ca.mcgill.sable.soot.Main.isVerbose = true;

      manager = Scene.v();
      SootClass sc = manager.loadClassAndSupport( args[0] );
      // InvokeGraph callgraph = new InvokeGraph ( sc, true ); 

      Jimplifier.NOLIB = false;
      // Jimplifier.NOLIB = true;
      Resolver.NOLIB = true;
      Inliner.NOLIB = true;
      callgraph = ClassHierarchyAnalysis.newInvokeGraph ( sc );
      CHA = true;
      printCallGraph(callgraph);
      CHA = false;
      System.out.println("**********************************************************************************");
      classgraphbuilder = callgraph.getCHA().getClassGraphBuilder();
      rtacallgraph = new InvokeGraph( sc );
      // Set optimizedclasses = GlobalInvokeInliner.inlineInvokes(callgraph);

      // callgraph = RapidTypeAnalysis.pruneInvokeGraph( callgraph );

      // printCallGraph(callgraph);

      // callgraph = DeclaredTypeAnalysis.pruneInvokeGraph( callgraph );

      //  callgraph = VariableTypeAnalysis.pruneInvokeGraph( callgraph );

      // printCallGraph(callgraph);

      // callgraph = VariableTypeAnalysis.pruneInvokeGraph( callgraph );

      // printCallGraph(callgraph);

      // Set optimizedclasses = GlobalInvokeInliner.inlineInvokes(callgraph);

      // Iterator optimizedit = optimizedclasses.iterator();

      // while ( optimizedit.hasNext() )
      // System.out.println ( "OPTIMIZED "+((SootClass) optimizedit.next()).getName());

      unprocessedmethods = callgraph.getEntryMethods();
      System.out.println("Entry size = "+unprocessedmethods.size());
      // processedmethods = new ArrayList();

      processedmethodsHT = new HashMap();
      int numiter = 0;
      while ( ! stopiterating() )
      {
         numiter++;
         newclassesiniteration = new ArrayList();
         newmethodsiniteration = new ArrayList();
         Iterator unprocessedmethodsit = unprocessedmethods.iterator();
         while ( unprocessedmethodsit.hasNext() )
         {
            SootMethod unprocessedmethod = (SootMethod) unprocessedmethodsit.next();
            // System.out.println("Looked at ********************** : "+unprocessedmethod.getSignature());

            processedmethodsHT.put(unprocessedmethod, unprocessedmethod);
            rtacallgraph.addMethod(unprocessedmethod);
            if (numiter == 1)
            rtacallgraph.addEntryMethod(unprocessedmethod);
            if ( unprocessedmethod.hasActiveBody() )
            {
               // System.out.println("HAS ACTIVE BODY FOR "+unprocessedmethod.getSignature());

               JimpleBody listBody = (JimpleBody) unprocessedmethod.getActiveBody();
               Iterator stmtIter = listBody.getStmtList().iterator();
               while ( stmtIter.hasNext() )
               {
                  Stmt stmt = (Stmt) stmtIter.next();
                  stmt.apply( new AbstractStmtSwitch() {
                     public void caseAssignStmt(AssignStmt s){
                        if ( s.getRightOp() instanceof NewExpr )
                        {
                           RefType reftype = ((NewExpr)s.getRightOp()).getBaseType();
                           if ( ((SootClass) instancetypesHT.get(reftype.className)) == null )
                           {
                              SootClass newclass = manager.getClass(reftype.className);
                              System.err.println(newclass.getName());
                              newclassesiniteration.add(newclass);
                              instancetypesHT.put ( reftype.className, newclass );
                           }

                        }

                     }

                  });

               }

            }

            Iterator invokeexprsit = callgraph.getInvokeExprsOf(unprocessedmethod).iterator();
            // System.out.println("INVOKE EXPRS SIZE = "+callgraph.getInvokeExprsOf(unprocessedmethod).size() );

            while ( invokeexprsit.hasNext() )
            {
               InvokeExpr invokeexpr = (InvokeExpr) invokeexprsit.next();
               rtacallgraph.addInvokeExpr(invokeexpr, unprocessedmethod);
               List targetsallowedbyRTA = null;
               if ( ( invokeexpr instanceof SpecialInvokeExpr ) || ( invokeexpr instanceof StaticInvokeExpr ) )
               targetsallowedbyRTA = callgraph.getTargetsOf(invokeexpr);
               else
               targetsallowedbyRTA = getTargetsAllowedByRTA(invokeexpr, callgraph.getTargetsOf(invokeexpr));
               Iterator targetsit = targetsallowedbyRTA.iterator();
               while ( targetsit.hasNext() )
               {
                  SootMethod targetmethod = (SootMethod) targetsit.next();
                  rtacallgraph.addMethod(targetmethod);
                  rtacallgraph.addTarget(invokeexpr, targetmethod);
                  newmethodsiniteration.add(targetmethod);
               }

            }

         }

         examinePendingLists(newclassesiniteration);
         unprocessedmethods = newmethodsiniteration;
         // System.out.println("****************************************************************************************");
         System.out.println("Done with iteration : "+numiter);
         // printCallGraph(rtacallgraph);
      }

      // Iterator iter = instancetypesHT.values().iterator();

      // while ( iter.hasNext() )
      // System.out.println( "INSTANCE "+((SootClass)iter.next()).getName() ); 

      printCallGraph(rtacallgraph);
   }


   static void printCallGraph(InvokeGraph lazycallgraph ) {
      int nodes = 0, edges = 0;
      int benchnodes = 0, benchedges = 0;
      int mono = 0, benchmono = 0, poly = 0, benchpoly = 0;
      int monoedges = 0, polyedges = 0, benchmonoedges = 0, benchpolyedges = 0;
      Iterator reachablemethodsit = lazycallgraph.getReachableMethods().iterator();
      System.out.println();
      System.out.println ("-----------------------------------------------------------------------------------");
      // System.out.println( "Number of reachable methods = "+lazycallgraph.getReachableMethods().size());

      boolean counting = false;
      while ( reachablemethodsit.hasNext() )
      {
         counting = false;
         SootMethod m = ( SootMethod ) reachablemethodsit.next();
         if ( ! ( m.getDeclaringClass().getName().startsWith("java") || m.getDeclaringClass().getName().startsWith("sun")) )
         counting = true;
         if ( counting )
         benchnodes++;
         nodes++;
         // if ( counting ) 
         // {
         //   System.out.println(); 
         //  System.out.println ( "METHOD BEING ANALYZED "+m.getSignature() );
         // }

         List invokeExprs = lazycallgraph.getInvokeExprsOf(m);
         Iterator ieit = invokeExprs.iterator();
         while ( ieit.hasNext() )
         {
            currInvokeExpr = (InvokeExpr) ieit.next();
            boolean single = false;
            // if ( counting )  
            // {
            //  System.out.println(); 
            //  System.out.println ( "Invoke Expr : "+currInvokeExpr ); 
            // }

            Iterator targetsit = lazycallgraph.getTargetsOf(currInvokeExpr).iterator();
            if ( lazycallgraph.getTargetsOf(currInvokeExpr).size() == 1 )
            {
               single = true;
               if (counting)
               benchmono++;
               mono++;
            }

            else if ( lazycallgraph.getTargetsOf(currInvokeExpr).size() == 0 )
            {
               if (counting)
               benchmono++;
               mono++;
            }

            else
            {
               if (counting)
               benchpoly++;
               poly++;
            }

            // System.out.println("CAUTION : CALLSITE "+currInvokeExpr+" WITH NO TARGET");

            while ( targetsit.hasNext() )
            {
               if ( counting )
               {
                  benchedges++;
                  if (single)
                  benchmonoedges++;
                  else
                  benchpolyedges++;
               }

               edges++;
               if (single)
               monoedges++;
               else
               polyedges++;
               SootMethod target = ( SootMethod ) targetsit.next();
               // if ( counting )
               // System.out.println ( " might invoke method "+target.getSignature() );

            }

         }

      }

      System.out.println();
      System.out.println("NUMBER OF NODES = "+nodes);
      System.out.println("NUMBER OF SITES = "+(mono+poly));
      System.out.println("NUMBER OF RESOLVED SITES = "+mono);
      System.out.println("NUMBER OF UNRESOLVED SITES = "+poly);
      System.out.println("NUMBER OF EDGES = "+edges);
      System.out.println("NUMBER OF MONOMORPHIC EDGES = "+monoedges);
      System.out.println("NUMBER OF POLYMORPHIC EDGES = "+polyedges);
      System.out.println();
      System.out.println("NUMBER OF BENCH NODES = "+benchnodes);
      System.out.println("NUMBER OF BENCH SITES = "+(benchmono+benchpoly));
      System.out.println("NUMBER OF RESOLVED BENCH SITES = "+benchmono);
      System.out.println("NUMBER OF UNRESOLVED BENCH SITES = "+benchpoly);
      System.out.println("NUMBER OF BENCH EDGES = "+benchedges);
      System.out.println("NUMBER OF BENCH MONOMORPHIC EDGES = "+benchmonoedges);
      System.out.println("NUMBER OF BENCH POLYMORPHIC EDGES = "+benchpolyedges);
      System.out.println();
      if (CHA)
      {
         CHAnodes = nodes;
         CHAtotal = mono+poly;
         CHAmono = mono;
         CHApoly = poly;
         CHAedges = edges;
         CHAmonoedges = monoedges;
         CHApolyedges = polyedges;
         CHAbenchnodes = benchnodes;
         CHAbenchtotal = benchmono+benchpoly;
         CHAbenchmono = benchmono;
         CHAbenchpoly = benchpoly;
         CHAbenchedges = benchedges;
         CHAbenchmonoedges = benchmonoedges;
         CHAbenchpolyedges = benchpolyedges;
      }

      else
      {
         int nodesreduction = CHAnodes - nodes;
         int edgesreduction = CHAedges - edges;
         int benchnodesreduction = CHAbenchnodes - benchnodes;
         int benchedgesreduction = CHAbenchedges - benchedges;
         System.out.println();
         System.out.println("NODES REMOVED = "+nodesreduction);
         System.out.println("PERCENTAGE NODES REMOVED = "+((double) (100*nodesreduction/CHAnodes) ) );
         System.out.println();
         System.out.println("EDGES REMOVED = "+edgesreduction);
         System.out.println("PERCENTAGE EDGES REMOVED = "+((double) (100*edgesreduction/CHAedges) ) );
         System.out.println();
         System.out.println("EXTRA BENCH SITES RESOLVED = "+(CHApoly - poly ));
         System.out.println("PERCENTAGE SITES RESOLVED = "+((double) (100*(CHApoly - poly)/CHApoly) ) );
         System.out.println();
         System.out.println("BENCH NODES REMOVED = "+benchnodesreduction);
         System.out.println("PERCENTAGE BENCH NODES REMOVED = "+((double) (100*benchnodesreduction/CHAbenchnodes) ) );
         System.out.println();
         System.out.println("BENCH EDGES REMOVED = "+benchedgesreduction);
         System.out.println("PERCENTAGE BENCH EDGES REMOVED = "+((double) (100*benchedgesreduction/CHAbenchedges) ) );
         System.out.println();
         System.out.println("EXTRA BENCH SITES RESOLVED = "+(CHAbenchpoly - benchpoly ));
         System.out.println("PERCENTAGE BENCH SITES RESOLVED = "+((double) (100*(CHAbenchpoly - benchpoly)/CHAbenchpoly) ) );
      }

   }


   static boolean stopiterating() {
      boolean stopiterating = true;
      List actuallyunprocessedmethods = new ArrayList();
      Map seenmethodsHT = new HashMap();
      Iterator unprocessedmethodsit = unprocessedmethods.iterator();
      while ( unprocessedmethodsit.hasNext() )
      {
         SootMethod unprocessedmethod = (SootMethod) unprocessedmethodsit.next();
         if ( processedmethodsHT.get(unprocessedmethod) == null )
         {
            if ( seenmethodsHT.get(unprocessedmethod) == null )
            {
               actuallyunprocessedmethods.add(unprocessedmethod);
               seenmethodsHT.put(unprocessedmethod, unprocessedmethod);
            }

            stopiterating = false;
         }

      }

      unprocessedmethods = actuallyunprocessedmethods;
      return stopiterating;
   }


   static void examinePendingLists(List newclasses) {
      Iterator newclassesit = newclasses.iterator();
      while (newclassesit.hasNext())
      {
         SootClass declaringclass = (SootClass) newclassesit.next();
         if ( ! ( pendinglistsHT.get(declaringclass.getName()) == null ) )
         {
            List pendinginvokes = (List) pendinglistsHT.get(declaringclass.getName());
            List pendingtargets = (List) pendingtargetsHT.get(declaringclass.getName());
            Iterator pendinginvokesit = pendinginvokes.iterator();
            Iterator pendingtargetsit = pendingtargets.iterator();
            while (pendinginvokesit.hasNext())
            {
               InvokeExpr ie = (InvokeExpr) pendinginvokesit.next();
               SootMethod target = (SootMethod) pendingtargetsit.next();
               rtacallgraph.addMethod(target);
               rtacallgraph.addTarget(ie, target);
               newmethodsiniteration.add(target);
            }

            pendinglistsHT.remove(declaringclass.getName());
            pendingtargetsHT.remove(declaringclass.getName());
         }

      }

   }


   static List getTargetsAllowedByRTA(InvokeExpr ie, List allowedbyCHA) {
      List targetsAllowedByRTA = new ArrayList();
      Map declaringclassesHT = new HashMap();
      Iterator chatargetsit = allowedbyCHA.iterator();
      while (chatargetsit.hasNext())
      {
         SootMethod targetmethod = (SootMethod) chatargetsit.next();
         declaringclassesHT.put(targetmethod.getDeclaringClass().getName(), targetmethod.getDeclaringClass());
      }

      chatargetsit = allowedbyCHA.iterator();
      while (chatargetsit.hasNext())
      {
         SootMethod targetmethod = (SootMethod) chatargetsit.next();
         if ( allowedbyRTA(targetmethod, declaringclassesHT) )
         targetsAllowedByRTA.add(targetmethod);
         else
         {
            SootClass declaringclass = targetmethod.getDeclaringClass();
            addToPendingLists(declaringclass, ie, targetmethod);
            /* List */ Set subclasses = classgraphbuilder.getAllSubClassesOf(classgraphbuilder.getNode(declaringclass.getName()));
            Iterator subclassesit = subclasses.iterator();
            while ( subclassesit.hasNext() )
            {
               SootClass subclass = ((ClassNode) subclassesit.next()).getSootClass();
               if ( declaringclassesHT.get(subclass.getName()) == null )
               {
                  if ( performLookup(subclass, targetmethod).getName().equals(declaringclass.getName()) )
                  addToPendingLists(subclass, ie, targetmethod);
               }

            }

         }

      }

      return targetsAllowedByRTA;
   }


   static SootClass performLookup ( SootClass subclass, SootMethod targetmethod )
   {
      SootClass correctsuperclass = subclass;
      while ( correctsuperclass.hasSuperClass() )
      {
         correctsuperclass = correctsuperclass.getSuperClass();
         if ( correctsuperclass.declaresMethod(targetmethod.getName(), targetmethod.getParameterTypes(), targetmethod.getReturnType() ) )
         {
            return correctsuperclass;
         }

      }

      return correctsuperclass;
   }


   static void addToPendingLists(SootClass declaringclass, InvokeExpr ie, SootMethod targetmethod )
   {
      if ( pendinglistsHT.get(declaringclass.getName()) == null )
      {
         pendinglistsHT.put(declaringclass.getName(), new ArrayList());
         pendingtargetsHT.put(declaringclass.getName(), new ArrayList());
      }

      ((List) pendinglistsHT.get(declaringclass.getName())).add(ie);
      ((List) pendingtargetsHT.get(declaringclass.getName())).add(targetmethod);
   }


   static boolean allowedbyRTA(SootMethod m, Map declaringclassesHT) {
      SootClass sc = m.getDeclaringClass();
      if ( ! (instancetypesHT.get(sc.getName()) == null) )
      return true;
      if ( ( sc.getName().equals("java.lang.Object") ) && arraycreated )
      return true;
      /* List */ Set subclasses = classgraphbuilder.getAllSubClassesOf(classgraphbuilder.getNode(sc.getName()));
      Iterator subclassesit = subclasses.iterator();
      while ( subclassesit.hasNext() )
      {
         SootClass subclass = ((ClassNode) subclassesit.next()).getSootClass();
         if ( ( ! (instancetypesHT.get(subclass.getName()) == null) ) && ( (declaringclassesHT.get(subclass.getName()) == null) ) )
         {
            if ( performLookup(subclass, m).getName().equals(sc.getName()) )
            return true;
         }

      }

      return false;
   }


   /*

    private void adjustForNativeMethods () {

     Iterator iter = rtacallgraph.getReachableMethods().iterator();

     clgb = classgraphbuilder; 
      
     while ( iter.hasNext() )
     {
       
       try {

         // MethodNode tempMN = (MethodNode) iter.next();
       
        currmethod = (SootMethod) iter.next();
      
        if ( currmethod.getSignature().equals ( "<'java.awt.Toolkit':'getDefaultToolkit':():java.awt.Toolkit>") )
        {

         if ( clgb.getNode ( "java.awt.Toolkit" ) != null )
         {
       
           
          SootClass newclass = manager.getClass("java.awt.Toolkit");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.awt.Toolkit", newclass );

          adjustSubClasses ( "java.awt.Toolkit" );

         }

        }
        else if ( currmethod.getSignature().equals ( "<'java.beans.Beans':'instantiate':(java.lang.ClassLoader,java.lang.String):java.lang.Object>" ) )
        {

         if ( clgb.getNode ( "java.applet.Applet" ) != null )
         {

          SootClass newclass = manager.getClass("java.applet.Applet");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.applet.Applet", newclass );

          adjustSubClasses ( "java.applet.Applet" );
       
         }

        }
        else if ( currmethod.getSignature().equals ( "<'java.beans.Introspector':'findInformant':(java.lang.Class):java.beans.BeanInfo>" ) )
        {

         if ( clgb.getNode ( "java.beans.BeanInfo" ) != null )
         {

          SootClass newclass = manager.getClass("java.beans.BeanInfo");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.beans.BeanInfo", newclass );

          adjustSubClasses ( "java.beans.BeanInfo" );

         }

        }
        else if ( currmethod.getSignature().equals ( "<'java.beans.PropertyEditorManager':'findEditor':(java.lang.Class):java.beans.PropertyEditor>" ) )
        {

         if ( clgb.getNode ( "java.beans.PropertyEditor" ) != null )
         {

          SootClass newclass = manager.getClass("java.beans.PropertyEditor");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.beans.PropertyEditor", newclass );

          adjustSubClasses ( "java.beans.PropertyEditor" ); 

         }

        }
        else if ( currmethod.getSignature().equals ( "<'java.beans.PropertyEditorManager':'instantiate':(java.lang.Class,java.lang.String):java.beans.PropertyEditor>" ) )
        {

         if ( clgb.getNode ( "java.beans.PropertyEditor" ) != null )
         {


          SootClass newclass = manager.getClass("java.beans.PropertyEditor");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.beans.PropertyEditor", newclass );

          adjustSubClasses ( "java.beans.PropertyEditor" ); 

         }

        }
        else if ( currmethod.getSignature().equals ( "<'java.net.DatagramSocket':'create':(int,java.net.InetAddress):void>" ) )
        {

         if ( clgb.getNode ( "java.net.DatagramSocketImpl" ) != null )
         {

          SootClass newclass = manager.getClass("java.net.DatagramSocketImpl");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.net.DatagramSocketImpl", newclass );

          adjustSubClasses ( "java.net.DatagramSocketImpl"); 

         }

        }
        else if ( currmethod.getSignature().equals ( "<'java.net.InetAddress':'<clinit>':():void>" ) )
        {

         if ( clgb.getNode ( "java.net.InetAddressImpl" ) != null )
         {

          SootClass newclass = manager.getClass("java.net.InetAddressImpl");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.net.InetAddressImpl", newclass );

          adjustSubClasses ( "java.net.InetAddressImpl"); 

         }

        }
        else if ( currmethod.getSignature().equals ( "<'java.net.MulticastSocket':'create':(int,java.net.InetAddress):void>" ) )
        {

         if ( clgb.getNode ( "java.net.DatagramSocketImpl" ) != null )
         {

          SootClass newclass = manager.getClass("java.net.DatagramSocketImpl");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.net.DatagramSocketImpl", newclass );

          adjustSubClasses ( "java.net.DatagramSocketImpl"); 

        }

        }
        else if ( currmethod.getSignature().equals ( "<'java.net.URL':'getURLStreamHandler':(java.lang.String):java.net.URLStreamHandler>" ) )
        {

         if ( clgb.getNode ( "java.net.URLStreamHandler" ) != null )
         {

          SootClass newclass = manager.getClass("java.net.URLStreamHandler");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.net.URLStreamHandler", newclass );

          adjustSubClasses ( "java.net.URLStreamHandler"); 

         }

        }
        else if ( currmethod.getSignature().equals ( "<'java.net.URLConnection':'lookupContentHandlerClassFor':(java.lang.String):java.net.ContentHandler>" ) )
        {

         if ( clgb.getNode ( "java.net.ContentHandler" ) != null )
         {

          SootClass newclass = manager.getClass("java.net.ContentHandler");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.net.ContentHandler", newclass );

          adjustSubClasses ( "java.net.ContentHandler"); 

         }

        }
        else if ( currmethod.getSignature().equals ( "<'java.security.Provider':'loadProvider':(java.lang.String):java.security.Provider>" ) )
        {

         if ( clgb.getNode ( "java.security.Provider" ) != null )
         {

          SootClass newclass = manager.getClass("java.security.Provider");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.security.Provider", newclass );

          adjustSubClasses ("java.security.Provider"); 

         }

        }
        else if ( currmethod.getSignature().equals ( "<'java.security.Security':'getImpl':(java.lang.String,java.lang.String,java.lang.String):java.lang.Object>" ) )
        {

         if ( clgb.getNode ( "java.security.KeyPairGenerator" ) != null )
         {

          SootClass newclass = manager.getClass("java.security.KeyPairGenerator");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.security.KeyPairGenerator", newclass );

          adjustSubClasses ("java.security.KeyPairGenerator"); 

         }

         if ( clgb.getNode ( "java.security.MessageDigest" ) != null )
         {

          SootClass newclass = manager.getClass("java.security.MessageDigest");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.security.MessageDigest", newclass );

          adjustSubClasses ("java.security.MessageDigest"); 

         }

         if ( clgb.getNode ( "java.security.Signature" ) != null )
         {

          SootClass newclass = manager.getClass("java.security.Signature");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.security.Signature", newclass );

          adjustSubClasses ("java.security.Signature"); 

         }

        }
        else if ( currmethod.getSignature().equals ( "<'java.util.ResourceBundle':'findBundle':(java.lang.String,java.lang.StringBuffer,java.lang.ClassLoader,boolean):java.util.ResourceBundle>" ) )
        {

         if ( clgb.getNode ( "java.util.ResourceBundle" ) != null )
         {

          SootClass newclass = manager.getClass("java.util.ResourceBundle");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.util.ResourceBundle", newclass );

          adjustSubClasses ("java.util.ResourceBundle"); 

         }

        }
        else if ( currmethod.getSignature().equals ( "<'java.rmi.registry.LocateRegistry':'createRegistry':(int):java.rmi.registry.Registry>" ) )
        {

         if ( clgb.getNode ( "java.rmi.registry.RegistryHandler" ) != null )
         {

          SootClass newclass = manager.getClass("java.rmi.registry.RegistryHandler");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.rmi.registry.RegistryHandler", newclass );

          adjustSubClasses ("java.rmi.registry.RegistryHandler"); 

         }

        }
        else if ( currmethod.getSignature().equals ( "<'java.rmi.server.RMIClassLoader':'getHandler':():java.rmi.server.LoaderHandler>" ) )
        {

         if ( clgb.getNode ( "java.rmi.server.LoaderHandler" ) != null )
         {


          SootClass newclass = manager.getClass("java.rmi.server.LoaderHandler");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.rmi.server.LoaderHandler", newclass );

          adjustSubClasses ("java.rmi.server.LoaderHandler"); 

         }

        }
        else if ( currmethod.getSignature().equals ("<'java.rmi.server.RemoteObject':'readObject':(java.io.ObjectInputStream):void>" ) )
        {

         if ( clgb.getNode ( "java.rmi.server.RemoteRef" ) != null )
         {

          SootClass newclass = manager.getClass("java.rmi.server.RemoteRef");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.rmi.server.RemoteRef", newclass );

          adjustSubClasses ("java.rmi.server.RemoteRef"); 

         }

        }
        else if ( currmethod.getSignature().equals ( "<'java.rmi.server.RemoteServer':'getClientHost':():java.lang.String>" ) )
        {

         if ( clgb.getNode ( "java.rmi.server.ServerRef" ) != null )
         {

          SootClass newclass = manager.getClass("java.rmi.server.ServerRef");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.rmi.server.ServerRef", newclass );

          adjustSubClasses ("java.rmi.server.ServerRef"); 

         }
         
        }
        else if ( currmethod.getSignature().equals ( "<'java.rmi.server.UnicastRemoteObject':'exportObject':(java.rmi.Remote):java.rmi.server.RemoteStub>" ) )
        {

         if ( clgb.getNode ( "java.rmi.server.ServerRef" ) != null )
         {

          SootClass newclass = manager.getClass("java.rmi.server.ServerRef");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ("java.rmi.server.ServerRef", newclass );

          adjustSubClasses ("java.rmi.server.ServerRef"); 

         }

        }
        
   // NEXT CATEGORY 

        else if ( currmethod.getSignature().equals ( "<'java.lang.Object':'getClass':():java.lang.Class>" ) )
        {

         if ( clgb.getNode ( "java.lang.Class" ) != null )
         {

          SootClass newclass = manager.getClass("java.lang.Class");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ( "java.lang.Class", newclass );

          adjustSubClasses ( "java.lang.Class"); 
          
         }
         
        }

       else if ( currmethod.getSignature().equals ( "<'java.lang.Class':'forName':(java.lang.String):java.lang.Class>" ) )
       {
    
         if ( clgb.getNode ( "java.lang.Class" ) != null )
         {

          SootClass newclass = manager.getClass("java.lang.Class");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ( "java.lang.Class", newclass );

          adjustSubClasses ( "java.lang.Class"); 
          
         }
          
        }

       else if ( currmethod.getSignature().equals ( "<'java.lang.Class':'getClassLoader':():java.lang.ClassLoader>" ) )   
       {

         if ( clgb.getNode ( "java.lang.ClassLoader" ) != null )
         {

          SootClass newclass = manager.getClass("java.lang.ClassLoader");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ( "java.lang.ClassLoader", newclass );

          adjustSubClasses ( "java.lang.ClassLoader"); 
          
         }

        }
        else if ( currmethod.getSignature().equals ( "<'java.lang.Class':'getSuperclass':():java.lang.Class>" ) )
        {


         if ( clgb.getNode ( "java.lang.Class" ) != null )
         {

          SootClass newclass = manager.getClass("java.lang.Class");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ( "java.lang.Class", newclass );

          adjustSubClasses ( "java.lang.Class"); 
          
         }

        }
        else if ( currmethod.getSignature().equals ( "<'java.lang.Class':'getInterfaces':():java.lang.Class[]>" ) )
        {
        
         if ( clgb.getNode ( "java.lang.Class" ) != null )
         {

          SootClass newclass = manager.getClass("java.lang.Class");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ( "java.lang.Class", newclass );

          adjustSubClasses ( "java.lang.Class"); 
          
         }
           
        }
        else if ( currmethod.getSignature().equals ( "<'java.lang.Class':'getComponentType':():java.lang.Class>" ) )
        {

         if ( clgb.getNode ( "java.lang.Class" ) != null )
         {

          SootClass newclass = manager.getClass("java.lang.Class");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ( "java.lang.Class", newclass );

          adjustSubClasses ( "java.lang.Class"); 
          
         }

        }
        else if ( currmethod.getSignature().equals ( "<'java.lang.Class':'getPrimitiveClass':(java.lang.String):java.lang.Class>" ) )
        {

         if ( clgb.getNode ( "java.lang.Class" ) != null )
         {

          SootClass newclass = manager.getClass("java.lang.Class");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ( "java.lang.Class", newclass );

          adjustSubClasses ( "java.lang.Class"); 
          
         }
       
        }
        else if ( currmethod.getSignature().equals ( "<'java.lang.Class':'getFields0':(int):java.lang.reflect.Field[]>" ) )
        {

         if ( clgb.getNode ( "java.lang.reflect.Field" ) != null )
         {
          
          SootClass newclass = manager.getClass("java.lang.reflect.Field");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ( "java.lang.reflect.Field", newclass );

          adjustSubClasses ( "java.lang.reflect.Field"); 
          
         }
         
        }
        else if ( currmethod.getSignature().equals ( "<'java.lang.Class':'getMethod0':(java.lang.String,java.lang.Class[],int):java.lang.reflect.Method>" ) )
        {

         if ( clgb.getNode ( "java.lang.reflect.Method" ) != null )
         {

          SootClass newclass = manager.getClass("java.lang.reflect.Method");

          System.err.println(newclass.getName());

          newclassesiniteration.add(newclass);

          instancetypesHT.put ( "java.lang.reflect.Method", newclass );

          adjustSubClasses ( "java.lang.reflect.Method"); 

         }
          
        }
        else if ( currmethod.getSignature().equals ( "<'java.lang.Class':'getConstructor0':(java.lang.Class[],int):java.lang.reflect.Constructor>" ) )
        {

         if ( clgb.getNode ( "java.lang.reflect.Constructor" ) != null )
         {

           SootClass newclass = manager.getClass("java.lang.reflect.Constructor");

           System.err.println(newclass.getName());

           newclassesiniteration.add(newclass);

           instancetypesHT.put ( "java.lang.reflect.Constructor", newclass );

           adjustSubClasses ( "java.lang.reflect.Constructor");

         }
          
        }
        else if ( currmethod.getSignature().equals ( "<'java.lang.System':'initProperties':(java.util.Properties):java.util.Properties>" ) )
        {

         if ( clgb.getNode ( "java.util.Properties" ) != null )
         {

           SootClass newclass = manager.getClass("java.util.Properties");

           System.err.println(newclass.getName());

           newclassesiniteration.add(newclass);

           instancetypesHT.put ( "java.util.Properties", newclass );

           adjustSubClasses ( "java.util.Properties");

         }

        }
        else if ( currmethod.getSignature().equals ( "<'java.lang.Thread':'currentThread':():java.lang.Thread>" ) )
        {

         if ( clgb.getNode ( "java.lang.Thread" ) != null )
         {
 
           SootClass newclass = manager.getClass("java.lang.Thread");

           System.err.println(newclass.getName());

           newclassesiniteration.add(newclass);

           instancetypesHT.put ( "java.lang.Thread", newclass );

           adjustSubClasses ( "java.lang.Thread");
       
         }

        }
        else if ( currmethod.getSignature().equals ( "<'java.lang.ClassLoader':'defineClass0':(java.lang.String,byte[],int,int):java.lang.Class>" ) )
        {

         if ( clgb.getNode ( "java.lang.Class" ) != null )
         {

           SootClass newclass = manager.getClass("java.lang.Class");

           System.err.println(newclass.getName());

           newclassesiniteration.add(newclass);

           instancetypesHT.put ( "java.lang.Class", newclass );

           adjustSubClasses ( "java.lang.Class");
       
         }
          
        } 
        else if ( currmethod.getSignature().equals ( "<'java.lang.ClassLoader':'findSystemClass0':(java.lang.String):java.lang.Class>" ) )
        {

         if ( clgb.getNode ( "java.lang.Class" ) != null )
         {
       
           SootClass newclass = manager.getClass("java.lang.Class");

           System.err.println(newclass.getName());

           newclassesiniteration.add(newclass);

           instancetypesHT.put ( "java.lang.Class", newclass );

           adjustSubClasses ( "java.lang.Class");

         }
         
        }
        else if ( currmethod.getSignature().equals ( "<'java.lang.ClassLoader':'getSystemResourceAsStream0':(java.lang.String):java.io.InputStream>" ) )
        {

         if ( clgb.getNode ( "java.io.InputStream" ) != null )
         {

           SootClass newclass = manager.getClass("java.io.InputStream");

           System.err.println(newclass.getName());

           newclassesiniteration.add(newclass);

           instancetypesHT.put ( "java.io.InputStream", newclass );

           adjustSubClasses ( "java.io.InputStream");
       
         }
       
        }
        else if ( currmethod.getSignature().equals ( "<'java.lang.SecurityManager':'getClassContext':():java.lang.Class[]>" ) )
        {

         if ( clgb.getNode ( "java.lang.Class" ) != null )
         {
          
           SootClass newclass = manager.getClass("java.lang.Class");

           System.err.println(newclass.getName());

           newclassesiniteration.add(newclass);

           instancetypesHT.put ( "java.lang.Class", newclass );

           adjustSubClasses ( "java.lang.Class");
          
         }
         
        }

        else if ( currmethod.getSignature().equals ( "<'java.lang.SecurityManager':'currentClassLoader':():java.lang.ClassLoader>" ) )
        {

         if ( clgb.getNode ( "java.lang.ClassLoader" ) != null )
         {

           SootClass newclass = manager.getClass("java.lang.ClassLoader");

           System.err.println(newclass.getName());

           newclassesiniteration.add(newclass);

           instancetypesHT.put ( "java.lang.ClassLoader", newclass );

           adjustSubClasses ( "java.lang.ClassLoader");

         }
       
        }
        else if ( currmethod.getSignature().equals ( "<'java.lang.SecurityManager':'currentLoadedClass0':():java.lang.Class>" ) )
        {

         if ( clgb.getNode ( "java.lang.Class" ) != null )
         {

           SootClass newclass = manager.getClass("java.lang.Class");

           System.err.println(newclass.getName());

           newclassesiniteration.add(newclass);

           instancetypesHT.put ( "java.lang.Class", newclass );

           adjustSubClasses ( "java.lang.Class");

         }
         
        }
        else if ( currmethod.getSignature().equals ( "<'java.io.ObjectInputStream':'loadClass0':(java.lang.Class,java.lang.String):java.lang.Class>" ) )
        {

         if ( clgb.getNode ( "java.lang.Class" ) != null )
         {
 
           SootClass newclass = manager.getClass("java.lang.Class");

           System.err.println(newclass.getName());

           newclassesiniteration.add(newclass);

           instancetypesHT.put ( "java.lang.Class", newclass );

           adjustSubClasses ( "java.lang.Class");
       
         }
       
        }
        else if ( currmethod.getSignature().equals ( "<'java.lang.Runtime':'execInternal':(java.lang.String[],java.lang.String[]):java.lang.Processjava.lang.Runtime.execInternal(java.lang.String[],java.lang.String[]):java.lang.Process>" ) )
        {

         if ( clgb.getNode ( "java.lang.Process" ) != null )
         {
          
           SootClass newclass = manager.getClass("java.lang.Process");

           System.err.println(newclass.getName());

           newclassesiniteration.add(newclass);

           instancetypesHT.put ( "java.lang.Process", newclass );

           adjustSubClasses ( "java.lang.Process");
          
         }
         
        }
        else if ( currmethod.getSignature().equals ( "<'java.io.FileDescriptor':'initSystemFD':(java.io.FileDescriptor,int):java.io.FileDescriptor>" ) )
        {

         if ( clgb.getNode ( "java.io.FileDescriptor" ) != null )
         {
          
           SootClass newclass = manager.getClass("java.io.FileDescriptor");

           System.err.println(newclass.getName());

           newclassesiniteration.add(newclass);

           instancetypesHT.put ( "java.io.FileDescriptor", newclass );

           adjustSubClasses ( "java.io.FileDescriptor");
          
         }
         
        }
        else if ( currmethod.getSignature().equals ( "<'java.util.ResourceBundle':'getClassContext':():java.lang.Class[]>" ) )
        {

         if ( clgb.getNode ( "java.lang.Class" ) != null )
         {

           SootClass newclass = manager.getClass("java.lang.Class");

           System.err.println(newclass.getName());

           newclassesiniteration.add(newclass);

           instancetypesHT.put ( "java.lang.Class", newclass );

           adjustSubClasses ( "java.lang.Class");
       
         }
          
        } 
        else if ( currmethod.getSignature().equals ( "<'java.io.ObjectStreamClass':'getFields0':(java.lang.Class):java.io.ObjectStreamField[]>" ) )
        {

         if ( clgb.getNode ( "java.io.ObjectStreamField" ) != null )
         {

           SootClass newclass = manager.getClass("java.io.ObjectStreamField");

           System.err.println(newclass.getName());

           newclassesiniteration.add(newclass);

           instancetypesHT.put ( "java.io.ObjectStreamField", newclass );

           adjustSubClasses ( "java.io.ObjectStreamField");
          
         }
         
        }

       } catch ( java.lang.RuntimeException e ) {}

      }

    }











    private void adjustSubClasses ( String s ) { 

      ClassNode cn = clgb.getNode ( s );

      Set subclassnodes = clgb.getAllSubClassesOf ( cn );

      Iterator subclassnodesit = subclassnodes.iterator();
        
      while ( subclassnodesit.hasNext() )
      {
    
       try {
     
       ClassNode subcn = ( ClassNode ) subclassnodesit.next();

       String name = subcn.getSootClass().getName();

       instancetypesHT.put ( name , subcn.getSootClass() );

       newclassesiniteration.add(subcn.getSootClass());

       } catch ( java.lang.RuntimeException e ) {}

      }

    }

   */
}




