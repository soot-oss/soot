/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Jerome Miecznikowski
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.dava;

import soot.*;
import java.util.*;
import soot.grimp.*;
import soot.dava.internal.*;
import soot.dava.toolkits.base.*;
import soot.jimple.*;
import soot.jimple.internal.*;

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
            
    
        // Import body contents from Grimp.
        {        
            HashMap bindings = new HashMap();
    
            Iterator it = grimpBody.getUnits().iterator();
    
            // Clone units in body's statement list 
            while(it.hasNext()) {
                Unit original = (Unit) it.next();
                

                if(original instanceof IdentityStmt)
                    System.out.println("found identity stmt: " + original);
                else
                    System.out.println("not found identity stmt: " + original);
                


                
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
    
        // Call transformers to recover structure
        {
            //BlockStructurer.v().transform(this, "db.bs");
            //IfThenElseMatcher.v().transform(this, "db.item");
        }
    }
}




