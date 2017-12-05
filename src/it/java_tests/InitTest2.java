public class InitTest2 {

    static class Inner1 {
        Inner1(){
            this(1);
            System.out.println("Inner1");
        }
        Inner1(int i ){
            System.out.println("i: "+i);
        }
    }

    public static void main(String [] args){
        new Inner1();
        
        InitTest2 it = new InitTest2();
        it.run();
    }

    public void run(){
        new Inner1();
    }
}
