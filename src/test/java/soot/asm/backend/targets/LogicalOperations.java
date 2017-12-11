package soot.asm.backend.targets;

public class LogicalOperations {
	private int i1;
	private boolean b1;
	private long l1;
	
	private int i2;
	private boolean b2;
	private long l2;
	
	public void doAnd(){
		i1 = i2 & i1;
		l1 = l2 & l1;
		b1 = b2 & b1;
	}
	
	public void doOr(){
		i1 = i2 | i1;
		l1 = l2 | l1;
		b1 = b2 | b1;
	}
	
	public void doXOr(){
		i1 = i2 ^ i1;
		l1 = l2 ^ l1;
		b1 = b2 ^ b1;
	}
	
	public void doInv(){
		i1 = ~i2;
		l1 = ~i2;
	}
	
	public void doShl(){
		i1 = i1 << i2;
		l1 = l1 << l2;
	}
	
	public void doShr(){
		i1 = i1 >> i2;
		l1 = l1 >> l2;
	}
	
	public void doUShr(){
		i1 = i1 >>> i2;
		l1 = l1 >>> l2;
	}

}
