package soot.javaToJimple;
import java.util.*;

public class PrivateAccessChecker extends polyglot.visit.NodeVisitor {

    private ArrayList list;
      
    public ArrayList getList() {
        return list;
    }

    public PrivateAccessChecker(){
        list = new ArrayList();
    }

    public polyglot.ast.Node leave(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor visitor) {
    
        if (n instanceof polyglot.ast.Field) {
            
            polyglot.types.FieldInstance fi = ((polyglot.ast.Field)n).fieldInstance();

            if (fi.flags().isPrivate()) {
                list.add(fi);
            }
        }
        if (n instanceof polyglot.ast.Call) {
            
            polyglot.types.MethodInstance mi = ((polyglot.ast.Call)n).methodInstance();

            if (mi.flags().isPrivate()) {
                list.add(mi);
            }
        }
        return n;
    }
}
