package soot.dava.toolkits.base.finders;

import java.util.*;
import soot.toolkits.graph.*;

class SwitchNodeGraph implements DirectedGraph
{
    private LinkedList body, heads, tails;
    private HashMap binding;
    

    public SwitchNodeGraph( List body)
    {
	this.body = new LinkedList();
	this.body.addAll( body);

	binding = new HashMap();

	heads = new LinkedList();
	tails = new LinkedList();

	Iterator it = body.iterator();
	while (it.hasNext()) {
	    SwitchNode sn = (SwitchNode) it.next();

	    binding.put( sn.get_AugStmt().bsuccs.get(0), sn);
	    sn.reset();
	}
	
	it = body.iterator();
	while (it.hasNext())
	    ((SwitchNode) it.next()).setup_Graph( binding);

	it = body.iterator();
	while (it.hasNext()) {
	    SwitchNode sn = (SwitchNode) it.next();

	    if (sn.get_Preds().isEmpty())
		heads.add( sn);

	    if (sn.get_Succs().isEmpty())
		tails.add( sn);
	}
    }

    public int size()
    {
	return body.size();
    }

    public List getHeads()
    {
	return heads;
    }

    public List getTails()
    {
	return tails;
    }

    public List getPredsOf( Object o)
    {
	return ((SwitchNode) o).get_Preds();
    }

    public List getSuccsOf( Object o)
    {
	return ((SwitchNode) o).get_Succs();
    }

    public Iterator iterator()
    {
	return body.iterator();
    }

    public List getBody()
    {
	return body;
    }
}
