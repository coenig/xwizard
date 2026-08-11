/*
 * File name:        HelperMethods.java (package veryFastPDF.web)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  04.09.2015 (09:50:46)
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

package veryFastPDF.web;

import eas.miscellaneous.StaticMethods;
import jodd.io.StreamGobbler;
import mainServlet.WebLink;
import veryFastPDF.HelpTexts;
import veryFastPDF.plugin.VFPWindow;
import veryFastPDF.script.RepresentableAsPDF;

/**
 * @author Lukas König
 */
public class ConvenienceMethods {

    public static final String INFO_II_MODE_NAME = ".i2";
    public static final String TEXTBOOK_MODE_NAME = ".lb";
    public static final String EFFALG_MODE = ".effalg";
    
    /**
     * Call this method to replace special characters for HTML.
     * 
     * @param sString  The String to replace special characters in.
     * @return  The cleaned-up string.
     */
    public static String replaceSpecialCharsHTML_G(String sString) {
        if (sString == null) {
            return sString;
        }
        
        String ssString = sString
                .replace("ß", "&szlig;")
                .replace("ä", "&auml;")
                .replace("ö", "&ouml;")
                .replace("ü", "&uuml;")
                .replace("Ä", "&Auml;")
                .replace("Ö", "&Ouml;")
                .replace("Ü", "&Uuml;")
                ;
        return replaceMathHTML(ssString);
    }
    
    public static String replaceMathHTML(String sString) {
        String ssString = sString
                .replace("\\circ", "<span style=\\\"font-style: normal\\\">&#9675;</span>")
                .replace("\\in", "&isin;")
                .replace("\\N", "<span style=\"font-style: normal\">&#x2115;</span>")
                .replace("\\cup", "&cup;")
                .replace("\\bigcup", "<span style=\"font-style: normal;font-size:1.3em;\">&cup;</span>")
                .replace("\\cdot", "&middot;")
                .replace("\\alpha", "&alpha;")
                .replace("\\beta", "&beta;")
                .replace("\\gamma", "&gamma;")
                .replace("\\delta", "&delta;")
                .replace("\\lambda", "&lambda;")
                .replace("\\emptyset", "<span style=\"font-style: normal\">&#8709;</span>")
                ;
        return ssString;
    }
    
    public static String createInfo2ModeString(
            int chapCourse, 
            int chapBook, 
            int volBook,
            String link1,
            String link2,
            boolean english) {
        String chap1 = english 
                ? "Recordings" // : Chap. " + chapCourse + "" 
                : "Video"; //: Kap. " + chapCourse + "";
//        String volBookRom = volBook == 1 ? "I" : "II";
        
        String chap2 = english 
                ? "Exercises" // + volBookRom + "-" + chapBook
                : "Aufgaben"; // + volBookRom + "-" + chapBook;
        
        return HelpTexts.link(link1, chap1, true) + " | " + HelpTexts.link(link2, chap2, true) + "";
    }

    /**
     * Executes a command and does NOT wait for the process to terminate.
     * Also, the file must be an executable. If you instead want to execute
     * a batch file, use the 3-par method.
     * (Convenience method.)
     * 
     * @param command  The command to execute.
     * 
     * @return  The process correpsonding to the executed command.
     */
    public static Process execCommand(String command) {
        return execCommand(command, false, false);
    }
    
    /**
     * Executes a command and waits for the process to terminate if desired.
     * (Convenience method.)
     * 
     * @param command             The command to execute.
     * @param waitForIt           If the execution should pause until the process has terminated.
     * @param executeAsBatchFile  Batch files alone are not executable. They need an application to run them, therefore
     *                            a different execution command is required.
     * 
     * @return  The process correpsonding to the executed command.
     */
    public static Process execCommand(
            String command, 
            boolean waitForIt, 
            boolean executeAsBatchFile) {
        try {
            if (WebLink.isDebugMode()) {
                String workingDir = WebLink.getWORKING_DIRECTORY();
                
                if (workingDir == null) {
                    workingDir = VFPWindow.TEMP_DIR.getAbsolutePath();
                }
                
                StaticMethods.saveTextToFile(workingDir, "debug_exec-command_" + command.hashCode() + ".txt", command);
            }

            Process p;
            if (executeAsBatchFile) {
                /* 
                 * Batch files alone are not executable. 
                 * They need an application to tun them, in this case, cmd. 
                 * Insert " start " after the "/c" to open terminal window.
                 */
                p = Runtime.getRuntime().exec("cmd /c " + command); 
            } else {
                p = Runtime.getRuntime().exec(command);
            }

            StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), System.err);            
            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), System.out);

            outputGobbler.start();
            errorGobbler.start();

            if (waitForIt) {
                p.waitFor();
            }
            
            return p;
        } catch (Exception e) {return null;}
    }

                public static Process execCommand(String[] command, boolean waitForIt) {
                    try {
                        Process p = Runtime.getRuntime().exec(command);

                        StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), System.err);
                        StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), System.out);

                        outputGobbler.start();
                        errorGobbler.start();

                        if (waitForIt) {
                            p.waitFor();
                        }

                        return p;
                    } catch (Exception e) {return null;}
                }
    
    public static boolean isNonNegativeInteger(String s) {
        return isNonNegativeInteger(s, 10);
    }
    
    public static boolean isNonNegativeInteger(String s, int radix) {
        try { 
            int i = Integer.parseInt(s);
            return i >= 0;
        } catch(NumberFormatException e) { 
            return false; 
        } catch(NullPointerException e) {
            return false;
        }
    }

    public static String repNameSQL(RepresentableAsPDF r) {
        return r.getClass().getSimpleName().toUpperCase();
    }
}
