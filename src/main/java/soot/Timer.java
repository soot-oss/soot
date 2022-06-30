package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import static java.lang.System.gc;
import static java.lang.System.nanoTime;

import soot.options.Options;

/**
 * Utility class providing a timer. Used for profiling various phases of Sootification.
 */
public class Timer {
  private long duration;
  private long startTime;
  private boolean hasStarted;

  private String name;

  /** Creates a new timer with the given name. */
  public Timer(String name) {
    this.name = name;
    duration = 0;
  }

  /** Creates a new timer. */
  public Timer() {
    this("unnamed");
  }

  static void doGarbageCollecting() {
    final G g = G.v();
    // Subtract garbage collection time
    if (g.Timer_isGarbageCollecting) {
      return;
    }

    if (!Options.v().subtract_gc()) {
      return;
    }

    // garbage collects only every 4 calls to avoid round off errors
    if ((g.Timer_count++ % 4) != 0) {
      return;
    }

    g.Timer_isGarbageCollecting = true;
    g.Timer_forcedGarbageCollectionTimer.start();

    // Stop all outstanding timers
    for (Timer t : g.Timer_outstandingTimers) {
      t.end();
    }

    gc();

    // Start all outstanding timers
    for (Timer t : g.Timer_outstandingTimers) {
      t.start();
    }

    g.Timer_forcedGarbageCollectionTimer.end();
    g.Timer_isGarbageCollecting = false;

  }

  /** Starts the given timer. */
  public void start() {
    doGarbageCollecting();

    startTime = nanoTime();

    if (hasStarted) {
      throw new RuntimeException("timer " + name + " has already been started!");
    }

    hasStarted = true;

    if (!G.v().Timer_isGarbageCollecting) {
      synchronized (G.v().Timer_outstandingTimers) {
        G.v().Timer_outstandingTimers.add(this);
      }
    }
  }

  /** Returns the name of the current timer. */
  public String toString() {
    return name;
  }

  /** Stops the current timer. */
  public void end() {
    if (!hasStarted) {
      throw new RuntimeException("timer " + name + " has not been started!");
    }

    hasStarted = false;

    duration += nanoTime() - startTime;

    if (!G.v().Timer_isGarbageCollecting) {
      synchronized (G.v().Timer_outstandingTimers) {
        G.v().Timer_outstandingTimers.remove(this);
      }
    }
  }

  /** Returns the sum of the intervals start()-end() of the current timer. */
  public long getTime() {
    return duration / 1000000L;
  }
}
