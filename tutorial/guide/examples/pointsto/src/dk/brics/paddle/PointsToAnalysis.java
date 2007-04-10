package dk.brics.paddle;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import soot.EntryPoints;
import soot.Local;
import soot.PointsToSet;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Value;
import soot.ValueBox;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;
import soot.jimple.paddle.PaddleTransformer;
import soot.jimple.spark.SparkTransformer;
import soot.options.PaddleOptions;
import soot.tagkit.LineNumberTag;

public class PointsToAnalysis {
	
	// Make sure we get line numbers and whole program analysis
	static {
		soot.options.Options.v().set_keep_line_number(true);
		soot.options.Options.v().set_whole_program(true);
		soot.options.Options.v().setPhaseOption("cg","verbose:true");
	}
	
	private static SootClass loadClass(String name, boolean main) {
		SootClass c = Scene.v().loadClassAndSupport(name);
		c.setApplicationClass();
		if (main) Scene.v().setMainClass(c);
		return c;
	}
	
	public static void main(String[] args) {
		loadClass("Item",false);
		loadClass("Container",false);
		SootClass c = loadClass(args[1],true);

		soot.Scene.v().loadNecessaryClasses();
		soot.Scene.v().setEntryPoints(EntryPoints.v().all());
		
		if (args[0].equals("paddle"))
			setPaddlePointsToAnalysis();
		else if (args[0].equals("spark"))
			setSparkPointsToAnalysis();

		SootField f = getField("Container","item");		
		Map/*<Local>*/ ls = getLocals(c,args[2],"Container");
		
		printLocalIntersects(ls);	
		printFieldIntersects(ls,f);		
	}
	
	static void setSparkPointsToAnalysis() {
		System.out.println("[spark] Starting analysis ...");
				
		HashMap opt = new HashMap();
		opt.put("enabled","true");
		opt.put("verbose","true");
		opt.put("ignore-types","false");          
		opt.put("force-gc","false");            
		opt.put("pre-jimplify","false");          
		opt.put("vta","false");                   
		opt.put("rta","false");                   
		opt.put("field-based","false");           
		opt.put("types-for-sites","false");        
		opt.put("merge-stringbuffer","true");   
		opt.put("string-constants","false");     
		opt.put("simulate-natives","true");      
		opt.put("simple-edges-bidirectional","false");
		opt.put("on-fly-cg","true");            
		opt.put("simplify-offline","false");    
		opt.put("simplify-sccs","false");        
		opt.put("ignore-types-for-sccs","false");
		opt.put("propagator","worklist");
		opt.put("set-impl","double");
		opt.put("double-set-old","hybrid");         
		opt.put("double-set-new","hybrid");
		opt.put("dump-html","false");           
		opt.put("dump-pag","false");             
		opt.put("dump-solution","false");        
		opt.put("topo-sort","false");           
		opt.put("dump-types","true");             
		opt.put("class-method-var","true");     
		opt.put("dump-answer","false");          
		opt.put("add-tags","false");             
		opt.put("set-mass","false"); 		
		
		SparkTransformer.v().transform("",opt);
		
		System.out.println("[spark] Done!");
	}
	
	private static void setPaddlePointsToAnalysis() {
		System.out.println("[paddle] Starting analysis ...");

		System.err.println("Soot version string: "+soot.Main.v().versionString);

		HashMap opt = new HashMap();
		opt.put("enabled","true");
		opt.put("verbose","true");
		opt.put("bdd","true");
		opt.put("backend","buddy");
		opt.put("context","kcfa");
		opt.put("k","2");
		//		opt.put("context-heap","true");
		opt.put("propagator","auto");
		opt.put("conf","ofcg");
		opt.put("order","32");
		opt.put("q","auto");
		opt.put("set-impl","double");
		opt.put("double-set-old","hybrid");
		opt.put("double-set-new","hybrid");
		opt.put("pre-jimplify","false");

		
		PaddleTransformer pt = new PaddleTransformer();
		PaddleOptions paddle_opt = new PaddleOptions(opt);
		pt.setup(paddle_opt);
		pt.solve(paddle_opt);
		soot.jimple.paddle.Results.v().makeStandardSootResults();	
		
		System.out.println("[paddle] Done!");
	}
	
