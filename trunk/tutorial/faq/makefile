all: tex

tex:
	latex faq
	latex faq
	dvips faq.dvi
	ps2pdf faq.ps faq.pdf
	latex2html faq -split 0 -nonavigation -dir ./

upload: tex
	scp -r . abc.cs.mcgill.ca:/home/user/sable/public_html/soot/tutorial/faq
