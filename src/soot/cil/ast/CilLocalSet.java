package soot.cil.ast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Class representing a set of locals in CIL code
 * 
 * @author Steven Arzt
 *
 */
public class CilLocalSet implements Iterable<CilLocal> {
	
	private Set<CilLocal> locals = new HashSet<CilLocal>();
	private Map<String, CilLocal> nameToLocal = new HashMap<String, CilLocal>();
	private Map<Integer, CilLocal> idToLocal = new HashMap<Integer, CilLocal>();
	
	public CilLocalSet() {
		
	}
	
	public void add(CilLocal local) {
		locals.add(local);
		nameToLocal.put(local.getName(), local);
		idToLocal.put(local.getID(), local);
	}
	
	public Set<CilLocal> getLocals() {
		return this.locals;
	}
	
	public CilLocal getLocalByName(String name) {
		return nameToLocal.get(name);
	}
	
	public CilLocal getLocalByID(int id) {
		return idToLocal.get(id);
	}

	@Override
	public Iterator<CilLocal> iterator() {
		return locals.iterator();
	}

}
