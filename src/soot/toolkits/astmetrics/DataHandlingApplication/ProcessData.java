/* Soot - a J*va Optimization Framework
 * Copyright (C) 2006 Nomair A. Naeem
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.toolkits.astmetrics.DataHandlingApplication;

public class ProcessData {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("I will process your xml files and make pretty tex docs");
		int argLength =args.length;
		if(argLength ==0){
			printIntro();
			useHelp();
			System.exit(1);
		}
		
		if(args[0].equals("--help")){
			printHelp();
			System.exit(1);
		}
		else if(args[0].equals("-metricList")){
			
		}
		else if (args[0].equals("-tables")){
			
		}
		else{
			System.out.println("Incorrect argument at arg index 0");
			System.exit(1);
		}
		
	}

	public static void printHelp(){
		printIntro();
		System.out.println("There are two main modes of execution");
		System.out.println("To execute the program the first argument should be one of these modes");
		System.out.println("-metricList and -tables");
		System.out.println("\n\n The -metricList mode");
		System.out.println("The argument at location 1 should be name of a file where the list of metrics will be stored");
		System.out.println("All arguments following argument 1 have to be xml files to be processed");
		System.out.println("If argument at location 2 is * then the current directory is searched and all xml files will be processed");
		
		System.out.println("\n\n The -tables mode");
		System.out.println("The argument at location 1 should be name of a file where the list of metrics are stored");
		System.out.println("These metrics will become the COLUMNS in the tables created");
		System.out.println("Argument at location 2 is the choice of aggregation");
		System.out.println("\t -class for class level metrics");
		System.out.println("\t -benchmark for benchmark level metrics");
		System.out.println("Each xml file is considered to be a benchmark with a bunch of classes in it");
		
		System.out.println("All arguments following argument 2 have to be xml files to be processed");
		System.out.println("If argument at location 3 is * then the current directory is searched and all xml files will be processed");
	}
	
	
	public static void printIntro(){
		System.out.println("Welcome to the processData application");
		System.out.println("The application is an xml document parser.");
		System.out.println("Its primary aim is to create pretty tex tables");	
	}
	
	public static void useHelp(){
		System.out.println("Use the --help flag for more details");
	}
}
