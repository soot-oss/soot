public class FieldAssign {

    public String s = "hi";

    public void run(){
        s += " ";
        s += "there";
        System.out.println(s);
    }

    public static void main(String [] args){
        FieldAssign fa = new FieldAssign();
        fa.run();
    }
}
