interface MyListener {
    public void action();
}
public class Test6 {

    public static void main (String [] args){
        Test6 t6 = new Test6();
        t6.run();
    }

    public static void run(){
        new MyListener () {
            public void action(){
                System.out.println("Smile");
            }
        }.action();
        new MyListener () {
            public void action(){
                System.out.println("Smile 2");
            }
        }.action();
        new MyListener () {
            public void action(){
                System.out.println("Smile 3");
            }
        }.action();
    }
}
