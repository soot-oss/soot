interface MyListener {
    public void action();
}
public class Test10 {

    private int x = 9;
    
    public static void main (String [] args){
        Test10 t = new Test10();
        t.run();
    }

    public void run(){
        MyListener ml = new MyListener() {
            public void action(){
                System.out.println("Smile: "+x);
                go();
            }
        };
        ml.action();
    }

    private void go(){
        System.out.println("today");
    }
}
