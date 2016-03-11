package soot.cil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import soot.G;
import soot.RefLikeType;
import soot.SootMethod;
import soot.Type;
import soot.cil.ast.CilClass;
import soot.cil.ast.CilClassReference;
import soot.cil.ast.CilInstruction;
import soot.cil.ast.CilLocal;
import soot.cil.ast.CilLocal.TypeFlag;
import soot.cil.ast.CilLocalSet;
import soot.cil.ast.CilMethod;
import soot.cil.ast.CilMethodParameter;
import soot.cil.ast.CilMethodParameterList;
import soot.cil.ast.CilTrap;
import soot.cil.sources.CilMethodSource;

import com.google.common.collect.Lists;

class Cil_Method {
	private final CilClass cilClass;
	
	private String methodName;
	private SootMethod currentMethod;
	private int modifiers = 0;
	
	private Map<String, String> genericType;
	private Set<CilClassReference> dependencies = new HashSet<CilClassReference>();
	 
	private Type returnType;
	private CilMethodParameterList parameters = new CilMethodParameterList();
	
	private Map<String, String> genericFunctionType;
	private List<String> genericFunctionTypeList;
	private List<String> genericReplacmentTypes = null;
	
	private List<String> method_lines;
	
	boolean generatedMethod = false;
	
	public Cil_Method(CilClass clazz, Map<String, String> genericMap) {
		this.genericType = genericMap;
		this.cilClass = clazz;
	}
	
	// TODO change this function.
	// genericReplacmentTypes is not used yet
	public Cil_Method(CilClass clazz, Map<String, String> genericMap,
			List<String> genericReplacmentTypes) {
		this.genericType = genericMap;
		this.genericReplacmentTypes = genericReplacmentTypes;
		this.generatedMethod = true;
		this.cilClass = clazz;
	}
	
	public void generateFunctionGenericTypMap() {
		this.genericFunctionType = new HashMap<String, String>();
		this.genericFunctionTypeList = new ArrayList<String>();
		String genericParameter = this.methodName.substring(this.methodName.indexOf("<")+1, this.methodName.lastIndexOf(">"));
		String[] tmp = genericParameter.split(",");
		
		if(this.genericReplacmentTypes==null) {
			for(int i=0; i<tmp.length; ++i) {
				String param = tmp[i];
				//TODO change to [mscorlib]System.Object
				String replacementType ="System.Object";
				//String replacementType ="void";
				this.genericFunctionType.put(param, replacementType);
				genericFunctionTypeList.add(replacementType);
			}
		} else {
			if(genericReplacmentTypes.size()!=tmp.length) {
				System.err.println("Error different number of function replacment types");
			}
			
			for(int i=0; i<genericReplacmentTypes.size(); ++i) {
				String param = tmp[i];
				String replacementType = genericReplacmentTypes.get(i);
				this.genericFunctionType.put(param, replacementType);
				genericFunctionTypeList.add(replacementType);
			}
		}
		
	}
	
