/**
 * A class used for CFG utilities that need to match different option
 * strings with classes that implement those options.
 *
 * A <tt>CFGOptionMatcher</tt> maintains a set of named
 * options, and provides a means for matching abbreviated option
 * values against those names.
 */

package soot.util.cfgcmd;

import soot.G;
import soot.CompilationDeathException;

public class CFGOptionMatcher {

    /**
     * The type stored within a <tt>CFGOptionMatcher</tt>. Options to
     * be stored in a <tt>CFGOptionMatcher</tt> must extend this
     * class.
     */
    public static abstract class CFGOption {
	private final String name;
	protected CFGOption(String name) {
	    this.name = name;
	}
	public String name() {
	    return name;
	}
    };

    private CFGOption[] options;

    /**
     * Creates a CFGOptionMatcher.
     *
     * @param options The set of command options to be stored.
     */
    public CFGOptionMatcher(CFGOption[] options) {
	this.options = options;
    }

    /**
     * Searches the options in this <tt>CFGOptionMatcher</tt>
     * looking for one whose name begins with the passed string
     * (ignoring the case of letters in the string). 
     *
     * @param quarry The string to be matched against the stored
     * option names.
     *
     * @return The matching <tt>CFGOption</tt> if exactly one of the
     * stored option names begins with <tt>quarry</tt>.
     *
     * @throws soot.CompilationDeathException if <tt>quarry</tt>
     * matches none of the option names, or if it matches more than
     * one.
     */
    public CFGOption match(String quarry) 
	throws soot.CompilationDeathException {
	String uncasedQuarry = quarry.toLowerCase();
	int match = -1;
	for (int i = 0; i < options.length; i++) {
	    String uncasedName = options[i].name().toLowerCase();
	    if (uncasedName.startsWith(uncasedQuarry)) {
		if (match == -1) {
		    match = i;
		} else {
		    G.v().out.println(quarry + " is ambiguous; it matches " +
				      options[match].name() + " and " +
				      options[i].name());
		    throw new CompilationDeathException(
			CompilationDeathException.COMPILATION_ABORTED,
			"Option parse error");
		}
	    }
	}
	if (match == -1) {
	    G.v().out.println("\"" + quarry + "\"" + 
			      " does not match any value.");
	    throw new CompilationDeathException(
		CompilationDeathException.COMPILATION_ABORTED,
		"Option parse error");
	} else {
	    return options[match];
	}
    }


    /**
     * Returns a string containing the names of all the
     * options in this <tt>CFGOptionMatcher</tt>, separated by
     * '|' characters. The string is intended for use in 
     * help messages.
     *
     * @param initialIndent The number of blank spaces to insert at the 
     *	                    beginning of the returned string. Ignored if 
     *                      negative.
     *
     * @param rightMargin   If positive, newlines will be inserted to try
     *                      to keep the length of each line in the
     *                      returned string less than or equal to
     *                      *<tt>rightMargin</tt>.
     *         
     * @param hangingIndent  If positive, this number of spaces will be
     *                       inserted immediately after each newline 
     *                       inserted to respect the <tt>rightMargin</tt>.
     */
    public String help(int initialIndent, int rightMargin, int hangingIndent) {

	StringBuffer newLineBuf = new StringBuffer(2 + rightMargin);
	newLineBuf.append('\n');
	if (hangingIndent < 0) {
	    hangingIndent = 0;
	}
	for (int i = 0; i < hangingIndent; i++) {
	    newLineBuf.append(' ');
	}
	String newLine = newLineBuf.toString();

	StringBuffer result = new StringBuffer();
	int lineLength = 0;
	for (int i = 0; i < initialIndent; i++) {
	    lineLength++;
	    result.append(' ');
	}

	for (int i = 0; i < options.length; i++) {
	    if (i > 0) {
		result.append('|');
		lineLength++;
	    }
	    String name = options[i].name();
	    int nameLength = name.length();	    
	    if ((lineLength + nameLength) > rightMargin) {
		result.append(newLine);
		lineLength = hangingIndent;
	    }
	    result.append(name);
	    lineLength += nameLength;
	}
	return result.toString();
    }
}
