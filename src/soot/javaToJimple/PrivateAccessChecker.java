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

public class PrivateAccessChecker extends polyglot.visit.NodeVisitor {

    private ArrayList list;
      
    public ArrayList getList() {
        return list;
    }

    public PrivateAccessChecker(){
        list = new ArrayList();
    }

    public polyglot.ast.Node leave(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor visitor) {
    
        if (n instanceof polyglot.ast.Field) {
            
            polyglot.types.FieldInstance fi = ((polyglot.ast.Field)n).fieldInstance();

            if (fi.flags().isPrivate()) {
                list.add(fi);
            }
        }
        if (n instanceof polyglot.ast.Call) {
            
            polyglot.types.MethodInstance mi = ((polyglot.ast.Call)n).methodInstance();

            if (mi.flags().isPrivate()) {
                list.add(mi);
            }
        }
        return n;
    }
}
