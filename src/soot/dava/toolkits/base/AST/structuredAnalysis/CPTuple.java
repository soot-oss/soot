package soot.dava.toolkits.base.AST.structuredAnalysis;

import soot.dava.DavaFlowAnalysisException;
import soot.dava.toolkits.base.AST.structuredAnalysis.CPVariable;

/********** START LOCAL CLASS DECLARATION *******************/
public class CPTuple{
	private String sootClass; //hold the name of the class to which the val belongs .... needed for interprocedural constant Fields info

	/*
	 * 
	 */
	private CPVariable variable;
	
	//		Double Float Long Boolean Integer
	private Object constant; //the known constant value for the local or field
	
	/*
	 * false means not top
	 * true mean TOP
	 */
	private Boolean TOP = new Boolean(false);
	
	/*
	 * Dont care about className and variable but the CONSTANT VALUE HAS TO BE A NEW ONE
	 * otherwise the clone of the flowset keeps pointing to the same bloody constant value
	 */
	public Object clone(){			
		if(isTop()){
			return new CPTuple(sootClass,variable, true);
		}
		else if (isValueADouble()){
			return new CPTuple(sootClass,variable, new Double(   ((Double)constant).doubleValue() ) );
		}
		else if(isValueAFloat()){
			return new CPTuple(sootClass,variable, new Float(   ((Float)constant).floatValue() ) );
		}
		else if(isValueALong()){
			return new CPTuple(sootClass,variable, new Long(   ((Long)constant).longValue() ) );
		}
		else if(isValueABoolean()){
			return new CPTuple(sootClass,variable, new Boolean(   ((Boolean)constant).booleanValue() ) );
		}
		else if (isValueAInteger()){
			return new CPTuple(sootClass,variable, new Integer(   ((Integer)constant).intValue() ) );
		}
		else
			throw new RuntimeException("illegal Constant Type...report to developer"+constant);
	}
	
	public CPTuple(String sootClass, CPVariable variable, Object constant){
		
		if( ! (constant instanceof Float || constant instanceof Double || constant instanceof Long || 
				constant instanceof Boolean || constant instanceof Integer))
			throw new DavaFlowAnalysisException("Third argument of VariableValuePair not an acceptable constant value...report to developer");
			
			
		this.sootClass = sootClass;
		this.variable=variable;
		this.constant = constant;
		TOP = new Boolean(false);					
	}

	
	
	public CPTuple(String sootClass, CPVariable variable, boolean top){
		this.sootClass = sootClass;
		this.variable=variable;
	
		//notice we dont really care whether the argument top was true or false 
		setTop();			
	}

	public boolean containsLocal(){
		return variable.containsLocal();
	}
	
	public boolean containsField(){
		return variable.containsSootField();
	}
	
	/*
	 * If TOP is non null then that means it is set to TOP
	 */		
	public boolean isTop(){
		return TOP.booleanValue(); 
	}
	
	public void setTop(){
		constant=null;
		TOP = new Boolean(true);
	}
	
	
	
	public boolean isValueADouble(){
		return (constant instanceof Double);
	}
	
	public boolean isValueAFloat(){
		return (constant instanceof Float);
	}
	
	public boolean isValueALong(){
		return (constant instanceof Long);
	}
	
	public boolean isValueABoolean(){
		return (constant instanceof Boolean);
	}
	
	public boolean isValueAInteger(){
		return (constant instanceof Integer);
	}
				
	
	
	public Object getValue(){
	    return constant;
	}

	
	public void  setValue(Object constant){
		//System.out.println("here currently valued as"+this.constant);
		if( ! (constant instanceof Float || constant instanceof Double || constant instanceof Long || 
				constant instanceof Boolean || constant instanceof Integer))
			throw new DavaFlowAnalysisException("argument to setValue not an acceptable constant value...report to developer");
			
		this.constant = constant;
		TOP = new Boolean(false);
	}

	
	public String getSootClassName(){
		return sootClass;
	}
	
	
	public CPVariable getVariable(){
	    return variable;
	}
	

	public boolean equals(Object other){
	    if(other instanceof CPTuple){
	    	CPTuple var = (CPTuple)other;
	    	
	    	//if both are top thats all right
	    	if( sootClass.equals(var.getSootClassName()) && variable.equals(var.getVariable()) && isTop() & var.isTop()){  
	    		return true;
	    	}
	    	
	    	//if any one is top thats no good
	    	if(isTop() || var.isTop())
	    		return false;

	    	if(sootClass.equals(var.getSootClassName()) && variable.equals(var.getVariable()) && constant.equals(var.getValue())  ){
	    		//System.out.println("constant value "+constant.toString() + " is equal to "+ var.toString());
	    		return true;
	    	}
	    }
	    return false;
	}
	
	public String toString(){
	    StringBuffer b = new StringBuffer();
	    if(isTop())
		    b.append("<"+sootClass + ", " + variable.toString()+", TOP>");
	    else
		    b.append("<"+sootClass + ", " + variable.toString()+","+constant.toString()+">");
	    return b.toString();
	}
}

/********** END LOCAL CLASS DECLARATION *******************/

