OPTIONS=../src/soot/options
all:
	xsltproc tutorial-phase.xsl ${OPTIONS}/soot_options.xml > phase.tex
	latex phase.tex < /dev/null
	dvips -o phase.ps phase.dvi
	pdflatex phase.tex < /dev/null
	xsltproc tutorial-usage.xsl ${OPTIONS}/soot_options.xml > usage.tex
	latex usage.tex < /dev/null
	dvips -o usage.ps usage.dvi
	pdflatex usage.tex < /dev/null
	xsltproc ${OPTIONS}/soot_options.xsl ${OPTIONS}/soot_options.xml > options.html
