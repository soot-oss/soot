public class FieldStringAssigns {
    public String x = "hello";
    public static void main(String [] args){
        FieldStringAssigns fsa = new FieldStringAssigns();
        fsa.run();
    }
    public void run(){
        x += x;
        System.out.println(x);
        x += " there";
        System.out.println(x);
    }
}
