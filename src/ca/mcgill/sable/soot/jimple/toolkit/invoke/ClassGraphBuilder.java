// package ca.mcgill.sable.soot.sideEffect;

package ca.mcgill.sable.soot.jimple.toolkit.invoke;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.baf.*; 

import java.util.StringTokenizer;
import java.io.File;

public class ClassGraphBuilder{
  

  SootClassManager cm = new SootClassManager();


  /**
   * a mapping from a class name to its ClassGraphNode Representative
   */

  Map ClassNodeHT = new HashMap();
  




  /**
   * @return a map containing all class nodes. Keys are class names specified in the package format. 
   */

  public Map getClassGraph(){

    return ClassNodeHT;

  } 




  /**
   * 
   */
  public ClassNode getNode( String className ){

    ClassNode classNode ;

    if ( (classNode = (ClassNode)ClassNodeHT.get( className )) == null )
      throw new NoSuchClassNodeException( "Can't Find classNode : " + className );

    return classNode;
  }




  /**
   * @return a collection of all the classNodes in the class hierarchy.
   */

  public Collection getClassNodes(){

    return ClassNodeHT.values();

  }





  static int numInterfaces = 0; 
  static int numClasses = 0;
  static int numBenchInterfaces = 0;
  static int numLibInterfaces = 0;
  static int numBenchClasses = 0;
  static int numLibClasses = 0;
  static int hierarchyDepth = 1; 
  static int benchhierarchyDepth = 1;
  static int libraryClasses = 0;
  static int benchmarkClasses = 0;
  static double totaldepth = 0.0;
  static double benchtotaldepth = 0.0;
  



  private Set incorrectlyjimplified = new HashSet();





  public Set getIncorrectlyJimplifiedClasses() {

   return incorrectlyjimplified;

  }




  public void setClassGraphNumbers() {

    Object[] htkeys = ClassNodeHT.keySet().toArray();
    
    for ( int i = 0 ; i < htkeys.length ; i++ )
      {
	try {

	  ClassNode cn = (ClassNode) ClassNodeHT.get(htkeys[i]);

	  String name = cn.getName();

	  boolean isJava = isLibraryNode("java.",name);
       
	  boolean isSun = isLibraryNode("sun.",name);

	  boolean isSunw = isLibraryNode("sunw.",name);

          boolean isBenchmark = false;

	  if ( isJava || isSun || isSunw )
	    libraryClasses++;
	  else
           {

	    benchmarkClasses++;

            isBenchmark = true;            

            }

	  int depth = 1;
       
          int benchdepth = 1;

	  if ( cn.isInterface() )
          {

	    numInterfaces++;

            if ( isBenchmark )
            numBenchInterfaces++;
            else
            numLibInterfaces++;
        
          }
	  else
          {
 
	    numClasses++;

            if ( isBenchmark )
            numBenchClasses++;
            else          
            numLibClasses++;

           }




	  SootClass bc = cn.getSootClass();

	  while ( bc.hasSuperClass() )
	    {
	      bc = bc.getSuperClass();
	      depth++; 

              // totaldepth++;

              isJava = isLibraryNode("java.",bc.getName());
   
              isSun = isLibraryNode("sun.",bc.getName());
        
              isSunw = isLibraryNode("sunw.",bc.getName());

              if ( ! ( isJava || isSun || isSunw ) )
              {
               benchdepth++;
               // benchtotaldepth++; 
              }

	    } 

            totaldepth = totaldepth + depth;

            if ( isBenchmark )
            benchtotaldepth = benchtotaldepth + benchdepth;

	  if ( depth > hierarchyDepth )
	    hierarchyDepth = depth;

          if ( benchdepth > benchhierarchyDepth )
            benchhierarchyDepth = benchdepth;

	} catch ( java.lang.RuntimeException e ) {}
      }
    

    System.out.println ("Inheritance hierarchy characterestics : ");
    System.out.println ("--------------------------------------- ");
    System.out.println();
    System.out.println("NUMBER OF CLASSFILES INCLUDED    : "+(libraryClasses+benchmarkClasses));
    System.out.println("NUMBER OF LIBRARY CLASSFILES     : "+libraryClasses);
    System.out.println("NUMBER OF BENCHMARK CLASSFILES   : "+benchmarkClasses);
    System.out.println();
    System.out.println("NUMBER OF CLASSES                : "+numClasses);
    System.out.println("NUMBER OF LIBRARY CLASSES        : "+numLibClasses);
    System.out.println("NUMBER OF BENCHMARK CLASSES      : "+numBenchClasses);
    System.out.println();
    System.out.println("NUMBER OF INTERFACES             : "+numInterfaces);
    System.out.println("NUMBER OF LIBRARY INTERFACES     : "+numLibInterfaces);
    System.out.println("NUMBER OF BENCHMARK INTERFACES   : "+numBenchInterfaces);
    System.out.println();
    System.out.println("MAX DEPTH OF ENTIRE HIERARCHY    : "+hierarchyDepth);
    System.out.println("MAX DEPTH OF BENCHMARK HIERARCHY : "+benchhierarchyDepth);
    System.out.println("AVG DEPTH OF ENTIRE HIERARCHY    : "+( ( double ) totaldepth / htkeys.length ) );
    System.out.println("AVG DEPTH OF BENCHMARK HIERARCHY : "+( ( double ) benchtotaldepth / benchmarkClasses ) );
    System.out.println();
    System.out.println();
  }    














