public class LabelDo {

    public static void main(String [] args) {
        int x = 0;
        one: do {
                 x++;
                 if (x > 5) break one;
                 System.out.println(x);
             }while (true);
    }
}

