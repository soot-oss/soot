package soot.javaToJimple;
import java.util.*;

public class AnonClassChecker extends polyglot.visit.NodeVisitor {

    private HashMap map;
    private HashMap bodyNameMap;

    public HashMap getBodyNameMap(){
        return bodyNameMap;
    }
    
    public HashMap getMap() {
        return map;
    }

    public AnonClassChecker(){
        map = new HashMap();
        bodyNameMap = new HashMap();
    }

    public polyglot.ast.Node leave(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor visitor) {
    
        if (n instanceof polyglot.ast.New) {
            if (((polyglot.ast.New)n).anonType() != null){
                polyglot.types.ClassType outerType = ((polyglot.ast.New)n).anonType().outer();
                while (outerType.isNested()) {
                    outerType = outerType.outer();
                }
                String anonClassName = outerType.toString();
                if (map.containsKey(outerType)) {
                    int count = ((Integer)map.get(outerType)).intValue();
                    count++;
                    map.put(outerType, new Integer(count));
                    anonClassName = anonClassName+"$"+count;
                }
                else {
                    map.put(outerType, new Integer(1));
                    anonClassName = anonClassName+"$"+1;
                }

                if (((polyglot.ast.New)n).body() != null){
                    System.out.println("anon class name: "+anonClassName);
                    bodyNameMap.put(((polyglot.ast.New)n).body(), anonClassName);
                }
            }
        }
        return n;
    }
}
