package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Robj_type implements Readers.Reader {
    public Robj_type(String name) {
        super();
        this.name = name;
        Readers.v().add(this);
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract Iterator iterator();
    
    public abstract jedd.internal.RelationContainer get();
    
    public abstract boolean hasNext();
    
    public static class Tuple {
        private AllocNode _obj;
        
        public AllocNode obj() { return _obj; }
        
        private Type _type;
        
        public Type type() { return _type; }
        
        public Tuple(AllocNode _obj, Type _type) {
            super();
            this._obj = _obj;
            this._type = _type;
        }
        
        public int hashCode() { return 0; }
        
        public boolean equals(Object other) {
            if (!(other instanceof Tuple)) return false;
            Tuple o = (Tuple) other;
            if (o._obj != _obj) return false;
            if (o._type != _type) return false;
            return true;
        }
        
        public String toString() {
            StringBuffer ret = new StringBuffer();
            ret.append(this.obj());
            ret.append(", ");
            ret.append(this.type());
            ret.append(", ");
            return ret.toString();
        }
    }
    
}
