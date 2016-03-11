package soot.cil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.G;
import soot.IntType;
import soot.LongType;
import soot.RefType;
import soot.Scene;
import soot.ShortType;
import soot.SootClass;
import soot.SootMethodRef;
import soot.Type;
import soot.VoidType;
import soot.cil.ast.CilClass;
import soot.cil.ast.CilClassReference;
import soot.cil.ast.CilGenericDeclaration;
import soot.cil.ast.CilGenericDeclarationList;

public class Cil_Utils { 
	
	private static Map<String, String> classAssemblyMap = new HashMap<String, String>();
	
	public static String getAssemblyForClassName(String className) {
		String assemblyName = classAssemblyMap.get(className);
		if(assemblyName==null) {
			className = G.v().soot_cil_CilNameMangling().getOriginalNameFromMangled(className);
		}
		return classAssemblyMap.get(className);
	}
	
	public static String getAssemblyNameFromClassSig(String className) {
		String assemblyName = null;
		if(className.startsWith("[")) {
			int pos = className.indexOf("[");
			int pos2 = className.indexOf("]");
			assemblyName = className.substring(pos+1, pos2);
		}
		return assemblyName;
	}
		
	public static String addClassToAssemblyMap(String className, String assemblyName) {
		className = Cil_Utils.removeAssemblyRefs(className).trim();
		
		// Do not consider array as loadable classes
		String baseName = className.replace("[]", "");
		
		Cil_Utils.classAssemblyMap.put(baseName, assemblyName);
		return className;
	}
	
	public static String removeTypePrefixes(String type) {
		type = removeTokenFromString(type, "class ");
		type = removeTokenFromString(type, "valuetype ");
		return type;
	}
	
	public static String removeGenericContrained(String name) {
		name = name.replace(".ctor ", "");
		while(name.contains("(")) {
			int start = name.indexOf("(");
			int end = name.indexOf(")");
			name = name.substring(0, start).trim() + name.substring(end+1, name.length()).trim();
		}
		return name;
	}
	
	public static String removeComments(String str) {
		for(;;) {
			int start = str.indexOf("/*");
			int end = str.indexOf("*/");
			if(start ==-1 || end== -1) {
				break;
			} else {
				str = str.substring(0, start) + str.substring(end+2, str.length());
			}
		}
		
		int cmtPos = str.indexOf("//");
		if (cmtPos > 0)
			str = str.substring(0, cmtPos);
		
		return str.trim();
	}
	
	public static String removeAssemblyRefs(String str) {
		if(str.contains("[") && str.contains("]")) {
			for(;;) {
				int start = str.indexOf("[");
				int end = str.indexOf("]");
				if(start ==-1 || end== -1 || end-start == 1) {
					break;
				} else {
					str = str.substring(0, start) + str.substring(end+1, str.length());
				}
			}
			str = str.trim();
		}
		return str.trim();
	}
	
	public static String clearString(String str) {
		str = Cil_Utils.removeAssemblyRefs(Cil_Utils.removeComments(str));
		return str;
	}
	
	public static int findEndOfBlock(List<String> lines, int start) {
		int endPos = -1;
		int counter = 0;
		boolean begin = false;
		
		for(int i=start; i<lines.size(); ++i) {
			String line = lines.get(i).trim();
			if(line.startsWith("{")) {
				counter++;
				begin = true;
			}else if(line.startsWith("}")){
				counter--;
			}
			
			if(counter == 0 && begin == true) {
				endPos = i;
				break;
			}
		}
		if(endPos==-1) {
			System.out.println("ParserError!!!!");
		}
		return endPos;
	}
	
	public static List<String> getCodeBLock(List<String> lines, int start) {
		int endPos = Cil_Utils.findEndOfBlock(lines, start);
		List<String> class_lines = lines.subList(start, endPos+1);
		List<String> ret = new LinkedList<String>();
		for(String s : class_lines) {
			s = s.trim();
			ret.add(s);
		}
		return ret; 
	}
	
	public static List<String> getHeader(List<String> lines) {
		List<String> header = new ArrayList<String>();
		
		for(int i=0; i<lines.size(); ++i) {
			String line = lines.get(i).trim();
			if(line.startsWith("{")) {
				break;
			} else {
				header.add(line.trim());
			}
		}
		return header;
	}
	
