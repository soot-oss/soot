package soot.jimple.paddle.bdddomains;

import jedd.*;

public class V2 extends PhysicalDomain {
    public int bits() { return 20; }
    
    public static PhysicalDomain v() { return instance; }
    
    private static PhysicalDomain instance = new V2();
    
    public V2() { super(); }
}
