package soot.lambdaMetaFactory;

import java.util.List;
/**
 * Please check the issue for detail:
 * https://github.com/soot-oss/soot/issues/1292 
 * 
 * @author raintung.li
 *
 */
public class Issue1292 {
	/**
	 * test class for new Issue1292
	 * @author raintung.li
	 *
	 */
	class test {
		public test(String s) {
		}
	}
	
	/**
	 * TestNew method for lambda new
	 * @param list
	 */
	public void testNew(List<String> list){
		list.stream().forEach(test::new);
	}
}
