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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;

import soot.dexpler.Util;
import soot.options.Options;

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
            if(Options.v().process_multiple_dex()){
                if(path.endsWith(".apk")){
                	try{
	                	ZipFile archive = new ZipFile(path);
	    				for (Enumeration<? extends ZipEntry> entries = archive.entries(); entries.hasMoreElements();) {
	    					ZipEntry entry = entries.nextElement();
	    					String entryName = entry.getName();
	    					// We are dealing with an apk file
	    					if (entryName.endsWith(".dex")){
	    						readDexFile(index, dir, entryName);
	    					}
	    				}
	    				archive.close();
                	}
                	catch(Exception ex){
                		ex.printStackTrace();
                	}
                }
            }
    		else if (path.endsWith(".apk") || path.endsWith(".dex")){
    			readDexFile(index, dir);
    		}           
        }
	}

    /**
     * Read dex file  into index.
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
     * Read dex files into index.
     */
    private void readDexFile(Map<String, File> index, File dex, String dexName) {
        try {
            for (String className : classesOfDex(dex, dexName)) {
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
		DexBackedDexFile d = DexFileFactory.loadDexFile(file, 1, false);
		for (ClassDef c : d.getClasses()) {
			String name = Util.dottedClassName(c.getType());
			classes.add(name);
		}
		return classes;
	}
	
	/**
	 * Return names of classes in the given dex/apk file.
	 *
	 * @param file
	 *            file to dex/apk file. Can be the path of a zip file.
	 * @param dexName
	 * 				a name of a given dex file
	 *
	 * @return set of class names
	 */
	public static Set<String> classesOfDex(File file, String dexName) throws IOException {
		Set<String> classes = new HashSet<String>();
		// TODO (SA): Go for API 1 because DexlibWrapper does so, but needs more attention
		DexBackedDexFile d = DexFileFactory.loadDexFile(file, dexName, 1, false);
		for (ClassDef c : d.getClasses()) {
			String name = Util.dottedClassName(c.getType());
			classes.add(name);
		}
		return classes;
	}
}
