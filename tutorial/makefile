OPTIONS=../src/soot/options
all:
	xsltproc ${OPTIONS}/tutorial-phase.xsl ${OPTIONS}/soot_options.xml > phase.tex
	latex phase.tex < /dev/null
	dvips -o phase.ps phase.dvi
	xsltproc ${OPTIONS}/tutorial-usage.xsl ${OPTIONS}/soot_options.xml > usage.tex
	latex usage.tex < /dev/null
	dvips -o usage.ps usage.dvi
	xsltproc ${OPTIONS}/soot_options.xsl ${OPTIONS}/soot_options.xml > options.html
