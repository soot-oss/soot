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

/** An implementation of Variable Type Analysis (as defined in Vijay Sundaresan's thesis). */

public class VariableTypeAnalysis2
{

  VTATypeGraph2 vtg;

  HashMap superNodesToReachingTypes;
  InvokeGraph ig;

  StronglyConnectedComponents scc;
  DirectedGraph superGraph;
  MethodCallGraph mcg;

  Date VTAStart, VTAFinish;

  TypeSet2 computeReachingTypes(List superNode) {
    TypeSet2 retVal = new TypeSet2();
    Iterator snIt = superNode.iterator();

    while (snIt.hasNext()) {
      Object o = snIt.next();
      TypeSet2 reachingTypes = vtg.getReachingTypesOf(o);
      retVal.addAll(reachingTypes);
    }

    return retVal;
  }

  TypeSet2 computeConservativeReachingTypes(Hierarchy h, List superNode) {
    
    TypeSet2 retVal = new TypeSet2();
    Iterator snIt = superNode.iterator();
        
    LinkedList st = new LinkedList();
    snIt = superNode.iterator(); 
    while (snIt.hasNext()) {
      Object o = snIt.next();

      Type t = (Type)vtg.getDeclaredTypeOf((TypeGraphNode2)o);

      if(t instanceof ArrayType) {
	retVal.add(RefType.v("java.lang.Object"));
	if(((ArrayType)t).baseType instanceof RefType)
	  st.addLast(((RefType)((ArrayType)t).baseType).getSootClass());
      } else {
	st.addLast(((RefType)t).getSootClass());
      }

      while(!st.isEmpty()) {
	SootClass c = (SootClass)st.removeLast();
	if(c.isInterface())
	  st.addAll(h.getImplementersOf(c));
	else
	  for (Iterator classIt = h.getSubclassesOfIncluding(c).iterator(); classIt.hasNext(); )
	    retVal.add(RefType.v((SootClass)classIt.next()));
      }
    }
        
    return retVal;
  }

  public List getReachingTypesOf(Object o) {

    // check type for 
    o = (TypeGraphNode2)o;

    TypeSet2 reachingTypes = 
      (TypeSet2)superNodesToReachingTypes.get(scc.getComponentOf(o));
 
    if (reachingTypes==null) return null;
   
    Hierarchy h = Scene.v().getActiveHierarchy();

    List validReachingTypes = new LinkedList();
   
    Type t = (Type)vtg.getDeclaredTypeOf((TypeGraphNode2)o);
    Type declaredType = t;

    if (t instanceof ArrayType) 
      t = ((ArrayType)t).baseType;

    if (t instanceof RefType) {
      SootClass cls = ((RefType)t).getSootClass();

      if (t.equals(RefType.v("java.lang.Object"))) {

	Iterator rtIt = reachingTypes.iterator();
	while (rtIt.hasNext()) {
	  TypeElement2 te = (TypeElement2)rtIt.next();
	  
	  validReachingTypes.add(te.getRefType());
	}
      }  else {
	for( Iterator rtIt = reachingTypes.iterator(); rtIt.hasNext(); ) {
	    final TypeElement2 rt = (TypeElement2) rtIt.next();
	  SootClass subCls = ((RefType) rt.getType()).getSootClass();
	  if (cls.isInterface()) {
	    boolean found = false;
	    List classList = h.getSuperclassesOfIncluding(subCls);
	    List interfaceList = h.getSubinterfacesOfIncluding(cls);
	    Iterator classesIt = classList.iterator(); 
	    while (classesIt.hasNext() && !found) {
	      SootClass superclass = (SootClass)classesIt.next();
	      Iterator interfacesIt = interfaceList.iterator(); 
	      while (interfacesIt.hasNext() && !found) {
		if (superclass.getInterfaces().contains(interfacesIt.next())) {
		  validReachingTypes.add(rt.getRefType());
		  found = true;
		}
	      }
	    }
	  } 
	  else if (h.isClassSubclassOfIncluding(subCls, cls))
	    validReachingTypes.add(rt.getRefType());
	}
      }
    }

    if (declaredType instanceof ArrayType 
	&& !t.equals(RefType.v("java.lang.Object")))
      validReachingTypes.add(RefType.v("java.lang.Object"));
    return validReachingTypes;
  }   

  /** Constructs a VariableTypeAnalysis2 object for the given
   * InvokeGraph.  Calling trimInvokeGraph will modify the
   * associated invokeGraph according to this VTA's results. 
   */
  public VariableTypeAnalysis2(InvokeGraph ig) {
    Date start, finish;

    start = new Date();
    VTAStart = start;
    //    if (Main.isVerbose) 
    {
      System.out.println("[vta2] VTA started on "+start);
      System.out.println("[vta2] Constructing Variable Type Analysis graph.");
    }

    this.ig = ig;
    vtg = new VTATypeGraph2(ig);

    finish = new Date();
    //    if (Main.isVerbose) 
    {
      System.out.println("[vta2] VTA graph has "+vtg.size()+" nodes and "+vtg.numEdges()+" edges.");
      long runtime = finish.getTime()-start.getTime();
      System.out.println("[vta2] Graph construction took "+
			 (runtime/60000)+" min. "+
			 ((runtime%60000)/1000)+" sec.");
      System.out.println("[vta2] Computing strongly connected components.");
    }
    start = finish;

        // ha!
    scc = new StronglyConnectedComponents(vtg);

    finish = new Date();
    //    if (Main.isVerbose) 
    {
      long runtime = finish.getTime()-start.getTime();
      System.out.println("[vta2] SCC took "+
			 (runtime/60000)+" min. "+
			 ((runtime%60000)/1000)+" sec.");
      System.out.println("[vta2] Propagating types.");
    }
    start = finish;

    // Now we need a new graph.
    superGraph = scc.getSuperGraph();

    Hierarchy h = Scene.v().getActiveHierarchy();
    visitNodes(h, superGraph);

    finish = new Date();
    //    if (Main.isVerbose) 
    {
      long runtime = finish.getTime()-start.getTime();
      System.out.println("[vta2] Type propagation took "+
			 (runtime/60000)+" min. "+
			 ((runtime%60000)/1000)+" sec.");
      System.out.println("[vta2] Done constructing Variable Type Analysis graph.");
    }
    if (Main.isVerbose)
      System.out.println("[vta2] Done constructing Variable Type Analysis graph.");
  }

