public class AssertInInner1 {

    public static void main(String [] args){
        
    }

    class InnerNonStaticInit {
        void run() {
            int x = 0;
            assert x > 1 : "NONSTATIC throwing assert during class init";
        }
        class InnerDeepNonStaticInit {
            void run(){
                int x = 0;
                assert x > 1 : "DEEP throwing assert";
            }
        }
    }
    
    static class InnerStaticInit {
        static {
            int x = 0;
            assert x > 1 : "STATIC throwing assert during class init";
        }
    }
    
}
