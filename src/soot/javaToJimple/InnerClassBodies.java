package soot.javaToJimple;
import java.util.*;

public class InnerClassBodies extends polyglot.visit.NodeVisitor {

    private ArrayList list;
      
    public ArrayList getList() {
        return list;
    }

    public InnerClassBodies(){
        list = new ArrayList();
    }

    public polyglot.ast.Node leave(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor visitor) {
    
        if (n instanceof polyglot.ast.LocalClassDecl) {
            
            list.add(((polyglot.ast.LocalClassDecl)n).decl().body());
        }
        if (n instanceof polyglot.ast.ClassDecl) {
            
            list.add(((polyglot.ast.ClassDecl)n).body());
        }
        if (n instanceof polyglot.ast.New) {
            
            polyglot.ast.New newNode = (polyglot.ast.New)n;

            if (newNode.anonType() != null){
                list.add(newNode.body());
            }

        }
        return n;
    }
}
