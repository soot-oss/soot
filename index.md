---
layout: post
title: A framework for analyzing and transforming Java and Android Applications
---

# What is Soot?

<img align="right" src="http://sable.github.io/soot/logo/soot-logo.png" size="50">

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
The current maintenance is driven by the [Secure Software Engineering Group](http://sse.ec-spride.de/) at [Technische Universität Darmstadt](http://www.tu-darmstadt.de).

[This publication](resources/lblh11soot.pdf) provides an insight into the first ten years of Soot's development.

# What kind of analyses does Soot provide?

* Call-graph construction
* Points-to analysis
* Def/use chains
* Template-driven Intra-procedural data-flow analysis
* Template-driven Inter-procedural data-flow analysis, in combination with [heros](https://github.com/Sable/heros)
* Taint analysis in combination with [FlowDroid](https://sseblog.ec-spride.de/tools/flowdroid/)

# How does Soot work internally?

Soot transforms programs into an intermediate representation, which can then be analyzed.
Soot provides four intermediate representations for analyzing and transforming Java bytecode:

* Baf: a streamlined representation of bytecode which is simple to manipulate.
* Jimple: a typed 3-address intermediate representation suitable for optimization.
* Shimple: an SSA variation of Jimple.
* Grimp: an aggregated version of Jimple suitable for decompilation and code inspection.

Jimple is Soot's primary IR and most analyses are implemented on the Jimple level. Custom IRs may be added when desired.

# How do I get started with Soot?

We have some documentation on Soot in the [wiki](https://github.com/Sable/soot/wiki), including a large range of [tutorials](https://github.com/Sable/soot/wiki/Tutorials) on Soot. We also have a [JavaDoc documentation](https://ssebuild.cased.de/nightly/soot/javadoc/).

# How do I obtain the nightly builds?

Nightly builds of soot can be obtained from [nightly build](http://ssebuild.cased.de/nightly/soot/). The "soot-trunk.jar" file is an all-in-one file that also contains all the required libraries. The "sootclasses-trunk.jar" file contains only Soot, allowing you to use manually pick dependencies as you need them.

# About Soot's source code

Soot follows the [git-flow convention](http://nvie.com/posts/a-successful-git-branching-model/). Releases and hotfixes are maintained in the master branch.
Development happens in the develop branch. To catch the bleeding edge of Soot, check out the latter.
You will also need the projects [jasmin](https://github.com/Sable/jasmin) and
[heros](https://github.com/Sable/heros). In case of any questions, please consult the Soot
[mailing list](https://github.com/Sable/soot/wiki/Getting-help).
