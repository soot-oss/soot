package soot.asm.backend.targets;

public class InnerClass {
	
	private class Inner{
		static final int a= 3;
	}
	
	public int getA(){
		return Inner.a;
	}
	
	public void doInner(){
		new Measurable() {
		};
		
	}

}
