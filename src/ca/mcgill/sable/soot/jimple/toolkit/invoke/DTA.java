// package ca.mcgill.sable.soot.virtualCalls;

package ca.mcgill.sable.soot.jimple.toolkit.invoke;
//import java.util.*;
import java.io.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.soot.*;
// import ca.mcgill.sable.soot.sideEffect.*;
import java.util.BitSet;
class DTA {
   Scene cm;
   private ArrayList bclasses;
   List SCCs;
   private Map declaredtypesHT = new HashMap();
   private Map instancetypesHT = new HashMap();
   private Map thisHT = new HashMap();
   private SootClass currclass;
   private SootMethod currmethod;
   private RefType currrtype;
   private RefType currRrtype;
   private RefType currLrtype;
   private ClassGraphBuilder clgb;
   private CallGraphBuilder cagb;
   private String reachedclass;
   private String reachedmethod;
   private String invokedclass;
   private boolean thisInvoked;
   private boolean thisParam;
   private boolean ref,refParam;
   private boolean nullflag;
   private String currlabel;
   private String currLlabel;
   private String currRlabel;
   private String curractuallabel;
   private String currformallabel;
   private boolean isStatic;
   private boolean isSpecial;
   private Map parameterHT = new HashMap();
   private boolean arraybothsides;
   private boolean staticfield;
   private boolean virflag;
   private boolean intflag;
   private int virnum = 0,intnum = 0,spenum = 0,stanum = 0,virmono = 0,intmono = 0;
   static Timer DTATimer = new Timer();
   static long DTAMem;
   static Timer DTAedgeRemovedTimer = new Timer();
   static long DTAedgeRemovedMem;
   private int totedges = 0;
   private int totadd = 0;
   private int totactadd = 0;
   private int intactedges = 0;
   private int tempnumincsHT = 0;
   private int tempintactedges = 0;
   private HashMap bitMap = new HashMap();
   private HashMap inversebitMap = new HashMap();
   void constructBitMap( Map allclassHT ) {
      Iterator classesIt = allclassHT.values().iterator();
      int i = 0;
      while ( classesIt.hasNext() )
      {
         String classname = ( ( ClassNode ) classesIt.next() ).getName();
         Integer ii = new Integer( i );
         bitMap.put ( classname, ii );
         inversebitMap.put ( ii, classname );
         i++;
      }

   }


   void initializeConstraintGraph(RTA rta) {
      /* THIS METHOD INITIALIZES THE NODES IN THE CONSTRAINT GRAPH, EVERY
           NODE THAT COULD BE REQUIRED WHEN THE STATEMENTS IN A METHOD ARE 
           ANALYSED IS CREATED AT THIS STAGE ITSELF */
      this.rta = rta;
      DTATimer.start();
      System.out.println("");
      System.out.println("Performing Declared Type Analysis.....");
      System.out.println("");
      System.out.print("Initializing constraint graph.....");
      clgb = rta.getClassGraphBuilder();
      cagb = rta.getCallGraphBuilder();
      Map classgraph = clgb.getClassGraph();
      Iterator iter = classgraph.values().iterator();
      while ( iter.hasNext() )
      {
         SootClass bclass = (SootClass) ( ( ClassNode ) iter.next() ).getSootClass();
         currclass = bclass;
         declaredtypesHT.put(bclass.getName(),new TypeNode(bclass.getName(), bitMap, inversebitMap ));
         List fields = currclass.getFields();
         Iterator fieldIt = fields.iterator();
         // ADD DECLARED TYPES OF EACH REFTYPE FIELD IN EACH CLASS

         while(fieldIt.hasNext())
         {
            try {
               SootField currfield = ( SootField ) fieldIt.next();
               Type currtype = currfield.getType();
               currtype.apply( new TypeSwitch() {
                  public void caseRefType( RefType r ) {
                     if ( ((TypeNode) declaredtypesHT.get(r.className)) == null )
                     declaredtypesHT.put( r.className , new TypeNode(r.className, bitMap, inversebitMap ) );
                  }

                  public void caseArrayType( ArrayType r ) {
                     BaseType currbtype = r.baseType;
                     currbtype.apply( new TypeSwitch() {
                        public void caseRefType( RefType r1 ) {
                           if ( ((TypeNode) declaredtypesHT.get(r1.className)) == null )
                           declaredtypesHT.put(r1.className, new TypeNode(r1.className, bitMap, inversebitMap ) );
                        }

                     });

                  }

               });

            }
            catch ( java.lang.RuntimeException e ){}

         }
         // WHILE FIELDITER                                                              

      }
      // WHILE IT

      // Iterator methodIt = bclass.getMethods().iterator();

      Collection callGraph = cagb.getCallGraph();
      Iterator methodIt = callGraph.iterator();
      while(methodIt.hasNext())
      {
         try {
            currmethod = ( (MethodNode) methodIt.next()).getMethod();
            List parametertypes = currmethod.getParameterTypes();
            Iterator parameterIt = parametertypes.iterator();
            // ADD DECLARED TYPES OF EACH REFTYPE PARAMETER IN EACH CLASS

            while(parameterIt.hasNext())
            {
               try {
                  Type currparamtype = ( Type ) parameterIt.next();
                  currparamtype.apply( new TypeSwitch() {
                     public void caseRefType( RefType r ) {
                        if ( ((TypeNode) declaredtypesHT.get(r.className)) == null )
                        declaredtypesHT.put(r.className, new TypeNode(r.className, bitMap, inversebitMap ) );
                     }

                     public void caseArrayType( ArrayType r ) {
                        BaseType currbtype = r.baseType;
                        currbtype.apply( new TypeSwitch(){
                           public void caseRefType( RefType r1 ){
                              if ( ((TypeNode) declaredtypesHT.get(r1.className)) == null )
                              declaredtypesHT.put(r1.className, new TypeNode(r1.className, bitMap, inversebitMap ) );
                           }

                        });

                     }

                  });

               }
               catch (java.lang.RuntimeException e ){}

            }
            // WHILE PARAMETERIT

            // ADD THE DECLARED RETURN TYPE OF THE CURRMETHOD IF ITS A REFTYPE

            // ALSO ADD NODE FOR RETURN_METHODSIGNATURE IF THE RETURN IS A REFTYPE 

            Type currreturntype = (Type) currmethod.getReturnType();
            currreturntype.apply( new TypeSwitch(){
               public void caseRefType( RefType r ) {
                  if ( ((TypeNode) declaredtypesHT.get(r.className)) == null )
                  declaredtypesHT.put(r.className, new TypeNode(r.className, bitMap, inversebitMap ) );
                  // MEMORY

                  //  declaredtypesHT.put("return_"+currmethod.getSignature(), new TypeNode("return_"+currmethod.getSignature(), bitMap, inversebitMap ));

               }

               public void caseArrayType( ArrayType r ) {
                  BaseType currbtype = r.baseType;
                  currbtype.apply( new TypeSwitch(){
                     public void caseRefType( RefType r1 ){
                        if ( ((TypeNode) declaredtypesHT.get(r1.className)) == null )
                        declaredtypesHT.put(r1.className, new TypeNode(r1.className, bitMap, inversebitMap ) );
                        // MEMORY

                        // declaredtypesHT.put("return_"+currmethod.getSignature(), new TypeNode("return_"+currmethod.getSignature(), bitMap, inversebitMap ));

                     }

                  });

               }

            });

            // MEMORY

            declaredtypesHT.put("return_"+currmethod.getSignature(), new TypeNode("return_"+currmethod.getSignature(), bitMap, inversebitMap ));
            //    System.out.println ("ADDED RETURN "+"return_"+currmethod.getSignature() );

            // ADD NODE FOR THIS_METHODSIGNATURE

            declaredtypesHT.put(new String("this_"+currmethod.getSignature()), new TypeNode(new String("this_"+currmethod.getSignature()), bitMap, inversebitMap ) );
            JimpleBody listBody = Jimplifier.getJimpleBody( currmethod );
            List locals = listBody.getLocals();
            Iterator localIt = locals.iterator();
            // ADD DECLARED TYPE OF EACH REFTYPE LOCAL IN EACH METHOD

            while(localIt.hasNext())
            {
               try {
                  Local currlocal = ( Local ) localIt.next();
                  Type currtype = currlocal.getType();
                  currtype.apply( new TypeSwitch() {
                     public void caseRefType( RefType r ){
                        if ( ((TypeNode) declaredtypesHT.get(r.className)) == null )
                        declaredtypesHT.put(r.className, new TypeNode(r.className, bitMap, inversebitMap ) );
                     }

                     public void caseArrayType( ArrayType r ) {
                        BaseType currbtype = r.baseType;
                        currbtype.apply( new TypeSwitch(){
                           public void caseRefType( RefType r1 ){
                              if ( ((TypeNode) declaredtypesHT.get(r1.className)) == null )
                              declaredtypesHT.put(r1.className, new TypeNode(r1.className, bitMap, inversebitMap ) );
                           }

                        });

                     }

                  });

               }
               catch ( java.lang.RuntimeException e ){}

            }
            // WHILE LOCALIT

         }
         catch (java.lang.RuntimeException e ){}

      }
      // WHILE METHODIT

      // } // WHILE ITER

      // KEEP TRACK OF LOCALS THAT ARE ACTUALLY A REFERENCE TO "THIS"

      System.out.println("Done");
      collectThisVars();
   }


