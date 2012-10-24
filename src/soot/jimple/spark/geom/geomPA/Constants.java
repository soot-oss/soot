package soot.jimple.spark.geom.geomPA;

import soot.RefType;

public class Constants {

	// Available encodings
	public static final String geomE = "Geom";
	public static final String heapinsE = "HeapIns";
	public static final String ptinsE = "PtIns";
	
	// The constants for the constraints type identification
	public static final int NEW_CONS = 0;
	public static final int ASSIGN_CONS = 1;
	public static final int LOAD_CONS = 2;
	public static final int STORE_CONS = 3;
	public static final int FIELD_ADDRESS = 4;
	
	// The constants for the call graph
	public static final int SUPER_MAIN = 0;
	public static final int UNKNOWN_FUNCTION = -1;
	
	// The number of contexts that is natively supported by Java (2^63)
	// Using big integer would not bring too many benefits.
	public static final long MAX_CONTEXTS = Long.MAX_VALUE - 1;
	
	// Some commonly referred to information
	public static final RefType exeception_type = RefType.v( "java.lang.Throwable" );
	
	// The parameters that are used to tune the precision and performance tradeoff
	public static int max_cons_budget = 40;
	public static int max_pts_budget = 80;
	public static int cg_refine_times = 1;

}
