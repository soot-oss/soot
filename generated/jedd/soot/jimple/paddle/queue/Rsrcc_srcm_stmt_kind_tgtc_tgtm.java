package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Rsrcc_srcm_stmt_kind_tgtc_tgtm {
    public abstract Iterator iterator();
    
    public abstract jedd.internal.RelationContainer get();
    
    public abstract boolean hasNext();
    
    public static class Tuple {
        private Context _srcc;
        
        public Context srcc() { return _srcc; }
        
        private SootMethod _srcm;
        
        public SootMethod srcm() { return _srcm; }
        
        private Unit _stmt;
        
        public Unit stmt() { return _stmt; }
        
        private Kind _kind;
        
        public Kind kind() { return _kind; }
        
        private Context _tgtc;
        
        public Context tgtc() { return _tgtc; }
        
        private SootMethod _tgtm;
        
        public SootMethod tgtm() { return _tgtm; }
        
        public Tuple(Context _srcc, SootMethod _srcm, Unit _stmt, Kind _kind, Context _tgtc, SootMethod _tgtm) {
            super();
            this._srcc = _srcc;
            this._srcm = _srcm;
            this._stmt = _stmt;
            this._kind = _kind;
            this._tgtc = _tgtc;
            this._tgtm = _tgtm;
        }
        
        public int hashCode() { return 0; }
        
        public boolean equals(Object other) {
            if (!(other instanceof Tuple)) return false;
            Tuple o = (Tuple) other;
            if (o._srcc != _srcc) return false;
            if (o._srcm != _srcm) return false;
            if (o._stmt != _stmt) return false;
            if (o._kind != _kind) return false;
            if (o._tgtc != _tgtc) return false;
            if (o._tgtm != _tgtm) return false;
            return true;
        }
        
        public String toString() {
            StringBuffer ret = new StringBuffer();
            ret.append(srcc());
            ret.append(", ");
            ret.append(srcm());
            ret.append(", ");
            ret.append(stmt());
            ret.append(", ");
            ret.append(kind());
            ret.append(", ");
            ret.append(tgtc());
            ret.append(", ");
            ret.append(tgtm());
            ret.append(", ");
            return ret.toString();
        }
    }
    
    
    public Rsrcc_srcm_stmt_kind_tgtc_tgtm() { super(); }
}
