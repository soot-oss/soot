interface MyListener {
    public void action();
}

public class Test14 {

    public static void main (String [] args){
        Test14 t14 = new Test14();
        t14.run();
    }

    public void run(){
        new MyListener () {
            public void action(){
                System.out.println("Smile");
            }
        }.action();
    }
}
