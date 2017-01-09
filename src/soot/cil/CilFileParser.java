package soot.cil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import soot.Modifier;
import soot.cil.ast.CilClass;
import soot.cil.ast.CilEvent;
import soot.cil.ast.CilField;
import soot.cil.ast.CilMethod;
import soot.cil.ast.CilMethodParameter;
import soot.cil.ast.CilProperty;
import soot.cil.ast.types.CilArrayTypeRef;
import soot.cil.ast.types.CilObjectTypeRef;
import soot.cil.ast.types.CilPointerTypeRef;
import soot.cil.ast.types.CilPrimType;
import soot.cil.ast.types.CilPrimTypeRef;
import soot.cil.ast.types.CilTypeRef;
import soot.cil.parser.cilBaseListener;
import soot.cil.parser.cilLexer;
import soot.cil.parser.cilParser;
import soot.cil.parser.cilParser.AccessModifierContext;
import soot.cil.parser.cilParser.ArrayTypeContext;
import soot.cil.parser.cilParser.ClassDefContext;
import soot.cil.parser.cilParser.CompileUnitContext;
import soot.cil.parser.cilParser.EventDefContext;
import soot.cil.parser.cilParser.FieldDefContext;
import soot.cil.parser.cilParser.MethodDefContext;
import soot.cil.parser.cilParser.ParameterContext;
import soot.cil.parser.cilParser.PointerTypeContext;
import soot.cil.parser.cilParser.PrimOrTypeRefContext;
import soot.cil.parser.cilParser.PrimTypeContext;
import soot.cil.parser.cilParser.PropertyDefContext;
import soot.cil.parser.cilParser.TypeRefContext;

/**
 * Parser for the global structures in a CIL disassembly file
 * 
 * @author Steven Arzt
 *
 */
public class CilFileParser {
	
	private final File file;
	private Map<String, CilClass> classes = new HashMap<String, CilClass>();
		
	public CilFileParser(File file) throws IOException {
		this.file = file;
		parse();
	}
	
	/**
	 * Listener class for parsing cil code and transforming it into an AST
	 * 
	 * @author Steven Arzt
	 *
	 */
	private class CilListener extends cilBaseListener {
		
		@Override
		public void enterClassDef(ClassDefContext ctx) {
			super.enterClassDef(ctx);
			
			CilClass clazz = parseClassDef(ctx);
			classes.put(clazz.getClassName(), clazz);
		}
		
		private CilClass parseClassDef(ClassDefContext ctx) {
			String className = ctx.className().getText();
			int accessModifiers = 0;
			for (AccessModifierContext amc : ctx.accessModifier()) {
				if (amc.getText().equals("private"))
					accessModifiers |= Modifier.PRIVATE;
				else if (amc.getText().equals("public"))
					accessModifiers |= Modifier.PUBLIC;

			}
			
			CilClass clazz = new CilClass(className, null, false, accessModifiers);
			
			// Parse superclass and implemented interfaces
			if (ctx.classExtension() != null)
				clazz.setSuperclass(parseTypeRef(ctx.classExtension().typeRef()));
			if (ctx.classImplements() != null)
				for (TypeRefContext tref : ctx.classImplements().typeRef())
					clazz.addInterface(parseTypeRef(tref));
			
			// Parse fields
			for (FieldDefContext fld : ctx.fieldDef())
				clazz.addField(parseFieldDef(fld));
			
			// Parse events
			for (EventDefContext event : ctx.eventDef())
				clazz.addEvent(parseEventDef(event));
			
			// Parse methods
			for (MethodDefContext method : ctx.methodDef())
				clazz.addMethod(parseMethodDef(clazz, method));
			
			// Parse inner classes
			for (ClassDefContext innerClass : ctx.classDef())
				clazz.addInnerClass(parseClassDef(innerClass));
			
			// Parse properties
			for (PropertyDefContext property : ctx.propertyDef())
				clazz.addProperty(parsePropertyDef(property));
			
			return clazz;
		}
		
		private CilProperty parsePropertyDef(PropertyDefContext propertyDef) {
			String propertyName = propertyDef.propertyName().getText();
			CilProperty property = new CilProperty(propertyName);
			return property;
		}

		private CilMethod parseMethodDef(CilClass parentClass,
				MethodDefContext methodDef) {
			String methodName = methodDef.methodName().getText();
			
			// Parse the parameter list
			List<CilMethodParameter> parameters = new ArrayList<>();
			if (methodDef.parameterList() != null) {
				int i = 0;
				for (ParameterContext paramDef : methodDef.parameterList().parameter())
					parameters.add(parseMethodParameter(i++, paramDef));
			}	
			
			// TODO: nulls
			CilMethod method = new CilMethod(parentClass, methodName, parameters, null, null);
			return method;
		}

