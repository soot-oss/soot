package ca.mcgill.sable.soot.jimple.parser;

import ca.mcgill.sable.soot.jimple.parser.parser.*;
import ca.mcgill.sable.soot.jimple.parser.lexer.*;
import ca.mcgill.sable.soot.jimple.parser.node.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;
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
                new InputStreamReader(System.in), 1024)));

            Start tree = p.parse();

            tree.apply(new Walker());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    } // main
} // Compiler
