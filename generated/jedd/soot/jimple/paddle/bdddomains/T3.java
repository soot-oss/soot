package soot.jimple.paddle.bdddomains;

import jedd.*;

public class T3 extends PhysicalDomain {
    public int bits() { return 12; }
    
    public static PhysicalDomain v() { return instance; }
    
    private static PhysicalDomain instance = new T3();
    
    public T3() { super(); }
}
