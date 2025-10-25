package soot.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2025 Marc Miltenberger
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

/**
 * Contains some utility functions to enable an easy use of parallel methods
 * 
 * @author Marc Miltenberger
 */
public class ParallelUtils {

  private static final int TIMEOUT_HOURS = 24;

  /**
   * Is used to process elements in parallel
   * 
   * @param <T>
   *          the type of one element
   */
  public interface ElementProcessor<T> {
    /**
     * Processes a single element
     * 
     * @param e
     *          the element
     */
    public void process(T e);
  }

  public static int NUM_CORES = Math.max(1, Runtime.getRuntime().availableProcessors());

  /**
   * Minimum number of elements for running in parallel
   */
  private static final int MIN_ELEMENTS = NUM_CORES;

  /**
   * Runs elements of a given iterator in parallel, if it's more elements than <i>MIN_ELEMENTS</i>.
   * 
   * @param <T>
   *          the type
   * @param iterator
   *          the iterator
   * @param processor
   *          the processor to pass elements to
   */
  public static <T> void runIteratorParallel(Iterator<T> iterator, ElementProcessor<T> processor) {
    List<T> checkElements = new ArrayList<>(MIN_ELEMENTS);
    for (int i = 0; i < MIN_ELEMENTS; i++) {
      if (iterator.hasNext()) {
        checkElements.add(iterator.next());
      } else {
        break;
      }
    }
    // one more is sufficient
    if (iterator.hasNext()) {
      ExecutorService executionService = Executors.newFixedThreadPool(NUM_CORES);
      for (T e : checkElements) {
        executionService.execute(new Runnable() {

          @Override
          public void run() {
            processor.process(e);
          }
        });
      }
      while (iterator.hasNext()) {
        final T e = iterator.next();
        executionService.execute(new Runnable() {

          @Override
          public void run() {
            processor.process(e);
          }
        });
      }
      executionService.shutdown();
      try {
        if (!executionService.awaitTermination(TIMEOUT_HOURS, TimeUnit.HOURS)) {
          throw new RuntimeException(String.format("Timeout after %d hours", TIMEOUT_HOURS));
        }
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    } else {
      // Not worth the effort; do it in our thread.
      for (T e : checkElements) {
        processor.process(e);
      }
    }
  }

}
