public class InnerInConstCall {

    public InnerInConstCall(){
        this(new Runnable() { 
            public void run(){
                System.out.println("Hello");
            }
        });
    }

    public InnerInConstCall(Runnable r){
        r.run();
    }

    private void go(){
        System.out.println("running go");
    }

    public static void main(String [] args){
        InnerInConstCall c = new InnerInConstCall();
    }
}
