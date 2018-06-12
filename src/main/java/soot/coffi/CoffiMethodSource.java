package soot.coffi;

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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.MethodSource;
import soot.PackManager;
import soot.PhaseOptions;
import soot.Scene;
import soot.SootMethod;
import soot.Timers;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.options.Options;

public class CoffiMethodSource implements MethodSource {
  private static final Logger logger = LoggerFactory.getLogger(CoffiMethodSource.class);
  public ClassFile coffiClass;
  public method_info coffiMethod;

  CoffiMethodSource(soot.coffi.ClassFile coffiClass, soot.coffi.method_info coffiMethod) {
    this.coffiClass = coffiClass;
    this.coffiMethod = coffiMethod;
  }

  public Body getBody(SootMethod m, String phaseName) {
    JimpleBody jb = Jimple.v().newBody(m);

    Map options = PhaseOptions.v().getPhaseOptions(phaseName);
    boolean useOriginalNames = PhaseOptions.getBoolean(options, "use-original-names");

    if (useOriginalNames) {
      soot.coffi.Util.v().setFaithfulNaming(true);
    }

    /*
     * I need to set these to null to free Coffi structures. fileBody.coffiClass = null; bafBody.coffiMethod = null;
     * 
     */
    if (Options.v().verbose()) {
      logger.debug("[" + m.getName() + "] Constructing JimpleBody from coffi...");
    }

    if (m.isAbstract() || m.isNative() || m.isPhantom()) {
      return jb;
    }

    if (Options.v().time()) {
      Timers.v().conversionTimer.start();
    }

    if (coffiMethod.instructions == null) {
      if (Options.v().verbose()) {
        logger.debug("[" + m.getName() + "]     Parsing Coffi instructions...");
      }

      coffiClass.parseMethod(coffiMethod);
    }

    if (coffiMethod.cfg == null) {
      if (Options.v().verbose()) {
        logger.debug("[" + m.getName() + "]     Building Coffi CFG...");
      }

      new soot.coffi.CFG(coffiMethod);

      // if just computing metrics, we don't need to actually return body
      if (soot.jbco.Main.metrics) {
        return null;
      }
    }

    if (Options.v().verbose()) {
      logger.debug("[" + m.getName() + "]     Producing naive Jimple...");
    }

    boolean oldPhantomValue = Scene.v().getPhantomRefs();

    Scene.v().setPhantomRefs(true);
    coffiMethod.cfg.jimplify(coffiClass.constant_pool, coffiClass.this_class, coffiClass.bootstrap_methods_attribute, jb);
    Scene.v().setPhantomRefs(oldPhantomValue);

    if (Options.v().time()) {
      Timers.v().conversionTimer.end();
    }

    coffiMethod.instructions = null;
    coffiMethod.cfg = null;
    coffiMethod.attributes = null;
    coffiMethod.code_attr = null;
    coffiMethod.jmethod = null;
    coffiMethod.instructionList = null;

    coffiMethod = null;
    coffiClass = null;

    PackManager.v().getPack("jb").apply(jb);
    return jb;
  }
}
