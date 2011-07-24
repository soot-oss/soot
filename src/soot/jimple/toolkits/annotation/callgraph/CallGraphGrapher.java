/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jimple.toolkits.annotation.callgraph;

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.*;
import soot.toolkits.graph.interaction.*;
import soot.options.*;

/** A scene transformer that creates a graphical callgraph. */
public class CallGraphGrapher extends SceneTransformer
{ 
    public CallGraphGrapher(Singletons.Global g){}
    public static CallGraphGrapher v() { return G.v().soot_jimple_toolkits_annotation_callgraph_CallGraphGrapher();}

    private MethodToContexts methodToContexts;
    private CallGraph cg;
    private boolean showLibMeths;
    
    private ArrayList<MethInfo> getTgtMethods(SootMethod method, boolean recurse){
        //G.v().out.println("meth for tgts: "+method);
        if (!method.hasActiveBody()){
            return new ArrayList<MethInfo>();
        }
        Body b = method.getActiveBody();
        ArrayList<MethInfo> list = new ArrayList<MethInfo>();
        Iterator sIt = b.getUnits().iterator();
        while (sIt.hasNext()){
            Stmt s = (Stmt) sIt.next();
            Iterator edges = cg.edgesOutOf(s);
            while (edges.hasNext()){
                Edge e = (Edge)edges.next();
                SootMethod sm = e.tgt();
                //G.v().out.println("found target method: "+sm);
                
                if (sm.getDeclaringClass().isLibraryClass()){
                    if (isShowLibMeths()){
                        if (recurse){
                            list.add(new MethInfo(sm, hasTgtMethods(sm) | hasSrcMethods(sm), e.kind()));
                        }
                        else {
                            list.add(new MethInfo(sm, true, e.kind()));
                        }
                    }
                }
                else {
                    if (recurse){
                        list.add(new MethInfo(sm, hasTgtMethods(sm) | hasSrcMethods(sm), e.kind()));
                    }
                    else {
                        list.add(new MethInfo(sm, true, e.kind()));
                    }
                }
            }
        }
        return list;
    }

    private boolean hasTgtMethods(SootMethod meth){
        ArrayList<MethInfo> list = getTgtMethods(meth, false);
        if (!list.isEmpty()) return true;
        else return false;
    }

    private boolean hasSrcMethods(SootMethod meth){
        ArrayList<MethInfo> list = getSrcMethods(meth, false);
        if (list.size() > 1) return true;
        else return false;
    }
    
    private ArrayList<MethInfo> getSrcMethods(SootMethod method, boolean recurse){
        //G.v().out.println("meth for srcs: "+method);
        ArrayList<MethInfo> list = new ArrayList<MethInfo>();
        
        for( Iterator momcIt = methodToContexts.get(method).iterator(); momcIt.hasNext(); ) {
            final MethodOrMethodContext momc = (MethodOrMethodContext) momcIt.next();
            Iterator callerEdges = cg.edgesInto(momc);
            while (callerEdges.hasNext()){
                Edge callEdge = (Edge)callerEdges.next();
                SootMethod methodCaller = callEdge.src();
                if (methodCaller.getDeclaringClass().isLibraryClass()){
                    if (isShowLibMeths()){
                        if (recurse){
                            list.add(new MethInfo(methodCaller, hasTgtMethods(methodCaller) | hasSrcMethods(methodCaller), callEdge.kind()));
                        }
                        else {
                            list.add(new MethInfo(methodCaller, true, callEdge.kind()));
                        }
                    }
                }
                else {
                    if (recurse){
                        list.add(new MethInfo(methodCaller, hasTgtMethods(methodCaller) | hasSrcMethods(methodCaller), callEdge.kind()));
                    }
                    else {
                        list.add(new MethInfo(methodCaller, true, callEdge.kind()));
                    }
                }
            } 
        }
        return list;
    }
    
    protected void internalTransform(String phaseName, Map options){
        
        CGGOptions opts = new CGGOptions(options);
        if (opts.show_lib_meths()){
            setShowLibMeths(true);
        }
        cg = Scene.v().getCallGraph();
        if (Options.v().interactive_mode()){
            reset();
        }
    }

    public void reset() {
        if (methodToContexts == null){
            methodToContexts = new MethodToContexts(Scene.v().getReachableMethods().listener());
        }
        
        if(Scene.v().hasCallGraph()) {
	        SootClass sc = Scene.v().getMainClass();
	        SootMethod sm = getFirstMethod(sc);
	        //G.v().out.println("got first method");
	        ArrayList<MethInfo> tgts = getTgtMethods(sm, true);
	        //G.v().out.println("got tgt methods");
	        ArrayList<MethInfo> srcs = getSrcMethods(sm, true);
	        //G.v().out.println("got src methods");
	        CallGraphInfo info = new CallGraphInfo(sm, tgts, srcs);
	        //G.v().out.println("will handle new call graph");
	        InteractionHandler.v().handleCallGraphStart(info, this);
        }
    }

    private SootMethod getFirstMethod(SootClass sc){
        ArrayList paramTypes = new ArrayList();
        paramTypes.add(soot.ArrayType.v(soot.RefType.v("java.lang.String"), 1));
        if (sc.declaresMethod("main", paramTypes, soot.VoidType.v())){
            return sc.getMethod("main", paramTypes, soot.VoidType.v());
        }
        else {
            return (SootMethod)sc.getMethods().get(0);
        }
    }
    
    public void handleNextMethod(){
        if (!getNextMethod().hasActiveBody()) return;
        ArrayList<MethInfo> tgts = getTgtMethods(getNextMethod(), true);
        //System.out.println("for: "+getNextMethod().getName()+" tgts: "+tgts);
        ArrayList<MethInfo> srcs = getSrcMethods(getNextMethod(), true);
        //System.out.println("for: "+getNextMethod().getName()+" srcs: "+srcs);
        CallGraphInfo info = new CallGraphInfo(getNextMethod(), tgts, srcs);
        //System.out.println("sending next method");
        InteractionHandler.v().handleCallGraphPart(info);
        //handleNextMethod();
    }
    
    private SootMethod nextMethod;

    public void setNextMethod(SootMethod m){
        nextMethod = m;
    }

    public SootMethod getNextMethod(){
        return nextMethod;
    }

    public void setShowLibMeths(boolean b){
        showLibMeths = b;
    }

    public boolean isShowLibMeths(){
        return showLibMeths;
    }

}


