package ca.mcgill.sable.soot;

import java.io.*;

class EscapedPrintWriter extends PrintWriter
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
