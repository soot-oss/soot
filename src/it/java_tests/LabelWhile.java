public class LabelWhile {

    public static void main(String [] args) {
        int x = 0;
        one: while (true) {
                 x++;
                 if (x > 5) break one;
                 System.out.println(x);
             }
    }
}
