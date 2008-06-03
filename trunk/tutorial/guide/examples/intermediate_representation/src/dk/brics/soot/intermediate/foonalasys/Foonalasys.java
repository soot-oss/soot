package dk.brics.soot.intermediate.foonalasys;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import dk.brics.soot.intermediate.representation.Method;
import dk.brics.soot.intermediate.representation.Statement;
import dk.brics.soot.intermediate.translation.JavaTranslator;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.ValueBox;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.util.dot.DotGraph;

/** A <code>Foonalysis</code> object encapsulates a foonalysis performed
 *  on a collection of classes.
 *  The class also contains some convenience methods for loading and traversing
 *  the classes to be analyzed.<p>
 */
public class Foonalasys {
	
    private static final boolean DEBUG = false;

    private JavaTranslator jt;
    private Map/*<ValueBox,String>*/ sourcefile_map;
    private Map/*<ValueBox,String>*/ class_map;
    private Map/*<ValueBox,String>*/ method_map;
    private Map/*<ValueBox,Integer>*/ line_map;
    
    // Make sure we get line numbers
    static {
    	soot.Scene.v().loadBasicClasses();
    	soot.options.Options.v().parse(new String[] { "-keep-line-number" });
    }
    
    /** Performs a foonalysis on the current application classes.
     * @throws IOException 
     */
    public Foonalasys() {
    	jt = new JavaTranslator();
    	debug("Translating classes to intermediate form...");
    	Method[] methods = jt.translateApplicationClasses();
    	
    	for (Method m: methods) {
			System.out.println("Method: "+m.getName()+":");
			Collection<Statement> stmts = m.getEntry().getSuccs();
			printStmts(stmts);
			System.out.println("------------------------");
		}
  
    	debug("Foonalasys done");
    }	
  
	private void printStmts(Collection<Statement> stmts) {
		for (Statement stmt: stmts) {
			System.out.println("stmt: "+" "+stmt);
			printStmts(stmt.getSuccs());
		}		
	}
    
    /** Returns the name of the source file containing the given expression.
     *  @param box the expression.
     *  @return the source file name.
     */
    public final String getSourceFile(ValueBox box) {
	return (String)sourcefile_map.get(box);
    }

    /** Returns the name of the class containing the given expression.
     *  @param box the expression.
     *  @return the fully qualified class name.
     */
    public final String getClassName(ValueBox box) {
	return (String)class_map.get(box);
    }

    /** Returns the name of the method containing the given expression.
     *  @param box the expression.
     *  @return the method name.
     */
    public final String getMethodName(ValueBox box) {
	return (String)method_map.get(box);
    }

    /** Returns the source line number of the given expression.
     *  @param box the expression.
     *  @return the line number.
     */
    public final int getLineNumber(ValueBox box) {
	return ((Integer)line_map.get(box)).intValue();
    }

    /** Loads the named class into the Soot scene,
     *  marks it as an application class, and generates bodies
     *  for all of its concrete methods.
     *  @param name the fully qualified name of the class to be loaded.
     */
    public static void loadClass(String name) {
    	SootClass c = Scene.v().loadClassAndSupport(name);
    	c.setApplicationClass();
    	Iterator mi = c.getMethods().iterator();
    	while (mi.hasNext()) {
    		SootMethod sm = (SootMethod)mi.next();
    		if (sm.isConcrete()) {
    			sm.retrieveActiveBody();
    		}
    	}
    }
    
    private void debug(String s) {
    	if (DEBUG) {
    		System.err.println(s);
    	}
    }
	

}
