package soot.jimple.toolkits.ide.libsumm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.Transform;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

public class Main {

	private static final Logger logger =LoggerFactory.getLogger(Main.class);
	static int yes=0, no=0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PackManager.v().getPack("jtp").add(new Transform("jtp.fixedie", new BodyTransformer() {

			@Override
			protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
				for(Unit u: b.getUnits()) {
					Stmt s = (Stmt) u;
					if(s.containsInvokeExpr()) {
						InvokeExpr ie = s.getInvokeExpr();						
						if(FixedMethods.isFixed(ie)) {
							System.err.println("+++ "+ie);
							yes++;
						} else {
							System.err.println(" -  "+ie);
							no++;
						}
					}
				}
			}
			
		}));
		soot.Main.main(args);
		System.err.println("+++ "+yes);
		System.err.println(" -  "+no);
	}

}
