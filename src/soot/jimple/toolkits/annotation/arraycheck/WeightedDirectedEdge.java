package soot.jimple.toolkits.annotation.arraycheck;

class WeightedDirectedEdge {
    Object from, to;
    int weight;
    public WeightedDirectedEdge(Object from, Object to, int weight)
    {
	this.from = from;
	this.to = to;
	this.weight = weight;
    }

    public int hashCode()
    {
	return from.hashCode()+to.hashCode()+weight;
    }

    public boolean equals(Object other)
    {
	if (other instanceof WeightedDirectedEdge)
	{
	    WeightedDirectedEdge another = (WeightedDirectedEdge)other;
	    return ( (this.from == another.from)
		   &&(this.to == another.to)
		   &&(this.weight==another.weight) );
	}
	return false;
    }
    
    public String toString()
    {
	return from+"->"+to+"="+weight;
    }
}
