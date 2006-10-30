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

import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class PopupDialog extends JDialog implements ActionListener {
      private JPanel myPanel = null;
      private JButton okButton = null;
      
      public PopupDialog (JFrame frame, boolean modal, String myMessage) {
        super(frame, modal);
        myPanel = new JPanel();
        getContentPane().add(myPanel);
        JTextArea jta = new JTextArea(myMessage);
        myPanel.add(jta);
        okButton = new JButton("Ok");
        okButton.addActionListener(this);
        myPanel.add(okButton); 
        pack();
        setLocationRelativeTo(frame);
        setVisible(true);
      }

      public void actionPerformed(ActionEvent e) {
        if(okButton == e.getSource())
            setVisible(false);
      }
}
