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
JC=javac -d classes -classpath classes:src:$$CLASSPATH

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

all: ${TARGETS} foo

$(TARGETS): classes/%.class: src/%.java
	$(JC) $<


classes/soot/jimple/parser/parser/parser.dat: src/soot/jimple/parser/parser/parser.dat
	cp src/soot/jimple/parser/parser/parser.dat classes/soot/jimple/parser/parser

classes/soot/jimple/parser/lexer/lexer.dat: src/soot/jimple/parser/lexer/lexer.dat
	cp src/soot/jimple/parser/lexer/lexer.dat classes/soot/jimple/parser/lexer

classes/soot/baf/toolkits/base/peephole.dat: src/soot/baf/toolkits/base/peephole.dat
	cp src/soot/baf/toolkits/base/peephole.dat classes/soot/baf/toolkits/base/peephole.dat

foo: classes/soot/jimple/parser/parser/parser.dat classes/soot/jimple/parser/lexer/lexer.dat classes/soot/baf/toolkits/base/peephole.dat


#
##########################################################################
#
# remove all the class files
#
clean:
	-rm -rf ${RM_TARGETS}


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