  public static boolean isLibraryNode ( String s1, String s2 )
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








  static List startmethods = new ArrayList();

  static List runmethods = new ArrayList();








  public boolean implementsRunnable(SootClass bclass) {

    boolean answer = false;
  
    while ( ( bclass.hasSuperClass() ) && ( answer == false ) )
      {

	if ( bclass.implementsInterface( new String("java.lang.Runnable") ) )
	  answer = true;
	else
	  bclass = bclass.getSuperClass();
      
      }
  
    return answer;
  
  }
  



 HashSet sources = new HashSet();




  
  public void getStartAndRunMethods() {
    
    Object[] htkeys = ClassNodeHT.keySet().toArray();
    
    for ( int i = 0 ; i < htkeys.length ; i++ )
      {
	try {
	  
	  ClassNode cn = (ClassNode) ClassNodeHT.get(htkeys[i]);
	  
	  SootClass bc = cn.getSootClass();
	  
	  List paramList = new ArrayList();
	  
	  if ( bc.declaresMethod(new String("start") , paramList ) ) 
	    {
	      if ( implementsRunnable(bc) )
              {
		startmethods.add(bc.getMethod(new String("start") , paramList )); 

                sources.add ( bc.getMethod( new String ("start"), paramList ).getSignature() );

              }
   
	    }       
	  
	  if ( bc.declaresMethod(new String("run") , paramList ) )
	    {
	      if ( implementsRunnable(bc) )
              {
               
		runmethods.add(bc.getMethod(new String("run") , paramList ));

                sources.add ( bc.getMethod( new String ("run"), paramList ).getSignature() );

              }

	    }
	  
	} catch (java.lang.RuntimeException e ) {}
	
      }
    
  }
  



  // external user: callSiteAnalysis;



  static Map virtualHT = new HashMap();







