public class FloatComp {

    public static void main (String [] args) {
    
            float f1 = 0.9F;
            float f2 = 0.9F;
            float f3 = 0.5F;
            float f4 = 0.5F;

            if (f1 == f2 && f3 == f4){
                System.out.println(f4);
            }

            int i = 9;
            int j = 8;
            int k = 10;
            int h = 2;
            
            boolean b2 = (i == j || win());
    }

    private static boolean win(){
        return true;
    }
}
