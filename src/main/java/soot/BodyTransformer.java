package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Patrick Lam
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

import soot.options.Options;

import java.util.*;

/**
 * An abstract class which acts on a Body. This class provides a harness and acts as an interface for classes that wish to
 * transform a Body. Subclasses provide the actual Body transformation implementation.
 */
public abstract class BodyTransformer extends Transformer {

  private static final Map<String, String> enabledOnlyMap = Collections.singletonMap("enabled", "true");

  /**
   * Called by clients of the transformation. Acts as a generic interface for BodyTransformers. Calls internalTransform with
   * the optionsString properly set up. That is, the options in optionsString override those in the Scene.
   *
   * @param b
   *          the body on which to apply the transformation
   * @param phaseName
   *          phaseName for the transform. Used to retrieve options from the Scene.
   */
  public final void transform(Body b, String phaseName, Map<String, String> options) {
    Options.getInternallyAppliedBT().add(phaseName);
    if (Options.allBTList.contains(phaseName)) {
      if (Options.getFirstBodyTransformer() == null){
        Options.setFirstBodyTransformer(phaseName);
        Options.setInitialStmtCount(Options.getInitialStmtCount() + b.getUnits().size());
      }
      if (Options.getFirstBodyTransformer().equals(phaseName)){
        Options.setInitialStmtCount(Options.getInitialStmtCount() + b.getUnits().size());
      }
      if (Options.getEnableBTList() != null && Options.getEnableBTList().contains(phaseName)){
        internalTransformWrapper(b, phaseName, options);
      }
    } else {
      if (PhaseOptions.getBoolean(options, "enabled")) {
        internalTransform(b, phaseName, options);
      }
    }
  }

  public final void internalTransformWrapper(Body b, String phaseName, Map<String, String> options) {
    long startTime = System.currentTimeMillis(); // Start time
    final int MB = 1024 * 1024;
    Runtime runtime = Runtime.getRuntime();
    long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();

    internalTransform(b, phaseName, options);

    long endTime = System.currentTimeMillis(); // End time
    long duration = endTime - startTime;
    long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
    long memoryUsed = (usedMemoryAfter - usedMemoryBefore) / MB;

    Map<String, List<Long>> bodyTransformerMetric = Options.getBodyTransformerMetric();
    LinkedList<Long> defaultValues = new LinkedList<>();
    defaultValues.add(0L); // index 0 has runtime
    defaultValues.add(0L); // index 1 has memory usage
    bodyTransformerMetric.putIfAbsent(phaseName, defaultValues);
    bodyTransformerMetric.computeIfPresent(phaseName,(str,list)->{
      list.set(0, list.get(0) + duration);
      list.set(1, list.get(1) + memoryUsed);
      return list;
    });
  }

  public final void transform(Body b, String phaseName) {
    Options.getInternallyAppliedBT().add(phaseName);
    if (Options.allBTList.contains(phaseName)) {
      if (Options.getFirstBodyTransformer() == null){
        Options.setFirstBodyTransformer(phaseName);
        Options.setInitialStmtCount(Options.getInitialStmtCount() + b.getUnits().size());
      }
      if (Options.getFirstBodyTransformer().equals(phaseName)){
        Options.setInitialStmtCount(Options.getInitialStmtCount() + b.getUnits().size());
      }
      Options.setInitialStmtCount(Options.getInitialStmtCount() + b.getUnits().size());
      if (Options.getEnableBTList() != null && Options.getEnableBTList().contains(phaseName)) {
        internalTransformWrapper(b, phaseName, enabledOnlyMap);
      }
    } else {
      internalTransform(b, phaseName, enabledOnlyMap);
    }
  }

  public final void transform(Body b) {
    transform(b, "");
  }

  /**
   * This method is called to perform the transformation itself. It is declared abstract; subclasses must implement this
   * method by making it the entry point to their actual Body transformation.
   *
   * @param b
   *          the body on which to apply the transformation
   * @param phaseName
   *          the phasename for this transform; not typically used by implementations.
   * @param options
   *          the actual computed options; a combination of default options and Scene specified options.
   */
  protected abstract void internalTransform(Body b, String phaseName, Map<String, String> options);

}
