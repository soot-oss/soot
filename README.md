[![Build Status](http://soot-build.cs.uni-paderborn.de/jenkins/buildStatus/icon?job=soot/soot-build-j9)](http://soot-build.cs.uni-paderborn.de/jenkins/job/soot/job/soot-build-j9/)

# What is Soot-j9?

Soot-j9 is an (experimental) extension of Soot for the java 9 module system.
For Soot details see http://www.sable.mcgill.ca/soot/.



**Note that Soot-j9 is not yet stable nor complete.**

# How do I obtain Soot-j9 nightly builds

Nightly builds of Soot can be obtained from [nightly build](https://soot-build.cs.uni-paderborn.de/nightly/soot/). The "sootj9-trunk.jar" file is an all-in-one file that also contains all the required libraries. 




# What works?

 * Automatic modules (modules automatically created from jars in the module-path)
 * Named modules
 * Exploded modules
 * Modular jar files
 * Resolving modules in Soot's `ModuleScene`
 * Spark

# What does not work yet?
 * Anonymous modules (mixing module- and class-path)
 * Multi-module jar files


# How to use Soot-j9?

## Use from Source Code
To load modules in Soot's `ModuleScene` from java:
```
// configure Soot's options
Options.v().set_prepend_classpath(true); // this is currently required to include the virtual java 9 filesystem jrt://
Options.v().set_soot_modulepath(modulePath);


// load classes from modules into Soot
  	Map<String, List<String>> map = ModulePathSourceLocator.v().getClassUnderModulePath(modulePath);
        for (String module : map.keySet()) {
            for (String klass : map.get(module)) {
                logger.info("Loaded Class: " + klass + "\n");
                loadClass(klass, false, module);

            }
    }


    //this must be called after all classes are resolved
    Scene.v().loadNecessaryClasses();


  public static SootClass loadClass(String name, boolean main, String module) {
        SootClass c = ModuleScene.v().loadClassAndSupport(name, Optional.of(module));
        c.setApplicationClass();
        if (main)
            Scene.v().setMainClass(c);
        return c;
    }

```

## Use from the Command Line
To execute Soot using Java 1.9, but analyzing a classpath run:
`java -cp soot-trunk-j9.jar soot.Main -cp VIRTUAL_FS_FOR_JDK9:directoryToAnalyse --process-dir directoryToAnalyse`

the value `VIRTUAL_FS_FOR_JDK9` indicates that Soot should search Java's 9 virtual filesystem `jrt:/` for classes, too, although Soot is not executed in module mode.

To load modules and classes in Soot using java 1.8 run:

` java -cp PATH_TO_JAVA9/jrt-fs.jar:soot-trunk-j9.jar soot.Main -pp -soot-modulepath modules/  `


Please replace `PATH_TO_JAVA9` with the path to your local installation of java 9.
The `jrt-fs.jar` is a built-in NIO FileSystem provider for the jrt:// filesystem java 9 uses that replaces `rt.jar`. 


# About Soot's source code

Soot follows the git-flow convention. Releases and hotfixes are maintained in the master branch.
Development happens in the develop branch. To catch the bleeding edge of Soot, check out the latter.
In case of any questions, please consult the Soot
mailing list at: http://www.sable.mcgill.ca/mailman/listinfo/soot-list/

# How do I contribute to Soot?

We are happy to accept arbitrary improvements to Soot in form of GitHub pull requests. Please read our [contribution guidelines](https://github.com/Sable/soot/wiki/Contributing-to-Soot) before setting up a pull request.

# What extensions exist to Soot?

We maintain a [list of extensions that can be used in combination with Soot](https://github.com/Sable/soot/wiki/Extensions-to-Soot). Feel free to add your own!

# Please help us improve Soot!
You are using Soot and would like to help us support it in the future? Then please support us by filling out [this little web form](https://goo.gl/forms/rk1oSxFIxAH0xaf52).
