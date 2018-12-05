package soot.jimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.MethodSource;
import soot.PackManager;
import soot.SootMethod;
import soot.jimple.parser.JimpleAST;
import soot.options.Options;

public class JimpleMethodSource implements MethodSource {
  private static final Logger logger = LoggerFactory.getLogger(JimpleMethodSource.class);
  JimpleAST mJimpleAST;

  public JimpleMethodSource(JimpleAST aJimpleAST) {
    mJimpleAST = aJimpleAST;
  }

  public Body getBody(SootMethod m, String phaseName) {
    JimpleBody jb = (JimpleBody) mJimpleAST.getBody(m);
    if (jb == null) {
      throw new RuntimeException("Could not load body for method " + m.getSignature());
    }

    if (Options.v().verbose()) {
      logger.debug("[" + m.getName() + "] Retrieving JimpleBody from AST...");
    }

    PackManager.v().getPack("jb").apply(jb);
    return jb;
  }
}
