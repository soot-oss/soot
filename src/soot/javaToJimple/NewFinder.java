package soot.javaToJimple;
import java.util.*;

public class NewFinder extends polyglot.visit.NodeVisitor {

    private polyglot.types.ClassType typeToFind;
    private polyglot.ast.New newFound;

    
    public void typeToFind(polyglot.types.ClassType type){
        typeToFind = type;
    }
    
    public polyglot.ast.New newFound(){
        return newFound;
    }
    

    public NewFinder(){
        newFound = null;
    }

    public polyglot.visit.NodeVisitor enter(polyglot.ast.Node parent, polyglot.ast.Node n) {
    
        if (n instanceof polyglot.ast.New) {
            if (((polyglot.ast.New)n).anonType() != null){
                if (((polyglot.ast.New)n).anonType().equals(typeToFind)){
                    newFound = (polyglot.ast.New)n;
                }
            }
        }
        return enter(n);
    }
}