	public static Type getSootType(CilClass clazz, CilClassReference toLookup) {
		// Replace the generic placeholders with the actual types
		toLookup = toLookup.resolveGenerics(clazz);
		
		// Check if the class name is a generic
		String className = toLookup.getClassReference();
		if (clazz != null && className.startsWith("!")) {
			// Check whether we have any more precise type info in the class
			CilGenericDeclaration generic = clazz.getGenericParams().getElementByName(
					className.substring(1));
			if (generic != null)
				return getSootType(clazz, new CilClassReference(generic.getSuperType().getClassName()));
		}
		
		// We may need to do name mangling for generic types
		String token = toLookup.getMangledName();
//		String token = G.v().soot_cil_CilNameMangling().doNameMangling(className);
		Type t = null;
		
		// Get rid of the modifiers
		token = removeTokenFromString(token, "instance");
		token = removeTokenFromString(token, "native");
		
		// Remove the assembly specifiers
		token = extractAssemblyNames(token);
		
		int arrayDimension = 0;
		while (token.endsWith("[]")) {
			arrayDimension++;
			token = token.substring(0, token.length() - 2);
		}
	
		if(arrayDimension > 0) {
			Type baseType = Cil_Utils.getSootType(clazz, new CilClassReference(token));
			t = ArrayType.v(baseType, arrayDimension);
			return t;
		}
				
		// Check for some special data types
		if (token.equals("object")) {
			t = RefType.v("System.Object");
		}
		else if (token.equals("string")) {
			t = RefType.v("System.String");
		}
		else if(token.equals("bool")) {
			t = BooleanType.v();
		}
		else if(token.equals("float32")
				|| token.equals("float")) {
				//|| token.equals("System.Single")) {
			t = FloatType.v();
		} else if(token.equals("float64")
				|| token.equals("double") ) {
				//|| token.equals("System.Double")) {
			t = DoubleType.v();
		} else if( token.equals("byte")
				|| token.equals("sbyte")
				|| token.equals("int8")
				|| token.equals("uint8")) {
				//|| token.equals("System.SByte")) {
			t = ByteType.v();
		} else if(token.equals("int16")
				|| token.equals("uint16")
				|| token.equals("short")) {
				//|| token.equals("System.Int16")) {
				//|| token.equals("System.UInt16")) {
			t = ShortType.v();
		} else if(token.equals("int32")
				|| token.equals("uint32")
				|| token.equals("int") ){
				//|| token.equals("System.Int32")
				//|| token.equals("System.UInt32")) {
			t = IntType.v();
		} else if(token.equals("int64")
				|| token.equals("uint64")
				|| token.equals("Long") ) {
				//|| token.equals("System.Int64")
				//|| token.equals("System.UInt64")) {
			t = LongType.v();
		} else if(token.equals("char")) {
			t = CharType.v();
		} else if(token.equals("void")) {
			t = VoidType.v();
		} else {
			t = RefType.v(token);
		}
		
		return t;
	}
	
	private static String extractAssemblyNames(String className) {
		// If we have an assembly reference, we note that down
		if (className.startsWith("[")) {
			int closeIdx = className.indexOf("]");
			String assemblyName = className.substring(1, closeIdx).trim();
			className = className.substring(closeIdx + 1);
			addClassToAssemblyMap(className, assemblyName);
		}
		
		int idx;
		while ((idx = className.indexOf("<[")) >= 0) {
			String part1 = className.substring(0, idx + 1);
			String part2 = className.substring(idx);
			
			int idx2 = part2.lastIndexOf(">");
			String genericClass = part2.substring(1, idx2);
			part2 = part2.substring(idx2);
			
			className = part1 + extractAssemblyNames(genericClass) + part2;
		}
		
		return className;
	}

	public static boolean isTypeUnisigned(String type) {
		boolean b = false;
		if(type.equals("byte")
				||type.equals("uint8")
				||type.equals("System.Byte")
				||type.equals("uint16")
				||type.equals("System.UInt16")
				||type.equals("uint32")
				||type.equals("System.UInt32")
				||type.equals("uint64")
				||type.equals("System.UInt64")) {
			b = true;
		}
		return b;
	}
	
	public static boolean isDWord(Type type) {
		return type instanceof LongType || type instanceof DoubleType;
	}
	
	public static boolean isNumeric(String str) {
		return str.matches("\\d+");
	}
	
	public static void printMemoryStatistics() {
		long totalMem = Runtime.getRuntime().totalMemory();
    	long freeMem =  Runtime.getRuntime().freeMemory();
    	long usedMem = totalMem - freeMem;
    	
    	//System.out.println("Total Memory: " + Math.round(totalMem/(1024.f*1024.f)) + " MB");
		//System.out.println("Free Memory: " + Math.round(freeMem/(1024.f*1024.f)) + " MB");
		System.out.println("Used Memory: " + Math.round(usedMem/(1024.f*1024.f)) + " MB");
	}
	
