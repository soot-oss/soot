package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Rsrc_dst_fld implements Readers.Reader {
    public Rsrc_dst_fld(String name) {
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
        private VarNode _src;
        
        public VarNode src() { return _src; }
        
        private VarNode _dst;
        
        public VarNode dst() { return _dst; }
        
        private PaddleField _fld;
        
        public PaddleField fld() { return _fld; }
        
        public Tuple(VarNode _src, VarNode _dst, PaddleField _fld) {
            super();
            this._src = _src;
            this._dst = _dst;
            this._fld = _fld;
        }
        
        public int hashCode() { return 0; }
        
        public boolean equals(Object other) {
            if (!(other instanceof Tuple)) return false;
            Tuple o = (Tuple) other;
            if (o._src != _src) return false;
            if (o._dst != _dst) return false;
            if (o._fld != _fld) return false;
            return true;
        }
        
        public String toString() {
            StringBuffer ret = new StringBuffer();
            ret.append(src());
            ret.append(", ");
            ret.append(dst());
            ret.append(", ");
            ret.append(fld());
            ret.append(", ");
            return ret.toString();
        }
    }
    
}
