package soot.util;

import java.io.*;

class BafInputRep  implements SootInputRepresentation	
{
	static BafInputRep singleton = new BafInputRep();
	private BafInputRep(){}
	public static BafInputRep v() { return singleton;}
	public InputStream createInputStream(InputStream is){return null;}
    public String getFileExtension(){return ".baf";}
}
