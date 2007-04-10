#!/usr/local/bin/bash
for FILE in `find examples/ -name \*.class`; do
  echo "deleting $FILE" 
  rm $FILE
done