   int iteration = 0;
   Collection getFinalCallGraph() {
      /* THIS METHOD ACTUALLY REMOVES THE CALL GRAPH EDGES THAT CAN BE REMOVED 
           ACCORDING TO DECLARED TYPE ANALYSIS */
      iteration++;
      String reachedclass = new String();
      Collection callGraph = null;
      /*
        System.out.println("BEFORE PRUNING THE CALL GRAPH HAS : "+totedges+" EDGES ");

        System.out.println("BEFORE PRUNING THE CALL GRAPH HAS : "+intactedges+" INTACT EDGES ");

        
        System.out.println("BEFORE PRUNING THE CALL GRAPH HAS : "+tempintactedges+" INTACT EDGES ");

        System.out.println ("NUMBER OF POLY CALLSITES : "+ tempnumincsHT );

        */
      int totunchanged = 0, methodcnt = 0, cscnt = 0 , changed = 0;
      try {
         callGraph = cagb.getCallGraph();
         Iterator iter = callGraph.iterator();
         while ( iter.hasNext() )
         {
            methodcnt++;
            try {
               MethodNode tempMN = (MethodNode) iter.next();
               currmethod = tempMN.getMethod();
               Iterator CSiter = tempMN.getCallSites().iterator();
               while ( CSiter.hasNext() )
               {
                  cscnt++;
                  try {
                     isStatic = false;
                     isSpecial = false;
                     CallSite cs = (CallSite) CSiter.next();
                     InvokeExpr invokeExpr = cs.getInvokeExpr();
                     invokeExpr.apply( new AbstractJimpleValueSwitch(){
                        public void caseStaticInvokeExpr(StaticInvokeExpr v) {
                           isStatic = true;
                        }

                        public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {
                           isSpecial = true;
                        }

                     });

                     Map actualinstanceHT = new HashMap();
                     HashMap reachedclassHT = new HashMap();
                     Set vs = new HashSet();
                     try {
                        // OBTAIN THE SET OF METHODNODES THAT CAN BE CALLED FROM THIS CALLSITE
                        // AS DETERMINED BY DECLARED TYPE ANALYSIS

                        vs = ( HashSet ) csHT.get( currmethod.getSignature()+invokeExpr.toString() );
                        totadd = totadd + vs.size();
                     }
                     catch ( java.lang.RuntimeException e ) {}

                     if ( ! ( isStatic || isSpecial ) )
                     {
                        // REPLACE THE SET OF METHODNODES THAT CAN BE CALLED FROM THIS 
                        // CALLSITE BY THE NEW SET OBTAINED USING DECLARED TYPE ANALYSIS        

                        cs.setMethods(vs);
                        totactadd = totactadd + vs.size();
                        changed++;
                     }

                     else
                     totunchanged++;
                  }
                  catch ( java.lang.RuntimeException e ) {}

               }
               // WHILE CSITER

            }
            catch ( java.lang.RuntimeException e ) {}

         }
         // WHILE ITER

      }
      catch ( java.lang.RuntimeException e ) {}

      /* CHECK ON NUMBER OF NODES AFTER PRUNING , CAN BE REMOVED */
      try {
         int totalnum = 0;
         Iterator iter = callGraph.iterator();
         PrintWriter pw = null;
         PrintWriter pw1 = null;
         HashSet seenclasses = new HashSet();
         try {
            File tempFile = new File("analysis.DTA"+iteration );
            FileOutputStream streamOut = new FileOutputStream(tempFile);
            pw = new PrintWriter(streamOut);
            tempFile = new File("profiled.DTA"+iteration );
            streamOut = new FileOutputStream(tempFile);
            pw1 = new PrintWriter(streamOut);
         }
         catch ( java.io.IOException e ) {}

         while ( iter.hasNext() )
         {
            try {
               MethodNode tempMN = (MethodNode) iter.next();
               currclass = tempMN.getMethod().getDeclaringClass();
               currmethod = tempMN.getMethod();
               String currname = currmethod.getDeclaringClass().getName();
               boolean isLibNode = ClassGraphBuilder.isLibraryNode("java.", currname) || ClassGraphBuilder.isLibraryNode("sun.", currname) ||
               ClassGraphBuilder.isLibraryNode("sunw.", currname) || ClassGraphBuilder.isLibraryNode("javax.", currname) ||
               ClassGraphBuilder.isLibraryNode("org.", currname) || ClassGraphBuilder.isLibraryNode("com.", currname);
               if ( ! isLibNode )
               {
                  if ( ! seenclasses.contains ( currname ) )
                  {
                     pw1.println ( currname );
                     seenclasses.add ( currname );
                  }

               }

               Iterator CSiter = tempMN.getCallSites().iterator();
               while ( CSiter.hasNext() )
               {
                  try {
                     CallSite cs = (CallSite) CSiter.next();
                     Set possMethodNodes = cs.getMethods();
                     totalnum = totalnum + possMethodNodes.size();
                     InvokeExpr inexpr = cs.getInvokeExpr();
                     //      String currname = currmethod.getDeclaringClass().getName();

                     //      if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currname) || ClassGraphBuilder.isLibraryNode("sun.", currname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currname) ) ) 

                     if ( ! isLibNode )
                     {
                        if ( ! ( ( inexpr instanceof StaticInvokeExpr ) || /* ( inexpr.getMethod().getName().equals ( "<init>" ) ) */ ( inexpr instanceof SpecialInvokeExpr ) ) )
                        {
                           String currentID = cs.getCallerID();
                           //      Set possMethodNodes = cs.getMethods();

                           //      totalnum = totalnum + possMethodNodes.size();

                           Iterator possibleit = possMethodNodes.iterator();
                           while ( possibleit.hasNext() )
                           {
                              SootMethod printmethod = ( ( MethodNode ) possibleit.next() ).getMethod()
                              ;
                              //  try {

                              pw.println ( currentID+" "+printmethod.getSignature() );
                              //  } catch ( java.io.IOException e ) {}

                           }

                        }

                     }

                  }
                  catch ( java.lang.RuntimeException e ) {}

               }

            }
            catch ( java.lang.RuntimeException e ) {}

         }
         // WHILE ITER

         pw.close();
         pw1.close();
         /*
           System.out.println("TOTAL METHODS : "+methodcnt);

           System.out.println("TOTAL CALLSITES : "+cscnt);

           System.out.println("TOTAL UNCHANGED : "+totunchanged+" EDGES ");

           System.out.println("TOTAL CHANGED : "+changed+" EDGES ");

           System.out.println("TOTAL ACTUALLY ADDED : "+totactadd+" EDGES ");
           */
         System.out.println("PRUNED CALL GRAPH HAS : "+totalnum+" EDGES");
      }
      catch ( java.lang.RuntimeException e ) {}

      DTAgc();
      return callGraph;
   }


   void DTAgc() {
      Iterator iterator = declaredtypesHT.values().iterator();
      while ( iterator.hasNext() )
      {
         TypeNode tn = (TypeNode) iterator.next();
         tn.prepareForGC();
      }

   }


   void adjustForFinalize() {
      Collection callGraph = cagb.getCallGraph();
      Iterator iter = callGraph.iterator();
      while ( iter.hasNext() )
      {
         try {
            MethodNode tempMN = (MethodNode) iter.next();
            SootMethod meth = tempMN.getMethod();
            if ( meth.getName().equals ("finalize" ) )
            {
               TypeNode fintn = ( TypeNode ) declaredtypesHT.get ( "this_"+meth.getSignature() );
               SootClass sc = meth.getDeclaringClass();
               Iterator methit = sc.getMethods().iterator();
               while ( methit.hasNext() )
               {
                  SootMethod nextmeth = ( SootMethod ) methit.next();
                  if ( nextmeth.getName().equals("<init>") )
                  {
                     if ( ! ( ( ( TypeNode ) declaredtypesHT.get ( "this_"+nextmeth.getSignature() ) ) == null ) )
                     {
                        TypeNode inittn = ( TypeNode ) declaredtypesHT.get ( "this_"+nextmeth.getSignature() );
                        inittn.addForwardNode ( fintn );
                        fintn.addBackwardNode ( inittn );
                        declaredtypesHT.put(inittn.getTypeName(), inittn );
                        declaredtypesHT.put(fintn.getTypeName(),fintn);
                     }

                  }

               }

            }

         }
         catch ( java.lang.RuntimeException e ) {}

      }

   }


