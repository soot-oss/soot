package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Rsrc_dst {
    public abstract Iterator iterator();
    
    public abstract jedd.internal.RelationContainer get();
    
    public abstract boolean hasNext();
    
    public static class Tuple {
        private VarNode _src;
        
        public VarNode src() { return _src; }
        
        private VarNode _dst;
        
        public VarNode dst() { return _dst; }
        
        public Tuple(VarNode _src, VarNode _dst) {
            super();
            this._src = _src;
            this._dst = _dst;
        }
        
        public int hashCode() { return 0; }
        
        public boolean equals(Object other) {
            if (!(other instanceof Tuple)) return false;
            Tuple o = (Tuple) other;
            if (o._src != _src) return false;
            if (o._dst != _dst) return false;
            return true;
        }
        
        public String toString() {
            StringBuffer ret = new StringBuffer();
            ret.append(src());
            ret.append(", ");
            ret.append(dst());
            ret.append(", ");
            return ret.toString();
        }
    }
    
    
    public Rsrc_dst() { super(); }
}
