public class SynchTest {
        
    public static void main(String[] args) {
        SynchTest t = new SynchTest();
            //synchronized(t) {
                synchronized(t) {
                    System.out.println("made it!");
                }
            //}
    }
}
