package soot.asm;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2021 Timothy Hoffman
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

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import soot.Body;
import soot.SootMethod;
import soot.options.Options;
import soot.testing.framework.AbstractTestingFramework;
import soot.validation.CheckInitValidator;
import soot.validation.ValidationException;

/**
 * @author Timothy Hoffman
 */
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*" })
public class AsmMethodSourceOrigNamesTest extends AbstractTestingFramework {

  @Override
  protected void setupSoot() {
    final Options opts = Options.v();
    opts.set_validate(false);
    opts.setPhaseOption("jb", "use-original-names:true");
    opts.setPhaseOption("jb.sils", "enabled:false");
  }

  /**
   * Below is the output of "javap -verbose" for the relevant method. The '|' character was added to mark the ranges where
   * the LocalVariableTable specifies a variable name for local slot 9. Notice there is an additional use of slot 9 at offset
   * 126 which does not appear in any of the specified ranges which means the local variable name for that use is unknown.
   * 
   * <code><pre>
   *  public void write(char[], int, int) throws java.io.IOException;
   *    descriptor: ([CII)V
   *    flags: ACC_PUBLIC
   *    Code:
   *      stack=5, locals=10, args_size=4
   *         0: iconst_3
   *         1: iload_3
   *         2: imul
   *         3: istore        4
   *         5: iload         4
   *         7: sipush        16384
   *        10: aload_0
   *        11: getfield      #5                  // Field count:I
   *        14: isub
   *        15: if_icmplt     96
   *        18: aload_0
   *        19: invokevirtual #6                  // Method flushBuffer:()V
   *        22: iload         4
   *        24: sipush        16384
   *        27: if_icmplt     96
   *        30: iconst_1
   *        31: iload_3
   *        32: sipush        5461
   *        35: idiv
   *        36: iadd
   *        37: istore        5
   *        39: iconst_0
   *        40: istore        6
   *        42: goto          88
   *        45: iload_2
   *        46: iload_3
   *        47: iload         6
   *        49: imul
   *        50: iload         5
   *        52: idiv
   *        53: iadd
   *        54: istore        7
   *        56: iload_2
   *        57: iload_3
   *        58: iload         6
   *        60: iconst_1
   *        61: iadd
   *        62: imul
   *        63: iload         5
   *        65: idiv
   *        66: iadd
   *        67: istore        8
   *        69: iload         8
   *        71: iload         7
   *        73: isub
   *    |   74: istore        9
   *    |   76: aload_0
   *    |   77: aload_1
   *    |   78: iload         7
   *    |   80: iload         9
   *    |   82: invokevirtual #7                  // Method write:([CII)V
   *    |   85: iinc          6, 1
   *        88: iload         6
   *        90: iload         5
   *        92: if_icmplt     45
   *        95: return
   *        96: iload_3
   *        97: iload_2
   *        98: iadd
   *        99: istore        5
   *       101: aload_0
   *       102: getfield      #3                  // Field m_outputBytes:[B
   *       105: astore        6
   *       107: aload_0
   *       108: getfield      #5                  // Field count:I
   *       111: istore        7
   *       113: iload_2
   *       114: istore        8
   *       116: goto          133
   *       119: aload         6
   *       121: iload         7
   *       123: iinc          7, 1
   *    ?  126: iload         9
   *       128: i2b
   *       129: bastore
   *       130: iinc          8, 1
   *       133: iload         8
   *       135: iload         5
   *       137: if_icmpge     153
   *       140: aload_1
   *       141: iload         8
   *       143: caload
   *       144: dup
   *    |  145: istore        9
   *    |  147: sipush        128
   *    |  150: if_icmplt     119
   *    |  153: goto          291
   *       156: aload_1
   *       157: iload         8
   *       159: caload
   *    |  160: istore        9
   *    |  162: iload         9
   *    |  164: sipush        128
   *    |  167: if_icmpge     184
   *    |  170: aload         6
   *    |  172: iload         7
   *    |  174: iinc          7, 1
   *    |  177: iload         9
   *    |  179: i2b
   *    |  180: bastore
   *    |  181: goto          288
   *    |  184: iload         9
   *    |  186: sipush        2048
   *    |  189: if_icmpge     231
   *    |  192: aload         6
   *    |  194: iload         7
   *    |  196: iinc          7, 1
   *    |  199: sipush        192
   *    |  202: iload         9
   *    |  204: bipush        6
   *    |  206: ishr
   *    |  207: iadd
   *    |  208: i2b
   *    |  209: bastore
   *    |  210: aload         6
   *    |  212: iload         7
   *    |  214: iinc          7, 1
   *    |  217: sipush        128
   *    |  220: iload         9
   *    |  222: bipush        63
   *    |  224: iand
   *    |  225: iadd
   *    |  226: i2b
   *    |  227: bastore
   *    |  228: goto          288
   *    |  231: aload         6
   *    |  233: iload         7
   *    |  235: iinc          7, 1
   *    |  238: sipush        224
   *    |  241: iload         9
   *    |  243: bipush        12
   *    |  245: ishr
   *    |  246: iadd
   *    |  247: i2b
   *    |  248: bastore
   *    |  249: aload         6
   *    |  251: iload         7
   *    |  253: iinc          7, 1
   *    |  256: sipush        128
   *    |  259: iload         9
   *    |  261: bipush        6
   *    |  263: ishr
   *    |  264: bipush        63
   *    |  266: iand
   *    |  267: iadd
   *    |  268: i2b
   *    |  269: bastore
   *    |  270: aload         6
   *    |  272: iload         7
   *    |  274: iinc          7, 1
   *    |  277: sipush        128
   *    |  280: iload         9
   *    |  282: bipush        63
   *    |  284: iand
   *    |  285: iadd
   *    |  286: i2b
   *    |  287: bastore
   *    |  288: iinc          8, 1
   *       291: iload         8
   *       293: iload         5
   *       295: if_icmplt     156
   *       298: aload_0
   *       299: iload         7
   *       301: putfield      #5                  // Field count:I
   *       304: return
   *      LocalVariableTable:
   *        Start  Length  Slot  Name   Signature
   *            0     305     0  this   Lorg/apache/xml/serializer/WriterToUTF8Buffered;
   *            0     305     1 chars   [C
   *            0     305     2 start   I
   *            0     305     3 length   I
   *            5     299     4 lengthx3   I
   *           39      57     5 chunks   I
   *           42      54     6 chunk   I
   *           56      29     7 start_chunk   I
   *           69      16     8 end_chunk   I
   *           76       9     9 len_chunk   I
   *          101     203     5     n   I
   *          107     197     6 buf_loc   [B
   *          113     191     7 count_loc   I
   *          116     188     8     i   I
   *          147       6     9     c   C
   *          162     126     9     c   C
   *    Exceptions:
   *      throws java.io.IOException
   * </pre></code>
   */
  @Test
  public void testWriterToUTF8Buffered1() {
    final String clazz = "org.apache.xml.serializer.WriterToUTF8Buffered";
    final String[] params = { "char[]", "int", "int" };
    runXalanTest(prepareTarget(methodSigFromComponents(clazz, "void", "write", params), clazz));
  }

