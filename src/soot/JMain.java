package soot;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.colorchooser.*;
import javax.swing.filechooser.*;
import javax.accessibility.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.io.*;
import java.applet.*;
import java.net.*;



public class JMain 
{
   
    
    static JTextField classField = new JTextField(20);
    static JCheckBox appModeCheckBox = new JCheckBox("Run in app mode");
    static JComboBox inputRepComboBox = new JComboBox();
    static JComboBox outputRepComboBox = new JComboBox();

    // optimization options
    static JCheckBox dash_O =  new JCheckBox("[-O]");
    static JCheckBox dash_W =  new JCheckBox("[-W]");
    static JComboBox finalRepBox = new JComboBox();

    // miscelaneous options
    static JTextField sootClasspath = new JTextField();
    static JCheckBox verboseCheckBox =  new JCheckBox("verbose");
    static JCheckBox debugCheckBox = new JCheckBox("debug");

    // execution output
    static JTextArea ref = new JTextArea();

    //runtime options
    static JButton sootifyButton = new JButton("Sootify Now");


    public static void main(String[] args)
    {
	
	
	JFrame mainFrame = new JFrame("Soot Bytecode Optimizer");
	Container pane = mainFrame.getContentPane();

	pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

	JPanel topLevelPanel = new JPanel();
	topLevelPanel.setLayout(new BoxLayout(topLevelPanel, BoxLayout.Y_AXIS));
	    
	//	topLevelPanel.setAlignmentY(JPanel.TOP_ALIGNMENT);
	//topLevelPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);

	pane.add(topLevelPanel);
			
	
	
	// classes to sootify
	{

	    JPanel appPanel = new JPanel(); 
	    Font boldFont = new Font("Dialog", Font.BOLD, 12);
	    Border appBorder = new  TitledBorder(null, "Class to Optimize", 
						 TitledBorder.LEFT, TitledBorder.TOP,
						 boldFont);	
	    appPanel.setBorder(appBorder);	       
	    appPanel.setLayout(new BoxLayout(appPanel, BoxLayout.Y_AXIS));
	    //	    appPanel.setAlignmentY(JPanel.TOP_ALIGNMENT);
	    //appPanel.setAlignmentX(JPanel.RIGHT_ALIGNMENT);


	    topLevelPanel.add(appPanel);
	
	    {
		JPanel topPanel = new JPanel();
		//		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		appPanel.add(topPanel);
   
		JLabel appLabel = new JLabel("Class Name");

		topPanel.add(classField);
		
		JButton browse = new JButton("Browse");
		browse.addActionListener( new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();

			javax.swing.filechooser.FileFilter ff = new javax.swing.filechooser.FileFilter() {
			    public boolean accept(File f) {
				String ext = null;
				String s = f.getName();
				int i = s.lastIndexOf('.');
				
				if (i > 0 &&  i < s.length() - 1) {
				    ext = s.substring(i+1).toLowerCase();
				}		
				if(ext != null)
				    return ext.equals("class");
				else
				    return false;
			    }
			    
			    public String getDescription() {
				return "Class Files (*.class)";
			    }
			};
			fc.setFileFilter(ff);
			fc.showOpenDialog(null);
			    
		    }});
		    topPanel.add(browse);

		    topPanel.add(appModeCheckBox);
		
	    }

