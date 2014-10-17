## RTAClassLoad
This class loader leaves items as strings for as long as possible and uses a varient of Rapid Type Analysis (RTA) Class Loader.  

RTAClass load only loads methods reachable from a DFS from the entry points. In addition, it uses the class hierarchy to resolve virtual methods while class loading.

### Previous Class Loader
The previous class loader had three resolving levels:  
1. Hierarchy  
2. Signatures  
3. Bodies  

**Hierarchy**  
For any class loaded to Hierarchy, SootClass objects are made for the Class, Super Class and Interfaces. The Super Class and Interfaces are raised to Hierarchy

**Signatures**  
For any class loaded to Signatures, empty SootMethods are created for all methods of current SootClass. Return type, parameter types and exception types of all empty methods are loaded to Hierarchy

**Bodies**  
For any class loaded to Bodies, the bodies of all SootMethods are loaded. Loading bodies requires that all types found by method invokes or field references are loaded to signatures. If not, we get those nasty exceptions we are all used to.

### RTAClassLoad compared
RTAClassLoad doesn't load method signatures and bodies for methods not found on a DFS walk. This makes less types loaded, reducing time and memory usage. Also, it loads virtual methods according to what possible types have been invoked with new. As more virtual methods are loaded, more possible types can be loaded, so this is done in a fixed point according to RTA class loading.

### Class Numbering
When loading a Scene, you need to properly load the parent of a SootClass object before loading the current SootClass object.

We do this by numbering classes. Object is numbered as zero, all interfaces are numbered after object and all non-interfaces are numbered after interfaces. Interfaces are numbered according to a reverse topological sort numbering that solves problems with interfaces extending other interfaces. Non-interfaces are numbered in a BFS order.

### Context Sensitive

RTAClassLoad can be configured to have context sensitive or context in-sensitive new_invoke propegation.

### CallGraphLinks

Runnable.run can be loaded by using CallGraphLinks. During setup you can link Thread.start() to Runnable.run(). In the DFS, it will follow Runnable.run() when it sees Thread.start() with the correct new_invokes

### Code Generation
Since RTAClassLoad only loads methos reachable on a DFS walk, code generation that adds code to classes can require full class loading at write out time. RTAClassLoad contains code that can merge the byte[] from a JAR file with a SootClass without doing full class loading of the byte[].

### Examples

See [Example0](https://github.com/pcpratts/soot/blob/feature/rtaclassload/src/soot/rtaclassload/examples/example0/Example0.java) and
See [ThreadFollow](https://github.com/pcpratts/soot/blob/feature/rtaclassload/src/soot/rtaclassload/examples/thread_follow/ThreadFollow.java)
