[![Build Status](http://soot-build.cs.uni-paderborn.de/jenkins/buildStatus/icon?job=soot/soot-master)](http://soot-build.cs.uni-paderborn.de/jenkins/job/soot/job/soot-master/)

# Soot supports Java 9 modules now!
Try and get involved in Soot's Java 9 bleeding edge developement. Check out the [Soot-j9](https://github.com/sable/soot/tree/java9) branch.

# What is Soot?

Soot is a Java optimization framework. It provides four intermediate representations for analyzing and transforming Java bytecode:

* Baf: a streamlined representation of bytecode which is simple to manipulate.
* Jimple: a typed 3-address intermediate representation suitable for optimization.
* Shimple: an SSA variation of Jimple.
* Grimp: an aggregated version of Jimple suitable for decompilation and code inspection.

See http://www.sable.mcgill.ca/soot/ for details.

# How do I get started with Soot?

We have some documentation on Soot in the [wiki](https://github.com/Sable/soot/wiki) and also a large range of [tutorials](http://www.sable.mcgill.ca/soot/tutorial/index.html) on Soot.

# Including Soot in your Project

A Soot release is currently built for each commit to the `master` branch. You can include Soot as 
a dependency via Maven, Gradle, SBT, etc using the following coordinates:


```.xml
<dependencies>
  <dependency>
    <groupId>ca.mcgill.sable</groupId>
    <artifactId>soot</artifactId>
    <version>3.2.0</version>
  </dependency>
</dependencies>
```

___Important___:
If you are using a build tool other than Maven (Gradle, SBT, Ivy, etc.), you will also have to add the following repository to your build file:
```.xml
<repository>
    <id>swt-upb</id>
    <name>Maven repository of the Software Engineering Group at University of Paderborn</name>
    <url>https://soot-build.cs.uni-paderborn.de/nexus/repository/swt-upb/</url>
</repository>
``` 

You can also obtain older builds of the `develop` branch. A complete listing of builds can be found in [Sonatype's SNAPSHOT repository](https://oss.sonatype.org/content/repositories/snapshots/ca/mcgill/sable/soot).

# How do I obtain Soot without Maven?

All of our Soot release builds are stored in our [Nexus repository](https://soot-build.cs.uni-paderborn.de/nexus/#browse/browse/components:soot-release) and can be obtained from there.
Furthermore, all releases of Soot can also be obtained [directly](https://soot-build.cs.uni-paderborn.de/public/origin/master/soot/soot-master/) in the `build` directory of the corresponding version directory. The "sootclasses-trunk-jar-with-dependencies.jar" file is an all-in-one file that also contains all the required libraries. The "sootclasses-trunk.jar" file contains only Soot, allowing you to manually pick dependencies as you need them. If you do not want to bother with dependencies, we recommend using the former.

# Building Soot yourself

If you cannot work with the prebuild versions and need to build Soot on your own, please consider the [wiki](https://github.com/Sable/soot/wiki/Building-Soot-from-the-Command-Line-(Recommended)) for further steps.

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

That way you can help us in two ways:
* By letting us know how we can improve Soot you can directly help us prioritize newly planned features.
* By stating your name and affiliation you help us showcasing Soot’s large user base.
Thanks!
