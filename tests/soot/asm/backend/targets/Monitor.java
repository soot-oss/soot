package soot.asm.backend.targets;

public class Monitor {
	Object o;
	
	public void doSth(){
		
		synchronized (o) {
			
		}
		
		System.out.println();
	}

}
