public class EverythingTest {

    private int i = 0;
    protected short s = 2;
    public long l = 9L;
    private static byte b = 4;
    protected static float f = 5F;
    public static double d = 3.09;
    private char c = 'j';

    private String st = "hi";
    
    public static void main(String [] args){
    
        //System.out.println(args[0]);
        EverythingTest et = new EverythingTest(9);
        et.run();
    }

    public EverythingTest(int x) {
        init();
    }

    private void run(){
        
        int [] a = new int [10];

        for (int i = 0; i < 10; i++) {
            a[i] = i;
        }

        int b = a[7];

        float [] f = {10F, 3F, 4F, 7F};

    
        float ch = f[2];

        double du = b + ch;
        double e = du - ch;
        double g = e * ch;
        double h = g / ch;
       
        print(h);
        
        boolean truth = false;
        boolean falsity = !truth;

        int x = 9;
        print(x);
        int y = -x;
        print(y);
        int z = +y;
        print(z);
       
        int j = z++ - 7;
        print(j);
        int k = ++z - 7;
        print(k);
        
        j = z-- - 7;
        print(j);
        k = --z - 7;
        print(k);
        
    }

    private void init(){
    }

    private void print(int i){
        System.out.println(i);
    }
    
    private void print(double d){
        System.out.println(d);
    }
}
