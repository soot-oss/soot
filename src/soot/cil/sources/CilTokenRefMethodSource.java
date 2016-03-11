package soot.cil.sources;

import soot.Body;
import soot.G;
import soot.Local;
import soot.MethodSource;
import soot.RefType;
import soot.SootMethod;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;

/**
 * Class implementing a specialized method source for handling type/method/field
 * references by token
 * 
 * @author Steven Arzt
 *
 */
class CilTokenRefMethodSource implements MethodSource {
	
	/**
	 * Gets whether this method source can provide an implementation for the
	 * given method
	 * @param m The method to check
	 * @return True if this method source supports the given method, otherwise
	 * false
	 */
	public static boolean supportsMethod(SootMethod m) {
		return G.v().soot_cil_CilNameMangling().getTypeRefFromMangled(
				m.getDeclaringClass().getName()) != null;
	}

	@Override
	public Body getBody(SootMethod m, String phaseName) {
		// Make sure that we don't generate any spurious method bodies
		if (!supportsMethod(m))
			return null;
		
		JimpleBody jb = Jimple.v().newBody(m);
		LocalGenerator localGenerator = new LocalGenerator(jb);
		
		// We need to check which method we are generating
		if (m.getName().equals("<init>"))
			createRefClassConstructor(jb, localGenerator);
		
		return jb;
	}

	/**
	 * Creates the constructor of a reference class
	 * @param jb The body in which to place the reference constructor
	 * @param localGenerator The local generator to use
	 */
	private void createRefClassConstructor(JimpleBody jb,
			LocalGenerator localGenerator) {
		RefType thisType = jb.getMethod().getDeclaringClass().getType();
		
		// Allocate the "this" local
		Local locThis = localGenerator.generateLocal(thisType);
		jb.getUnits().add(Jimple.v().newIdentityStmt(locThis,
				Jimple.v().newThisRef(thisType)));
		
		// Finish the method
		jb.getUnits().add(Jimple.v().newReturnVoidStmt());
	}

}
