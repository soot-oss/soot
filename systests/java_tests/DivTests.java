public class DivTests {

    public static void main(String [] args) {
        
        Double d = new Double(0.9);
        Integer i = new Integer(9);
        int t = 4;
        double result = 6;

        result += d.doubleValue() * i.intValue() / t;

        System.out.println(result);
        
    }
}
