public class AssertTest {

    static int x = 9;

    public static void main(String [] args) {
    
        int i = 10;
        if (i % 3 == 0) {
            i = i + 9;
        } 
        else if (i % 3 == 1) {
            i = i + 8;
        } 
        else {
            assert i % 3 == 2 : "result of "+i+" % 3 != 2???";
            i = i + 7;
        }            

    }
}
