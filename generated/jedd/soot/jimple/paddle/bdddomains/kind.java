package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class kind extends Attribute {
    public final KindDomain domain = (KindDomain) KindDomain.v();
    
    public Domain domain() { return domain; }
    
    public static Attribute v() { return instance; }
    
    private static Attribute instance = new kind();
    
    public kind() { super(); }
}
