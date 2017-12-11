public class Compare2 {
    public static void main(String [] args) {
    
        Compare2 c = new Compare2();
        c.run();
    }

    private void run() {
        int i = 9;
        long l = 10;

        if (l >= i) {
            System.out.println(l);
        }
        else if (l < i) {
            System.out.println(i);
        }


        /*float f = 0.978213F;
        double d = 0.9;

        if (d == f) {
            d += f;
        }*/
    }
}
