package ca.mcgill.sable.soot.jimple.toolkit.invoke;
//import java.util.*;
import java.io.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.soot.grimp.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.baf.*;
// import ca.mcgill.sable.soot.sideEffect.*;
import ca.mcgill.sable.soot.*;
class StringComparator implements Comparator {
   public int compare ( Object o1, Object o2 ) {
      MethodNode mn1 = ( MethodNode ) o1;
      MethodNode mn2 = ( MethodNode ) o2;
      return ( mn1.getMethod().getSignature().compareTo ( mn2.getMethod().getSignature() ) );
   }


   public boolean equals ( Object o1, Object o2 ) {
      MethodNode mn1 = ( MethodNode ) o1;
      MethodNode mn2 = ( MethodNode ) o2;
      return ( mn1.getMethod().getSignature().equals ( mn2.getMethod().getSignature() ) );
   }


}




