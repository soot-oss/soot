package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class local extends Attribute {
    public final LocalDomain domain = (LocalDomain) LocalDomain.v();
    
    public Domain domain() { return domain; }
    
    public static Attribute v() { return instance; }
    
    private static Attribute instance = new local();
    
    public local() { super(); }
}
