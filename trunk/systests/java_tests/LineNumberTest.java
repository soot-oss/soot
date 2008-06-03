public class LineNumberTest {
    
    public static void main(String [] args) {

        //Object o = new Integer(9);
        int i = 10;
        int x = 9;
        int y = 9;
        int z = 6;
        if (i == 2) {
            i = x + 1;
            i = y - 1;
            i = z * 2;
        }
        else if (i == 5){
            z = 3;
            y = 4;
        }
        while (i > 10 ) {
            i = i - 3;
        }
    }

    public LineNumberTest(){
        super();
    }
}
