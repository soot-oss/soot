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

package soot.toolkits.graph.interaction;

public class FlowInfo {

    private Object info;
    private Object unit;
    private boolean before;

    public FlowInfo(Object info, Object unit, boolean b){
        info(info);
        unit(unit);
        setBefore(b);
    }
    
    public Object unit(){
        return unit;
    }

    public void unit(Object u){
        unit = u;
    }
    
    public Object info(){
        return info;
    }

    public void info(Object i){
        info = i;
    }

    public boolean isBefore(){
        return before;
    }

    public void setBefore(boolean b){
        before = b;
    }

    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("unit: "+unit);
        sb.append(" info: "+info);
        sb.append(" before: "+before);
        return sb.toString();
    }
}
