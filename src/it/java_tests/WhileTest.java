public class WhileTest {
    public static void main(String [] args) {
        int i = 4;
        while (i < 10 ) {
            System.out.println(i);
            if (i == 6) break;
            i = i + 2;
        }
        
    }
}
