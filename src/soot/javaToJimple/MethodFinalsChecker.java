package soot.javaToJimple;
import java.util.*;

public class MethodFinalsChecker extends polyglot.visit.NodeVisitor{

    private ArrayList classNames;
    private ArrayList locals;
    private HashMap map;
    private String currentSootClass;
    private HashMap newToOuter;
    
    public void setCurrentSootClass(String name){
        currentSootClass = name;
    }

    public HashMap getNewToOuter(){
        return newToOuter;
    }
    
    public ArrayList getClassNames(){
        return classNames;
    }
    
    public ArrayList getLocals() {
        return locals;
    }

    public MethodFinalsChecker(){
        map = new HashMap();
        newToOuter = new HashMap();
        classNames = new ArrayList();
        locals = new ArrayList();
    }

    public polyglot.visit.NodeVisitor enter(polyglot.ast.Node parent, polyglot.ast.Node n) {
    
        
        if (n instanceof polyglot.ast.LocalDecl){
            polyglot.ast.LocalDecl ld = (polyglot.ast.LocalDecl)n;
            if (ld.flags().isFinal()){
                //System.out.println("adding final local: "+ld.name());
                locals.add(new polyglot.util.IdentityKey(ld.localInstance()));
            }
        }
        if (n instanceof polyglot.ast.New) {
            //System.out.println("in mfc and parent is: "+parent+" parent type: "+parent.getClass());
            //System.out.println("in mfc and n is: "+n);
            //System.out.println("current outer class name is: "+currentSootClass);
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
                //System.out.println("adding anon class name: "+anonClassName);
                //System.out.println("adding new: "+((polyglot.ast.New)n).arguments());
                classNames.add(n);
                newToOuter.put(n, currentSootClass);
            }
        }
        return enter(n);
    }
}
