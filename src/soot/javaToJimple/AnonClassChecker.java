package soot.javaToJimple;
import java.util.*;

public class AnonClassChecker extends polyglot.visit.NodeVisitor {

    private HashMap map;
    private BiMap bodyNameMap;

    public BiMap getBodyNameMap(){
        return bodyNameMap;
    }
    
    public HashMap getMap() {
        return map;
    }

    public AnonClassChecker(){
        map = new HashMap();
        bodyNameMap = new BiMap();
    }

    public polyglot.visit.NodeVisitor enter(polyglot.ast.Node parent, polyglot.ast.Node n) {
    
        if (n instanceof polyglot.ast.New) {
            //System.out.println("in anon class checker new: object type: "+((polyglot.ast.New)n).objectType());
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
                    //System.out.println("anon class name: "+anonClassName);
                    
                    // new or new body??? -- new
                    bodyNameMap.put(((polyglot.ast.New)n), anonClassName);
                }
            }
        }
        return enter(n);
    }
}
