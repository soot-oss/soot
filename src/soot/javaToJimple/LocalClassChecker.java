package soot.javaToJimple;
import java.util.*;

public class LocalClassChecker extends polyglot.visit.NodeVisitor {

    private HashMap map;
    private BiMap classMap;
    
    public HashMap getMap() {
        return map;
    }

    public BiMap getClassMap(){
        return classMap;
    }
    
    public LocalClassChecker(){
        map = new HashMap();
        classMap = new BiMap();
    }

    public polyglot.ast.Node leave(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor visitor) {
    
        if (n instanceof polyglot.ast.LocalClassDecl) {
            polyglot.ast.LocalClassDecl lcDecl = (polyglot.ast.LocalClassDecl)n;

            //System.out.println("local class decl type: "+lcDecl.decl().type());            
            polyglot.types.ClassType outerType = lcDecl.decl().type().outer();
            while (outerType.isNested()) {
                outerType = outerType.outer();
            }
            int num = 1;
            if (map.containsKey(outerType)){         
                HashMap tempMap = (HashMap)map.get(outerType);
                if (tempMap.containsKey(lcDecl.decl().name())) {
                    int counter = ((Integer)tempMap.get(lcDecl.decl().name())).intValue();
                    counter++;
                    tempMap.put(lcDecl.decl().name(), new Integer(counter));
                    map.put(outerType, tempMap);
                    num = counter;
                }
                else {
                    tempMap.put(lcDecl.decl().name(), new Integer(1));
                    map.put(outerType, tempMap);
                }
            }
            else {
                HashMap classNumMap = new HashMap();
                classNumMap.put(lcDecl.decl().name(), new Integer(1));
                map.put(outerType, classNumMap);

            }
           
            String realName = outerType.toString()+"$"+num+"$"+lcDecl.decl().name();
            //System.out.println("making local class map: realName: "+realName+" lcDecl: "+lcDecl.decl());
            classMap.put(lcDecl, realName);
            
        }
        return n;
    }
}
