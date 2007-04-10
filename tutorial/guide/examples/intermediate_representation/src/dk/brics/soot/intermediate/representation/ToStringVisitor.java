package dk.brics.soot.intermediate.representation;

public class ToStringVisitor extends StatementProcessor<String> {
	
	@Override
	public String process(FooMethodCall s) {
		return ""+s.getAssignmentTarget()+" = "+s.getTarget().getName()+"()";
	}
	
	@Override
	public String process(SomeMethodCall s) {
		return ""+s.getTarget().getName()+"("+getArgs(s.getArgs())+")";
	}
	
	private String getArgs(Variable[] args) {
		String s = "";
		for (Variable v: args) {
			//System.err.println(v);
			s += v.toString()+", ";
		}
		if (s.length()>2)
			return s.substring(0, s.length()-2);
		return s;
	}

	@Override
	public String process(FooInit s) {
		return ""+s.getAssignmentTarget()+" = new Foo();";
	}
	
	@Override
	public String process(FooAssignment s) {
		return ""+"f = f";
	}

	@Override
	public String process(Return s) {
		if (s.getAssignmentTarget() != null)
			return "return "+s.getAssignmentTarget();
		return "return";
	}
	
	@Override
	public String process(Nop s) {
		return "nop";
	}
	
	@Override
	public String process(MethodHead s) {
		return "methodhead";
	}
}
