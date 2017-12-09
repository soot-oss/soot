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

    protected soot.Local handlePrivateFieldUnarySet(polyglot.ast.Unary unary){
        if (unary.expr() instanceof soot.javaToJimple.jj.ast.JjAccessField_c){
            // not sure about strings here but...
            soot.javaToJimple.jj.ast.JjAccessField_c accessField = (soot.javaToJimple.jj.ast.JjAccessField_c)unary.expr();
          
            // create field target
            soot.Local baseLocal = (soot.Local)base().getBaseLocal(accessField.field().target());
            //soot.Value fieldGetLocal = getPrivateAccessFieldLocal(accessField, base);
            soot.Local fieldGetLocal = handleCall(accessField.field(), accessField.getMeth(), null, baseLocal);

            soot.Local tmp = base().generateLocal(accessField.field().type());
            soot.jimple.AssignStmt stmt1 = soot.jimple.Jimple.v().newAssignStmt(tmp, fieldGetLocal);
            ext().body.getUnits().add(stmt1);
            Util.addLnPosTags(stmt1, unary.position());
            soot.Value incVal = base().getConstant(Util.getSootType(accessField.field().type()), 1);
            soot.jimple.BinopExpr binExpr;
            if (unary.operator() == polyglot.ast.Unary.PRE_INC || unary.operator() == polyglot.ast.Unary.POST_INC){
                binExpr = soot.jimple.Jimple.v().newAddExpr(tmp, incVal);
            }
            else {
                binExpr = soot.jimple.Jimple.v().newSubExpr(tmp, incVal);
            }
            soot.Local tmp2 = generateLocal(accessField.field().type());
            soot.jimple.AssignStmt assign = soot.jimple.Jimple.v().newAssignStmt(tmp2, binExpr);
            ext().body.getUnits().add(assign);
            if (unary.operator() == polyglot.ast.Unary.PRE_INC || unary.operator() == polyglot.ast.Unary.PRE_DEC){
                return base().handlePrivateFieldSet(accessField, tmp2, baseLocal);
            }
            else {
                base().handlePrivateFieldSet(accessField, tmp2, baseLocal);
                return tmp;
            }
        }
        else {
            return ext().handlePrivateFieldUnarySet(unary);
        }
    
    }
    protected soot.Local handlePrivateFieldAssignSet(polyglot.ast.Assign assign){
        if (assign.left() instanceof soot.javaToJimple.jj.ast.JjAccessField_c){
            // not sure about strings here but...
            soot.javaToJimple.jj.ast.JjAccessField_c accessField = (soot.javaToJimple.jj.ast.JjAccessField_c)assign.left();
          
            // create field target
            soot.Local baseLocal = (soot.Local)base().getBaseLocal(accessField.field().target());
            if (assign.operator() == polyglot.ast.Assign.ASSIGN){
                soot.Value right = base().getSimpleAssignRightLocal(assign);
                return base().handlePrivateFieldSet(accessField, right, baseLocal);
            }
            else {

                // create field get using target
                soot.Local leftLocal = handleCall(accessField.field(), accessField.getMeth(), null, baseLocal);
                // handle field set using same target
                //soot.Local leftLocal = (soot.Local)base().createExpr(accessField); 
                soot.Value right = base().getAssignRightLocal(assign, leftLocal);
                return handleFieldSet(accessField, right, baseLocal);
            }
        }
        else {
            return ext().handlePrivateFieldAssignSet(assign);
        }
    }

    private soot.Local handleCall(polyglot.ast.Field field, polyglot.ast.Call call, soot.Value param, soot.Local base){
        
        soot.Type sootRecType = Util.getSootType(call.target().type());
        soot.SootClass receiverTypeClass = soot.Scene.v().getSootClass("java.lang.Object");
        if (sootRecType instanceof soot.RefType){
            receiverTypeClass = ((soot.RefType)sootRecType).getSootClass();
        }
        
        soot.SootMethodRef methToCall = base().getSootMethodRef(call);
        List<soot.Value> params = new ArrayList<soot.Value>();
        /*if (!field.flags().isStatic()){
            //params.add(base().getThis(Util.getSootType(field.target().type())));
            params.add((soot.Local)ext().getBaseLocal(field.target()));
        }*/
        if (param != null){
            params.add(param);
        }
        soot.jimple.InvokeExpr invoke;
        
        soot.Local baseLocal = base;
        if (base == null){
            baseLocal = (soot.Local)ext().getBaseLocal(call.target());
        }
        if (methToCall.isStatic()){
            invoke = soot.jimple.Jimple.v().newStaticInvokeExpr(methToCall, params);
        }
        else if (soot.Modifier.isInterface(receiverTypeClass.getModifiers()) && 
call.methodInstance().flags().isAbstract()){
            invoke = soot.jimple.Jimple.v().newInterfaceInvokeExpr(baseLocal, methToCall, params);
        }
        else {
            invoke = soot.jimple.Jimple.v().newVirtualInvokeExpr(baseLocal, methToCall, params);
        }
        soot.Local retLocal = base().generateLocal(field.type());
        soot.jimple.AssignStmt assignStmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, invoke);
        ext().body.getUnits().add(assignStmt);
        
        return retLocal;
    }
    
    protected soot.Local handlePrivateFieldSet(polyglot.ast.Expr expr, soot.Value right, soot.Value baseLocal){
        if (expr instanceof soot.javaToJimple.jj.ast.JjAccessField_c){
            soot.javaToJimple.jj.ast.JjAccessField_c accessField = (soot.javaToJimple.jj.ast.JjAccessField_c)expr;
            return handleCall(accessField.field(), accessField.setMeth(), right, null);
        }
        else {
            return ext().handlePrivateFieldSet(expr, right, baseLocal);
        }
    }

    private soot.Local handleFieldSet(soot.javaToJimple.jj.ast.JjAccessField_c accessField, soot.Value right, soot.Local base){
        return handleCall(accessField.field(), accessField.setMeth(), right, base);
    }
    /*protected soot.Value createExpr(polyglot.ast.Expr expr){
        if (expr instanceof soot.javaToJimple.jj.ast.JjAccessField_c){
            soot.javaToJimple.jj.ast.JjAccessField_c accessField = (soot.javaToJimple.jj.ast.JjAccessField_c)expr;
         
            
            // here is where we need to return the field using get method
            return handleCall(accessField.field(), accessField.getMeth(), null, null);
            // return ext().createExpr(accessField.field());
        }
        else {
            return ext().createExpr(expr);
        }
    }*/

    protected soot.Value createAggressiveExpr(polyglot.ast.Expr expr, boolean redAgg, boolean revIfNec){
        if (expr instanceof soot.javaToJimple.jj.ast.JjAccessField_c){
            soot.javaToJimple.jj.ast.JjAccessField_c accessField = (soot.javaToJimple.jj.ast.JjAccessField_c)expr;
         
            
            // here is where we need to return the field using get method
            return handleCall(accessField.field(), accessField.getMeth(), null, null);
            // return ext().createExpr(accessField.field());
        }
        else {
            return ext().createAggressiveExpr(expr, redAgg, revIfNec);
        }
    }

    protected soot.Value createLHS(polyglot.ast.Expr expr){
        if (expr instanceof soot.javaToJimple.jj.ast.JjAccessField_c) {
            soot.javaToJimple.jj.ast.JjAccessField_c accessField = (soot.javaToJimple.jj.ast.JjAccessField_c)expr;
        
            return handleCall(accessField.field(), accessField.getMeth(), null, null);//base().getFieldRef(accessField.field());
        }
        else {
            return ext().createLHS(expr);
        }
    }
    
}
