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
