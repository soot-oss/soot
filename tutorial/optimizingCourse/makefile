all:	
	latex course.tex < /dev/null
	dvips -t landscape -o course.ps course.dvi
	ps2pdf -sPAPERSIZE=a4 course.ps course.pdf

clean:
	rm *.ps *.pdf *.dvi *.aux *.log
