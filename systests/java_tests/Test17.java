public class Test17 {

    public static void main (String [] args){
        Test17 t17 = new Test17();
        t17.run();
    }

    public void run(){
        class MyListener1 {
            public void action(){
                System.out.println("Smile");
            }
        };
        new MyListener1().action();
    }
}
