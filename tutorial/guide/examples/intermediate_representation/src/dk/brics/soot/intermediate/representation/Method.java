package dk.brics.soot.intermediate.representation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soot.SootMethod;

public class Method {

	private String name = "";
	private Variable[] params;
	
    private List<Statement> sl;
    private Set<Return> rs;
    private List<MethodCall> sites;
	private MethodHead entry;
	
	public Method(String name, Variable[] params) {
		this.name = name;
		this.params = params;
		sl = new LinkedList<Statement>();
		rs = new HashSet<Return>();
		sites = new LinkedList<MethodCall>();
		entry = new MethodHead(params);
	}

	public MethodHead getEntry() {
		return this.entry;
	}

    /** Adds the given statement to the list of statements for this method.
     *  @param s the statement to add.
     */
    public void addStatement(Statement s) {
    	s.setIndex(sl.size());
    	sl.add(s);
    	s.setMethod(this);
    	if (s instanceof Return) {
    		rs.add((Return) s);
    	}
    	if (s instanceof MethodCall) {
    		MethodCall mc = (MethodCall) s;
    		mc.getTarget().sites.add(mc);
    	}
    }
    
    public String getName() {
    	return name;
    }
    
    public String toString() {
    	String result = "";
    	for (Statement s: sl) {
    		result += s.toString()+"\n";
    	}
    	return result;
    }
}
