public class ThisTest {

    int m(ThisTest t) { return t.i; }
    int j = m(this);
    final int i = 1;
    public static void main(String[] args) {
      System.out.print(new ThisTest().j);
    }
                
}
