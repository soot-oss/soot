public class Test11 {

    private int x = 9;
    
    public static void main (String [] args){
        Test11 t = new Test11();
        t.run();
    }

    public void run(){
        TestInner t = new TestInner();
        t.run();
    }
    
    class TestInner {
        public void run(){
            System.out.println("Smile: "+x);
            go();
        }
    }

    private void go(){
        System.out.println("today");
    }
}
