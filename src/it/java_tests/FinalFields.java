public class FinalFields {

    private final int i = 9;
    private final String s = "j";

    public static void main(String [] args){
        FinalFields ff = new FinalFields();
        System.out.println(ff.i);
        ff.run();
    }

    public void run(){
        System.out.println(s+"ennifer");
    } 
}
