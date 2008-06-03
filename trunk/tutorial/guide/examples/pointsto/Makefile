
# Remember your must have zChaff or similar on your path!!!


# The filename of the file to be tested
TESTCASE=Test1

# Get the current dir
USERPATH= $(shell pwd)

# Set the classpath
CP=.:${USERPATH}/test/:/home/java/Linux-j2sdk1.4.2/jre/lib/rt.jar:${USERPATH}/lib/sootclasses.jar:${USERPATH}/lib/jasminclasses.jar:${USERPATH}/lib/jedd-runtime.jar:${USERPATH}/lib/jedd-translator.jar:${USERPATH}/lib/paddle-nightly.jar:${USERPATH}/lib/polyglotclasses-1.3.2.jar

# Path to your installation of java 1.4.2
# - The JVM
JAVA=/home/java/Linux-j2sdk1.4.2/bin/java -Xmx146m -Xss146m -Djava.library.path=${USERPATH}/lib/
# - javac
JAVAC=/home/java/Linux-j2sdk1.4.2/bin/javac

all: paddleanalysis tests paddletest

paddleanalysis:
	${JAVAC} -classpath ${CP} -d bin/ -sourcepath src/ src/dk/brics/paddle/PointsToAnalysis.java

paddletest:
	${JAVA} -classpath ./bin:${CP} dk.brics.paddle.PointsToAnalysis paddle ${TESTCASE} go


tests:
	${JAVAC} -d test/ -sourcepath test/ test/*.java

paddlecommandline:
	${JAVA} soot.Main -dump-cfg all -w -p cg.paddle on Test1
