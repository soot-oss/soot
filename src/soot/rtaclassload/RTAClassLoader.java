/* Soot - a J*va Optimization Framework
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.rtaclassload;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import org.apache.commons.collections4.trie.PatriciaTrie;

import soot.SourceLocator;
import soot.Scene;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import soot.Singletons;
import soot.G;
import soot.SootMethod;
import soot.SootClass;
import soot.util.Chain;
import soot.Type;
import soot.SootField;
import soot.options.Options;
import soot.Modifier;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

public class RTAClassLoader {

  private Set<String> entryMethodSignatures;
  private Set<String> entryMethodSubSignatures;
  private List<EntryMethodTester> entryMethodTesters;
  private List<MethodTester> dontFollowMethodTesters;
  private List<MethodTester> toSignaturesMethodTesters;
  private Map<String, List<String>> callGraphLinks;

  private List<ClassTester> dontFollowClassTesters;
  private List<ClassTester> toSignaturesClassTesters;

  private Set<MethodSignature> cgVisitedMethods;
  private LinkedList<Pair<MethodSignature, Set<RTAType>>> cgMethodQueue;
  private List<Pair<MethodSignature, Set<RTAType>>> entryPoints;

  private List<String> sourcePaths;
  private List<String> classPaths;

  private Set<RTAType> applicationClasses;
  private Set<String> applicationJARs;
  private Map<String, Boolean> jarPaths;
  private Set<RTAType> newInvokes;
  private Set<MethodSignature> allMethods;
  private Set<MethodSignature> signaturesMethods;
  private Set<MethodSignature> bodyMethods;
  private Set<FieldSignature> allFields;
  private Set<String> forcedLoadedFieldSignatures;
  private Map<RTAType, Integer> rtaTypeToJar;

  private Map<String, String> classRemappings;
  private Map<String, String> reverseClassRemappings;

  private Map<RTAType, byte[]> classNumberToContents;

  private Map<RTAType, RTAClass> rtaClasses;
  private Map<RTAMethod, RTAMethodVisitor> rtaMethodVisitorMap;

  private Set<String> followMethods;
  private Set<String> toSignaturesMethods;
  private Set<String> followClasses;
  private Set<String> toSignaturesClasses;
  private Set<String> toHierarchyClasses;

  private RTAClassHierarchy classHierarchy;
  private List<NumberedType> numberedClasses;
  private Set<String> modifiedClasses;

  private Map<RTAType, Set<RTAType>> hierarchyTypeCache;

  private boolean verbose;
  private boolean callGraphPrint;
  private boolean contextSensitiveNewInvokes;
  private boolean allowPhantomRefs;

  public static RTAClassLoader v() {
    return G.v().soot_rtaclassload_RTAClassLoader();
  }

  public RTAClassLoader(Singletons.Global g){

    entryMethodSignatures = new HashSet<String>();
    entryMethodSubSignatures = new HashSet<String>();

    entryMethodTesters = new ArrayList<EntryMethodTester>();
    dontFollowMethodTesters = new ArrayList<MethodTester>();
    toSignaturesMethodTesters = new ArrayList<MethodTester>();

    dontFollowClassTesters = new ArrayList<ClassTester>();
    toSignaturesClassTesters = new ArrayList<ClassTester>();
    callGraphLinks = new HashMap<String, List<String>>();

    cgVisitedMethods = new HashSet<MethodSignature>();
    cgMethodQueue = new LinkedList<Pair<MethodSignature, Set<RTAType>>>();
    entryPoints = new ArrayList<Pair<MethodSignature, Set<RTAType>>>();
    applicationClasses = new HashSet<RTAType>();
    applicationJARs = new HashSet<String>();

    jarPaths = new HashMap<String, Boolean>();
    newInvokes = new HashSet<RTAType>();
    classNumberToContents = new HashMap<RTAType, byte[]>();

    rtaClasses = new HashMap<RTAType, RTAClass>();
    rtaMethodVisitorMap = new HashMap<RTAMethod, RTAMethodVisitor>();
    classRemappings = new HashMap<String, String>();
    reverseClassRemappings = new HashMap<String, String>();

    allMethods = new HashSet<MethodSignature>();
    signaturesMethods = new HashSet<MethodSignature>();
    bodyMethods = new HashSet<MethodSignature>();
    allFields = new HashSet<FieldSignature>();

    followMethods = new HashSet<String>();
    toSignaturesMethods = new HashSet<String>();
    followClasses = new HashSet<String>();
    toSignaturesClasses = new HashSet<String>();
    toHierarchyClasses = new HashSet<String>();
    forcedLoadedFieldSignatures = new HashSet<String>();
    modifiedClasses = new HashSet<String>();

    classHierarchy = new RTAClassHierarchy();
    rtaTypeToJar = new HashMap<RTAType, Integer>();

    hierarchyTypeCache = new HashMap<RTAType, Set<RTAType>>();

    verbose = Options.v().rtaclassload_verbose();
    callGraphPrint = Options.v().rtaclassload_callgraph_print();
    contextSensitiveNewInvokes = Options.v().rtaclassload_context_sensitive_new_invokes();
    allowPhantomRefs = Options.v().allow_phantom_refs();
  }

  public void setCallGraphPrint(boolean value){
    callGraphPrint = value;
  }

  public void addEntryMethodSignature(String signature){
    entryMethodSignatures.add(signature);
  }

  public void addEntryMethodSubSignature(String subSignature){
    entryMethodSubSignatures.add(subSignature);
  }

  public void addEntryMethodTester(EntryMethodTester method_tester){
    entryMethodTesters.add(method_tester);
  }

  public void addDontFollowMethodTester(MethodTester method_tester){
    dontFollowMethodTesters.add(method_tester);
  }

  public void addToSignaturesMethodTester(MethodTester method_tester){
    toSignaturesMethodTesters.add(method_tester);
  }

  public void addDontFollowClassTester(ClassTester class_tester){
    dontFollowClassTesters.add(class_tester);
  }

  public void addToSignaturesClassTester(ClassTester class_tester){
    toSignaturesClassTesters.add(class_tester);
  }

  public void addCallGraphLink(String sourceMethodSignature, String destMethodSignature){
    List<String> destinations;
    if(callGraphLinks.containsKey(sourceMethodSignature)){
      destinations = callGraphLinks.get(sourceMethodSignature);
    } else {
      destinations = new ArrayList<String>();
      callGraphLinks.put(sourceMethodSignature, destinations);
    }
    destinations.add(destMethodSignature);
  }

  public void addClassRemapping(String originalClass, String newClass){
    classRemappings.put(originalClass, newClass);
    reverseClassRemappings.put(newClass, originalClass);
  }

  public void addNewInvoke(String className){
    newInvokes.add(RTAType.create(className));
  }

  public void loadField(String fieldSignature){
    forcedLoadedFieldSignatures.add(fieldSignature);
  }

  public void addModifiedClass(String className){
    modifiedClasses.add(className);
  }

  public Map<String, String> getReverseClassRemappings(){
    return reverseClassRemappings;
  }

  public void addApplicationJar(String filename){
    applicationJARs.add(filename);
  }

  public List<SootMethod> getEntryPoints(){
    MethodFieldFinder finder = new MethodFieldFinder();
    List<SootMethod> ret = new ArrayList<SootMethod>();
    for(Pair<MethodSignature, Set<RTAType>> entryPoint : entryPoints){
      MethodSignature methodSig = entryPoint.getKey();
      SootMethod sootMethod = finder.findMethod(methodSig.toString());
      ret.add(sootMethod);
    }
    return ret;
  }

  public List<NumberedType> getNumberedTypes(){
    return numberedClasses;
  }

  public void loadNecessaryClasses(){
    sourcePaths = SourceLocator.v().sourcePath();
    classPaths = SourceLocator.v().classPath();

    loadBuiltIns();
    loadHierarchy();
    remapClasses();
    findEntryPoints();
    loadToSignaturesString();
    callGraphFixedPoint();
    loadHierarchyClasses();
    loadSignaturesClasses();
    loadForcedFields();
    loadScene();
    Scene.v().loadDynamicClasses();
    buildCallGraph();
  }

  private void addJarPath(String path, boolean appClass){
    path = normalizePathElement(path);
    if(path.endsWith(".jar")){
      File jarFile = new File(path);
      if(jarFile.exists()){
        jarPaths.put(path, appClass);
      }
    }
  }

  public byte[] readFully(JarInputStream jin) throws IOException {
    List<byte[]> returnList = new ArrayList<byte[]>();
    int returnCount = 0;
    int bufferSize = 4096;
    while(true){
      byte[] currArray = new byte[bufferSize];
      int readLength = jin.read(currArray, 0, bufferSize);
      if(readLength == -1){
        break;
      }
      byte[] sizedArray = new byte[readLength];
      System.arraycopy(currArray, 0, sizedArray, 0, readLength);
      returnList.add(sizedArray);
      returnCount += readLength;
    }
    byte[] ret = new byte[returnCount];
    int offset = 0;
    for(byte[] array : returnList){
      System.arraycopy(array, 0, ret, offset, array.length);
      offset += array.length;
    }
    return ret;
  }

  private void loadBuiltIns(){
    addHierarchyClass("java.lang.Object");
    addSignaturesClass("java.lang.Class");

    addSignaturesClass("java.lang.Void");
    addSignaturesClass("java.lang.Boolean");
    addSignaturesClass("java.lang.Byte");
    addSignaturesClass("java.lang.Character");
    addSignaturesClass("java.lang.Short");
    addSignaturesClass("java.lang.Integer");
    addSignaturesClass("java.lang.Long");
    addSignaturesClass("java.lang.Float");
    addSignaturesClass("java.lang.Double");

    addHierarchyClass("java.lang.String");
    addSignaturesClass("java.lang.StringBuffer");

    addSignaturesClass("java.lang.Error");
    addSignaturesClass("java.lang.AssertionError");
    addSignaturesClass("java.lang.Throwable");
    addSignaturesClass("java.lang.NoClassDefFoundError");
    addHierarchyClass("java.lang.ExceptionInInitializerError");
    addHierarchyClass("java.lang.RuntimeException");
    addHierarchyClass("java.lang.ClassNotFoundException");
    addHierarchyClass("java.lang.ArithmeticException");
    addHierarchyClass("java.lang.ArrayStoreException");
    addHierarchyClass("java.lang.ClassCastException");
    addHierarchyClass("java.lang.IllegalMonitorStateException");
    addHierarchyClass("java.lang.IndexOutOfBoundsException");
    addHierarchyClass("java.lang.ArrayIndexOutOfBoundsException");
    addHierarchyClass("java.lang.NegativeArraySizeException");
    addHierarchyClass("java.lang.NullPointerException");
    addHierarchyClass("java.lang.InstantiationError");
    addHierarchyClass("java.lang.InternalError");
    addHierarchyClass("java.lang.OutOfMemoryError");
    addHierarchyClass("java.lang.StackOverflowError");
    addHierarchyClass("java.lang.UnknownError");
    addHierarchyClass("java.lang.ThreadDeath");
    addHierarchyClass("java.lang.ClassCircularityError");
    addHierarchyClass("java.lang.ClassFormatError");
    addHierarchyClass("java.lang.IllegalAccessError");
    addHierarchyClass("java.lang.IncompatibleClassChangeError");
    addHierarchyClass("java.lang.LinkageError");
    addHierarchyClass("java.lang.VerifyError");
    addHierarchyClass("java.lang.NoSuchFieldError");
    addHierarchyClass("java.lang.AbstractMethodError");
    addHierarchyClass("java.lang.NoSuchMethodError");
    addHierarchyClass("java.lang.UnsatisfiedLinkError");

    addHierarchyClass("java.lang.Thread");
    addHierarchyClass("java.lang.Runnable");
    addHierarchyClass("java.lang.Cloneable");

    addHierarchyClass("java.io.Serializable");
    addHierarchyClass("java.lang.ref.Finalizer");
    addHierarchyClass("java.lang.ref.FinalReference");
  }

  public void addHierarchyClass(String className){
    toHierarchyClasses.add(className);
  }

  public void addSignaturesClass(String className){
    toSignaturesClasses.add(className);
  }

  private void log(String str){
    if(verbose){
      System.out.println(str);
    }
  }

  private void loadHierarchy(){

    for(String path : classPaths){
      addJarPath(path, false);
    }
    for(String appJar : applicationJARs){
      addJarPath(appJar, true);
    }

    for(String jarPath : jarPaths.keySet()){
      boolean applicationJar = jarPaths.get(jarPath);
      log("[rtaclassload] caching package names for: "+jarPath+" appJar: "+applicationJar);
      int jarNumber = StringNumbers.v().addString(jarPath);
      try {
        JarInputStream jin = new JarInputStream(new FileInputStream(jarPath));
        while(true){
          JarEntry entry = jin.getNextJarEntry();
          if(entry == null){
            break;
          }
          String filename = entry.getName();
          if(filename.endsWith(".class")){
            String name = filename.replace(".class", "");
            name = name.replace("/", ".");
            RTAType fullClass = RTAType.create(name);
            rtaTypeToJar.put(fullClass, jarNumber);

            if(applicationJar){
              applicationClasses.add(fullClass);
            }

            byte[] classContents = readFully(jin);
            String superClass = SuperClassReader.read(classContents);
            classNumberToContents.put(fullClass, classContents);
            RTAType fullSuperClass = RTAType.create(superClass);

            fullClass.setSuperClass(fullSuperClass);
            fullSuperClass.addSubClass(fullClass);
          } else if (filename.endsWith(".jar")){
            //TODO: go into jar
          }
        }
        jin.close();
      } catch(Exception ex){
        ex.printStackTrace();
      }
    }
  }

  public List<RTAType> getSubClasses(RTAType rtaType){
    return rtaType.getSubClasses();
  }

  private void findEntryPoints(){
    log("[rtaclassload] finding entry points...");
    for(RTAType type : applicationClasses){
      RTAClass rtaClass = getRTAClass(type);
      RTAMethod[] methods = rtaClass.getMethods();
      for(RTAMethod method : methods){
        if(entryMethodSignatures.contains(method.getSignature().toString())){
          MethodSignature methodSig = method.getSignature();
          Set<RTAType> entryNewInvokes = new HashSet<RTAType>();
          entryNewInvokes.add(methodSig.getClassName());
          Pair<MethodSignature, Set<RTAType>> pair =
            new Pair<MethodSignature, Set<RTAType>>(methodSig, entryNewInvokes);
          entryPoints.add(pair);
        }
        if(entryMethodSubSignatures.contains(method.getSignature().getSubSignatureString())){
          MethodSignature methodSig = method.getSignature();
          Set<RTAType> entryNewInvokes = new HashSet<RTAType>();
          entryNewInvokes.add(methodSig.getClassName());
          Pair<MethodSignature, Set<RTAType>> pair =
            new Pair<MethodSignature, Set<RTAType>>(methodSig, entryNewInvokes);
          entryPoints.add(pair);
        }
        for(EntryMethodTester methodTester : entryMethodTesters){
          if(methodTester.matches(method)){
            Set<String> stringNewInvokes = methodTester.getNewInvokes();
            Set<RTAType> entryNewInvokes = new HashSet<RTAType>();
            for(String stringNewInvoke : stringNewInvokes){
              RTAType rtaType = RTAType.create(stringNewInvoke);
              entryNewInvokes.add(rtaType);
            }
            MethodSignature methodSig = method.getSignature();
            Pair<MethodSignature, Set<RTAType>> pair =
              new Pair<MethodSignature, Set<RTAType>>(methodSig, entryNewInvokes);
            entryPoints.add(pair);
          }
        }
      }
    }
  }

  private void remapClasses(){
    //TODO: remap classes
    /*
    for(String originalClass : classRemappings.keySet()){
      System.out.println("remapping class: "+originalClass);
      String newClass = classRemappings.get(originalClass);

      RTAClass originalRTAClass = getRTAClass(phase0ClassNumber);
      RTAClass newRTAClass = getRTAClass(phase0ClassNumber);

      newRTAClass.setName(originalClass);
      newRTAClass.setApplicationClass(originalRTAClass.isApplicationClass());
      m_classHierarchy.put(original_hclass.getClassNumber(), new_hclass);
    }
    */
  }

  private void loadToSignaturesString(){
    log("[rtaclassload] loading to_signatures strings...");
    loadMethodStrings(toSignaturesMethodTesters, toSignaturesMethods);
    loadClassStrings(toSignaturesClassTesters, toSignaturesClasses);
  }

  private void loadMethodStrings(List<MethodTester> testers, Set<String> dest){
    for(RTAType classNumber : classNumberToContents.keySet()){
      for(MethodTester tester : testers){
        RTAClass rtaClass = getRTAClass(classNumber);
        RTAMethod[] methods = rtaClass.getMethods();
        for(RTAMethod method : methods){
          if(tester.matches(method)){
            dest.add(method.getSignature().toString());
          }
        }
      }
    }
  }

  private void loadClassStrings(List<ClassTester> testers, Set<String> dest){
    for(RTAType classNumber : classNumberToContents.keySet()){
      for(ClassTester tester : testers){
        RTAClass rtaClass = getRTAClass(classNumber);
        if(tester.matches(rtaClass)){
          dest.add(rtaClass.getName());
        }
      }
    }
  }

  private void callGraphFixedPoint(){
    callGraphFixedPoint(entryPoints);
  }

  public Set<MethodSignature> callGraphFixedPoint(List<Pair<MethodSignature, Set<RTAType>>> entryPairs){
    Set<MethodSignature> ret = new HashSet<MethodSignature>();
    int prevSize = -1;
    while(prevSize != newInvokes.size()){
      prevSize = newInvokes.size();
      for(Pair<MethodSignature, Set<RTAType>> entry : entryPairs){
        MethodSignature signature = entry.getKey();
        log("[rtaclassload] callGraphFixedPoint: "+signature.toString());
        callGraphForward(entry);
        ret.addAll(cgVisitedMethods);
      }
    }
    return ret;
  }

  private void addPhantomRef(RTAType type){
    type = type.getNonArray();
    RTAClass rtaClass = new RTAClass(type, true);
    type.setRTAClass(rtaClass);
  }

  public RTAClass getRTAClass(RTAType type, boolean create){
    RTAClass ret = type.getRTAClass();
    if(ret != null){
      return ret;
    } else {
      if(type.isArray()){
        RTAClass arrayClass = new RTAClass(type, true);
        type.setRTAClass(arrayClass);
        return arrayClass;
      } else {
        if(classNumberToContents.containsKey(type) == false){
          if(create == false){
            return null;
          }
          if(allowPhantomRefs){
            System.out.println("adding phantom class: "+type.toString());
            addPhantomRef(type);
            return type.getRTAClass();
          } else {
            throw new RuntimeException("Cannot find class "+type.toString()+" and not allowing phantom refs");
          }
        }
        byte[] classContents = classNumberToContents.get(type);
        RTAClass rtaClass = new RTAClass(type, classContents);
        type.setRTAClass(rtaClass);
        return rtaClass;
      }
    }
  }

  public RTAClass getRTAClass(RTAType type){
    return getRTAClass(type, true);
  }

  public RTAClass getRTAClass(String fullClassName){
    RTAType type = RTAType.create(fullClassName);
    return getRTAClass(type.getNonArray());
  }

  public boolean dontFollow(MethodSignature signature){
    if(dontFollowMethod(signature)){
      return true;
    }
    if(dontFollowClass(signature.getClassName())){
      return true;
    }
    return false;
  }

  private boolean dontFollowMethod(MethodSignature signature){
    RTAType classNumber = signature.getClassName();
    RTAClass rtaClass = getRTAClass(classNumber);
    RTAMethod rtaMethod = rtaClass.getMethod(signature);
    if(rtaMethod == null){
      return false;
    }
    return testMethod(dontFollowMethodTesters, rtaMethod);
  }

  private boolean dontFollowClass(RTAType classNumber) {
    RTAClass rtaClass = getRTAClass(classNumber);
    if(rtaClass == null){
      return false;
    }
    return testClass(dontFollowClassTesters, rtaClass);
  }

  private boolean testMethod(List<MethodTester> testers, RTAMethod rtaMethod){
    for(MethodTester tester : testers){
      if(tester.matches(rtaMethod)){
        return true;
      }
    }
    return false;
  }

  private boolean testClass(List<ClassTester> testers, RTAClass rtaClass){
    for(ClassTester tester : testers){
      if(tester.matches(rtaClass)){
        return true;
      }
    }
    return false;
  }

  private String cgQueueString(Pair<MethodSignature, Set<RTAType>> pair){
    MethodSignature methodSig = pair.getKey();
    Set<RTAType> contextNewInvokes = pair.getValue();
    List<RTAType> newInvokesList = new ArrayList<RTAType>();
    newInvokesList.addAll(contextNewInvokes);
    StringBuilder ret = new StringBuilder();
    ret.append("sig: ");
    ret.append(methodSig.toString());
    ret.append(" newInvokes: [");
    for(int i = 0; i < newInvokesList.size(); ++i){
      RTAType type = newInvokesList.get(i);
      ret.append(type.toString());
      if(i < newInvokesList.size() - 1){
        ret.append(", ");
      }
    }
    ret.append("]");
    return ret.toString();
  }

  private List<Pair<MethodSignature, Set<RTAType>>> followMethodsPairs(){
    List<Pair<MethodSignature, Set<RTAType>>> ret =
      new ArrayList<Pair<MethodSignature, Set<RTAType>>>();

    for(String followMethod : followMethods){
      MethodSignature methodSignature = new MethodSignature(followMethod);
      Pair<MethodSignature, Set<RTAType>> pair =
        new Pair<MethodSignature, Set<RTAType>>(methodSignature, new HashSet<RTAType>());
      ret.add(pair);
    }

    return ret;
  }

  private List<Pair<MethodSignature, Set<RTAType>>> followClassesPairs(){
    List<Pair<MethodSignature, Set<RTAType>>> ret =
      new ArrayList<Pair<MethodSignature, Set<RTAType>>>();

    for(String followClass : followClasses){
      RTAType followClassType = RTAType.create(followClass);
      RTAClass rtaClass = getRTAClass(followClassType);
      for(RTAMethod rtaMethod : rtaClass.getMethods()){
        MethodSignature methodSignature = rtaMethod.getSignature();
        Pair<MethodSignature, Set<RTAType>> pair =
          new Pair<MethodSignature, Set<RTAType>>(methodSignature, new HashSet<RTAType>());
        ret.add(pair);
      }
    }

    return ret;
  }

  private void callGraphForward(Pair<MethodSignature, Set<RTAType>> entry){
    cgVisitedMethods.clear();
    cgMethodQueue.add(entry);
    cgMethodQueue.addAll(followMethodsPairs());
    cgMethodQueue.addAll(followClassesPairs());

    while(cgMethodQueue.isEmpty() == false){
      Pair<MethodSignature, Set<RTAType>> pair = cgMethodQueue.removeFirst();

      MethodSignature methodSig = pair.getKey();
      Set<RTAType> contextNewInvokes = pair.getValue();
      newInvokes.addAll(contextNewInvokes);

      if(dontFollow(methodSig)){
        continue;
      }

      if(cgVisitedMethods.contains(methodSig)){
        continue;
      }
      cgVisitedMethods.add(methodSig);

      if(callGraphPrint){
        System.out.println("[callGraphForward] "+cgQueueString(pair));
      }

      allMethods.add(methodSig);
      classHierarchy.addType(methodSig.getClassName());
      classHierarchy.addType(methodSig.getReturnType());
      classHierarchy.addTypes(methodSig.getParameterTypes());

      if(callGraphLinks.containsKey(methodSig.toString())){
        List<String> destinations = callGraphLinks.get(methodSig.toString());
        if(contextSensitiveNewInvokes){
          addCallGraphLinks(destinations, contextNewInvokes);
        } else {
          addCallGraphLinks(destinations, newInvokes);
        }
      }

      RTAType classNumber = methodSig.getClassName();
      if(classNumber.isArray()){
        continue;
      }
      RTAClass rtaClass = getRTAClass(classNumber);
      RTAMethod rtaMethod = rtaClass.findMethodBySubSignature(methodSig.getSubSignatureString());
      if(rtaMethod == null){
        throw new RuntimeException("Cannot find method "+methodSig.toString()+". Try adding more jars to classpath or enable phantom refs");
      }

      if(rtaMethod.isConcrete()){
        RTAMethodVisitor visitor = getMethodVisitor(rtaMethod);

        classHierarchy.addTypes(visitor.getAllTypes());
        allMethods.addAll(visitor.getMethodRefs());
        allFields.addAll(visitor.getFieldRefs());

        Set<RTAType> methodInvokes = visitor.getNewInvokes();
        Set<RTAType> forwardNewInvokes = new HashSet<RTAType>();
        forwardNewInvokes.addAll(methodInvokes);
        forwardNewInvokes.addAll(contextNewInvokes);
        newInvokes.addAll(methodInvokes);

        Set<MethodSignature> methodRefs = visitor.getMethodRefs();
        for(MethodSignature methodRef : methodRefs){
          cgMethodEnqueue(methodRef, forwardNewInvokes);
        }
      }

      List<MethodSignature> virtualMethods;
      if(contextSensitiveNewInvokes){
        virtualMethods = getVirtualMethods(methodSig, contextNewInvokes);
      } else {
        virtualMethods = getVirtualMethods(methodSig, newInvokes);
      }
      for(MethodSignature virtualMethod : virtualMethods){
        cgMethodEnqueue(virtualMethod, contextNewInvokes);
      }
    }
  }

  private void addCallGraphLinks(List<String> destinations, Set<RTAType> currentNewInvokes){
    for(String destination : destinations){
      MethodSignature methodSignature = new MethodSignature(destination);
      RTAType declaringClass = methodSignature.getClassName();
      RTAClass rtaClass = getRTAClass(declaringClass);
      if(rtaClass.isInterface()){
        String subsig = methodSignature.getSubSignatureString();
        for(RTAType newInvoke : currentNewInvokes){
          RTAClass newInvokeClass = getRTAClass(newInvoke);
          if(newInvokeClass.implementsInterface(declaringClass)){
            RTAMethod rtaMethod = newInvokeClass.findMethodBySubSignature(subsig);
            cgMethodEnqueue(rtaMethod.getSignature(), currentNewInvokes);
          }
        }
      } else {
        if(currentNewInvokes.contains(declaringClass)){
          cgMethodEnqueue(methodSignature, currentNewInvokes);
        }
      }
    }
  }

  private List<MethodSignature> getVirtualMethods(MethodSignature methodSig,
    Set<RTAType> contextNewInvokes){

    List<MethodSignature> ret = new ArrayList<MethodSignature>();
    String methodName = StringNumbers.v().getString(methodSig.getMethodName());
    if(methodName.equals("<init>") || methodName.equals("<clinit>")){
      ret.add(methodSig);
      return ret;
    }

    RTAType methodSigClass = methodSig.getClassName();
    Set<RTAType> hierarchyTypes = getHierarchyTypes(methodSigClass);

    for(RTAType newInvoke : contextNewInvokes){
      if(hierarchyTypes.contains(newInvoke) == false){
        continue;
      }

      RTAClass rtaClass = getRTAClass(newInvoke);
      for(RTAMethod method : rtaClass.getMethods()){
        if(method.getSignature().covarientMatch(methodSig)){
          ret.add(method.getSignature());
        }
      }
    }

    return ret;
  }

  private Set<RTAType> getHierarchyTypes(RTAType rtaType){
    if(hierarchyTypeCache.containsKey(rtaType)){
      return hierarchyTypeCache.get(rtaType);
    }
    List<RTAType> retList = new ArrayList<RTAType>();
    Set<RTAType> ret = new HashSet<RTAType>();
    List<RTAType> queue = new ArrayList<RTAType>();
    int queueIndex = 0;
    RTAType superClass = rtaType.getSuperClass();
    if(superClass != null){
      queue.add(superClass);
    }
    while(queueIndex < queue.size()){
      RTAType curr = queue.get(queueIndex);
      ++queueIndex;
      retList.add(curr);
      superClass = curr.getSuperClass();
      if(superClass != null){
        queue.add(superClass);
      }
    }
    queue.add(rtaType);
    while(queueIndex < queue.size()){
      RTAType curr = queue.get(queueIndex);
      ++queueIndex;
      retList.add(curr);
      queue.addAll(curr.getSubClasses());
    }
    ret.addAll(retList);
    hierarchyTypeCache.put(rtaType, ret);
    return ret;
  }

  private void addHierarchy(RTAType className){
    if(className.isRefType() == false){
      return;
    }
    LinkedList<RTAType> queue = new LinkedList<RTAType>();
    queue.add(className);

    while(queue.isEmpty() == false){
      RTAType item = queue.removeFirst();
      classHierarchy.addType(item);

      RTAClass rtaClass = getRTAClass(item);
      if(rtaClass.hasSuperClass()){
        queue.add(rtaClass.getSuperClass());
      }
    }
  }

  private void loadHierarchyClasses(){
    for(String className : toHierarchyClasses){
      addHierarchy(RTAType.create(className));
    }
  }

  private void loadSignaturesClasses(){
    for(String className : toSignaturesClasses){
      addHierarchy(RTAType.create(className));
      RTAClass rtaClass = getRTAClass(className);
      for(RTAMethod rtaMethod : rtaClass.getMethods()){
        MethodSignature signature = rtaMethod.getSignature();
        addHierarchy(signature.getReturnType());
        for(RTAType paramType : signature.getParameterTypes()){
          addHierarchy(paramType);
        }
        for(RTAType exType : rtaMethod.getExceptionTypes()){
          addHierarchy(exType);
        }
        signaturesMethods.add(signature);
      }
    }
  }

  private void loadForcedFields(){
    for(String fieldSignature : forcedLoadedFieldSignatures){
      FieldSignature typedField = new FieldSignature(fieldSignature);
      RTAType declaringClass = typedField.getDeclaringClass();
      RTAType fieldType = typedField.getType();
      addHierarchy(declaringClass);
      addHierarchy(fieldType);
      allFields.add(typedField);
    }
  }

  private void cgMethodEnqueue(MethodSignature signature, Set<RTAType> newInvokes){
    Pair<MethodSignature, Set<RTAType>> pair =
      new Pair<MethodSignature, Set<RTAType>>(signature, newInvokes);
    cgMethodQueue.add(pair);
  }

  public RTAMethodVisitor getMethodVisitor(RTAMethod rtaMethod){
    if(rtaMethodVisitorMap.containsKey(rtaMethod)){
      return rtaMethodVisitorMap.get(rtaMethod);
    } else {
      RTAMethodVisitor newVisitor = new RTAMethodVisitor(rtaMethod);
      rtaMethodVisitorMap.put(rtaMethod, newVisitor);
      return newVisitor;
    }
  }

  private String normalizePathElement(String path){
    if(File.separator.equals("/")){
      return path;
    } else {
      if(path.startsWith("/")){
        path = path.substring(1);
      }
      path = path.replace("/", "\\");
      return path;
    }
  }


  private void loadScene(){
    log("[rtaclassload] loading scene...");
    numberClasses();
    createEmptyClasses();
    fillInOuterClasses();
    loadFields();
    createEmptyMethods();
    fillInMethodBodies();
  }

  private void numberClasses(){
    numberedClasses = classHierarchy.numberClasses();
    log("[rtaclassload] Total loaded classes: "+numberedClasses.size());
  }

  private void createEmptyClasses(){
    log("[rtaclassload] creating empty classes according to type number...");
    for(NumberedType numberedType : numberedClasses){
      RTAType rtaType = numberedType.getType();
      RTAClass rtaClass = getRTAClass(rtaType);
      SootClass emptyClass = new SootClass(rtaType.toString(), rtaClass.getModifiers());
      Scene.v().addClass(emptyClass);

      if(applicationClasses.contains(rtaType)){
        emptyClass.setApplicationClass();
      } else {
        emptyClass.setLibraryClass();
      }

      if(rtaClass.hasSuperClass()){
        RTAType superClassType = rtaClass.getSuperClass();
        SootClass superClass = Scene.v().getSootClass(superClassType.toString());
        emptyClass.setSuperclass(superClass);
      }

      for(RTAType iface : rtaClass.getInterfaces()){
        SootClass ifaceClass = Scene.v().getSootClass(iface.toString());
        emptyClass.addInterface(ifaceClass);
      }
    }
  }

  private void fillInOuterClasses(){
    log("[rtaclassload] filling in outer classes...");
    Chain<SootClass> chain = Scene.v().getClasses();
    SootClass curr = chain.getFirst();
    while(curr != null){
      String name = curr.getName();
      if(name.contains("$")){
        int index = name.lastIndexOf('$');
        String outer_class_str = name.substring(0, index);
        SootClass outer_class = Scene.v().getSootClass(outer_class_str);
        curr.setOuterClass(outer_class);
      }
      curr = chain.getSuccOf(curr);
    }
  }

  private void loadFields(){
    log("[rtaclassload] loading fields...");
    for(FieldSignature fieldSignature : allFields){
      String declaringClass = findDeclaredFieldClass(fieldSignature);

      SootClass fieldClass = Scene.v().getSootClass(declaringClass);
      String fieldName = StringNumbers.v().getString(fieldSignature.getName());

      if(fieldClass.declaresFieldByName(fieldName)){
        continue;
      }

      RTAClass rtaClass = getRTAClass(declaringClass);
      RTAField rtaField = rtaClass.findFieldByName(fieldName);
      int fieldModifiers;
      if(rtaField != null){
        fieldModifiers = rtaField.getAccessFlags();
      } else {
        fieldModifiers = Modifier.PUBLIC;
      }
      Type fieldType = fieldSignature.getType().toSootType();
      SootField newField = new SootField(fieldName, fieldType, fieldModifiers);
      fieldClass.addField(newField);
    }
  }

  private String findDeclaredFieldClass(FieldSignature fieldSignature){
    LinkedList<RTAType> queue = new LinkedList<RTAType>();
    queue.add(fieldSignature.getDeclaringClass());
    while(queue.isEmpty() == false){
      RTAType item = queue.removeFirst();
      RTAClass rtaClass = getRTAClass(item);
      if(rtaClass.isPhantom()){
        return item.toString();
      }

      RTAField[] fields = rtaClass.getFields();
      for(RTAField field : fields){
        if(field.getName() == fieldSignature.getName() &&
           field.getType() == fieldSignature.getType()){

          return item.toString();
        }
      }

      if(rtaClass.hasSuperClass()){
        queue.add(rtaClass.getSuperClass());
      }
    }

    return fieldSignature.getDeclaringClass().toString();
  }

  private void createEmptyMethods(){
    log("[rtaclassload] creating empty methods...");
    Set<MethodSignature> emptyMethods = new HashSet<MethodSignature>();
    emptyMethods.addAll(allMethods);
    emptyMethods.addAll(signaturesMethods);
    for(MethodSignature methodSignature : emptyMethods){
      RTAType declaringClass = methodSignature.getClassName();
      String methodName = StringNumbers.v().getString(methodSignature.getMethodName());

      RTAClass rtaClass = getRTAClass(declaringClass);
      RTAMethod rtaMethod = rtaClass.findMethodBySubSignature(methodSignature.getSubSignatureString());
      declaringClass = rtaMethod.getSignature().getClassName();

      List<Type> parameterTypes = new ArrayList<Type>();
      for(RTAType param : methodSignature.getParameterTypes()){
        parameterTypes.add(StringToType.convert(param.toString()));
      }
      Type returnType = StringToType.convert(methodSignature.getReturnType().toString());
      int modifiers = rtaMethod.getModifiers();
      List<SootClass> thrownExceptions = new ArrayList<SootClass>();
      for(RTAType exception : rtaMethod.getExceptionTypes()){
        SootClass exceptionClass = Scene.v().getSootClass(exception.toString());
        thrownExceptions.add(exceptionClass);
      }
      SootMethod sootMethod = new SootMethod(methodName, parameterTypes,
        returnType, modifiers, thrownExceptions);
      SootClass sootClass = Scene.v().getSootClass(declaringClass.toString());

      if(sootClass.declaresMethod(sootMethod.getSubSignature()) == false){
        sootClass.addMethod(sootMethod);
        MethodSignature addedSig = new MethodSignature(sootMethod.getSignature());
        if(allMethods.contains(addedSig)){
          bodyMethods.add(addedSig);
        }
      }
    }
  }

  private void fillInMethodBodies(){
    log("[rtaclassload] filling in method bodies...");
    Set<String> visited = new HashSet<String>();
    for(MethodSignature methodSignature : bodyMethods){
      RTAType declaringClass = methodSignature.getClassName();
      RTAClass rtaClass = getRTAClass(declaringClass);
      RTAMethod rtaMethod = rtaClass.findMethodBySubSignature(methodSignature.getSubSignatureString());
      if(rtaMethod.isConcrete() && rtaMethod.isPhantom() == false){
        SootClass sootClass = Scene.v().getSootClass(declaringClass.toString());
        SootMethod sootMethod = sootClass.getMethod(methodSignature.getSubSignatureString());
        if(visited.contains(sootMethod.getSignature())){
          continue;
        }
        visited.add(sootMethod.getSignature());
        sootMethod.setSource(rtaMethod.getMethodSource());
      }
    }
  }

  public void setModifiedClass(String className){
    modifiedClasses.add(className);
  }

  public List<BytecodeFile> getModifiedBytecodeFiles(){
    List<BytecodeFile> ret = new ArrayList<BytecodeFile>();
    for(String className : modifiedClasses){
      SootClass sootClass = Scene.v().getSootClass(className);
      RTAType rtaType = RTAType.create(className);
      byte[] originalContents = classNumberToContents.get(rtaType);

      String jarFilename;
      if(rtaTypeToJar.containsKey(rtaType)){
        int jarNumber = rtaTypeToJar.get(rtaType);
        jarFilename = StringNumbers.v().getString(jarNumber);
      } else {
        //this is the case in purely generated SootClasses
        jarFilename = "";
      }
      ModifiedClassMerger merger = new ModifiedClassMerger();
      ret.add(merger.merge(jarFilename, className, sootClass, originalContents));
    }
    return ret;
  }

  private void buildCallGraph(){
  }
}
