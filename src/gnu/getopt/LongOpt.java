/**************************************************************************
/* LongOpt.java -- Long option object for Getopt
/*
/* Copyright (c) 1998 by Aaron M. Renn (arenn@urbanophile.com)
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
  * This object represents the definition of a long option in the Java port
  * of GNU getopt.  An array of LongOpt objects is passed to the Getopt
  * object to define the list of valid long options for a given parsing
  * session.  Refer to the getopt documentation for details on the
  * format of long options.
  * 
  * @version 1.0.5
  * @author Aaron M. Renn (arenn@urbanophile.com)
  *
  * @see Getopt
  */
public class LongOpt extends Object
{

/**************************************************************************/

/*
 * Class Variables
 */

/**
  * Constant value used for the "has_arg" constructor argument.  This
  * value indicates that the option takes no argument.
  */
public static final int NO_ARGUMENT = 0;

/** 
  * Constant value used for the "has_arg" constructor argument.  This
  * value indicates that the option takes an argument that is required.
  */
public static final int REQUIRED_ARGUMENT = 1;

/**
  * Constant value used for the "has_arg" constructor argument.  This
  * value indicates that the option takes an argument that is optional.
  */
public static final int OPTIONAL_ARGUMENT = 2;

/**************************************************************************/

/*
 * Instance Variables
 */

/**
  * The name of the long option
  */
protected String name;

/**
  * Indicates whether the option has no argument, a required argument, or
  * an optional argument.
  */
protected int has_arg;

/**
  * If this variable is not null, then the value stored in "val" is stored
  * here when this long option is encountered.  If this is null, the value
  * stored in "val" is treated as the name of an equivalent short option.
  */
protected StringBuffer flag;

/**
  * The value to store in "flag" if flag is not null, otherwise the
  * equivalent short option character for this long option.
  */
protected int val;

/**
  * Localized strings for error messages
  */
private ResourceBundle _messages = PropertyResourceBundle.getBundle(
                            "gnu/getopt/MessagesBundle", Locale.getDefault());

/**************************************************************************/

/*
 * Constructors
 */

/**
  * Create a new LongOpt object with the given parameter values.  If the
  * value passed as has_arg is not valid, then an exception is thrown.
  *
  * @param name The long option String.
  * @param has_arg Indicates whether the option has no argument (NO_ARGUMENT), a required argument (REQUIRED_ARGUMENT) or an optional argument (OPTIONAL_ARGUMENT).
  * @param flag If non-null, this is a location to store the value of "val" when this option is encountered, otherwise "val" is treated as the equivalent short option character.
  * @param val The value to return for this long option, or the equivalent single letter option to emulate if flag is null.
  * 
  * @exception IllegalArgumentException If the has_arg param is not one of NO_ARGUMENT, REQUIRED_ARGUMENT or OPTIONAL_ARGUMENT.
  */
public
LongOpt(String name, int has_arg, 
        StringBuffer flag, int val) throws IllegalArgumentException
{
  // Validate has_arg
  if ((has_arg != NO_ARGUMENT) && (has_arg != REQUIRED_ARGUMENT) 
     && (has_arg != OPTIONAL_ARGUMENT))
    {
      Object[] msgArgs = { new Integer(has_arg).toString() };
      throw new IllegalArgumentException(MessageFormat.format(
                    _messages.getString("getopt.invalidValue"), msgArgs));
    }

  // Store off values
  this.name = name;
  this.has_arg = has_arg;
  this.flag = flag;
  this.val = val;
}

/**************************************************************************/

/**
  * Returns the name of this LongOpt as a String
  *
  * @return Then name of the long option
  */
public String
getName()
{
  return(name);
}

/**************************************************************************/

/**
  * Returns the value set for the 'has_arg' field for this long option
  *
  * @return The value of 'has_arg'
  */
public int
getHasArg()
{
  return(has_arg);
}

/**************************************************************************/

/**
  * Returns the value of the 'flag' field for this long option
  *
  * @return The value of 'flag'
  */
public StringBuffer
getFlag()
{
  return(flag);
}

/**
  * Returns the value of the 'val' field for this long option
  *
  * @return The value of 'val'
  */
public int
getVal()
{
  return(val);
}

/**************************************************************************/

} // Class LongOpt

