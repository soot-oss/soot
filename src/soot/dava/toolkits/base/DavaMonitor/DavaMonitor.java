package soot.dava.toolkits.base.DavaMonitor;

import java.util.HashMap;
import java.util.LinkedList;

public class DavaMonitor
{
    private static DavaMonitor instance;
    private static HashMap ref, lockTable;
    private static LinkedList q;
    private DavaMonitor() {}

    static
    {
	instance = new DavaMonitor();
	ref = new HashMap( 1, 0.7f);
	lockTable = new HashMap( 1, 0.7f);
	q = new LinkedList();
    }

    public static DavaMonitor v() { return instance; }

    public synchronized void enter( Object o) throws NullPointerException
    {
	Thread currentThread = Thread.currentThread();

	if (o == null)
	    throw new NullPointerException();

	Lock lock = (Lock) ref.get( o);

	if (lock == null) {
	    lock = new Lock();
	    ref.put( o, lock);
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
	
	lockTable.put( currentThread, lock);
	lock.enQ( currentThread);
	
	while ((lock.level > 0) || (lock.nextThread() != currentThread)) {
	    try {
		wait();
	    }
	    catch (InterruptedException e) {
		e.printStackTrace();
		System.exit(0);
	    }
	    
	    currentThread = Thread.currentThread();
	    lock = (Lock) lockTable.get( currentThread);
	}
	
	lock.deQ( currentThread);
	
	lock.level = 1;
	lock.owner = currentThread;
    }

    public synchronized void exit( Object o) throws NullPointerException, IllegalMonitorStateException
    {
	if (o == null)
	    throw new NullPointerException();

	Lock lock = (Lock) ref.get( o);

	if ((lock == null) || (lock.level == 0) || (lock.owner != Thread.currentThread()))
	    throw new IllegalMonitorStateException();
	
	lock.level--;
	
	if (lock.level == 0)
	    notifyAll();
    }
}
