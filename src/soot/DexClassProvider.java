package soot;
/* Soot - a J*va Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */



import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;

import soot.dexpler.Util;

/**
 * Looks for a dex file which includes the definition of a class.
 * 
 */
public class DexClassProvider implements ClassProvider {

	/**
	 * Provides the DexClassSource for the class.
	 * 
	 * @param className
	 *            class to provide.
	 * 
	 * @return a DexClassSource that defines the className named class.
	 */
	public ClassSource find(String className) {
		Map<String, File> index = SourceLocator.v().dexClassIndex();
		if (index == null) {
			index = new HashMap<String, File>();
			buildDexIndex(index, SourceLocator.v().classPath());
			SourceLocator.v().setDexClassIndex(index);
		}

		File file = index.get(className);
		if (file == null)
			return null;

		return new DexClassSource(className, file);
	}


	/**
	 * Build index of ClassName-to-File mappings.
	 *
	 * @param index
	 *            map to insert mappings into
	 * @param classPath
	 *            paths to index
	 */
	private void buildDexIndex(Map<String, File> index, List<String> classPath) {
		for (String path : classPath) {
			File dir = new File(path);
            File[] dexs = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(".dex");
                }
            });
            if (dexs != null)
                for (File dex : dexs)
                    readDexFile(index, dex);
            if (path.endsWith(".dex") || path.endsWith(".apk"))
                readDexFile(index, dir);
        }
    }

    /**
     * Read dex filen into index.
     */
    private void readDexFile(Map<String, File> index, File dex) {
        try {
            for (String className : classesOfDex(dex)) {
                index.put(className, dex);
            }
        } catch (IOException e) { 
          G.v().out.println("Warning: IO error while processing dex file '"+ dex +"'");
          G.v().out.println("Exception: "+ e);
        } catch (Exception e) {
          G.v().out.println("Warning: exception while processing dex file '"+ dex +"'");
          G.v().out.println("Exception: "+ e);
        }
    }


	/**
	 * Return names of classes in dex/apk file.
	 *
	 * @param file
	 *            file to dex/apk file. Can be the path of a zip file.
	 *
	 * @return set of class names
	 */
	public static Set<String> classesOfDex(File file) throws IOException {
		Set<String> classes = new HashSet<String>();
		// TODO (SA): Go for API 1 because DexlibWrapper does so, but needs more attention
		DexBackedDexFile d = DexFileFactory.loadDexFile(file, 1);
		for (ClassDef c : d.getClasses()) {
			String name = Util.dottedClassName(c.getType());
			classes.add(name);
		}
		return classes;
	}
}