	public static String removeTypeAttributes(String str) {
		int start=0, end =0;
		if(str.contains("modopt")) {
			start = str.indexOf("modopt");
			String t = str.substring(start, str.length());
			end = t.indexOf(")") + 1 + start;
			str = str.substring(0, start) + str.substring(end, str.length());
		} else if (str.contains("modreq")) {
			start = str.indexOf("modreq");
			String t = str.substring(start, str.length());
			end = t.indexOf(")")+ 1 + start;
			str = str.substring(0, start) + str.substring(end, str.length());
		}
		
		return str;
	}
		
	/***
	 * Removes a token from string, if the prefix and suffix of the token do not consist of alphanumeric characters 
	 * @param str
	 * @param token
	 * @return str with removed token
	 */
	public static String removeTokenFromString(String str, String token) {
		str = str.trim();
		token = token.trim();
		String prefix = "(?<!\\w)";
		String sufix = "\\s+";//(?!\\w)";
		String pattern = prefix+token+sufix;
		String ret = str.replaceAll(pattern, "");
		return ret;
	}
	
	public static String replaceGenericPlaceholders(String input) {
		if (!input.contains("!"))
			return input;
		
		// Get the list of generics
		CilGenericDeclarationList generics = parseGenericDeclaration(input);
		if (generics != null) {
			for (int i = 0; i < generics.getAllElements().size(); i++) {
				CilGenericDeclaration decl = generics.getAllElements().get(i);
				input = input.replace("!!" + i, decl.getName());
				input = input.replace("!" + i, decl.getName());
			}
		}
		
		return input;
	}
	
	public static BigInteger parseInteger(String value) {
		int radix = 10;
		if (value.startsWith("0x")) {
			radix = 16;
			value = value.substring(2);
		}
		return new BigInteger(value, radix);
	}

	/**
	 * Gets the class name from a CIL method signature
	 * @param originalSig The CIL method signature
	 * @return The name of the class references in the CIL signature
	 */
	public static String getClassNameFromMethodSignature(String originalSig) {
		String className = originalSig.substring(0, originalSig.indexOf("::"));
		return className;
	}
	
	/**
	 * Parses the concrete type instances that take the place of the generic
	 * ones in the given signature
	 * @param signature The signature to parse
	 * @return The ordered list of (possibly nested) referenced to the concrete
	 * classes for the original generic ones
	 */
	public static List<CilClassReference> parseGenericInstances(String signature) {
		// Parse the generics. We are looking for concrete instances that map
		// generics to actual types.
		CilGenericDeclarationList generics = Cil_Utils.parseGenericDeclaration(signature);
		if (generics != null) {
			List<CilClassReference> genericInstances = new ArrayList<CilClassReference>();
			for (CilGenericDeclaration decl : generics) {
				List<CilClassReference> nestedGenerics = null;
				if (decl.getName().contains("<"))
					nestedGenerics = parseGenericInstances(decl.getName());
				genericInstances.add(new CilClassReference(decl.getName(), nestedGenerics));
			}
			return genericInstances;
		}
		return null;
	}

	public static SootMethodRef getMethodRef(CilClass containingClass,
			String signature, boolean isStatic) {
 		// Parse the generics
		List<CilClassReference> genericInstances = parseGenericInstances(signature);
 		
		// remove modifiers
 		signature = Cil_Utils.removeTokenFromString(signature, "class");
 		signature = Cil_Utils.removeTokenFromString(signature, "valuetype");
 		signature = Cil_Utils.removeTokenFromString(signature, "instance");
 		
 		// Extract the return type
 		String returnType = signature.substring(0, signature.indexOf(" ")).trim();
		Type retType = Cil_Utils.getSootType(containingClass,
				new CilClassReference(returnType));
 		signature = signature.substring(signature.indexOf(" ")).trim();
 		
 		// Extract the class name
 		String className = signature.substring(0, signature.indexOf(":"));
		SootClass declaringClass = ((RefType) Cil_Utils.getSootType(containingClass,
				new CilClassReference(className, genericInstances))).getSootClass();

		// Extract the method name
		String methodName = signature.substring(signature.lastIndexOf(":")+1,signature.indexOf("("));		
		methodName = G.v().soot_cil_CilNameMangling().doNameMangling(methodName);
		methodName = Cil_Method.renameConstructorName(methodName);
		signature = signature.substring(signature.indexOf("(")).trim();
		
		// get parameter types
		List<Type> parameterTypes = new ArrayList<Type>();
		List<String> mSignature = splitMethodParameters(signature);
		for(String token : mSignature) {
			token = Cil_Utils.replaceGenericPlaceholders(token);
			parameterTypes.add(Cil_Utils.getSootType(containingClass,
					new CilClassReference(token)));
		}
		
		//generate mtehodRef
		SootMethodRef method = Scene.v().makeMethodRef(declaringClass, methodName,
				parameterTypes, retType, isStatic);
		
		return method;
	}
 	
