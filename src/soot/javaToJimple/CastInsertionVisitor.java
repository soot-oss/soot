package soot.javaToJimple;

public class CastInsertionVisitor extends polyglot.visit.AscriptionVisitor {

    public CastInsertionVisitor(polyglot.frontend.Job job, polyglot.types.TypeSystem ts, polyglot.ast.NodeFactory nf) {
        super(job, ts, nf);
    }

    public polyglot.ast.Expr ascribe(polyglot.ast.Expr e, polyglot.types.Type toType) {
        polyglot.types.Type fromType = e.type();

        //System.out.println("in ascribe: "+e.getClass().toString());
        //System.out.println("toType: "+toType+" fromType: "+fromType+" expr: "+e);
        if (toType == null){
            return e;
        }
        if (toType.isVoid()) {
            return e;
        }

        if (toType.equals(fromType)){
            return e;
        }

        //System.out.println("toType: "+toType+" fromType: "+fromType+" expr: "+e);
        polyglot.util.Position p = e.position();

        if (toType.isPrimitive() && fromType.isPrimitive()) {
            polyglot.ast.Expr newExpr = nf.Cast(p, nf.CanonicalTypeNode(p, toType), e).type(toType);
            //System.out.println(newExpr);
            return newExpr;
        }
        
        return e;
        
    }
    
    public polyglot.ast.Node leaveCall(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor v) throws polyglot.types.SemanticException {
    
        n = super.leaveCall(old, n, v);

        //return n.ext().rewrite(typeSystem(), nodeFactory());    
        //return n.visit(v); 
        return n;
    }
}
