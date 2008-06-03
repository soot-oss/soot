all:
	latex soot.tex
	bibtex soot

pdf:
	dvips -o soot.ps soot.dvi
	ps2pdf14 soot.ps

commit:
	cvs commit -m ''

update:
	cvs update -d -P

clean:
	rm -f *.aux *.blg *.log *.bbl *.dvi *.ps *.toc

examples: cleanexamples
	tarexamples.sh

cleanexamples:
	rm -f sootnoteexamples.tar.gz
	cleanexamples.sh

.PHONY:	all pdf commit update examples
