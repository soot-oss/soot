/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
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

package soot;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import soot.JavaClassProvider.JarException;
import soot.options.Options;

/** Provides utility methods to retrieve an input stream for a class name, given
 * a classfile, or jimple or baf output files. */
public class SourceLocator
{
    public SourceLocator( Singletons.Global g ) {}
    public static SourceLocator v() { return G.v().soot_SourceLocator(); }

    protected Set<ClassLoader> additionalClassLoaders = new HashSet<ClassLoader>();
	protected Set<String> classesToLoad;
    
    /** Given a class name, uses the soot-class-path to return a ClassSource for the given class. */
	public ClassSource getClassSource(String className) 
    {
		if(classesToLoad==null) {
			classesToLoad = new HashSet<String>();
			classesToLoad.addAll(Scene.v().getBasicClasses());
			for(SootClass c: Scene.v().getApplicationClasses()) {
				classesToLoad.add(c.getName());
			}
		}
    	
        if( classPath == null ) {
            classPath = explodeClassPath(Scene.v().getSootClassPath());
        }
        if( classProviders == null ) {
            setupClassProviders();
        }
        JarException ex = null;
        for (ClassProvider cp : classProviders) {
            try {
	        	ClassSource ret = cp.find(className);
	            if( ret != null ) return ret;
            } catch(JarException e) {
            	ex = e;
            }
        }
        if(ex!=null) throw ex;
        for(final ClassLoader cl: additionalClassLoaders) {
            try {
            	ClassSource ret = new ClassProvider() {
					
					public ClassSource find(String className) {
				        String fileName = className.replace('.', '/') + ".class";
						InputStream stream = cl.getResourceAsStream(fileName);
						if(stream==null) return null;
						return new CoffiClassSource(className, stream, fileName, null);
					}

            	}.find(className);
	            if( ret != null ) return ret;
            } catch(JarException e) {
            	ex = e;
            }
        }
        if(ex!=null) throw ex;
        if(className.startsWith("soot.rtlib.tamiflex.")) {
	        String fileName = className.replace('.', '/') + ".class";
        	InputStream stream = getClass().getClassLoader().getResourceAsStream(fileName);
        	if(stream!=null) {
				return new CoffiClassSource(className, stream, fileName, null);
        	}
        }
        return null;
    }
    
    public void additionalClassLoader(ClassLoader c) {
    	additionalClassLoaders.add(c);
    }

    private void setupClassProviders() {
        classProviders = new LinkedList<ClassProvider>();
        switch( Options.v().src_prec() ) {
            case Options.src_prec_class:
                classProviders.add(new CoffiClassProvider());
                classProviders.add(new JimpleClassProvider());
                classProviders.add(new JavaClassProvider());
                break;
            case Options.src_prec_only_class:
                classProviders.add(new CoffiClassProvider());
                break;
            case Options.src_prec_java:
                classProviders.add(new JavaClassProvider());
                classProviders.add(new CoffiClassProvider());
                classProviders.add(new JimpleClassProvider());
                break;
            case Options.src_prec_jimple:
                classProviders.add(new JimpleClassProvider());
                classProviders.add(new CoffiClassProvider());
                classProviders.add(new JavaClassProvider());
                break;
            case Options.src_prec_apk:
                classProviders.add(new DexClassProvider());
				classProviders.add(new CoffiClassProvider());
				classProviders.add(new JavaClassProvider());
				classProviders.add(new JimpleClassProvider());
                break;
            default:
                throw new RuntimeException("Other source precedences are not currently supported.");
        }
    }

	private List<ClassProvider> classProviders;
    public void setClassProviders( List<ClassProvider> classProviders ) {
        this.classProviders = classProviders;
    }

    private List<String> classPath;
    public List<String> classPath() { return classPath; }
    public void invalidateClassPath() {
        classPath = null;
    }

    private List<String> sourcePath;
    public List<String> sourcePath() {
        if( sourcePath == null ) {
            sourcePath = new ArrayList<String>();
            for (String dir : classPath) {
                if( !isArchive(dir) ) sourcePath.add(dir);
            }
        }
        return sourcePath;
    }

