/* Soot - a J*va Optimization Framework
 * Copyright (C) 2007 Manu Sridharan
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
package soot.jimple.spark.ondemand.pautil;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import soot.G;
import soot.SootMethod;
import soot.jimple.InvokeExpr;
import soot.jimple.spark.ondemand.genericutil.ArraySet;
import soot.jimple.spark.ondemand.genericutil.ArraySetMultiMap;
import soot.jimple.spark.pag.GlobalVarNode;
import soot.jimple.spark.pag.LocalVarNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.pag.VarNode;
import soot.toolkits.scalar.Pair;
import soot.util.HashMultiMap;

/**
 * Information for a context-sensitive analysis, eg. for call sites
 * 
 * @author manu
 */
public class ContextSensitiveInfo {

    private static final boolean SKIP_STRING_NODES = false;

    private static final boolean SKIP_EXCEPTION_NODES = false;

    private static final boolean SKIP_THREAD_GLOBALS = false;

    private static final boolean PRINT_CALL_SITE_INFO = false;

    /**
     * assignment edges, but properly handling multiple calls to a method
     * VarNode -> ArraySet[AssignEdge]
     */
    private final ArraySetMultiMap<VarNode, AssignEdge> contextSensitiveAssignEdges = new ArraySetMultiMap<VarNode, AssignEdge>();

    private final ArraySetMultiMap<VarNode, AssignEdge> contextSensitiveAssignBarEdges = new ArraySetMultiMap<VarNode, AssignEdge>();

    /**
     * nodes in each method
     */
    private final ArraySetMultiMap<SootMethod, VarNode> methodToNodes = new ArraySetMultiMap<SootMethod, VarNode>();

    private final ArraySetMultiMap<SootMethod, VarNode> methodToOutPorts = new ArraySetMultiMap<SootMethod, VarNode>();

    private final ArraySetMultiMap<SootMethod, VarNode> methodToInPorts = new ArraySetMultiMap<SootMethod, VarNode>();

    private final ArraySetMultiMap<SootMethod, Integer> callSitesInMethod = new ArraySetMultiMap<SootMethod, Integer>();

    private final ArraySetMultiMap<SootMethod, Integer> callSitesInvokingMethod = new ArraySetMultiMap<SootMethod, Integer>();

    private final ArraySetMultiMap<Integer, SootMethod> callSiteToTargets = new ArraySetMultiMap<Integer, SootMethod>();

    private final ArraySetMultiMap<Integer, AssignEdge> callSiteToEdges = new ArraySetMultiMap<Integer, AssignEdge>();

    private final Map<Integer, LocalVarNode> virtCallSiteToReceiver = new HashMap<Integer, LocalVarNode>();

    private final Map<Integer, SootMethod> callSiteToInvokedMethod = new HashMap<Integer, SootMethod>();

    private final Map<Integer, SootMethod> callSiteToInvokingMethod = new HashMap<Integer, SootMethod>();

    private final ArraySetMultiMap<LocalVarNode, Integer> receiverToVirtCallSites = new ArraySetMultiMap<LocalVarNode, Integer>();

