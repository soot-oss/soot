public class JAssert2 {
    public static void main (String [] args) {
        int x = 0;
        JAssert2.class.getClassLoader().setClassAssertionStatus("MyAsserts", true);
        MyAsserts.run();
    }
}
class MyAsserts {
    static void run(){
        int x = 0;
        assert x < 2 ? true: false;
        assert x < 2 ? true: false: false;
        assert x < 2 ? true: false: "bug" ;
    }
}
