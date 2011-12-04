/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
 * Copyright (C) 2007 Manu Sridharan
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package soot.toolkits.scalar;

import soot.PointsToAnalysis;
import soot.SootMethod;

/** Just a pair of arbitrary objects.
 * 
 * @author Ondrej Lhotak
 * @author Manu Sridharan (genericized it)
 * @author xiao, extend it with more functions
 */
public class Pair<T, U>
{
	public Pair() { o1 = null; o2 = null; }
    public Pair( T o1, U o2 ) { this.o1 = o1; this.o2 = o2; }
    public int hashCode() {
        return o1.hashCode() + o2.hashCode();
    }
    public boolean equals( Object other ) {
        if( other instanceof Pair) {
            Pair p = (Pair) other;
            return o1.equals( p.o1 ) && o2.equals( p.o2 );
        } else return false;
    }

	/**
	 * Decide if this pair represents a method parameter.
     */
	public boolean isParameter()
	{
		if ( o1 instanceof SootMethod && o2 instanceof Integer ) return true;
		return false;
	}
	
	/**
	 * Decide if this pair stores the THIS parameter for a method.
	 */
	public boolean isThisParameter()
	{
		return (o1 instanceof SootMethod &&
				o2.equals(PointsToAnalysis.THIS_NODE)) ? true : false;
	}

    public String toString() {
        return "Pair "+o1+","+o2;
    }
    public T getO1() { return o1; }
    public U getO2() { return o2; }
	public void setO1( T no1 ) { o1 = no1; }
	public void setO2( U no2 ) { o2 = no2; }
	public void setPair( T no1, U no2 ) { o1 = no1; o2 = no2; }
    protected T o1;
    protected U o2;
}