package soot.jimple.toolkits.annotation.callgraph;

import java.util.*;
import soot.*;

public class CallGraphInfo {

    private ArrayList inputs = new ArrayList();
    private ArrayList outputs = new ArrayList();
    private SootMethod center;

    public CallGraphInfo(SootMethod sm, ArrayList outputs, ArrayList inputs){
        setCenter(sm);
        setOutputs(outputs);
        setInputs(inputs);
    }
    
    public void setCenter(SootMethod sm){
        center = sm;
    } 

    public SootMethod getCenter(){
        return center;
    }

    public ArrayList getInputs(){
        return inputs;
    }

    public void setInputs(ArrayList list){
        inputs = list;
    }

    public ArrayList getOutputs(){
        return outputs;
    }

    public void setOutputs(ArrayList list){
        outputs = list;
    }


}
