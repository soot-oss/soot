public class Test80 {

    public static void main(String [] args){
        Test80 t80 = new Test80();
        t80.run();
    }

    public void run(){
        new Inner() {
            public void run(){
                System.out.println("running from Anon 1");
            }
        };
    }

    static class Inner {
        public void run(){
            System.out.println("running in Inner");
        }
    }
}   
