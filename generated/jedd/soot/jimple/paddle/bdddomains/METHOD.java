package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class METHOD extends Domain {
    public Numberer numberer() { return new soot.util.JeddNumberer(Scene.v().getMethodNumberer()); }
    
    public final int bits = 20;
    
    public static Domain v() { return instance; }
    
    private static Domain instance = new METHOD();
    
    public METHOD() { super(); }
}
