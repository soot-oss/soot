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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import soot.Kind;
import soot.ModuleUtil;
import soot.Scene;
import soot.util.NumberedString;
import soot.util.StringNumberer;

/**
 * Utility class used by {@link OnFlyCallGraphBuilder} for finding functions at which to place virtual callgraph edges.
 * Function signatures are configurable in {@link #SUMMARIESFILE}.
 * 
 * @author Julius Naeumann
 */
public class VirtualEdgesSummaries {

  private static final String SUMMARIESFILE = "virtualedges.xml";

  private final HashMap<NumberedString, VirtualEdge> instanceinvokeEdges;
  private final HashMap<String, VirtualEdge> staticinvokeEdges;
  private final HashMap<NumberedString, VirtualEdge> registerfunctionsToEdges;

  private static final Logger logger = LoggerFactory.getLogger(VirtualEdgesSummaries.class);

  public VirtualEdgesSummaries() {
    this.instanceinvokeEdges = new HashMap<>();
    this.staticinvokeEdges = new HashMap<>();
    this.registerfunctionsToEdges = new HashMap<>();

    Path summariesFile = Paths.get(SUMMARIESFILE);
    try (InputStream in = Files.exists(summariesFile) ? Files.newInputStream(summariesFile)
        : ModuleUtil.class.getResourceAsStream("/" + SUMMARIESFILE)) {

      Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
      doc.getDocumentElement().normalize();

      NodeList edges = doc.getElementsByTagName("edge");
      for (int i = 0, e = edges.getLength(); i < e; i++) {
        if (edges.item(i).getNodeType() == Node.ELEMENT_NODE) {
          Element edge = (Element) edges.item(i);
          VirtualEdge edg = new VirtualEdge();
          switch (edge.getAttribute("type")) {
            case "THREAD":
              edg.edgeType = Kind.THREAD;
              break;
            case "EXECUTOR":
              edg.edgeType = Kind.EXECUTOR;
              break;
            case "HANDLER":
              edg.edgeType = Kind.HANDLER;
              break;
            case "ASYNCTASK":
              edg.edgeType = Kind.ASYNCTASK;
              break;
            case "PRIVILEDGED":
              edg.edgeType = Kind.PRIVILEGED;
              break;
            default:
              edg.edgeType = Kind.GENERIC_FAKE;
              break;
          }
          edg.source = parseEdgeSource((Element) (edge.getElementsByTagName("source").item(0)));
          edg.targets = new ArrayList<VirtualEdgesSummaries.VirtualEdgeTarget>();
          Element targetsElement = (Element) edge.getElementsByTagName("targets").item(0);
          edg.targets.addAll(parseDirectEdgeTargets(targetsElement));
          edg.targets.addAll(parseWrapperTargets(targetsElement));
          if (edg.source instanceof InstanceinvokeSource) {
            InstanceinvokeSource inst = (InstanceinvokeSource) edg.source;
            NumberedString subsig = inst.subSignature;

            // don't overwrite existing definition
            VirtualEdge existing = instanceinvokeEdges.get(subsig);
            if (existing != null) {
              existing.targets.addAll(edg.targets);
            } else {
              instanceinvokeEdges.put(subsig, edg);
            }
          }
          if (edg.source instanceof StaticinvokeSource) {
            StaticinvokeSource stat = (StaticinvokeSource) edg.source;
            staticinvokeEdges.put(stat.signature, edg);
          }
          for (VirtualEdgeTarget t : edg.targets) {
            if (t instanceof WrapperTarget) {
              WrapperTarget target = (WrapperTarget) t;
              registerfunctionsToEdges.put(target.registrationSignature, edg);
            }
          }

        }
      }

    } catch (IOException | ParserConfigurationException | SAXException e1) {
      logger.error("An error occurred while reading in virtual edge summaries", e1);
    }
    logger.debug("Found %d instanceinvoke, %d staticinvoke edge descriptions", instanceinvokeEdges.size(),
        staticinvokeEdges.size());
  }

  public VirtualEdge getVirtualEdgesMatchingSubSig(NumberedString subsig) {
    return instanceinvokeEdges.get(subsig);
  }

  public VirtualEdge getVirtualEdgesMatchingFunction(String signature) {
    return staticinvokeEdges.get(signature);
  }

