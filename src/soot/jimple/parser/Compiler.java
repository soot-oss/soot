package soot.jimple.parser;

import soot.jimple.parser.parser.*;
import soot.jimple.parser.lexer.*;
import soot.jimple.parser.node.*;
import soot.jimple.parser.analysis.*;
import java.io.*;

public class Compiler 
{
    public static void main(String args[]) 
    {
        try {
            Parser p =
                new Parser(
                new Lexer(
                new PushbackReader(
                new InputStreamReader(System.in), 65536)));

            Start tree = p.parse();

            tree.apply(new Walker());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    } // main
} // Compiler








