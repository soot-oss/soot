package soot.javaToJimple;
import java.util.*;

public class NestedClassListBuilder extends polyglot.visit.NodeVisitor {

    private ArrayList classDeclsList;
    private ArrayList anonClassBodyList;
      
    public ArrayList getClassDeclsList() {
        return classDeclsList;
    }
    
    public ArrayList getAnonClassBodyList() {
        return anonClassBodyList;
    }

    public NestedClassListBuilder(){
        classDeclsList = new ArrayList();
        anonClassBodyList = new ArrayList();
    }

    public polyglot.ast.Node leave(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor visitor) {
    
        if (n instanceof polyglot.ast.New) {
            if ((((polyglot.ast.New)n).anonType() != null) && (((polyglot.ast.New)n).body() != null)){
                anonClassBodyList.add(n);
            }
        }
        if (n instanceof polyglot.ast.ClassDecl) {

            if (((polyglot.types.ClassType)((polyglot.ast.ClassDecl)n).type()).isNested()){
                classDeclsList.add(n);
            }
        }
        return n;
    }
}
