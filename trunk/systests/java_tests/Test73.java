interface MyListener {
    public void action();
}
public class Test73 {

    public static void main (String [] args){
        Test73 t73 = new Test73();
        t73.run(new Integer(7), new Integer(8), new Integer(9));
    }

    public void run(final Object o1, final Object o2, final Object o3){
        new MyListener () {
            public void action(){
                System.out.println("Smile: "+o1);
            }
        }.action();
        new MyListener () {
            public void action(){
                System.out.println("Smile: "+o2);
            }
        }.action();
        class MyClass {
            public void action(){
                System.out.println("Smile: "+o3);
            }
        };
        MyClass mc = new MyClass();
        mc.action();
    }
}
