package soot.cil;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import soot.ClassProvider;
import soot.ClassSource;
import soot.SourceLocator;
import soot.SourceLocator.FoundFile;
import soot.cil.CilFileParser.CilClass;

/**
 * 
 * @author Tobias Kussmaul
 *
 */
public class CilClassProvider implements ClassProvider {
	
	private Map<File, CilFileParser> cache = new ConcurrentHashMap<File, CilFileParser>();

	@Override
	public ClassSource find(String className) {
		// Do we have a special class?
		if (CilDelegateClassSource.supportsClass(className))
			return new CilDelegateClassSource(className);
		
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
		return new CilClassSource(assemblyFile.file, clazz);
	}
	
	public static Set<String> classesOfIL(File file) throws IOException {
		CilFileParser parser = new CilFileParser(file);
		Set<String> classes = new HashSet<String>(parser.getClasses().size());
		for (CilClass clazz : parser.getClasses())
			classes.add(clazz.getClassName());
		return classes;
	}
	
}
