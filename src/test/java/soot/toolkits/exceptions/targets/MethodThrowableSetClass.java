package soot.toolkits.exceptions.targets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodThrowableSetClass {
    private static final Logger logger = LoggerFactory.getLogger(MethodThrowableSetClass.class);
	class target{
		public target(){
			
		}
		public int foo(int a, int b){
			try{
				a = 0;
				int c = b/a;
				return a + b;
			}catch(ArithmeticException e){
				logger.error(e.getMessage(), e);
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
			logger.error(e.getMessage(), e);
		}catch(OutOfMemoryError e){
			logger.error(e.getMessage(), e);
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
				logger.error(e.getMessage(), e);
			}
		}catch(NegativeArraySizeException e){
			logger.error(e.getMessage(), e);
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
			logger.error(e.getMessage(), e);
		}
	}
	
	public void foo(){
		try{
			bar();
		}catch(StackOverflowError e){
			logger.error(e.getMessage(), e);
		}catch(ThreadDeath e){
			logger.error(e.getMessage(), e);
		}
		
	}
	
	private void bar(){
		try{
			tool();
		}catch(ArrayIndexOutOfBoundsException e){
			logger.error(e.getMessage(), e);
		}
		
	}
	
	public void tool(){
		try{
			int array[] = new int[10];
			int d = 0;
			int c = array[0]/d;
		}catch(NegativeArraySizeException e){
			logger.error(e.getMessage(), e);
		}
	}
	
	public void getAllException(){
		try{
			tool();
		}catch(Error e){
			logger.error(e.getMessage(), e);
		}catch(RuntimeException e){
			logger.error(e.getMessage(), e);
		}
	}
	
	public void getMyException() {
		try{
			throw new MyException();
		}catch(MyException e){
			logger.error(e.getMessage(), e);
		}
	}
}
