/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
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

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.jimple.CaughtExceptionRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.ParameterRef;
import soot.jimple.ThisRef;
import soot.options.Options;
import soot.tagkit.AbstractHost;
import soot.tagkit.CodeAttribute;
import soot.tagkit.Tag;
import soot.toolkits.exceptions.PedanticThrowAnalysis;
import soot.toolkits.exceptions.ThrowAnalysis;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.InitAnalysis;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.toolkits.scalar.SmartLocalDefs;
import soot.util.Chain;
import soot.util.EscapedWriter;
import soot.util.HashChain;


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
public abstract class Body extends AbstractHost implements Serializable
{
    /** The method associated with this Body. */
    protected transient SootMethod method = null;

    /** The chain of locals for this Body. */
    protected Chain<Local> localChain = new HashChain<Local>();

    /** The chain of traps for this Body. */
    protected Chain<Trap> trapChain = new HashChain<Trap>();

    /** The chain of units for this Body. */
    protected PatchingChain<Unit> unitChain = new PatchingChain<Unit>(new HashChain<Unit>());

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
    public Map<Object, Object> importBodyContentsFrom(Body b)
    {
        HashMap<Object, Object> bindings = new HashMap<Object, Object>();

        {
	        Iterator<Unit> it = b.getUnits().iterator();
	
	        // Clone units in body's statement list
	        while(it.hasNext()) {
	            Unit original = it.next();
	            Unit copy = (Unit) original.clone();
	
	            copy.addAllTagsOf(original);
	
	            // Add cloned unit to our unitChain.
	            unitChain.addLast(copy);
	
	            // Build old <-> new map to be able to patch up references to other units
	            // within the cloned units. (these are still refering to the original
	            // unit objects).
	            bindings.put(original, copy);
	        }
        }

        {
	        // Clone trap units.
	        Iterator<Trap> it = b.getTraps().iterator();
	        while(it.hasNext()) {
	            Trap original = it.next();
	            Trap copy = (Trap) original.clone();
	
	            // Add cloned unit to our trap list.
	            trapChain.addLast(copy);
	
	            // Store old <-> new mapping.
	            bindings.put(original, copy);
	        }
        }

        {
	        // Clone local units.
	        Iterator<Local> it = b.getLocals().iterator();
	        while(it.hasNext()) {
	            Local original = it.next();
	            Local copy = (Local) original.clone();
	
	            // Add cloned unit to our trap list.
	            localChain.addLast(copy);
	
	            // Build old <-> new mapping.
	            bindings.put(original, copy);
	        }
        }

        {
	        // Patch up references within units using our (old <-> new) map.
	        Iterator<UnitBox> it = getAllUnitBoxes().iterator();
	        while(it.hasNext()) {
	            UnitBox box = it.next();
	            Unit newObject, oldObject = box.getUnit();
	
	            // if we have a reference to an old object, replace it
	            // it's clone.
	            if( (newObject = (Unit)  bindings.get(oldObject)) != null )
	                box.setUnit(newObject);
	
	        }
        }


        {
	        // backpatching all local variables.
	        Iterator<ValueBox> it = getUseBoxes().iterator();
	        while(it.hasNext()) {
	            ValueBox vb = it.next();
	            if(vb.getValue() instanceof Local)
	                vb.setValue((Value) bindings.get(vb.getValue()));
	        }
	        it = getDefBoxes().iterator();
	        while(it.hasNext()) {
	            ValueBox vb = it.next();
	            if(vb.getValue() instanceof Local)
	                vb.setValue((Value) bindings.get(vb.getValue()));
	        }
        }
        return bindings;
    }

    /** Verifies a few sanity conditions on the contents on this body. */
    public void validate()
    {
        //System.out.println("body: "+this.getUnits());
        validateLocals();
        validateTraps();
        validateUnitBoxes();
        if (Options.v().debug() || Options.v().validate()) {
            validateUses();
            validateValueBoxes();
            checkInit();
            checkTypes();
            checkLocals();
        }
    }

	/** Verifies that a ValueBox is not used in more than one place. */
    public void validateValueBoxes()
    {
        List<ValueBox> l = getUseAndDefBoxes();
        for( int i = 0; i < l.size(); i++ ) {
            for( int j = 0; j < l.size(); j++ ) {
                if( i == j ) continue;
                if( l.get(i) == l.get(j) ) {
                    System.err.println("Aliased value box : "+l.get(i)+" in "+getMethod());
                    for( Iterator<Unit> uIt = getUnits().iterator(); uIt.hasNext(); ) {
                        final Unit u = uIt.next();
                        System.err.println(""+u);
                    }
                    throw new RuntimeException("Aliased value box : "+l.get(i)+" in "+getMethod());
                }
            }
        }
    }

