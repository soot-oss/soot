/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

package soot;

import soot.tagkit.*;
import soot.baf.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.*;
import soot.util.*;
import java.util.*;
import java.io.*;
import soot.toolkits.scalar.*;


/**
 *   Abstract base class that models the body (code attribute) of a Java method.
 *   Classes that implement an Intermediate Representation for a method body should subclass it.
 *   In particular the classes GrimpBody, JimpleBody and BafBody all extend this
 *   class. This class provides methods that are common to any IR, such as methods
 *   to get the body's units (statements), traps, and locals.
 *   
 *  @see soot.grimp.GrimpBody
 *  @see soot.jimple.JimpleBody
 *  @see soot.baf.BafBody
 */
public abstract class Body extends AbstractHost
{
    /** The method associated with this Body. */
    protected SootMethod method = null;

    /** The chain of locals for this Body. */
    protected Chain localChain = new HashChain();

    /** The chain of traps for this Body. */
    protected Chain trapChain = new HashChain();

    /** The chain of units for this Body. */
    protected PatchingChain unitChain = new PatchingChain(new HashChain());

    /** Creates a deep copy of this Body. */
    abstract public Object clone();

    /** Creates a Body associated to the given method.  Used by subclasses during initialization. 
     *  Creation of a Body is triggered by e.g. Jimple.v().newBody(options).
     */
    protected Body(SootMethod m) 
    {       
        this.method = m;
    }

    /** Creates an extremely empty Body.  The Body is not associated to any method. */
    protected Body() 
    {               
    }

    /** 
     * Returns the method associated with this Body. 
     * @return the method that owns this body.
     */
    public SootMethod getMethod()
    {
        if(method == null)
            throw new RuntimeException("no method associated w/ body");
        return method;
    }


    /** 
     * Sets the method associated with this Body. 
     * @param method the method that owns this body.
     * 
     */    
    public void setMethod(SootMethod method)
    {
        this.method = method;
    }
    
    /** Returns the number of locals declared in this body. */
    public int getLocalCount()
    {
        return localChain.size();
    }

