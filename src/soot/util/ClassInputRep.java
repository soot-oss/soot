package soot.util;

import java.io.*;
class ClassInputRep  implements SootInputRepresentation
{
	static ClassInputRep singleton = new ClassInputRep();
	private ClassInputRep(){}
	public static ClassInputRep v() { return singleton;}
	public InputStream createInputStream(InputStream is){ return new ClassInputStream(is);}
	public String getFileExtension(){return ".class";}
}
