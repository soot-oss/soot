package soot.util;

import java.io.PrintStream;

import org.apache.commons.exec.LogOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows to forward everything written to the old PrintStream logging
 * <code>G.v().out</code> to a real logging framework (SLF4J) in this case.
 * 
 * <p>Depends on {@link LogOutputStream} (from apache.commons.exec).</p>
 * 
 * @author Jan Peter Stotz
 * 
 */
public class SLF4JPrintStreamLogger extends PrintStream {

	public SLF4JPrintStreamLogger() {
		super(new SLF4JLogOutputStream());
	}

	private static class SLF4JLogOutputStream extends LogOutputStream {

		Logger log = LoggerFactory.getLogger("Soot");

		public SLF4JLogOutputStream() {
			super();
		}

		@Override
		protected void processLine(String line, int unused) {
			log.debug(line);
		}

	}
}
