public class TryCatch3 {

    public static void main(String [] args){
        new TryCatch3().run();
    }
    public void run(){
        try {
            throw new RuntimeException();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}
