public class InnerClassTest {

    public class Inner {
        
        public void run() {
            System.out.println("Hello - from inner");
        }
    }

    public static void main(String [] args) {
        System.out.println("Hello - from outer");
        InnerClassTest ict = new InnerClassTest();
        ict.run();
    }

    private void run() {
        Inner i = new Inner();
        i.run();
    }
}

