[![Build Status](https://github.com/soot-oss/soot/workflows/Soot%20CI/badge.svg?branch=develop)](https://github.com/soot-oss/soot/actions)
[![Gitpod Ready-to-Code](https://img.shields.io/badge/Gitpod-Ready--to--Code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/soot-oss/soot) 

# IMPORTANT: Soot is now succeeded by SootUp!
**In December 2022, we have officially released [SootUp](https://soot-oss.github.io/SootUp/), a version of Soot with a completely overhauled, more modular, testable, maintainable and usable architecture. Please check this out in case you wish to start a new program-analysis project.**

Since there has been some confusion: SootUp is not yet feature-complete. Therefore, the "old" Soot needs to live on for the time being, especially for projects that require instrumentation capabilities or robust Android support. The "old" Soot is still being maintained until it can safely be dropped for a feature-complete successor.

# Using Soot? Let us know about it!
We are regularly applying for funding to help us maintain Soot. You can help us immensely by letting us know about [**projects that use Soot**](https://github.com/soot-oss/soot/wiki/Users-of-Soot), both commercially or in the form of research tools.

Also many thanks to [![JProfiler](https://www.ej-technologies.com/images/product_banners/jprofiler_small.png)](https://www.ej-technologies.com/products/jprofiler/overview.html) for supporting Soot with a free-to-use open source license!

# Thanks to our Sponsors...
... for supporting the further Development of Soot!
Amazon Web Services is a Gold Sponsor. [![AWS](https://upload.wikimedia.org/wikipedia/commons/thumb/9/93/Amazon_Web_Services_Logo.svg/150px-Amazon_Web_Services_Logo.svg.png)]()

[Read more here about how to become a sponsor on your own.](https://github.com/sponsors/soot-oss)


# Soot supports Java 9 modules now!
Try and get involved in Soot's Java 9 bleeding edge developement.
## What works and is tested?
* Automatic modules (modules automatically created from jars in the module-path)
* Named modules
* Exploded modules
* Modular jar files
* Resolving modules in Soot's `ModuleScene`
* Spark
   
## What does not work yet?
 * Anonymous modules (mixing module- and class-path)
 * Multi-module jar files

# What is Soot?

Soot is a Java optimization framework. It provides four intermediate representations for analyzing and transforming Java bytecode:

* Baf: a streamlined representation of bytecode which is simple to manipulate.
* Jimple: a typed 3-address intermediate representation suitable for optimization.
* Shimple: an SSA variation of Jimple.
* Grimp: an aggregated version of Jimple suitable for decompilation and code inspection.

See https://soot-oss.github.io/soot for details.

# How do I get started with Soot?

We have some documentation on Soot in the [wiki](https://github.com/soot-oss/soot/wiki) and also a large range of [tutorials](https://github.com/soot-oss/soot/wiki/Tutorials) on Soot. 

For detailed information please also consider the Soot's [JavaDoc and Options](https://github.com/soot-oss/soot/wiki/Options-and-JavaDoc) Documentations.

# Including Soot in your Project

A Soot release is currently built for each commit to the `master` branch. You can include Soot as
a dependency via Maven, Gradle, SBT, etc using the following coordinates:


```.xml
<dependencies>
  <dependency>
    <groupId>org.soot-oss</groupId>
    <artifactId>soot</artifactId>
    <version>4.3.0</version>
  </dependency>
</dependencies>
```

You can also obtain older builds of the `master` branch. A complete listing of builds can be found on [Maven Central](https://repo.maven.apache.org/maven2/org/soot-oss/soot/).

A Soot SNAPSHOT is currently built for each commit to the `develop` branch. You can include Soot as 
a dependency via Maven, Gradle, SBT, etc using the following coordinates:


```.xml
<dependencies>
  <dependency>
    <groupId>org.soot-oss</groupId>
    <artifactId>soot</artifactId>
    <version>4.4.0-SNAPSHOT</version>
  </dependency>
</dependencies>
<repositories>
  <repository>
      <id>sonatype-snapshots</id>
      <url>https://oss.sonatype.org/content/repositories/snapshots</url>
      <releases>
          <enabled>false</enabled>
      </releases>
  </repository>
</repositories>	
```

You can also obtain older builds of the `develop` branch. A complete listing of builds can be found on [Maven Central](https://oss.sonatype.org/content/repositories/snapshots/org/soot-oss/soot/).

# How do I obtain Soot without Maven?
**We recommend using Soot with Maven**
You can obtain the latest release build of Soot [directly](https://repo1.maven.org/maven2/org/soot-oss/soot/).
You can obtain the latest SNAPSHOT build of Soot [directly](https://oss.sonatype.org/content/repositories/snapshots/org/soot-oss/soot/).

The `soot-<RELEASE>-jar-with-dependencies.jar` file is an all-in-one file that also contains all the required libraries. 

The `soot-<RELEASE>.jar`  file contains only Soot, allowing you to manually pick dependencies as you need them. If you do not want to bother with dependencies, we recommend using the former.

# Building Soot yourself

If you cannot work with the prebuild versions and need to build Soot on your own, please consider the [wiki](https://github.com/soot-oss/soot/wiki/Building-Soot-from-the-Command-Line-(Recommended)) for further steps.

# About Soot's source code

Soot follows the git-flow convention. Releases and hotfixes are maintained in the master branch.
Development happens in the develop branch. To catch the bleeding edge of Soot, check out the latter.

# How do I contribute to Soot?

We are happy to accept arbitrary improvements to Soot in form of GitHub pull requests. Please read our [contribution guidelines](https://github.com/soot-oss/soot/blob/master/CONTRIBUTING.md) before setting up a pull request.

# Please help us improve Soot!
You are using Soot and would like to help us support it in the future? Then please support us by filling out [this little web form](https://goo.gl/forms/rk1oSxFIxAH0xaf52).

That way you can help us in two ways:
* By letting us know how we can improve Soot you can directly help us prioritize newly planned features.
* By stating your name and affiliation you help us showcasing Soot’s large user base.
Thanks!

# How to use Soot's Java 9 Features?

If you want to run Soot with Java > 8, you are done. Just run it as usal.
If you want to execute Soot with Java 8 but analyze Java >8 Projects or vice versa, see below.

## Use from Source Code
To load modules in Soot's `ModuleScene` from java:
```.java
// configure Soot's options, refer to example configurations below
Options.v().set_soot_modulepath(modulePath);


// load classes from modules into Soot
// Here, getClassUnderModulePath() expects the module path to be set using the Options class as seen above
Map<String, List<String>> map = ModulePathSourceLocator.v().getClassUnderModulePath(modulePath);
for (String module : map.keySet()) {
   for (String klass : map.get(module)) {
       logger.info("Loaded Class: " + klass + "\n");
       loadClass(klass, false, module);
       // the loadClass() method is defined below
   }
}


//this must be called after all classes are loaded
Scene.v().loadNecessaryClasses();


public static SootClass loadClass(String name, boolean main, String module) {
     SootClass c = ModuleScene.v().loadClassAndSupport(name, Optional.of(module));
     c.setApplicationClass();
     if (main)
         Scene.v().setMainClass(c);
     return c;
}

```
ModuleUtil.module_mode() helps you check whether you have modules enabled in Soot. This is done based on whether the module path is set using the Options class.

### Example Configurations: Java 8, Java >= 9 Classpath, Java >= 9 Modulepath

```.java

if(java < 9 ) { // when you have a target benchmark with Java < 9 and hence no modules
    Options.v().set_prepend_classpath(true);
    Options.v().set_process_dir(Arrays.asList(applicationClassPath().split(File.pathSeparator)));
    Options.v().set_soot_classpath(sootClassPath());
}

if(java >= 9 && USE_CLASSPATH) { // when you have a target benchmark with Java >= 9 and do not want module support
    Options.v().set_soot_classpath("VIRTUAL_FS_FOR_JDK" + File.pathSeparator + sootClassPath());
    Options.v().set_process_dir(Arrays.asList(applicationClassPath().split(File.pathSeparator)));
}


if(java>=9 && USE_MODULEPATH) { // when you have a target benchmark with Java >= 9 and want module support
    Options.v().set_prepend_classpath(true);
    Options.v().set_soot_modulepath(sootClassPath());
    Options.v().set_process_dir(Arrays.asList(applicationClassPath().split(File.pathSeparator)));
}

```
In the above examples, applicationClassPath() should be replaced with the path to the application classes for analysis by Soot and sootClassPath() should be replaced with the Soot classpath.

## Use from the Command Line
To execute Soot using Java 1.9, but analyzing a classpath run, just as before:
`java -cp soot-trunk.jar soot.Main --process-dir directoryToAnalyse`


if you want to specify the classpath explicitly run:
`java -cp soot-trunk.jar soot.Main -cp VIRTUAL_FS_FOR_JDK --process-dir directoryToAnalyse`

the value `VIRTUAL_FS_FOR_JDK` indicates that Soot should search Java's (>9) virtual filesystem `jrt:/` for classes, too, although Soot is not executed in module mode.


To load modules and classes in Soot using java 1.8 run:

` java -cp PATH_TO_JAVA9/jrt-fs.jar:soot-trunk.jar soot.Main -pp -soot-modulepath modules/  `


Please replace `PATH_TO_JAVA9` with the path to your local installation of java 9.
The `jrt-fs.jar` is a built-in NIO FileSystem provider for the jrt:// filesystem java 9 uses that replaces `rt.jar`. 


