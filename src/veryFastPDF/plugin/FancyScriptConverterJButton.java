/*
 * File name:        FancyFSMJButton.java (package eas.math.fundamentalAlgorithms.graphBased.fsm)
 * Author(s):        Lukas König
 * Java version:     8.0
 * Generation date:  25.07.2014 (18:35)
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
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

import eas.miscellaneous.convenience.GeneralDialog;
import veryFastPDF.script.ScriptConvertable;

/**
 * @author Lukas König
 */
public class FancyScriptConverterJButton extends JButton {
    
    private static final long serialVersionUID = -8214901489562286474L;

    /**
     * Most general constructor, all other constructors invoke this constructor.
     * 
     * @param title                    The button's displayed text.
     * @param newScriptLeftClick       Action to happen on left click.
     * @param newScriptRightClick      Action to happen on right click (can be {@code null}).
     * @param father                   The father window.
     * @param addNewScriptToOldScript  If the generated script should be added 
     *                                 to the already existing script (or 
     *                                 replace it otherwise).
     * @param targetRep                The target script generator required to render
     *                                 the generated script. If it is missing
     *                                 at the father level (meaning that the
     *                                 according processor is not initialized),
     *                                 the button is not set visible. Can be
     *                                 {@code null} to indicate that the type
     *                                 is unknown beforehand, but will not be 
     *                                 changed by the conversion.
     */
    public FancyScriptConverterJButton(
            String title, 
            ScriptConvertable newScriptLeftClick, 
            ScriptConvertable newScriptRightClick, 
            VFPWindow father,
            boolean addNewScriptToOldScript,
            boolean returnValueIsScript) {
        super((newScriptRightClick != null? "L<< ": "") + title + (newScriptRightClick != null? " >>R": ""));
        this.setBackground(new Color(0xaface6));
        
        this.addMouseListener(new MouseListener() {
            @Override public void mouseReleased(MouseEvent e) {}
            @Override public void mousePressed(MouseEvent e) {}
            @Override public void mouseExited(MouseEvent e) {}
            @Override public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!FancyScriptConverterJButton.this.isEnabled()) {
                    return;
                }
                
//                boolean isRefreshOnFly = father.getRefreshOnTheFlyCheckbox().isSelected();
//                father.getRefreshOnTheFlyCheckbox().setSelected(false);
                
                father.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                
                String converted;
                if (e.getButton() == 1 || newScriptRightClick == null) {
                    converted = newScriptLeftClick.createConvertedString();
                } else {
                    converted = newScriptRightClick.createConvertedString();
                }
                
                if (converted != null) {
                    if (returnValueIsScript) {
                        if (addNewScriptToOldScript) {
                            father.setScriptAndManageUndo(
                                    father.getScriptArea().getText() + converted);
                        } else {
                            father.setScriptAndManageUndo(converted);
                            father.getScriptArea().setCaretPosition(0);
                        }
                    } else {
                        GeneralDialog.message(
                                converted, 
                                title, 
                                true);
                    }
                }

//                father.getRefreshOnTheFlyCheckbox().setSelected(isRefreshOnFly);
                father.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }
}
