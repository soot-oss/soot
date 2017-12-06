
public class DeeplyNestedAnon {

    public static void main(String [] args){
        DeeplyNestedAnon d = new DeeplyNestedAnon();
        d.run();
    }
    
    public void run(){
    
        final int x = 8;

        Object o = new TopClass(5) {
          
            public int getB(){
                
                final int y = 9;

                Object obj = new TopClass(){
                    public int getB(){
                        return y;
                    }
                };
                return 4;
                
            }
            public int getC(){
                return 6;
            }
        };

        o = new TopClass(){
            public int getB(){
                return 7;
            }
        };
    }

   
}
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

