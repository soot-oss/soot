package soot.toolkits.graph.interaction;

import soot.*;
import soot.toolkits.graph.*;

public class InteractionHandler {
   
    public InteractionHandler(Singletons.Global g){}
    public static InteractionHandler v() { return G.v().InteractionHandler();}

    public void handleNewAnalysis(Transform t, Body b){
        //G.v().out.println("about to send new transform event");
        if (PhaseOptions.getBoolean(PhaseOptions.v().getPhaseOptions( t.getPhaseName()), "enabled")){
            G.v().out.println("sending new analysis event for: "+t.getPhaseName());
            String name = t.getPhaseName()+" for method: "+b.getMethod().getName();
            doInteraction(new InteractionEvent(IInteractionConstants.NEW_ANALYSIS, name));
        }
        else {
            setInteractThisAnalysis(false);
        }
    }

    public void handleCfgEvent(DirectedGraph g){
        if (isInteractThisAnalysis()){
            //G.v().out.println("about to send cfg event");
            doInteraction(new InteractionEvent(IInteractionConstants.NEW_CFG, g));
        }
    }

    public void handleBeforeAnalysisEvent(Object beforeFlow){
        if (isInteractThisAnalysis()){
            //G.v().out.println("about to send before flow event");
            doInteraction(new InteractionEvent(IInteractionConstants.NEW_BEFORE_ANALYSIS_INFO, beforeFlow));
        }
    }

    public void handleAfterAnalysisEvent(Object afterFlow){
        if (isInteractThisAnalysis()){
            //G.v().out.println("about to send after flow event");
            doInteraction(new InteractionEvent(IInteractionConstants.NEW_AFTER_ANALYSIS_INFO, afterFlow));
        }
    }

    private synchronized void doInteraction(InteractionEvent event){
        getInteractionListener().setEvent(event);
        getInteractionListener().handleEvent();
        //getInteractionListener().setAvailable();//true);
        //while (!isInteractionCon()){
            /*try {
                System.out.println("Soot wait");
                this.wait();
            }
            catch (InterruptedException e){
            }*/
        //}
            
        //G.v().out.println("listener responded");
        //setInteractionCon(false);
    
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
    public synchronized void setInteractionCon(){//boolean b){
        //interactionCon = b;
        //G.v().out.println("set interaction con to true.");
        this.notify();
        System.out.println("Soot notify");
    }

    public boolean isInteractionCon(){
        return interactionCon;
    }
    private IInteractionListener interactionListener;
    public void setInteractionListener(IInteractionListener listener){
        interactionListener = listener;
        //G.v().out.println("interaction listener set: "+interactionListener);
    }
    public IInteractionListener getInteractionListener(){
        return interactionListener;
    }
    

}
