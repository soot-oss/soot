package soot.javaToJimple;
import java.util.*;

public class TypeListBuilder extends polyglot.visit.NodeVisitor {


    private HashSet list;

    public HashSet getList() {
        return list;
    }

    public TypeListBuilder(){
        list = new HashSet();
    }

    public polyglot.ast.Node leave(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor visitor) {
    
        if (n instanceof polyglot.ast.Typed) {
            polyglot.ast.Typed typedNode = (polyglot.ast.Typed)n;
            //System.out.println("Node: "+typedNode+" is type: "+typedNode.type());
            if (typedNode.type() instanceof polyglot.types.ClassType) {
                //System.out.println("Node Type Is Class Type: "+typedNode.type());
                list.add(typedNode.type());
            }
            else {
                //System.out.println("Node type is not class");
                //System.out.println("Node: "+n+" Type: "+typedNode.type());
            }
        }
        return n;
    }
}