	public void parse(List<String> method_lines) {
		this.method_lines = method_lines;
		List<String> headerLines = Cil_Utils.getHeader(method_lines);
		this.parseHeader(headerLines);
		
		List<CilInstruction> instructions = new LinkedList<CilInstruction>();
		CilLocalSet locals = new CilLocalSet();
		List<String> opcodes = new LinkedList<String>();
		List<CilTrap> trapList = new LinkedList<CilTrap>();
		
		boolean automaticGeneratedClass = false;
		
		if(!(this.genericType == null)) {
			for(String values : genericType.values()) {
				//if(!values.equals("void")) {
				//TODO change to System.Object
				if(!values.equals("System.Object")) {
					automaticGeneratedClass = true;
					break;
				}
			}
		}
		
		List<String> localLines = new LinkedList<String>();
		if(!automaticGeneratedClass) {
			for(int i=headerLines.size(); i<method_lines.size(); ++i) {
				String line = method_lines.get(i).trim();
				
				if(line.startsWith(".entrypoint")) {
					// ignored
				} else if(line.startsWith(".maxstack")) {
					// ignored
				} else if(line.startsWith(".locals")) {
					int counter = 0;
					do {
						if(counter > 0) {
							i++;
							line = method_lines.get(i).trim();
						}
						
						line = Cil_Utils.removeComments(line);
						if(!line.isEmpty()){
							localLines.add(line);
						}
						counter++;
					} while(line.endsWith(","));
				} else if(line.startsWith(".custom")) {
//					List<String> attributeList = new LinkedList<String>();
//					attributeList.add(line);
//					
//					boolean exit = false;
//					
//					while(!exit) {
//						String next_line = method_lines.get(i+1); 
//						if(next_line.isEmpty() || next_line.startsWith(".") ||next_line.startsWith("//") || next_line.startsWith("IL_")) {
//							exit = true;
//						} else {
//							attributeList.add(next_line);
//						}
//					}
//					Cil_Attributes attributeParser = new Cil_Attributes(attributeList);
//					attributeParser.run();
				} else if(line.startsWith("IL_")) {
					int counter = 0;
					String tmpLine = "";
					
					do {
					if(counter > 0) {
						i++;
						line = method_lines.get(i).trim();
					}					
						line = Cil_Utils.removeComments(line);
						if(!line.isEmpty()) {
							tmpLine += line;
							if(!line.endsWith(",") && !line.endsWith("(")) {
								opcodes.add(tmpLine);
							} 
						}
						counter++;
					} while(line.endsWith(",") || line.endsWith("("));
				} else if(line.startsWith(".try")) {
					String nextLine = method_lines.get(i+1).trim();
					if(nextLine.startsWith("{")) {
						
						// handle expaned try-catch blocks
						handleExpanedTryCatch(i, method_lines, trapList);
					} else {
						handleSingleLineTryCatch(line, trapList);
					}
				}
			}
			
			// Parse the local definitions
			locals = parseLocals(localLines);
			
			// Parse the actual code
			instructions = parseOpcodes(opcodes);
		}
		else {
			String label = "IL_0000";
			String opcode = "generic";
			List<String> param = new ArrayList<String>();
			instructions.add(new CilInstruction(opcode, param, label));
		}
		
		// Get the Soot parameter types
		List<Type> parameterTypes = new ArrayList<Type>();
		for (CilMethodParameter param : parameters)
			parameterTypes.add(Cil_Utils.getSootType(cilClass,
					new CilClassReference(param.getType())));
		
		currentMethod = new SootMethod(methodName, parameterTypes, returnType);
		currentMethod.setModifiers(modifiers);		
		
		CilMethod method = new CilMethod(cilClass, parameters, instructions, trapList);
		
		CilMethodSource ms = new CilMethodSource(method, locals,
				this.genericFunctionType, this.genericFunctionTypeList, this.genericType);
		currentMethod.setSource(ms);
	}
	
	private void handleExpanedTryCatch(int linePos, List<String> methodLines,  List<CilTrap> trapList) {
		
		List<String> tryBlock = Cil_Utils.getCodeBLock(methodLines, linePos);
		
		String tryStartLabel = findFirstLabelInBlock(tryBlock);
		String tryEndLablel = findLastLabelInBlock(tryBlock);
		
		
		List<String> handlerBlock = Cil_Utils.getCodeBLock(methodLines, linePos + tryBlock.size() );
		
		String handlerStartLabel = findFirstLabelInBlock(handlerBlock);
		String handlerEndLabel = findLastLabelInBlock(handlerBlock);
		
		String[] handler = handlerBlock.get(0).split("\\s+");
		String catchType = handler.length > 1 ? handler[1].trim(): null;
		
		CilTrap trap = new CilTrap(tryStartLabel, tryEndLablel, catchType,
				handlerStartLabel, handlerEndLabel);
		trapList.add(trap);
	}
	
	private String findFirstLabelInBlock(List<String> block) {
		String ret = "";
		
		for(String token : block){
			if(token.startsWith("IL_")) {
				ret = token.split("\\s+")[0];
				ret = ret.replace(":", "");
				break;
			}
		}
		
		return ret.trim();
	}
	
