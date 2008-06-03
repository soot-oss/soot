public class InnerTest1 {
    
    int c = 0;
    public static void main(String [] args){
        InnerTest1 it1 = new InnerTest1();
        it1.run();
    }

    public void run(){
        if (c--<0) return;
        //int x = new InnerClass().run();
        //System.out.println("x: "+x);
    }

    int i = 10;
    public class InnerClass{
        public int run(){
            System.out.println("Smile");
            if (i--<0) return 0;
            
            return new InnerClass().run();
        }
    }
}
