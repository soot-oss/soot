/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Fabien Deschodt
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


import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

/** Harness for calling javac/jikes to compile Soot. 
 * Replaces jams. */
public class SootCompiler
{
  private File srcDir = new File("src");
  private File classesDir = new File("classes");

  private ArrayList compilerArguments;

  private String compilerName = "javac";

  public static void main (String[] argv)
  {
    long startTime = System.currentTimeMillis ();

    SootCompiler compiler = new SootCompiler (argv);
    compiler.start ();
    System.out.println ("Total time: "
			+ formatTime (System.currentTimeMillis ()
				      - startTime));
  }

  public SootCompiler (String[] argv)
  {
    setupCompilerArguments (argv);
    setupSrcDir ();
    setupClassesDir ();
  }

  protected void start ()
  {
    // fetching java files
    System.out.print ("Finding java files... ");
    List javaFiles = findFiles (srcDir, "java");
    System.out.println ("(" + javaFiles.size () + " file"
			+ (javaFiles.size () > 1 ? "s" : "") + " found)");

    // compiling java files
    Iterator fileIt = javaFiles.iterator ();
    while (fileIt.hasNext ())
      {
	String filename = (String) fileIt.next ();
	if (needsUpdate (filename,
			 classesDir.toString()
			 + filename.substring (3, filename.length () - 4)
			 + "class"))
	  {
	      if (!compileFile (filename))
		  break;
	  }

      }

    // fetching dat files
    System.out.print ("Finding dat files ...");
    List datFiles = findFiles (srcDir, "dat");
    System.out.println ("(" + datFiles.size () + " file"
			+ (datFiles.size () > 1 ? "s" : "") + " found)");

    // copying dat files
    fileIt = datFiles.iterator ();
    while (fileIt.hasNext ())
      {
	String filename = (String) fileIt.next ();
	String target = classesDir.toString() + filename.substring (3);
	if (needsUpdate (filename, target))
	  {
	    copyFile (filename, target);
	  }
      }

    // fetching properties files
    System.out.print ("Finding properties files ...");
    List pFiles = findFiles (srcDir, "properties");
    System.out.println ("(" + pFiles.size () + " file"
			+ (pFiles.size () > 1 ? "s" : "") + " found)");

    // copying properties files
    fileIt = pFiles.iterator ();
    while (fileIt.hasNext ())
      {
	String filename = (String) fileIt.next ();
	String target = classesDir.toString() + filename.substring (3);
	if (needsUpdate (filename, target))
	  {
	    copyFile (filename, target);
	  }
      }
  }

  protected boolean compileFile (String filename)
  {
    System.out.print (compilerArguments.get(0));
    for (int i = 1; i < compilerArguments.size(); i++)
      System.out.print (" " + compilerArguments.get(i));
    System.out.println (" " + filename);

    try
    {
	// This is not very nice...
	String[] q = new String[compilerArguments.size()+1];
	compilerArguments.toArray(q);
	q[compilerArguments.size()] = filename;

	Process p = Runtime.getRuntime().exec(q);
	java.io.InputStream pOut = p.getInputStream();
        java.io.BufferedReader br = new java.io.BufferedReader
            (new java.io.InputStreamReader(pOut));

        String s = br.readLine();

        while (s != null)
        {
            System.out.println(s);
            s = br.readLine();
        }

	pOut = p.getErrorStream();
        br = new java.io.BufferedReader
            (new java.io.InputStreamReader(pOut));

        s = br.readLine();

        while (s != null)
        {
            System.out.println(s);
            s = br.readLine();
        }

	// do stuff about writing p's output here.

	int ok = p.waitFor();

        if (ok != 0)
	{
	  System.err.println ("An error occured while compiling file");
	  return false;
	}
    }
    catch (Exception e)
    {
	System.err.println ("Error occurred while calling the compile method:");
	e.printStackTrace();
	return false;
    }
    return true;
  }

