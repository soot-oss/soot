/* Usage: java MainDriver appClass
 */

/* import necessary soot packages */
import soot.*;

public class MainDriver {
  public static void main(String[] args) {

    /* check the arguments */
    if (args.length == 0) {
      System.err.println("Usage: java MainDriver [options] classname");
      System.exit(0);
    }
    
    /* add a phase to transformer pack by call Pack.add */
    Pack jtp = PackManager.v().getPack("jtp");
    jtp.add(new Transform("jtp.instrumenter", 
			  new InvokeStaticInstrumenter()));

    /* Give control to Soot to process all options, 
     * InvokeStaticInstrumenter.internalTransform will get called.
     */
    soot.Main.main(args);
  }
}


