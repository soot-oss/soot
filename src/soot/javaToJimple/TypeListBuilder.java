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
            if (typedNode.type() instanceof polyglot.types.ClassType) {
                list.add(typedNode.type());
            }
            else {
            }
        }
        if (n instanceof polyglot.ast.ClassDecl){
            polyglot.ast.ClassDecl cd = (polyglot.ast.ClassDecl)n;
            if (cd.type() instanceof polyglot.types.ClassType){
                list.add(cd.type());
            }
            
        }
        return n;
    }
}
