/**************************************************************************
/* Getopt.java -- Java port of GNU getopt from glibc 2.0.6
/*
/* Copyright (c) 1987-1997 Free Software Foundation, Inc.
/* Java Port Copyright (c) 1998 by Aaron M. Renn (arenn@urbanophile.com)
/*
/* This program is free software; you can redistribute it and/or modify
/* it under the terms of the GNU Library General Public License as published 
/* by  the Free Software Foundation; either version 2 of the License or
/* (at your option) any later version.
/*
/* This program is distributed in the hope that it will be useful, but
/* WITHOUT ANY WARRANTY; without even the implied warranty of
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
/* GNU Library General Public License for more details.
/*
/* You should have received a copy of the GNU Library General Public License
/* along with this program; see the file COPYING.LIB.  If not, write to 
/* the Free Software Foundation Inc., 59 Temple Place - Suite 330, 
/* Boston, MA  02111-1307 USA
/**************************************************************************/

package gnu.getopt;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.text.MessageFormat;

/**************************************************************************/

/**
  * This is a Java port of GNU getopt, a class for parsing command line
  * arguments passed to programs.  It it based on the C getopt() functions
  * in glibc 2.0.6 and should parse options in a 100% compatible manner.
  * If it does not, that is a bug.  The programmer's interface is also
  * very compatible.
  * <p>
  * To use Getopt, create a Getopt object with a argv array passed to the
  * main method, then call the getopt() method in a loop.  It will return an
  * int that contains the value of the option character parsed from the
  * command line.  When there are no more options to be parsed, it
  * returns -1.
  * <p>
  * A command line option can be defined to take an argument.  If an
  * option has an argument, the value of that argument is stored in an
  * instance variable called optarg, which can be accessed using the
  * getOptarg() method.  If an option that requires an argument is
  * found, but there is no argument present, then an error message is
  * printed. Normally getopt() returns a '?' in this situation, but
  * that can be changed as described below.
  * <p>
  * If an invalid option is encountered, an error message is printed
  * to the standard error and getopt() returns a '?'.  The value of the
  * invalid option encountered is stored in the instance variable optopt
  * which can be retrieved using the getOptopt() method.  To suppress
  * the printing of error messages for this or any other error, set
  * the value of the opterr instance variable to false using the 
  * setOpterr() method.
  * <p>
  * Between calls to getopt(), the instance variable optind is used to
  * keep track of where the object is in the parsing process.  After all
  * options have been returned, optind is the index in argv of the first
  * non-option argument.  This variable can be accessed with the getOptind()
  * method.
  * <p>
  * Note that this object expects command line options to be passed in the
  * traditional Unix manner.  That is, proceeded by a '-' character. 
  * Multiple options can follow the '-'.  For example "-abc" is equivalent
  * to "-a -b -c".  If an option takes a required argument, the value
  * of the argument can immediately follow the option character or be
  * present in the next argv element.  For example, "-cfoo" and "-c foo"
  * both represent an option character of 'c' with an argument of "foo"
  * assuming c takes a required argument.  If an option takes an argument
  * that is not required, then any argument must immediately follow the
  * option character in the same argv element.  For example, if c takes
  * a non-required argument, then "-cfoo" represents option character 'c'
  * with an argument of "foo" while "-c foo" represents the option
  * character 'c' with no argument, and a first non-option argv element
  * of "foo".
  * <p>
  * The user can stop getopt() from scanning any further into a command line
  * by using the special argument "--" by itself.  For example: 
  * "-a -- -d" would return an option character of 'a', then return -1
  * The "--" is discarded and "-d" is pointed to by optind as the first
  * non-option argv element.
  * <p>
  * Here is a basic example of using Getopt:
  * <p>
  * <pre>
  * Getopt g = new Getopt("testprog", argv, "ab:c::d");
  * //
  * int c;
  * String arg;
  * while ((c = g.getopt()) != -1)
  *   {
  *     switch(c)
  *       {
  *          case 'a':
  *          case 'd':
  *            System.out.print("You picked " + (char)c + "\n");
  *            break;
  *            //
  *          case 'b':
  *          case 'c':
  *            arg = g.getOptarg();
  *            System.out.print("You picked " + (char)c + 
  *                             " with an argument of " +
  *                             ((arg != null) ? arg : "null") + "\n");
  *            break;
  *            //
  *          case '?':
  *            break; // getopt() already printed an error
  *            //
  *          default:
  *            System.out.print("getopt() returned " + c + "\n");
  *       }
  *   }
  * </pre>
  * <p>
  * In this example, a new Getopt object is created with three params.
  * The first param is the program name.  This is for printing error
  * messages in the form "program: error message".  In the C version, this
  * value is taken from argv[0], but in Java the program name is not passed
  * in that element, thus the need for this parameter.  The second param is
  * the argument list that was passed to the main() method.  The third
  * param is the list of valid options.  Each character represents a valid
  * option.  If the character is followed by a single colon, then that
  * option has a required argument.  If the character is followed by two
  * colons, then that option has an argument that is not required.
  * <p>
  * Note in this example that the value returned from getopt() is cast to
  * a char prior to printing.  This is required in order to make the value
  * display correctly as a character instead of an integer.
  * <p>
  * If the first character in the option string is a colon, for example
  * ":abc::d", then getopt() will return a ':' instead of a '?' when it
  * encounters an option with a missing required argument.  This allows the
  * caller to distinguish between invalid options and valid options that
  * are simply incomplete.
  * <p>
  * In the traditional Unix getopt(), -1 is returned when the first non-option
  * charcter is encountered.  In GNU getopt(), the default behavior is to
  * allow options to appear anywhere on the command line.  The getopt()
  * method permutes the argument to make it appear to the caller that all
  * options were at the beginning of the command line, and all non-options
  * were at the end.  For example, calling getopt() with command line args
  * of "-a foo bar -d" returns options 'a' and 'd', then sets optind to 
  * point to "foo".  The program would read the last two argv elements as
  * "foo" and "bar", just as if the user had typed "-a -d foo bar". 
  * <p> 
  * The user can force getopt() to stop scanning the command line with
  * the special argument "--" by itself.  Any elements occuring before the
  * "--" are scanned and permuted as normal.  Any elements after the "--"
  * are returned as is as non-option argv elements.  For example, 
  * "foo -a -- bar -d" would return  option 'a' then -1.  optind would point 
  * to "foo", "bar" and "-d" as the non-option argv elements.  The "--"
  * is discarded by getopt().
  * <p>
  * There are two ways this default behavior can be modified.  The first is
  * to specify traditional Unix getopt() behavior (which is also POSIX
  * behavior) in which scanning stops when the first non-option argument
  * encountered.  (Thus "-a foo bar -d" would return 'a' as an option and
  * have "foo", "bar", and "-d" as non-option elements).  The second is to
  * allow options anywhere, but to return all elements in the order they
  * occur on the command line.  When a non-option element is ecountered,
  * an integer 1 is returned and the value of the non-option element is
  * stored in optarg is if it were the argument to that option.  For
  * example, "-a foo -d", returns first 'a', then 1 (with optarg set to
  * "foo") then 'd' then -1.  When this "return in order" functionality
  * is enabled, the only way to stop getopt() from scanning all command
  * line elements is to use the special "--" string by itself as described
  * above.  An example is "-a foo -b -- bar", which would return 'a', then
  * integer 1 with optarg set to "foo", then 'b', then -1.  optind would
  * then point to "bar" as the first non-option argv element.  The "--"
  * is discarded.
  * <p>
  * The POSIX/traditional behavior is enabled by either setting the 
  * property "gnu.posixly_correct" or by putting a '+' sign as the first
  * character of the option string.  The difference between the two 
  * methods is that setting the gnu.posixly_correct property also forces
  * certain error messages to be displayed in POSIX format.  To enable
  * the "return in order" functionality, put a '-' as the first character
  * of the option string.  Note that after determining the proper 
  * behavior, Getopt strips this leading '+' or '-', meaning that a ':'
  * placed as the second character after one of those two will still cause
  * getopt() to return a ':' instead of a '?' if a required option
  * argument is missing.
  * <p>
  * In addition to traditional single character options, GNU Getopt also
  * supports long options.  These are preceeded by a "--" sequence and
  * can be as long as desired.  Long options provide a more user-friendly
  * way of entering command line options.  For example, in addition to a
  * "-h" for help, a program could support also "--help".  
  * <p>
  * Like short options, long options can also take a required or non-required 
  * argument.  Required arguments can either be specified by placing an
  * equals sign after the option name, then the argument, or by putting the
  * argument in the next argv element.  For example: "--outputdir=foo" and
  * "--outputdir foo" both represent an option of "outputdir" with an
  * argument of "foo", assuming that outputdir takes a required argument.
  * If a long option takes a non-required argument, then the equals sign
  * form must be used to specify the argument.  In this case,
  * "--outputdir=foo" would represent option outputdir with an argument of
  * "foo" while "--outputdir foo" would represent the option outputdir
  * with no argument and a first non-option argv element of "foo".
  * <p>
  * Long options can also be specified using a special POSIX argument 
  * format (one that I highly discourage).  This form of entry is 
  * enabled by placing a "W;" (yes, 'W' then a semi-colon) in the valid
  * option string.  This causes getopt to treat the name following the
  * "-W" as the name of the long option.  For example, "-W outputdir=foo"
  * would be equivalent to "--outputdir=foo".  The name can immediately
  * follow the "-W" like so: "-Woutputdir=foo".  Option arguments are
  * handled identically to normal long options.  If a string follows the 
  * "-W" that does not represent a valid long option, then getopt() returns
  * 'W' and the caller must decide what to do.  Otherwise getopt() returns
  * a long option value as described below.
  * <p>
  * While long options offer convenience, they can also be tedious to type
  * in full.  So it is permissible to abbreviate the option name to as
  * few characters as required to uniquely identify it.  If the name can
  * represent multiple long options, then an error message is printed and
  * getopt() returns a '?'.  
  * <p>
  * If an invalid option is specified or a required option argument is 
  * missing, getopt() prints an error and returns a '?' or ':' exactly
  * as for short options.  Note that when an invalid long option is
  * encountered, the optopt variable is set to integer 0 and so cannot
  * be used to identify the incorrect option the user entered.
  * <p>
  * Long options are defined by LongOpt objects.  These objects are created
  * with a contructor that takes four params: a String representing the
  * object name, a integer specifying what arguments the option takes
  * (the value is one of LongOpt.NO_ARGUMENT, LongOpt.REQUIRED_ARGUMENT,
  * or LongOpt.OPTIONAL_ARGUMENT), a StringBuffer flag object (described
  * below), and an integer value (described below).
  * <p>
  * To enable long option parsing, create an array of LongOpt's representing
  * the legal options and pass it to the Getopt() constructor.  WARNING: If
  * all elements of the array are not populated with LongOpt objects, the
  * getopt() method will throw a NullPointerException.
  * <p>
  * When getopt() is called and a long option is encountered, one of two
  * things can be returned.  If the flag field in the LongOpt object 
  * representing the long option is non-null, then the integer value field
  * is stored there and an integer 0 is returned to the caller.  The val
  * field can then be retrieved from the flag field.  Note that since the
  * flag field is a StringBuffer, the appropriate String to integer converions
  * must be performed in order to get the actual int value stored there.
  * If the flag field in the LongOpt object is null, then the value field
  * of the LongOpt is returned.  This can be the character of a short option.
  * This allows an app to have both a long and short option sequence 
  * (say, "-h" and "--help") that do the exact same thing.
  * <p>
  * With long options, there is an alternative method of determining 
  * which option was selected.  The method getLongind() will return the
  * the index in the long option array (NOT argv) of the long option found.
  * So if multiple long options are configured to return the same value,
  * the application can use getLongind() to distinguish between them. 
  * <p>
  * Here is an expanded Getopt example using long options and various
  * techniques described above:
  * <p>
  * <pre>
  * int c;
  * String arg;
  * LongOpt[] longopts = new LongOpt[3];
  * // 
  * StringBuffer sb = new StringBuffer();
  * longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
  * longopts[1] = new LongOpt("outputdir", LongOpt.REQUIRED_ARGUMENT, sb, 'o'); 
  * longopts[2] = new LongOpt("maximum", LongOpt.OPTIONAL_ARGUMENT, null, 2);
  * // 
  * Getopt g = new Getopt("testprog", argv, "-:bc::d:hW;", longopts);
  * g.setOpterr(false); // We'll do our own error handling
  * //
  * while ((c = g.getopt()) != -1)
  *   switch (c)
  *     {
  *        case 0:
  *          arg = g.getOptarg();
  *          System.out.println("Got long option with value '" +
  *                             (char)(new Integer(sb.toString())).intValue()
  *                             + "' with argument " +
  *                             ((arg != null) ? arg : "null"));
  *          break;
  *          //
  *        case 1:
  *          System.out.println("I see you have return in order set and that " +
  *                             "a non-option argv element was just found " +
  *                             "with the value '" + g.getOptarg() + "'");
  *          break;
  *          //
  *        case 2:
  *          arg = g.getOptarg();
  *          System.out.println("I know this, but pretend I didn't");
  *          System.out.println("We picked option " +
  *                             longopts[g.getLongind()].getName() +
  *                           " with value " + 
  *                           ((arg != null) ? arg : "null"));
  *          break;
  *          //
  *        case 'b':
  *          System.out.println("You picked plain old option " + (char)c);
  *          break;
  *          //
  *        case 'c':
  *        case 'd':
  *          arg = g.getOptarg();
  *          System.out.println("You picked option '" + (char)c + 
  *                             "' with argument " +
  *                             ((arg != null) ? arg : "null"));
  *          break;
  *          //
  *        case 'h':
  *          System.out.println("I see you asked for help");
  *          break;
  *          //
  *        case 'W':
  *          System.out.println("Hmmm. You tried a -W with an incorrect long " +
  *                             "option name");
  *          break;
  *          //
  *        case ':':
  *          System.out.println("Doh! You need an argument for option " +
  *                             (char)g.getOptopt());
  *          break;
  *          //
  *        case '?':
  *          System.out.println("The option '" + (char)g.getOptopt() + 
  *                           "' is not valid");
  *          break;
  *          //
  *        default:
  *          System.out.println("getopt() returned " + c);
  *          break;
  *     }
  * //
  * for (int i = g.getOptind(); i < argv.length ; i++)
  *   System.out.println("Non option argv element: " + argv[i] + "\n");
  * </pre>
  * <p>
  * There is an alternative form of the constructor used for long options
  * above.  This takes a trailing boolean flag.  If set to false, Getopt
  * performs identically to the example, but if the boolean flag is true
  * then long options are allowed to start with a single '-' instead of
  * "--".  If the first character of the option is a valid short option
  * character, then the option is treated as if it were the short option.
  * Otherwise it behaves as if the option is a long option.  Note that
  * the name given to this option - long_only - is very counter-intuitive.
  * It does not cause only long options to be parsed but instead enables
  * the behavior described above.
  * <p> 
  * Note that the functionality and variable names used are driven from 
  * the C lib version as this object is a port of the C code, not a 
  * new implementation.  This should aid in porting existing C/C++ code,
  * as well as helping programmers familiar with the glibc version to
  * adapt to the Java version even if it seems very non-Java at times.
  * <p>
  * In this release I made all instance variables protected due to
  * overwhelming public demand.  Any code which relied on optarg,
  * opterr, optind, or optopt being public will need to be modified to
  * use the appropriate access methods.
  * <p>
  * Please send all bug reports, requests, and comments to
  * <a href="mailto:arenn@urbanophile.com">arenn@urbanophile.com</a>.
  *
  * @version 1.0.7
  *
  * @author Roland McGrath (roland@gnu.ai.mit.edu)
  * @author Ulrich Drepper (drepper@cygnus.com)
  * @author Aaron M. Renn (arenn@urbanophile.com)
  *
  * @see LongOpt
  */
