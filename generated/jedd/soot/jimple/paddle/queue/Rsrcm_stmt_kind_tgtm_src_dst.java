package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Rsrcm_stmt_kind_tgtm_src_dst implements Readers.Reader {
    public Rsrcm_stmt_kind_tgtm_src_dst(String name) {
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
        private SootMethod _srcm;
        
        public SootMethod srcm() { return _srcm; }
        
        private Unit _stmt;
        
        public Unit stmt() { return _stmt; }
        
        private Kind _kind;
        
        public Kind kind() { return _kind; }
        
        private SootMethod _tgtm;
        
        public SootMethod tgtm() { return _tgtm; }
        
        private VarNode _src;
        
        public VarNode src() { return _src; }
        
        private VarNode _dst;
        
        public VarNode dst() { return _dst; }
        
        public Tuple(SootMethod _srcm, Unit _stmt, Kind _kind, SootMethod _tgtm, VarNode _src, VarNode _dst) {
            super();
            this._srcm = _srcm;
            this._stmt = _stmt;
            this._kind = _kind;
            this._tgtm = _tgtm;
            this._src = _src;
            this._dst = _dst;
        }
        
        public int hashCode() { return 0; }
        
        public boolean equals(Object other) {
            if (!(other instanceof Tuple)) return false;
            Tuple o = (Tuple) other;
            if (o._srcm != _srcm) return false;
            if (o._stmt != _stmt) return false;
            if (o._kind != _kind) return false;
            if (o._tgtm != _tgtm) return false;
            if (o._src != _src) return false;
            if (o._dst != _dst) return false;
            return true;
        }
        
        public String toString() {
            StringBuffer ret = new StringBuffer();
            ret.append(this.srcm());
            ret.append(", ");
            ret.append(this.stmt());
            ret.append(", ");
            ret.append(this.kind());
            ret.append(", ");
            ret.append(this.tgtm());
            ret.append(", ");
            ret.append(this.src());
            ret.append(", ");
            ret.append(this.dst());
            ret.append(", ");
            return ret.toString();
        }
    }
    
}
