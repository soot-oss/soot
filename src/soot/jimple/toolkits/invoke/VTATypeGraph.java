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
 * edges indicate that there is assignment between two types. */
public class VTATypeGraph extends MemoryEfficientGraph implements TypeGraph
{
    int state;
    int edges;
    Scene sc;

    HashMap labelToReachingTypes = new HashMap();
    HashMap labelToDeclaredType = new HashMap();
    HashMap castEdges = new HashMap();
    HashSet arrayNodes = new HashSet(0);

    public void addNode(Object o)
    {
        super.addNode(o);
        labelToReachingTypes.put(o, new TypeSet());
    }

    public void addEdge(Object o, Object p) {
        super.addEdge(o, p);
        edges++;
    }

    public int numEdges() {
        return edges;
    }

    /** Returns true if t is RefType or ArrayType. */
    public static boolean isRefLikeType(Type t)
    {
        return (t instanceof RefType) || (t instanceof ArrayType);
    }

    public VTATypeGraph(InvokeGraph ig)
    {
        this.sc = Scene.v();
        state = sc.getState();
        edges = 0;

        // Construct nodes of graph.
        {
            Chain allClasses = sc.getClasses();

            Iterator classesIt = allClasses.iterator();
            while (classesIt.hasNext())
            {
                SootClass c = (SootClass)classesIt.next();

		// Snarky comment about interfaces indeed possibly having fields goes here.
                // Add fields of c.
                Iterator fieldsIt = c.getFields().iterator();

                while (fieldsIt.hasNext())
                {
                    SootField f = (SootField)fieldsIt.next();

                    if (isRefLikeType(f.getType())) 
                    {
                        addNode(getVTALabel(f));
                        labelToDeclaredType.put(getVTALabel(f), f.getType());
                        if (f.getType() instanceof ArrayType)
                            arrayNodes.add(getVTALabel(f));
                    }
                }

                // More snarky comments about interfaces having a concrete <clinit> method (since
                // it can initialize fields).

                if (c.declaresMethod("void <clinit>()")) {

                    SootMethod m = c.getMethod("void <clinit>()");

                    if (!m.isConcrete())
                        continue;

                    if (!ig.mcg.isReachable(m.toString()))
                        continue;

                    // Get the body & add the locals.
		    Body b = m.retrieveActiveBody();

                    Iterator localIt = b.getLocals().iterator();
                    while (localIt.hasNext())
                    {
                        Local l = (Local)localIt.next();
                        Type t = l.getType();
                        if (isRefLikeType(t)) {
                            String s = getVTALabel(m, l);
                            if (s!=null) {
                              addNode(s);
                              labelToDeclaredType.put(s, t);
                            }                              
                        }
                    }
                    addNode(getVTALabel(m, "throw"));
                    labelToDeclaredType.put(getVTALabel(m, "throw"), m.getExceptions());
                }                    

                if (c.isInterface())
                    continue;

                // Add nodes for method contents, if it is reachable.
                Iterator methodsIt = c.methodIterator();

                while (methodsIt.hasNext())
                {
                    SootMethod m = (SootMethod)methodsIt.next();
                    
                    if (m.getSubSignature().equals("void <clinit>()"))
                        continue;

                    if (!ig.mcg.isReachable(m.toString()))
                        continue;

                    // For instance methods, add "this" node.
                    if (!m.isStatic()) {
                        addNode(getVTALabel(m, "this"));
                        labelToDeclaredType.put(getVTALabel(m, "this"),RefType.v(m.getDeclaringClass()));
                        if (m.getSubSignature().equals("void <init>()")) {
                            TypeSet t = (TypeSet)labelToReachingTypes.get(getVTALabel(m, "this"));
                            t.add(RefType.v(m.getDeclaringClass()));
                        }
                    }
                        
                    // Add return node, if appropriate.
                    if (isRefLikeType(m.getReturnType())) 
                    {
                        addNode(getVTALabel(m, "return"));
                        labelToDeclaredType.put(getVTALabel(m, "return"), m.getReturnType());
                    }

                    // Add the parameters.
                    Iterator paramIt = m.getParameterTypes().iterator();
                    int paramCount = 0;
                    while (paramIt.hasNext())
                    {
                        Type t = (Type)paramIt.next();
                        if (isRefLikeType(t)) 
                        {
                            addNode(getVTALabel(m, "p"+paramCount));
                            labelToDeclaredType.put(getVTALabel(m, "p"+paramCount), t);
                        }
                        paramCount++;
                    }

                    // Add throw node (to keep track of exceptions thrown by the method).
                    addNode(getVTALabel(m, "throw"));
                    labelToDeclaredType.put(getVTALabel(m, "throw"), m.getExceptions());


                    if (!m.isConcrete())
                        continue;

                    // Get the body & add the locals.
		    Body b = m.retrieveActiveBody();

                    Iterator localIt = b.getLocals().iterator();
                    while (localIt.hasNext())
                    {
                        Local l = (Local)localIt.next();
                        Type t = l.getType();
                        if (isRefLikeType(t)) {
                            String s = getVTALabel(m, l);
                            if (s!=null) {
                              addNode(s);
                              labelToDeclaredType.put(s, t);
                            }                              
                        }                        
                    }
                }
            }
        }

        // Add edges.
        {
            Chain allClasses = sc.getClasses();
            HashMap methodToReturnStmts = new HashMap(allClasses.size() * 8 + 1, 0.7f);

            Iterator classesIt = allClasses.iterator();
            while (classesIt.hasNext())
            {
                SootClass c = (SootClass)classesIt.next();

                if (c.isInterface())
                    continue;

		/* solution, report to user that needs -a switch */
		if (c.isContextClass())
		{
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
                    if (c.declaresMethod(SootMethod.getSubSignature("main", l, VoidType.v()))) {
                        SootMethod m = c.getMethod("main", l, VoidType.v());
                        String label = getVTALabel(m, "p0");
                        TypeSet rt = (TypeSet)labelToReachingTypes.get(label);
                        rt.add(RefType.v("java.lang.Object"));
                        rt.add(RefType.v("java.lang.String"));
                        arrayNodes.add(label);
                    }
                }

                // Set up hierarchy (used in conservative approximation of exception types).
                Hierarchy h = null;
                if (!Scene.v().hasActiveHierarchy()) {
                    h = new Hierarchy();
                    Scene.v().setActiveHierarchy(h);
                }
                else
                    h = Scene.v().getActiveHierarchy();

                // Add edges for method contents, if it is reachable.
                Iterator methodsIt = c.methodIterator();

                while (methodsIt.hasNext())
                {
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
                                    TypeSet l = (TypeSet)labelToReachingTypes.get(getVTALabel(m, "this"));
                                    l.add(RefType.v(clazz));
                                }
                            }
                            if (c.declaresMethod("void exit()")) {
                                SootMethod meth = c.getMethod("void exit()");
                                addEdge(getVTALabel(m, "this"), getVTALabel(meth, "this"));
                            }
                        }
                    }
                                
