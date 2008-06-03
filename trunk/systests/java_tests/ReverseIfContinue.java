public class ReverseIfContinue {

    public static void main(String [] args) {
        int i = 0;
        while (i < 10) {
            i++;
            if (i < 4 ) continue;
            System.out.println(i);
        }
    }
}
