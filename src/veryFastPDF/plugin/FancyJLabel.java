/*
 * File name:        FancyJLabel.java (package veryFastPDF.script)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  29.05.2015 (19:41:16)
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

import javax.swing.BorderFactory;
import javax.swing.JLabel;

/**
 * @author Lukas König
 */
public class FancyJLabel extends JLabel {

    private static final long serialVersionUID = -672995184451470600L;
    private static final String PREAMBLE = "    ";
    private static final String POSTAMBLE = "    ";
    
    public FancyJLabel(String title) {
        super(PREAMBLE + title + POSTAMBLE);
        this.setBorder(BorderFactory.createLineBorder(Color.blue, 2, true));
        this.setForeground(Color.BLUE);
    }
    
    @Override
    public void setText(String text) {
        super.setText(PREAMBLE + text + POSTAMBLE);
    }
}
