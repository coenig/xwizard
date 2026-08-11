/*
 * File name:        StringArrayList.java (package eas.math.type2grammars.charty)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  06.03.2015 (18:18:51)
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

package veryFastPDF.algorithms.grammars.type2grammars.charty;

import java.util.LinkedList;

/**
 * @author Lukas König
 */
public class EdgeListList extends LinkedList<EdgeList> {
    private static final long serialVersionUID = 2064604973472498785L;
    
    @Override
    public String toString() {
        String s = "";
        s += this.get(0) + "&";
        int currentLength = 0;
        
        for (int i = 1; i < this.size(); i++) {
            String tempStr = "";
            
            if (currentLength > 200) {
                tempStr = "\\\\\n&";
                currentLength = 0;
            }
            
            String newString = " \\Rightarrow " + this.get(i);
            currentLength += newString.length();
            s += tempStr + newString;
        }
        
        return s;
    }
}
