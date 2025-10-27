package soot.util;

import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

  private static final int NUM_CORES = Runtime.getRuntime().availableProcessors();
  private static final long KEEP_ALIVE_SECONDS = 60;

  private static final Executor executionService
      = new ThreadPoolExecutor(NUM_CORES, NUM_CORES, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, new LinkedBlockingQueue<>()) {
        {
          allowCoreThreadTimeOut(true);
        }
      };

  /**
   * Runs elements of a given iterator in parallel. This implementation can cope with elements being added to the iterator
   * while the processor is running. This is useful for e.g. reachable methods, where processing a method can introduce new
   * reachable methods. The method only terminates when the iterator claims to have no new elements <i>after</i> all elements
   * in the iterator have been processed.
   * 
   * @param <T>
   *          the type
   * @param iterator
   *          the iterator
   * @param processor
   *          the processor to pass elements to
   */
  public static <T> void runIteratorParallelUntilEnd(Iterator<T> iterator, ElementProcessor<T> processor) {
    try {
      AtomicInteger running = new AtomicInteger();
      while (true) {
        while (iterator.hasNext()) {
          final T e = iterator.next();
          running.incrementAndGet();
          executionService.execute(new Runnable() {

            @Override
            public void run() {
              try {
                processor.process(e);
              } finally {
                running.decrementAndGet();
              }
            }
          });
        }
        if (running.get() != 0) {
          Thread.sleep(5);
        } else {
          break;
        }
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
