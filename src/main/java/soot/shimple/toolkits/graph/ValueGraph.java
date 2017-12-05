/////////////////////////
// Confusion on new Node(Value value)
// is value leftOp or rightOp?
/////////////////////////




/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
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

package soot.shimple.toolkits.graph;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import soot.shimple.*;
import soot.toolkits.graph.*;

import java.util.*;

// consider implementing DirectedGraph
public class ValueGraph
{
    // can we handle field writes/reads?
    // Issues:  - does the field write DOMINATE field uses?
    //          - do intervening method calls have SIDE-EFFECTs?
    //            Affects fields whether of simple type or ref type
    //          - CONCURRENT writes?

    protected Map<Value, Node> localToNode;
    protected Map<Node, Value> nodeToLocal;
    protected List<Node> nodeList;
    protected int currentNodeNumber;
    
    public ValueGraph(BlockGraph cfg)
    {
        if(!(cfg.getBody() instanceof ShimpleBody))
            throw new RuntimeException("ValueGraph requires SSA form");

        localToNode = new HashMap<Value, Node>();
        nodeToLocal = new HashMap<Node, Value>();
        nodeList = new ArrayList<Node>();
        currentNodeNumber = 0;
        Orderer<Block> pto = new PseudoTopologicalOrderer<Block>();
        List<Block> blocks = pto.newList(cfg,false);

        for(Iterator<Block> blocksIt = blocks.iterator(); blocksIt.hasNext();){
            Block block = (Block) blocksIt.next();
            for(Iterator<Unit> blockIt = block.iterator(); blockIt.hasNext();)
                handleStmt((Stmt) blockIt.next());
        }

        for (Node node : nodeList) {
            node.patchStubs();
        }
    }

    protected void handleStmt(Stmt stmt)
    {
        if(!(stmt instanceof DefinitionStmt))
            return;
        DefinitionStmt dStmt = (DefinitionStmt) stmt;

        Value leftOp = dStmt.getLeftOp();
        if(!(leftOp instanceof Local))
            return;

        Value rightOp = dStmt.getRightOp();
        Node node = fetchGraph(rightOp);
        localToNode.put(leftOp, node);

        // only update for non-trivial assignments and non-stubs
        if(!(rightOp instanceof Local) && !node.isStub())
            nodeToLocal.put(node, leftOp);
    }
    
    protected Node fetchNode(Value value)
    {
        Node ret = null;

        if(value instanceof Local){
            // assumption: the local definition has already been processed            
            ret = getNode(value);

            // or maybe not...  a PhiExpr may refer to a local that
            // has not been seen yet in the pseudo topological order.
            // use a stub node in that case and fill in the details later.
            if(ret == null)
                ret = new Node(value, true);
        }

        // theoretically we could share Constant nodes...
        else
            ret = new Node(value);

        return ret;
    }

