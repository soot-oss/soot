package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class objc extends Attribute {
    public final ContextDomain domain = (ContextDomain) ContextDomain.v();
    
    public Domain domain() { return domain; }
    
    public static Attribute v() { return instance; }
    
    private static Attribute instance = new objc();
    
    public objc() { super(); }
}
