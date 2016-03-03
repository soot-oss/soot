package soot.cil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.G;
import soot.RefType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootResolver;
import soot.Type;
import soot.util.ArraySet;

class Cil_ClassParser {
	private SootClass sootClass;
	private SootClass nestedSuperClass;

	private String className = "";
	private String superClassName = "";

	private List<String> interfaces = new LinkedList<String>();

	private boolean isGenericClass = false;
	private boolean isGeneratedGenericClass = false;
	
	private List<SootClass> listNestedClasses = new LinkedList<SootClass>();
	private List<String> genericParameters = new ArrayList<String>();
	private List<String> genericReplaceTypes = null;
	private Set<Type> dependencies = new ArraySet<Type>();

	private List<String> class_lines;
	private int bodyLinesOffset = 0;

	private Map<String, String> genericMap = null;

	private List<String> constFieldList;

	public Cil_ClassParser(List<String> class_lines, SootClass sootClass, boolean sootClassIsOuterClass) {
		this.class_lines = class_lines;
		if (sootClassIsOuterClass) {
			this.nestedSuperClass = sootClass;
		} else {
			this.sootClass = sootClass;
		}
		this.constFieldList = new ArrayList<String>();
	}

	public SootClass getSootClass() {
		return this.sootClass;
	}

	public boolean isGeneratedGenericClass() {
		return isGeneratedGenericClass;
	}

	public boolean isGenericClass() {
		return isGenericClass;
	}

	private void parseHeader() {
		List<String> header = Cil_Utils.getHeader(class_lines);
		if (header.isEmpty())
			return;

		this.bodyLinesOffset = header.size();
		int modifiers = 0;

		//TODO change to count of < and >
		// for generic classes the signature can be split over multiple lines
		// first line has to contain the complete signature
		if (header.get(0).contains("<") && !header.get(0).contains(">")) {
			String line = "";
			int endLine = 0;
			for (int i = 0; i < header.size(); ++i) {
				line = line + header.get(i);
				if (line.contains(">")) {
					endLine = i;
					break;
				}
			}

			List<String> header2 = new LinkedList<String>();
			header2.add(line);
			header2.addAll(header.subList(endLine + 1, header.size()));
			header = header2;
		}

		String firstLine = header.get(0);
		int startExtends = 0;
		int startImplements = 0;

		for (int i = 0; i < header.size(); ++i) {
			String line = header.get(i);
			if (line.contains("extends")) {
				startExtends = i;
			} else if (line.contains("implements")) {
				startImplements = i;
			}
		}
		
		// Remove comments
		firstLine = Cil_Utils.removeComments(firstLine);
		
		// If we have static initialization, we remove it
		{
			int pos = firstLine.indexOf("=");
			if (pos >= 0)
				firstLine = firstLine.substring(0, pos);
		}

		// TODO handle Covariant and Contravariant Generic
		firstLine = firstLine.replace("+ ", "");
		firstLine = firstLine.replace("- ", "");
		firstLine = Cil_Utils.removeTypePrefixes(firstLine);

//		firstLine = Cil_Utils.removeGenericContrained(firstLine);

		String[] tokens = firstLine.split("\\s+");
				
		// check if class is a generic
		if (className.contains("`") || className.contains("<")) {
			this.isGenericClass = true; 
		}

		if (!(this.nestedSuperClass == null)) {
			String nestedSuperClassName = this.nestedSuperClass.toString();
			nestedSuperClassName = G.v().soot_cil_CilNameMangling().doNameMangling(nestedSuperClassName);
			
			System.out.println(this.className);
			this.sootClass = SootResolver.v().makeClassRef(this.className);
			Cil_Utils.addClassToAssemblyMap(this.className, Cil_Utils.getAssemblyForClassName(this.nestedSuperClass.toString()));
		}
		
		if (this.isGenericClass()) {
			this.genericMap = generateGenericTypMap();
		}
		// parse class attributes
		for (String s : tokens) {
			s = Cil_Utils.removeComments(s);
			if (!(s.startsWith("/*") && s.endsWith("*/")) && !s.startsWith(".class")) {
				if (Cil_ClassAttributes.attributes.containsKey(s)) {
					int modifier = Cil_ClassAttributes.attributes.get(s);
					modifiers = modifiers | modifier;
				}
			}
		}
		this.sootClass.setModifiers(modifiers);

		// handle super class
		if (startExtends > 0 && !this.isGeneratedGenericClass()) {
			superClassName = header.get(startExtends);
			superClassName = Cil_Utils.removeTypePrefixes(superClassName);
			superClassName = superClassName.replace("extends ", "");

			superClassName = Cil_Utils.removeComments(superClassName);
			
			// handle generic super class
			superClassName = G.v().soot_cil_CilNameMangling().doNameMangling(superClassName);
			
			SootClass superClass = ((RefType) Cil_Utils.getSootType(superClassName)).getSootClass();
			this.sootClass.setSuperclass(superClass);
		}
		else if (this.isGeneratedGenericClass) {
			superClassName = G.v().soot_cil_CilNameMangling().doNameMangling(superClassName);
			SootClass superSootClass = SootResolver.v().makeClassRef(superClassName);
			this.sootClass.setSuperclass(superSootClass);

			// update modifiers of superClass!~
			// TODO update modifiers of superClass
			// Cil_GenericHandler.v().updateSuperClassModifiers(this.sootClass);
		}

		// handle interfaces
		if (startImplements > 0 && !this.isGeneratedGenericClass()) {
			this.interfaces = header.subList(startImplements, header.size());
		}

		if (!this.interfaces.isEmpty()) {
			this.interfaces = handleGenericInterfacesBaseClasses(this.interfaces);
			// addExternalInterfacesToAssemblyMap();
			for (String iface : this.interfaces) {
				iface = iface.replace("implements ", "");
				iface = Cil_Utils.removeComments(iface);
				SootClass ifaceClass = SootResolver.v().makeClassRef(iface);
				this.sootClass.addInterface(ifaceClass);
			}
		}

		if (this.isGenericClass()) {
			Cil_GenericHandler.v().addGenericClassToMap(className, class_lines);
		}
	}

