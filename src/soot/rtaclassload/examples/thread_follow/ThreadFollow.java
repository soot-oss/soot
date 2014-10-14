package soot.rtaclassload.examples.thread_follow;

import soot.options.Options;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import soot.rtaclassload.RTAClassLoader;
import soot.rtaclassload.RTAType;
import soot.rtaclassload.MethodSignature;
import soot.rtaclassload.Pair;
import soot.rtaclassload.MainEntryMethodTester;
import soot.SootMethod;
import soot.SootClass;
import soot.Scene;
import soot.util.Chain;

public class ThreadFollow {

  public void show(String jarPath){
    Options.v().set_rtaclassload_verbose(true);
    Options.v().set_rtaclassload_callgraph_print(false);
    Options.v().set_rtaclassload_context_sensitive_new_invokes(true);
    Options.v().set_allow_phantom_refs(true);
    Options.v().set_prepend_classpath(true);
    List<String> procesDirectory = new ArrayList<String>();
    procesDirectory.add(jarPath);
    Options.v().set_process_dir(procesDirectory);
    RTAClassLoader.v().addApplicationJar(jarPath);

    RTAClassLoader.v().addEntryMethodTester(new MainEntryMethodTester());
    RTAClassLoader.v().addCallGraphLink("<java.lang.Thread: void start()>", "<java.lang.Runnable: void run()>");
    RTAClassLoader.v().loadNecessaryClasses();

    List<SootMethod> entryPoints = RTAClassLoader.v().getEntryPoints();
    System.out.println("entryPoints: ");
    for(SootMethod sootMethod : entryPoints){
      System.out.println("  "+sootMethod.getSignature());
    }

    System.out.println("application methods: ");
    Chain<SootClass> appClasses = Scene.v().getApplicationClasses();
    for(SootClass appClass : appClasses){
      for(SootMethod appMethod : appClass.getMethods()){
        System.out.println("  "+appMethod.getSignature());
      }
    }
  }

  public static void main(String[] args){
    if(args.length == 0){
      System.out.println("Please pass $soot_path/src/soot/rtaclassload/examples/analysis_jar/dist/analysis.jar as an argument");
      return;
    }
    ThreadFollow threadFollow = new ThreadFollow();
    threadFollow.show(args[0]);
  }
}