  /** Uses the results of this analysis to trim the active invoke graph. */
  public void trimActiveInvokeGraph() {
    if (Main.isVerbose)
      System.out.println("[vta2] Trimming active invoke graph.");

    Date trimStart = new Date();
    
    Hierarchy h = Scene.v().getActiveHierarchy();

    List reachableMethods = ig.mcg.getReachableMethods();

    Iterator methodsIt = reachableMethods.iterator();
    while (methodsIt.hasNext()) {
      SootMethod m = (SootMethod)methodsIt.next();

      if(!m.isConcrete())
	continue;

      /* use InvokeGraph.getSitesOf() */
      List stmts = new ArrayList(ig.getSitesOf(m));
	
      if (!ig.mcg.isReachable(m)) {
	for (int i=0, n=stmts.size(); i<n; i++) {
	  Stmt s = (Stmt)stmts.get(i);
	  if (!s.containsInvokeExpr()) {
	    throw new RuntimeException("PANIC: the site does not "
				       +"have invoke expression.");
	  }
	  ig.removeAllTargets(s);
	  ig.removeSite(s);
	}
      } else {
	for (int i=0, n=stmts.size(); i<n; i++) {
	  Stmt s = (Stmt)stmts.get(i);
	  if (!s.containsInvokeExpr()) {
	    throw new RuntimeException("PANIC: the site does not "
				       +"have invoke expression.");
	  }
	  
	  InvokeExpr ie = (InvokeExpr)s.getInvokeExpr();	  
	  List ieSites = ig.getTargetsOf(s);
	  
	  if (ie instanceof VirtualInvokeExpr ||
	      ie instanceof InterfaceInvokeExpr) {
	    Value base = ((InstanceInvokeExpr)ie).getBase();
	    Type receiverType = base.getType();
	    
	    if(receiverType instanceof RefType) {
	      // we have, now, a set of reaching types for receiver.
	      // remove extra targets (by clearing targets and re-adding
	      // the ones that VTA doesn't rule out.)
	      ig.removeAllTargets(s);
	      
	      if (Main.isVerbose) {
		System.out.println("stmt "+s);
		System.out.println("local: "
				   +VTATypeGraph2.getVTALabel(m, base));
		System.out.println("reaching types: "
				   +getReachingTypesOf(TypeGraphNode2.v(
	      			VTATypeGraph2.getVTALabel(m, base))));
	      }
	      
	      List validReachingTypes = 
		getReachingTypesOf(TypeGraphNode2.v(
				    VTATypeGraph2.getVTALabel(m, base)));
	      
	      TypeGraphNode2 node = 
		TypeGraphNode2.v(VTATypeGraph2.getVTALabel(m, 
							     base));
	      Type t = (Type)vtg.getDeclaredTypeOf(node);

	      if (t instanceof ArrayType) 
		t = RefType.v("java.lang.Object");
	      
	      SootClass cls = ((RefType)t).getSootClass();
		
	      TypeSet2 reachingTypes = 
		(TypeSet2)superNodesToReachingTypes.get(scc.getComponentOf
		     (TypeGraphNode2.v(VTATypeGraph2.getVTALabel(m, base))));

	      List targets = 
		h.resolveConcreteDispatch(validReachingTypes, 
					    ie.getMethod());
	      Iterator targetsIt = targets.iterator();
	      
	      while (targetsIt.hasNext())
		ig.addTarget(s, (SootMethod)targetsIt.next());
	    }
	  }
	}
      }
    }

    Date trimEnd = new Date();

    long trimtime = trimEnd.getTime() - trimStart.getTime();

    System.out.println("[vta2] Trimming invoke graph takes "
		       +(trimtime/60000)+" min "
		       +((trimtime%60000)/1000)+" sec.");

    VTAFinish = new Date();

    long runtime = VTAFinish.getTime() - VTAStart.getTime();
    System.out.println("[vta2] VTA has run for "
		       +(runtime/60000)+" min "+
		       ((runtime%60000)/1000)+" sec.");
    System.out.println();
  }

    // You can also ask about the reaching types for any variable.
    // The API is not yet defined.
    
    private static final int 
            WHITE = 0,
            GRAY = 1,
            BLACK = 2;

    private void visitNodes(Hierarchy h, DirectedGraph graph)
    {
        PseudoTopologicalOrderer topOrderer = new PseudoTopologicalOrderer();
        List nodelist = topOrderer.newList(graph);
        HashMap outTypes = new HashMap(0);
        TypeSet2 set;
        boolean recompute = false;

        for( Iterator nodeIt = nodelist.iterator(); nodeIt.hasNext(); ) {

            final Object node = (Object) nodeIt.next();
            List preds = graph.getPredsOf(node);
            set = new TypeSet2();
            set.addAll(computeReachingTypes((List)node));
            for (Iterator predsIt = preds.iterator(); predsIt.hasNext(); )
                set.addAll((TypeSet2)outTypes.get(predsIt.next()));
            outTypes.put(node, set);
        }
        
        superNodesToReachingTypes = outTypes;
    }
}

