/*
 * Project: GoL
 *
 * Copyright (c) 2004-2022,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for computer sciences (Lab4Inf).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package de.lab4inf.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/**
 * Beispiel einer Swing Applikation mit Menu, ToolBar 
 * und einem Dialog.
 * @author nwulff
 *
 */
public class ExampleApplication extends SwingApp {
	public static boolean showComponents = false; 
	/**
	 * 
	 */
	public ExampleApplication() {
		getFrame().setTitle("Beispiel einer Java Swing Applikation");
	}

	/**
	 * @return
	 * @see de.lab4inf.gui.SwingApp#createToolBar()
	 */
	@Override
	protected JComponent createToolBar() {
        JToolBar tb = new JToolBar(JToolBar.HORIZONTAL);
        if(showComponents) {
        	Border bo = new LineBorder(Color.green,3);
        	tb.setBorder(bo);
        }
        tb.setToolTipText("dies ist die ToolBar, teste die Buttons");
        JButton b;
        String name;
        for (int i = 1; i < 6; i++) {
            name = "Button:" + i;
            b = new JButton("" + i);
            b.setName(name);
            b.setToolTipText("Anzeige in der Status Zeile von " + name);
            tb.add(b);
            b.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent evt) {
                    final String fmt = "Evt: %s pressed";
                    JButton button = (JButton) evt.getSource();
                    setStatusMsg(String.format(fmt, button.getName()));
                }

            });
        }
        return tb;
	}

	/**
	 * @return
	 * @see de.lab4inf.gui.SwingApp#createContent()
	 */
	@Override
	protected JComponent createContent() {
        JTextPane content = new JTextPane();
        Dimension d = new Dimension(200, 400);
        content.setMinimumSize(d);
        String msg = "Hallo World! \n\n Mit einer beispielhaften Swing GUI\n\n"+
        		" Probiere die Knöpfe in der ToolBar aus und achte auf den Status...";
        content.setText(msg);
        return content;
	}

	/**
	 * @param statusField
	 * @return
	 * @see de.lab4inf.gui.SwingApp#createStatusBar(javax.swing.JTextField)
	 */
	@Override
	protected JComponent createStatusBar(JTextField status) {
        FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
        JPanel statusBar = new JPanel(layout);
        if(showComponents) {
	        Border bo = new LineBorder(Color.red,2);
	        statusBar.setBorder(bo);
        }
        statusBar.setToolTipText("dies ist die StatusBar");
        String msg = "Hey die GUI scheint zu laufen :-)";
        Font font = status.getFont();
        int height = font.getSize() + 8;
        int width = 8 * msg.length();
        status.setText(msg);
        Dimension dim = new Dimension(width, height);
        status.setMaximumSize(dim);
        status.setMinimumSize(dim);
        status.setPreferredSize(dim);
        statusBar.add(new JLabel("Status: "));
        statusBar.add(status);
        return statusBar;
	}

	/**
	 * @return
	 * @see de.lab4inf.gui.SwingApp#createMenuBar()
	 */
	@Override
	protected JMenuBar createMenuBar() {
        JMenuBar mb = new JMenuBar();
        mb.setToolTipText("Dies ist die MenuBar");
        JMenu menu = new JMenu();
        JMenuItem item;
        if(showComponents) {
	        Border bo = new LineBorder(Color.blue,5);
	        mb.setBorder(bo);
        }
        menu.setText("Menu-1");
        menu.setToolTipText("Nur \"exit\" funktioniert! ");
        mb.add(menu);

        item = new JMenuItem("TutGarNichts");
        item.setToolTipText("macht nichts");
        menu.add(item);
        item = new JMenuItem("TutNichts");
        item.setToolTipText("macht noch weniger als nichts");
        menu.add(item);
        menu.addSeparator();
        item = new JMenuItem("Exit");
        menu.add(item);
        item.setToolTipText("Ende der Anwendung");
        item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                shutDown();
            }

        });
        menu = new JMenu();
        menu.setText("Menu-2");
        menu.setToolTipText("hier gibt's noch nichts zu gucken");
        mb.add(menu);
        
        // for the Help menu to be at the right side!
        mb.add(Box.createHorizontalGlue());
        menu = new JMenu();
        menu.setText("Help");
        menu.setToolTipText("Viel Hilfe gibt es noch nicht...");
        item = new JMenuItem("About");
        item.setToolTipText("startet einen modalen Dialog");
        item.addActionListener((evt)-> {
	        	setStatusMsg("starting JDialog");
	        	JDialog dialog = new JDialog(getFrame(),"Hilfe");
	        	dialog.setModal(true);
	        	dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	        	Rectangle bounds = getFrame().getBounds();
	        	int w = bounds.width/2;
	        	int h = bounds.height/2;
	        	bounds.width-=w;
	        	bounds.height-=h;
	        	bounds.x+=w/2;
	        	bounds.y+=h/2;
	        	dialog.setBounds(bounds);
	        	JButton ok = new JButton("ok");
	        	ok.setToolTipText("schließt den Dialog");
	        	ok.addActionListener((e)->{dialog.setVisible(false); setStatusMsg("Dialog closed");});
	        	JTextField about = new JTextField();
	        	about.setText("Demo GUI Applikation mit sperrendem Dialog");
	        	dialog.add(about,BorderLayout.CENTER);
	        	dialog.add(ok,BorderLayout.SOUTH);
	        	dialog.setVisible(true);
        });
        menu.add(item);
        mb.add(menu);

        return mb;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExampleApplication.showComponents = true;
        SwingApp app = new ExampleApplication();
        app.startUp();
	}

}
