package soot.jimple.paddle.bdddomains;

import jedd.*;

public class H1 extends PhysicalDomain {
    public int bits() { return 20; }
    
    public static PhysicalDomain v() { return instance; }
    
    private static PhysicalDomain instance = new H1();
    
    public H1() { super(); }
}
