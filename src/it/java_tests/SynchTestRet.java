public class SynchTestRet {
        
    public static void main(String[] args) {
        SynchTestRet t = new SynchTestRet();
        run(t);
    }

    public static int run(SynchTestRet t){
        synchronized(t) {
            System.out.println("made it!");
            return 4;
        }
        
    }
}
