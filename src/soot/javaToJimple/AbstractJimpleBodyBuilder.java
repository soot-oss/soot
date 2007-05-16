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



package soot.javaToJimple;

import java.util.*;

public abstract class AbstractJimpleBodyBuilder {
   
    protected soot.jimple.JimpleBody body;

    public void ext(AbstractJimpleBodyBuilder ext){
        this.ext = ext;
        if (ext.ext != null){
            throw new RuntimeException("Extensions created in wrong order.");
        }
        ext.base = this.base;
    }
    public AbstractJimpleBodyBuilder ext(){
        if (ext == null) return this;
        return ext;
    }
    private AbstractJimpleBodyBuilder ext = null;
    
    public void base(AbstractJimpleBodyBuilder base){
        this.base = base;
    }
    public AbstractJimpleBodyBuilder base(){
        return base;
    }
    private AbstractJimpleBodyBuilder base = this;
    
    protected soot.jimple.JimpleBody createJimpleBody(polyglot.ast.Block block, List formals, soot.SootMethod sootMethod){
        return ext().createJimpleBody(block, formals, sootMethod);
    }
    
    /*protected soot.Value createExpr(polyglot.ast.Expr expr){
        return ext().createExpr(expr);
    }*/
    
    protected soot.Value createAggressiveExpr(polyglot.ast.Expr expr, boolean reduceAggressively, boolean reverseCondIfNec){
        //System.out.println("in abstract");
        return ext().createAggressiveExpr(expr, reduceAggressively, reverseCondIfNec);
    }
    
    protected void createStmt(polyglot.ast.Stmt stmt){
        ext().createStmt(stmt);
    }

    protected boolean needsAccessor(polyglot.ast.Expr expr){
        return ext().needsAccessor(expr);
    }
    
    protected soot.Local handlePrivateFieldAssignSet(polyglot.ast.Assign assign){
        return ext().handlePrivateFieldAssignSet(assign);
    }
    
    protected soot.Local handlePrivateFieldUnarySet(polyglot.ast.Unary unary){
        return ext().handlePrivateFieldUnarySet(unary);
    }
    

    protected soot.Value getAssignRightLocal(polyglot.ast.Assign assign, soot.Local leftLocal){
        return ext().getAssignRightLocal(assign, leftLocal);
    }
   
    protected soot.Value getSimpleAssignRightLocal(polyglot.ast.Assign assign){
        return ext().getSimpleAssignRightLocal(assign);
    }
   
    protected soot.Local handlePrivateFieldSet(polyglot.ast.Expr expr, soot.Value right, soot.Value base){
        return ext().handlePrivateFieldSet(expr, right, base);
    }

    protected soot.SootMethodRef getSootMethodRef(polyglot.ast.Call call){
        return ext().getSootMethodRef(call);
    }

    protected soot.Local generateLocal(soot.Type sootType){
        return ext().generateLocal(sootType);
    }

    protected soot.Local generateLocal(polyglot.types.Type polyglotType){
        return ext().generateLocal(polyglotType);
    }

    protected soot.Local getThis(soot.Type sootType){
        return ext().getThis(sootType);
    }

    protected soot.Value getBaseLocal(polyglot.ast.Receiver receiver){
        return ext().getBaseLocal(receiver);
    }

    protected soot.Value createLHS(polyglot.ast.Expr expr){
        return ext().createLHS(expr);
    }

    protected soot.jimple.FieldRef getFieldRef(polyglot.ast.Field field){
        return ext().getFieldRef(field);
    }

    protected soot.jimple.Constant  getConstant(soot.Type sootType, int val){
        return ext().getConstant(sootType, val);
    }
}
