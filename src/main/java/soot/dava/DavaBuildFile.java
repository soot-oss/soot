package soot.dava;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2006 Nomair A. Naeem
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

/*
 * TODO: Jalopy would be awesome here!!
 */
public class DavaBuildFile {
  public static void generate(PrintWriter out, ArrayList<String> decompiledClasses) {
    out.print("<project default=\"compile\" name=\"Build file for decompiled code\">\n");
    out.print("	<description>\n");
    out.print("  This is the build file produced by Dava for the decompiled code.\n");
    out.print("  New features like (formatting using jalopy etc) will be added to this build file\n");
    out.print("</description>\n");
    out.print("<!-- properties for project directories -->\n");
    out.print("<property name=\"srcDir\" location=\"src\"/>\n");
    out.print("<property name=\"classesDir\" location=\"classes\"/>\n");
    out.print("");
    out.print("");
    out.print("");
    out.print("");
    out.print("");
    out.print("");
    out.print("");
    /*
     * out.print("<target name=\"init\" description=\"Create necessary directories\">\n"); out.print("<tstamp/>\n");
     * out.print("		<!-- set the timestamps -->\n"); out.print("		<mkdir dir=\"${classesDir}\"/>\n");
     * out.print("		<mkdir dir=\"${docDir}\"/>\n"); // out.print("		<mkdir dir=\"${libDir}\"/>\n");
     * out.print("</target>\n");
     */
    out.print("	<!--  ========== Compile Target ================= -->\n");
    out.print("	<target name=\"compile\" description=\"Compile .java files\">\n");
    out.print("	<javac srcdir=\"${srcDir}\" destdir=\"${classesDir}\">\n");
    out.print("	  <classpath>\n");
    out.print("		 <pathelement location=\"${junitJar}\"/>\n");
    out.print("	  </classpath>\n");
    out.print("	 </javac>\n");
    out.print("	</target>\n");

    out.print("	<!--  ==========AST METRICS FOR DECOMPILED CODE================= -->\n");
    out.print("<target name=\"ast-metrics\" description=\"Compute the ast metrics\">\n");
    /*
     * NEED TO MAKE SURE SRC-PREC IS SET so that java to jimple gets evaluate The command is going to be java soot.Main
     * -ast-metrics followed by all the classes on which we had originally done the decompile Need a specialized task
     */

    out.print("   <exec executable=\"java\" dir=\"src\">\n");
    out.print("		<arg value=\"-Xmx400m\" />\n");
    out.print("		<arg value=\"soot.Main\" />\n");
    out.print("		<arg value=\"-ast-metrics\" />\n");
    out.print("		<arg value=\"--src-prec\" />\n");
    out.print("		<arg value=\"java\" />\n");

    Iterator<String> it = decompiledClasses.iterator();
    while (it.hasNext()) {
      String temp = it.next();
      if (temp.endsWith(".java")) {
        temp = temp.substring(0, temp.length() - 5);
      }
      // System.out.println(temp);
      out.print("		<arg value=\"" + temp + "\" />\n");

    }

    out.print("");
    out.print("	  </exec>\n");
    out.print("	</target>\n");
    out.print("");
    out.print("");
    out.print("");
    out.print("");
    out.print("");
    out.print("");
    out.print("");
    out.print("</project>");

  }
}