    /** Verifies that each Local of getUseAndDefBoxes() is in this body's locals Chain. */
    public void validateLocals()
    {
        Iterator<ValueBox> it = getUseBoxes().iterator();
        while(it.hasNext()){
            validateLocal( it.next() );
        }
        it = getDefBoxes().iterator();
        while(it.hasNext()){
            validateLocal( it.next() );
        }
    }
    private void validateLocal( ValueBox vb ) {
        Value value;
        if( (value = vb.getValue()) instanceof Local) {
            //System.out.println("localChain: "+localChain);
            if(!localChain.contains(value))
                throw new RuntimeException("Local not in chain : "+value+" in "+getMethod());
        }
    }

    /** Verifies that the begin, end and handler units of each trap are in this body. */
    public void validateTraps()
    {
        Iterator<Trap> it = getTraps().iterator();
        while (it.hasNext())
        {
            Trap t = it.next();
            if (!unitChain.contains(t.getBeginUnit()))
                throw new RuntimeException("begin not in chain"+" in "+getMethod());

            if (!unitChain.contains(t.getEndUnit()))
                throw new RuntimeException("end not in chain"+" in "+getMethod());

            if (!unitChain.contains(t.getHandlerUnit()))
                throw new RuntimeException("handler not in chain"+" in "+getMethod());
        }
    }

    /** Verifies that the UnitBoxes of this Body all point to a Unit contained within this body. */
    public void validateUnitBoxes()
    {
        Iterator<UnitBox> it = getAllUnitBoxes().iterator();
        while (it.hasNext())
        {
            UnitBox ub = it.next();
            if (!unitChain.contains(ub.getUnit()))
                throw new RuntimeException
                    ("Unitbox points outside unitChain! to unit : "+ub.getUnit()+" in "+getMethod());
        }
    }

    /** Verifies that each use in this Body has a def. */
    public void validateUses()
    {      
        // Conservative validation of uses: add edges to exception handlers 
        // even if they are not reachable.
        //
        // class C {
        //   int a;
        //   public void m() {
        //     try {
        //      a = 2;
        //     } catch (Exception e) {
        //      System.out.println("a: "+ a);
        //     }
        //   }
        // }
        //
        // In a graph generated from the Jimple representation there would 
        // be no edge from "a = 2;" to "catch..." because "a = 2" can only 
        // generate an Error, a subclass of Throwable and not of Exception. 
        // Use of 'a' in "System.out.println" would thus trigger a 'no defs 
        // for value' RuntimeException. 
        // To avoid this  we create an ExceptionalUnitGraph that considers all 
        // exception handlers (even unreachable ones as the one in the code 
        // snippet above) by using a PedanticThrowAnalysis and setting the 
        // parameter 'omitExceptingUnitEdges' to false.
        // 
        // Note that unreachable traps can be removed by setting jb.uce's 
        // "remove-unreachable-traps" option to true.
        ThrowAnalysis throwAnalysis = PedanticThrowAnalysis.v();
        UnitGraph g = new ExceptionalUnitGraph(this, throwAnalysis, false);
        
        LocalDefs ld = new SmartLocalDefs(g, new SimpleLiveLocals(g));

        Iterator<Unit> unitsIt = getUnits().iterator();
        while (unitsIt.hasNext())
        {
            Unit u = unitsIt.next();
            Iterator<ValueBox> useBoxIt = u.getUseBoxes().iterator();
            while (useBoxIt.hasNext())
            {
                Value v = (useBoxIt.next()).getValue();
                if (v instanceof Local)
                {
                    // This throws an exception if there is
                    // no def already; we check anyhow.
                    List<Unit> l = ld.getDefsOfAt((Local)v, u);
                    if (l.size() == 0){
                        for( Iterator<Unit> uuIt = getUnits().iterator(); uuIt.hasNext(); ) {
                            final Unit uu = uuIt.next();
                            System.err.println(""+uu);
                        }
                        throw new RuntimeException("("+ getMethod() +") no defs for value: " + v + "!\n" + this);
                    }
                }
            }
        }
    }

    /** Returns a backed chain of the locals declared in this Body. */
    public Chain<Local> getLocals() {return localChain;}

