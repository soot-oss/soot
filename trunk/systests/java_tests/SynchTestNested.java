public class SynchTestNested {
        
    public static void main(String[] args) {
        SynchTestNested t = new SynchTestNested();
            synchronized(t) {
                synchronized(t) {
                    System.out.println("made it!");
                }
            }
    }
}
