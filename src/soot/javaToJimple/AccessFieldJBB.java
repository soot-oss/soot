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


public class AccessFieldJBB extends AbstractJimpleBodyBuilder{

    public AccessFieldJBB(){
        //ext(null);
        //base(this);
    }

    protected boolean needsAccessor(polyglot.ast.Expr expr){
        if (expr instanceof soot.javaToJimple.jj.ast.JjAccessField_c){
            return true;
        }
        else {
            return ext().needsAccessor(expr);
        }
    }

    protected soot.Local handlePrivateFieldAssignSet(polyglot.ast.Assign assign){
        if ((assign.left() instanceof soot.javaToJimple.jj.ast.JjAccessField_c) && (assign.operator() != polyglot.ast.Assign.ASSIGN)){
            // not sure about strings here but...
            soot.javaToJimple.jj.ast.JjAccessField_c accessField = (soot.javaToJimple.jj.ast.JjAccessField_c)assign.left();
            
            soot.Local leftLocal = (soot.Local)base().createExpr(accessField.getMeth()); 
            soot.Value right = base().getAssignRightLocal(assign, leftLocal);
            return base().handlePrivateFieldSet(accessField, right);
        } 
        else {
            return ext().handlePrivateFieldAssignSet(assign);
        }
    }

    protected soot.Local handlePrivateFieldSet(polyglot.ast.Expr expr, soot.Value right){
        if (expr instanceof soot.javaToJimple.jj.ast.JjAccessField_c){
            soot.javaToJimple.jj.ast.JjAccessField_c accessField = (soot.javaToJimple.jj.ast.JjAccessField_c)expr;
            soot.SootMethodRef methToCall = base().getSootMethodRef(accessField.setMeth());
            ArrayList params = new ArrayList();
            if (!accessField.field().flags().isStatic()){
                params.add(base().getThis(Util.getSootType(accessField.field().target().type())));
            }
            params.add(right);
            soot.jimple.InvokeExpr invoke;
            if (methToCall.isStatic()){
                invoke = soot.jimple.Jimple.v().newStaticInvokeExpr(methToCall, params);
            }
            else {
                soot.Local baseLocal = (soot.Local)ext().getBaseLocal((polyglot.ast.Receiver)accessField.setMeth().target());
                invoke = soot.jimple.Jimple.v().newVirtualInvokeExpr(baseLocal, methToCall, params);
            }
            soot.Local retLocal = base().generateLocal(right.getType());
            soot.jimple.AssignStmt assignStmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, invoke);
            body.getUnits().add(assignStmt);
            
            return retLocal;
        }
        else {
            return ext().handlePrivateFieldSet(expr, right);
        }
    }

    protected soot.Value createExpr(polyglot.ast.Expr expr){
        if (expr instanceof soot.javaToJimple.jj.ast.JjAccessField_c){
            soot.javaToJimple.jj.ast.JjAccessField_c accessField = (soot.javaToJimple.jj.ast.JjAccessField_c)expr;
            return ext().createExpr(accessField.field());
        }
        else {
            return ext().createExpr(expr);
        }
    }
    
}
