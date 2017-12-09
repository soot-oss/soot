public class SynchTestRet3 {
        
    public static void main(String[] args) {
        SynchTestRet3 t = new SynchTestRet3();
        run(t, 8);
    }

    public static int run(SynchTestRet3 t, int j){
        synchronized(t) {
            System.out.println("made it!");
            if (j == 9){
                return 4;
            }
        }
        return 5;
        
    }
}
