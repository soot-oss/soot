package soot.dava;

import soot.*;
import java.util.*;
import soot.grimp.*;
import soot.dava.internal.*;

public class DavaBody extends Body
{
     /**
        Construct an empty DavaBody 
     **/
     
    DavaBody(SootMethod m)
    {
        super(m);
    }

    public Object clone()
    {
        Body b = Dava.v().newBody(getMethod());
        b.importBodyContentsFrom(this);
        return b;
    }

    /**
        Constructs a DavaBody from the given Body.
     */

    DavaBody(Body body, Map options)
    {
        super(body.getMethod());

        if(!(body instanceof GrimpBody))
            throw new RuntimeException("can only create a DavaBody from a GrimpBody!");
        
        GrimpBody grimpBody = (GrimpBody) body;
            
        // Convert all locals
        {
            Iterator localIt = grimpBody.getLocals().iterator();
            
            while(localIt.hasNext())
            {
                Local l = (Local) localIt.next();
                Local newLocal;
                
                newLocal = Grimp.v().newLocal(l.getName(), l.getType());
                getLocals().add(newLocal);
            }
        }

        // Import body contents from Grimp.
        {        
            HashMap bindings = new HashMap();
    
            Iterator it = grimpBody.getUnits().iterator();
    
            // Clone units in body's statement list 
            while(it.hasNext()) {
                Unit original = (Unit) it.next();
                                
                Unit copy = (Unit) original.clone();
                
                // Add cloned unit to our unitChain.
                getUnits().addLast(copy);
    
                // Build old <-> new map to be able to patch up references to other units 
                // within the cloned units. (these are still refering to the original
                // unit objects).
                bindings.put(original, copy);
            }
    
            // Clone locals.
            it = grimpBody.getLocals().iterator();
            while(it.hasNext()) {
                Local original = (Local) it.next();
                Value copy = Dava.v().newLocal(original.getName(), original.getType());
                
                getLocals().addLast(copy);
    
                // Build old <-> new mapping.
                bindings.put(original, copy);
            }
            
    
            // Patch up references within units using our (old <-> new) map.
            it = getUnitBoxes().iterator();
            while(it.hasNext()) {
                UnitBox box = (UnitBox) it.next();
                Unit newObject, oldObject = box.getUnit();
                
                // if we have a reference to an old object, replace it 
                // it's clone.
                if( (newObject = (Unit)  bindings.get(oldObject)) != null )
                    box.setUnit(newObject);
                    
            }        
    
            // backpatch all local variables.
            it = getUseAndDefBoxes().iterator();
            while(it.hasNext()) {
                ValueBox vb = (ValueBox) it.next();
                if(vb.getValue() instanceof Local) 
                    vb.setValue((Value) bindings.get(vb.getValue()));
            }
        }    
    
    }
}



