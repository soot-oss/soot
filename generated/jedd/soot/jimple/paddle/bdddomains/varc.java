package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class varc extends Attribute {
    public final ContextDomain domain = (ContextDomain) ContextDomain.v();
    
    public Domain domain() { return domain; }
    
    public static Attribute v() { return instance; }
    
    private static Attribute instance = new varc();
    
    public varc() { super(); }
}
