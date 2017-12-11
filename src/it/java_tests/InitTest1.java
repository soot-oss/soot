public class InitTest1 {

    static class Inner1 {
        Inner1(){
            System.out.println("Inner1");
        }
    }

    public static void main(String [] args){
        new Inner1();
        
        InitTest1 it = new InitTest1();
        it.run();
    }

    public void run(){
        new Inner1();
    }
}
