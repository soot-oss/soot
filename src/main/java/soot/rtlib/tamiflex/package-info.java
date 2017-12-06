/**
 * This package contains classes that may be emitted into a program during code generation.
 * If Soot cannot find a class on the soot-classpath then it automatically loads the class from
 * Soot's own JAR file but <i>only</i> if this class is in this package. This is to avoid accidental
 * mix-ups between the classes of the application being analyzed and Soot's own classes.
 * 
 * To add a class, use, for example, the following:
 * <pre>
 * //before calling soot.Main.main
 * Scene.v().addBasicClass(SootSig.class.getName(),SootClass.BODIES);
 * 
 * //then at some point
 * Scene.v().getSootClass(SootSig.class.getName()).setApplicationClass();
 * </pre>
 * 
 * This will cause Soot to emit the class SootSig along with the analyzed program.
 */
package soot.rtlib.tamiflex;