package soot.javaToJimple;
import java.util.*;

public class LocalClassChecker extends polyglot.visit.NodeVisitor {

    private HashMap map;
      
    public HashMap getMap() {
        return map;
    }

    public LocalClassChecker(){
        map = new HashMap();
    }

    public polyglot.ast.Node leave(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor visitor) {
    
        if (n instanceof polyglot.ast.LocalClassDecl) {
            polyglot.ast.LocalClassDecl lcDecl = (polyglot.ast.LocalClassDecl)n;

            
            polyglot.types.ClassType outerType = lcDecl.decl().type().outer();
            while (outerType.isNested()) {
                outerType = outerType.outer();
            }
            if (map.containsKey(outerType)){         
                HashMap classMap = (HashMap)map.get(outerType);
                if (classMap.containsKey(lcDecl.decl().name())) {
                    int counter = ((Integer)classMap.get(lcDecl.decl().name())).intValue();
                    counter++;
                    classMap.put(lcDecl.decl().name(), new Integer(counter));
                    map.put(outerType, classMap);
                }
                else {
                    classMap.put(lcDecl.decl().name(), new Integer(1));
                    map.put(outerType, classMap);
                }
            }
            else {
                HashMap classNumMap = new HashMap();
                classNumMap.put(lcDecl.decl().name(), new Integer(1));
                map.put(outerType, classNumMap);

            }
        }
        return n;
    }
}
