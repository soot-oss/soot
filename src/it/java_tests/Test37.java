public class Test37 {

    public static void main (String [] args){
        Test37 t37 = new Test37();
        t37.run();
    }

    public void run(){
        TopLevel tl = new TopLevel();
        tl.run();
    }
}

class TopLevel {

    public void run(){
        System.out.println("TopLevel in file Test37.java");
    }
    
}
