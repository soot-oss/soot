package soot.jimple.paddle.bdddomains;

import jedd.*;

public class V1 extends PhysicalDomain {
    public int bits() { return 30; }
    
    public static PhysicalDomain v() { return instance; }
    
    private static PhysicalDomain instance = new V1();
    
    public V1() { super(); }
}