		private CilMethodParameter parseMethodParameter(int paramIdx, ParameterContext paramDef) {
			String parameterName = paramDef.parameterName().getText();
			CilTypeRef parameterType = parseTypeRef(paramDef.parameterType().primOrTypeRef());
			
			CilMethodParameter param = new CilMethodParameter(paramIdx, parameterName, parameterType);
			return param;
		}

		private CilField parseFieldDef(FieldDefContext fieldDef) {
			String fieldName = fieldDef.fieldName().getText();
			
			CilField fld = new CilField(fieldName);
			return fld;
		}
		
		private CilEvent parseEventDef(EventDefContext eventDef) {
			String eventName = eventDef.eventName().getText();
			
			CilEvent event = new CilEvent(eventName);
			return event;
		}

		private CilTypeRef parseTypeRef(PrimOrTypeRefContext typeRef) {
			if (typeRef.typeRef() != null)
				return parseTypeRef(typeRef.typeRef());
			else if (typeRef.primType() != null)
				return parseTypeRef(typeRef.primType());
			else if (typeRef.arrayType() != null)
				return parseTypeRef(typeRef.arrayType());
			else if (typeRef.pointerType() != null)
				return parseTypeRef(typeRef.pointerType());
			else
				throw new RuntimeException("Unsupported type reference");
		}
		
		private CilTypeRef parseTypeRef(PointerTypeContext pointerTypeDef) {
			CilTypeRef baseType;
			if (pointerTypeDef.primType() != null)
				baseType = parseTypeRef(pointerTypeDef.primType());
			else if (pointerTypeDef.typeRef() != null)
				baseType = parseTypeRef(pointerTypeDef.typeRef());
			else
				throw new RuntimeException("Unsupported array base type");
			
			CilPointerTypeRef arrayType = new CilPointerTypeRef(baseType);
			return arrayType;
		}

		private CilTypeRef parseTypeRef(ArrayTypeContext arrayTypeDef) {
			CilTypeRef baseType;
			if (arrayTypeDef.primType() != null)
				baseType = parseTypeRef(arrayTypeDef.primType());
			else if (arrayTypeDef.typeRef() != null)
				baseType = parseTypeRef(arrayTypeDef.typeRef());
			else
				throw new RuntimeException("Unsupported array base type");
			
			CilArrayTypeRef arrayType = new CilArrayTypeRef(baseType);
			return arrayType;
		}

		private CilTypeRef parseTypeRef(PrimTypeContext primTypeDef) {
			
			CilPrimType primType;
			if (primTypeDef.getText().equals("void"))
				primType = CilPrimType.cilVoid;
			else if (primTypeDef.getText().equals("char"))
				primType = CilPrimType.cilChar;
			else if (primTypeDef.getText().equals("string"))
				primType = CilPrimType.cilString;
			else if (primTypeDef.getText().equals("object"))
				primType = CilPrimType.cilObject;
			else if (primTypeDef.getText().equals("bool"))
				primType = CilPrimType.cilBool;
			
			else if (primTypeDef.getText().equals("int"))
				primType = CilPrimType.cilInt;
			else if (primTypeDef.getText().equals("int8"))
				primType = CilPrimType.cilInt8;
			else if (primTypeDef.getText().equals("int16"))
				primType = CilPrimType.cilInt16;
			else if (primTypeDef.getText().equals("int32"))
				primType = CilPrimType.cilInt32;

			else if (primTypeDef.getText().equals("float32"))
				primType = CilPrimType.cilFloat32;
			else if (primTypeDef.getText().equals("float64"))
				primType = CilPrimType.cilFloat64;
			
			else if (primTypeDef.getText().equals("uint"))
				primType = CilPrimType.cilUInt;
			else if (primTypeDef.getText().equals("uint8"))
				primType = CilPrimType.cilUInt8;
			else if (primTypeDef.getText().equals("uint16"))
				primType = CilPrimType.cilUInt16;
			else if (primTypeDef.getText().equals("uint32"))
				primType = CilPrimType.cilUInt32;
			else if (primTypeDef.getText().equals("uint64"))
				primType = CilPrimType.cilUInt64;

			else if (primTypeDef.getText().equals("decimal"))
				primType = CilPrimType.cilDecimal;

			else
				throw new RuntimeException("Unsupported primitive type: " + primTypeDef.getText());
			
			CilPrimTypeRef typeRef = new CilPrimTypeRef(primType);
			return typeRef;
		}

