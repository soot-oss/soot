package soot.jimple.paddle.bdddomains;

import jedd.*;

public class FD extends PhysicalDomain {
    public int bits() { return 20; }
    
    public static PhysicalDomain v() { return instance; }
    
    private static PhysicalDomain instance = new FD();
    
    public FD() { super(); }
}
