package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class FIELD extends Domain {
    public Numberer numberer() { return new soot.util.JeddNumberer(Scene.v().getFieldNumberer()); }
    
    public final int bits = 20;
    
    public static Domain v() { return instance; }
    
    private static Domain instance = new FIELD();
    
    public FIELD() { super(); }
}
