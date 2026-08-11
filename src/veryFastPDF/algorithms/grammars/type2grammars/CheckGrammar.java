/*
 * File name:        CheckGrammar.java (package eas.earleyParser)
 * Author(s):        Lukas König
 * Java version:     6.0
 * Generation date:  19.01.2011 (10:00:01)
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

package veryFastPDF.algorithms.grammars.type2grammars;

import java.util.Random;

/**
 * @author Lukas König
 *
 */
public class CheckGrammar {

    private static Random rand = new Random();
    
    public static String randomBinString(final int length) {
        if (length == 0) {
            return "";
        } else {
            if (rand.nextBoolean()) {
                return randomBinString(length - 1) + "1";
            } else {
                return randomBinString(length - 1) + "0";
            }
        }
    }

    public static String randomABCString(final int length) {
        int r = rand.nextInt(3);
        if (length == 0) {
            return "";
        } else {
            if (r == 0) {
                return randomABCString(length - 1) + "a";
            } else if (r == 1) {
                return randomABCString(length - 1) + "b";
            } else {
                return randomABCString(length - 1) + "c";
            }
        }
    }
    
    public static boolean hasMoreOnesThanZeroesOrEqual(final String s) {
        int count = 0;
        
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '1') {
                count++;
            } else {
                count--;
            }
        }
        
        return count >= 0;
    }

    @SuppressWarnings("unused")
    private static String abcString(int length) {
        if (length == 0) {
            return "";
        }
        
        String s = "";
        int i = rand.nextInt(length);
        int j = rand.nextInt(length - i);
        int k = length - i - j;
        
        for (int a = 0; a < i; a++) {
            s += "a";
        }
        for (int a = 0; a < j; a++) {
            s += "b";
        }
        for (int a = 0; a < k; a++) {
            s += "c";
        }
        
        return s;
    }
}
