package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
 * Copyright (C) 2004 Jennifer Lhotak, Ondrej Lhotak
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

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.baf.DoubleWordType;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IdentityStmt;
import soot.options.Options;
import soot.tagkit.AnnotationAnnotationElem;
import soot.tagkit.AnnotationArrayElem;
import soot.tagkit.AnnotationBooleanElem;
import soot.tagkit.AnnotationClassElem;
import soot.tagkit.AnnotationConstants;
import soot.tagkit.AnnotationDefaultTag;
import soot.tagkit.AnnotationDoubleElem;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationEnumElem;
import soot.tagkit.AnnotationFloatElem;
import soot.tagkit.AnnotationIntElem;
import soot.tagkit.AnnotationLongElem;
import soot.tagkit.AnnotationStringElem;
import soot.tagkit.AnnotationTag;
import soot.tagkit.Attribute;
import soot.tagkit.Base64;
import soot.tagkit.DeprecatedTag;
import soot.tagkit.DoubleConstantValueTag;
import soot.tagkit.EnclosingMethodTag;
import soot.tagkit.FloatConstantValueTag;
import soot.tagkit.InnerClassAttribute;
import soot.tagkit.InnerClassTag;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.LongConstantValueTag;
import soot.tagkit.SignatureTag;
import soot.tagkit.SourceFileTag;
import soot.tagkit.StringConstantValueTag;
import soot.tagkit.SyntheticTag;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;
import soot.tagkit.VisibilityParameterAnnotationTag;
import soot.toolkits.graph.Block;
import soot.util.StringTools;

public abstract class AbstractJasminClass {
  private static final Logger logger = LoggerFactory.getLogger(AbstractJasminClass.class);

  protected Map<Unit, String> unitToLabel;
  protected Map<Local, Integer> localToSlot;
  protected Map<Unit, Integer> subroutineToReturnAddressSlot;

  protected List<String> code;

  protected boolean isEmittingMethodCode;
  protected int labelCount;

  protected boolean isNextGotoAJsr;
  protected int returnAddressSlot;
  protected int currentStackHeight = 0;
  protected int maxStackHeight = 0;

  protected Map<Local, Object> localToGroup;
  protected Map<Object, Integer> groupToColorCount;
  protected Map<Local, Integer> localToColor;

  // maps a block to the stack height upon entering it
  protected Map<Block, Integer> blockToStackHeight = new HashMap<Block, Integer>();

  // maps a block to the logical stack height upon entering it
  protected Map<Block, Integer> blockToLogicalStackHeight = new HashMap<Block, Integer>();

  public static String slashify(String s) {
    return s.replace('.', '/');
  }

  public static int sizeOfType(Type t) {
    if (t instanceof DoubleWordType || t instanceof LongType || t instanceof DoubleType) {
      return 2;
    } else if (t instanceof VoidType) {
      return 0;
    } else {
      return 1;
    }
  }

  public static int argCountOf(SootMethodRef m) {
    int argCount = 0;
    for (Type t : m.parameterTypes()) {
      argCount += sizeOfType(t);
    }
    return argCount;
  }

  public static String jasminDescriptorOf(Type type) {
    TypeSwitch<String> sw = new TypeSwitch<String>() {
      @Override
      public void caseBooleanType(BooleanType t) {
        setResult("Z");
      }

      @Override
      public void caseByteType(ByteType t) {
        setResult("B");
      }

      @Override
      public void caseCharType(CharType t) {
        setResult("C");
      }

      @Override
      public void caseDoubleType(DoubleType t) {
        setResult("D");
      }

      @Override
      public void caseFloatType(FloatType t) {
        setResult("F");
      }

      @Override
      public void caseIntType(IntType t) {
        setResult("I");
      }

      @Override
      public void caseLongType(LongType t) {
        setResult("J");
      }

      @Override
      public void caseShortType(ShortType t) {
        setResult("S");
      }

      @Override
      public void defaultCase(Type t) {
        throw new RuntimeException("Invalid type: " + t);
      }

      @Override
      public void caseArrayType(ArrayType t) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < t.numDimensions; i++) {
          buffer.append('[');
        }
        buffer.append(jasminDescriptorOf(t.baseType));
        setResult(buffer.toString());
      }

