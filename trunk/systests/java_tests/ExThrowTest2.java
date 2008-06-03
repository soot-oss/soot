public class ExThrowTest2 {

    public static void main(String [] args){
        ExThrowTest2 ett = new ExThrowTest2();
        try {
            ett.run(8, 9);
        }
        catch(Throwable e){
        }
    }

    public void run(int x, int y) throws Throwable {
        if (x < y) throw new Throwable("x must be bigger then y");
        System.out.println("x - y: "+(x-y));
    }
    
    public class MyException extends RuntimeException{
        public MyException(String s){
            super(s);
        }
    }
}
