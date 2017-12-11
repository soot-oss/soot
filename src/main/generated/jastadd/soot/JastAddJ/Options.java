package soot.JastAddJ;

import java.util.HashSet;
import java.io.File;
import java.util.*;
import beaver.*;
import java.util.ArrayList;
import java.util.zip.*;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.Collection;
import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.coffi.ClassFile;
import soot.coffi.method_info;
import soot.coffi.CONSTANT_Utf8_info;
import soot.tagkit.SourceFileTag;
import soot.coffi.CoffiMethodSource;
/**
  * @ast class
 * 
 */
public class Options extends java.lang.Object {
static  class Option {
    public String name;
    public boolean hasValue;
    public boolean isCollection;
    public Option(String name, boolean hasValue, boolean isCollection) {
      this.name = name;
      this.hasValue = hasValue;
      this.isCollection = isCollection;
    }
  }

private  Map options = new HashMap();

private  Map optionDescriptions = new HashMap();

private  HashSet files = new HashSet();

public  Collection files() {
    return files;
  }

public  void initOptions() {
    options = new HashMap();
    optionDescriptions = new HashMap();
    files = new HashSet();
  }

public  void addKeyOption(String name) {
    if(optionDescriptions.containsKey(name))
      throw new Error("Command line definition error: option description for " + name + " is multiply declared");
    optionDescriptions.put(name, new Option(name, false, false));
  }

public  void addKeyValueOption(String name) {
    if(optionDescriptions.containsKey(name))
      throw new Error("Command line definition error: option description for " + name + " is multiply declared");
    optionDescriptions.put(name, new Option(name, true, false));
  }

public  void addKeyCollectionOption(String name) {
    if(optionDescriptions.containsKey(name))
      throw new Error("Command line definition error: option description for " + name + " is multiply declared");
    optionDescriptions.put(name, new Option(name, true, true));
  }

public  void addOptionDescription(String name, boolean value) {
    if(optionDescriptions.containsKey(name))
      throw new Error("Command line definition error: option description for " + name + " is multiply declared");
    optionDescriptions.put(name, new Option(name, value, false));
  }

public  void addOptionDescription(String name, boolean value, boolean isCollection) {
    if(optionDescriptions.containsKey(name))
      throw new Error("Command line definition error: option description for " + name + " is multiply declared");
    optionDescriptions.put(name, new Option(name, value, isCollection));
  }

public  void addOptions(String[] args) {
    for(int i = 0; i < args.length; i++) {
      String arg = args[i];
      if(arg.startsWith("@")) {
        try {
          String fileName = arg.substring(1,arg.length());
          java.io.FileReader r = new java.io.FileReader(fileName);
		  java.io.StreamTokenizer tokenizer = new java.io.StreamTokenizer(r);
          tokenizer.resetSyntax();
          tokenizer.whitespaceChars(' ',' ');
          tokenizer.whitespaceChars('\t','\t');
          tokenizer.whitespaceChars('\f','\f');
          tokenizer.whitespaceChars('\n','\n');
          tokenizer.whitespaceChars('\r','\r');
          tokenizer.wordChars(33,255);
          ArrayList list = new ArrayList();
          int next = tokenizer.nextToken();
          while(next != java.io.StreamTokenizer.TT_EOF) {
            if(next == java.io.StreamTokenizer.TT_WORD) {
              list.add(tokenizer.sval);
            }
            next = tokenizer.nextToken();
          }
          String[] newArgs = new String[list.size()];
          int index = 0;
          for(Iterator iter = list.iterator(); iter.hasNext(); index++) {
            newArgs[index] = (String)iter.next();
          }
          addOptions(newArgs);
          r.close();
        } catch (java.io.FileNotFoundException e) {
          System.err.println("File not found: "+arg.substring(1));
        } catch (java.io.IOException e) {
          System.err.println("Exception: "+e.getMessage());
        }
      }
      else if(arg.startsWith("-")) {
        if(!optionDescriptions.containsKey(arg))
          throw new Error("Command line argument error: option " + arg + " is not defined");
        Option o = (Option)optionDescriptions.get(arg);
        
        if(!o.isCollection && options.containsKey(arg))
          throw new Error("Command line argument error: option " + arg + " is multiply defined");
        
        if(o.hasValue && !o.isCollection) {
          String value = null;
          if(i + 1 > args.length - 1)
            throw new Error("Command line argument error: value missing for key " + arg);
          value = args[i+1];
          if(value.startsWith("-"))
            throw new Error("Command line argument error: value missing for key " + arg);
          i++;
          options.put(arg, value);
        }
        else if(o.hasValue && o.isCollection) {
          String value = null;
          if(i + 1 > args.length - 1)
            throw new Error("Command line argument error: value missing for key " + arg);
          value = args[i+1];
          if(value.startsWith("-"))
            throw new Error("Command line argument error: value missing for key " + arg);
          i++;
          Collection c = (Collection)options.get(arg);
          if(c == null)
            c = new ArrayList();
          c.add(value);
          options.put(arg, c);
        }
        else {
          options.put(arg, null);
        }
      }
      else {
        files.add(arg);
      }
    }
  }

public  boolean hasOption(String name) {
    return options.containsKey(name);
  }

public  void setOption(String name) {
    options.put(name, null);
  }

public  boolean hasValueForOption(String name) {
    return options.containsKey(name) && options.get(name) != null;
  }

public  String getValueForOption(String name) {
    if(!hasValueForOption(name))
      throw new Error("Command line argument error: key " + name + " does not have a value");
    return (String)options.get(name);
  }

public  void setValueForOption(String value, String option) {
    options.put(option, value);
  }

public  Collection getValueCollectionForOption(String name) {
    if(!hasValueForOption(name))
      throw new Error("Command line argument error: key " + name + " does not have a value");
    return (Collection)options.get(name);
  }

public  boolean verbose() {
    return hasOption("-verbose");
  }


}
