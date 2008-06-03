public class AssignStmts2 {

    int x = 0;
    public static void main(String [] args) {
    

        int i = 8;
        int j = 9;

        int k = 7;
        int f = 4;

        i = j + k + f + 1;
        i = j + 1;
        System.out.println(i);
        i += j;
        System.out.println(i);
        i -= j;
        System.out.println(i);
        i *= j;
        System.out.println(i);
        i /= j;
        System.out.println(i);
        i %= j;
        System.out.println(i);
        i <<= j;
        System.out.println(i);
        i >>= j;
        System.out.println(i);
        i >>>= j;
        System.out.println(i);
        i &= j;
        System.out.println(i);
        i |= j;
        System.out.println(i);
        i ^= j;
        System.out.println(i);
            
        AssignStmts2 as = new AssignStmts2(); 
        as.run();
    }

    public void run() {
        x += 1;
        System.out.println(x);
    }
}
