[![Build Status](http://soot-build.cs.uni-paderborn.de/jenkins/buildStatus/icon?job=soot/soot-develop)](http://soot-build.cs.uni-paderborn.de/jenkins/job/soot/job/soot-develop/)

# Looking for PostDoc or Ph.D. student to work on Soot!

The [research group of Eric Bodden](https://www.hni.uni-paderborn.de/en/software-engineering/) has acquired funding for a three-year project that will fund a PostDoc or Ph.D. student with the goal of applying major upgrades to Soot to make it sustainable for the future. The position is fully funded and does not include any teaching or administrative duties (although teaching is possible if desired). We will assure that at least 50% of the time can also be spent on personal research.

As Soot is one of the most widely used research infrastructures worldwide, this position will give you a great opportunity to obtain wide exposition in the research community. In case you are interested in applying, please [contact Eric Bodden directly](https://www.hni.uni-paderborn.de/en/software-engineering/staff/bodden/).

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

A Soot snapshot release is currently built for each commit to the `develop` branch. You can include Soot as 
a dependency via Maven, Gradle, SBT, etc using the following coordinates:


```.xml
<dependencies>
  <dependency>
    <groupId>ca.mcgill.sable</groupId>
    <artifactId>soot</artifactId>
    <version>3.1.0-SNAPSHOT</version>
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

All of our Soot builds for the `develop` branch are stored up to one month in our [Nexus repository](https://soot-build.cs.uni-paderborn.de/nexus/#browse/browse/components:soot-snapshot) and can be obtained from there.
The latest snapshot build of Soot can also be obtained [directly](https://soot-build.cs.uni-paderborn.de/public/origin/develop/soot/soot-develop/build/). The "sootclasses-trunk-jar-with-dependencies.jar" file is an all-in-one file that also contains all the required libraries. The "sootclasses-trunk.jar" file contains only Soot, allowing you to manually pick dependencies as you need them. If you do not want to bother with dependencies, we recommend using the former.

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
