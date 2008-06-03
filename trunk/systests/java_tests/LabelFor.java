public class LabelFor {

    public static void main(String [] args) {
        int x = 0;
        one: for (; x < 10; x++) {
                 
                 if (x > 5) break one;
                 System.out.println(x);
             }
    }
}

