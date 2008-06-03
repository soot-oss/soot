/**
 * @author jlhotak
 *
 */
public class Liveness {
	
	public void run (){
		int x = 4;
		int z = x + 3;
		for (int i = 0; i < 10; i++){
			z = x * i;
		} 
		int y = 9;
		z = y + 9;
		System.out.println(z);
	}
}
