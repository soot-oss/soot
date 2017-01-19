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

/**
 * Entry class for resolving and enumerating classes in a CIL disassembly file
 * 
 * @author Tobias Kussmaul
 * @author Steven Arzt
 *
 */
public class CilClassProvider implements ClassProvider {
	
	private static Map<String, CilClass> cache = new ConcurrentHashMap<String, CilClass>();
	
	
	@Override
	public ClassSource find(String className) {
		/*
		// Do we have a special class?
		if (CilDelegateClassSource.supportsClass(className))
			return new CilDelegateClassSource(className);
		if (CilTokenRefClassSource.supportsClass(className))
			return new CilTokenRefClassSource(className);
		*/

		// Have we already seen this class?
		CilClass clazz = cache.get(className);
		if (clazz != null)
			return new CilClassSource(clazz);
		
		// Parse the class name
		CilClassReference classRef = new CilClassReference(className);
		
		// Have we seen this class before in some assembly?
		String assemblyName = Cil_Utils.getAssemblyForClassName(classRef.getMangledShortName());
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
		try {
			CilFileParser parser = new CilFileParser(assemblyFile.file);
			for (CilClass clazz2 : parser.getClasses())
				cache.put(className, clazz2);
			clazz = parser.findClass(className);				
			return new CilClassSource(clazz);
		} catch (IOException e) {
			// Could not load the CIL file
			return null;
		}
	}
	
	public static Set<String> classesOfIL(File file) throws IOException {
		CilFileParser parser = new CilFileParser(file);
		Set<String> classes = new HashSet<String>(parser.getClasses().size());
		for (CilClass clazz : parser.getClasses()) {
			classes.add(clazz.getUniqueClassName());
			cache.put(clazz.getUniqueClassName(), clazz);
		}
		return classes;
	}
	
}
