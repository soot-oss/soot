public class SimpleAssert {

    public static void main(String [] args){
        SimpleAssert sa = new SimpleAssert();
        sa.run();
    }
    
    public void run(){
    
        int i = 0;
        assert i == 0;
        assert i == 0: i;
    }
}
