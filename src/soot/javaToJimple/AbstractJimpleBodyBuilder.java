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
    
    protected abstract soot.Value createExpr(polyglot.ast.Expr expr);
    //    return ext().createExpr(expr);
    //}
    
    protected abstract void createStmt(polyglot.ast.Stmt stmt);
    //    ext().createStmt(stmt);
    //}
}
