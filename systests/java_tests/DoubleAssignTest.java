public class DoubleAssignTest {

        private double x;
        private double y;
        public static void main (String [] args) {
            DoubleAssignTest dat = new DoubleAssignTest();
            dat.run();
        }

        private void run() {
            this.x = this.y = 0.9;
            System.out.println(x);
        }
} 
