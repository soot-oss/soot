public class Compare {
    public static void main(String [] args) {
    
        Compare c = new Compare();
        c.run();
    }

    private void run() {
        int i = 9;
        long l = 10;
        long l2 = 11;
        short s = 4;
        byte by = 2;

        if (s < by) {
        }

        if (by < i) {
        }
        
        if (l2 >= l) {
            l2 = l2 - 1;
        }
        
        if (l != i) {
            i = i + 1;
        }
        else if (l == i) {
            i = i - 1;
        }


        float f = 0.9F;
        double d = 0.9;

        if (d == f) {
            d += f;
        }
        
        if (f <= d) {
            d += f;
        }

        if (f < i) {
            i *= i;
        }

        if (f < l) {
            l = i + l;
        }

        char c = 'c';

        if (c < i ) {
            i++;
        }
    }
}
