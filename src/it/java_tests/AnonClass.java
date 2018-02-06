
class TopClass {

    public TopClass(int x){
    }
    public TopClass(){
    }
    public int getB(){
        return 2;
    }
    public int getC(){
        return 3;
    }
}

public class AnonClass {

    public static void main(String args[] ) {
        AnonClass ac = new AnonClass();
        final int h = 9; 
        for (int i = 0; i < 5; i++){
            ac.run(i);
        }
    }

    public void run(int y){
        Object o;
        final int x = y;
        go(o = new AnonClass().new AnotherClass(){
            public int getB(){
                return x;
            }
            public int getC(){
                return 24;
            }
            public void run(){
                System.out.println("hi");
            }
            public int hi(int i){
                return 8*i;
            }
        });

        
        Object obj = new TopClass(){
            public int getB(){
                return 20;
            }
            public int getC(){
                return 30;
            }
        };
        
    }

    public void go(Object o){
    
    }

    public class AnotherClass {
    
        public int hi(int i){
            return 7*i;
        }
    }
}

