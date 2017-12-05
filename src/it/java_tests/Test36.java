public class Test36 {

    public static void main (String [] args){
        Test36 t36 = new Test36();
        t36.run();
    }

    public void run(){
        TopLevel tl = new TopLevel();
        tl.run();
    }
}

class TopLevel {

    public void run(){
        System.out.println("TopLevel in file Test36.java");
    }
    
}
