package soot.dava.toolkits.base.AST.structuredAnalysis;

import soot.BooleanType;
import soot.Value;
import soot.dava.internal.javaRep.DIntConstant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.StringConstant;

public class CPHelper {

	/*
	 * The helper class just checks the type of the 
	 * data being sent and create a clone of it
	 * 
	 *  If it is not a data of interest a null is send back
	 */
	public static Object wrapperClassCloner(Object value){
		if (value instanceof Double)
			return new Double(   ((Double)value).doubleValue() ) ;
		else if (value instanceof Float)
			return new Float(   ((Float)value).floatValue() ) ;
		else if (value instanceof Long)
			return new Long(   ((Long)value).longValue() ) ;
		else if (value instanceof Boolean) 	
			return new Boolean(   ((Boolean)value).booleanValue() ) ;
		else if (value instanceof Integer)
			return new Integer(   ((Integer)value).intValue() ) ;
		else
			return null;
	}
	
	
	
	/*
	 * isAConstantValue(Value toCheck)
	 *    it will check whether toCheck is one of the interesting Constants IntConstant FloatConstant etc etc
	 *    if yes return the Integer/Long/float/Double
	 *    
	 *    Notice for integer the callee has to check whether what is required is a Boolean!!!!
	 */	
	public static Object isAConstantValue(Value toCheck){
		Object value=null;
		
		if(toCheck instanceof LongConstant){
			value = new Long(((LongConstant)toCheck).value);
		}
		else if(toCheck instanceof DoubleConstant){
			value = new Double(((DoubleConstant)toCheck).value);
		}
		else if(toCheck instanceof FloatConstant){
			value = new Float(((FloatConstant)toCheck).value);
		}
		else if(toCheck instanceof IntConstant){
			int val = ((IntConstant)toCheck).value;
			value = new Integer(val);			
		}
		return value;
	}

	
	
	
	public static Value createConstant(Object toConvert){
		if(toConvert instanceof Long){
			return LongConstant.v( ((Long)toConvert).longValue() );
		}
		else if(toConvert instanceof Double){
			return DoubleConstant.v( ((Double)toConvert).doubleValue());
		}
		else if(toConvert instanceof Boolean){
			boolean val = ((Boolean)toConvert).booleanValue();
			if(val)
				return DIntConstant.v(1,BooleanType.v());
			else
				return DIntConstant.v(0,BooleanType.v());
		}	
		else if(toConvert instanceof Float){
			return FloatConstant.v( ((Float)toConvert).floatValue());
		}
		else if(toConvert instanceof Integer){
			return IntConstant.v( ((Integer)toConvert).intValue());
		}
		else
			return null;
	}

	
	
}
