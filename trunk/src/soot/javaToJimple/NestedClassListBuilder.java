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

public class NestedClassListBuilder extends polyglot.visit.NodeVisitor {

    private final ArrayList<Node> classDeclsList;
    private final ArrayList<Node> anonClassBodyList;
    private final ArrayList<Node> nestedUsedList;
    
    public ArrayList<Node> getClassDeclsList() {
        return classDeclsList;
    }
    
    public ArrayList<Node> getAnonClassBodyList() {
        return anonClassBodyList;
    }
    
    public ArrayList<Node> getNestedUsedList() {
        return nestedUsedList;
    }

    public NestedClassListBuilder(){
        classDeclsList = new ArrayList<Node>();
        anonClassBodyList = new ArrayList<Node>();
        nestedUsedList = new ArrayList<Node>();
    }

    public polyglot.visit.NodeVisitor enter(polyglot.ast.Node parent, polyglot.ast.Node n) {
    
        if (n instanceof polyglot.ast.New) {
            
            if ((((polyglot.ast.New)n).anonType() != null) && (((polyglot.ast.New)n).body() != null)){
                anonClassBodyList.add(n);
            }
            else if (((polyglot.types.ClassType)((polyglot.ast.New)n).objectType().type()).isNested()){
                nestedUsedList.add(n);
            }
        }
        if (n instanceof polyglot.ast.ClassDecl) {

            if (((polyglot.types.ClassType)((polyglot.ast.ClassDecl)n).type()).isNested()){
                classDeclsList.add(n);
            }
        }
        return enter(n);
    }
}
