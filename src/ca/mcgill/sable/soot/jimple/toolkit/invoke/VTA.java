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

public class VTA {
	
	SootClassManager cm;

	private ArrayList bclasses;

	List SCCs;

	private Map declaredtypesHT = new HashMap();

	private Map instancetypesHT = new HashMap();

//        private Map clinitHT = new HashMap();

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

	private boolean arraylside;

	private boolean arrayrside;

	private boolean arraybothsides;

	private boolean staticfield;

	private boolean virflag;
 
	private boolean intflag;

	private int virnum = 0,intnum = 0,spenum = 0,stanum = 0,virmono = 0,intmono = 0;

	static Timer VTATimer = new Timer();
       
	static long VTAMem;

	static Timer VTAedgeRemovedTimer = new Timer();

	static long VTAedgeRemovedMem;

        private HashMap bitMap = new HashMap();

        private HashMap inversebitMap = new HashMap();

//        private HashMap nodesbitMap = new HashMap();

//        private HashMap nodesinversebitMap = new HashMap(); 

       private HashMap methodsigHT = new HashMap();








  public void constructBitMap( Map allclassHT ) {

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





  public int nodesindex = 0;


  private RTA rta;





  public void initializeConstraintGraph(RTA rta ) {

  /* THIS METHOD INITIALIZES THE NODES IN THE CONSTRAINT GRAPH, EVERY
     NODE THAT COULD BE REQUIRED WHEN THE STATEMENTS IN A METHOD ARE 
     ANALYSED IS CREATED AT THIS STAGE ITSELF */

  this.rta = rta;

  VTATimer.start();

  System.out.println("");
  
  System.out.println("Performing Variable Type Analysis.....");

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

   List fields = currclass.getFields();

   Iterator fieldIt = fields.iterator();

   // ADD FULL VARIABLE NAME OF EACH REFTYPE FIELD IN EACH CLASS

   while(fieldIt.hasNext())
   {    

    try{

     SootField currfield = ( SootField ) fieldIt.next();

     currlabel = currfield.getSignature(); 
	    
     Type currtype = currfield.getType();

     currtype.apply( new TypeSwitch(){
	
      public void caseRefType( RefType r ){
	 
       if ( ((TypeNode) declaredtypesHT.get(currlabel)) == null )
       {

        TypeNode t = new TypeNode(currlabel, bitMap, inversebitMap /* , nodesbitMap, nodesinversebitMap */ );

        declaredtypesHT.put(currlabel, t );

        // Integer ii = new Integer ( nodesindex++ );

        // nodesbitMap.put ( t, ii );

        // nodesinversebitMap.put ( ii, t );

       }
 
      }        

      public void caseArrayType( ArrayType r ){

       BaseType currbtype = r.baseType;
		
       currbtype.apply( new TypeSwitch(){

	public void caseRefType( RefType r1 ){
		
	 if ( ((TypeNode) declaredtypesHT.get(currlabel)) == null )
         {         

          TypeNode t = new TypeNode(currlabel, bitMap, inversebitMap /* ,nodesbitMap, nodesinversebitMap */ );

          declaredtypesHT.put(currlabel, t );
/*
          Integer ii = new Integer ( nodesindex++ );

          nodesbitMap.put ( t, ii );

          nodesinversebitMap.put ( ii, t );
*/

         }

	}   

       });
	 
      }

     });
 
    } catch ( java.lang.RuntimeException e ){}
     
   } // WHILE FIELDITER                                                           

  } // WHILE IT 



  Collection callGraph = cagb.getCallGraph();
     
  Iterator methodIt = callGraph.iterator();
           
  while ( methodIt.hasNext() )
  {         
       
   try {
         
    MethodNode tempMN = (MethodNode) methodIt.next();

    currmethod = tempMN.getMethod();

     // ADD NODE FOR RETURN_METHODSIGNATURE IF THE RETURN IS A REFTYPE 

     Type currreturntype = (Type) currmethod.getReturnType(); 
	 
     currreturntype.apply( new TypeSwitch(){
	       
      public void caseRefType( RefType r ) {
  
      // MEMORY

    //  declaredtypesHT.put("return_"+currmethod.getSignature(), new TypeNode("return_"+currmethod.getSignature(), bitMap));
 
      }

     public void caseArrayType( ArrayType r ) {

      BaseType currbtype = r.baseType;
      
      currbtype.apply( new TypeSwitch(){

       public void caseRefType( RefType r1 ){
	 
	// MEMORY

       // declaredtypesHT.put("return_"+currmethod.getSignature(), new TypeNode("return_"+currmethod.getSignature(), bitMap));

       }

      });

     }

    });

 //    declaredtypesHT.put("return_"+currmethod.getSignature(), new TypeNode("return_"+currmethod.getSignature(), bitMap, inversebitMap ));

     if ( ((TypeNode) declaredtypesHT.get("return_"+currmethod.getSignature())) == null )
     {         

          TypeNode t = new TypeNode("return_"+currmethod.getSignature(), bitMap, inversebitMap /* , nodesbitMap, nodesinversebitMap */ );

          declaredtypesHT.put("return_"+currmethod.getSignature(), t );
/*
          Integer ii = new Integer ( nodesindex++ );

          nodesbitMap.put ( t, ii );

          nodesinversebitMap.put ( ii, t );
*/

     }

     // ADD NODE FOR THIS_METHODSIGNATURE

     if ( ((TypeNode) declaredtypesHT.get("this_"+currmethod.getSignature())) == null )
     {         

          TypeNode t = new TypeNode("this_"+currmethod.getSignature(), bitMap, inversebitMap /* , nodesbitMap, nodesinversebitMap */ );

          declaredtypesHT.put("this_"+currmethod.getSignature(), t );
/*
          Integer ii = new Integer ( nodesindex++ );

          nodesbitMap.put ( t, ii );

          nodesinversebitMap.put ( ii, t );
*/

     }


     // declaredtypesHT.put("this_"+currmethod.getSignature(), new TypeNode("this_"+currmethod.getSignature(), bitMap, inversebitMap ));
     
     JimpleBody listBody = Jimplifier.getJimpleBody( currmethod );

     List locals = listBody.getLocals();
	
     Iterator localIt = locals.iterator();

     // ADD FULL VARIABLE NAME OF EACH REFTYPE LOCAL IN EACH METHOD

     while(localIt.hasNext())
     {
	  
      try{ 
	  
       Local currlocal = ( Local ) localIt.next();

       currlabel = currmethod.getSignature()+currlocal.getName();
	  
       Type currtype = currlocal.getType();

       currtype.apply( new TypeSwitch(){

	public void caseRefType( RefType r ){
/*
	 if ( ((TypeNode) declaredtypesHT.get(currlabel)) == null )
	 declaredtypesHT.put(currlabel, new TypeNode(currlabel, bitMap, inversebitMap ) );
*/
	 if ( ((TypeNode) declaredtypesHT.get(currlabel)) == null )
         {         

          TypeNode t = new TypeNode(currlabel, bitMap, inversebitMap /*, nodesbitMap, nodesinversebitMap */ );

          declaredtypesHT.put(currlabel, t );
/*
          Integer ii = new Integer ( nodesindex++ );

          nodesbitMap.put ( t, ii );

          nodesinversebitMap.put ( ii, t );
*/

         }



	}
   
	public void caseArrayType( ArrayType r ){
		
	 BaseType currbtype = r.baseType;
	      
	 currbtype.apply( new TypeSwitch(){

	  public void caseRefType( RefType r1 ){
/*	
	   if ( ((TypeNode) declaredtypesHT.get(currlabel)) == null )
	   declaredtypesHT.put(currlabel, new TypeNode(currlabel, bitMap, inversebitMap ) ); 
*/

	 if ( ((TypeNode) declaredtypesHT.get(currlabel)) == null )
         {         

          TypeNode t = new TypeNode(currlabel, bitMap, inversebitMap /* , nodesbitMap, nodesinversebitMap */ );

          declaredtypesHT.put(currlabel, t );
/*
          Integer ii = new Integer ( nodesindex++ );

          nodesbitMap.put ( t, ii );

          nodesinversebitMap.put ( ii, t );
*/

         }




	  }

	 });

	}  
 
       });

     } catch ( java.lang.RuntimeException e ){}

    } // WHILE LOCALIT   

   } catch (java.lang.RuntimeException e ){}

  } // WHILE METHODIT
 
 // }  WHILE ITER ---- REMOVED THIS IN CHANGE ON 25 JAN

  // KEEP TRACK OF LOCALS THAT ARE ACTUALLY A REFERENCE TO "THIS"
 
  System.out.println("Done");

  collectThisVars();

 }