      @Override
      public void caseRefType(RefType t) {
        setResult("L" + t.getClassName().replace('.', '/') + ";");
      }

      @Override
      public void caseVoidType(VoidType t) {
        setResult("V");
      }
    };

    type.apply(sw);
    return sw.getResult();
  }

  public static String jasminDescriptorOf(SootMethodRef m) {
    StringBuilder buffer = new StringBuilder();
    buffer.append('(');

    // Add methods parameters
    for (Type t : m.parameterTypes()) {
      buffer.append(jasminDescriptorOf(t));
    }

    buffer.append(')');
    buffer.append(jasminDescriptorOf(m.returnType()));

    return buffer.toString();
  }

  protected void emit(String s) {
    okayEmit(s);
  }

  protected void okayEmit(String s) {
    if (isEmittingMethodCode && !s.endsWith(":")) {
      code.add("    " + s);
    } else {
      code.add(s);
    }
  }

  private String getVisibilityAnnotationAttr(VisibilityAnnotationTag tag) {
    if (tag == null) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    switch (tag.getVisibility()) {
      case AnnotationConstants.RUNTIME_VISIBLE:
        sb.append(".runtime_visible_annotation\n");
        break;
      case AnnotationConstants.RUNTIME_INVISIBLE:
        sb.append(".runtime_invisible_annotation\n");
        break;
      default:
        // source level annotation
        return "";
    }
    if (tag.hasAnnotations()) {
      for (AnnotationTag annot : tag.getAnnotations()) {
        sb.append(".annotation ");
        sb.append(StringTools.getQuotedStringOf(annot.getType())).append('\n');
        for (AnnotationElem ae : annot.getElems()) {
          sb.append(getElemAttr(ae));
        }
        sb.append(".end .annotation\n");
      }
    }
    sb.append(".end .annotation_attr\n");
    return sb.toString();
  }

  private String getVisibilityParameterAnnotationAttr(VisibilityParameterAnnotationTag tag) {
    StringBuilder sb = new StringBuilder();
    sb.append(".param ");
    if (tag.getKind() == AnnotationConstants.RUNTIME_VISIBLE) {
      sb.append(".runtime_visible_annotation\n");
    } else {
      sb.append(".runtime_invisible_annotation\n");
    }
    ArrayList<VisibilityAnnotationTag> vis_list = tag.getVisibilityAnnotations();
    if (vis_list != null) {
      for (VisibilityAnnotationTag vat : vis_list) {
        VisibilityAnnotationTag safeVat = vat == null ? SafeVisibilityAnnotationTags.get(tag.getKind()) : vat;
        sb.append(getVisibilityAnnotationAttr(safeVat));
      }
    }
    sb.append(".end .param\n");
    return sb.toString();
  }

  private static class SafeVisibilityAnnotationTags {

    private static final Map<Integer, VisibilityAnnotationTag> safeVats = new HashMap<Integer, VisibilityAnnotationTag>();

    static VisibilityAnnotationTag get(int kind) {
      VisibilityAnnotationTag safeVat = safeVats.get(kind);
      if (safeVat == null) {
        safeVats.put(kind, safeVat = new VisibilityAnnotationTag(kind));
      }
      return safeVat;
    }

    private SafeVisibilityAnnotationTags() {
    }
  }

  private String getElemAttr(AnnotationElem elem) {
    StringBuilder result = new StringBuilder(".elem ");
    switch (elem.getKind()) {
      case 'Z': {
        result.append(".bool_kind ");
        result.append('"').append(elem.getName()).append("\" ");
        if (elem instanceof AnnotationIntElem) {
          result.append(((AnnotationIntElem) elem).getValue());
        } else {
          result.append(((AnnotationBooleanElem) elem).getValue() ? 1 : 0);
        }
        result.append('\n');
        break;
      }
      case 'S': {
        result.append(".short_kind ");
        result.append('"').append(elem.getName()).append("\" ");
        result.append(((AnnotationIntElem) elem).getValue());
        result.append('\n');
        break;
      }
      case 'B': {
        result.append(".byte_kind ");
        result.append('"').append(elem.getName()).append("\" ");
        result.append(((AnnotationIntElem) elem).getValue());
        result.append('\n');
        break;
      }
      case 'C': {
        result.append(".char_kind ");
        result.append('"').append(elem.getName()).append("\" ");
        result.append(((AnnotationIntElem) elem).getValue());
        result.append('\n');
        break;
      }
      case 'I': {
        result.append(".int_kind ");
        result.append('"').append(elem.getName()).append("\" ");
        result.append(((AnnotationIntElem) elem).getValue());
        result.append('\n');
        break;
      }
      case 'J': {
        result.append(".long_kind ");
        result.append('"').append(elem.getName()).append("\" ");
        result.append(((AnnotationLongElem) elem).getValue());
        result.append('\n');
        break;
      }
      case 'F': {
        result.append(".float_kind ");
        result.append('"').append(elem.getName()).append("\" ");
        result.append(((AnnotationFloatElem) elem).getValue());
        result.append('\n');
        break;
      }
      case 'D': {
        result.append(".doub_kind ");
        result.append('"').append(elem.getName()).append("\" ");
        result.append(((AnnotationDoubleElem) elem).getValue());
        result.append('\n');
        break;
      }
      case 's': {
        result.append(".str_kind ");
        result.append('"').append(elem.getName()).append("\" ");
        result.append(StringTools.getQuotedStringOf(((AnnotationStringElem) elem).getValue()));
        result.append('\n');
        break;
      }
      case 'e': {
        result.append(".enum_kind ");
        result.append('"').append(elem.getName()).append("\" ");
        result.append(StringTools.getQuotedStringOf(((AnnotationEnumElem) elem).getTypeName()));
        result.append(' ');
        result.append(StringTools.getQuotedStringOf(((AnnotationEnumElem) elem).getConstantName()));
        result.append('\n');
        break;
      }
      case 'c': {
        result.append(".cls_kind ");
        result.append('"').append(elem.getName()).append("\" ");
        result.append(StringTools.getQuotedStringOf(((AnnotationClassElem) elem).getDesc()));
        result.append('\n');
        break;
      }
      case '[': {
        result.append(".arr_kind ");
        result.append('"').append(elem.getName()).append("\" ");
        result.append('\n');
        AnnotationArrayElem arrayElem = (AnnotationArrayElem) elem;
        for (int i = 0; i < arrayElem.getNumValues(); i++) {
          // result.append('\n');
          result.append(getElemAttr(arrayElem.getValueAt(i)));
        }
        result.append(".end .arr_elem\n");
        break;
      }
      case '@': {
        result.append(".ann_kind ");
        result.append('"').append(elem.getName()).append("\"\n");
        AnnotationTag annot = ((AnnotationAnnotationElem) elem).getValue();
        result.append(".annotation ");
        result.append(StringTools.getQuotedStringOf(annot.getType())).append('\n');
        for (AnnotationElem ae : annot.getElems()) {
          result.append(getElemAttr(ae));
        }
        result.append(".end .annotation\n");
        result.append(".end .annot_elem\n");
        break;
      }
      default: {
        throw new RuntimeException("Unknown Elem Attr Kind: " + elem.getKind());
      }
    }
    return result.toString();
  }

  public AbstractJasminClass(SootClass sootClass) {
    if (Options.v().time()) {
      Timers.v().buildJasminTimer.start();
    }

    if (Options.v().verbose()) {
      logger.debug("[" + sootClass.getName() + "] Constructing baf.JasminClass...");
    }

    code = new LinkedList<String>();

    // Emit the header
    {
      int modifiers = sootClass.getModifiers();

      if (!Options.v().no_output_source_file_attribute()) {
        SourceFileTag tag = (SourceFileTag) sootClass.getTag(SourceFileTag.NAME);
        if (tag != null) {
          String srcName = tag.getSourceFile();
          // Since Jasmin fails on backslashes and only Windows uses backslashes,
          // but also accepts forward slashes, we transform it.
          if (File.separatorChar == '\\') {
            srcName = srcName.replace('\\', '/');
          }
          srcName = StringTools.getEscapedStringOf(srcName);

          // if 'srcName' starts with a digit, Jasmin throws an
          // 'Badly formatted number' error. When analyzing an Android
          // applications (.apk) their name is stored in srcName and
          // can start with a digit.
          if (!Options.v().android_jars().isEmpty() && !srcName.isEmpty() && Character.isDigit(srcName.charAt(0))) {
            srcName = "n_" + srcName;
          }

          // Jasmin does not support blanks and quotes, so get rid of them
          srcName = srcName.replace(" ", "-");
          srcName = srcName.replace("\"", "");

          if (!srcName.isEmpty()) {
            emit(".source " + srcName);
          }
        }
      }
      if (Modifier.isInterface(modifiers)) {
        modifiers -= Modifier.INTERFACE;
        emit(".interface " + Modifier.toString(modifiers) + " " + slashify(sootClass.getName()));
      } else {
        emit(".class " + Modifier.toString(modifiers) + " " + slashify(sootClass.getName()));
      }

      if (sootClass.hasSuperclass()) {
        emit(".super " + slashify(sootClass.getSuperclass().getName()));
      } else {
        emit(".no_super");
      }

      emit("");
    }

    // Emit the interfaces
    for (SootClass inter : sootClass.getInterfaces()) {
      emit(".implements " + slashify(inter.getName()));
    }
    /*
     * why do this???? if(sootClass.getInterfaceCount() != 0) emit("");
     */

    // emit class attributes.
    for (Tag tag : sootClass.getTags()) {
      if (tag instanceof Attribute) {
        emit(".class_attribute " + tag.getName() + " \"" + String.valueOf(Base64.encode(((Attribute) tag).getValue()))
            + "\"");
        /*
         * else { emit(""); }
         */
      }
    }

    // emit synthetic attributes
    if (sootClass.hasTag(SyntheticTag.NAME) || Modifier.isSynthetic(sootClass.getModifiers())) {
      emit(".synthetic\n");
    }
    // emit inner class attributes
    if (!Options.v().no_output_inner_classes_attribute()) {
      InnerClassAttribute ica = (InnerClassAttribute) sootClass.getTag(InnerClassAttribute.NAME);
      if (ica != null) {
        List<InnerClassTag> specs = ica.getSpecs();
        if (!specs.isEmpty()) {
          emit(".inner_class_attr ");
          for (InnerClassTag ict : specs) {
            StringBuilder str = new StringBuilder(".inner_class_spec_attr ");
            str.append('"').append(ict.getInnerClass()).append("\" ");
            str.append('"').append(ict.getOuterClass()).append("\" ");
            str.append('"').append(ict.getShortName()).append("\" ");
            str.append(Modifier.toString(ict.getAccessFlags()));
            str.append(" .end .inner_class_spec_attr");
            emit(str.toString());
          }
          emit(".end .inner_class_attr\n");
        }
      }
    }
    {
      EnclosingMethodTag eMethTag = (EnclosingMethodTag) sootClass.getTag(EnclosingMethodTag.NAME);
      if (eMethTag != null) {
        StringBuilder encMeth = new StringBuilder(".enclosing_method_attr ");
        encMeth.append('"').append(eMethTag.getEnclosingClass()).append("\" ");
        encMeth.append('"').append(eMethTag.getEnclosingMethod()).append("\" ");
        encMeth.append('"').append(eMethTag.getEnclosingMethodSig()).append("\"\n");
        emit(encMeth.toString());
      }
    }
    // emit deprecated attributes
    if (sootClass.hasTag(DeprecatedTag.NAME)) {
      emit(".deprecated\n");
    }
    {
      SignatureTag sigTag = (SignatureTag) sootClass.getTag(SignatureTag.NAME);
      if (sigTag != null) {
        emit(".signature_attr " + "\"" + sigTag.getSignature() + "\"\n");
      }
    }

    for (Tag t : sootClass.getTags()) {
      if (VisibilityAnnotationTag.NAME.equals(t.getName())) {
        emit(getVisibilityAnnotationAttr((VisibilityAnnotationTag) t));
      }
    }

    // Emit the fields
    {
      for (SootField field : sootClass.getFields()) {
        StringBuilder fieldString = new StringBuilder();
        fieldString.append(".field ").append(Modifier.toString(field.getModifiers()));
        fieldString.append(" \"").append(field.getName()).append("\" ");
        fieldString.append(jasminDescriptorOf(field.getType()));

        TAG_LOOP: for (Tag t : field.getTags()) {
          switch (t.getName()) {
            case StringConstantValueTag.NAME:
              fieldString.append(" = ");
              fieldString.append(StringTools.getQuotedStringOf(((StringConstantValueTag) t).getStringValue()));
              break TAG_LOOP;
            case IntegerConstantValueTag.NAME:
              fieldString.append(" = ");
              fieldString.append(((IntegerConstantValueTag) t).getIntValue());
              break TAG_LOOP;
            case LongConstantValueTag.NAME:
              fieldString.append(" = ");
              fieldString.append(((LongConstantValueTag) t).getLongValue());
              break TAG_LOOP;
            case FloatConstantValueTag.NAME:
              fieldString.append(" = ");
              fieldString.append(floatToString(((FloatConstantValueTag) t).getFloatValue()));
              break TAG_LOOP;
            case DoubleConstantValueTag.NAME:
              fieldString.append(" = ");
              fieldString.append(doubleToString(((DoubleConstantValueTag) t).getDoubleValue()));
              break TAG_LOOP;
          }
        }

        if (field.hasTag(SyntheticTag.NAME) || Modifier.isSynthetic(field.getModifiers())) {
          fieldString.append(" .synthetic");
        }

        fieldString.append('\n');
        if (field.hasTag(DeprecatedTag.NAME)) {
          fieldString.append(".deprecated\n");
        }
        {
          SignatureTag sigTag = (SignatureTag) field.getTag(SignatureTag.NAME);
          if (sigTag != null) {
            fieldString.append(".signature_attr ");
            fieldString.append('"').append(sigTag.getSignature()).append("\"\n");
          }
        }

        for (Tag t : field.getTags()) {
          if (VisibilityAnnotationTag.NAME.equals(t.getName())) {
            fieldString.append(getVisibilityAnnotationAttr((VisibilityAnnotationTag) t));
          }
        }
        emit(fieldString.toString());

        for (Tag tag : field.getTags()) {
          if (tag instanceof Attribute) {
            emit(".field_attribute " + tag.getName() + " \"" + String.valueOf(Base64.encode(((Attribute) tag).getValue()))
                + "\"");
          }
        }
      }

      if (sootClass.getFieldCount() != 0) {
        emit("");
      }
    }

    // Emit the methods
    for (Iterator<SootMethod> methodIt = sootClass.methodIterator(); methodIt.hasNext();) {
      SootMethod next = methodIt.next();
      emitMethod(next);
      emit("");
    }

    if (Options.v().time()) {
      Timers.v().buildJasminTimer.end();
    }
  }

  protected void assignColorsToLocals(Body body) {
    if (Options.v().verbose()) {
      logger.debug("[" + body.getMethod().getName() + "] Assigning colors to locals...");
    }

    if (Options.v().time()) {
      Timers.v().packTimer.start();
    }

    localToGroup = new HashMap<Local, Object>(body.getLocalCount() * 2 + 1, 0.7f);
    groupToColorCount = new HashMap<Object, Integer>(body.getLocalCount() * 2 + 1, 0.7f);
    localToColor = new HashMap<Local, Integer>(body.getLocalCount() * 2 + 1, 0.7f);

    // Assign each local to a group, and set that group's color count to 0.
    for (Local l : body.getLocals()) {
      Object g = (sizeOfType(l.getType()) == 1) ? IntType.v() : LongType.v();

      localToGroup.put(l, g);
      groupToColorCount.putIfAbsent(g, 0);
    }

    // Assign colors to the parameter locals.
    for (Unit s : body.getUnits()) {
      if (s instanceof IdentityStmt) {
        Value leftOp = ((IdentityStmt) s).getLeftOp();
        if (leftOp instanceof Local) {
          Local l = (Local) leftOp;

          Object group = localToGroup.get(l);
          int count = groupToColorCount.get(group);

          localToColor.put(l, count);
          groupToColorCount.put(group, count + 1);
        }
      }
    }
  }

  protected void emitMethod(SootMethod method) {
    if (method.isPhantom()) {
      return;
    }

    // Emit prologue
    emit(".method " + Modifier.toString(method.getModifiers()) + " " + method.getName()
        + jasminDescriptorOf(method.makeRef()));

    for (SootClass exceptClass : method.getExceptions()) {
      emit(".throws " + exceptClass.getName());
    }
    if (method.hasTag(SyntheticTag.NAME) || Modifier.isSynthetic(method.getModifiers())) {
      emit(".synthetic");
    }
    if (method.hasTag(DeprecatedTag.NAME)) {
      emit(".deprecated");
    }
    {
      SignatureTag sigTag = (SignatureTag) method.getTag(SignatureTag.NAME);
      if (sigTag != null) {
        emit(".signature_attr " + "\"" + sigTag.getSignature() + "\"");
      }
    }
    {
      AnnotationDefaultTag annotDefTag = (AnnotationDefaultTag) method.getTag(AnnotationDefaultTag.NAME);
      if (annotDefTag != null) {
        emit(".annotation_default " + getElemAttr(annotDefTag.getDefaultVal()) + ".end .annotation_default");
      }
    }

    for (Tag t : method.getTags()) {
      String name = t.getName();
      if (VisibilityAnnotationTag.NAME.equals(name)) {
        emit(getVisibilityAnnotationAttr((VisibilityAnnotationTag) t));
      } else if (VisibilityParameterAnnotationTag.NAME.equals(name)) {
        emit(getVisibilityParameterAnnotationAttr((VisibilityParameterAnnotationTag) t));
      }
    }

    if (method.isConcrete()) {
      if (!method.hasActiveBody()) {
        throw new RuntimeException("method: " + method.getName() + " has no active body!");
      } else {
        emitMethodBody(method);
      }
    }

    // Emit epilogue
    emit(".end method");

    for (Tag tag : method.getTags()) {
      if (tag instanceof Attribute) {
        emit(".method_attribute " + tag.getName() + " \"" + String.valueOf(Base64.encode(tag.getValue())) + "\"");
      }
    }
  }

  protected abstract void emitMethodBody(SootMethod method);

  public void print(PrintWriter out) {
    for (String s : code) {
      out.println(s);
    }
  }

  protected String doubleToString(DoubleConstant v) {
    String s = v.toString();
    switch (s) {
      case "#Infinity":
        return "+DoubleInfinity";
      case "#-Infinity":
        return "-DoubleInfinity";
      case "#NaN":
        return "+DoubleNaN";
      default:
        return s;
    }
  }

  protected String doubleToString(double d) {
    String s = Double.toString(d);
    switch (s) {
      case "NaN":
        return "+DoubleNaN";
      case "Infinity":
        return "+DoubleInfinity";
      case "-Infinity":
        return "-DoubleInfinity";
      default:
        return s;
    }
  }

  protected String floatToString(FloatConstant v) {
    String s = v.toString();
    switch (s) {
      case "#InfinityF":
        return "+FloatInfinity";
      case "#-InfinityF":
        return "-FloatInfinity";
      case "#NaNF":
        return "+FloatNaN";
      default:
        return s;
    }
  }

  protected String floatToString(float d) {
    String s = Float.toString(d);
    switch (s) {
      case "NaN":
        return "+FloatNaN";
      case "Infinity":
        return "+FloatInfinity";
      case "-Infinity":
        return "-FloatInfinity";
      default:
        return s;
    }
  }
}
