public class FieldUnary {

    private int count = 9;

    static FieldUnary getThis(){
        return new FieldUnary();
    }

    public static void main(String [] args){
        System.out.println(getThis().count += 1);
        Inner i = new FieldUnary().new Inner();
        i.run();
    }

    class Inner {
        public void run(){
            System.out.println(getThis().count += 1);
        }
    }
}
