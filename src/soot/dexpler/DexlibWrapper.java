/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 *
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

package soot.dexpler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;

import soot.ArrayType;
import soot.PrimType;
import soot.Scene;
import soot.SootClass;
import soot.SootResolver;
import soot.Type;
import soot.VoidType;
import soot.javaToJimple.IInitialResolver.Dependencies;
import soot.options.Options;

/**
 * DexlibWrapper provides an entry point to the dexlib library from the smali
 * project. Given a dex file, it will use dexlib to retrieve all classes for
 * further processing A call to getClass retrieves the specific class to analyze
 * further.
 *
 */
public class DexlibWrapper {

	static {
		Set<String> systemAnnotationNamesModifiable = new HashSet<String>();
		// names as defined in the ".dex - Dalvik Executable Format" document
		systemAnnotationNamesModifiable
				.add("dalvik.annotation.AnnotationDefault");
		systemAnnotationNamesModifiable.add("dalvik.annotation.EnclosingClass");
		systemAnnotationNamesModifiable
				.add("dalvik.annotation.EnclosingMethod");
		systemAnnotationNamesModifiable.add("dalvik.annotation.InnerClass");
		systemAnnotationNamesModifiable.add("dalvik.annotation.MemberClasses");
		systemAnnotationNamesModifiable.add("dalvik.annotation.Signature");
		systemAnnotationNamesModifiable.add("dalvik.annotation.Throws");
		systemAnnotationNames = Collections
				.unmodifiableSet(systemAnnotationNamesModifiable);
	}

	private List<DexFile> dexFiles;
	private final DexClassLoader dexLoader = new DexClassLoader();
	private final Map<String, ClassDef> classesToDefItems = new HashMap<String, ClassDef>();
	
	private final static Set<String> systemAnnotationNames;

	private final File inputDexFile;

	/**
	 * Construct a DexlibWrapper from a dex file and stores its classes
	 * referenced by their name. No further process is done here.
	 *
	 * @param inputDexFileName
	 *            the dex file.
	 */

	public DexlibWrapper(File inputDexFile) {
		this.inputDexFile = inputDexFile;
		this.dexFiles = new ArrayList<DexFile>();
	}

	public void initialize() {
		ZipFile archive = null;
		try {
			int api = 24; // TODO: this matters now so it should be a soot option
			if(Options.v().process_multiple_dex() && (inputDexFile.getName().endsWith(".apk") || 
					inputDexFile.getName().endsWith(".zip") || inputDexFile.getName().endsWith(".jar"))){
	            archive = new ZipFile(inputDexFile);
				for (Enumeration<? extends ZipEntry> entries = archive.entries(); entries.hasMoreElements();) {
					ZipEntry entry = entries.nextElement();
					String entryName = entry.getName();
					// We are dealing with an apk file
					if (entryName.endsWith(".dex")){
						this.dexFiles.add(DexFileFactory.loadDexEntry(inputDexFile, entryName, true, Opcodes.forApi(api)));
					}
				}
        	}
        	else{
        		this.dexFiles.add(DexFileFactory.loadDexFile(inputDexFile, Opcodes.forApi(api)));
        	}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			try{
				if(archive != null)
					archive.close();
			}catch(Throwable t) {}
		}

		for(DexFile dexFile: this.dexFiles){
			for (ClassDef defItem : dexFile.getClasses()) {
				String forClassName = Util.dottedClassName(defItem.getType());
				classesToDefItems.put(forClassName, defItem);
			}
		}
		
		for(DexFile dexFile: this.dexFiles){
			if (dexFile instanceof DexBackedDexFile) {
				DexBackedDexFile dbdf = (DexBackedDexFile) dexFile;
				for (int i = 0; i < dbdf.getTypeCount(); i++) {
					String t = dbdf.getType(i);
	
					Type st = DexType.toSoot(t);
					if (st instanceof ArrayType) {
						st = ((ArrayType) st).baseType;
					}
					Debug.printDbg("Type: ", t, " soot type:", st);
					String sootTypeName = st.toString();
					if (!Scene.v().containsClass(sootTypeName)) {
						if (st instanceof PrimType || st instanceof VoidType
								|| systemAnnotationNames.contains(sootTypeName)) {
							// dex files contain references to the Type IDs of void
							// / primitive types - we obviously do not want them to
							// be resolved
							/*
							 * dex files contain references to the Type IDs of the
							 * system annotations. They are only visible to the
							 * Dalvik VM (for reflection, see
							 * vm/reflect/Annotations.cpp), and not to the user - so
							 * we do not want them to be resolved.
							 */
							continue;
						}
						SootResolver.v().makeClassRef(sootTypeName);
					}
					SootResolver.v().resolveClass(sootTypeName,
							SootClass.SIGNATURES);
				}
			} else {
				System.out
						.println("Warning: DexFile not instance of DexBackedDexFile! Not resolving types!");
				System.out.println("type: " + dexFile.getClass());
			}
		}
	}

	public Dependencies makeSootClass(SootClass sc, String className) {
		if (Util.isByteCodeClassName(className)) {
			className = Util.dottedClassName(className);
		}

		for(DexFile dexFile: this.dexFiles){
			ClassDef defItem = classesToDefItems.get(className);			
			return dexLoader.makeSootClass(sc, defItem, dexFile);
		}
		
		throw new RuntimeException("Error: class not found in DEX files: "
					+ className);
	}

}
