package soot.jimple.paddle.bdddomains;

import jedd.*;

public class VC3 extends PhysicalDomain {
    public int bits() { return 0; }
    
    public static PhysicalDomain v() { return instance; }
    
    private static PhysicalDomain instance = new VC3();
    
    public VC3() { super(); }
}
