// package ca.mcgill.sable.soot.sideEffect;

package ca.mcgill.sable.soot.jimple.toolkit.invoke;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.baf.*;
class ClassNode{
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
   ClassNode( SootClass jc, ClassGraphBuilder clgb ){
      this.name = jc.getName();
      this.clgb = clgb;
      this.bclass = jc;
      //  if( Main.SPEED )
      //    this.bclass = jc;
   }


   /**
   *
   */
   String getName(){
      return name;
   }


   /**
   *
   */
   Set getSubClasses(){
      return subClasses;
   }


   /**
   * adds a subnode to this Classgraphnode
   */
   void addSubClass( ClassNode sc ){
      if( subClasses == null )
      subClasses = new HashSet();
      subClasses.add(sc);
   }


   /**
   * adds an interfacenode to this Classgraphnode
   */
   void addInterface( ClassNode sc ){
      if( interfaces == null )
      interfaces = new HashSet();
      interfaces.add(sc);
   }


   Set getInterfaces(){
      //    if( interfaces == null )
      //throw new ca.mcgill.sable.soot.sideEffect.NoSuchClassNodeException();
      return interfaces;
   }


   void setMayBeUsed( boolean b ){
      mayBeUsed = b;
   }


   ClassNode getSuperNode(){
      return SuperNode;
   }


   SootClass getSootClass(){
      //bclass will be null if Main.SPEED is false
      //    if( bclass == null )
      //      return this.clgb.getManager().getClass( name );

      return bclass;
   }


   void setIsInterface( boolean bool ){
      isInterface = bool ;
   }


   //DEBUG
   void setSootClassToNull(){
      bclass = null;
   }


   /**
   * Only used if this node is an Interface
   */
   // !!! Name sounds really bad.
   void addImplementer( ClassNode classNode ){
      if ( implementedBy == null )
      implementedBy = new HashSet();
      implementedBy.add( classNode );
   }


   /**
   * Only used if this node is an Interface.
   */
   //!!! Name sounds really bad.
   Set getImplementers( ){
      //    if( implementedBy == null )
      //throw new ca.mcgill.sable.soot.sideEffect.NoSuchClassNodeException();
      return implementedBy;
   }


   boolean isInterface(){
      return isInterface;
   }


   void prepareForGC(){
      SuperNode = null;
      //bclass = null;
      subClasses = null;
      interfaces = null;
      implementedBy = null;
   }


}




