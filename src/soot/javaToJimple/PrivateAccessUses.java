package soot.javaToJimple;
import java.util.*;

public class PrivateAccessUses extends polyglot.visit.NodeVisitor {

    private ArrayList list;
    private ArrayList avail;
    
    public ArrayList getList() {
        return list;
    }

    public void avail(ArrayList list){
        avail = list;
    }
    
    public PrivateAccessUses(){
        list = new ArrayList();
    }

    public polyglot.ast.Node leave(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor visitor) {
    
        if (n instanceof polyglot.ast.Field) {
            
            polyglot.types.FieldInstance fi = ((polyglot.ast.Field)n).fieldInstance();

            if (avail.contains(new polyglot.util.IdentityKey(fi))){
                list.add(new polyglot.util.IdentityKey(fi));
            }
        }
        if (n instanceof polyglot.ast.Call) {
            
            polyglot.types.ProcedureInstance pi = ((polyglot.ast.Call)n).methodInstance();

            if (avail.contains(new polyglot.util.IdentityKey(pi))) {
                list.add(new polyglot.util.IdentityKey(pi));
            }
        }
        if (n instanceof polyglot.ast.New) {
            
            polyglot.types.ProcedureInstance pi = ((polyglot.ast.New)n).constructorInstance();

            if (avail.contains(new polyglot.util.IdentityKey(pi))) {
                list.add(new polyglot.util.IdentityKey(pi));
            }
        }
        if (n instanceof polyglot.ast.ConstructorCall) {
            
            polyglot.types.ProcedureInstance pi = ((polyglot.ast.ConstructorCall)n).constructorInstance();

            if (avail.contains(new polyglot.util.IdentityKey(pi))) {
                list.add(new polyglot.util.IdentityKey(pi));
            }
        }
        return n;
    }
}
