# Please help us improve Soot!
You are using Soot and would like to help us support it in the future? Then please support us by filling out [this little web form](https://goo.gl/forms/rk1oSxFIxAH0xaf52).

That way you can help us in two ways:
* By letting us know how we can improve Soot you can directly help us prioritize newly planned features.
* By stating your name and affiliation you help us showcasing Soot’s large user base.
Thanks!

# What is Soot?

<img align="right" src="http://sable.github.io/soot/logo/soot-logo.png" width="150">

Originally, Soot started off as a Java optimization framework. By now, researchers and practitioners from around the world use Soot to analyze, instrument, optimize and visualize Java and Android applications.

# What input formats does Soot provide?

Currently, Soot can process code from the following sources:

* **Java** (bytecode and source code up to Java 7), including other languages that compile to Java bytecode, e.g. Scala
* **Android** bytecode
* **Jimple** intermediate representation (see below)
* [**Jasmin**](https://github.com/Sable/jasmin/), a low-level intermediate representation.

# What output formats does Soot provide?

Soot can produce (possibly transformed/instrumented/optimized) code in these output formats:

* **Java** bytecode
* **Android** bytecode
* **Jimple**
* **Jasmin**

Soot can go from any input format to any output format, i.e., for instance, allows the translation from Android to Java or Java to Jasmin.

# Who develops and maintains Soot?

Soot was originally developed by the [**Sable Research Group**](http://www.sable.mcgill.ca/soot/) of [McGill University](http://www.mcgill.ca/). The [first publication on Soot](resources/sable-paper-1999-1.pdf) appeared at CASCON 1999. Since then, Soot has seen contributions from many people inside and outside the research community. 
The current maintenance is driven by Eric Bodden's [Software Engineering Group](https://www.hni.uni-paderborn.de/en/software-engineering/) at [Heinz Nixdorf Institute](https://www.hni.uni-paderborn.de/) of [Paderborn University](https://www.uni-paderborn.de/).

[This publication](resources/lblh11soot.pdf) provides an insight into the first ten years of Soot's development.

# What kind of analyses does Soot provide?

* Call-graph construction
* Points-to analysis
* Def/use chains
* Template-driven Intra-procedural data-flow analysis
* Template-driven Inter-procedural data-flow analysis, in combination with [heros](https://github.com/Sable/heros)
* Taint analysis in combination with [FlowDroid](https://blogs.uni-paderborn.de/sse/tools/flowdroid/)

# What extensions exist to Soot?

* We maintain a [list of extensions that can be used in combination with Soot](https://github.com/Sable/soot/wiki/Extensions-to-Soot). Feel free to add your own!

# How does Soot work internally?

Soot transforms programs into an intermediate representation, which can then be analyzed.
Soot provides four intermediate representations for analyzing and transforming Java bytecode:

* Baf: a streamlined representation of bytecode which is simple to manipulate.
* Jimple: a typed 3-address intermediate representation suitable for optimization.
* Shimple: an SSA variation of Jimple.
* Grimp: an aggregated version of Jimple suitable for decompilation and code inspection.

Jimple is Soot's primary IR and most analyses are implemented on the Jimple level. Custom IRs may be added when desired.

# How do I get started with Soot?

We have some documentation on Soot in the [wiki](https://github.com/Sable/soot/wiki), including a large range of [tutorials](https://github.com/Sable/soot/wiki/Tutorials) on Soot. We also have a [JavaDoc documentation and a reference on the command line options](https://github.com/Sable/soot/wiki/Options-and-JavaDoc).

# Including Soot in your Project

A Soot snapshot release is currently built for each commit to the `develop` branch. You can include Soot as 
a dependency via Maven, Gradle, SBT, etc using the following coordinates:


```.xml
<dependencies>
  <dependency>
    <groupId>ca.mcgill.sable</groupId>
    <artifactId>soot</artifactId>
    <version>3.0.0-SNAPSHOT</version>
  </dependency>
</dependencies>
<repositories>
  <repository>
    <id>soot-snapshot</id>
    <name>soot snapshots</name>
    <url>https://soot-build.cs.uni-paderborn.de/nexus/repository/soot-snapshot/</url>
  </repository>
</repositories>	

```

**Please make sure that your Java version is up to date to avoid problems with our SSL certificate**

You can also obtain older builds of the `develop` branch. A complete listing of builds can be found in our [Nexus repository](https://soot-build.cs.uni-paderborn.de/nexus/#browse/browse/components:soot-snapshot).

# How do I obtain Soot without Maven?

**Note that the nightly build server has moved**

All of our Soot builds for the `develop` branch are stored up to one month in our [Nexus repository](https://soot-build.cs.uni-paderborn.de/nexus/#browse/browse/components:soot-snapshot) and can be obtained from there.
The latest snapshot build of Soot can also be obtained [directly](https://soot-build.cs.uni-paderborn.de/public/origin/develop/soot/soot-develop/build/). The "sootclasses-trunk-jar-with-dependencies.jar" file is an all-in-one file that also contains all the required libraries. The "sootclasses-trunk.jar" file contains only Soot, allowing you to manually pick dependencies as you need them. If you do not want to bother with dependencies, we recommend using the former.

# Building Soot yourself

If you cannot work with the prebuild versions and need to build Soot on your own, please consider the [wiki](https://github.com/Sable/soot/wiki/Building-Soot-from-the-Command-Line-(Recommended)) for further steps.

# About Soot's source code

Soot follows the git-flow convention. Releases and hotfixes are maintained in the master branch.
Development happens in the develop branch. To catch the bleeding edge of Soot, check out the latter.
In case of any questions, please consult the Soot
mailing list at: http://www.sable.mcgill.ca/mailman/listinfo/soot-list/

# Acknowledgements

We would like to thank the team of [YourKit](https://www.yourkit.com/) for providing us with free licenses of their profiler to improve the performance of Soot.
