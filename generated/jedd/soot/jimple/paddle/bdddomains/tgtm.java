package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class tgtm extends Attribute {
    public final MethodDomain domain = (MethodDomain) MethodDomain.v();
    
    public Domain domain() { return domain; }
    
    public static Attribute v() { return instance; }
    
    private static Attribute instance = new tgtm();
    
    public tgtm() { super(); }
}
