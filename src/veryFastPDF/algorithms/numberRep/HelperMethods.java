/*
 * File name:        HelperMethods.java (package veryFastPDF.algorithms.numberRep)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  11.02.2016 (14:09:27)
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

package veryFastPDF.algorithms.numberRep;

/**
 * @author Lukas König
 */
public class HelperMethods {

    public static String complement(String code) {
        return code.replace("0", "N").replace("1", "0").replace("N", "1");
    }
    
    public static String increment(String code) {
        String code2 = "";
        
        int i = code.length() - 1;
        while (i >= 0 && (code.charAt(i) == '1' || NumberRepresentable.TOO_LONG_MARKER.equals(code.charAt(i) + ""))) {
            if (code.charAt(i) == '1') {
                code2 = 0 + code2;
            } else {
                code2 = NumberRepresentable.TOO_LONG_MARKER + code2;
            }
            i--;
        }

        code2 = 1 + code2;
        i--;
        
        for (int j = i; j >= 0; j--) {
            code2 = code.charAt(j) + code2;
        }
        
        return code2;
    }
}
