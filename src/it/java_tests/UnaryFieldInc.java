public class UnaryFieldInc {

    private int x = 9;

    public static void main(String [] args){
        UnaryFieldInc ufi = new UnaryFieldInc();
        ufi.run();
    }

    public UnaryFieldInc foo(){
        System.out.println("running foo()");
        return new UnaryFieldInc();
    }
    
    public void run(){
        Inner i = new Inner();
        i.run();
    }

    class Inner {
    
        public void run(){
            foo().x++;
        }
    }
    
}
