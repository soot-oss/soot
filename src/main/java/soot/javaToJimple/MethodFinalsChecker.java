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

import polyglot.ast.Node;
import polyglot.util.IdentityKey;

public class MethodFinalsChecker extends polyglot.visit.NodeVisitor{

    private final ArrayList<IdentityKey> inners;
    private final ArrayList<IdentityKey> finalLocals;
    private final HashMap<IdentityKey, ArrayList<IdentityKey>> typeToLocalsUsed;
    private final ArrayList<Node> ccallList;
    
    public HashMap<IdentityKey, ArrayList<IdentityKey>> typeToLocalsUsed(){
        return typeToLocalsUsed;
    }
    
    public ArrayList<IdentityKey> finalLocals(){
        return finalLocals;
    }
    
    public ArrayList<IdentityKey> inners(){
        return inners;
    }
    
    public ArrayList<Node> ccallList(){
        return ccallList;
    }
    
    
    public MethodFinalsChecker(){
        finalLocals = new ArrayList<IdentityKey>();
        inners = new ArrayList<IdentityKey>();
        ccallList = new ArrayList<Node>();
        typeToLocalsUsed = new HashMap<IdentityKey, ArrayList<IdentityKey>>();
    }

    public polyglot.ast.Node override(polyglot.ast.Node parent, polyglot.ast.Node n){
        if (n instanceof polyglot.ast.LocalClassDecl){
            inners.add(new polyglot.util.IdentityKey(((polyglot.ast.LocalClassDecl)n).decl().type()));
            polyglot.ast.ClassBody localClassBody = ((polyglot.ast.LocalClassDecl)n).decl().body();
            LocalUsesChecker luc = new LocalUsesChecker();
            localClassBody.visit(luc);
            typeToLocalsUsed.put(new polyglot.util.IdentityKey(((polyglot.ast.LocalClassDecl)n).decl().type()), luc.getLocals());
            return n;
        }
        else if (n instanceof polyglot.ast.New) {
            if (((polyglot.ast.New)n).anonType() != null) {
                inners.add(new polyglot.util.IdentityKey(((polyglot.ast.New)n).anonType()));
                polyglot.ast.ClassBody anonClassBody = ((polyglot.ast.New)n).body();
                LocalUsesChecker luc = new LocalUsesChecker();
                anonClassBody.visit(luc);
                typeToLocalsUsed.put(new polyglot.util.IdentityKey(((polyglot.ast.New)n).anonType()), luc.getLocals());
                return n;
            }
        }
        return null;
    }
    
    public polyglot.visit.NodeVisitor enter(polyglot.ast.Node parent, polyglot.ast.Node n) {
    
        
        if (n instanceof polyglot.ast.LocalDecl){
            polyglot.ast.LocalDecl ld = (polyglot.ast.LocalDecl)n;
            if (ld.flags().isFinal()){
                if (!finalLocals.contains(new polyglot.util.IdentityKey(ld.localInstance()))){
                    finalLocals.add(new polyglot.util.IdentityKey(ld.localInstance()));
                }
            }
        }
        if (n instanceof polyglot.ast.Formal){
            polyglot.ast.Formal ld = (polyglot.ast.Formal)n;
            if (ld.flags().isFinal()){
                if (!finalLocals.contains(new polyglot.util.IdentityKey(ld.localInstance()))){
                    finalLocals.add(new polyglot.util.IdentityKey(ld.localInstance()));
                }
            }
        }

        if (n instanceof polyglot.ast.ConstructorCall){
            ccallList.add(n);
        }
        return enter(n);
    }
}
