package soot.javaToJimple;
import java.util.*;

public class ClassLiteralChecker extends polyglot.visit.NodeVisitor {

    private ArrayList list;

    public ArrayList getList() {
        return list;
    }

    public ClassLiteralChecker(){
        list = new ArrayList();
    }

    public polyglot.ast.Node leave(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor visitor) {
    
        if (n instanceof polyglot.ast.ClassLit) {
            polyglot.ast.ClassLit lit = (polyglot.ast.ClassLit)n;
            // only find ones where type is not primitive
            if (!lit.typeNode().type().isPrimitive()){
                list.add(n);
            }
        }
        return n;
    }
}
