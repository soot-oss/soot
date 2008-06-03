package dk.brics.soot.intermediate.representation;

public abstract class MethodCall extends Statement {

    /** The target method */
    private Method target;
    /** The arguments given */
    private Variable[] args;

    public MethodCall(Method target, Variable[] args) {
    	this.target = target;
    	this.args = args;
    }

    
    public Method getTarget() {
    	return target;
    }


	public Variable[] getArgs() {
		return args;
	}


	public void setArgs(Variable[] args) {
		this.args = args;
	}
}
