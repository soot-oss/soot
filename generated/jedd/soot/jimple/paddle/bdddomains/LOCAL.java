package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.jimple.paddle.*;
import soot.*;

public class LOCAL extends Domain {
    public Numberer numberer() { return new soot.util.JeddNumberer(Scene.v().getLocalNumberer()); }
    
    public final int bits = 20;
    
    public static Domain v() { return instance; }
    
    private static Domain instance = new LOCAL();
    
    public LOCAL() { super(); }
}
