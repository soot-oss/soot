package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class SigDomain extends Domain {
    public Numberer numberer() { return new soot.util.JeddNumberer(Scene.v().getSubSigNumberer()); }
    
    public final int bits = 16;
    
    public static Domain v() { return instance; }
    
    private static Domain instance = new SigDomain();
    
    public SigDomain() { super(); }
}
