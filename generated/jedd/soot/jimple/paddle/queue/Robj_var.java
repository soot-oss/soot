package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Robj_var implements Readers.Reader {
    public Robj_var(String name) {
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
        
        private VarNode _var;
        
        public VarNode var() { return _var; }
        
        public Tuple(AllocNode _obj, VarNode _var) {
            super();
            this._obj = _obj;
            this._var = _var;
        }
        
        public int hashCode() { return 0; }
        
        public boolean equals(Object other) {
            if (!(other instanceof Tuple)) return false;
            Tuple o = (Tuple) other;
            if (o._obj != _obj) return false;
            if (o._var != _var) return false;
            return true;
        }
        
        public String toString() {
            StringBuffer ret = new StringBuffer();
            ret.append(obj());
            ret.append(", ");
            ret.append(var());
            ret.append(", ");
            return ret.toString();
        }
    }
    
}
