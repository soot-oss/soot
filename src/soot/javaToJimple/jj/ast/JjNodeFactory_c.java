package soot.javaToJimple.jj.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.types.Flags;
import polyglot.types.Package;
import polyglot.types.Type;
import polyglot.types.Qualifier;
import polyglot.util.*;
import java.util.*;

/**
 * NodeFactory for jj extension.
 */
public class JjNodeFactory_c extends NodeFactory_c implements JjNodeFactory {
    // TODO:  Implement factory methods for new AST nodes.
    // TODO:  Override factory methods for overriden AST nodes.
    // TODO:  Override factory methods for AST nodes with new extension nodes.

    public Unary Unary(Position pos, Unary.Operator op, Expr expr) {
        Unary n = new JjUnary_c(pos, op, expr);
        n = (Unary)n.ext(extFactory().extUnary());
        n = (Unary)n.del(delFactory().delUnary());
        return n;
    }
    
    public Binary Binary(Position pos, Expr left, Binary.Operator op, Expr right) {
        Binary n = new JjBinary_c(pos, left, op, right);
        n = (Binary)n.ext(extFactory().extBinary());
        n = (Binary)n.del(delFactory().delBinary());
        return n;
    }

    public Assign Assign(Position pos, Expr left, Assign.Operator op, Expr right) {
        Assign n;
        if (left instanceof Local) {
            return LocalAssign(pos, (Local)left, op, right);
        }
        else if (left instanceof Field) {
            return FieldAssign(pos, (Field)left, op, right);
        }
        else if (left instanceof ArrayAccess) {
            return ArrayAccessAssign(pos, (ArrayAccess)left, op, right);
        }
        return AmbAssign(pos, left, op, right);
    }

    
    public LocalAssign LocalAssign(Position pos, Local left, Assign.Operator op, Expr right) {
        LocalAssign n = new JjLocalAssign_c(pos, left, op, right);
        n = (LocalAssign)n.ext(extFactory().extLocalAssign());
        n = (LocalAssign)n.del(delFactory().delLocalAssign());
        return n;
    }
    
    public FieldAssign FieldAssign(Position pos, Field left, Assign.Operator op, Expr right) {
        FieldAssign n = new JjFieldAssign_c(pos, left, op, right);
        n = (FieldAssign)n.ext(extFactory().extFieldAssign());
        n = (FieldAssign)n.del(delFactory().delFieldAssign());
        return n;
    }
    
    public ArrayAccessAssign ArrayAccessAssign(Position pos, ArrayAccess left, Assign.Operator op, Expr right) {
        ArrayAccessAssign n = new JjArrayAccessAssign_c(pos, left, op, right);
        n = (ArrayAccessAssign)n.ext(extFactory().extArrayAccessAssign());
        n = (ArrayAccessAssign)n.del(delFactory().delArrayAccessAssign());
        return n;
    }
    
    public Cast Cast(Position pos, TypeNode type, Expr expr) {
        Cast n = new JjCast_c(pos, type, expr);
        n = (Cast)n.ext(extFactory().extCast());
        n = (Cast)n.del(delFactory().delCast());
        return n;
    }
    
    public ArrayInit ArrayInit(Position pos, List elements) {
        ArrayInit n = new JjArrayInit_c(pos, elements);
        n = (ArrayInit)n.ext(extFactory().extArrayInit());
        n = (ArrayInit)n.del(delFactory().delArrayInit());
        return n;
    }
}
