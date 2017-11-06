package soot.toolkits.purity;

import org.junit.Ignore;

/**
 * This example is from the article "A Combined Pointer and Purity Analysis for
 * Java Programs" by Alexandru Salcianu and Martin Rinard.
 * It is supposed to demonstrate the purity analysis (-annot-purity)
 *
 * by Antoine Mine, 2005/02/08
 */

class List {

    Cell head = null;

    void add(Object e) {
	head = new Cell(e,head);
    }

    Iterator iterator() {
	return new ListItr(head);
    }
}

class Cell {
   
    Object data;
    Cell   next;

    Cell(Object d, Cell n) {
	data = d;
	next = n;
    }
}

interface Iterator {
    boolean hasNext();
    Object next();
}

class ListItr implements Iterator {

    Cell cell;

    ListItr(Cell head) {
	cell = head;
    }

    public boolean hasNext() {
	return cell != null;
    }

    public Object next() {
	Object result = cell.data;
	cell = cell.next;
	return result;
    }
}

class Point {
    
    float x,y;

    Point(float x,float y) {
	this.x = x;
	this.y = y;
    }

    void flip() {
	float t = x;
	x = y;
	y = t;
    }

    void print() {
	System.out.print("("+x+","+y+")");
    }
}

@Ignore("not a real test!")
public class PurityTest {

    static float sumX(List list) {
	float s = 0;
	Iterator it = list.iterator();
	while (it.hasNext()) {
	    Point p = (Point) it.next();
	    s += p.x;
	}
	return s;
    }

    static void flipAll(List list) {
	Iterator it = list.iterator();
	while (it.hasNext()) {
	    Point p = (Point) it.next();
	    p.flip();
	}
    }

    static void print(List list) {
	Iterator it = list.iterator();
	System.out.print("[");
	while (it.hasNext()) {
	    Point p = (Point) it.next();
	    p.print();
	    if (it.hasNext()) System.out.print(";");
	}
	System.out.println("]");
    }

    public static void main(String args[]) {
	List list = new List();
	list.add(new Point(1,2));
	list.add(new Point(2,3));
	list.add(new Point(3,4));
	print(list);
	System.out.println("sum="+sumX(list));
	sumX(list);
	print(list);
	flipAll(list);
	print(list);
    }
}
