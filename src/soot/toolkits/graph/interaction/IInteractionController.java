package soot.toolkits.graph.interaction;

public interface IInteractionController {

    //public void fireInteractionEvent(InteractionEvent event);
    public void addListener(IInteractionListener listener);
    public void removeListener(IInteractionListener listener);
}
