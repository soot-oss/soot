package soot.javaToJimple;
import java.util.*;

public class AnonClassChecker extends polyglot.visit.NodeVisitor {

    private HashMap map;
      
    public HashMap getMap() {
        return map;
    }

    public AnonClassChecker(){
        map = new HashMap();
    }

    public polyglot.ast.Node leave(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor visitor) {
    
        if (n instanceof polyglot.ast.New) {
            if (((polyglot.ast.New)n).anonType() != null){
                polyglot.types.ClassType outerType = ((polyglot.ast.New)n).anonType().outer();
                while (outerType.isNested()) {
                    outerType = outerType.outer();
                }
                if (map.containsKey(outerType)) {
                    int count = ((Integer)map.get(outerType)).intValue();
                    count++;
                    map.put(outerType, new Integer(count));
                }
                else {
                    map.put(outerType, new Integer(1));
                }
            }
        }
        return n;
    }
}
