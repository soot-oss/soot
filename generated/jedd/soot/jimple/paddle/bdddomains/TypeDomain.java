package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class TypeDomain extends Domain {
    public Numberer numberer() { return new soot.util.JeddNumberer(Scene.v().getTypeNumberer()); }
    
    public final int bits = 12;
    
    public static Domain v() { return instance; }
    
    private static Domain instance = new TypeDomain();
    
    public TypeDomain() { super(); }
}
