// package ca.mcgill.sable.soot.virtualCalls;

package ca.mcgill.sable.soot.jimple.toolkit.invoke;

//import java.util.*;
import java.io.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.baf.*; 
// import ca.mcgill.sable.soot.sideEffect.*;
import ca.mcgill.sable.soot.*;

public class DTANativeAdjustor {



 public HashMap declaredtypesHT; 

 public RTA rta;



 public DTANativeAdjustor ( HashMap declaredtypesht, RTA rta ) {

  declaredtypesHT = declaredtypesht;

  this.rta = rta;

 }







 public void adjustNativeNode ( String s1, String s2 ) {
  
  if ( ( ( TypeNode ) declaredtypesHT.get ( s1 ) ) != null ) 
  {

   TypeNode tn = ( TypeNode ) declaredtypesHT.get ( s1 );
  
   tn.addInstanceType ( s2 );
  
   adjustSubClasses ( s2, tn );

   declaredtypesHT.put ( s1, tn );
 
  }

 }








 private void adjustSubClasses ( String s, TypeNode tn ) {
      
   ClassNode cn = rta.getClassGraphBuilder().getNode ( s );
      
   Set subclassnodes = rta.getClassGraphBuilder().getAllSubClassesOf ( cn );
       
   Iterator subclassnodesit = subclassnodes.iterator();
       
   while ( subclassnodesit.hasNext() )
   {  
       
    try {
       
    ClassNode subcn = ( ClassNode ) subclassnodesit.next();

    String name = subcn.getSootClass().getName();
    
    tn.addInstanceType ( name );
  
    } catch ( java.lang.RuntimeException e ) {}
       
   }
      
 }

















public void adjustForNativeMethods() {

 String s1 = "return_<'java.lang.Object':'getClass':():java.lang.Class>";
 String s2 = "java.lang.Class"; 
 adjustNativeNode ( s1, s2 );


 try {

 s1 = "return_<'java.lang.Object':'clone':():java.lang.Object>";
 
 s2 = "this_<'java.lang.Object':'clone':():java.lang.Object>";


 if ( ( ( ( TypeNode ) declaredtypesHT.get ( s1 ) ) != null ) && ( ( ( TypeNode ) declaredtypesHT.get ( s2 ) ) != null ) )
 {
 
 TypeNode tn1 = ( TypeNode ) declaredtypesHT.get ( s1  );

 TypeNode tn2 = ( TypeNode ) declaredtypesHT.get ( s2 );
 
 tn2.addForwardNode ( tn1 );

 tn1.addBackwardNode ( tn2 );

 declaredtypesHT.put ( s1, tn1 ); 
 
 declaredtypesHT.put ( s2, tn2 );

 }

} catch ( java.lang.RuntimeException e ) {}



 s1 = "return_<'java.lang.String':'intern':():java.lang.String>";
 s2 = "java.lang.String";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.lang.Throwable':'fillInStackTrace':():java.lang.Throwable>";
 s2 = "java.lang.Throwable";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.lang.Class':'forName':(java.lang.String):java.lang.Class>";
 s2 = "java.lang.Class";
 adjustNativeNode ( s1, s2 );



 s1 = "return_<'java.lang.Class':'newInstance':():java.lang.Object>";
 
 Iterator newinstit = rta.newinstances.iterator();

 while ( newinstit.hasNext() )
 {

  String newinstance = ( String ) newinstit.next();

  adjustNativeNode ( s1, newinstance );

 }



 s1 = "return_<'java.lang.Class':'getName':():java.lang.String>";
 s2 = "java.lang.String";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.lang.Class':'getClassLoader':():java.lang.ClassLoader>";
 s2 = "java.lang.ClassLoader";
 adjustNativeNode ( s1, s2 );


 s1 = "return_<'java.lang.Class':'getSuperclass':():java.lang.Class>";
 s2 = "java.lang.Class";
 adjustNativeNode ( s1, s2 );
 
 s1 = "return_<'java.lang.Class':'getInterfaces':():java.lang.Class[]>";
 s2 = "java.lang.Class";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.lang.Class':'getComponentType':():java.lang.Class>";
 s2 = "java.lang.Class";
 adjustNativeNode ( s1, s2 );


// ADD NATIVE METHOD java.lang.Class.getSigners():java.lang.Object[]
// ADD NATIVE METHOD java.lang.Class.setSigners(java.lang.Object[]):void

 try {

 s1 = "return_<'java.lang.Class':'getSigners':():java.lang.Object[]>";

 s2 = getCorrectParameterType ( "java.lang.Class", "setSigners" , 0 );


 if ( ( ( ( TypeNode ) declaredtypesHT.get ( s1 ) ) != null ) && ( ( ( TypeNode ) declaredtypesHT.get ( s2 ) ) != null ) )
 {

 TypeNode tn1 = ( TypeNode ) declaredtypesHT.get ( s1  );
 
 TypeNode tn2 = ( TypeNode ) declaredtypesHT.get ( s2 );

 tn2.addForwardNode ( tn1 );

 tn1.addBackwardNode ( tn2 );   

 tn1.addForwardNode ( tn2 );

 tn2.addBackwardNode ( tn1 );

 declaredtypesHT.put ( s1, tn1 );

 declaredtypesHT.put ( s2, tn2 );

 }

} catch ( java.lang.RuntimeException e ) {}


 s1 = "return_<'java.lang.Class':'getPrimitiveClass':(java.lang.String):java.lang.Class>";
 s2 = "java.lang.Class";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.lang.Class':'getFields0':(int):java.lang.reflect.Field[]>";
 s2 = "java.lang.reflect.Field";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.lang.Class':'getMethods0':(int):java.lang.reflect.Method[]>";
 s2 = "java.lang.reflect.Method";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.lang.Class':'getConstructors0':(int):java.lang.reflect.Constructor[]>";
 s2 = "java.lang.reflect.Constructor";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.lang.Class':'getField0':(java.lang.String,int):java.lang.reflect.Field>";
 s2 = "java.lang.reflect.Field";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.lang.Class':'getMethod0':(java.lang.String,java.lang.Class[],int):java.lang.reflect.Method>";
 s2 = "java.lang.reflect.Method";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.lang.Class':'getConstructor0':(java.lang.Class[],int):java.lang.reflect.Constructor>";
 s1 = "java.lang.reflect.Constructor";
 adjustNativeNode ( s1, s2 );


// ADD NATIVE METHOD java.lang.System.arraycopy(java.lang.Object,int,java.lang.Object,int,int):void
 
 try {

 s1 = getCorrectParameterType ( "java.lang.System", "arraycopy", 0 );

 s2 = getCorrectParameterType ( "java.lang.System", "arraycopy", 2 );

 if ( ( ( ( TypeNode ) declaredtypesHT.get ( s1 ) ) != null ) && ( ( ( TypeNode ) declaredtypesHT.get ( s2 ) ) != null ) )
 {

 TypeNode tn1 = ( TypeNode ) declaredtypesHT.get ( s1  );

 TypeNode tn2 = ( TypeNode ) declaredtypesHT.get ( s2 );
 
 tn2.addForwardNode ( tn1 );

 tn1.addBackwardNode ( tn2 );

 tn1.addForwardNode ( tn2 );

 tn2.addBackwardNode ( tn1 );

 declaredtypesHT.put ( s1, tn1 ); 
 
 declaredtypesHT.put ( s2, tn2 );

 }

} catch ( java.lang.RuntimeException e ) {}




 s1 = "return_<'java.lang.System':'initProperties':(java.util.Properties):java.util.Properties>";
 s2 = "java.util.Properties";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.lang.Thread':'currentThread':():java.lang.Thread>";
 s2 = "java.lang.Thread";
 adjustNativeNode ( s1, s2 );


// ADD NATIVE METHOD java.lang.reflect.Field.get(java.lang.Object):java.lang.Object
// ADD NATIVE METHOD java.lang.reflect.Field.set(java.lang.Object,java.lang.Object):void


 try {

 s1 = "return_<'java.lang.reflect.Field':'get':(java.lang.Object):java.lang.Object>";

 s2 = getCorrectParameterType ( "java.lang.reflect.Field", "set", 0 );


 if ( ( ( ( TypeNode ) declaredtypesHT.get ( s1 ) ) != null ) && ( ( ( TypeNode ) declaredtypesHT.get ( s2 ) ) != null ) )
 {

 TypeNode tn1 = ( TypeNode ) declaredtypesHT.get ( s1  );
 
 TypeNode tn2 = ( TypeNode ) declaredtypesHT.get ( s2 );

 tn2.addForwardNode ( tn1 );
 
 tn1.addBackwardNode ( tn2 );
 
 tn1.addForwardNode ( tn2 );

 tn2.addBackwardNode ( tn1 );

 declaredtypesHT.put ( s1, tn1 );

 declaredtypesHT.put ( s2, tn2 );

 }

} catch ( java.lang.RuntimeException e ) {}


// ADD NATIVE METHOD java.lang.reflect.Method.invoke(java.lang.Object,java.lang.Object[]):java.lang.Object

// ADD NATIVE METHOD java.lang.reflect.Constructor.newInstance(java.lang.Object[]):java.lang.Object




try {

  // s1 = (String) /* parameterHT.get ( */ "<'java.lang.System':'setErr0':(java.io.PrintStream):void>$0" /* ) */ ;

 s1 = getCorrectParameterType ( "java.lang.System", "setErr0", 0 );

 // s2 = "<'java.lang.System':'err':java.io.PrintStream>";

 s2 = "java.io.PrintStream";

 if ( ( ( ( TypeNode ) declaredtypesHT.get ( s1 ) ) != null ) && ( ( ( TypeNode ) declaredtypesHT.get ( s2 ) ) != null ) )
 {

  TypeNode tn1 = ( TypeNode ) declaredtypesHT.get ( s1  );

  TypeNode tn2 = ( TypeNode ) declaredtypesHT.get ( s2 );
  
  tn1.addForwardNode ( tn2 );

  tn2.addBackwardNode ( tn1 );

  declaredtypesHT.put ( s1, tn1 ); 
 
  declaredtypesHT.put ( s2, tn2 );
 }

} catch ( java.lang.RuntimeException e ) {}




 try {

 s1 = getCorrectParameterType ( "java.lang.System", "setOut0", 0 );

 // s2 = "<'java.lang.System':'err':java.io.PrintStream>";

 s2 = "java.io.PrintStream";

 if ( ( ( ( TypeNode ) declaredtypesHT.get ( s1 ) ) != null ) && ( ( ( TypeNode ) declaredtypesHT.get ( s2 ) ) != null ) )
 {

  TypeNode tn1 = ( TypeNode ) declaredtypesHT.get ( s1  );

  TypeNode tn2 = ( TypeNode ) declaredtypesHT.get ( s2 );

  tn1.addForwardNode ( tn2 );

  tn2.addBackwardNode ( tn1 );

  declaredtypesHT.put ( s1, tn1 ); 
 
  declaredtypesHT.put ( s2, tn2 );
 }

} catch ( java.lang.RuntimeException e ) {}





 try {

 s1 = getCorrectParameterType ( "java.lang.System", "setIn0", 0 );

 s2 = "java.io.InputStream";

 if ( ( ( ( TypeNode ) declaredtypesHT.get ( s1 ) ) != null ) && ( ( ( TypeNode ) declaredtypesHT.get ( s2 ) ) != null ) )
 {

  TypeNode tn1 = ( TypeNode ) declaredtypesHT.get ( s1  );

  TypeNode tn2 = ( TypeNode ) declaredtypesHT.get ( s2 );
  
  tn1.addForwardNode ( tn2 );

  tn2.addBackwardNode ( tn1 );

  declaredtypesHT.put ( s1, tn1 ); 
 
  declaredtypesHT.put ( s2, tn2 );
 }

} catch ( java.lang.RuntimeException e ) {}























 s1 = "return_<'java.lang.ClassLoader':'defineClass0':(java.lang.String,byte[],int,int):java.lang.Class>";
 s2 = "java.lang.Class";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.lang.ClassLoader':'findSystemClass0':(java.lang.String):java.lang.Class>";
 s2 = "java.lang.Class";
 adjustNativeNode ( s1, s2 );

// ADD NATIVE METHOD java.lang.ClassLoader.getSystemResourceAsStream0(java.lang.String):java.io.InputStream

 s1 = "return_<'java.lang.ClassLoader':'getSystemResourceAsName0':(java.lang.String):java.lang.String>";
 s2 = "java.lang.String";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.lang.SecurityManager':'getClassContext':():java.lang.Class[]>";
 s2 = "java.lang.Class";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.lang.SecurityManager':'currentClassLoader':():java.lang.ClassLoader>";
 s2 = "java.lang.ClassLoader";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.lang.SecurityManager':'currentLoadedClass0':():java.lang.Class>";
 s2 = "java.lang.Class";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.io.ObjectInputStream':'loadClass0':(java.lang.Class,java.lang.String):java.lang.Class>";
 s2 = "java.lang.Class";
 adjustNativeNode ( s1, s2 );


// ADD NATIVE METHOD java.io.ObjectInputStream.inputClassFields(java.lang.Object,java.lang.Class,int[]):void
// ADD NATIVE METHOD java.io.ObjectInputStream.allocateNewObject(java.lang.Class,java.lang.Class):java.lang.Object
// ADD NATIVE METHOD java.io.ObjectInputStream.allocateNewArray(java.lang.Class,int):java.lang.Object
// ADD NATIVE METHOD java.io.ObjectInputStream.invokeObjectReader(java.lang.Object,java.lang.Class):boolean

// ADD NATIVE METHOD java.lang.Runtime.execInternal(java.lang.String[],java.lang.String[]):java.lang.Process

 s1 = "return_<'java.lang.Runtime':'initializeLinkerInternal':():java.lang.String>";
 s2 = "java.lang.String";
 adjustNativeNode ( s1, s2 );


 s1 = "return_<'java.lang.Runtime':'buildLibName':(java.lang.String,java.lang.String):java.lang.String>";
 s2 = "java.lang.String";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.io.FileDescriptor':'initSystemFD':(java.io.FileDescriptor,int):java.io.FileDescriptor>";
 s2 = "java.io.FileDescriptor";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.util.ResourceBundle':'getClassContext':():java.lang.Class[]>";
 s2 = "java.lang.Class";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.io.File':'list0':():java.lang.String[]>";
 s2 = "java.lang.String";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.io.File':'canonPath':(java.lang.String):java.lang.String>";
 s2 = "java.lang.String";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.io.ObjectStreamClass':'getMethodSignatures':(java.lang.Class):java.lang.String[]>";
 s2 = "java.lang.String";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.io.ObjectStreamClass':'getFieldSignatures':(java.lang.Class):java.lang.String[]>";
 s2 = "java.lang.String";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.io.ObjectStreamClass':'getFields0':(java.lang.Class):java.io.ObjectStreamField[]>";
 s2 = "java.io.ObjectStreamField";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.net.InetAddressImpl':'getLocalHostName':():java.lang.String>";
 s2 = "java.lang.String";
 adjustNativeNode ( s1, s2 );

 s1 = "return_<'java.net.InetAddressImpl':'getHostByAddr':(int):java.lang.String>";
 s2 = "java.lang.String";
 adjustNativeNode ( s1, s2 );

 }

 
 Scene cm = Scene.v();



 public String getCorrectParameterType ( String classname, String methodname, int i ) {

  SootClass sc = cm.getClass ( classname );

  String formaltype = null;

  List methods = sc.getMethods();

  Iterator methodsit = methods.iterator();

  while ( methodsit.hasNext() )
  {

   SootMethod method = ( SootMethod ) methodsit.next();  

   if ( method.getName().equals ( methodname ) )
   formaltype = method.getParameterType(i).toString();

  }

  return formaltype;

 }




}





