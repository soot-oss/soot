package soot.jimple.toolkits.callgraph;

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
 * @author Julius Naeumann
 *
 */
public class VirtualEdgesSummaries {
  private static final String SUMMARIESFILE = "virtualedges.xml";
  private static HashMap<NumberedString, VirtualEdge> virtualinvokeEdges;
  private static HashMap<NumberedString, VirtualEdge> staticinvokeEdges;

  private void parseSummaries() {
    virtualinvokeEdges = new HashMap<>();
    staticinvokeEdges = new HashMap<>();
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
          if ("instance".equals(edge.getAttribute("invoketype"))) {

          }
        }
      }

    } catch (IOException | ParserConfigurationException | SAXException e1) {
      e1.printStackTrace();
    }
  }

  private static VirtualEdgeSource parseEdgeSource(Element source) {
    String type = source.getAttribute("invoketype");
    if ("instance".equals(type))
        return new InstanceinvokeSource(source.getAttribute("subsignature"));
    if ("static".equals(type))
        return new StaticinvokeSource(source.getAttribute("signature"));
    return null;
  }

  private static ArrayList<VirtualEdgeTarget> parseEdgeTargets(Element targetsElement) {
    ArrayList<VirtualEdgeTarget> targets = new ArrayList<>();
    NodeList children = targetsElement.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element target = (Element) children.item(i);
        if ("method".equals(target.getNodeName())) {
          DirectTarget t = new DirectTarget();
          t.targetMethod = Scene.v().getSubSigNumberer().findOrAdd(target.getAttribute("subsignature"));
          if (target.getAttribute("target").equals("argument")) {
            t.isBase = false;
            t.argIndex = Integer.valueOf(target.getAttribute("index"));
          } else {
            t.isBase = true;
            t.argIndex = -1;
          }
          targets.add(t);
        } else if ("wrapper".equals(target.getNodeName())) {
          IndirectTarget t = new IndirectTarget();
          t.registrationSignature = Scene.v().getSubSigNumberer().findOrAdd(s)
        }
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
  }

  public static class InstanceinvokeSource extends VirtualEdgeSource {
    public InstanceinvokeSource(String subSignature) {
      this.subSignature = Scene.v().getSubSigNumberer().findOrAdd(subSignature);
    }
    /**
     * The method subsignature at which to insert this edge.
     */
    NumberedString subSignature;
  }

  public static abstract class VirtualEdgeTarget {

  }

  public static class DirectTarget extends VirtualEdgeTarget {
    NumberedString targetMethod;

    boolean isBase;

    int argIndex;
  }

  public static class IndirectTarget extends VirtualEdgeTarget {
    NumberedString registrationSignature;

    boolean isBase;

    int argIndex;
    ArrayList<DirectTarget> targets;
  }


  public static class VirtualEdge {
    /**
     * The kind of edge to insert
     */
    Kind edgeType;
    VirtualEdgeSource source;
    ArrayList<VirtualEdgeTarget> targets;


  }

}
