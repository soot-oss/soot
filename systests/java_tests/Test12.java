public class Test12 {

    
    public static void main (String [] args){
        Test12 t = new Test12();
        t.run();
    }

    public void run(){
        TestInner1 t = new TestInner1();
        t.run();
    }
    
    class TestInner1 {
        public void run(){
            System.out.println("Smile: Inner1");
            TestInner2 t2 = new TestInner2();
            t2.run();
        }

        class TestInner2 {
            public void run(){
                System.out.println("Smile: Inner2");
            }
        
        }
        
        
        
    }

}
