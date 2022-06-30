package soot.dava;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
 * Copyright (C) 2004 - 2005 Nomair A. Naeem
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

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.CompilationDeathException;
import soot.G;
import soot.Local;
import soot.Singletons;
import soot.SootMethod;
import soot.Type;
import soot.jimple.Jimple;
import soot.util.IterableSet;

public class Dava {
  private static final Logger logger = LoggerFactory.getLogger(Dava.class);

  public Dava(Singletons.Global g) {
  }

  public static Dava v() {
    return G.v().soot_dava_Dava();
  }

  private static final String LOG_TO_FILE = null;
  private static final PrintStream LOG_TO_SCREEN = null;

  private Writer iOut = null;
  private IterableSet currentPackageContext = null;
  private String currentPackage;

  public void set_CurrentPackage(String cp) {
    currentPackage = cp;
  }

  public String get_CurrentPackage() {
    return currentPackage;
  }

  public void set_CurrentPackageContext(IterableSet cpc) {
    currentPackageContext = cpc;
  }

  public IterableSet get_CurrentPackageContext() {
    return currentPackageContext;
  }

  public DavaBody newBody(SootMethod m) {
    return new DavaBody(m);
  }

  /** Returns a DavaBody constructed from the given body b. */
  public DavaBody newBody(Body b) {
    return new DavaBody(b);
  }

  public Local newLocal(String name, Type t) {
    return Jimple.v().newLocal(name, t);
  }

  public void log(String s) {
    if (LOG_TO_SCREEN != null) {
      LOG_TO_SCREEN.println(s);
      LOG_TO_SCREEN.flush();
    }

    if (LOG_TO_FILE != null) {
      if (iOut == null) {
        try {
          iOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(LOG_TO_FILE), "US-ASCII"));
        } catch (FileNotFoundException fnfe) {
          logger.debug("" + "Unable to open " + LOG_TO_FILE);
          logger.error(fnfe.getMessage(), fnfe);
          throw new CompilationDeathException(CompilationDeathException.COMPILATION_ABORTED);
        } catch (UnsupportedEncodingException uee) {
          logger.debug("" + "This system doesn't support US-ASCII encoding!!");
          logger.error(uee.getMessage(), uee);
          throw new CompilationDeathException(CompilationDeathException.COMPILATION_ABORTED);
        }
      }

      try {
        iOut.write(s);
        iOut.write("\n");
        iOut.flush();
      } catch (IOException ioe) {
        logger.debug("" + "Unable to write to " + LOG_TO_FILE);
        logger.error(ioe.getMessage(), ioe);
        throw new CompilationDeathException(CompilationDeathException.COMPILATION_ABORTED);
      }
    }
  }
}
