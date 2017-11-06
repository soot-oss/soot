public class InnerStatic {
    
    public static void main(String [] args){
        Inner1 i1 = new Inner1();
        i1.run();
    }
    
    static class Inner1 {
        
        public void run() {
            new Inner2();
        }
        
        class Inner2 {
        
        }
    }
}
