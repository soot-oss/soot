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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import soot.CompilationDeathException;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ProcessData {
	
	//when printing class names what is the max size of each class name
	private static final int CLASSNAMESIZE=15;
	
	private static final int CLASS =0;
	private static final int BENCHMARK =1;
	
	private static String metricListFileName=null;
	
	
	private static ArrayList xmlFileList = new ArrayList();

	
	private static int aggregationMechanism =-1;
	
	
	private static OutputStream streamOut;
	private static PrintWriter bench;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
			metricListFileName(args);
			System.out.println("A list of metrics will be stored in: "+metricListFileName);
			
			readXMLFileNames(2,args);
			
           	try{
        		OutputStream streamOut = new FileOutputStream(metricListFileName);
        		PrintWriter writerOut = new PrintWriter(new OutputStreamWriter(streamOut));
        		writeMetricLists(writerOut);
        		
        		writerOut.flush();
        		streamOut.close();
        	} catch (IOException e) {
        		throw new CompilationDeathException("Cannot output file " + metricListFileName);
        	}

		}
		else if (args[0].equals("-tables")){
			metricListFileName(args);
			System.out.println("Will read column table headings from: "+metricListFileName);
			
			//read aggregation option
			aggregationOption(args);
			if(aggregationMechanism == ProcessData.BENCHMARK){
				System.out.println("Aggregating over benchmarks...each row is one of the xml files");
				System.out.println("Only one tex file with the name"+metricListFileName+".tex will be created");
			}
			else if (aggregationMechanism == ProcessData.CLASS){
				System.out.println("Aggregating over class...each row is one class...");
				System.out.println("Each benchmark (xml file) will have its own tex file");
			}
			
			readXMLFileNames(3,args);
	
			//TODO: hello
			generateMetricsTables();
		}
		else{
			System.out.println("Incorrect argument number 1: expecting -metricList or -tables");
			System.exit(1);
		}
		
	}

	
	
	/*
	 * check if there is a 3rd argument and it should be either -class or -benchmark 
	 */
	private static void aggregationOption(String[] args){
		if(args.length < 3){
			System.out.println("Expecting -class or -benchmark at argument number 3");
			System.exit(1);
		}
		if(args[2].equals("-class")){
			aggregationMechanism = ProcessData.CLASS;
		}
		else if(args[2].equals("-benchmark")){
			aggregationMechanism = ProcessData.BENCHMARK;
		}
		else{
			System.out.println("Expecting -class or -benchmark at argument number 3");
			System.exit(1);
		}
	}
	
	/*
	 * Check if there is a 3rd argument if not complain 
	 * @param startIndex is the first args element where we expect to have an xml file or a *
	 */
	private static void readXMLFileNames(int startIndex, String args[]){
		if(args.length<startIndex+1){
			System.out.println("Expecting an xml file OR * symbol as argument number"+(startIndex+1));
			System.exit(1);
		}
		
		//check if its a *
		if(args[startIndex].equals("*")){
			System.out.println("Will read all xml files from directory");
			
			//READ DIRECTORY STRUCTURE
			readStar();
		}
		else{
			for(int i=startIndex;i<args.length;i++){
				String temp = args[i];
				if(!temp.endsWith(".xml")){
					//System.out.println("Argument number "+(startIndex+1) + ": '" + temp+"' is not a valid xml file");
					//System.exit(1);
				}
				else{
					xmlFileList.add(temp);
				}				
			}
		}
		
		Iterator it = xmlFileList.iterator();
		while(it.hasNext()){
			System.out.println("Will be reading: "+(String)it.next());
		}
	}
	
	
	
	/*
	 * Check if args1 exists and if so store the metric List FileName
	 */
	private static void metricListFileName(String[] args){
		if(args.length < 2 ){
			System.out.println("Expecting name of metricList as argumnet number 2");
			System.exit(1);
		}
		metricListFileName = args[1];

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
	
	
	/*
	 * Read all the xml files from current directory into the xmlFileList arraylist
	 */
	private static void readStar(){
		String curDir = System.getProperty("user.dir");
		System.out.println("Current system directory is"+curDir);
		File dir = new File(curDir);
		    
		String[] children = dir.list();
		if (children == null) {
			// Either dir does not exist or is not a directory
		} else {
	
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".xml");
				}
			};
			children = dir.list(filter);
			
			for(int i=0;i<children.length;i++)
				xmlFileList.add(children[i]);
		}
	}
	
	
	
	private static void writeMetricLists(PrintWriter out){
		ArrayList metricList = new ArrayList();
		
		Iterator it = xmlFileList.iterator();
		while(it.hasNext()){
			String fileName = (String)it.next();
			try {
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
				Document doc = docBuilder.parse (new File(fileName));
				System.out.println("Retrieving Metric List from xml file: "+fileName);
				// normalize text representation
				doc.getDocumentElement ().normalize ();

				NodeList metrics = doc.getElementsByTagName("Metric");

				for(int s=0; s<metrics.getLength() ; s++){
					Node metricNode = metrics.item(s);
					if(metricNode.getNodeType() == Node.ELEMENT_NODE){

						Element metricElement = (Element)metricNode;
						NodeList metricName = metricElement.getElementsByTagName("MetricName");
						Element name = (Element)metricName.item(0);

						NodeList textFNList = name.getChildNodes();
						//System.out.println("MetricName: " +    ((Node)textFNList.item(0)).getNodeValue().trim());
						if(!metricList.contains(((Node)textFNList.item(0)).getNodeValue().trim()))
							metricList.add(((Node)textFNList.item(0)).getNodeValue().trim());

					}//end of if clause
				}//end of for loop with s var
			}catch (SAXParseException err) {
				System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
				System.out.println(" " + err.getMessage ());
			}
			catch (SAXException e) {
				Exception x = e.getException ();
				((x == null) ? e : x).printStackTrace ();
			}
			catch (Throwable t) {
				t.printStackTrace ();
			}
		}
		
		it = metricList.iterator();
		while(it.hasNext()){
			out.println(it.next());
		}
		System.out.println(metricListFileName+ " created.");
	}
	
	
		
	private static void generateMetricsTables(){
		
		Vector columns = new Vector(); 
		
		/*
		 * create the columns which are the metriclist
		 */
		try{
			FileReader file = new FileReader(metricListFileName);
			BufferedReader fileInput = new BufferedReader(file);
			String text;
					
			//System.out.println("Columns");
			while( (text = fileInput.readLine()) != null){
				//System.out.print(text+"\t");
				columns.add(text);
			}
			fileInput.close();
		}
		catch(Exception e){
			System.out.println("Exception while reading from metricList"+metricListFileName);
			System.exit(1);
		}
		
		
		
		
		
		
		
		String newClassName="";
		if(aggregationMechanism == ProcessData.BENCHMARK){
			//TODO: create a metricListfileName.xml with metric info
		
			newClassName = metricListFileName+".tex";
	
			System.out.println("Creating tex file"+newClassName+" from metrics info");

			bench = openWriteFile(newClassName);

			/*
			 * For benchmarks we want to print xml files dealing with same benchmark in one table
			 * hence
			 * fft-enabled.xml fft-disabled.xml should be in one table where as
			 * matrix-enabled.xml matrix-disabled.xml should be in another table
			 */
			HashMap benchMarkToFiles = new HashMap();
			Iterator it = xmlFileList.iterator();
			while(it.hasNext()){
				String fileName = (String)it.next();
				if(fileName.indexOf('-') <0){
					System.out.println("XML files should have following syntax:\n <BENCHMARKNAME>-<PROPERTY>.xml\n PROPERTY should be enabled disabled etc");
					return;
				}
				String benchmark = fileName.substring(0,fileName.indexOf('-'));
				Object temp = benchMarkToFiles.get(benchmark);
				List tempList = null;
				if(temp == null){
					tempList = new ArrayList();
				}
				else{
					tempList = (ArrayList)temp;
				}
				tempList.add(fileName);
				benchMarkToFiles.put(benchmark,tempList);
			}
			
			
			Iterator keys = benchMarkToFiles.keySet().iterator();
			while(keys.hasNext()){
				//each key gets its own table
				String key = (String)keys.next();
				
				printTexTableHeader(bench,key,columns);				
				
				//go through each value which is an xml file
				Object tempValue = benchMarkToFiles.get(key);
				if(tempValue == null)
					continue;
				List files = (List)tempValue;
				Iterator fileIt = files.iterator();
				while(fileIt.hasNext()){
					String fileName = (String)fileIt.next();

					try{
						DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

						DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
						Document doc = docBuilder.parse (new File(fileName));
						System.out.println("Gethering metric info from from xml file: "+fileName);
						// normalize text representation
		
			
						//print the name of the xml file as the name of the benchmark
						if(fileName.endsWith(".xml"))
							bench.print(fileName.substring(0,fileName.length()-4));
						else
							bench.print(fileName);
			
			
						HashMap aggregatedValues = new HashMap();
						
						Iterator tempIt = columns.iterator();
						while(tempIt.hasNext()){
							aggregatedValues.put(tempIt.next(),new Integer(0));
						}
					
						aggregateXMLFileMetrics(doc,aggregatedValues);
			
						//at this point the hashmap contains aggregatedValue of all columns
						tempIt = columns.iterator();
						while(tempIt.hasNext()){
							Object temp = aggregatedValues.get(tempIt.next());
							if(temp instanceof Integer){
								int val = ((Integer)temp).intValue();
								bench.print("&"+val);
							}
							else if(temp instanceof Double){
								double val = ((Double)temp).doubleValue();
								bench.print("&"+val);
							}
							else
								throw new RuntimeException("Unknown type of object stored!!!");
							if(tempIt.hasNext())
								bench.print("   ");
							else
								bench.println("\\\\");
						}
					}catch (SAXParseException err) {
						System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
						System.out.println(" " + err.getMessage ());
					}
					catch (SAXException e) {
						Exception x = e.getException ();
						((x == null) ? e : x).printStackTrace ();
					}
					catch (Throwable t) {
						t.printStackTrace ();
					}
				}//done with all files for this benchmark
				
				//print closing for the table for this benchmark
				printTexTableFooter(bench,"");
			}//done with all benchmarks
			closeWriteFile(bench,newClassName);


		}
		else{		
		
		
			Iterator it = xmlFileList.iterator();
			while(it.hasNext()){
				String fileName = (String)it.next();

				try{
					DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

					DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
					Document doc = docBuilder.parse (new File(fileName));
					System.out.println("Gethering metric info from from xml file: "+fileName);
					// normalize text representation
					doc.getDocumentElement ().normalize ();

					
					if (aggregationMechanism == ProcessData.CLASS){
						getClassMetrics(fileName,doc,columns);
					}
					else{
						System.out.println("Unknown aggregation Mechanism");
						System.exit(1);
					}
				}catch (SAXParseException err) {
					System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
					System.out.println(" " + err.getMessage ());
				}
				catch (SAXException e) {
					Exception x = e.getException ();
					((x == null) ? e : x).printStackTrace ();
				}
				catch (Throwable t) {
					t.printStackTrace ();
				}
			}
		}		
	}

	
	private static PrintWriter openWriteFile(String fileName){
		PrintWriter writerOut;
       	try{
    		streamOut = new FileOutputStream(fileName);
    		writerOut = new PrintWriter(new OutputStreamWriter(streamOut));
    	} catch (IOException e) {
    		throw new CompilationDeathException("Cannot output file " + fileName);
    	}
    	return writerOut;
	}
	
	
	private static void closeWriteFile(PrintWriter writerOut,String fileName){
		try{
			writerOut.flush();
			streamOut.close();
		} catch (IOException e) {
			throw new CompilationDeathException("Cannot output file " + fileName);
		}
	}
	
	
	
	
	/*
	 * This method is called for each xml class sent as input.
	 * 
	 * Should read all of its metrics and add them to the aggregated values
	 */
	private static void aggregateXMLFileMetrics(Document doc, HashMap aggregated){
		
		NodeList metrics = doc.getElementsByTagName("Metric");

		for(int s=0; s<metrics.getLength() ; s++){
			Node metricNode = metrics.item(s);
			if(metricNode.getNodeType() == Node.ELEMENT_NODE){

				Element metricElement = (Element)metricNode;
				NodeList metricName = metricElement.getElementsByTagName("MetricName");
				Element name = (Element)metricName.item(0);

				NodeList textFNList = name.getChildNodes();
				String tempName = ((Node)textFNList.item(0)).getNodeValue().trim();
					
				Object tempObj  = aggregated.get(tempName);
				if( tempObj == null){
					//not found in hashmap so we dont care about this metric
					continue;
				}
				
				//We get to this point only if the metric is important
				
				NodeList value = metricElement.getElementsByTagName("Value");
				Element name1 = (Element)value.item(0);
				
				NodeList textFNList1 = name1.getChildNodes();
				String valToPrint = ((Node)textFNList1.item(0)).getNodeValue().trim();

				boolean notInt = false;
				try{
					int temp = Integer.parseInt(valToPrint);
					if(tempObj instanceof Integer){
						Integer valSoFar = (Integer)tempObj;				
						aggregated.put(tempName, new Integer(valSoFar.intValue()+temp) );
					}
					else if(tempObj instanceof Double){
						Double valSoFar = (Double)tempObj;
						aggregated.put(tempName, new Double(valSoFar.doubleValue() + temp));
					}
					else
						throw new RuntimeException("\n\nobject type not found");
				}
				catch(Exception e){
					//temp was not an int
					notInt = true;
				}
				
				if(notInt){
					//probably a double
					try{
						double temp = Double.parseDouble(valToPrint);
						if(tempObj instanceof Integer){
							Integer valSoFar = (Integer)tempObj;				
							aggregated.put(tempName, new Double(valSoFar.intValue()+temp) );
						}
						else if(tempObj instanceof Double){
							Double valSoFar = (Double)tempObj;
							aggregated.put(tempName, new Double(valSoFar.doubleValue() + temp));
						}
						else
							throw new RuntimeException("\n\nobject type not found");								
					}	
					catch(Exception e){
						throw new RuntimeException("\n\n not an integer not a double unhandled!!!!");
					}
				}
			}//end of if metricNode is an element_Node	
		}//end of for loop with s var
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static void getClassMetrics(String fileName, Document doc,Vector columns){
		//create a tex file with name (fileName - xml + tex)
		String newClassName = fileName;
		if(newClassName.endsWith(".xml"))
			newClassName = newClassName.substring(0,newClassName.length()-4);

		newClassName += ".tex";
		
		System.out.println("Creating tex file"+newClassName+" from metrics info in file"+fileName);
		
		PrintWriter writerOut = openWriteFile(newClassName);
    	printTexTableHeader(writerOut,"Classes",columns);
    	
    	
    	
    	
    	
    	/*
    	 * In order to print all class info alphabetically
    	 * we will create a map of className to data to be displayed
    	 */
    	ArrayList classNames = new ArrayList();
    	HashMap classData = new HashMap();
		
    	//each row is a class the name is obtained from the tag Class
		NodeList classes = doc.getElementsByTagName("Class");
		
		for(int cl=0;cl<classes.getLength();cl++){
			//going through each class node
			Node classNode = classes.item(cl);
			if(classNode.getNodeType() == Node.ELEMENT_NODE){
			
				Element classElement = (Element)classNode;
				
				/*
				 * Class Name
				 */
				NodeList classNameNodeList = classElement.getElementsByTagName("ClassName");
				Element classNameElement = (Element)classNameNodeList.item(0);

				NodeList classNameTextFNList = classNameElement.getChildNodes();
				String className = ((Node)classNameTextFNList.item(0)).getNodeValue().trim();
				
				//writerOut.println("");
		
				className = className.replace('_','-');
				if(className.length()>CLASSNAMESIZE){
					//writerOut.print(className.substring(0,CLASSNAMESIZE)+"   ");
					className = className.substring(0,CLASSNAMESIZE);
					classNames.add(className);
				}
				else{
					//writerOut.print(className+"   ");
					classNames.add(className);
				}
				
				System.out.print("\nclassName "+className);							
				
	
				
				String data = "   ";

				
				/*
				 * Metrics
				 */
				NodeList metrics = classElement.getElementsByTagName("Metric");

		    	int columnIndex=0; //which one we are printing right now
		    	
				for(int s=0; s<metrics.getLength() && columnIndex < columns.size(); s++){
					Node metricNode = metrics.item(s);
					if(metricNode.getNodeType() == Node.ELEMENT_NODE){

						Element metricElement = (Element)metricNode;
						NodeList metricName = metricElement.getElementsByTagName("MetricName");
						Element name = (Element)metricName.item(0);

						NodeList textFNList = name.getChildNodes();
						String tempName = ((Node)textFNList.item(0)).getNodeValue().trim();
					
						/*
						 * If the name of this metric is not the next column name in the columns
						 * simply skip over it and continue
						 */
						if(! tempName.equals(columns.elementAt(columnIndex))){
							//System.out.println("here");	
							continue;
						}

						
					
						//We get to this point only if the metric name is the same as the column name
					
						NodeList value = metricElement.getElementsByTagName("Value");
						Element name1 = (Element)value.item(0);

						NodeList textFNList1 = name1.getChildNodes();
						String valToPrint = ((Node)textFNList1.item(0)).getNodeValue().trim();
						System.out.print(" " + valToPrint);
						//writerOut.print("&"+valToPrint);
						data += "&"+valToPrint;
						
						
						
						columnIndex++;
						if(columns.size()>columnIndex){
							//writerOut.print("   ");
							data += "   ";
						}
						else{
							//writerOut.println("\\\\");
							data += "\\\\";
						}
						
					}//end of if metricNode is an element_Node	
				}//end of for loop with s var
				
				
				
				
				
				
				classData.put(className,data);
					
				
				
			}//end of if classNode is an element_Node
		}//end of for loop with cl

		//writerOut.println();
		//writerOut.println();
		//writerOut.println();
		
		
		Collections.sort(classNames);
		
		Iterator tempIt = classNames.iterator();
		while(tempIt.hasNext()){
			String className = (String)tempIt.next();
			String data = (String)classData.get(className);
			writerOut.print(className);
			writerOut.println(data);
		}
		printTexTableFooter(writerOut,fileName);
	
		closeWriteFile(writerOut,metricListFileName);
	}

	
	private static void printTexTableFooter(PrintWriter out, String tableCaption){
		out.println("");
		out.println("\\hline");
		out.println("\\end{tabular}");
		out.println("\\caption{ ..."+tableCaption+"..... }");
		out.println("\\end{table}");
		//out.println("\\end{figure}");
	}
	
	
	
	private static void printTexTableHeader(PrintWriter out, String rowHeading, Vector columns){
		//out.println("\\begin{figure}[hbtp]");
		out.println("\\begin{table}[hbtp]");
		out.print("\\begin{tabular}{");

		for(int i =0;i<=columns.size();i++)
			out.print("|l");
		
		out.println("|}");
		out.println("\\hline");
		
		out.print(rowHeading+"   ");
		
		Iterator it = columns.iterator();
		while(it.hasNext()){
			out.print("&"+(String)it.next());
			if(it.hasNext())
				out.print("   ");
		}
		out.println("\\\\");
		
		out.println("\\hline");
	}
}
