package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class STMT extends Domain {
    public Numberer numberer() { return new soot.util.JeddNumberer(Scene.v().getUnitNumberer()); }
    
    public final int bits = 20;
    
    public static Domain v() { return instance; }
    
    private static Domain instance = new STMT();
    
    public STMT() { super(); }
}
