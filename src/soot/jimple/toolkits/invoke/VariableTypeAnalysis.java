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
import soot.jimple.spark.*;

/** An implementation of Variable Type Analysis (as defined in Vijay Sundaresan's thesis). */

public class VariableTypeAnalysis implements PointsToAnalysis
{

  VTATypeGraph vtg;

  HashMap superNodesToReachingTypes;
  InvokeGraph ig;

  StronglyConnectedComponents scc;
  DirectedGraph superGraph;
  MethodCallGraph mcg;

  Date VTAStart, VTAFinish;

  TypeSet computeReachingTypes(List superNode) {
    TypeSet retVal = new TypeSet();
    Iterator snIt = superNode.iterator();

    while (snIt.hasNext()) {
      Object o = snIt.next();
      TypeSet reachingTypes = (TypeSet)vtg.labelToReachingTypes.get(o);
      retVal.addAll(reachingTypes);
    }

    return retVal;
  }

  TypeSet computeConservativeReachingTypes(Hierarchy h, List superNode) {
    
    TypeSet retVal = new TypeSet();
    Iterator snIt = superNode.iterator();
        
    LinkedList st = new LinkedList();
    snIt = superNode.iterator(); 
    while (snIt.hasNext()) {
      Object o = snIt.next();

      Type t = (Type)vtg.labelToDeclaredType.get(o);

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
    TypeSet reachingTypes = (TypeSet)superNodesToReachingTypes.get(scc.getComponentOf(o));
    if (reachingTypes==null) return null;
    Hierarchy h = Scene.v().getActiveHierarchy();

        List validReachingTypes = new LinkedList();
	
	Type t = (Type)vtg.labelToDeclaredType.get(o);
        Type declaredType = t;

        if (t instanceof ArrayType) 
            t = ((ArrayType)t).baseType;
        if (t instanceof RefType) {

            SootClass cls = ((RefType)t).getSootClass();

            if (t.equals(RefType.v("java.lang.Object")))
                validReachingTypes.addAll(reachingTypes);
            else
                for( Iterator rtIt = reachingTypes.iterator(); rtIt.hasNext(); ) {
                    final Type rt = (Type) rtIt.next();
                    SootClass subCls = ((RefType)rt).getSootClass();
                    if (cls.isInterface()) {
                        boolean found = false;
                        List classList = h.getSuperclassesOfIncluding(subCls);
                        List interfaceList = h.getSubinterfacesOfIncluding(cls);
                        for (Iterator classesIt = classList.iterator(); classesIt.hasNext() && !found; ) {
                            SootClass superclass = (SootClass)classesIt.next();
                            for (Iterator interfacesIt = interfaceList.iterator(); interfacesIt.hasNext() && !found; ) {
                                if (superclass.getInterfaces().contains(interfacesIt.next())) {
                                    validReachingTypes.add(rt);
                                    found = true;
                                }
                            }
                        }
                    }
                    else if (h.isClassSubclassOfIncluding(subCls, cls))
                        validReachingTypes.add(rt);
                }
        }
        if (declaredType instanceof ArrayType && !t.equals(RefType.v("java.lang.Object")))
            validReachingTypes.add(RefType.v("java.lang.Object"));
        return validReachingTypes;
    }   

  /** Constructs a VariableTypeAnalysis object for the given
   * InvokeGraph.  Calling trimInvokeGraph will modify the
   * associated invokeGraph according to this VTA's results. 
   */
  public VariableTypeAnalysis(InvokeGraph ig) {
    Date start, finish;

    start = new Date();
    VTAStart = start;
    //    if (Main.isVerbose) 
    {
      System.out.println("[vta] VTA started on "+start);
      System.out.println("[vta] Constructing Variable Type Analysis graph.");
    }

    this.ig = ig;
    vtg = new VTATypeGraph(ig);

    finish = new Date();
    //    if (Main.isVerbose) 
    {
      System.out.println("[vta] VTA graph has "+vtg.size()+" nodes and "+vtg.numEdges()+" edges.");
      long runtime = finish.getTime()-start.getTime();
      System.out.println("[vta] Graph construction took "+
			 (runtime/60000)+" min. "+
			 ((runtime%60000)/1000)+" sec.");
      System.out.println("[vta] Computing strongly connected components.");
    }
    start = finish;

        // ha!
    scc = new StronglyConnectedComponents(vtg);

    finish = new Date();
    //    if (Main.isVerbose) 
    {
      long runtime = finish.getTime()-start.getTime();
      System.out.println("[vta] SCC took "+
			 (runtime/60000)+" min. "+
			 ((runtime%60000)/1000)+" sec.");
      System.out.println("[vta] Propagating types.");
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
      System.out.println("[vta] Type propagation took "+
			 (runtime/60000)+" min. "+
			 ((runtime%60000)/1000)+" sec.");
      System.out.println("[vta] Done constructing Variable Type Analysis graph.");
    }
    if (Main.isVerbose)
      System.out.println("[vta] Done constructing Variable Type Analysis graph.");
  }

  /** Uses the results of this analysis to trim the active invoke graph. */
  public void trimActiveInvokeGraph()
  {
        if (Main.isVerbose)
            System.out.println("[vta] Trimming active invoke graph.");

	Date trimStart = new Date();

        // Is there a better way to do this?

        List appAndLibClasses = new ArrayList();
        appAndLibClasses.addAll(Scene.v().getApplicationClasses());
        appAndLibClasses.addAll(Scene.v().getLibraryClasses());

        Hierarchy h = null;

        if (!Scene.v().hasActiveHierarchy())
        {
            h = new Hierarchy();
            Scene.v().setActiveHierarchy(h);
        }
        else
            h = Scene.v().getActiveHierarchy();

        Iterator classesIt = appAndLibClasses.iterator();
        while (classesIt.hasNext())
        {
            SootClass c = (SootClass)classesIt.next();

            if (Main.isVerbose) {
                Iterator fieldsIt = c.getFields().iterator();
                while (fieldsIt.hasNext()) {
                    SootField f = (SootField)fieldsIt.next();

		    if (!VTATypeGraph.isRefLikeType(f.getType()))
                        continue;
		    System.out.println(f+" :: "+getReachingTypesOf(VTATypeGraph.getVTALabel(f)));
                }
            }

            Iterator methodsIt = c.methodIterator();
            while (methodsIt.hasNext())
            {
                SootMethod m = (SootMethod)methodsIt.next();

                if(!m.isConcrete())
                    continue;

                Body b = null;

                if (!m.hasActiveBody())
                    b = m.getBodyFromMethodSource("vta.jb");
                else
                    b = m.getActiveBody();

                Iterator unitsIt = b.getUnits().iterator();
                while (unitsIt.hasNext())
                {
                    Stmt s = (Stmt)unitsIt.next();
                    if (!s.containsInvokeExpr())
                        continue;

                    if (!ig.mcg.isReachable(m.toString())) {
                        if (ig.containsSite(s)) {
                            ig.removeAllTargets(s);
                            ig.removeSite(s);
                        }
                        continue;
                    }

                    InvokeExpr ie = (InvokeExpr)s.getInvokeExpr();
                    
                    List ieSites = ig.getTargetsOf(s);

                    if (ie instanceof VirtualInvokeExpr ||
                        ie instanceof InterfaceInvokeExpr)
                    {
                        Value base = ((InstanceInvokeExpr)ie).getBase();
                        Type receiverType = base.getType();

                        if(receiverType instanceof RefType)
                        {
                            // we have, now, a set of reaching types for receiver.
                            // remove extra targets (by clearing targets and re-adding
                            // the ones that VTA doesn't rule out.)
                            ig.removeAllTargets(s);

                            if (Main.isVerbose) {
                                System.out.println("stmt "+s);
                                System.out.println("local: "+VTATypeGraph.getVTALabel(m, base));
                                System.out.println("reaching types: "+getReachingTypesOf(VTATypeGraph.getVTALabel(m, base)));
                            }

                            List validReachingTypes = getReachingTypesOf(VTATypeGraph.getVTALabel(m, base));

			    Type t = (Type)vtg.labelToDeclaredType.get(VTATypeGraph.getVTALabel(m, base));

                            if (t instanceof ArrayType) 
                                t = RefType.v("java.lang.Object");
                            SootClass cls = ((RefType)t).getSootClass();

                 TypeSet reachingTypes = 
		   (TypeSet)superNodesToReachingTypes.get(scc.getComponentOf
							  (VTATypeGraph.getVTALabel(m, base)));

                            List targets = h.resolveConcreteDispatch(validReachingTypes, ie.getMethod());
                            /*
			    if( targets.isEmpty() ) {
				System.out.println( "Couldn't resolve dispatch "+s+" in method "+m );
				System.out.println( "reaching types: "+validReachingTypes );
			    }
                            */
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
    
	System.out.println("[vta] Trimming invoke graph takes "
			   +(trimtime/60000)+" min "
			   +((trimtime%60000)/1000)+" sec.");

        VTAFinish = new Date();

        long runtime = VTAFinish.getTime() - VTAStart.getTime();
        
	if (Main.isVerbose) {
	    System.out.println("[vta] VTA has run for "+(runtime/60000)+" min. "+
			       ((runtime%60000)/1000)+" sec.");
	    System.out.println();
	}
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
        TypeSet set;
        boolean recompute = false;

        for( Iterator nodeIt = nodelist.iterator(); nodeIt.hasNext(); ) {

            final Object node = (Object) nodeIt.next();
            List preds = graph.getPredsOf(node);
            set = new TypeSet();
            set.addAll(computeReachingTypes((List)node));
            for (Iterator predsIt = preds.iterator(); predsIt.hasNext(); )
                set.addAll((TypeSet)outTypes.get(predsIt.next()));
            outTypes.put(node, set);
        }
        
        superNodesToReachingTypes = outTypes;
    }
    class VTAP2Set implements PointsToSet {
        protected Set ts;
        VTAP2Set( Set ts ) { this.ts = ts; }
        public Set possibleTypes() { return ts; }
        public boolean isEmpty() { throw new RuntimeException( "NYI" ); }
        public boolean hasNonEmptyIntersection( PointsToSet other )
        { throw new RuntimeException( "NYI" ); }
    }
    public PointsToSet reachingObjects( SootMethod m, Stmt stmt, Local l ) {
        Collection c = getReachingTypesOf(VTATypeGraph.getVTALabel(m, l));
        if( c == null ) return new VTAP2Set( Collections.EMPTY_SET );
        return new VTAP2Set( new HashSet( c ) );
    }
}