    /**
     * 
     */
    public ContextSensitiveInfo(PAG pag) {
        // set up method to node map
        for (Iterator iter = pag.getVarNodeNumberer().iterator(); iter
                .hasNext();) {
            VarNode varNode = (VarNode) iter.next();
            if (varNode instanceof LocalVarNode) {
                LocalVarNode local = (LocalVarNode) varNode;
                SootMethod method = local.getMethod();
                assert method != null : local;
                methodToNodes.put(method, local);
                if (SootUtil.isRetNode(local)) {
                    methodToOutPorts.put(method, local);
                }
                if (SootUtil.isParamNode(local)) {
                    methodToInPorts.put(method, local);
                }
            }
        }
        int callSiteNum = 0;
        // first, add regular assigns
        Set assignSources = pag.simpleSources();
        for (Iterator iter = assignSources.iterator(); iter.hasNext();) {
            VarNode assignSource = (VarNode) iter.next();
            if (skipNode(assignSource)) {
                continue;
            }
            boolean sourceGlobal = assignSource instanceof GlobalVarNode;
            Node[] assignTargets = pag.simpleLookup(assignSource);
            for (int i = 0; i < assignTargets.length; i++) {
                VarNode assignTarget = (VarNode) assignTargets[i];
                if (skipNode(assignTarget))
                    continue;
                boolean isFinalizerNode = false;
                if (assignTarget instanceof LocalVarNode) {
                    LocalVarNode local = (LocalVarNode) assignTarget;
                    SootMethod method = local.getMethod();
                    if (method.toString().indexOf("finalize()") != -1
                            && SootUtil.isThisNode(local)) {
                        isFinalizerNode = true;
                    }
                }
                boolean targetGlobal = assignTarget instanceof GlobalVarNode;
                AssignEdge assignEdge = new AssignEdge(assignSource,
                        assignTarget);
                // handle weird finalizers
                if (isFinalizerNode) {
                    assignEdge.setParamEdge();
                    Integer callSite = new Integer(callSiteNum++);
                    assignEdge.setCallSite(callSite);
                }
                addAssignEdge(assignEdge);
                if (sourceGlobal) {
                    if (targetGlobal) {
                        // System.err.println("G2G " + assignSource + " --> "
                        // + assignTarget);
                    } else {
                        SootMethod method = ((LocalVarNode) assignTarget)
                                .getMethod();
                        // don't want to include things assigned something that
                        // is already an in port
                        if (!methodToInPorts.get(method).contains(assignTarget)) {
                            methodToInPorts.put(method, assignSource);
                        }
                    }
                } else {
                    if (targetGlobal) {
                        SootMethod method = ((LocalVarNode) assignSource)
                                .getMethod();
                        // don't want to include things assigned from something
                        // that
                        // is already an out port
                        if (!methodToOutPorts.get(method)
                                .contains(assignSource)) {
                            methodToOutPorts.put(method, assignTarget);
                        }
                    }
                }
            }
        }
        // now handle calls
        HashMultiMap callAssigns = pag.callAssigns;
        PrintWriter callSiteWriter = null;
        if (PRINT_CALL_SITE_INFO) {
            try {
                callSiteWriter = new PrintWriter(
                        new FileWriter("callSiteInfo"), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (Iterator iter = callAssigns.keySet().iterator(); iter.hasNext();) {
            InvokeExpr ie = (InvokeExpr) iter.next();
            Integer callSite = new Integer(callSiteNum++);
            callSiteToInvokedMethod.put(callSite, ie.getMethod());
            SootMethod invokingMethod = pag.callToMethod.get(ie);
            callSiteToInvokingMethod.put(callSite, invokingMethod);
            if (PRINT_CALL_SITE_INFO) {
                callSiteWriter.println(callSite + " "
                        + callSiteToInvokingMethod.get(callSite) + " " + ie);
            }
            if (pag.virtualCallsToReceivers.containsKey(ie)) {
                LocalVarNode receiver = (LocalVarNode) pag.virtualCallsToReceivers
                        .get(ie);
                assert receiver != null;
                virtCallSiteToReceiver.put(callSite, receiver);
                receiverToVirtCallSites.put(receiver, callSite);
            }
            Set curEdges = callAssigns.get(ie);
            for (Iterator iterator = curEdges.iterator(); iterator.hasNext();) {
                Pair callAssign = (Pair) iterator.next();
                //for reflective calls, the "O1" value can actually be a FieldRefNode
                //we simply ignore such cases here (appears to be sound)
                if(!(callAssign.getO1() instanceof VarNode)) continue;
                VarNode src = (VarNode) callAssign.getO1();
                VarNode dst = (VarNode) callAssign.getO2();
                if (skipNode(src)) {
                    continue;
                }
                ArraySet edges = getAssignBarEdges(src);
                AssignEdge edge = null;
                for (int i = 0; i < edges.size() && edge == null; i++) {
                    AssignEdge curEdge = (AssignEdge) edges.get(i);
                    if (curEdge.getDst() == dst) {
                        edge = curEdge;
                    }
                }
                assert edge != null : "no edge from " + src + " to " + dst;
                boolean edgeFromOtherCallSite = edge.isCallEdge();
                if (edgeFromOtherCallSite) {
                    edge = new AssignEdge(src, dst);
                }
                edge.setCallSite(callSite);
                callSiteToEdges.put(callSite, edge);
                if (SootUtil.isParamNode(dst)) {
                    // assert src instanceof LocalVarNode : src + " " + dst;
                    edge.setParamEdge();
                    SootMethod invokedMethod = ((LocalVarNode) dst).getMethod();
                    callSiteToTargets.put(callSite, invokedMethod);
                    callSitesInvokingMethod.put(invokedMethod, callSite);
                    // assert src instanceof LocalVarNode : src + " NOT LOCAL";
                    if (src instanceof LocalVarNode) {
                        callSitesInMethod.put(((LocalVarNode) src).getMethod(),
                                callSite);
                    }
                } else if (SootUtil.isRetNode(src)) {
                    edge.setReturnEdge();
                    SootMethod invokedMethod = ((LocalVarNode) src).getMethod();
                    callSiteToTargets.put(callSite, invokedMethod);
                    callSitesInvokingMethod.put(invokedMethod, callSite);
                    if (dst instanceof LocalVarNode) {
                        callSitesInMethod.put(((LocalVarNode) dst).getMethod(),
                                callSite);
                    }
                } else {
                    assert false : "weird call edge " + callAssign;
                }
                if (edgeFromOtherCallSite) {
                    addAssignEdge(edge);
                }
            }
        }
        // System.err.println(callSiteNum + " call sites");
        assert callEdgesReasonable();
        if (PRINT_CALL_SITE_INFO) {
            callSiteWriter.close();
        }
        // assert assignEdgesWellFormed(pag) == null :
        // assignEdgesWellFormed(pag);

    }

    private boolean callEdgesReasonable() {
        Set<VarNode> vars = contextSensitiveAssignEdges.keySet();
        for (VarNode node : vars) {
            ArraySet<AssignEdge> assigns = contextSensitiveAssignEdges
                    .get(node);
            for (AssignEdge edge : assigns) {
                if (edge.isCallEdge()) {
                    if (edge.getCallSite() == null) {
                    	G.v().out.println(edge + " is weird!!");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @SuppressWarnings("unused")
    private String assignEdgesWellFormed(PAG pag) {
        for (Iterator iter = pag.getVarNodeNumberer().iterator(); iter
                .hasNext();) {
            VarNode v = (VarNode) iter.next();
            Set<AssignEdge> outgoingAssigns = getAssignBarEdges(v);
            for (AssignEdge edge : outgoingAssigns) {
                if (edge.getSrc() != v)
                    return edge + " src should be " + v;
            }
            Set<AssignEdge> incomingAssigns = getAssignEdges(v);
            for (AssignEdge edge : incomingAssigns) {
                if (edge.getDst() != v)
                    return edge + " dst should be " + v;
            }
        }
        return null;
    }

    /**
     * @param node
     * @return
     */
    private boolean skipNode(VarNode node) {
        return (SKIP_STRING_NODES && SootUtil.isStringNode(node))
                || (SKIP_EXCEPTION_NODES && SootUtil.isExceptionNode(node))
                || (SKIP_THREAD_GLOBALS && SootUtil.isThreadGlobal(node));
    }

    /**
     * @param assignSource
     * @param assignTarget
     */
    private void addAssignEdge(AssignEdge assignEdge) {
        contextSensitiveAssignEdges.put(assignEdge.getSrc(), assignEdge);
        contextSensitiveAssignBarEdges.put(assignEdge.getDst(), assignEdge);
    }

    public ArraySet<AssignEdge> getAssignBarEdges(VarNode node) {
        return contextSensitiveAssignEdges.get(node);
    }

    /**
     * 
     * @param node
     * @return edges capturing assign flow <em>into</em> node
     */
    public ArraySet<AssignEdge> getAssignEdges(VarNode node) {
        return contextSensitiveAssignBarEdges.get(node);
    }

    public Set<SootMethod> methods() {
        return methodToNodes.keySet();
    }

    public ArraySet<VarNode> getNodesForMethod(SootMethod method) {
        return methodToNodes.get(method);
    }

    public ArraySet<VarNode> getInPortsForMethod(SootMethod method) {
        return methodToInPorts.get(method);
    }

    public ArraySet<VarNode> getOutPortsForMethod(SootMethod method) {
        return methodToOutPorts.get(method);
    }

    /**
     * @param method
     * @return
     */
    public ArraySet<Integer> getCallSitesInMethod(SootMethod method) {
        return callSitesInMethod.get(method);
    }

    public Set<Integer> getCallSitesInvokingMethod(SootMethod method) {
        return callSitesInvokingMethod.get(method);
    }

    public ArraySet<AssignEdge> getCallSiteEdges(Integer callSite) {
        return callSiteToEdges.get(callSite);
    }

    public ArraySet<SootMethod> getCallSiteTargets(Integer callSite) {
        return callSiteToTargets.get(callSite);
    }

    public LocalVarNode getReceiverForVirtCallSite(Integer callSite) {
        LocalVarNode ret = virtCallSiteToReceiver.get(callSite);
        assert ret != null;
        return ret;
    }

    public Set<Integer> getVirtCallSitesForReceiver(LocalVarNode receiver) {
        return receiverToVirtCallSites.get(receiver);
    }

    public SootMethod getInvokedMethod(Integer callSite) {
        return callSiteToInvokedMethod.get(callSite);
    }

    public SootMethod getInvokingMethod(Integer callSite) {
        return callSiteToInvokingMethod.get(callSite);
    }

    public boolean isVirtCall(Integer callSite) {
        return virtCallSiteToReceiver.containsKey(callSite);
    }

}
