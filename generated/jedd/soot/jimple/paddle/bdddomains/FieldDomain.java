package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class FieldDomain extends Domain {
    public Numberer numberer() { return new soot.util.JeddNumberer(Scene.v().getFieldNumberer()); }
    
    public final int bits = 14;
    
    public static Domain v() { return instance; }
    
    private static Domain instance = new FieldDomain();
    
    public FieldDomain() { super(); }
}