	private List<String> handleGenericInterfacesBaseClasses(List<String> interfaces) {
		ArrayList<String> list = new ArrayList<String>();
		for (String line : interfaces) {
			line = Cil_Utils.removeTypePrefixes(line);
			line = line.replace("implements ", "");
			
			String interfaceName = null;

			if (line.contains("`")) {
				line = line.trim();
				if(line.startsWith(",")) {
					line = line.substring(1);
				} else if(line.endsWith(",")) {
					line = line.substring(0, line.length()-1);
				}
				interfaceName = G.v().soot_cil_CilNameMangling().doNameMangling(interfaceName);

			} else {
				String[] tokens2 = line.split("\\s+");
				interfaceName = tokens2[tokens2.length - 1];
				int pos = interfaceName.lastIndexOf(",");
				if (pos != -1) {
					interfaceName = interfaceName.substring(0, pos);
				}
			}
			list.add(interfaceName);

		}
		return list;
	}

	public void parse() {
		this.parseHeader();
		this.parseBody();
	}

	private void parseBody() {
		for (int i = bodyLinesOffset; i < class_lines.size(); ++i) {
			String line = class_lines.get(i).trim();
			
			// handle methods
			if (line.startsWith(".method")) {
				Cil_Method method = new Cil_Method(genericMap);

				List<String> method_lines = Cil_Utils.getCodeBLock(class_lines, i);
				method.parse(method_lines);
				i = i + method_lines.size() - 1;
				
				//TODO change this back so every method is added.
				SootMethod m = method.getSootMethod();
				if(!this.sootClass.declaresMethod(m.getName(), m.getParameterTypes(), m.getReturnType())) {
					this.sootClass.addMethod(method.getSootMethod());
					this.dependencies.addAll(method.getDependencies());
				}
			}

			// handle fields
			else if (line.startsWith(".field")) {
				if (!this.isGeneratedGenericClass()) {
					Cil_FieldParser parser = new Cil_FieldParser(this.constFieldList);
					parser.run(line);

					SootField f = parser.getSootField();
					this.sootClass.addField(f);
				}
			}

			// handle attributes
			else if (line.startsWith(".custom")) {
				List<String> attributeList = new LinkedList<String>();
				// attributeList.add(line);
				//
				// boolean exit = false;
				//
				// while(!exit) {
				// String next_line = class_lines.get(i+1);
				// if(next_line.isEmpty() || next_line.startsWith(".")
				// ||next_line.startsWith("//")) {
				// exit = true;
				// } else {
				// attributeList.add(next_line);
				// }
				// }
				// Cil_Attributes attributeParser = new
				// Cil_Attributes(attributeList);
				// attributeParser.run();
			}

			// handle events
			else if (line.startsWith(".event")) {

			}

			// handle
			else if (line.startsWith(".data")) {

			}

			//
			else if (line.startsWith(".override")) {

			}

			else if (line.startsWith(".pack")) {

			}

			else if (line.startsWith(".param")) {

			}

			else if (line.startsWith(".property")) {

			}

			else if (line.startsWith(".size")) {

			}

			// handle nested classes
			else if (line.startsWith(".class")) {
				// System.out.println("Handle nested classes");
				//TODO currently we do not parse nested classes directly, only if application class or if necessary due to dependency 
				
				List<String> nestedClassLines = Cil_Utils.getCodeBLock(class_lines, i);
//				Cil_ClassParser parser = new Cil_ClassParser(nestedClassLines, this.sootClass, true);
//				parser.parse();
//
//				this.dependencies.addAll(parser.getDependencies());
//
//				SootClass innerClass = parser.getSootClass();
//
//				if (this.sootClass.isApplicationClass()) {
//					innerClass.setApplicationClass();
//				} else if (this.sootClass.isLibraryClass()) {
//					innerClass.setLibraryClass();
//				}
//				innerClass.setOuterClass(this.sootClass);
//
				i = i + nestedClassLines.size() - 1;
			}
		}
	}

	private Map<String, String> generateGenericTypMap() {
		Map<String, String> map = new HashMap<String, String>();

		if (genericReplaceTypes != null) {
			if (this.genericParameters.size() != genericReplaceTypes.size()) {
				System.err.println("Error while generating automatic generic class: " + this.className);
			}
		}

		for (int i = 0; i < this.genericParameters.size(); ++i) {
			if (genericReplaceTypes == null) {
			    map.put(this.genericParameters.get(i), "void");
				//map.put(this.genericParameters.get(i), "System.Object");
				// TODO change to System.Object
			} else {
				map.put(this.genericParameters.get(i), genericReplaceTypes.get(i));
			}
		}
		return map;
	}

	public void addClasse() {
		for (SootClass theClass : this.listNestedClasses)
			if(this.sootClass.isLibraryClass()) {
				theClass.setLibraryClass();
			} else if(this.sootClass.isApplicationClass()) {
				theClass.setApplicationClass();
			}
			
	}

	public Set<Type> getDependencies() {
		return this.dependencies;
	}

}
