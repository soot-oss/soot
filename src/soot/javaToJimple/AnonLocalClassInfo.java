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

public class AnonLocalClassInfo{

    private boolean inStaticMethod;
    private ArrayList finalLocals;

    public boolean inStaticMethod(){
        return inStaticMethod;
    }
    public void inStaticMethod(boolean b){
        inStaticMethod = b;
    }

    public ArrayList finalLocals(){
        return finalLocals;
    }
    public void finalLocals(ArrayList list){
        finalLocals = list;
    }

    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("static: ");
        sb.append(inStaticMethod);
        sb.append(" finalLocals: ");
        sb.append(finalLocals);
        return sb.toString();
    }
}
