package soot.jimple.paddle.bdddomains;

import jedd.*;

public class KD extends PhysicalDomain {
    public int bits() { return 4; }
    
    public static PhysicalDomain v() { return instance; }
    
    private static PhysicalDomain instance = new KD();
    
    public KD() { super(); }
}
