package soot.javaToJimple;
import java.util.*;

public class LocalUsesChecker extends polyglot.visit.NodeVisitor{

    //private ArrayList localDecls;
    private ArrayList locals;
    /*private HashMap map;
    
    public ArrayList getLocalDecls(){
        return localDecls;
    }*/
    
    public ArrayList getLocals() {
        return locals;
    }

    public LocalUsesChecker(){
        //map = new HashMap();
        //localDecls = new ArrayList();
        locals = new ArrayList();
    }

    public polyglot.ast.Node leave(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor visitor) {
    
        /*if (n instanceof polyglot.ast.LocalDecl){
            //System.out.println("adding to localDecls: "+n);
            localDecls.add(new polyglot.util.IdentityKey(((polyglot.ast.LocalDecl)n).localInstance()));
        }*/
        if (n instanceof polyglot.ast.Local){
            if (!(locals.contains(new polyglot.util.IdentityKey(((polyglot.ast.Local)n).localInstance())))){
                //System.out.println("adding to locals: "+n);
                locals.add(new polyglot.util.IdentityKey(((polyglot.ast.Local)n).localInstance()));
            }
        }
        /*if (n instanceof polyglot.ast.Formal){
            //System.out.println("adding a formal: "+n);
            localDecls.add(new polyglot.util.IdentityKey(((polyglot.ast.Formal)n).localInstance()));
        }*/
        return n;
    }
}
