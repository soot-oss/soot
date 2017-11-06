interface MyListener {
    public void action();
}
public class Test3 {

    public static void main (String [] args){
        Test3 t3 = new Test3();
        MyListener ml = t3.run(2);
        ml.action();
    }

    public MyListener run(int x){
        if (x == 1){
            class MyListener1 implements MyListener{
                public void action(){
                    System.out.println("Smile");
                }
            };
            return new MyListener1();
        }
        else if (x == 2){
            class MyListener2 implements MyListener{
                public void action(){
                    System.out.println("Smile 2");
                }
            };
            return new MyListener2();
        }
        else {
            class MyListener3 implements MyListener{
                public void action(){
                    System.out.println("Smile 3");
                }
            };
            return new MyListener3();
        }
    }
}
