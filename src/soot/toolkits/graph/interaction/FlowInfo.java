package soot.toolkits.graph.interaction;

public class FlowInfo {

    private Object info;
    private Object unit;
    private boolean before;

    public FlowInfo(Object info, Object unit, boolean b){
        info(info);
        unit(unit);
        setBefore(b);
    }
    
    public Object unit(){
        return unit;
    }

    public void unit(Object u){
        unit = u;
    }
    
    public Object info(){
        return info;
    }

    public void info(Object i){
        info = i;
    }

    public boolean isBefore(){
        return before;
    }

    public void setBefore(boolean b){
        before = b;
    }

    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("unit: "+unit);
        sb.append(" info: "+info);
        sb.append(" before: "+before);
        return sb.toString();
    }
}
