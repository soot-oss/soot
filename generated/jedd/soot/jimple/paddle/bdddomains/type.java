package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class type extends Attribute {
    public final TYPE domain = (TYPE) TYPE.v();
    
    public Domain domain() { return domain; }
    
    public static Attribute v() { return instance; }
    
    private static Attribute instance = new type();
    
    public type() { super(); }
}
