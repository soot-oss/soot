package soot.jimple.parser;

import soot.jimple.parser.parser.*;
import soot.jimple.parser.lexer.*;
import soot.jimple.parser.node.*;
import soot.jimple.parser.analysis.*;

import java.io.*;

class Walker extends DepthFirstAdapter 
{
    static int stmtCount = 0;

    public void inAFile(AFile node)
    {
        System.out.println("reading class " + node.getName());
    } // inAFile

    public void inAMethodMember(AMethodMember node)
    {
        System.out.println("  reading method " + node.getName());
        stmtCount = 0;
    } // inAMethodMember

    public void outAMethodMember(AMethodMember node)
    {
        // System.out.println("    found " + stmtCount +
        //                    " assignment statements");
    } // outAMethodMember

    public void inAAssignStatement(AAssignStatement node)
    {
        // stmtCount++;
    } // inAAssignStatement
} // Walker
