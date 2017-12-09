public class InnerInConstCallWithQualifier {

    public static void main(String [] args){
        new InnerInConstCallWithQualifier().go();
    }
    
    public InnerInConstCallWithQualifier(){
        this(new QualifierClass().new QInner() {
            public void run(){
            }});
    }

    public InnerInConstCallWithQualifier(Object r){
    }

    private void go(){
        System.out.println("running go");
    }
}

class QualifierClass {

    public class QInner {
        public void run(){
        
        }
    }
}