  /**
   * Below is the output of "javap -verbose" for the relevant method. The '|' character was added to mark the ranges where
   * the LocalVariableTable specifies a variable name for local slot 9. Notice there is an additional use of slot 9 at offset
   * 161 which does not appear in any of the specified ranges which means the local variable name for that use is unknown.
   * 
   * <code><pre>
   *  public void write(java.lang.String) throws java.io.IOException;
   *    descriptor: (Ljava/lang/String;)V
   *    flags: ACC_PUBLIC
   *    Code:
   *      stack=5, locals=10, args_size=2
   *         0: aload_1
   *         1: invokevirtual #9                  // Method java/lang/String.length:()I
   *         4: istore_2
   *         5: iconst_3
   *         6: iload_2
   *         7: imul
   *         8: istore_3
   *         9: iload_3
   *        10: sipush        16384
   *        13: aload_0
   *        14: getfield      #5                  // Field count:I
   *        17: isub
   *        18: if_icmplt     116
   *        21: aload_0
   *        22: invokevirtual #6                  // Method flushBuffer:()V
   *        25: iload_3
   *        26: sipush        16384
   *        29: if_icmplt     116
   *        32: iconst_0
   *        33: istore        4
   *        35: iconst_1
   *        36: iload_2
   *        37: sipush        5461
   *        40: idiv
   *        41: iadd
   *        42: istore        5
   *        44: iconst_0
   *        45: istore        6
   *        47: goto          108
   *        50: iconst_0
   *        51: iload_2
   *        52: iload         6
   *        54: imul
   *        55: iload         5
   *        57: idiv
   *        58: iadd
   *        59: istore        7
   *        61: iconst_0
   *        62: iload_2
   *        63: iload         6
   *        65: iconst_1
   *        66: iadd
   *        67: imul
   *        68: iload         5
   *        70: idiv
   *        71: iadd
   *        72: istore        8
   *        74: iload         8
   *        76: iload         7
   *        78: isub
   *    |   79: istore        9
   *    |   81: aload_1
   *    |   82: iload         7
   *    |   84: iload         8
   *    |   86: aload_0
   *    |   87: getfield      #4                  // Field m_inputChars:[C
   *    |   90: iconst_0
   *    |   91: invokevirtual #10                 // Method java/lang/String.getChars:(II[CI)V
   *    |   94: aload_0
   *    |   95: aload_0
   *    |   96: getfield      #4                  // Field m_inputChars:[C
   *    |   99: iconst_0
   *    |  100: iload         9
   *    |  102: invokevirtual #7                  // Method write:([CII)V
   *    |  105: iinc          6, 1
   *       108: iload         6
   *       110: iload         5
   *       112: if_icmplt     50
   *       115: return
   *       116: aload_1
   *       117: iconst_0
   *       118: iload_2
   *       119: aload_0
   *       120: getfield      #4                  // Field m_inputChars:[C
   *       123: iconst_0
   *       124: invokevirtual #10                 // Method java/lang/String.getChars:(II[CI)V
   *       127: aload_0
   *       128: getfield      #4                  // Field m_inputChars:[C
   *       131: astore        4
   *       133: iload_2
   *       134: istore        5
   *       136: aload_0
   *       137: getfield      #3                  // Field m_outputBytes:[B
   *       140: astore        6
   *       142: aload_0
   *       143: getfield      #5                  // Field count:I
   *       146: istore        7
   *       148: iconst_0
   *       149: istore        8
   *       151: goto          168
   *       154: aload         6
   *       156: iload         7
   *       158: iinc          7, 1
   *    ?  161: iload         9
   *       163: i2b
   *       164: bastore
   *       165: iinc          8, 1
   *       168: iload         8
   *       170: iload         5
   *       172: if_icmpge     189
   *       175: aload         4
   *       177: iload         8
   *       179: caload
   *       180: dup
   *    |  181: istore        9
   *    |  183: sipush        128
   *    |  186: if_icmplt     154
   *    |  189: goto          328
   *       192: aload         4
   *       194: iload         8
   *       196: caload
   *    |  197: istore        9
   *    |  199: iload         9
   *    |  201: sipush        128
   *    |  204: if_icmpge     221
   *    |  207: aload         6
   *    |  209: iload         7
   *    |  211: iinc          7, 1
   *    |  214: iload         9
   *    |  216: i2b
   *    |  217: bastore
   *    |  218: goto          325
   *    |  221: iload         9
   *    |  223: sipush        2048
   *    |  226: if_icmpge     268
   *    |  229: aload         6
   *    |  231: iload         7
   *    |  233: iinc          7, 1
   *    |  236: sipush        192
   *    |  239: iload         9
   *    |  241: bipush        6
   *    |  243: ishr
   *    |  244: iadd
   *    |  245: i2b
   *    |  246: bastore
   *    |  247: aload         6
   *    |  249: iload         7
   *    |  251: iinc          7, 1
   *    |  254: sipush        128
   *    |  257: iload         9
   *    |  259: bipush        63
   *    |  261: iand
   *    |  262: iadd
   *    |  263: i2b
   *    |  264: bastore
   *    |  265: goto          325
   *    |  268: aload         6
   *    |  270: iload         7
   *    |  272: iinc          7, 1
   *    |  275: sipush        224
   *    |  278: iload         9
   *    |  280: bipush        12
   *    |  282: ishr
   *    |  283: iadd
   *    |  284: i2b
   *    |  285: bastore
   *    |  286: aload         6
   *    |  288: iload         7
   *    |  290: iinc          7, 1
   *    |  293: sipush        128
   *    |  296: iload         9
   *    |  298: bipush        6
   *    |  300: ishr
   *    |  301: bipush        63
   *    |  303: iand
   *    |  304: iadd
   *    |  305: i2b
   *    |  306: bastore
   *    |  307: aload         6
   *    |  309: iload         7
   *    |  311: iinc          7, 1
   *    |  314: sipush        128
   *    |  317: iload         9
   *    |  319: bipush        63
   *    |  321: iand
   *    |  322: iadd
   *    |  323: i2b
   *    |  324: bastore
   *    |  325: iinc          8, 1
   *       328: iload         8
   *       330: iload         5
   *       332: if_icmplt     192
   *       335: aload_0
   *       336: iload         7
   *       338: putfield      #5                  // Field count:I
   *       341: return
   *      LocalVariableTable:
   *        Start  Length  Slot  Name   Signature
   *            0     342     0  this   Lorg/apache/xml/serializer/WriterToUTF8Buffered;
   *            0     342     1     s   Ljava/lang/String;
   *            5     336     2 length   I
   *            9     332     3 lengthx3   I
   *           35      81     4 start   I
   *           44      72     5 chunks   I
   *           47      69     6 chunk   I
   *           61      44     7 start_chunk   I
   *           74      31     8 end_chunk   I
   *           81      24     9 len_chunk   I
   *          133     208     4 chars   [C
   *          136     205     5     n   I
   *          142     199     6 buf_loc   [B
   *          148     193     7 count_loc   I
   *          151     190     8     i   I
   *          183       6     9     c   C
   *          199     126     9     c   C
   *    Exceptions:
   *      throws java.io.IOException
   * </pre></code>
   */
  @Test
  public void testWriterToUTF8Buffered2() {
    final String clazz = "org.apache.xml.serializer.WriterToUTF8Buffered";
    final String[] params = { "java.lang.String" };
    runXalanTest(prepareTarget(methodSigFromComponents(clazz, "void", "write", params), clazz));
  }

