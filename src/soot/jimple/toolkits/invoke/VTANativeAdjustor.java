package soot.jimple.toolkits.invoke;

import java.util.*;
import soot.util.*;
import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.*;

/** Adjusts the VTA type graph to take into account the effects of native methods. */

public class VTANativeAdjustor {

    private VTATypeGraph vtagraph;
    private HashMap nodeToReachingTypes;
    private Hierarchy h;
    private Set arrayNodes;
    
    public VTANativeAdjustor(Hierarchy h, VTATypeGraph g) {
        vtagraph = g;
        this.h = h;
        arrayNodes = g.arrayNodes;
        nodeToReachingTypes = g.nodeToReachingTypes;
    }

    void includeType(String node, String type) {
        TypeSet types = (TypeSet)nodeToReachingTypes.get(node);
        if (types !=null) {
            RefType t = RefType.v(type);
            if (!types.contains(t)) 
                types.add(t);
            if (type.equals("java.lang.Object"))
                arrayNodes.add(node);
        }
    }

    void includeSubtypesOf(String node, String type) {
        
        TypeSet types = (TypeSet)nodeToReachingTypes.get(node);
        if (types!=null) {
            for (Iterator clsIt = h.getSubclassesOfIncluding(RefType.v(type).getSootClass()).iterator(); 
                 clsIt.hasNext(); )
                types.add(RefType.v((SootClass)clsIt.next()));
            if (type.equals("java.lang.Object"))
                arrayNodes.add(node);
        }
    }

    /** Adds edges to and from native method nodes on a case-by-case basis. */

