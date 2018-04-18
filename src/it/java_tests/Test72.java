public class Test72 {

    public static void main(String [] args) {
        Test72 t72 = new Test72();
        t72.run();
    }

    private void run() {
        class MyClass {
            public void run(int x){
                if (x % 3 == 0){
                    System.out.println(x+" is a multiple of 3");
                }
                else if (x % 3 == 1) {
                    System.out.println(x+" mod 3 is 1");
                }
                else {
                    assert x % 3 == 2: x;
                    System.out.println(x+" mod 3 is 2");
                }
            }
        };

        MyClass mc = new MyClass();
        mc.run(7);
        
    }
}
