package soot.jimple.parser;

import soot.baf.*;
import soot.*;
import soot.jimple.*;

import soot.jimple.parser.parser.*;
import soot.jimple.parser.lexer.*;
import soot.jimple.parser.node.*;
import soot.jimple.parser.analysis.*;

import java.io.*;
import java.util.*;


/* 
   Walks a jimple AST and extracts all the reference 
   types found within it.
*/
   
public class CstPoolExtractorWalker extends DepthFirstAdapter
{           
    private Set mRefTypes = null;
    
    public CstPoolExtractorWalker() 
    {	
    }
   
    public void inStart(Start node)
    {
	mRefTypes = new HashSet();
        defaultIn(node);
    }

    public void outAQuotedClassName(AQuotedClassName node)
    {
      if((node.getQuotedName().getText()).equals("double"))throw new RuntimeException(node.getQuotedName().getLine() + ":" + node.getQuotedName().getPos());
	mRefTypes.add(node.getQuotedName().getText());
    }
    public void outAIdentClassName(AIdentClassName node)
    {
            if((node.getIdentifier().getText()).equals("double"))throw new RuntimeException(node.getIdentifier().getLine() + ":" + node.getIdentifier().getPos());
	mRefTypes.add(node.getIdentifier().getText());
    }
    public void outAFullIdentClassName(AFullIdentClassName node)
    {
      if((node.getFullIdentifier().getText()).equals("double"))throw new RuntimeException(node.getFullIdentifier().getLine() + ":" + node.getFullIdentifier().getPos());
	mRefTypes.add(node.getFullIdentifier().getText());
    }


    public void outAQuotedNonvoidType(AQuotedNonvoidType node)
    {
            if((node.getQuotedName().getText()).equals("double"))throw new RuntimeException(node.getQuotedName().getLine() + ":" + node.getQuotedName().getPos());
	mRefTypes.add(node.getQuotedName().getText());
    }
   
    public void outAFullIdentNonvoidType(AFullIdentNonvoidType node)
    {
      if((node.getFullIdentifier().getText()).equals("double"))throw new RuntimeException(node.getFullIdentifier().getLine() + ":" + node.getFullIdentifier().getPos());
        mRefTypes.add(node.getFullIdentifier().getText());
    }    
    
    public void outAIdentNonvoidType(AIdentNonvoidType node)
    {
      if((node.getIdentifier().getText()).equals("double"))throw new RuntimeException(node.getIdentifier().getLine() + ":" + node.getIdentifier().getPos());
        mRefTypes.add(node.getIdentifier().getText());

    }
    
    public Set getCstPool()
    {
	if(mRefTypes == null)
	    throw new RuntimeException("no constant pool has been computed yet");
	return mRefTypes;
    }        
} 

















