all:
	./makeSparkOptions.awk options > SparkOptions.java
	./makeOptDoc.awk options > opts.tex
	latex opts.tex
	latex2html -split 0 opts.tex
	dvips -o opts.ps opts.dvi

