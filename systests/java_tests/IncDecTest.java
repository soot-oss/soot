public class IncDecTest {

    public static void main(String [] args){
        int x = 4;
        x = x++ + ++x;
        System.out.println(x);
    }
}
