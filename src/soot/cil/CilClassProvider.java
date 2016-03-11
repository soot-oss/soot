package soot.cil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import soot.ClassProvider;
import soot.ClassSource;
import soot.SourceLocator;
import soot.SourceLocator.FoundFile;
import soot.cil.ast.CilClass;
import soot.cil.ast.CilClassReference;
import soot.cil.sources.CilClassSource;
import soot.cil.sources.CilDelegateClassSource;
import soot.cil.sources.CilGenericsClassSource;
import soot.cil.sources.CilTokenRefClassSource;

/**
 * Entry class for resolving and enumerating classes in a CIL disassembly file
 * 
 * @author Tobias Kussmaul
 * @author Steven Arzt
 *
 */
public class CilClassProvider implements ClassProvider {
	
	private final CilDependencyManager dependencyManager = new CilDependencyManager();
	private Map<File, CilFileParser> cache = new ConcurrentHashMap<File, CilFileParser>();
	
	/**
	 * Class for managing dependencies between classes in CIL disassembly files
	 * 
	 * @author Steven Arzt
	 *
	 */
	public class CilDependencyManager {
		
		// Map mangled class names to original references
		private Map<String, CilClassReference> classNameToReference = new HashMap<String, CilClassReference>();
		
		public CilDependencyManager() {
			
		}
		
		/**
		 * Adds a new mapping between mangled class name and original reference
		 * @param mangledClassName The mangled class name used for identifying a generic
		 * class
		 * @param originalReference The original reference containing all the generic
		 * information
		 */
		public void addReference(String mangledClassName,
				CilClassReference originalReference) {
			this.classNameToReference.put(mangledClassName, originalReference);
			
			if (mangledClassName.equals("GenericsTest.A__2__class GenericsTest.A__2__!T_int32_GenericsTest.Z"))
				System.out.println("x");
		}
		
		public CilClassReference getOriginalReference(String mangledClassName) {
			return this.classNameToReference.get(mangledClassName);
		}
		
	}
	
	@Override
	public ClassSource find(String className) {
		// Do we have a special class?
		if (CilDelegateClassSource.supportsClass(className))
			return new CilDelegateClassSource(className);
		if (CilTokenRefClassSource.supportsClass(className))
			return new CilTokenRefClassSource(className);
		
		// Do we have a generic class?
		CilClassReference originalRef = dependencyManager.getOriginalReference(className);
		if (originalRef != null)
			return new CilGenericsClassSource(originalRef);
		
		// Have we seen this class before in some assembly?
		String assemblyName = Cil_Utils.getAssemblyForClassName(className);
		if (assemblyName == null)
			return null;
		
		// We may need to patch the extension of the file
		if (!assemblyName.endsWith(".il"))
			assemblyName += ".il";
		
		// If we have full path, we need to strip it
		if (assemblyName.contains(File.separator))
			assemblyName = new File(assemblyName).getName();
		
		// Locate the IL file
		FoundFile assemblyFile = SourceLocator.v().lookupInClassPath(assemblyName);
		if (assemblyFile == null || assemblyFile.file == null)
			return null;
		
		// Once we know the class file, we can load it
		CilFileParser parser = cache.get(assemblyFile.file);
		if (parser == null) {
			try {
				parser = new CilFileParser(assemblyFile.file);
			} catch (IOException e) {
				// Could not load the CIL file
				return null;
			}
			cache.put(assemblyFile.file, parser);
		}
		
		CilClass clazz = parser.findClass(className);
		if (clazz == null)
			return null;
		return new CilClassSource(assemblyFile.file, clazz, dependencyManager);
	}
	
	public static Set<String> classesOfIL(File file) throws IOException {
		CilFileParser parser = new CilFileParser(file);
		Set<String> classes = new HashSet<String>(parser.getClasses().size());
		for (CilClass clazz : parser.getClasses())
			classes.add(clazz.getClassName());
		return classes;
	}
	
}
