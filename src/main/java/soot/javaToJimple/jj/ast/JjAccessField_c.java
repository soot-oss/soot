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
import polyglot.visit.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import java.util.*;

public class JjAccessField_c extends Expr_c implements Expr {

    private Call getMeth;
    private Call setMeth;
    private Field field;
    
    public JjAccessField_c(Position pos, Call getMeth, Call setMeth, Field field){
        super(pos);
        this.getMeth = getMeth;
        this.setMeth = setMeth;
        this.field = field;
    }

    public Call getMeth(){
        return getMeth;
    }

    public Call setMeth(){
        return setMeth;
    }

    public Field field() {
        return field;
    }
    
    public String toString(){
        return field+" "+getMeth+" "+setMeth;
    }
    
    public List acceptCFG(CFGBuilder v, List succs)
    {
        return succs;
    }           

    public Term entry(){
        return field.entry();
    }

    public Node visitChildren(NodeVisitor v){
        visitChild(field, v);
        visitChild(getMeth, v);
        visitChild(setMeth, v);
        return this;
    }
}

