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
import java.util.Collection;
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
import soot.MethodSubSignature;
import soot.ModuleUtil;
import soot.Scene;
import soot.jimple.Stmt;
import soot.util.StringNumberer;

/**
 * Utility class used by {@link OnFlyCallGraphBuilder} for finding functions at which to place virtual callgraph edges.
 * Function signatures are configurable in {@link #SUMMARIESFILE}.
 * 
 * @author Julius Naeumann
 */
public class VirtualEdgesSummaries {

  private static final String SUMMARIESFILE = "virtualedges.xml";

  private final HashMap<MethodSubSignature, VirtualEdge> instanceinvokeEdges = new HashMap<>();
  private final HashMap<String, VirtualEdge> staticinvokeEdges = new HashMap<>();

  private static final Logger logger = LoggerFactory.getLogger(VirtualEdgesSummaries.class);

  public VirtualEdgesSummaries() {
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
            case "PRIVILEGED":
              edg.edgeType = Kind.PRIVILEGED;
              break;
            case "GENERIC_FAKE":
            default:
              edg.edgeType = Kind.GENERIC_FAKE;
              break;
          }
          edg.source = parseEdgeSource((Element) (edge.getElementsByTagName("source").item(0)));
          edg.targets = new ArrayList<VirtualEdgesSummaries.VirtualEdgeTarget>();
          Element targetsElement = (Element) edge.getElementsByTagName("targets").item(0);
          edg.targets.addAll(parseEdgeTargets(targetsElement));
          if (edg.source instanceof InstanceinvokeSource) {
            InstanceinvokeSource inst = (InstanceinvokeSource) edg.source;
            MethodSubSignature subsig = inst.subSignature;

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
        }
      }

    } catch (IOException | ParserConfigurationException | SAXException e1) {
      logger.error("An error occurred while reading in virtual edge summaries", e1);
    }
    logger.debug("Found {} instanceinvoke, {} staticinvoke edge descriptions", instanceinvokeEdges.size(),
        staticinvokeEdges.size());
  }

  public VirtualEdge getVirtualEdgesMatchingSubSig(MethodSubSignature subsig) {
    return instanceinvokeEdges.get(subsig);
  }

  public VirtualEdge getVirtualEdgesMatchingFunction(String signature) {
    return staticinvokeEdges.get(signature);
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

  private static List<VirtualEdgeTarget> parseEdgeTargets(Element targetsElement) {
    List<VirtualEdgeTarget> targets = new ArrayList<>();
    final StringNumberer nmbr = Scene.v().getSubSigNumberer();
    NodeList children = targetsElement.getChildNodes();
    for (int i = 0, e = children.getLength(); i < e; i++) {
      if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element targetElement = (Element) children.item(i);

        switch (targetElement.getTagName()) {
          case "direct": {
            MethodSubSignature subsignature
                = new MethodSubSignature(nmbr.findOrAdd(targetElement.getAttribute("subsignature")));
            if ("argument".equals(targetElement.getAttribute("target-position"))) {
              int argIdx = Integer.valueOf(targetElement.getAttribute("index"));
              targets.add(new DirectTarget(subsignature, argIdx));
            } else {
              targets.add(new DirectTarget(subsignature));
            }
            break;
          }
          case "indirect": {
            // Parse the attributes of the current target
            IndirectTarget target;
            MethodSubSignature subsignature
                = new MethodSubSignature(nmbr.findOrAdd(targetElement.getAttribute("subsignature")));
            if ("argument".equals(targetElement.getAttribute("target-position"))) {
              int argIdx = Integer.valueOf(targetElement.getAttribute("index"));
              target = new IndirectTarget(subsignature, argIdx);
            } else {
              target = new IndirectTarget(subsignature);
            }

            // Parse child targets, since we have a chain of target methods to track back to the point where the actual
            // callback
            // was originally registered
            target.addTargets(parseEdgeTargets(targetElement));

            targets.add(target);
            break;
          }
        }
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

    public String getSignature() {
      return signature;
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
    MethodSubSignature subSignature;

    /**
     * Creates a new instance of the {@link InstanceinvokeSource} class based on a method that is being invoked on the
     * current object instance
     * 
     * @param subSignature
     *          The subsignature of the method that is invoked
     */
    public InstanceinvokeSource(String subSignature) {
      this.subSignature = new MethodSubSignature(Scene.v().getSubSigNumberer().findOrAdd(subSignature));
    }

    /**
     * Convenience constructor that extracts the subsignature of the callee from a call site statement
     * 
     * @param invokeStmt
     *          The statement at the call site
     */
    public InstanceinvokeSource(Stmt invokeStmt) {
      this(invokeStmt.getInvokeExpr().getMethodRef().getSubSignature().getString());
    }

    @Override
    public String toString() {
      return subSignature.toString();
    }

    public MethodSubSignature getSubSignature() {
      return subSignature;
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

    protected int argIndex;
    protected MethodSubSignature targetMethod;

    VirtualEdgeTarget() {
      // internal use only
    }

    public VirtualEdgeTarget(MethodSubSignature targetMethod) {
      this.argIndex = -1;
      this.targetMethod = targetMethod;
    }

    public VirtualEdgeTarget(MethodSubSignature targetMethod, int argIndex) {
      this.argIndex = argIndex;
      this.targetMethod = targetMethod;
    }

    @Override
    public String toString() {
      return isBase() ? "base" : String.format("argument %d", argIndex);
    }

    public boolean isBase() {
      return argIndex == -1;
    }

    public int getArgIndex() {
      return argIndex;
    }

    public MethodSubSignature getTargetMethod() {
      return targetMethod;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + argIndex;
      result = prime * result + ((targetMethod == null) ? 0 : targetMethod.hashCode());
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

  public static class DirectTarget extends VirtualEdgeTarget {

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
    public DirectTarget(MethodSubSignature targetMethod, int argIndex) {
      super(targetMethod, argIndex);
    }

    /**
     * Creates a new direct method invocation on the base object of the original source. For example, <code>foo.do()></code>
     * could invoke <code>foo.bar()</code> as a callback.
     * 
     * @param targetMethod
     *          The target method that is invoked on the base object
     */
    public DirectTarget(MethodSubSignature targetMethod) {
      super(targetMethod);
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

  public static class IndirectTarget extends VirtualEdgeTarget {
    List<VirtualEdgeTarget> targets = new ArrayList<>();

    IndirectTarget() {
      // internal use only
    }

    /**
     * Creates a new direct method invocation. The signature of this indirect target references a method that was called
     * earlier, and which received the object on which the callback is invoked. This constructor assumes that the earlier
     * method has created an object which is passed the current method as an argument.
     * 
     * @param targetMethod
     *          The method with which the original callback was registered
     * @param argIndex
     *          The index of the argument that holds the object that holds the callback or next step of the indirect
     *          invocation
     */
    public IndirectTarget(MethodSubSignature targetMethod, int argIndex) {
      super(targetMethod, argIndex);
    }

    /**
     * Creates a new indirect target as an indirection from a method that was previously considered a source
     * 
     * @param source
     *          The source from which to create the indirect target
     */
    public IndirectTarget(InstanceinvokeSource source) {
      super(source.subSignature);
    }

    /**
     * Creates a new direct method invocation. The signature of this indirect target references a method that was called
     * earlier, and which received the object on which the callback is invoked.
     * 
     * @param targetMethod
     *          The method with which the original callback was registered
     */
    public IndirectTarget(MethodSubSignature targetMethod) {
      super(targetMethod);
    }

    public void addTarget(VirtualEdgeTarget target) {
      targets.add(target);
    }

    public void addTargets(Collection<? extends VirtualEdgeTarget> targets) {
      this.targets.addAll(targets);
    }

    public List<VirtualEdgeTarget> getTargets() {
      return targets;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      for (VirtualEdgeTarget t : targets) {
        sb.append('(').append(t.toString()).append(") ");
      }
      return String.format("(Instances passed to <?: %s> on %s => %s)", targetMethod.toString(), super.toString(),
          sb.toString());

    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
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
      IndirectTarget other = (IndirectTarget) obj;
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

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((edgeType == null) ? 0 : edgeType.hashCode());
      result = prime * result + ((source == null) ? 0 : source.hashCode());
      result = prime * result + ((targets == null) ? 0 : targets.hashCode());
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
      VirtualEdge other = (VirtualEdge) obj;
      if (edgeType == null) {
        if (other.edgeType != null) {
          return false;
        }
      } else if (!edgeType.equals(other.edgeType)) {
        return false;
      }
      if (source == null) {
        if (other.source != null) {
          return false;
        }
      } else if (!source.equals(other.source)) {
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

  public Set<VirtualEdge> getAllVirtualEdges() {
    Set<VirtualEdge> allEdges = new HashSet<>(instanceinvokeEdges.size() + staticinvokeEdges.size());
    allEdges.addAll(instanceinvokeEdges.values());
    allEdges.addAll(staticinvokeEdges.values());
    return allEdges;
  }

}
