
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;

public class Options extends java.lang.Object {
    // Declared in Options.jadd at line 20
static   class Option {
    public String name;
    public boolean hasValue;
    public boolean isCollection;
    public Option(String name, boolean hasValue, boolean isCollection) {
      this.name = name;
      this.hasValue = hasValue;
      this.isCollection = isCollection;
    }
  }

    // Declared in Options.jadd at line 30
private   Map options = new HashMap();

    // Declared in Options.jadd at line 31
private   Map optionDescriptions = new HashMap();

    // Declared in Options.jadd at line 33
private   HashSet files = new HashSet();

    // Declared in Options.jadd at line 34
public   Collection files() {
    return files;
  }

    // Declared in Options.jadd at line 38
public   void initOptions() {
    options = new HashMap();
    optionDescriptions = new HashMap();
    files = new HashSet();
  }

    // Declared in Options.jadd at line 44
public   void addKeyOption(String name) {
    if(optionDescriptions.containsKey(name))
      throw new Error("Command line definition error: option description for " + name + " is multiply declared");
    optionDescriptions.put(name, new Option(name, false, false));
  }

    // Declared in Options.jadd at line 50
public   void addKeyValueOption(String name) {
    if(optionDescriptions.containsKey(name))
      throw new Error("Command line definition error: option description for " + name + " is multiply declared");
    optionDescriptions.put(name, new Option(name, true, false));
  }

    // Declared in Options.jadd at line 56
public   void addKeyCollectionOption(String name) {
    if(optionDescriptions.containsKey(name))
      throw new Error("Command line definition error: option description for " + name + " is multiply declared");
    optionDescriptions.put(name, new Option(name, true, true));
  }

    // Declared in Options.jadd at line 62
public   void addOptionDescription(String name, boolean value) {
    if(optionDescriptions.containsKey(name))
      throw new Error("Command line definition error: option description for " + name + " is multiply declared");
    optionDescriptions.put(name, new Option(name, value, false));
  }

    // Declared in Options.jadd at line 67
public   void addOptionDescription(String name, boolean value, boolean isCollection) {
    if(optionDescriptions.containsKey(name))
      throw new Error("Command line definition error: option description for " + name + " is multiply declared");
    optionDescriptions.put(name, new Option(name, value, isCollection));
  }

    // Declared in Options.jadd at line 73
public   void addOptions(String[] args) {
    for(int i = 0; i < args.length; i++) {
      String arg = args[i];
      if(arg.startsWith("@")) {
        try {
          String fileName = arg.substring(1,arg.length());
          java.io.StreamTokenizer tokenizer = new java.io.StreamTokenizer(new java.io.FileReader(fileName));
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
        } catch (java.io.IOException e) {
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

    // Declared in Options.jadd at line 145
public   boolean hasOption(String name) {
    return options.containsKey(name);
  }

    // Declared in Options.jadd at line 148
public   void setOption(String name) {
    options.put(name, null);
  }

    // Declared in Options.jadd at line 151
public   boolean hasValueForOption(String name) {
    return options.containsKey(name) && options.get(name) != null;
  }

    // Declared in Options.jadd at line 154
public   String getValueForOption(String name) {
    if(!hasValueForOption(name))
      throw new Error("Command line argument error: key " + name + " does not have a value");
    return (String)options.get(name);
  }

    // Declared in Options.jadd at line 159
public   void setValueForOption(String value, String option) {
    options.put(option, value);
  }

    // Declared in Options.jadd at line 162
public   Collection getValueCollectionForOption(String name) {
    if(!hasValueForOption(name))
      throw new Error("Command line argument error: key " + name + " does not have a value");
    return (Collection)options.get(name);
  }

    // Declared in Options.jadd at line 168
public   boolean verbose() {
    return hasOption("-verbose");
  }


}
