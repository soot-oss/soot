package soot.util;

import java.io.*;

public class EscapedPrintWriter extends PrintWriter
{
    public EscapedPrintWriter(FileOutputStream fos)
    {
        super(fos);
    }

    public void write(String out)
    {
        super.write(StringTools.getEscapedStringOf(out));
    }
}
