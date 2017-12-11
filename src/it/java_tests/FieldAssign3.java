public class FieldAssign3 {

    public int x = 9;
    
    public static void main(String [] args){
        FieldAssign3 fa = new FieldAssign3();
        fa.run();
    }

    public void run(){
        foo().x += 3;
    }

    public FieldAssign3 foo(){
        return new FieldAssign3();
    }
}
