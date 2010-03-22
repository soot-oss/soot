/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999-2010 Hossein Sadat-Mohtasham
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
package soot.toolkits.graph.pdg;


import java.util.List;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.UnitGraph;

/**
 * This interface represents a region of control dependence in the control flow graph.
 * There are different kinds of region representations that may slightly differ in 
 * definition or implementation; here is an interface that is expected to be supported
 * by all these different regions.
 * 
 * 
 * @author Hossein Sadat-Mohtasham
 * Jan 2009
 */

public interface IRegion {
	
	public SootMethod getSootMethod();
	public SootClass getSootClass();	
	public UnitGraph getUnitGraph();
	public List<Unit> getUnits();
	public List<Unit> getUnits(Unit from, Unit to);
	public List<Block> getBlocks();
	public Unit getLast();
	public Unit getFirst();
	public int getID();
	public boolean occursBefore(Unit u1, Unit u2);
	public void setParent(IRegion pr);
	public IRegion getParent();
	public void addChildRegion(IRegion chr);
	public List<IRegion> getChildRegions();

}