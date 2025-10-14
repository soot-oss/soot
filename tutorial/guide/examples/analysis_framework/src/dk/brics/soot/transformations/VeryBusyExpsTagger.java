package dk.brics.soot.transformations;

import java.util.Map;

import dk.brics.soot.analyses.SimpleVeryBusyExpressions;
import dk.brics.soot.analyses.VeryBusyExpressions;

import soot.*;
import soot.tagkit.ColorTag;
import soot.tagkit.StringTag;
import soot.toolkits.graph.ExceptionalUnitGraph;

public class VeryBusyExpsTagger extends BodyTransformer {
	public static final String PHASE_NAME = "vbetagger";
	public static final String TAG_TYPE = "Busy Expressions";
	
	// NOTE: According to SOOT coding rules, this is not the correct way of
	// providing a singleton. Instead one should add the class name to
	// %SOOTHOME/src/singletons.list and then run:
	// %SOOTHOME/src/make_singletons > soot/src/soot/Singletons.java
	// and then provide:
	// 1. public VeryBusyExpsTagger(Singletons.G.global g) {}
	// 2. public static VeryBusyExpsTagger v() {return G.v().<singleton_name>}
	// This is so that when resetting soot by calling G.v().reset() this
	// class will also be reset. (But there's nothing to reset here so
	// I'll ignore that for now :) )
	private static VeryBusyExpsTagger instance = new VeryBusyExpsTagger();	
	private VeryBusyExpsTagger() {}	
	public static VeryBusyExpsTagger v() {return instance;}
	
	/**
	 * Adds <code>StringTag</code>s and <code>ColorTag</code>s to the body
	 * in the interest of using Eclipse to relay information to the user.<br/>
	 * Every unit that has a busy expression flowing out of it, gets a
	 * <code>StringTag</code> describing that fact (per expression).
	 * If an expression that is busy out of that unit is also used within
	 * it, then we add a <code>ColorTag</code> to that expression.
	 * @param b the body to transform
	 * @param phaseName the name of the phase this transform belongs to
	 * @param options any options to this transform (in this case always empty) 
	 */
	protected void internalTransform(Body b, String phaseName, Map options) {
		VeryBusyExpressions vbe = new SimpleVeryBusyExpressions(new ExceptionalUnitGraph(b));
		
		for (Unit u : b.getUnits()) {
			for (Value v : vbe.getBusyExpressionsAfter(u)) {
				u.addTag(new StringTag("Busy expression: " + v, TAG_TYPE));
				
				for (ValueBox use : u.getUseBoxes()) {
					if (use.getValue().equivTo(v))
						use.addTag(new ColorTag(ColorTag.RED, TAG_TYPE));
				}
			}
		}
	}
}
