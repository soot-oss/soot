public class MultiDimArrayTest {

    public static void main (String [] args) {
   
        int [][] intArray = new int[10][10];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                intArray[i][j] = i * j;
                System.out.println(intArray[i][j]);
            }
        }
    }
}
