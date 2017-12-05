public class SwitchLabel {

    public static void main(String [] args) {
    
        label:  switch(args.length){
            case 0:
                break label;
            }
            System.out.println("OK");
        
    }
}
