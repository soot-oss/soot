package soot.jimple.paddle.bdddomains;

import jedd.*;

public class VC1 extends PhysicalDomain {
    public int bits() { return 0; }
    
    public static PhysicalDomain v() { return instance; }
    
    private static PhysicalDomain instance = new VC1();
    
    public VC1() { super(); }
}
