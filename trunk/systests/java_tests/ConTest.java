public class ConTest {

    public static final double x = 43;
    double y = x;

    public static void main(String [] args) {
    
        System.out.println("x: "+x);
        System.out.println("y: "+new ConTest().y);
    }
}
