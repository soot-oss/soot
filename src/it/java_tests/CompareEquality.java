public class CompareEquality {
    public static void main(String [] args) {
    
        CompareEquality c = new CompareEquality();
        c.run();
    }

    private void run() {
        int i = 9;
        long l = 10;
        long l2 = 11;
        short s = 4;
        byte by = 2;
        float f = 0.9F;
        double d = 0.9;    
    
        if (d == f) {
        }
        if (d == i) {
        }
        if (l == d) {
        }
        if (f != s) {
        }
        if (s != l) {
        }
        if (by != d) {
        }

        if (d == d) {
        }
        if (f == f) {
        }

        if (f != f){
        }
    }
}
