public class EnclosingClass {

    private class PriClass {
        public void run(){
            System.out.println("go");
        }
    }

    private void happy(){
        System.out.println("smile");
    }
    
    public static void main(String [] args){
        EnclosingClass e = new EnclosingClass();
        e.run();
    }

    class PubClass {
        public void run(){
            new PriClass().run();
            happy();
        }
    }
    
    public void run(){
        PubClass p = new PubClass();
        p.run();
    }
}