  public void buildVirtualTables() {

    Object[] htkeys = ClassNodeHT.keySet().toArray();
        
    for ( int i = 0 ; i < htkeys.length ; i++ )
      {
	try {
    
	  ClassNode cn = (ClassNode) ClassNodeHT.get(htkeys[i]);
	 
	  // System.out.println("BUILDING VTable for: " +  cn.getName()+" KEY "+htkeys[i] ); 

	  SootClass bc = cn.getSootClass();

	  Map classHT = new HashMap();
	 
	  List methods = bc.getMethods();
	 
	  Iterator it = methods.iterator();
	 
	  while ( it.hasNext() )
	    {
	     
	      StringBuffer buffer = new StringBuffer();
	     
	      try {        
	       
		SootMethod method = ( SootMethod ) it.next();
	       
		if ( ! Modifier.isPrivate( method.getModifiers() ) )
		  {
		   
		    buffer.append(method.getName());
		    buffer.append("(");
		   
		    Iterator typeIt = method.getParameterTypes().iterator();
		   
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
		   
		    buffer.append(":" + method.getReturnType().toString());
		   
		    classHT.put(buffer.toString(), bc.getName() );   
		  }            
	      } catch ( java.lang.RuntimeException e ) {
		System.out.println("IN BUILDVtables 1 " + e ); 
	      }
	    }
	 
	 
	 
	  while ( bc.hasSuperClass() )
	    {
	      bc = bc.getSuperClass();
	     
	      // CR Try
	      try{
		List scmethods = bc.getMethods();
		
	     
		Iterator scit = scmethods.iterator();
		
		while ( scit.hasNext() )
		  {
		    
		    StringBuffer scbuffer = new StringBuffer();
		    
		    
		    try {
		      
		      SootMethod scmethod = ( SootMethod ) scit.next();
		      
		      if ( ! Modifier.isPrivate( scmethod.getModifiers() ) )
			{
			  
			  scbuffer.append(scmethod.getName());
			  scbuffer.append("(");
			  
			  Iterator sctypeIt = scmethod.getParameterTypes().iterator();
			  
			  if(sctypeIt.hasNext())
			    {
			      scbuffer.append(sctypeIt.next());
			   
			      while(sctypeIt.hasNext())
				{
				  scbuffer.append(",");
				  scbuffer.append(sctypeIt.next());
				}
			    }
			  
			  scbuffer.append(")");
			  
			  scbuffer.append(":" + scmethod.getReturnType().toString());
		       
			  if ( ( (String) classHT.get(scbuffer.toString()) ) == null )        
			    classHT.put(scbuffer.toString(), bc.getName() );
			  
			}
		    } catch ( java.lang.RuntimeException e ) {
		      System.out.println("IN BUILDVtables 2 " + e ); 
		    }
		  }
	      } catch ( java.lang.RuntimeException e ) {
		System.out.println("IN BUILDVtables 2 " + e ); 
	      }
	    }
	  
	  virtualHT.put( cn.getSootClass().getName(), classHT );
	 
	  // System.out.println("ADDED in VTableHT :" +  cn.getSootClass().getName() ); 
	} catch ( java.lang.RuntimeException e ) {
	  System.out.println("IN BUILDVtables 3 " /*+ e*/);
	  e.printStackTrace(System.out); 
	}
      }
  }








  /**
   * 
   */
  public SootClassManager getManager(){
    return cm;
  } 









  /**
   * Add a Node for class c in the class Graph
   */

  public ClassNode CreateNodeAndAddToHT( SootClass c ){
    ClassNode cn = new ClassNode( c, this );
    //    System.out.println(" In ClassNodeHT :   " + c.getName() );
    ClassNodeHT.put( c.getName() , cn);
    return cn;
  }
    










  /**
   * return classes ( SootClasses ) directly used in in the class called 'name'
   */

  public List getClassesDirectlyUsed(String Name){

    Jimplifier.CLGB = true;

    // Iterator iter = cm.getClasses().iterator();

    // while ( iter.hasNext() )
    // cm.removeClass( ((SootClass)iter.next()) );
    
    /*
    cm.getClasses().clear();
    */


    try{

      // SootClass bclass = cm.getClass( Name );

      /*
     SootClass bclass = cm.loadClassAndSupport( Name );
     */

     SootClass bclass = cm.getClass( Name );

     // jimplify all methods of the class.

     List methods = bclass.getMethods();

     Iterator methodIt = methods.iterator();

     while(methodIt.hasNext()){

      SootMethod method = (SootMethod) methodIt.next();

       try{
	  
	  //	  SootMethod method = (Method)methodIt.next();
	  
	  //!!! will add bclasses that are referenced by the method into cm.
	  
	  //CHANGE BY VIJAY Jimplifier.jimplify( method );

	  //           if (!(method.getName().equals("<clinit>")))

	  Jimplifier.getJimpleBody( method );

	  // in order to save space delete the jimplified method
	  //Jimplifier.remove( method );
	  
	 } catch ( java.lang.RuntimeException e ) {

          incorrectlyjimplified.add ( Name );

	  System.err.println("\t ------ RuntimeException : Jimple can't handle " + Name +" MTHD "+method.getName()+" :  " + e.getMessage() );

   	 }   
      
      } // WHILE 
    
    } catch ( java.lang.RuntimeException e ){


    }

    Jimplifier.CLGB = false;

    return cm.getClasses();

  }
  
  
  











