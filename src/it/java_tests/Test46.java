public class Test46 {

    public static void main(String [] args){
        Test46 t46 = new Test46();
        t46.run();
    }

    public void run(){
        class MyClass {
            public int x;
            public Integer y = new Integer(9);
            public void run(){
                System.out.println("x: "+x);
            }
        }
        MyClass m = new MyClass();
        System.out.println("m.x: "+m.x); 
    }
}
