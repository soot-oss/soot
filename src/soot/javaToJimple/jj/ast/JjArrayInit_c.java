package soot.javaToJimple.jj.ast;

import polyglot.ast.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.util.*;
import polyglot.ext.jl.ast.*;
import java.util.*;

public class JjArrayInit_c extends ArrayInit_c {
    
    public JjArrayInit_c(Position pos, List elements) {
        super(pos, elements);
    }

    public Type childExpectedType(Expr child, AscriptionVisitor av){
        if (elements.isEmpty()) {
            return child.type();
        }

        Type t = av.toType();

        //System.out.println("t type: "+t);
        if (t == null) {
            //System.out.println("t is null");
            return child.type();
        }
        if (! t.isArray()) {
            throw new InternalCompilerError("Type of array initializer must be " +
                                        "an array.", position());
        }

        t = t.toArray().base();

	for (Iterator i = elements.iterator(); i.hasNext(); ) {
	    Expr e = (Expr) i.next();

            if (e == child) {
                return t;
            }
        }

       return child.type(); 
    }
}