  /**
   * 
   */
  public void addAllClassesUsedBy( String Name ){
    
    LinkedList workQ = new LinkedList();

    List directlyUsed = null;

    int i = 0;
    
    workQ.addFirst( Name );
    
      
    while( !workQ.isEmpty() )
    {


      String className = (String)workQ.removeFirst();
	
      if(!ClassNodeHT.containsKey( className ) )
      {

        // System.out.println("\n******** Class:  " + className +"   will be in the hierarchy " + ClassNodeHT.size() );
	  
	    directlyUsed = getClassesDirectlyUsed( className );
        
	    try {

	      ClassNode cn = CreateNodeAndAddToHT( cm.getClass(className) );
          cn.setMayBeUsed( true );
          cn.setIsInterface( false );

 	     } catch ( ca.mcgill.sable.soot.NoSuchClassException e ){

           // System.out.println(e.toString()+" IN CLGB.GETALLCLASSESUSED 1");

	     } catch ( ca.mcgill.sable.soot.ClassFileNotFoundException e ){

           // System.out.println(e.toString()+" IN CLGB.GETALLCLASSESUSED 1");

	     }



	     for ( i = 0 ; i < directlyUsed.size() ; i++ )
         {
	    
	      SootClass bClass = (SootClass)directlyUsed.get(i);



	      if( !ClassNodeHT.containsKey( bClass.getName() ) )
          {

           try {

	        ClassNode cn = CreateNodeAndAddToHT( bClass );
            cn.setMayBeUsed( true );
            cn.setIsInterface( false );

 	      } catch ( ca.mcgill.sable.soot.NoSuchClassException e ){

            // System.out.println(e.toString()+" IN CLGB.GETALLCLASSESUSED 1");

	      } catch ( ca.mcgill.sable.soot.ClassFileNotFoundException e ){

            // System.out.println(e.toString()+" IN CLGB.GETALLCLASSESUSED 1");

	      }
          /*

	       if( !workQ.contains(bClass.getName()) )
	       workQ.addLast( bClass.getName() );

           */

	      }
	    
         }
      
       } // IF
	  
     } // WHILE 
  
   }
    


  
  /**
   * Add all superclasses Nodes for node cn in the class Graph
   * and build links
   */
  public void addSuperClassesOf( ClassNode cn ){
    
    ClassNode superNode = null ;
    
    if ( cn.getSootClass().hasSuperClass() )
    {

      SootClass superClass = cn.getSootClass().getSuperClass();


      if ( ( superNode = (ClassNode)ClassNodeHT.get(superClass.getName()) ) == null )
      {	

	addAllClassesUsedBy( superClass.getName() );

	superNode = CreateNodeAndAddToHT( superClass );
	superNode.setMayBeUsed( false );
	superNode.setIsInterface( false );
      }
      
      // recursion
      addSuperClassesOf( superNode );

      cn.SuperNode  = superNode;

      superNode.addSubClass( cn );

    }

    // link all objects whitout superclass to java.lang.Object.
    // ( add java.lang.Object if necessary , but don't link it
    // to itself.
    //
    // !!! this is used when for instance the library
    // is not avalaible.

    else if( !cn.getName().equals("java.lang.Object") ){

      // check if java.lang.Object is already in the HT

      if (  (superNode = (ClassNode)ClassNodeHT
	     .get("java.lang.Object")) == null  )
	
	// cm.getClass creates a SootClass if it doesn't already exist

	superNode = CreateNodeAndAddToHT( cm.getClass("java.lang.Object") );

      { //links nodes
	cn.SuperNode  = superNode;
	superNode.addSubClass( cn );
      }

     }

  }
  












