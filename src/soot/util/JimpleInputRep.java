package soot.util;

import java.io.*;

class JimpleInputRep  implements SootInputRepresentation
{
	static JimpleInputRep singleton = new JimpleInputRep();
	private JimpleInputRep(){}
	public static JimpleInputRep v() { return singleton;}
	public InputStream createInputStream(InputStream is){return new JimpleInputStream(is);}
	public String getFileExtension(){return ".jimple";}
}
