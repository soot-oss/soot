package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class MethodDomain extends Domain {
    public Numberer numberer() { return new soot.util.JeddNumberer(Scene.v().getMethodNumberer()); }
    
    public final int bits = 20;
    
    public static Domain v() { return instance; }
    
    private static Domain instance = new MethodDomain();
    
    public MethodDomain() { super(); }
}
