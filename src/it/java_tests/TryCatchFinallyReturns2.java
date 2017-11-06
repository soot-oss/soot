public class TryCatchFinallyReturns2 {

    public static void main(String [] args){
        TryCatchFinallyReturns2 t = new TryCatchFinallyReturns2();
        int y = t.run(3);
        System.out.println(y);
    }

    public int run(int i){
        int [] x = new int[]{i};
        try {
            if (x[0] == 4) {
                return 7;
            }
            else if (x[1] == 8){
                return 2;
            }
        
        }
        catch (ArrayIndexOutOfBoundsException e){
            try {
                if (x[0] == 4){
                    return 7;
                }
                else if (x[1] == 8){
                    return 2;
                }
            }
            catch (ArrayIndexOutOfBoundsException e1){
                return 17;
            }
            finally {
                System.out.println("hello");
                return 27;
            }
        }
        finally {
            System.out.println("Hello 2");
            return 28;
        }
    }
}
