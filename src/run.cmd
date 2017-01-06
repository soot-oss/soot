java -jar ..\libs\antlr-4.6-complete.jar -package soot.cil.parser -o soot/cil/parser cil.g4
"\Program Files\Java\jdk1.8.0_102\bin\javac.exe" -cp ..\libs\antlr-4.6-complete.jar soot/cil/parser/cil*.java
java -cp ..\libs\antlr-4.6-complete.jar;. org.antlr.v4.gui.TestRig soot.cil.parser.cil topLevelDef test.il -gui