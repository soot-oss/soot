package soot;

import soot.*;
import java.util.*;

public interface LocalPrinter
{
    public void printLocalsInBody( Body body, java.io.PrintWriter out, boolean isPrecise);
}