  public void adjustForFinalize() {

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
    
    } catch ( java.lang.RuntimeException e ) {}

   }

  }
 





























  int numinvokes = 1;






  public void analyseStatements() {

  /* THIS METHOD ANALYSES EACH STATEMENT IN EACH METHOD IN THE CALL GRAPH
     AND ADDS EDGES, INSTANCE TYPES TO THE CONSTRAINT GRAPH  

     IT CALLS ANALYSECALLSITES TO ANALYSE CALLSITES 
  */

  adjustForFinalize();

  System.out.println("");

  Collection callGraph = cagb.getCallGraph();

  int mnum = 0;
  
  System.out.print("Variable Type Analysis collecting constraints for ordinary statements");

  Iterator iter = callGraph.iterator();
   
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
	  
    // System.out.println ("CURRMETHOD "+currmethod.getSignature() );

    currclass = currmethod.getDeclaringClass();
  
    try {
  
     JimpleBody listBody = Jimplifier.getJimpleBody( currmethod );
	
     Iterator stmtIter = listBody.getStmtList().iterator();

     while ( stmtIter.hasNext() )
     {

      Stmt stmt = null;

      try {

       stmt = (Stmt)stmtIter.next();

       stmt.apply( new AbstractStmtSwitch(){

        public void caseInvokeStmt( InvokeStmt s){

         numinvokes++;

        }

	public void caseReturnStmt(ReturnStmt s){

	 if ( s.getReturnValue() instanceof Local ) {

	  Type type = s.getReturnValue().getType();

	  currlabel = currmethod.getSignature()+( (Local) s.getReturnValue() ).getName();

	  type.apply( new TypeSwitch(){

	   public void caseRefType( RefType r ){

	    if ( !(((TypeNode) declaredtypesHT.get(currlabel)) == null ))
	    {
	   
	     TypeNode rtn = (TypeNode) declaredtypesHT.get( currlabel );

	     TypeNode ltn = (TypeNode) declaredtypesHT.get(new String("return_"+currmethod.getSignature()));
	    
/*
    System.out.println("");
    System.out.println("ADDED EDGE BETWEEN "+rtn.getTypeName()+" AND
"+ltn.getTypeName()+" : RETURN STMT");
    System.out.println("");
*/
	     rtn.addForwardNode(ltn);

	     ltn.addBackwardNode(rtn);

	     try {

	      if ( r.className.equals(new String("java.lang.Object") ) )
	      {

	       ltn.addForwardNode(rtn);

	       rtn.addBackwardNode(ltn);

	      }

	     } catch ( java.lang.RuntimeException e ) {}

	  declaredtypesHT.put(ltn.getTypeName(), ltn );

	  declaredtypesHT.put(rtn.getTypeName(),rtn);

	 }

	}

	public void caseArrayType( ArrayType r ){
 
	 BaseType currbtype = r.baseType;
    
	 currbtype.apply( new TypeSwitch(){
 
	  public void caseRefType( RefType r1 ){
	   
	   if ( !(((TypeNode) declaredtypesHT.get(currlabel)) == null ))
	   {
       
	    TypeNode rtn = (TypeNode) declaredtypesHT.get( currlabel );

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

	 } 

	});

       }

      }); 

     } // IF S.GETRETURNVALUE
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

     } // ELSE

    } // CASERETURNSTMT 

    public void caseAssignStmt(AssignStmt s){

     currLlabel = getCorrectLeftLabel(s);

     if( s.getRightOp() instanceof NewExpr )
     {

      currrtype = ((NewExpr)s.getRightOp()).getBaseType();

      Type type = s.getLeftOp().getType();

      type.apply( new TypeSwitch(){

       public void caseRefType( RefType r ){
	 
	if ( !(((TypeNode) declaredtypesHT.get(currLlabel)) == null ))
	{
/*
	    System.out.println("ADDED INSTANCE TYPE "+currrtype.className+" TO
DECLARED TYPE "+currLlabel);
*/

	 TypeNode tn = (TypeNode) declaredtypesHT.get(currLlabel);

	 tn.addInstanceType(currrtype.className);

	 declaredtypesHT.put(currLlabel, tn );

	}

       }

       public void caseArrayType( ArrayType r ){
	  
	BaseType currbtype = r.baseType;
       
	currbtype.apply( new TypeSwitch(){
	   
	 public void caseRefType( RefType r1 ){
	   
	  if ( !(((TypeNode) declaredtypesHT.get(currLlabel)) == null ))
	  {
	   
	   TypeNode tn = (TypeNode) declaredtypesHT.get( currLlabel );

	   tn.addInstanceType(currrtype.className);
	
	   declaredtypesHT.put(currLlabel, tn );

	  }

	 }

	});

       }

      });

     } // IF S.GETRIGHTOP() INSTANCEOF NEWEXPR
     else 
     {

      if( s.getRightOp() instanceof NewArrayExpr )
      {

       try {
	 
	currrtype = (RefType) ((NewArrayExpr)s.getRightOp()).getBaseType();

	Type type = s.getLeftOp().getType();
	   
	type.apply( new TypeSwitch(){

	 public void caseArrayType( ArrayType r ){
	    
	  if ( !(((TypeNode) declaredtypesHT.get(currLlabel)) == null) )
	  {

	   TypeNode tn = (TypeNode) declaredtypesHT.get(currLlabel);

	   tn.addInstanceType(currrtype.className);

	   declaredtypesHT.put(currLlabel, tn );

	  }

	 }

	});
	   
       } catch(java.lang.RuntimeException e){}

      } // IF S.GETRIGHTOP() INSTANCEOF NEWARRAYEXPR
      else 
      {

       if( s.getRightOp() instanceof NewMultiArrayExpr )
       {

	try {

	  currrtype = (RefType) ((ArrayType) ((NewMultiArrayExpr)s.getRightOp()).getBaseType()).baseType;
	  
	  Type type = s.getLeftOp().getType();
		     
	  type.apply( new TypeSwitch(){

	   public void caseArrayType( ArrayType r ){
	   
	    if ( !(((TypeNode) declaredtypesHT.get(currLlabel)) == null ))
	    { 
/*
	    System.out.println("ADDED MULTIDIMENSIONAL ARRAY INSTANCE TYPE
"+currrtype.className+" TO DECLARED TYPE "+currLlabel);
*/
	   
	     TypeNode tn = (TypeNode) declaredtypesHT.get(currLlabel);

	     tn.addInstanceType(currrtype.className);
	 
	     declaredtypesHT.put(currLlabel, tn );

	    }

	   }

	  });

	 } catch (java.lang.RuntimeException e) {}

	} // IF S.GETRIGHTOP() INSTANCEOF NEWMULTIARRAYEXPR 
	else 
	{
	
	 if ( s.getRightOp() instanceof InstanceOfExpr )
	 {

	  InstanceOfExpr ioexpr = ( InstanceOfExpr ) s.getRightOp();

	  if ( ioexpr.getOp() instanceof Constant )
	  {
 
        //   System.out.println ( "REACHED INSTANCEOF CONSTT " );

	   Type type = ioexpr.getOp().getType();
	  
       //   System.out.println ( "REACHED INSTANCEOF CONSTT 1 " );


	   type.apply( new TypeSwitch(){

	    public void caseRefType( RefType r ){
	  
	     if ( !(((TypeNode) declaredtypesHT.get(currLlabel)) == null ))
	     {   
       
	      TypeNode tn = (TypeNode) declaredtypesHT.get(currLlabel);
/*
    System.out.println("");
    System.out.println("ADDED INSTANCE TYPE "+r.className+" TO DECLARED TYPE
"+tn.getTypeName()+" : INSTANCEOF STMT (CONSTT) ");   

    System.out.println("");
*/

	      tn.addInstanceType(r.className);
	  
	      declaredtypesHT.put(tn.getTypeName(), tn );

	     }
	  
	    }
	   
	   });

	  } // IF IOEXPR.GETOP() INSTANCEOF CONSTANT
	  else 
	  {
	  
        //   System.out.println ( "REACHED INSTANCEOF LOCAL " );

	   Type ltype = s.getLeftOp().getType();
	  
	   try {

	    currRlabel = currmethod.getSignature()+( (Local) ioexpr.getOp()).getName();
	   
	    Type Rrtype = ioexpr.getOp().getType();

        //  System.out.println ( "REACHED INSTANCEOF LOCAL 1 " );

	    Rrtype.apply( new TypeSwitch(){

	     public void caseNullType( NullType u ){

	      nullflag = true;

	     }

	    });

	    currRrtype = ( RefType ) Rrtype; 

	   } catch ( java.lang.ClassCastException e1) {}


	   ioexpr.getOp().getType().apply( new TypeSwitch(){

	    public void caseRefType( RefType r ){

          //  System.out.println ( "REACHED INSTANCEOF LOCAL 2 " );
	  
	     if ( !(nullflag) )
	     { 

	      if ( !(((TypeNode) declaredtypesHT.get(currRlabel)) == null ))
	      {
	
	       TypeNode rtn = (TypeNode) declaredtypesHT.get( currRlabel );
	 
	       TypeNode ltn = (TypeNode) declaredtypesHT.get( currLlabel );

           //  System.out.println ( "REACHED INSTANCEOF LOCAL 21 " );
	    
           //    System.out.println ( "CURRRLABEL "+currRlabel );

               //    System.out.println ( "CURRLLABEL "+currLlabel );
               

	       if (  !((currRlabel).equals(currLlabel)) )
	       {
          
             //     System.out.println ( "REACHED INSTANCEOF LOCAL 31 " );

 /*
    System.out.println("");
    System.out.println("ADDED EDGE BETWEEN "+currRlabel+" AND "+currLlabel+" :
INSTANCEOF STMT ");
    System.out.println("");
 */       
		rtn.addForwardNode(ltn);
	
		ltn.addBackwardNode(rtn);

        //      System.out.println ( "REACHED INSTANCEOF LOCAL 311 " );


		try {
       
		 if  ( ( r.className.equals(new String("java.lang.Object") ) ) || ( arraylside || arrayrside ) )
		 {

		  ltn.addForwardNode(rtn);
	   
		  rtn.addBackwardNode(ltn);
 
		 }
       
		} catch ( java.lang.RuntimeException e ){}
	    
        //       System.out.println ( "REACHED INSTANCEOF LOCAL 32 " );

		declaredtypesHT.put(ltn.getTypeName(), ltn );

        //        System.out.println ( "REACHED INSTANCEOF LOCAL 33 " );

		declaredtypesHT.put(rtn.getTypeName(), rtn);

	       } //IF !CURRRLABEL
	 
	      }
  
	     } // IF !NULLFLAG

	    }  
	 
	    public void caseArrayType( ArrayType r ){

             System.out.println ( "REACHED INSTANCEOF LOCAL 3 " );

	     BaseType currbtype = r.baseType;
    
	     currbtype.apply( new TypeSwitch(){

	      public void caseRefType( RefType r1 ){

	       if ( !(nullflag) )
	       {

		if ( !(((TypeNode) declaredtypesHT.get(currRlabel)) == null ))
		{
	    
                 System.out.println ( "REACHED INSTANCEOF LOCAL 22 " );
             
		 TypeNode rtn = (TypeNode) declaredtypesHT.get( currRlabel );
	  
		 TypeNode ltn = (TypeNode) declaredtypesHT.get( currLlabel );
		 
		 if (  !((currRlabel).equals(currLlabel)) )   
		 {
/*
    System.out.println("");
    System.out.println("ADDED EDGE BETWEEN "+currRlabel+" AND "+currLlabel+" :
INSTANCEOF STMT ( ARRAY )");
  */       
		  rtn.addForwardNode(ltn);
	
		  ltn.addBackwardNode(rtn);

		  if (( arrayrside ) || ( arraylside ) ) 
		  {

		   ltn.addForwardNode(rtn);

		   rtn.addBackwardNode(ltn);

 /*  System.out.println("ADDED EDGE BETWEEN "+currLlabel+" AND "+currRlabel+" :
INSTANCEOF STMT ( ARRAY )");  
	  System.out.println("PAIRED"); */

		   }
	
		   declaredtypesHT.put(ltn.getTypeName(), ltn );

		   declaredtypesHT.put(rtn.getTypeName(), rtn);

		  } // IF !CURRRLABEL
	  
		 }

		} // IF !NULLFLAG

	       }

	      });

	     }  

	    });

	   } // ELSE


      //  System.out.println ( "REACHED END OF INSTANCEOF " );


	  } // IF S.GETRIGHTOP() INSTANCEOF INSTANCEOFEXPR  
	  else 
	  {
	
	   nullflag = false;
	
	   if ( s.getRightOp() instanceof CastExpr )
	   {

	    CastExpr caexpr = ( CastExpr ) s.getRightOp();

	    if ( caexpr.getOp() instanceof Constant )
	    {

	     Type type = caexpr.getOp().getType();
	  
	     type.apply( new TypeSwitch(){

	      public void caseRefType( RefType r ){
	  
	       if ( !(((TypeNode) declaredtypesHT.get(currLlabel)) == null ))
	       {   
       
		TypeNode tn = (TypeNode) declaredtypesHT.get(currLlabel);

/*
    System.out.println("");
    System.out.println("ADDED INSTANCE TYPE "+r.className+" TO DECLARED TYPE
"+tn.getTypeName()+" : CLASSCAST STMT (CONSTT) ");   

    System.out.println("");
  */ 
		tn.addInstanceType(r.className);
	  
		declaredtypesHT.put(tn.getTypeName(), tn );

	       } // IF
	  
	      }
	   
	     });

	    } // IF CAEXPR.GETOP() INSTANCEOF CONSTANT
	    else 
	    {
	  
	     Type ltype = s.getLeftOp().getType();
	  
	     try {

	      currRlabel = currmethod.getSignature()+( (Local) caexpr.getOp()).getName();
	   
	      Type Rrtype = caexpr.getOp().getType();

	      Rrtype.apply( new TypeSwitch(){

	       public void caseNullType( NullType u ){

		nullflag = true;

	       }

	      });

	      currRrtype = ( RefType ) Rrtype;

	     } catch ( java.lang.ClassCastException e1) {}
	  
	     caexpr.getOp().getType().apply( new TypeSwitch(){
 
	      public void caseRefType( RefType r ){
	   
	       if ( !(nullflag) )
	       { 

		if ( !(((TypeNode) declaredtypesHT.get(currRlabel)) == null ))
		{
	
		 TypeNode rtn = (TypeNode) declaredtypesHT.get( currRlabel );
	 
		 TypeNode ltn = (TypeNode) declaredtypesHT.get( currLlabel );
	   
		 if (  !((currRlabel).equals(currLlabel)) )
		 {
 /*
    System.out.println("");
    System.out.println("ADDED EDGE BETWEEN "+currRlabel+" AND "+currLlabel+" :
CLASSCAST STMT ");
    System.out.println("");
   */     
		  rtn.addForwardNode(ltn);
	
		  ltn.addBackwardNode(rtn);

		  try {

		   if  ( ( r.className.equals(new String("java.lang.Object") ) && currRrtype.equals(new String("java.lang.Object") ) )  || ( arraylside || arrayrside ) )
		   {

		    ltn.addForwardNode(rtn);
	 
		    rtn.addBackwardNode(ltn);

		   }

		  } catch ( java.lang.RuntimeException e ){}
	   
		  declaredtypesHT.put(ltn.getTypeName(), ltn );

		  declaredtypesHT.put(rtn.getTypeName(), rtn);

		 } // IF !CURRRLABEL
	 
		}

	       } // IF !NULLFLAG

	      } 
 

	      public void caseArrayType( ArrayType r ){

	       BaseType currbtype = r.baseType;
    
	       currbtype.apply( new TypeSwitch(){

		public void caseRefType( RefType r1 ){

		 if ( !(nullflag) )
		 {

		  if ( !(((TypeNode) declaredtypesHT.get(currRlabel)) == null ))
		  {
	    
		   TypeNode rtn = (TypeNode) declaredtypesHT.get( currRlabel );
	  
		   TypeNode ltn = (TypeNode) declaredtypesHT.get( currLlabel );
	
		   if (  !((currRlabel).equals(currLlabel)) )   
		   {
/*
    System.out.println("");
    System.out.println("ADDED EDGE BETWEEN "+currRlabel+" AND "+currLlabel+" :
CLASSCAST STMT ( ARRAY )");
  */       

		    rtn.addForwardNode(ltn);
	
		    ltn.addBackwardNode(rtn);

		    if (( arrayrside ) || ( arraylside ) ) 
		    {

		     ltn.addForwardNode(rtn);

		     rtn.addBackwardNode(ltn);

		     /* System.out.println("ADDED EDGE BETWEEN "+currLlabel+" AND
"+currRlabel+" : CLASSCAST STMT ( ARRAY )");  */

		     }
	
		     declaredtypesHT.put(ltn.getTypeName(), ltn );

		     declaredtypesHT.put(rtn.getTypeName(), rtn);

		    } // IF !CURRRLABEL
	  
		   }

		  } // IF !NULLFLAG 

		 }

		});

	       }  

	      });

	     } // ELSE

	   } // IF S.GETRIGHTOP() INSTANCEOF CASTEXPR 
	   else 
	   {

	    if ( s.getRightOp() instanceof InvokeExpr )
	    {

	     try {

	      InvokeExpr invexpr = ( InvokeExpr ) s.getRightOp();

	      boolean search = true;

	      MethodNode currmnode = cagb.getNode( currmethod );

/*
// MARCH 1     Iterator CSiter = currmnode.getCallSites().iterator();
      
	      // CallSite cs = new CallSite();
  
              CallSite cs = null;

              try {
    
                cs = currmnode.getCallSite ( invexpr );

                search = false;

              } catch ( java.lang.RuntimeException e ) {}


*/

 // EFFICIENCY

             CallSite cs = currmnode.getCallSite ( new Integer( numinvokes ) );



/*       MARCH 1

	      while (( CSiter.hasNext() )&&(search == true) )
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

	      Set possMethodNodes = cs.getMethods();
	 
	      Iterator MNiter = possMethodNodes.iterator();
	
	      while ( MNiter.hasNext() )
	      {  

	       try {

		SootMethod meth = ((MethodNode) MNiter.next()).getMethod();
	 
		reachedmethod = meth.getSignature();

		reachedclass = (meth.getDeclaringClass()).getName();

		try {

		 currLrtype = ( RefType )s.getLeftOp().getType();

		} catch ( java.lang.RuntimeException e ) {}

		Type ltype = s.getLeftOp().getType();
	  
		meth.getReturnType().apply( new TypeSwitch(){

		 public void caseRefType( RefType r ){
       
		  TypeNode rtn = (TypeNode) declaredtypesHT.get( new String("return_"+reachedmethod) );
     
		  TypeNode ltn = (TypeNode) declaredtypesHT.get(currLlabel);
 /*
    System.out.println("");
    System.out.println("ADDED EDGE BETWEEN "+rtn.getTypeName()+" AND
"+ltn.getTypeName()+" : ASSIGN STMT (METHOD RETURN)");
    System.out.println("");
   */

		  rtn.addForwardNode(ltn);
	  
		  ltn.addBackwardNode(rtn);

		  try {
	  
		   if ( ( r.className.equals(new String("java.lang.Object") ) && ((RefType) currLrtype).equals(new String("java.lang.Object") ) ) || arraylside )
		   {

		    ltn.addForwardNode(rtn);
	 
		    rtn.addBackwardNode(ltn);

		   }

		  } catch ( java.lang.RuntimeException e ){}
    
		  declaredtypesHT.put(ltn.getTypeName(), ltn );
 
		  declaredtypesHT.put(rtn.getTypeName(), rtn);

		 }
    
		 public void caseArrayType( ArrayType r ){
    
		  BaseType currbtype = r.baseType;
    
		  currbtype.apply( new TypeSwitch(){

		   public void caseRefType( RefType r1 ){
	  
		    TypeNode rtn = (TypeNode) declaredtypesHT.get( new String("return_"+reachedmethod) );
	  
		    TypeNode ltn = (TypeNode) declaredtypesHT.get(currLlabel);
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

	       } catch ( java.lang.RuntimeException e ){}
     
	      }

	     } catch ( java.lang.RuntimeException e ){}

             numinvokes++;

	    } // IF S.GETRIGHTOP() INSTANCEOF INVOKEEXPR
	    else
	    {

	     nullflag = false;

	     if ( s.getRightOp() instanceof Constant )
	     {
	   
	      Type type = ((Constant)s.getRightOp()).getType();
	
	      type.apply( new TypeSwitch(){

	       public void caseRefType( RefType r ){
	   
		if ( !(((TypeNode) declaredtypesHT.get(currLlabel)) == null ))
		{
	 
		 TypeNode tn = (TypeNode) declaredtypesHT.get(currLlabel);
       
  /*
    System.out.println("");
    System.out.println("ADDED INSTANCE TYPE "+r.className+" TO DECLARED TYPE
"+tn.getTypeName()+" : ASSIGN STMT (CONSTANT) ");
    System.out.println("");
    */    
		 tn.addInstanceType(r.className);
	
		 declaredtypesHT.put(tn.getTypeName(), tn );

		}

	       }
	
	      });
	 
	     } // IF S.GETRIGHTOP() INSTANCEOF CONSTANT
	     else 
	     {

	      Type ltype = s.getLeftOp().getType();

	      try {

	       currRlabel = getCorrectRightLabel(s);

	       Type Rrtype = s.getRightOp().getType();
	       
	       Rrtype.apply( new TypeSwitch(){

		public void caseNullType( NullType u ){

		 nullflag = true;

		}

	       });

	       currRrtype = ( RefType ) Rrtype;

	      } catch ( java.lang.ClassCastException e1) {}
     

	      ltype.apply( new TypeSwitch(){

	       public void caseRefType( RefType r ){
    
		if ( !(nullflag) )
		{

		 if ( !(((TypeNode) declaredtypesHT.get(currRlabel)) == null ))
		 {
 
		  TypeNode rtn = (TypeNode) declaredtypesHT.get( currRlabel );
	
		  TypeNode ltn = (TypeNode) declaredtypesHT.get( currLlabel );

		  if (  !((currLlabel).equals(currRlabel)) )
		  {
/*
    System.out.println("");
    System.out.println("ADDED EDGE BETWEEN "+rtn.getTypeName()+" AND
"+ltn.getTypeName()+" : ASSIGN STMT ");

    System.out.println("");
*/
		   rtn.addForwardNode(ltn);
	  
		   ltn.addBackwardNode(rtn);

		   try {
       
		    if ( currRrtype.className.equals(new String("java.lang.Object") ) && r.className.equals(new String("java.lang.Object") ) )
		    {

		     ltn.addForwardNode(rtn);
	   
		     rtn.addBackwardNode(ltn);

		    }
       
		   } catch ( java.lang.RuntimeException e ){}

		   declaredtypesHT.put(ltn.getTypeName(), ltn );

		   declaredtypesHT.put(rtn.getTypeName(),rtn);

		  } // IF !CURRLLABEL

		 }

		} // IF !NULLFLAG

	       }

	       public void caseArrayType( ArrayType r ){
	  
		BaseType currbtype = r.baseType;
    
		currbtype.apply( new TypeSwitch(){

		 public void caseRefType( RefType r1 ){
	   
		  if ( !(nullflag) )
		  {   

		   if ( !(((TypeNode) declaredtypesHT.get(currRlabel)) == null
))
		   {
	  
		    TypeNode rtn = (TypeNode) declaredtypesHT.get( currRlabel );
    
		    TypeNode ltn = (TypeNode) declaredtypesHT.get( currLlabel );
  
		    if (  !((currLlabel).equals(currRlabel)) )
		    {
/*
    System.out.println("");
    System.out.println("ADDED EDGE BETWEEN "+rtn.getTypeName()+" AND
"+ltn.getTypeName()+" : ASSIGN STMT (ARRAY)");
*/       

		     rtn.addForwardNode(ltn);
	   
		     ltn.addBackwardNode(rtn);
	     
		     if (( arrayrside ) || ( arraylside ) )
		     {

		      ltn.addForwardNode(rtn);

		      rtn.addBackwardNode(ltn);
/*
   System.out.println("ADDED EDGE BETWEEN "+ltn.getTypeName()+" AND
"+rtn.getTypeName()+" : ASSIGN STMT (ARRAY)");
   System.out.println("PAIRED");
*/

		      }

		      declaredtypesHT.put(ltn.getTypeName(), ltn );

		      declaredtypesHT.put(rtn.getTypeName(),rtn);

		     } // IF !CURRLLABEL
      
		    }

		   } // IF !NULLFLAG

		  } 

		 });

		}

	       });

	      } } } } } } } // ELSE'S

	     } // CASE ASSIGNSTMT

	    });

	   } catch (java.lang.RuntimeException e){ 

         //	      System.err.println("\t ------ Runtime ERROR AT VTA : IGNORED THE PROBLEM STMT IN "+currclass.getName()+" "+currmethod.getName());

         // System.err.println(" PROBLEM STMT : "+stmt.toString());
	     } // CATCH

     } // WHILE STMTITER
    } catch ( java.lang.NullPointerException e ){

      // System.err.println("\t------- NullPtr ERROR AT VTA : Jimple can't handle " + currclass.getName() );
	  
      } catch ( java.lang.RuntimeException e ){

        // System.err.println("\t ------ Runtime ERROR AT VTA : Jimple can't handle " + currclass.getName() +" :  " + e.getMessage());

      }   

      } catch (java.lang.RuntimeException e ) {} 

     } // WHILE ITER

     System.out.println("Done");

     analyseCallSites();
        
     VTANativeAdjustor vtanative = new VTANativeAdjustor ( (HashMap) declaredtypesHT, (HashMap) parameterHT, rta );

     vtanative.adjustForNativeMethods();

  }





  public String getCorrectLeftLabel( AssignStmt s ){ 

  /* RETURNS THE CORRECT LABEL ( OF THE CORRESPONDING NODE IN THE CONSTRAINT
     GRAPH ) FOR THE VARIABLE ON THE LEFT HAND SIDE OF AN ASSIGNMENT */

	  String label = null;

	  arraylside = false;


	  if ( s.getLeftOp() instanceof ArrayRef )
	  {

	   arraylside = true;

	   label = currmethod.getSignature()+((Local) ( (ArrayRef) s.getLeftOp() ).getBase()).getName();

	  }
	  else if ( s.getLeftOp() instanceof InstanceFieldRef )
	  {
	   label = ( (InstanceFieldRef) s.getLeftOp() ).getField().getSignature();
	  
	   ( (InstanceFieldRef) s.getLeftOp() ).getField().getType().apply( new TypeSwitch(){
		    
	    public void caseArrayType( ArrayType r ) {

	     arraylside = true;

	    }

	   }); 

	  }
	  else if ( s.getLeftOp() instanceof StaticFieldRef )
	  {
	   label = ( ( StaticFieldRef) s.getLeftOp() ).getField().getSignature();

	   ( (StaticFieldRef) s.getLeftOp() ).getField().getType().apply( new TypeSwitch(){
	  
	    public void caseArrayType( ArrayType r ) {
	 
	     arraylside = true;

	    }
 
	   });

	  }
	  else  if ( s.getLeftOp() instanceof Local )
	  {

	   label = currmethod.getSignature()+( (Local) s.getLeftOp() ).getName(); 

	   ( (Local) s.getLeftOp() ).getType().apply( new TypeSwitch(){
  
	    public void caseArrayType( ArrayType r ) {
      
	     arraylside = true;

	    }

	   });

	  }

	  return label;
  }






  public String getCorrectRightLabel( AssignStmt s ){
    
   
  /* RETURNS THE CORRECT LABEL ( OF THE CORRESPONDING NODE IN THE CONSTRAINT
     GRAPH ) FOR THE VARIABLE ON THE RIGHT HAND SIDE OF AN ASSIGNMENT */

	  String label = null;        

	  arrayrside = false;

	  if ( s.getRightOp() instanceof ArrayRef )
	  {
	   arrayrside = true;

	   label = currmethod.getSignature()+((Local) ( (ArrayRef) s.getRightOp() ).getBase()).getName();
 
	  }
	  else if ( s.getRightOp() instanceof InstanceFieldRef )
	  {

	   label = ( (InstanceFieldRef) s.getRightOp() ).getField().getSignature();

	   ( (InstanceFieldRef) s.getRightOp() ).getField().getType().apply( new TypeSwitch(){
	  
	    public void caseArrayType( ArrayType r ) {
	 
	     arrayrside = true;

	    }

	   });

	  }
	  else if ( s.getRightOp() instanceof StaticFieldRef )
	  {

	   label = ( ( StaticFieldRef) s.getRightOp() ).getField().getSignature();

	   ( (StaticFieldRef) s.getRightOp() ).getField().getType().apply( new TypeSwitch(){
	  
	    public void caseArrayType( ArrayType r ) {
	 
	     arrayrside = true;

	    }

	   });

	  }
	  else  if ( s.getRightOp() instanceof Local )
	  {

	   ( (Local) s.getRightOp() ).getType().apply( new TypeSwitch(){

	    public void caseArrayType( ArrayType r ) {
  
	     arrayrside = true;

	    }

	   });

	   label = currmethod.getSignature()+( (Local) s.getRightOp() ).getName();
	  }

	  return label;
  }










  public void collectThisVars() {

  /* THIS METHOD COLLECTS THE LOCALS REPRESENTING "THIS" IN EACH METHOD 
     IN THE CALL GRAPH AND STORES THEM IN A HASHTABLE TO BE USED AT THE 
     TIME OF ANALYSING STATEMENTS 

     ALSO STORES THE LOCAL VARIABLE IN EACH METHOD THAT CORRESPONDS TO 
     EACH FORMAL PARAMETER FOR THAT METHOD 

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

      if ( Modifier.isNative ( currmethod.getModifiers() ) )
      {


      if ( ((TypeNode) declaredtypesHT.get("this_"+currmethod.getSignature())) == null )
      {         

       TypeNode t1 = new TypeNode("this_"+currmethod.getSignature(), bitMap, inversebitMap /* , nodesbitMap, nodesinversebitMap */ );

       declaredtypesHT.put("this_"+currmethod.getSignature(), t1 );

      }

       int paramcnt = currmethod.getParameterCount();
        
       for (int count = 0; count < paramcnt; count++)
       {
 
        TypeNode t = new TypeNode(currmethod.getSignature()+"$"+count, bitMap, inversebitMap /* , nodesbitMap, nodesinversebitMap */ );

        declaredtypesHT.put(currmethod.getSignature()+"$"+count, t );

       }
 
      }

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

	   TypeNode localtn = (TypeNode) declaredtypesHT.get(localname);
