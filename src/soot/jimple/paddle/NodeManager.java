/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002, 2003 Ondrej Lhotak
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

package soot.jimple.paddle;
import soot.jimple.*;
import soot.*;
import soot.util.*;
import soot.toolkits.scalar.Pair;
import java.util.*;
import soot.util.queue.*;
import soot.tagkit.*;
import soot.options.*;

/** Class implementing builder parameters (this decides
 * what kinds of nodes should be built for each kind of Soot value).
 * @author Ondrej Lhotak
 */

public class NodeManager {
    private Map nodeToTag = new HashMap();
    private void addNodeTag( Node node, SootMethod m ) {
        if( nodeToTag != null ) {
            Tag tag;
            if( m == null ) {
                tag = new StringTag( node.toString() );
            } else {
                tag = new LinkTag( node.toString(), m, m.getDeclaringClass().getName() );
            }
            nodeToTag.put( node, tag );
        }
    }
    public AllocNode makeAllocNode( Object newExpr, Type type, SootMethod m ) {
        if( PaddleScene.v().options().types_for_sites() || PaddleScene.v().options().vta() ) newExpr = type;
	AllocNode ret = (AllocNode) valToAllocNode.get( newExpr );
	if( ret == null ) {
	    valToAllocNode.put( newExpr, ret = new AllocNode( newExpr, type, m ) );
            newAllocNodes.add( ret );
            addNodeTag( ret, m );
	} else if( !( ret.getType().equals( type ) ) ) {
	    throw new RuntimeException( "NewExpr "+newExpr+" of type "+type+
		    " previously had type "+ret.getType() );
	}
	return ret;
    }
    public AllocNode makeStringConstantNode( String s ) {
        if( PaddleScene.v().options().types_for_sites() || PaddleScene.v().options().vta() )
            return makeAllocNode( RefType.v( "java.lang.String" ),
                    RefType.v( "java.lang.String" ), null );
        StringConstantNode ret = (StringConstantNode) valToAllocNode.get( s );
	if( ret == null ) {
	    valToAllocNode.put( s, ret = new StringConstantNode( s ) );
            newAllocNodes.add( ret );
            addNodeTag( ret, null );
	}
	return ret;
    }

    ChunkedQueue newAllocNodes = new ChunkedQueue();
    public QueueReader allocNodeListener() { return newAllocNodes.reader(); }