                    // Add edges and exception types for native methods
		    if (!m.isConcrete()) {
                        if (!m.isNative())
                            continue;
                        if (!isRefLikeType(m.getReturnType()))
                          continue;
                        Iterator siteIt = ig.getCallingSitesOf(m).iterator();
                        while (siteIt.hasNext()) {
                            Stmt caller = (Stmt)siteIt.next();
                            if (caller instanceof AssignStmt) {
                                SootMethod declaringMethod = ig.getDeclaringMethod(caller);
                                if (ig.mcg.isReachable(declaringMethod.toString()))
                                    addEdge(getVTALabel(m, "return"), 
                                            getVTALabel(declaringMethod, ((AssignStmt)caller).getLeftOp()));
                            }
                        }
                        String label = getVTALabel(m, "throw");
                        TypeSet rt = (TypeSet)labelToReachingTypes.get(label);
                        
                        LinkedList st = new LinkedList((Collection)labelToDeclaredType.get(label));
                        while(!st.isEmpty()) {
                            SootClass cls = (SootClass)st.removeLast();
                            if(cls.isInterface())
                                st.addAll(h.getImplementersOf(cls));
                            else {
                                for (Iterator clsIt = h.getSubclassesOfIncluding(cls).iterator(); clsIt.hasNext(); )
                                    rt.add(RefType.v((SootClass)clsIt.next()));
                            }
                        }
                        continue;
                    }

                    String methodSig = m.getSignature();
                    JimpleBody b = (JimpleBody)m.retrieveActiveBody();

                    // Look for assignStmts and method calls.
                    Iterator unitsIt = b.getUnits().iterator();

