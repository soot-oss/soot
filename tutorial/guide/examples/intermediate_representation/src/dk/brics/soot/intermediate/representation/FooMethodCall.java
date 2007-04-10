package dk.brics.soot.intermediate.representation;

public class FooMethodCall extends MethodCall {

	public FooMethodCall(Method target) {
		super(target, new Variable[0]);
	}

	@Override
	public <T> T process(StatementProcessor<T> v) {
		return v.dispatch(this);
	}

}
