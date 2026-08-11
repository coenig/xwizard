/*
 * File name:        InscriptMethods.java (package veryFastPDF.script.testing)
 * Author(s):        hq0976
 * Java version:     8.0 (at generation time)
 * Generation date:  30.03.2017 (10:57:47)
 * Part of the EAS => VFP => XWizard webapp implementation.
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

package veryFastPDF.script.testing;

import java.util.HashMap;

import veryFastPDF.script.RepresentableDefault;

/**
 * @author hq0976
 *
 */
public class InscriptMethods {
    private static final String[] METHOD_NAMES = new String[] {
            "fak",
            "slowfib",
            "fib",
            "prime",
    };
    
    private static final String[] USEFUL_METHODS = new String[] {
            "@{@{\n" 
            + "@(1)@\n" 
            + "@(@{@{" 
            + RepresentableDefault.makroPattern(0, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) 
            + "}@.sub[1].fak}@.mult[" 
            + RepresentableDefault.makroPattern(0, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) + "])@\n" 
            + "}@*.if[this.smeq[" 
            + RepresentableDefault.makroPattern(0, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) + ", 1]]}@**." 
            + RepresentableDefault.NAME_OF_NEW_COMMAND_METHOD + "[fak, 0] " 
            + "/* Factorial function */",
            
            "@{@{\n"
            + "@(" + RepresentableDefault.makroPattern(0, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) + ")@\n" 
            + "@(@{@{@{"
            + RepresentableDefault.makroPattern(0, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) 
            + "}@*.sub[1].fib}@*}@.add[@{"
            + RepresentableDefault.makroPattern(0, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) 
            + "}@*.sub[2].fib])@\n" 
            + "}@**.if[this.smeq["
            + RepresentableDefault.makroPattern(0, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) 
            + ", 1]]}@***."
            + RepresentableDefault.NAME_OF_NEW_COMMAND_METHOD
            + "[fib, 0]"
            + " /* Slow Fibonacci function */",
            
            "@{@{\n" 
            + "@(" 
            + RepresentableDefault.makroPattern(0, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) + ")@\n" 
            + "@(@{" 
            + RepresentableDefault.makroPattern(0, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) 
            + ".fib}@.sethard[@{@{@{@{" + RepresentableDefault.makroPattern(0, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) 
            + "}@*.sub[1]}@*.fib}@*}@.add[@{@{" 
            + RepresentableDefault.makroPattern(0, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) + "}@*.sub[2]}@*.fib]])@\n" 
            + "}@**.if[this.smeq[" + RepresentableDefault.makroPattern(0, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) 
            + ", 1]]}@***."
            + RepresentableDefault.NAME_OF_NEW_COMMAND_METHOD
            + "[fib, 0]"
            + " /* Dynamic Fibonacci */\n",
            
            "@{\n" 
            + "@{@{\n" 
            + "@(Nein, teilbar durch: " + RepresentableDefault.makroPattern(1, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) + ")@\n" 
            + "@(\n" 
            + "@{@(Ja)@@(@{" + RepresentableDefault.makroPattern(0, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) + "}@.primeSm[@{" 
            + RepresentableDefault.makroPattern(1, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) + "}@.sub[1]])@}@.if[this.smeq[" 
            + RepresentableDefault.makroPattern(1, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) + ", 2]]\n" 
            + ")@\n" 
            + "}@**.if[this.eq[@{" + RepresentableDefault.makroPattern(0, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) + "}@.mod[" 
            + RepresentableDefault.makroPattern(1, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) + "], 0]]}@***."
            + RepresentableDefault.NAME_OF_NEW_COMMAND_METHOD
            + "[primeSm, 1]\n" 
            + "\n" 
            + "@{@{\n" 
            + "@(Nein)@\n" 
            + "@(\n" 
            + "@{@(Ja)@@(@{" + RepresentableDefault.makroPattern(0, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) + "}@.primeSm[@{" 
            + RepresentableDefault.makroPattern(0, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) 
            + "}@.sqrt])@}@.if[this.eq[" + RepresentableDefault.makroPattern(0, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) + ", 2]]\n" 
            + ")@\n" 
            + "}@*.if[this.smeq[" + RepresentableDefault.makroPattern(0, RepresentableDefault.INSCRIPT_STANDARD_PARAMETER_PATTERN) + ", 1]]}@**."
            + RepresentableDefault.NAME_OF_NEW_COMMAND_METHOD
            + "[prime, 0]"
            + " /* Prime numbers */",
    };
    
    private static HashMap<String, String> METHOD_NAME_MAPPINGS = null;
    
    public static String getMethod(String name) {
        if (METHOD_NAME_MAPPINGS == null) {
            METHOD_NAME_MAPPINGS = new HashMap<>();
            for (int i = 0; i < METHOD_NAMES.length; i++) {
                METHOD_NAME_MAPPINGS.put(METHOD_NAMES[i], "\n" + USEFUL_METHODS[i] + "\n");
            }
        }
        
        return METHOD_NAME_MAPPINGS.get(name);
    }
}
