package soot.jimple.toolkits.annotation.callgraph;

import java.util.*;

public class CallData {

    private HashMap map = new HashMap();
    private ArrayList children = new ArrayList();
    private ArrayList outputs = new ArrayList();
    private String data;

    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("Data: ");
        sb.append(data);
        //sb.append(" Children: ");
        //sb.append(children);
        //sb.append(" Outputs: ");
        //sb.append(outputs);
        return sb.toString();
    }
    
    public void addChild(CallData cd){
        children.add(cd);
    }

    public void addOutput(CallData cd){
        if (!outputs.contains(cd)){
            outputs.add(cd);
        }
    }

    public void setData(String d){
        data = d;
    } 

    public String getData(){
        return data;
    }

    public ArrayList getChildren(){
        return children;
    }

    public ArrayList getOutputs(){
        return outputs;
    }

    public void addToMap(Object key, CallData val){
        map.put(key, val);
    }

    public HashMap getMap(){
        return map;
    }

}
