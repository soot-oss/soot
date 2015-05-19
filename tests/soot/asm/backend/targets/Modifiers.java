package soot.asm.backend.targets;

public abstract strictfp class Modifiers {
	
	private volatile int i;
	private final int j = 213;
	private transient int k;
	
	public final void a(){	
	}
	
	public synchronized void b(){
	}
	
	public static void c(){
	}
	
	void d(){
	}
	
	protected void e(){
	}
	
	abstract void f();
	
	private native void g();
	
}
