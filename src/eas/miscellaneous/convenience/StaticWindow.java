/*
 * File name:        StaticWindow.java (package eas.miscellaneous)
 * Author(s):        Lukas König
 * Java version:     6.0
 * Generation date:  16.09.2011 (15:57:31)
 *
 * (c) This file and the EAS (Easy Agent Simulation) framework containing it
 * is protected by Creative Commons by-nc-sa license. Any altered or
 * further developed versions of this file have to meet the agreements
 * stated by the license conditions. 
 * 
 * In a nutshell
 * -------------
 * You are free:
 * - to Share -- to copy, distribute and transmit the work
 * - to Remix -- to adapt the work
 * 
 * Under the following conditions:
 * - Attribution -- You must attribute the work in the manner specified by the 
 *   author or licensor (but not in any way that suggests that they endorse 
 *   you or your use of the work).
 * - Noncommercial -- You may not use this work for commercial purposes.
 * - Share Alike -- If you alter, transform, or build upon this work, you may 
 *   distribute the resulting work only under the same or a similar license to 
 *   this one. 
 * 
 * + Detailed license conditions (Germany):
 *   http://creativecommons.org/licenses/by-nc-sa/3.0/de/
 * + Detailed license conditions (unported):
 *   http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en
 * 
 * This header must be placed in the beginning of any version of this file.
 */

package eas.miscellaneous.convenience;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import eas.GlobalVariables;
import eas.miscellaneous.StaticMethods;

/**
 * @author Lukas König
 */
public class StaticWindow extends JFrame {

    private static final long serialVersionUID = 2L;

    public class MyPanel extends JLabel {

        private static final long serialVersionUID = 3L;
        
        private transient BufferedImage img;

        public BufferedImage getImg() {
            return this.img;
        }

        public void displayShortMessage(
                final Graphics g,
                final String message,
                final Color c) {
            try {
                g.setFont(new Font("", 0, 15));
                g.setColor(Color.white);
                g.fillRect(65, this.img.getHeight() / 2 - 15, 450, 20);
                g.setColor(c);
                g.drawRect(65, this.img.getHeight() / 2 - 15, 450, 20);
                g.drawString(
                        message, 
                        70,
                        this.img.getHeight() / 2);
            } catch (Exception e) {
                StaticMethods.logError("Short message '" + message + "' could not be drawn.", GlobalVariables.getParameters());
            }
        }
        
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            StaticWindow.this.setTitle(windowName);
            if (this.img != null) {
                g.drawImage(this.img, 0, 0, this);
            }
        }
    }

    private String windowName;
    
//    public void displayShortMessage(final String s) {
//        panel.displayShortMessage(this.getGraphics(), s, Color.black);
//    }
    
    public StaticWindow(
            final String windowTitle, 
            int i, 
            int j,
            boolean showImmediately) {
        super(windowTitle);
        this.windowName = windowTitle;
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setSize(i, j);
        
//        this.panel = new MyPanel();
//        JScrollPane pane = new JScrollPane(this.panel);
//        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
//        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
//        this.getContentPane().add(pane);
        if (showImmediately) {
            this.setVisible(true);
        }
    }
}
