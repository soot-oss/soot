/* Soot - a J*va Optimization Framework
 * Copyright (C) 2010 Hela Oueslati
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

package soot;

import java.io.PrintWriter;

import soot.dava.internal.AST.ASTTryNode.container;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;
import soot.util.Chain;

public class TemplatePrinter {
    
	private PrintWriter out;
	private int indentationLevel = 0;

	public TemplatePrinter(Singletons.Global g) {
    }
	
    public static TemplatePrinter v() {
        return G.v().soot_TemplatePrinter();
    }

    //see also class soot.Printer!
	public void printTo(SootClass c, PrintWriter out) {
		this.out = out;
		
		printTo(c);
	}

	private void printTo(SootClass c) {
		String templateClassName = c.getName().replace('.', '_')+"_Maker";
		
		//imports
		println("import soot.*;");
		println("import soot.jimple.*;");
		println("import soot.util.*;");
		println("");
		
		//open class
		print("public class ");
		print(templateClassName);
		println(" {");

		//open main method
		indent();
		println("public void create() {");
		indent();
		
		println("SootClass c = new SootClass(\""+c.getName()+"\");");
		println("c.setApplicationClass();");
		//todo modifiers, extends etc.
		println("Scene.v().addClass(c);");
		
		for(int i=0; i<c.getMethodCount(); i++) {
			println("createMethod"+i+"(c);");
		}
		
		//close main method
		closeMethod();
		
		int i = 0;
		for(SootMethod m: c.getMethods()) {
			
			newMethod("createMethod"+i);
			
			//TODO modifiers, types
			println("SootMethod m = new SootMethod(\""+m.getName()+"\",null,null);");
			println("Body b = Jimple.v().newBody(m);");
			println("m.setActiveBody(b);");
			
			if(!m.hasActiveBody()) continue;
			
			Body b = m.getActiveBody();
			
			println("Chain<Local> locals = b.getLocals();");
			for(Local l: b.getLocals()) {
				
				//TODO properly treat primitive types
				println("locals.add(Jimple.v().newLocal(\""+l.getName()+"\", RefType.v(\""+l.getType()+"\")));");
				
			}
			
			println("Chain<Unit> units = b.getUnits();");
			StmtTemplatePrinter sw = new StmtTemplatePrinter(this);
			for(Unit u: b.getUnits()) {
				
				u.apply(sw);
				
			}
			
			closeMethod();
			
			i++;
		}
	
		//close class
		println("}");
	}

	private void closeMethod() {
		unindent();
		println("}");
		unindent();
		println("");
	}

	private void newMethod(String name) {
		indent();
		println("public void "+name+"(SootClass c) {");
		indent();
	}
	
	public void println(String s) {
		print(s); print("\n");
	}

	public void print(String s) {
		for(int i=0; i<indentationLevel; i++) {
			out.print("  ");
		}
		out.print(s);
	}
	
	public void indent() {
		indentationLevel++;
	}

	public void unindent() {
		indentationLevel--;
	}
}
