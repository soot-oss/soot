/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Raja Vallee-Rai
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

import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.scalar.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import java.util.*;
import soot.util.*;

/** Tests whether VTA detects all runtime types. */
public class VTATestingFramework extends SceneTransformer
{
    private static VTATestingFramework instance = new VTATestingFramework();
    private VTATestingFramework() {}

    public static VTATestingFramework v() { return instance; }

    public String getDefaultOptions() 
    {
        return "insert-null-checks insert-redundant-casts allowed-modifier-changes:unsafe";
    }

    public String getDeclaredOptions() 
    { 
        return super.getDeclaredOptions() + " insert-null-checks insert-redundant-casts allowed-modifier-changes";
    }
    
    protected void internalTransform(String phaseName, Map options)
    {
        Date start = new Date();
        if(Main.isVerbose) {
            System.out.println("[] Starting VTA...");
            System.out.println("[vta] Invoke graph builder started on "+start);
        }

        InvokeGraphBuilder.v().transform(phaseName + ".igb");

        Date finish = new Date();
        if (Main.isVerbose) {
            System.out.println("[vta] Done building invoke graph.");
            long runtime = finish.getTime() - start.getTime();
            System.out.println("[stb] This took "+ (runtime/60000)+" min. "+ ((runtime%60000)/1000)+" sec.");
        }

        boolean enableNullPointerCheckInsertion = Options.getBoolean(options, "insert-null-checks");
        boolean enableRedundantCastInsertion = Options.getBoolean(options, "insert-redundant-casts");
        String modifierOptions = Options.getString(options, "allowed-modifier-changes");

        HashMap instanceToStaticMap = new HashMap();

        InvokeGraph graph = Scene.v().getActiveInvokeGraph();
        System.out.println(graph.computeStats());
        VariableTypeAnalysis vta = new VariableTypeAnalysis(graph);
        vta.trimActiveInvokeGraph();
        graph.refreshReachableMethods();
        System.out.println(graph.computeStats());

        /* Enable if you want to run VTA + VTA */

          vta = new VariableTypeAnalysis(graph);
          vta.trimActiveInvokeGraph();
          graph.refreshReachableMethods();
          System.out.println(graph.computeStats());

        Hierarchy hierarchy = Scene.v().getActiveHierarchy();

        SootClass mainClass = Scene.v().getMainClass();
        SootMethod mainMethod = mainClass.getMethod("void main(java.lang.String[])");

        String fieldName = "__isProfiling";
        while (mainClass.declaresFieldByName(fieldName))
            fieldName = fieldName + "_";
        SootField __isProfiling = new SootField(fieldName, BooleanType.v(), Modifier.STATIC | Modifier.PUBLIC);
        mainClass.addField(__isProfiling);

        if(mainClass.declaresMethod("void <clinit>()")) {
            Chain units = mainClass.getMethod("void <clinit>()").getActiveBody().getUnits();
            units.addFirst(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(__isProfiling), IntConstant.v(0)));
        }
        else {
            SootMethod clinit = new SootMethod("<clinit>", new LinkedList(), VoidType.v(), Modifier.STATIC);
            mainClass.addMethod(clinit);
            JimpleBody body = Jimple.v().newBody(clinit);
            clinit.setActiveBody(body);
            Chain units = body.getUnits();
            units.addFirst(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(__isProfiling), IntConstant.v(0)));
            units.addLast(Jimple.v().newReturnVoidStmt());
        }

        Chain units = mainMethod.getActiveBody().getUnits();
        units.addFirst(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(__isProfiling), IntConstant.v(1)));

        // To avoid infinite recursion, do not profile methods that are mutually recursive
        // with println. So we shall resolve dispatches to println abstractly, and check if 
        // the method we would like to profile belongs to the same strongly connected component.

        // Lookup the required methods ahead of time (so that we don't have to get them every time
        // we want to use them).
        
        SootMethod m1 = Scene.v().getMethod("<java.lang.Class: java.lang.Class forName(java.lang.String)>");
        SootMethod m2 = Scene.v().getMethod("<java.util.Vector: void addElement(java.lang.Object)>");
        SootMethod m3 = Scene.v().getMethod("<java.lang.Object: java.lang.Class getClass()>");
        SootMethod m4 = Scene.v().getMethod("<java.util.Vector: boolean contains(java.lang.Object)>");
        SootMethod m5 = Scene.v().getMethod("<java.io.PrintStream: void println(java.lang.String)>");
        SootMethod m6 = Scene.v().getMethod("<java.lang.Class: boolean isArray()>");
        SootMethod m7 = Scene.v().getMethod("<java.io.PrintStream: void print(java.lang.Object)>");
        SootMethod m8 = Scene.v().getMethod("<java.util.Vector: void <init>()>");

        // We don't need to find the transitive closure of m1, m3 and m6 since these are native methods. 
        Iterator it = Arrays.asList(new SootMethod[] {m2, m4, m5, m7, m8}).iterator();
        HashSet methSet = new HashSet(0);
        while (it.hasNext()) {
            SootMethod meth = (SootMethod)it.next();
            SootClass cls = meth.getDeclaringClass();
            methSet.addAll(hierarchy.resolveAbstractDispatch(cls, meth));
        }

        HashSet excludeSet = new HashSet(graph.mcg.getMethodsReachableFrom(methSet));
        int excludeCount = 0;
                
        Iterator classesIt = Scene.v().getApplicationClasses().iterator();
        while (classesIt.hasNext())
        {

            SootClass c = (SootClass)classesIt.next();
            
            LinkedList methodsList = new LinkedList(); 
            methodsList.addAll(c.getMethods());

            while (!methodsList.isEmpty())
            {
                SootMethod container = (SootMethod)methodsList.removeFirst();

                if (!container.isConcrete())
                    continue;

                if (excludeSet.contains(container)) {
                    if (Main.isVerbose)
                        excludeCount++;
                        System.out.println(container+" is excluded from profiling.");
                    continue;
                }

                JimpleBody b = (JimpleBody)container.getActiveBody();
                          
                // Add a variable to hold a list of types (represented by strings).

                Local __typeList = Jimple.v().newLocal("__typeList", RefType.v("java.util.Vector"));
                b.getLocals().add(__typeList);
                Local __vtaclass = Jimple.v().newLocal("__vtaclass", RefType.v("java.lang.Class"));
                b.getLocals().add(__vtaclass);
                Local __condition = Jimple.v().newLocal("__condition", IntType.v());
                b.getLocals().add(__condition);
                Local __outRef = Jimple.v().newLocal("__outRef", RefType.v("java.io.PrintStream"));
                b.getLocals().add(__outRef);

                PatchingChain unitChain = b.getUnits();
                Iterator unitIt = unitChain.snapshotIterator();

                while (unitIt.hasNext())
                {
                    Stmt s = (Stmt)unitIt.next();
                    if (!s.containsInvokeExpr())
                        continue;


                    InvokeExpr ie = (InvokeExpr)s.getInvokeExpr();

                    if (ie instanceof StaticInvokeExpr || 
                        ie instanceof SpecialInvokeExpr)
                        continue;

                    Jimple j = Jimple.v();
                    Local base = (Local)((InstanceInvokeExpr)ie).getBase();
                    String label = VTATypeGraph.getVTALabel(container, base);
                    List types = vta.getReachingTypesOf(label);
                    AssignStmt assignFirst = j.newAssignStmt(__typeList, j.newNewExpr(RefType.v("java.util.Vector")));
                    unitChain.insertBefore(assignFirst, s);
                    InvokeStmt invoke = j.newInvokeStmt(j.newSpecialInvokeExpr(__typeList, 
                                                        RefType.v("java.util.Vector").getSootClass().getMethod("void <init>()")));
                    unitChain.insertBefore(invoke, s);
                    AssignStmt assign = j.newAssignStmt(__outRef, j.newStaticFieldRef(RefType.v("java.lang.System").getSootClass()
                                                                           .getField("out", RefType.v("java.io.PrintStream"))));
                    unitChain.insertBefore(assign, s);

                    if (types!=null) {
                        for (Iterator typesIt = types.iterator(); typesIt.hasNext(); ) {
                            Type t = (Type)typesIt.next();
                            assign = j.newAssignStmt(__vtaclass, j.newStaticInvokeExpr(m1, StringConstant.v(t.toString())));
                            unitChain.insertBefore(assign, s);
                            invoke = j.newInvokeStmt(j.newVirtualInvokeExpr(__typeList, m2, __vtaclass));
                            unitChain.insertBefore(invoke, s);
                        }
                        assign = j.newAssignStmt(__vtaclass, j.newVirtualInvokeExpr(base, m3)); 
                        unitChain.insertBefore(assign,s);
                        assign = j.newAssignStmt(__condition, j.newVirtualInvokeExpr(__vtaclass, m6));
                        unitChain.insertBefore(assign, s);
                        AssignStmt assign2 = j.newAssignStmt(__condition, j.newVirtualInvokeExpr(__typeList, m4, __vtaclass));
                        unitChain.insertBefore(assign2, s);
                        IfStmt ifstmt = j.newIfStmt(j.newEqExpr(__condition, IntConstant.v(0)), assign2);
                        unitChain.insertAfter(ifstmt, assign);
                        assign = j.newAssignStmt(__vtaclass, j.newStaticInvokeExpr(m1, StringConstant.v("java.lang.Object")));
                        unitChain.insertAfter(assign, ifstmt);
                        ifstmt = j.newIfStmt(j.newNeExpr(__condition, IntConstant.v(0)), s);
                        unitChain.insertAfter(ifstmt, assign2);
                        invoke = j.newInvokeStmt(j.newVirtualInvokeExpr(__outRef, m7, __vtaclass));
                        unitChain.insertAfter(invoke, ifstmt);
                        InvokeStmt invoke2 = j.newInvokeStmt(j.newVirtualInvokeExpr(__outRef, m5, 
                                                  StringConstant.v(" is not detected in "+base+" in method "+container)));
                        unitChain.insertAfter(invoke2, invoke);
                    }
                    else {
                        invoke = j.newInvokeStmt(j.newVirtualInvokeExpr(__outRef, m5, 
                                                  StringConstant.v("Reaching type is empty in "+container)));
                        unitChain.insertBefore(invoke, s);
                    }
                    assign = j.newAssignStmt(__condition, j.newStaticFieldRef(__isProfiling));
                    unitChain.insertBefore(assign, assignFirst);
                    IfStmt ifstmt = j.newIfStmt(j.newNeExpr(__condition, IntConstant.v(1)), s);
                    unitChain.insertAfter(ifstmt, assign);
                        
                }

            }
        }
  
        if (Main.isVerbose) {
            System.out.println(excludeCount+" methods have been excluded from profiling.");
        }
        Scene.v().releaseActiveInvokeGraph();
        graph = null;
        vta = null;
    }
}


