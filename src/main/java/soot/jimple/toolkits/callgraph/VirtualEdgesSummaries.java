package soot.jimple.toolkits.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import soot.Kind;
import soot.ModuleUtil;
import soot.Scene;
import soot.util.NumberedString;

/**
 * 
 * Utility class used by {@link OnFlyCallGraphBuilder} for finding functions at which to place virtual callgraph edges.
 * Function signatures are configurable in virtualedges.xml.
 * 
 * 
 * @author Julius Naeumann
 *
 */
public class VirtualEdgesSummaries {
  private static final String SUMMARIESFILE = "virtualedges.xml";
  private static HashMap<NumberedString, VirtualEdge> instanceinvokeEdges;
  private static HashMap<String, VirtualEdge> staticinvokeEdges;
  private static HashMap<NumberedString, VirtualEdge> registerfunctionsToEdges;

  public static VirtualEdge getVirtualEdgesMatchingSubSig(NumberedString subsig) {
    return instanceinvokeEdges.get(subsig);
  }

  public static VirtualEdge getVirtualEdgesMatchingFunction(String signature) {
    return staticinvokeEdges.get(signature);
  }

  public static VirtualEdge getVirtualEdgesForPotentialRegisterFunction(NumberedString subsig) {
    return registerfunctionsToEdges.get(subsig);
  }

  public static void parseSummaries() {
    if (instanceinvokeEdges != null) {
      // only parse once.
      return;
    }
    instanceinvokeEdges = new HashMap<>();
    staticinvokeEdges = new HashMap<>();
    registerfunctionsToEdges = new HashMap<>();
    InputStream in = null;
    Path summariesFile = Paths.get(SUMMARIESFILE);
    try {
      if (!Files.exists(summariesFile)) {
        // else take the one package

        in = ModuleUtil.class.getResourceAsStream("/" + SUMMARIESFILE);
      } else {

        in = Files.newInputStream(summariesFile);

      }
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(in);
      doc.getDocumentElement().normalize();

      NodeList edges = doc.getElementsByTagName("edge");
      for (int i = 0; i < edges.getLength(); i++) {
        if (edges.item(i).getNodeType() == Node.ELEMENT_NODE) {
          Element edge = (Element) edges.item(i);
          VirtualEdge e = new VirtualEdge();
          switch (edge.getAttribute("type")) {
            case "THREAD":
              e.edgeType = Kind.THREAD;
              break;
            case "EXECUTOR":
              e.edgeType = Kind.EXECUTOR;
              break;
            case "HANDLER":
              e.edgeType = Kind.HANDLER;
              break;
            case "ASYNCTASK":
              e.edgeType = Kind.ASYNCTASK;
              break;
            case "PRIVILEDGED":
              e.edgeType = Kind.PRIVILEGED;
              break;
          }
          e.source = parseEdgeSource((Element) (edge.getElementsByTagName("source").item(0)));
          e.targets = new ArrayList<VirtualEdgesSummaries.VirtualEdgeTarget>();
          Element targetsElement = (Element) edge.getElementsByTagName("targets").item(0);
          e.targets.addAll(parseDirectEdgeTargets(targetsElement));
          e.targets.addAll(parseWrapperTargets(targetsElement));
          if (e.source instanceof InstanceinvokeSource) {
            InstanceinvokeSource inst = (InstanceinvokeSource) e.source;

            // don't overwrite existing definition
            VirtualEdge existing = instanceinvokeEdges.get(inst.subSignature);
            if (existing != null) {
              existing.targets.addAll(e.targets);
            } else {
              instanceinvokeEdges.put(inst.subSignature, e);
            }
          }
          if (e.source instanceof StaticinvokeSource) {
            StaticinvokeSource stat = (StaticinvokeSource) e.source;
            staticinvokeEdges.put(stat.signature, e);
          }
          for (VirtualEdgeTarget t : e.targets) {
            if (t instanceof WrapperTarget) {
              WrapperTarget target = (WrapperTarget) t;
              registerfunctionsToEdges.put(target.registrationSignature, e);
            }
          }

        }
      }

    } catch (IOException | ParserConfigurationException | SAXException e1) {
      e1.printStackTrace();
    }
    System.out.println(
        String.format("Found %d instanceinvoke , %d staticinvoke edge descriptions", instanceinvokeEdges.size(),
            staticinvokeEdges.size()));
  }

  private static VirtualEdgeSource parseEdgeSource(Element source) {
    String type = source.getAttribute("invoketype");
    if ("instance".equals(type))
        return new InstanceinvokeSource(source.getAttribute("subsignature"));
    if ("static".equals(type))
        return new StaticinvokeSource(source.getAttribute("signature"));
    return null;
  }

