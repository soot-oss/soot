package soot.cil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import soot.ClassSource;
import soot.PrimType;
import soot.SootClass;
import soot.Type;
import soot.VoidType;
import soot.cil.ast.CilClass;
import soot.javaToJimple.IInitialResolver.Dependencies;
import soot.options.Options;

/**
 * CIL class source
 * 
 * @author Tobias Kussmaul
 *
 */

class CilClassSource extends ClassSource {
	private final File file;
	private final CilClass clazz;
	boolean genericClass = false;

	private Dependencies deps = new Dependencies();

	public CilClassSource(File file, CilClass clazz) {
		super(clazz.getClassName());
		this.file = file;
		this.clazz = clazz;
	}
	
	public boolean isGenericClass() {
		return this.genericClass;
	}

	@Override
	public Dependencies resolve(SootClass sc) {
		// Make sure that we're not loading the wrong class
		if (!sc.getName().equals(clazz.getClassName()))
			throw new RuntimeException("Class name mismatch");
		
		if (this.isGenericClass()) {
			// If this is a generic class, we need to generate a fake class
			// and overwrite all the methods that use the generic.
			// TODO
			return new Dependencies();
		}

		// Load the class
		List<String> lines = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			lines = extractClassDefinition(br);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		// If we don't have that class, we abort
		if (lines == null || lines.isEmpty()) {
			if (!Options.v().allow_phantom_refs())
				throw new RuntimeException("Could not load class " + sc.getName());
			sc.setPhantomClass();
			return deps;
		}
		
		Cil_ClassParser parser = new Cil_ClassParser(lines, sc, false);
		parser.parse();

		Set<Type> depen = parser.getDependencies();		
		removePrimitiveTypesFromDependencies(depen);

		deps.typesToSignature.addAll(depen);
		return deps;
	}
	
	/**
	 * Gets the code region that defines the given class
	 * @param className The class to get the code region for
	 * @param br The reader from which to load the class definition
	 * @return The block of code that defines the class with the given name in
	 * the file that is currently open in the given reader.
	 * @throws IOException Thrown if the code could not read from the buffer
	 */
	private List<String> extractClassDefinition(BufferedReader br) throws IOException {
		List<String> lines = new ArrayList<String>();
		int lineNum = 0;
		String line;
		while ((line = br.readLine()) != null) {
			if (lineNum >= clazz.getStartLine() && lineNum <= clazz.getEndLine())
				lines.add(line);
			if (lineNum > clazz.getEndLine())
				break;
			lineNum++;
		}
		return lines;
	}

	private void removePrimitiveTypesFromDependencies(Set<Type> depen) {
		for (Iterator<Type> tpIt = depen.iterator(); tpIt.hasNext(); ) {
			Type tp = tpIt.next();
			if (tp instanceof PrimType || tp instanceof VoidType)
				tpIt.remove();
		}
	}
	
}
