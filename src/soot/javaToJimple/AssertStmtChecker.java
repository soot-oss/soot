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

public class AssertStmtChecker extends polyglot.visit.NodeVisitor {

    private boolean hasAssert = false;

    public boolean isHasAssert() {
        return hasAssert;
    }

    public AssertStmtChecker(){
    }

    public polyglot.ast.Node override(polyglot.ast.Node parent, polyglot.ast.Node n){
        if (n instanceof polyglot.ast.ClassDecl){
            return n;
        }
        if ((n instanceof polyglot.ast.New) && (((polyglot.ast.New)n).anonType() != null)){
            return n;
        }
        return null;
    }
    
    public polyglot.visit.NodeVisitor enter(polyglot.ast.Node parent, polyglot.ast.Node n){
    
        if (n instanceof polyglot.ast.Assert) {
            hasAssert = true;
        }
        return enter(n);
    }
}
