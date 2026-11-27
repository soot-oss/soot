package soot.dotnet.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Marc Miltenberger
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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Runs stack-intensive work on a thread with a large stack. Parsing deeply nested CIL protobuf messages recurses once
 * per nesting level, which can exhaust the stack of the thread that drives the frontend.
 */
public final class LargeStackRunner {

  /**
   * Stack size for the worker thread. A stack is only reserved, pages are committed when used, so this costs address
   * space rather than memory. The value covers roughly 8 000 nesting levels at the observed cost of ~4 KiB per level,
   * which is far beyond the ~2 000 levels the C# compiler accepts for a single expression.
   */
  private static final long STACK_SIZE = 32L * 1024 * 1024;

  private static final Object LOCK = new Object();
  private static final BlockingQueue<Runnable> QUEUE = new LinkedBlockingQueue<>();
  private static volatile Thread worker;

  private LargeStackRunner() {
  }

  /**
   * Runs the given task on the shared large-stack thread and returns its result.
   */
  public static <T> T call(Callable<T> task) throws Exception {
    if (Thread.currentThread() == worker) {
      // Nested call, the worker is already running our caller; executing inline avoids a deadlock.
      return task.call();
    }
    FutureTask<T> future = new FutureTask<>(task);
    worker();
    QUEUE.add(future);
    try {
      return future.get();
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof Exception) {
        throw (Exception) cause;
      }
      throw (Error) cause;
    }
  }

  private static void worker() {
    synchronized (LOCK) {
      if (worker == null) {
        worker = new Thread(null, LargeStackRunner::runLoop, "Soot .NET worker", STACK_SIZE);
        worker.setDaemon(true);
        worker.start();
      }
    }
  }

  /** Runs queued tasks until the thread is interrupted. */
  private static void runLoop() {
    while (true) {
      try {
        Runnable task = QUEUE.take();
        task.run();
      } catch (InterruptedException e) {
        return;
      }
    }
  }
}