	private static int getLineNumber(Stmt s) {
		Iterator ti = s.getTags().iterator();
		while (ti.hasNext()) {
			Object o = ti.next();
			if (o instanceof LineNumberTag) 
				return Integer.parseInt(o.toString());
		}
		return -1;
	}
	
	private static SootField getField(String classname, String fieldname) {
		Collection app = Scene.v().getApplicationClasses();
		Iterator ci = app.iterator();
		while (ci.hasNext()) {
			SootClass sc = (SootClass)ci.next();
			if (sc.getName().equals(classname))
				return sc.getFieldByName(fieldname);
		}
		throw new RuntimeException("Field "+fieldname+" was not found in class "+classname);
	}
	
	private static Map/*<Integer,Local>*/ getLocals(SootClass sc, String methodname, String typename) {
		Map res = new HashMap();
		Iterator mi = sc.getMethods().iterator();
		while (mi.hasNext()) {
			SootMethod sm = (SootMethod)mi.next();
			System.err.println(sm.getName());
			if (true && sm.getName().equals(methodname) && sm.isConcrete()) {
				JimpleBody jb = (JimpleBody)sm.retrieveActiveBody();
				Iterator ui = jb.getUnits().iterator();
				while (ui.hasNext()) {
					Stmt s = (Stmt)ui.next();						
					int line = getLineNumber(s);
					// find definitions
					Iterator bi = s.getDefBoxes().iterator();
					while (bi.hasNext()) {
						Object o = bi.next();
						if (o instanceof ValueBox) {
							Value v = ((ValueBox)o).getValue();
							if (v.getType().toString().equals(typename) && v instanceof Local)
								res.put(new Integer(line),v);
						}
					}					
				}
			}
		}
		
		return res;
	}
	
	private static void printLocalIntersects(Map/*<Integer,Local>*/ ls) {
		soot.PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		Iterator i1 = ls.entrySet().iterator();
		while (i1.hasNext()) {
			Map.Entry e1 = (Map.Entry)i1.next();
			int p1 = ((Integer)e1.getKey()).intValue();
			Local l1 = (Local)e1.getValue();
			PointsToSet r1 = pta.reachingObjects(l1);
			Iterator i2 = ls.entrySet().iterator();
			while (i2.hasNext()) {
				Map.Entry e2 = (Map.Entry)i2.next();
				int p2 = ((Integer)e2.getKey()).intValue();
				Local l2 = (Local)e2.getValue();
				PointsToSet r2 = pta.reachingObjects(l2);	
				if (p1<=p2)
					System.out.println("["+p1+","+p2+"]\t Container intersect? "+r1.hasNonEmptyIntersection(r2));
			}
		}
	}
	
	
	private static void printFieldIntersects(Map/*<Integer,Local>*/ ls, SootField f) {
		soot.PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		Iterator i1 = ls.entrySet().iterator();
		while (i1.hasNext()) {
			Map.Entry e1 = (Map.Entry)i1.next();
			int p1 = ((Integer)e1.getKey()).intValue();
			Local l1 = (Local)e1.getValue();
			PointsToSet r1 = pta.reachingObjects(l1,f);
			Iterator i2 = ls.entrySet().iterator();
			while (i2.hasNext()) {
				Map.Entry e2 = (Map.Entry)i2.next();
				int p2 = ((Integer)e2.getKey()).intValue();
				Local l2 = (Local)e2.getValue();
				PointsToSet r2 = pta.reachingObjects(l2,f);	
				if (p1<=p2)
					System.out.println("["+p1+","+p2+"]\t Container.item intersect? "+r1.hasNonEmptyIntersection(r2));
			}
		}
	}
	
}
