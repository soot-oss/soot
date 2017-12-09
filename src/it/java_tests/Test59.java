public class Test59 {

    public static void main(String [] args){
        Test59 t59 = new Test59();
        t59.run();
    }
    
    public void run(){
        new Test59(){
            public void run(){
            }
        }.run();
    }
}
