ECLIPSE_HOME=/home/jeshaw/eclipse/plugins/
SOOT_DIR=/home/jeshaw/soot/classes

all:
	javac -d classes -classpath $(ECLIPSE_HOME)org.eclipse.ui_2.0.2/workbench.jar:$(ECLIPSE_HOME)org.eclipse.core.boot_2.0.2/boot.jar:$(ECLIPSE_HOME)org.eclipse.jdt.ui_2.0.2/jdt.jar:$(ECLIPSE_HOME)org.eclipse.jdt.core_2.0.2/jdtcore.jar:$(ECLIPSE_HOME)org.eclipse.core.resources_2.0.1/resources.jar:$(ECLIPSE_HOME)org.eclipse.core.runtime_2.0.2/runtime.jar:$(ECLIPSE_HOME)org.eclipse.swt.motif_2.0.2/ws/motif/swt.jar:$(SOOT_DIR):. src/ca/mcgill/sable/soot/*.java src/ca/mcgill/sable/soot/*/*.java

	jar -cf soot-plugin.jar icons/*.jpg icons/*.gif build.properties .project .classpath plugin.properties plugin.xml

	jar -uf soot-plugin.jar -C classes ca

updateDialog:
	xsltproc -o src/ca/mcgill/sable/soot/testing/PhaseOptionsDialog.java ../../src/soot/options/phase_options_dialog.xsl ../../src/soot/options/soot_options.xml
