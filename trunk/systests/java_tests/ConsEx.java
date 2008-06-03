public class ConsEx {

    public static void main(String [] agrs){
    }
    
    public ConsEx() throws MyException{
        throw new MyException();
    }
}
class MyException extends Exception{

}
