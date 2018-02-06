interface MyListener {
    public void action();
}
public class Test1 {

    public static void main (String [] args){
        Test1 t = new Test1();
        t.run();
    }

    public void run(){
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