    private boolean isArchive(String path) {
        File f = new File(path);
        if (f.isFile() && f.canRead()) {
            if (path.endsWith(".zip") || path.endsWith(".jar") || path.endsWith(".apk")) {
                return true;
            } else {
            G.v().out.println("Warning: the following soot-classpath entry is not a supported archive file (must be .zip, .jar or .apk): " + path);
            }
        }
        return false;
    }

	public List<String> getClassesUnder(String aPath) {
		List<String> classes = new ArrayList<String>();

		if (isArchive(aPath)) {
			List<String> inputExtensions = new ArrayList<String>(3);
			inputExtensions.add(".class");
			inputExtensions.add(".jimple");

			try {
				ZipFile archive = new ZipFile(aPath);

				boolean hasClassesDotDex = false;
				for (Enumeration<? extends ZipEntry> entries = archive.entries(); entries.hasMoreElements();) {
					ZipEntry entry = entries.nextElement();
					String entryName = entry.getName();
					// We are dealing with an apk file
					if (entryName.equals("classes.dex")) {
						hasClassesDotDex = true;
						classes.addAll(DexClassProvider.classesOfDex(new File(aPath)));
					}
				}

				for (Enumeration<? extends ZipEntry> entries = archive.entries(); entries.hasMoreElements();) {
					ZipEntry entry = entries.nextElement();
					String entryName = entry.getName();
					int extensionIndex = entryName.lastIndexOf('.');
					if (extensionIndex >= 0) {
						String entryExtension = entryName.substring(extensionIndex);
						if (inputExtensions.contains(entryExtension)) {
							entryName = entryName.substring(0, extensionIndex);
							entryName = entryName.replace('/', '.');
							if (!hasClassesDotDex) {
								classes.add(entryName);
							} else {
								G.v().out.println("Warning: Since archive contains 'classes.dex', the following entry is not loaded: "
												+ entry.getName());
							}
						}
					}
				}
			} catch (IOException e) {
				G.v().out.println("Error reading " + aPath + ": " + e.toString());
				throw new CompilationDeathException(CompilationDeathException.COMPILATION_ABORTED);
			}
		} else {
			File file = new File(aPath);

			File[] files = file.listFiles();
			if (files == null) {
				files = new File[1];
				files[0] = file;
			}

			for (File element : files) {
				if (element.isDirectory()) {
					List<String> l = getClassesUnder(aPath + File.separatorChar + element.getName());
					Iterator<String> it = l.iterator();
					while (it.hasNext()) {
						String s = it.next();
						classes.add(element.getName() + "." + s);
					}
				} else {
					String fileName = element.getName();

					if (fileName.endsWith(".class")) {
						int index = fileName.lastIndexOf(".class");
						classes.add(fileName.substring(0, index));
					}

					if (fileName.endsWith(".jimple")) {
						int index = fileName.lastIndexOf(".jimple");
						classes.add(fileName.substring(0, index));
					}

					if (fileName.endsWith(".java")) {
						int index = fileName.lastIndexOf(".java");
						classes.add(fileName.substring(0, index));
					}
					if (fileName.endsWith(".dex")) {
						try {
							classes.addAll(DexClassProvider.classesOfDex(element));
						} catch (IOException e) { /* Ignore unreadable files */
						}
					}
				}
			}
		}
		return classes;
	}

    public String getFileNameFor(SootClass c, int rep) {
        if (rep == Options.output_format_none)
            return null;

        StringBuffer b = new StringBuffer();

        if( !Options.v().output_jar() ) {
            b.append(getOutputDir());
        }

        if ((b.length() > 0) && (b.charAt(b.length() - 1) != File.separatorChar))
            b.append(File.separatorChar);

        if (rep != Options.output_format_dava) {
            if(rep == Options.output_format_class) {
                b.append(c.getName().replace('.', File.separatorChar));
            } else if(rep == Options.output_format_template) {
                b.append(c.getName().replace('.', '_'));
                b.append("_Maker");
            } else {
                b.append(c.getName());
            }
            b.append(getExtensionFor(rep));

            return b.toString();
        }

        return getDavaFilenameFor(c, b);
    }
    