    /** Copies the contents of the given Body into this one. */
    public void importBodyContentsFrom(Body b)
    {
        HashMap bindings = new HashMap();

        Iterator it = b.getUnits().iterator();

        // Clone units in body's statement list 
        while(it.hasNext()) {
            Unit original = (Unit) it.next();
            Unit copy = (Unit) original.clone();
             
            // Add cloned unit to our unitChain.
            unitChain.addLast(copy);

            // Build old <-> new map to be able to patch up references to other units 
            // within the cloned units. (these are still refering to the original
            // unit objects).
            bindings.put(original, copy);
        }

        // Clone trap units.
        it = b.getTraps().iterator();
        while(it.hasNext()) {
            Trap original = (Trap) it.next();
            Trap copy = (Trap) original.clone();
            
            // Add cloned unit to our trap list.
            trapChain.addLast(copy);

            // Store old <-> new mapping.
            bindings.put(original, copy);
        }

        
        // Clone local units.
        it = b.getLocals().iterator();
        while(it.hasNext()) {
            Value original = (Value) it.next();
            Value copy = (Value) original.clone();
            
            // Add cloned unit to our trap list.
            localChain.addLast(copy);

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



        // backpatching all local variables.
        it = getUseAndDefBoxes().iterator();
        while(it.hasNext()) {
            ValueBox vb = (ValueBox) it.next();
            if(vb.getValue() instanceof Local) 
                vb.setValue((Value) bindings.get(vb.getValue()));
        }
    }
    
    /** Verifies a few sanity conditions on the contents on this body. */
    public void validate()
    {
        validateLocals();
        validateTraps();
        validateUnitBoxes();
        if (Main.isInDebugMode)
            validateUses();
    }

    /** Verifies that each Local of getUseAndDefBoxes() is in this body's locals Chain. */
    public void validateLocals()
    {
        Iterator it = getUseAndDefBoxes().iterator();
        
        while(it.hasNext()){
            ValueBox vb = (ValueBox) it.next();
            Value value;
            if( (value = vb.getValue()) instanceof Local) {
                if(!localChain.contains(value))
                    throw new RuntimeException("Local not in chain : "+value);                
            }
        }
    }

    /** Verifies that the begin, end and handler units of each trap are in this body. */
    public void validateTraps()
    {
        Iterator it = getTraps().iterator();
        while (it.hasNext())
        {
            Trap t = (Trap)it.next();
            if (!unitChain.contains(t.getBeginUnit()))
                throw new RuntimeException("begin not in chain");

            if (!unitChain.contains(t.getEndUnit()))
                throw new RuntimeException("end not in chain");

            if (!unitChain.contains(t.getHandlerUnit()))
                throw new RuntimeException("handler not in chain");
        }
    }

    /** Verifies that the UnitBoxes of this Body all point to a Unit contained within this body. */
    public void validateUnitBoxes()
    {
        Iterator it = getUnitBoxes().iterator();
        while (it.hasNext())
        {
            UnitBox ub = (UnitBox)it.next();
            if (!unitChain.contains(ub.getUnit()))
                throw new RuntimeException
                    ("Unitbox points outside unitChain! to unit : "+ub.getUnit());
        }
    }

    /** Verifies that each use in this Body has a def. */
    public void validateUses()
    {
        LocalDefs ld = new SimpleLocalDefs(new CompleteUnitGraph(this));

        Iterator unitsIt = getUnits().iterator();
        while (unitsIt.hasNext())
        {
            Unit u = (Unit) unitsIt.next();
            Iterator useBoxIt = u.getUseBoxes().iterator();
            while (useBoxIt.hasNext())
            {
                Value v = ((ValueBox)useBoxIt.next()).getValue();
                if (v instanceof Local)
                {
                    // This throws an exception if there is
                    // no def already; we check anyhow.
                    List l = ld.getDefsOfAt((Local)v, u);
                    if (l.size() == 0)
                        throw new RuntimeException("no defs for value!");
                }
            }
        }
    }

    /** Returns a backed chain of the locals declared in this Body. */
    public Chain getLocals() {return localChain;} 

    /** Returns a backed view of the traps found in this Body. */
    public Chain getTraps() {return trapChain;}


    /**
     *  Returns the Chain of Units that make up this body. The units are
     *  returned as a PatchingChain. The client can then manipulate the chain,
     *  adding and removing units, and the changes will be reflected in the body.  
     *  Since a PatchingChain is returned the client need <i>not</i> worry about removing exception
     *  boundary units or otherwise corrupting the chain.
     * 
     *  @return the units in this Body 
     *
     *  @see PatchingChain
     *  @see Unit
     */
    public PatchingChain getUnits() 
    {
        return unitChain;
    }

    /**
     *   Returns the result of iterating through all Units in this
     *   body and querying them for their UnitBoxes.  All 
     *   UnitBoxes thus found are returned. Only branching Units will
     *   have UnitBoxes; a UnitBox contains a Unit that is a target of
     *   a branch.
     *
     *   @return a list of all the UnitBoxes held by this body's units.
     *     
     *   @see UnitBox
     *   @see Unit#getUnitBoxes
     * */
    public List getUnitBoxes() 
    {
        ArrayList unitBoxList = new ArrayList();
        {
	    Iterator it = unitChain.iterator();
	    while(it.hasNext()) {
		Unit item = (Unit) it.next();
		unitBoxList.addAll(item.getUnitBoxes());  
	    }
	}

        
	{
	    Iterator it = trapChain.iterator();
	    while(it.hasNext()) {
		Trap item = (Trap) it.next();
		unitBoxList.addAll(item.getUnitBoxes());  
	    }
        }


	{
	    Iterator it = getTags().iterator();
	    while(it.hasNext()) {
		Tag t = (Tag) it.next();
		if( t instanceof CodeAttribute) 		    
		    unitBoxList.addAll(((CodeAttribute) t).getUnitBoxes());
	    }
	}
	
        return unitBoxList;
    }


    /**
     *   Returns the result of iterating through all Units in this
     *   body and querying them for ValueBoxes used. 
     *   All of the ValueBoxes found are then returned as a List.
     *
     *   @return a list of all the ValueBoxes for the Values used this body's units.
     *     
     *   @see Value
     *   @see Unit#getUseBoxes
     *   @see ValueBox
     *   @see Value
     *
     */   
    public List getUseBoxes()
    {
        ArrayList useBoxList = new ArrayList();
        
        Iterator it = unitChain.iterator();
        while(it.hasNext()) {
            Unit item = (Unit) it.next();
            useBoxList.addAll(item.getUseBoxes());  
        }
        return useBoxList;
    }


    /**
     *   Returns the result of iterating through all Units in this
     *   body and querying them for ValueBoxes defined.
     *   All of the ValueBoxes found are then returned as a List.
     *
     *   @return a list of all the ValueBoxes for Values defined by this body's units.
     *     
     *   @see Value
     *   @see Unit#getDefBoxes
     *   @see ValueBox
     *   @see Value
     */   
    public List getDefBoxes()
    {
        ArrayList defBoxList = new ArrayList();
        
        Iterator it = unitChain.iterator();
        while(it.hasNext()) {
            Unit item = (Unit) it.next();
            defBoxList.addAll(item.getDefBoxes());  
        }
        return defBoxList;
    }

     /**
     *   Returns a list of boxes corresponding to Values 
     * either used or defined in any unit of this Body.
     *
     *   @return a list of ValueBoxes for held by the body's Units.
     *     
     *   @see Value
     *   @see Unit#getUseAndDefBoxes
     *   @see ValueBox
     *   @see Value
     */       
    public List getUseAndDefBoxes()
    {        
        ArrayList useAndDefBoxList = new ArrayList();
        
        Iterator it = unitChain.iterator();
        while(it.hasNext()) {
            Unit item = (Unit) it.next();
            useAndDefBoxList.addAll(item.getUseAndDefBoxes());  
        }
        return useAndDefBoxList;
    }


    /**
     *   Prints out the method corresponding to this Body, (declaration and body),
     *   in the textual format corresponding to the IR used to encode this body. Default
     *   printBodyOptions are used.
     *
     *   @param out a PrintWriter instance to print to. 
     *
     */
    public void printTo(java.io.PrintWriter out)
    {
        printTo(out, 0);
    }
    

    /**
     *   Prints out the method corresponding to this Body, (declaration and body),
     *   in the textual format corresponding to the IR used to encode this body.
     *
     *   @param out a PrintWriter instance to print to.
     *   @param printBodyOptions options for printing.
     *
     *   @see PrintJimpleBodyOption
     */   
    public void printTo(PrintWriter out, int printBodyOptions)
    {
        printToImpl(out, printBodyOptions, false);
    }

    
    /**
     *   Prints out the method corresponding to this Body, (declaration and body),
     *   in the textual format corresponding to the IR used to encode this body. Includes
     *   extra debugging information.
     *
     *   @param out a PrintWriter instance to print to.
     *   @param printBodyOptions options for printing.
     *
     *   @see PrintJimpleBodyOption
     */
    public void printDebugTo(PrintWriter out, int printBodyOptions)
    {
        printToImpl(out, printBodyOptions, true);
    }        

    

    private void printToImpl(PrintWriter out, int printBodyOptions, boolean debug)
    {
        validate();

        boolean isPrecise = !PrintJimpleBodyOption.useAbbreviations(printBodyOptions);
        boolean isNumbered = PrintJimpleBodyOption.numbered(printBodyOptions);
        Map stmtToName = new HashMap(unitChain.size() * 2 + 1, 0.7f);
        String decl = getMethod().getDeclaration();

        out.println("    " + decl);        
        out.println("    {");

        // Print out local variables
        {
            Map typeToLocals = new DeterministicHashMap(this.getLocalCount() * 2 + 1, 0.7f);

            // Collect locals
            {
                Iterator localIt = this.getLocals().iterator();

                while(localIt.hasNext())
                {
                    Local local = (Local) localIt.next();

                    List localList;
 
                    String typeName;
                    Type t = local.getType();

                    typeName = (isPrecise) ?  t.toString() :  t.toBriefString();

                    if(typeToLocals.containsKey(typeName))
                        localList = (List) typeToLocals.get(typeName);
                    else
                    {
                        localList = new ArrayList();
                        typeToLocals.put(typeName, localList);
                    }

                    localList.add(local);
                }
            }

            // Print locals
            {
                Iterator typeIt = typeToLocals.keySet().iterator();

                while(typeIt.hasNext())
                {
                    String type = (String) typeIt.next();

                    List localList = (List) typeToLocals.get(type);
                    Object[] locals = localList.toArray();
                    out.print("        "  + type + " ");
                    
                    for(int k = 0; k < locals.length; k++)
                    {
                        if(k != 0)
                            out.print(", ");

                        out.print(((Local) locals[k]).getName());
                    }

                    out.println(";");
                }
            }


            if(!typeToLocals.isEmpty())
                out.println();
        }

        // Print out statements
        // Use an external class so that it can be overridden.
        if(debug) {
            Scene.v().getJimpleStmtPrinter().printDebugStatementsInBody(this, out, isPrecise);
        } else {
            Scene.v().getJimpleStmtPrinter().printStatementsInBody(this, out, isPrecise, isNumbered);
        }
        
        out.println("    }");
    }
    
}








