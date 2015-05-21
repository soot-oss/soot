package soot.asm.backend;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import soot.G;
import soot.Main;

/**
 * Abstract base class for tests for the ASM backend that work with compiled class files
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public abstract class AbstractASMBackendTest implements Opcodes {
	
	private final StringWriter sw = new StringWriter();
	private final PrintWriter pw = new PrintWriter(sw);
	
	private final TraceClassVisitor visitor = new TraceClassVisitor(pw); 
	
	private String comparisonOutput;
	
	protected TargetCompiler targetCompiler = TargetCompiler.javac;
	
	/**
	 * Enumeration containing the supported Java compilers
	 */
	enum TargetCompiler {
		eclipse,
		javac
	}
	
	/**
	 * Sets up everything for testing by generation of Soot's output and reference output
	 */
	@Before
	public void setupTest(){
		runSoot();
		createComparison();
	}
	
	/**
	 * Runs Soot with the arguments needed for running one test
	 */
	protected void runSoot(){
		G.reset();
		// Location of the rt.jar
		String rtJar = System.getProperty("java.home")+File.separator+"lib"+File.separator+"rt.jar";

		// Run Soot and print output to .asm-files. 
		Main.main(new String[] {
			"-cp", getClassPathFolder()
					+File.pathSeparator+rtJar,
			"-src-prec", "only-class",
			"-output-format", "asm",
			"-allow-phantom-refs",
			"-java-version", getRequiredJavaVersion(),
			getTargetClass()
		});
	}
	
	/**
	 * Generates the textual output and saves it for later for comparison
	 */
	private void createComparison(){
		generate(visitor);
		comparisonOutput = sw.toString();
	}

	/**
	 * Compares the generated test output with Soot's output for the tested class line by line
	 * @throws FileNotFoundException if either the file for comparison could not be created or the soot output could not be opened
	 */
	@Test
	public void compareOutput() throws FileNotFoundException{
		/*
		 * Print output for comparison to file for debugging purposes.
		 */
		PrintWriter ow = new PrintWriter(new File("sootOutput/"+getTargetClass()+".asm.compare"));
		ow.print(comparisonOutput);
		ow.flush();
		ow.close();
		
		Scanner sootOutput = new Scanner(new File("sootOutput/"+getTargetClass()+".asm"));
		Scanner compareOutput = new Scanner(comparisonOutput);
		
		int line = 1;
		while(compareOutput.hasNextLine()){
			// Soot-output must have as much lines as the compared output.
			assertTrue("Too few lines in Soot-output!", sootOutput.hasNextLine());
			
			// Get both lines
			String compare = compareOutput.nextLine();
			String output = sootOutput.nextLine();
			
			// Compare lines
			assertTrue("Expected line "+compare.trim()+" but got "+output.trim()+" in line "+line, compare.equals(output));
			++line;
		}
		
		assertFalse("Too many lines in Soot-output!", sootOutput.hasNextLine());
		
		sootOutput.close();
		compareOutput.close();
	}
	
	/**
	 * Generates the textual output for comparison with Soot's output
	 * @param cw The TraceClassVisitor used to generate the textual output
	 */
	protected abstract void generate(TraceClassVisitor cw);

	/**
	 * Returns the name of the class to be tested
	 * @return The fully qualified name of the tested class
	 */
	protected abstract String getTargetClass();
	
	/**
	 * Return a folder for the process-dir option of Soot
	 * @return The location of the process-dir folder
	 */
	protected abstract String getTargetFolder();
	
	/**
	 * Returns the folder that is to be added to the class path for running soot
	 * @return The location of the folder containing the test files
	 */
	protected abstract String getClassPathFolder();
	
	/**
	 * Returns the Java version required, can be overridden by individual tests if needed
	 * @return The required Java version, "default" by default
	 */
	protected String getRequiredJavaVersion(){
		return "default";
	}
	
}
