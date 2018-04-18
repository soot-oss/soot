public class StaticBlockTest {

    private static int [] column;
    private int [] row;
    
    static {
        column = new int[34];
        for (int i = 0; i < 34; i++) {
            column[i] = i * i;
        }
    }
    
    {
        row = new int[9];    
    }
    
    public static void main (String [] args) {
        System.out.println(column[7]);
    }

    public StaticBlockTest() {
        System.out.println("Smile");
    }
    
    public StaticBlockTest(int i) {
        System.out.println("Smile"+i);
    }
}
