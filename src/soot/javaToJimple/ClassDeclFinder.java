package soot.javaToJimple;
import java.util.*;

public class ClassDeclFinder extends polyglot.visit.NodeVisitor {

    //private polyglot.types.ClassType typeToFind;
    private ArrayList typesToFind;
    private ArrayList declsFound;

    
    public void typesToFind(ArrayList types){
        typesToFind = types;
    }
    
    public ArrayList declsFound(){
        return declsFound;
    }
    

    public ClassDeclFinder(){
        declsFound = new ArrayList();
    }

    public polyglot.visit.NodeVisitor enter(polyglot.ast.Node parent, polyglot.ast.Node n) {
    
        if (n instanceof polyglot.ast.ClassDecl) {
            if (typesToFind.contains(((polyglot.ast.ClassDecl)n).type())){
                declsFound.add((polyglot.ast.ClassDecl)n);
            }
        }
        return enter(n);
    }
}
