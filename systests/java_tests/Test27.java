public class Test27 {

    private int x = 9;

    private void go(){
        System.out.println("hi");
    }
    
    public static void main (String [] args){
        Test27 t = new Test27();
        t.run();
    }

    public void run(){
        TestInner1 t = new TestInner1();
        t.run();
    }
    
    class TestInner1 {

        private int y = 9;
        public void run(){
            System.out.println("Smile: Inner1: "+x);
            go();
            TestInner2 t2 = new TestInner2();
            t2.run();
        }

        class TestInner2 {
            public void run(){
                System.out.println("Smile: Inner2: "+x+y);
                go();
            }
        
        }
        
        
        
    }

}
