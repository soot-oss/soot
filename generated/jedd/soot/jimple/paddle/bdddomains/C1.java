package soot.jimple.paddle.bdddomains;

import jedd.*;

public class C1 extends PhysicalDomain {
    public int bits() { return 61; }
    
    public static PhysicalDomain v() { return instance; }
    
    private static PhysicalDomain instance = new C1();
    
    public C1() { super(); }
}
