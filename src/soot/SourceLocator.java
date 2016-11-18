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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import soot.JavaClassProvider.JarException;
import soot.asm.AsmClassProvider;
import soot.options.Options;

/** Provides utility methods to retrieve an input stream for a class name, given
 * a classfile, or jimple or baf output files. */
public class SourceLocator
{
    public SourceLocator( Singletons.Global g ) {}
    public static SourceLocator v() { return G.v().soot_SourceLocator(); }

    protected Set<ClassLoader> additionalClassLoaders = new HashSet<ClassLoader>();
	protected Set<String> classesToLoad;
	
	private enum ClassSourceType { jar, zip, apk, dex, directory, unknown };
    
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
						return new CoffiClassSource(className, stream, fileName);
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
	        ClassLoader cl = getClass().getClassLoader();
	        if (cl == null)
	        	return null;
        	InputStream stream = cl.getResourceAsStream(fileName);
        	if(stream!=null) {
				return new CoffiClassSource(className, stream, fileName);
        	}
        }
        return null;
    }
    
    public void additionalClassLoader(ClassLoader c) {
    	additionalClassLoaders.add(c);
    }

    private void setupClassProviders() {
        classProviders = new LinkedList<ClassProvider>();
        ClassProvider classFileClassProvider = Options.v().coffi() ? new CoffiClassProvider() : new AsmClassProvider();
		switch( Options.v().src_prec() ) {
            case Options.src_prec_class:
                classProviders.add(classFileClassProvider);
                classProviders.add(new JimpleClassProvider());
                classProviders.add(new JavaClassProvider());
                break;
            case Options.src_prec_only_class:
                classProviders.add(classFileClassProvider);
                break;
            case Options.src_prec_java:
                classProviders.add(new JavaClassProvider());
                classProviders.add(classFileClassProvider);
                classProviders.add(new JimpleClassProvider());
                break;
            case Options.src_prec_jimple:
                classProviders.add(new JimpleClassProvider());
                classProviders.add(classFileClassProvider);
                classProviders.add(new JavaClassProvider());
                break;
            case Options.src_prec_apk:
                classProviders.add(new DexClassProvider());
				classProviders.add(classFileClassProvider);
				classProviders.add(new JavaClassProvider());
				classProviders.add(new JimpleClassProvider());
                break;
            case Options.src_prec_apk_c_j:
                classProviders.add(new DexClassProvider());
				classProviders.add(classFileClassProvider);
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
        dexClassIndex = null;
    }

    private List<String> sourcePath;
    public List<String> sourcePath() {
        if( sourcePath == null ) {
            sourcePath = new ArrayList<String>();
            for (String dir : classPath) {
            	ClassSourceType cst = getClassSourceType(dir);
                if( cst != ClassSourceType.apk
                		&& cst != ClassSourceType.jar
                		&& cst != ClassSourceType.zip)
                	sourcePath.add(dir);
            }
        }
        return sourcePath;
    }
    
    private LoadingCache<String, ClassSourceType> pathToSourceType = CacheBuilder.newBuilder()
    		.initialCapacity(60)
    		.maximumSize(500)
    		.softValues()
    		.concurrencyLevel(Runtime.getRuntime().availableProcessors())
    		.build(
    				new CacheLoader<String, ClassSourceType>(){
						@Override
						public ClassSourceType load(String path) throws Exception {
							File f = new File(path);
							if(!f.exists() && !Options.v().ignore_classpath_errors())
								throw new Exception("Error: The path '" + path + "' does not exist.");
							if(!f.canRead() && !Options.v().ignore_classpath_errors())
								throw new Exception("Error: The path '" + path + "' exists but is not readable.");
							if(f.isFile()) {
								if (path.endsWith(".zip"))
					                return ClassSourceType.zip;
					            else if (path.endsWith(".jar"))
					                return ClassSourceType.jar;
					            else if (path.endsWith(".apk"))
					                return ClassSourceType.apk;
					            else if (path.endsWith(".dex"))
					                return ClassSourceType.dex;
					            else
					                return ClassSourceType.unknown;
							}
							return ClassSourceType.directory;
						}
    				}
    );
    
    private ClassSourceType getClassSourceType(String path) {
    	try {
			return pathToSourceType.get(path);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
    
    public List<String> getClassesUnder(String aPath) {
    	return getClassesUnder(aPath, "");
    }
    
    private List<String> getClassesUnder(String aPath, String prefix) {
		List<String> classes = new ArrayList<String>();
		ClassSourceType cst = getClassSourceType(aPath);
		
		// Get the dex file from an apk
		if (cst == ClassSourceType.apk) {
			ZipFile archive = null;
			try {
				archive = new ZipFile(aPath);
				for (Enumeration<? extends ZipEntry> entries = archive.entries(); entries.hasMoreElements();) {
					ZipEntry entry = entries.nextElement();
					String entryName = entry.getName();
					// We are dealing with an apk file
					if (entryName.endsWith(".dex"))
						if (Options.v().process_multiple_dex() || entryName.equals("classes.dex"))
							classes.addAll(DexClassProvider.classesOfDex(new File(aPath), entryName));
				}		
			} catch (IOException e) {
				throw new CompilationDeathException("Error reasing archive '" + aPath + "'",e);
			}finally{
				try{
					if(archive != null)
						archive.close();
				}catch(Throwable t) {}
			}
		}
		// Directly load a dex file
		else if (cst == ClassSourceType.dex) {
			try {
				classes.addAll(DexClassProvider.classesOfDex(new File(aPath)));
			} catch (IOException e) {
				throw new CompilationDeathException("Error reasing '" + aPath + "'",e);
			}
		}
		// load Java class files from ZIP and JAR
		else if (cst == ClassSourceType.jar || cst == ClassSourceType.zip) {
			Set<String> dexEntryNames = new HashSet<String>();
			ZipFile archive = null;
			try {
				archive = new ZipFile(aPath);
				for (Enumeration<? extends ZipEntry> entries = archive.entries(); entries.hasMoreElements();) {
					ZipEntry entry = entries.nextElement();
					String entryName = entry.getName();
					if(entryName.endsWith(".class") || entryName.endsWith(".jimple")){
						int extensionIndex = entryName.lastIndexOf('.');
						entryName = entryName.substring(0, extensionIndex);
						entryName = entryName.replace('/', '.');
						classes.add(prefix + entryName);
					}
					else if(entryName.endsWith(".dex")){
						dexEntryNames.add(entryName);
					}
				}
			} catch (Throwable e) {
				throw new CompilationDeathException("Error reading archive '" + aPath + "'", e);
			}finally{
				try{
					if(archive != null)
						archive.close();
				}catch(Throwable t) {}
			}
			
			if(!dexEntryNames.isEmpty()){
				File file = new File(aPath);
				if(Options.v().process_multiple_dex()){
					for(String dexEntryName : dexEntryNames){
						try {
							classes.addAll(DexClassProvider.classesOfDex(
									file,dexEntryName));
						} catch (Throwable e) {} /* Ignore unreadable files */
					}
				}else{
					try {
						classes.addAll(DexClassProvider.classesOfDex(file));
					} catch (Throwable e) {} /* Ignore unreadable files */
				}
			}
		}
		else if (cst == ClassSourceType.directory) {
			File file = new File(aPath);

			File[] files = file.listFiles();
			if (files == null) {
				files = new File[1];
				files[0] = file;
			}

			for (File element : files) {
				if (element.isDirectory()) {
					classes.addAll(getClassesUnder(aPath + File.separatorChar + element.getName(),
							prefix + element.getName() + "."));
				} else {
					String fileName = element.getName();

					if (fileName.endsWith(".class")) {
						int index = fileName.lastIndexOf(".class");
						classes.add(prefix + fileName.substring(0, index));
					}else if (fileName.endsWith(".jimple")) {
						int index = fileName.lastIndexOf(".jimple");
						classes.add(prefix + fileName.substring(0, index));
					}else if (fileName.endsWith(".java")) {
						int index = fileName.lastIndexOf(".java");
						classes.add(prefix + fileName.substring(0, index));
					}else if (fileName.endsWith(".dex")) {
						try {
							classes.addAll(DexClassProvider.classesOfDex(element));
						} catch (IOException e) { /* Ignore unreadable files */
						}
					}
				}
			}
		}
		else
			throw new RuntimeException("Invalid class source type");
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
        ensureDirectoryExists(new File(b.toString() + "classes"));

        b.append("src");
        b.append(File.separatorChar);
        String fixedPackageName = c.getJavaPackageName();
        if (!fixedPackageName.equals("")) {
            b.append(fixedPackageName.replace('.', File.separatorChar));
            b.append(File.separatorChar);
        }
        
        ensureDirectoryExists(new File(b.toString()));
        
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
            if(getClassSourceType(path) != ClassSourceType.directory) {
            	continue;
            }
            // For jimple files
            List<String> l = getClassesUnder(path);
            for (String filename : l) {
                if (filename.startsWith(str))
                    set.add(filename);
            }

            // For class files;
            path = path + File.separatorChar;
            StringTokenizer tokenizer = new StringTokenizer(str, ".");
            while (tokenizer.hasMoreTokens()) {
                path = path + tokenizer.nextToken();
                if (tokenizer.hasMoreTokens())
                    path = path + File.separatorChar;
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
            case Options.output_format_asm: 	 return ".asm";
            default:
                throw new RuntimeException();
        }
    }
    
    /**
     * Create the given directory and all parent directories if {@code dir} is
     * non-null.
     *
     * @param dir
     */
    public static void ensureDirectoryExists(File dir) {
        if (dir != null && !dir.exists()) {
            try {
                dir.mkdirs();
            } catch (SecurityException se) {
                G.v().out.println("Unable to create " + dir);
                throw new CompilationDeathException(CompilationDeathException.COMPILATION_ABORTED);
            }
        }
    }

    /**
     * Returns the output directory given by {@link Options} or a default if not
     * set. Also ensures that all directories in the path exist.
     *
     * @return the output directory from {@link Options} or a default if not set
     */
    public String getOutputDir() {
        File dir;
        if (Options.v().output_dir().length() == 0) {
            //Default if -output-dir was not set
            dir = new File("sootOutput");
        } else {
            dir = new File(Options.v().output_dir());
            //If a Jar name was given as the output dir
            //  get its parent path (possibly empty)
            if (dir.getPath().endsWith(".jar")) {
                dir = dir.getParentFile();
                if (dir == null) {
                    dir = new File("");
                }
            }
        }

        ensureDirectoryExists(dir);
        return dir.getPath();
    }

    /**
     * If {@link Options#v()#output_jar()} is set, returns the name of the jar
     * file to which the output will be written. The name of the jar file can be
     * given with the -output-dir option or a default will be used. Also ensures
     * that all directories in the path exist.
     *
     * @return the name of the Jar file to which outputs are written
     */
    public String getOutputJarName() {
        if (!Options.v().output_jar()) {
            return "";
        }

        File dir;
        if (Options.v().output_dir().length() == 0) {
            //Default if -output-dir was not set
            dir = new File("sootOutput/out.jar");
        } else {
            dir = new File(Options.v().output_dir());
            //If a Jar name was not given, then supply default
            if (!dir.getPath().endsWith(".jar")) {
                dir = new File(dir.getPath(), "out.jar");
            }
        }

        ensureDirectoryExists(dir.getParentFile());
        return dir.getPath();
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
    	private File file;
        private String entryName;
        private ZipFile zipFile;
        private ZipEntry zipEntry;
        private List<InputStream> openedInputStreams;
        
        FoundFile(String archivePath, String entryName) {
        	this();
        	if(archivePath == null || entryName == null)
        		throw new IllegalArgumentException("Error: The archive path and entry name cannot be null.");
        	this.file = new File(archivePath);
        	this.entryName = entryName;
        }
    	
        FoundFile(File file) {
        	this();
        	if(file == null)
        		throw new IllegalArgumentException("Error: The file cannot be null.");
            this.file = file;
            this.entryName = null;
        }
        
        private FoundFile() {
        	this.zipFile = null;
        	this.zipEntry = null;
        	this.openedInputStreams = new ArrayList<InputStream>();
        }
        
        public String getFilePath() {
        	return file.getPath();
        }
        
        public boolean isZipFile() {
        	return entryName != null;
        }
        
        public File getFile() {
        	return file;
        }
        
        public InputStream inputStream() {
        	InputStream ret = null;
        	if(!isZipFile()) {
        		try{
        			ret = new FileInputStream(file);
        		} catch(Exception e) {
        			throw new RuntimeException("Error: Failed to open a InputStream for the file at path '" + file.getPath() + "'.",e);
        		}
        	} else {
        		if(zipFile == null) {
        			try {
        				zipFile = new ZipFile(file);
        				zipEntry = zipFile.getEntry(entryName);
        				if(zipEntry == null) {
        					silentClose();
        					throw new RuntimeException("Error: Failed to find entry '" + entryName + "' in the archive file at path '" + 
        							file.getPath() + "'.");
        				}
        			} catch(Exception e) {
        				silentClose();
        				throw new RuntimeException("Error: Failed to open the archive file at path '" + file.getPath() + "' for entry '" + 
        						entryName + "'.",e);
        			}
        		}
        		
        		InputStream stream = null;
        		try{
        			stream = zipFile.getInputStream(zipEntry);
        			ret = doJDKBugWorkaround(stream, zipEntry.getSize());
        		} catch(Exception e){
        			throw new RuntimeException("Error: Failed to open a InputStream for the entry '" + zipEntry.getName() + 
        					"' of the archive at path '" + zipFile.getName() + "'.",e);
        		}
        		finally {
        			if (stream != null) {
						try {
							stream.close();
						}
        				catch (IOException e) {
							// There's not much we can do here
						}
        			}
        		}
        	}
        	
        	openedInputStreams.add(ret);
        	return ret;
        }
        
        public void silentClose() {
        	try {
        		close();
        	} catch(Exception e) {}
        }
        
        public void close(){
        	//Try to close all opened input streams
        	List<Exception> errs = new ArrayList<Exception>();
        	for(Iterator<InputStream> it = openedInputStreams.iterator(); it.hasNext();){
        		InputStream is = it.next();
        		try {
        			is.close();
        		} catch(Exception e) {
        			errs.add(e);//record errors for later
        		}
        		it.remove();//remove the stream no matter what
        	}
        	//Try to close the opened zip file if it exists
        	if(zipFile != null) {
        		try {
        			zipFile.close();
        			errs.clear();//Successfully closed the archive so all input streams were closed successfully also
        		} catch(Exception e) {
        			errs.add(e);
        		}
        		zipFile = null;//set to null no matter what
        		zipEntry = null;//set to null no matter what
        	}
        	//Throw single exception combining all errors
        	if(!errs.isEmpty()) {
        		String msg = null;
    			ByteArrayOutputStream baos = new ByteArrayOutputStream();
    			PrintStream ps = null;
    			try {
    				ps = new PrintStream(baos,true,"utf-8");
    				ps.println("Error: Failed to close all opened resources. The following exceptions were thrown in the process: ");
    				int i = 0;
    				for(Throwable t : errs){
    					ps.print("Exception ");
    					ps.print(i++);
    					ps.print(": ");
    					t.printStackTrace(ps);
    				}
    				msg = new String(baos.toByteArray(), StandardCharsets.UTF_8);
    			} catch(Exception e) {
    				//Do nothing as this will never occur
    			} finally {
    				ps.close();
    			}
    			throw new RuntimeException(msg);
        	}
        }
        
        private InputStream doJDKBugWorkaround(InputStream is, long size) throws IOException {
    		int sz = (int) size;
    		byte[] buf = new byte[sz];					
    		final int N = 1024;
    		int ln = 0;
    		int count = 0;
    		while (sz > 0 && (ln = is.read(buf, count, Math.min(N, sz))) != -1) {
    			count += ln;
    			sz -= ln;
    		}
    		return  new ByteArrayInputStream(buf);		
    	}
    }

    /** Searches for a file with the given name in the exploded classPath. */
    public FoundFile lookupInClassPath( String fileName ) {
        for (String dir : classPath) {
            FoundFile ret = null;
            ClassSourceType cst = getClassSourceType(dir);
            if(cst == ClassSourceType.zip || cst == ClassSourceType.jar) {
                ret = lookupInArchive(dir, fileName);
            }
            else if (cst == ClassSourceType.directory) {
                ret = lookupInDir(dir, fileName);
            }
            if( ret != null )
            	return ret;
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
    
    private LoadingCache<String, Set<String>> archivePathsToEntriesCache = CacheBuilder.newBuilder()
    		.initialCapacity(60)
    		.maximumSize(500)
    		.softValues()
    		.concurrencyLevel(Runtime.getRuntime().availableProcessors())
    		.build(
    				new CacheLoader<String, Set<String>>(){
						@Override
						public Set<String> load(String archivePath) throws Exception {
							ZipFile archive = null;
							try {
								archive = new ZipFile(archivePath);
								Set<String> ret = new HashSet<String>();
								Enumeration<? extends ZipEntry> it = archive.entries();
								while(it.hasMoreElements()){
									ret.add(it.nextElement().getName());
								}
								return ret;
							} finally {
								if(archive != null)
									archive.close();
							}
						}
    				}
    );
    
    private FoundFile lookupInArchive(String archivePath, String fileName) {
    	Set<String> entryNames = null;
    	try {
    		entryNames = archivePathsToEntriesCache.get(archivePath);
    	} catch(Exception e) {
    		throw new RuntimeException("Error: Failed to retrieve the archive entries list for the archive at path '" + archivePath + "'.",e);
    	}
    	if(entryNames.contains(fileName)){
    		return new FoundFile(archivePath, fileName);
    	}
    	return null;
    }
   
    /** Returns the name of the class in which the (possibly inner) class
     * className appears. */
    public String getSourceForClass(String className) {
        String javaClassName = className;
        int i = className.indexOf("$");
        if (i > -1) {
            // class is an inner class and will be in
            // Outer of Outer$Inner
            javaClassName = className.substring(0, i);
        }
        return javaClassName;
    }
    
    /**
     * Set containing all dex files that were appended to the classpath
     * later on. The classes from these files are not yet loaded and are
     * still missing from dexClassIndex.
     */
    private Set<String> dexClassPathExtensions;

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
    
	public void extendClassPath(String newPathElement) {
		classPath = null;
		if (newPathElement.endsWith(".dex") || newPathElement.endsWith(".apk")) {
			if (dexClassPathExtensions == null)
				dexClassPathExtensions = new HashSet<String>();
			dexClassPathExtensions.add(newPathElement);
		}
	}
	
	/**
	 * Gets all files that were added to the classpath later on and that have
	 * not yet been processed for the dexClassIndex mapping
	 * @return The set of dex or apk files that still need to be indexed
	 */
	public Set<String> getDexClassPathExtensions() {
		return this.dexClassPathExtensions;
	}
	
	/**
	 * Clears the set of dex or apk files that still need to be indexed
	 */
	public void clearDexClassPathExtensions() {
		this.dexClassPathExtensions = null;
	}
}