	    {
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		bottomPanel.setAlignmentY(JPanel.TOP_ALIGNMENT);
		bottomPanel.setAlignmentX(JPanel.RIGHT_ALIGNMENT);

		appPanel.add(bottomPanel);
		
		{
		    JPanel inputPanel = new JPanel();
		    bottomPanel.add(inputPanel);

		    JLabel targetLabel = new JLabel("Input Representation");
		    inputPanel.add(targetLabel);
		    inputPanel.add(inputRepComboBox);
		    inputRepComboBox.addItem(".class");
		    inputRepComboBox.addItem(".jimple");
		    
		}
	    
		{
		    JPanel outputPanel = new JPanel();
		    bottomPanel.add(outputPanel);
		    

		    JLabel targetLabel = new JLabel("Output Representation");
		    outputPanel.add(targetLabel);
		    outputPanel.add(outputRepComboBox);
		    outputRepComboBox.addItem(".class");
		    outputRepComboBox.addItem(".jasmin");		
		    outputRepComboBox.addItem(".gimple");
		    outputRepComboBox.addItem(".grimp");
		    outputRepComboBox.addItem(".jimple");
		    outputRepComboBox.addItem(".jimp");
		    outputRepComboBox.addItem(".baf");
		    outputRepComboBox.addItem(".b");
		}
		
	    }


	  
	
	    
	}



	// optimization options
	{
	    JPanel optiPanel = new JPanel(); 
	    //	    optiPanel.setLayout(new BoxLayout(optiPanel, BoxLayout.X_AXIS));

	    Font boldFont = new Font("Dialog", Font.BOLD, 12);
	    Border optiBorder = new  TitledBorder(null, "Optimization Flags", 
						  TitledBorder.LEFT, TitledBorder.TOP,
						  boldFont);	
	    optiPanel.setBorder(optiBorder);	       

	    optiPanel.add(dash_O);
	    optiPanel.add(dash_W);

	    


	    JLabel label = new JLabel("Final Internal Representation");
	    optiPanel.add(label);
	    optiPanel.add(finalRepBox);
	    finalRepBox.addItem("baf");
	    finalRepBox.addItem("grimp");




	    topLevelPanel.add(optiPanel);
	}
	
	/*	
	{
	    JRadioButton enable = new JRadioButton("Enable Jit");	    
	    java.lang.Compiler.disable();
	}
	*/

	
	// miscellaneous options
	{
	    JPanel miscPanel = new JPanel();
	    miscPanel.setLayout(new BoxLayout(miscPanel, BoxLayout.Y_AXIS));	   
	    topLevelPanel.add(miscPanel);
	    

	    Font boldFont = new Font("Dialog", Font.BOLD, 12);
	    Border miscBorder = new  TitledBorder(null, "Miscellaneous Options", 
						  TitledBorder.LEFT, TitledBorder.TOP,
						  boldFont);	
	    
	    miscPanel.setBorder(miscBorder);
    	    	    
	    {
		JPanel left = new JPanel();
		miscPanel.add(left);

	        left.add(new JLabel("Soot Classpath"));
		
		sootClasspath = new JTextField(40);
		sootClasspath.setText(System.getProperty("java.class.path"));
		left.add(sootClasspath);

	    }

	    {
		JPanel right = new JPanel();
		miscPanel.add(right);
       
		right.add(verboseCheckBox);
		right.add(debugCheckBox);
	    }
	}



	{

	    JPanel outputPanel = new JPanel();
	    topLevelPanel.add(outputPanel);

	    Font boldFont = new Font("Dialog", Font.BOLD, 12);
	    Border outputBorder = new  TitledBorder(null, "Execution Output", 
						  TitledBorder.LEFT, TitledBorder.TOP,
						  boldFont);	
	    outputPanel.setBorder(outputBorder);	       

	    JScrollPane scrollPane = new JScrollPane();
	    outputPanel.add(scrollPane);

	    ref.setEditable(false);

	    ref.setRows(10);
	    ref.setColumns(50);
	    scrollPane.setViewportView(ref);	    	    
	}
	
	System.setOut( new PrintStream(new OutputStream() {       
		private StringBuffer buf = new StringBuffer();
				
		public void write(int b) 
		{
		    buf.append((char) b);
		    if(b == (int) '\n') {
			ref.append(buf.toString());
			buf = new StringBuffer();
		    }
		}       
	    }));
	
	System.setErr(new PrintStream(new OutputStream() {       
		private StringBuffer buf = new StringBuffer();
				
		public void write(int b) 
		{
		    buf.append((char) b);
		    if(b == (int) '\n') {
			ref.append(buf.toString());
			buf = new StringBuffer();
		    }
		}       
	    }));
	


	{
	    
	    JPanel runPanel = new JPanel();
	    topLevelPanel.add(runPanel);

	    Font boldFont = new Font("Dialog", Font.BOLD, 12);
	    Border runBorder = new  TitledBorder(null, "Runtime Options", 
						  TitledBorder.LEFT, TitledBorder.TOP,
						  boldFont);	
	    runPanel.setBorder(runBorder);	       

	    
	    runPanel.add(sootifyButton);
	    sootifyButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {	
		    final String[] cmdLine = getCmdLine();
		    

		    Main.addCompilationListener( new ICompilationListener(){
			public void compilationTerminated(int status, String msg)
			    {
				compilationTerminated(status);
			    }
			public void compilationTerminated(int status)
			    {
				if(status == Main.COMPILATION_ABORTED)
				    System.out.println("Compilation aborted.");
				else if(status == Main.COMPILATION_SUCCEDED)
				    System.out.println("Compilation succeded.");	   
			    }			
		    });

		    (new Thread() {
			public void run() {
			    Main.main(cmdLine);
			}
			}).start();
		    
		    System.out.println("started thread");
		}   
	    });
	    
	
	    mainFrame.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
			System.exit(0);
		    }
		});
				    
				   
	    mainFrame.pack();
	    mainFrame.show();	
	}

		    
    }

    public static String[] getCmdLine()
    {
	
	String mainClass = classField.getText();
	boolean appMode =  appModeCheckBox.isSelected();
	String inputRep = (String) inputRepComboBox.getSelectedItem();
	String outputRep = (String) outputRepComboBox.getSelectedItem();

	// optimization options
	boolean optimize = dash_O.isSelected();
	boolean optimizeWhole = dash_W.isSelected();
	String finalRep = (String) finalRepBox.getSelectedItem();

	// miscelaneous options
	String sootClasspathString  = sootClasspath.getText();
	boolean verbose = verboseCheckBox.isSelected();
	boolean debug = debugCheckBox.isSelected();
	String cmd = (appMode ? "--app ": "") + mainClass 
			   + " --src-prec " + inputRep.substring(1) + " --" + outputRep.substring(1) + " " +
			   (optimize ? " -O ": "") + (optimizeWhole ? " -W ": "") + 
			   "--final-rep " + finalRep + (verbose ? " --verbose ": "") +
			   (debug ? " --debug ": "") + (sootClasspathString.length() > 0 ? " --soot-class-path " +
							sootClasspathString: "");
	System.out.println(cmd);
	System.out.println();
	
	StringTokenizer st = new StringTokenizer(cmd);
	int count = st.countTokens();
	String[] cmdLine = new String[count];	
	count = 0;
	while(st.hasMoreTokens()) {
	    cmdLine[count++] = st.nextToken();
	    System.err.print(cmdLine[count-1] + " ");
	}
	
	return cmdLine;	    	
    }
      

}
    
   class SplashScreen extends Window
    {
	private JLabel StatusBar;

	// SplashScreen's constructor
    public SplashScreen(ImageIcon CoolPicture, String message)
    {
        super(new Frame());

        // Create a JPanel so we can use a BevelBorder
        JPanel PanelForBorder=new JPanel(new BorderLayout());
        PanelForBorder.setLayout(new BorderLayout());
        PanelForBorder.add(new JLabel(CoolPicture),BorderLayout.CENTER);
	/*        PanelForBorder.add(StatusBar=new JLabel(message,SwingConstants.CENTER),
		  BorderLayout.SOUTH);*/
        PanelForBorder.setBorder(new BevelBorder(BevelBorder.RAISED));

        add(PanelForBorder);    
    }
    public void setVisible(boolean show)
    {
        if (show)
        {
            pack();

            // Plonk it on center of screen
            Dimension WindowSize=getSize(),
                ScreenSize=Toolkit.getDefaultToolkit().getScreenSize();
            setBounds((ScreenSize.width-WindowSize.width)/2,
                      (ScreenSize.height-WindowSize.height)/2,WindowSize.width,
                      WindowSize.height);
        }
        super.setVisible(show);
    }

    public void showStatus(String CurrentStatus)
    {
        if (isVisible())
        {
            SwingUtilities.invokeLater(new UpdateStatus(CurrentStatus));
        }
    }

    public void close() 
    {
        if (isVisible())
        {
            SwingUtilities.invokeLater(new CloseSplashScreen());
        }
    }

    class UpdateStatus implements Runnable
    {
        private String NewStatus;
        public UpdateStatus(String Status){NewStatus=Status;}
        public void run(){StatusBar.setText(NewStatus);}
    }

    class CloseSplashScreen implements Runnable
    {
        public void run()
        {
            setVisible(false);
            dispose();
        }
    }
    }

