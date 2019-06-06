package soot.asm.backend.targets;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

public class LogicalOperations {
	private int i1;
	private boolean b1;
	private long l1;
	
	private int i2;
	private boolean b2;
	private long l2;
	
	public void doAnd(){
		i1 = i2 & i1;
		l1 = l2 & l1;
		b1 = b2 & b1;
	}
	
	public void doOr(){
		i1 = i2 | i1;
		l1 = l2 | l1;
		b1 = b2 | b1;
	}
	
	public void doXOr(){
		i1 = i2 ^ i1;
		l1 = l2 ^ l1;
		b1 = b2 ^ b1;
	}
	
	public void doInv(){
		i1 = ~i2;
		l1 = ~i2;
	}
	
	public void doShl(){
		i1 = i1 << i2;
		l1 = l1 << l2;
	}
	
	public void doShr(){
		i1 = i1 >> i2;
		l1 = l1 >> l2;
	}
	
	public void doUShr(){
		i1 = i1 >>> i2;
		l1 = l1 >>> l2;
	}

}
