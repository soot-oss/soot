public class ArrayTests {

        int [][] x;
        
        public static void main (String [] args) {
        
            ArrayTests a = new ArrayTests();
            a.run();
        }

        private void run () {
            int [] i = new int [9];
            x = new int [3][3];
            x[0][0] = 4;
            System.out.println(x[0][0]);
            x[0][1] = 3;
            System.out.println(x[0][0]);
        }
}
