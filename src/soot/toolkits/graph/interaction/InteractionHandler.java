package soot.toolkits.graph.interaction;

import soot.*;
import soot.toolkits.graph.*;

public class InteractionHandler {
   
    public InteractionHandler(Singletons.Global g){}
    public static InteractionHandler v() { return G.v().InteractionHandler();}

    public void handleNewAnalysis(Transform t, Body b){
        // here save current phase name and only send if actual data flow analysis exists
        if (PhaseOptions.getBoolean(PhaseOptions.v().getPhaseOptions( t.getPhaseName()), "enabled")){
            String name = t.getPhaseName()+" for method: "+b.getMethod().getName();
            currentPhaseName(name);
            currentPhaseEnabled(true);
        }
        else {
            currentPhaseEnabled(false);
            setInteractThisAnalysis(false);
        }
    }

    public void handleCfgEvent(DirectedGraph g){
        if (currentPhaseEnabled()){
            doInteraction(new InteractionEvent(IInteractionConstants.NEW_ANALYSIS, currentPhaseName()));
        }
        if (isInteractThisAnalysis()){
            doInteraction(new InteractionEvent(IInteractionConstants.NEW_CFG, g));
        }
    }

    public void handleBeforeAnalysisEvent(Object beforeFlow){
        if (isInteractThisAnalysis()){
            doInteraction(new InteractionEvent(IInteractionConstants.NEW_BEFORE_ANALYSIS_INFO, beforeFlow));
        }
    }

    public void handleAfterAnalysisEvent(Object afterFlow){
        if (isInteractThisAnalysis()){
            doInteraction(new InteractionEvent(IInteractionConstants.NEW_AFTER_ANALYSIS_INFO, afterFlow));
        }
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
}
