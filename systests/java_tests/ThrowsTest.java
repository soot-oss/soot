public class ThrowsTest {

    public static void main(String [] args){
        ThrowsTest t = new ThrowsTest();
        try {
            t.run();
        }
        catch(Exception e){
        }
    }

    public void run() throws MyException {
        throw new MyException();
    }
    
}
class MyException extends Exception{

}