  /**
   * Below is the output of "javap -verbose" for the relevant method. The '|' character was added to mark the ranges where
   * the LocalVariableTable specifies a variable name for local slot 20. Notice there are several additional uses of slot 20
   * at the locations marked with a '?' which do not appear in any of the specified ranges which means the local variable
   * names for those uses are unknown.
   * 
   * <code><pre>
   *  public void transformSelectedNodes(org.apache.xalan.transformer.TransformerImpl) throws javax.xml.transform.TransformerException;
   *    descriptor: (Lorg/apache/xalan/transformer/TransformerImpl;)V
   *    flags: ACC_PUBLIC
   *    Code:
   *      stack=8, locals=36, args_size=2
   *         0: aload_1
   *         1: invokevirtual #17                 // Method org/apache/xalan/transformer/TransformerImpl.getXPathContext:()Lorg/apache/xpath/XPathContext;
   *         4: astore_2
   *         5: aload_2
   *         6: invokevirtual #18                 // Method org/apache/xpath/XPathContext.getCurrentNode:()I
   *         9: istore_3
   *        10: aload_0
   *        11: getfield      #19                 // Field org/apache/xalan/templates/ElemForEach.m_selectExpression:Lorg/apache/xpath/Expression;
   *        14: aload_2
   *        15: iload_3
   *        16: invokevirtual #20                 // Method org/apache/xpath/Expression.asIterator:(Lorg/apache/xpath/XPathContext;I)Lorg/apache/xml/dtm/DTMIterator;
   *        19: astore        4
   *        21: aload_2
   *        22: invokevirtual #21                 // Method org/apache/xpath/XPathContext.getVarStack:()Lorg/apache/xpath/VariableStack;
   *        25: astore        5
   *        27: aload_0
   *        28: invokevirtual #22                 // Method org/apache/xalan/templates/ElemCallTemplate.getParamElemCount:()I
   *        31: istore        6
   *        33: aload         5
   *        35: invokevirtual #23                 // Method org/apache/xpath/VariableStack.getStackFrame:()I
   *        38: istore        7
   *        40: aload_1
   *        41: invokevirtual #24                 // Method org/apache/xalan/transformer/TransformerImpl.getStackGuard:()Lorg/apache/xalan/transformer/StackGuard;
   *        44: astore        8
   *        46: aload         8
   *        48: invokevirtual #25                 // Method org/apache/xalan/transformer/StackGuard.getRecursionLimit:()I
   *        51: iconst_m1
   *        52: if_icmple     59
   *        55: iconst_1
   *        56: goto          60
   *        59: iconst_0
   *        60: istore        9
   *        62: iconst_0
   *        63: istore        10
   *        65: aload_2
   *        66: iconst_m1
   *        67: invokevirtual #26                 // Method org/apache/xpath/XPathContext.pushCurrentNode:(I)V
   *        70: aload_2
   *        71: iconst_m1
   *        72: invokevirtual #27                 // Method org/apache/xpath/XPathContext.pushCurrentExpressionNode:(I)V
   *        75: aload_2
   *        76: invokevirtual #28                 // Method org/apache/xpath/XPathContext.pushSAXLocatorNull:()V
   *        79: aload_1
   *        80: aconst_null
   *        81: invokevirtual #29                 // Method org/apache/xalan/transformer/TransformerImpl.pushElemTemplateElement:(Lorg/apache/xalan/templates/ElemTemplateElement;)V
   *        84: aload_0
   *        85: getfield      #30                 // Field org/apache/xalan/templates/ElemForEach.m_sortElems:Ljava/util/Vector;
   *        88: ifnonnull     95
   *        91: aconst_null
   *        92: goto          101
   *        95: aload_1
   *        96: aload_0
   *        97: iload_3
   *        98: invokevirtual #31                 // Method org/apache/xalan/transformer/TransformerImpl.processSortKeys:(Lorg/apache/xalan/templates/ElemForEach;I)Ljava/util/Vector;
   *       101: astore        11
   *       103: aconst_null
   *       104: aload         11
   *       106: if_acmpeq     120
   *       109: aload_0
   *       110: aload_2
   *       111: aload         11
   *       113: aload         4
   *       115: invokevirtual #32                 // Method org/apache/xalan/templates/ElemForEach.sortNodes:(Lorg/apache/xpath/XPathContext;Ljava/util/Vector;Lorg/apache/xml/dtm/DTMIterator;)Lorg/apache/xml/dtm/DTMIterator;
   *       118: astore        4
   *       120: getstatic     #10                 // Field org/apache/xalan/transformer/TransformerImpl.S_DEBUG:Z
   *       123: ifeq          157
   *       126: aload_1
   *       127: invokevirtual #11                 // Method org/apache/xalan/transformer/TransformerImpl.getTraceManager:()Lorg/apache/xalan/trace/TraceManager;
   *       130: iload_3
   *       131: aload_0
   *       132: ldc           #33                 // String select
   *       134: new           #34                 // class org/apache/xpath/XPath
   *       137: dup
   *       138: aload_0
   *       139: getfield      #19                 // Field org/apache/xalan/templates/ElemForEach.m_selectExpression:Lorg/apache/xpath/Expression;
   *       142: invokespecial #35                 // Method org/apache/xpath/XPath."<init>":(Lorg/apache/xpath/Expression;)V
   *       145: new           #36                 // class org/apache/xpath/objects/XNodeSet
   *       148: dup
   *       149: aload         4
   *       151: invokespecial #37                 // Method org/apache/xpath/objects/XNodeSet."<init>":(Lorg/apache/xml/dtm/DTMIterator;)V
   *       154: invokevirtual #38                 // Method org/apache/xalan/trace/TraceManager.fireSelectedEvent:(ILorg/apache/xalan/templates/ElemTemplateElement;Ljava/lang/String;Lorg/apache/xpath/XPath;Lorg/apache/xpath/objects/XObject;)V
   *       157: aload_1
   *       158: invokevirtual #39                 // Method org/apache/xalan/transformer/TransformerImpl.getSerializationHandler:()Lorg/apache/xml/serializer/SerializationHandler;
   *       161: astore        12
   *       163: aload_1
   *       164: invokevirtual #40                 // Method org/apache/xalan/transformer/TransformerImpl.getStylesheet:()Lorg/apache/xalan/templates/StylesheetRoot;
   *       167: astore        13
   *       169: aload         13
   *       171: invokevirtual #41                 // Method org/apache/xalan/templates/StylesheetRoot.getTemplateListComposed:()Lorg/apache/xalan/templates/TemplateList;
   *       174: astore        14
   *       176: aload_1
   *       177: invokevirtual #42                 // Method org/apache/xalan/transformer/TransformerImpl.getQuietConflictWarnings:()Z
   *       180: istore        15
   *       182: aload_2
   *       183: iload_3
   *       184: invokevirtual #43                 // Method org/apache/xpath/XPathContext.getDTM:(I)Lorg/apache/xml/dtm/DTM;
   *       187: astore        16
   *       189: iconst_m1
   *       190: istore        17
   *       192: iload         6
   *       194: ifle          295
   *       197: aload         5
   *       199: iload         6
   *       201: invokevirtual #44                 // Method org/apache/xpath/VariableStack.link:(I)I
   *       204: istore        17
   *       206: aload         5
   *       208: iload         7
   *       210: invokevirtual #45                 // Method org/apache/xpath/VariableStack.setStackFrame:(I)V
   *       213: iconst_0
   *       214: istore        18
   *       216: goto          281
   *       219: aload_0
   *       220: getfield      #46                 // Field org/apache/xalan/templates/ElemCallTemplate.m_paramElems:[Lorg/apache/xalan/templates/ElemWithParam;
   *       223: iload         18
   *       225: aaload
   *       226: astore        19
   *       228: getstatic     #10                 // Field org/apache/xalan/transformer/TransformerImpl.S_DEBUG:Z
   *       231: ifeq          243
   *       234: aload_1
   *       235: invokevirtual #11                 // Method org/apache/xalan/transformer/TransformerImpl.getTraceManager:()Lorg/apache/xalan/trace/TraceManager;
   *       238: aload         19
   *       240: invokevirtual #12                 // Method org/apache/xalan/trace/TraceManager.fireTraceEvent:(Lorg/apache/xalan/templates/ElemTemplateElement;)V
   *       243: aload         19
   *       245: aload_1
   *       246: iload_3
   *       247: invokevirtual #47                 // Method org/apache/xalan/templates/ElemWithParam.getValue:(Lorg/apache/xalan/transformer/TransformerImpl;I)Lorg/apache/xpath/objects/XObject;
   *    |  250: astore        20
   *    |  252: getstatic     #10                 // Field org/apache/xalan/transformer/TransformerImpl.S_DEBUG:Z
   *    |  255: ifeq          267
   *    |  258: aload_1
   *    |  259: invokevirtual #11                 // Method org/apache/xalan/transformer/TransformerImpl.getTraceManager:()Lorg/apache/xalan/trace/TraceManager;
   *    |  262: aload         19
   *    |  264: invokevirtual #14                 // Method org/apache/xalan/trace/TraceManager.fireTraceEndEvent:(Lorg/apache/xalan/templates/ElemTemplateElement;)V
   *    |  267: aload         5
   *    |  269: iload         18
   *    |  271: aload         20
   *    |  273: iload         17
   *    |  275: invokevirtual #48                 // Method org/apache/xpath/VariableStack.setLocalVariable:(ILorg/apache/xpath/objects/XObject;I)V
   *    |  278: iinc          18, 1
   *       281: iload         18
   *       283: iload         6
   *       285: if_icmplt     219
   *       288: aload         5
   *       290: iload         17
   *       292: invokevirtual #45                 // Method org/apache/xpath/VariableStack.setStackFrame:(I)V
   *       295: aload_2
   *       296: aload         4
   *       298: invokevirtual #49                 // Method org/apache/xpath/XPathContext.pushContextNodeList:(Lorg/apache/xml/dtm/DTMIterator;)V
   *       301: iconst_1
   *       302: istore        10
   *       304: aload_2
   *       305: invokevirtual #50                 // Method org/apache/xpath/XPathContext.getCurrentNodeStack:()Lorg/apache/xml/utils/IntStack;
   *       308: astore        18
   *       310: aload_2
   *       311: invokevirtual #51                 // Method org/apache/xpath/XPathContext.getCurrentExpressionNodeStack:()Lorg/apache/xml/utils/IntStack;
   *       314: astore        19
   *       316: goto          831
   *       319: aload         18
   *    ?  321: iload         20
   *       323: invokevirtual #52                 // Method org/apache/xml/utils/IntStack.setTop:(I)V
   *       326: aload         19
   *    ?  328: iload         20
   *       330: invokevirtual #52                 // Method org/apache/xml/utils/IntStack.setTop:(I)V
   *       333: aload_2
   *    ?  334: iload         20
   *       336: invokevirtual #43                 // Method org/apache/xpath/XPathContext.getDTM:(I)Lorg/apache/xml/dtm/DTM;
   *       339: aload         16
   *       341: if_acmpeq     352
   *       344: aload_2
   *    ?  345: iload         20
   *       347: invokevirtual #43                 // Method org/apache/xpath/XPathContext.getDTM:(I)Lorg/apache/xml/dtm/DTM;
   *       350: astore        16
   *       352: aload         16
   *    ?  354: iload         20
   *       356: invokeinterface #53,  2           // InterfaceMethod org/apache/xml/dtm/DTM.getExpandedTypeID:(I)I
   *       361: istore        21
   *       363: aload         16
   *    ?  365: iload         20
   *       367: invokeinterface #54,  2           // InterfaceMethod org/apache/xml/dtm/DTM.getNodeType:(I)S
   *       372: istore        22
   *       374: aload_1
   *       375: invokevirtual #7                  // Method org/apache/xalan/transformer/TransformerImpl.getMode:()Lorg/apache/xml/utils/QName;
   *       378: astore        23
   *       380: aload         14
   *       382: aload_2
   *    ?  383: iload         20
   *       385: iload         21
   *       387: aload         23
   *       389: iconst_m1
   *       390: iload         15
   *       392: aload         16
   *       394: invokevirtual #55                 // Method org/apache/xalan/templates/TemplateList.getTemplateFast:(Lorg/apache/xpath/XPathContext;IILorg/apache/xml/utils/QName;IZLorg/apache/xml/dtm/DTM;)Lorg/apache/xalan/templates/ElemTemplate;
   *       397: astore        24
   *       399: aconst_null
   *       400: aload         24
   *       402: if_acmpne     526
   *       405: iload         22
   *       407: tableswitch   { // 1 to 11
   *
   *                       1: 464
   *
   *                       2: 474
   *
   *                       3: 474
   *
   *                       4: 474
   *
   *                       5: 523
   *
   *                       6: 523
   *
   *                       7: 523
   *
   *                       8: 523
   *
   *                       9: 513
   *
   *                      10: 523
   *
   *                      11: 464
   *                 default: 523
   *            }
   *       464: aload         13
   *       466: invokevirtual #56                 // Method org/apache/xalan/templates/StylesheetRoot.getDefaultRule:()Lorg/apache/xalan/templates/ElemTemplate;
   *       469: astore        24
   *       471: goto          532
   *       474: aload_1
   *       475: aload         13
   *       477: invokevirtual #57                 // Method org/apache/xalan/templates/StylesheetRoot.getDefaultTextRule:()Lorg/apache/xalan/templates/ElemTemplate;
   *    ?  480: iload         20
   *       482: invokevirtual #58                 // Method org/apache/xalan/transformer/TransformerImpl.pushPairCurrentMatched:(Lorg/apache/xalan/templates/ElemTemplateElement;I)V
   *       485: aload_1
   *       486: aload         13
   *       488: invokevirtual #57                 // Method org/apache/xalan/templates/StylesheetRoot.getDefaultTextRule:()Lorg/apache/xalan/templates/ElemTemplate;
   *       491: invokevirtual #59                 // Method org/apache/xalan/transformer/TransformerImpl.setCurrentElement:(Lorg/apache/xalan/templates/ElemTemplateElement;)V
   *       494: aload         16
   *    ?  496: iload         20
   *       498: aload         12
   *       500: iconst_0
   *       501: invokeinterface #60,  4           // InterfaceMethod org/apache/xml/dtm/DTM.dispatchCharactersEvents:(ILorg/xml/sax/ContentHandler;Z)V
   *       506: aload_1
   *       507: invokevirtual #61                 // Method org/apache/xalan/transformer/TransformerImpl.popCurrentMatched:()V
   *       510: goto          831
   *       513: aload         13
   *       515: invokevirtual #62                 // Method org/apache/xalan/templates/StylesheetRoot.getDefaultRootRule:()Lorg/apache/xalan/templates/ElemTemplate;
   *       518: astore        24
   *       520: goto          532
   *       523: goto          831
   *       526: aload_1
   *       527: aload         24
   *       529: invokevirtual #59                 // Method org/apache/xalan/transformer/TransformerImpl.setCurrentElement:(Lorg/apache/xalan/templates/ElemTemplateElement;)V
   *       532: aload_1
   *       533: aload         24
   *    ?  535: iload         20
   *       537: invokevirtual #58                 // Method org/apache/xalan/transformer/TransformerImpl.pushPairCurrentMatched:(Lorg/apache/xalan/templates/ElemTemplateElement;I)V
   *       540: iload         9
   *       542: ifeq          550
   *       545: aload         8
   *       547: invokevirtual #63                 // Method org/apache/xalan/transformer/StackGuard.checkForInfinateLoop:()V
   *       550: aload         24
   *       552: getfield      #64                 // Field org/apache/xalan/templates/ElemTemplate.m_frameSize:I
   *       555: ifle          713
   *       558: aload_2
   *       559: invokevirtual #65                 // Method org/apache/xpath/XPathContext.pushRTFContext:()V
   *       562: aload         5
   *       564: invokevirtual #23                 // Method org/apache/xpath/VariableStack.getStackFrame:()I
   *       567: istore        25
   *       569: aload         5
   *       571: aload         24
   *       573: getfield      #64                 // Field org/apache/xalan/templates/ElemTemplate.m_frameSize:I
   *       576: invokevirtual #44                 // Method org/apache/xpath/VariableStack.link:(I)I
   *       579: pop
   *       580: aload         24
   *       582: getfield      #66                 // Field org/apache/xalan/templates/ElemTemplate.m_inArgsSize:I
   *       585: ifle          716
   *       588: iconst_0
   *       589: istore        26
   *       591: aload         24
   *       593: invokevirtual #67                 // Method org/apache/xalan/templates/ElemTemplateElement.getFirstChildElem:()Lorg/apache/xalan/templates/ElemTemplateElement;
   *       596: astore        27
   *       598: goto          704
   *       601: bipush        41
   *       603: aload         27
   *       605: invokevirtual #68                 // Method org/apache/xalan/templates/ElemTemplateElement.getXSLToken:()I
   *       608: if_icmpne     710
   *       611: aload         27
   *       613: checkcast     #69                 // class org/apache/xalan/templates/ElemParam
   *       616: astore        28
   *       618: iconst_0
   *       619: istore        29
   *       621: goto          672
   *       624: aload_0
   *       625: getfield      #46                 // Field org/apache/xalan/templates/ElemCallTemplate.m_paramElems:[Lorg/apache/xalan/templates/ElemWithParam;
   *       628: iload         29
   *       630: aaload
   *       631: astore        30
   *       633: aload         30
   *       635: getfield      #70                 // Field org/apache/xalan/templates/ElemWithParam.m_qnameID:I
   *       638: aload         28
   *       640: getfield      #71                 // Field org/apache/xalan/templates/ElemParam.m_qnameID:I
   *       643: if_icmpne     669
   *       646: aload         5
   *       648: iload         29
   *       650: iload         17
   *       652: invokevirtual #72                 // Method org/apache/xpath/VariableStack.getLocalVariable:(II)Lorg/apache/xpath/objects/XObject;
   *       655: astore        31
   *       657: aload         5
   *       659: iload         26
   *       661: aload         31
   *       663: invokevirtual #73                 // Method org/apache/xpath/VariableStack.setLocalVariable:(ILorg/apache/xpath/objects/XObject;)V
   *       666: goto          679
   *       669: iinc          29, 1
   *       672: iload         29
   *       674: iload         6
   *       676: if_icmplt     624
   *       679: iload         29
   *       681: iload         6
   *       683: if_icmpne     694
   *       686: aload         5
   *       688: iload         26
   *       690: aconst_null
   *       691: invokevirtual #73                 // Method org/apache/xpath/VariableStack.setLocalVariable:(ILorg/apache/xpath/objects/XObject;)V
   *       694: iinc          26, 1
   *       697: aload         27
   *       699: invokevirtual #74                 // Method org/apache/xalan/templates/ElemTemplateElement.getNextSiblingElem:()Lorg/apache/xalan/templates/ElemTemplateElement;
   *       702: astore        27
   *       704: aconst_null
   *       705: aload         27
   *       707: if_acmpne     601
   *       710: goto          716
   *       713: iconst_0
   *       714: istore        25
   *       716: getstatic     #10                 // Field org/apache/xalan/transformer/TransformerImpl.S_DEBUG:Z
   *       719: ifeq          731
   *       722: aload_1
   *       723: invokevirtual #11                 // Method org/apache/xalan/transformer/TransformerImpl.getTraceManager:()Lorg/apache/xalan/trace/TraceManager;
   *       726: aload         24
   *       728: invokevirtual #12                 // Method org/apache/xalan/trace/TraceManager.fireTraceEvent:(Lorg/apache/xalan/templates/ElemTemplateElement;)V
   *       731: aload         24
   *       733: getfield      #75                 // Field org/apache/xalan/templates/ElemTemplateElement.m_firstChild:Lorg/apache/xalan/templates/ElemTemplateElement;
   *       736: astore        26
   *       738: goto          788
   *       741: aload_2
   *       742: aload         26
   *       744: invokevirtual #76                 // Method org/apache/xpath/XPathContext.setSAXLocator:(Ljavax/xml/transform/SourceLocator;)V
   *       747: aload_1
   *       748: aload         26
   *       750: invokevirtual #29                 // Method org/apache/xalan/transformer/TransformerImpl.pushElemTemplateElement:(Lorg/apache/xalan/templates/ElemTemplateElement;)V
   *       753: aload         26
   *       755: aload_1
   *       756: invokevirtual #77                 // Method org/apache/xalan/templates/ElemTemplateElement.execute:(Lorg/apache/xalan/transformer/TransformerImpl;)V
   *       759: jsr           773
   *       762: goto          781
   *       765: astore        32
   *       767: jsr           773
   *       770: aload         32
   *       772: athrow
   *       773: astore        33
   *       775: aload_1
   *       776: invokevirtual #78                 // Method org/apache/xalan/transformer/TransformerImpl.popElemTemplateElement:()V
   *       779: ret           33
   *       781: aload         26
   *       783: getfield      #79                 // Field org/apache/xalan/templates/ElemTemplateElement.m_nextSibling:Lorg/apache/xalan/templates/ElemTemplateElement;
   *       786: astore        26
   *       788: aload         26
   *       790: ifnonnull     741
   *       793: getstatic     #10                 // Field org/apache/xalan/transformer/TransformerImpl.S_DEBUG:Z
   *       796: ifeq          808
   *       799: aload_1
   *       800: invokevirtual #11                 // Method org/apache/xalan/transformer/TransformerImpl.getTraceManager:()Lorg/apache/xalan/trace/TraceManager;
   *       803: aload         24
   *       805: invokevirtual #14                 // Method org/apache/xalan/trace/TraceManager.fireTraceEndEvent:(Lorg/apache/xalan/templates/ElemTemplateElement;)V
   *       808: aload         24
   *       810: getfield      #64                 // Field org/apache/xalan/templates/ElemTemplate.m_frameSize:I
   *       813: ifle          827
   *       816: aload         5
   *       818: iload         25
   *       820: invokevirtual #80                 // Method org/apache/xpath/VariableStack.unlink:(I)V
   *       823: aload_2
   *       824: invokevirtual #81                 // Method org/apache/xpath/XPathContext.popRTFContext:()V
   *       827: aload_1
   *       828: invokevirtual #61                 // Method org/apache/xalan/transformer/TransformerImpl.popCurrentMatched:()V
   *       831: iconst_m1
   *       832: aload         4
   *       834: invokeinterface #82,  1           // InterfaceMethod org/apache/xml/dtm/DTMIterator.nextNode:()I
   *       839: dup
   *    |  840: istore        20
   *    |  842: if_icmpne     319
   *    |  845: jsr           885
   *       848: goto          970
   *       851: astore        11
   *       853: aload_1
   *       854: invokevirtual #84                 // Method org/apache/xalan/transformer/TransformerImpl.getErrorListener:()Ljavax/xml/transform/ErrorListener;
   *       857: new           #85                 // class javax/xml/transform/TransformerException
   *       860: dup
   *       861: aload         11
   *       863: invokespecial #86                 // Method javax/xml/transform/TransformerException."<init>":(Ljava/lang/Throwable;)V
   *       866: invokeinterface #87,  2           // InterfaceMethod javax/xml/transform/ErrorListener.fatalError:(Ljavax/xml/transform/TransformerException;)V
   *       871: jsr           885
   *       874: goto          970
   *       877: astore        34
   *       879: jsr           885
   *       882: aload         34
   *       884: athrow
   *       885: astore        35
   *       887: getstatic     #10                 // Field org/apache/xalan/transformer/TransformerImpl.S_DEBUG:Z
   *       890: ifeq          924
   *       893: aload_1
   *       894: invokevirtual #11                 // Method org/apache/xalan/transformer/TransformerImpl.getTraceManager:()Lorg/apache/xalan/trace/TraceManager;
   *       897: iload_3
   *       898: aload_0
   *       899: ldc           #33                 // String select
   *       901: new           #34                 // class org/apache/xpath/XPath
   *       904: dup
   *       905: aload_0
   *       906: getfield      #19                 // Field org/apache/xalan/templates/ElemForEach.m_selectExpression:Lorg/apache/xpath/Expression;
   *       909: invokespecial #35                 // Method org/apache/xpath/XPath."<init>":(Lorg/apache/xpath/Expression;)V
   *       912: new           #36                 // class org/apache/xpath/objects/XNodeSet
   *       915: dup
   *       916: aload         4
   *       918: invokespecial #37                 // Method org/apache/xpath/objects/XNodeSet."<init>":(Lorg/apache/xml/dtm/DTMIterator;)V
   *       921: invokevirtual #88                 // Method org/apache/xalan/trace/TraceManager.fireSelectedEndEvent:(ILorg/apache/xalan/templates/ElemTemplateElement;Ljava/lang/String;Lorg/apache/xpath/XPath;Lorg/apache/xpath/objects/XObject;)V
   *       924: iload         6
   *       926: ifle          936
   *       929: aload         5
   *       931: iload         7
   *       933: invokevirtual #80                 // Method org/apache/xpath/VariableStack.unlink:(I)V
   *       936: aload_2
   *       937: invokevirtual #89                 // Method org/apache/xpath/XPathContext.popSAXLocator:()V
   *       940: iload         10
   *       942: ifeq          949
   *       945: aload_2
   *       946: invokevirtual #90                 // Method org/apache/xpath/XPathContext.popContextNodeList:()V
   *       949: aload_1
   *       950: invokevirtual #78                 // Method org/apache/xalan/transformer/TransformerImpl.popElemTemplateElement:()V
   *       953: aload_2
   *       954: invokevirtual #91                 // Method org/apache/xpath/XPathContext.popCurrentExpressionNode:()V
   *       957: aload_2
   *       958: invokevirtual #92                 // Method org/apache/xpath/XPathContext.popCurrentNode:()V
   *       961: aload         4
   *       963: invokeinterface #93,  1           // InterfaceMethod org/apache/xml/dtm/DTMIterator.detach:()V
   *       968: ret           35
   *       970: return
   *      Exception table:
   *         from    to  target type
   *           747   765   765   any
   *            65   845   851   Class org/xml/sax/SAXException
   *            65   877   877   any
   *      LocalVariableTable:
   *        Start  Length  Slot  Name   Signature
   *            0     971     0  this   Lorg/apache/xalan/templates/ElemApplyTemplates;
   *            0     971     1 transformer   Lorg/apache/xalan/transformer/TransformerImpl;
   *            5     965     2 xctxt   Lorg/apache/xpath/XPathContext;
   *           10     960     3 sourceNode   I
   *           21     949     4 sourceNodes   Lorg/apache/xml/dtm/DTMIterator;
   *           27     943     5  vars   Lorg/apache/xpath/VariableStack;
   *           33     937     6 nParams   I
   *           40     930     7 thisframe   I
   *           46     924     8 guard   Lorg/apache/xalan/transformer/StackGuard;
   *           62     908     9 check   Z
   *           65     905    10 pushContextNodeListFlag   Z
   *          103     742    11  keys   Ljava/util/Vector;
   *          163     682    12   rth   Lorg/apache/xml/serializer/SerializationHandler;
   *          169     676    13 sroot   Lorg/apache/xalan/templates/StylesheetRoot;
   *          176     669    14    tl   Lorg/apache/xalan/templates/TemplateList;
   *          182     663    15 quiet   Z
   *          189     656    16   dtm   Lorg/apache/xml/dtm/DTM;
   *          192     653    17 argsFrame   I
   *          216      79    18     i   I
   *          228      50    19   ewp   Lorg/apache/xalan/templates/ElemWithParam;
   *          252      26    20   obj   Lorg/apache/xpath/objects/XObject;
   *          310     535    18 currentNodes   Lorg/apache/xml/utils/IntStack;
   *          316     529    19 currentExpressionNodes   Lorg/apache/xml/utils/IntStack;
   *          842       3    20 child   I
   *          363     468    21 exNodeType   I
   *          374     457    22 nodeType   I
   *          380     451    23  mode   Lorg/apache/xml/utils/QName;
   *          399     432    24 template   Lorg/apache/xalan/templates/ElemTemplate;
   *          569     262    25 currentFrameBottom   I
   *          591     119    26 paramIndex   I
   *          598     112    27  elem   Lorg/apache/xalan/templates/ElemTemplateElement;
   *          618      76    28    ep   Lorg/apache/xalan/templates/ElemParam;
   *          621      73    29     i   I
   *          633      36    30   ewp   Lorg/apache/xalan/templates/ElemWithParam;
   *          657      12    31   obj   Lorg/apache/xpath/objects/XObject;
   *          738      93    26     t   Lorg/apache/xalan/templates/ElemTemplateElement;
   *          853     117    11    se   Lorg/xml/sax/SAXException;
   *    Exceptions:
   *      throws javax.xml.transform.TransformerException
   * </pre></code>
   */
  @Test
  public void testElemApplyTemplates() {
    final String clazz = "org.apache.xalan.templates.ElemApplyTemplates";
    final String[] params = { "org.apache.xalan.transformer.TransformerImpl" };
    runXalanTest(prepareTarget(methodSigFromComponents(clazz, "void", "transformSelectedNodes", params), clazz));
  }

