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

package soot.jimple.toolkits.annotation.callgraph;

import java.util.*;

public class CallData {

    private final HashMap<Object, CallData> map = new HashMap<Object, CallData>();
    private final ArrayList<CallData> children = new ArrayList<CallData>();
    private final ArrayList<CallData> outputs = new ArrayList<CallData>();
    private String data;

    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("Data: ");
        sb.append(data);
        //sb.append(" Children: ");
        //sb.append(children);
        //sb.append(" Outputs: ");
        //sb.append(outputs);
        return sb.toString();
    }
    
    public void addChild(CallData cd){
        children.add(cd);
    }

    public void addOutput(CallData cd){
        if (!outputs.contains(cd)){
            outputs.add(cd);
        }
    }

    public void setData(String d){
        data = d;
    } 

    public String getData(){
        return data;
    }

    public ArrayList<CallData> getChildren(){
        return children;
    }

    public ArrayList<CallData> getOutputs(){
        return outputs;
    }

    public void addToMap(Object key, CallData val){
        map.put(key, val);
    }

    public HashMap<Object, CallData> getMap(){
        return map;
    }

}
