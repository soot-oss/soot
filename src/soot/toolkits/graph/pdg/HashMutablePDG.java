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


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;

import soot.Body;
import soot.SootClass;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.DominatorNode;
import soot.toolkits.graph.DominatorTree;
import soot.toolkits.graph.HashMutableEdgeLabelledDirectedGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.pdg.IRegion;


/**
 *
 * This class implements a Program Dependence Graph as defined in 
 * 
 * Ferrante, J., Ottenstein, K. J., and Warren, J. D. 1987. 
 * The program dependence graph and its use in optimization. 
 * ACM Trans. Program. Lang. Syst. 9, 3 (Jul. 1987), 319-349. 
 * DOI= http://doi.acm.org/10.1145/24039.24041
 * 
 * Note: the implementation is not exactly as in the above paper. It first finds
 * the regions of control dependence then uses part of the algorithm given in
 * the above paper to build the graph.
 * 
 * The constructor accepts a UnitGraph, which can be a BriefUnitGraph, an ExceptionalUnitGraph,
 * or an EnhancedUnitGraph. At the absence of exception handling constructs in
 * a method, all of these work the same. However, at the presence of exception handling
 * constructs, BriefUnitGraph is multi-headed and potentially multi-tailed which makes
 * the results of RegionAnalysis and PDG construction unreliable (It's not clear if
 * it would be useful anyway); Also, ExceptionalGraph's usefulness when exception handling
 * is present is not so clear since almost every unit can throw exception hence the 
 * dependency is affected. Currently, the PDG is based on a UnitGraph (BlockGraph) and does
 * not care whether flow is exceptional or not.
 * 
 * The nodes in a PDG are of type PDGNode and the edges can have three labels: "dependency",
 * "dependency-back", and "controlflow"; however, the "controlflow" edges are auxiliary
 * and the dependencies are represented by the labels beginning with "dependency". 
 * Other labels can be added later for application
 * or domain-specific cases.
 * 
 * 
 * To support methods that contain exception-handling and multiple-heads or tails, use EnhancedUnitGraph.
 * It does not represent exceptional flow in the way ExceptionalUnitGraph does, but it integrates 
 * them in a concise way. Also, it adds START/STOP nodes to graph if necessary to make the graph
 * single entry single exit.
 * 
 *
 * @author Hossein Sadat-Mohtasham
 * Sep 2009
 */

public class HashMutablePDG extends HashMutableEdgeLabelledDirectedGraph<PDGNode, String> implements ProgramDependenceGraph {
	
	protected Body m_body = null;
	protected SootClass m_class = null;
	protected UnitGraph m_cfg = null;
	protected BlockGraph m_blockCFG = null;
	protected Hashtable<Object, PDGNode> m_obj2pdgNode = new Hashtable<Object, PDGNode>();
	protected List<Region> m_weakRegions = null;
	protected List<Region> m_strongRegions = null;
	protected PDGNode m_startNode = null;
	protected List<PDGRegion> m_pdgRegions = null;
	private RegionAnalysis m_regionAnalysis = null;
	private int m_strongRegionStartID;
	
	public HashMutablePDG(UnitGraph cfg)
	{
		this.m_body = cfg.getBody();
		this.m_class = this.m_body.getMethod().getDeclaringClass();
		this.m_cfg = cfg;
				
		this.m_regionAnalysis = new RegionAnalysis(this.m_cfg, this.m_body.getMethod(), this.m_class);
		
		/*
		 * Get the weak regions and save a copy. Note that the strong regions list is
		 * initially cloned from the weak region to be later modified.
		 */
		this.m_strongRegions = this.m_regionAnalysis.getRegions();
		this.m_weakRegions = this.cloneRegions(this.m_strongRegions);
		this.m_blockCFG = this.m_regionAnalysis.getBlockCFG();
		
		//Construct the PDG
		this.constructPDG();
		this.m_pdgRegions = HashMutablePDG.computePDGRegions(this.m_startNode);

		
		/*This is needed to convert the initially Region-typed inner node of the PDG's head
			to a PDGRegion-typed one after the whole graph is computed.
			The root PDGRegion is the one with no parent.
		*/

		IRegion r = this.m_pdgRegions.get(0);
		while(r.getParent() != null)
			r = r.getParent();
		
		this.m_startNode.setNode(r);
		
	}

