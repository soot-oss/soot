package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class obj extends Attribute {
    public final ObjDomain domain = (ObjDomain) ObjDomain.v();
    
    public Domain domain() { return domain; }
    
    public static Attribute v() { return instance; }
    
    private static Attribute instance = new obj();
    
    public obj() { super(); }
}
