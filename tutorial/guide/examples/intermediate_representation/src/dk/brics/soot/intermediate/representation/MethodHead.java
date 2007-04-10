package dk.brics.soot.intermediate.representation;

public class MethodHead extends Statement {

    /** The parameter variables for the method */
    public Variable[] params;

    public MethodHead(Variable[] params) {
    	this.params = params;
    }
	
	@Override
	public <T> T process(StatementProcessor<T> v) {
		return v.dispatch(this);
	}
	
	public String toString() {
		String res = "";
		for (Variable v: params) {
			res += v;
		}
		return res;
	}

}