/*
	   if ( ((TypeNode) declaredtypesHT.get(new String("this_"+currmethod.getSignature()))) == null )
	   declaredtypesHT.put(new String("this_"+currmethod.getSignature()),new TypeNode(new String("this_"+currmethod.getSignature()), bitMap, inversebitMap ));
*/
	 if ( ((TypeNode) declaredtypesHT.get("this_"+currmethod.getSignature())) == null )
         {         

          TypeNode t = new TypeNode("this_"+currmethod.getSignature(), bitMap, inversebitMap /* , nodesbitMap, nodesinversebitMap */ );

          declaredtypesHT.put("this_"+currmethod.getSignature(), t );

/*
          Integer ii = new Integer ( nodesindex++ );

          nodesbitMap.put ( t, ii );

          nodesinversebitMap.put ( ii, t );
*/

         }




	   TypeNode thistn = (TypeNode) declaredtypesHT.get(new String("this_"+currmethod.getSignature()));

	   thistn.addForwardNode(localtn);
	
	   localtn.addBackwardNode(thistn);
    
	   declaredtypesHT.put(thistn.getTypeName(), thistn );

	   declaredtypesHT.put(localtn.getTypeName(),localtn);

	   thisHT.put(currmethod.getSignature(),localname);

	  } // IF S.GETRIGHTOP() 
	  else if ( s.getRightOp() instanceof ParameterRef )
	  {

	   String parametername = ((Local) s.getLeftOp()).getName();
 
	   int parameterindex = ((ParameterRef) s.getRightOp()).getIndex();
  
 parameterHT.put(currmethod.getSignature()+"$"+parameterindex,currmethod.getSignature()+parametername);

           if ( currmethod.getName().equals ( "main" ) )
           {
           
            TypeNode tn = (TypeNode) declaredtypesHT.get( currmethod.getSignature()+parametername );
           
            tn.addInstanceType( "java.lang.String" );

            declaredtypesHT.put( currmethod.getSignature()+parametername, tn );

           }
	
	  }

	}

       });

      } catch ( java.lang.RuntimeException e){}

     } // WHILE STMTITER

    } catch ( java.lang.RuntimeException e){}
 
   } catch ( java.lang.RuntimeException e){}

  } // WHILE ITER
    
 }













 public void analyseCallSites() {

  /* THIS METHOD EXAMINES EACH CALLSITE IN DETAIL AND MODIFIES THE 
     CONSTRAINT GRAPH 

     EDGES, INSTANCE TYPES ARE ADDED TO ACCOUNT FOR THE MAPPING BETWEEN
     THE ACTUAL RECEIVER OF THE METHOD CALL, AND "THIS" OF EACH CALLEE
     METHOD IN THE INPUT CALL GRAPH, AND ALSO THE MAPPING BETWEEN THE 
     FORMAL AND ACTUAL PARAMETERS IN THESE CASES */  

  int num = 0, mnum = 0;

  Collection callGraph = cagb.getCallGraph();

  Iterator iter = callGraph.iterator();

  System.out.println();

  System.out.print("Variable Type Analysis collecting constraints at method call sites");

  while ( iter.hasNext() )
  {

   try {

     //    System.out.print("<"+mnum+">");

    mnum++;

    if ( ( mnum % 10 ) == 0 )
    System.out.print(".");


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

	      ref = true;

	     }

	    });

	   }

	  });
    
	 } // CASE INTERFACEINVOKE

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

	      ref = true;

	     } 

	    });

	   } 

	  });

	} // CASE SPECIALINVOKE

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

	      ref = true;

	     } 

	    });

	   } 

	  });

	 } // CASE STATICINVOKE

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

	     ref = true;

	    } 

	   });
 
	  } 

	 });

	} // CASEVIRTUALINVOKE

       });

      } catch ( java.lang.RuntimeException e ) {}

      Set possMethodNodes = cs.getMethods();

      Iterator MNiter = possMethodNodes.iterator();

      while ( MNiter.hasNext() )
      {

       try {

	MethodNode mn = (MethodNode) MNiter.next();

	SootMethod method = mn.getMethod();

	reachedclass = (method.getDeclaringClass()).getName();

	if ( !(isStatic) )
	{

	 if ( ref )
	 {

	  TypeNode invokedtn = new TypeNode();

	  invokedtn = (TypeNode) declaredtypesHT.get( currlabel );

	  TypeNode thisreachedtn = new TypeNode();

	  try {

	  thisreachedtn = (TypeNode) declaredtypesHT.get( new String("this_"+method.getSignature()) );

	  } catch ( java.lang.RuntimeException e ) {

/*
	     thisreachedtn = new TypeNode( new String("this_"+method.getSignature()), bitMap, inversebitMap );

	     declaredtypesHT.put(thisreachedtn.getTypeName(),thisreachedtn);
*/

          TypeNode t = new TypeNode("this_"+method.getSignature(), bitMap, inversebitMap /* , nodesbitMap, nodesinversebitMap */ );

          thisreachedtn = t;

          declaredtypesHT.put("this_"+method.getSignature(), t );
/*

          Integer ii = new Integer ( nodesindex++ );

          nodesbitMap.put ( t, ii );

          nodesinversebitMap.put ( ii, t );
*/


	    } // CATCH

    
	  if (  !(invokedtn.getTypeName().equals(thisreachedtn.getTypeName())) )
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

       } // IF !ISSTATIC 

       int argcount = invokeExpr.getArgCount();

       int counter = 0;

       while ( counter < argcount )
       {

	refParam = false;

	thisParam = false;

	arraybothsides = false;

	String actualtype = invokeExpr.getArg(counter).getType().toString();

	if ( invokeExpr.getArg(counter) instanceof Constant  )
	{

	 currformallabel = (String) parameterHT.get(new String(method.getSignature()+"$"+counter));

     if ( currformallabel == null )
     currformallabel =  new String(method.getSignature()+"$"+counter);

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

	} // IF INVOKEEXPR.GETARG(COUNTER) INSTANCEOF CONSTANT
	else 
	{
	 
	 curractuallabel = currmethod.getSignature()+((Local) invokeExpr.getArg(counter)).getName();

	 invokeExpr.getArg(counter).getType().apply( new TypeSwitch(){

	  public void caseRefType( RefType r ){

	   refParam = true;

	   currRrtype = r;

	  }

	  public void caseArrayType( ArrayType r ){

	   BaseType currbtype = r.baseType;
    
	   currbtype.apply( new TypeSwitch(){

	    public void caseRefType( RefType r1 ){

	     refParam = true;

	     arraybothsides = true;

	    } 

	   });

	  }

	});


	String formaltype = method.getParameterType(counter).toString(); 

	method.getParameterType(counter).apply( new TypeSwitch(){

	 public void caseArrayType( ArrayType r ){

	  arraybothsides = true;

	 }

	});


	currformallabel = (String) parameterHT.get(new String(method.getSignature()+"$"+counter));

    if ( currformallabel == null )
    currformallabel =  new String(method.getSignature()+"$"+counter);

	if ( refParam )
	{

	 if ( !(((TypeNode) declaredtypesHT.get(currformallabel)) == null ) ) 
	 {

	  TypeNode actualtn = new TypeNode();

	  actualtn = (TypeNode) declaredtypesHT.get( curractuallabel );

	  TypeNode formaltn = (TypeNode) declaredtypesHT.get(currformallabel);

	  if (  !(actualtn.getTypeName().equals(formaltn.getTypeName())) )
	  { 
/*
    System.out.println("");
    System.out.println("ADDED EDGE BETWEEN "+actualtn.getTypeName()+" AND
"+formaltn.getTypeName()+" AND VICE VERSA : PARAMETER PASSING");
    System.out.println("");
  */      

	   actualtn.addForwardNode(formaltn);

	   formaltn.addBackwardNode(actualtn);
	  
	   try {
       
	    if ( currRrtype.className.equals(new String("java.lang.Object") ) && formaltype.equals(new String("java.lang.Object")) )
	    arraybothsides = true;
       
	   } catch ( java.lang.RuntimeException e ){}

	   if ( arraybothsides )
	   {

	    actualtn.addBackwardNode(formaltn);
	  
	    formaltn.addForwardNode(actualtn);

	   }

	  declaredtypesHT.put(actualtn.getTypeName(), actualtn );

	  declaredtypesHT.put(formaltn.getTypeName(), formaltn);

	 }

	}

       } // IF REFPARAM

      } // ELSE

      counter++;

     } // WHILE COUNTER
 
    } catch ( java.lang.RuntimeException e ) {}

   } // WHILE MNITER

   } catch ( java.lang.RuntimeException e ) {}

   } // WHILE CSITER
     
   } catch ( java.lang.NullPointerException e ) {}

   } // WHILE ITER

   System.out.println("Done");

  }







 public void solveConstraints( Map allclassHT ) {

  /* THIS METHOD SOLVES THE CONSTRAINT GRAPH OBTAINED AS A RESULT 
     OF ANALYSING ALL THE STATEMENTS IN THE INPUT CALL GRAPH

     A WORKLIST ALGORITHM IS BEING USED 

     GARBAGE COLLECTION IS ALSO DONE TO FREE THE MEMORY TAKEN BY THE 
     STORAGE OF JIMPLIFIED METHODS. THE SPACE OCCUPIED BY JIMPLIFIED 
     METHODS CAN BE RELEASED NOW AS THE STATEMENTS ARE NOT USED 
     ANY MORE BY VARIABLE TYPE ANALYSIS */


  int constraintedges = 0;

  //  System.out.println("READY FOR GARBAGE COLLECTION");

  // GARBAGE COLLECTION

  // Jimplifier.removeall();

/*

  int i = 0;

  for ( i = 0; i < 30; i++ )
  {

   System.gc();

   System.out.println(" i = "+i);

  }

*/

  // System.out.println("DONE WITH GARBAGE COLLECTION");

  // COMPUTE THE SCCs

  Timer SCCTimer = new Timer();

  SCCTimer.start();

  ReplaceStrConnComp();

  SCCTimer.end();

  // System.out.println("TIME FOR SCC : "+SCCTimer.getTime());
  
  int solved = 0; 

  Timer SolverTimer = new Timer();

  SolverTimer.start();  

  // SOLVE THE CONSTRAINT GRAPH

  List workQ = new ArrayList();

  // Object[] keys = declaredtypesHT.keySet().toArray();

  System.out.println("");
 
  System.out.print("Variable Type Analysis solving constraints.....");

  Iterator iterator = declaredtypesHT.values().iterator();

  while ( iterator.hasNext() )
  {

   TypeNode tn = (TypeNode) iterator.next();

   if ( tn.isSource() )
   workQ.add(tn);

  }


  while ( !workQ.isEmpty() )
  { 

   TypeNode tn = (TypeNode) workQ.remove(0);

   tn.solveNode(allclassHT, instancetypesHT );
 
   // System.out.print("<"+solved+">");
 
   solved++;

   // GARBAGE COLLECTION  

   // if ( ( solved % 5000 ) == 0 )
   // System.gc();
 
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

  System.out.println("TOTAL NUMBER OF CONSTRAINT NODES AFTER SCCs COMPUTATION : "+declaredtypesHT.values().size());
  System.out.println("TOTAL NUMBER OF CONSTRAINT EDGES AFTER SCCs COMPUTATION : "+constraintedges);

  SolverTimer.end();

  // System.out.println("TIME FOR SOLVING THE SYSTEM : "+SolverTimer.getTime());


 }






public int rtared[];

public void setRTAred(RTA rta ){ rtared = rta.getReduced(); }

int iteration = 0;





public Collection getFinalCallGraph() {

  /* THIS METHOD ACTUALLY REMOVES THE CALL GRAPH EDGES THAT CAN BE REMOVED 
     ACCORDING TO VARIABLE TYPE ANALYSIS */

  iteration++;

  String reachedclass = new String();
   
  Collection callGraph = null;

  try {

   callGraph = cagb.getCallGraph();

   Iterator iter = callGraph.iterator();

   while ( iter.hasNext() )
   {
   
    try {
   
     MethodNode tempMN = (MethodNode) iter.next();

     currclass = tempMN.getMethod().getDeclaringClass();

     currmethod = tempMN.getMethod();
   
     Iterator CSiter = tempMN.getCallSites().iterator();
   
     while ( CSiter.hasNext() )
     {


       try {

	isStatic = false; 

	isSpecial = false;

	CallSite cs = (CallSite) CSiter.next();

	InvokeExpr invokeExpr = cs.getInvokeExpr();

	invokeExpr.apply( new AbstractJimpleValueSwitch(){
	
	 public void caseStaticInvokeExpr(StaticInvokeExpr v){

	  isStatic = true;
 
	 }

	 public void caseSpecialInvokeExpr(SpecialInvokeExpr v){
 
	  isSpecial = true;

	 }

	});

       Map actualinstanceHT = new HashMap();

       HashMap reachedclassHT = new HashMap();

       HashSet vs = new HashSet();

       try { 

	// OBTAIN THE SET OF METHODNODES THAT CAN BE CALLED FROM THIS CALLSITE
	// AS DETERMINED BY VARIABLE TYPE ANALYSIS

	vs = ( HashSet ) csHT.get( currmethod.getSignature()+invokeExpr.toString() ); 

       } catch ( java.lang.RuntimeException e ) {}

       Set possMethodNodes = cs.getMethods();
    
       Iterator MNiter = possMethodNodes.iterator();

       // REPLACE THE SET OF METHODNODES THAT CAN BE CALLED FROM THIS 
       // CALLSITE BY THE NEW SET OBTAINED USING VARIABLE TYPE ANALYSIS    

       if ( ! ( isStatic || isSpecial ) )
       cs.setMethods(vs);

       } catch ( java.lang.RuntimeException e ) {}

      } // WHILE CSITER

     } catch ( java.lang.RuntimeException e ) {}

    } // WHILE ITER

   } catch ( java.lang.RuntimeException e ) {}

   /* CHECK ON NUMBER OF NODES AFTER PRUNING , CAN BE REMOVED */

  try {   
       
   int totalnum = 0;
   
   Iterator iter = callGraph.iterator();

   PrintWriter pw = null;

   PrintWriter pw1 = null;

   HashSet seenclasses = new HashSet();


   try {

    File tempFile = new File("analysis.VTA"+ iteration );
 
    FileOutputStream streamOut = new FileOutputStream(tempFile);
            
    pw = new PrintWriter(streamOut);

    tempFile = new File("profiled.VTA"+ iteration );

    streamOut = new FileOutputStream(tempFile);
   
    pw1 = new PrintWriter(streamOut);


   } catch ( java.io.IOException e ) {}



   while ( iter.hasNext() )
   {
 
    try {
 
    MethodNode tempMN = (MethodNode) iter.next();
 
    currclass = tempMN.getMethod().getDeclaringClass();

    currmethod = tempMN.getMethod();

    String currname = currmethod.getDeclaringClass().getName();
       
    if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currname) || ClassGraphBuilder.isLibraryNode("sun.", currname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currname) ) )
    {

    //  try {

    //   System.out.println ( "SEEN BENCHMARK" );
    //   System.out.println ( currname );       

       if ( ! seenclasses.contains ( currname ) )
       {

      //  System.out.println ( "PRINTING....");

        pw1.println ( currname );
     
        seenclasses.add ( currname );

       }

    //  } catch ( java.io.IOException e ) {}

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

      if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currname) || ClassGraphBuilder.isLibraryNode("sun.", currname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currname) ) ) 
      {

      if ( ! ( ( inexpr instanceof StaticInvokeExpr ) || /* ( inexpr.getMethod().getName().equals ( "<init>" ) ) */ ( inexpr instanceof SpecialInvokeExpr ) ) )
      {     

      String currentID = cs.getCallerID();

//      Set possMethodNodes = cs.getMethods();

//      totalnum = totalnum + possMethodNodes.size();

      Iterator possibleit = possMethodNodes.iterator();

      while ( possibleit.hasNext() )
      {

       SootMethod printmethod = ( ( MethodNode ) possibleit.next() ).getMethod();

      //  try {

       pw.println ( currentID+" "+printmethod.getSignature() );

      //  } catch ( java.io.IOException e ) {}

      }

      }

     }

      } catch ( java.lang.RuntimeException e ) {}

    } // WHILE CSITER

   } catch ( java.lang.RuntimeException e ) {}

  } // WHILE ITER

  pw.close();

  pw1.close();

  System.out.println("PRUNED CALL GRAPH HAS : "+totalnum+" EDGES");

 } catch ( java.lang.RuntimeException e ) {}

  VTAgc();

  return callGraph;

 }






  public void VTAgc() {

   Iterator iterator = declaredtypesHT.values().iterator();
      
   while ( iterator.hasNext() )
   {
   
    TypeNode tn = (TypeNode) iterator.next();

    tn.prepareForGC();

   }

  }










  private Map csHT = new HashMap();

  public List ImprovedCallSites = new ArrayList();





  public Collection getCallGraph() {

/* THIS METHOD PERFORMS ALL THE STATISTICS GATHERING ON THE CALL GRAPH 
   IMPROVEMENT POSSIBLE USING VARIABLE TYPE ANALYSIS

   THE NEW SET OF METHODNODES ( OBTAINED USING VTA ) THAT CAN BE INVOKED 
   FROM EACH CALL SITE IS ALSO COMPUTED IN THIS METHOD

*/
	
  int edges = 0,removededges = 0,polymorphicsites = 0, polytotal = 
0,polyred = 0, intpolyred = 0, virpolyred = 0,sites = 0,zerored = 0,siteno =
0,twoedges =0,inttwoedges = 0, virtwoedges = 
0,threeedges =0, intthreeedges = 0, virthreeedges = 0, moreedges =0,
intmoreedges = 0, 
virmoreedges = 0, twored = 0, inttwored = 0, virtwored = 0,threered =0,
intthreered = 0, 
virthreered = 0, morered =0, intmorered = 0, virmorered = 0, zonered = 0,ztwored
=0,zthreered 
=0,zmorered = 0,bired = 0, intbired = 0, virbired = 0, bthreered = 0,
intbthreered = 0, 
virbthreered = 0, bmorered = 0, intbmorered = 0, virbmorered = 0;
 

  int benchtotedges = 0, benchremovededges = 0, benchreduction = 0, benchintactedges = 0, benchpolyred = 0, benchintpolyred = 0, benchvirpolyred = 0;

  List paramList = new ArrayList();
	
  String reachedclass = new String(); 

  Collection callGraph = null;
	
  try {       

   callGraph = cagb.getCallGraph();

   // Map OpenMethodsHT = cagb.getOpenMethodsHT();
   /*	
   System.out.println("");
   System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++");
   System.out.println("NUMBER OF METHODNODES = "+callGraph.size());
   System.out.println("NUMBER OF CALLSITES = "+cagb.getSitesNum() );
   System.out.println("NUMBER OF CALLGRAPH EDGES = "+cagb.getEdgesNum() );
   System.out.println("");
   */
   VTATimer.end();

   VTAMem = Runtime.getRuntime().totalMemory() -
Runtime.getRuntime().freeMemory();
   /*
   System.out.println("TIME FOR VTA (ANALYSIS ONLY) : "+VTATimer.getTime());
   System.out.println("SPACE FOR VTA (ANALYSIS ONLY) : "+VTAMem);
   */

   System.out.println("");
   System.out.print ("Pruning call graph based on Variable Type Analysis");

   VTAedgeRemovedTimer.start();

   int mthdcount = 10;

   Iterator iter = callGraph.iterator();
 
   while ( iter.hasNext() )
   {    

     if ( mthdcount == 10 )
     {
      System.out.print(".");
      mthdcount = 1;
     }
     else
     mthdcount++;

    try {

     MethodNode tempMN = (MethodNode) iter.next();
	
     String currclassname = tempMN.getMethod().getDeclaringClass().getName();

     sites = sites + tempMN.getCallSites().size();

     Iterator CSiter = tempMN.getCallSites().iterator();
 
     while ( CSiter.hasNext() )
     {

      HashSet filteredMethodNodes = new HashSet();
    
      CallSite cs = (CallSite) CSiter.next();

      try {

       siteno++;

       // System.out.print("<"+siteno+">");

       thisInvoked = false;
 
       ref = false;

       isStatic = false;

       isSpecial = false;

       intflag = false;        

       virflag = false;

       try {

	currclass = tempMN.getMethod().getDeclaringClass();
   
	currmethod = tempMN.getMethod();

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
	  
	 } // CASE INTERFACEINVOKE

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

	 } // CASE SPECIALINVOKE
	
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
	  
	} // CASE STATIC INVOKE
    
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
	   
	} // CASE VIRTUALINVOKE

       });

      } catch ( java.lang.NullPointerException e ) {}
	 

      // Map actualinstanceHT = new HashMap();

      BitSet actualinstanceBS = new BitSet();

      HashMap reachedclassHT = new HashMap(10, 0.7f );

      try {

/*
       if ( ! (( (HashMap) instancetypesHT.get( currlabel ) ) == null ) )
       actualinstanceHT = (HashMap) instancetypesHT.get( currlabel );
*/  
  
       if ( ! (( (BitSet) instancetypesHT.get( currlabel ) ) == null ) )
       actualinstanceBS = ( BitSet ) instancetypesHT.get ( currlabel );   

       // GET THE HASHTABLE CONTAINING THE NAMES OF THE CLASSES THAT CAN BE 
       // REACHED DUE TO THIS METHOD CALL
       // THIS ANSWER IS ARRIVED AT BY CHECKVIRTUALTABLES(..) BY USING THE
       // RESULT FROM VARIABLE TYPE ANALYSIS 


       // reachedclassHT = checkVirtualTables(actualinstanceHT, cs.getInvokeExpr().getMethod() );

          reachedclassHT = checkVirtualTables(actualinstanceBS, cs.getInvokeExpr().getMethod() );

      } catch ( java.lang.RuntimeException e ) {}

      Set possMethodNodes = cs.getMethods();

      int size = possMethodNodes.size();
  
      int reduction = 0;

      if ( size > 1 ) 
      { 

       polymorphicsites++; 

       polytotal = polytotal + size;

       if ( size == 2 ) 
       { 

	twoedges++; 

	if ( intflag ) 
	inttwoedges++; 
	else 
	virtwoedges++;
 
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

      } // IF SIZE > 1


       if ( size == 1 ) 
       { 

	if ( virflag ) 
	virmono++;
	else if ( intflag ) 
	intmono++;

       }

       edges = edges + size;
	
       Iterator MNiter = possMethodNodes.iterator();

       if ( !(isStatic || isSpecial) )
       {

	while ( MNiter.hasNext() )
	{

	 try {

	  MethodNode menode = (MethodNode) MNiter.next();

	  SootMethod method = menode.getMethod();
	
	  reachedclass = (method.getDeclaringClass()).getName();

	   String tempdebug = null;

	   if ( ref )
	   {
/*
	    actualinstanceHT = new HashMap();

	    if ( ! (( (HashMap) instancetypesHT.get( currlabel ) ) == null ) )
	    actualinstanceHT = (HashMap) instancetypesHT.get( currlabel );
*/
	    tempdebug = currlabel;          

/*
	 if (( ( ((String) actualinstanceHT.get(reachedclass)) == null )&& (
!((Boolean) OpenMethodsHT.get(reachedclass+method.getName())).booleanValue()) )
&& (! method.getName().equals(new String("<init>"))))
	 {

*/
	   
	    // CHECK TO SEE IF CALL GRAPH EDGE CAN BE REMOVED

	    if ( (  (( (String) reachedclassHT.get(reachedclass) ) == null ) ) && ( ! invokedclass.endsWith ( "[]" ) ) )
	    {

             if ( ! ( ( size == 1 ) && ( invokedclass.equals ( "java.lang.Object" ) ) ) )
             {

//       System.out.print("CALLED METHOD "+reachedclass+" "+method.getName());
//       System.out.println(" REMOVED");

               /*

             if ( menode.getMethod().getSignature().startsWith ( "spec.ben" ) )
             {

              System.out.println ( " BEN NODE = "+menode.getMethod().getSignature() );

              Iterator reachedclassit = reachedclassHT.values().iterator();

              if ( reachedclassHT.values().size() > 0 )
              {
              
               while ( reachedclassit.hasNext() )
               System.out.println ( ( String ) reachedclassit.next() );

              } 
              else
              System.out.println ( "ZERO REACHEDCLASSES " );

             }

             */

	     menode.incomingedges--;
         menode.removeInvokingSite(cs);


             // if ( menode.incomingedges < 1 )
             // menode.isRedundant = true;

	     removededges++; 

	     reduction++;

             if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
            {

             benchtotedges++;

             benchremovededges++;

             benchreduction++;
 
            } 

	     try {
//       System.out.println("CALL SITE : "+cs.getInvokeExpr().toString()+" BASE CLASS "+invokedclass);
	      } catch ( NoInvokeExprException e ){}

//       System.out.println(" OBTAINED SOLUTION FOR : "+tempdebug);

//       System.out.println(" CURRENT CLASS AND METHOD : "+currclass.getName()+" "+currmethod.getName());

            }

	    } // IF REACHEDCLASSHT
	    else 
	    {

	     filteredMethodNodes.add(menode);

	     if ( (( String) aliveHT.get(menode.getMethod().getSignature())) == null )      
	     {

	      alive++;

	     
aliveHT.put(menode.getMethod().getSignature(),menode.getMethod().getSignature());

	     }

	    } // ELSE 

/*
      System.out.println("THE SOLUTION : ");
  
      Object[] debugkeys = actualinstanceHT.keySet().toArray();
      for ( int i = 0 ; i < debugkeys.length ; i++ )
      System.out.println( (String)  actualinstanceHT.get(debugkeys[i]) );
*/

	   } // IF REF
	   else 
	   {

	    filteredMethodNodes.add(menode);
     
	    if ( (( String) aliveHT.get(menode.getMethod().getSignature())) == null )
	    {

	     alive++;

	    
aliveHT.put(menode.getMethod().getSignature(),menode.getMethod().getSignature());

	    }

	   }
	
	  } catch ( java.lang.NullPointerException e ) {}

	 } // WHILE MNITER

	} // IF !ISSTATIC
   

/*
	System.out.println("");

	if ( rtared[siteno-1] > reduction )
	System.out.println("CAUTION : RTA DOES BETTER HERE ");
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

          if ( ( rtared[siteno-1] > 1 ) && ( ! ImprovedCallSites.contains ( cs ) ) )
          ImprovedCallSites.add ( cs );

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

	} // ELSE IF SIZE - REDUCTION == 2

/*

      System.out.println("THE SOLUTION : ");

      Object[] debugkeys = actualinstanceHT.keySet().toArray();
      for ( int i = 0 ; i < debugkeys.length ; i++ )
      System.out.println( (String)  actualinstanceHT.get(debugkeys[i]) );


*/

       } // IF REDUCTION > 0 


      } catch ( java.lang.RuntimeException e ) {}

       
      try {

csHT.put(currmethod.getSignature()+cs.getInvokeExpr().toString(),filteredMethodNodes);

      } catch ( java.lang.RuntimeException e ){}



     } // WHILE CSITER
    
    } catch ( java.lang.NullPointerException e ) {}
  
   } // WHILE ITER

      System.out.println("Done");
      System.out.println("");

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
      System.out.println("NUMBER OF BENCHMARK EDGES REMOVED = "+benchremovededges);
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
      System.out.println("NUMBER OF BENCHMARK POLYMORPHIC SITES REDUCED TO 1 = "+benchpolyred);
   
      System.out.println("NUMBER OF INVOKEINTERFACE SITES REDUCED TO 1 = "+intpolyred);
      System.out.println("NUMBER OF BENCHMARK INVOKEINTERFACE SITES REDUCED TO 1 = " +benchintpolyred);


      System.out.println("NUMBER OF INVOKEVIRTUAL SITES REDUCED TO 1 = "+virpolyred);
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

      VTAedgeRemovedTimer.end();

      VTAedgeRemovedMem =  Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
      /*
      System.out.println("TIME FOR VTA (EDGE REMOVAL ONLY) : "+VTAedgeRemovedTimer.getTime());
      System.out.println("SPACE FOR VTA (EDGE REMOVAL ONLY) : "+VTAedgeRemovedMem);
      */
      printRemainingMethods();
       
     } catch ( java.lang.NullPointerException e ){

	System.err.println("\t------- NullPtr ERROR: Jimple can't handle " + reachedclass );
	
       } catch ( java.lang.RuntimeException e ){

	  System.err.println("\t ------ Runtime ERROR: Jimple can't handle " + reachedclass );

	 } catch( Throwable e ){

	    System.err.println("Throwable ERROR:" + e.getMessage() );
      
	   }

    return callGraph;
	  
  }




