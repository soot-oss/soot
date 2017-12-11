public class Compare4 {
    public static void main(String [] args) {
    
        Compare4 c = new Compare4();
        c.run();
    }

    private void run() {
        int i = 9;
        long l = 10;
        double d = 0.98F;

        float f = 19L;
        if (l >= i) {
            System.out.println(l);
        }
        else if (l < i) {
            System.out.println(i);
        }


        long j = i + l;
        float f2 = 0.978213F;
        double d2 = 0.9;

        if (d2 == f2) {
            d2 += f2;
        }
    }
}
