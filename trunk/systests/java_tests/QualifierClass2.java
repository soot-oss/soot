public class QualifierClass2 {

    public static void main(String [] args){
        
    }

    public class Qualifier2 {
        public void run(){
            go();
        }

        private void go(){
            System.out.println("y");
        }
    }

    private void go(){
        System.out.println("x");
    }
}