   int numinvokes;
   void analyseStatements() {
      /* THIS METHOD ANALYSES EACH STATEMENT IN EACH METHOD IN THE CALL GRAPH
           AND ADDS EDGES, INSTANCE TYPES TO THE CONSTRAINT GRAPH  

           IT CALLS ANALYSECALLSITES TO ANALYSE CALLSITES 
        */
      adjustForFinalize();
      System.out.println("");
      Collection callGraph = cagb.getCallGraph();
      Iterator iter = callGraph.iterator();
      int mnum = 0;
      System.out.print("Declared Type Analysis collecting constraints for ordinary statements");
      //  System.out.println("DTA ANALYSING STATEMENTS");

      while ( iter.hasNext() )
      {
         try {
            numinvokes = 1;
            // System.out.print("<"+mnum+">");

            mnum++;
            if ( ( mnum % 10 ) == 0 )
            System.out.print(".");
            MethodNode tempMN = (MethodNode) iter.next();
            currmethod = tempMN.getMethod();
            currclass = currmethod.getDeclaringClass();
            try {
               JimpleBody listBody = Jimplifier.getJimpleBody( currmethod );
               Iterator stmtIter = listBody.getStmtList().iterator();
               while ( stmtIter.hasNext() )
               {
                  Stmt stmt = null;
                  try {
                     stmt = (Stmt) stmtIter.next();
                     stmt.apply( new AbstractStmtSwitch(){
                        public void caseInvokeStmt(InvokeStmt s) {
                           numinvokes++;
                        }

                        public void caseReturnStmt(ReturnStmt s) {
                           if ( s.getReturnValue() instanceof Local )
                           {
                              // System.out.println ( "RETURN "+s.getReturnValue() );

                              Type type = s.getReturnValue().getType();
                              // System.out.println ( "RETURN "+type );

                              try {
                                 currlabel = currmethod.getSignature()+( (Local) s.getReturnValue()).getName();
                              }
                              catch ( java.lang.RuntimeException e ) {}

                              type.apply( new TypeSwitch(){
                                 public void caseRefType( RefType r ){
                                    // System.out.println ("RETURN REF "+r.className );

                                    if ( !(((TypeNode) declaredtypesHT.get(r.className)) == null ))
                                    {
                                       // System.out.println ("REACHED HERE 0" );

                                       TypeNode rtn = (TypeNode) declaredtypesHT.get( r.className );
                                       try {
                                          if ( ( (String) thisHT.get( currmethod.getSignature() )).equals(currlabel) )
                                          rtn = (TypeNode) declaredtypesHT.get( new String("this_"+currmethod.getSignature()) );
                                       }
                                       catch ( java.lang.RuntimeException e ) {}

                                       TypeNode ltn = (TypeNode) declaredtypesHT.get(new String("return_"+currmethod.getSignature()));
                                       /*
                                           System.out.println("");
                                           System.out.println("ADDED EDGE BETWEEN "+rtn.getTypeName()+" AND
                                       "+ltn.getTypeName()+" : RETURN STMT");
                                           System.out.println("");
                                       */
                                       //   System.out.println ( "REACHED HERE"+" "+ltn+" "+"return_"+currmethod.getSignature() );

                                       rtn.addForwardNode(ltn);
                                       ltn.addBackwardNode(rtn);
                                       declaredtypesHT.put(ltn.getTypeName(), ltn );
                                       declaredtypesHT.put(rtn.getTypeName(),rtn);
                                       //            System.out.println ( "REACHED HERE 1");

                                    }
                                    // IF 

                                 }

                                 public void caseArrayType( ArrayType r ){
                                    //         System.out.println ( "RETURN ARRAY " );

                                    BaseType currbtype = r.baseType;
                                    currbtype.apply( new TypeSwitch(){
                                       public void caseRefType( RefType r1 ){
                                          //         System.out.println ("RETURN REF "+r1.className );

                                          if ( !(((TypeNode) declaredtypesHT.get(r1.className)) == null ))
                                          {
                                             TypeNode rtn = (TypeNode) declaredtypesHT.get( r1.className );
                                             TypeNode ltn = (TypeNode) declaredtypesHT.get(new String("return_"+currmethod.getSignature()));
                                             /*
                                                 System.out.println("");
                                                 System.out.println("ADDED EDGE BETWEEN "+rtn.getTypeName()+" AND
                                             "+ltn.getTypeName()+" AND VICE VERSA : RETURN STMT (ARRAY) ");
                                                 System.out.println("");
                                                   */
                                             rtn.addForwardNode(ltn);
                                             ltn.addBackwardNode(rtn);
                                             ltn.addForwardNode(rtn);
                                             rtn.addBackwardNode(ltn);
                                             declaredtypesHT.put(ltn.getTypeName(), ltn );
                                             declaredtypesHT.put(rtn.getTypeName(),rtn);
                                          }
                                          // IF

                                       }

                                    });

                                 }

                              });

                           }
                           // IF S.GETRETURNVALUE  

                           else
                           {
                              Type type = s.getReturnValue().getType();
                              type.apply( new TypeSwitch(){
                                 public void caseRefType( RefType r ){
                                    TypeNode tn = (TypeNode) declaredtypesHT.get(new String("return_"+currmethod.getSignature()));
                                    /*
                                        System.out.println("");
                                        System.out.println("ADDED INSTANCE TYPE "+r.className+" TO DECLARED TYPE
                                    "+tn.getTypeName()+" : RETURN STMT (CONSTT) ");
                                        System.out.println("");
                                        */
                                    tn.addInstanceType(r.className);
                                    declaredtypesHT.put(tn.getTypeName(), tn );
                                 }

                              });

                           }
                           // ELSE 

                        }
                        // CASERETURNSTMT  

                        public void caseAssignStmt(AssignStmt s) {
                           if( s.getRightOp() instanceof NewExpr )
                           {
                              currrtype = ((NewExpr)s.getRightOp()).getBaseType();
                              Type type = s.getLeftOp().getType();
                              type.apply( new TypeSwitch(){
                                 public void caseRefType( RefType r ) {
                                    if ( !(((TypeNode) declaredtypesHT.get(r.className)) == null ))
                                    {
                                       /*
                                       	    System.out.println("ADDED INSTANCE TYPE "+currrtype.className+" TO
                                       DECLARED TYPE "+r.className);
                                       */
                                       TypeNode tn = (TypeNode) declaredtypesHT.get(r.className);
                                       tn.addInstanceType(currrtype.className);
                                       declaredtypesHT.put(tn.getTypeName(), tn );
                                    }

                                 }

                                 public void caseArrayType( ArrayType r ) {
                                    BaseType currbtype = r.baseType;
                                    currbtype.apply( new TypeSwitch(){
                                       public void caseRefType( RefType r1 ){
                                          if ( !(((TypeNode) declaredtypesHT.get(r1.className)) == null ))
                                          {
                                             TypeNode tn = (TypeNode) declaredtypesHT.get( r1.className );
                                             tn.addInstanceType(currrtype.className);
                                             declaredtypesHT.put(tn.getTypeName(), tn );
                                          }

                                       }

                                    });

                                 }

                              });

                           }
                           // IF S.GETRIGHTOP() INSTANCEOF NEWEXPR
                           else
                           {
                              if( s.getRightOp() instanceof NewArrayExpr )
                              {
                                 try {
                                    currrtype = (RefType) ((NewArrayExpr)s.getRightOp()).getBaseType();
                                    Type type = s.getLeftOp().getType();
                                    type.apply( new TypeSwitch(){
                                       public void caseArrayType( ArrayType r ){
                                          String ltypestring = ((RefType) r.baseType).className;
                                          if ( !(((TypeNode) declaredtypesHT.get(ltypestring)) == null) )
                                          {
                                             /*
                                             	  System.out.println("ADDED ARRAY INSTANCE TYPE "+currrtype.className+"
                                             TO DECLARED TYPE "+ltypestring);
                                               */
                                             TypeNode tn = (TypeNode) declaredtypesHT.get(ltypestring);
                                             tn.addInstanceType(currrtype.className);
                                             declaredtypesHT.put(tn.getTypeName(), tn );
                                          }

                                       }

                                    });

                                 }
                                 catch(java.lang.RuntimeException e) {}

                              }
                              // IF S.GETRIGHTOP() INSTANCEOF NEWARRAYEXPR
                              else
                              {
                                 if( s.getRightOp() instanceof NewMultiArrayExpr )
                                 {
                                    try {
                                       currrtype = (RefType) ((ArrayType) ((NewMultiArrayExpr)s.getRightOp()).getBaseType()).baseType;
                                       Type type = s.getLeftOp().getType();
                                       type.apply( new TypeSwitch() {
                                          public void caseArrayType( ArrayType r ) {
                                             String ltypestring = ((RefType) r.baseType).className;
                                             if ( !(((TypeNode) declaredtypesHT.get(ltypestring)) == null ))
                                             {
                                                /*
                                                	    System.out.println("ADDED MULTIDIMENSIONAL ARRAY INSTANCE TYPE
                                                "+currrtype.className+" TO DECLARED TYPE "+ltypestring);
                                                */
                                                TypeNode tn = (TypeNode) declaredtypesHT.get(ltypestring);
                                                tn.addInstanceType(currrtype.className);
                                                declaredtypesHT.put(tn.getTypeName(), tn );
                                             }

                                          }

                                       });

                                    }
                                    catch (java.lang.RuntimeException e) {}

                                 }
                                 // IF S.GETRIGHTOP() INSTANCEOF NEWMULTIARRAYEXPR
                                 else
                                 {
                                    if ( s.getRightOp() instanceof InstanceOfExpr )
                                    {
                                       InstanceOfExpr ioexpr = ( InstanceOfExpr ) s.getRightOp();
                                       if ( ioexpr.getOp() instanceof Constant )
                                       {
                                          Type ltype = s.getLeftOp().getType();
                                          try {
                                             nullflag = false;
                                             Type Rrtype = ioexpr.getOp().getType();
                                             Rrtype.apply( new TypeSwitch(){
                                                public void caseNullType( NullType u ){
                                                   nullflag = true;
                                                }

                                             });

                                             currRrtype = (RefType) ioexpr.getOp().getType();
                                          }
                                          catch ( java.lang.ClassCastException e1) {}

                                          ltype.apply( new TypeSwitch(){
                                             public void caseRefType( RefType r ){
                                                if ( !nullflag )
                                                {
                                                   if ( !(((TypeNode) declaredtypesHT.get(r.className)) == null ))
                                                   {
                                                      TypeNode tn = (TypeNode) declaredtypesHT.get(r.className);
                                                      /*
                                                          System.out.println("");
                                                          System.out.println("ADDED INSTANCE TYPE "+currRrtype.className+" TO DECLARED
                                                      TYPE "+tn.getTypeName()+" : INSTANCEOF STMT (CONSTT) ");   
                                                          System.out.println("");
                                                      */
                                                      tn.addInstanceType(currRrtype.className);
                                                      declaredtypesHT.put(tn.getTypeName(), tn );
                                                   }

                                                }
                                                // IF !NULLFLAG

                                             }

                                             public void caseArrayType( ArrayType r ) {
                                                BaseType currbtype = r.baseType;
                                                currbtype.apply( new TypeSwitch(){
                                                   public void caseRefType( RefType r1 ){
                                                      if ( !nullflag )
                                                      {
                                                         if ( !(((TypeNode) declaredtypesHT.get(r1.className)) == null
                                                         ))
                                                         {
                                                            TypeNode tn = (TypeNode) declaredtypesHT.get( r1.className
                                                            );
                                                            /*
                                                                System.out.println("");
                                                                System.out.println("ADDED INSTANCE TYPE "+currRrtype.className+" TO DECLARED
                                                            TYPE "+tn.getTypeName()+" : INSTANCEOF STMT (CONSTT TO ARRAY) ");
                                                                System.out.println("");
                                                                */
                                                            tn.addInstanceType(currRrtype.className);
                                                            declaredtypesHT.put(tn.getTypeName(), tn );
                                                         }

                                                      }
                                                      // IF !NULLFLAG

                                                   }

                                                });

                                             }

                                          });

                                       }
                                       // IF IOEXPR.GETOP() INSTANCEOF CONSTANT
                                       else
                                       {
                                          arraybothsides = false;
                                          Type ltype = s.getLeftOp().getType();
                                          try {
                                             nullflag = false;
                                             Type Rrtype = ioexpr.getOp().getType();
                                             Rrtype.apply( new TypeSwitch(){
                                                public void caseNullType( NullType u ){
                                                   nullflag = true;
                                                }

                                             });

                                             currRrtype = (RefType) ioexpr.getOp().getType();
                                          }
                                          catch ( java.lang.ClassCastException e1) {
                                             try {
                                                currRrtype = (RefType) (((ArrayType) ioexpr.getOp().getType()).baseType);
                                                arraybothsides = true;
                                             }
                                             catch ( java.lang.ClassCastException e2) {}

                                          }
                                          // CATCH

                                          try {
                                             currlabel = currmethod.getSignature()+( (Local) ioexpr.getOp() ).getName();
                                          }
                                          catch ( java.lang.RuntimeException e ) {}

                                          ltype.apply( new TypeSwitch(){
                                             public void caseRefType( RefType r ){
                                                if ( !(nullflag) )
                                                {
                                                   if ( !(((TypeNode) declaredtypesHT.get(currRrtype.className)) == null ))
                                                   {
                                                      TypeNode rtn = (TypeNode) declaredtypesHT.get( currRrtype.className );
                                                      try {
                                                         if ( ( (String) thisHT.get( currmethod.getSignature() )).equals(currlabel) )
                                                         rtn = (TypeNode) declaredtypesHT.get( new String("this_"+currmethod.getSignature()) );
                                                      }
                                                      catch ( java.lang.RuntimeException e ) {}

                                                      TypeNode ltn = (TypeNode) declaredtypesHT.get( r.className );
                                                      if ( !((ltn.getTypeName()).equals(rtn.getTypeName())) )
                                                      {
                                                         /*
                                                             System.out.println("");
                                                             System.out.println("ADDED EDGE BETWEEN "+rtn.getTypeName()+" AND
                                                         "+ltn.getTypeName()+" : INSTANCEOF STMT ");
                                                             System.out.println("");
                                                            */
                                                         rtn.addForwardNode(ltn);
                                                         ltn.addBackwardNode(rtn);
                                                         if ( arraybothsides )
                                                         {
                                                            ltn.addForwardNode(rtn);
                                                            rtn.addBackwardNode(ltn);
                                                         }

                                                         declaredtypesHT.put(ltn.getTypeName(), ltn );
                                                         declaredtypesHT.put(rtn.getTypeName(), rtn);
                                                      }

                                                   }

                                                }
                                                // IF !NULLFLAG

                                             }

                                             public void caseArrayType( ArrayType r ){
                                                BaseType currbtype = r.baseType;
                                                currbtype.apply( new TypeSwitch(){
                                                   public void caseRefType( RefType r1 ){
                                                      if ( !(nullflag) )
                                                      {
                                                         if ( !(((TypeNode) declaredtypesHT.get(currRrtype.className)) == null ))
                                                         {
                                                            TypeNode rtn = (TypeNode) declaredtypesHT.get( currRrtype.className );
                                                            TypeNode ltn = (TypeNode) declaredtypesHT.get( r1.className );
                                                            if ( !((ltn.getTypeName()).equals(rtn.getTypeName())) )
                                                            {
                                                               /*
                                                                   System.out.println("");
                                                                   System.out.println("ADDED EDGE BETWEEN "+rtn.getTypeName()+" AND
                                                               "+ltn.getTypeName()+" AND VICE VERSA : INSTANCEOF STMT ( ARRAY )");
                                                                   System.out.println("");
                                                                 */
                                                               rtn.addForwardNode(ltn);
                                                               ltn.addBackwardNode(rtn);
                                                               if ( arraybothsides )
                                                               {
                                                                  ltn.addForwardNode(rtn);
                                                                  rtn.addBackwardNode(ltn);
                                                               }

                                                            }

                                                         }

                                                      }
                                                      // IF !NULLFLAG

                                                   }

                                                });

                                             }

                                          });

                                       }
                                       // ELSE

                                    }
                                    // IF S.GETRIGHTOP() INSTANCEOF INSTANCEOFEXPR
                                    else
                                    {
                                       nullflag = false;
                                       if ( s.getRightOp() instanceof CastExpr )
                                       {
                                          CastExpr caexpr = ( CastExpr ) s.getRightOp();
                                          if ( caexpr.getOp() instanceof Constant )
                                          {
                                             Type ltype = s.getLeftOp().getType();
                                             try {
                                                nullflag = false;
                                                Type Rrtype = caexpr.getOp().getType();
                                                Rrtype.apply( new TypeSwitch(){
                                                   public void caseNullType( NullType u ){
                                                      nullflag = true;
                                                   }

                                                });

                                                currRrtype = (RefType) caexpr.getOp().getType();
                                             }
                                             catch ( java.lang.ClassCastException e1) {}

                                             ltype.apply( new TypeSwitch(){
                                                public void caseRefType( RefType r ){
                                                   if ( !nullflag )
                                                   {
                                                      if ( !(((TypeNode) declaredtypesHT.get(r.className)) == null ))
                                                      {
                                                         TypeNode tn = (TypeNode) declaredtypesHT.get(r.className);
                                                         /*
                                                             System.out.println("");
                                                             System.out.println("ADDED INSTANCE TYPE "+currRrtype.className+" TO DECLARED
                                                         TYPE "+tn.getTypeName()+" : CLASSCAST STMT (CONSTT) ");   
                                                             System.out.println("");
                                                           */
                                                         tn.addInstanceType(currRrtype.className);
                                                         declaredtypesHT.put(tn.getTypeName(), tn );
                                                      }

                                                   }
                                                   // IF !NULLFLAG  

                                                }

                                                public void caseArrayType( ArrayType r ){
                                                   BaseType currbtype = r.baseType;
                                                   currbtype.apply( new TypeSwitch(){
                                                      public void caseRefType( RefType r1 ){
                                                         if ( !nullflag )
                                                         {
                                                            if ( !(((TypeNode) declaredtypesHT.get(r1.className)) == null ))
                                                            {
                                                               TypeNode tn = (TypeNode) declaredtypesHT.get( r1.className );
                                                               /*
                                                                   System.out.println("");
                                                                   System.out.println("ADDED INSTANCE TYPE "+currRrtype.className+" TO DECLARED
                                                               TYPE "+tn.getTypeName()+" : CLASSCAST STMT (CONSTT TO ARRAY) ");
                                                                   System.out.println("");
                                                                   */
                                                               tn.addInstanceType(currRrtype.className);
                                                               declaredtypesHT.put(tn.getTypeName(), tn );
                                                            }

                                                         }
                                                         // IF !NULLFLAG

                                                      }

                                                   });

                                                }

                                             });

                                          }
                                          // IF CAEXPR.GETOP() INSTANCEOF CONSTANT 
                                          else
                                          {
                                             arraybothsides = false;
                                             Type ltype = s.getLeftOp().getType();
                                             try {
                                                nullflag = false;
                                                Type Rrtype = caexpr.getOp().getType();
                                                Rrtype.apply( new TypeSwitch(){
                                                   public void caseNullType( NullType u ){
                                                      nullflag = true;
                                                   }

                                                });

                                                currRrtype = (RefType) caexpr.getOp().getType();
                                             }
                                             catch ( java.lang.ClassCastException e1) {
                                                try {
                                                   currRrtype = (RefType) (((ArrayType) caexpr.getOp().getType()).baseType);
                                                   arraybothsides = true;
                                                }
                                                catch ( java.lang.ClassCastException e2) {}

                                             }
                                             // CATCH

                                             try {
                                                currlabel = currmethod.getSignature()+( (Local) caexpr.getOp() ).getName();
                                             }
                                             catch ( java.lang.RuntimeException e ) {}

                                             ltype.apply( new TypeSwitch(){
                                                public void caseRefType( RefType r ){
                                                   if ( !(nullflag) )
                                                   {
                                                      if ( !(((TypeNode) declaredtypesHT.get(currRrtype.className)) == null ))
                                                      {
                                                         TypeNode rtn = (TypeNode) declaredtypesHT.get( currRrtype.className );
                                                         try {
                                                            if ( ( (String) thisHT.get( currmethod.getSignature() )).equals(currlabel) )
                                                            rtn = (TypeNode) declaredtypesHT.get( new String("this_"+currmethod.getSignature()) );
                                                         }
                                                         catch ( java.lang.RuntimeException e ) {}

                                                         TypeNode ltn = (TypeNode) declaredtypesHT.get( r.className );
                                                         if ( !((ltn.getTypeName()).equals(rtn.getTypeName())) )
                                                         {
                                                            /*
                                                                System.out.println("");
                                                                System.out.println("ADDED EDGE BETWEEN "+rtn.getTypeName()+" AND
                                                            "+ltn.getTypeName()+" : CLASSCAST STMT ");
                                                                System.out.println("");
                                                               */
                                                            rtn.addForwardNode(ltn);
                                                            ltn.addBackwardNode(rtn);
                                                            if ( arraybothsides )
                                                            {
                                                               ltn.addForwardNode(rtn);
                                                               rtn.addBackwardNode(ltn);
                                                            }

                                                            declaredtypesHT.put(ltn.getTypeName(), ltn );
                                                            declaredtypesHT.put(rtn.getTypeName(), rtn);
                                                         }

                                                      }

                                                   }
                                                   // IF !NULLFLAG

                                                }

                                                public void caseArrayType( ArrayType r ) {
                                                   BaseType currbtype = r.baseType;
                                                   currbtype.apply( new TypeSwitch(){
                                                      public void caseRefType( RefType r1 ){
                                                         if ( !(nullflag) )
                                                         {
                                                            if ( !(((TypeNode) declaredtypesHT.get(currRrtype.className)) == null ))
                                                            {
                                                               TypeNode rtn = (TypeNode) declaredtypesHT.get( currRrtype.className );
                                                               TypeNode ltn = (TypeNode) declaredtypesHT.get( r1.className );
                                                               if ( !((ltn.getTypeName()).equals(rtn.getTypeName())) )
                                                               {
                                                                  /*
                                                                      System.out.println("");
                                                                      System.out.println("ADDED EDGE BETWEEN "+rtn.getTypeName()+" AND
                                                                  "+ltn.getTypeName()+" AND VICE VERSA : CLASSCAST STMT ( ARRAY )");
                                                                      System.out.println("");
                                                                    */
                                                                  rtn.addForwardNode(ltn);
                                                                  ltn.addBackwardNode(rtn);
                                                                  //                if ( arraybothsides )
                                                                  //                {

                                                                  ltn.addForwardNode(rtn);
                                                                  rtn.addBackwardNode(ltn);
                                                                  //                 }        

                                                                  declaredtypesHT.put(ltn.getTypeName(), ltn );
                                                                  declaredtypesHT.put(rtn.getTypeName(), rtn);
                                                               }

                                                            }

                                                         }
                                                         // IF !NULLFLAG

                                                      }

                                                   });

                                                }

                                             });

                                          }
                                          // ELSE

                                       }
                                       // IF S.GETRIGHTOP() INSTANCEOF CASTEXPR
                                       else
                                       {
                                          if ( s.getRightOp() instanceof InvokeExpr )
                                          {
                                             try {
                                                InvokeExpr invexpr = ( InvokeExpr ) s.getRightOp();
                                                boolean search = true;
                                                MethodNode currmnode = cagb.getNode( currmethod );
                                                /* EFFICIENCY

                                                	     Iterator CSiter = currmnode.getCallSites().iterator();                   
                                                	     // CallSite cs = new CallSite();
                                                  
                                                             CallSite cs = null;

                                                	     while ( ( CSiter.hasNext() )&&(search == true) )
                                                	     {
                                                	 
                                                	      try {
                                                 
                                                		cs = (CallSite) CSiter.next();
                                                     
                                                		InvokeExpr invokeExpr = cs.getInvokeExpr();  

                                                		if ( invokeExpr.equals(invexpr) )
                                                		{
                                                   
                                                	       search = false;
                                                   
                                                	       }

                                                	      } catch ( java.lang.RuntimeException e ) {} 
                                                 
                                                	     }


                                                */
                                                CallSite cs = currmnode.getCallSite ( new Integer(numinvokes) );
                                                Set possMethodNodes = cs.getMethods();
                                                Iterator MNiter = possMethodNodes.iterator();
                                                while ( MNiter.hasNext() )
                                                {
                                                   try {
                                                      SootMethod meth = ((MethodNode) MNiter.next()).getMethod();
                                                      reachedmethod = meth.getSignature();
                                                      reachedclass = (meth.getDeclaringClass()).getName();
                                                      try {
                                                         arraybothsides = false;
                                                         ArrayType atype = ( ArrayType ) meth.getReturnType();
                                                         arraybothsides = true;
                                                      }
                                                      catch ( java.lang.RuntimeException e ) {}

                                                      Type ltype = s.getLeftOp().getType();
                                                      ltype.apply( new TypeSwitch(){
                                                         public void caseRefType( RefType r ){
                                                            TypeNode rtn = (TypeNode) declaredtypesHT.get( new String("return_"+reachedmethod));
                                                            TypeNode ltn = (TypeNode) declaredtypesHT.get(r.className);
                                                            /*
                                                                System.out.println("");
                                                                System.out.println("ADDED EDGE BETWEEN "+rtn.getTypeName()+" AND
                                                            "+ltn.getTypeName()+" : ASSIGN STMT (METHOD RETURN)");
                                                                System.out.println("");
                                                               */
                                                            rtn.addForwardNode(ltn);
                                                            ltn.addBackwardNode(rtn);
                                                            if ( arraybothsides )
                                                            {
                                                               ltn.addForwardNode(rtn);
                                                               rtn.addBackwardNode(ltn);
                                                            }

                                                            declaredtypesHT.put(ltn.getTypeName(), ltn );
                                                            declaredtypesHT.put(rtn.getTypeName(), rtn);
                                                         }

                                                         public void caseArrayType( ArrayType r ){
                                                            BaseType currbtype = r.baseType;
                                                            currbtype.apply( new TypeSwitch(){
                                                               public void caseRefType( RefType r1 ){
                                                                  TypeNode rtn = (TypeNode) declaredtypesHT.get( new String("return_"+reachedmethod) );
                                                                  TypeNode ltn = (TypeNode) declaredtypesHT.get(r1.className);
                                                                  /*
                                                                      System.out.println("");
                                                                      System.out.println("ADDED EDGE BETWEEN "+rtn.getTypeName()+" AND
                                                                  "+ltn.getTypeName()+" AND VICE VERSA : ASSIGN STMT (METHOD RETURN) (ARRAY) ");
                                                                      System.out.println("");
                                                                  */
                                                                  rtn.addForwardNode(ltn);
                                                                  ltn.addBackwardNode(rtn);
                                                                  ltn.addForwardNode(rtn);
                                                                  rtn.addBackwardNode(ltn);
                                                                  declaredtypesHT.put(ltn.getTypeName(), ltn );
                                                                  declaredtypesHT.put(rtn.getTypeName(), rtn);
                                                               }

                                                            });

                                                         }

                                                      });

                                                   }
                                                   catch ( java.lang.RuntimeException e ){}

                                                }

                                             }
                                             catch ( java.lang.RuntimeException e ){}

                                             numinvokes++;
                                          }
                                          // IF S.GETRIGHTOP() INSTANCEOF INVOKEEXPR
                                          else
                                          {
                                             nullflag = false;
                                             if ( s.getRightOp() instanceof Constant )
                                             {
                                                Type ltype = s.getLeftOp().getType();
                                                try {
                                                   nullflag = false;
                                                   Type Rrtype = s.getRightOp().getType();
                                                   Rrtype.apply( new TypeSwitch(){
                                                      public void caseNullType( NullType u ){
                                                         nullflag = true;
                                                      }

                                                   });

                                                   currRrtype = (RefType) s.getRightOp().getType();
                                                }
                                                catch ( java.lang.ClassCastException e1) {}

                                                ltype.apply( new TypeSwitch(){
                                                   public void caseRefType( RefType r ){
                                                      if ( !nullflag )
                                                      {
                                                         if ( !(((TypeNode) declaredtypesHT.get(r.className)) == null ))
                                                         {
                                                            TypeNode tn = (TypeNode) declaredtypesHT.get(r.className);
                                                            /*
                                                                System.out.println("");
                                                                System.out.println("ADDED INSTANCE TYPE "+currRrtype.className+" TO DECLARED
                                                            TYPE "+tn.getTypeName()+" : ASSIGN STMT (CONSTANT) ");
                                                                System.out.println("");
                                                                */
                                                            tn.addInstanceType(currRrtype.className);
                                                            declaredtypesHT.put(tn.getTypeName(), tn );
                                                         }

                                                      }
                                                      //  IF !NULLFLAG

                                                   }

                                                   public void caseArrayType( ArrayType r ){
                                                      BaseType currbtype = r.baseType;
                                                      currbtype.apply( new TypeSwitch(){
                                                         public void caseRefType( RefType r1 ){
                                                            if ( !nullflag )
                                                            {
                                                               if ( !(((TypeNode) declaredtypesHT.get(r1.className)) == null ))
                                                               {
                                                                  TypeNode tn = (TypeNode) declaredtypesHT.get( r1.className );
                                                                  /*
                                                                      System.out.println("");
                                                                      System.out.println("ADDED INSTANCE TYPE "+currRrtype.className+" TO DECLARED
                                                                  TYPE "+tn.getTypeName()+" : ASSIGN STMT (CONSTT TO ARRAY) ");
                                                                      System.out.println("");
                                                                        */
                                                                  tn.addInstanceType(currRrtype.className);
                                                                  declaredtypesHT.put(tn.getTypeName(), tn );
                                                               }

                                                            }
                                                            //  IF !NULLFLAG

                                                         }

                                                      });

                                                   }

                                                });

                                             }
                                             //  IF S.GETRIGHTOP() INSTANCEOF CONSTANT
                                             else
                                             {
                                                arraybothsides = false;
                                                Type ltype = s.getLeftOp().getType();
                                                try {
                                                   nullflag = false;
                                                   Type Rrtype = s.getRightOp().getType();
                                                   Rrtype.apply( new TypeSwitch(){
                                                      public void caseNullType( NullType u ){
                                                         nullflag = true;
                                                      }

                                                   });

                                                   currRrtype = (RefType) s.getRightOp().getType();
                                                }
                                                catch ( java.lang.ClassCastException e1) {
                                                   try {
                                                      currRrtype = (RefType) (((ArrayType) s.getRightOp().getType()).baseType);
                                                      arraybothsides = true;
                                                   }
                                                   catch ( java.lang.ClassCastException e2) {}

                                                }
                                                // CATCH

                                                try {
                                                   currlabel = currmethod.getSignature()+( (Local) s.getRightOp() ).getName();
                                                }
                                                catch ( java.lang.RuntimeException e ) {}

                                                ltype.apply( new TypeSwitch(){
                                                   public void caseRefType( RefType r ){
                                                      if ( !(nullflag) )
                                                      {
                                                         if ( !(((TypeNode) declaredtypesHT.get(currRrtype.className)) == null ))
                                                         {
                                                            TypeNode rtn = (TypeNode) declaredtypesHT.get( currRrtype.className );
                                                            try {
                                                               if ( ( (String) thisHT.get( currmethod.getSignature() )).equals(currlabel) )
                                                               rtn = (TypeNode) declaredtypesHT.get( new String("this_"+currmethod.getSignature()) );
                                                            }
                                                            catch ( java.lang.RuntimeException e ) {}

                                                            TypeNode ltn = (TypeNode) declaredtypesHT.get( r.className );
                                                            if ( !((ltn.getTypeName()).equals(rtn.getTypeName())) )
                                                            {
                                                               /*
                                                                   System.out.println("");
                                                                   System.out.println("ADDED EDGE BETWEEN "+rtn.getTypeName()+" AND
                                                               "+ltn.getTypeName()+" : ASSIGN STMT ");

                                                                   System.out.println("");
                                                               */
                                                               rtn.addForwardNode(ltn);
                                                               ltn.addBackwardNode(rtn);
                                                               if ( arraybothsides )
                                                               {
                                                                  ltn.addForwardNode(rtn);
                                                                  rtn.addBackwardNode(ltn);
                                                               }

                                                               declaredtypesHT.put(ltn.getTypeName(), ltn );
                                                               declaredtypesHT.put(rtn.getTypeName(),rtn);
                                                            }

                                                         }

                                                      }
                                                      // IF !NULLFLAG

                                                   }

                                                   public void caseArrayType( ArrayType r ){
                                                      BaseType currbtype = r.baseType;
                                                      currbtype.apply( new TypeSwitch(){
                                                         public void caseRefType( RefType r1 ){
                                                            if ( !(nullflag) )
                                                            {
                                                               if ( !(((TypeNode) declaredtypesHT.get(currRrtype.className)) == null ))
                                                               {
                                                                  TypeNode rtn = (TypeNode) declaredtypesHT.get( currRrtype.className );
                                                                  TypeNode ltn = (TypeNode) declaredtypesHT.get( r1.className );
                                                                  if ( !((ltn.getTypeName()).equals(rtn.getTypeName())) )
                                                                  {
                                                                     /*
                                                                         System.out.println("");
                                                                         System.out.println("ADDED EDGE BETWEEN "+rtn.getTypeName()+" AND
                                                                     "+ltn.getTypeName()+" AND VICE VERSA : ASSIGN STMT (ARRAY)");
                                                                            
                                                                         System.out.println("");
                                                                     */
                                                                     rtn.addForwardNode(ltn);
                                                                     ltn.addBackwardNode(rtn);
                                                                     if ( arraybothsides )
                                                                     {
                                                                        ltn.addForwardNode(rtn);
                                                                        rtn.addBackwardNode(ltn);
                                                                     }

                                                                     declaredtypesHT.put(ltn.getTypeName(), ltn );
                                                                     declaredtypesHT.put(rtn.getTypeName(),rtn);
                                                                  }

                                                               }

                                                            }
                                                            // IF !NULLFLAG

                                                         }

                                                      });

                                                   }

                                                });

                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                           // ELSE'S

                        }
                        //  CASE ASSIGNSTMT

                     });

                  }
                  catch (java.lang.RuntimeException e){
                     //       System.out.println("\t ------ Runtime ERROR AT DTA : IGNORED THE PROBLEM STMT IN "+currclass.getName()+" "+currmethod.getName());

                     //    System.out.println(" PROBLEM STMT : "+stmt.toString());

                  }

               }
               // WHILE STMTITER

            }
            catch ( java.lang.NullPointerException e ){
               //	  System.err.println("\t------- NullPtr ERROR AT DTA : Jimple can't handle " + currclass.getName() );

            }
            catch ( java.lang.RuntimeException e ){
               //	  System.err.println("\t ------ Runtime ERROR AT DTA : Jimple can't handle " + currclass.getName() +" :  " + e.getMessage());

            }

         }
         catch ( java.lang.RuntimeException e ) {}

      }
      // WHILE ITER

      System.out.println("Done");
      analyseCallSites();
      DTANativeAdjustor dtanative = new DTANativeAdjustor ( (HashMap) declaredtypesHT, rta );
      dtanative.adjustForNativeMethods();
   }


   RTA rta;
   void collectThisVars() {
      /* THIS METHOD COLLECTS THE LOCALS REPRESENTING "THIS" IN EACH METHOD 
          IN THE CALL GRAPH AND STORES THEM IN A HASHTABLE TO BE USED AT THE 
          TIME OF ANALYSING STATEMENTS 

          CALLED BY INITIALIZECONSTRAINTGRAPH() */
      Collection callGraph = cagb.getCallGraph();
      Iterator iter = callGraph.iterator();
      while ( iter.hasNext() )
      {
         try {
            MethodNode tempMN = (MethodNode) iter.next();
            currmethod = tempMN.getMethod();
            currclass = currmethod.getDeclaringClass();
            try {
               JimpleBody listBody = Jimplifier.getJimpleBody( currmethod );
               Iterator stmtIter = listBody.getStmtList().iterator();
               while ( stmtIter.hasNext() )
               {
                  try {
                     Stmt stmt = (Stmt)stmtIter.next();
                     stmt.apply( new AbstractStmtSwitch(){
                        public void caseIdentityStmt(IdentityStmt s){
                           if ( s.getRightOp() instanceof ThisRef )
                           {
                              String localname = currmethod.getSignature()+(((Local) s.getLeftOp()).getName());
                              thisHT.put(currmethod.getSignature(),localname);
                           }

                        }

                     });

                  }
                  catch ( java.lang.RuntimeException e){}

               }
               // WHILE STMTITER

            }
            catch ( java.lang.RuntimeException e){}

         }
         catch ( java.lang.RuntimeException e){}

      }
      // WHILE ITER

   }


   void analyseCallSites() {
      /* THIS METHOD EXAMINES EACH CALLSITE IN DETAIL AND MODIFIES THE 
           CONSTRAINT GRAPH 

           EDGES, INSTANCE TYPES ARE ADDED TO ACCOUNT FOR THE MAPPING BETWEEN
           THE ACTUAL RECEIVER OF THE METHOD CALL, AND "THIS" OF EACH CALLEE
           METHOD IN THE INPUT CALL GRAPH, AND ALSO THE MAPPING BETWEEN THE 
           FORMAL AND ACTUAL PARAMETERS IN THESE CASES */
      int num = 0, mnum = 0;
      numinvokes = 1;
      Collection callGraph = cagb.getCallGraph();
      Iterator iter = callGraph.iterator();
      // System.out.println("DTA ANALYSING CALLSITES");

      System.out.println();
      System.out.print("Declared Type Analysis collecting constraints at method call sites");
      while ( iter.hasNext() )
      {
         try {
            // System.out.print("<"+mnum+">");

            mnum++;
            MethodNode tempMN = (MethodNode) iter.next();
            Iterator CSiter = tempMN.getCallSites().iterator();
            while ( CSiter.hasNext() )
            {
               ref = false;
               thisInvoked = false;
               isStatic = false;
               num++;
               CallSite cs = (CallSite) CSiter.next();
               InvokeExpr invokeExpr = null;
               try {
                  invokeExpr = cs.getInvokeExpr();
                  currmethod = tempMN.getMethod();
                  currclass = currmethod.getDeclaringClass();
                  try {
                     invokedclass = null;
                     invokeExpr.apply( new AbstractJimpleValueSwitch(){
                        public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v){
                           invokedclass = v.getBase().getType().toString();
                           currlabel = currmethod.getSignature()+((Local) v.getBase()).getName();
                           v.getBase().getType().apply( new TypeSwitch(){
                              public void caseRefType( RefType r ){
                                 ref = true;
                              }

                              public void caseArrayType( ArrayType r ){
                                 BaseType currbtype = r.baseType;
                                 currbtype.apply( new TypeSwitch(){
                                    public void caseRefType( RefType r1 ){
                                       invokedclass = r1.className;
                                       ref = true;
                                    }

                                 });

                              }

                           });

                        }
                        // CASE INTERFACEINVOKE

                        public void caseSpecialInvokeExpr(SpecialInvokeExpr v){
                           invokedclass = v.getBase().getType().toString();
                           currlabel = currmethod.getSignature()+((Local) v.getBase()).getName();
                           v.getBase().getType().apply( new TypeSwitch(){
                              public void caseRefType( RefType r ){
                                 ref = true;
                              }

                              public void caseArrayType( ArrayType r ){
                                 BaseType currbtype = r.baseType;
                                 currbtype.apply( new TypeSwitch(){
                                    public void caseRefType( RefType r1 ){
                                       invokedclass = r1.className;
                                       ref = true;
                                    }

                                 });

                              }

                           });

                        }
                        // CASESPECIALINVOKE

                        public void caseStaticInvokeExpr(StaticInvokeExpr v){
                           invokedclass = v.getType().toString();
                           isStatic = true;
                           v.getType().apply( new TypeSwitch(){
                              public void caseRefType( RefType r ){
                                 ref = true;
                              }

                              public void caseArrayType( ArrayType r ){
                                 BaseType currbtype = r.baseType;
                                 currbtype.apply( new TypeSwitch(){
                                    public void caseRefType( RefType r1 ){
                                       invokedclass = r1.className;
                                       ref = true;
                                    }

                                 });

                              }

                           });

                        }
                        // CASESTATICINVOKE

                        public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
                           invokedclass = v.getBase().getType().toString();
                           currlabel = currmethod.getSignature()+((Local) v.getBase()).getName();
                           v.getBase().getType().apply( new TypeSwitch(){
                              public void caseRefType( RefType r ){
                                 ref = true;
                              }

                              public void caseArrayType( ArrayType r ){
                                 BaseType currbtype = r.baseType;
                                 currbtype.apply( new TypeSwitch(){
                                    public void caseRefType( RefType r1 ){
                                       invokedclass = r1.className;
                                       ref = true;
                                    }

                                 });

                              }

                           });

                        }
                        // CASEVIRTUALINVOKE

                     });

                  }
                  catch ( java.lang.RuntimeException e ) {}

                  Set possMethodNodes = cs.getMethods();
                  Iterator MNiter = possMethodNodes.iterator();
                  while ( MNiter.hasNext() )
                  {
                     try {
                        MethodNode mn = (MethodNode) MNiter.next();
                        SootMethod method = mn.getMethod();
                        reachedclass = (method.getDeclaringClass()).getName();
                        if ( !isStatic )
                        {
                           if ( ref )
                           {
                              try {
                                 TypeNode invokedtn = new TypeNode();
                                 invokedtn = (TypeNode) declaredtypesHT.get( invokedclass );
                                 try {
                                    if ( ( (String) thisHT.get( currmethod.getSignature() )).equals(currlabel) )
                                    invokedtn = (TypeNode) declaredtypesHT.get( new String("this_"+currmethod.getSignature()) );
                                 }
                                 catch ( java.lang.RuntimeException e ) {}

                                 TypeNode thisreachedtn = new TypeNode();
                                 try {
                                    thisreachedtn = (TypeNode) declaredtypesHT.get( new String("this_"+method.getSignature()) );
                                 }
                                 catch ( java.lang.RuntimeException e ) {
                                    thisreachedtn = new TypeNode( new String("this_"+method.getSignature()), bitMap, inversebitMap );
                                    declaredtypesHT.put(thisreachedtn.getTypeName(),thisreachedtn);
                                 }
                                 // CATCH

                                 if ( !(invokedtn.getTypeName().equals(thisreachedtn.getTypeName())) )
                                 {
                                    /*
                                        System.out.println("");
                                        System.out.println("ADDED EDGE BETWEEN "+invokedtn.getTypeName()+" AND
                                    "+thisreachedtn.getTypeName()+" : METHOD CALL");
                                        System.out.println("");
                                    */
                                    invokedtn.addForwardNode(thisreachedtn);
                                    thisreachedtn.addBackwardNode(invokedtn);
                                    declaredtypesHT.put(invokedtn.getTypeName(), invokedtn );
                                    declaredtypesHT.put(thisreachedtn.getTypeName(), thisreachedtn);
                                 }

                              }
                              catch ( java.lang.RuntimeException e ) {}

                           }

                        }
                        // IF !ISSTATIC 

                        int argcount = invokeExpr.getArgCount();
                        int counter = 0;
                        while ( counter < argcount )
                        {
                           try {
                              refParam = false;
                              thisParam = false;
                              arraybothsides = false;
                              String actualtype = invokeExpr.getArg(counter).getType().toString();
                              String formaltype = method.getParameterType(counter).toString();
                              if ( invokeExpr.getArg(counter) instanceof Constant )
                              {
                                 currformallabel = formaltype;
                                 invokeExpr.getArg(counter).getType().apply( new TypeSwitch(){
                                    public void caseRefType( RefType r ){
                                       if ( !(((TypeNode) declaredtypesHT.get(currformallabel)) == null ) )
                                       {
                                          TypeNode tn = (TypeNode) declaredtypesHT.get(currformallabel);
                                          /*
                                          	    System.out.println("");
                                              System.out.println("ADDED INSTANCE TYPE "+r.className+" TO DECLARED TYPE
                                          "+tn.getTypeName()+" : PARAMETER PASSING (CONSTANT) ");
                                              System.out.println("");
                                          */
                                          tn.addInstanceType(r.className);
                                          declaredtypesHT.put(tn.getTypeName(), tn);
                                       }

                                    }

                                 });

                              }
                              // IF INVOKEEXPR.GETARG(COUNTER) INSTANCEOF CONSTANT
                              else
                              {
                                 curractuallabel = invokeExpr.getArg(counter).getType().toString();
                                 try {
                                    currlabel = currmethod.getSignature()+( (Local) invokeExpr.getArg(counter) ).getName();
                                 }
                                 catch ( java.lang.RuntimeException e ) {}

                                 invokeExpr.getArg(counter).getType().apply( new TypeSwitch(){
                                    public void caseRefType( RefType r ){
                                       refParam = true;
                                    }

                                    public void caseArrayType( ArrayType r ){
                                       BaseType currbtype = r.baseType;
                                       currbtype.apply( new TypeSwitch(){
                                          public void caseRefType( RefType r1 ){
                                             curractuallabel = r1.className;
                                             refParam = true;
                                             arraybothsides = true;
                                          }

                                       });

                                    }

                                 });

                                 currformallabel = formaltype;
                                 method.getParameterType(counter).apply( new TypeSwitch(){
                                    public void caseArrayType( ArrayType r ){
                                       BaseType currbtype = r.baseType;
                                       currbtype.apply( new TypeSwitch(){
                                          public void caseRefType( RefType r1 ){
                                             currformallabel = r1.className;
                                          }

                                       });

                                       arraybothsides = true;
                                    }

                                 });

                                 if ( refParam )
                                 {
                                    if ( !(((TypeNode) declaredtypesHT.get(currformallabel)) == null ) )
                                    {
                                       TypeNode actualtn = new TypeNode();
                                       actualtn = (TypeNode) declaredtypesHT.get( curractuallabel );
                                       try {
                                          if ( ( (String) thisHT.get( currmethod.getSignature() )).equals(currlabel) )
                                          actualtn = (TypeNode) declaredtypesHT.get( new String("this_"+currmethod.getSignature()) );
                                       }
                                       catch ( java.lang.RuntimeException e ) {}

                                       TypeNode formaltn = (TypeNode) declaredtypesHT.get(currformallabel);
                                       if ( !(actualtn.getTypeName().equals(formaltn.getTypeName())) )
                                       {
                                          /*
                                              System.out.println("");
                                              System.out.println("ADDED EDGE BETWEEN "+actualtn.getTypeName()+" AND
                                          "+formaltn.getTypeName()+" AND VICE VERSA : PARAMETER PASSING");
                                              System.out.println("");
                                           */
                                          actualtn.addForwardNode(formaltn);
                                          formaltn.addBackwardNode(actualtn);
                                          if ( arraybothsides )
                                          {
                                             actualtn.addBackwardNode(formaltn);
                                             formaltn.addForwardNode(actualtn);
                                          }

                                          declaredtypesHT.put(actualtn.getTypeName(), actualtn );
                                          declaredtypesHT.put(formaltn.getTypeName(), formaltn);
                                       }

                                    }

                                 }
                                 // IF REFPARAM 

                              }
                              // ELSE

                           }
                           catch ( java.lang.RuntimeException e ) {}

                           counter++;
                        }
                        // WHILE COUNTER

                     }
                     catch ( java.lang.RuntimeException e ) {}

                  }
                  // WHILE MNITER

               }
               catch ( java.lang.RuntimeException e ) {}

            }
            // WHILE CSITER 

         }
         catch ( java.lang.NullPointerException e ) {}

      }
      // WHILE ITER 

      System.out.println("Done");
   }


   void solveConstraints( Map allclassHT )
   {
      /* THIS METHOD SOLVES THE CONSTRAINT GRAPH OBTAINED AS A RESULT 
           OF ANALYSING ALL THE STATEMENTS IN THE INPUT CALL GRAPH

           A WORKLIST ALGORITHM IS BEING USED */
      int constraintedges = 0;
      //  System.out.println("READY FOR GARBAGE COLLECTION");

      //  Jimplifier.removeall();

      //  System.out.println("DONE WITH GARBAGE COLLECTION");

      Timer SCCTimer = new Timer();
      SCCTimer.start();
      ReplaceStrConnComp();
      SCCTimer.end();
      // System.out.println("TIME FOR SCC : "+SCCTimer.getTime());

      int solved = 0;
      Timer SolverTimer = new Timer();
      SolverTimer.start();
      List workQ = new ArrayList();
      Object[] keys = declaredtypesHT.keySet().toArray();
      System.out.println("");
      System.out.print("Declared Type Analysis solving constraints.....");
      for ( int i = 0 ; i < keys.length ; i++ )
      {
         TypeNode tn = ((TypeNode) declaredtypesHT.get( keys[i] ));
         if ( tn.isSource() )
         workQ.add(tn);
      }

      while ( !workQ.isEmpty() )
      {
         TypeNode tn = (TypeNode) workQ.remove(0);
         tn.solveNode(allclassHT, instancetypesHT );
         //   System.out.print("<"+solved+">");

         solved++;
         if ( ! ( tn.isSink() ) )
         {
            List forwardnodes = tn.getForwardNodes();
            Iterator forwardit = forwardnodes.iterator();
            while ( forwardit.hasNext() )
            {
               TypeNode forwardtn = (TypeNode) forwardit.next();
               constraintedges++;
               if ( forwardtn.isReady() )
               workQ.add( forwardtn );
            }

         }

      }

      System.out.println("Done");
      System.out.println("");
      System.out.println("TOTAL NUMBER OF CONSTRAINT NODES = "+declaredtypesHT.keySet().size());
      System.out.println("TOTAL NUMBER OF CONSTRAINT EDGES = "+constraintedges);
      SolverTimer.end();
      //  System.out.println("TIME FOR SOLVING THE SYSTEM : "+SolverTimer.getTime());

   }


   int rtared[];
   void setRTAred(RTA rta ){ rtared = rta.getReduced(); }


   Collection getCallGraph() {
      /* THIS METHOD PERFORMS ALL THE STATISTICS GATHERING ON THE CALL GRAPH 
         IMPROVEMENT POSSIBLE USING DECLARED TYPE ANALYSIS

         THE NEW SET OF METHODNODES ( OBTAINED USING DTA ) THAT CAN BE INVOKED 
         FROM EACH CALL SITE IS ALSO COMPUTED IN THIS METHOD
      */
      int edges = 0,removededges = 0,polymorphicsites = 0, polytotal =
      0,polyred = 0, intpolyred = 0, virpolyred = 0,sites = 0,zerored = 0,siteno =
      0,twoedges =
      0,inttwoedges = 0,virtwoedges = 0,threeedges =0, intthreeedges = 0,
      virthreeedges = 0, moreedges =0,
      intmoreedges = 0, virmoreedges = 0, twored = 0, inttwored = 0, virtwored =
      0,threered =0,
      intthreered = 0, virthreered = 0, morered =0, intmorered = 0, virmorered = 0,
      zonered = 0,ztwored =0,
      zthreered =0,zmorered = 0,bired = 0, intbired = 0, virbired = 0, bthreered = 0,
      intbthreered = 0,
      virbthreered = 0, bmorered = 0, intbmorered = 0, virbmorered = 0;
      int benchtotedges = 0, benchremovededges = 0, benchreduction = 0, benchintactedges = 0, benchpolyred = 0, benchintpolyred = 0, benchvirpolyred = 0;
      List paramList = new ArrayList();
      String reachedclass = new String();
      Collection callGraph = null;
      try {
         callGraph = cagb.getCallGraph();
         // Map OpenMethodsHT = cagb.getOpenMethodsHT();

         Iterator iter = callGraph.iterator();
         /*
            System.out.println("");
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("NUMBER OF METHODNODES = "+callGraph.size());
            System.out.println("NUMBER OF CALLSITES = "+cagb.getSitesNum());
            System.out.println("NUMBER OF CALLGRAPH EDGES = "+cagb.getEdgesNum());
            System.out.println("");
            */
         DTATimer.end();
         DTAMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
         /*
            System.out.println("TIME FOR DTA (ANALYSIS ONLY) : "+DTATimer.getTime());
            System.out.println("SPACE FOR DTA (ANALYSIS ONLY) : "+DTAMem);
            */
         DTAedgeRemovedTimer.start();
         System.out.println("");
         System.out.print ("Pruning call graph based on Declared Type Analysis");
         int mthdcount = 10;
         while ( iter.hasNext() )
         {
            String currclassname = null;
            if ( mthdcount == 10 )
            {
               System.out.print(".");
               mthdcount = 1;
            }

            else
            mthdcount++;
            try {
               MethodNode tempMN = (MethodNode) iter.next();
               // String currclassname = null;

               sites = sites + tempMN.getCallSites().size();
               Iterator CSiter = tempMN.getCallSites().iterator();
               while ( CSiter.hasNext() )
               {
                  try {
                     siteno++;
                     // System.out.print("<"+siteno+">");

                     thisInvoked = false;
                     ref = false;
                     isStatic = false;
                     isSpecial = false;
                     intflag = false;
                     virflag = false;
                     CallSite cs = (CallSite) CSiter.next();
                     // String currclassname = null;

                     try {
                        currclass = tempMN.getMethod().getDeclaringClass();
                        currmethod = tempMN.getMethod();
                        currclassname = currclass.getName();
                        InvokeExpr invokeExpr = cs.getInvokeExpr();
                        invokeExpr.apply( new AbstractJimpleValueSwitch(){
                           public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v){
                              intnum++;
                              intflag = true;
                              invokedclass = v.getBase().getType().toString();
                              currlabel = currmethod.getSignature()+((Local)v.getBase()).getName();
                              v.getBase().getType().apply( new TypeSwitch(){
                                 public void caseRefType( RefType r ){
                                    ref = true;
                                 }

                                 public void caseArrayType( ArrayType r ){
                                    ref = true;
                                 }

                              });

                           }
                           // CASE INTERFACEINVOKE 

                           public void caseSpecialInvokeExpr(SpecialInvokeExpr v){
                              spenum++;
                              invokedclass = v.getBase().getType().toString();
                              isSpecial = true;
                              currlabel = currmethod.getSignature()+((Local)v.getBase()).getName();
                              v.getBase().getType().apply( new TypeSwitch(){
                                 public void caseRefType( RefType r ){
                                    ref = true;
                                 }

                                 public void caseArrayType( ArrayType r ){
                                    ref = true;
                                 }

                              });

                           }
                           // CASE SPECIALINVOKE

                           public void caseStaticInvokeExpr(StaticInvokeExpr v){
                              stanum++;
                              invokedclass = v.getType().toString();
                              isStatic = true;
                              v.getType().apply( new TypeSwitch(){
                                 public void caseRefType( RefType r ){
                                    ref = true;
                                 }

                                 public void caseArrayType( ArrayType r ){
                                    ref = true;
                                 }

                              });

                           }
                           // CASE STATICINVOKE

                           public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
                              virnum++;
                              virflag = true;
                              invokedclass = v.getBase().getType().toString();
                              currlabel = currmethod.getSignature()+((Local)v.getBase()).getName();
                              v.getBase().getType().apply( new TypeSwitch(){
                                 public void caseRefType( RefType r ){
                                    ref = true;
                                 }

                                 public void caseArrayType( ArrayType r ){
                                    ref = true;
                                 }

                              });

                           }
                           // CASE VIRTUALINVOKE 

                        });

                     }
                     catch ( java.lang.NullPointerException e ) {}

                     Map actualinstanceHT = new HashMap();
                     BitSet actualinstanceBS = new BitSet();
                     HashMap reachedclassHT = new HashMap();
                     try {
                        /*

                               if ( ! (( (HashMap) instancetypesHT.get( invokedclass ) ) == null ) )
                               actualinstanceHT = (HashMap) instancetypesHT.get( invokedclass );
                        */
                        if ( ! (( (BitSet) instancetypesHT.get( invokedclass ) ) == null ) )
                        actualinstanceBS = ( BitSet ) instancetypesHT.get ( invokedclass );
                        // GET THE HASHTABLE CONTAINING THE NAMES OF THE CLASSES THAT CAN BE 
                        // REACHED DUE TO THIS METHOD CALL
                        // THIS ANSWER IS ARRIVED AT BY CHECKVIRTUALTABLES(..) BY USING THE
                        // RESULT FROM DECLARED TYPE ANALYSIS 

                        // reachedclassHT = checkVirtualTables(actualinstanceHT, cs.getInvokeExpr().getMethod());

                        reachedclassHT = checkVirtualTables(actualinstanceBS, cs.getInvokeExpr().getMethod());
                        // System.out.println ( "SIZEE "+reachedclassHT.size() );

                     }
                     catch ( java.lang.RuntimeException e ) {}

                     Set possMethodNodes = cs.getMethods();
                     HashSet filteredMethodNodes = new HashSet();
                     int size = possMethodNodes.size();
                     int reduction = 0;
                     if ( size > 1 )
                     {
                        polymorphicsites++;
                        polytotal = polytotal + size;
                        if ( size == 2 )
                        {
                           twoedges++;
                           if ( intflag ) inttwoedges++;
                           else virtwoedges++;
                        }

                        else if ( size == 3 )
                        {
                           threeedges++;
                           if ( intflag ) intthreeedges++;
                           else virthreeedges++;
                        }

                        else
                        {
                           moreedges++;
                           if ( intflag ) intmoreedges++;
                           else virmoreedges++;
                        }

                     }

                     if ( size == 1 )
                     {
                        if ( virflag ) virmono++;
                        else if ( intflag ) intmono++;
                     }

                     edges = edges + size;
                     Iterator MNiter = possMethodNodes.iterator();
                     if ( !(isStatic || isSpecial ) )
                     {
                        while ( MNiter.hasNext() )
                        {
                           try {
                              MethodNode menode = ( MethodNode ) MNiter.next();
                              SootMethod method = menode.getMethod();
                              reachedclass = (method.getDeclaringClass()).getName();
                              String tempdebug = null;
                              if ( ref )
                              {
                                 try {
                                    if ( ( (String) thisHT.get( currmethod.getSignature() )).equals(currlabel) )
                                    {
                                       invokedclass = new String("this_"+currmethod.getSignature());
                                       // } catch ( java.lang.RuntimeException e ) {}

                                       // actualinstanceHT = new HashMap();

                                       if ( ! (( (BitSet) instancetypesHT.get( invokedclass ) ) == null ) )
                                       actualinstanceBS = (BitSet) instancetypesHT.get( invokedclass );
                                       reachedclassHT = checkVirtualTables(actualinstanceBS, cs.getInvokeExpr().getMethod());
                                    }

                                 }
                                 catch ( java.lang.RuntimeException e ) {}

                                 tempdebug = invokedclass;
                                 // CHECK TO SEE IF CALL GRAPH EDGE CAN BE REMOVED

                                 if ( ( (( (String) reachedclassHT.get(reachedclass) ) == null ) ) && ( ! invokedclass.endsWith ( "[]" ) ) )
                                 {
                                    if ( ! ( ( size == 1 ) && ( invokedclass.equals ( "java.lang.Object" ) ) ) )
                                    {
                                       /*
                                                System.out.print("CALLED METHOD "+reachedclass+" "+method.getName());
                                                System.out.println(" REMOVED"+size);
                                       */
                                       menode.incomingedges--;
                                       menode.removeInvokingSite(cs);
                                       // if ( menode.incomingedges < 1 )
                                       // menode.isRedundant = true;

                                       totedges++;
                                       removededges++; reduction++;
                                       if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
                                       {
                                          benchtotedges++;
                                          benchremovededges++;
                                          benchreduction++;
                                       }

                                       try {
                                          //  System.out.println("CALL SITE : "+cs.getInvokeExpr().toString()+" BASE CLASS "+invokedclass+" "+thisInvoked);
                                       }
                                       catch ( NoInvokeExprException e ){}

                                       //   System.out.println(" OBTAINED SOLUTION FOR : "+tempdebug);

                                       //   System.out.println(" CURRENT CLASS AND METHOD : "+currclass.getName()+" "+currmethod.getName());

                                    }

                                 }
                                 // IF REACHEDCLASSHT.GET
                                 else
                                 {
                                    intactedges++;
                                    totedges++;
                                    if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
                                    {
                                       benchtotedges++;
                                       benchintactedges++;
                                    }

                                    filteredMethodNodes.add(menode);
                                    if ( (( String) aliveHT.get(menode.getMethod().getSignature())) == null )
                                    {
                                       alive++;
                                       aliveHT.put(menode.getMethod().getSignature(),menode.getMethod().getSignature());
                                    }

                                 }
                                 // ELSE
                                 /*
                                       System.out.println("THE SOLUTION : ");
                                   
                                       Object[] debugkeys = actualinstanceHT.keySet().toArray();
                                       for ( int i = 0 ; i < debugkeys.length ; i++ )
                                       System.out.println( (String)  actualinstanceHT.get(debugkeys[i]) );
                                 */
                              }
                              // IF REF
                              else
                              {
                                 totedges++;
                                 intactedges++;
                                 if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
                                 {
                                    benchtotedges++;
                                    benchintactedges++;
                                 }

                                 filteredMethodNodes.add(menode);
                                 if ( (( String) aliveHT.get(menode.getMethod().getSignature())) == null)
                                 {
                                    alive++;
                                    aliveHT.put(menode.getMethod().getSignature(),menode.getMethod().getSignature());
                                 }

                              }
                              // ELSE

                           }
                           catch ( java.lang.NullPointerException e ) {}

                        }
                        // WHILE MNITER

                     }
                     // IF !ISSTATIC

                     /*
                            System.out.println("");

                            if ( rtared[siteno-1] > reduction )
                            {

                             System.out.println ( cs.getInvokeExpr().toString() );

                             System.out.println("CAUTION : RTA DOES BETTER HERE ");

                            }
                     */
                     if ( reduction > 0)
                     {
                        //      System.out.println("REDUCTION FROM "+size+" TO "+(size - reduction));
                        //      System.out.println("");

                        if ( ( size - reduction ) == 1 )
                        {
                           polyred++;
                           if ( intflag )
                           intpolyred++;
                           else if ( virflag )
                           virpolyred++;
                           if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
                           {
                              benchpolyred++;
                              if ( intflag )
                              benchintpolyred++;
                              else if ( virflag )
                              benchvirpolyred++;
                           }

                           if ( size == 2 )
                           {
                              twored++;
                              if ( intflag )
                              inttwored++;
                              else if ( virflag )
                              virtwored++;
                              /*
                                       if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
                                        {
                                         benchtwored++;

                                         if ( intflag )
                                         benchinttwored++;
                                         else if ( virflag )
                                         benchvirtwored++;

                                        }

                              */
                           }

                           else if ( size == 3 )
                           {
                              threered++;
                              if ( intflag )
                              intthreered++;
                              else if ( virflag )
                              virthreered++;
                           }

                           else
                           {
                              morered++;
                              if ( intflag )
                              intmorered++;
                              else if ( virflag )
                              virmorered++;
                           }

                        }

                        else if ( ( size - reduction ) == 0 )
                        {
                           zerored++;
                           if ( size == 1 )
                           zonered++;
                           else if ( size == 2 )
                           ztwored++;
                           else if ( size == 3 )
                           zthreered++;
                           else
                           zmorered++;
                        }

                        else if ( ( size - reduction ) == 2 )
                        {
                           bired++;
                           if ( intflag )
                           intbired++;
                           else if ( virflag )
                           virbired++;
                           if ( size == 3 )
                           {
                              bthreered++;
                              if ( intflag )
                              intbthreered++;
                              else if ( virflag )
                              virbthreered++;
                           }

                           else
                           {
                              bmorered++;
                              if ( intflag )
                              intbmorered++;
                              else if ( virflag )
                              virbmorered++;
                           }

                        }
                        // ELSE IF SIZE - REDUCTION == 2

                        /*
                        	try {

                                 tempintactedges = tempintactedges + filteredMethodNodes.size();         
                                 tempnumincsHT++;
                                
                            	 csHT.put(currmethod.getSignature()+cs.getInvokeExpr().toString(), filteredMethodNodes);

                        	} catch ( java.lang.RuntimeException e ){}
                        */
                        /* COMMENTED OUT TEMPORARILY 
                              System.out.println("THE SOLUTION : ");

                              Object[] debugkeys = actualinstanceHT.keySet().toArray();
                              for ( int i = 0 ; i < debugkeys.length ; i++ )
                              System.out.println( (String)  actualinstanceHT.get(debugkeys[i]) );
                        */
                     }
                     // IF REDUCTION > 0

                     try {
                        tempintactedges = tempintactedges + filteredMethodNodes.size();
                        tempnumincsHT++;
                        csHT.put(currmethod.getSignature()+cs.getInvokeExpr().toString(), filteredMethodNodes);
                     }
                     catch ( java.lang.RuntimeException e ){}

                  }
                  catch ( java.lang.RuntimeException e ) {}

               }
               // WHILE CSITER

            }
            catch ( java.lang.NullPointerException e ) {}

         }
         // WHILE ITER

         /*
               System.out.println("TOTAL NUMBER OF EDGES IN CALL GRAPH = "+edges);
               System.out.println("TOTAL NUMBER OF POLYMORPHIC EDGES = "+polytotal);
               System.out.println("TOTAL NUMBER OF SITES = "+sites);
               System.out.println("TOTAL NUMBER OF INVOKEVIRTUAL SITES = "+virnum);
               System.out.println("TOTAL NUMBER OF INVOKESPECIAL SITES = "+spenum);
               System.out.println("TOTAL NUMBER OF INVOKESTATIC SITES = "+stanum);
               System.out.println("TOTAL NUMBER OF INVOKEINTERFACE SITES = "+intnum);
               System.out.println("TOTAL NUMBER OF INVOKEVIRTUAL SITES GOING TO 1 MTHD ORIGINALLY = "+virmono);
               System.out.println("TOTAL NUMBER OF INVOKEINTERFACE SITES GOING TO 1 MTHD ORIGINALLY = "+intmono);
               System.out.println("TOTAL NUMBER OF POLYMORPHIC SITES = "+polymorphicsites);
               System.out.println("TOTAL NUMBER OF SITES ( TO 2 MTHDS ) = "+twoedges);
               System.out.println("TOTAL NUMBER OF INVOKEINTERFACE SITES ( TO 2 MTHDS ) = "+inttwoedges);
               System.out.println("TOTAL NUMBER OF INVOKEVIRTUAL SITES ( TO 2 MTHDS ) = "+virtwoedges);
               System.out.println("TOTAL NUMBER OF SITES ( TO 3 MTHDS ) = "+threeedges);
               System.out.println("TOTAL NUMBER OF INVOKEINTERFACE SITES ( TO 3 MTHDS ) = "+intthreeedges);
               System.out.println("TOTAL NUMBER OF INVOKEVIRTUAL SITES ( TO 3 MTHDS ) = "+virthreeedges);
               System.out.println("TOTAL NUMBER OF SITES ( TO >3 MTHDS ) = "+moreedges);
               System.out.println("TOTAL NUMBER OF INVOKEINTERFACE SITES ( TO >3 MTHDS ) = "+intmoreedges);
               System.out.println("TOTAL NUMBER OF INVOKEVIRTUAL SITES ( TO >3 MTHDS ) = "+virmoreedges);
               System.out.println("TOTAL NUMBER OF EDGES REMOVED = "+removededges);
               System.out.println("TOTAL NUMBER OF BENCHMARK EDGES REMOVED = "+benchremovededges);

               System.out.println("NUMBER OF POLYMORPHIC SITES REDUCED TO 2 = "+bired);
               System.out.println("NUMBER OF INVOKEINTERFACE SITES REDUCED TO 2 = "+intbired);
               System.out.println("NUMBER OF INVOKEVIRTUAL SITES REDUCED TO 2 = "+virbired);
               System.out.println("TOTAL NUMBER OF SITES ( TO 3 MTHDS ) REDUCED TO 2 = "+bthreered);
               System.out.println("TOTAL NUMBER OF INVOKEINTERFACE SITES ( TO 3 MTHDS ) REDUCED TO 2 = "+intbthreered);
               System.out.println("TOTAL NUMBER OF INVOKEVIRTUAL ( TO 3 MTHDS ) REDUCED TO 2 = "+virbthreered);
               System.out.println("TOTAL NUMBER OF SITES ( TO >3 MTHDS ) REDUCED TO 2 = "+bmorered);
               System.out.println("TOTAL NUMBER OF INVOKEINTERFACE SITES ( TO >3 MTHDS ) REDUCED TO 2 = "+intbmorered);
               System.out.println("TOTAL NUMBER OF INVOKEVIRTUAL SITES ( TO >3 MTHDS ) REDUCED TO 2 = "+virbmorered);
               System.out.println("NUMBER OF POLYMORPHIC SITES REDUCED TO 1 = "+polyred);
                System.out.println("NUMBER OF BENCHMARK SITES REDUCED TO 1 = "+benchpolyred);

               System.out.println("NUMBER OF BENCHMARK INVOKEINTERFACE SITES REDUCED TO 1 = "+benchintpolyred);
               System.out.println("NUMBER OF BENCHMARK INVOKEVIRTUAL SITES REDUCED TO 1 = "+benchvirpolyred);
               System.out.println("TOTAL NUMBER OF SITES ( TO 2 MTHDS ) REDUCED TO 1 = "+twored);     
               System.out.println("TOTAL NUMBER OF INVOKEINTERFACE SITES ( TO 2 MTHDS ) REDUCED TO 1 = "+inttwored); 
               System.out.println("TOTAL NUMBER OF INVOKEVIRTUAL SITES ( TO 2 MTHDS ) REDUCED TO 1 = "+virtwored); 
               System.out.println("TOTAL NUMBER OF SITES ( TO 3 MTHDS ) REDUCED TO 1 = "+threered);
               System.out.println("TOTAL NUMBER OF INVOKEINTERFACE SITES ( TO 3 MTHDS ) REDUCED TO 1 = "+intthreered);
               System.out.println("TOTAL NUMBER OF INVOKEVIRTUAL SITES ( TO 3 MTHDS ) REDUCED TO 1 = "+virthreered);
               System.out.println("TOTAL NUMBER OF SITES ( TO >3 MTHDS ) REDUCED TO 1 = "+morered);
               System.out.println("TOTAL NUMBER OF INVOKEINTERFACE SITES ( TO >3 MTHDS ) REDUCED TO 1 = "+intmorered);
               System.out.println("TOTAL NUMBER OF INVOKEVIRTUAL SITES ( TO >3 MTHDS ) REDUCED TO 1 = "+virmorered);
               System.out.println("NUMBER OF SITES REDUCED TO 0 = "+zerored);
               System.out.println("TOTAL NUMBER OF SITES ( TO 1 MTHD ) REDUCED TO 0 = "+zonered);
               System.out.println("TOTAL NUMBER OF SITES ( TO 2 MTHDS ) REDUCED TO 0 = "+ztwored);    
               System.out.println("TOTAL NUMBER OF SITES ( TO 3 MTHDS ) REDUCED TO 0 = "+zthreered);
               System.out.println("TOTAL NUMBER OF SITES ( TO >3 MTHDS ) REDUCED TO 0 = "+zmorered);
               */
         DTAedgeRemovedTimer.end();
         DTAedgeRemovedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
         //   System.out.println("NUMBER OF ALIVE METHODS = "+alive);

         // System.out.println("TIME FOR DTA (EDGE REMOVAL ONLY) : "+DTAedgeRemovedTimer.getTime());

         // System.out.println("SPACE FOR DTA (EDGE REMOVAL ONLY) : "+DTAedgeRemovedMem);

         printRemainingMethods();
      }
      catch ( java.lang.NullPointerException e ){
         // System.err.println("\t------- NullPtr ERROR: Jimple can't handle " + reachedclass );

      }
      catch ( java.lang.RuntimeException e ){
         // System.err.println("\t ------ Runtime ERROR: Jimple can't handle " + reachedclass );

      }
      catch ( Throwable e ){
         // System.err.println("Throwable ERROR:" + e.getMessage() );

      }

      return callGraph;
   }


   private Map csHT = new HashMap();
   private Map aliveHT = new HashMap();
   private int alive = 0;
   HashMap checkHierarchy( Map instanceHT, SootMethod meth, SootMethod currmeth )
   {
      // NOT USED

      Object[] htkeys = instanceHT.keySet().toArray();
      HashMap answerHT = new HashMap();
      for ( int i = 0 ; i < htkeys.length ; i++ )
      {
         try {
            String className = (String) instanceHT.get(htkeys[i]);
            ClassNode cnode = clgb.getNode(className);
            SootMethod method = cagb.getSuperMethod( cnode, meth, currmeth );
            answerHT.put(method.getDeclaringClass().getName(),method.getDeclaringClass().getName());
         }
         catch (java.lang.RuntimeException e ) {}

      }

      return answerHT;
   }


   HashMap checkVirtualTables( BitSet instanceBS, SootMethod meth ) {
      /* THIS METHOD PERFORMS THE LOOKUP IN THE VIRTUALHT CREATED IN 
           CLASSGRAPHBUILDER

           RETURNS A HASHTABLE CONTAINING CLASSES WHICH DECLARE A METHOD 
           NAMED METH AND ARE DETERMINED TO BE POTENTIAL TARGETS BY DECLARED 
           TYPE ANALYSIS     
        */
      // USE THE RESULT FROM DECLARED TYPE ANALYSIS 

      // Object[] htkeys = instanceHT.keySet().toArray();

      StringBuffer buffer = new StringBuffer();
      buffer.append(meth.getName());
      buffer.append("(");
      Iterator typeIt = meth.getParameterTypes().iterator();
      if(typeIt.hasNext())
      {
         buffer.append(typeIt.next());
         while(typeIt.hasNext())
         {
            buffer.append(",");
            buffer.append(typeIt.next());
         }

      }

      buffer.append(")");
      buffer.append(":" + meth.getReturnType().toString());
      HashMap answerHT = new HashMap(10, 0.7f );
      // for ( int i = 0 ; i < htkeys.length ; i++ )
      // {

      for ( int i = 0 ; i < /* htkeys.length */ instanceBS.size(); i++ )
      {
         try {
            // String className = (String) instanceHT.get(htkeys[i]);

            String className = null;
            // System.out.println ( " ORIG CLASS " + className );

            if ( instanceBS.get(i) )
            {
               className = ( String ) inversebitMap.get ( new Integer ( i ) );
               HashMap stm = ( HashMap ) clgb.virtualHT.get(className);
               String reachedclass = ( String ) stm.get(buffer.toString() );
               // System.out.println ( " REACHED CLASS " + reachedclass );

               answerHT.put(reachedclass, reachedclass);
            }

         }
         catch (java.lang.RuntimeException e ) {}

      }

      return answerHT;
   }


   void ReplaceStrConnComp(){
      /* THIS METHOD EXAMINES THE CONSTRAINT GRAPH, DETECTS THE SCCs IN THE GRAPH
            AND REPLACES THEM BY A SPECIAL CGSCC NODE AND UPDATES THE EDGES AND 
            INSTANCE TYPES SETS OF EACH NODE TO ACCOUNT FOR THE CHANGE IN THE 
            STRUCTURE OF THE CONSTRAINT GRAPH

            THE CONSTRAINT GRAPH IS A DAG AFTER THIS METHOD RETURNS

            CALLED BY SOLVECONSTRAINTS()

            USES THE SCCDETECTOR CLASS IN THE SIDEEFFECT PACKAGE

          */
      // System.out.println("COMPUTING STRONGLY CONNECTED COMPONENTS");

      SCCDetector sccd = new SCCDetector();
      SCCs = sccd.computeSCCs( declaredtypesHT.values() );
      // System.out.println("COMPUTED STRONGLY CONNECTED COMPONENTS");

      System.out.println();
      System.out.println("ORIGINAL NUMBER OF NODES IN CONSTRAINT GRAPH = "+sccd.Originalnodes);
      System.out.println("ORIGINAL NUMBER OF EDGES IN CONSTRAINT GRAPH = "+sccd.Originaledges);
      Iterator iter = SCCs.iterator();
      while( iter.hasNext() )
      {
         List aSCC = (List)iter.next();
         if ( aSCC.size() > 1 )
         //     System.out.println("SCC : "+aSCC.size());

         if ( aSCC.size() > 1 )
         {
            CGSCCNode aCGSCCNode = new CGSCCNode( aSCC );
            aCGSCCNode.updateLinks(declaredtypesHT);
            declaredtypesHT.put( aCGSCCNode.getTypeName() , aCGSCCNode );
         }

      }

   }


   void printRemainingMethods() {
      /* CALCULATES THE NUMBER OF METHODS THAT MIGHT BE CALLED ( ALIVE )
           AS A RESULT OF PERFORMING DTA AND PRINTS OUT THE NUMBER */
      alive = 0;
      int benchalive = 0;
      int clinitnum = 0;
      int benchclinitnum = 0;
      Iterator iter = cagb.getCallGraph().iterator();
      while ( iter.hasNext() )
      {
         MethodNode mn = ( MethodNode ) iter.next();
         String currclassname = mn.getMethod().getDeclaringClass().getName();
         if ( mn.incomingedges > 0 )
         {
            alive++;
            if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
            benchalive++;
         }

         else
         {
            // System.out.println ( "DEAD "+mn.getMethod().getSignature() );

            if ( ( mn.getMethod().getName().equals("<clinit>") ) || ( mn.getMethod().getName().equals("finalize") ) )
            {
               clinitnum++;
               if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
               benchclinitnum++;
            }

            for( Iterator runMethodIter = clgb.runmethods.iterator() ;
            runMethodIter.hasNext() ; )
            {
               SootMethod method = (SootMethod)runMethodIter.next();
               if ( method.getSignature().equals ( mn.getMethod().getSignature() ) )
               {
                  clinitnum++;
                  if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
                  benchclinitnum++;
               }

            }

            for( Iterator startMethodIter = clgb.startmethods.iterator() ;
            startMethodIter.hasNext() ; )
            {
               SootMethod method = (SootMethod) startMethodIter.next();
               if ( method.getSignature().equals ( mn.getMethod().getSignature() ) )
               {
                  clinitnum++;
                  if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
                  benchclinitnum++;
               }

            }

         }

      }

      /*
        System.out.println("NUMBER OF ALIVE METHODS = "+alive);

        System.out.println("NUMBER OF ALIVE BENCHMARK METHODS = "+benchalive);
        
        System.out.println("NUMBER OF CLINIT METHODS = "+clinitnum);

        System.out.println("NUMBER OF BENCHMARK CLINIT METHODS = "+benchclinitnum );
        */
   }


}




