public class SynchMeth {
    
    public int x;

    public static void main(String [] args){
        new SynchMeth().run();
    }

    public synchronized void run(){
        x = x + 9;
    }
}
