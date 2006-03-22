package soot.dava.toolkits.base.AST.structuredAnalysis;

import soot.Local;
import soot.PrimType;
import soot.SootField;
import soot.dava.DavaFlowAnalysisException;

/*
 * Needed since we want to track locals and SootFields (not FieldRefs)
 */
public class CPVariable{
	
	private Local local;
	private SootField field;

	
	public CPVariable(SootField field){
		this.field=field;
		this.local=null;
		
		if(! (field.getType() instanceof PrimType))
			throw new DavaFlowAnalysisException("Variables managed for CP should only be primitives");
	}

	
	public CPVariable(Local local){
		this.field=null;
		this.local=local;
		
		if(! (local.getType() instanceof PrimType))
			throw new DavaFlowAnalysisException("Variables managed for CP should only be primitives");

	}
	
	public boolean containsLocal(){
		return (local != null);
	}
	
	public boolean containsSootField(){
		return (field != null);
	}
	
	public SootField getSootField(){
		if(containsSootField())
			return field;
		else
			throw new DavaFlowAnalysisException("getsootField invoked when variable is not a sootfield!!!");		
	}
	
	public Local getLocal(){
		if(containsLocal())
			return local;
		else
			throw new DavaFlowAnalysisException("getLocal invoked when variable is not a local");
	}

	/*
	 * VERY IMPORTANT METHOD: invoked from ConstantPropagationTuple equals method which is invoked from
	 * the main merge intersection method of CPFlowSet
	 */
	public boolean equals(CPVariable var){
		//check they have the same type Local or SootField
		if( this.containsLocal() && var.containsLocal()){
			//both locals and same name
			if(this.getLocal().getName().equals(var.getLocal().getName())){
				return true;
			}
		}
		if(this.containsSootField() && var.containsSootField()){
			//both SootFields check they have same name
			if(this.getSootField().getName().equals(var.getSootField().getName()) ){
				return true;
			}
		}
		
		return false;
	}
	
	public String toString(){
		if(containsLocal())
			return "Local: "+getLocal().getName();
		else if(containsSootField())
			return "SootField: " + getSootField().getName();
		else
			return "UNKNOWN CONSTANT_PROPAGATION_VARIABLE";
	}
}


