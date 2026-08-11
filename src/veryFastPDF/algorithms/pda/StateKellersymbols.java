/*
 * File name:        StateKellerSymbols.java (package eas.fundamentalAlgorithms.graphBased.pushDown)
 * Author(s):        Lukas König
 * Java version:     7.0
 * Generation date:  10.01.2014 (14:32:32)
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

package veryFastPDF.algorithms.pda;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukas König
 */
public class StateKellersymbols {

    public StateKellersymbols(String state, String kellersymbolsAsChar) {
        super();
        this.state = state;

        this.kellersymbols = new ArrayList<String>(kellersymbolsAsChar.length());
        
        for (int i = 0; i < kellersymbolsAsChar.length(); i++) {
            this.kellersymbols.add(kellersymbolsAsChar.charAt(i) + "");
        }
    }

    public StateKellersymbols(String state, List<String> kellersymbols) {
        super();
        this.state = state;
        this.kellersymbols = kellersymbols;
    }
    
    protected String state;
    protected List<String> kellersymbols;
    
    @Override
    public String toString() {
        String kellerSymb = this.kellersymbols.toString().replace(", ", "");
        if (this.kellersymbols.size() == 0) {
            kellerSymb = PDA.getLambda();
        }

        return "(" + this.state + ", " + kellerSymb + ")";
    }
}
