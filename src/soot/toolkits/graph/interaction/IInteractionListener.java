package soot.toolkits.graph.interaction;

public interface IInteractionListener {
    
    public void setEvent(InteractionEvent event);
    //public void setAvailable(boolean available);
    //public void setAvailable();
    public void handleEvent();
}