	private String findLastLabelInBlock(List<String> block) {
		block = Lists.reverse(block);
		
		return findFirstLabelInBlock(block);
	}
	
	private void handleSingleLineTryCatch(String line, List<CilTrap> trapList){
		String[] tmp = line.split("\\s+");
		List<String> tokens = new ArrayList<String>();
		
		for(String token : tmp) {
			token = token.trim();
			if(!token.isEmpty()) {
				tokens.add(Cil_Utils.clearString(token));
			}
		}
		
		String tryStartLabel = tokens.get(1);
		String tryEndLablel = tokens.get(3);
		
		String handlerStartLabel = tokens.get(tokens.size()-3);
		String catchType = tmp.length <10 ? null : tokens.get(5);
		
		// TODO: double-check end label
		CilTrap trap = new CilTrap(tryStartLabel, tryEndLablel, catchType,
				handlerStartLabel, "");
		trapList.add(trap);
	}
	
	private List<CilInstruction> parseOpcodes(List<String> opcodes) {
		List<CilInstruction> list = new LinkedList<CilInstruction>();
		CilInstruction inst;
		String opcode, label;
		
		for(String str : opcodes) {
			str = Cil_Utils.removeTypePrefixes(str);
			
			// If this instruction references a generic type, we need to
			// replace it with the proper type.
			str = Cil_Utils.replaceGenericPlaceholders(str);
			
			String[] tokens = str.split("(\\s|;)+");
			if(tokens.length >1) {
				opcode = tokens[1];
				label = tokens[0];
				label = label.substring(0, label.length()-1);
				List<String> param = new LinkedList<String>();
				
				if(tokens.length>2){
					boolean inArgList = false;
					for(int i=2; i<tokens.length; ++i) {
						if (inArgList && !param.isEmpty())
							param.set(param.size() - 1, param.get(param.size() - 1) + " "  + tokens[i]);
						else
							param.add(tokens[i]);
						
						if (tokens[i].contains("(") && !tokens[i].contains(")"))
							inArgList = true;
						else if (tokens[i].contains(")") && !tokens[i].contains("("))
							inArgList = false;
					}
				}
				inst = new CilInstruction(opcode, param, label);
				list.add(inst);
				
				
				this.parseOpcodesForDependecies(label, opcode, param);
				
			}
		}
		return list;
	}
	
