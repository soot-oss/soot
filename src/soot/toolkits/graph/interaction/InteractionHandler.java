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

package soot.toolkits.graph.interaction;

import soot.*;
import soot.toolkits.graph.*;
import soot.jimple.toolkits.annotation.callgraph.*;
import java.util.*;
import soot.options.*;

public class InteractionHandler {
   
    public InteractionHandler(Singletons.Global g){}
    public static InteractionHandler v() { return G.v().soot_toolkits_graph_interaction_InteractionHandler();}

    private ArrayList stopUnitList;
    public ArrayList getStopUnitList(){
        return stopUnitList;
    }
    public void addToStopUnitList(Object elem){
        if (stopUnitList == null){
            stopUnitList = new ArrayList();
        }
        stopUnitList.add(elem);
    }
    
    public void removeFromStopUnitList(Object elem){
        if (stopUnitList.contains(elem)){
            stopUnitList.remove(elem);
        }
    }

    public void handleNewAnalysis(Transform t, Body b){
        // here save current phase name and only send if actual data flow analysis exists
        if (PhaseOptions.getBoolean(PhaseOptions.v().getPhaseOptions( t.getPhaseName()), "enabled")){
            String name = t.getPhaseName()+" for method: "+b.getMethod().getName();
            currentPhaseName(name);
            currentPhaseEnabled(true);
            doneCurrent(false);
        }
        else {
            currentPhaseEnabled(false);
            setInteractThisAnalysis(false);
        }
    }

    public void handleCfgEvent(DirectedGraph g){
        if (currentPhaseEnabled()){
            G.v().out.println("Analyzing: "+currentPhaseName());
            doInteraction(new InteractionEvent(IInteractionConstants.NEW_ANALYSIS, currentPhaseName()));
        }
        if (isInteractThisAnalysis()){
            doInteraction(new InteractionEvent(IInteractionConstants.NEW_CFG, g));
        }
    }

    public void handleStopAtNodeEvent(Object u){
        if (isInteractThisAnalysis()){
            doInteraction(new InteractionEvent(IInteractionConstants.STOP_AT_NODE, u));
        }
    }
    
    public void handleBeforeAnalysisEvent(Object beforeFlow){
        if (isInteractThisAnalysis()){
            if (autoCon()){
                doInteraction(new InteractionEvent(IInteractionConstants.NEW_BEFORE_ANALYSIS_INFO_AUTO, beforeFlow));
            }
            else{
                doInteraction(new InteractionEvent(IInteractionConstants.NEW_BEFORE_ANALYSIS_INFO, beforeFlow));
            }
        }
    }

    public void handleAfterAnalysisEvent(Object afterFlow){
        if (isInteractThisAnalysis()){
            if (autoCon()){
                doInteraction(new InteractionEvent(IInteractionConstants.NEW_AFTER_ANALYSIS_INFO_AUTO, afterFlow));
            }
            else {
                doInteraction(new InteractionEvent(IInteractionConstants.NEW_AFTER_ANALYSIS_INFO, afterFlow));
            }
        }
    }

    public void handleTransformDone(Transform t, Body b){
        doneCurrent(true);
        if (isInteractThisAnalysis()){
            doInteraction(new InteractionEvent(IInteractionConstants.DONE, null));
        }
    }
   
    public void handleCallGraphStart(Object info, CallGraphGrapher grapher){
        setGrapher(grapher);
        doInteraction(new InteractionEvent(IInteractionConstants.CALL_GRAPH_START, info));
        if (!isCgReset()){
            handleCallGraphNextMethod();
        }
        else {
            setCgReset(false);
            handleReset();
        }
    }
   
    public void handleCallGraphNextMethod(){
        if (!cgDone()){
            getGrapher().setNextMethod(getNextMethod());
            getGrapher().handleNextMethod();
        }
    }

    private boolean cgReset = false;
    public void setCgReset(boolean v){
        cgReset = v;
    }
    public boolean isCgReset(){
        return cgReset;
    }
    
    public void handleReset(){
        if (!cgDone()){
            getGrapher().reset();
        }
    }

    public void handleCallGraphPart(Object info){
        doInteraction(new InteractionEvent(IInteractionConstants.CALL_GRAPH_PART, info));
        if (!isCgReset()){
            handleCallGraphNextMethod();
        }
        else {
            setCgReset(false);
            handleReset();
        }
    }
        
    private CallGraphGrapher grapher;
    private void setGrapher(CallGraphGrapher g){
        grapher = g;
    }
    private CallGraphGrapher getGrapher(){
        return grapher;
    }

    private SootMethod nextMethod;
    public void setNextMethod(SootMethod m){
        nextMethod = m;
    }
    private SootMethod getNextMethod(){
        return nextMethod;
    }
    
    private synchronized void doInteraction(InteractionEvent event){
        getInteractionListener().setEvent(event);
        getInteractionListener().handleEvent();
    
    }

    public synchronized void waitForContinue(){
        try {
            this.wait();
        }
        catch (InterruptedException e){
        }
        
    }
    
    private boolean interactThisAnalysis;
    public void setInteractThisAnalysis(boolean b){
        interactThisAnalysis = b;
    }
    public boolean isInteractThisAnalysis(){
        return interactThisAnalysis;
    }
    private boolean interactionCon;
    public synchronized void setInteractionCon(){
        this.notify();
    }

    public boolean isInteractionCon(){
        return interactionCon;
    }
    private IInteractionListener interactionListener;
    public void setInteractionListener(IInteractionListener listener){
        interactionListener = listener;
    }
    public IInteractionListener getInteractionListener(){
        return interactionListener;
    }
    
    private String currentPhaseName;
    public void currentPhaseName(String name){
        currentPhaseName = name;
    }
    public String currentPhaseName(){
        return currentPhaseName;
    }

    private boolean currentPhaseEnabled;    
    public void currentPhaseEnabled(boolean b){
        currentPhaseEnabled = b;
    }
    public boolean currentPhaseEnabled(){
        return currentPhaseEnabled;
    }

    private boolean cgDone = false;
    public void cgDone(boolean b){
        cgDone = b;
    }
    public boolean cgDone(){
        return cgDone;
    }

    private boolean doneCurrent;
    public void doneCurrent(boolean b){
        doneCurrent = b;
    }
    public boolean doneCurrent(){
        return doneCurrent;
    }

    private boolean autoCon;
    public void autoCon(boolean b){
        autoCon = b;
    }
    public boolean autoCon(){
        return autoCon;
    }

    private boolean stopInteraction = false;
    public void stopInteraction(boolean b){
        stopInteraction = b;
        Options.v().set_interactive_mode(false);
    }
    
}

