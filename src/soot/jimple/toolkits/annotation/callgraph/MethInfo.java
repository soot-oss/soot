package soot.jimple.toolkits.annotation.callgraph;

import soot.*;

public class MethInfo {
    private SootMethod method;
    private boolean canExpandCollapse;
       
    public MethInfo(SootMethod meth, boolean b){
        method(meth);
        canExpandCollapse(b);
    }
        
    public boolean canExpandCollapse(){
        return canExpandCollapse;
    }
    public void canExpandCollapse(boolean b){
        canExpandCollapse = b;
    }
    public SootMethod method(){
        return method;
    }
    public void method(SootMethod m){
        method = m;
    }
}
