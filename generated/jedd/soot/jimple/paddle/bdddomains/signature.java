package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class signature extends Attribute {
    public final SigDomain domain = (SigDomain) SigDomain.v();
    
    public Domain domain() { return domain; }
    
    public static Attribute v() { return instance; }
    
    private static Attribute instance = new signature();
    
    public signature() { super(); }
}
