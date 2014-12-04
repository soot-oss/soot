# What is Soot?

Soot is a Java optimization framework. It provides four intermediate representations for analyzing and transforming Java bytecode:

* Baf: a streamlined representation of bytecode which is simple to manipulate.
* Jimple: a typed 3-address intermediate representation suitable for optimization.
* Shimple: an SSA variation of Jimple.
* Grimp: an aggregated version of Jimple suitable for decompilation and code inspection.

See http://www.sable.mcgill.ca/soot/ for details.

# How do I get started with Soot?

We have some documentation on Soot in the [wiki](https://github.com/Sable/soot/wiki) and also a large range of [tutorials](http://www.sable.mcgill.ca/soot/tutorial/index.html) on Soot.

# How do I obtain the nightly builds

** Note that the nightly build server has moved**

Nightly builds of soot can be obtained from [nightly build](https://ssebuild.cased.de/nightly/soot/). The "soot-trunk.jar" file is an all-in-one file that also contains all the required libraries. The "sootclasses-trunk.jar" file contains only Soot, allowing you to use manually pick dependencies as you need them.

# About Soot's source code

Soot follows the git-flow convention. Releases and hotfixes are maintained in the master branch.
Development happens in the develop branch. To catch the bleeding edge of Soot, check out the latter.
You will also need the projects [jasmin](https://github.com/Sable/jasmin) and
[heros](https://github.com/Sable/heros). In case of any questions, please consult the Soot
mailing list at: http://www.sable.mcgill.ca/mailman/listinfo/soot-list/
