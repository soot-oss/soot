package soot.javaToJimple;
import java.util.*;

public class ClassDeclFinder extends polyglot.visit.NodeVisitor {

    private polyglot.types.ClassType typeToFind;
    private polyglot.ast.ClassDecl declFound;

    
    public void typeToFind(polyglot.types.ClassType type){
        typeToFind = type;
    }
    
    public polyglot.ast.ClassDecl declFound(){
        return declFound;
    }
    

    public ClassDeclFinder(){
        declFound = null;
    }

    public polyglot.visit.NodeVisitor enter(polyglot.ast.Node parent, polyglot.ast.Node n) {
    
        if (n instanceof polyglot.ast.ClassDecl) {
            if (((polyglot.ast.ClassDecl)n).type().equals(typeToFind)){
                declFound = (polyglot.ast.ClassDecl)n;
            }
        }
        return enter(n);
    }
}
