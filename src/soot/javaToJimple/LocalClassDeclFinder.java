package soot.javaToJimple;
import java.util.*;

public class LocalClassDeclFinder extends polyglot.visit.NodeVisitor {

    private polyglot.types.ClassType typeToFind;
    private polyglot.ast.LocalClassDecl declFound;

    
    public void typeToFind(polyglot.types.ClassType type){
        typeToFind = type;
    }
    
    public polyglot.ast.LocalClassDecl declFound(){
        return declFound;
    }
    

    public LocalClassDeclFinder(){
        declFound = null;
    }

    public polyglot.visit.NodeVisitor enter(polyglot.ast.Node parent, polyglot.ast.Node n) {
    
        if (n instanceof polyglot.ast.LocalClassDecl) {
            if (((polyglot.ast.LocalClassDecl)n).decl().type().equals(typeToFind)){
                declFound = (polyglot.ast.LocalClassDecl)n;
            }
        }
        return enter(n);
    }
}
