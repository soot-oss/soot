interface MyListener {

    public void action();
}
public class Test39{

    public static void main(String [] args){
        Test39 t39 = new Test39();
        t39.run();
    }

    public void run(){
        new MyListener(){
            public void action(){
                System.out.println("Test39 anon1");
            }
        };
        TopLevel1 tl1 = new TopLevel1();
        tl1.run();
    }
}
class TopLevel1{

    public void run(){
        new MyListener(){
            public void action(){
                System.out.println("TopLevel1 anon1");
            }
        };
    }
}
