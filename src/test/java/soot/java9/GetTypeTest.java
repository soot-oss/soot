package soot.java9;

import org.junit.Assert;
import org.junit.Test;

import soot.G;
import soot.RefType;
import soot.Scene;
import soot.Type;
import soot.options.Options;

public class GetTypeTest {

	@Test
	  public void testGetType() {
		  G.reset();
		  Options.v().set_prepend_classpath(true);
		  Options.v().set_soot_modulepath("VIRTUAL_FS_FOR_JDK");
		  Scene.v().loadNecessaryClasses();
		  
		  Type resultType = Scene.v().getType("java.lang.Throwable");
		  
		  Assert.assertEquals("java.lang.Throwable", ((RefType) resultType).getClassName());
	  }
}
