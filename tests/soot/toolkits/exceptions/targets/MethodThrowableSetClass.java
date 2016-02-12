package soot.toolkits.exceptions.targets;

public class MethodThrowableSetClass {
	class target{
		public target(){
			
		}
		public int foo(int a, int b){
			try{
				a = 0;
				int c = b/a;
				return a + b;
			}catch(ArithmeticException e){
				e.printStackTrace();
				return 0;
			}
		}
	}
	public static  target c;
		
	public void recursion(){
		try{
			int a = 0;
			int b = 1;
			int c = 0;
			recursion();
			c = a/b;
		}catch(ArithmeticException e){
			e.printStackTrace();
		}catch(OutOfMemoryError e){
			e.printStackTrace();
		}
	}
	
	public void nestedTry() {
		try{
			int array[] = new int[10];
			int b = 0;
			int c = array[9]/b;
			try{
			     c = 3/b;
			}catch(ArithmeticException e){
				e.printStackTrace();
			}
		}catch(NegativeArraySizeException e){
			e.printStackTrace();
		}
	}
	
	public void unitInCatchBlock(){
		try{
			int a = 0;
			int b = 0; 
			int c = a/b;
		}catch(ArithmeticException e){
			int a0 = 0;
			int b0 = 0; 
			int c0 = a0/b0;
			e.printStackTrace();
		}
	}
	
	public void foo(){
		try{
			bar();
		}catch(StackOverflowError e){
			e.printStackTrace();
		}catch(ThreadDeath e){
			e.printStackTrace();
		}
		
	}
	
	private void bar(){
		try{
			tool();
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
		}
		
	}
	
	public void tool(){
		try{
			int array[] = new int[10];
			int d = 0;
			int c = array[0]/d;
		}catch(NegativeArraySizeException e){
			e.printStackTrace();
		}
	}
	
	public void getAllException(){
		try{
			tool();
		}catch(Error e){
			e.printStackTrace();
		}catch(RuntimeException e){
			e.printStackTrace();
		}
	}
	
	public void getMyException() {
		try{
			throw new MyException();
		}catch(MyException e){
			e.printStackTrace();
		}
	}
}
