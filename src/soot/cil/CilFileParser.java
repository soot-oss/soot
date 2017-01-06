package soot.cil;

import heros.solver.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.codegen.LexerFactory;

import soot.Modifier;
import soot.cil.ast.CilClass;
import soot.cil.ast.CilGenericDeclarationList;
import soot.cil.ast.CilTypeRef;
import soot.cil.parser.cilBaseListener;
import soot.cil.parser.cilLexer;
import soot.cil.parser.cilParser;
import soot.cil.parser.cilParser.AccessModifierContext;
import soot.cil.parser.cilParser.ClassDefContext;
import soot.cil.parser.cilParser.CompileUnitContext;
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
			
			String className = ctx.className().getText();
			int accessModifiers = 0;
			for (AccessModifierContext amc : ctx.accessModifier()) {
				if (amc.getText().equals("private"))
					accessModifiers |= Modifier.PRIVATE;
				else if (amc.getText().equals("public"))
					accessModifiers |= Modifier.PUBLIC;

			}
			
			CilClass clazz = new CilClass(className, null, false, accessModifiers);
			classes.put(className, clazz);
			
			if (ctx.classExtension() != null)
				clazz.setSuperclass(parseTypeRef(ctx.classExtension().typeRef()));
			if (ctx.classImplements() != null)
				for (TypeRefContext tref : ctx.classImplements().typeRef())
					clazz.addInterface(parseTypeRef(tref));
		}

		private CilTypeRef parseTypeRef(TypeRefContext typeRef) {
			String assemblyName = typeRef.assemblyName().getText();
			String className = typeRef.className().getText();
			
			// TODO: Parse generics list
			
			return new CilTypeRef(assemblyName, className);
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