	private String getDavaFilenameFor(SootClass c, StringBuffer b) {
		b.append("dava");
        b.append(File.separatorChar);
        {
            String classPath = b.toString() + "classes";
            File dir = new File(classPath);

            if (!dir.exists())
                try {
                    dir.mkdirs();
                } catch (SecurityException se) {
                    G.v().out.println("Unable to create " + classPath);
                    throw new CompilationDeathException(CompilationDeathException.COMPILATION_ABORTED);
                }
        }

        b.append("src");
        b.append(File.separatorChar);

        String fixedPackageName = c.getJavaPackageName();
        if (fixedPackageName.equals("") == false) {
            b.append(fixedPackageName.replace('.', File.separatorChar));
            b.append(File.separatorChar);
        }

        {
            String path = b.toString();
            File dir = new File(path);

            if (!dir.exists())
                try {
                    dir.mkdirs();
                } catch (SecurityException se) {
                    G.v().out.println("Unable to create " + path);
                    throw new CompilationDeathException(CompilationDeathException.COMPILATION_ABORTED);
                }
        }

        b.append(c.getShortJavaStyleName());
        b.append(".java");

        return b.toString();
	}

    /* This is called after sootClassPath has been defined. */
    public Set<String> classesInDynamicPackage(String str) {
        HashSet<String> set = new HashSet<String>(0);
        StringTokenizer strtok = new StringTokenizer(
                Scene.v().getSootClassPath(), String.valueOf(File.pathSeparatorChar));
        while (strtok.hasMoreTokens()) {
            String path = strtok.nextToken();

            // For jimple files
            List<String> l = getClassesUnder(path);
            for (String filename : l) {
                if (filename.startsWith(str))
                    set.add(filename);
            }

            // For class files;
            path = path + File.pathSeparatorChar;
            StringTokenizer tokenizer = new StringTokenizer(str, ".");
            while (tokenizer.hasMoreTokens()) {
                path = path + tokenizer.nextToken();
                if (tokenizer.hasMoreTokens())
                    path = path + File.pathSeparatorChar;
            }
            l = getClassesUnder(path);
            for (String string : l)
				set.add(str + "." + string);
        }
        return set;
    }

    public String getExtensionFor(int rep) {
        switch (rep) {
            case Options.output_format_baf:      return ".baf";
            case Options.output_format_b:        return ".b";
            case Options.output_format_jimple:   return ".jimple";
            case Options.output_format_jimp:     return ".jimp";
            case Options.output_format_shimple:  return ".shimple";
            case Options.output_format_shimp:    return ".shimp";
            case Options.output_format_grimp:    return ".grimp";
            case Options.output_format_grimple:  return ".grimple";
            case Options.output_format_class:    return ".class";
            case Options.output_format_dava:     return ".java";
            case Options.output_format_jasmin:   return ".jasmin";
            case Options.output_format_xml:      return ".xml";
            case Options.output_format_template: return ".java";
            default:
                throw new RuntimeException();
        }
    }

    public String getOutputDir() {
        String ret = Options.v().output_dir();
        if( ret.length() == 0 ) {
        	ret = "sootOutput";
	        if (Options.v().output_jar())
	        	ret += File.separatorChar + "out.jar";
        }
        File dir = new File(ret);

        if (!dir.exists()) {
            try {
                if( !Options.v().output_jar() ) {
                    dir.mkdirs();
                }
            } catch (SecurityException se) {
                G.v().out.println("Unable to create " + ret);
                throw new CompilationDeathException(CompilationDeathException.COMPILATION_ABORTED);
            }
        }
        return ret;
    }

