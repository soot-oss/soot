package soot.dava.toolkits.base.DavaMonitor;

import java.util.LinkedList;

class Lock
{
    public Thread owner;
    public int level;

    private LinkedList q;

    Lock()
    {
	level = 0;
	owner = null;
	q = new LinkedList();
    }

    public Thread nextThread()
    {
	return (Thread) q.getFirst();
    }

    public Thread deQ( Thread t) throws IllegalMonitorStateException
    {
	if (t != q.getFirst())
	    throw new IllegalMonitorStateException();

	return (Thread) q.removeFirst();
    }

    public void enQ( Thread t)
    {
	q.add( t);
    }
}
