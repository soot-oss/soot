package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Rvar_obj {
    public abstract Iterator iterator();
    
    public abstract jedd.internal.RelationContainer get();
    
    public abstract boolean hasNext();
    
    public static class Tuple {
        private VarNode _var;
        
        public VarNode var() { return _var; }
        
        private AllocNode _obj;
        
        public AllocNode obj() { return _obj; }
        
        public Tuple(VarNode _var, AllocNode _obj) {
            super();
            this._var = _var;
            this._obj = _obj;
        }
        
        public int hashCode() { return 0; }
        
        public boolean equals(Object other) {
            if (!(other instanceof Tuple)) return false;
            Tuple o = (Tuple) other;
            if (o._var != _var) return false;
            if (o._obj != _obj) return false;
            return true;
        }
        
        public String toString() {
            StringBuffer ret = new StringBuffer();
            ret.append(var());
            ret.append(", ");
            ret.append(obj());
            ret.append(", ");
            return ret.toString();
        }
    }
    
    
    public Rvar_obj() { super(); }
}
