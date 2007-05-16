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

public class StrictFPPropagator extends polyglot.visit.NodeVisitor {

    boolean strict = false;
    public StrictFPPropagator(boolean val){
        strict = val;
    }

    public polyglot.visit.NodeVisitor enter(polyglot.ast.Node parent, polyglot.ast.Node n){
        if (n instanceof polyglot.ast.ClassDecl){
            if (((polyglot.ast.ClassDecl)n).flags().isStrictFP()){
                return new StrictFPPropagator(true);
            }
        }
        if (n instanceof polyglot.ast.LocalClassDecl){
            if (((polyglot.ast.LocalClassDecl)n).decl().flags().isStrictFP()){
                return new StrictFPPropagator(true);
            }
        }
        if (n instanceof polyglot.ast.MethodDecl){
            if (((polyglot.ast.MethodDecl)n).flags().isStrictFP()){
                return new StrictFPPropagator(true);
            }
        }
        if (n instanceof polyglot.ast.ConstructorDecl){
            if (((polyglot.ast.ConstructorDecl)n).flags().isStrictFP()){
                return new StrictFPPropagator(true);
            }
        }
        return this;
    }
    
    public polyglot.ast.Node leave(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor nodeVisitor){
    
        if (n instanceof polyglot.ast.MethodDecl) {
            polyglot.ast.MethodDecl decl = (polyglot.ast.MethodDecl)n;
            if (strict && !decl.flags().isAbstract() && !decl.flags().isStrictFP()){
                //  System.out.println("changing method decl "+decl);
                decl = decl.flags(decl.flags().StrictFP());
                //System.out.println("changed decl: "+decl);
                return decl;
            }
        }
        if (n instanceof polyglot.ast.ConstructorDecl) {
            polyglot.ast.ConstructorDecl decl = (polyglot.ast.ConstructorDecl)n;
            if (strict && !decl.flags().isAbstract() && !decl.flags().isStrictFP()){
                return decl.flags(decl.flags().StrictFP());
            }
        }
        if (n instanceof polyglot.ast.LocalClassDecl){
            polyglot.ast.LocalClassDecl decl = (polyglot.ast.LocalClassDecl)n;
            if (decl.decl().flags().isStrictFP()){
                return decl.decl().flags(decl.decl().flags().clearStrictFP());
            }
        }
        if (n instanceof polyglot.ast.ClassDecl){
            polyglot.ast.ClassDecl decl = (polyglot.ast.ClassDecl)n;
            if (decl.flags().isStrictFP()){
                return decl.flags(decl.flags().clearStrictFP());
            }
        }
        return n;
    }

}
