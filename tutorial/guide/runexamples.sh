#!/usr/local/bin/bash


if [ $# != 0 ]
  then
    echo "Usage: runexamples <sootclasses> <jasminclasses> <polyglot>"
    echo "runexamples will try to compile and execute all the examples in examples dir"
    exit 1
fi


BASE="/users/fagidiot/SOOT"

SOOT="${BASE}/soot-2.3.0/lib/sootclasses-2.3.0.jar"

JASMIN="${BASE}/jasmin-2.3.0/lib/jasminclasses-2.3.0.jar"

JFLEX="${BASE}/polyglot-1.3.5/lib/JFlex.jar"
COFFER="${BASE}/polyglot-1.3.5/lib/coffer.jar"
JAVACUP="${BASE}/polyglot-1.3.5/lib/java_cup.jar"
PAO="${BASE}/polyglot-1.3.5/lib/pao.jar"
POLYGLOT="${BASE}/polyglot-1.3.5/lib/polyglot.jar"
PTH="${BASE}/polyglot-1.3.5/lib/pth.jar"

CP="${SOOT}:${JASMIN}:${JFLEX}${COFFER}:${JAVACUP}:${PAO}:${POLYGLOT}:${PTH}:."

echo "Call graph example"
FILE="dk/brics/soot/callgraphs/CallGraphExample.java"
cd examples/call_graph/src
echo "compiling ${FILE}"
javac -cp ${CP} ${FILE}

EXITSTATUS=$?
if [ ${EXITSTATUS} != 0 ]; then
   echo "The compilation of ${FILE} failed" 
   exit 2;
fi

echo "running"
java -Xmx512m -cp ${CP} dk/brics/soot/callgraphs/CallGraphExample
cd -

exit $?;

