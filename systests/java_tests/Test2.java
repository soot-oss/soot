interface MyListener {
    public void action();
}
public class Test2 {

    public static void main (String [] args){
    
    }

    public void run(){
        new MyListener () {
            public void action(){
                System.out.println("Smile");
            }
        }.action();
    }
    
    public void runAgain(){
        new MyListener () {
            public void action(){
                System.out.println("Smile 2");
            }
        }.action();
    }
}
