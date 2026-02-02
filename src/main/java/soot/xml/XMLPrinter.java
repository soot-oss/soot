package soot.xml;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 David Eng
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.G;
import soot.LabeledUnitPrinter;
import soot.Local;
import soot.Main;
import soot.Modifier;
import soot.NormalUnitPrinter;
import soot.Scene;
import soot.Singletons;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.ValueBox;
import soot.toolkits.graph.ExceptionalUnitGraphFactory;
import soot.toolkits.scalar.LiveLocals;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.util.Chain;

/** XML printing routines all XML output comes through here */
public class XMLPrinter {
  private static final Logger logger = LoggerFactory.getLogger(XMLPrinter.class);

  // xml and dtd header
  public static final String xmlHeader = "<?xml version=\"1.0\" ?>\n";
  public static final String dtdHeader = "<!DOCTYPE jil SYSTEM \"http://www.sable.mcgill.ca/~flynn/jil/jil10.dtd\">\n";

  // xml tree
  public XMLRoot root;

  public XMLPrinter(Singletons.Global g) {
  }

  public static XMLPrinter v() {
    return G.v().soot_xml_XMLPrinter();
  }

  // returns the buffer - this is the XML output
  @Override
  public String toString() {
    if (root != null) {
      return root.toString();
    } else {
      throw new RuntimeException("Error generating XML!");
    }
  }

  // add single element <...>...</...>
  public XMLNode addElement(String name) {
    return addElement(name, "", "", "");
  }

  public XMLNode addElement(String name, String value) {
    return addElement(name, value, "", "");
  }

  public XMLNode addElement(String name, String value, String[] attributes) {
    return addElement(name, value, attributes, null);
  }

  public XMLNode addElement(String name, String value, String attribute, String attributeValue) {
    return addElement(name, value, new String[] { attribute }, new String[] { attributeValue });
  }

  public XMLNode addElement(String name, String value, String[] attributes, String[] values) {
    return root.addElement(name, value, attributes, values);
  }

  /**
   * Prints out an XML representation of the given SootClass with each method Body printed in the textual format
   * corresponding to the IR used to encode the Body.
   *
   * @param cl
   * @param out
   *          a PrintWriter instance to print to.
   */
  public void printJimpleStyleTo(SootClass cl, PrintWriter out) {
    this.root = new XMLRoot();

    final Scene sc = Scene.v();
    final XMLNode xmlClassNode;

    // Print XML class output
    {
      // add header nodes
      XMLNode xmlRootNode = root.addElement("jil");

      // add history node
      // TODO: grab the software version and command line
      StringBuilder cmdlineStr = new StringBuilder();
      for (String element : Main.v().cmdLineArgs) {
        cmdlineStr.append(element).append(' ');
      }
      String dateStr = new Date().toString();
      XMLNode xmlHistoryNode = xmlRootNode.addChild("history");
      xmlHistoryNode.addAttribute("created", dateStr);
      xmlHistoryNode.addChild("soot", new String[] { "version", "command", "timestamp" },
          new String[] { Main.versionString, cmdlineStr.toString().trim(), dateStr });

      // add class root node
      xmlClassNode = xmlRootNode.addChild("class", new String[] { "name" }, new String[] { sc.quotedNameOf(cl.getName()) });
      if (!cl.getPackageName().isEmpty()) {
        xmlClassNode.addAttribute("package", cl.getPackageName());
      }
      if (cl.hasSuperclass()) {
        xmlClassNode.addAttribute("extends", sc.quotedNameOf(cl.getSuperclass().getName()));
      }

      // add modifiers subnode
      XMLNode xmlTempNode = xmlClassNode.addChild("modifiers");
      for (StringTokenizer st = new StringTokenizer(Modifier.toString(cl.getModifiers())); st.hasMoreTokens();) {
        xmlTempNode.addChild("modifier", new String[] { "name" }, new String[] { st.nextToken() });
      }
      xmlTempNode.addAttribute("count", String.valueOf(xmlTempNode.getNumberOfChildren()));
    }

    // Print interfaces
    {
      XMLNode xmlTempNode = xmlClassNode.addChild("interfaces", "", new String[] { "count" },
          new String[] { String.valueOf(cl.getInterfaceCount()) });
      for (SootClass next : cl.getInterfaces()) {
        xmlTempNode.addChild("implements", "", new String[] { "class" }, new String[] { sc.quotedNameOf(next.getName()) });
      }
    }

    // Print fields
    {
      XMLNode xmlTempNode = xmlClassNode.addChild("fields", "", new String[] { "count" },
          new String[] { String.valueOf(cl.getFieldCount()) });

      int i = 0;
      for (SootField f : cl.getFields()) {
        if (!f.isPhantom()) {
          // add the field node
          XMLNode xmlFieldNode = xmlTempNode.addChild("field", "", new String[] { "id", "name", "type" },
              new String[] { String.valueOf(i++), f.getName(), f.getType().toString() });

          XMLNode xmlModifiersNode = xmlFieldNode.addChild("modifiers");
          for (StringTokenizer st = new StringTokenizer(Modifier.toString(f.getModifiers())); st.hasMoreTokens();) {
            xmlModifiersNode.addChild("modifier", new String[] { "name" }, new String[] { st.nextToken() });
          }
          xmlModifiersNode.addAttribute("count", String.valueOf(xmlModifiersNode.getNumberOfChildren()));
        }
      }
    }

    // Print methods
    {
      XMLNode methodsNode
          = xmlClassNode.addChild("methods", new String[] { "count" }, new String[] { String.valueOf(cl.getMethodCount()) });

      for (Iterator<SootMethod> methodIt = cl.methodIterator(); methodIt.hasNext();) {
        SootMethod method = methodIt.next();
        if (!method.isPhantom()) {
          if (!Modifier.isAbstract(method.getModifiers()) && !Modifier.isNative(method.getModifiers())) {
            if (!method.hasActiveBody()) {
              throw new RuntimeException("method " + method.getName() + " has no active body!");
            } else {
              Body body = method.getActiveBody();
              body.validate();
              printStatementsInBody(body, methodsNode);
            }
          }
        }
      }
    }

    out.println(toString());
  }

