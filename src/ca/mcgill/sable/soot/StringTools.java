package ca.mcgill.sable.soot;

public class StringTools
{
    public static String lineSeparator = System.getProperty("line.separator");;
    static StringBuffer whole = new StringBuffer();
    static StringBuffer mini = new StringBuffer();

    public static java.lang.String getEscapedStringOf(String fromString)
    {
        char[] fromStringArray;
        int cr, lf, ch;

	whole.setLength(0);
	mini.setLength(0);

        fromStringArray = fromString.toCharArray();

        cr = StringTools.lineSeparator.charAt(0);
        lf = -1;

        if (lineSeparator.length() == 2)
	    lf = ca.mcgill.sable.soot.StringTools.lineSeparator.charAt(1);

	for (int i = 0; i < fromStringArray.length; i++)
	{
	    ch = fromStringArray[i];
	    if (ch >= 32 && ch <= 126 || ch == cr || ch == lf)
	    {
		ca.mcgill.sable.soot.StringTools.whole.append((char) ch);
		continue;
	    }
	    
	    mini.setLength(0);
	    mini.append(java.lang.Integer.toHexString(ch));

	    while (mini.length() < 4)
		mini.insert(0, "0");

	    mini.insert(0, "\\u");
	    whole.append(mini.toString());
	}

        return whole.toString();
    }

    public static java.lang.String getQuotedStringOf(String fromString)
    {
        StringBuffer toStringBuffer;
        char[] fromStringArray;

        toStringBuffer = new java.lang.StringBuffer();
        fromStringArray = fromString.toCharArray();

	toStringBuffer.append("\"");

	for (int i = 0; i < fromStringArray.length; i++)
	{
	    int ch = fromStringArray[i];
	    if (ch == 92)
		{ toStringBuffer.append("\\\\"); continue; }

	    if (ch == 39)
		{ toStringBuffer.append("\\\'"); continue; }

	    if (ch == 34)
		{ toStringBuffer.append("\\\""); continue; }

	    toStringBuffer.append(ch);
	}

        toStringBuffer.append("\"");
        return toStringBuffer.toString();
    }
}
