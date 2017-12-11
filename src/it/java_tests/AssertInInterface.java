public class AssertInInterface implements AssertInter{

    public static void main(String [] args){
        AssertInInterface aii = new AssertInInterface();
        Object obj = aii.o;
    }
}

interface AssertInter {

    Object o = new Object(){
        public void run(){
            int i = 0;
            assert i > -1;
        }
    };
}
