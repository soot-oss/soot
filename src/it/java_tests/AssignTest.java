public class AssignTest {

    public int h = 0;
    
    public static void main (String [] args) {
        int x = 9;
        int j = 0;
        int y = 8;

        j = x + y;
        j += y;
        j -= y;
        j /= y;

        AssignTest t = new AssignTest();
        t.run();
    }

    private void run(){
        h = 8;

        h++;
    }
}
