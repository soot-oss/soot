package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class stmt extends Attribute {
    public final STMT domain = (STMT) STMT.v();
    
    public Domain domain() { return domain; }
    
    public static Attribute v() { return instance; }
    
    private static Attribute instance = new stmt();
    
    public stmt() { super(); }
}
