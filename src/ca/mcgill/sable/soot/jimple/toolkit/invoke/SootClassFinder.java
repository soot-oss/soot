// package ca.mcgill.sable.soot.virtualCalls;

package ca.mcgill.sable.soot.jimple.toolkit.invoke;
// import java.util.*;
import java.io.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.soot.coffi.*;
// import ca.mcgill.sable.sideEffect.*;

class SootClassFinder {
   Scene cm = Scene.v();
   private ArrayList bclasses = new ArrayList();
   ArrayList getSootClasses (String MainClass) {
      SootClass sootclass = null;
      sootclass = cm.loadClassAndSupport( MainClass );
      bclasses.add(sootclass);
      return bclasses;
   }


}




