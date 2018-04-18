public class TryCatchFinallyReturns {

    public static void main(String [] args){
        TryCatchFinallyReturns t = new TryCatchFinallyReturns();
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
            return 0;
        }
        finally {
            return 3;
        }
    }
}
