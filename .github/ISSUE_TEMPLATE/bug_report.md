---
name: Bug report
about: Create a bug report to help us improve Soot.
title: ''
labels: 'bug'
assignees: ''

---

Please examine each of the following points *carefully* so that we can help you as soon and best as possible. For reference, [here](https://github.com/soot-oss/soot/issues/2188) you can find a good example of a helpful bug report.

**Describe the bug**
A clear and concise description of what the bug is. If you use any third party library or tool (such as FlowDroid, etc.) that could interfere with Soot in any way, you should indicate that clearly.

**Input file**
Please upload or provide a (working) link to the .class, .jar, .dex, .apk or any other input file which caused the issue (the code you want to analyze). You can drop a ZIP file right into this textbox.
This is _very_ important. In many cases, a bug triggers only on certain input files, which happen to be structured in a particular way which causes this problem. In case you do not supply the input files, we unfortunately often **cannot** help you at all.

**To reproduce**
Steps to reproduce the behavior:
Please include soot options you used or supply a code snippet to ease reproduction of the problem. **Please do not supply code snippets as _Screenshots_**. If possible, make sure that the supplied code is somewhat complete. It helps when the code for reproduction is somewhat minimal, but that is not necessary.

**Expected behavior**
A clear and concise description of what you expected to happen.

**Stacktrace**
If applicable, add complete stacktraces. In case your stack trace contains a line starting with "Caused by:", that line as well as the following lines are still part of the original stacktrace, so please include them as well. **Please do not supply stack traces as _Screenshots_**, copy and paste the stacktrace instead.

**Additional context**
Add any other context about the problem here, which might help us to understand or solve your problem better. If there is more log output of Soot, you should add that as well.
