public class Test34 {

    public static void main(String [] args){
        Test34 t34 = new Test34();
        t34.run();
    }

    public void run(){
        new Inner() {
            public void run(){
                System.out.println("running from Anon 1");
            }
        };
    }

    class Inner {
        public void run(){
            System.out.println("running in Inner");
        }
    }
}   
