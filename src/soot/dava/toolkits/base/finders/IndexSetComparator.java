package soot.dava.toolkits.base.finders;

import java.util.*;

class IndexSetComparator implements Comparator
{
    public int compare( Object o1, Object o2)
    {
	if (o1 == o2)
	    return 0;

	o1 = ((TreeSet) o1).last();
	o2 = ((TreeSet) o2).last();
	
	if (o1 instanceof String)
	    return 1;

	if (o2 instanceof String)
	    return -1;

	return ((Integer) o1).intValue() - ((Integer) o2).intValue();
    }

    public boolean equals( Object o)
    {
	return (o instanceof IndexSetComparator);
    }
}

