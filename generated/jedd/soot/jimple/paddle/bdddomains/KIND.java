package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class KIND extends Domain {
    public Numberer numberer() { return new soot.util.JeddNumberer(Scene.v().kindNumberer()); }
    
    public final int bits = 4;
    
    public static Domain v() { return instance; }
    
    private static Domain instance = new KIND();
    
    public KIND() { super(); }
}
