/**
 * @author jlhotak
 *
 */
public class LiveInteractive {
	
	public void run (){
		int x = 4;
		int z = x + 3;
		int i = 0;
		
		do {
			z = x * i;
			x = x - 1;
			i = i + 2;
		}while (i <20); 
		
		int y = 9;
		z = y + 9;
		System.out.println(z);
	}
}
