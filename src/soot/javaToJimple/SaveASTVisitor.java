package soot.javaToJimple;

import java.util.*;

public class SaveASTVisitor extends polyglot.frontend.AbstractPass {

    private polyglot.frontend.Job job;
    private polyglot.frontend.ExtensionInfo extInfo;
    private HashMap sourceJobMap;
    
    public SaveASTVisitor(polyglot.frontend.Pass.ID id, polyglot.frontend.Job job,  polyglot.frontend.ExtensionInfo extInfo){
        super(id);
        this.job = job;
        this.extInfo = extInfo;
    }

    public boolean run(){
        if (extInfo instanceof soot.javaToJimple.jj.ExtensionInfo){
            soot.javaToJimple.jj.ExtensionInfo jjInfo = (soot.javaToJimple.jj.ExtensionInfo)extInfo; 
            if (jjInfo.sourceJobMap() == null){
                jjInfo.sourceJobMap(new HashMap());
            }
            jjInfo.sourceJobMap().put(job.source(), job);
            return true;
        }
        return false;
    }
}
