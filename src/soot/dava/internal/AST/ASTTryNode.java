/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
 * Copyright (C) 2005 Nomair A. Naeem
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

package soot.dava.internal.AST;

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.dava.internal.SET.*;
import soot.dava.toolkits.base.AST.*;
import soot.dava.toolkits.base.AST.analysis.*;

public class ASTTryNode extends ASTLabeledNode
{
    private List tryBody, catchList;
    private Map exceptionMap, paramMap;
    private container tryBodyContainer;

    public class container
    {
	public Object o;

	public container( Object o)
	{
	    this.o = o;
	}

	public void replaceBody(Object newBody){
	    this.o=newBody;
	}
    }

    public ASTTryNode( SETNodeLabel label, List tryBody, List catchList, Map exceptionMap, Map paramMap)
    {
	super( label);

	this.tryBody = tryBody;
	tryBodyContainer = new container( tryBody);

	this.catchList = new ArrayList();
	Iterator cit = catchList.iterator();
	while (cit.hasNext())
	    this.catchList.add( new container( cit.next()));
	
	this.exceptionMap = new HashMap();
	cit = this.catchList.iterator();
	while (cit.hasNext()) {
	    container c = (container) cit.next();
	    this.exceptionMap.put( c, exceptionMap.get( c.o));
	}

	this.paramMap = new HashMap();
	cit = this.catchList.iterator();
	while (cit.hasNext()) {
	    container c = (container) cit.next();
	    this.paramMap.put( c, paramMap.get( c.o));
	}
	
	subBodies.add( tryBodyContainer);
	cit = this.catchList.iterator();
	while (cit.hasNext())
	    subBodies.add( cit.next());
    }


    /*
      Nomair A Naeem 21-FEB-2005
      used to support UselessLabeledBlockRemover
    */
    public void replaceTryBody(List tryBody){
	this.tryBody = tryBody;
	tryBodyContainer = new container( tryBody);
	
	List oldSubBodies=subBodies;
	subBodies=new ArrayList();

	subBodies.add( tryBodyContainer);

	Iterator oldIt = oldSubBodies.iterator();
	//discard the first since that was the old tryBodyContainer
	oldIt.next();

	while (oldIt.hasNext())
	    subBodies.add( oldIt.next());
	
    }

    protected void perform_AnalysisOnSubBodies( ASTAnalysis a)
    {
	if (a instanceof TryContentsFinder) {
	    TryContentsFinder tcf = (TryContentsFinder) a;

	    Iterator sbit = subBodies.iterator();
	    while (sbit.hasNext()) {
		container subBody = (container) sbit.next();

		Iterator it = ((List) subBody.o).iterator();
		while (it.hasNext()) {
		    ASTNode n = (ASTNode) it.next();
		    
		    n.perform_Analysis( a);
		    tcf.v().add_ExceptionSet( subBody, tcf.v().get_ExceptionSet( n));
		}
	    }

	    a.analyseASTNode( this);
	}
	else 
	    super.perform_AnalysisOnSubBodies( a);
    }

    public boolean isEmpty()
    {
	return tryBody.isEmpty();
    }

    public List get_TryBody()
    {
	return tryBody;
    }

    public container get_TryBodyContainer()
    {
	return tryBodyContainer;
    }

    public List get_CatchList()
    {
	return catchList;
    }

    public Map get_ExceptionMap()
    {
	return exceptionMap;
    }


    /*
      Nomair A. Naeem 08-FEB-2005
      Needed for call from DepthFirstAdapter
    */
    public Map get_ParamMap(){
	return paramMap;
    }

    public Set get_ExceptionSet()
    {
	HashSet s = new HashSet();

	Iterator it = catchList.iterator();
	while (it.hasNext())
	    s.add( exceptionMap.get( it.next()));

	return s;
    }

    public Object clone()
    {
	ArrayList newCatchList = new ArrayList();
	Iterator it = catchList.iterator();
	while (it.hasNext())
	    newCatchList.add( ((container) it.next()).o);

	return new ASTTryNode( get_Label(), tryBody, newCatchList, exceptionMap, paramMap);
    }

    public void toString( UnitPrinter up )
    {
        label_toString( up );

	up.literal( "try");
        up.newline();
	
        up.literal( "{" );
        up.newline();

        up.incIndent();
	body_toString( up, tryBody );
        up.decIndent();

        up.literal( "}" );
        up.newline();



	Iterator cit = catchList.iterator();
	while (cit.hasNext()) {
	    container catchBody = (container) cit.next();

            up.literal( "catch" );
            up.literal( " " );
            up.literal( "(" );
            up.type( ((SootClass) exceptionMap.get(catchBody)).getType() );
	    up.literal( " ");
	    up.local( (Local) paramMap.get(catchBody) );
	    up.literal( ")");
            up.newline();

            up.literal("{");
            up.newline();

            up.incIndent();
            body_toString( up, (List) catchBody.o );
            up.decIndent();

            up.literal( "}" );
            up.newline();
	}
    }

    public String toString()
    {
	StringBuffer b = new StringBuffer();
	
	b.append( label_toString( ));

	b.append( "try");
	b.append( NEWLINE);
	
	b.append( "{");
	b.append( NEWLINE);

	b.append( body_toString( tryBody));

	b.append( "}");
	b.append( NEWLINE);

	Iterator cit = catchList.iterator();
	while (cit.hasNext()) {
	    container catchBody = (container) cit.next();

	    b.append( "catch (");
	    b.append( ((SootClass) exceptionMap.get( catchBody)).getName());
	    b.append( " ");
	    b.append( ((Local) paramMap.get( catchBody)).getName());
	    b.append( ")");
	    b.append( NEWLINE);

	    b.append( "{");
	    b.append( NEWLINE);

	    b.append( body_toString( (List) catchBody.o));

	    b.append( "}");
	    b.append( NEWLINE);
	}

	return b.toString();
    }


    /*
      Nomair A. Naeem, 7-FEB-05
      Part of Visitor Design Implementation for AST
      See: soot.dava.toolkits.base.AST.analysis For details
    */
    public void apply(Analysis a){
	a.caseASTTryNode(this);
    }
}
