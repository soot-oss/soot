/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jbco.gui;
import java.io.*;
import javax.swing.*;
public class RunnerThread implements Runnable {

  public boolean stopRun = false;
  private JBCOViewer viewer = null;
  private String[] cmdarray = null;
  private String wdir = null;
  
  public RunnerThread(String[] argv, JBCOViewer jv, String workingdir) {
    cmdarray = argv;
    viewer = jv;
    wdir = workingdir;
  }
  
  public void run() {
    synchronized (viewer.newFileMenuItem) {
      viewer.newFileMenuItem.setEnabled(false);
    }
    synchronized (viewer.openFileMenuItem) {
      viewer.openFileMenuItem.setEnabled(true);
    }
    try {
      File f = null;
      if (wdir != null) {
        f = new File(wdir);
        if (!f.exists() || !f.isDirectory())
          throw new Exception(f + " does not appear to be a proper working directory.");
      }
      
      Process p = Runtime.getRuntime().exec(cmdarray, null, f);
      BufferedReader br_in = new BufferedReader(new InputStreamReader(p.getInputStream()));
      BufferedReader br_er = new BufferedReader(new InputStreamReader(p.getErrorStream()));
      
      String line_in = "";
      try {
        while ((line_in = br_in.readLine()) != null || (line_in = br_er.readLine()) != null) {
          if (stopRun) {
            p.destroy();
            synchronized(viewer.TextAreaOutput) {  
              viewer.TextAreaOutput.append("\n\n*** Execution STOPPED ***");
              viewer.TextAreaOutput.setCaretPosition(viewer.TextAreaOutput.getDocument().getLength() );
            }
            break;
          }
          synchronized(viewer.TextAreaOutput) {
            boolean autoScroll = false;
            JScrollBar vbar = viewer.jScrollPane1.getVerticalScrollBar();
            synchronized (vbar) {
              autoScroll = ((vbar.getValue() + vbar.getVisibleAmount()) == vbar.getMaximum());
            }
            viewer.TextAreaOutput.append("\n"+line_in);
            if (autoScroll) 
              viewer.TextAreaOutput.setCaretPosition(viewer.TextAreaOutput.getDocument().getLength() );
          }
        }
      } catch (Exception exc) {
        throw exc;
      } finally {
        br_in.close();
        br_er.close();
      }
    } catch (Exception exc) {
      synchronized(viewer.TextAreaOutput) {
        viewer.TextAreaOutput.append("\n\n"+exc.toString());
        viewer.TextAreaOutput.setCaretPosition(viewer.TextAreaOutput.getDocument().getLength() );
      }
    }
    synchronized (viewer.newFileMenuItem) {
      viewer.newFileMenuItem.setEnabled(true);
    }
    synchronized (viewer.openFileMenuItem) {
      viewer.openFileMenuItem.setEnabled(false);
    }
  }

}
