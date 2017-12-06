package soot.asm.backend.targets;

public class Dups {

	public Object dup(){
		Object o = new Object();
		return o;
	}
	
	public long dubl(){
		long l = 1234;
		l = l + l;
		return l;
	}
	
}
