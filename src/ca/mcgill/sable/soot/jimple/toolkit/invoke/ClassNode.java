// package ca.mcgill.sable.soot.sideEffect;

package ca.mcgill.sable.soot.jimple.toolkit.invoke;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.baf.*;

public class ClassNode{
  boolean mayBeUsed;
  ClassNode SuperNode;
 
  /**
   * true only if this node is an Interface
   */
  boolean isInterface;

  /**
   * 
   */
  String name;
  
  /**
   * 
   */ 
  SootClass bclass;
  
  /**
   *
   */
  Set subClasses = new ArraySet();


  /**
   * All interfaces implemented either by a class or by another interface.
   * Note: java doesn't allow interfaces to implement other interfaces,
   * but to extend them. However in Jimple interfaces "implement" rather than "extend" other interfaces. 
   */
  Set interfaces = new HashSet();


  /**
   * Only used if this node is an Interface.
   * Points to all classes or interfaces that implements 
   * this interface.
   */
  Set implementedBy = new HashSet();

  

  /*public ClassNode(String name){
    this.name = name;
  }
  */

  /**
   * 
   */





  ClassGraphBuilder clgb;






  public ClassNode( SootClass jc, ClassGraphBuilder clgb ){
    this.name = jc.getName();

    this.clgb = clgb;

    this.bclass = jc;

  //  if( Main.SPEED )
  //    this.bclass = jc;
  }


  /**
   * 
   */
  public String getName(){
    return name;
  }

  /**
   * 
   */
  public Set getSubClasses(){
    return subClasses;
  }


  /**
   * adds a subnode to this Classgraphnode
   */
  public void addSubClass( ClassNode  sc ){
    if( subClasses == null )
      subClasses = new HashSet();

    subClasses.add(sc);
  }

  
  /**
   * adds an interfacenode to this Classgraphnode
   */
  public void addInterface( ClassNode  sc ){
    if( interfaces == null )
      interfaces = new HashSet();

    interfaces.add(sc);
  }


  public Set getInterfaces(){
    //    if( interfaces == null )
    //throw new ca.mcgill.sable.soot.sideEffect.NoSuchClassNodeException();
    return interfaces;
  }


  public void setMayBeUsed( boolean b ){
    mayBeUsed = b;
  }

  
  public ClassNode getSuperNode(){
    return SuperNode;
  }


  public SootClass getSootClass(){
    //bclass will be null if Main.SPEED is false
//    if( bclass == null )
//      return this.clgb.getManager().getClass( name );

    return bclass;
  }


  public void setIsInterface( boolean bool ){ 
    isInterface = bool ; 
  }

  //DEBUG
  public void setSootClassToNull(){
    bclass = null;
  }
  
  /**
   * Only used if this node is an Interface
   */
   // !!! Name sounds really bad.
  public void addImplementer( ClassNode classNode ){
    if ( implementedBy == null )
      implementedBy = new HashSet();

    implementedBy.add( classNode );
  }

  
  /**
   * Only used if this node is an Interface.
   */
  //!!! Name sounds really bad.
  public Set getImplementers( ){
    //    if( implementedBy == null )
    //throw new ca.mcgill.sable.soot.sideEffect.NoSuchClassNodeException();
    return implementedBy;
  }


  public boolean isInterface(){
    return isInterface;
  }


  public void prepareForGC(){
	SuperNode = null;
	//bclass = null;
	subClasses = null;
	interfaces = null;
	implementedBy = null;
    }


}








