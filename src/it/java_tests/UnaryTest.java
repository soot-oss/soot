public class UnaryTest {

    public static void main(String [] args) {
    
        int [] i = new int[4];
        int j = 9;

        i[0] = j++;
        System.out.println(i[0]);
        i[1] = ++j;
        System.out.println(i[1]);
    }
}