    public void adjustForNativeMethods() {

        String s1, s2;
        TypeSet l;

        s1 = "<java.lang.Object: java.lang.Class getClass()>$return";
        s2 = "java.lang.Class";
        includeType(s1, s2);

        s1 = "<java.lang.Object: java.lang.Object clone()>$return";
        s2 = "<java.lang.Object: java.lang.Object clone()>$this";
        if(vtagraph.containsNode(s1) && vtagraph.containsNode(s2))
            vtagraph.addEdge(s2, s1);

        s1 = "<java.lang.String: java.lang.String intern()>$return";
        s2 = "java.lang.String";
        includeType(s1, s2);

        s1 = "<java.lang.Throwable: java.lang.Throwable fillInStackTrace()>$return";
        s2 = "java.lang.Throwable";
        includeSubtypesOf(s1, s2);

        s1 = "<java.lang.Class: java.lang.Class forName(java.lang.String)>$return";
        s2 = "java.lang.Class";
        includeType(s1, s2);

            
        s1 = "<java.lang.Class: java.lang.String getName()>$return";
        s2 = "java.lang.String";
        includeType(s1, s2);

        s1 = "<java.lang.Class: java.lang.ClassLoader getClassLoader()>$return";
        s2 = "java.lang.ClassLoader";
        includeSubtypesOf(s1, s2);

        s1 = "<java.lang.Class: java.lang.Class getSuperClass()>$return";
        s2 = "java.lang.Class";
        includeType(s1, s2);

        s1 = "<java.lang.Class: java.lang.Class[] getInterfaces()>$return";
        s2 = "java.lang.Class";
        includeType(s1, s2);
        l = (TypeSet)nodeToReachingTypes.get(s1);
        if (l!=null) {
            if (!l.contains(RefType.v("java.lang.Object")))
                l.add(RefType.v("java.lang.Object"));
            nodeToReachingTypes.put(s1, l);
        }

        s1 = "<java.lang.Class: java.lang.Class getComponentType()>$return";
        s2 = "java.lang.Class";
        includeType(s1, s2);

        s1 = "<java.lang.Class: java.lang.Object[] getSigners()>$return";
        s2 = "<java.lang.Class: void setSigners(java.lang.Object[])>$p0";
        if(vtagraph.containsNode(s1) && vtagraph.containsNode(s2)) {
            vtagraph.addEdge(s2, s1);
            vtagraph.addEdge(s1, s2);
        }

        s1 = "<java.lang.Class: java.lang.Class getPrimitiveClass(java.lang.String)>$return";
        s2 = "java.lang.Class";
        includeType(s1, s2);

        s1 = "<java.lang.Class: java.lang.reflect.Field[] getFields0()>$return";
        s2 = "java.lang.reflect.Field";
        includeType(s1, s2);
        l = (TypeSet)nodeToReachingTypes.get(s1);
        if (l!=null) {
            if (!l.contains(RefType.v("java.lang.Object")))
                l.add(RefType.v("java.lang.Object"));
            nodeToReachingTypes.put(s1, l);
        }

        s1 = "<java.lang.Class: java.lang.reflect.Method[] getMethods0()>$return";
        s2 = "java.lang.reflect.Method";
        includeType(s1, s2);
        l = (TypeSet)nodeToReachingTypes.get(s1);
        if (l!=null) {
            if (!l.contains(RefType.v("java.lang.Object")))
                l.add(RefType.v("java.lang.Object"));
            nodeToReachingTypes.put(s1, l);
        }

        s1 = "<java.lang.Class: java.lang.reflect.Constructor[] getConstructors0()>$return";
        s2 = "java.lang.reflect.Constructor";
        includeType(s1, s2);
        l = (TypeSet)nodeToReachingTypes.get(s1);
        if (l!=null) {
            if (!l.contains(RefType.v("java.lang.Object")))
                l.add(RefType.v("java.lang.Object"));
            nodeToReachingTypes.put(s1, l);
        }

        s1 = "<java.lang.Class: java.lang.reflect.Field getField0(java.lang.String)>$return";
        s2 = "java.lang.reflect.Field";
        includeType(s1, s2);

        s1 = "<java.lang.Class: java.lang.reflect.Method getMethod0(java.lang.String,java.lang.Class[])>$return";
        s2 = "java.lang.reflect.Method";
        includeType(s1, s2);

        s1 = "<java.lang.Class: java.lang.reflect.Constructor getConstructor0(java.lang.Class[])>$return";
        s2 = "java.lang.reflect.Constructor";
        includeType(s1, s2);

        s1 = "<java.lang.System: void arraycopy(java.lang.Object,int,java.lang.Object,int,int)>$p0";
        s2 = "<java.lang.System: void arraycopy(java.lang.Object,int,java.lang.Object,int,int)>$p2";
        if(vtagraph.containsNode(s1) && vtagraph.containsNode(s2)) {
            vtagraph.addEdge(s2, s1);        
            vtagraph.addEdge(s1, s2);
        }

        s1 = "<java.lang.System: void setErr0(java.io.PrintStream)>$p0";
        s2 = "<java.lang.System: java.io.PrintStream err>";
        if(vtagraph.containsNode(s1) && vtagraph.containsNode(s2))
            vtagraph.addEdge(s1, s2);

        s1 = "<java.lang.System: void setOut0(java.io.PrintStream)>$p0";
        s2 = "<java.lang.System: java.io.PrintStream out>";
        if(vtagraph.containsNode(s1) && vtagraph.containsNode(s2))
            vtagraph.addEdge(s1, s2);

        s1 = "<java.lang.System: void setIn0(java.io.InputStream)>$p0";
        s2 = "<java.lang.System: java.io.InputStream in>";
        if(vtagraph.containsNode(s1) && vtagraph.containsNode(s2))
            vtagraph.addEdge(s1, s2);

        s1 = "<java.lang.System: java.util.Properties initProperties(java.util.Properties)>$return";
        s2 = "<java.lang.System: java.util.Properties initProperties(java.util.Properties)>$p0";
        if(vtagraph.containsNode(s1) && vtagraph.containsNode(s2))
            vtagraph.addEdge(s2, s1);

        s1 = "<java.lang.Thread: java.lang.Thread currentThread()>$return";
        s2 = "java.lang.Thread";
        includeSubtypesOf(s1, s2);

        s1 = "<java.lang.reflect.Field: java.lang.Object get(java.lang.Object)>$return";
        s2 = "<java.lang.reflect.Field: void set(java.lang.Object,java.lang.Object)>$p1";
        if(vtagraph.containsNode(s1) && vtagraph.containsNode(s2)) {
            vtagraph.addEdge(s1, s2);
            vtagraph.addEdge(s2, s1);
        }

        // NATIVE METHOD <java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>        
        s1 = "<java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>$return";
        s2 = "java.lang.Object";
        includeSubtypesOf(s1, s2);
        s1 = "<java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>$p0";
        s2 = "<java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>$p1";
        if (vtagraph.containsNode(s1) && vtagraph.containsNode(s2)) {
            vtagraph.addEdge(s1, s2);
            vtagraph.addEdge(s2, s1);
        }

        // NATIVE METHOD <java.lang.reflect.Constructor: java.lang.Object newInstance(java.lang.Object[]);
        s1 = "<java.lang.reflect.Constructor: java.lang.Object newInstance(java.lang.Object[])>$return";
        s2 = "java.lang.Object";
        includeSubtypesOf(s1, s2);

        s1 = "<java.lang.ClassLoader: java.lang.Class defineClass0(java.lang.String,byte[],int.int)>$return";
        s2 = "java.lang.Class";
        includeType(s1, s2);
        
        s1 = "<java.lang.ClassLoader: java.lang.Class findSystemClass0(java.lang.String)>$return";
        s2 = "java.lang.Class";
        includeType(s1, s2);

        s1 = "<java.lang.ClassLoader: java.lang.String getSystemResourceAsName0(java.lang.String)>$return";
        s2 = "java.lang.String";
        includeType(s1, s2);

        // NATIVE METHOD <java.lang.ClassLoader: java.io.InputStream getSystemResourceAsStream0(java.lang.String)>
        s1 = "<java.lang.ClassLoader: java.io.InputStream getSystemResourceAsStream0(java.lang.String)>$return";
        TypeSet types = (TypeSet)nodeToReachingTypes.get(s1);
        if (types!=null) {
            TypeSet temp = new TypeSet();
            for (Iterator clsIt = h.getSubclassesOfIncluding(Scene.v().getSootClass("java.io.InputStream")).iterator(); 
                 clsIt.hasNext(); )
                temp.add(RefType.v((SootClass)clsIt.next()));
            temp.retainAll(TypeSet.libraryTypes);
            types.addAll(temp);
        }

        s1 = "<java.lang.SecurityManager: java.lang.Class[] getClassContext()>$return";
        s2 = "java.lang.Class";
        includeType(s1, s2);
        l = (TypeSet)nodeToReachingTypes.get(s1);
        if (l!=null) {
            if (!l.contains(RefType.v("java.lang.Object")))
                l.add(RefType.v("java.lang.Object"));
            nodeToReachingTypes.put(s1, l);
        }

        s1 = "<java.lang.SecurityManager: java.lang.ClassLoader currentClassLoader()>$return";
        s2 = "java.lang.ClassLoader";
        includeSubtypesOf(s1, s2);

        s1 = "<java.lang.SecurityManager: java.lang.Class currentLoadedClass0()>$return";
        s2 = "java.lang.Class";
        includeType(s1, s2);

        s1 = "<java.io.ObjectInputStream: java.lang.Class loadClass0(java.lang.Class,java.lang.String)>$return";
        s2 = "java.lang.Class";
        includeType(s1, s2);

        // NATIVE METHOD <java.io.ObjectInputStream: void inputClassFields(java.lang.Object,java.lang.Class,int[])>
        s1 = "<java.io.ObjectInputStream: void inputClassFields(java.lang.Object,java.lang.Class,int[])>$p0";
        s2 = "java.lang.Object";
        includeSubtypesOf(s1, s2);

        // NATIVE METHOD <java.io.ObjectInputStream: java.lang.Object allocateNewObject(java.lang.Class,java.lang.Class)>
        s1 = "<java.io.ObjectInputStream: java.lang.Object allocateNewObject(java.lang.Class,java.lang.Class)>$return";
        s2 = "java.lang.Object";
        includeSubtypesOf(s1, s2);

        // NATIVE METHOD <java.io.ObjectInputStream: java.lang.Object allocateNewArray(java.lang.Class,int)>
        s1 = "<java.io.ObjectInputStream: java.lang.Object allocateNewArray(java.lang.Class,int)>$return";
        s2 = "java.lang.Object";
        includeType(s1, s2);

        //ADD NATIVE METHOD <java.io.ObjectInputStream: boolean invokeObjectReader(java.lang.Object,java.lang.Class)>

        // This may be incorrect, since side effects are ignored.
        s1 = "<java.lang.Runtime: java.lang.Process execInternal(java.lang.String[],java.lang.String[])>$return";
        s2 = "java.lang.Process";
        includeSubtypesOf(s1, s2);

        s1 = "<java.lang.Runtime: java.lang.String initializeLinkerInternal()>$return";
        s2 = "java.lang.String";
        includeType(s1, s2);
       
        s1 = "<java.lang.Runtime: java.lang.String buildLibName(java.lang.String,java.lang.String)>$return";
        s2 = "java.lang.String";
        includeType(s1, s2);

        s1 = "<java.io.FileDescriptor: java.io.FileDescriptor initSystemFD(java.io.FileDescriptor,int)>$return";
        s2 = "java.io.FileDescriptor";
        includeType(s1, s2);

        s1 = "<java.util.ResourceBundle: java.lang.Class[] getClassContext()>$return";
        s2 = "java.lang.Class";
        includeType(s1, s2);
        l = (TypeSet)nodeToReachingTypes.get(s1);
        if (l!=null) {
            if (!l.contains(RefType.v("java.lang.Object")))
                l.add(RefType.v("java.lang.Object"));
            nodeToReachingTypes.put(s1, l);
        }        

        s1 = "<java.io.File: java.lang.String[] list0()>$return";
        s2 = "java.lang.String";
        includeType(s1, s2);
        l = (TypeSet)nodeToReachingTypes.get(s1);
        if (l!=null) {
            if (!l.contains(RefType.v("java.lang.Object")))
                l.add(RefType.v("java.lang.Object"));
            nodeToReachingTypes.put(s1, l);
        }

        s1 = "<java.io.File: java.lang.String canonPath(java.lang.String)>$return";
        s2 = "java.lang.String";
        includeType(s1, s2);

        s1 = "<java.io.ObjectStreamClass: java.lang.String[] getMethodSignatures(java.lang.Class)>$return";
        s2 = "java.lang.String";
        includeType(s1, s2);
        l = (TypeSet)nodeToReachingTypes.get(s1);
        if (l!=null) {
            if (!l.contains(RefType.v("java.lang.Object")))
                l.add(RefType.v("java.lang.Object"));
            nodeToReachingTypes.put(s1, l);
        }

        s1 = "<java.io.ObjectStreamClass: java.lang.String[] getFieldSignatures(java.lang.Class)>$return";
        s2 = "java.lang.String";
        includeType(s1, s2);
        l = (TypeSet)nodeToReachingTypes.get(s1);
        if (l!=null) {
            if (!l.contains(RefType.v("java.lang.Object")))
                l.add(RefType.v("java.lang.Object"));
            nodeToReachingTypes.put(s1, l);
        }

        s1 = "<java.io.ObjectStreamClass: java.io.ObjectStreamField[] getFields0(java.lang.Class)>$return";
        s2 = "java.io.ObjectStreamField";
        includeSubtypesOf(s1, s2);
                l = (TypeSet)nodeToReachingTypes.get(s1);
        if (l!=null) {
            if (!l.contains(RefType.v("java.lang.Object")))
                l.add(RefType.v("java.lang.Object"));
            nodeToReachingTypes.put(s1, l);
        }
        
        s1 = "<java.net.InetAddressImpl: java.lang.String getLocalHostName()>$return";
        s2 = "java.lang.String";
        includeType(s1, s2);

        s1 = "<java.net.InetAddressImpl: java.lang.String getHostByAddr(int)>$return";
        s2 = "java.lang.String";
        includeType(s1, s2);

        s1 = "<java.net.InetAddressImpl: byte[][] lookupAllHostAddr(java.lang.String)>$return";
        l = (TypeSet)nodeToReachingTypes.get(s1);
        if (l!=null) {
            if (!l.contains(RefType.v("java.lang.Object")))
                l.add(RefType.v("java.lang.Object"));
            nodeToReachingTypes.put(s1, l);
        }

        // The following nodes correspond to fields that are filled in directly by the VM.
        s1 = "<java.lang.Thread: java.lang.ThreadGroup group>";
        s2 = "java.lang.ThreadGroup";
        includeSubtypesOf(s1, s2);

        // NATIVE METHOD <java.lang.Class: java.lang.Object newInstance()>

        if (vtagraph.containsNode("<java.lang.Class: java.lang.Object newInstance()>$return")) {
            
            // Do a breadth-first search starting from the return node; if it hits a cast expression, include
            // all subtypes of the type indicated by the cast. If it hits an instance invoke without passing
            // through a cast, then we are forced to include all types.

            List locals = vtagraph.getSuccsOf("<java.lang.Class: java.lang.Object newInstance()>$return");
            InvokeGraph ig = Scene.v().getActiveInvokeGraph();
            ig.mcg.clearEntryPoints();  // To find more accurate entry points

            for (Iterator localsIt = locals.iterator(); localsIt.hasNext(); ) {

                String local = (String)localsIt.next();
                types = (TypeSet)vtagraph.nodeToReachingTypes.get(local);

                HashSet visited = new HashSet(0);
                LinkedList typeStack = new LinkedList();

                // Do a BFS to find the closest casts or instance invokes.
                LinkedList q = new LinkedList();
                q.addLast(local);
                while (!q.isEmpty()) {
                    String v = (String)q.removeFirst();
                    visited.add(v);
                    Type t = (Type)vtagraph.labelToDeclaredType.get(v);
                    if (t instanceof ArrayType) {
                        t = ((ArrayType)t).baseType;
                        types.add(RefType.v("java.lang.Object"));
                        arrayNodes.add(v);
                    }
                    if (!(t instanceof RefType))
                        continue;
                    if (v.endsWith("this"))
                        typeStack.add(((RefType)t).getSootClass());
                    else {
                        if (vtagraph.castEdges.containsKey(v)) {
                            List pairs = (List)vtagraph.castEdges.get(v);
                            for (Iterator pairsIt = pairs.iterator(); pairsIt.hasNext(); ) {
                                NodeTypePair pair = (NodeTypePair)pairsIt.next();
                                visited.add(pair.getNode());
                                Type type = pair.getType();
                                if (type instanceof ArrayType) {
                                    types.add(RefType.v("java.lang.Object"));
                                    if (((ArrayType)type).baseType instanceof RefType)
                                        typeStack.add(((ArrayType)type).baseType);
                                }
                                else
                                    typeStack.add(((RefType)type).getSootClass());
                            }
                        }

                        List succs = vtagraph.getSuccsOf(v);
                        for (Iterator succsIt = succs.iterator(); succsIt.hasNext(); ) {
                            String succ = (String)succsIt.next();
                            if (!visited.contains(succ))
                                q.addLast(succ);
                        }
                    }
                }
                // Now add the types found by BFS, and indicate which "void <init>()" methods are actually entry points.
                while(!typeStack.isEmpty()) {
                    SootClass c = (SootClass)typeStack.removeLast();
                    if(c.isInterface())
                        typeStack.addAll(h.getImplementersOf(c));
                    else
                        for (Iterator classIt = h.getSubclassesOfIncluding(c).iterator(); classIt.hasNext(); ) {
                            SootClass clazz = (SootClass)classIt.next();
                            types.add(RefType.v(clazz));
                            if (clazz.declaresMethod("void <init>()"))
                                ig.mcg.addEntryPoint(clazz.getMethod("void <init>()"));
                        }
                }
            }
        }

        
    }
}