    /** Explodes a class path into a list of individual class path entries. */
    public static List<String> explodeClassPath( String classPath ) {
        List<String> ret = new ArrayList<String>();

        StringTokenizer tokenizer = 
            new StringTokenizer(classPath, File.pathSeparator);
        while( tokenizer.hasMoreTokens() ) {
            String originalDir = tokenizer.nextToken();
            String canonicalDir;
            try {
                canonicalDir = new File(originalDir).getCanonicalPath();
                ret.add(canonicalDir);
            } catch( IOException e ) {
                throw new CompilationDeathException( "Couldn't resolve classpath entry "+originalDir+": "+e );
            }
        }
        return ret;
    }
    public static class FoundFile {
        FoundFile( ZipFile zipFile, ZipEntry entry ) {
            this.zipFile = zipFile;
            this.entry = entry;
        }
        FoundFile( File file ) {
            this.file = file;
        }
        public File file;
        public ZipFile zipFile;
        public ZipEntry entry;
        public InputStream inputStream() {
            try {
                if( file != null ) return new FileInputStream(file);
                return doJDKBugWorkaround(zipFile.getInputStream(entry),
                        entry.getSize());
            } catch( IOException e ) {
                throw new RuntimeException( "Caught IOException "+e );
            }
        }
        public File inputFile(){
            if (file != null)
                return file;
            else
                return new File(zipFile.getName());
        }
    }

    private static InputStream doJDKBugWorkaround(InputStream is, long size) throws IOException {
	
	int sz = (int) size;
	byte[] buf = new byte[sz];					
				
				    
	final int N = 1024;
	int ln = 0;
	int count = 0;
	while (sz > 0 &&  
	       (ln = is.read(buf, count, Math.min(N, sz))) != -1) {
	    count += ln;
	    sz -= ln;
	}
	return  new ByteArrayInputStream(buf);		
    }


    /** Searches for a file with the given name in the exploded classPath. */
    public FoundFile lookupInClassPath( String fileName ) {
        for (String dir : classPath) {
            FoundFile ret;
            if(isArchive(dir)) {
                ret = lookupInArchive(dir, fileName);
            } else {
                ret = lookupInDir(dir, fileName);
            }
            if( ret != null ) return ret;
        }
        return null;
    }
    private FoundFile lookupInDir(String dir, String fileName) {
        File f = new File( dir+File.separatorChar+fileName );
        if( f.canRead() ) {
            return new FoundFile(f);
        }
        return null;
    }
    private FoundFile lookupInArchive(String archivePath, String fileName) {
        try {
            ZipFile archive = new ZipFile(archivePath);
            ZipEntry entry = archive.getEntry(fileName);
            if( entry == null ) return null;
            return new FoundFile(archive, entry);
        } catch( IOException e ) {
            throw new RuntimeException("Caught IOException " + e + " looking in archive file " + archivePath + " for file " + fileName);
        }
    }
    private HashMap<String, String> sourceToClassMap;

    public HashMap<String, String> getSourceToClassMap(){
        return sourceToClassMap;
    }
    public void setSourceToClassMap(HashMap<String, String> map){
        sourceToClassMap = map;
    }
    public void addToSourceToClassMap(String key, String val) {
        sourceToClassMap.put(key, val);
    }
    /** Returns the name of the class in which the (possibly inner) class
     * className appears. */
    public String getSourceForClass( String className ) {
        String javaClassName = className;
        if (className.indexOf("$") != -1) {
            // class is an inner class and will be in
            // Outer of Outer$Inner
            javaClassName = className.substring(0, className.indexOf("$"));
            //System.out.println("cut off inner class: look for: "+javaClassName);
        }
        // always do this because an inner class could be in a class
        // thats in the map
        if (sourceToClassMap != null) {
            //System.out.println("in source map: "+sourceToClassMap);
            if (sourceToClassMap.get(javaClassName) != null) {
                javaClassName = sourceToClassMap.get(javaClassName);
            }
        }
        return javaClassName;
    }

    /**
     * The index that maps classes to the files they are defined in.
     * This is necessary because a dex file can hold multiple classes.
     */
    private Map<String, File> dexClassIndex;

    /**
     * Return the dex class index that maps class names to files
     *
     * @return the index
     */
    public Map<String, File> dexClassIndex() {
        return dexClassIndex;
    }

    /**
     * Set the dex class index
     *
     * @param index the index
     */
    public void setDexClassIndex(Map<String, File> index) {
    	dexClassIndex = index;
    }
}

