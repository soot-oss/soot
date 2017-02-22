[![Build Status](http://build.renjin.org/job/Soot/job/soot/badge/icon)](http://build.renjin.org/job/Soot/job/soot/)

# Please help us improve Soot!
You are using Soot and would like to help us support it in the future? Then please support us by filling out [this little web form](https://goo.gl/forms/rk1oSxFIxAH0xaf52).

That way you can help us in two ways:
* By letting us know how we can improve Soot you can directly help us prioritize newly planned features.
* By stating your name and affiliation you help us showcasing Soot’s large user base.
Thanks!

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

A Soot "release" is currently built for each commit to the `develop` branch. You can include Soot as 
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
    <url>http://soot-build.cs.uni-paderborn.de/nexus/repository/soot-snapshot/</url>
  </repository>
</repositories>	

```

# How do I obtain the nightly builds

**Note that the nightly build server has moved**

Nightly builds of soot can be obtained from [nightly build](http://soot-build.cs.uni-paderborn.de/nightly/soot/). The "soot-trunk.jar" file is an all-in-one file that also contains all the required libraries. The "sootclasses-trunk.jar" file contains only Soot, allowing you to use manually pick dependencies as you need them.

# About Soot's source code

Soot follows the git-flow convention. Releases and hotfixes are maintained in the master branch.
Development happens in the develop branch. To catch the bleeding edge of Soot, check out the latter.
You will also need the projects [jasmin](https://github.com/Sable/jasmin) and
[heros](https://github.com/Sable/heros). In case of any questions, please consult the Soot
mailing list at: http://www.sable.mcgill.ca/mailman/listinfo/soot-list/
