package soot.cil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.G;
import soot.Singletons;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class Cil_GenericHandler {
	// generic functions
	private Map<String, List<String>> genericFunctionMap;       // Stores lines of a generic function: E.g A`1=[]
	private Map<String, Map<String, String>> genericFunctionGenericTypeMap;    // Stores the generic replacement types of a generic class for a generic function e.g. func`1=[T=int,X=float]
	private Map<String, List<String>> genericFunctionReplacmentTypeMap; // Stored the replacement map for a generic function
 	
	public Cil_GenericHandler(Singletons.Global g) {
		genericFunctionMap = new HashMap<String, List<String>>();
		genericFunctionGenericTypeMap = new HashMap<String, Map<String,String>>();
		genericFunctionReplacmentTypeMap = new HashMap<String, List<String>>();
	}

	public static Cil_GenericHandler v() {
		return G.v().soot_cil_Cil_GenericHandler();
	}
	
	public void addGenericTypeMapForGenericFunction(String functionName, Map<String, String> genericTypeMap) {
		int numberOfGenericParameters = getNumberOfGenericFunctionParameters(functionName);
		functionName = functionName.substring(0, functionName.indexOf("<"));
		functionName += "`" + numberOfGenericParameters;
		
		if(!genericFunctionGenericTypeMap.containsKey(functionName)) {
			this.genericFunctionGenericTypeMap.put(functionName, genericTypeMap);
		}
	}
	
	public Map<String, String> getGenericTypeMapForGenericFunctionName(String functionName) {
		int numberOfGenericParameters = getNumberOfGenericFunctionParameters(functionName);
		functionName = functionName.substring(0, functionName.indexOf("<"));
		functionName += "`" + numberOfGenericParameters;
		
		Map<String, String> map = null;
		
		try {	
			map = this.genericFunctionGenericTypeMap.get(functionName);
		} catch(Exception e) {
			System.err.println("Error could not finde: " + functionName + " in genericMap!");
			System.err.println(e.getMessage());
		}
		return map;
	}
	
	public void addGenericReplacmentTypeMapForGenericFunction(String functionName, List<String> genericFunctionReplacmentTypeMap) {
		int numberOfGenericParameters = getNumberOfGenericFunctionParameters(functionName);
		functionName = functionName.substring(0, functionName.indexOf("<"));
		functionName += "`" + numberOfGenericParameters;
		
		if(!this.genericFunctionReplacmentTypeMap.containsKey(functionName)) {
			this.genericFunctionReplacmentTypeMap.put(functionName, genericFunctionReplacmentTypeMap);
		} 
	}
	
	public List<String> getGenericReplacmentTypeMapForGenericFunctionName(String functionName) {
		int numberOfGenericParameters = getNumberOfGenericFunctionParameters(functionName);
		functionName = functionName.substring(0, functionName.indexOf("<"));
		functionName += "`" + numberOfGenericParameters;
		
		List<String> list = null;
		
		try {	
			list = this.genericFunctionReplacmentTypeMap.get(functionName);
		} catch(Exception e) {
			System.err.println("Error could not finde: " + functionName + " in genericMap!");
			System.err.println(e.getMessage());
		}
		return list;
	}
	
	public void addGenericFunctionToMap(String functionName, List<String> lines) {
		int numberOfGenericParameters = getNumberOfGenericFunctionParameters(functionName);
		functionName = functionName.substring(0, functionName.indexOf("<"));
		functionName += "`" + numberOfGenericParameters;
		
		if(!genericFunctionMap.containsKey(functionName)) {
			this.genericFunctionMap.put(functionName, lines);
		} 
	}
		
	public void updateSuperClassModifiers(SootClass sootclass) {
		SootClass superClass = sootclass.getSuperclass();
		if(superClass != null) {
			if(superClass.isFinal()) {
				int mod = superClass.getModifiers();
				mod &= ~Cil_ClassAttributes.attributes.get("sealed");
				superClass.setModifiers(mod);
			}
			
			// update field modifiers
			for(SootField field : superClass.getFields()){
				int filed_modifiers = field.getModifiers();
				filed_modifiers &= ~Cil_FieldAttributes.attributes.get("sealed");
				if(field.isPrivate()) {
					filed_modifiers &= ~Cil_FieldAttributes.attributes.get("private");
					filed_modifiers |= Cil_FieldAttributes.attributes.get("protected");
				}
				field.setModifiers(filed_modifiers);
			}
			
			// update method modifiers
			for(SootMethod method : superClass.getMethods()) {
				int method_modifiers = method.getModifiers();
				method_modifiers &= ~Cil_MethodAttributes.attributes.get("sealed");
				if(method.isPrivate()) {
					method_modifiers &= ~Cil_MethodAttributes.attributes.get("private");
					method_modifiers |= Cil_MethodAttributes.attributes.get("protected");
				}
				method.setModifiers(method_modifiers);
			}
		}
	}
	
	public int getNumberOfGenericFunctionParameters(String functionName) {
		int numberOfParameters = 0;
		
		if(functionName.contains("<")) {
			String generic = functionName.substring(functionName.indexOf("<")+1, functionName.lastIndexOf(">"));
			int counter=0;
			if(!generic.isEmpty()) {
				numberOfParameters++;
			}
			for(int i=0; i<generic.length(); ++i) {
				char letter = generic.charAt(i);
				if(letter=='<') {
					counter++;
				} else if(letter=='>') {
					counter--;
				} else if(letter==',' && counter==0) {
					numberOfParameters++;
				}
			}
		}
		
		return numberOfParameters;
	}
	
}