	private void parseOpcodesForDependecies(String lable,
			String opcode, List<String> parameters) {
		if(opcode.equals("newobj") ||
				opcode.equals("callvirt") ||
				opcode.equals("call")) {
			String signature = "";
			for(String str : parameters) {
 				signature = signature + " " + str;
			}
			
			signature = Cil_Utils.removeTokenFromString(signature, "instance");
			signature = Cil_Utils.removeTypePrefixes(signature);
			signature = Cil_Utils.replaceGenericPlaceholders(signature);
			
			//handle return type
			String returnType = signature.substring(0, signature.indexOf(" "));
			signature = signature.substring(signature.indexOf(" "));			
			addDependency(new CilClassReference(returnType));
			
			// Get generic types. If we reference a generic class, we need to
			// create its concrete subclass.
			List<CilClassReference> genericInstances = Cil_Utils.parseGenericInstances(signature);
			
			//handle className
			int pos = signature.indexOf(":");
			String className = signature.substring(0, pos);
			className = className.substring(className.lastIndexOf(" ")).trim();
			addDependency(new CilClassReference(className, genericInstances));
			
			//handle parameters
			String params = signature.substring(signature.indexOf("(")+1,signature.indexOf(")"));
			params = Cil_Utils.replaceGenericPlaceholders(params);
			if(!params.isEmpty()) {
				List<String> arguments = splitMethodArguments(params);
				
				for(String arg : arguments) {
					arg = G.v().soot_cil_CilNameMangling().doNameMangling(arg);
					addDependency(new CilClassReference(arg));
				}
			}
		}
		else if(opcode.equals("castclass")) {
			//TODO check if correct
			String signature = "";
			for(String str : parameters) {
				signature = signature + str; 
			}
			signature = Cil_Utils.removeTypePrefixes(signature);			
			signature = Cil_Utils.replaceGenericPlaceholders(signature);
			addDependency(new CilClassReference(signature));
		} else if(opcode.equals("isinst")) {
			//TODO implement
		} else if(opcode.equals("ldelem")) {
			//TODO implement
		} else if(opcode.equals("stelem")) {
			//TODO implement
		} else if(opcode.equals("ldfld")) {
			//TODO implement
		} else if(opcode.equals("stfld")) {
			//TODO implement
		} else if(opcode.equals("ldsfld")) {
			//TODO implement
		} else if(opcode.equals("stsfld")) {
			//TODO implement
		} else if(opcode.equals("newarr")) {
			//TODO implement
		} else if(opcode.equals("sizeof")) {
			//TODO implement
		}
		else if(opcode.equals("ldftn")) {
			// If the code is dealing with function pointers, we need our fake
			// interface for emulating such pointers.
			addDependency(new CilClassReference("_cil_delegate_"));
			
			// Create the fake class. Make sure to also include the return type,
			// because we need the return type afterwards when referencing the
			// original method.
			String targetSig = parameters.get(parameters.size() - 2) + " "
					+ parameters.get(parameters.size() - 1);
			String dispatcherClassName = G.v().soot_cil_CilNameMangling()
					.createDispatcherClassName(targetSig);
			addDependency(new CilClassReference(dispatcherClassName));
		}
		else if(opcode.equals("ldtoken")) {
			// We create a custom data structure derived from the CLR's token
			// data structures to capture the semantics of the constant token
			switch (Cil_Utils.getTokenType(parameters.get(0))) {
			case TypeRef:
				String typeRefClassName = G.v().soot_cil_CilNameMangling()
						.createTypeRefClassName(parameters.get(0));
				addDependency(new CilClassReference(typeRefClassName));
				break;
			case MethodRef:
				String methodRefClassName = G.v().soot_cil_CilNameMangling()
						.createMethodRefClassName(parameters.get(0));
				addDependency(new CilClassReference(methodRefClassName));
				break;
			case FieldRef:
				String fieldRefClassName = G.v().soot_cil_CilNameMangling()
						.createFieldRefClassName(parameters.get(0));
				addDependency(new CilClassReference(fieldRefClassName));
				break;
			default:
				throw new RuntimeException("Unsupported token type");
			}
		}
		else if(opcode.equals("box")) {
			addDependency(new CilClassReference(parameters.get(0)));
		}
	}
	
	/**
	 * Adds the given class reference as a dependency of the current method
	 * @param cilClassReference The dependency to add
	 */
	private void addDependency(CilClassReference cilClassReference) {
		if (Cil_Utils.getSootType(cilClass, cilClassReference) instanceof RefLikeType)
			dependencies.add(cilClassReference);
	}

	public static List<String> splitMethodArguments(String param) {
		List<String> argmunets = new ArrayList<String>();
		int counter = 0;
		int lastPos = 0; 
		for(int i=0; i<param.length(); ++i){
			char currentChar = param.charAt(i);
			if(currentChar=='<') {
				counter++;
			} else if(currentChar=='>') {
				counter--;
			} else if(currentChar==',' && counter==0) {
				String arg = param.substring(lastPos,i);
				lastPos = i+1;
				argmunets.add(arg);
			}
		}
		
		//adding last argument
		String arg = param.substring(lastPos,param.length());
		argmunets.add(arg);
		
		return argmunets;
	}
	
