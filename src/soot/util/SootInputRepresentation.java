package soot.util;

import java.io.*;

interface SootInputRepresentation 
{
    InputStream createInputStream(InputStream is);
    String getFileExtension();
	
}