  private static ArrayList<DirectTarget> parseDirectEdgeTargets(Element targetsElement) {
    ArrayList<DirectTarget> targets = new ArrayList<>();
    NodeList children = targetsElement.getElementsByTagName("direct");
    for (int i = 0; i < children.getLength(); i++) {
      if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element targetElement = (Element) children.item(i);
        DirectTarget target = new DirectTarget();

        target.targetMethod = Scene.v().getSubSigNumberer().findOrAdd(targetElement.getAttribute("subsignature"));

        if (targetElement.getAttribute("target-position").equals("argument")) {
          target.isBase = false;
          target.argIndex = Integer.valueOf(targetElement.getAttribute("index"));
        } else {
          target.isBase = true;
          target.argIndex = -1;
        }
        targets.add(target);
      }
    }
    return targets;
  }

  private static ArrayList<RegisteredHandlerTarget> parseRegisteredTargets(Element registermethodsElement) {
    ArrayList<RegisteredHandlerTarget> targets = new ArrayList<>();
    NodeList children = registermethodsElement.getElementsByTagName("registered-handler");
    for (int i = 0; i < children.getLength(); i++) {
      if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element targetElement = (Element) children.item(i);
        RegisteredHandlerTarget target = new RegisteredHandlerTarget();
        target.targetMethod = Scene.v().getSubSigNumberer().findOrAdd(targetElement.getAttribute("target-subsignature"));
        target.argIndex = Integer.valueOf(targetElement.getAttribute("target-argument-index"));
        targets.add(target);
      }
    }
    return targets;
  }

  private static ArrayList<WrapperTarget> parseWrapperTargets(Element targetsElement) {
    ArrayList<WrapperTarget> targets = new ArrayList<>();
    NodeList children = targetsElement.getElementsByTagName("callback-wrapper");
    for (int i = 0; i < children.getLength(); i++) {
      if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element targetElement = (Element) children.item(i);
        WrapperTarget target = new WrapperTarget();

        target.registrationSignature
            = Scene.v().getSubSigNumberer().findOrAdd(targetElement.getAttribute("registration-subsignature"));

        target.targets
            = parseRegisteredTargets((Element) (targetElement.getElementsByTagName("handlers").item(0)));

        if (targetElement.getAttribute("wrapper-position").equals("argument")) {
          target.isBase = false;
          target.argIndex = Integer.valueOf(targetElement.getAttribute("wrapper-argument-index"));
        } else {
          target.isBase = true;
          target.argIndex = -1;
        }
        targets.add(target);
      }
    }
    return targets;
  }

  public static abstract class VirtualEdgeSource {

  }

  public static class StaticinvokeSource extends VirtualEdgeSource {
    public StaticinvokeSource(String signature) {
      this.signature = (signature);
    }
    /**
     * The method signature at which to insert this edge.
     */
    String signature;

    @Override
    public String toString() {
      return signature;
    }

  }

  public static class InstanceinvokeSource extends VirtualEdgeSource {
    public InstanceinvokeSource(String subSignature) {
      this.subSignature = Scene.v().getSubSigNumberer().findOrAdd(subSignature);
    }
    /**
     * The method subsignature at which to insert this edge.
     */
    NumberedString subSignature;

    @Override
    public String toString() {
      return subSignature.toString();
    }

  }

  public static abstract class VirtualEdgeTarget {

    boolean isBase;

    int argIndex;

    @Override
    public String toString() {
      return isBase ? "base" : String.format("argument %d", argIndex);
    }

  }

  public static class DirectTarget extends VirtualEdgeTarget {
    NumberedString targetMethod;

    @Override
    public String toString() {
      return String.format("Direct to %s on %s", targetMethod.toString(), super.toString());
    }

  }

  public static class RegisteredHandlerTarget {
    int argIndex;
    NumberedString targetMethod;

    @Override
    public String toString() {
      return String.format("Register to %s passed at %d", targetMethod.toString(), argIndex);
    }

  }

  public static class WrapperTarget extends VirtualEdgeTarget {
    NumberedString registrationSignature;

    @Override
    public String toString() {
      String targetstr = "";
      for (RegisteredHandlerTarget t : targets) {
        targetstr += "(" + t.toString() + ") ";
      }
      return String.format("(Instances passed to <?: %s> on %s => %s)", registrationSignature.toString(), super.toString(),
          targetstr);

    }

    ArrayList<RegisteredHandlerTarget> targets;


  }


  public static class VirtualEdge {
    /**
     * The kind of edge to insert
     */
    Kind edgeType;
    VirtualEdgeSource source;
    ArrayList<VirtualEdgeTarget> targets;

    @Override
    public String toString() {
      String targetstr = "";
      for (VirtualEdgeTarget t : targets) {
        targetstr += t.toString() + " ";
      }
      return String.format("%s %s => %s", edgeType, source.toString(), targetstr);
    }


  }

}
