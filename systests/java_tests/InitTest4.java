public class InitTest4 {

    public class Inner1 {
        public void m() {
            System.out.println("hello from inner 1");
        }
    }

    public static class Inner2 {
        public void m() {
            System.out.println("hello from inner 2");
        }
    }

    public static interface InnerInterface1 {
        public void go();
    }
    
    public interface InnerInterface2 {
        public void go();
    }
    
    public static void main(String [] args){
        InitTest4 it = new InitTest4();
        it.run();
        //Inner1 in = new Inner1();
        class MyClass extends Inner2{
            public void m() {
                System.out.println("hello from anon subtype of inner 1");
            }
        };
        /*class MyClass1 extends MyStaticClass1{
            public void m() {
                System.out.println("hello from anon subtype of inner 1");
            }
        };*/

        new Inner2 () {
            public void m(){
                System.out.println("hello from inner 2 again");
            }
        }.m();

        new InnerInterface1 () {
            public void go(){
                System.out.println("go");
            }
        }.go();

        new InnerInterface2 () {
            public void go(){
                System.out.println("go2");
            }
        }.go();

        
    }

    public void run(){
        new Inner1 () {
            public void m() {
                System.out.println("hello from anon subtype of inner 1");
            }
        }.m();
    }
}