  public VirtualEdge getVirtualEdgesForPotentialRegisterFunction(NumberedString subsig) {
    return registerfunctionsToEdges.get(subsig);
  }

  private static VirtualEdgeSource parseEdgeSource(Element source) {
    switch (source.getAttribute("invoketype")) {
      case "instance":
        return new InstanceinvokeSource(source.getAttribute("subsignature"));
      case "static":
        return new StaticinvokeSource(source.getAttribute("signature"));
      default:
        return null;
    }
  }

  private static ArrayList<DirectTarget> parseDirectEdgeTargets(Element targetsElement) {
    ArrayList<DirectTarget> targets = new ArrayList<>();
    final StringNumberer nmbr = Scene.v().getSubSigNumberer();
    NodeList children = targetsElement.getElementsByTagName("direct");
    for (int i = 0, e = children.getLength(); i < e; i++) {
      if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element targetElement = (Element) children.item(i);
        DirectTarget target = new DirectTarget();

        target.targetMethod = nmbr.findOrAdd(targetElement.getAttribute("subsignature"));
        if ("argument".equals(targetElement.getAttribute("target-position"))) {
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
    final StringNumberer nmbr = Scene.v().getSubSigNumberer();
    NodeList children = registermethodsElement.getElementsByTagName("registered-handler");
    for (int i = 0, e = children.getLength(); i < e; i++) {
      if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element targetElement = (Element) children.item(i);
        RegisteredHandlerTarget target = new RegisteredHandlerTarget();
        target.targetMethod = nmbr.findOrAdd(targetElement.getAttribute("target-subsignature"));
        target.argIndex = Integer.valueOf(targetElement.getAttribute("target-argument-index"));
        targets.add(target);
      }
    }
    return targets;
  }

  private static ArrayList<WrapperTarget> parseWrapperTargets(Element targetsElement) {
    ArrayList<WrapperTarget> targets = new ArrayList<>();
    final StringNumberer nmbr = Scene.v().getSubSigNumberer();
    NodeList children = targetsElement.getElementsByTagName("callback-wrapper");
    for (int i = 0, e = children.getLength(); i < e; i++) {
      if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element targetElement = (Element) children.item(i);
        WrapperTarget target = new WrapperTarget();

        target.registrationSignature = nmbr.findOrAdd(targetElement.getAttribute("registration-subsignature"));
        target.targets = parseRegisteredTargets((Element) targetElement.getElementsByTagName("handlers").item(0));
        if ("argument".equals(targetElement.getAttribute("wrapper-position"))) {
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
    /**
     * The method signature at which to insert this edge.
     */
    String signature;

    public StaticinvokeSource(String signature) {
      this.signature = signature;
    }

    @Override
    public String toString() {
      return signature;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((signature == null) ? 0 : signature.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      StaticinvokeSource other = (StaticinvokeSource) obj;
      if (signature == null) {
        if (other.signature != null) {
          return false;
        }
      } else if (!signature.equals(other.signature)) {
        return false;
      }
      return true;
    }
  }

  public static class InstanceinvokeSource extends VirtualEdgeSource {
    /**
     * The method subsignature at which to insert this edge.
     */
    NumberedString subSignature;

    public InstanceinvokeSource(String subSignature) {
      this.subSignature = Scene.v().getSubSigNumberer().findOrAdd(subSignature);
    }

    @Override
    public String toString() {
      return subSignature.toString();
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((subSignature == null) ? 0 : subSignature.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      InstanceinvokeSource other = (InstanceinvokeSource) obj;
      if (subSignature == null) {
        if (other.subSignature != null) {
          return false;
        }
      } else if (!subSignature.equals(other.subSignature)) {
        return false;
      }
      return true;
    }
  }

  public static abstract class VirtualEdgeTarget {
    boolean isBase;
    int argIndex;

    public VirtualEdgeTarget() {
      this.isBase = true;
      this.argIndex = -1;
    }

    public VirtualEdgeTarget(int argIndex) {
      this.isBase = false;
      this.argIndex = argIndex;
    }

    @Override
    public String toString() {
      return isBase ? "base" : String.format("argument %d", argIndex);
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + argIndex;
      result = prime * result + (isBase ? 1231 : 1237);
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      VirtualEdgeTarget other = (VirtualEdgeTarget) obj;
      if (argIndex != other.argIndex) {
        return false;
      }
      if (isBase != other.isBase) {
        return false;
      }
      return true;
    }
  }

  public static class DirectTarget extends VirtualEdgeTarget {
    NumberedString targetMethod;

    DirectTarget() {
      // internal use only
    }

    /**
     * Creates a new direct method invocation on an object passed to the original source as an argument. For example,
     * <code>foo.do(x)></code> could invoke <code>x.bar()</code> as a callback.
     * 
     * @param targetMethod
     *          The target method that is invoked on the argument object
     * @param argIndex
     *          The index of the argument that receives the target object
     */
    public DirectTarget(NumberedString targetMethod, int argIndex) {
      super(argIndex);
      this.targetMethod = targetMethod;
    }

    /**
     * Creates a new direct method invocation on the base object of the original source. For example, <code>foo.do()></code>
     * could invoke <code>foo.bar()</code> as a callback.
     * 
     * @param targetMethod
     *          The target method that is invoked on the base object
     */
    public DirectTarget(NumberedString targetMethod) {
      this.targetMethod = targetMethod;
    }

    @Override
    public String toString() {
      return String.format("Direct to %s on %s", targetMethod.toString(), super.toString());
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((targetMethod == null) ? 0 : targetMethod.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!super.equals(obj)) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      DirectTarget other = (DirectTarget) obj;
      if (targetMethod == null) {
        if (other.targetMethod != null) {
          return false;
        }
      } else if (!targetMethod.equals(other.targetMethod)) {
        return false;
      }
      return true;
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
    ArrayList<RegisteredHandlerTarget> targets;

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      for (RegisteredHandlerTarget t : targets) {
        sb.append('(').append(t.toString()).append(") ");
      }
      return String.format("(Instances passed to <?: %s> on %s => %s)", registrationSignature.toString(), super.toString(),
          sb.toString());

    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((registrationSignature == null) ? 0 : registrationSignature.hashCode());
      result = prime * result + ((targets == null) ? 0 : targets.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!super.equals(obj)) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      WrapperTarget other = (WrapperTarget) obj;
      if (registrationSignature == null) {
        if (other.registrationSignature != null) {
          return false;
        }
      } else if (!registrationSignature.equals(other.registrationSignature)) {
        return false;
      }
      if (targets == null) {
        if (other.targets != null) {
          return false;
        }
      } else if (!targets.equals(other.targets)) {
        return false;
      }
      return true;
    }
  }

  public static class VirtualEdge {
    /**
     * The kind of edge to insert
     */
    Kind edgeType;
    VirtualEdgeSource source;
    List<VirtualEdgeTarget> targets;

    VirtualEdge() {
      // internal use only
    }

    public VirtualEdge(Kind edgeType, VirtualEdgeSource source, VirtualEdgeTarget target) {
      this(edgeType, source, new ArrayList<>(Collections.singletonList(target)));
    }

    public VirtualEdge(Kind edgeType, VirtualEdgeSource source, List<VirtualEdgeTarget> targets) {
      this.edgeType = edgeType;
      this.source = source;
      this.targets = targets;
    }

    public Kind getEdgeType() {
      return edgeType;
    }

    public VirtualEdgeSource getSource() {
      return source;
    }

    public List<VirtualEdgeTarget> getTargets() {
      return targets;
    }

    /**
     * Adds the given targets to this edge summary
     * 
     * @param newTargets
     *          The targets to add
     */
    public void addTargets(List<VirtualEdgeTarget> newTargets) {
      this.targets.addAll(newTargets);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      for (VirtualEdgeTarget t : targets) {
        sb.append(t.toString()).append(' ');
      }
      return String.format("%s %s => %s", edgeType, source.toString(), sb.toString());
    }

  }

  public Set<VirtualEdge> getAllVirtualEdges() {
    Set<VirtualEdge> allEdges
        = new HashSet<>(instanceinvokeEdges.size() + registerfunctionsToEdges.size() + staticinvokeEdges.size());
    allEdges.addAll(instanceinvokeEdges.values());
    allEdges.addAll(registerfunctionsToEdges.values());
    allEdges.addAll(staticinvokeEdges.values());
    return allEdges;
  }

}
