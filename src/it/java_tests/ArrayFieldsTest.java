public class ArrayFieldsTest {

    private int [] column = new int[4];
    
    {
        for (int i = 0; i < 4; i++) {
            column[i] = 4;
        }
    }
    
    public static void main(String [] args) {
    
        ArrayFieldsTest aft = new ArrayFieldsTest();
        aft.run();
    }

    private void run() {
        int i = 0;
        while(column[i] != 32) {
            System.out.println(column[i]);
            column[i] += column[i];
        }
        System.out.println(column.length);
    }
}
