/*
 * Created on Jan 11, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg;

import org.eclipse.ui.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Display;
import org.eclipse.draw2d.*;
import org.eclipse.draw2d.graph.*;
import org.eclipse.draw2d.geometry.*;
import java.util.*;
import ca.mcgill.sable.soot.launching.*;
import soot.toolkits.graph.*;
import org.eclipse.swt.SWT;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CFGViewer {
	
	public void run(Object sootGraph){
		Shell shell = new Shell();
		shell.open();
		shell.setText("CFG Test");
		
		LightweightSystem lws = new LightweightSystem(shell);
		//FigureCanvas canvas = new FigureCanvas(shell, lws);
		
		//ScrollPane sp = new ScrollPane();
		//sp.setVerticalScrollBarVisibility(ScrollPane.NEVER);
		//sp.setHorizontalScrollBarVisibility(ScrollPane.AUTOMATIC);
		
		Panel p = new Panel();
		//FigureCanvas fg = new FigureCanvas(shell, lws);
		//fg.setHorizontalScrollBarVisibility(FigureCanvas.AUTOMATIC);
		//fg.setVerticalScrollBarVisibility(FigureCanvas.AUTOMATIC);
		//p.setBounds(new Rectangle(0,0,-1,-1));
		//lws.setContents(p);
		//IFigure label = new org.eclipse.draw2d.Label("CFG Tests");
		//canvas.add(label);
		//p.add(label);
		HashMap nodeMap = new HashMap();
		//DirectedGraph dg = makeSimpleGraph();
		org.eclipse.draw2d.graph.DirectedGraph dg = makeSootGraph((soot.toolkits.graph.DirectedGraph)sootGraph);
		Iterator nIt = dg.nodes.iterator();
		while (nIt.hasNext()){
			Node nextNode = (Node)nIt.next();
			IFigure node = new RectangleFigure();
			IFigure label = new Label((String)nextNode.data);
			label.setSize(nextNode.width, 36);
			node.add(label);
			int len = ((String)nextNode.data).length() * 5;
			//node.setBounds(new Rectangle(nextNode.x, nextNode.y, -1, -1));
			node.setLocation(new Point(nextNode.x, nextNode.y));
			node.setSize(nextNode.width, 36);
			System.out.println("bounds: "+node.getBounds());
			p.add(node);
			nodeMap.put(nextNode, node);
		}
		Iterator eIt = dg.edges.iterator();
		while (eIt.hasNext()){
			Edge nextEdge = (Edge)eIt.next();
			PolylineConnection edge = new PolylineConnection();
			ChopboxAnchor ca1 = new ChopboxAnchor((IFigure)nodeMap.get(nextEdge.source));
			ChopboxAnchor ca2 = new ChopboxAnchor((IFigure)nodeMap.get(nextEdge.target));
			
			edge.setSourceAnchor(ca1);
			edge.setTargetAnchor(ca2);
			edge.setTargetDecoration(new PolygonDecoration());
			p.add(edge);
		}
		//fg.setContents(p);
		
		
		lws.setContents(p);
		Display display = Display.getDefault();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ())
				display.sleep ();
		}
	}
	
	public org.eclipse.draw2d.graph.DirectedGraph makeSootGraph(soot.toolkits.graph.DirectedGraph sootGraph){
		org.eclipse.draw2d.graph.DirectedGraph dg = new org.eclipse.draw2d.graph.DirectedGraph();
		NodeList nodes = new NodeList();
		EdgeList edges = new EdgeList();
		HashMap nodeMap = new HashMap();
		
		Iterator it = sootGraph.iterator();
		while (it.hasNext()){
			Object node = it.next();
			System.out.println("node: "+node.toString());
			Node n;
			if (!nodeMap.containsKey(node)){
				n = new Node(node.toString());
				n.width = node.toString().length() * 7;
				nodes.add(n);
				nodeMap.put(node, n);
			}
			else {
				n = (Node)nodeMap.get(node);
			}
			Iterator succIt = sootGraph.getSuccsOf(node).iterator();
			while (succIt.hasNext()){
				Object succ = succIt.next();
				System.out.println("succ: "+succ.toString());
				Node s;
				if (!nodeMap.containsKey(succ)){
					s = new Node(succ.toString());
					s.width = s.toString().length() * 7;
					nodes.add(s);
					nodeMap.put(succ, s);
				}
				else {
					s = (Node)nodeMap.get(succ);
				}
				Edge e = new Edge(n, s);
				edges.add(e);
			}	
			//System.out.println("node: "+it.next().toString());
		}
		
		dg.nodes = nodes;
		dg.edges = edges;
		DirectedGraphLayout dgl = new DirectedGraphLayout();
		dgl.visit(dg);
		return dg;
	}
	
	public org.eclipse.draw2d.graph.DirectedGraph makeSimpleGraph(){
		NodeList nl = new NodeList();
		Node n1 = new Node();
		String data = "y = 3";
		n1.data = data;
		n1.width = data.length() * 7;
		nl.add(n1);
		Node n2 = new Node();
		data = "if i >= 10 goto L0";
		n2.data = data;
		n2.width = data.length() * 7;
		nl.add(n2);
		Node n3 = new Node();
		data = "if i != 0 goto L1";
		n3.data = data;
		n3.width = data.length() * 7;
		nl.add(n3);
		Node n4 = new Node();
		data  = "x = 5";
		n4.data = data;
		n4.width = data.length() * 7;
		nl.add(n4);
		EdgeList el = new EdgeList();
		Edge e1 = new Edge(n1, n2);
		el.add(e1);
		Edge e2 = new Edge(n2, n3);
		el.add(e2);
		Edge e3 = new Edge(n2, n4);
		el.add(e3);
		org.eclipse.draw2d.graph.DirectedGraph dg = new org.eclipse.draw2d.graph.DirectedGraph();
		dg.edges = el;
		dg.nodes = nl;
		DirectedGraphLayout dgl = new DirectedGraphLayout();
		dgl.visit(dg);
		return dg;
	}
	
	public void selectionChanged(IAction action, ISelection selection){
		
	}
	
	public void dispose(){
	
	}
	
	public void init(IWorkbenchWindow window){
	
	}
	
	/**
	 * 
	 */
	public CFGViewer() {
		super();
		// TODO Auto-generated constructor stub
	}

}
