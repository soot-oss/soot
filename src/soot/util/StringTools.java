package soot.util;

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

        cr = lineSeparator.charAt(0);
        lf = -1;

        if (lineSeparator.length() == 2)
            lf = lineSeparator.charAt(1);

        for (int i = 0; i < fromStringArray.length; i++)
        {
            ch = (int) fromStringArray[i];
            if (ch >= 32 && ch <= 126 || ch == cr || ch == lf)
            {
                whole.append((char) ch);
 
                continue;
            }
            
            mini.setLength(0);
            mini.append(Integer.toHexString(ch));

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
            char ch = fromStringArray[i];
            if (ch == '\\')
                { toStringBuffer.append("\\\\"); continue; }

            if (ch == '\'')
                { toStringBuffer.append("\\\'"); continue; }

            if (ch == '\"')
                { toStringBuffer.append("\\\""); continue; }

            toStringBuffer.append(ch);
        }

        toStringBuffer.append("\"");
        return toStringBuffer.toString();
    }
}
