package soot.toolkits.graph;

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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class SCCFastTest {

	@Test
	public void testSCC1() {
		Node rootNode = new Node(0);
		Node left0 = new Node(1);
		Node left1 = new Node(2);
		Graph g = new Graph(rootNode);
		rootNode.addkid(left0);
		rootNode.addkid(left1);
		StronglyConnectedComponentsFast<Node> scc = new StronglyConnectedComponentsFast<Node>(g);
		Assert.assertTrue(scc.getTrueComponents().isEmpty());
	}

	@Test
	public void testSCC2() {
		Node rootNode = new Node(0);
		Node left0 = new Node(1);
		Node left1 = new Node(2);
		Node left0a = new Node(3);
		Node left0b = new Node(4);
		Graph g = new Graph(rootNode);
		rootNode.addkid(left0);
		rootNode.addkid(left1);
		left0.addkid(left0a);
		left0a.addkid(left0b);
		left0b.addkid(left0);
		StronglyConnectedComponentsFast<Node> scc = new StronglyConnectedComponentsFast<Node>(g);
		Assert.assertEquals(1, scc.getTrueComponents().size());
		
		List<Node> nodes = scc.getTrueComponents().get(0);
		Assert.assertEquals(3, nodes.size());
	}
	
}
