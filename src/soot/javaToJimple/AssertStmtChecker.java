package soot.javaToJimple;
import java.util.*;

public class AssertStmtChecker extends polyglot.visit.NodeVisitor {

    private boolean hasAssert = false;

    public boolean isHasAssert() {
        return hasAssert;
    }

    public AssertStmtChecker(){
    }

    public polyglot.ast.Node leave(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor visitor) {
    
        if (n instanceof polyglot.ast.Assert) {
            hasAssert = true;
        }
        return n;
    }
}
