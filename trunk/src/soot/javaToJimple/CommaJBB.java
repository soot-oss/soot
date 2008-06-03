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

public class CommaJBB extends AbstractJimpleBodyBuilder{

    public CommaJBB(){
        //ext(null);
        //base(this);
    }

    /*protected soot.Value createExpr(polyglot.ast.Expr expr){
        if (expr instanceof soot.javaToJimple.jj.ast.JjComma_c){
            return getCommaLocal((soot.javaToJimple.jj.ast.JjComma_c)expr);
        }
        else {
            return ext().createExpr(expr);
        }
    }*/
    
    protected soot.Value createAggressiveExpr(polyglot.ast.Expr expr, boolean redAggr, boolean revIfNec){
        if (expr instanceof soot.javaToJimple.jj.ast.JjComma_c){
            return getCommaLocal((soot.javaToJimple.jj.ast.JjComma_c)expr);
        }
        else {
            return ext().createAggressiveExpr(expr, redAggr, revIfNec);
        }
    }
    
    private soot.Value getCommaLocal(soot.javaToJimple.jj.ast.JjComma_c comma){
        base().createAggressiveExpr(comma.first(), false, false);
        soot.Value val = base().createAggressiveExpr(comma.second(), false, false);
        return val;
    }

}
