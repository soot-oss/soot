package dk.brics.soot.intermediate.representation;

public class StatementProcessor<T> {

	/**
	 * Constructs a new Statement processor.
	 */
	public StatementProcessor() {}


	/**
	 * Processes an {@link FooMethodCall}.
	 * This invokes {@link #pre(Statment)}, 
	 * if that returns null then {@link #process(FooMethodCall)} is invoked, 
	 * Finally, {@link #post(Statement, Object)} is invoked.
	 * @param s Statement
	 * @return result
	 */
	public T dispatch(FooMethodCall s) { 
		T t = pre(s);
		if (t == null)
			t = process(s);
		post(s, t);
		return t;
	}
	
	/**
	 * Processes an {@link SomeMethodCall}.
	 * This invokes {@link #pre(Statment)}, 
	 * if that returns null then {@link #process(SomeMethodCall)} is invoked, 
	 * Finally, {@link #post(Statement, Object)} is invoked.
	 * @param s Statement
	 * @return result
	 */
	public T dispatch(SomeMethodCall s) { 
		T t = pre(s);
		if (t == null)
			t = process(s);
		post(s, t);
		return t;
	}

	/**
	 * Processes an {@link FooInit}.
	 * This invokes {@link #pre(Statment)}, 
	 * if that returns null then {@link #process(FooInit)} is invoked, 
	 * Finally, {@link #post(Statement, Object)} is invoked.
	 * @param s Statement
	 * @return result
	 */
	public T dispatch(FooInit s) { 
		T t = pre(s);
		if (t == null)
			t = process(s);
		post(s, t);
		return t;
	}

	/**
	 * Processes an {@link FooAssignment}.
	 * This invokes {@link #pre(Statment)}, 
	 * if that returns null then {@link #process(FooAssignment)} is invoked, 
	 * Finally, {@link #post(Statement, Object)} is invoked.
	 * @param s Statement
	 * @return result
	 */
	public T dispatch(FooAssignment s) { 
		T t = pre(s);
		if (t == null)
			t = process(s);
		post(s, t);
		return t;
	}
	
	/**
	 * Processes an {@link Return}.
	 * This invokes {@link #pre(Statment)}, 
	 * if that returns null then {@link #process(Return)} is invoked, 
	 * Finally, {@link #post(Statement, Object)} is invoked.
	 * @param s Statement
	 * @return result
	 */
	public T dispatch(Return s) {
		T t = pre(s);
		if (t == null)
			t = process(s);
		post(s, t);
		return t;
	}

	/**
	 * Processes an {@link Nop}.
	 * This invokes {@link #pre(Statment)}, 
	 * if that returns null then {@link #process(Nop)} is invoked, 
	 * Finally, {@link #post(Statement, Object)} is invoked.
	 * @param s Statement
	 * @return result
	 */
	public T dispatch(Nop s) {
		T t = pre(s);
		if (t == null)
			t = process(s);
		post(s, t);
		return t;
	}
	
	/**
	 * Processes an {@link MethodHead}.
	 * This invokes {@link #pre(Statment)}, 
	 * if that returns null then {@link #process(MethodHead)} is invoked, 
	 * Finally, {@link #post(Statement, Object)} is invoked.
	 * @param s Statement
	 * @return result
	 */
	public T dispatch(MethodHead s) {
		T t = pre(s);
		if (t == null)
			t = process(s);
		post(s, t);
		return t;
	}

	/**
	 * Method to be invoked for processing an {@link FooMethodCall}.
	 * By default, nothing happens and null is returned.
	 * @param s current Statement
	 * @return result
	 */
	public T process(FooMethodCall s) {
		return null;
	}
	
	/**
	 * Method to be invoked for processing an {@link SomeMethodCall}.
	 * By default, nothing happens and null is returned.
	 * @param s current Statement
	 * @return result
	 */
	public T process(SomeMethodCall s) {
		return null;
	}
	
	/**
	 * Method to be invoked for processing an {@link FooInit}.
	 * By default, nothing happens and null is returned.
	 * @param s current Statement
	 * @return result
	 */
	public T process(FooInit s) {
		return null;
	}
	
	/**
	 * Method to be invoked for processing an {@link FooAssignment}.
	 * By default, nothing happens and null is returned.
	 * @param s current Statement
	 * @return result
	 */
	public T process(FooAssignment s) {
		return null;
	}

	/**
	 * Method to be invoked for processing an {@link Return}.
	 * By default, nothing happens and null is returned.
	 * @param s current Statement
	 * @return result
	 */
	public T process(Return s) {
		return null;
	}
	
	/**
	 * Method to be invoked for processing an {@link Nop}.
	 * By default, nothing happens and null is returned.
	 * @param s current Statement
	 * @return result
	 */
	public T process(Nop s) {
		return null;
	}
	
	/**
	 * Method to be invoked for processing an {@link MethodHead}.
	 * By default, nothing happens and null is returned.
	 * @param s current Statement
	 * @return result
	 */
	public T process(MethodHead s) {
		return null;
	}

	
	/**
	 * Method to be invoked for preprocessing a {@link Statement}.
	 * By default, nothing happens and null is returned.
	 * @param s current Statement
	 * @return result
	 */
	public T pre(Statement s) {
		return null;
	}
	
	/**
	 * Method to be invoked for postprocessing a {@link Statement}.
	 * By default, nothing happens.
	 * @param s current Statment
	 * @param t result from <code>process</code>
	 */
	public void post(Statement s, T t) {}


}

