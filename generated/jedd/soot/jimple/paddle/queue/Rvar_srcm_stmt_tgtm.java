package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Rvar_srcm_stmt_tgtm implements Readers.Reader {
    public Rvar_srcm_stmt_tgtm(String name) {
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
        
        private SootMethod _tgtm;
        
        public SootMethod tgtm() { return _tgtm; }
        
        public Tuple(VarNode _var, SootMethod _srcm, Unit _stmt, SootMethod _tgtm) {
            super();
            this._var = _var;
            this._srcm = _srcm;
            this._stmt = _stmt;
            this._tgtm = _tgtm;
        }
        
        public int hashCode() { return 0; }
        
        public boolean equals(Object other) {
            if (!(other instanceof Tuple)) return false;
            Tuple o = (Tuple) other;
            if (o._var != _var) return false;
            if (o._srcm != _srcm) return false;
            if (o._stmt != _stmt) return false;
            if (o._tgtm != _tgtm) return false;
            return true;
        }
        
        public String toString() {
            StringBuffer ret = new StringBuffer();
            ret.append(this.var());
            ret.append(", ");
            ret.append(this.srcm());
            ret.append(", ");
            ret.append(this.stmt());
            ret.append(", ");
            ret.append(this.tgtm());
            ret.append(", ");
            return ret.toString();
        }
    }
    
}