  /**
   * add all super classes of classes present in the classgraph.
   */
  public void addAllSuperClasses(){
    
    Object[] keyArr = ClassNodeHT.keySet().toArray();
    for ( int i = 0 ; i < keyArr.length ; i++ ){
      
      addSuperClassesOf( (ClassNode)ClassNodeHT.get(keyArr[i]) );
      
    }
  }











  /**
   * 
   */
  void addSuperInterfacesOf( ClassNode interfaceNode ){
    
    List superInterfaces = interfaceNode.getSootClass().getInterfaces();
    for ( int i = 0 ; i < superInterfaces.size() ; i++ ){
      
      // if superinterfaces[i] already exists
      ClassNode superIntNode;
      if ( (superIntNode = (ClassNode)ClassNodeHT.get(((SootClass)superInterfaces.get(i)).getName())
	     ) == null ){
	
	//loadExtClass( superInterfaces[i] );
	addAllClassesUsedBy( ((SootClass)superInterfaces.get(i)).getName() );

	superIntNode = CreateNodeAndAddToHT( (SootClass)superInterfaces.get(i) );

      }

      superIntNode.setIsInterface(true);

      // Recursion
      addSuperInterfacesOf( superIntNode );
      
      //Link Nodes
      {
	interfaceNode.addInterface( superIntNode );
	superIntNode.addImplementer( interfaceNode );
      }
    }
  }














  /**
   *
   *
   * @return the set of all subclasses (not only direct sublclasses) of
   * 'classnode'. The returned set contains ClassNode objects.
   */

  public Set getAllSubClassesOf( ClassNode classNode ){
    
    Set ClassSet = new HashSet();
    
    // now get all the subclasses of the base class node
    //Queue workQ = new VectorQueue();
    LinkedList workQ = new LinkedList();

    //workQ.insert( classNode );
    workQ.addFirst( classNode );
    while ( !workQ.isEmpty() ){ 
	
      //classNode = (ClassNode)workQ.next();
      classNode = (ClassNode)workQ.removeFirst();
      
      Set subClasses = classNode.getSubClasses();
      if ( subClasses != null ){
	
	Object[] subNodes = subClasses.toArray();
	for (int i = 0 ; i < subNodes.length ; i++ ){
	  
	  //workQ.insert( subNodes[i] );
	  workQ.addLast( subNodes[i] );
	  
	  ClassSet.add( subNodes[i] );
	    
	}
      }
       
    }
    
    return ClassSet;
  }
  









  /**
   * Starts with the set of classes get so far by building classGraph
   * This set may already contain interfaces, but doesn't contain all of 
   * them. 
   * Thus this method sets all interfaces already detected as interface
   * and adds all the missing ones. ( This includes all superInterfaces )
   */
  public void buildInterfaceGraph(){
    
    ClassNode classNode;

    Object[] keys = ClassNodeHT.keySet().toArray();
    for( int c = 0 ; c < keys.length ; c++ ){

      classNode = (ClassNode)ClassNodeHT.get(keys[c]);

      try{

	List interfaceArr = classNode.getSootClass().getInterfaces();


	for ( int i = 0 ; i < interfaceArr.size() ; i++ ){
	  ClassNode interfaceNode ;
	  SootClass anInterface = (SootClass)interfaceArr.get(i);

	  if ( (interfaceNode = (ClassNode)ClassNodeHT.get(anInterface.getName())) == null ){
	    
	    addAllClassesUsedBy( anInterface.getName() );
	    interfaceNode = CreateNodeAndAddToHT( anInterface );    

	  }
	  
	  interfaceNode.setIsInterface( true );
	  addSuperInterfacesOf( interfaceNode );
	  
	  // Link classNode and its current interfaceNode
	  {
           

	    classNode.addInterface( interfaceNode );
	    interfaceNode.addImplementer( classNode );


	  }
	}
      } catch ( RuntimeException e ){
	System.out.println( e.toString() );
      }
    }
  }    
    









  
  /**
   *
   */
  public void buildClassGraph( String className ){
    
    if( ClassNodeHT.get( className ) == null ){

      addAllClassesUsedBy( className );
      addAllSuperClasses();
  
    }
  }    








  
  /**
   *
   */
  public void buildClassAndInterfaceGraph( String className ){
          
    buildClassGraph( className );
    buildInterfaceGraph();
    buildVirtualTables();
    /*
    for ( int i = 0; i < 20; i++ )
    {

      System.out.println ( " GC BEFORE CALL GRAPH, ITERATION  "+i );
 
      System.gc();

    }

    */

  }    



