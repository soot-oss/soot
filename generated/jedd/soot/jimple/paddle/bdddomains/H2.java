package soot.jimple.paddle.bdddomains;

import jedd.*;

public class H2 extends PhysicalDomain {
    public int bits() { return 20; }
    
    public static PhysicalDomain v() { return instance; }
    
    private static PhysicalDomain instance = new H2();
    
    public H2() { super(); }
}
