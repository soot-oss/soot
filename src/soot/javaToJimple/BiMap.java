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

public class BiMap {

    HashMap keyVal;
    HashMap valKey;
    
    public BiMap(){
    }

    public void put(Object key, Object val){
        if (keyVal == null){
            keyVal = new HashMap();
        }
        if (valKey == null){
            valKey = new HashMap();
        }

        keyVal.put(key, val);
        valKey.put(val, key);
        
    }

    public Object getKey(Object val){
        if (valKey == null) return null;
        return valKey.get(val);
    }

    public Object getVal(Object key){
        if (keyVal == null) return null;
        return keyVal.get(key);
    }

    public boolean containsKey(Object key){
        if (keyVal == null) return false;
        return keyVal.containsKey(key);
    }
    
    public boolean containsVal(Object val){
        if (valKey == null) return false;
        return valKey.containsKey(val);
    }
}
