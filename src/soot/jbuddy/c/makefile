all:
	swig -java -package soot.jbuddy jbuddy.i  
	mv JBuddy.java ..
	gcc -c jbuddy_wrap.c  -Ijava/include -Ijava/include/linux
	gcc -c myfdd.c -Ibuddy/src
	ld -G *.o buddy/src/libbdd.a -o libjbuddy.so
	rm *.o
