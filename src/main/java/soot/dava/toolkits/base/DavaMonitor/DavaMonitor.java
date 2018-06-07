package soot.dava.toolkits.base.DavaMonitor;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
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

import java.util.HashMap;

public class DavaMonitor {
  private static final DavaMonitor instance = new DavaMonitor();

  private final HashMap<Object, Lock> ref, lockTable;

  private DavaMonitor() {
    ref = new HashMap<Object, Lock>(1, 0.7f);
    lockTable = new HashMap<Object, Lock>(1, 0.7f);
  }

  public static DavaMonitor v() {
    return instance;
  }

  public synchronized void enter(Object o) throws NullPointerException {
    Thread currentThread = Thread.currentThread();

    if (o == null) {
      throw new NullPointerException();
    }

    Lock lock = ref.get(o);

    if (lock == null) {
      lock = new Lock();
      ref.put(o, lock);
    }

    if (lock.level == 0) {
      lock.level = 1;
      lock.owner = currentThread;
      return;
    }

    if (lock.owner == currentThread) {
      lock.level++;
      return;
    }

    lockTable.put(currentThread, lock);
    lock.enQ(currentThread);

    while ((lock.level > 0) || (lock.nextThread() != currentThread)) {
      try {
        wait();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      currentThread = Thread.currentThread();
      lock = lockTable.get(currentThread);
    }

    lock.deQ(currentThread);

    lock.level = 1;
    lock.owner = currentThread;
  }

  public synchronized void exit(Object o) throws NullPointerException, IllegalMonitorStateException {
    if (o == null) {
      throw new NullPointerException();
    }

    Lock lock = ref.get(o);

    if ((lock == null) || (lock.level == 0) || (lock.owner != Thread.currentThread())) {
      throw new IllegalMonitorStateException();
    }

    lock.level--;

    if (lock.level == 0) {
      notifyAll();
    }
  }
}
