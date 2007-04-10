public class GrimpExample {

  public static void main(String[] args) {
    GrimpExample f = new GrimpExample();
    int a = 7;
    int b = 14;
    int x = (f.bar(21)+a)*b;
  }

  public int bar(int n) { return n+21; }
}
