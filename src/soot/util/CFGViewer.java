/* A utility class for generating dot graph file for a control flow graph
 *
 * @author Feng Qian
 */

package soot.util;

import soot.*;
import soot.toolkits.graph.*;
import soot.util.*;
import java.util.*;

public class CFGViewer extends BodyTransformer{

  private String clsname;
  private String methname;

  public static void main(String[] args) {

    /* check the arguments */
    if (args.length != 1) {
      System.err.println("Usage: java MainDriver class[:method]");
      System.exit(0);
    }

    /* add a phase to transformer pack by call Pack.add */
    Pack jtp = Scene.v().getPack("jtp");
    CFGViewer viewer = new CFGViewer();
    jtp.add(new Transform("jtp.cfgviewer", 
			  viewer));
    
    int smpos = args[0].indexOf(':');
    if (smpos == -1) {
      viewer.clsname = args[0]; 
    } else {
      viewer.clsname  = args[0].substring(0, smpos);
      viewer.methname = args[0].substring(smpos+1);
      args[0]  = viewer.clsname;
    }

    soot.Main.setTargetRep(soot.Main.NO_OUTPUT);

    soot.Main.main(args);
  }

  protected void internalTransform(Body body, String phase, Map options) {
    SootMethod method = body.getMethod();
    SootClass  sclass = method.getDeclaringClass();

    if (!sclass.getName().equals(clsname)) {
      return;
    }

    if (methname != null && !method.getName().equals(methname)) {
      return;
    }

    UnitGraph ugraph = new UnitGraph(body, false);
    ugraph.toDotFile(); 
  }
}
