package soot.toolkits.graph.interaction;

import soot.*;
import soot.toolkits.graph.*;
import soot.jimple.toolkits.annotation.callgraph.*;

public class InteractionHandler {
   
    public InteractionHandler(Singletons.Global g){}
    public static InteractionHandler v() { return G.v().InteractionHandler();}

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
        handleCallGraphNextMethod();
    }
   
    public void handleCallGraphNextMethod(){
        if (!cgDone()){
            getGrapher().setNextMethod(getNextMethod());
            System.out.println("about to handle next method: "+getNextMethod());
            getGrapher().handleNextMethod();
        }
    }

    public void handleCallGraphPart(Object info){
        doInteraction(new InteractionEvent(IInteractionConstants.CALL_GRAPH_PART, info));
        handleCallGraphNextMethod();
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
            System.out.println("Soot wait");
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
        System.out.println("Soot notify");
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
}