	private CilLocalSet parseLocals(List<String> locals) {
		Pattern pattern = Pattern.compile("(\\.locals init\\s*\\()?"
				+ "\\s*(\\[[0-9]+\\])?\\s*"
				+ "(class\\s+|valuetype\\s+)?\\s*"
				+ "([\\S]+)\\s+"
				+ "(\\w+)\\s*"
				+ "[,|\\)]");
		
		CilLocalSet list = new CilLocalSet();
		int autoId = 0;
		for(String str : locals) {
			// If we have a generic type, we need to sort that out first for not
			// confusing the regular expression.
			List<CilClassReference> genericInstances = Cil_Utils.parseGenericInstances(str);
			str = Cil_Utils.removeGenericsDeclaration(str);
			
			str = Cil_Utils.replaceGenericPlaceholders(str);
			Matcher matcher = pattern.matcher(str);
			matcher.find();
			
			int id = autoId++;
			int groupIdx = 1;
			
			// We might have the prefix
			String token = matcher.group(groupIdx++);
			if (token == null || token.startsWith(".locals"))
				token = matcher.group(groupIdx++);
			
			// Get the variable id. In case we don't have an explicit id, we
			// automatically generate one
			token = matcher.group(groupIdx++);
			if (token == null) {
				token = matcher.group(groupIdx++);
			}
			else if (token.startsWith("[")) {
				token = token.substring(1, token.length() - 1);
				id = Integer.parseInt(token);
				token = matcher.group(groupIdx++);
			}
			
			// Parse the next token. This can be a modifier.
			TypeFlag flag = TypeFlag.Unspecified;
			if (token == null) {
				token = matcher.group(groupIdx++);
			}
			else if (token.trim().equals("valuetype")) {
				flag = TypeFlag.ValueType;
				token = matcher.group(groupIdx++);
			}
			else if (token.trim().equals("class")) {
				flag = TypeFlag.Class;
				token = matcher.group(groupIdx++);
			}
			
			String type = token.trim();
			token = matcher.group(groupIdx++);
			
			String name = token.trim();
			
			type = G.v().soot_cil_CilNameMangling().doNameMangling(type);
			list.add(new CilLocal(id, name, new CilClassReference(type, genericInstances), flag));
		}
		return list;
	}

