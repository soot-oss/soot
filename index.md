<iframe src="https://github.com/sponsors/soot-oss/card" title="Sponsor Soot" height="225" width="600" style="border: 0;"></iframe>

# What is Soot?

<img align="right" src="/soot/logo/soot-logo.png" width="150">

Originally, Soot started off as a Java optimization framework. By now, researchers and practitioners from around the world use Soot to analyze, instrument, optimize and visualize Java and Android applications.

# IMPORTANT: Soot is now succeeded by SootUp!
**In December 2022, we have officially released [SootUp](https://soot-oss.github.io/SootUp/announce/), a version of Soot with a completely overhauled, more modular, testable, maintainable and usable architecture. Please check this out in case you wish to start a new program-analysis project.**

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
* Template-driven Inter-procedural data-flow analysis, in combination with [heros](https://github.com/Sable/heros) (uses IFDS/IDE) or [Weighted Pushdown Systems](https://github.com/CROSSINGTUD/WPDS)
* Aliasing can be resolved using the flow-, field-, context-sensitive demand-driven pointer analysis [Boomerang](https://github.com/CROSSINGTUD/WPDS)
* Taint analysis in combination with [FlowDroid](https://blogs.uni-paderborn.de/sse/tools/flowdroid/) or [IDEal](https://github.com/CROSSINGTUD/WPDS)

# What extensions exist to Soot?

* We maintain a [list of extensions that can be used in combination with Soot](https://github.com/soot-oss/soot/wiki/Extensions-to-Soot). Feel free to add your own!

# How does Soot work internally?

Soot transforms programs into an intermediate representation, which can then be analyzed.
Soot provides four intermediate representations for analyzing and transforming Java bytecode:

* Baf: a streamlined representation of bytecode which is simple to manipulate.
* Jimple: a typed 3-address intermediate representation suitable for optimization.
* Shimple: an SSA variation of Jimple.
* Grimp: an aggregated version of Jimple suitable for decompilation and code inspection.

Jimple is Soot's primary IR and most analyses are implemented on the Jimple level. Custom IRs may be added when desired.

# How do I get started with Soot?

We have some documentation on Soot in the [wiki](https://github.com/soot-oss/soot/wiki), including a large range of [tutorials](https://github.com/soot-oss/soot/wiki/Tutorials) on Soot. We also have a [JavaDoc documentation and a reference on the command line options](https://github.com/soot-oss/soot/wiki/Options-and-JavaDoc).

# Including Soot in your Project

### Bleeding Edge

A Soot snapshot release is currently built for each commit to the `develop` branch. You can include Soot as 
a dependency via Maven, Gradle, SBT, etc using the following coordinates:


```.xml
<dependencies>
  <dependency>
    <groupId>org.soot-oss</groupId>
    <artifactId>soot</artifactId>
    <version>4.1.0-SNAPSHOT</version>
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


You can also obtain older builds of the `develop` branch. A complete listing of builds can be found in [Sonatype's SNAPSHOT repository](https://oss.sonatype.org/content/repositories/snapshots/org/soot-oss/soot/).

### Stable Releases

For each commit to the `master` branch, a new release is pushed to Maven Central. You can include Soot as 
a dependency via Maven, Gradle, SBT, etc using the following coordinates:


```.xml
<dependencies>
  <dependency>
    <groupId>org.soot-oss</groupId>
    <artifactId>soot</artifactId>
    <version>4.1.0</version>
  </dependency>
</dependencies>
```

You can also obtain older builds of the `master` branch. A complete listing of builds can be found on [Maven Central](https://repo.maven.apache.org/maven2/org/soot-oss/soot/).

# How do I obtain Soot without Maven?
**We recommend using Soot with Maven**

You can obtain the latest release build of Soot [directly](https://repo1.maven.org/maven2/org/soot-oss/soot/).

The `soot-<RELEASE>-jar-with-dependencies.jar` file is an all-in-one file that also contains all the required libraries. 

The `soot-<RELEASE>.jar`  file contains only Soot, allowing you to manually pick dependencies as you need them. If you do not want to bother with dependencies, we recommend using the former.

# Building Soot yourself

If you cannot work with the prebuild versions and need to build Soot on your own, please consider the [wiki](https://github.com/soot-oss/soot/wiki/Building-Soot-from-the-Command-Line-(Recommended)) for further steps.

# About Soot's source code

Soot follows the git-flow convention. Releases and hotfixes are maintained in the master branch.
Development happens in the develop branch. To catch the bleeding edge of Soot, check out the latter.
In case of any questions, please consult the Soot
mailing list at: http://www.sable.mcgill.ca/mailman/listinfo/soot-list/

# Please help us improve Soot!
You are using Soot and would like to help us support it in the future? Then please support us by filling out [this little web form](https://goo.gl/forms/rk1oSxFIxAH0xaf52).

That way you can help us in two ways:
* By letting us know how we can improve Soot you can directly help us prioritize newly planned features.
* By stating your name and affiliation you help us showcasing Soot’s large user base.
Thanks!

# Supporters

The further development of Soot is financed by generous support from the German Research Foundation, the Heinz Nixdorf Institute and Amazon Web Services.

<a href="http://www.dfg.de/"><img src="images/dfg_logo_englisch_blau_en.jpg" height="40"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<a href="https://www.hni.uni-paderborn.de/en/"><img src="images/Heinz_Nixdorf_Institut_Logo_CMYK.jpg" height="40"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<a href="https://aws.amazon.com/"><img src="images/200px-Amazon_Web_Services_Logo.svg.png" height="40"></a>

Amazon Web Services is a Gold Sponsor. [![AWS](https://upload.wikimedia.org/wikipedia/commons/thumb/9/93/Amazon_Web_Services_Logo.svg/150px-Amazon_Web_Services_Logo.svg.png)]()

[Read more here about how to become a sponsor on your own.](https://github.com/sponsors/soot-oss)

Also many thanks to [![JProfiler](https://www.ej-technologies.com/images/product_banners/jprofiler_small.png)](https://www.ej-technologies.com/products/jprofiler/overview.html) for supporting Soot with a free-to-use open source license!



<a class="twitter-timeline tw-align-center" data-width="220" data-dnt="true" data-theme="light" href="https://twitter.com/sootAnalyzer?ref_src=twsrc%5Etfw">Tweets by sootAnalyzer</a> <script async src="https://platform.twitter.com/widgets.js" charset="utf-8"></script>
