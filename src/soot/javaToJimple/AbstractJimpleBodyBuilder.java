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

import soot.SootFieldRef;

public abstract class AbstractJimpleBodyBuilder {
    
    protected abstract AbstractJimpleBodyBuilder ext();
    public void ext(AbstractJimpleBodyBuilder ext){
        this.ext = ext;
    }
    protected AbstractJimpleBodyBuilder ext;

    protected soot.jimple.JimpleBody createJimpleBody(polyglot.ast.Block block, List formals, soot.SootMethod sootMethod){
        return ext().createJimpleBody(block, formals, sootMethod);
    }
    
    protected soot.Value createExpr(polyglot.ast.Expr expr){
        return ext().createExpr(expr);
    }
    
    protected void createStmt(polyglot.ast.Stmt stmt){
        ext().createStmt(stmt);
    }
}
