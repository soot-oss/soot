package soot.jimple.paddle.bdddomains;

import jedd.*;

public class SG extends PhysicalDomain {
    public int bits() { return 16; }
    
    public static PhysicalDomain v() { return instance; }
    
    private static PhysicalDomain instance = new SG();
    
    public SG() { super(); }
}
