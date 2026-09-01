/*
 * File name:        WebLink.java (package mainServlet)
 * Author(s):        Lukas König
 * Java version:     8.0
 * Generation date:  30.07.2015 (08:24)
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

package mainServlet;

import java.awt.Color;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Parameter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import eas.GlobalVariables;
import eas.math.MiscMath;
import eas.miscellaneous.StaticMethods;
import eas.veryFastPDF.MainLink;
import veryFastPDF.HelpTexts;
import veryFastPDF.VFPVariables;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.pdfProcessors.PDFProcessorFactory;
import veryFastPDF.plugin.VFPWindow;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.script.RepresentableFactory;
import veryFastPDF.script.ScriptConversionMethods;
import veryFastPDF.web.ConvenienceMethods;
import veryFastPDF.web.SessionMetaInf;
import veryFastPDF.web.Watchdog;

public class WebLink {
    
    private static int lastSourceIDColl = -1;

    public static final String COLLECTION_POSTAMBLE_HTML_STRING = "</fieldset>\r\n" + "</DIV>\r\n";
    public static final String SCRIPT_TYPE_NOT_DETECTED_STRING = "<i><font color=\"red\">Script type not detected</font></i>";
    public static final String SCRIPT_TYPE_NOT_DETECTED_STRING_G = "<i><font color=\"red\">Skript-Typ nicht erkannt</font></i>";
    public static final String SUBMIT_PARS_BUTTON_NAME = "Submit parameters";
    public static final String SUBMIT_PARS_BUTTON_NAME_G = "Parameter senden";
    public static final int MIN_COLOR_FACTOR = 50;

    public static final String WORKING_DIRECTORY_NAME = "workingDir";
    private static String WORKING_DIRECTORY;

    public static void setWORKING_DIRECTORY(String workingDir) {
        if (WORKING_DIRECTORY == null || !WORKING_DIRECTORY.equals(workingDir)) {
            WORKING_DIRECTORY = workingDir;
            GlobalVariables.getParameters().logWeb("Working directory set to '" + WORKING_DIRECTORY + "'.");
        }
    }

    public static String getWORKING_DIRECTORY() {
        if (WORKING_DIRECTORY == null) {
            WORKING_DIRECTORY = VFPWindow
                    .getSINGLETON_INSTANCE()
                    .getWorkingDirectory()
                    .getAbsolutePath();
        }
        
        return WORKING_DIRECTORY;
    }
    
    /**
     * Converts a script into the next translation step. This is usually the
     * <UL>
     * <LI>SVG graph; otherwise, it can be</LI>
     * <LI>a new converted script that has to be translated once more; or</LI>
     * <LI>some simple text output.</LI>
     * </UL>
     * The script can be an ID.
     * 
     * @return  The script translated to the next step.
     */
    public static synchronized String invokeVFPScriptConversion(
            String script2, 
            SessionMetaInf metaInf,
            boolean english,
            String cookieUserName,
            String workingDir,
            boolean alwaysTranslateToTheEnd,
            int hash,
            Wizz myWizz) {
        setWorkDir(workingDir);

        RepresentableDefault.removeKnownStuff();
        
        String convertedString;
        Watchdog w = new Watchdog(script2);
        w.startWatching(); // Watchdog has to be stopped once the return value has been received.
        WebLink.setCookieUserName(cookieUserName);

        String script = script2;
        
        // Check for ID.
        Integer scrID = Wizz.scriptIsID(script);
        if (scrID != null) {
            script = SQLQueries.getWebFreeScript(scrID);
        }

        // Always return SVG (not converted script, if any).
        if (alwaysTranslateToTheEnd) {
            long startTime = System.currentTimeMillis();

            ScriptConversionMethods.LAST_TRANSLATED_REP = null;
            String svg = ScriptConversionMethods.translateScript(script, WebLink.defaultFileName(), hash);
            RepresentableAsPDF r = ScriptConversionMethods.LAST_TRANSLATED_REP;
            
            String repName = null;
            HashMap<String, String> metaData = null;
            if (r != null) {
                repName = SQLQueries.tableName(r);
                metaData = r.getMetaProperties();
            }

            // Database
            SQLQueries.accessDatabase(
                    script,                                      // The complete script.
                    -2,                                          // The source script ID.
                    -2,                                          // The target script ID.
                    System.currentTimeMillis() - startTime,      // The duration of the calculation.
                    metaInf,                                     // The meta information.
                    repName,                                     // The table name for the current rep.
                    metaData,                                    // The representable meta props.
                    0,                                            /* Error code */
                    w);

            SQLQueries.storeCache(script2, svg, "null");
            
            w.stopWatching();
            return svg;
        }
        
        try {
            String replacedScript = script;
            
            if (replacedScript.startsWith(Exercise.ENCRYPTED_PREFIX)) {
                replacedScript = ScriptConversionMethods.decryptScript(replacedScript);
            }
            
            convertedString = WebLink.convertScriptIntoPDFScript(
                    cookieUserName,
                    replacedScript, 
                    metaInf,
                    english,
                    w,
                    hash,
                    myWizz);
        } catch (Exception e) {
            GlobalVariables.getParameters().logError("Some error occurred during script conversion, I'm resetting to original script.");
            convertedString = script;
            w.stopWatching();
            throw new RuntimeException(e);
        }
        
        w.stopWatching();
        
        return convertedString;
    }

    /**
     * @param workingDir
     */
    private static void setWorkDir(String workingDir) {
        if (workingDir == null) {
            workingDir = "./DeScriptorWorkingDir";
        }
        WebLink.setWORKING_DIRECTORY(workingDir);
    }

    /**
     * Generates a converted script out of a script. Note the differences
     * in the return value compared to the original intention of this method
     * (which in the case of no conversion instructions in the script
     * was to return the PDF code then, instead of directly creating a
     * figure).
     * <BR/>
     * <BR/>
     * Dear Lhuk or other programmer: Please don't underestimate the
     * "complexity" of this simple method. It's just one of two cases - dynamic
     * method invocation or not. But if you confuse what's really happening, 
     * strangely seeming effects will haunt you. Read your own nice comments,
     * then all will turn out ok!
     * 
     * @return  The converted script is either another script which the given script 
     *          encodes to be translated to or {@code null}. In the latter case,
     *          rather than creating a converted script, the script is stored
     *          directly in SVG format to be displayed in a Web Application.
     *          Note that this behavior differs from the original intention
     *          of this method.
     */
    private static synchronized String convertScriptIntoPDFScript(
            final String cookieUserID,
            final String script,
//            final String realWorkingPath,
            final SessionMetaInf metaInf,
            final boolean isEnglish,
            final Watchdog w,
            final int hash,
            final Wizz myWizz) {
        long startTime = System.currentTimeMillis();
        int sourceID = lastSourceIDColl;
        
        try {
            String scriptTextWithoutComments = ScriptConversionMethods.removeComments(script);
            RepresentableAsPDF r = WebLink.getApplicablePDFType(
                    scriptTextWithoutComments, false, null, myWizz == null ? null : myWizz.realPath());
            
            // DATABASE.
            String repNameForLater = null;
            HashMap<String, String> metaDataForLater = null;
            if (r != null) {
                repNameForLater = SQLQueries.tableName(r);
                metaDataForLater = r.getMetaProperties();
            }
            // EO DATABASE.
            
            RepresentableAsPDF rConverted = ScriptConversionMethods.getConvertedRepresentableIfAny(
                    scriptTextWithoutComments, 
                    r,
                    WebLink.availablePDFTypes);
            
            // In case of dynamic method invocation, else part comes first.
            if (rConverted == null) { // Regular case where no dynamic method has been applied.
                PDFProcessor p = ScriptConversionMethods.getPDFProcessorFrom(r, WebLink.getWORKING_DIRECTORY());
                String realScript = p.getPreparedSourceString();
                
                if (realScript.length() > p.getCodeSizeToBeConsideredLarge()) {
                    // No code prefix needed as we go right into the PDF processor with the raw code.
                    realScript = p.safetyCodeInCaseOfLargeCodeOrLongOperation(p.getPreparedSourceString().length());
                }

                String svgCode = p.getSVGCode(
                        WebLink.defaultFileName(), 
                        WebLink.getWORKING_DIRECTORY(), 
                        realScript,
                        hash);
                
                // Return plain SVG graphics description.
                String finalConvertedScript = svgCode;
//                String finalConvertedScript = WebLink.setSVGWidth(svgCode);

                // DATABASE. (In this case the "ForLater" vars could be initialized here, too.)
                SQLQueries.accessDatabase(
                        script,                                      // The complete script.
                        sourceID,                                    // The source script ID.
                        -1,                                          // The target script ID.
                        System.currentTimeMillis() - startTime,      // The duration of the calculation.
                        metaInf,                                     // The meta information.
                        repNameForLater,                             // The table name for the current rep.
                        metaDataForLater,                            // The representable meta props.
                        0,                                            /* Error code */
                        w);
                // EO DATABASE.
                
                lastSourceIDColl = -1;
                
                return finalConvertedScript;
            } else { // Dynamic method invocation took place => we will need a second turn.
                PDFProcessor p = PDFProcessorFactory.getPrematureInstanceOf(rConverted.getPDFProcessorClass());
                
                // Note that, actually, we compare apples to oranges here. The converted code is different from the PDF code.
                String finalConvertedScript = rConverted.getRawScript();
                if (finalConvertedScript.length() > p.getCodeSizeToBeConsideredLarge()) {
                    // Notice how we need a code prefix here as the script is interpreted from scratch one more time.
                    return p.getCodePrefix() + "\n" + p.safetyCodeInCaseOfLargeCodeOrLongOperation(finalConvertedScript.length());
                }
                
                // DATABASE. ("ForLater" vars needed since "r" has changed in the meanwhile.)
                int targetID = SQLQueries.updateOrInsertScriptInMainTable(finalConvertedScript);
                int id = SQLQueries.accessDatabase(
                        script,                                      // The complete script.
                        -1,                                          // The source script ID.
                        targetID,                                    // The target script ID.
                        System.currentTimeMillis() - startTime,      // The duration of the calculation.
                        metaInf,                                     // The meta information.
                        repNameForLater,                             // The table name for the current rep.
                        metaDataForLater,                            // The representable meta props.
                        0,                                            /* Error code */
                        w);
                // EO DATABASE.
                
                lastSourceIDColl = id; // Used next time when the "then" part is accessed.
                
                return finalConvertedScript; // Return script code which has to be processed once more.
            }
        } catch (Exception e) {
            /* 
             * You can usually ignore this error case.
             * It happens, e.g., every time a script is syntactically incorrect, 
             * which is obviously a common case. Normally, only database exceptions
             * should raise concerns.
             * 
             * Note that the error is logged in the database (if possible).
             */

            SQLQueries.accessDatabase(
                    script,                                      // The complete script.
                    -1,                                          // The source script ID.
                    -1,                                          // The target script ID.
                    System.currentTimeMillis() - startTime,      // The duration of the calculation.
                    metaInf,                                     // The meta information.
                    null,                                        // The table name for the current rep.
                    null,                                        // The representable meta props.
                    1,                                            /* Error code (1, so far: in future maybe more sophisticated) */
                    w);
            
            GlobalVariables.getParameters().logWeb("Script conversion failed.\n"
                    + " BUT DON'T PANIC! This happens all the time, it's probably nothing :-)\n"
                    + " Just in case, I'm givin you the stack trace. (However, "
                    + "look out for database exceptions! They really shouldn't occur!)");
            e.printStackTrace();
            
            throw new RuntimeException(e);
        }
    }

    /*
     * Here comes the stuff formerly stuffed in WebLink. Maybe has to be sorted out sometime.
     */
    

    private static final String DEBUG_FILE_NAME = "debug.exists";
    private static Boolean debugMode = true;
    private static int[] exampleCounters;
    private static List<Class<? extends RepresentableAsPDF>> possiblyUnstableReps = null;

    /**
     * Sets debug mode according to debug file existing or not.
     * This method overrides the setting above, and should be called once in
     * a while to allow for a admin-induced change of debug mode.
     * Setting this will always switch to web mode!
     * 
     * @param initializeInAnyCase  Use true to run the initialization methods
     *                             regardless of the state of the debugMode variable. 
     *                             If false, the initialization
     *                             methods are run only if a change of the variable
     *                             occurred.
     */
    public static void setDebugMode(boolean initializeInAnyCase) {
        String debugFileLocation = mainLoggingFolder() + "/" + DEBUG_FILE_NAME;

        GlobalVariables.getParameters().logWeb("Looking for debug flag file at " + debugFileLocation + ".");
        
        boolean tempDebugMode = new File(debugFileLocation).exists();
        
        if (tempDebugMode) {
            GlobalVariables.getParameters().logWeb("Debug file found - entering debug mode.");
        } else {
            GlobalVariables.getParameters().logWeb("Debug file not found - entering regular mode.");
        }
        
        if (initializeInAnyCase || tempDebugMode != debugMode) {
            debugMode = tempDebugMode;
            setWebMode();
            initializeReps();
            exampleCounters = new int[WebLink.availablePDFTypes.size()];
            
            GlobalVariables.getParameters().logWeb("DEBUG_MODE set to " + debugMode);
        }
    }
    
    public static List<Class<? extends RepresentableAsPDF>> getPossiblyUnstableReps() {
        return possiblyUnstableReps;
    }
    
    public static int[] getExampleCounters() {
        return exampleCounters;
    }
    
    public static boolean isDebugMode() {
        return debugMode;
    }

    public static List<Class<? extends RepresentableAsPDF>> availablePDFTypes;

    public static HashMap<String, List<Class<? extends RepresentableAsPDF>>> availablePDFTypesGrouped() {
        HashMap<String, List<Class<? extends RepresentableAsPDF>>> types = new HashMap<>();
        HashMap<String, List<Class<? extends RepresentableAsPDF>>> all = RepresentableFactory.getAvailableTypesGrouped();
        
        LinkedList<String> keySet = new LinkedList<>(all.keySet());

        for (String name : keySet) {
            types.put(name, new LinkedList<>());
            
            for (Class<? extends RepresentableAsPDF> c : all.get(name)) {
                if (availablePDFTypes.contains(c)) {
                    types.get(name).add(c);
                }
            }
        }
        
        return types;
    }
    
    private static String[] pdfTypesClassNames;
    public static final String DEFAULT_SVG_NAME = RepresentableDefault.THIS_NAME;
    public static final String CONF_FILE_NAME = "conf.txt";
    public static final String DEFAULT_OUTPUT_FILE_NAME = RepresentableDefault.THIS_NAME;
    public static final String INTRO = "Frage zu Skript:\n\n------------\n";
    public static final String EXTRO = "\n------------\n\n";

    /**
     * Configuration file storing global parameters. The file is
     * created automatically, but has to be filled manually with information.
     * So far, 
     * - the first entry is the dot.exe location,
     * - the second entry is the pdflatex.exe location,
     * - the third entry is the pdf2svg.exe location.
     */
    public static File CONF_FILE;

    private static void initializeReps() {
        availablePDFTypes = RepresentableFactory.getRepsForWeb();
//        pdfTypesPrefixes = new String[availablePDFTypes.size()];
        pdfTypesClassNames = new String[availablePDFTypes.size()];
            
        {int i = 0;
        for (Class<? extends RepresentableAsPDF> r : availablePDFTypes) {
            pdfTypesClassNames[i] = r.getSimpleName();
//            pdfTypesPrefixes[i] = guessPrefixOfCode(r.getExampleScripts()); // TODO: Do we need this, actually?
            i++;
        }}
        
        possiblyUnstableReps = RepresentableFactory.getAvailableWebRepNamesWhichMightBeUnstable();
    }
    
    static {
        boolean oldDebugMode = debugMode;
        debugMode = true; // To avoid redirecting console output.
        setWebMode(); // Standard mode. This allows to not care about mode from "web side" ;-)
        debugMode = oldDebugMode; // on servlet side.
    }

    private static String pathDOT = null;
    private static String pathLATEX = null;
    private static String pathPython = null;
    private static String pathPDF2SVG = null;
    private static String pathInkscape = null;
    private static String pathPdftk = null;
    
    public static String getPDFTKPath() {
        if (pathPdftk == null) {
            loadPaths();
        }
        return pathPdftk;
    }
    
    public static String getPythonPath() {
        if (pathPython == null) {
            loadPaths();
        }
        return pathPython;
    }

    public static String getDOTPath() {
        if (pathDOT == null) {
            loadPaths();
        }
        return pathDOT;
    }

    public static String getLATEXPath() {
        if (pathLATEX == null) {
            loadPaths();
        }
        return pathLATEX;
    }

    public static String getLATEXDirectoryPath() {
        return new File(getLATEXPath()).getParent().replace("\\", "/");
    }
    
    public static String getPDF2SVGPath() {
        if (pathPDF2SVG == null) {
            loadPaths();
        }
        return pathPDF2SVG;
    }
    
    public static String getInkscapePath() {
        if (pathInkscape  == null) {
            loadPaths();
        }
        return pathInkscape;
    }

    public static final int pathDOT_POS = 0;
    public static final int pathLATEX_POS = 1;
    public static final int pathPDF2SVG_POS = 2;
    public static final int pathPython_POS = 3;
    public static final int pathInkscape_POS = 4;
    public static final int pathPdftk_POS = 5;
    
    public static void loadPaths() {
        instantiateConfFile();
        
        GlobalVariables.getParameters().logWeb("Trying to access conf file at '" + CONF_FILE + "'");
        
        LinkedList<String> paths = StaticMethods.readTextArrayFromFile(WebLink.CONF_FILE, null);
        
        if (paths != null && paths.size() >= 6) {
            pathDOT = paths.get(0);
            pathLATEX = paths.get(1);
            pathPDF2SVG = paths.get(2);
            pathPython = paths.get(3);
            pathInkscape = paths.get(4);
            pathPdftk = paths.get(5);
            
            GlobalVariables.getParameters().logWeb("DOT path '" + pathDOT + "'.");
            GlobalVariables.getParameters().logWeb("LaTeXCode path '" + pathLATEX + "'.");
            GlobalVariables.getParameters().logWeb("PDF2SVG path '" + pathPDF2SVG + "'.");
            GlobalVariables.getParameters().logWeb("Python path '" + pathPython + "' (deprecated).");
            GlobalVariables.getParameters().logWeb("Inkscape path '" + pathInkscape + "' (deprecated).");
            GlobalVariables.getParameters().logWeb("PDFTK path '" + pathPdftk  + "'.");
            GlobalVariables.getParameters().logWeb("Success.");
        } else {
            GlobalVariables.getParameters().logError("Failed!");
        }
    }

    public static void instantiateConfFile() {
        if (CONF_FILE == null) {
            try {
                CONF_FILE = new File(mainLoggingFolder() + "/" + CONF_FILE_NAME);
                CONF_FILE.createNewFile();
                GlobalVariables.getParameters().logDebug("'" + CONF_FILE + "' written.");
            } catch (Exception e1) {
                throw new RuntimeException(e1);
            }
        }
    }

    public static void sendEmail(String[] recipients, String subject, String messageText) {
        for (String recipient : recipients) {
            sendEmail(recipient, subject, messageText);
        }
    }

    private static void sendEmail(String recipient, String subject, String messageText) {
        final String username = System.getenv("SMTP_USERNAME");
        final String password = System.getenv("SMTP_PASSWORD");

        if (username == null || password == null) {
            throw new IllegalStateException(
                    "SMTP_USERNAME/SMTP_PASSWORD environment variables not set - cannot send email.");
        }
 
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "mail.gmx.net");
        props.put("mail.smtp.port", "587");
 
        Session session = Session.getInstance(props,
          new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
          });
 
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(messageText);
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void attemptRestartingTomcatServer() {
        String command = "call \"" + new File(mainLoggingFolder() + "/restartTomcat.bat").getAbsolutePath() + "\"";
        ConvenienceMethods.execCommand(command, true, true);
    }

    public static RepresentableAsPDF getApplicablePDFType(
            String script, 
            boolean hasComments, 
            RepresentableAsPDF father,
            String workDir) {
        setWorkDir(workDir);
        
        try {
            String scriptTextWithoutComments = ScriptConversionMethods.decryptScript(script);
            
            if (hasComments) {
                scriptTextWithoutComments = ScriptConversionMethods.removeComments(scriptTextWithoutComments);
            }
            
            return ScriptConversionMethods.getApplicablePDFTypeToplevel(
                    scriptTextWithoutComments,
                    availablePDFTypes,
                    father);
        } catch (Exception e) {
            GlobalVariables.getParameters().logWeb("DON'T PANIC. This is just "
                    + "an exception in Rep. type detection. Can happen...\nJust "
                    + "in case, here's the stack trace.");
//            e.printStackTrace();
//            return null;
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets all methods to disabled that require a target PDF type which
     * is not available. Already disabled methods stay disabled.
     * 
     * @param methods                  The dynamic methods collection.
     * @param availableRepresentables  The available PDF types.
     */
    public static void checkMethodsForAvailability(
            HashMap<String, MethodWrapper> methods,
            List<Class<? extends RepresentableAsPDF>> availableRepresentables) {
        for (MethodWrapper mw : methods.values()) {
            Collection<Class<? extends RepresentableAsPDF>> convertedClass = mw.getClassesOfTargetScript();
            boolean available = false;
            
            if (convertedClass == null || convertedClass.isEmpty()) {
                available = true;
            } else {
                for (Class<? extends RepresentableAsPDF> c : convertedClass) {
                    for (Class<? extends RepresentableAsPDF> c2 : availableRepresentables) {
                        if (c == null || c2.equals(c)) {
                            available = true;
                            break;
                        }
                    }
                }
            }
            
            if (!available) {
                mw.setMethodButtonEnabled(false);
            }
        }
    }

    public static void killProcess(String process) {
        ConvenienceMethods.execCommand("taskkill.exe /F /IM " + process, true, false);
    }

    public static String[] getTomcatProcessNames() {
        return new String[] {"Tomcat8.exe", "Tomcat8w.exe"};
    }

    public static String[] getJavaProcessNames() {
        return new String[] {"javaw.exe", "java.exe"};
    }
    
    public static String getLatexProcessName() {
        return "pdflatex.exe";
    }
    
    public static String getGraphvizProcessName() {
        return "dot.exe";
    }
    
    public static String getPDF2SVGProcessName() {
        return "pdf2svg.exe";
    }
    
    /*
     * TODO: Grammar prefix cannot be guessed.
     */

    public static List<String> sortMethods(HashMap<String, MethodWrapper> methods) {
        List<String> methodNames = new ArrayList<>(methods.keySet());
        Collections.sort(methodNames, (c1, c2) -> {
            Double col1 = methods.get(c1).getDisplayLevel();
            Double col2 = methods.get(c2).getDisplayLevel();
            if (!col1.equals(col2)) {
                return col1.hashCode() - col2.hashCode();
            }
            return c1.compareTo(c2);});
        return methodNames;
    }

    // URLs to Info II Q/A.
    public static final String URL_PAR_SCRIPT_NAME = "template";
    public static final String URL_PAR_HELP = "help";
    public static final String URL_PAR_HIDE_EXAMPLES = "hide";

    public static String encodeScriptAsURLPar(String script, boolean isFirstParameter) {
        String code = "";
        String initSymb = isFirstParameter ? "?" : "&";
        
        try {
            code = initSymb + URL_PAR_SCRIPT_NAME + "=" + URLEncoder.encode(script.trim(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        return code;
    }

    public static String setSVGWidth(String srcString) {
        StringBuffer strBuff = new StringBuffer(srcString);
        String firstPart = "width=\"";
        String secondPart = "height=\"";
        
        int pos = strBuff.indexOf("<svg");
        
        strBuff = replace(pos, strBuff, firstPart, "99%");
        strBuff = replace(pos, strBuff, secondPart, "99%");
        return strBuff.toString();
    }

    private static StringBuffer replace(int startAt, StringBuffer strBuff, String firstPart, String replaced) {
        int start;
        int end;
        
        for (int i = Math.max(firstPart.length(), startAt); i < strBuff.length(); i++) {
            if (strBuff.substring(i - firstPart.length(), i).equals(firstPart)) {
                start = i;
                
                for (int j = i; j < strBuff.length(); j++) {
                    if (strBuff.charAt(j) == '\"') {
                        end = j;
                        return strBuff.replace(start, end, replaced);
                    }
                }
                
            }
        }
        
        return null;
    }

    // Helper methods for logging.
    
    public static String getDynamicMethodButtonsHTMLString(
            RepresentableAsPDF r, 
            String escapedScript,
            boolean isEnglish,
            List<Class<? extends RepresentableAsPDF>> availablePDFTypes,
            String regexMethodName,
            String regexCurrClass,
            String regexTargetClass,
            HashMap<String, Integer> clickCounts) {
        return getDynamicMethodButtonsHTMLString(
                r,
                escapedScript,
                isEnglish, 
                false, 
                availablePDFTypes,
                regexMethodName,
                regexCurrClass,
                regexTargetClass,
                clickCounts);
    }
    
    public static String getDynamicMethodButtonsHTMLString(
            RepresentableAsPDF r, 
            String escapedScript,
            boolean isEnglish,
            boolean allDisabled,
            List<Class<? extends RepresentableAsPDF>> availablePDFTypes,
            String regexMethodName,
            String regexCurrClass,
            String regexTargetClass,
            HashMap<String, Integer> clickCounts) {
        double max = 0;
        if (clickCounts != null && clickCounts.size() > 0) {
            for (int c : clickCounts.values()) {
                max = MiscMath.max(max, (double) c);
            }
            max += MIN_COLOR_FACTOR;
        }
        
        if (r != null) {
            String dynamicMethodButtons = "";
            HashMap<String, MethodWrapper> methods = r.getFilteredDynamicMethods(
                    regexMethodName,
                    regexCurrClass,
                    regexTargetClass);
            
            WebLink.checkMethodsForAvailability(methods, availablePDFTypes);
    
            List<String> methodNames = WebLink.sortMethods(methods);
            double oldCol = -1;
            String levelName = "";
            
            if (methodNames.size() > 0) {
                MethodWrapper mw = methods.get(methodNames.get(0));
                oldCol = mw.getDisplayLevel();
                levelName = isEnglish ? mw.getLevelName() : ConvenienceMethods.replaceSpecialCharsHTML_G(mw.getLevelName_G());
            }

            dynamicMethodButtons += collectionPreambleHTMLString(null, levelName, null, "ConversionMethods", "invisibleBorder");
//                    "<div><span style=\"font-weight: bold;\">" + levelName + "</span>";
            
            for (String methodName : methodNames) {
                MethodWrapper mw = methods.get(methodName);

                Color bckgnd = mw.getBckgnd();
                double level = mw.getDisplayLevel();
                levelName = isEnglish ? mw.getLevelName() : mw.getLevelName_G();

                if (oldCol != level) {
                    dynamicMethodButtons += COLLECTION_POSTAMBLE_HTML_STRING
                            + collectionPreambleHTMLString(null, levelName, null, null, "invisibleBorder");
//                            + "<div><span style=\"font-weight: bold;\">" + levelName + "</span>";
                    oldCol = level;
                }
                
                if (mw.isMethodButtonVisible()) {
                    double proz = 0;
                    if (clickCounts != null && clickCounts.size() > 0) {
                        Integer cc = clickCounts.get(methodName);
                        
                        if (cc == null) {
                            cc = 0;
                        }
                        
                        proz = 0.8 - ((double) cc + MIN_COLOR_FACTOR) / max;
                    }

                    bckgnd = new Color(
                            (int) (bckgnd.getRed() + ((255 - bckgnd.getRed()) * proz)), 
                            (int) (bckgnd.getGreen() + ((255 - bckgnd.getGreen()) * proz)), 
                            (int) (bckgnd.getBlue() + ((255 - bckgnd.getBlue()) * proz)));
                    
                    dynamicMethodButtons += singleButtonHTML(isEnglish, mw, allDisabled, bckgnd, mw.getDisplayName(), escapedScript);
                }
            }

            String additionalClass = "";
            if (allDisabled) {
                additionalClass = " mLDisabled";
            }

            // Add web free button.
            String additionalPars2 = " title=\"" +
                    (isEnglish
                        ? "Unlocks the current script in the database and retrieves the URL to it"
                        : ConvenienceMethods.replaceSpecialCharsHTML_G("Macht das aktuelle Skript in der Datenbank vom Web aus zugänglich und gibt die entsprechende URL zurück"))
                    + "\"";
            
            dynamicMethodButtons += "<input class=\"button" + additionalClass + "\" type=\"submit\" name=\"" 
            + SET_WEB_FREE_NAME
            + "\" value=\"" 
            + (isEnglish ? SET_WEB_FREE_VALUE : SET_WEB_FREE_VALUE_G) 
            + "\"" 
            + additionalPars2
            + ">\r\n";
            
            dynamicMethodButtons += "</div>";
//            dynamicMethodButtons += COLLECTION_POSTAMBLE_HTML_STRING;
            return embedInTable("<div style=\"text-align: left;\">" + dynamicMethodButtons + "</div>", isEnglish);
        }
        
        return "";
    }

    private static String embedInTable(String html, boolean english) {
        return "<table style=\"width:100%\">\r\n" + 
                "  <tr>\r\n" + 
                "    <td id=\"rotate\">" + (english ? "Conversion</BR> methods" : "Konversions-</BR>methoden") + "</td>\r\n" + 
                "    <td>" + html + "</td> \r\n" + 
                "  </tr>\r\n" + 
                "</table>";
    }

    public static String collectionPreambleHTMLString(String anchor2, String name, String cssClass, String cssID, String cssClassFieldset) {
        String anchor = name;
        
        try {
            anchor = "<a name=\"" + anchor2 + "\">" + name + "</a>";
        } catch (Exception e) {
        }
        
//        String style = "";
//        if (bckgndCol != null) {
//            style = "style=\"background-color: " + bckgndCol + ";\"";
//        }
        
        String classString = "";
        String idString = "";
        String classStringFieldSet = "";
        
        if (cssClass != null) {
            classString = " class=\"" + cssClass + "\"";
        }
        
        if (cssID != null) {
            idString = " id=\"" + cssID + "\"";
        }

        if (cssClassFieldset != null) {
            classStringFieldSet = " class=\"" + cssClassFieldset + "\"";
        }
        
        return "\r\n<DIV" + classString + ">"
                + "\r\n"
//                + "<form action=\"Wizz\" " + style + " method=\"post\">\r\n"
                + "<fieldset" + classStringFieldSet + idString + ">"
                + "<legend><b>" + anchor + "</b></legend>\r\n";
    }
    
    public static String getDynMethodButtonName(String methodName) {
        return "**>" + methodName;
    }

    public static String getDynMethodDisplayButtonName(boolean isEnglish, MethodWrapper mw) {
        return isEnglish ? mw.getDisplayNameWithDots() : ConvenienceMethods.replaceSpecialCharsHTML_G(mw.getDisplayNameWithDots_G());
    }
    
    /**
     * @param mw  A method wrapper object.
     * 
     * @return  Iff the method is active under the current conditions, i.e., if 
     *          - it is allowed in productive mode or
     *          - the current mode is desktop mode or
     *          - the current mode is web debug mode.
     */
    private static boolean allowProductive(MethodWrapper mw) {
        if (mw.isUseInWebProductiveMode()) {
            return true;
        } else {
            if (MainLink.isApplicationOriginDesktop()) {
                return true;
            } else {
                if (WebLink.isDebugMode()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }
    
    private static String additionalParsForDynButtonWithParsHTML(
            boolean isEnglish, boolean allDisabled, MethodWrapper mw, Color bckgnd2) {
        Color bckgnd = bckgnd2;
        String additionalPars = "";
        
        if (!allDisabled && (!mw.isMethodButtonEnabled() || !allowProductive(mw))) {
            bckgnd = new Color(
                    (bckgnd.getRed() + Color.lightGray.getRed()) / 2, 
                    (bckgnd.getGreen() + Color.lightGray.getGreen()) / 2, 
                    (bckgnd.getBlue() + Color.lightGray.getBlue()) / 2);
            
            if (!allowProductive(mw)) {
                mw.setTooltip(mw.getTooltip() + " (method marked instable, only available in Debug mode.)");
                mw.setTooltip_G(mw.getTooltip_G() + " (diese Methode ist als instabil gekennzeichnet und wird nur im Debug-Modus aktiviert.)");
            }
        }
        
        if (bckgnd != null) {
            additionalPars += " style=\"background-color:rgb(" 
                    + bckgnd.getRed() + "," 
                    + bckgnd.getGreen() + "," 
                    + bckgnd.getBlue() 
                    + ");\"";
        }
        
        String tooltip = isEnglish ? mw.getTooltip() : ConvenienceMethods.replaceSpecialCharsHTML_G(mw.getTooltip_G());
        
        if (tooltip != null) {
            additionalPars += " title=\"" + tooltip + "\"";
        }
        
        return additionalPars;
    }

    private static String additionalParsForDynButtonHTML(boolean isEnglish,
            boolean allDisabled, MethodWrapper mw, Color bckgnd2) {
        Color bckgnd = bckgnd2;
        String additionalPars = "";
   
        if (!allDisabled && (!mw.isMethodButtonEnabled() || !allowProductive(mw))) {
            bckgnd = new Color(
                    (bckgnd.getRed() + Color.lightGray.getRed()) / 2, 
                    (bckgnd.getGreen() + Color.lightGray.getGreen()) / 2, 
                    (bckgnd.getBlue() + Color.lightGray.getBlue()) / 2);
            
            if (!allowProductive(mw)) {
                mw.setTooltip(mw.getTooltip() + " (method marked instable, only available in Debug mode.)");
                mw.setTooltip_G(mw.getTooltip_G() + " (diese Methode ist als instabil gekennzeichnet und wird nur im Debug-Modus aktiviert.)");
            }
        }
        
        if (bckgnd != null) {
            additionalPars += " style=\"background-color:rgb(" 
                    + bckgnd.getRed() + "," 
                    + bckgnd.getGreen() + "," 
                    + bckgnd.getBlue() 
                    + ");\"";
        }
        
        String tooltip = isEnglish ? mw.getTooltip() : ConvenienceMethods.replaceSpecialCharsHTML_G(mw.getTooltip_G());
        if (tooltip != null) {
            additionalPars += " title=\"" + tooltip + "\"";
        }
        return additionalPars;
    }

    /**
     * Important to escape script!!
     */
    public static String singleButtonHTML(
            boolean isEnglish, 
            MethodWrapper mw, 
            boolean allDisabled, 
            Color bckgnd,
            String valuePrefix,
            String escapedScript) {
        String methodName = mw.getDisplayName();
        String methodName_G = mw.getDisplayName_G();
        String buttonString;
        String disabled = "";
        if (allDisabled || !mw.isMethodButtonEnabled() || !allowProductive(mw)) {
            disabled = " mLDisabled";
        }
        
        if (mw.getParameterCount() > 0) {
            String additionalPars = additionalParsForDynButtonWithParsHTML(isEnglish, allDisabled, mw, bckgnd);
            buttonString = "<a href=\"#openModal" + methodName.hashCode() + "\" class=\"modalLink" + disabled + "\"" + additionalPars + ">" 
                    + (isEnglish ? methodName : ConvenienceMethods.replaceSpecialCharsHTML_G(methodName_G)).replace(" ", "&nbsp;")
                    + "...</a>\r\n" + 
                    "<div id=\"openModal" + methodName.hashCode() + "\" class=\"modalDialog\">\r\n" + 
                    "    <div>\r\n" + 
                    "        <a href=\"#close\" title=\"Close\" class=\"close\">X</a>\r\n" + 
                    "\r\n" + 
                    buildParametersHTMLPage(mw, valuePrefix, isEnglish, escapedScript) +
                    "    </div>\r\n" + 
                    "</div>\r\n";
        } else {
            String additionalPars = additionalParsForDynButtonHTML(isEnglish, allDisabled, mw, bckgnd);
            buttonString = "<input " + onClickDisable(isEnglish, mw.isMethodButtonEnabled()) + "class=\"button" + disabled + "\" type=\"submit\" name=\"" + getDynMethodButtonName(methodName)
                    + "\" value=\"" + getDynMethodDisplayButtonName(isEnglish, mw) + "\"" + additionalPars + ">\r\n";
        }
        
        return buttonString;
    }
    
    public static String buildParametersHTMLPage(
            MethodWrapper mw,
            String methodName,
            boolean english,
            String escapedScript) {
        String html = "<form action=\"Wizz\" method=\"post\">";
        
        html += "<DIV class=\"hiddenDIV\">";
        html += "<textarea " 
            + "title=\"" 
            + "\" name=\"mainTextArea\" maxlength=\"50000\">"
            + escapedScript
            + "</textarea>";
        html += "</DIV>";
        
        Parameter[] pars = mw.getMethodToWrap().getParameters();
        
        String tempHTML = "";
        if (pars != null) { // Actually parameters.
            int num = 0;
            
            String conversionMethodString = english ? " Please enter parameters:" : " Bitte Parameter eingeben:";
            tempHTML += "<H3>" 
                    + (english ? mw.getTooltip() : ConvenienceMethods.replaceSpecialCharsHTML_G(mw.getTooltip_G()))
                    + conversionMethodString
                    + "</H3>";
            tempHTML += "<table style=\"width:100%\">";
            
            for (Parameter p : pars) {
                tempHTML += "<tr>";
                String parExplanation = english 
                        ? mw.getParameterExplanation(num) 
                        : ConvenienceMethods.replaceSpecialCharsHTML_G(mw.getParameterExplanation_G(num));
                
                tempHTML += "<td>";
                tempHTML += p.getName() + ": ";
                tempHTML += "</td><td>";
                tempHTML += "<input type=\"text\" name=\"" + p.getName() + "\">";
                tempHTML += "</td><td>";
                tempHTML += " (<B>" 
                        + p.getType().getSimpleName()
                        + "</B>"
                        + (parExplanation.isEmpty() ? "" : ": " + parExplanation)
                        + ")<BR/>";
                
                tempHTML += "<div"
                        + " class=\"hiddenDIV\""
                        + "><textarea name=\"paramArea" + num + "\">\r\n" 
                        + p.getName()
                        + "</textarea></div>";
                tempHTML += "</td>";
                tempHTML += "</tr>";
                
                num++;
            }
            
            tempHTML += "</table>";
        } else { // Pragmatic switch to exercise input :-)
            tempHTML += (english ? "Your solution" : ConvenienceMethods.replaceSpecialCharsHTML_G("Deine Lösung")) + ": ";
            tempHTML += "<input type=\"text\" name=\"" + "ExerciseSolution" + "\">";
            tempHTML += "<BR/>";
        }
        
        html += HelpTexts.par(tempHTML);
                
        String submitButtonDisplayName = english
                ? SUBMIT_PARS_BUTTON_NAME
                : SUBMIT_PARS_BUTTON_NAME_G;
        
        String tempHTML2 = "";
        tempHTML2 += "<input class=\"button\"  type=\"submit\" name=\""
                + SUBMIT_PARS_BUTTON_NAME
                + "\" value=\"" 
                + submitButtonDisplayName 
                + "\""
                + ">\r\n";
        html += HelpTexts.par(tempHTML2) + "\r\n";

        html += plainTextArea(null, english, false, "");
        html += "<div class=\"hiddenDIV\">" + "<textarea name=\"methodNameArea\">" 
                + getDynMethodButtonName(methodName)
                + "</textarea></div>";

        return html + "</form>";
    }

    public static final String SET_WEB_FREE_NAME = "setWebFree";
    public static final String SET_WEB_FREE_VALUE = "Short URL (ID) to this script...";
    public static final String SET_WEB_FREE_VALUE_G 
        = ConvenienceMethods.replaceSpecialCharsHTML_G("Erstelle kurze URL (ID) zu diesem Skript...");
    
    public static String onClickDisable(boolean isEnglish, boolean isEnabled) {
        if (!isEnabled) {
            return "";
        }
        
        return "";
        
//        return "onclick=\"this.value='" 
//                + (isEnglish ? BUTTON_DISABLED_TEXT : BUTTON_DISABLED_TEXT_G)
//                + "'; this.mainForm.submit();\" ";
    }

    public static String sectionHTMLString(
            String html, 
            String sectionAnchor, 
            String sectionName,
            int titleLevel,
            String wrapAroundID) {
        return sectionHTMLString(html, sectionAnchor, sectionName, titleLevel, wrapAroundID, false);
    }
    
    public static String sectionHTMLString(
            String html, 
            String sectionAnchor, 
            String sectionName,
            int titleLevel,
            String wrapAroundID,
            boolean invisibleTitle) {
        String preamble = "";
        String postamble = "";
        String section = "<section id=\"" + sectionAnchor + "\">" 
                + (invisibleTitle 
                    ? "<BR/>"
                    : "<H" + titleLevel + ">" + sectionName + "</H" + titleLevel + ">")
                + html 
                + "</section>";

        if (wrapAroundID != null) {
            preamble = "<DIV id=\"" + wrapAroundID + "\">\r\n";
            postamble = "\r\n</DIV>";
        }
        
        return preamble + section + postamble;
    }

    private static String cookieUserName = "Default";
    
    public static void setCookieUserName(String cookieUserName) {
        WebLink.cookieUserName = cookieUserName;
    }
    
    public static String getCookieUserName() {
        return cookieUserName;
    }
    
    /**
     * Important to escape script!!
     */
    public static String plainTextArea(Exercise exercise, boolean english, boolean show, String escapedScript) {
        String classes = "txt_area";
        String id = "txtarea";
        String preamble = "";
        String postamble = "";
        
        if (!show) {
            preamble = "\r\n<div class=\"hiddenDIV\">";
            postamble = "</div>\r\n";
        }
        
        String scriptTooltip = english
                ? "Type or paste a script. Scripts completely control "
                    + VFPVariables.PROG_NAME_XWIZZ + "'s behavior. (You can use unlocked ids 'ID-1234', too.)"
                : "Tippe ein Skript ein oder kopiere es von woanders. Skripte kontrollieren den " 
                    + VFPVariables.PROG_NAME_XWIZZ + " vollst&auml;ndig. (Auch freigegebene Ids 'ID-1234' sind erlaubt.)";
        
        String disabled = isEncrypted(exercise)
//                ? "readonly "
                ? ""
                : "";
        
        return preamble
                + "<textarea " 
                + disabled 
                + "title=\"" 
                + scriptTooltip 
                + "\" name=\"mainTextArea\" maxlength=\"50000\" id=\"" 
                + id 
                + "\" class=\"" 
                + classes 
                + "\">\r\n" 
                + escapedScript
                + "</textarea>"
                + postamble;
    }

    public static boolean isEncrypted(Exercise exercise) {
        return exercise != null && (exercise.isEncrypted() || exercise.isExEncrypted());
    }

    public static String fileName(String baseName) {
        return baseName + "_" + WebLink.getCookieUserName();
    }


    public static String defaultFileName() {
        return fileName(WebLink.DEFAULT_OUTPUT_FILE_NAME);
    }
    
    private static String consoleOutputFolder() {
        File folder = new File(mainLoggingFolder() + "/consoleOutput/");
        folder.mkdirs();
        return folder.getAbsolutePath() + "/";
    }

    private static String scriptOutputFolder() {
        File folder = new File(mainLoggingFolder() + "/scripts/");
        folder.mkdirs();
        return folder.getAbsolutePath() + "/";
    }

    public static String mainLoggingFolder() {
        File folder = new File("./logging/");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder.getAbsolutePath();
    }
    
    public static void deleteAllLoggingFiles() {
        File folder1 = new File(consoleOutputFolder());
        File folder2 = new File(scriptOutputFolder());
        for (File f : folder1.listFiles()) {
            f.delete();
        }
        for (File f : folder2.listFiles()) {
            f.delete();
        }
    }
    
    public static void setWebMode() { // This is the standard mode.
        MainLink.setWebMode();
        Integer storedLogLevel = GlobalVariables.getParameters().getLogOutputLevel();
        MainLink.loggingLevelWhenNotInWebMode = storedLogLevel == null ? StaticMethods.LOG_INFO: storedLogLevel;
        
        if (debugMode) {
            GlobalVariables.getParameters().setLoggingLevel(StaticMethods.LOG_DEBUG);
        } else {
            GlobalVariables.getParameters().setLoggingLevel(StaticMethods.LOG_WARNING);
//            try {redirectConsoleOutput();} catch (Exception e1) {e1.printStackTrace();}
        }
        
        GlobalVariables.getParameters().setStoreLogMessages(false);
    }
    
    public static RepresentableAsPDF getRepByClassName(String className) {
        for (Class<? extends RepresentableAsPDF> repClass : availablePDFTypes) {
            if (repClass.getSimpleName().equals(className)) {
                return RepresentableFactory.getRepByClass(repClass);
            }
        }
        
        return null;
    }
    
    /**
     * Lightweight function, use "carelessly" ;-)
     * 
     * @param script  A script.
     * 
     * @return  The representable class corresponding to the script or else {@code null}.
     */
    public static Class<? extends RepresentableAsPDF> getScriptClass(String script) {
        for (Class<? extends RepresentableAsPDF> repClass : availablePDFTypes) {
            try {
                if (RepresentableFactory.getRepByClass(repClass).isAcceptableScript(
                        ScriptConversionMethods.decryptScript(script))) {
                    return repClass;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }

    public static String EXCEPTION_EXPLANATION = null;
    public static int count = 0;
    
    public static String createDOTExceptionNode(String exceptionDescription) {
        return "exceptionNode" + (count++) + " [label=\"" + exceptionDescription + "\" shape=\"rectangle\" color=\"red\"];";
    }
    
//    public static void main(String[] args) {
//        System.out.println(PDFGeneratorWindow.decryptScript("scrypt:401s3H0f332f142Q2k063Z0x232B1y3o2F2H212O2I2l3y0s16370t2o071k3c2c1S0b3A2e2e1k1x3Q2m1D2q0D1V451N0u0X3h2y1q0I0Z0p0f253J13020Z0p0f2B0V1q2Z0N3H2s3Q463i1W1f0N1d0x1d0m3i080c3y2H0y2x2l2R2t2R0F3F3E1s0m0V0s2k0p1k3W3g0R0s3M2Z1f2F3Q2O0z0s0r2g3m1b0O2O06380H0K3b243R2G0p3C3V0J0Q0o2G2O0v0Z2p1K3M2L3H3t3v3L0A1H1v3Y3S1y3d2Y1P2f3y1N0H1D1Z0I0q1n1k2A1B2A2G3N3W1P2T1D1O1I2h0L073n3k443Q2O0y3M180W1i1Q1k0t0R1K05210c3M1Z3n2f1t2f282a3c1V260j110T1b0S261W241I2q3G0m"));
//    }
}
