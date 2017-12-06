public class InstanceOf {

    public static void main(String [] args){
        InstanceOf i = new InstanceOf();
        i.run("S");
    }
    
    public void run(Object o){
        if (o instanceof String){
            String s = (String)o;
            String y = s + s;
        }
        Object n = new Object();
    }
}
