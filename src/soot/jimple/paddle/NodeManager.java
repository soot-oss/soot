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
import soot.jimple.paddle.queue.*;
import soot.*;
import soot.util.*;
import java.util.*;
import soot.tagkit.*;

/** Class implementing builder parameters (this decides
 * what kinds of nodes should be built for each kind of Soot value).
 * @author Ondrej Lhotak
 */

public class NodeManager {
    public NodeManager( Qvar_method_type locals, Qvar_type globals, Qobj_method_type localallocs, Qobj_type globalallocs ) {
        this.locals = locals;
        this.globals = globals;
        this.localallocs = localallocs;
        this.globalallocs = globalallocs;
    }

    private Qvar_method_type locals;
    private Qvar_type globals;
    private Qobj_method_type localallocs;
    private Qobj_type globalallocs;

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
    public AllocNode makeGlobalAllocNode( Object newExpr, Type type ) {
        return makeGlobalAllocNode(newExpr, type, null);
    }
    public AllocNode makeGlobalAllocNode( Object newExpr, Type type, SootMethod m ) {
        if( PaddleScene.v().options().types_for_sites() || PaddleScene.v().options().vta() ) newExpr = type;
	AllocNode ret = (AllocNode) valToGlobalAllocNode.get( newExpr );
	if( ret == null ) {
	    valToGlobalAllocNode.put( newExpr, ret = new GlobalAllocNode( newExpr, type, m ) );
            globalallocs.add( ret, type );
            addNodeTag( ret, m );
	} else if( !( ret.getType().equals( type ) ) ) {
	    throw new RuntimeException( "NewExpr "+newExpr+" of type "+type+
		    " previously had type "+ret.getType() );
	}
	return ret;
    }
    public GlobalAllocNode findGlobalAllocNode( Object value ) {
	return (GlobalAllocNode) valToGlobalAllocNode.get( value );
    }
    public AllocNode makeStringConstantNode( String s ) {
        Type type = RefType.v( "java.lang.String" );
        if( PaddleScene.v().options().types_for_sites() || PaddleScene.v().options().vta() )
            return makeGlobalAllocNode( type, type, null );
        StringConstantNode ret = (StringConstantNode) valToGlobalAllocNode.get( s );
	if( ret == null ) {
	    valToGlobalAllocNode.put( s, ret = new StringConstantNode( s ) );
            globalallocs.add( ret, type );
            addNodeTag( ret, null );
	}
	return ret;
    }

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
                    ret = new GlobalVarNode( value, type ) );
            globals.add( ret, type );
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
                    ret = new LocalVarNode( value, type, method ) );
                locals.add( ret, method, type );
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
                    ret = new LocalVarNode( value, type, method ) );
            locals.add( ret, method, type );
            addNodeTag( ret, method );
        } else if( !( ret.getType().equals( type ) ) ) {
            throw new RuntimeException( "Value "+value+" of type "+type+
                    " previously had type "+ret.getType() );
        }
	return ret;
    }

    private Map contextMap = new HashMap(1000);

    public AllocDotField get( AllocNode var, PaddleField field ) {
        Cons c = new Cons(var, field);
        AllocDotField ret = (AllocDotField) contextMap.get(c);
        return ret;
    }
    public AllocDotField make( AllocNode var, PaddleField field ) {
        Cons c = new Cons(var, field);
        AllocDotField ret = (AllocDotField) contextMap.get(c);
        if( ret == null ) {
            contextMap.put( c, ret = new AllocDotField(var, field) );
        }
        return ret;
    }

    public FieldRefNode get( VarNode var, PaddleField field ) {
        Cons c = new Cons(var, field);
        FieldRefNode ret = (FieldRefNode) contextMap.get(c);
        return ret;
    }
    public FieldRefNode make( VarNode var, PaddleField field ) {
        Cons c = new Cons(var, field);
        FieldRefNode ret = (FieldRefNode) contextMap.get(c);
        if( ret == null ) {
            contextMap.put( c, ret = new FieldRefNode(var, field) );
        }
        return ret;
    }

    public ContextVarNode get( Context ctxt, VarNode var ) {
        Cons c = new Cons(ctxt, var);
        ContextVarNode ret = (ContextVarNode) contextMap.get(c);
        return ret;
    }
    public ContextVarNode make( Context ctxt, VarNode var ) {
        Cons c = new Cons(ctxt, var);
        ContextVarNode ret = (ContextVarNode) contextMap.get(c);
        if( ret == null ) {
            contextMap.put( c, ret = new ContextVarNode(ctxt, var) );
        }
        return ret;
    }

    public ContextFieldRefNode get( Context ctxt, FieldRefNode var ) {
        Cons c = new Cons(ctxt, var);
        ContextFieldRefNode ret = (ContextFieldRefNode) contextMap.get(c);
        return ret;
    }
    public ContextFieldRefNode make( Context ctxt, FieldRefNode var ) {
        Cons c = new Cons(ctxt, var);
        ContextFieldRefNode ret = (ContextFieldRefNode) contextMap.get(c);
        if( ret == null ) {
            contextMap.put( c, ret = new ContextFieldRefNode(ctxt, var) );
        }
        return ret;
    }

    public ContextAllocNode get( Context ctxt, AllocNode var ) {
        Cons c = new Cons(ctxt, var);
        ContextAllocNode ret = (ContextAllocNode) contextMap.get(c);
        return ret;
    }
    public ContextAllocNode make( Context ctxt, AllocNode var ) {
        Cons c = new Cons(ctxt, var);
        ContextAllocNode ret = (ContextAllocNode) contextMap.get(c);
        if( ret == null ) {
            contextMap.put( c, ret = new ContextAllocNode(ctxt, var) );
        }
        return ret;
    }

    public ContextAllocDotField get( Context ctxt, AllocDotField var ) {
        Cons c = new Cons(ctxt, var);
        ContextAllocDotField ret = (ContextAllocDotField) contextMap.get(c);
        return ret;
    }
    public ContextAllocDotField make( Context ctxt, AllocDotField var ) {
        Cons c = new Cons(ctxt, var);
        ContextAllocDotField ret = (ContextAllocDotField) contextMap.get(c);
        if( ret == null ) {
            contextMap.put( c, ret = new ContextAllocDotField(ctxt, var) );
        }
        return ret;
    }

    private Map valToLocalVarNode = new HashMap(1000);
    private Map valToGlobalVarNode = new HashMap(1000);
    private Map valToGlobalAllocNode = new HashMap(1000);
    private LargeNumberedMap localToNodeMap = new LargeNumberedMap( Scene.v().getLocalNumberer() );
    public int maxFinishNumber = 0;

}

