public class JimpleExample {

    public static void main(String[] args) {
	JimpleExample f = new JimpleExample();
	f.foo();
    }

    public void foo() {
      /* assume n%2 = 0 */ 
      int n = 8;
      /*s*/int[] d = new /*s*/int[n];
      /*s*/int[] result = new /*s*/int[n];
  
      int i = 0;
      int j = 0;

      while (n>0) {
	  /*s*/int[] dd = new /*s*/int[n/2];
	  while (i<n) {
	      /*s*/int b = lt(d[i], d[i+1]);
	      dd[j] = b*d[i] + (1-b)*d[i+1];
	      i = i+2;
	      j = j+1;
	  }
	  System.out.println(dd);
      }
      System.out.println(result);
    }

    public int lt(int a, int b) {
	if (a>b)
	    return 1;
	return 0;
    }
}
