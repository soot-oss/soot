public class SynchTestRet2 {
        
    public static void main(String[] args) {
        SynchTestRet2 t = new SynchTestRet2();
        run(t);
    }

    public static int run(SynchTestRet2 t){
        synchronized(t) {
            synchronized(t){
                System.out.println("made it!");
            }
            return 5;
        }
        
    }
}
