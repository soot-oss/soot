public class Test81 {

    public static void main(String [] args){
        Test81 t81 = new Test81();
        t81.run();
    }

    public void run(){
        Inner1 in1 = new Inner1();
        in1.run();
    }
    
    public class Inner1 {
    
        public void run(){
            new Inner() {
                public void run(){
                    System.out.println("running from Anon 1");
                }
            }.run();
        }
    }

    public class Inner {
        public void run(){
            System.out.println("running in Inner");
        }
    }
}   