  protected void copyFile (String inFile, String outFile)
  {
    System.out.println ("Copying " + inFile + " to " + outFile);

    try
    {
      java.io.FileInputStream in =
	new java.io.FileInputStream (new File (inFile));
      java.io.FileOutputStream out =
	new java.io.FileOutputStream (new File (outFile));

      byte[]buffer = new byte[8 * 1024];
      int count = 0;

      do
	{
	  out.write (buffer, 0, count);
	  count = in.read (buffer, 0, buffer.length);
	}
      while (count != -1);

      in.close ();
      out.close ();
    }
    catch (Exception e)
    {
      System.err.println ("Unable to perform copy:\n" + e.toString ());
    }
  }


  protected List findFiles (File dir, String extension)
  {
    List l = new ArrayList ();
    String[]files = dir.list ();

    for (int i = 0; i < files.length; i++)
      {
	File file = new File (dir, files[i]);
	if (file.isDirectory ())
	  {
	    l.addAll (findFiles (file, extension));
	  }
	if (file.isFile ())
	  {
	    if (file.getName ().endsWith ("." + extension))
	      {
		l.add (file.getPath ());
	      }
	  }
      }

    return l;
  }


  protected static String formatTime (long millis)
  {
    long seconds = millis / 1000;
    long minutes = seconds / 60;

    if (minutes > 0)
      {
	return Long.toString (minutes) + " minute"
	  + (minutes == 1 ? " " : "s ")
	  + Long.toString (seconds % 60) + " second"
	  + (seconds % 60 == 1 ? "" : "s");
      }
    else
      {
	return Long.toString (seconds) + " second"
	  + (seconds % 60 == 1 ? "" : "s");
      }
  }

  /**
   *  Checks if the target file corresponding to targetFileName needs to be
   *  updated
   */
  protected boolean needsUpdate (String srcFileName, String targetFileName)
  {
    File srcFile = new File (srcFileName);
    File targetFile = new File (targetFileName);

    if (targetFile.exists ()
	&& (targetFile.lastModified () - srcFile.lastModified ()) > 0)
      {
	return false;
      }

    return true;
  }

  protected void setupClassesDir ()
  {
    if (!classesDir.exists ())
      {
	System.out.println (" The \""+classesDir.toString()+"\" directory does not exist. "
			    + " Creating one ...");
	if (!classesDir.mkdirs ())
	  {
	    System.err.println (classesDir.getPath () + " cannot be created");
	    System.exit (1);
	  }
      }
    else
      {
	if (!classesDir.isDirectory ())
	  {
	    System.out.println (classesDir.getPath () +
				" is not a directory");
	    System.exit (1);
	  }
      }
  }

  protected void setupCompilerArguments (String[] argv)
  {
    compilerArguments = new ArrayList();
    String[] tmp = new String[argv.length]; int j = 0;

    int caIndex = 0;

    // parse cmd line arguments
    for (int i = 0; i < argv.length; i++)
	{
	    if (argv[i].equals("--"))
		{
		    // check that the last parameter works right...
		    while (++i < argv.length)
			tmp[j++] = argv[i];
		    break;
		}
	    else if (argv[i].equals("--src") || argv[i].equals("-s"))
		{
		    srcDir = new File(argv[++i]);
		}
	    else if (argv[i].equals("--classes") || argv[i].equals("-c") || 
		     argv[i].equals("-d"))
		{
		    classesDir = new File(argv[++i]);
		}
	    else if (argv[i].equals("--jikes"))
		{
		    compilerName = "jikes";
		}
            else if (argv[i].equals("--help"))
                {
                    System.out.println("usage: java SootCompiler [--src|-s <src-dir>] [--classes|-c <classes-dir>]\n"+
                                       "                [--jikes] [-- <java-compiler-options>]");
                    System.exit(1);
                }
	    else
		System.out.println("warning: unrecognized option "+argv[i]);
	}

    compilerArguments.add(compilerName);
    compilerArguments.add("-classpath");
    compilerArguments.add(srcDir.toString()+":"+System.getProperty ("java.class.path"));

    compilerArguments.add("-d");
    compilerArguments.add(classesDir.toString());

    int i = 0;
    while (i < j)
	compilerArguments.add(argv[i++]);
  }

  protected void setupSrcDir ()
  {
    if (!srcDir.exists ())
      {
	System.err.println (srcDir.getPath () + " does not exist");
	System.exit (1);
      }
    if (!srcDir.isDirectory ())
      {
	System.err.println (srcDir.getPath () + " is not a directory");
	System.exit (1);
      }
  }
}