public class Getopt extends Object
{

/**************************************************************************/

/*
 * Class Variables
 */

/** 
  * Describe how to deal with options that follow non-option ARGV-elements.
  *
  * If the caller did not specify anything,
  * the default is REQUIRE_ORDER if the property 
  * gnu.posixly_correct is defined, PERMUTE otherwise.
  *
  * The special argument `--' forces an end of option-scanning regardless
  * of the value of `ordering'.  In the case of RETURN_IN_ORDER, only
  * `--' can cause `getopt' to return -1 with `optind' != ARGC.
  *
  * REQUIRE_ORDER means don't recognize them as options;
  * stop option processing when the first non-option is seen.
  * This is what Unix does.
  * This mode of operation is selected by either setting the property
  * gnu.posixly_correct, or using `+' as the first character
  * of the list of option characters.
  */
protected static final int REQUIRE_ORDER = 1;

/**
  * PERMUTE is the default.  We permute the contents of ARGV as we scan,
  * so that eventually all the non-options are at the end.  This allows options
  * to be given in any order, even with programs that were not written to
  * expect this.
  */
protected static final int PERMUTE = 2;

/**
  * RETURN_IN_ORDER is an option available to programs that were written
  * to expect options and other ARGV-elements in any order and that care about
  * the ordering of the two.  We describe each non-option ARGV-element
  * as if it were the argument of an option with character code 1.
  * Using `-' as the first character of the list of option characters
  * selects this mode of operation.
  */
protected static final int RETURN_IN_ORDER = 3;

/**************************************************************************/

/*
 * Instance Variables
 */
 
/**
  * For communication from `getopt' to the caller.
  * When `getopt' finds an option that takes an argument,
  * the argument value is returned here.
  * Also, when `ordering' is RETURN_IN_ORDER,
  * each non-option ARGV-element is returned here.
  */
protected String optarg;

/**
  *  Index in ARGV of the next element to be scanned.
  *  This is used for communication to and from the caller
  *  and for communication between successive calls to `getopt'.
  *
  *  On entry to `getopt', zero means this is the first call; initialize.
  *
  *  When `getopt' returns -1, this is the index of the first of the
  *  non-option elements that the caller should itself scan.
  *
  *  Otherwise, `optind' communicates from one call to the next
  *  how much of ARGV has been scanned so far.  
  */
protected int optind = 0;

/** 
  * Callers store false here to inhibit the error message
  * for unrecognized options.  
  */
protected boolean opterr = true;

/** 
  * When an unrecognized option is encountered, getopt will return a '?'
  * and store the value of the invalid option here.
  */
protected int optopt = '?';

/** 
  * The next char to be scanned in the option-element
  * in which the last option character we returned was found.
  * This allows us to pick up the scan where we left off.
  *
  * If this is zero, or a null string, it means resume the scan
  * by advancing to the next ARGV-element.  
  */
protected String nextchar;

/**
  * This is the string describing the valid short options.
  */
protected String optstring;

/**
  * This is an array of LongOpt objects which describ the valid long 
  * options.
  */
protected LongOpt[] long_options;

/**
  * This flag determines whether or not we are parsing only long args
  */
protected boolean long_only;

/**
  * Stores the index into the long_options array of the long option found
  */
protected int longind;

/**
  * The flag determines whether or not we operate in strict POSIX compliance
  */
protected boolean posixly_correct;

/**
  * A flag which communicates whether or not checkLongOption() did all
  * necessary processing for the current option
  */
protected boolean longopt_handled;

/**
  * The index of the first non-option in argv[]
  */
protected int first_nonopt = 1;

/**
  * The index of the last non-option in argv[]
  */
protected int last_nonopt = 1;

/**
  * Flag to tell getopt to immediately return -1 the next time it is
  * called.
  */
private boolean endparse = false;

/**
  * Saved argument list passed to the program
  */
protected String[] argv;

/**
  * Determines whether we permute arguments or not
  */
protected int ordering;

/**
  * Name to print as the program name in error messages.  This is necessary
  * since Java does not place the program name in argv[0]
  */
protected String progname;

/**
  * The localized strings are kept in a separate file
  */
private ResourceBundle _messages = PropertyResourceBundle.getBundle(
                           "gnu/getopt/MessagesBundle", Locale.getDefault());

/**************************************************************************/

/*
 * Constructors
 */

/**
  * Construct a basic Getopt instance with the given input data.  Note that
  * this handles "short" options only.
  *
  * @param progname The name to display as the program name when printing errors
  * @param argv The String array passed as the command line to the program.
  * @param optstring A String containing a description of the valid args for this program
  */
public
Getopt(String progname, String[] argv, String optstring)
{
  this(progname, argv, optstring, null, false);
}

/**************************************************************************/

/**
  * Construct a Getopt instance with given input data that is capable of
  * parsing long options as well as short.
  *
  * @param progname The name to display as the program name when printing errors
  * @param argv The String array passed as the command ilne to the program
  * @param optstring A String containing a description of the valid short args for this program
  * @param long_options An array of LongOpt objects that describes the valid long args for this program
  */
public
Getopt(String progname, String[] argv, String optstring, 
       LongOpt[] long_options)
{
  this(progname, argv, optstring, long_options, false);
}

/**************************************************************************/

/**
  * Construct a Getopt instance with given input data that is capable of
  * parsing long options and short options.  Contrary to what you might
  * think, the flag 'long_only' does not determine whether or not we 
  * scan for only long arguments.  Instead, a value of true here allows
  * long arguments to start with a '-' instead of '--' unless there is a
  * conflict with a short option name.
  *
  * @param progname The name to display as the program name when printing errors
  * @param argv The String array passed as the command ilne to the program
  * @param optstring A String containing a description of the valid short args for this program
  * @param long_options An array of LongOpt objects that describes the valid long args for this program
  * @param long_only true if long options that do not conflict with short options can start with a '-' as well as '--'
  */
public
Getopt(String progname, String[] argv, String optstring, 
       LongOpt[] long_options, boolean long_only)
{
  if (optstring.length() == 0)
    optstring = " ";

  // This function is essentially _getopt_initialize from GNU getopt
  this.progname = progname;
  this.argv = argv;
  this.optstring = optstring;
  this.long_options = long_options;
  this.long_only = long_only;

  // Check for property "gnu.posixly_correct" to determine whether to
  // strictly follow the POSIX standard.  This replaces the "POSIXLY_CORRECT"
  // environment variable in the C version
  if (System.getProperty("gnu.posixly_correct", null) == null)
    posixly_correct = false;
  else
    {
      posixly_correct = true;
      _messages = PropertyResourceBundle.getBundle("gnu/getopt/MessagesBundle",
                                                   Locale.US);
    }

  // Determine how to handle the ordering of options and non-options
  if (optstring.charAt(0) == '-')
    {
      ordering = RETURN_IN_ORDER;
      if (optstring.length() > 1)
        this.optstring = optstring.substring(1);
    }
  else if (optstring.charAt(0) == '+')
    {
      ordering = REQUIRE_ORDER;
      if (optstring.length() > 1)
        this.optstring = optstring.substring(1);
    }
  else if (posixly_correct)
    {
      ordering = REQUIRE_ORDER;
    }
  else
    {
      ordering = PERMUTE; // The normal default case
    }
}

/**************************************************************************/
 
/*
 * Instance Methods
 */

/**
  * In GNU getopt, it is possible to change the string containg valid options
  * on the fly because it is passed as an argument to getopt() each time.  In
  * this version we do not pass the string on every call.  In order to allow
  * dynamic option string changing, this method is provided.
  *
  * @param optstring The new option string to use
  */
public void
setOptstring(String optstring)
{
  if (optstring.length() == 0)
    optstring = " ";

  this.optstring = optstring;
}

/**************************************************************************/

/**
  * optind it the index in ARGV of the next element to be scanned.
  * This is used for communication to and from the caller
  * and for communication between successive calls to `getopt'.
  *
  * When `getopt' returns -1, this is the index of the first of the
  * non-option elements that the caller should itself scan.
  *
  * Otherwise, `optind' communicates from one call to the next
  * how much of ARGV has been scanned so far.  
  */
public int
getOptind()
{
  return(optind);
}

/**************************************************************************/

/**
  * This method allows the optind index to be set manually.  Normally this
  * is not necessary (and incorrect usage of this method can lead to serious
  * lossage), but optind is a public symbol in GNU getopt, so this method 
  * was added to allow it to be modified by the caller if desired.
  *
  * @param optind The new value of optind
  */
public void
setOptind(int optind)
{
  this.optind = optind;
}

/**************************************************************************/

/**
  * Since in GNU getopt() the argument vector is passed back in to the
  * function every time, the caller can swap out argv on the fly.  Since
  * passing argv is not required in the Java version, this method allows
  * the user to override argv.  Note that incorrect use of this method can
  * lead to serious lossage.
  *
  * @param argv New argument list
  */
public void
setArgv(String[] argv)
{
  this.argv = argv;
}

/**************************************************************************/

/** 
  * For communication from `getopt' to the caller.
  * When `getopt' finds an option that takes an argument,
  * the argument value is returned here.
  * Also, when `ordering' is RETURN_IN_ORDER,
  * each non-option ARGV-element is returned here.
  * No set method is provided because setting this variable has no effect.
  */
public String
getOptarg()
{
  return(optarg);
}

/**************************************************************************/

/**
  * Normally Getopt will print a message to the standard error when an
  * invalid option is encountered.  This can be suppressed (or re-enabled)
  * by calling this method.  There is no get method for this variable 
  * because if you can't remember the state you set this to, why should I?
  */
public void
setOpterr(boolean opterr)
{
  this.opterr = opterr;
}

/**************************************************************************/

/**
  * When getopt() encounters an invalid option, it stores the value of that
  * option in optopt which can be retrieved with this method.  There is
  * no corresponding set method because setting this variable has no effect.
  */
public int
getOptopt()
{
  return(optopt);
}

/**************************************************************************/

/**
  * Returns the index into the array of long options (NOT argv) representing
  * the long option that was found.
  */
public int
getLongind()
{
  return(longind);
}

/**************************************************************************/

/**
  * Exchange the shorter segment with the far end of the longer segment.
  * That puts the shorter segment into the right place.
  * It leaves the longer segment in the right place overall,
  * but it consists of two parts that need to be swapped next.
  * This method is used by getopt() for argument permutation.
  */
protected void
exchange(String[] argv)
{
  int bottom = first_nonopt;
  int middle = last_nonopt;
  int top = optind;
  String tem;

  while (top > middle && middle > bottom)
    {
      if (top - middle > middle - bottom)
        {
          // Bottom segment is the short one. 
          int len = middle - bottom;
          int i;

          // Swap it with the top part of the top segment. 
          for (i = 0; i < len; i++)
            {
              tem = argv[bottom + i];
              argv[bottom + i] = argv[top - (middle - bottom) + i];
              argv[top - (middle - bottom) + i] = tem;
            }
          // Exclude the moved bottom segment from further swapping. 
          top -= len;
        }
      else
        {
          // Top segment is the short one.
          int len = top - middle;
          int i;

          // Swap it with the bottom part of the bottom segment. 
          for (i = 0; i < len; i++)
            {
              tem = argv[bottom + i];
              argv[bottom + i] = argv[middle + i];
              argv[middle + i] = tem;
            }
          // Exclude the moved top segment from further swapping. 
          bottom += len;
        }
    }

  // Update records for the slots the non-options now occupy. 

  first_nonopt += (optind - last_nonopt);
  last_nonopt = optind;
}

/**************************************************************************/

/**
  * Check to see if an option is a valid long option.  Called by getopt().
  * Put in a separate method because this needs to be done twice.  (The
  * C getopt authors just copy-pasted the code!).
  *
  * @param longind A buffer in which to store the 'val' field of found LongOpt
  *
  * @return Various things depending on circumstances
  */
protected int
checkLongOption()
{
  LongOpt pfound = null;
  int nameend;
  boolean ambig;
  boolean exact;
  
  longopt_handled = true;
  ambig = false;
  exact = false;
  longind = -1;

  nameend = nextchar.indexOf("=");
  if (nameend == -1)
    nameend = nextchar.length();
  
  // Test all lnog options for either exact match or abbreviated matches
  for (int i = 0; i < long_options.length; i++)
    {
      if (long_options[i].getName().startsWith(nextchar.substring(0, nameend)))
        {
          if (long_options[i].getName().equals(nextchar.substring(0, nameend)))
            {
              // Exact match found
              pfound = long_options[i];
              longind = i;
              exact = true;
              break;
            }
          else if (pfound == null)
            {
              // First nonexact match found
              pfound = long_options[i];
              longind = i;
            }
          else
            {
              // Second or later nonexact match found
              ambig = true;
            }
        }
    } // for
  
  // Print out an error if the option specified was ambiguous
  if (ambig && !exact)
    {
      if (opterr)
        {
          Object[] msgArgs = { progname, argv[optind] };
          System.err.println(MessageFormat.format(
                             _messages.getString("getopt.ambigious"), 
                             msgArgs));
        }

       nextchar = "";
       optopt = 0;
       ++optind;
 
       return('?');
    }
 
  if (pfound != null)
    {
      ++optind;
 
      if (nameend != nextchar.length())
        {
          if (pfound.has_arg != LongOpt.NO_ARGUMENT)
            {
              if (nextchar.substring(nameend).length() > 1)
                optarg = nextchar.substring(nameend+1);
              else
                optarg = "";
            }
          else
            {
              if (opterr)
                {
                  // -- option
                  if (argv[optind - 1].startsWith("--"))
                    {
                      Object[] msgArgs = { progname, pfound.name };
                      System.err.println(MessageFormat.format(
                                  _messages.getString("getopt.arguments1"), 
                                  msgArgs));
                    }
                  // +option or -option
                  else
                    {
                      Object[] msgArgs = { progname, new 
                               Character(argv[optind-1].charAt(0)).toString(),
                               pfound.name };
                      System.err.println(MessageFormat.format(
                               _messages.getString("getopt.arguments2"), 
                               msgArgs));
                    }
                 }
   
              nextchar = "";
              optopt = pfound.val;
   
              return('?');
            }
        } // if (nameend)
      else if (pfound.has_arg == LongOpt.REQUIRED_ARGUMENT)
        {
          if (optind < argv.length)
            {
               optarg = argv[optind];
               ++optind;
            }
          else
            {
              if (opterr)
                {
                  Object[] msgArgs = { progname, argv[optind-1] };
                  System.err.println(MessageFormat.format(
                                     _messages.getString("getopt.requires"), 
                                     msgArgs));
                }
   
              nextchar = "";
              optopt = pfound.val;
              if (optstring.charAt(0) == ':')
                return(':');
              else
                return('?');
            }
        } // else if (pfound)
   
      nextchar = "";

      if (pfound.flag != null)
        {
          pfound.flag.setLength(0);
          pfound.flag.append(pfound.val);
   
          return(0);
        }

      return(pfound.val);
   } // if (pfound != null)
  
  longopt_handled = false;

  return(0);
}

/**************************************************************************/

/**
  * This method returns a char that is the current option that has been
  * parsed from the command line.  If the option takes an argument, then
  * the internal variable 'optarg' is set which is a String representing
  * the the value of the argument.  This value can be retrieved by the
  * caller using the getOptarg() method.  If an invalid option is found,
  * an error message is printed and a '?' is returned.  The name of the
  * invalid option character can be retrieved by calling the getOptopt()
  * method.  When there are no more options to be scanned, this method
  * returns -1.  The index of first non-option element in argv can be
  * retrieved with the getOptind() method.
  *
  * @return Various things as described above
  */
public int
getopt()
{
  optarg = null;

  if (endparse == true)
    return(-1);

  if ((nextchar == null) || (nextchar.equals("")))
    {
      // If we have just processed some options following some non-options,
      //  exchange them so that the options come first.
      if (last_nonopt > optind)
        last_nonopt = optind;
      if (first_nonopt > optind)
        first_nonopt = optind;

      if (ordering == PERMUTE)
        {
          // If we have just processed some options following some non-options,
          // exchange them so that the options come first.
          if ((first_nonopt != last_nonopt) && (last_nonopt != optind))
            exchange(argv);
          else if (last_nonopt != optind)
            first_nonopt = optind;

          // Skip any additional non-options
          // and extend the range of non-options previously skipped.
          while ((optind < argv.length) && (argv[optind].equals("") ||
            (argv[optind].charAt(0) != '-') || argv[optind].equals("-")))
            {
              optind++;
            }
          
          last_nonopt = optind;
        }

      // The special ARGV-element `--' means premature end of options.
      // Skip it like a null option,
      // then exchange with previous non-options as if it were an option,
      // then skip everything else like a non-option.
      if ((optind != argv.length) && argv[optind].equals("--"))
        {
          optind++;

          if ((first_nonopt != last_nonopt) && (last_nonopt != optind))
            exchange (argv);
          else if (first_nonopt == last_nonopt)
            first_nonopt = optind;

          last_nonopt = argv.length;

          optind = argv.length;
        }

      // If we have done all the ARGV-elements, stop the scan
      // and back over any non-options that we skipped and permuted.
      if (optind == argv.length)
        {
          // Set the next-arg-index to point at the non-options
          // that we previously skipped, so the caller will digest them.
          if (first_nonopt != last_nonopt)
            optind = first_nonopt;

          return(-1);
        }

      // If we have come to a non-option and did not permute it,
      // either stop the scan or describe it to the caller and pass it by.
      if (argv[optind].equals("") || (argv[optind].charAt(0) != '-') || 
          argv[optind].equals("-"))
        {
          if (ordering == REQUIRE_ORDER)
            return(-1);

            optarg = argv[optind++];
            return(1);
        }
      
      // We have found another option-ARGV-element.
      // Skip the initial punctuation.
      if (argv[optind].startsWith("--"))
        nextchar = argv[optind].substring(2);
      else
        nextchar = argv[optind].substring(1);
   }

  // Decode the current option-ARGV-element.

  /* Check whether the ARGV-element is a long option.

     If long_only and the ARGV-element has the form "-f", where f is
     a valid short option, don't consider it an abbreviated form of
     a long option that starts with f.  Otherwise there would be no
     way to give the -f short option.

     On the other hand, if there's a long option "fubar" and
     the ARGV-element is "-fu", do consider that an abbreviation of
     the long option, just like "--fu", and not "-f" with arg "u".

     This distinction seems to be the most useful approach.  */
  if ((long_options != null) && (argv[optind].startsWith("--")
      || (long_only && ((argv[optind].length()  > 2) || 
      (optstring.indexOf(argv[optind].charAt(1)) == -1)))))
    {
       int c = checkLongOption();

       if (longopt_handled)
         return(c);
         
      // Can't find it as a long option.  If this is not getopt_long_only,
      // or the option starts with '--' or is not a valid short
      // option, then it's an error.
      // Otherwise interpret it as a short option.
      if (!long_only || argv[optind].startsWith("--")
        || (optstring.indexOf(nextchar.charAt(0)) == -1))
        {
          if (opterr)
            {
              if (argv[optind].startsWith("--"))
                {
                  Object[] msgArgs = { progname, nextchar };
                  System.err.println(MessageFormat.format(
                                   _messages.getString("getopt.unrecognized"), 
                                   msgArgs));
                }
              else
                {
                  Object[] msgArgs = { progname, new 
                                 Character(argv[optind].charAt(0)).toString(), 
                                 nextchar };
                  System.err.println(MessageFormat.format(
                                 _messages.getString("getopt.unrecognized2"), 
                                 msgArgs));
                }
            }

          nextchar = "";
          ++optind;
          optopt = 0;
    
          return('?');
        }
    } // if (longopts)

  // Look at and handle the next short option-character */
  int c = nextchar.charAt(0); //**** Do we need to check for empty str?
  if (nextchar.length() > 1)
    nextchar = nextchar.substring(1);
  else
    nextchar = "";
  
  String temp = null;
  if (optstring.indexOf(c) != -1)
    temp = optstring.substring(optstring.indexOf(c));

  if (nextchar.equals(""))
    ++optind;

  if ((temp == null) || (c == ':'))
    {
      if (opterr)
        {
          if (posixly_correct)
            {
              // 1003.2 specifies the format of this message
              Object[] msgArgs = { progname, new 
                                   Character((char)c).toString() };
              System.err.println(MessageFormat.format(
                            _messages.getString("getopt.illegal"), msgArgs));
            }
          else
            {
              Object[] msgArgs = { progname, new 
                                   Character((char)c).toString() };
              System.err.println(MessageFormat.format(
                            _messages.getString("getopt.invalid"), msgArgs));
            }
        }

      optopt = c;

      return('?');
    }

  // Convenience. Treat POSIX -W foo same as long option --foo
  if ((temp.charAt(0) == 'W') && (temp.length() > 1) && (temp.charAt(1) == ';'))
    {
      if (!nextchar.equals(""))
        {
          optarg = nextchar;
        }
      // No further cars in this argv element and no more argv elements
      else if (optind == argv.length)
        {
          if (opterr)
            {
              // 1003.2 specifies the format of this message. 
              Object[] msgArgs = { progname, new 
                                   Character((char)c).toString() };
              System.err.println(MessageFormat.format(
                            _messages.getString("getopt.requires2"), msgArgs));
            }

          optopt = c;
          if (optstring.charAt(0) == ':')
            return(':');
          else
            return('?');
        }
      else
        {
          // We already incremented `optind' once;
          // increment it again when taking next ARGV-elt as argument. 
          nextchar = argv[optind];
          optarg  = argv[optind];
        }

      c = checkLongOption();

      if (longopt_handled)
        return(c);
      else
        // Let the application handle it
        {
          nextchar = null;
          ++optind;
          return('W');
        }
    }

  if ((temp.length() > 1) && (temp.charAt(1) == ':'))
    {
      if ((temp.length() > 2) && (temp.charAt(2) == ':'))
        // This is an option that accepts and argument optionally
        {
          if (!nextchar.equals(""))
            {
               optarg = nextchar;
               ++optind;
            }
          else
            {
              optarg = null;
            }

          nextchar = null;
        }
      else
        {
          if (!nextchar.equals(""))
            {
              optarg = nextchar;
              ++optind;
            }
          else if (optind == argv.length)
            {
              if (opterr)
                {
                  // 1003.2 specifies the format of this message
                  Object[] msgArgs = { progname, new 
                                       Character((char)c).toString() };
                  System.err.println(MessageFormat.format(
                            _messages.getString("getopt.requires2"), msgArgs));
                }

              optopt = c;
 
              if (optstring.charAt(0) == ':')
                return(':');
              else
                return('?');
            }
          else
            {
              optarg = argv[optind];
              ++optind;

              // Ok, here's an obscure Posix case.  If we have o:, and
              // we get -o -- foo, then we're supposed to skip the --,
              // end parsing of options, and make foo an operand to -o.
              // Only do this in Posix mode.
              if ((posixly_correct) && optarg.equals("--"))
                {
                  // If end of argv, error out
                  if (optind == argv.length)
                    {
                      if (opterr)
                        {
                          // 1003.2 specifies the format of this message
                          Object[] msgArgs = { progname, new 
                                               Character((char)c).toString() };
                          System.err.println(MessageFormat.format(
                             _messages.getString("getopt.requires2"), msgArgs));
                        }

                      optopt = c;
 
                      if (optstring.charAt(0) == ':')
                        return(':');
                      else
                        return('?');
                    }

                  // Set new optarg and set to end
                  // Don't permute as we do on -- up above since we
                  // know we aren't in permute mode because of Posix.
                  optarg = argv[optind];
                  ++optind;
                  first_nonopt = optind;
                  last_nonopt = argv.length;
                  endparse = true;
                }
            }

          nextchar = null;
        }
    }

  return(c);
}

} // Class Getopt


