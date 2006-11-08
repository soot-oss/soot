package soot.jbco.gui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.*;
import java.io.*;
/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class JBCOViewer extends javax.swing.JFrame {

	{
		//Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


  private JMenuItem helpMenuItem;
  private JMenu jMenu5;
  private JMenuItem deleteMenuItem;
  private JMenuItem speedMenuItem;
  private JMenuItem sizeMenuItem;
  private JMenuItem protMenuItem;
  private JSeparator jSeparator1;
  private JMenuItem pasteMenuItem;
  private JLabel LabelMainClass;
  private JRadioButton RadioSummary;
  private JRadioButton RadioVerbose;
  private JPanel PanelExecute;
  private JPanel PanelTransforms;
  private JPanel PanelBasicOptions;
  private JTabbedPane TabbedPane;
  private JMenuItem copyMenuItem;
  private JMenuItem cutMenuItem;
  private JMenu jMenu4;
  private JMenuItem exitMenuItem;
  private JSeparator jSeparator2;
  private JTextField TextFieldConstraint;
  private JTextPane PaneExplain;
  private JList AvoidList;
  private JPanel jPanel1;
  private JTextField ClasspathTextField;
  private JLabel LabelClassPath;
  private JTextField TextFieldMain;
  private JMenuItem closeFileMenuItem;
  private JMenuItem saveAsMenuItem;
  private JMenuItem saveMenuItem;
  public JMenuItem openFileMenuItem;
  public JMenuItem newFileMenuItem;
  private JMenu jMenu3;
  private JMenuBar jMenuBar1;
  private ActionListener AvoidButtonListener;
  private JTextPane jTextPane1;
  private JRadioButton DebugRadio;
  private JTextField WorkingDirTextField;
  private JLabel LabelWorkingDir;
  private JTextPane DefaultClassPathPane;
  public JTextArea TextAreaOutput;
  public JScrollPane jScrollPane1;
  private JPanel jPanel2;
  private JTextField TextFieldMinMem;
  private JButton ButtonAddItem;
  private JComboBox ComboBoxDefWeight;
  private JLabel LabelDefWeight;
  private JLabel LabelTransformHeading;
  private JList ListTransforms;
  private JComboBox ComboWeight;
  private JLabel LabelOutputDir;
  private JTextField TextField;
  private JTextField TextFieldJVMArgs;
  private JLabel LabelJVM;
  private JTextField TextFieldMaxMem;
  private JLabel LabelMaxMem;
  private JLabel LabelMinMem;
  private JDialog jDialog1;
  private JTextField TextFieldOutputFolder;
  private JButton ButtonSaveOutput;
  private JButton ButtonRemove;
  private JFrame thisRef;
  private RunnerThread runner;
  
  static int previousSelected = -1;
  static ListModel models[] = new ListModel[20];
  static String[][] optionStrings = new String[][] { {"Rename Classes","Rename Methods","Rename Fields","Build API Buffer Methods","Build Library Buffer Classes","Goto Instruction Augmentation","Add Dead Switch Statements","Convert Arith. Expr. To Bit Ops","Convert Branches to JSR Instructions","Disobey Constructor Conventions","Reuse Duplicate Sequences","Replace If(Non)Nulls with Try-Catch","Indirect If Instructions","Pack Locals into Bitfields","Reorder Loads Above Ifs","Combine Try and Catch Blocks","Embed Constants in Fields","Partially Trap Switches"}, 
                                                     {"wjtp.jbco_cr",  "wjtp.jbco_mr",  "wjtp.jbco_fr", "wjtp.jbco_bapibm",        "wjtp.jbco_blbc",              "jtp.jbco_gia",                 "jtp.jbco_adss",             "jtp.jbco_cae2bo",                        "bb.jbco_cb2ji",                       "bb.jbco_dcc",                    "bb.jbco_rds",              "bb.jbco_riitcb",                     "bb.jbco_iii",             "bb.jbco_plvb",              "bb.jbco_rlaii",          "bb.jbco_ctbcb",               "bb.jbco_ecvf",             "bb.jbco_ptss"           }};
  
  static int[][] defaultWeights     = new int[][]  { {9,               9,               9,              9,                         9,                             9,                              6,                           9,                                        0,                                     0,                                3,                          9,                                    6,                         3,                           9,                        9,                             0,                          0,                       },
                                                     {0,               0,               0,              0,                         9,                             6,                              0,                           9,                                        9,                                     9,                                0,                          9,                                    0,                         0,                           9,                        9,                             0,                          9,                       },
                                                     {5,               5,               5,              6,                         9,                             9,                              5,                           9,                                        9,                                     5,                                7,                          9,                                    9,                         2,                           9,                        9,                             0,                          9,                       }};
  static String[] arguments = null;
  /**
  * Auto-generated main method to display this JFrame
  */
  public static void main(String[] args) {
    arguments = args;
    JBCOViewer inst = new JBCOViewer();
    inst.setVisible(true);
  }
  
  public JBCOViewer() {
    super();
    initGUI();
  }
  
  private void initGUI() {
    thisRef = this;
    try {
      {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("soot/jbco/gui/jbco.jpg")).getImage());
        this.setTitle("Java Bytecode Obfuscator");
      }
      {
        TabbedPane = new JTabbedPane();
        getContentPane().add(TabbedPane, BorderLayout.CENTER);
        {
          PanelBasicOptions = new JPanel();
          TabbedPane.addTab("Basic Options", null, PanelBasicOptions, null);
          PanelBasicOptions.setLayout(null);
          PanelBasicOptions.setPreferredSize(new java.awt.Dimension(623, 413));
          {
            RadioVerbose = new JRadioButton();
            PanelBasicOptions.add(RadioVerbose);
            RadioVerbose.setText("Verbose Output");
            RadioVerbose.setBounds(7, 9, 130, 26);
            RadioVerbose.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent evt) {
                if (RadioVerbose.isSelected())
                  RadioSummary.setSelected(false);
              }
            });
          }
          {
            RadioSummary = new JRadioButton();
            PanelBasicOptions.add(RadioSummary);
            RadioSummary.setText("Silent Output");
            RadioSummary.setBounds(147, 7, 140, 28);
            RadioSummary.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent evt) {
                if (RadioSummary.isSelected())
                  RadioVerbose.setSelected(false);
              }
            });
          }
          {
            LabelMainClass = new JLabel();
            PanelBasicOptions.add(LabelMainClass);
            LabelMainClass.setText("Main Class");
            LabelMainClass.setHorizontalTextPosition(SwingConstants.CENTER);
            LabelMainClass.setBounds(14, 98, 77, 28);
          }
          {
            TextFieldMain = new JTextField();
            PanelBasicOptions.add(TextFieldMain);
            TextFieldMain.setBounds(98, 98, 245, 28);
          }
          {
            ClasspathTextField = new JTextField();
            PanelBasicOptions.add(ClasspathTextField);
            ClasspathTextField.setBounds(98, 203, 511, 28);
          }
          {
            LabelClassPath = new JLabel();
            PanelBasicOptions.add(LabelClassPath);
            LabelClassPath.setText("Classpath");
            LabelClassPath.setBounds(14, 203, 77, 28);
          }
          {
            LabelMinMem = new JLabel();
            PanelBasicOptions.add(LabelMinMem);
            LabelMinMem.setText("Minimum Memory (MB)");
            LabelMinMem.setBounds(378, 7, 161, 28);
          }
          {
            LabelMaxMem = new JLabel();
            PanelBasicOptions.add(LabelMaxMem);
            LabelMaxMem.setText("Maximum Memory (MB)");
            LabelMaxMem.setBounds(378, 42, 161, 28);
          }
          {
            TextFieldMinMem = new JTextField();
            PanelBasicOptions.add(TextFieldMinMem);
            TextFieldMinMem.setBounds(546, 7, 63, 28);
            TextFieldMinMem.setText("256");
          }
          {
            TextFieldMaxMem = new JTextField();
            PanelBasicOptions.add(TextFieldMaxMem);
            TextFieldMaxMem.setText("1024");
            TextFieldMaxMem.setBounds(546, 42, 63, 28);
          }
          {
            LabelJVM = new JLabel();
            PanelBasicOptions.add(LabelJVM);
            LabelJVM.setText("JVM Args");
            LabelJVM.setBounds(14, 42, 77, 28);
          }
          {
            TextFieldJVMArgs = new JTextField();
            PanelBasicOptions.add(TextFieldJVMArgs);
            TextFieldJVMArgs.setBounds(98, 42, 245, 28);
          }
          {
            TextField = new JTextField();
            PanelBasicOptions.add(TextField);
            TextField.setBounds(98, 133, 511, 28);
          }
          {
            LabelOutputDir = new JLabel();
            PanelBasicOptions.add(LabelOutputDir);
            LabelOutputDir.setText("Output Dir");
            LabelOutputDir.setBounds(14, 133, 77, 28);
          }
          {
            jPanel2 = new JPanel();
            PanelBasicOptions.add(jPanel2);
            jPanel2.setBounds(14, 84, 595, 7);
            jPanel2.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
            jPanel2.setPreferredSize(new java.awt.Dimension(2, 2));
            jPanel2.setSize(595, 2);
          }
          {
            DefaultClassPathPane = new JTextPane();
            DefaultClassPathPane.setText("./:/usr/lib/jvm/java-1.5.0-sun-1.5.0.06/jre/lib/charsets.jar\n" +
                ":/usr/lib/jvm/java-1.5.0-sun-1.5.0.06/jre/lib/jce.jar\n" +
                ":/usr/lib/jvm/java-1.5.0-sun-1.5.0.06/jre/lib/jsse.jar\n" +
                ":/usr/lib/jvm/java-1.5.0-sun-1.5.0.06/jre/lib/rt.jar");
            
            if (arguments!=null) {
              for (int i = 0; i < arguments.length; i++) {
                if (arguments[i].equals("-cp") || arguments[i].equals("-classpath") && arguments.length>(i+1)) {
                  StringTokenizer cptokenizer = new StringTokenizer(arguments[i+1],":");
                  String cp = cptokenizer.nextToken();
                  while (cptokenizer.hasMoreTokens())
                    cp += "\n:" + cptokenizer.nextToken();
                  DefaultClassPathPane.setText(arguments[i+1]);
                }
              }
            }
            PanelBasicOptions.add(DefaultClassPathPane);
            DefaultClassPathPane.setBounds(98, 238, 518, 133);
          }
          {
            LabelWorkingDir = new JLabel();
            PanelBasicOptions.add(LabelWorkingDir);
            LabelWorkingDir.setText("Working Dir");
            LabelWorkingDir.setBounds(14, 168, 84, 28);
          }
          {
            WorkingDirTextField = new JTextField();
            WorkingDirTextField.setText(System.getProperty("user.dir"));
            PanelBasicOptions.add(WorkingDirTextField);
            WorkingDirTextField.setBounds(98, 168, 511, 28);
          }
          {
            DebugRadio = new JRadioButton();
            PanelBasicOptions.add(DebugRadio);
            DebugRadio.setText("Debug");
            DebugRadio.setBounds(280, 7, 84, 28);
          }
          {
            jTextPane1 = new JTextPane();
            PanelBasicOptions.add(jTextPane1);
            jTextPane1.setText("Default Classpath");
            jTextPane1.setBounds(14, 238, 84, 35);
          }
        }
        {
          PanelTransforms = new JPanel();
          TabbedPane.addTab("Transforms", null, PanelTransforms, null);
          PanelTransforms.setLayout(null);
          PanelTransforms.setPreferredSize(new java.awt.Dimension(630, 385));
          {
            jPanel1 = new JPanel();
            PanelTransforms.add(jPanel1);
            jPanel1.setBounds(245, 49, 378, 329);
            jPanel1.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0,0,0)));
            jPanel1.setLayout(null);
            {
              ListModel AvoidListModel = new DefaultComboBoxModel(new String[] {});
              AvoidList = new JList();
              jPanel1.add(AvoidList);
              AvoidList.setModel(AvoidListModel);
              AvoidList.setBounds(7, 112, 364, 210);
              AvoidList.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0,0,0)));
              AvoidList.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent evt) {
                  int length = AvoidList.getSelectedIndices().length;
                  if (length < 1) 
                    ButtonRemove.setEnabled(false);
                  else
                    ButtonRemove.setEnabled(true);
                }
              });
            }
            {
              TextFieldConstraint = new JTextField();
              jPanel1.add(TextFieldConstraint);
              TextFieldConstraint.setBounds(7, 42, 294, 28);
            }
            {
              ButtonRemove = new JButton();
              jPanel1.add(ButtonRemove);
              ButtonRemove.setText("Remove Item");
              ButtonRemove.setBounds(231, 77, 133, 28);
              ButtonRemove.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                  int index[] = AvoidList.getSelectedIndices();
                  if (index.length < 1) { 
                    java.awt.Toolkit.getDefaultToolkit().beep();
                    return;
                  }
                  
                  Object o[] = new Object[index.length];
                  DefaultComboBoxModel lm = (DefaultComboBoxModel)AvoidList.getModel();
                  for (int i = 0; i < index.length; i++)
                    o[i] = lm.getElementAt(index[i]);
                  for (int i = 0; i < index.length; i++)
                    lm.removeElement(o[i]);
                  
                  models[previousSelected] = lm;
                }
              });
            }
            {
              ComboBoxModel ComboWeightModel = new DefaultComboBoxModel(
                new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" });
              ComboWeight = new JComboBox();
              jPanel1.add(ComboWeight);
              ComboWeight.setModel(ComboWeightModel);
              ComboWeight.setBounds(308, 42, 56, 28);
            }
            {
              LabelTransformHeading = new JLabel();
              jPanel1.add(LabelTransformHeading);
              LabelTransformHeading.setText("Rename Classes");
              LabelTransformHeading.setBounds(7, 7, 182, 28);
            }
            {
              LabelDefWeight = new JLabel();
              jPanel1.add(LabelDefWeight);
              LabelDefWeight.setText("Default Weight");
              LabelDefWeight.setBounds(203, 7, 98, 28);
            }
            {
              ComboBoxModel ComboBoxDefWeightModel = new DefaultComboBoxModel(
                new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" });
              ComboBoxDefWeight = new JComboBox();
              jPanel1.add(ComboBoxDefWeight);
              ComboBoxDefWeight.setModel(ComboBoxDefWeightModel);
              ComboBoxDefWeight.setBounds(308, 7, 56, 28);
              ComboBoxDefWeight.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                  int index = ListTransforms.getSelectedIndex();
                  if (index < 0) return;
                  
                  DefaultComboBoxModel lm = (DefaultComboBoxModel)ListTransforms.getModel();
                  lm.removeElementAt(index);
                  lm.insertElementAt(optionStrings[0][index] + " - "+ComboBoxDefWeight.getSelectedItem(), index);
                }
              });
            }
            {
              ButtonAddItem = new JButton();
              jPanel1.add(ButtonAddItem);
              ButtonAddItem.setText("Add Item");
              ButtonAddItem.setBounds(91, 77, 133, 28);
              ButtonAddItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                  String text = TextFieldConstraint.getText();
                  if (text == null || text.trim().length() == 0) {
                    java.awt.Toolkit.getDefaultToolkit().beep();
                    return;
                  }
                  
                  boolean regex = text.startsWith("*");
                  if (regex) {
                    try {
                      java.util.regex.Pattern.compile(text.substring(1));
                    } catch (java.util.regex.PatternSyntaxException pse) {
                      java.awt.Toolkit.getDefaultToolkit().beep();
                      return;
                    }
                  }
                  
                  DefaultComboBoxModel lm = (DefaultComboBoxModel)AvoidList.getModel();
                  int size = lm.getSize();
                  for (int i = 0; i < size; i++) {
                    String item = (String)lm.getElementAt(i);
                    if (item != null && item.equals(text)) {
                      TextFieldConstraint.setText("");
                      return;
                    }
                  }
                  
                  lm.addElement(text + " - " + ComboWeight.getSelectedItem());
                  
                  models[previousSelected] = lm;
                  
                  TextFieldConstraint.setText("");
                  ComboWeight.setSelectedIndex(0);
              }});
            }
          }
          {
            PaneExplain = new JTextPane();
            PanelTransforms.add(PaneExplain);
            PaneExplain
              .setText("Adjust transform weights and add restrictions for specific Classes, Methods, and Fields.");
            PaneExplain.setBounds(7, 7, 616, 35);
            PaneExplain.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            PaneExplain.setEditable(false);
          }
          {
            DefaultComboBoxModel ListTransformsModel = new DefaultComboBoxModel();
            for (int i = 0; i < optionStrings[0].length; i++)
              ListTransformsModel.addElement(optionStrings[0][i] + " - 9");
            
            ListTransforms = new JList();
            PanelTransforms.add(ListTransforms);
            ListTransforms.setModel(ListTransformsModel);
            ListTransforms.setBounds(7, 49, 238, 329);
            ListTransforms
              .addListSelectionListener(new ListSelectionListener() {
              public void valueChanged(ListSelectionEvent evt) {
                int selected[] = ListTransforms.getSelectedIndices();
                if (selected.length > 1) {
                  ListTransforms.setSelectedIndices(new int[0]);
                } else if (selected.length == 0) {
                  return;
                }
              
                String val = (String)ListTransforms.getSelectedValue();
                if (ListTransforms.getSelectedIndex() == previousSelected)
                  return;
                previousSelected = ListTransforms.getSelectedIndex();
                
                if (val.indexOf("-") > 0) {
                  String weight = val.substring(val.indexOf("-") + 1, val.length()).trim();
                  val = val.substring(0,val.indexOf("-"));
                  
                  try {
                    int w = Integer.parseInt(weight);
                    if (w < 0 || w > 10) 
                      weight = "0";
                  } catch (NumberFormatException nfe) {
                    weight = "0";
                  }
                  
                  ComboBoxDefWeight.setSelectedItem(weight);
                }
                LabelTransformHeading.setText(val);
                
                DefaultComboBoxModel lm = (DefaultComboBoxModel)models[previousSelected];
                if (lm == null)
                  lm = new DefaultComboBoxModel(new String[0]);
                AvoidList.setModel(lm);
              }
              });
          }
        }
        {
          PanelExecute = new JPanel();
          TabbedPane.addTab("Output", null, PanelExecute, null);
          PanelExecute.setLayout(null);
          PanelExecute.setPreferredSize(new java.awt.Dimension(623, 427));
          {
            ButtonSaveOutput = new JButton();
            PanelExecute.add(ButtonSaveOutput);
            ButtonSaveOutput.setText("Save Output To File:");
            ButtonSaveOutput.setBounds(7, 382, 182, 28);
            ButtonSaveOutput.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent evt) {
                String file = TextFieldOutputFolder.getText();
                if (file.startsWith("~"))
                  file = System.getProperty("user.home") + file.substring(1);
                
                try {
                  File f = new File(file);
                  if (!f.getParentFile().exists() || !f.getParentFile().isDirectory())
                    throw new Exception("Directory does not appear to exist");
                  if (f.exists() && f.isDirectory())
                    throw new Exception("File points to a directory");
                  if (f.exists())  
                    f.delete();
                  f.createNewFile();
                  RandomAccessFile rf = new RandomAccessFile(f, "rw");
                  try {
                    rf.write(TextAreaOutput.getText().getBytes());
                  } catch (Exception exc) {
                    throw exc;
                  } finally {
                    rf.close();
                  }
                } catch (Exception exc) {
                  new PopupDialog(thisRef, true, "Exception: "+exc.toString());
                }
              }
            });
          }
          {
            TextFieldOutputFolder = new JTextField();
            PanelExecute.add(TextFieldOutputFolder);
            TextFieldOutputFolder.setBounds(196, 382, 427, 28);
          }
          {
            TextAreaOutput = new JTextArea();
            TextAreaOutput.setFont(new java.awt.Font("Courier 10 Pitch",0,10));
            jScrollPane1 = new JScrollPane(TextAreaOutput);
            PanelExecute.add(jScrollPane1);
            jScrollPane1.setBounds(7, 0, 616, 378);
            jScrollPane1.setAutoscrolls(true);
          }
        }
      }
      this.setSize(640, 504);
      {
        jMenuBar1 = new JMenuBar();
        setJMenuBar(jMenuBar1);
        {
          jMenu3 = new JMenu();
          jMenuBar1.add(jMenu3);
          jMenu3.setText("File");
          {
            speedMenuItem = new JMenuItem();
            jMenu3.add(speedMenuItem);
            speedMenuItem.setText("Use Speed-Tuned Combo");
            speedMenuItem.addActionListener(new  ActionListener() {
              public void actionPerformed(ActionEvent evt) {
                DefaultComboBoxModel ListTransformsModel = new DefaultComboBoxModel();
                for (int i = 0; i < optionStrings[0].length; i++)
                  ListTransformsModel.addElement(optionStrings[0][i] + " - " + defaultWeights[0][i]);
                
                ListTransforms.setModel(ListTransformsModel);
              }
            });
            
            sizeMenuItem = new JMenuItem();
            jMenu3.add(sizeMenuItem);
            sizeMenuItem.setText("Use Size-Tuned Combo");
            sizeMenuItem.addActionListener(new  ActionListener() {
              public void actionPerformed(ActionEvent evt) {
                DefaultComboBoxModel ListTransformsModel = new DefaultComboBoxModel();
                for (int i = 0; i < optionStrings[0].length; i++)
                  ListTransformsModel.addElement(optionStrings[0][i] + " - " + defaultWeights[1][i]);
                
                ListTransforms.setModel(ListTransformsModel);
              }
            });
            
            protMenuItem = new JMenuItem();
            jMenu3.add(protMenuItem);
            protMenuItem.setText("Use Protection-Tuned Combo");
            protMenuItem.addActionListener(new  ActionListener() {
              public void actionPerformed(ActionEvent evt) {
                DefaultComboBoxModel ListTransformsModel = new DefaultComboBoxModel();
                for (int i = 0; i < optionStrings[0].length; i++)
                  ListTransformsModel.addElement(optionStrings[0][i] + " - " + defaultWeights[2][i]);
                
                ListTransforms.setModel(ListTransformsModel);
              }
            });
            
            newFileMenuItem = new JMenuItem();
            jMenu3.add(newFileMenuItem);
            newFileMenuItem.setText("Execute");
            newFileMenuItem.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent evt) {
                String main = TextFieldMain.getText().trim();
                if (main.length() == 0) { 
                  new PopupDialog(thisRef, true, "No Main Class Specified");
                  return;
                }
                
                String cp = ClasspathTextField.getText().trim();
                if (cp.length() == 0) { 
                  StringTokenizer cptokenizer = new StringTokenizer(DefaultClassPathPane.getText());
                  cp = cptokenizer.nextToken();
                  while (cptokenizer.hasMoreTokens())
                    cp += cptokenizer.nextToken();
                }
                
                Integer min = null, max = null;
                try {
                  min = new Integer(TextFieldMinMem.getText());
                } catch (NumberFormatException nfe) {
                  min = null;
                }
                try {
                  max = new Integer(TextFieldMaxMem.getText());
                } catch (NumberFormatException nfe) {
                  max = null;
                }
                
                Vector tmp = new Vector();
                String args = TextFieldJVMArgs.getText();
                StringTokenizer st = new StringTokenizer(args,",");
                while (st.hasMoreTokens())
                  tmp.add(st.nextToken());
                
                boolean customclasspath = false;
                String vmargs[] = new String[tmp.size() + (min == null ? 0 : 1) + + (max == null ? 0 : 1)];
                for (int i = 0; i < tmp.size(); i++) {
                  vmargs[i] = (String)tmp.get(i);
                  if (vmargs[i].startsWith("-cp") || vmargs[i].startsWith("-classpath"))
                    customclasspath = true;
                }
                if (min != null) {
                  vmargs[tmp.size()] = "-Xms"+min.intValue()+"m";
                  if (max != null)
                    vmargs[tmp.size()+1] = "-Xmx"+max.intValue()+"m";
                } else if (max != null){
                  vmargs[tmp.size()] = "-Xmx"+max.intValue()+"m";
                }
                
                Vector trans = new Vector();
                ListModel lmy = (ListModel)ListTransforms.getModel();
                for (int i = 0; i < lmy.getSize(); i++) {
                  String text = (String)lmy.getElementAt(i);
                  for (int j = 0; j < optionStrings[0].length; j++)
                    if (text.startsWith(optionStrings[0][j])) {
                      String weight = "9";
                      if (text.lastIndexOf("-") > 0) {
                        weight = text.substring(text.lastIndexOf("-")+1).trim();
                        try {
                          Integer.parseInt(weight);
                        } catch (Exception exc) {
                          weight = "9";
                        }
                      }
                      trans.add("-t:"+weight+":"+optionStrings[1][j]);
                      
                      ListModel lmx = (ListModel)models[j];
                      if (lmx != null) {
                        for (int k = 0; k < lmx.getSize(); k++) {
                          String val = (String)lmx.getElementAt(k);
                          weight = val.substring(val.lastIndexOf("-")+1).trim();
                          val = val.substring(0,val.lastIndexOf("-")-1);
                          trans.add("-it:"+weight+":"+optionStrings[1][j]+":\""+val+"\"");
                        }
                      }
                      break;
                    }
                }
                String[] transforms = new String[trans.size()]; 
                trans.copyInto(transforms);
                trans = null;
                
                int index = 0;
                String outdir = TextField.getText();
                String[] cmdarray = new String[6 + (customclasspath ? 0 : 2) 
                                               + vmargs.length + transforms.length 
                                               + (RadioSummary.isSelected() ? 1 : 0)
                                               + (RadioVerbose.isSelected() ? 1 : 0)
                                               + (DebugRadio.isSelected() ? 1 : 0)
                                               + (outdir.length() > 0 ? 2 : 0)];
                cmdarray[index++] = "java";
                if (!customclasspath) {
                  cmdarray[index++] = "-cp";
                  cmdarray[index++] = System.getProperty("java.class.path");
                }
                System.arraycopy(vmargs, 0, cmdarray, index, vmargs.length);
                cmdarray[vmargs.length + index++] = "soot.jbco.Main";
                cmdarray[vmargs.length + index++] = "-cp";
                cmdarray[vmargs.length + index++] = cp;
                if (outdir.length() > 0) {
                  cmdarray[vmargs.length + index++] = "-d";
                  cmdarray[vmargs.length + index++] = outdir;
                }
                cmdarray[vmargs.length + index++] = "-app";
                cmdarray[vmargs.length + index++] = main;
                if (RadioSummary.isSelected()) 
                  cmdarray[vmargs.length + index++] = "-jbco:silent";
                if (RadioVerbose.isSelected()) 
                  cmdarray[vmargs.length + index++] = "-jbco:verbose";
                if (DebugRadio.isSelected()) 
                  cmdarray[vmargs.length + index++] = "-jbco:debug";
                System.arraycopy(transforms, 0, cmdarray, vmargs.length + index, transforms.length); 

                String output = "";
                for (int i = 0; i < cmdarray.length; i++)
                  output += cmdarray[i] + " ";
                output += "\n";

                TextAreaOutput.setText(output);
                TabbedPane.setSelectedComponent(PanelExecute);
                try {
                  runner = new RunnerThread(cmdarray, (JBCOViewer)thisRef, WorkingDirTextField.getText());
                  Thread t = new Thread(runner);
                  t.start();
                } catch (Exception exc) {
                  TextAreaOutput.append("\n\n" + exc.toString());
                  synchronized (runner) {
                    runner.stopRun = true;
                  }
                  runner = null;
                }
              }
            });
          }
          {
            openFileMenuItem = new JMenuItem();
            jMenu3.add(openFileMenuItem);
            openFileMenuItem.setEnabled(false);
            openFileMenuItem.setText("Stop");
            openFileMenuItem.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent evt) {
                if (runner != null) {
                  synchronized (runner) {
                    runner.stopRun = true;
                  }
                  runner = null;
                }
                openFileMenuItem.setEnabled(false);
              }
            });
          }
          /*{
            newFileMenuItem = new JMenuItem();
            jMenu3.add(newFileMenuItem);
            newFileMenuItem.setText("Stop");
          }
          {
            openFileMenuItem = new JMenuItem();
            jMenu3.add(openFileMenuItem);
            openFileMenuItem.setText("Open");
          }
          {
            saveMenuItem = new JMenuItem();
            jMenu3.add(saveMenuItem);
            saveMenuItem.setText("Save");
          }
          {
            saveAsMenuItem = new JMenuItem();
            jMenu3.add(saveAsMenuItem);
            saveAsMenuItem.setText("Save As ...");
          }
          {
            closeFileMenuItem = new JMenuItem();
            jMenu3.add(closeFileMenuItem);
            closeFileMenuItem.setText("Close");
          }
          {
            jSeparator2 = new JSeparator();
            jMenu3.add(jSeparator2);
          }*/
          {
            exitMenuItem = new JMenuItem();
            jMenu3.add(exitMenuItem);
            exitMenuItem.setText("Exit");
            exitMenuItem.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent evt) {
                System.exit(0);
              }
            });
          }
        }
        /*{
          jMenu4 = new JMenu();
          jMenuBar1.add(jMenu4);
          jMenu4.setText("Edit");
          {
            cutMenuItem = new JMenuItem();
            jMenu4.add(cutMenuItem);
            cutMenuItem.setText("Cut");
          }
          {
            copyMenuItem = new JMenuItem();
            jMenu4.add(copyMenuItem);
            copyMenuItem.setText("Copy");
          }
          {
            pasteMenuItem = new JMenuItem();
            jMenu4.add(pasteMenuItem);
            pasteMenuItem.setText("Paste");
          }
          {
            jSeparator1 = new JSeparator();
            jMenu4.add(jSeparator1);
          }
          {
            deleteMenuItem = new JMenuItem();
            jMenu4.add(deleteMenuItem);
            deleteMenuItem.setText("Delete");
          }
        }
        {
          jMenu5 = new JMenu();
          jMenuBar1.add(jMenu5);
          jMenu5.setText("Help");
          {
            helpMenuItem = new JMenuItem();
            jMenu5.add(helpMenuItem);
            helpMenuItem.setText("Help");
          }
        }*/
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
