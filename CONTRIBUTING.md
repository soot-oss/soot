## Branches  

Currently, Soot follows the [git-flow branching model](http://nvie.com/posts/a-successful-git-branching-model/). This means that there are two main branches, master and develop. Master usually only points to stable release versions. Most people use the develop branch to get hands-on latest features but only in beta version, that gets updated on daily basis. On release, the stable develop version is merged into the Master branch.

All our nightly tests and builds operate on develop branch.

## Pull Request  
> Note: If you already have a fork of Soot and need to merge our formatting changes, [this](https://github.com/Sable/soot/wiki/Merging-changes-after-introduction-of-formatting-guidelines) might help.

To fix a problem in Soot or to contribute to Soot you can fork the Soot project on GitHub. This will generate a personal cloned repository for you to work on. To reincorporate your changes into Soot after they have been completed and tested, just send us a [pull request](https://help.github.com/articles/using-pull-requests). This will allow us to review your changes and merge them into the develop branch.

### Style guidelines

Pull requests will have to conform to our coding style guidelines which are _as close as possible_* to the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html). Our build server will reject pull requests that do not comply with our guidelines.

Tutorials on how to apply automatic formatting and style enhancements can be found here:
* [Eclipse](https://github.com/Sable/soot/wiki/Formatting-for-Eclipse)
* [IntelliJ Idea](https://github.com/Sable/soot/wiki/Formatting-for-IntelliJ)

Compliance with our guidelines is ensured by the Maven Checkstyle plugin. One can run the checks by invoking `mvn checkstyle:check`. Also, the check is bound to the Maven's `verify` phase. 
Our build server will reject pull requests with existing violations. 

### License header

Newly created classes will have to have a [correct license header](https://github.com/Sable/soot/wiki/License-Header) right after the _package declaration_ of the class. Our build server will reject pull requests with missing license headers. The license check can be run locally by invoking `mvn license:check-file-header` or during the `verify` phase.

**Make sure that `mvn clean verify` terminates successfully before setting up a pull request or committing to the repository directly!**



## Mailing List  

In case, if you have any queries regarding SOOT, please refer to the link below. You can start by first registering yourself and you can then post related questions. It really helps us to figure out the problem if you post the reference code along with the question or bug/error in your code.

http://www.sable.mcgill.ca/mailman/listinfo/soot-list/

In the link below, you can also find all the queries posted in the past and may help you find an answer to your query.

http://www.sable.mcgill.ca/pipermail/soot-list/

---

*Unfortunately, we have to relax some of the original rules by Google since conforming to those would lead to a lot of client-breaking changes due to Soot being maintained without any guidelines for a long time now.
