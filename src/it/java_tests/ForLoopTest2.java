public class ForLoopTest2 {

    public static void main (String [] args) {
        int i = 1;
        for (;;) {
        
            if (i == 9) break;
            if (i == 8) break;
            i += i;
        }
    }
}