  private void printStatementsInBody(Body body, XMLNode methodsNode) {
    final LabeledUnitPrinter up = new NormalUnitPrinter(body);
    final Map<Unit, String> stmtToName = up.labels();
    final Chain<Unit> units = body.getUnits();

    String currentLabel = "default";
    long labelCount = 0;

    /*
     * // for invokes, add a list of potential targets if (!Scene.v().hasActiveInvokeGraph()) {
     * InvokeGraphBuilder.v().transform("jil.igb"); }
     *
     * // build an invoke graph based on class hiearchy analysis InvokeGraph igCHA = Scene.v().getActiveInvokeGraph();
     *
     * // build an invoke graph based on variable type analysis InvokeGraph igVTA = Scene.v().getActiveInvokeGraph(); try {
     * VariableTypeAnalysis vta = null; int VTApasses = 1; //Options.getInt( PackManager.v().getPhaseOptions( "jil.igb" ),
     * "VTA-passes" ); for (int i = 0; i < VTApasses; i++) { vta = new VariableTypeAnalysis(igVTA);
     * vta.trimActiveInvokeGraph(); igVTA.refreshReachableMethods(); } } catch (RuntimeException re) { // this will fail if
     * the --analyze-context flag is not specified // logger.debug(""+ "JIL VTA FAILED: " + re ); igVTA = null; }
     */

    final String cleanMethodName = cleanMethod(body.getMethod().getName());

    // add method node
    XMLNode methodNode = methodsNode.addChild("method", new String[] { "name", "returntype", "class" }, new String[] {
        cleanMethodName, body.getMethod().getReturnType().toString(), body.getMethod().getDeclaringClass().getName() });
    String declarationStr = body.getMethod().getDeclaration().trim();
    methodNode.addChild("declaration", toCDATA(declarationStr), new String[] { "length" },
        new String[] { String.valueOf(declarationStr.length()) });

    // create references to parameters, locals, labels, stmts nodes
    XMLNode parametersNode = methodNode.addChild("parameters", new String[] { "method" }, new String[] { cleanMethodName });
    XMLNode localsNode = methodNode.addChild("locals");
    XMLNode labelsNode = methodNode.addChild("labels");
    XMLNode stmtsNode = methodNode.addChild("statements");

    // create default label
    XMLLabel xmlLabel = new XMLLabel(labelCount, cleanMethodName, currentLabel);
    labelsNode.addChild("label", new String[] { "id", "name", "method" },
        new String[] { String.valueOf(labelCount++), currentLabel, cleanMethodName });

    // include any analysis which will be used in the xml output
    final LiveLocals sll = new SimpleLiveLocals(ExceptionalUnitGraphFactory.createExceptionalUnitGraph(body));

    // lists
    ArrayList<String> useList = new ArrayList<String>();
    ArrayList<ArrayList<Long>> useDataList = new ArrayList<ArrayList<Long>>();
    ArrayList<String> defList = new ArrayList<String>();
    ArrayList<ArrayList<Long>> defDataList = new ArrayList<ArrayList<Long>>();
    ArrayList<ArrayList<String>> paramData = new ArrayList<ArrayList<String>>();
    ArrayList<XMLLabel> xmlLabelsList = new ArrayList<XMLLabel>();
    long statementCount = 0;
    long maxStmtCount = 0;
    long labelID = 0;

    // for each statement...
    for (Unit currentStmt : units) {
      // new label
      if (stmtToName.containsKey(currentStmt)) {
        currentLabel = stmtToName.get(currentStmt);

        // fill in the stmt count for the previous label
        // index = xmlLabels.indexOf( "%s" );
        // if( index != -1 )
        // xmlLabels = xmlLabels.substring( 0, index ) + ( labelID ) + xmlLabels.substring( index + 2 );
        // index = xmlLabels.indexOf( "%d" );
        // if( index != -1 )
        // xmlLabels = xmlLabels.substring( 0, index ) + new Float( ( new Float( labelID ).floatValue() / new Float(
        // units.size() ).intValue() ) *
        // 100.0 ).intValue() + xmlLabels.substring( index + 2 );

        xmlLabel.stmtCount = labelID;
        xmlLabel.stmtPercentage = (long) (((float) labelID) / ((float) units.size()) * 100f);
        if (xmlLabel.stmtPercentage > maxStmtCount) {
          maxStmtCount = xmlLabel.stmtPercentage;
        }

        xmlLabelsList.add(xmlLabel);
        // xmlLabel.clear();

        xmlLabel = new XMLLabel(labelCount, cleanMethodName, currentLabel);
        labelsNode.addChild("label", new String[] { "id", "name", "method" },
            new String[] { String.valueOf(labelCount), currentLabel, cleanMethodName });
        labelCount++;
        labelID = 0;
      }

      // examine each statement
      XMLNode stmtNode = stmtsNode.addChild("statement", new String[] { "id", "label", "method", "labelid" },
          new String[] { String.valueOf(statementCount), currentLabel, cleanMethodName, String.valueOf(labelID) });
      XMLNode sootstmtNode = stmtNode.addChild("soot_statement", new String[] { "branches", "fallsthrough" },
          new String[] { boolToString(currentStmt.branches()), boolToString(currentStmt.fallsThrough()) });

      // uses for each statement
      int j = 0;
      for (ValueBox box : currentStmt.getUseBoxes()) {
        if (box.getValue() instanceof Local) {
          String local = cleanLocal(box.getValue().toString());
          sootstmtNode.addChild("uses", new String[] { "id", "local", "method" },
              new String[] { String.valueOf(j), local, cleanMethodName });
          j++;

          ArrayList<Long> tempArrayList = null;
          int useIndex = useList.indexOf(local);
          if (useIndex == -1) {
            useDataList.add(tempArrayList);
            useIndex = useList.size();
            useList.add(local);
          }

          if (useDataList.size() > useIndex) {
            tempArrayList = useDataList.get(useIndex);
            if (tempArrayList == null) {
              tempArrayList = new ArrayList<Long>();
            }
            tempArrayList.add(statementCount);
            useDataList.set(useIndex, tempArrayList);
          }
        }
      }

      // defines for each statement
      j = 0;
      for (ValueBox box : currentStmt.getDefBoxes()) {
        if (box.getValue() instanceof Local) {
          String local = cleanLocal(box.getValue().toString());
          sootstmtNode.addChild("defines", new String[] { "id", "local", "method" },
              new String[] { String.valueOf(j), local, cleanMethodName });
          j++;

          ArrayList<Long> tempArrayList = null;
          int defIndex = defList.indexOf(local);
          if (defIndex == -1) {
            defDataList.add(tempArrayList);
            defIndex = defList.size();
            defList.add(local);
          }

          if (defDataList.size() > defIndex) {
            tempArrayList = defDataList.get(defIndex);
            if (tempArrayList == null) {
              tempArrayList = new ArrayList<Long>();
            }
            tempArrayList.add(statementCount);
            defDataList.set(defIndex, tempArrayList);
          }
        }
      }

      /*
       * // for invokes, add a list of potential targets if (stmtCurrentStmt.containsInvokeExpr()) { // default analysis is
       * CHA if (igCHA != null) { try { List targets = igCHA.getTargetsOf(stmtCurrentStmt); XMLNode CHAinvoketargetsNode =
       * sootstmtNode.addChild( "invoketargets", new String[] { "analysis", "count" }, new String[] { "CHA", targets.size() +
       * "" }); for (int i = 0; i < targets.size(); i++) { SootMethod meth = (SootMethod) targets.get(i);
       * CHAinvoketargetsNode.addChild( "target", new String[] { "id", "class", "method" }, new String[] { String.valueOf(i),
       * meth.getDeclaringClass().getFullName(), cleanMethod(meth.getName())}); } } catch (RuntimeException re) {
       * //logger.debug(""+ "XML: " + re + " (" + stmtCurrentStmt + ")" ); } }
       *
       * // now try VTA, which will only work if the -a or --analyze-context switch is specified if (igVTA != null) {
       * InvokeExpr ie = (InvokeExpr) stmtCurrentStmt.getInvokeExpr(); if (!(ie instanceof StaticInvokeExpr) && !(ie
       * instanceof SpecialInvokeExpr)) { try { List targets = igVTA.getTargetsOf(stmtCurrentStmt); XMLNode
       * VTAinvoketargetsNode = sootstmtNode.addChild( "invoketargets", new String[] { "analysis", "count" }, new String[] {
       * "VTA", String.valueOf(targets.size()) }); for (int i = 0; i < targets.size(); i++) { SootMethod meth = (SootMethod)
       * targets.get(i); VTAinvoketargetsNode.addChild( "target", new String[] { "id", "class", "method" }, new String[] { i
       * + "", meth.getDeclaringClass().getFullName(), cleanMethod(meth.getName())}); } } catch (RuntimeException re) {
       * //logger.debug(""+ "XML: " + re + " (" + stmtCurrentStmt + ")" ); } } } }
       */

      // simple live locals
      {
        List<Local> liveLocalsIn = sll.getLiveLocalsBefore(currentStmt);
        List<Local> liveLocalsOut = sll.getLiveLocalsAfter(currentStmt);
        XMLNode livevarsNode = sootstmtNode.addChild("livevariables", new String[] { "incount", "outcount" },
            new String[] { String.valueOf(liveLocalsIn.size()), String.valueOf(liveLocalsOut.size()) });
        for (ListIterator<Local> it = liveLocalsIn.listIterator(); it.hasNext();) {
          int i = it.nextIndex();// index must be retrieved before 'next()'
          Local val = it.next();
          livevarsNode.addChild("in", new String[] { "id", "local", "method" },
              new String[] { String.valueOf(i), cleanLocal(val.toString()), cleanMethodName });
        }
        for (ListIterator<Local> it = liveLocalsOut.listIterator(); it.hasNext();) {
          int i = it.nextIndex();// index must be retrieved before 'next()'
          Local val = it.next();
          livevarsNode.addChild("out", new String[] { "id", "local", "method" },
              new String[] { String.valueOf(i), cleanLocal(val.toString()), cleanMethodName });
        }
      }

      // parameters
      for (int i = 0, e = body.getMethod().getParameterCount(); i < e; i++) {
        paramData.add(new ArrayList<String>());
      }

      // parse any info from the statement code
      currentStmt.toString(up);
      String jimpleStr = up.toString().trim();
      if (currentStmt instanceof soot.jimple.IdentityStmt && jimpleStr.contains("@parameter")) {
        // this line is a use of a parameter
        String tempStr = jimpleStr.substring(jimpleStr.indexOf("@parameter") + 10);
        int idx = tempStr.indexOf(':');
        if (idx != -1) {
          tempStr = tempStr.substring(0, idx).trim();
        }
        idx = tempStr.indexOf(' ');
        if (idx != -1) {
          tempStr = tempStr.substring(0, idx).trim();
        }
        int paramIndex = Integer.valueOf(tempStr);
        ArrayList<String> tempVec = paramData.get(paramIndex);
        if (tempVec != null) {
          tempVec.add(Long.toString(statementCount));
        }
        paramData.set(paramIndex, tempVec);
      }

      // add plain jimple representation of each statement
      sootstmtNode.addChild("jimple", toCDATA(jimpleStr), new String[] { "length" },
          new String[] { String.valueOf(jimpleStr.length() + 1) });

      // increment statement counters
      labelID++;
      statementCount++;
    }

    // add count to statments
    stmtsNode.addAttribute("count", String.valueOf(statementCount));

    // method parameters
    {
      List<Type> parameterTypes = body.getMethod().getParameterTypes();
      parametersNode.addAttribute("count", String.valueOf(parameterTypes.size()));
      for (ListIterator<Type> it = parameterTypes.listIterator(); it.hasNext();) {
        int i = it.nextIndex();// index must be retrieved before 'next()'
        Type val = it.next();
        XMLNode paramNode = parametersNode.addChild("parameter", new String[] { "id", "type", "method", "name" },
            new String[] { String.valueOf(i), String.valueOf(val), cleanMethodName, "_parameter" + i });
        XMLNode sootparamNode = paramNode.addChild("soot_parameter");

        ArrayList<String> tempVec = paramData.get(i);
        for (ListIterator<String> itk = tempVec.listIterator(); itk.hasNext();) {
          int k = itk.nextIndex();// index must be retrieved before 'next()'
          String valk = itk.next();
          sootparamNode.addChild("use", new String[] { "id", "line", "method" },
              new String[] { String.valueOf(k), valk, cleanMethodName });
        }
        sootparamNode.addAttribute("uses", String.valueOf(tempVec.size()));
      }
    }
    /*
     * index = xmlLabels.indexOf( "%s" ); if( index != -1 ) xmlLabels = xmlLabels.substring( 0, index ) + ( labelID ) +
     * xmlLabels.substring( index + 2 ); index = xmlLabels.indexOf( "%d" ); if( index != -1 ) xmlLabels =
     * xmlLabels.substring( 0, index ) + new Float( ( new Float( labelID ).floatValue() / new Float( units.size()
     * ).floatValue() ) * 100.0 ).intValue() + xmlLabels.substring( index + 2 );
     */

    xmlLabel.stmtCount = labelID;
    xmlLabel.stmtPercentage = (long) (((float) labelID) / ((float) units.size()) * 100f);
    if (xmlLabel.stmtPercentage > maxStmtCount) {
      maxStmtCount = xmlLabel.stmtPercentage;
    }
    xmlLabelsList.add(xmlLabel);

    // print out locals
    {
      Collection<Local> locals = body.getLocals();
      ArrayList<String> localTypes = new ArrayList<String>();
      ArrayList<ArrayList<XMLNode>> typedLocals = new ArrayList<ArrayList<XMLNode>>();
      ArrayList<Integer> typeCounts = new ArrayList<Integer>();
      int j = 0;
      int currentType = 0;
      for (Local localData : locals) {
        int useCount = 0;
        int defineCount = 0;

        // collect the local types
        final String localType = localData.getType().toString();
        if (!localTypes.contains(localType)) {
          localTypes.add(localType);
          typedLocals.add(new ArrayList<XMLNode>());
          typeCounts.add(0);
        }

        final String local = cleanLocal(localData.toString());
        // create a reference to the local node
        XMLNode localNode = new XMLNode("local", "", new String[] { "id", "method", "name", "type" },
            new String[] { String.valueOf(j), cleanMethodName, local, localType });
        XMLNode sootlocalNode = localNode.addChild("soot_local");
        currentType = 0;
        for (ListIterator<String> it = localTypes.listIterator(); it.hasNext();) {
          String val = it.next();
          if (localType.equalsIgnoreCase(val)) {
            int k = it.previousIndex();
            currentType = k;
            typeCounts.set(k, typeCounts.get(k) + 1);
            break;
          }
        }

        // add all uses to this local
        for (String nextUse : useList) {
          if (local.equalsIgnoreCase(nextUse)) {
            ArrayList<Long> tempArrayList = useDataList.get(useList.indexOf(local));
            useCount = tempArrayList.size();
            for (ListIterator<Long> it = tempArrayList.listIterator(); it.hasNext();) {
              int i = it.nextIndex();// index must be retrieved before 'next()'
              Long val = it.next();
              sootlocalNode.addChild("use", new String[] { "id", "line", "method" },
                  new String[] { String.valueOf(i), String.valueOf(val), cleanMethodName });
            }
            break;
          }
        }

        // add all definitions to this local
        for (String nextDef : defList) {
          if (local.equalsIgnoreCase(nextDef)) {
            ArrayList<Long> tempArrayList = defDataList.get(defList.indexOf(local));
            defineCount = tempArrayList.size();
            for (ListIterator<Long> it = tempArrayList.listIterator(); it.hasNext();) {
              int i = it.nextIndex();// index must be retrieved before 'next()'
              Long val = it.next();
              sootlocalNode.addChild("definition", new String[] { "id", "line", "method" },
                  new String[] { String.valueOf(i), String.valueOf(val), cleanMethodName });
            }
            break;
          }
        }

        // add number of uses and defines to this local
        sootlocalNode.addAttribute("uses", String.valueOf(useCount));
        sootlocalNode.addAttribute("defines", String.valueOf(defineCount));

        // create a list of locals sorted by type
        ArrayList<XMLNode> list = typedLocals.get(currentType);
        list.add(localNode);
        typedLocals.set(currentType, list);

        // add local to locals node
        localsNode.addChild((XMLNode) localNode.clone());
        j++;
      }

      // add count to the locals node
      localsNode.addAttribute("count", String.valueOf(locals.size()));

      // add types node to locals node, and each type with each local per type
      XMLNode typesNode
          = localsNode.addChild("types", new String[] { "count" }, new String[] { String.valueOf(localTypes.size()) });

      for (ListIterator<String> it = localTypes.listIterator(); it.hasNext();) {
        int i = it.nextIndex();// index must be retrieved before 'next()'
        String val = it.next();
        XMLNode typeNode = typesNode.addChild("type", new String[] { "id", "type", "count" },
            new String[] { String.valueOf(i), val, String.valueOf(typeCounts.get(i)) });
        for (XMLNode n : typedLocals.get(i)) {
          typeNode.addChild(n);
        }
      }
    }

    // add count attribute to labels node, and stmtcount, and stmtpercentage attributes to each label node
    labelsNode.addAttribute("count", String.valueOf(labelCount));
    XMLNode current = labelsNode.child;
    for (XMLLabel tempLabel : xmlLabelsList) {
      tempLabel.stmtPercentage = (long) (((float) tempLabel.stmtPercentage) / ((float) maxStmtCount) * 100f);

      if (current != null) {
        current.addAttribute("stmtcount", String.valueOf(tempLabel.stmtCount));
        current.addAttribute("stmtpercentage", String.valueOf(tempLabel.stmtPercentage));
        current = current.next;
      }
    }

    // Print out exceptions
    {
      int j = 0;
      XMLNode exceptionsNode = methodNode.addChild("exceptions");
      for (Trap trap : body.getTraps()) {
        // catch java.io.IOException from label0 to label1 with label2;
        XMLNode catchNode = exceptionsNode.addChild("exception", new String[] { "id", "method", "type" },
            new String[] { String.valueOf(j++), cleanMethodName, Scene.v().quotedNameOf(trap.getException().getName()) });
        catchNode.addChild("begin", new String[] { "label" }, new String[] { stmtToName.get(trap.getBeginUnit()) });
        catchNode.addChild("end", new String[] { "label" }, new String[] { stmtToName.get(trap.getEndUnit()) });
        catchNode.addChild("handler", new String[] { "label" }, new String[] { stmtToName.get(trap.getHandlerUnit()) });
      }
      exceptionsNode.addAttribute("count", String.valueOf(exceptionsNode.getNumberOfChildren()));
    }
    // Scene.v().releaseActiveInvokeGraph();
  }

  private static String cleanMethod(String str) {
    // method names can be filtered here, for example replacing < and > with _ to comply with XML name tokens
    return str.trim().replace('<', '_').replace('>', '_');
  }

  private static String cleanLocal(String str) {
    // local names can be filtered here, for example replacing $ with _ to comply with XML name tokens
    return str.trim(); // .replace( '$', '_' );
  }

  private static String toCDATA(String str) {
    // wrap a string in CDATA markup - str can contain anything and will pass XML validation
    return "<![CDATA[" + str.replaceAll("]]>", "]]&gt;") + "]]>";
  }

  private static String boolToString(boolean bool) {
    return bool ? "true" : "false";
  }

  private static class XMLLabel {
    public final long id;
    public final String methodName;
    public final String label;
    public long stmtCount;
    public long stmtPercentage;

    public XMLLabel(long in_id, String in_methodName, String in_label) {
      this.id = in_id;
      this.methodName = in_methodName;
      this.label = in_label;
    }
  }
}
