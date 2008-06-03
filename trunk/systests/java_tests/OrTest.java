public class OrTest {

    public static void main (String [] args) {
        OrTest ot = new OrTest();
        ot.run();
    }

    private void run() {
        long l;
        int t1 = 34;
        int t2 = 45;
        
        l = t1 > t2 ? (long)(t1 << 7 | 5) << 21 | t2:
                      (long)(t2 << 7 | 5) << 21 | t1;
    }
}