    public BlockGraph getBlockGraph(){
      return m_blockCFG;
    }
	
	/**
	 * This is the heart of the PDG contruction. It is huge and definitely needs 
	 * some refactorings, but since it's been evlovong to cover some boundary cases
	 * it has become hard to rafactor.
	 * 
	 * It uses the list of weak regions, along with the dominator and post-dominator
	 * trees to construct the PDG nodes.
	 */
	protected void constructPDG()
	{
		Hashtable<Block, Region> block2region = this.m_regionAnalysis.getBlock2RegionMap();
		DominatorTree<Block> pdom = this.m_regionAnalysis.getPostDominatorTree();
		DominatorTree<Block> dom = this.m_regionAnalysis.getDominatorTree();
				
		List<Region> regions2process = new LinkedList<Region>();
		Region topLevelRegion = this.m_regionAnalysis.getTopLevelRegion();
		m_strongRegionStartID = m_weakRegions.size();
		
		//This becomes the top-level region (or ENTRY region node)
		PDGNode pdgnode = new PDGNode(topLevelRegion, PDGNode.Type.REGION);
		this.addNode(pdgnode);
		this.m_obj2pdgNode.put(topLevelRegion, pdgnode);
		this.m_startNode = pdgnode;
		topLevelRegion.setParent(null);
		
        Set<Region> processedRegions = new HashSet<Region>();
		regions2process.add(topLevelRegion);
		
		//while there's a (weak) region to process
		while(!regions2process.isEmpty())
		{
			Region r = regions2process.remove(0);
            processedRegions.add(r);
			
			//get the corresponding pdgnode
			pdgnode = this.m_obj2pdgNode.get(r);
			
			/*
			 * For all the CFG nodes in the region, create the corresponding PDG node and edges, and
			 * process them if they are in the dependence set of other regions, i.e. other regions
			 * depend on them.
			 */
			List<Block> blocks = r.getBlocks();
			Hashtable<Region, List<Block>> toBeRemoved = new Hashtable<Region, List<Block>>();
			PDGNode prevPDGNodeInRegion = null;
			PDGNode curNodeInRegion = null;
			for(Iterator<Block> itr = blocks.iterator(); itr.hasNext();)
			{
				/*
				 * Add the PDG node corresponding to the CFG block node.
				 */
				Block a = itr.next();
				PDGNode pdgNodeOfA = null;
				if(!this.m_obj2pdgNode.containsKey(a))
				{
					pdgNodeOfA = new PDGNode(a, PDGNode.Type.CFGNODE);
					this.addNode(pdgNodeOfA);
					this.m_obj2pdgNode.put(a, pdgNodeOfA);
				}
				else
					pdgNodeOfA = this.m_obj2pdgNode.get(a);
								
				this.addEdge(pdgnode, pdgNodeOfA, "dependency");
				pdgnode.addDependent(pdgNodeOfA);
				//
				
				curNodeInRegion = pdgNodeOfA;
				
				/*
				 * For each successor B of A, if B does not post-dominate A, add all the
				 * nodes on the path from B to the L in the post-dominator tree, where L is the least
				 * common ancestor of A and B in the post-dominator tree (L will be either A itself or 
				 * the parent of A.).
				 */
				
				List<Block> bs = this.m_blockCFG.getSuccsOf(a);
				for(Iterator<Block> bItr = bs.iterator(); bItr.hasNext();)
				{
					List<Block> dependents = new ArrayList<Block>();
					
					Block b = bItr.next();
					
					if(b.equals(a))
						throw new RuntimeException("PDG construction: A and B are not supposed to be the same node!");
					
					
					DominatorNode<Block> aDode = pdom.getDode(a);
					DominatorNode<Block> bDode = pdom.getDode(b);
					
					//If B post-dominates A, go to the next successor.
					if(pdom.isDominatorOf(bDode, aDode))
						continue;
					
					//FIXME: what if the parent is null?!!
					DominatorNode<Block> aParentDode = aDode.getParent();
					
					DominatorNode<Block> dode = bDode;
					while(dode != aParentDode)
					{
						dependents.add((Block)dode.getGode());
						
						//This occurs if the CFG is multi-tailed and therefore the pdom is a forest.
						if(dode.getParent() == null)
							//throw new RuntimeException("parent dode in pdom is null: dode is " + aDode);
							break;
						dode = dode.getParent();						
					}
					
					/*
					 * If node A is in the dependent list of A, then A is the header of a loop.
					 * Otherwise, A could still be the header of a loop or just a simple predicate.
					 */
					
					//first make A's pdg node be a conditional (predicate) pdgnode, if it's not already.					
					if(pdgNodeOfA.getAttrib() != PDGNode.Attribute.CONDHEADER)
					{
						PDGNode oldA = pdgNodeOfA;
						pdgNodeOfA = new ConditionalPDGNode(pdgNodeOfA);
						this.replaceInGraph(pdgNodeOfA, oldA);
						pdgnode.removeDependent(oldA);
						this.m_obj2pdgNode.put(a, pdgNodeOfA);
						pdgnode.addDependent(pdgNodeOfA);
						pdgNodeOfA.setAttrib(PDGNode.Attribute.CONDHEADER);
						
						curNodeInRegion = pdgNodeOfA;
					}		
					
					List<Block> copyOfDependents = new ArrayList<Block>();
					copyOfDependents.addAll(dependents);
					
					//First, add the dependency for B and its corresponding region.
					Region regionOfB = block2region.get(b);
					PDGNode pdgnodeOfBRegion = null;
					if(!this.m_obj2pdgNode.containsKey(regionOfB))
					{
						pdgnodeOfBRegion = new PDGNode(regionOfB, PDGNode.Type.REGION);
						this.addNode(pdgnodeOfBRegion);
						this.m_obj2pdgNode.put(regionOfB, pdgnodeOfBRegion);
						
					}
					else
						pdgnodeOfBRegion = this.m_obj2pdgNode.get(regionOfB);
					
					//set the region hierarchy
					regionOfB.setParent(r);
					r.addChildRegion(regionOfB);
					
					//add the dependency edges
					this.addEdge(pdgNodeOfA, pdgnodeOfBRegion, "dependency");
					pdgNodeOfA.addDependent(pdgnodeOfBRegion);
                    if(!processedRegions.contains(regionOfB)){
					  regions2process.add(regionOfB);
                    }
					//now remove b and all the nodes in the same weak region from the list of dependents
					copyOfDependents.remove(b);
					copyOfDependents.removeAll(regionOfB.getBlocks());
				
					
					/* What remains here in the dependence set needs to be processed separately. For
					 * each node X remained in the dependency set, find the corresponding PDG region node
					 * and add a dependency edge from the region of B to the region of X. If X's weak 
					 * region contains other nodes not in the dependency set of A, create a new region
					 * for X and add the proper dependency edges (this actually happens if X is the header
					 * of a loop and B is a predicate guarding a break/continue.)
					 * 
					 * Note: it seems the only case that there is a node remained in the dependents is when there 
					 * is a path from b to the header of a loop. 
					 */
					
					while(!copyOfDependents.isEmpty())
					{
						Block depB = copyOfDependents.remove(0);
						Region rdepB = block2region.get(depB);
						
						/* 
						 * Actually, there are cases when depB is not the header of a loop and therefore would not dominate the current node
						 * (A) and therefore might not have been created yet. This has happened when an inner loop breaks out of the outer 
						 * loop but could have other cases too.
						 */
						PDGNode depBPDGNode = this.m_obj2pdgNode.get(depB);
						if(depBPDGNode == null)
						{						
							//First, add the dependency for depB and its corresponding region.
							
							PDGNode pdgnodeOfdepBRegion = null;
							if(!this.m_obj2pdgNode.containsKey(rdepB))
							{
								pdgnodeOfdepBRegion = new PDGNode(rdepB, PDGNode.Type.REGION);
								this.addNode(pdgnodeOfdepBRegion);
								this.m_obj2pdgNode.put(rdepB, pdgnodeOfdepBRegion);
								
							}
							else
								pdgnodeOfdepBRegion = this.m_obj2pdgNode.get(rdepB);
							
							//set the region hierarchy
							rdepB.setParent(regionOfB);
							regionOfB.addChildRegion(rdepB);
							
							//add the dependency edges
							this.addEdge(pdgnodeOfBRegion, pdgnodeOfdepBRegion, "dependency");
							pdgnodeOfBRegion.addDependent(pdgnodeOfdepBRegion);
                            if(!processedRegions.contains(rdepB)){
							  regions2process.add(rdepB);
                            }
							
							//now remove all the nodes in the same weak region from the list of dependents
							
							copyOfDependents.removeAll(rdepB.getBlocks());
							
							continue;

						}
						
						/**
						 * If all the nodes in the weak region of depB are dependent on A, then add 
						 * an edge from the region of B to the region of depB.
						 * 
						 * else, a new region has to be created to contain the dependences of depB, if
						 * not already created.
						 */
						if(dependents.containsAll(rdepB.getBlocks()))
						{
							/*
							 * Just add an edge to the pdg node of the existing depB region.
							 * 
							 */
							//add the dependency edges
							//First, add the dependency for depB and its corresponding region.
							
							PDGNode pdgnodeOfdepBRegion = null;
							if(!this.m_obj2pdgNode.containsKey(rdepB))
							{
								pdgnodeOfdepBRegion = new PDGNode(rdepB, PDGNode.Type.REGION);
								this.addNode(pdgnodeOfdepBRegion);
								this.m_obj2pdgNode.put(rdepB, pdgnodeOfdepBRegion);
								
							}
							else
								pdgnodeOfdepBRegion = this.m_obj2pdgNode.get(rdepB);
							
							//set the region hierarchy
							//Do not set this because the region was created before so must have the
							//proper parent already.
							//rdepB.setParent(regionOfB);
							//regionOfB.addChildRegion(rdepB);
							
							this.addEdge(pdgnodeOfBRegion, pdgnodeOfdepBRegion, "dependency");
							pdgnodeOfBRegion.addDependent(pdgnodeOfdepBRegion);
                            if(!processedRegions.contains(rdepB)){
							  regions2process.add(rdepB);
                            }
							
							//now remove all the nodes in the same weak region from the list of dependents
							
							copyOfDependents.removeAll(rdepB.getBlocks());
	
							continue;
						}
						else
						{
							PDGNode predPDGofdepB = (PDGNode) this.getPredsOf(depBPDGNode).get(0);
							assert(this.m_obj2pdgNode.containsKey(rdepB));							
							PDGNode pdgnodeOfdepBRegion = this.m_obj2pdgNode.get(rdepB);
							//If the loop header has not been separated from its weak region yet
							if(predPDGofdepB == pdgnodeOfdepBRegion)
							{
								/*
								 * Create a new region to represent the whole loop. In fact, this is a strong
								 * region as opposed to the weak regions that were created in the RegionAnalysis.
								 * This strong region only contains the header of the loop, A, and is dependent 
								 * on it. Also, A is dependent on this strong region as well.
								 */
								
								Region newRegion = new Region(this.m_strongRegionStartID++, topLevelRegion.getSootMethod(), topLevelRegion.getSootClass(), this.m_cfg);
								newRegion.add(depB);
								
								this.m_strongRegions.add(newRegion);
																
								//toBeRemoved.add(depB);
								List<Block> blocks2BRemoved;
								if(toBeRemoved.contains(predPDGofdepB))
									blocks2BRemoved = toBeRemoved.get(predPDGofdepB);
								else
								{
									blocks2BRemoved = new ArrayList<Block>();
									toBeRemoved.put(rdepB, blocks2BRemoved);
								}
								blocks2BRemoved.add(depB);
								
								
								
								PDGNode newpdgnode = new LoopedPDGNode(newRegion, PDGNode.Type.REGION, depBPDGNode);
								this.addNode(newpdgnode);
								this.m_obj2pdgNode.put(newRegion, newpdgnode);
								newpdgnode.setAttrib(PDGNode.Attribute.LOOPHEADER);
								depBPDGNode.setAttrib(PDGNode.Attribute.LOOPHEADER);
								
								this.removeEdge(pdgnodeOfdepBRegion, depBPDGNode, "dependency");
								pdgnodeOfdepBRegion.removeDependent(depBPDGNode);
								this.addEdge(pdgnodeOfdepBRegion, newpdgnode, "dependency");
								this.addEdge(newpdgnode, depBPDGNode, "dependency");
								pdgnodeOfdepBRegion.addDependent(newpdgnode);
								newpdgnode.addDependent(depBPDGNode);
								
								
								
								//If a is dependent on itself (simple loop)
								if(depB == a)
								{
									PDGNode loopBodyPDGNode = (PDGNode) this.getSuccsOf(depBPDGNode).get(0);
									this.addEdge(depBPDGNode, newpdgnode, "dependency-back");
									((LoopedPDGNode)newpdgnode).setBody(loopBodyPDGNode);
									
									depBPDGNode.addBackDependent(newpdgnode);
									
									//This is needed to correctly adjust the prev/next pointers for the new loop node. We should not need
									//to adjust the old loop header node because the prev/next should not have been set previously for it.
									curNodeInRegion = newpdgnode;
									
								}
								else
								{
									
									//this is a back-dependency
									pdgnodeOfBRegion.addBackDependent(newpdgnode);
									this.addEdge(pdgnodeOfBRegion, newpdgnode, "dependency-back");
									
									//DEtermine which dependent of the loop header is actually the loop body region
									PDGNode loopBodyPDGNode = null;
									List<PDGNode> successors = this.getSuccsOf(depBPDGNode);
									Iterator<PDGNode> succItr = successors.iterator();
									while(succItr.hasNext())
									{
										PDGNode succRPDGNode = succItr.next();
										
										assert(succRPDGNode.getType() == PDGNode.Type.REGION);
										Region succR = (Region) succRPDGNode.getNode();
										Block h = succR.getBlocks().get(0);
										
										DominatorNode<Block> hdode = dom.getDode(h);
										DominatorNode<Block> adode = dom.getDode(a);
										
										if(dom.isDominatorOf(hdode, adode))
										{
											loopBodyPDGNode = succRPDGNode;
											break;
										}
										
									}
									assert(loopBodyPDGNode != null);
									((LoopedPDGNode)newpdgnode).setBody(loopBodyPDGNode);
									
									
									PDGNode prev = depBPDGNode.getPrev();
									if(prev != null)
									{
										this.removeEdge(prev, depBPDGNode, "controlflow");
										this.addEdge(prev, newpdgnode, "controlflow");
										prev.setNext(newpdgnode);
										newpdgnode.setPrev(prev);
										depBPDGNode.setPrev(null);
										
									}
									
									PDGNode next = depBPDGNode.getNext();
									if(next != null)
									{
										this.removeEdge(depBPDGNode, next, "controlflow");
										this.addEdge(newpdgnode, next, "controlflow");
										newpdgnode.setNext(next);
										next.setPrev(newpdgnode);
										depBPDGNode.setNext(null);
										
									}
								}
								
							}
							else
							{
								/*
								 * The strong region for the header has already been created and its 
								 * corresponding PDGNode exist. Just add the dependency edge.
								 */
								this.addEdge(pdgnodeOfBRegion, predPDGofdepB, "dependency-back");
								//this is a back-dependency
								pdgnodeOfBRegion.addBackDependent(predPDGofdepB);
								
							}
							
						}		
						
					}
		
				}	
			
			

				/* If there is a previous node in this region, add a control flow edge to indicate the
				 * the correct direction of control flow in the region.
				 */
				if(prevPDGNodeInRegion != null)
				{
					this.addEdge(prevPDGNodeInRegion, curNodeInRegion, "controlflow");
					prevPDGNodeInRegion.setNext(curNodeInRegion);
					curNodeInRegion.setPrev(prevPDGNodeInRegion);
					
				}
				
				prevPDGNodeInRegion = curNodeInRegion;

			}
			
			//remove all the blocks marked to be removed from the region (to change a weak region
			//to a strong region.)
			
			
			Enumeration<Region> itr1 = toBeRemoved.keys();
			while(itr1.hasMoreElements())
			{
				Region region = itr1.nextElement();
				
				Iterator<Block> blockItr = toBeRemoved.get(region).iterator();
				while(blockItr.hasNext())
					region.remove(blockItr.next());
					
			}

		}
		
	}
	
