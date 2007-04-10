package dk.brics.soot.intermediate.representation;

public class SomeMethodCall extends MethodCall {

	
	public SomeMethodCall(Method target, Variable[] args) {
		super(target, args);
	}

	@Override
	public <T> T process(StatementProcessor<T> v) {
		return v.dispatch(this);
	}

}
