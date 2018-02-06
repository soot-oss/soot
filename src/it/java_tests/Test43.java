interface MyListener {
    public void action();
}
public class Test43 {

    public static void main(String [] args){
        Test43 t43 = new Test43();
        t43.run();
    }
    
    public void run(){
        class MyListener1 implements MyListener{
            public void action(){
                System.out.println("Smile");
            }
        };
        new MyListener1().action();
    } 
}