                    while (unitsIt.hasNext())
                    {
                        Stmt s = (Stmt) unitsIt.next();

                        // Add an edge for throw statements.
                        if (s instanceof ThrowStmt) {
                            Value v = ((ThrowStmt)s).getOp();
                            addEdge(getVTALabel(m, v), getVTALabel(m, "throw"));
                        }       

                        // Add an edge for method invocations.
                        if (s.containsInvokeExpr())
                        {
                            InvokeExpr ie = (InvokeExpr)s.getInvokeExpr();

                            List targetMethods = ig.getTargetsOf(s);
                            Iterator targetsIt = targetMethods.iterator();

                            while (targetsIt.hasNext())
                            {
                                SootMethod target = (SootMethod)targetsIt.next();

                                // Link base object with this.
                                if (ie instanceof InstanceInvokeExpr)
                                    addEdge(getVTALabel(m, ((InstanceInvokeExpr)ie).getBase()), 
                                            target.getSignature()+"$this");
                                
                                // Add parameter bindings
                                // Remember to add double edges for arrays/Objects even for parameters!

                                Iterator paramIt = target.getParameterTypes().iterator();
                                int paramCount = 0;
                                while (paramIt.hasNext())
                                {
                                    Type t = (Type)paramIt.next();
                                    if (isRefLikeType(t)) {
                                        if(ie.getArg(paramCount) instanceof Local
                                           && isRefLikeType(((Local)ie.getArg(paramCount)).getType())) {
                                        Type argType = ie.getArg(paramCount).getType();
                                        addEdge(getVTALabel(m, ie.getArg(paramCount)), 
                                                target.getSignature() + "$p"+paramCount);
                                        }
                                        else if (ie.getArg(paramCount) instanceof StringConstant)
                                            ((Collection)labelToReachingTypes.get
                                                (target.getSignature()+"$p"+paramCount)).add(RefType.v("java.lang.String"));
                                    }
				    paramCount++;
				}
                            }
                        }

                        // Add edges corresponding to return statements.

                        if (s instanceof ReturnStmt && isRefLikeType(((ReturnStmt)s).getOp().getType())
			     && ((ReturnStmt)s).getOp() instanceof Local)
                        {
                            Iterator siteIt = ig.getCallingSitesOf(m).iterator();
                            while (siteIt.hasNext())
                            {
                                Stmt caller = (Stmt)siteIt.next();
                                if (caller instanceof AssignStmt) {
                                    SootMethod declaringMethod = ig.getDeclaringMethod(caller);
                                    if (ig.mcg.isReachable(declaringMethod.toString()))
                                        addEdge(getVTALabel(m, ((ReturnStmt)s).getOp()), 
                                                getVTALabel(declaringMethod,((AssignStmt)caller).getLeftOp()));
                                }
                            }
                        }


                        // Add edges corresponding to identityStmts.

                        if (s instanceof IdentityStmt)
                        {
                            IdentityStmt is = (IdentityStmt)s;
                            Value lhs = is.getLeftOp(), rhs = is.getRightOp();
                            if (rhs instanceof ThisRef)
                            {
                                addEdge(methodSig+"$this", getVTALabel(m, lhs));

                                // I have no clue whether or not this is right.
                                if (m.getName().equals("<init>") && finalizer != null)
                                    addEdge(methodSig+"$this", finalizer.getSignature()+"$this");

                            }
                            else if (rhs instanceof ParameterRef && isRefLikeType(rhs.getType()))
                                addEdge(methodSig+"$p"+((ParameterRef)rhs).getIndex(), getVTALabel(m, lhs));

                            // Include all subtypes of RuntimeException and Error in CaughtExceptionRefs.

                            else if (rhs instanceof CaughtExceptionRef) {
                                TypeSet set = (TypeSet)labelToReachingTypes.get(getVTALabel(m, lhs));
                                for (Iterator clsIt = h.getSubclassesOfIncluding(
                                     Scene.v().getSootClass("java.lang.Error")).iterator(); clsIt.hasNext(); )
                                    set.add(RefType.v((SootClass)clsIt.next()));
                                for (Iterator clsIt = h.getSubclassesOfIncluding(
                                     Scene.v().getSootClass("java.lang.RuntimeException")).iterator();
                                     clsIt.hasNext(); )
                                    set.add(RefType.v((SootClass)clsIt.next()));
                            }
                        }


                        // Add edges corresponding to assignStmts.

                        if (s instanceof AssignStmt)
                        {
                            AssignStmt as = (AssignStmt)s;
                            Value lhs = as.getLeftOp(), rhs = as.getRightOp();

			    if (isRefLikeType(lhs.getType()))
			    {
                              String lhsRep = getVTALabel(m, lhs); 
                              String rhsRep = (rhs instanceof CastExpr)? getVTALabel(m, ((CastExpr)rhs).getOp()) : 
                                                                         getVTALabel(m, rhs);

                              if (lhsRep==null)
                                  continue;

                              // Remember cast expressions (useful for native adjusts)
                              if (rhs instanceof CastExpr) {
                                  if (!castEdges.containsKey(rhsRep))
                                      castEdges.put(rhsRep, new LinkedList());
                                  LinkedList lizt = (LinkedList)castEdges.get(rhsRep);
                                  lizt.add(new NodeTypePair(lhsRep, ((CastExpr)rhs).getCastType()));
                              }

			      // add to the reachingTypes sets.
                              if (rhs instanceof StringConstant) {
                                  TypeSet l = (TypeSet)labelToReachingTypes.get(lhsRep);
                                  if (!l.contains(RefType.v("java.lang.String")))
                                      l.add(RefType.v("java.lang.String"));
                              }

			      if (rhs instanceof NewExpr)
				{
				  TypeSet l = (TypeSet)labelToReachingTypes.get(lhsRep);
				  l.add(((NewExpr)rhs).getType());
				}
			      else if (rhs instanceof NewArrayExpr)
				{
				  TypeSet l = (TypeSet)labelToReachingTypes.get(lhsRep);
                                  if (!l.contains(RefType.v("java.lang.Object")))
                                      l.add(RefType.v("java.lang.Object"));
                                  arrayNodes.add(lhsRep);
                                  
				}
			      else if (rhs instanceof NewMultiArrayExpr)
				{
                                  TypeSet l = (TypeSet)labelToReachingTypes.get(lhsRep);
                                  if (!l.contains(RefType.v("java.lang.Object")))
                                      l.add(RefType.v("java.lang.Object"));
                                  arrayNodes.add(lhsRep);
				}
			      
			      // check that we have the right form: by right form we
                              // mean that it's actually an rvalue (not a NewExpr, a StringConstant,
                              // or something similar.
			      if (rhsRep == null)
                                continue;			      
			      addEdge(rhsRep, lhsRep);

			    }
			}
                    }

