package soot.util;

import soot.util.StringTools;
import java.util.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * JUnit test suite for the StringTools.replaceAll() method.
 */
public class StringTools_replaceAll_Test extends TestCase {

    public StringTools_replaceAll_Test(String name) {
	super(name);
    }

    private static boolean useJJ = false;
    private static void runAndCompare(String orig, String toBeReplaced,
			       String replacement, String expected) {
	String result = StringTools.replaceAll(orig, toBeReplaced, replacement);
	assertEquals(expected, result);
    }

    public void testNoOccurrences() {
	runAndCompare("This string contains no occurrences of the pattern.",
		      "absent", "present",
		      "This string contains no occurrences of the pattern.");
    }

    public void testOccurrenceAtBeginning() {
	runAndCompare("The pattern is at the beginning of this string.",
		      "The pattern", "A replacement",
		      "A replacement is at the beginning of this string.");
    }

    public void testOccurrenceAtEnd() {
	runAndCompare("This string ends with the pattern.",
		      "the pattern.", "a replacement.",
		      "This string ends with a replacement.");
    }

    public void testReplaceWholeOriginal() {
	runAndCompare("The whole shebang.",
		      "The whole shebang.", "Everything in its entirety.",
		      "Everything in its entirety.");
    }

    public void testReplaceEverythingInPieces() {
	runAndCompare("old?old?old?old?old?old?old?",
		      "old?", "new!",
		      "new!new!new!new!new!new!new!");
    }

    public void testMultipleOccurrencesSameLength() {
	runAndCompare("abcOLDabcOLDabOLDOLDa",
		      "OLD", "NEW",
		      "abcNEWabcNEWabNEWNEWa");
    }

    public void testMultipleOccurrencesShorter() {
	runAndCompare("abcOLDabcOLDabOLDOLDa",
		      "OLD", "NE",
		      "abcNEabcNEabNENEa");
    }

    public void testMultipleOccurrencesLonger() {
	runAndCompare("abcOLDabcOLDabOLDOLDa",
		      "OLD", "_REFURBISHED_",
		      "abc_REFURBISHED_abc_REFURBISHED_ab_REFURBISHED__REFURBISHED_a");
    }

    public void testBlankReplacement() {
	runAndCompare("abcOLDabcOLDabOLDOLDa",
		      "OLD", "",
		      "abcabcaba");
    }

    public void testBlankPattern() {
	runAndCompare("abcOLDabcOLDabOLDOLDa",
		      "", "new",
		      "abcOLDabcOLDabOLDOLDa");
    }

    public void testPatternLongerThanOrig() {
	runAndCompare("This is a string",
		      "This is a string pattern", "This is a string replacement",
		      "This is a string");
    }

    public void testSingleLetterPatternSingleLetterReplacement() {
	runAndCompare("in this string is a phrase in in which to replace the 'i's, including the last i",
		      "i", "1",
		      "1n th1s str1ng 1s a phrase 1n 1n wh1ch to replace the '1's, 1nclud1ng the last 1");
    }

    public void testSingleLetterPatternZeroLetterReplacement() {
	runAndCompare("in this string is a phrase in in which to replace the 'i's, including the last i",
		      "i", "",
		      "n ths strng s a phrase n n whch to replace the ''s, ncludng the last ");
    }

    public void testSingleLetterPatternMultiLetterReplacement() {
	runAndCompare("in this string is a phrase in in which to replace the 'i's, including the last i",
		      "i", "III",
		      "IIIn thIIIs strIIIng IIIs a phrase IIIn IIIn whIIIch to replace the 'III's, IIIncludIIIng the last III");
    }

    public void testPatternIncludedInsideReplacement() {
	// This checks that including the pattern to be replaced within
	// the replacing text does not cause some sort of infinite loop.
	runAndCompare("Some old text to be folded.",
		      "old", "embold",
		      "Some embold text to be fembolded.");
    }

    public void testBlankOriginal() {
	runAndCompare("", "old", "new", "");
    }

    public static Test reflectionSuite() {
	TestSuite suite = new TestSuite(StringTools_replaceAll_Test.class);
	return suite;
    }

    public static void main(String[] arg) {
	if (arg.length > 0 && arg[0].equals("useJJ")) 
	    useJJ = true;
	else
	    useJJ = false;
    	junit.textui.TestRunner.run(reflectionSuite());
    }
}