    protected Node fetchGraph(Value value)
    {
        AbstractShimpleValueSwitch vs;
        
        value.apply(vs = new AbstractShimpleValueSwitch()
        {
            /**
             * No default case, we implement explicit handling for
             * each situation.
             **/
            public void defaultCase(Object object)
            {
                throw new RuntimeException("Internal error: " + object +
                                           " unhandled case.");
            }
            
            /**
             * Handle a trivial assignment.
             **/
            public void caseLocal(Local l)
            {
                setResult(fetchNode(l));
            }

            /**
             * Handle other simple assignments.
             **/
            public void handleConstant(Constant constant)
            {
                setResult(fetchNode(constant));
            }

            /**
             * Assume nothing about Refs.
             **/
            public void handleRef(Ref ref)
            {
                setResult(fetchNode(ref));
            }

            public void handleBinop(BinopExpr binop, boolean ordered)
            {
                Node nop1 = fetchNode(binop.getOp1());
                Node nop2 = fetchNode(binop.getOp2());

                List<Node> children = new ArrayList<Node>();
                children.add(nop1);
                children.add(nop2);

                setResult(new Node(binop, ordered, children));
            }
            
            // *** FIXME
            // *** assume non-equality by default
            // *** what about New expressions?
            public void handleUnknown(Expr expr)
            {
                setResult(fetchNode(expr));
            }
            
            public void handleUnop(UnopExpr unop)
            {
                Node nop = fetchNode(unop.getOp());
                List<Node> child = Collections.<Node>singletonList(nop);
                setResult(new Node(unop, true, child));
            }
            
            public void caseFloatConstant(FloatConstant v)
            {
                handleConstant(v);
            }

            public void caseIntConstant(IntConstant v)
            {
                handleConstant(v);
            }
            
            public void caseLongConstant(LongConstant v)
            {
                handleConstant(v);
            }

            public void caseNullConstant(NullConstant v)
            {
                handleConstant(v);
            }

            public void caseStringConstant(StringConstant v)
            {
                handleConstant(v);
            }

            public void caseArrayRef(ArrayRef v)
            {
                handleRef(v);
            }

            public void caseStaticFieldRef(StaticFieldRef v)
            {
                handleRef(v);
            }

            public void caseInstanceFieldRef(InstanceFieldRef v)
            {
                handleRef(v);
            }

            public void caseParameterRef(ParameterRef v)
            {
                handleRef(v);
            }

            public void caseCaughtExceptionRef(CaughtExceptionRef v)
            {
                handleRef(v);
            }

            public void caseThisRef(ThisRef v)
            {
                handleRef(v);
            }

            public void caseAddExpr(AddExpr v)
            {
                handleBinop(v, false);
            }

            public void caseAndExpr(AndExpr v)
            {
                handleBinop(v, false);
            }

            public void caseCmpExpr(CmpExpr v)
            {
                handleBinop(v, true);
            }

            public void caseCmpgExpr(CmpgExpr v)
            {
                handleBinop(v, true);
            }
            
            public void caseCmplExpr(CmplExpr v)
            {
                handleBinop(v, true);
            }
            
            public void caseDivExpr(DivExpr v)
            {
                handleBinop(v, true);
            }
            
            public void caseEqExpr(EqExpr v)
            {
                handleBinop(v, false);
            }
                
            public void caseNeExpr(NeExpr v)
            {
                handleBinop(v, false);
            }
            
            public void caseGeExpr(GeExpr v)
            {
                handleBinop(v, true);
            }
                
            public void caseGtExpr(GtExpr v)
            {
                handleBinop(v, true);
            }
            
            public void caseLeExpr(LeExpr v)
            {
                handleBinop(v, true);
            }
            
            public void caseLtExpr(LtExpr v)
            {
                handleBinop(v, true);
            }
            
            public void caseMulExpr(MulExpr v)
            {
                handleBinop(v, false);
            }

            // *** check
            public void caseOrExpr(OrExpr v)
            {
                handleBinop(v, false);
            }
            
            public void caseRemExpr(RemExpr v)
            {
                handleBinop(v, true);
            }
            
            public void caseShlExpr(ShlExpr v)
            {
                handleBinop(v, true);
            }
            
            public void caseShrExpr(ShrExpr v)
            {
                handleBinop(v, true);
            }
            
            public void caseUshrExpr(UshrExpr v)
            {
                handleBinop(v, true);
            }
            
            public void caseSubExpr(SubExpr v)
            {
                handleBinop(v, true);
            }

            // *** check
            public void caseXorExpr(XorExpr v)
            {
                handleBinop(v, false);
            }
            
            public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v)
            {
                handleUnknown(v);
            }

            public void caseSpecialInvokeExpr(SpecialInvokeExpr v)
            {
                handleUnknown(v);
            }
            
            public void caseStaticInvokeExpr(StaticInvokeExpr v)
            {
                handleUnknown(v);
            }
            
            public void caseVirtualInvokeExpr(VirtualInvokeExpr v)
            {
                handleUnknown(v);
            }

            /**
             * Handle like a trivial assignment.
             **/
            public void caseCastExpr(CastExpr v)
            {
                setResult(fetchNode(v.getOp()));
            }

            /**
             * Handle like an ordered binop.
             **/
            public void caseInstanceOfExpr(InstanceOfExpr v)
            {
                Node nop1 = fetchNode(v.getOp());

                Value op2 = new TypeValueWrapper(v.getCheckType());
                Node nop2 = fetchNode(op2);

                List<Node> children = new ArrayList<Node>();
                children.add(nop1);
                children.add(nop2);

                setResult(new Node(v, true, children));
            }

            // *** perhaps New expressions require special handling?
            public void caseNewArrayExpr(NewArrayExpr v)
            {
                handleUnknown(v);
            }
            
            public void caseNewMultiArrayExpr(NewMultiArrayExpr v)
            {
                handleUnknown(v);
            }

            public void caseNewExpr(NewExpr v)
            {
                handleUnknown(v);
            }

            public void caseLengthExpr(LengthExpr v)
            {
                handleUnop(v);
            }

            public void caseNegExpr(NegExpr v)
            {
                handleUnop(v);
            }

            public void casePhiExpr(PhiExpr v)
            {
                List<Node> children = new ArrayList<Node>();
                Iterator<Value> argsIt = v.getValues().iterator();

                while(argsIt.hasNext()){
                    Value arg = argsIt.next();
                    children.add(fetchNode(arg));
                }

                // relies on Phi nodes in same block having a
                // consistent sort order...
                setResult(new Node(v, true, children));
            }
        });

