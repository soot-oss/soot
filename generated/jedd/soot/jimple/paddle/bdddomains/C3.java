package soot.jimple.paddle.bdddomains;

import jedd.*;

public class C3 extends PhysicalDomain {
    public int bits() { return 61; }
    
    public static PhysicalDomain v() { return instance; }
    
    private static PhysicalDomain instance = new C3();
    
    public C3() { super(); }
}
