public class ExceptionTest {

    public static void main(String [] args) {
        try {
            throw new Exception();
            
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            return;
        }
    }
}
