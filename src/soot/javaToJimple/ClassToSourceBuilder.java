package soot.javaToJimple;
import java.util.*;

public class ClassToSourceBuilder extends polyglot.visit.NodeVisitor {

    private List list;
    private HashMap anonMap;
    
    public HashMap getAnonMap() {
        return anonMap;
    }

    public ClassToSourceBuilder(){
        list = new ArrayList();
        anonMap = new HashMap();
    }

    public polyglot.ast.Node leave(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor visitor) {
    
        if (n instanceof polyglot.ast.New) {
            if (((polyglot.ast.New)n).anonType() != null){
                polyglot.types.ClassType outerType = ((polyglot.ast.New)n).anonType().outer();
                while (outerType.isNested()) {
                    outerType = outerType.outer();
                }
                if (anonMap.containsKey(outerType)) {
                    int count = ((Integer)anonMap.get(outerType)).intValue();
                    count++;
                    anonMap.put(outerType, new Integer(count));
                }
                else {
                    anonMap.put(outerType, new Integer(1));
                }
            }
        }
        if (n instanceof polyglot.ast.ClassDecl) {
            list.add(fixInnerClassName(((polyglot.ast.ClassDecl)n).type()));
        }
        return n;
    }
    
    private String fixInnerClassName(polyglot.types.ClassType innerClass){
                
        String fullName = innerClass.fullName();
                
        while (innerClass.isNested()){
                    
            StringBuffer sb = new StringBuffer(fullName);
                    
            int lastDot = fullName.lastIndexOf(".");
            if (lastDot != -1) {
                sb.replace(lastDot, lastDot+1, "$");
                fullName = sb.toString();
            }
            innerClass = innerClass.outer();
        }

        return fullName;
    }
}
