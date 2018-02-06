public class Test79 {

    public static void main(String [] args){
        Test79 t79 = new Test79();
        t79.run();
    }

    public void run(){
        new Inner(4) {
            public void run(){
                System.out.println("running from Anon 1");
            }
        }.run();
    }

    class Inner {

        public Inner(){
            System.out.println("running in Inner simple init");
        }

        public Inner(int x){
            this();
        }
        
        public void run(){
            System.out.println("running in Inner");
        }
    }
}   
