package soot.jimple.paddle.bdddomains;

import jedd.*;

public class T2 extends PhysicalDomain {
    public int bits() { return 20; }
    
    public static PhysicalDomain v() { return instance; }
    
    private static PhysicalDomain instance = new T2();
    
    public T2() { super(); }
}