private HashMap aliveHT = new HashMap();

private int alive = 0;







  public HashMap checkHierarchy( Map instanceHT, SootMethod meth, SootMethod currmeth )
{
   // NOT USED ANY MORE    

   Object[] htkeys = instanceHT.keySet().toArray();

   HashMap answerHT = new HashMap();

   for ( int i = 0 ; i < htkeys.length ; i++ )
   {

    try {

     String className = (String) instanceHT.get(htkeys[i]);

     ClassNode cnode = clgb.getNode(className);

     SootMethod method = cagb.getSuperMethod( cnode, meth, currmeth ); 

    
answerHT.put(method.getDeclaringClass().getName(),method.getDeclaringClass().getName());
       
    } catch (java.lang.RuntimeException e ) {}

   }

   return answerHT;

  }








  public HashMap checkVirtualTables( BitSet instanceBS, SootMethod meth ) {

  /* THIS METHOD PERFORMS THE LOOKUP IN THE VIRTUALHT CREATED IN 
     CLASSGRAPHBUILDER

     RETURNS A HASHTABLE CONTAINING CLASSES WHICH DECLARE A METHOD 
     NAMED METH AND ARE DETERMINED TO BE POTENTIAL TARGETS BY VARIABLE 
     TYPE ANALYSIS     
  */

   // USE THE RESULT FROM VARIABLE TYPE ANALYSIS 

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
   
   HashMap answerHT = new HashMap( 10, 0.7f );
      
   for ( int i = 0 ; i < /* htkeys.length */ instanceBS.size(); i++ )
   {

    try {
   
     // String className = (String) instanceHT.get(htkeys[i]);

     String className = null;

     if ( instanceBS.get(i) )
     {

     className = ( String ) inversebitMap.get ( new Integer ( i ) );

     HashMap stm = ( HashMap ) clgb.virtualHT.get(className);

     String reachedclass = ( String ) stm.get(buffer.toString() );
      
     answerHT.put(reachedclass, reachedclass);

     }

    } catch (java.lang.RuntimeException e ) {}

   }
      
   return answerHT;

  }     









  public void ReplaceStrConnComp(){

  
   /* THIS METHOD EXAMINES THE CONSTRAINT GRAPH, DETECTS THE SCCs IN THE GRAPH
      AND REPLACES THEM BY A SPECIAL CGSCC NODE AND UPDATES THE EDGES AND 
      INSTANCE TYPES SETS OF EACH NODE TO ACCOUNT FOR THE CHANGE IN THE 
      STRUCTURE OF THE CONSTRAINT GRAPH

      THE CONSTRAINT GRAPH IS A DAG AFTER THIS METHOD RETURNS

      CALLED BY SOLVECONSTRAINTS()

      USES THE SCCDETECTOR CLASS IN THE SIDEEFFECT PACKAGE

    */


    //   System.out.println("COMPUTING STRONGLY CONNECTED COMPONENTS");

   SCCDetector sccd = new SCCDetector();

   SCCs = sccd.computeSCCs(declaredtypesHT.values());
    
   // System.out.println("COMPUTED STRONGLY CONNECTED COMPONENTS");
    
   System.out.println("");

   System.out.println("ORIGINAL NUMBER OF NODES IN CONSTRAINT GRAPH : "+sccd.Originalnodes); 

   System.out.println("ORIGINAL NUMBER OF EDGES IN CONSTRAINT GRAPH : "+sccd.Originaledges); 

   System.out.println("");

   Iterator iter = SCCs.iterator();
    
   while( iter.hasNext() )
   {
      
      List aSCC = (List)iter.next();
      
      //      if ( aSCC.size() > 1 ) 
      // System.out.println("FOR A STRONGLY CONNECTED COMPONENT : "+aSCC.size());


      if ( aSCC.size() > 1 )
      {

	CGSCCNode aCGSCCNode = new CGSCCNode( aSCC );

	aCGSCCNode.updateLinks(declaredtypesHT);

	declaredtypesHT.put( aCGSCCNode.getTypeName() , aCGSCCNode );

/*
        Integer ii = new Integer ( nodesindex++ );

        nodesbitMap.put ( aCGSCCNode, ii );

        nodesinversebitMap.put ( ii, aCGSCCNode );
*/

//	aCGSCCNode.updateLinks(declaredtypesHT);

      }

   } // WHILE

  }








  void printRemainingMethods() {

   /* CALCULATES THE NUMBER OF METHODS THAT MIGHT BE CALLED ( ALIVE )
      AS A RESULT OF PERFORMING VTA AND PRINTS OUT THE NUMBER */
 
   alive = 0;

   int benchalive = 0;

   int clinitnum = 0;

   int benchclinitnum = 0;

   Iterator iter = cagb.getCallGraph().iterator();

   while ( iter.hasNext() ) {
   
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
      //      System.out.println ( "DEAD "+mn.getMethod().getSignature() );

      if ( ( mn.getMethod().getName().equals("<clinit>") ) ||  ( mn.getMethod().getName().equals("finalize") ) )
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































