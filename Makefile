#
# Soot Makefile
#
#

#
##########################################################################
#
# The .java to .class rule
#
.SUFFIXES: .java .class

#
##########################################################################
#
# Default java sdk tools
#
# Can be overridden by a redefinition in jam.def
#

#
# Compiler
#
JC=javac -target 1.2 -d classes -classpath classes:src:$$CLASSPATH

#
# Virtual machine
#
JVM = java

#
# Debugger
#
JD = jdb

#
#
##########################################################################
#
# compile all the sources from current directory down
#
SOURCES = ${shell find src -name "*.java" -print}
TARGETS_TMP = $(SOURCES:.java=.class)
TARGETS = $(subst src,classes,${TARGETS_TMP})
RM_TARGETS = $(TARGETS) $(subst .class,\$$*.class,$(TARGETS))

all: classes/soot/options/Options.class ${TARGETS} foo 

$(TARGETS): classes/%.class: src/%.java
	$(JC) $<


classes/soot/jimple/parser/parser/parser.dat: src/soot/jimple/parser/parser/parser.dat
	cp src/soot/jimple/parser/parser/parser.dat classes/soot/jimple/parser/parser

classes/soot/jimple/parser/lexer/lexer.dat: src/soot/jimple/parser/lexer/lexer.dat
	cp src/soot/jimple/parser/lexer/lexer.dat classes/soot/jimple/parser/lexer

classes/soot/baf/toolkits/base/peephole.dat: src/soot/baf/toolkits/base/peephole.dat
	cp src/soot/baf/toolkits/base/peephole.dat classes/soot/baf/toolkits/base/peephole.dat

src/soot/options/Options.java: src/soot/options/*.xml src/soot/options/make-soot-options.xsl
	xsltproc src/soot/options/make-soot-options.xsl src/soot/options/soot_options.xml > src/soot/options/Options.java

foo: classes/soot/jimple/parser/parser/parser.dat classes/soot/jimple/parser/lexer/lexer.dat classes/soot/baf/toolkits/base/peephole.dat src/soot/options/Options.java


#
#
#########################################################################
#
# compile document files
#
#

javadoc: document

document: 
	find src -name '*.java' | xargs grep ^package | sed 's/.*package //' | sed 's/;//' | sort -u > src/packageList
	javadoc -J-Xmx200m -d doc -sourcepath src -windowtitle "Soot API" @src/packageList

badfields: all
	java -Xmx200m soot.tools.BadFields -w -f none -process-dir classes soot.Main


#
##########################################################################
#
# compile tests
#
# To compile the tests, CLASSPATH needs to include a path to junit classes
# (/usr/share/java/junit.jar, in the Sable lab)
#
TEST_SOURCES = ${shell find tests -name "*.java" -print}
TEST_TARGETS_TMP = $(TEST_SOURCES:.java=.class)
TEST_TARGETS = $(subst tests,testclasses,${TEST_TARGETS_TMP})
RM_TEST_TARGETS = $(TEST_TARGETS) $(subst .class,\$$*.class,$(TEST_TARGETS))
TEST_JC=javac -target 1.2 -d testclasses -classpath classes:testclasses:src:tests:$$CLASSPATH

buildtests: ${TARGETS} testclasses ${TEST_TARGETS}

$(TEST_TARGETS): testclasses/%.class: tests/%.java
	$(TEST_JC) $<

testclasses: 
	mkdir -p testclasses

#
##########################################################################
#
# remove all the class files
#
clean: testclasses
	find classes testclasses -name '*.class' | xargs rm


#
# JMAIN is the main program for this project
# if defined, a run target and a debug target will be defined
#
ifdef JMAIN
run:
	$(JVM) $(JMAIN)

debug:
	$(JD) $(JMAIN)
endif


