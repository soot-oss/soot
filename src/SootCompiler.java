
import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

public class SootCompiler {


	private File srcDir;
	private File classesDir;

	private Object javaCompiler;
	private Method compile;
	
	private String[] compilerDefaultArguments = { //"-J-mx128M", 
		//		"-verbose",
		"-d", "classes",
		//	"-classpath", "classes:src:/home/acaps/u2/fdesch1/soot/stable/jasmin/classes" 
	};
	private String[] compilerArguments;

	private String jasminClassPath = "/home/acaps/u2/fdesch1/soot/stable/jasmin/classes";
	private String javaClassPath;


	public static void main(String[] args) {
        long startTime = System.currentTimeMillis();		
		SootCompiler compiler = new SootCompiler();
		compiler.start();
        System.out.println("Total time: " 
						   + formatTime(System.currentTimeMillis() 
										- startTime));
	}


	public SootCompiler() {

		setupSrcDir();
		setupClassesDir();
		setupJavaCompiler();
		setupCompilerArguments();

	}

	protected void start() {

		// fetching java files
		System.out.print("Finding java files... ");
		List javaFiles = findFiles(srcDir, "java");
		System.out.println( "(" + javaFiles.size() + " file" 
							+ (javaFiles.size() > 1 ? "s" : "") 
							+ " found)");

		// compiling java files
		Iterator fileIt = javaFiles.iterator();
		while (fileIt.hasNext()) {
			String filename = (String)fileIt.next();
			if (needsUpdate(filename,
							"classes" 
							+ filename.substring(3, filename.length()-4) 
							+ "class")) {
				compileFile(filename);
			}
			
		}

		// fetching dat files
		System.out.print("Finding dat files ...");
		List datFiles = findFiles(srcDir, "dat");
		System.out.println( "(" + datFiles.size() + " file"
							+ (datFiles.size() > 1 ? "s" : "") 
							+ " found)");

		// copying dat files
		fileIt = datFiles.iterator();
		while (fileIt.hasNext()) {
			String filename = (String)fileIt.next();
			String target = "classes" + filename.substring(3);
			if (needsUpdate(filename, target)) {
				copyFile(filename, target);
			}
		}

		// fetching properties files
		System.out.print("Finding properties files ...");
		List pFiles = findFiles(srcDir, "properties");
		System.out.println( "(" + pFiles.size() + " file"
							+ (pFiles.size() > 1 ? "s" : "") 
							+ " found)");

		// copying properties files
		fileIt = pFiles.iterator();
		while (fileIt.hasNext()) {
			String filename = (String)fileIt.next();
			String target = "classes" + filename.substring(3);
			if (needsUpdate(filename, target)) {
				copyFile(filename, target);
			}
		}



	}



	protected void compileFile(String filename) {
		
		compilerArguments[compilerArguments.length-1] = filename; 
	
		System.out.print("javac"); 
		for (int i=0; i<compilerArguments.length; i++)
			System.out.print (" " + compilerArguments[i]);
	   System.out.println("");		

		try {
			// Call the compile() method
			Boolean ok = (Boolean)compile.invoke(javaCompiler, 
												 new Object[] { compilerArguments });
			if (!ok.booleanValue()) {
				System.err.println("An error occured while compiling file");
			}
        }
        catch (Exception e) {
			System.err.println("Error occurred while calling the compile method:\n"
							   + e.toString());			
		}

	}


	protected void copyFile(String inFile, String outFile) {

		System.out.println("Copying " + inFile + " to " + outFile);

		try {
			java.io.FileInputStream in = new java.io.FileInputStream(new File(inFile));
			java.io.FileOutputStream out = new java.io.FileOutputStream(new File(outFile));
		
			byte[] buffer = new byte[8 * 1024];
			int count = 0;
		
			do {
				out.write(buffer, 0, count);
				count = in.read(buffer, 0, buffer.length);
			} while (count != -1);
			
			in.close();
			out.close();
		} catch (Exception e) {
			System.err.println("Unable to perform copy:\n" 
							   + e.toString());
		}

	}


	protected List findFiles( File dir, String extension ) {
		List l = new ArrayList();
		String[] files = dir.list();

        for (int i = 0; i < files.length; i++) {
            File file = new File(dir, files[i]);
            if (file.isDirectory()) {
				l.addAll(findFiles(file, extension));
			}
			if (file.isFile()) {
				if (file.getName().endsWith("." + extension)) {
					l.add(file.getPath());
				}
			}
		}

		return l;
	}


    protected static String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;

        if (minutes > 0) {
            return Long.toString(minutes) + " minute"
                + (minutes == 1 ? " " : "s ")
                + Long.toString(seconds%60) + " second"
                + (seconds%60 == 1 ? "" : "s");
        }
        else {
            return Long.toString(seconds) + " second"
                + (seconds%60 == 1 ? "" : "s");
        }

    }

	/**
	   Checks if the target file corresponding to targetFileName needs to be
	   updated
	 */
	protected boolean needsUpdate(String srcFileName, String targetFileName) {
		File srcFile = new File(srcFileName);
		File targetFile = new File(targetFileName);

		if (targetFile.exists()
		   && (targetFile.lastModified() - srcFile.lastModified()) > 0 ) {
			return false;
		}

		return true;
	}

	
	protected void setupClassesDir() {

		classesDir = new File("classes");
		
		if (!classesDir.exists()) {
			System.out.println(" The \"classes\" directory does not exist. "
							   + " Creating one ...");
			if (!classesDir.mkdirs()) {
				System.err.println(classesDir.getPath() 
								   + " cannot be created");
				System.exit(1);	
			}
		} else {
			if (!classesDir.isDirectory()) {
				System.out.println(classesDir.getPath() + " is not a directory");
				System.exit(1);
			}
		}

	}


	protected void setupCompilerArguments() {
		compilerArguments = new String[compilerDefaultArguments.length + 3];

		for (int i=0; i<compilerDefaultArguments.length; i++)
			compilerArguments[i] = compilerDefaultArguments[i];

		compilerArguments[compilerArguments.length-3] = "-classpath";
		compilerArguments[compilerArguments.length-2] = System.getProperty("java.class.path");

	}


	protected void setupJavaCompiler() {
		
		try {
            // Create an instance of the compiler 
			// redirecting output to stdout
            Class c = Class.forName("sun.tools.javac.Main");
            Constructor cons = c.getConstructor(new Class[] { OutputStream.class, String.class });
            this.javaCompiler = cons.newInstance(new Object[] { System.out, "javac" });

            // Get the compile() method
            this.compile = c.getMethod("compile", new Class [] { String[].class });
        }
		catch (Exception e) {
			System.err.println("Unable to load the compiler :\n" 
							   + e.toString());
			System.exit(1);
        }
		
	}
	
	protected void setupSrcDir() {

		srcDir = new File("src");

		if (!srcDir.exists()) {
			System.err.println(srcDir.getPath() + " does not exist");
			System.exit(1);
		}
		if (!srcDir.isDirectory()) {
			System.err.println(srcDir.getPath() + " is not a directory");
			System.exit(1);
		}

	}

}
