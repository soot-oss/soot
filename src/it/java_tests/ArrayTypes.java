public class ArrayTypes {


    public static void main(String [] args){
        ArrayTypes a = new ArrayTypes();
        a.run();
    }
    
    long [] bits;

    public void run(){
    
        bits = new long[9];
        for (int i = 0; i < bits.length; i++){
            bits[i] = 8;
        }

        for (int j = 0; j < bits.length; j++){
            long l = bits[j];
        }
        
    }
}