	private List<Region> cloneRegions(List<Region> weak)
	{
		List<Region> strong = new ArrayList<Region>();
		Iterator<Region> itr = weak.iterator();
		while(itr.hasNext())
		{
			Region r = itr.next();
			strong.add((Region)r.clone());
		}
		return strong;
	}
	
	/**
	 * 
	 * @return The Corresponding UnitGraph
	 */
	public UnitGraph getCFG()
	{
		return this.m_cfg;
	}
	
	/**
	 * {@inheritDoc}
	 */

	public List<Region> getWeakRegions()
	{
		return this.m_weakRegions;
	}
	
	/**
	 * {@inheritDoc}
	 */

	public List<Region> getStrongRegions()
	{
		return this.m_strongRegions;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public IRegion GetStartRegion()
	{
		return this.m_pdgRegions.get(0);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public PDGNode GetStartNode()
	{
		return this.m_startNode;
	}

	public static List<IRegion> getPreorderRegionList(IRegion r)
	{
		List<IRegion> list = new ArrayList<IRegion>();

		
		Queue<IRegion> toProcess = new LinkedList<IRegion>();
		toProcess.add(r);
		while(!toProcess.isEmpty())
		{
			IRegion reg = toProcess.poll();
			list.add(reg);
			for(Iterator<IRegion> itr = reg.getChildRegions().iterator(); itr.hasNext(); )
				toProcess.add((Region)itr.next());
			
		}
		
		return list;
	}
	
	public static List<IRegion> getPostorderRegionList(IRegion r)
	{
		List<IRegion> list = new ArrayList<IRegion>();			
		postorder(r, list);
		
		return list;
	}
	
	private static List<IRegion> postorder(IRegion r, List<IRegion> list)
	{
		//If there are children, push the children to the stack
		if(!r.getChildRegions().isEmpty())
			for(Iterator<IRegion> itr = r.getChildRegions().iterator(); itr.hasNext(); )
				postorder(itr.next(), list);
		
		list.add(r);
		return list;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<PDGRegion> getPDGRegions()
	{
		return this.m_pdgRegions;
	}
	
	/**
	 * This method returns a list of regions obtained by post-order 
	 * traversal of the region hierarchy. This takes advantage of the hierarchical
	 * (parent/child) information encoded within the PDGNodes at construction
	 * time; it should be noted that, we have not counted the strong regions
	 * that represent the loop header as a separate region; instead, a PDGRegion
	 * that represents both the loop header and its body are counted.
	 * @param The root from which the traversal should begin.
	 * @return The list of regions obtained thru post-order traversal of the 
	 * region hierarchy.
	 */
	public static List<PDGRegion> getPostorderPDGRegionList(PDGNode r)
	{
		return computePDGRegions(r);
		
	}
	
	private static Hashtable<PDGNode, PDGRegion> node2Region = new Hashtable<PDGNode, PDGRegion>();
	//compute the pdg region list with in post order 
	private static List<PDGRegion> computePDGRegions(PDGNode root)
	{
		List<PDGRegion> regions = new ArrayList<PDGRegion>();
				
		node2Region.clear();
		pdgpostorder(root, regions);
		
		return regions;
	}
	
	
	
	private static PDGRegion pdgpostorder(PDGNode node, List<PDGRegion> list)
	{
        if(node.getVisited()){
          return null;
        }
		node.setVisited(true);

		PDGRegion region = null;
		if(!node2Region.containsKey(node))
		{
			region = new PDGRegion(node);
			node2Region.put(node, region);
		}
		else
			region = node2Region.get(node);

		//If there are children, push the children to the stack
		List<PDGNode> dependents = node.getDependets();
		if(!dependents.isEmpty())
			for(Iterator<PDGNode> itr = dependents.iterator(); itr.hasNext(); )
			{
				PDGNode curNode = (PDGNode) itr.next();
				if(curNode.getVisited())
					continue;
				
				region.addPDGNode(curNode);
				
				if(curNode instanceof LoopedPDGNode)
				{
					PDGNode body = ((LoopedPDGNode)curNode).getBody();
					PDGRegion kid = pdgpostorder(body, list);
                    if(kid != null){
					  kid.setParent(region);
					  region.addChildRegion(kid);
					
					  //This sets the node from the old Region into a PDGRegion
					  body.setNode(kid);
                    }
				}
				else if(curNode instanceof ConditionalPDGNode)
				{
					List<PDGNode> childs = curNode.getDependets();
					Iterator<PDGNode> condItr = childs.iterator();
					while(condItr.hasNext())
					{
						PDGNode child = (PDGNode)condItr.next();
						PDGRegion kid = pdgpostorder(child, list);
                        if(kid != null){
						  kid.setParent(region);
						  region.addChildRegion(kid);
						  //This sets the node from the old Region into a PDGRegion

						  child.setNode(kid);
						}
					}

				}
					
			}
		
		list.add(region);
		return region;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean dependentOn(PDGNode node1, PDGNode node2)
	{
		return node2.getDependets().contains(node1);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<PDGNode> getDependents(PDGNode node)
	{
	       List<PDGNode> toReturn = new ArrayList<PDGNode>();
	       for(PDGNode succ : this.getSuccsOf(node)) {
	    	   if(this.dependentOn(succ, node)) {
	    		   toReturn.add(succ);
	    	   }
	       }
	       return toReturn;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public PDGNode getPDGNode(Object cfgNode)
	{
		if(cfgNode != null && cfgNode instanceof Block)
			if(this.m_obj2pdgNode.containsKey(cfgNode))
				return this.m_obj2pdgNode.get(cfgNode);
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void replaceInGraph(PDGNode newnode, PDGNode oldnode)
	{		
		this.addNode(newnode);
		
		HashMutableEdgeLabelledDirectedGraph graph = (HashMutableEdgeLabelledDirectedGraph) this.clone();
				
		List<PDGNode> succs = graph.getSuccsOf(oldnode);
		List<PDGNode> preds = graph.getPredsOf(oldnode);
		
		for (PDGNode succ : succs) {
			List<Object> labels = graph.getLabelsForEdges(oldnode, succ);
			for(Iterator<Object> labelItr = labels.iterator(); labelItr.hasNext(); )
			{
				Object label = labelItr.next();
				this.addEdge(newnode, succ, new String(label.toString()));	
			}	
		}
			
		for (PDGNode pred : preds) {
			List<Object> labels = graph.getLabelsForEdges(pred, oldnode);
			for(Iterator<Object> labelItr = labels.iterator(); labelItr.hasNext(); )
			{
				Object label = labelItr.next();
				this.addEdge(pred, newnode, new String(label.toString()));
			}		
		}
		
		this.removeNode(oldnode);
	
	}
	/**
	 * The existing removeAllEdges in the parent class seems to be throwing concurrentmodification exception
	 * most of the time. Here is a version that doesn't throw that exception.
	 * 
	 */
	public void removeAllEdges(PDGNode from, PDGNode to)
	{
        if (!containsAnyEdge(from, to))
            return;
            
        List<String> labels = new ArrayList<String>(this.getLabelsForEdges(from, to));        
        for(String label : labels) {        
	        this.removeEdge(from, to, label);
	    }
	}

	/**
	 * {@inheritDoc}
	 */
    @Override
	public String toString()
	{
		String s = new String("\nProgram Dependence Graph for Method " + this.m_body.getMethod().getName());
		s += "\n*********CFG******** \n" + RegionAnalysis.CFGtoString(this.m_blockCFG, true);
		s += "\n*********PDG******** \n";
		
		List<PDGNode> processed = new ArrayList<PDGNode>();
		Queue<PDGNode> nodes = new LinkedList<PDGNode>();
		nodes.offer(this.m_startNode);
		
		while(nodes.peek() != null)
		{
			PDGNode node = (PDGNode) nodes.remove();
			processed.add(node);
			
			s += "\n Begin PDGNode: " + node;
			List<PDGNode> succs = this.getSuccsOf(node);
			s += "\n has " + succs.size() + " successors:\n";
			
			int i = 0;
			for(PDGNode succ : succs) {
				List<String> labels = this.getLabelsForEdges(node, succ);
				
				s += i++;
				s += ": Edge's label: " + labels + " \n";
				s += "   Target: " + succ.toShortString();
				s += "\n";
				
				if(labels.get(0).equals("dependency"))
				if(!processed.contains(succ))
					nodes.offer(succ);
				
			}
			
			s += "\n End PDGNode.";
			
		}
		return s;
	}

}
