package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Rvar_srcm_stmt_type_signature_kind implements Readers.Reader {
    public Rvar_srcm_stmt_type_signature_kind(String name) {
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
        
        private SootMethod _srcm;
        
        public SootMethod srcm() { return _srcm; }
        
        private Unit _stmt;
        
        public Unit stmt() { return _stmt; }
        
        private Type _type;
        
        public Type type() { return _type; }
        
        private NumberedString _signature;
        
        public NumberedString signature() { return _signature; }
        
        private Kind _kind;
        
        public Kind kind() { return _kind; }
        
        public Tuple(VarNode _var, SootMethod _srcm, Unit _stmt, Type _type, NumberedString _signature, Kind _kind) {
            super();
            this._var = _var;
            this._srcm = _srcm;
            this._stmt = _stmt;
            this._type = _type;
            this._signature = _signature;
            this._kind = _kind;
        }
        
        public int hashCode() { return 0; }
        
        public boolean equals(Object other) {
            if (!(other instanceof Tuple)) return false;
            Tuple o = (Tuple) other;
            if (o._var != _var) return false;
            if (o._srcm != _srcm) return false;
            if (o._stmt != _stmt) return false;
            if (o._type != _type) return false;
            if (o._signature != _signature) return false;
            if (o._kind != _kind) return false;
            return true;
        }
        
        public String toString() {
            StringBuffer ret = new StringBuffer();
            ret.append(var());
            ret.append(", ");
            ret.append(srcm());
            ret.append(", ");
            ret.append(stmt());
            ret.append(", ");
            ret.append(type());
            ret.append(", ");
            ret.append(signature());
            ret.append(", ");
            ret.append(kind());
            ret.append(", ");
            return ret.toString();
        }
    }
    
}