  /**
   * Below is the output of "javap -verbose" for the relevant method. The '|' character was added to mark the ranges where
   * the LocalVariableTable specifies a variable name for local slot 10. Notice there is an additional use of slot 10 at
   * offset 53 which does not appear in any of the specified ranges which means the local variable name for that use is
   * unknown.
   * 
   * <code><pre>
   *  public boolean compare(org.apache.xpath.objects.XObject, org.apache.xpath.objects.Comparator) throws javax.xml.transform.TransformerException;
   *    descriptor: (Lorg/apache/xpath/objects/XObject;Lorg/apache/xpath/objects/Comparator;)Z
   *    flags: ACC_PUBLIC
   *    Code:
   *      stack=5, locals=12, args_size=3
   *         0: iconst_0
   *         1: istore_3
   *         2: aload_1
   *         3: invokevirtual #48                 // Method org/apache/xpath/objects/XObject.getType:()I
   *         6: istore        4
   *         8: iconst_4
   *         9: iload         4
   *        11: if_icmpne     193
   *        14: aload_0
   *        15: invokevirtual #49                 // Method iterRaw:()Lorg/apache/xml/dtm/DTMIterator;
   *        18: astore        5
   *        20: aload_1
   *        21: checkcast     #2                  // class org/apache/xpath/objects/XNodeSet
   *        24: invokevirtual #49                 // Method iterRaw:()Lorg/apache/xml/dtm/DTMIterator;
   *        27: astore        6
   *        29: aconst_null
   *        30: astore        8
   *        32: goto          162
   *        35: aload_0
   *        36: iload         7
   *        38: invokevirtual #26                 // Method getStringFromNode:(I)Lorg/apache/xml/utils/XMLString;
   *        41: astore        9
   *        43: aconst_null
   *        44: aload         8
   *        46: if_acmpne     115
   *        49: goto          98
   *        52: aload_0
   *    ?   53: iload         10
   *        55: invokevirtual #26                 // Method getStringFromNode:(I)Lorg/apache/xml/utils/XMLString;
   *        58: astore        11
   *        60: aload_2
   *        61: aload         9
   *        63: aload         11
   *        65: invokevirtual #50                 // Method org/apache/xpath/objects/Comparator.compareStrings:(Lorg/apache/xml/utils/XMLString;Lorg/apache/xml/utils/XMLString;)Z
   *        68: ifeq          76
   *        71: iconst_1
   *        72: istore_3
   *        73: goto          112
   *        76: aconst_null
   *        77: aload         8
   *        79: if_acmpne     91
   *        82: new           #51                 // class java/util/Vector
   *        85: dup
   *        86: invokespecial #52                 // Method java/util/Vector."<init>":()V
   *        89: astore        8
   *        91: aload         8
   *        93: aload         11
   *        95: invokevirtual #53                 // Method java/util/Vector.addElement:(Ljava/lang/Object;)V
   *        98: iconst_m1
   *        99: aload         6
   *       101: invokeinterface #54,  1           // InterfaceMethod org/apache/xml/dtm/DTMIterator.nextNode:()I
   *       106: dup
   *    |  107: istore        10
   *    |  109: if_icmpne     52
   *    |  112: goto          162
   *       115: aload         8
   *       117: invokevirtual #55                 // Method java/util/Vector.size:()I
   *    |  120: istore        10
   *    |  122: iconst_0
   *    |  123: istore        11
   *    |  125: goto          155
   *    |  128: aload_2
   *    |  129: aload         9
   *    |  131: aload         8
   *    |  133: iload         11
   *    |  135: invokevirtual #56                 // Method java/util/Vector.elementAt:(I)Ljava/lang/Object;
   *    |  138: checkcast     #57                 // class org/apache/xml/utils/XMLString
   *    |  141: invokevirtual #50                 // Method org/apache/xpath/objects/Comparator.compareStrings:(Lorg/apache/xml/utils/XMLString;Lorg/apache/xml/utils/XMLString;)Z
   *    |  144: ifeq          152
   *    |  147: iconst_1
   *    |  148: istore_3
   *    |  149: goto          162
   *    |  152: iinc          11, 1
   *    |  155: iload         11
   *    |  157: iload         10
   *    |  159: if_icmplt     128
   *    |  162: iconst_m1
   *       163: aload         5
   *       165: invokeinterface #54,  1           // InterfaceMethod org/apache/xml/dtm/DTMIterator.nextNode:()I
   *       170: dup
   *       171: istore        7
   *       173: if_icmpne     35
   *       176: aload         5
   *       178: invokeinterface #58,  1           // InterfaceMethod org/apache/xml/dtm/DTMIterator.reset:()V
   *       183: aload         6
   *       185: invokeinterface #58,  1           // InterfaceMethod org/apache/xml/dtm/DTMIterator.reset:()V
   *       190: goto          451
   *       193: iconst_1
   *       194: iload         4
   *       196: if_icmpne     231
   *       199: aload_0
   *       200: invokevirtual #59                 // Method bool:()Z
   *       203: ifeq          210
   *       206: dconst_1
   *       207: goto          211
   *       210: dconst_0
   *       211: dstore        5
   *       213: aload_1
   *       214: invokevirtual #60                 // Method org/apache/xpath/objects/XObject.num:()D
   *       217: dstore        7
   *       219: aload_2
   *       220: dload         5
   *       222: dload         7
   *       224: invokevirtual #61                 // Method org/apache/xpath/objects/Comparator.compareNumbers:(DD)Z
   *       227: istore_3
   *       228: goto          451
   *       231: iconst_2
   *       232: iload         4
   *       234: if_icmpne     300
   *       237: aload_0
   *       238: invokevirtual #49                 // Method iterRaw:()Lorg/apache/xml/dtm/DTMIterator;
   *       241: astore        5
   *       243: aload_1
   *       244: invokevirtual #60                 // Method org/apache/xpath/objects/XObject.num:()D
   *       247: dstore        6
   *       249: goto          276
   *       252: aload_0
   *       253: iload         8
   *       255: invokevirtual #20                 // Method getNumberFromNode:(I)D
   *       258: dstore        9
   *       260: aload_2
   *       261: dload         9
   *       263: dload         6
   *       265: invokevirtual #61                 // Method org/apache/xpath/objects/Comparator.compareNumbers:(DD)Z
   *       268: ifeq          276
   *       271: iconst_1
   *       272: istore_3
   *       273: goto          290
   *       276: iconst_m1
   *       277: aload         5
   *       279: invokeinterface #54,  1           // InterfaceMethod org/apache/xml/dtm/DTMIterator.nextNode:()I
   *       284: dup
   *       285: istore        8
   *       287: if_icmpne     252
   *       290: aload         5
   *       292: invokeinterface #58,  1           // InterfaceMethod org/apache/xml/dtm/DTMIterator.reset:()V
   *       297: goto          451
   *       300: iconst_5
   *       301: iload         4
   *       303: if_icmpne     369
   *       306: aload_1
   *       307: invokevirtual #62                 // Method org/apache/xpath/objects/XObject.xstr:()Lorg/apache/xml/utils/XMLString;
   *       310: astore        5
   *       312: aload_0
   *       313: invokevirtual #49                 // Method iterRaw:()Lorg/apache/xml/dtm/DTMIterator;
   *       316: astore        6
   *       318: goto          345
   *       321: aload_0
   *       322: iload         7
   *       324: invokevirtual #26                 // Method getStringFromNode:(I)Lorg/apache/xml/utils/XMLString;
   *       327: astore        8
   *       329: aload_2
   *       330: aload         8
   *       332: aload         5
   *       334: invokevirtual #50                 // Method org/apache/xpath/objects/Comparator.compareStrings:(Lorg/apache/xml/utils/XMLString;Lorg/apache/xml/utils/XMLString;)Z
   *       337: ifeq          345
   *       340: iconst_1
   *       341: istore_3
   *       342: goto          359
   *       345: iconst_m1
   *       346: aload         6
   *       348: invokeinterface #54,  1           // InterfaceMethod org/apache/xml/dtm/DTMIterator.nextNode:()I
   *       353: dup
   *       354: istore        7
   *       356: if_icmpne     321
   *       359: aload         6
   *       361: invokeinterface #58,  1           // InterfaceMethod org/apache/xml/dtm/DTMIterator.reset:()V
   *       366: goto          451
   *       369: iconst_3
   *       370: iload         4
   *       372: if_icmpne     438
   *       375: aload_1
   *       376: invokevirtual #62                 // Method org/apache/xpath/objects/XObject.xstr:()Lorg/apache/xml/utils/XMLString;
   *       379: astore        5
   *       381: aload_0
   *       382: invokevirtual #49                 // Method iterRaw:()Lorg/apache/xml/dtm/DTMIterator;
   *       385: astore        6
   *       387: goto          414
   *       390: aload_0
   *       391: iload         7
   *       393: invokevirtual #26                 // Method getStringFromNode:(I)Lorg/apache/xml/utils/XMLString;
   *       396: astore        8
   *       398: aload_2
   *       399: aload         8
   *       401: aload         5
   *       403: invokevirtual #50                 // Method org/apache/xpath/objects/Comparator.compareStrings:(Lorg/apache/xml/utils/XMLString;Lorg/apache/xml/utils/XMLString;)Z
   *       406: ifeq          414
   *       409: iconst_1
   *       410: istore_3
   *       411: goto          428
   *       414: iconst_m1
   *       415: aload         6
   *       417: invokeinterface #54,  1           // InterfaceMethod org/apache/xml/dtm/DTMIterator.nextNode:()I
   *       422: dup
   *       423: istore        7
   *       425: if_icmpne     390
   *       428: aload         6
   *       430: invokeinterface #58,  1           // InterfaceMethod org/apache/xml/dtm/DTMIterator.reset:()V
   *       435: goto          451
   *       438: aload_2
   *       439: aload_0
   *       440: invokevirtual #63                 // Method num:()D
   *       443: aload_1
   *       444: invokevirtual #60                 // Method org/apache/xpath/objects/XObject.num:()D
   *       447: invokevirtual #61                 // Method org/apache/xpath/objects/Comparator.compareNumbers:(DD)Z
   *       450: istore_3
   *       451: iload_3
   *       452: ireturn
   *      LocalVariableTable:
   *        Start  Length  Slot  Name   Signature
   *            0     453     0  this   Lorg/apache/xpath/objects/XNodeSet;
   *            0     453     1  obj2   Lorg/apache/xpath/objects/XObject;
   *            0     453     2 comparator   Lorg/apache/xpath/objects/Comparator;
   *            2     451     3 result   Z
   *            8     445     4  type   I
   *           20     170     5 list1   Lorg/apache/xml/dtm/DTMIterator;
   *           29     161     6 list2   Lorg/apache/xml/dtm/DTMIterator;
   *          173      17     7 node1   I
   *           32     158     8 node2Strings   Ljava/util/Vector;
   *           43     119     9    s1   Lorg/apache/xml/utils/XMLString;
   *          109       3    10 node2   I
   *           60      38    11    s2   Lorg/apache/xml/utils/XMLString;
   *          122      40    10     n   I
   *          125      37    11     i   I
   *          213      15     5  num1   D
   *          219       9     7  num2   D
   *          243      54     5 list1   Lorg/apache/xml/dtm/DTMIterator;
   *          249      48     6  num2   D
   *          287      10     8  node   I
   *          260      16     9  num1   D
   *          312      54     5    s2   Lorg/apache/xml/utils/XMLString;
   *          318      48     6 list1   Lorg/apache/xml/dtm/DTMIterator;
   *          356      10     7  node   I
   *          329      16     8    s1   Lorg/apache/xml/utils/XMLString;
   *          381      54     5    s2   Lorg/apache/xml/utils/XMLString;
   *          387      48     6 list1   Lorg/apache/xml/dtm/DTMIterator;
   *          425      10     7  node   I
   *          398      16     8    s1   Lorg/apache/xml/utils/XMLString;
   *    Exceptions:
   *      throws javax.xml.transform.TransformerException
   * </pre></code>
   */
  @Test
  public void testXNodeSet() {
    final String clazz = "org.apache.xpath.objects.XNodeSet";
    final String[] params = { "org.apache.xpath.objects.XObject", "org.apache.xpath.objects.Comparator" };
    runXalanTest(prepareTarget(methodSigFromComponents(clazz, "boolean", "compare", params), clazz));
  }

  private void runXalanTest(SootMethod m) {
    Body body = m.retrieveActiveBody();
    // Run CheckInitValidator to ensure the special case for "use-original-names"
    // in AsmMethodSource did not cause any problems when replacing locals.
    ArrayList<ValidationException> exceptions = new ArrayList<>();
    CheckInitValidator.INSTANCE.validate(body, exceptions);
    Assert.assertTrue(exceptions.isEmpty());
  }
}
