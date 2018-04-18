public class JAssert {

    public static void main(String [] args){
        int x = 0;
        assert x == 0 : MyError.throwError("bug if error thrown");
        assert x < 2 ? true : false : MyError.throwError("bug if error thrown");
        
    }
}
class MyError extends Error {
    MyError(String s) { super(s); }
    static boolean throwError(String s) {
        throw new MyError(s);
    }
}


