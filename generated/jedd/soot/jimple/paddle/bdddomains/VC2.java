package soot.jimple.paddle.bdddomains;

import jedd.*;

public class VC2 extends PhysicalDomain {
    public int bits() { return 0; }
    
    public static PhysicalDomain v() { return instance; }
    
    private static PhysicalDomain instance = new VC2();
    
    public VC2() { super(); }
}
