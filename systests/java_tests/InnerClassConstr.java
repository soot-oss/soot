public class InnerClassConstr {

    public static  void main(String [] args){
        Inner i = new InnerClassConstr().new Inner();
    }
    
    public class Inner {
    
        public Inner() {
        
        }
    }
}
