class Helper {

    public static void main(String [] args){
    }

    public void action(){
        System.out.println("Helper");
    }
}

public class Test15 {

    public static void main (String [] args){
        Test15 t15 = new Test15();
        t15.run();
    }

    public void run(){
        new Helper () {
            public void action(){
                System.out.println("Smile Anon");
            }
        }.action();
    }
}
