package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;
import soot.jimple.paddle.*;

public class ShadowDomain extends Domain {
    public Numberer numberer() { return new soot.util.JeddNumberer(ShadowNumberer.v()); }
    
    public final int bits = 16;
    
    public static Domain v() { return instance; }
    
    private static Domain instance = new ShadowDomain();
    
    public ShadowDomain() { super(); }
}