	private void parseHeader(List<String> headerLines) {
		String line = "";
		
		for(String s : headerLines){
			line = line + " " + s.trim();
		}
		
		line = Cil_Utils.removeTypePrefixes(line);
		
		line = removeMarhalInformation(line);
		
		//method is implemented in native code
		if(line.contains("pinvokeimpl")) {
			this.modifiers = this.modifiers | Cil_MethodAttributes.attributes.get("native");
			line = line.substring(0, line.indexOf("pinvokeimpl")) + line.substring(line.indexOf(")")+1);
		}
		
		// handle method name
		int startMethodSignature = line.lastIndexOf("(");
		int endMethodSignature = line.lastIndexOf(")");
		
		//if(line.contains("<"))
		String MethodName = line.substring(0, startMethodSignature);
		MethodName = MethodName.replace("'", "");
		int pos =0;
		if(MethodName.endsWith(">")) {
			String newMethodName = Cil_Utils.removeGenericContrained(MethodName);
			line = line.replace(MethodName, newMethodName);
			MethodName = newMethodName;
			pos = MethodName.lastIndexOf(" ");
		} else {
			pos = MethodName.lastIndexOf(" ");
			MethodName = Cil_Method.renameConstructorName(MethodName);
		}
		
		this.methodName = MethodName.substring(pos+1).trim();	
		
		if(this.methodName.contains(".")) {
			this.methodName = this.methodName.substring(this.methodName.lastIndexOf("."), this.methodName.length());
		}
		
		if(this.methodName.contains("<") && !this.methodName.startsWith("<")) {
			//this.methodName = this.methodName.replace(".ctor ", "");
			this.methodName = this.methodName.replace(".ctor", "");
			while(this.methodName.contains("(")) {
				this.methodName = this.methodName.substring(0, this.methodName.indexOf("(")) + this.methodName.substring(this.methodName.indexOf(")")+1, this.methodName.length()); 
				//int pos = this.methodName.indexOf("(")
			}
			
			//generate method generic map
			generateFunctionGenericTypMap();
			
			if(!this.generatedMethod) {
				//add lines for gene
				Cil_GenericHandler.v().addGenericFunctionToMap(this.methodName, this.method_lines);
				// add generic
				Cil_GenericHandler.v().addGenericReplacmentTypeMapForGenericFunction(this.methodName, this.genericReplacmentTypes);
				Cil_GenericHandler.v().addGenericTypeMapForGenericFunction(this.methodName, this.genericType);
			} else {
				String firstPart = this.methodName.substring(0, this.methodName.indexOf("<"));
				String genericPart = this.methodName.substring(this.methodName.indexOf("<")+1, this.methodName.lastIndexOf(">"));
				String[] genericParameters = genericPart.split(",");
				
				this.methodName = firstPart + "<";
				for(String generic : genericParameters) {
					String type = genericFunctionType.get(generic);
					this.methodName += type+ ",";
				}
				// remove last comma
				if(this.methodName.charAt(this.methodName.length()-1)==',') {
					this.methodName = this.methodName.substring(0, this.methodName.length()-1);	
				}
				this.methodName += ">";
			}
			
			this.methodName = G.v().soot_cil_CilNameMangling().doNameMangling(this.methodName);
		}
		
		//TODO check generic methodName
		if(this.methodName.contains("'")) {
			this.methodName = this.methodName.replace("'", "");
		}
		
		// replace generic types
		line = Cil_Utils.replaceGenericPlaceholders(line);
		
		// handle return type and modifiers
		pos = line.indexOf("(");
		String tmp = line.substring(0, pos);
		pos = tmp.lastIndexOf(" ");
		
		String modifierLine = line.substring(0, pos);
		String[] mods = modifierLine.split("\\s+");
		
		// Parse the return type
		String returnType = mods[mods.length-1];
		returnType = Cil_Utils.clearString(returnType);
		returnType = G.v().soot_cil_CilNameMangling().doNameMangling(returnType);
		this.returnType = Cil_Utils.getSootType(cilClass,
				new CilClassReference(returnType.trim()));
		
		for(String token : mods) {
			token = Cil_Utils.removeComments(token);
			
			if(!(token.startsWith("/*") && token.endsWith("*/")) && !token.startsWith(".class")) {
				if(Cil_MethodAttributes.attributes.containsKey(token)) {
					int modifier = Cil_MethodAttributes.attributes.get(token);
					modifiers = modifiers | modifier;
				}
			}
		}
		
		//handle parameters
		startMethodSignature = line.lastIndexOf("(");
		endMethodSignature = line.lastIndexOf(")");
		String parameters = line.substring(startMethodSignature+1, endMethodSignature);
		
		parameters = Cil_Utils.removeTypePrefixes(parameters);
		String[] params = parameters.trim().split(",?\\s+");
		
		if(params.length >= 2) {
			for(int i=0; i<params.length; i=i+2) {
				String type = params[i];
				String parameterName = params[i+1];
				
				if(!type.isEmpty()) {
					type = G.v().soot_cil_CilNameMangling().doNameMangling(type);
					this.parameters.add(new CilMethodParameter(i, parameterName.trim(), type));
				}
			}
		}
	}
	
	public SootMethod getSootMethod() {
 		return this.currentMethod;
	}
	
	public Set<CilClassReference> getDependencies() {
		return this.dependencies;
	}
	
	public static String renameConstructorName(String str) {
		str = str.replace(".ctor", "<init>");
		str = str.replace(".cctor", "<clinit>");
		return str;
	}
	
	private String removeMarhalInformation(String line) {
		boolean exit = true;
		
		while(exit) {
			int start = 0;
			int pos0 = line.indexOf(" marshal( ");
			int pos1 = line.indexOf(" marshal([");
			
			if(pos0 > 0 && pos1 > 0 ) {
				if(pos0 < pos1) {
					start = pos0;
				} else if(pos1 < pos0) {
					start = pos1;
				}
			} else if(pos0 > 0 || pos1 > 0 )  {
				if(pos0 > 0) {
					start = pos0;
				} else if(pos1 > 0) {
					start = pos1;
				}
			} else {
				break;
			}
			
			String substring = line.substring(start);
			int end = substring.indexOf(")");
			line = line.substring(0, start).trim() + " " + line.substring(start+end+1, line.length()).trim();
		}
		return line;
	}
}
