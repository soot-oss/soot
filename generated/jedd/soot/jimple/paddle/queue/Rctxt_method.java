package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Rctxt_method {
    public abstract Iterator iterator();
    
    public abstract jedd.internal.RelationContainer get();
    
    public abstract boolean hasNext();
    
    public static class Tuple {
        private Context _ctxt;
        
        public Context ctxt() { return _ctxt; }
        
        private SootMethod _method;
        
        public SootMethod method() { return _method; }
        
        public Tuple(Context _ctxt, SootMethod _method) {
            super();
            this._ctxt = _ctxt;
            this._method = _method;
        }
        
        public int hashCode() { return 0; }
        
        public boolean equals(Object other) {
            if (!(other instanceof Tuple)) return false;
            Tuple o = (Tuple) other;
            if (o._ctxt != _ctxt) return false;
            if (o._method != _method) return false;
            return true;
        }
        
        public String toString() {
            StringBuffer ret = new StringBuffer();
            ret.append(this.ctxt());
            ret.append(", ");
            ret.append(this.method());
            ret.append(", ");
            return ret.toString();
        }
    }
    
    
    public Rctxt_method() { super(); }
}