 public void buildClassAndInterfaceGraph( String className, SootClassManager manager ){

    this.cm = manager;
          
    buildClassGraph( className );
    buildInterfaceGraph();
    buildVirtualTables();
    /*
    for ( int i = 0; i < 20; i++ )
    {

      System.out.println ( " GC BEFORE CALL GRAPH, ITERATION  "+i );
 
      System.gc();

    }
    */
  }    























  //CR
  /**
   * build the class hierarchy ( including interfaces ) using all the 
   * classes that are in the application. 
   */
  public void buildClassAndInterfaceGraph(){
    boolean classFound = false;
    
    //String classPath = ca.mcgill.sable.soot.jimple.Main.jimpleClassPath;

     String classPath = ca.mcgill.sable.soot.Main.sootClassPath;

    char pathSeparator = System.getProperty("path.separator").charAt(0);
    
    char fileSeparator = System.getProperty("file.separator").charAt(0);
    
    // List classPathElements = new VectorList();
    

    List classPathElements = new ArrayList();

    // Split up the class path into classPathElements
    {
      int sepIndex;
      
      for(;;)
	{
	  sepIndex = classPath.indexOf(pathSeparator);
	  
	  if(sepIndex == -1)
	    {
	      classPathElements.add(classPath);
	      break;
	    }   
	  
	  classPathElements.add(classPath.substring(0, sepIndex));
	  
	  classPath = classPath.substring(sepIndex + 1);
	}
    }
    

    String packageName = "ca.mcgill.sable.soot.jimple";

    // for each classPathElement, get and build class hierarchy for all .class
    // in the classPathElement and in its subdirectory.
    {
      for( Iterator classPathElementIter = classPathElements.iterator() ;
	   classPathElementIter.hasNext() ; ){

	String classPathElement = (String) classPathElementIter.next();
	
    	// for gnu tools under windows
	classPathElement = classPathElement.replace( '/', fileSeparator);
	
	String fullPath;
	
	if( classPathElement.
	    endsWith(new Character(fileSeparator).toString())){
	  
	  fullPath = classPathElement + packageName.replace( '.' , 
							     fileSeparator );
	}
	else
	  fullPath =  classPathElement + fileSeparator + 
	    packageName.replace( '.' , fileSeparator );	
	
	File file = new File( fullPath );
	
	if( file.isDirectory() ){
	  
	  String[] fileNames = file.list();
	  
	  for( int i = 0 ; i < fileNames.length ; i++ ){
	    
	    String fileName = fileNames[i];

	    if( fileName.endsWith( ".class" ) ){
	      
	      classFound = true;
	      
	      int extensionIndex = fileName.indexOf( ".class" );
	      
	      fileName = fileName.substring(0 , extensionIndex);
	      
	      //int classPathLastIndex = tempName.
	      //lastIndexOf( classPathElement );
	      String fileNameAsPackage = packageName + "." +  fileName;
	      
	      buildClassAndInterfaceGraph(fileNameAsPackage);
	    }
	  }
	}
      }
    }

    if( !classFound )
      throw new RuntimeException("No classes to be analysed found");
    else
      buildVirtualTables();
  }









  /**
   *
   */
  public void deleteClassNodeHT(){
    Object[] keyArr = ClassNodeHT.keySet().toArray();
    for ( int i = 0 ; i < keyArr.length ; i++ ){
      ((ClassNode)ClassNodeHT.get(keyArr[i])).prepareForGC() ;
    }      
  }

  








