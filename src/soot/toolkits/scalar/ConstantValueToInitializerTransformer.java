package soot.toolkits.scalar;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.ValueBox;
import soot.VoidType;
import soot.jimple.DoubleConstant;
import soot.jimple.FieldRef;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.tagkit.DoubleConstantValueTag;
import soot.tagkit.FloatConstantValueTag;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.LongConstantValueTag;
import soot.tagkit.StringConstantValueTag;
import soot.tagkit.Tag;
import soot.util.Chain;

/**
 * Transformer that creates a static initializer which sets constant values into
 * final static fields to emulate the initializations that are done through the
 * constant table in CLASS and DEX code, but that are not supported by Jimple.
 * 
 * @author Steven Arzt
 */
public class ConstantValueToInitializerTransformer extends SceneTransformer {

	public static ConstantValueToInitializerTransformer v() {
		return new ConstantValueToInitializerTransformer();
	}

	@Override
	protected void internalTransform(String phaseName, Map<String, String> options) {
		for (SootClass sc : Scene.v().getClasses()) {
			transformClass(sc);
		}
	}

	public void transformClass(SootClass sc) {
		SootMethod smInit = null;
		Set<SootField> alreadyInitialized = new HashSet<SootField>();

		for (SootField sf : sc.getFields()) {
			// We can only create an initializer for static final fields that
			// have a constant value. We ignore non-static final fields as
			// different constructors might assign different values.
			if (!sf.isStatic() || !sf.isFinal())
				continue;

			// If there is already an initializer for this field, we do not
			// generate a second one
			if (alreadyInitialized.contains(sf))
				continue;

			// Look for constant values
			for (Tag t : sf.getTags()) {
				Stmt initStmt = null;
				if (t instanceof DoubleConstantValueTag) {
					double value = ((DoubleConstantValueTag) t).getDoubleValue();
					initStmt = Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(sf.makeRef()),
							DoubleConstant.v(value));
				} else if (t instanceof FloatConstantValueTag) {
					float value = ((FloatConstantValueTag) t).getFloatValue();
					initStmt = Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(sf.makeRef()),
							FloatConstant.v(value));
				} else if (t instanceof IntegerConstantValueTag) {
					int value = ((IntegerConstantValueTag) t).getIntValue();
					initStmt = Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(sf.makeRef()),
							IntConstant.v(value));
				} else if (t instanceof LongConstantValueTag) {
					long value = ((LongConstantValueTag) t).getLongValue();
					initStmt = Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(sf.makeRef()),
							LongConstant.v(value));
				} else if (t instanceof StringConstantValueTag) {
					String value = ((StringConstantValueTag) t).getStringValue();
					initStmt = Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(sf.makeRef()),
							StringConstant.v(value));
				}

				if (initStmt != null) {
					if (smInit == null)
						smInit = getOrCreateInitializer(sc, alreadyInitialized);
					smInit.getActiveBody().getUnits().addFirst(initStmt);
				}
			}
		}

		if (smInit != null) {
			Chain<Unit> units = smInit.getActiveBody().getUnits();
			if (units.isEmpty() || !(units.getLast() instanceof ReturnVoidStmt))
				units.add(Jimple.v().newReturnVoidStmt());
		}
	}

	private SootMethod getOrCreateInitializer(SootClass sc, Set<SootField> alreadyInitialized) {
		SootMethod smInit;
		// Create a static initializer if we don't already have one
		smInit = sc.getMethodByNameUnsafe("<clinit>");
		if (smInit == null) {
			smInit = Scene.v().makeSootMethod("<clinit>", Collections.<Type>emptyList(), VoidType.v());
			smInit.setActiveBody(Jimple.v().newBody(smInit));
			sc.addMethod(smInit);
			smInit.setModifiers(Modifier.PUBLIC | Modifier.STATIC);
		} else {
			smInit.retrieveActiveBody();

			// We need to collect those variables that are already initialized
			// somewhere
			for (Unit u : smInit.getActiveBody().getUnits()) {
				Stmt s = (Stmt) u;
				for (ValueBox vb : s.getDefBoxes())
					if (vb.getValue() instanceof FieldRef)
						alreadyInitialized.add(((FieldRef) vb.getValue()).getField());
			}
		}
		return smInit;
	}

}