		private CilObjectTypeRef parseTypeRef(TypeRefContext typeRef) {
			String assemblyName = typeRef.assemblyName() == null ? "" : typeRef.assemblyName().getText();
			String className = typeRef.className().getText();
			
			// TODO: Parse generics list
			
			return new CilObjectTypeRef(assemblyName, className);
		}
		
	}
	
	/**
	 * Parses the current CIL disassembly file and creates a list of classes
	 * @throws IOException Thrown if the file could not be read
	 */
	private void parse() throws IOException {
		// TODO: remove comments before parsing
		
		// Run the lexer and the parser
		ANTLRFileStream fis = new ANTLRFileStream(file.getAbsolutePath());
		cilLexer lexer = new cilLexer(fis);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		cilParser cilParser = new cilParser(tokenStream);
		
		// Compile the document
		CompileUnitContext context = cilParser.compileUnit();
		cilBaseListener listener = new CilListener();

		// Invoke the listener
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(listener, context);
		/*
		BufferedReader rdr = null;
		try {
			int lineNum = 0;
			rdr = new BufferedReader(new FileReader(file));
			String line;
			List<Pair<Integer, CilClass>> classStack = new ArrayList<Pair<Integer, CilClass>>();
			int levelCounter = 0;
			while ((line = rdr.readLine()) != null) {
				try {
					line = line.trim();
					
					// Remove comments
					{
						int cmtIdx = line.indexOf("//");
						if (cmtIdx >= 0)
							line = line.substring(0, cmtIdx);
						if (line.isEmpty())
							continue;
					}
					
					// Scan for a class definition
					if (line.startsWith(".class")) {
						// Is this a generic class definition?
						CilGenericDeclarationList generics = Cil_Utils.parseGenericDeclaration(line);
						
						// Parse the parameters
						List<String> tokens = Cil_Utils.split(line, ' ');
						boolean isInterface = false;
						for (String token : tokens) {
							if (token.equals("interface"))
								isInterface = true;
							else if (!isReservedModifier(token)) {
								// This is a class name. Whatever follows afterwards
								// no longer belongs to the class name
								String className = token;
								if (!classStack.isEmpty() && classStack.get(0).getO1() == levelCounter - 1)
									className = classStack.get(0).getO2().getClassName() + "$" + className;
								
								CilClass clazz = new CilClass(className, lineNum, generics, isInterface);
								Cil_Utils.addClassToAssemblyMap(clazz.getUniqueClassName(), file.getAbsolutePath());
								classStack.add(0, new Pair<Integer, CilClass>(levelCounter, clazz));
								break;
							}
						}
					}
					
					// Keep the scan stack for nesting
					for (int i = 0; i < line.length(); i++) {
						if (line.charAt(i) == '{') {
							levelCounter++;
						}
						else if (line.charAt(i) == '}') {
							if (levelCounter == 0)
								throw new RuntimeException("Stack underrun on line " + lineNum);
							levelCounter--;
							if (!classStack.isEmpty() && classStack.get(0).getO1() == levelCounter) {
								CilClass theClass = classStack.remove(0).getO2();
								theClass.setEndLine(lineNum + 1);
								classes.put(theClass.getUniqueClassName(), theClass);
							}
						}
					}
				}
				finally {
					lineNum++;
				}
			}
			
			if (levelCounter != 0)
				throw new RuntimeException("CIL file seems to be truncated");
		}
		finally {
			if (rdr != null)
				rdr.close();
		}
		*/
	}
	
	private boolean isReservedModifier(String token) {
		return token.equals("private")
				|| token.equals("public")
				|| token.equals("sealed")
				|| token.equals("nested")
				|| token.equals("ansi")
				|| token.equals("sequential")
				|| token.equals("auto")
				|| token.equals("beforefieldinit")
				|| token.equals("abstract")
				|| token.equals("extends")
				|| token.equals("interface")
				|| token.equals(".class");
	}
	
	/**
	 * Gets the classes declared in this CIL disassembly file
	 * @return The list of classes in the current CIL disassemblx file
	 */
	public Collection<CilClass> getClasses() {
		return this.classes.values();
	}
	
	/**
	 * Gets the parsed class with the given name
	 * @param className The name of the class to get
	 * @return The parsed data structure for the class with the given name if it
	 * exists, otherwise null
	 */
	public CilClass findClass(String className) {
		return this.classes.get(className);
	}
	
}
