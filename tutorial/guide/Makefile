compile:
	latex soot.tex
	bibtex soot

pdf:
	dvips -o soot.ps soot.dvi
	ps2pdf14 soot.ps
	mv soot.ps sootsurvivorsguide.ps
	mv soot.pdf sootsurvivorsguide.pdf

examples: cleanexamples
	tarexamples.sh

all: compile pdf examples

clean: cleanexamples
	rm -f *.aux *.blg *.log *.bbl *.dvi *.ps *.pdf *.toc

cleanexamples:
	rm -f sootsurvivorsguideexamples.tar.gz
	cleanexamples.sh

.PHONY:	clean compile

commit:
	cvs commit -m ''

update:
	cvs update -d -P

