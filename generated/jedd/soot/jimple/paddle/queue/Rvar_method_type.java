package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Rvar_method_type implements Readers.Reader {
    public Rvar_method_type(String name) {
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
        private VarNode _var;
        
        public VarNode var() { return _var; }
        
        private SootMethod _method;
        
        public SootMethod method() { return _method; }
        
        private Type _type;
        
        public Type type() { return _type; }
        
        public Tuple(VarNode _var, SootMethod _method, Type _type) {
            super();
            this._var = _var;
            this._method = _method;
            this._type = _type;
        }
        
        public int hashCode() { return 0; }
        
        public boolean equals(Object other) {
            if (!(other instanceof Tuple)) return false;
            Tuple o = (Tuple) other;
            if (o._var != _var) return false;
            if (o._method != _method) return false;
            if (o._type != _type) return false;
            return true;
        }
        
        public String toString() {
            StringBuffer ret = new StringBuffer();
            ret.append(var());
            ret.append(", ");
            ret.append(method());
            ret.append(", ");
            ret.append(type());
            ret.append(", ");
            return ret.toString();
        }
    }
    
}
