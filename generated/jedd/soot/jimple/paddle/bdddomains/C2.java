package soot.jimple.paddle.bdddomains;

import jedd.*;

public class C2 extends PhysicalDomain {
    public int bits() { return 61; }
    
    public static PhysicalDomain v() { return instance; }
    
    private static PhysicalDomain instance = new C2();
    
    public C2() { super(); }
}
