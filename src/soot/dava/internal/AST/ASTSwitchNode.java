package soot.dava.internal.AST;

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.dava.internal.SET.*;
import soot.dava.toolkits.base.AST.*;

public class ASTSwitchNode extends ASTLabeledNode
{
    private ValueBox keyBox;
    private List indexList;
    private Map index2BodyList;

    public ASTSwitchNode( SETNodeLabel label, Value key, List indexList, Map index2BodyList)
    {
	super( label);

	this.keyBox = Jimple.v().newRValueBox( key );
	this.indexList = indexList;
	this.index2BodyList = index2BodyList;

	Iterator it = indexList.iterator();
	while (it.hasNext()) {
	    List body = (List) index2BodyList.get( it.next());
	    
	    if (body != null)
		subBodies.add( body);
	}
    }

    public Value get_Key()
    {
	return keyBox.getValue();
    }

    public Object clone()
    {
	return new ASTSwitchNode( get_Label(), get_Key(), indexList, index2BodyList);
    }

    public void perform_Analysis( ASTAnalysis a)
    {
	ASTWalker.v().walk_value( a, get_Key());

	if (a instanceof TryContentsFinder) {
	    TryContentsFinder tcf = (TryContentsFinder) a;
	    tcf.v().add_ExceptionSet( this, tcf.v().remove_CurExceptionSet());
	}
  
	perform_AnalysisOnSubBodies( a);
    }


    public void toString( UnitPrinter up )
    {
        label_toString( up );

        up.literal( "switch" );
        up.literal( " " );
        up.literal( "(" );
        keyBox.toString( up );
        up.literal( ")" );
        up.newline();

        up.literal( "{" );
        up.newline();

	Iterator it = indexList.iterator();
	while (it.hasNext()) {
	    
	    Object index = it.next();

            up.incIndent();
	    
	    if (index instanceof String) 
                up.literal( "default" );

	    else {
                up.literal( "case" );
                up.literal( " " );
                up.literal( index.toString() );
	    }
	    
            up.literal( ":" );
            up.newline();

	    List subBody = (List) index2BodyList.get( index);

	    if (subBody != null) {
                up.incIndent();
                body_toString( up, subBody );
	    
		if (it.hasNext())
		    up.newline();
                up.decIndent();
	    }
            up.decIndent();
	}

	up.literal( "}");
        up.newline();
    }

    public String toString( Map stmtToName, String indentation)
    {
	StringBuffer b = new StringBuffer();
	
	b.append( label_toString( indentation));
	
	b.append( indentation);
	b.append( "switch (");
	b.append( get_Key() );
	b.append( ")");
	b.append( NEWLINE);

	b.append( indentation);
	b.append( "{");
	b.append( NEWLINE);

	Iterator it = indexList.iterator();
	while (it.hasNext()) {
	    
	    Object index = it.next();

	    b.append( indentation);
	    b.append( TAB);
	    
	    if (index instanceof String) 
		b.append( "default");

	    else {
		b.append( "case ");
		b.append( ((Integer) index).toString());
	    }
	    
	    b.append( ":");
	    b.append( NEWLINE);

	    List subBody = (List) index2BodyList.get( index);

	    if (subBody != null) {
		b.append( body_toString( stmtToName, indentation + TAB + TAB, subBody));
	    
		if (it.hasNext())
		    b.append( NEWLINE);
	    }
	}

	b.append( indentation);
	b.append( "}");
	b.append( NEWLINE);

	return b.toString();
    }
}
