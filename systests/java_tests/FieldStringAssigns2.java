public class FieldStringAssigns2 {
    public final String x = "hello";
    public static void main(String [] args){
        FieldStringAssigns2 fsa = new FieldStringAssigns2();
        fsa.run();
    }
    public void run(){
        //x += x;
        //System.out.println(x);
        String y = " there";
        System.out.println(x+y);
    }
}
