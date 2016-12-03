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
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
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
		
		// Process the classpath extensions
		if (SourceLocator.v().getDexClassPathExtensions() != null) {
			buildDexIndex(SourceLocator.v().dexClassIndex(),
					new ArrayList<>(SourceLocator.v().getDexClassPathExtensions()));
			SourceLocator.v().clearDexClassPathExtensions();
		}

		File file = index.get(className);
		if (file == null)
			return null;

		return new DexClassSource(className, file);
	}

	private List<File> getAllDexFiles(String path){
		Queue<File> toVisit = new ArrayDeque<File>();
		Set<File> visited = new HashSet<File>();
		List<File> ret = new ArrayList<File>();
		toVisit.add(new File(path));
		while(!toVisit.isEmpty()){
			File cur = toVisit.poll();
			if(visited.contains(cur))
				continue;
			visited.add(cur);
			if(cur.isDirectory()){
				toVisit.addAll(Arrays.asList(cur.listFiles()));
			}else if(cur.isFile() && cur.getName().endsWith(".dex")){
				ret.add(cur);
			}
		}
		return ret;
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
			List<File> allDexFiles = getAllDexFiles(path);
			if(!allDexFiles.isEmpty()){//path is directory containing dex files or a single dex file
				for(File dexFile : allDexFiles){
					readDexFile(index, dexFile);
				}
			}else{//path is directory containing no dex files, a apk, jar, or zip
				File file = new File(path);
				if(file.isFile()){
					if(file.getName().endsWith(".apk") || file.getName().endsWith(".jar") || file.getName().endsWith(".zip")){
						//check if the archive contains dex files and record the names if there are multiple
						Set<String> entryNames = new HashSet<String>();
						ZipFile archive = null;
						try{
							archive = new ZipFile(file);
							for (Enumeration<? extends ZipEntry> entries = archive.entries(); entries.hasMoreElements();) {
								ZipEntry entry = entries.nextElement();
		    					String entryName = entry.getName();
		    					if(entryName.endsWith(".dex")) {
		    						if (Options.v().process_multiple_dex() || entryName.equals("classes.dex"))
		    							entryNames.add(entryName);
		    					}
							}
						}catch(Exception e){
							throw new RuntimeException(e);
						}finally{
							try{
								if(archive != null){
									archive.close();
									archive = null;
								}
							}catch(Throwable e) {}
						}
						if(!entryNames.isEmpty()){
							if(Options.v().process_multiple_dex()){
								for(String entryName : entryNames){
									readDexFile(index, file, entryName);
								}
							}else{
								readDexFile(index, file);
							}
						}
					}
				}
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
		return classesOfDex(file, null);
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
		int api = Scene.v().getAndroidAPIVersion();
		DexBackedDexFile d = dexName != null  
				? DexFileFactory.loadDexEntry(file, dexName, true, Opcodes.forApi(api))  
				: DexFileFactory.loadDexFile(file, Opcodes.forApi(api));  
		for (ClassDef c : d.getClasses()) {
			String name = Util.dottedClassName(c.getType());
			classes.add(name);
		}
		return classes;
	}
}
