public class Test69 {
    public static void main (String [] args){
        Test69 t69 = new Test69();
        try {
            t69.run(4, 5);
        }
        catch (MyException e){
        }
    }

    public void run(int x, int y) throws MyException{
        if (x < y){
            throw new MyException("my exception from outer");
        }
        else {
            System.out.println(x);
        }
        Inner in = new Inner();
        in.run(8, 7);
    }

    public class MyException extends Throwable{
        public MyException(String s){
            super(s);
        }
    }

    public class Inner {
        public void run(int x, int y) throws MyException{
            if (x < y) throw new MyException("x too small");
            else {
                x = x - y;
            }
        }
    }
}
