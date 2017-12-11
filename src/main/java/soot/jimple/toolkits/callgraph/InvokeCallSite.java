package soot.jimple.toolkits.callgraph;

import soot.Local;
import soot.SootMethod;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.ConstantArrayAnalysis.ArrayTypes;

public class InvokeCallSite {
	public static final int MUST_BE_NULL = 0;
	public static final int MUST_NOT_BE_NULL = 1;
	public static final int MAY_BE_NULL = -1;
	
    private InstanceInvokeExpr iie;
    private Stmt stmt;
    private SootMethod container;
	private Local argArray;
	private Local base;
	private int nullnessCode;
	private ArrayTypes reachingTypes;

    public InvokeCallSite(Stmt stmt, SootMethod container,
            InstanceInvokeExpr iie, Local base) {
    	this(stmt, container, iie, base, (Local)null, 0);
    }
    public InvokeCallSite(Stmt stmt, SootMethod container, InstanceInvokeExpr iie,
			Local base, Local argArray, int nullnessCode) {
    	this.stmt = stmt;
        this.container = container;
        this.iie = iie;
        this.base = base;
        this.argArray = argArray;
        this.nullnessCode = nullnessCode;
	}
    
    
	public InvokeCallSite(Stmt stmt, SootMethod container, InstanceInvokeExpr iie,
			Local base, ArrayTypes reachingArgTypes, int nullnessCode) {
		this.stmt = stmt;
		this.container = container;
		this.iie = iie;
		this.base = base;
		this.nullnessCode = nullnessCode;
		this.argArray = null;
		this.reachingTypes = reachingArgTypes;
	}
	public Stmt stmt() { return stmt; }
    public SootMethod container() { return container; }
    public InstanceInvokeExpr iie() { return iie; }
	public Local base() { return base; }
	public Local argArray() { return argArray; }
	public int nullnessCode() { return nullnessCode; }
	public ArrayTypes reachingTypes() { return reachingTypes; }
}
