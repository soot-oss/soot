package soot.cil;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.G;
import soot.RefType;
import soot.Singletons;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootResolver;
import soot.Type;

public class Cil_GenericHandler {
	// generic class
	private Map<String, List<String>> genericClassMap;    		// Stores lines of a generic class: E.g A`1=[]
	
	// generic functions
	private Map<String, List<String>> genericFunctionMap;       // Stores lines of a generic function: E.g A`1=[]
	private Deque<String> genericFunctionWorklist;				// Stores signature of a typed generic functions that need to be generated! e.g. void func<int>()
	private Map<String, Map<String, String>> genericFunctionGenericTypeMap;    // Stores the generic replacement types of a generic class for a generic function e.g. func`1=[T=int,X=float]
	private Map<String, List<String>> genericFunctionReplacmentTypeMap; // Stored the replacement map for a generic function
 	
	public Cil_GenericHandler(Singletons.Global g) {
		genericClassMap = new HashMap<String, List<String>>();
		
		genericFunctionMap = new HashMap<String, List<String>>();
		genericFunctionWorklist = new ArrayDeque<String>();
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
	
	public List<String> getFunctionClassLines(String functionName) {
		int numberOfGenericParameters = getNumberOfGenericFunctionParameters(functionName);
		functionName = functionName.substring(0, functionName.indexOf("<"));
		functionName += "`" + numberOfGenericParameters;
		
		List<String> list = null;
			
		list = this.genericFunctionMap.get(functionName);
		if(list == null) {
			System.err.println("Error could not finde: " + functionName + " in genericMap!");
		}
		return list;
	}
	
	public void addGenericClassToMap(String className, List<String> lines) {
		className = className.substring(0, className.indexOf("<"));
		if(!genericClassMap.containsKey(className)) {
			this.genericClassMap.put(className, lines);
		} 
	}
	
	public List<String> getGenericClassLines(String className) {
		int idx = className.indexOf("<");
		if (idx < 0)
			return null;
		
		className = className.substring(0, idx);
		List<String> list = null;
		
		try {	
			list = this.genericClassMap.get(className);
		} catch(Exception e) {
			System.err.println("Error could not finde: " + className + " in genericMap!");
			System.err.println(e.getMessage());
		}
		return list;
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
	
	public void addToAssemblyMap(String str, String currentClassAssemblyName){
		String str2 = str.replace("!", "");
		if(str2.startsWith("[")) {
			String assemblyName = str2.substring(str2.indexOf("[")+1,str2.indexOf("]"));
			
			if(str2.contains("`")) {
				String actualClassName = str2.substring(0, str2.indexOf("<"));
				Cil_Utils.addClassToAssemblyMap(Cil_Utils.removeAssemblyRefs(actualClassName), assemblyName);
			} else {
				Cil_Utils.addClassToAssemblyMap(Cil_Utils.removeAssemblyRefs(str2), assemblyName);
			}
		} else {
			Type type = Cil_Utils.getSootType(str2);
			if(type instanceof RefType) {
				Cil_Utils.addClassToAssemblyMap(Cil_Utils.removeAssemblyRefs(str2), currentClassAssemblyName);
			}
		}
		
		if(str.contains("`")) {
			int pos = str.indexOf("<");
			int pos2 = str.lastIndexOf(">");
			
			int numberOfParameters = Integer.parseInt(str.substring(str.indexOf("`")+1,pos));
			String innerPart = str.substring(pos+1, pos2);
			
			if(!innerPart.startsWith("!")) {
				if(numberOfParameters==1) {
					addToAssemblyMap(innerPart, currentClassAssemblyName);
				} else  {
					int lastPos = 0;
					int counter = 0;
					for(int i=0; i<innerPart.length(); ++i) {
						char current = innerPart.charAt(i);
						if(current==',' && counter==0) {
							String tmp = innerPart.substring(lastPos,i);
							lastPos = i+1;
							addToAssemblyMap(tmp, currentClassAssemblyName);
						} else if(current=='<') {
							counter++;
						} else if(current=='>') {
							counter--;
						} 
					}
					String last = innerPart.substring(lastPos,innerPart.length());
					addToAssemblyMap(last, currentClassAssemblyName);
				}
			}
		}
	}
	
	/*
	public List<String> genParameterListForGenericTypeMap(String className) {
		List<String> list;
		className = className.substring(className.indexOf("<")+1,className.lastIndexOf(">"));
		if(className.contains("`")) {
			list = generateGenericParameterList(className);
			List<String> list2 = new ArrayList<String>();
			
			for(int i=0; i<list.size(); ++i) {
				String str = list.get(i);
				if(str.contains("`") && str.contains("<")) {
					str = generateGenericClassName(str);
				}
				list2.add(str);
			}
			list =list2;
		} else {
			list = Arrays.asList(className.split(","));
		}
		return list;
	}
	*/
			
	public String generateGenericBaseClassName(String className) {
		int pos = className.indexOf("`");
		int start = className.indexOf("<");
		
		String str_numberOfGenericParameters = className.substring(pos+1, start);
		int numberOfGenericParameters = Integer.parseInt(str_numberOfGenericParameters);
		
		className = className.substring(0, pos);
		
		className += "_" + numberOfGenericParameters + "T";
				
		return className;
	}

	public void addGenericsToAssemblyMap(String className) {
		String assemblyName = getAssemblyName(className);
		String name = className.substring(assemblyName.length()+2,className.length());
		Cil_Utils.addClassToAssemblyMap(name, assemblyName);
	}
	
	private String getAssemblyName(String className) {
		String ret = null;
		if(className.startsWith("[")) {
			ret= className.substring(className.indexOf("[")+1, className.indexOf("]"));
		}
		return ret;
	}
	
	/*
	public String generateGenericFunctionName(String name, SootClass sootClass) {
		String generatedName = "";
		String baseName = name.substring(0, name.indexOf("<"));
		String genericParameters = name.substring(name.indexOf("<")+1,name.lastIndexOf(">"));
		
		String genericPart="";
		
		if(genericParameters.startsWith("[")) {
			String assemblyName = Cil_Utils.getAssemblyNameFromClassSig(genericParameters);
			genericParameters = Cil_Utils.clearString(genericParameters);
			Cil_Utils.addClassToAssemblyMap(genericParameters, assemblyName);
		} 
		
		if(genericParameters.contains("`")) {
			genericPart += "__";
			List<String> genParameters = Cil_Method.splitMethodArguments(genericParameters);
			for(String param : genParameters) {
				genericPart = Cil_GenericHandler.v().run(param, sootClass, false) + "_";
			} if(!genericParameters.isEmpty()) {
				genericPart = genericPart.substring(0, genericPart.length()-1);
			}
			
			genericPart += "__";
		} else {
			String tmp[] = genericParameters.split(",");
			for(int i=0; i<tmp.length; ++i) {
				genericPart += "__" + tmp[i];
			}
			genericPart += "__";
		}
		
		generatedName = baseName + genericPart;
		generatedName = generatedName.replace(".", "");
		return generatedName;
	}
	*/
	
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
	
	public void runGenricFunctionGenerator() {
		//return;
		while(!this.genericFunctionWorklist.isEmpty()) {
			String functionSignature = this.genericFunctionWorklist.pop();
			
			String functionName = functionSignature.substring(functionSignature.lastIndexOf(":")+1,functionSignature.indexOf("("));
			System.out.println("Generating function: " + functionName);
			String className = functionSignature.substring(functionSignature.lastIndexOf(" ")+1, functionSignature.indexOf(":"));
			
			List<String> method_lines = this.getFunctionClassLines(functionName);
			SootClass sootClass = SootResolver.v().makeClassRef(className);
			Map<String, String> genericMap = this.getGenericTypeMapForGenericFunctionName(functionName);
			
			String generic = functionName.substring(functionName.indexOf("<")+1, functionName.lastIndexOf(">"));
			
			List<String> genericReplacmentTypes = Cil_Method.splitMethodArguments(generic);
			
			functionSignature = functionSignature.substring(0, functionSignature.indexOf(" ")) + " " + functionSignature.substring(functionSignature.lastIndexOf(":")+1,functionSignature.length());
			Cil_Method method = new Cil_Method(genericMap, genericReplacmentTypes);
			method.parse(method_lines);
			sootClass.addMethod(method.getSootMethod());
		}
	}
}
