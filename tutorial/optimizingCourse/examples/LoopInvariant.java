/*
 * Created on Dec 27, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author jlhotak
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LoopInvariant {

	public static void main(String[] args) {
		int x = 9;
		int z = 10;
		int y = 8;
		int k = 0;
		int m = 0;
		
		for (int i = 0; i < 100; i++){
			y = x + z;
			System.out.println(y);
			k = x + i;
			
			int j = 9;
			m = j + 1;
		}
	}
}
