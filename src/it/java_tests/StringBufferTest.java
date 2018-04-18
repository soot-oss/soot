public class StringBufferTest {

    public static void main (String [] args) {
    
        StringBufferTest sbt = new StringBufferTest();
    }


    public String toString() {
    
        int data = 0;
        
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        sb.append(data);
        sb.append("]");

        return sb.toString();
    }
}
