public class InitTest3 {

    public class Inner1 {
        public void m() {
            System.out.println("hello from inner 1");
        }
    }
    public static void main(String [] args){
        InitTest3 it = new InitTest3();
        it.run();
        new InitTest3().new Inner1 () {
            public void m() {
                System.out.println("hello from anon subtype of inner 1");
            }
        }.m();
    }

    public void run(){
        new Inner1 () {
            public void m() {
                System.out.println("hello from anon subtype of inner 1");
            }
        }.m();
    }
}