        return((Node)vs.getResult());
    }

    public Node getNode(Value local)
    {
        return localToNode.get(local);
    }

    // *** Check for non-determinism
    public Collection<Node> getTopNodes()
    {
        return localToNode.values();
    }

    public Local getLocal(Node node)
    {
        return (Local)nodeToLocal.get(node);
    }

    public String toString()
    {
        StringBuffer tmp = new StringBuffer();
        
        for(int i = 0; i < nodeList.size(); i++){
            tmp.append(nodeList.get(i));
            tmp.append("\n");
        }

        return tmp.toString();
    }

    // testing
    public static void main(String[] args)
    {
        // assumes 2 args:  Class + Method

        Scene.v().loadClassAndSupport(args[0]);
        SootClass sc = Scene.v().getSootClass(args[0]);
        SootMethod sm = sc.getMethod(args[1]);
        Body b = sm.retrieveActiveBody();
        ShimpleBody sb = Shimple.v().newBody(b);
        CompleteBlockGraph cfg = new CompleteBlockGraph(sb);
        ValueGraph vg = new ValueGraph(cfg);
        System.out.println(vg);
    }

    public class Node
    {
        protected int nodeNumber;
        protected Value node;
        protected String nodeLabel;
        protected boolean ordered;
        protected List<Node> children;

        protected boolean stub = false;
        
        // stub node
        protected Node(Value local, boolean ignored)
        {
            this.stub = true;
            setNode(local);
        }

        protected void patchStubs()
        {
            // can't patch self
            if(isStub())
                throw new RuntimeException("Assertion failed.");

            // if any immediate children are stubs, patch them
            for(int i = 0; i < children.size(); i++){
                Node child = children.get(i);

                if(child.isStub()){
                    Node newChild = localToNode.get(child.node);
                    if(newChild == null || newChild.isStub())
                        throw new RuntimeException("Assertion failed.");
                    children.set(i, newChild);
                }
            }
        }
        
        protected void checkIfStub()
        {
            if(isStub())
                throw new RuntimeException("Assertion failed:  Attempted operation on invalid node (stub)");
        }

        protected Node(Value node)
        {
            this(node, true, Collections.<Node>emptyList());
        }

        protected Node(Value node, boolean ordered, List<Node> children)
        {
            setNode(node);
            setOrdered(ordered);
            setChildren(children);

            // updateLabel() relies on nodeNumber being set
            nodeNumber = currentNodeNumber++;
            updateLabel(); 
            nodeList.add(nodeNumber, this);
        }

        protected void setNode(Value node)
        {
            this.node = node;
        }
        
        protected void setOrdered(boolean ordered)
        {
            this.ordered = ordered;
        }

        protected void setChildren(List<Node> children)
        {
            this.children = children;
        }

        protected void updateLabel()
        {
            if(!children.isEmpty()){
                nodeLabel = node.getClass().getName();
                if(node instanceof PhiExpr)
                    nodeLabel = nodeLabel + ((PhiExpr)node).getBlockId();
            }
            else{
                // *** FIXME
                
                // NewExpr
                // NewArrayExpr
                // NewMultiArrayExpr

                // Ref
                // FieldRef?
                // InstanceFieldRef?
                // IdentityRef?
                // ArrayRef?
                // CaughtExceptionRef

                // InvokeExpr?
                // InstanceInvokeExpr?
                // InterfaceInvokeExpr?
                // SpecialInvokeExpr
                // StaticInvokeExpr
                // VirtualInvokeExpr
                nodeLabel = node.toString();
                if((node instanceof NewExpr) ||
                   (node instanceof NewArrayExpr) ||
                   (node instanceof NewMultiArrayExpr) ||
                   (node instanceof Ref) ||
                   (node instanceof InvokeExpr))
                    nodeLabel = nodeLabel + " " + getNodeNumber();
            }
        }
        
        public boolean isStub()
        {
            return stub;
        }

        public String getLabel()
        {
            checkIfStub();            
            return nodeLabel;
        }

        public boolean isOrdered()
        {
            checkIfStub();
            return ordered;
        }

        public List<Node> getChildren()
        {
            checkIfStub();
            return children;
            //return Collections.unmodifiableList(children);
        }

        public int getNodeNumber()
        {
            checkIfStub();
            return nodeNumber;
        }
        
        public String toString()
        {
            checkIfStub();

            StringBuffer tmp = new StringBuffer();
            
            Local local = getLocal(this);
            if(local != null)
                tmp.append(local.toString());
            
            tmp.append("\tNode " + getNodeNumber() + ": " + getLabel());

            List<Node> children = getChildren();

            if(!children.isEmpty()){
                tmp.append(" [" +
                           (isOrdered() ? "ordered" : "unordered") + ": ");
                for(int i = 0; i < children.size(); i++){
                    if(i != 0)
                        tmp.append(", ");
                    tmp.append(children.get(i).getNodeNumber());
                }
                tmp.append("]");
            }
        
            return tmp.toString();
        }
    }

    protected static class TypeValueWrapper implements Value
    {
        protected Type type;

        protected TypeValueWrapper(Type type)
        {
            this.type = type;
        }

        public List<ValueBox> getUseBoxes()
        {
            return Collections.<ValueBox>emptyList();
        }

        public Type getType()
        {
            return type;
        }

        public Object clone()
        {
            return new TypeValueWrapper(type);
        }

        public void toString(UnitPrinter up)
        {
            up.literal("[Wrapped] " + type);
        }

        public void apply(Switch sw)
        {
            throw new RuntimeException("Not Implemented.");
        }

        public boolean equals(Object o)
        {
            if(!(o instanceof TypeValueWrapper))
                return false;

            return getType().equals(((TypeValueWrapper)o).getType());
        }

        public int hashCode()
        {
            return getType().hashCode();
        }

        public boolean equivTo(Object o)
        {
            return equals(o);
        }

        public int equivHashCode()
        {
            return hashCode();
        }
    }    
}
