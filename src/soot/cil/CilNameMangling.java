package soot.cil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.G;
import soot.Singletons;

/**
 * Class that performs name mangling for nested and generic classes
 * 
 * @author Steven Arzt
 *
 */
public class CilNameMangling {
	
	private Map<String, String> generatedNameToActualNameMap =
			new HashMap<String, String>();	// A__T__=A`1<T> and A__Int__=A`1<Int>
	private Map<String, String> dispatcherNameToMethodSig =
			new HashMap<String, String>();	// original method signature to generated dispatcher class name
	private Map<String, String> typeRefNameToClassSig =
			new HashMap<String, String>();	// original class name to generated type reference struct name
	private Map<String, String> methodRefNameToClassSig =
			new HashMap<String, String>();	// original class name to generated type reference struct name
	private Map<String, String> fieldRefNameToClassSig =
			new HashMap<String, String>();	// original class name to generated type reference struct name
	
	public CilNameMangling(Singletons.Global g) {
		//
	}
	
	public static CilNameMangling v() {
		return G.v().soot_cil_CilNameMangling();
	}
	
	private String generateGenericClassName(String name) {
		// If this is not a generic class, we return the name as-is
		if (!name.contains("<"))
			return name;
		
		// Get the base class name
		String baseName = name.substring(0, name.indexOf("<"));
		baseName = baseName.replace("`", "__");
		
		generatedNameToActualNameMap.put(baseName, name);
		return baseName;
	}
	
	/**
	 * Takes a class name and creates a mangled version of it that is legal Soot
	 * class name
	 * @param className The original class name
	 * @return The mangled, Soot-compatible class name
	 */
	public String doNameMangling(String className) {
		// Generic classes
		className = className.replace("'", "");
		className = className.replace("<>", "");
		className = className.replace("`", "__");
//		className = generateGenericClassName(className);
		
		// Inner classes
		className = className.replace("/", "$");		
//		className = mangleNestedClass(className);
		
		return className;
	}
	
	private String mangleNestedClass(String className) {		
		List<String> nestedClasses = this.seperateNestedClassName(className);
		String generatedName = "";
		
		for(String name : nestedClasses) {
			if(name.contains("`")) {
				name = name.replace("`", "__");
			} else if(name.contains("<")) {
				name = name.replace("<", "__");
				name = name.replace(">", "__");
				name = name.replace(",", "_");
				name = name.replace(".", "");
				name = name.replace("[]", "_");
			}
			generatedName += name + "$";
		}
		if(generatedName.charAt(generatedName.length()-1)=='$') {
			generatedName = generatedName.substring(0, generatedName.length()-1);
		}
		return generatedName;
	}
	
	private List<String> seperateNestedClassName(String className) {
		List<String> list = new ArrayList<String>();
		String workingString = className;
		
		while(workingString.contains("$")) {
			int pos = workingString.indexOf("$");
			int pos2 = workingString.indexOf("<");
			
			if(pos > 0 && pos2 > 0) {
				if(pos < pos2) {
					String item = workingString.substring(0, pos);
					workingString = workingString.substring(pos+1);
					list.add(item);
				} else {
					int counter =0;
					for(int i=pos2; i<workingString.length(); ++i) {
						char c = workingString.charAt(i);
						if(c=='>') {
							counter --;
						} else if(c=='<') {
							counter ++;
						}
						if(counter==0) {
							pos2 = i;
							break;
						}
					}
					String item = workingString.substring(0, pos2+1);
					
					workingString = workingString.substring(pos2+1);
					
					list.add(item);
				}
			} else if(pos > 0) {
				list = Arrays.asList(workingString.split("\\$+"));
				workingString = "";
			} 
		}
		if(!workingString.isEmpty()) {
			list.add(workingString);
		}
		
		return list;
	}

	/**
	 * Gets the original name of a mangled class name
	 * @param mangledName The mangled class name to look up
	 * @return The original name of the class as it was in the IL disassembly
	 * file
	 */
	public String getOriginalNameFromMangled(String mangledName) {
		return generatedNameToActualNameMap.get(mangledName);
	}
	
	/**
	 * Performs basic string mangling that is independent of the concrete use
	 * case
	 * @param str The input string
	 * @return The mangled string
	 */
	private String doBaseMangling(String str) {
		String mangled = str.replace(":", "_");
		mangled = doNameMangling(mangled);
		mangled = mangled.replace(".", "_");
		mangled = mangled.replace(" ", "_");
		return mangled;
	}

	/**
	 * This method takes a method signature and creates a valid name for a
	 * dispatcher class from it
	 * @param targetSig The target method signature
	 * @return The name of the dispatcher class
	 */
	public String createDispatcherClassName(String targetSig) {
		String mangled = doBaseMangling(targetSig);
		String dispatcherClassName = "_cil_dispatch_" + mangled;
		
		dispatcherNameToMethodSig.put(dispatcherClassName, targetSig);
		return dispatcherClassName;
	}
	
	/**
	 * Gets the original method signature that was used to generate the given
	 * dispatcher class name
	 * @param dispatcherClassName The dispatcher class name to look up
	 * @return The original method signature if the given string is a dispatcher
	 * class name, otherwise false
	 */
	public String getTargetSigForDispatcherName(String dispatcherClassName) {
		return dispatcherNameToMethodSig.get(dispatcherClassName);
	}
	
	/**
	 * This method takes a class name and generates a name for a type reference
	 * structure from it 
	 * @param targetSig The target class name
	 * @return The name of the type reference structure
	 */
	public String createTypeRefClassName(String targetSig) {
		String mangled = doBaseMangling(targetSig);
		String dispatcherClassName = "_cil_typeref_" + mangled;
		
		typeRefNameToClassSig.put(dispatcherClassName, targetSig);
		return dispatcherClassName;
	}
	
	/**
	 * Gets the original reference for which the given mangled name was created
	 * @param mangled The mangled class name
	 * @return The original reference for which the given mangled name was
	 * created
	 */
	public String getTypeRefFromMangled(String mangled) {
		return typeRefNameToClassSig.get(mangled);
	}

	/**
	 * This method takes a method name and generates a name for a type reference
	 * structure from it 
	 * @param targetSig The target method name
	 * @return The name of the type reference structure
	 */
	public String createMethodRefClassName(String targetSig) {
		String mangled = doBaseMangling(targetSig);
		String dispatcherClassName = "_cil_methodref_" + mangled;
		
		methodRefNameToClassSig.put(dispatcherClassName, targetSig);
		return dispatcherClassName;
	}
	
	/**
	 * Gets the original reference for which the given mangled name was created
	 * @param mangled The mangled class name
	 * @return The original reference for which the given mangled name was
	 * created
	 */
	public String getMethodRefFromMangled(String mangled) {
		return methodRefNameToClassSig.get(mangled);
	}

	/**
	 * This method takes a field name and generates a name for a type reference
	 * structure from it 
	 * @param targetSig The target field name
	 * @return The name of the type reference structure
	 */
	public String createFieldRefClassName(String targetSig) {
		String mangled = doBaseMangling(targetSig);
		String dispatcherClassName = "_cil_fieldref_" + mangled;
		
		fieldRefNameToClassSig.put(dispatcherClassName, targetSig);
		return dispatcherClassName;
	}
	
	/**
	 * Gets the original reference for which the given mangled name was created
	 * @param mangled The mangled class name
	 * @return The original reference for which the given mangled name was
	 * created
	 */
	public String getFieldRefFromMangled(String mangled) {
		return fieldRefNameToClassSig.get(mangled);
	}

}
