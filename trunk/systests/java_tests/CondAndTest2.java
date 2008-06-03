public class CondAndTest2 {
    
    public static void main(String [] args){
        String s1 = "S1";   
        String s2 = "S2";   
        String s3 = "S3";

        CondAndTest2 cat = new CondAndTest2();

        boolean result = cat.isValid(s1, s2, s3);
    }

    public boolean isValid(String s1, String s2, String s3){

        boolean p1 = s1 == null ? true : false;
        return ((s1 == null) && (s2 == null) && (s3 == null)); 
    }
}