    /** Finds the GlobalVarNode for the variable value, or returns null. */
    public GlobalVarNode findGlobalVarNode( Object value ) {
        if( PaddleScene.v().options().rta() ) {
            value = null;
        }
	return (GlobalVarNode) valToGlobalVarNode.get( value );
    }
    /** Finds the LocalVarNode for the variable value, or returns null. */
    public LocalVarNode findLocalVarNode( Object value ) {
        if( PaddleScene.v().options().rta() ) {
            value = null;
        } else if( value instanceof Local ) {
            return (LocalVarNode) localToNodeMap.get( (Local) value );
        }
	return (LocalVarNode) valToLocalVarNode.get( value );
    }
    /** Finds or creates the GlobalVarNode for the variable value, of type type. */
    public GlobalVarNode makeGlobalVarNode( Object value, Type type ) {
        if( PaddleScene.v().options().rta() ) {
            value = null;
            type = RefType.v("java.lang.Object");
        }
        GlobalVarNode ret = (GlobalVarNode) valToGlobalVarNode.get( value );
        if( ret == null ) {
            valToGlobalVarNode.put( value, 
                    ret = new GlobalVarNode( this, value, type ) );
            addNodeTag( ret, null );
        } else if( !( ret.getType().equals( type ) ) ) {
            throw new RuntimeException( "Value "+value+" of type "+type+
                    " previously had type "+ret.getType() );
        }
	return ret;
    }
    /** Finds or creates the LocalVarNode for the variable value, of type type. */
    public LocalVarNode makeLocalVarNode( Object value, Type type, SootMethod method ) {
        if( PaddleScene.v().options().rta() ) {
            value = null;
            type = RefType.v("java.lang.Object");
            method = null;
        } else if( value instanceof Local ) {
            Local val = (Local) value;
            if( val.getNumber() == 0 ) Scene.v().getLocalNumberer().add(val);
            LocalVarNode ret = (LocalVarNode) localToNodeMap.get( val );
            if( ret == null ) {
                localToNodeMap.put( (Local) value,
                    ret = new LocalVarNode( this, value, type, method ) );
                addNodeTag( ret, method );
            } else if( !( ret.getType().equals( type ) ) ) {
                throw new RuntimeException( "Value "+value+" of type "+type+
                        " previously had type "+ret.getType() );
            }
            return ret;
        }
        LocalVarNode ret = (LocalVarNode) valToLocalVarNode.get( value );
        if( ret == null ) {
            valToLocalVarNode.put( value, 
                    ret = new LocalVarNode( this, value, type, method ) );
            addNodeTag( ret, method );
        } else if( !( ret.getType().equals( type ) ) ) {
            throw new RuntimeException( "Value "+value+" of type "+type+
                    " previously had type "+ret.getType() );
        }
	return ret;
    }
    /** Finds the ContextVarNode for base variable value and context
     * context, or returns null. */
    public ContextVarNode findContextVarNode( Object baseValue, Object context ) {
	LocalVarNode base = findLocalVarNode( baseValue );
	if( base == null ) return null;
	return base.context( context );
    }
    /** Finds or creates the ContextVarNode for base variable baseValue and context
     * context, of type type. */
    public ContextVarNode makeContextVarNode( Object baseValue, Type baseType,
	    Context context, SootMethod method ) {
	LocalVarNode base = makeLocalVarNode( baseValue, baseType, method );
        return makeContextVarNode( base, context );
    }
    /** Finds or creates the ContextVarNode for base variable base and context
     * context, of type type. */
    public ContextVarNode makeContextVarNode( LocalVarNode base, Context context ) {
	ContextVarNode ret = base.context( context );
	if( ret == null ) {
	    ret = new ContextVarNode( this, base, context );
            addNodeTag( ret, base.getMethod() );
	}
	return ret;
    }
    /** Finds the FieldRefNode for base variable value and field
     * field, or returns null. */
    public FieldRefNode findLocalFieldRefNode( Object baseValue, PaddleField field ) {
	VarNode base = findLocalVarNode( baseValue );
	if( base == null ) return null;
	return base.dot( field );
    }
    /** Finds the FieldRefNode for base variable value and field
     * field, or returns null. */
    public FieldRefNode findGlobalFieldRefNode( Object baseValue, PaddleField field ) {
	VarNode base = findGlobalVarNode( baseValue );
	if( base == null ) return null;
	return base.dot( field );
    }
    /** Finds or creates the FieldRefNode for base variable baseValue and field
     * field, of type type. */
    public FieldRefNode makeLocalFieldRefNode( Object baseValue, Type baseType,
	    PaddleField field, SootMethod method ) {
	VarNode base = makeLocalVarNode( baseValue, baseType, method );
        return makeFieldRefNode( base, field );
    }
    /** Finds or creates the FieldRefNode for base variable baseValue and field
     * field, of type type. */
    public FieldRefNode makeGlobalFieldRefNode( Object baseValue, Type baseType,
	    PaddleField field ) {
	VarNode base = makeGlobalVarNode( baseValue, baseType );
        return makeFieldRefNode( base, field );
    }
    /** Finds or creates the FieldRefNode for base variable base and field
     * field, of type type. */
    public FieldRefNode makeFieldRefNode( VarNode base, PaddleField field ) {
	FieldRefNode ret = base.dot( field );
	if( ret == null ) {
	    ret = new FieldRefNode( base, field );
	    if( base instanceof LocalVarNode ) {
	    	addNodeTag( ret, ((LocalVarNode) base).getMethod() );
	    } else {
	    	addNodeTag( ret, null );
	    }
	}
	return ret;
    }
    /** Finds the AllocDotField for base AllocNode an and field
     * field, or returns null. */
    public AllocDotField findAllocDotField( AllocNode an, PaddleField field ) {
	return an.dot( field );
    }
    /** Finds or creates the AllocDotField for base variable baseValue and field
     * field, of type t. */
    public AllocDotField makeAllocDotField( AllocNode an, PaddleField field ) {
	AllocDotField ret = an.dot( field );
	if( ret == null ) {
	    ret = new AllocDotField( an, field );
	}
	return ret;
    }
    private Map valToLocalVarNode = new HashMap(1000);
    private Map valToGlobalVarNode = new HashMap(1000);
    private Map valToAllocNode = new HashMap(1000);
    private LargeNumberedMap localToNodeMap = new LargeNumberedMap( Scene.v().getLocalNumberer() );
    public int maxFinishNumber = 0;
}

