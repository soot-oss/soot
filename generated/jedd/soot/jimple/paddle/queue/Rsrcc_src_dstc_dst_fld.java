package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Rsrcc_src_dstc_dst_fld implements Readers.Reader {
    public Rsrcc_src_dstc_dst_fld(String name) {
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
        private Context _srcc;
        
        public Context srcc() { return _srcc; }
        
        private VarNode _src;
        
        public VarNode src() { return _src; }
        
        private Context _dstc;
        
        public Context dstc() { return _dstc; }
        
        private VarNode _dst;
        
        public VarNode dst() { return _dst; }
        
        private PaddleField _fld;
        
        public PaddleField fld() { return _fld; }
        
        public Tuple(Context _srcc, VarNode _src, Context _dstc, VarNode _dst, PaddleField _fld) {
            super();
            this._srcc = _srcc;
            this._src = _src;
            this._dstc = _dstc;
            this._dst = _dst;
            this._fld = _fld;
        }
        
        public int hashCode() { return 0; }
        
        public boolean equals(Object other) {
            if (!(other instanceof Tuple)) return false;
            Tuple o = (Tuple) other;
            if (o._srcc != _srcc) return false;
            if (o._src != _src) return false;
            if (o._dstc != _dstc) return false;
            if (o._dst != _dst) return false;
            if (o._fld != _fld) return false;
            return true;
        }
        
        public String toString() {
            StringBuffer ret = new StringBuffer();
            ret.append(srcc());
            ret.append(", ");
            ret.append(src());
            ret.append(", ");
            ret.append(dstc());
            ret.append(", ");
            ret.append(dst());
            ret.append(", ");
            ret.append(fld());
            ret.append(", ");
            return ret.toString();
        }
    }
    
}
