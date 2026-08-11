/*
 * File name:        FancyJButton.java (package eas.math.fundamentalAlgorithms.graphBased)
 * Author(s):        Lukas König
 * Java version:     7.0
 * Generation date:  23.04.2014 (07:57:21)
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

package veryFastPDF.plugin;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import eas.miscellaneous.convenience.GeneralDialog;
import veryFastPDF.script.ScriptConvertable;

/**
 * @author Lukas König
 */
public class FancyJButton extends JButton {
    
    private static final long serialVersionUID = -8214901489562286474L;

    public static final Color NON_WEB_BUTTON_COLOR = new Color(100, 100, 255);
    
    public FancyJButton() {
        this(null, null, null, null);
    }

    public FancyJButton(Action a) {
        this(null, null, null, a);
    }

    public FancyJButton(Icon icon) {
        this(null, null, icon, null);
    }

    public FancyJButton(String text) {
        this(text, null, null, null);
    }

    public FancyJButton(String text, Action a) {
        this(text, null, null, a);
    }

    public FancyJButton(String text, ScriptConvertable stringToDisplay) {
        this(text, stringToDisplay, null, null);
    }

    public FancyJButton(String text, ScriptConvertable stringToDisplay, Icon icon, Action a) {
        super(text, icon);
        this.setBackground(NON_WEB_BUTTON_COLOR);
        if (a != null) {
            setAction(a);
        }
        
        if (stringToDisplay != null) {
            this.setText(text);
            this.setToolTipText("Get additional information for " + text);
            this.addMouseListener(new MouseListener() {
                @Override public void mouseReleased(MouseEvent e) {}
                @Override public void mousePressed(MouseEvent e) {}
                @Override public void mouseExited(MouseEvent e) {}
                @Override public void mouseEntered(MouseEvent e) {}

                @Override
                public void mouseClicked(MouseEvent e) {
                    GeneralDialog dia = new GeneralDialog(
                            null, 
                            null, 
                            "Output of button '" + text + "'", 
                            GeneralDialog.OK_BUTT, 
                            stringToDisplay.createConvertedString());
                    
                    dia.setVisible(true);
                }
            });
        } else {
            this.setEnabled(false);
        }
    }
}
