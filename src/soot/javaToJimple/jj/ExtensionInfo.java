package soot.javaToJimple.jj;

import polyglot.lex.Lexer;
import soot.javaToJimple.jj.parse.Lexer_c;
import soot.javaToJimple.jj.parse.Grm;
import soot.javaToJimple.jj.ast.*;
import soot.javaToJimple.jj.types.*;

import polyglot.ast.*;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.frontend.*;
import polyglot.main.*;

import java.util.*;
import java.io.*;

/**
 * Extension information for jj extension.
 */
public class ExtensionInfo extends polyglot.ext.jl.ExtensionInfo {
    static {
        // force Topics to load
        Topics t = new Topics();
    }

    public String defaultFileExtension() {
        return "jj";
    }

    public String compilerName() {
        return "jjc";
    }

    public Parser parser(Reader reader, FileSource source, ErrorQueue eq) {
        Lexer lexer = new Lexer_c(reader, source.name(), eq);
        Grm grm = new Grm(lexer, ts, nf, eq);
        return new CupParser(grm, source, eq);
    }

    protected NodeFactory createNodeFactory() {
        return new JjNodeFactory_c();
    }

    protected TypeSystem createTypeSystem() {
        return new JjTypeSystem_c();
    }

    public List passes(Job job) {
        List passes = super.passes(job);
        // TODO: add passes as needed by your compiler
        return passes;
    }

    private HashMap sourceJobMap;

    public HashMap sourceJobMap(){
        return sourceJobMap;
    }

    public void sourceJobMap(HashMap map){
        sourceJobMap = map;
    }
}
