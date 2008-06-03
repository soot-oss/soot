class TestInvoke {
  private static int calls=0;
  public static void main(String[] args) {
			
	for (int i=0; i<10; i++) {
	  foo();
	}
	
	System.out.println("I made "+calls+" static calls");
  }

  private static void foo(){
	calls++;
	bar();
  }

  private static void bar(){
	calls++;
  }
}