                    // Add edges corresponding to throw/catch pairs.
                    {
                        Chain trapChain = b.getTraps();
                        PatchingChain ch = b.getUnits();
                        for( Iterator trapIt = trapChain.iterator(); trapIt.hasNext(); ) {
                            final Trap trap = (Trap) trapIt.next();
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
                                        addEdge(getVTALabel(meth, "throw"), getVTALabel(m, lhs));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Date start = new Date();

        // Adjust for native methods.
	//        if (Main.opts.verbose()) 
	{
            System.out.println("[vta] Adjust for native methods");
        }

        {
            Hierarchy h = Scene.v().getActiveHierarchy();
            
            VTANativeAdjustor a = new VTANativeAdjustor(h, this);
            a.adjustForNativeMethods();
        }

        Date finish = new Date();
	//        if (Main.opts.verbose()) 
	{
            System.out.println("[vta] Done adjusting for native methods.");
            long runtime = finish.getTime()-start.getTime();
            System.out.println("[vta] Native adjustments took "+
                               (runtime/60000)+" min. "+
                               ((runtime%60000)/1000)+" sec.");
        }


        // Add back edges to array type variables (including Objects that are possibly assigned array types).
        {
            LinkedList st = new LinkedList(arrayNodes);
            LinkedList parents = new LinkedList();
            for (Iterator it = st.iterator(); it.hasNext(); it.next())
                parents.addLast(new Integer(5));  // to signal that these are roots of the DFS forest.
            HashMap nodeToColor = new HashMap();
            Integer GREY  = new Integer(0);
            Integer BLACK = new Integer(1);

            // Only add edges after the whole graph is traversed; we don't want to change the graph as we go!
            LinkedList from = new LinkedList();
            LinkedList to = new LinkedList();

            while(!st.isEmpty()) {
                Object o = st.getLast();
                if (!nodeToColor.containsKey(o)) {
                    // NODE IS WHITE
                    nodeToColor.put(o, GREY);
                    for( Iterator childIt = ((List)getSuccsOf(o)).iterator(); childIt.hasNext(); ) {
                        final Object child = (Object) childIt.next();
                        Type t = (Type)labelToDeclaredType.get(child);
                        if (t instanceof ArrayType || t.equals(RefType.v("java.lang.Object"))) {
                            if (!nodeToColor.containsKey(child)) {
                                st.addLast(child);
                                parents.addLast(o);
                            }
                            else {
                                from.addLast(child);
                                to.addLast(o);
                            }
                        }
                    }
                }
                else if (nodeToColor.get(o) == GREY) {
                    // WE ARE DONE WITH ALL THE DESCENDENTS
                    Object parent = parents.removeLast();
                    if (!(parent instanceof Integer)) {
                        from.addLast(o);
                        to.addLast(parent);
                    }
                    nodeToColor.put(st.removeLast(), BLACK);
                }
                else {
                    // Black node: either we hit a cross edge, or this entry point has been discovered.
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
                addEdge(fromIt.next(), toIt.next());
        }                

    }

      


    /** Returns the name of the VTA node corresponding to the given field. */
    static String getVTALabel(SootField f)
    {
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
            throw new ConcurrentModificationException("Scene changed for VTATypeGraph!");
    }

}