	private static List<String> splitMethodParameters(String str) {
		List<String> list = new ArrayList<String>();
		int pos = str.indexOf("(");
		int pos2 = str.lastIndexOf(")");
		if(pos == -1) {
			pos = 0;
		} 
		if(pos2 == -1) {
			pos2 = str.length()-1;
		}
		
		str = str.substring(pos+1, pos2);
		
		String[] tokens = str.split(",");
		for(String token : tokens) {
			if(!token.isEmpty()) {
				token = Cil_Utils.clearString(token);
				list.add(Cil_Utils.clearString(token));
			}
		}
		
		return list;
	}
	
	/**
	 * Enumeration representing the different types of tokens that can be loaded
	 * with the ldtoken opcode
	 * @author sarzt
	 *
	 */
	public enum TokenType {
		MethodRef,
		TypeRef,
		FieldRef
	}
	
	/**
	 * Gets the type of a token that is about to be loaded using an ldtoken
	 * instruction
	 * @param token
	 * @return
	 */
	public static TokenType getTokenType(String token) {
		if (token.contains("("))
			return TokenType.MethodRef;
		else if (token.contains(":"))
			return TokenType.FieldRef;
		else
			return TokenType.TypeRef;
	}
	
	public static String removeGenericsDeclaration(String str) {
		return replaceGenericsDeclaration(str, "");
	}
	
	public static String replaceGenericsDeclaration(String str, String replacmeent) {
		Pattern genericClassPattern = Pattern.compile("(`\\d+<(\\w|\\s|,|<|>|\\(|\\)|\\.|`|\\!|\\+|-)*)>");
		Matcher m = genericClassPattern.matcher(str);
		if (m.find()) {
			String generic = m.group(0);
			return str.replace(generic, replacmeent);
		}
		return str;
	}

	/**
	 * Parses the generic declarations for CIL methods
	 * @param line The string containing the generic declarations
	 * @return The generic declarations in the given CIL string
	 */
	public static CilGenericDeclarationList parseGenericDeclaration(String line) {
		// Look for generic definitions
		Pattern genericClassPattern = Pattern.compile("(`(\\d+)<((\\w|\\s|,|<|>|\\(|\\)|\\.|`|\\!|\\+|-)*)>)");
		Matcher m = genericClassPattern.matcher(line);
		if (m.find() && m.groupCount() >= 3) {
			String genericsStr = m.group(3);
			
			// We have found generic definitions, now parse them
			CilGenericDeclarationList results = new CilGenericDeclarationList();
			for (String token : split(genericsStr, ',')) {
				results.add(parseSingleGeneric(token));
			}
			return results;
		}
		return null;
	}
	
	/**
	 * Splits the given string at the given delimiter, but makes sure to not
	 * split inside expressions
	 * @param in The string to split
	 * @param delimiter The delimiter at which to split
	 * @return The partial strings split from the incoming one
	 */
	public static List<String> split(String in, char delimiter) {
		List<String> results = new ArrayList<String>();
		
		int curLevel = 0;
		int startPos = 0;
		for (int i = 0; i < in.length(); i++) {
			char c = in.charAt(i);
			if (c == '(' || c == '<')
				curLevel++;
			else if (c == ')' || c == '>')
				curLevel--;
			else if (c == delimiter && curLevel == 0) {
				String decl = in.substring(startPos, i).trim();
				results.add(decl);
				startPos = i+1;
			}
		}
		if (startPos < in.length()) {
			String decl = in.substring(startPos).trim();
			results.add(decl);
		}
		return results;
		
	}
	
	/**
	 * Parses a single generic definition
	 * @param decl The generic definition
	 * @return The AST object that corresponds to the given generic definition
	 */
	private static CilGenericDeclaration parseSingleGeneric(String decl) {
		if (decl.startsWith("(")) {
			int level = 0;
			for (int i = 0; i < decl.length(); i++) {
				char c = decl.charAt(i);
				if (c == '(')
					level++;
				else if (c == ')') {
					level--;
					if (level == 0) {
						String strSuperType = decl.substring(1, i);
						List<CilClassReference> genericInstances = Cil_Utils.parseGenericInstances(strSuperType);
						CilClassReference superType = new CilClassReference(removeGenericsDeclaration(strSuperType),
								genericInstances);
						return new CilGenericDeclaration(decl.substring(i + 1), superType);
					}
				}
			}
			throw new RuntimeException("Could not parse generic definition");
		}
		else
			return new CilGenericDeclaration(decl.replace("class ", ""), new CilClassReference("System.Object"));
	}
	
}
