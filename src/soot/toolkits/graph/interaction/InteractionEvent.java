package soot.toolkits.graph.interaction;

public class InteractionEvent {

    
    private int type;
    private Object info;

    public InteractionEvent (int type){
        type(type);
    }
    
    public InteractionEvent (int type, Object info){
        type(type);
        info(info);
    }

    private void type(int t){
        type = t;
    }

    private void info(Object i){
        info = i;
    }
    
    public int type(){
        return type;
    }

    public Object info(){
        return info;
    }
}
