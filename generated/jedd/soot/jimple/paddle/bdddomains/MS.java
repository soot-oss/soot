package soot.jimple.paddle.bdddomains;

import jedd.*;

public class MS extends PhysicalDomain {
    public int bits() { return 17; }
    
    public static PhysicalDomain v() { return instance; }
    
    private static PhysicalDomain instance = new MS();
    
    public MS() { super(); }
}
