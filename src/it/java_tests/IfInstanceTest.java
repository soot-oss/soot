public class IfInstanceTest {

    public static void main (String [] args) {
        IfInstanceTest ift = new IfInstanceTest();
        ift.run(new Integer(9));
    }

    private void run(Object o) {
        if (o instanceof String) {
        }
        else if (!(o instanceof Integer)){
        }

    }
}
