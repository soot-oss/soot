package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Rvarc_var_objc_obj implements Readers.Reader {
    public Rvarc_var_objc_obj(String name) {
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
        private Context _varc;
        
        public Context varc() { return _varc; }
        
        private VarNode _var;
        
        public VarNode var() { return _var; }
        
        private Context _objc;
        
        public Context objc() { return _objc; }
        
        private AllocNode _obj;
        
        public AllocNode obj() { return _obj; }
        
        public Tuple(Context _varc, VarNode _var, Context _objc, AllocNode _obj) {
            super();
            this._varc = _varc;
            this._var = _var;
            this._objc = _objc;
            this._obj = _obj;
        }
        
        public int hashCode() { return 0; }
        
        public boolean equals(Object other) {
            if (!(other instanceof Tuple)) return false;
            Tuple o = (Tuple) other;
            if (o._varc != _varc) return false;
            if (o._var != _var) return false;
            if (o._objc != _objc) return false;
            if (o._obj != _obj) return false;
            return true;
        }
        
        public String toString() {
            StringBuffer ret = new StringBuffer();
            ret.append(this.varc());
            ret.append(", ");
            ret.append(this.var());
            ret.append(", ");
            ret.append(this.objc());
            ret.append(", ");
            ret.append(this.obj());
            ret.append(", ");
            return ret.toString();
        }
    }
    
}