    /** Returns a backed view of the traps found in this Body. */
    public Chain<Trap> getTraps() {return trapChain;}

    /** Return LHS of the first identity stmt assigning from \@this. **/
    public Local getThisLocal()
    {
        Iterator<Unit> unitsIt = getUnits().iterator();

        while (unitsIt.hasNext())
        {
            Unit s = unitsIt.next();
            if (s instanceof IdentityStmt &&
                ((IdentityStmt)s).getRightOp() instanceof ThisRef)
                return (Local)(((IdentityStmt)s).getLeftOp());
        }

        throw new RuntimeException("couldn't find identityref!"+" in "+getMethod());
    }

    /** Return LHS of the first identity stmt assigning from \@parameter i. **/
    public Local getParameterLocal(int i)
    {
        Iterator<Unit> unitsIt = getUnits().iterator();
        while (unitsIt.hasNext())
        {
            Unit s = unitsIt.next();
            if (s instanceof IdentityStmt &&
                ((IdentityStmt)s).getRightOp() instanceof ParameterRef)
            {
                IdentityStmt is = (IdentityStmt)s;
                ParameterRef pr = (ParameterRef)is.getRightOp();
                if (pr.getIndex() == i)
                    return (Local)is.getLeftOp();
            }
        }

        throw new RuntimeException("couldn't find parameterref" + i +"! in "+getMethod());
    }

    /**
     * Get all the LHS of the identity statements assigning from parameter references.
     *
     * @return a list of size as per <code>getMethod().getParameterCount()</code> with all elements ordered as per the parameter index.
     * @throws RuntimeException if a parameterref is missing
     */
    public List<Local> getParameterLocals(){
        final int numParams = getMethod().getParameterCount();
        final List<Local> retVal = new ArrayList<Local>(numParams);

        //Parameters are zero-indexed, so the keeping of the index is safe
        for (Unit u : getUnits()){
            if (u instanceof IdentityStmt){
                IdentityStmt is = ((IdentityStmt)u);
                if (is.getRightOp() instanceof ParameterRef){
                    ParameterRef pr = (ParameterRef) is.getRightOp();
                    retVal.add(pr.getIndex(), (Local) is.getLeftOp());
                }
            }
        }
        if (retVal.size() != numParams)
            throw new RuntimeException("couldn't find parameterref! in " + getMethod());
        return retVal;
    }
    
    /**
     * Returns the list of parameter references used in this body. The list is as long as
     * the number of parameters declared in the associated method's signature.
     * The list may have <code>null</code> entries for parameters not referenced in the body.
     * The returned list is of fixed size.
     */
    public List<Value> getParameterRefs()
    {
    	Value[] res = new Value[getMethod().getParameterCount()];
        Iterator<Unit> unitsIt = getUnits().iterator();
        while (unitsIt.hasNext())
        {
            Unit s = unitsIt.next();
            if (s instanceof IdentityStmt) {
				Value rightOp = ((IdentityStmt)s).getRightOp();
				if (rightOp instanceof ParameterRef) {
					ParameterRef parameterRef = (ParameterRef) rightOp;
					res[parameterRef.getIndex()] = parameterRef;
				}
			}
        }
        return Arrays.asList(res);
    }


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
    public PatchingChain<Unit> getUnits()
    {
        return unitChain;
    }

    /**
     * Returns the result of iterating through all Units in this body
     * and querying them for their UnitBoxes.  All UnitBoxes thus
     * found are returned.  Branching Units and statements which use
     * PhiExpr will have UnitBoxes; a UnitBox contains a Unit that is
     * either a target of a branch or is being used as a pointer to
     * the end of a CFG block.
     *
     * <p> This method is typically used for pointer patching, eg when
     * the unit chain is cloned.
     *
     * @return A list of all the UnitBoxes held by this body's units.
     * @see UnitBox
     * @see #getUnitBoxes(boolean)
     * @see Unit#getUnitBoxes()
     * @see soot.shimple.PhiExpr#getUnitBoxes()
     **/
    public List<UnitBox> getAllUnitBoxes()
    {
        ArrayList<UnitBox> unitBoxList = new ArrayList<UnitBox>();
        {
            Iterator<Unit> it = unitChain.iterator();
            while(it.hasNext()) {
                Unit item = it.next();
                unitBoxList.addAll(item.getUnitBoxes());
            }
        }

        {
            Iterator<Trap> it = trapChain.iterator();
            while(it.hasNext()) {
                Trap item = it.next();
                unitBoxList.addAll(item.getUnitBoxes());
            }
        }

        {
            Iterator<Tag> it = getTags().iterator();
            while(it.hasNext()) {
                Tag t = it.next();
                if( t instanceof CodeAttribute)
                    unitBoxList.addAll(((CodeAttribute) t).getUnitBoxes());
            }
        }

        return unitBoxList;
    }

