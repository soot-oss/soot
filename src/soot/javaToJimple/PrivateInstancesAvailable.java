package soot.javaToJimple;
import java.util.*;

public class PrivateInstancesAvailable extends polyglot.visit.NodeVisitor {

    private ArrayList list;
      
    public ArrayList getList() {
        return list;
    }

    public PrivateInstancesAvailable(){
        list = new ArrayList();
    }

    public polyglot.ast.Node leave(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor visitor) {
    
        if (n instanceof polyglot.ast.FieldDecl) {
            
            polyglot.types.FieldInstance fi = ((polyglot.ast.FieldDecl)n).fieldInstance();

            if (fi.flags().isPrivate()) {
                list.add(new polyglot.util.IdentityKey(fi));
            }
        }
        if (n instanceof polyglot.ast.ProcedureDecl) {
            
            polyglot.types.ProcedureInstance pi = ((polyglot.ast.ProcedureDecl)n).procedureInstance();

            if (pi.flags().isPrivate()) {
                list.add(new polyglot.util.IdentityKey(pi));
            }
        }
        return n;
    }
}
