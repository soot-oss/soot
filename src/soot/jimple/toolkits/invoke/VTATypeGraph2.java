/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.jimple.toolkits.invoke;

import java.util.*;
import soot.util.*;
import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.*;

/** A graph where the nodes are types in the analysed program;
 * edges indicate that there is assignment between two types. 
 */

/* 2nd implementation for points-to. Difference from the 1st impl.
 *
 *   a) Use unique TypeGraphNode2 as graph node rather than Strings,
 *      TypeGraphNode2 has fast hashCode, and equal check.
 *
 *   b) Use native method package as other points-to analyses.
 */

public class VTATypeGraph2 extends MemoryEfficientGraph 
  implements TypeGraph {
  
  int state;
  int edges;
  Scene sc;

  // HashMap nodeToReachingTypes = new HashMap();
  private HashMap nodeToDeclaredType = new HashMap();
  private HashMap castEdges = new HashMap();
  private HashSet arrayNodes = new HashSet(0);

  public void addNode(TypeGraphNode2 node) {
    super.addNode(node);
  }

  public void addEdge(TypeGraphNode2 src, TypeGraphNode2 dst) {
    super.addEdge(src, dst);
    edges++;
  }

  public void addNode(TypeGraphNode2 node, Type decltype){
    super.addNode(node);
    nodeToDeclaredType.put(node, decltype);
  }

  public TypeSet2 getReachingTypesOf(Object node){
    return ((TypeGraphNode2)node).getTypeSet2();
  }

  /* @param is expecting to be a node.
   */
  public Object getDeclaredTypeOf(TypeGraphNode2 node){
    return nodeToDeclaredType.get(node);
  }

  public int numEdges() {
    return edges;
  }

  /** Returns true if t is RefType or ArrayType. */
  public static boolean isRefLikeType(Type t) {
    return (t instanceof RefType) || (t instanceof ArrayType);
  }

  public VTATypeGraph2(InvokeGraph ig) {
    this.sc = Scene.v();
    state = sc.getState();
    edges = 0;

    // Construct nodes of graph.
    {
      Chain allClasses = sc.getClasses();

      Iterator classesIt = allClasses.iterator();
      while (classesIt.hasNext()) {
	SootClass c = (SootClass)classesIt.next();

	/* Snarky comment about interfaces indeed possibly having
	 * fields goes here.  Add fields of c. */
	Iterator fieldsIt = c.getFields().iterator();

	while (fieldsIt.hasNext()) {
	  SootField f = (SootField)fieldsIt.next();

	  if (isRefLikeType(f.getType())) {
	    TypeGraphNode2 node = TypeGraphNode2.v(getVTALabel(f));
	    addNode(node);
	    nodeToDeclaredType.put(node, f.getType());
	    if (f.getType() instanceof ArrayType)
	      arrayNodes.add(node);
	  }
	}

        /* More snarky comments about interfaces having a concrete
	 * <clinit> method (since it can initialize fields).
	 */
	if (c.declaresMethod("void <clinit>()")) {
	  SootMethod m = c.getMethod("void <clinit>()");
	  if (!m.isConcrete())
	    continue;
	  if (!ig.mcg.isReachable(m.toString()))
	    continue;

	  // Get the body & add the locals.
	  Body b = m.retrieveActiveBody();

	  Iterator localIt = b.getLocals().iterator();
	  while (localIt.hasNext()) {
	    Local l = (Local)localIt.next();
	    Type t = l.getType();
	    if (isRefLikeType(t)) {
	      String s = getVTALabel(m, l);
	      if (s!=null) {
		TypeGraphNode2 node = TypeGraphNode2.v(s);
		addNode(node);
		nodeToDeclaredType.put(node, t);
	      }                              
	    }
	  }

	  TypeGraphNode2 node = TypeGraphNode2.v(getVTALabel(m, "throw"));
	  addNode(node);
	  nodeToDeclaredType.put(node, m.getExceptions());
	}                    

	if (c.isInterface())
	  continue;

	// Add nodes for method contents, if it is reachable.
	Iterator methodsIt = c.getMethods().iterator();

	while (methodsIt.hasNext()) {
	  SootMethod m = (SootMethod)methodsIt.next();
                    
	  if (m.getSubSignature().equals("void <clinit>()"))
	    continue;

	  if (!ig.mcg.isReachable(m.toString()))
	    continue;

	  // For instance methods, add "this" node.
	  if (!m.isStatic()) {
	    TypeGraphNode2 node = TypeGraphNode2.v(getVTALabel(m, "this"));
	    addNode(node);
	    nodeToDeclaredType.put(node, TypeElement2.v(m.getDeclaringClass()));
	    if (m.getSubSignature().equals("void <init>()")) {

	      TypeSet2 t = node.getTypeSet2();
	      t.add(TypeElement2.v(m.getDeclaringClass()));
	    }
	  }
                        
	  // Add return node, if appropriate.
	  if (isRefLikeType(m.getReturnType())) {
	    TypeGraphNode2 node = TypeGraphNode2.v(getVTALabel(m, "return"));
	    addNode(node);
	    nodeToDeclaredType.put(node, m.getReturnType());
	  }

	  // Add the parameters.
	  Iterator paramIt = m.getParameterTypes().iterator();
	  int paramCount = 0;
	  while (paramIt.hasNext()) {
	    Type t = (Type)paramIt.next();
	    if (isRefLikeType(t)) {
	      TypeGraphNode2 node = 
		TypeGraphNode2.v(getVTALabel(m, "p"+paramCount));
	      addNode(node);
	      nodeToDeclaredType.put(node, t);
	    }
	    paramCount++;
	  }

	  // Add throw node (to keep track of exceptions thrown by the method).
	  {
	    TypeGraphNode2 node = TypeGraphNode2.v(getVTALabel(m, "throw"));
	    addNode(node);
	    nodeToDeclaredType.put(node, m.getExceptions());
	  }

	  if (!m.isConcrete())
	    continue;

	  // Get the body & add the locals.
	  Body b = m.retrieveActiveBody();

	  Iterator localIt = b.getLocals().iterator();
	  while (localIt.hasNext()) {
	    Local l = (Local)localIt.next();
	    Type t = l.getType();
	    if (isRefLikeType(t)) {
	      String s = getVTALabel(m, l);
	      if (s!=null) {
		TypeGraphNode2 node = TypeGraphNode2.v(s);
		addNode(node);
		nodeToDeclaredType.put(node, t);
	      }                              
	    }                        
	  }
	}
      }
    }

    // Add edges.
    {
      Chain allClasses = sc.getClasses();
      HashMap methodToReturnStmts = 
	new HashMap(allClasses.size() * 8 + 1, 0.7f);

      Iterator classesIt = allClasses.iterator();
      while (classesIt.hasNext()) {
	SootClass c = (SootClass)classesIt.next();

	if (c.isInterface())
	  continue;

	/* solution, report to user that needs -a switch */
	if (c.isContextClass()) {
	  throw new RuntimeException("VTA needs '-a' or '--analyze-context' switch to run correctly.");
	}

	// The following are entry points that have to be treated specially
	SootMethod finalizer = null;
	if (c.declaresMethod("void finalize()"))
	  finalizer = c.getMethod("void finalize()");

	// Add types for parameter (String[]) in main method.
	{
	  LinkedList l = new LinkedList();
	  l.add(ArrayType.v(RefType.v("java.lang.String"),1));
	  String subsig = SootMethod.getSubSignature("main", l, VoidType.v());
	  if (c.declaresMethod(subsig)) {
	    SootMethod m = c.getMethod("main", l, VoidType.v());
	    String label = getVTALabel(m, "p0");
	    TypeGraphNode2 node = TypeGraphNode2.v(label);
	    TypeSet2 rt = node.getTypeSet2();
	    rt.add(TypeElement2.v("java.lang.Object"));
	    rt.add(TypeElement2.v("java.lang.String"));
	    arrayNodes.add(node);
	  }
	}

	// Set up hierarchy (used in conservative approximation of 
	// exception types).
	Hierarchy h = null;
	if (!Scene.v().hasActiveHierarchy()) {
	  h = new Hierarchy();
	  Scene.v().setActiveHierarchy(h);
	}
	else
	  h = Scene.v().getActiveHierarchy();

	// prepare NativeHelper
	VTANativeHelper2 nhelper = new VTANativeHelper2();
	nhelper.vtg = this;
	nhelper.h   = h;
	VTANativeMethodWrapper2.initialize(nhelper);
	  
	// Add edges for method contents, if it is reachable.
	Iterator methodsIt = c.getMethods().iterator();

	while (methodsIt.hasNext()) {
	  SootMethod m = (SootMethod)methodsIt.next();
	  if (!ig.mcg.isReachable(m.toString()))
	    continue;

	  // Add reaching types to the "this" node of methods corresponding 
	  // to the entry points run and exit.
                    
	  if (m.getName().equals("run")) {

	    // First check if it implements Runnable.
	    boolean runnable = false;
	    Iterator cIt = h.getSuperclassesOfIncluding(c).iterator();
	    while (cIt.hasNext()) {
	      SootClass clazz = (SootClass)cIt.next();
	      if (clazz.getInterfaces().contains(Scene.v().getSootClass("java.lang.Runnable"))) {
		runnable = true;
		break;
	      }
	    }

	    if (runnable) {
	      cIt = h.getSubclassesOfIncluding(c).iterator();
	      while (cIt.hasNext()) {
		SootClass clazz = (SootClass)cIt.next();
		SootMethod meth = h.resolveConcreteDispatch(clazz, 
		     Scene.v().getMethod("<java.lang.Runnable: void run()>"));
		if (meth.equals(m)) {
		  TypeGraphNode2 node = TypeGraphNode2.v(getVTALabel(m, "this"));
		  TypeSet2 l = node.getTypeSet2();
		  l.add(TypeElement2.v(clazz));
		}
	      }

	      if (c.declaresMethod("void exit()")) {
		SootMethod meth = c.getMethod("void exit()");
		TypeGraphNode2 node1 =TypeGraphNode2.v(getVTALabel(m, "this"));
		TypeGraphNode2 node2 =TypeGraphNode2.v(getVTALabel(meth,"this"));
		addEdge(node1, node2);
	      }
	    }
	  }
                                
	  // Add edges and exception types for native methods
	  if (!m.isConcrete()){

	    if (!m.isNative())
	      continue;

	    VTANativeMethodWrapper2.collect(m);
	    
	    if (!isRefLikeType(m.getReturnType()))
	      continue;
	    
	    Iterator siteIt = ig.getCallingSitesOf(m).iterator();
	    while (siteIt.hasNext()) {
	      Stmt caller = (Stmt)siteIt.next();
	      if (caller instanceof AssignStmt) {
		SootMethod declaringMethod = ig.getDeclaringMethod(caller);
		if (ig.mcg.isReachable(declaringMethod.toString())) {
		  TypeGraphNode2 node1=TypeGraphNode2.v(getVTALabel(m,"return"));
		  TypeGraphNode2 node2=TypeGraphNode2.v(
					 getVTALabel(
					    declaringMethod,
				            ((AssignStmt)caller).getLeftOp()));
		  addEdge(node1, node2);
		}
	      }
	    }
            
	    {
	      String label = getVTALabel(m, "throw");
	      
	      TypeGraphNode2 node = TypeGraphNode2.v(label);
	      TypeSet2 rt = node.getTypeSet2();
              
	      Collection decltypes = (Collection)nodeToDeclaredType.get(node);
	      LinkedList st = new LinkedList(decltypes);

	      while(!st.isEmpty()) {
		SootClass cls = (SootClass)st.removeLast();
		if(cls.isInterface())
		  st.addAll(h.getImplementersOf(cls));
		else {
		  Iterator clsIt = h.getSubclassesOfIncluding(cls).iterator();
		  while (clsIt.hasNext()) {
		    rt.add(TypeElement2.v((SootClass)clsIt.next()));
		  }
		}
	      }
	    }

	    continue;
	  }

	  // now process concreate methods
	  String methodSig = m.getSignature();
	  JimpleBody b = (JimpleBody)m.retrieveActiveBody();

	  // Look for assignStmts and method calls.
	  Iterator unitsIt = b.getUnits().iterator();

	  while (unitsIt.hasNext()) {
	    Stmt s = (Stmt) unitsIt.next();

	    // Add an edge for throw statements.
	    if (s instanceof ThrowStmt) {
	      Value v = ((ThrowStmt)s).getOp();
	      TypeGraphNode2 node1 = TypeGraphNode2.v(getVTALabel(m, v));
	      TypeGraphNode2 node2 = TypeGraphNode2.v(getVTALabel(m, "throw"));
	      addEdge(node1, node2);
	    }       

	    // Add an edge for method invocations.
	    if (s.containsInvokeExpr()) {
	      InvokeExpr ie = (InvokeExpr)s.getInvokeExpr();

	      List targetMethods = ig.getTargetsOf(s);
	      Iterator targetsIt = targetMethods.iterator();

	      while (targetsIt.hasNext()) {
		SootMethod target = (SootMethod)targetsIt.next();

		// Link base object with this.
		if (ie instanceof InstanceInvokeExpr) {		  
		  TypeGraphNode2 node1 = 
		    TypeGraphNode2.v(getVTALabel(m, 
				((InstanceInvokeExpr)ie).getBase()));
		  TypeGraphNode2 node2 = 
		    TypeGraphNode2.v(target.getSignature()+"$this");
		  addEdge(node1, node2);
		}

		/* Add parameter bindings Remember to add double edges
		 * for arrays/Objects even for parameters!  */
		Iterator paramIt = target.getParameterTypes().iterator();
		int paramCount = 0;
		while (paramIt.hasNext()) {
		  Type t = (Type)paramIt.next();
		  if (isRefLikeType(t)) {
		    if(ie.getArg(paramCount) instanceof Local
		       && isRefLikeType(((Local)ie.getArg(paramCount)).getType())) {
		      Type argType = ie.getArg(paramCount).getType();
		      
		      TypeGraphNode2 node1 =
			TypeGraphNode2.v(getVTALabel(m, ie.getArg(paramCount)));
		      TypeGraphNode2 node2 =
			TypeGraphNode2.v(target.getSignature()+"$p"+paramCount);
		      addEdge(node1, node2);
		    } else 
		    if (ie.getArg(paramCount) instanceof StringConstant) {
		      TypeGraphNode2 node = 
			TypeGraphNode2.v(target.getSignature()+"$p"+paramCount);
		      TypeSet2 ts = node.getTypeSet2();
		      ts.add(TypeElement2.v("java.lang.String"));
		    }
		  }
		  paramCount++;
		}
	      }
	    }

	    // Add edges corresponding to return statements.

	    if (s instanceof ReturnStmt 
		&& isRefLikeType(((ReturnStmt)s).getOp().getType())
		&& ((ReturnStmt)s).getOp() instanceof Local) {
	      Iterator siteIt = ig.getCallingSitesOf(m).iterator();
	      while (siteIt.hasNext()) {
		Stmt caller = (Stmt)siteIt.next();
		if (caller instanceof AssignStmt) {
		  SootMethod declaringMethod = ig.getDeclaringMethod(caller);
		  if (ig.mcg.isReachable(declaringMethod.toString())) {
		    TypeGraphNode2 node1 =
		      TypeGraphNode2.v(getVTALabel(m, ((ReturnStmt)s).getOp()));
		    TypeGraphNode2 node2 =
		      TypeGraphNode2.v(getVTALabel(declaringMethod,
				     ((AssignStmt)caller).getLeftOp()));
		    addEdge(node1, node2);
		  }
		}
	      }
	    }


	    // Add edges corresponding to identityStmts.

	    if (s instanceof IdentityStmt) {
	      IdentityStmt is = (IdentityStmt)s;
	      Value lhs = is.getLeftOp(), rhs = is.getRightOp();
	      if (rhs instanceof ThisRef) {

		addEdge(TypeGraphNode2.v(methodSig+"$this"), 
			TypeGraphNode2.v(getVTALabel(m, lhs)));

		// I have no clue whether or not this is right.
		if (m.getName().equals("<init>") && finalizer != null) {
		  addEdge(TypeGraphNode2.v(methodSig+"$this"), 
			  TypeGraphNode2.v(finalizer.getSignature()+"$this"));
		}
	      } else if (rhs instanceof ParameterRef 
			 && isRefLikeType(rhs.getType())) {
		TypeGraphNode2 node1 = TypeGraphNode2.v(methodSig
						      +"$p"
					+((ParameterRef)rhs).getIndex());
		TypeGraphNode2 node2 = TypeGraphNode2.v(getVTALabel(m, lhs));
		
		addEdge(node1, node2);

	      } else if (rhs instanceof CaughtExceptionRef) {
		// Include all subtypes of RuntimeException and 
		// Error in CaughtExceptionRefs.
		TypeSet2 set = TypeGraphNode2.v(getVTALabel(m, lhs)).getTypeSet2();
		Iterator clsIt = h.getSubclassesOfIncluding(
		     Scene.v().getSootClass("java.lang.Error")).iterator(); 
		
		while (clsIt.hasNext()) {
		  set.add(TypeElement2.v((SootClass)clsIt.next()));
		}

		Iterator subclsIt = h.getSubclassesOfIncluding(
                    Scene.v().getSootClass("java.lang.RuntimeException")).iterator();
                while (subclsIt.hasNext()) {
		  set.add(TypeElement2.v((SootClass)subclsIt.next()));
		}
	      }
	    }

	    // Add edges corresponding to assignStmts.

	    if (s instanceof AssignStmt) {
	      AssignStmt as = (AssignStmt)s;
	      Value lhs = as.getLeftOp(), rhs = as.getRightOp();

	      if (isRefLikeType(lhs.getType())) {
		String lhsRep = getVTALabel(m, lhs); 
		String rhsRep = (rhs instanceof CastExpr)? 
		  getVTALabel(m, ((CastExpr)rhs).getOp()) : 
		  getVTALabel(m, rhs);

		if (lhsRep==null)
		  continue;

		// Remember cast expressions (useful for native adjusts)
		if (rhs instanceof CastExpr) {
		  if (!castEdges.containsKey(rhsRep))
		    castEdges.put(rhsRep, new LinkedList());
		  LinkedList lizt = (LinkedList)castEdges.get(rhsRep);
		  lizt.add(new NodeTypePair(lhsRep, 
					    ((CastExpr)rhs).getCastType()));
		} else if (rhs instanceof StringConstant) {
		  // add to the reachingTypes sets.
		  TypeSet2 l = TypeGraphNode2.v(lhsRep).getTypeSet2();
		  if (!l.contains(TypeElement2.v("java.lang.String")))
		    l.add(TypeElement2.v("java.lang.String"));
		} else if (rhs instanceof NewExpr) {
		  TypeSet2 l = TypeGraphNode2.v(lhsRep).getTypeSet2();
		  l.add(TypeElement2.v((RefType)((NewExpr)rhs).getType()));
		} else if (rhs instanceof NewArrayExpr) {
		  TypeSet2 l = TypeGraphNode2.v(lhsRep).getTypeSet2();
		  if (!l.contains(TypeElement2.v("java.lang.Object")))
		    l.add(TypeElement2.v("java.lang.Object"));
		  arrayNodes.add(TypeGraphNode2.v(lhsRep));
		  
		} else if (rhs instanceof NewMultiArrayExpr) {
		  TypeSet2 l = TypeGraphNode2.v(lhsRep).getTypeSet2();
		  if (!l.contains(TypeElement2.v("java.lang.Object")))
		    l.add(TypeElement2.v("java.lang.Object"));
		  arrayNodes.add(TypeGraphNode2.v(lhsRep));
		}
			      
		// check that we have the right form: by right form we
		// mean that it's actually an rvalue (not a NewExpr, 
		// a StringConstant or something similar.
		if (rhsRep == null)
		  continue;			      
		addEdge(TypeGraphNode2.v(rhsRep), 
			TypeGraphNode2.v(lhsRep));

	      }
	    }
	  }

	  // Add edges corresponding to throw/catch pairs.
	  {
	    Chain trapChain = b.getTraps();
	    PatchingChain ch = b.getUnits();
	    Iterator trapsIt = trapChain.iterator(); 
	    while (trapsIt.hasNext()) {
	      Trap trap = (Trap)trapsIt.next();
	      Unit start = trap.getBeginUnit();
	      Unit end = trap.getEndUnit();
	      IdentityStmt handler = (IdentityStmt)trap.getHandlerUnit();
	      Value lhs = handler.getLeftOp();
	      for (Iterator uIt = ch.iterator(start, end); uIt.hasNext(); ) {
		Stmt stmt = (Stmt)uIt.next();
		if (stmt.containsInvokeExpr()) {
		  List targetMethods = ig.getTargetsOf(stmt);
		  Iterator targetsIt = targetMethods.iterator();
		  while (targetsIt.hasNext()) {
		    SootMethod meth = (SootMethod)targetsIt.next();
		    addEdge(TypeGraphNode2.v(getVTALabel(meth, "throw")), 
			    TypeGraphNode2.v(getVTALabel(m, lhs)));
		  }
		}
	      }
	    }
	  }
	}
      }
    }

    Date start = new Date();

    // Add back edges to array type variables 
    // (including Objects that are possibly assigned array types).
    {
      LinkedList st = new LinkedList(arrayNodes);
      LinkedList parents = new LinkedList();
      Iterator it = st.iterator(); 
      while (it.hasNext()) {
	it.next();
	// to signal that these are roots of the DFS forest.
	parents.addLast(new Integer(5));  
      }

      HashMap nodeToColor = new HashMap();
      Integer GREY  = new Integer(0);
      Integer BLACK = new Integer(1);

      // Only add edges after the whole graph is traversed; 
      // we don't want to change the graph as we go!
      LinkedList from = new LinkedList();
      LinkedList to = new LinkedList();

      while(!st.isEmpty()) {
	Object o = st.getLast();
	if (!nodeToColor.containsKey(o)) {
	  // NODE IS WHITE
	  nodeToColor.put(o, GREY);
	  for (Iterator succsIt = ((List)getSuccsOf(o)).iterator(); 
	       succsIt.hasNext(); ) {
	    Object child = succsIt.next();
	    Type t = (Type)nodeToDeclaredType.get(child);
	    if (t instanceof ArrayType 
		|| t.equals(TypeElement2.v("java.lang.Object"))) {
	      if (!nodeToColor.containsKey(child)) {
		st.addLast(child);
		parents.addLast(o);
	      } else {
		from.addLast(child);
		to.addLast(o);
	      }
	    }
	  }
	} else if (nodeToColor.get(o) == GREY) {
	  // WE ARE DONE WITH ALL THE DESCENDENTS
	  Object parent = parents.removeLast();
	  if (!(parent instanceof Integer)) {
	    from.addLast(o);
	    to.addLast(parent);
	  }
	  nodeToColor.put(st.removeLast(), BLACK);
	} else {
	  // Black node: either we hit a cross edge, 
	  // or this entry point has been discovered.
	  Object parent = parents.removeLast();
	  if (!(parent instanceof Integer)) {
	    from.addLast(o);
	    to.addLast(parent);
	  }
	  st.removeLast();
	}
      }

      // Now add the back edges.
      Iterator fromIt = from.iterator();
      Iterator toIt = to.iterator();
      while (fromIt.hasNext())
	addEdge((TypeGraphNode2)fromIt.next(), (TypeGraphNode2)toIt.next());
    }                
  }

      
  /** Returns the name of the VTA node corresponding to the given field. */
  static String getVTALabel(SootField f) {
    return f.getSignature();
  }

  /** Returns the name of the VTA node corresponding to the given method/value pair. */
    static String getVTALabel(SootMethod m, Value v)
    {
        // In Jimple, ArrayRef must have a Local base, so this is fine.
        if ((v instanceof ArrayRef) && (isRefLikeType(((ArrayRef)v).getType())))
            return m.getSignature()+"$$"+((Local)((ArrayRef)v).getBase()).getName();
        else if (v instanceof Local && isRefLikeType(((Local)v).getType()))
            return m.getSignature()+"$$"+((Local)v).getName();
        else if (v instanceof FieldRef)
            return ((FieldRef)v).getField().getSignature();
	return null;
    }

    /** Returns the name of the VTA node corresponding to the given method, and flagged with <code>id</code>. */
    static String getVTALabel(SootMethod m, String id)
    {
        return m.getSignature() + "$" + id;
    }

    /** Returns the signature of a SootMethod given a VTALabel */
    static String labelToMethodSignature(String label) {
        if (label.charAt(label.length()-1)=='>') return null;
        else {
            StringTokenizer strtok = new StringTokenizer(label,"$");
            return strtok.nextToken();
        }
    }

    private void checkState()
    {
        if (state != sc.getState())
            throw new ConcurrentModificationException("Scene changed for VTATypeGraph2!");
    }

}

