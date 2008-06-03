public class InnerInConst {
    public InnerInConst(){
        new Object () {
            public void run(){
                System.out.println("Hello");
            }
        }.run();
    }

    public static void main(String [] args){
        InnerInConst i = new InnerInConst();
    }
}