    /**
     * If branchTarget is true, returns the result of iterating
     * through all branching Units in this body and querying them for
     * their UnitBoxes. These UnitBoxes contain Units that are the
     * target of a branch.  This is useful for, say, labeling blocks
     * or updating the targets of branching statements.
     *
     * <p> If branchTarget is false, returns the result of iterating
     * through the non-branching Units in this body and querying them
     * for their UnitBoxes.  Any such UnitBoxes (typically from
     * PhiExpr) contain a Unit that indicates the end of a CFG block.
     *
     * @return a list of all the UnitBoxes held by this body's
     * branching units.
     *
     * @see UnitBox
     * @see #getAllUnitBoxes()
     * @see Unit#getUnitBoxes()
     * @see soot.shimple.PhiExpr#getUnitBoxes()
     **/
    public List<UnitBox> getUnitBoxes(boolean branchTarget)
    {
        ArrayList<UnitBox> unitBoxList = new ArrayList<UnitBox>();
        {
            Iterator<Unit> it = unitChain.iterator();
            while(it.hasNext()) {
                Unit item = it.next();
                if(branchTarget){
                    if(item.branches())
                        unitBoxList.addAll(item.getUnitBoxes());
                }
                else{
                    if(!item.branches())
                        unitBoxList.addAll(item.getUnitBoxes());
                }
            }
        }

        {
            Iterator<Trap> it = trapChain.iterator();
            while(it.hasNext()) {
                Trap item = it.next();
                unitBoxList.addAll(item.getUnitBoxes());
            }
        }

        {
            Iterator<Tag> it = getTags().iterator();
            while(it.hasNext()) {
                Tag t = it.next();
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
    public List<ValueBox> getUseBoxes()
    {
        ArrayList<ValueBox> useBoxList = new ArrayList<ValueBox>();

        Iterator<Unit> it = unitChain.iterator();
        while(it.hasNext()) {
            Unit item = it.next();
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
    public List<ValueBox> getDefBoxes()
    {
        ArrayList<ValueBox> defBoxList = new ArrayList<ValueBox>();

        Iterator<Unit> it = unitChain.iterator();
        while(it.hasNext()) {
            Unit item = it.next();
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
    public List<ValueBox> getUseAndDefBoxes()
    {
        ArrayList<ValueBox> useAndDefBoxList = new ArrayList<ValueBox>();

        Iterator<Unit> it = unitChain.iterator();
        while(it.hasNext()) {
            Unit item = it.next();
            useAndDefBoxList.addAll(item.getUseBoxes());
            useAndDefBoxList.addAll(item.getDefBoxes());
        }
        return useAndDefBoxList;
    }

    private void checkLocals() {
	Chain<Local> locals=getLocals();

	Iterator<Local> it=locals.iterator();
	while(it.hasNext()) {
	    Local l=it.next();
	    if(l.getType() instanceof VoidType) 
		throw new RuntimeException("Local "+l+" in "+method+" defined with void type");
	}
    }

    private void checkTypes() {
	Chain<Unit> units=getUnits();

	Iterator<Unit> it=units.iterator();
	while(it.hasNext()) {
	    Unit stmt=(it.next());
	    InvokeExpr iexpr=null;

	    String errorSuffix=" at "+stmt+" in "+getMethod();

	    if(stmt instanceof DefinitionStmt) {
		DefinitionStmt astmt=(DefinitionStmt) stmt;
                if( !(astmt.getRightOp() instanceof CaughtExceptionRef ) ) {
                    Type leftType=Type.toMachineType(astmt.getLeftOp().getType());
                    Type rightType=Type.toMachineType(astmt.getRightOp().getType());

                    checkCopy(leftType,rightType,errorSuffix);
                    if(astmt.getRightOp() instanceof InvokeExpr) 
                        iexpr=(InvokeExpr) (astmt.getRightOp());
                }
	    }

	    if(stmt instanceof InvokeStmt) iexpr=((InvokeStmt) stmt).getInvokeExpr();

	    if(iexpr!=null) {
		SootMethodRef called=iexpr.getMethodRef();

		if(iexpr instanceof InstanceInvokeExpr) {
		    InstanceInvokeExpr iiexpr=(InstanceInvokeExpr) iexpr;
		    checkCopy(called.declaringClass().getType(),
			      iiexpr.getBase().getType(),
			      " in receiver of call"+errorSuffix);
		}

		if(called.parameterTypes().size() != iexpr.getArgCount())
		    throw new RuntimeException("Warning: Argument count doesn't match up with signature in call"+errorSuffix+" in "+getMethod());
		else 
		    for(int i=0;i<iexpr.getArgCount();i++)
			checkCopy(Type.toMachineType(called.parameterType(i)),
				  Type.toMachineType(iexpr.getArg(i).getType()),
				  " in argument "+i+" of call"+errorSuffix);
	    }
	}
    }

    private void checkCopy(Type leftType,Type rightType,String errorSuffix) {
	if(leftType instanceof PrimType || rightType instanceof PrimType) {
	    if(leftType instanceof IntType && rightType instanceof IntType) return;
	    if(leftType instanceof LongType && rightType instanceof LongType) return;
	    if(leftType instanceof FloatType && rightType instanceof FloatType) return;
	    if(leftType instanceof DoubleType && rightType instanceof DoubleType) return;
	    throw new RuntimeException("Warning: Bad use of primitive type"+errorSuffix+" in "+getMethod());
	}

	if(rightType instanceof NullType) return;
	if(leftType instanceof RefType &&
	   ((RefType) leftType).getClassName().equals("java.lang.Object")) return;
	
	if(leftType instanceof ArrayType || rightType instanceof ArrayType) {
	    if(leftType instanceof ArrayType && rightType instanceof ArrayType) return;
	    //it is legal to assign arrays to variables of type Serializable, Cloneable or Object
	    if(rightType instanceof ArrayType) {
	    	if(leftType.equals(RefType.v("java.io.Serializable")) ||
	    			leftType.equals(RefType.v("java.lang.Cloneable")) ||
	    			leftType.equals(RefType.v("java.lang.Object")))
	    		return;
	    }

	    throw new RuntimeException("Warning: Bad use of array type"+errorSuffix+" in "+getMethod());
	}

	if(leftType instanceof RefType && rightType instanceof RefType) {
	    SootClass leftClass=((RefType) leftType).getSootClass();
	    SootClass rightClass=((RefType) rightType).getSootClass();
	    if(leftClass.isPhantom() || rightClass.isPhantom()) {
	    	return;
	    }
	    
	    if(leftClass.isInterface()) {
		if(rightClass.isInterface()) {
		    if(!(leftClass.getName().equals(rightClass.getName()) || 
			 Scene.v().getActiveHierarchy().isInterfaceSubinterfaceOf(rightClass,leftClass)))
			throw new RuntimeException("Warning: Bad use of interface type"+errorSuffix+" in "+getMethod());
		} else {
		    // No quick way to check this for now.
		}
	    } else {
		if(rightClass.isInterface()) {
		    throw new RuntimeException("Warning: trying to use interface type where non-Object class expected"
				       +errorSuffix+" in "+getMethod());
		} else {
		    if(!Scene.v().getActiveHierarchy().isClassSubclassOfIncluding(rightClass,leftClass))
			throw new RuntimeException("Warning: Bad use of class type"+errorSuffix+" in "+getMethod());
		}
	    }
	    return;
	}
	throw new RuntimeException("Warning: Bad types"+errorSuffix+" in "+getMethod());
    }

    @SuppressWarnings("unchecked")
	public void checkInit() {
        ExceptionalUnitGraph g = new ExceptionalUnitGraph
	    (this, PedanticThrowAnalysis.v(), false);

		InitAnalysis analysis=new InitAnalysis(g);
		for (Unit s : getUnits()) {
			FlowSet init=(FlowSet) analysis.getFlowBefore(s);
		    for (ValueBox vBox : s.getUseBoxes()) {
				Value v=vBox.getValue();
				if(v instanceof Local) {
				    Local l=(Local) v;
				    if(!init.contains(l))
						throw new RuntimeException("Warning: Local variable "+l
								   +" not definitely defined at "+s
								   +" in "+method);
				}
		    }
		}
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
        PrintWriter writerOut = new PrintWriter(new EscapedWriter(new OutputStreamWriter(streamOut)));
        try { 
            Printer.v().printTo(this, writerOut);
        } catch (RuntimeException e) {
            e.printStackTrace(writerOut);
        }
        writerOut.flush();
        writerOut.close();
        return streamOut.toString();
    }
}








