/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Sable Research Group
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.util;
import java.util.ArrayList;
import soot.*;


public class CommandLine {

	/**
	 *  A copy of the original command line
	 */
	protected String[] args;

	/**
	 *  A list of the options (Instances of the class Option)
	 */	
	protected ArrayList options = new ArrayList();

	/**
	 *  A list of the non-options (Instances of the class String)
	 */
	protected ArrayList nonOptions = new ArrayList();

	private String lastChecked = "";

	protected class Option {
		String name;
		ArrayList values = new ArrayList();
		boolean lastValueChecked = false;
		int waterMark = -1;

		public Option(String name) {
			for (int i=0; i<name.length(); i++) {
				char c = name.charAt(i);
				if ( (!Character.isLetterOrDigit(c)) && c != '-' ) {
					throw new CompilationDeathException(Main.COMPILATION_ABORTED, 
														"Invalid option name: "
														+ name);
				}
			}
			this.name = name;
		}

		public void addValue(Object value) {
			values.add(value);
		}
		
		public boolean equals(Object other) {
			if (other instanceof Option) {
				if (name.equals(((Option)other).name))
					return true;
			}
			return false;
		}
		
		public String toString() {
			StringBuffer buf = new StringBuffer(name + ": ");
			for (int i=0; i < values.size() ; i++)
				buf.append("(" + (String)values.get(i) + ") , "); 
			return buf.substring(0, buf.length()-2);
		}
	}

    public CommandLine(String[] args) {
		this.args = args;
		
		Option pending = null;
		for (int i=0; i<args.length; i++) {
		
			// if the previous option may have an argument
			if (pending != null) {
				// if the current current is an option, notify that the previous
				// option is used without any argument
				if (args[i].startsWith("-") && args[i].length() > 1) {
					pending.addValue("");
					pending = null;
				} 
				// else give the current non option as the potential value
				// of the previous option
				else {
					pending.addValue(args[i]);
					pending = null;
					continue;
				}
			}

			// the argument marks the end of options
			if (args[i].equals("--")) {
				for (i = i + 1; i<args.length; i++) {
					nonOptions.add(args[i]);
				}
				continue;
			}


			// the argument is a long option
			if (args[i].startsWith("--")) {

				pending = getOptionEntry(args[i].substring(2));

// 				This piece of code is the main part of what I have done
// 				when I decided to replace --long-option=value with 
// 				--long-option value
//
// 				// check if a value is specified
// 				String oName;
// 				String oValue;
// 				int j = args[i].indexOf("=");
// 				if ( j == -1 ) {
// 					oName = args[i].substring(2);
// 					oValue = "";
// 				} else {
// 					oName = args[i].substring(2, j);
// 					oValue = args[i].substring(j+1);
// 				}
// 				getOptionEntry(oName).addValue(oValue);
				
				continue;
			}

			// the argument is a short option
			if (args[i].startsWith("-") && args[i].length() > 1) {			
				if (args[i].length() == 2) {
					pending = getOptionEntry(args[i].substring(1));
				} else {
					for ( int j=1; j < args[i].length() ; j++ ) {
						getOptionEntry(args[i].substring(j, j+1)).addValue("");
					}
				}
				continue;
			}
		
			// the argument is not an option
			nonOptions.add(args[i]);
		
		}

		if (pending != null) {
			pending.addValue("");
		}

    }

	/* only for the constructor */
	/* it adds a new option if it doesn't find one */
	private Option getOptionEntry(String name) {
		Option o = new Option(name);
		if (options.contains(o)) {
			return (Option)options.get(options.indexOf(o));
		}
		options.add(o);
		return o;
	}


    /**
       Check if some options are still not checked, and if so,
       considers these options as unknown and returns an error
    */
    public String[] completeOptionsCheck() {
		
		java.util.Iterator optIt = options.iterator();
		while (optIt.hasNext()) {
			Option o = (Option)optIt.next();
			// option never => unknown
			if (o.waterMark == -1) {
				throw new CompilationDeathException(Main.COMPILATION_ABORTED,
													"Unknown option: " 
													+ o.name);
			}
			// verify if contains has been called enough
			for (int i = o.waterMark+1; i < o.values.size(); i++) {
				System.err.println("Option " + o.name + " is not always checked.\n"
								   + "Bug in command-line parsing");
			}
		}
		return null;
	}


    public boolean contains(String option) {
		int index = options.indexOf(new Option(option));
		if (index >= 0) {
			Option o = (Option)options.get(index);
			// if the value of the previous option was not queried
			// we interpret it as a non-option argument
			if (o.waterMark >=0 && !o.lastValueChecked) {
				if (!((String)o.values.get(o.waterMark)).equals(""))
					nonOptions.add(o.values.set(o.waterMark, ""));
			}
			o.waterMark++;
			if (o.waterMark < o.values.size()) {
				this.lastChecked = option;
				return true;
			}
		}
		return false;
    }


    public int getLength() {
		return args.length;
    }

    public java.util.List getNonOptionArguments() {
		return nonOptions;
    }


	public String getValue() {
		return getValueOf(this.lastChecked);
	}


    public String getValueOf(String option) {

		int index = options.indexOf(new Option(option));
		Option o = null;
		
		if (index >= 0) {
			o = (Option)options.get(index);
			if (o.waterMark == -1 || o.waterMark >= o.values.size()) 
				index = -1;
		}

		if ( index == -1 ) {
			System.err.println("CommandLine usage: You must first check "
							   + "that an option is present before asking "
							   + "for its value");
			return null;
		}
		o.lastValueChecked = true;

		return (String)o.values.get(o.waterMark);
	}


	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i < options.size() ; i++)
			buf.append("    " + ((Option)options.get(i)).toString() + "\n"); 
	
		return "Options:\n" 
			+ buf.toString()
			+ "Non-options:\n"
			+nonOptions.toString();

		
	}

}
