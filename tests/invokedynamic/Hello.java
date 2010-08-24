import java.dyn.*;

public class Hello {
  static Hello h;
  MethodHandle greeter;

  public static void main(String... av) throws Throwable {
    if (av.length == 0)  av = new String[] { "world" };
    h = new Hello();
    h.greeter = MethodHandles.lookup().bind(h, "greet", MethodType.make(int.class, String.class));
    for (String whom : av) {
      //ein direkter, statisch typisierter aufruf der Methode auf dem Methodenhandle erzeugt ein invoke virtual
      h.greeter.<int>invoke(whom + " (from typed direct call)");
      //mit IvokeDynamic."Name unter der die Methode registriert ist" kann ein schwach typiserter aufruf gemacht werden, dieser erzeugt ein invokedynamic bytecode
      //InvokeDynamic ist dabei ein syntaktischer marker fuer javac ein invokedynamic bytecode zu erzeugen
      Object x = whom;
      int i = InvokeDynamic.<int>hail(x);   
      //erzeugt invokedynamic MethodHandle.invoke(Object)Object
    }
  }

	int greet(String x) { System.out.println("Hello, " + x); return 0; }
  
  //es wird ein MethodHandle fuer die methode benoetigt, dies kann man mit MethodHandles.lookup() bekommen
  //static MethodHandle greeter = MethodHandles.lookup().findStatic(Hello.class, "greeter",
  //                                                                MethodType.make(void.class, String.class));

  //damit dynamische aufrufe moeglich sind, muss eine bootstrapmethode definiert werden
  //diese Methode muss dem Linkage objekt bekannt gemacht werden, damit die Methode fuer die Aufloesung aufgerufen werden kann
  static { Linkage.registerBootstrapMethod("bootstrapDynamic"); }

  //die bootstrap methode uebernimmt den caller, den namen fuer die methode und den datentyp der methode und gibt eine Aufrufstelle in form eines CallSite Objekts zurueck
  private static CallSite bootstrapDynamic(Class caller, String name, MethodType type) {
    assert(type.parameterCount() == 1 && (Object)name == "hail");
    System.out.println("set target to adapt "+h.greeter);
    //hier wird das methodhandle konvertiert, dass es dem datentyp der methode entspricht, dazu wird wieder methodhandles verwendet
    MethodHandle target = MethodHandles.convertArguments(h.greeter, type);
    //es wird eine aufrufstelle erzeugt die den caller, den methoden namen und den methodentyp kennt
    CallSite site = new CallSite(caller, name, type);
    //als ziel der aufrufstelle wird das methodenhandle, das ebn bestimmt wurde angegeben.
    site.setTarget(target);
    //ie aufrufstelle wird zurueckgegeben
    return site;
  }
}
