package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jennifer Lhotak
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLAttributesPrinter {
  private static final Logger logger = LoggerFactory.getLogger(XMLAttributesPrinter.class);

  private String useFilename;
  private String outputDir;

  private FileOutputStream streamOut = null;
  private PrintWriter writerOut = null;

  private void setOutputDir(String dir) {
    outputDir = dir;
  }

  private String getOutputDir() {
    return outputDir;
  }

  public XMLAttributesPrinter(String filename, String outputDir) {
    setInFilename(filename);
    setOutputDir(outputDir);
    initAttributesDir();
    createUseFilename();
  }

  private void initFile() {
    try {
      streamOut = new FileOutputStream(getUseFilename());
      writerOut = new PrintWriter(new OutputStreamWriter(streamOut));
      writerOut.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
      writerOut.println("<attributes>");
    } catch (IOException e1) {
      logger.debug(e1.getMessage());
    }
  }

  private void finishFile() {
    writerOut.println("</attributes>");
    writerOut.close();
  }

  public void printAttrs(SootClass c, soot.xml.TagCollector tc) {
    printAttrs(c, tc, false);
  }

  public void printAttrs(SootClass c) {
    printAttrs(c, new soot.xml.TagCollector(), true);
  }

  private void printAttrs(SootClass c, soot.xml.TagCollector tc, boolean includeBodyTags) {
    tc.collectKeyTags(c);
    tc.collectTags(c, includeBodyTags);

    // If there are no attributes, then the attribute file is not created.
    if (tc.isEmpty()) {
      return;
    }
    initFile();
    tc.printTags(writerOut);
    tc.printKeys(writerOut);
    finishFile();
  }

  private void initAttributesDir() {
    final String attrDir = "attributes";

    File dir = new File(getOutputDir() + File.separatorChar + attrDir);
    if (!dir.exists()) {
      try {
        dir.mkdirs();
      } catch (SecurityException se) {
        logger.debug("Unable to create " + attrDir);
        // System.exit(0);
      }
    }
  }

  private void createUseFilename() {
    String tmp = getInFilename();
    // logger.debug("attribute file name: "+tmp);
    tmp = tmp.substring(0, tmp.lastIndexOf('.'));
    int slash = tmp.lastIndexOf(File.separatorChar);
    if (slash != -1) {
      tmp = tmp.substring(slash + 1, tmp.length());
    }

    final String attrDir = "attributes";
    StringBuilder sb = new StringBuilder();
    sb.append(getOutputDir());
    sb.append(File.separatorChar);
    sb.append(attrDir);
    sb.append(File.separatorChar);
    sb.append(tmp);
    sb.append(".xml");
    // tmp = sb.toString()+tmp+".xml";
    setUseFilename(sb.toString());
  }

  private void setInFilename(String file) {
    useFilename = file;
  }

  private String getInFilename() {
    return useFilename;
  }

  private void setUseFilename(String file) {
    useFilename = file;
  }

  private String getUseFilename() {
    return useFilename;
  }
}
