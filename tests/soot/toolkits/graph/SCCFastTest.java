package soot.toolkits.graph;

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
