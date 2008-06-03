class Helper {

    public static void main(String [] args){
    }

    public void action(){
        System.out.println("Helper");
    }
}
public class Test93 {
    public static void main(String [] args) {
        Test93 t93 = new Test93();
        t93.run();
    }

    public void run(){
        new Helper(){
            int x = 9;
            {x=x+3;}
            public void action(){
                System.out.println("x: "+x);
            }
        }.action();
    }
}
