package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class signature extends Attribute {
    public final SIG domain = (SIG) SIG.v();
    
    public Domain domain() { return domain; }
    
    public static Attribute v() { return instance; }
    
    private static Attribute instance = new signature();
    
    public signature() { super(); }
}
