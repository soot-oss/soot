/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.javaToJimple.jj.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.types.Flags;
import polyglot.util.*;
import java.util.*;

/**
 * NodeFactory for jj extension.
 */
public class JjNodeFactory_c extends NodeFactory_c implements JjNodeFactory {
    // TODO:  Implement factory methods for new AST nodes.
    // TODO:  Override factory methods for overriden AST nodes.
    // TODO:  Override factory methods for AST nodes with new extension nodes.

    public JjComma_c JjComma(Position pos, Expr first, Expr second){
        JjComma_c n = new JjComma_c(pos, first, second);
        return n;
    }
   
    public JjAccessField_c JjAccessField(Position pos, Call getMeth, Call setMeth, Field field){
        JjAccessField_c n = new JjAccessField_c(pos, getMeth, setMeth, field);
        return n;
    }
    
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
    
    public LocalDecl LocalDecl(Position pos, Flags flags, TypeNode type, String name, Expr init) {
        LocalDecl n = new JjLocalDecl_c(pos, flags, type, name, init);
        n = (LocalDecl)n.ext(extFactory().extLocalDecl());
        n = (LocalDecl)n.del(delFactory().delLocalDecl());
        return n;
    }
    
    public FieldAssign FieldAssign(Position pos, Field left, Assign.Operator op, Expr right) {
        FieldAssign n = new JjFieldAssign_c(pos, left, op, right);
        n = (FieldAssign)n.ext(extFactory().extFieldAssign());
        n = (FieldAssign)n.del(delFactory().delFieldAssign());
        return n;
    }
    
    public FieldDecl FieldDecl(Position pos, Flags flags, TypeNode type, String name, Expr init) {
        FieldDecl n = new JjFieldDecl_c(pos, flags, type, name, init);
        n = (FieldDecl)n.ext(extFactory().extFieldDecl());
        n = (FieldDecl)n.del(delFactory().delFieldDecl());
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
    
    public NewArray NewArray(Position pos, TypeNode base, List dims, int addDims, ArrayInit init) {
        //System.out.println("new array pos: "+pos);
        return super.NewArray(pos, base, dims, addDims, init);
    }
    
    public ArrayInit ArrayInit(Position pos, List elements) {
        ArrayInit n = new JjArrayInit_c(pos, elements);
        n = (ArrayInit)n.ext(extFactory().extArrayInit());
        n = (ArrayInit)n.del(delFactory().delArrayInit());
        return n;
    }

    public Return Return(Position pos, Expr expr) {
        Return n = new JjReturn_c(pos, expr);
        n = (Return)n.ext(extFactory().extReturn());
        n = (Return)n.del(delFactory().delReturn());
        return n;
    }
        
}
