package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class var extends Attribute {
    public final VarDomain domain = (VarDomain) VarDomain.v();
    
    public Domain domain() { return domain; }
    
    public static Attribute v() { return instance; }
    
    private static Attribute instance = new var();
    
    public var() { super(); }
}