  /**
   * 
   */
  public void printContents(){
    // print HT contents
    Object[] keys = ClassNodeHT.keySet().toArray();

    ClassNode classNode;

    // print superClasses relations
    {
      System.out.println(" --- superClasses relations ---");
      for( int i = 0 ; i < keys.length ; i++ ){
	System.out.print( (String)keys[i] + " - " );
	
	// if we are not on java.lang.Object
	// print superNode
	if ( (classNode = (ClassNode)ClassNodeHT.get(keys[i]))
	     .getSuperNode() != null )
	  
	  System.out.println( classNode.getSuperNode().getName() );
	
	else
	  System.out.println();
      }
    }
    
    // print subclasses relation
    {
      System.out.println("******************************");
      System.out.println("**** SUSootClasses relations ****");
      System.out.println("******************************");
      for( int i = 0 ; i < keys.length ; i++ ){
	System.out.println( (String)keys[i] + " : " );
	
	classNode = (ClassNode)ClassNodeHT.get(keys[i]) ;
	try{
	  //if ( classNode.getSubClasses() != null ){
	  Object[] subCArr = classNode.getSubClasses().toArray();
	
	  for( int a = 0 ; a < subCArr.length ; a++ ){
	    System.out.println( "\t" +((ClassNode)subCArr[a])
				.getName() );
	  }
	} catch( NoSuchClassNodeException e ){ }

	System.out.println();
      }
    }

    
    // print interfaces relation
    {
      System.out.println("******************************");
      System.out.println("**** INTERFACES relations ****");
      System.out.println("******************************");
      for( int i = 0 ; i < keys.length ; i++ ){
	System.out.println( (String)keys[i] + " : " );
	
	classNode = (ClassNode)ClassNodeHT.get(keys[i]) ;
	try{
	  //if( classNode.getInterfaces() != null ){
	  Object[] interfArr = classNode.getInterfaces().toArray();
	
	  for( int a = 0 ; a < interfArr.length ; a++ ){
	    System.out.println( "\t" +((ClassNode)interfArr[a])
				.getName() );
	  }
	} catch( NoSuchClassNodeException e ){ }

	System.out.println();
      }
    }
    
    System.out.println( keys.length + " Classes are used" );

  }
  
  public void printSummary(){
    System.out.println( "\nClasses Hierarchy contains:" + ClassNodeHT.size() +
			"  classes");
    System.out.println();
    setClassGraphNumbers();
    System.out.println();
    System.out.println("Num of JIMPLIFIED meth.:  " + Jimplifier.jimplifiedNum );    
  }















  public ClassNode findLeastSuperClass( String cName1 , String cName2 ){

    Set class1SuperClasses = new HashSet();

    try{

      ClassNode currentClassNode = getNode( cName1 );

      while( currentClassNode != null ){
	
	class1SuperClasses.add( currentClassNode );

	currentClassNode = currentClassNode.getSuperNode();

      }


      // step2: go up the hierarchy for cName2 and check if 
      // cName1's set contains cName2's parent

      ClassNode classNode2 = getNode( cName2 );

      while( ! class1SuperClasses.contains( classNode2 ) )
	
	classNode2 = classNode2.getSuperNode();


      return classNode2;


    } catch( NoSuchClassNodeException e ){

      System.err.println( "FindLeastSuperClass: " + e.getMessage() );

      return null;

    }
  }










  public void prepareForGC(){
    deleteClassNodeHT();
    ClassNodeHT = null;
  }


  /**
   * 
   */



  /*public static void main( String args[] ){
    ClassGraphBuilder classgb;
    classgb = new ClassGraphBuilder();
    classgb.buildClassAndInterfaceGraph( args[0] );
    classgb.printContents();
    
    CallGraphBuilder callgb = new CallGraphBuilder( classgb );
    SootClassCFG jCFG = new  JClassCFG( args[0] );
    MethodCFG mCFG = null;
    try{
    if ( args.length == 1 )
    mCFG = jCFG.getMethodCFG( "main-java.lang.String[]" );
    else
    mCFG = jCFG.getMethodCFG( args[1] );

    callgb.buildCallGraph( mCFG );
    callgb.printContents();
    }catch ( NoSuchMethodCFGException e ){
    System.out.println( e.getMessage() );
    }

    }*/








}






























