tutorial.pdf: tutorial.tex flow.tex figs
	latex tutorial && latex tutorial && ./dvi2pdf.pl -t a4 tutorial && dvips -o tutorial.ps tutorial.dvi && psnup -l6 -Pa4 -pletter tutorial.ps > tutorial-6up.ps && pstops '1:0(0,.25in)' tutorial-6up.ps > ps2pdf_is_broken.ps && ps2pdf -sPAPERSIZE=letter ps2pdf_is_broken.ps tutorial-6up.pdf

figs:  rd.eepic rd-example.eepic rd-example-specific.eepic vocab.tex ecosystem.eepic body-ecosystem.eepic intra.tex lv.eepic lv-example.eepic lv-example-specific.eepic br-example.eepic br-example-specific.eepic copy-bkwd.eepic merge-bkwd.eepic flowthrough-bkwd.eepic flow-fwd.eepic flow-bkwd.eepic copy-fwd.eepic merge-fwd.eepic body-bare.eepic

%.eepic: %.fig
	fig2dev -L eepic $< > $@

#   1.  latex file.tex
#   2. dvips -Ppdf -G0 file.dvi -o file.ps
#   3. ps2pdf -dPDFsettings=/prepress file.ps file.pdf 

#Two points to note:

#    * The -G0 parameter passed to dvips is used to get around a bug in GhostScript which converts the "f" character to a pound sign in the final PDF.
#    * The -dPDFsettings parameter for ps2pdf is used to prevent downsampling of EPS images when they are converted to PDF. Without this switch, EPS graphics in the final PDF look very fuzzy, especially when viewed with a projector. 
