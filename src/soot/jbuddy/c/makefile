all:
	swig -java -package soot.jbuddy jbuddy.i  
	mv *.java ..
	gcc -g -c jbuddy_wrap.c  -Ijava/include -Ijava/include/linux
	gcc -g -c myfdd.c -Ibuddy/src
	ld -G -g *.o buddy/src/libbdd.a -o libjbuddy.so
	rm *.o
