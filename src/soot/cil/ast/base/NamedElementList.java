package soot.cil.ast.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A list of named elements
 * 
 * @author Steven Arzt
 *
 */
public class NamedElementList<N extends INamedElement> implements Iterable<N> {
	
	private final List<N> elements;
	private final Map<String, N> nameToElements = new HashMap<String, N>();
	
	public NamedElementList() {
		this.elements = new ArrayList<N>();
	}
	
	public NamedElementList(List<N> elements) {
		this.elements = elements;
		for (N param : elements)
			this.nameToElements.put(param.getName(), param);
	}
	
	public void add(N param) {
		this.elements.add(param);
		this.nameToElements.put(param.getName(), param);
	}
	
	public List<N> getAllElements() {
		return this.elements;
	}
	
	public N getElementByName(String name) {
		return this.nameToElements.get(name);
	}
	
	public int size() {
		return this.elements.size();
	}

	@Override
	public Iterator<N> iterator() {
		return this.elements.iterator();
	}

}
