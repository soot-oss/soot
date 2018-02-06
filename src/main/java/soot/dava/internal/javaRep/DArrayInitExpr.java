package soot.dava.internal.javaRep;

import java.util.ArrayList;
import java.util.List;

import soot.Type;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.util.Switch;
/*
 * TODO: Starting with a 1D array in mind will try to refactor for multi D arrays
 * later
 */
public class DArrayInitExpr implements Value {
	// an array of elements for the initialization
	ValueBox[] elements;
	
	//store the type of the array
	Type type;
	
	public DArrayInitExpr (ValueBox[] elements,Type type){
		this.elements = elements;
		this.type=type;
	}
	
	/*
	 * go through the elements array
	 * return useBoxes of each value plus the valuebox itself
	 */
	public List<ValueBox> getUseBoxes() {
        List<ValueBox> list = new ArrayList<ValueBox>();

        for (ValueBox element : elements) {
        	list.addAll(element.getValue().getUseBoxes());
        	list.add(element);
        }
        return list;
	}

	/*
	 * TODO: Does not work
	 */
	public Object clone() {
		return this;
	}
	
	public Type getType() {
		return type;
	}

	public void toString(UnitPrinter up) {
		up.literal("{");
		for(int i=0;i<elements.length;i++){
			elements[i].toString(up);
			if(i+1<elements.length)
				up.literal(" , ");
		}
		up.literal("}");
	}

	
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("{");
		for(int i=0;i<elements.length;i++){
			b.append(elements[i].toString());
			if(i+1<elements.length)
				b.append(" , ");
		}
		b.append("}");
		return b.toString();
	}

	public void apply(Switch sw) {
		// TODO Auto-generated method stub

	}

	public boolean equivTo(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	public int equivHashCode() {
		int toReturn =0;
		for (ValueBox element : elements)
			toReturn += element.getValue().equivHashCode();
		
		return toReturn;
	}
	
	public ValueBox[] getElements() {
		return elements;
	}
	

}
