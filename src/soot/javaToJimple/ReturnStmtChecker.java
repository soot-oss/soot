package soot.javaToJimple;
import java.util.*;

public class ReturnStmtChecker extends polyglot.visit.NodeVisitor {

    private boolean hasReturn;

    public boolean hasRet() {
        return hasReturn;
    }

    public ReturnStmtChecker(){
        hasReturn = false;
    }

    public polyglot.ast.Node leave(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor visitor) {
    
        if (n instanceof polyglot.ast.Return) {
            hasReturn = true;
        }
        return n;
    }
}
