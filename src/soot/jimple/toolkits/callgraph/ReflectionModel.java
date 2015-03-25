package soot.jimple.toolkits.callgraph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.SootMethod;
import soot.jimple.Stmt;

public interface ReflectionModel {

	void methodInvoke(SootMethod container, Stmt invokeStmt);

	void classNewInstance(SootMethod source, Stmt s);

	void contructorNewInstance(SootMethod source, Stmt s);

	void classForName(SootMethod source, Stmt s);

}
