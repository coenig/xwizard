/*
 * File name:        Wizz.java (package mainServlet)
 * Author(s):        Lukas K�nig
 * Java version:     8.0
 * Generation date:  10.07.2015 (07:53)
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

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import eas.GlobalVariables;
import eas.miscellaneous.StaticMethods;
import veryFastPDF.HelpTexts;
import veryFastPDF.TextbookScripts;
import veryFastPDF.VFPVariables;
import veryFastPDF.algorithms.latex.LaTeXCommands;
import veryFastPDF.pdfProcessors.LaTeXPDF;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.MemorizingHashMap;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.script.RepresentableFactory;
import veryFastPDF.script.ScriptConversionMethods;
import veryFastPDF.web.ConvenienceMethods;
import veryFastPDF.web.SessionMetaInf;;

/**
 * Servlet implementation class Wizz.
 */
@WebServlet("/Wizz")
public class Wizz extends HttpServlet {

    private static final String BODY_ONLOAD_SCROLL_TO_OUTPUT = "  <body onload=\"scrollTo('Output')\">";
    private static final long serialVersionUID = 1L;
    private static final String BUTTON_RETURN_TO_MAIN_PAGE = "<input class=\"button\" type=\"submit\" name=\"returnToMainPage\" value=\"Return to main page\">\r\n";
    private static final String BUTTON_RETURN_TO_MAIN_PAGE_G = "<input class=\"button\" type=\"submit\" name=\"returnToMainPage\" value=\"Zur&uuml;ck zur Hauptseite\">\r\n";

    /**
     * This code is not actually supposed to be pasted directly in the HTML code, 
     * but rather imported from file XWizard.js into HTML.
     */
    private static String JAVA_SCRIPT_CODE = null;
    
    /**
     * The JS header pasted into the HTML code.
     */
    private static String JAVA_SCRIPT_HEADER = null;
    
    /**
     * The JS references pasted into the HTML code, including the import of
     * XWizard.js.
     */
    private static String JAVA_SCRIPT_REFERENCES = null;
    
    /*
     * http://stackoverflow.com/questions/31207763/java-easter-calculator?rq=1
     */
    public LocalDate getEasterDate(int year) {
        int a = year % 19;
        int b = year % 4;
        int c = year % 7;
        int k = year / 100;
        int p = (13 + 8 * k) / 25;
        int q = k / 4;
        int M = (15 - p + k - q) % 30;
        int N = (4 + k - q) % 7;
        int d = (19 * a + M) % 30;
        int e = (2 * b + 4 * c + 6 * d + N) % 7;

        if (d == 29 && e == 6) {
            return LocalDate.of(year, 3, 22).plusDays(d + e).minusDays(7);
        } else
            return LocalDate.of(year, 3, 22).plusDays(d + e);
    }
    
    /**
     * @return  A customized welcome message depending on the time of year
     *          or so...
     */
    @SuppressWarnings("unused")
    private String firstScriptToShow(boolean english) {
        int year = Calendar.getInstance().get(Calendar.YEAR); 
        int month = Calendar.getInstance().get(Calendar.MONTH); 
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        LocalDate beforeEaster = getEasterDate(year).minusDays(4);
        LocalDate afterEaster = getEasterDate(year).plusDays(1);
        LocalDate today = LocalDate.now();
        
        boolean christmas = month == 11 && day >= 18 && day < 27;
        boolean easter = today.isAfter(beforeEaster) && today.isBefore(afterEaster);
        boolean halloween = month == 9 && day > 29;

        halloween = false;
        
        return /* christmas 
                ? LaTeXCommands.WELCOME_CHRISTMAS
                : (easter
                        ? LaTeXCommands.WELCOME_EASTER(english)
                        : (halloween 
                                ? LaTeXCommands.WELCOME_HALLOWEEN
                                :*/ LaTeXCommands.XWIZZ_WELCOME_MESSAGE_LOGO(english);
    }

    static {
        WebLink.setDebugMode(true); // This is, among initialization, to force class loader to load WebLink.
    }

    private MemorizingHashMap<String> languages = new MemorizingHashMap<>(VFPVariables.LANGUAGE_ENGLISH);
    private MemorizingHashMap<Collection<Class<? extends RepresentableAsPDF>>> 
        examplesToHide = new MemorizingHashMap<>(new HashSet<>());
    private MemorizingHashMap<String[]> hiddenPersonalizedMessage = new MemorizingHashMap<>(new String[] {"", ""});
    private MemorizingHashMap<String> currentMode = new MemorizingHashMap<>("");

    private static HashMap<String, Boolean> cachedMode = new HashMap<>();
    private String retrievedFromCacheString = "";
    private String retrieveCachedSVG;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean isInLockedMode = false;
        resetTooltipDIVs();
        
        loadJavaScript(request);
        
        String source = null;
        Cookie userNameCookie = getOrSetUserNameCookie(request, response);
        String cookieUserName = userNameCookie.getValue();
        
        // This should be available on every page.
        String script = request.getParameter("mainTextArea");
        
        script = resolvePotentialChachedMode(script, cookieUserName);
        
//        if (script == null) {
            // This should actually NEVER occur!
//            script = SCRIPT_NOT_RECEIVED_MESSAGE + " (Execution point 1: this is very unusual).";
//        } else {
        script = script.trim();
//        }
        
        Integer scrID = scriptIsID(script);
        if (scrID != null) {
            script = SQLQueries.getWebFreeScript(scrID);
        }
        
        Exercise exercise = this.getExercise(script);
        boolean exerciseAvailable = RepresentableDefault.exerciseAvailable(exercise);

        script = encryptForDisplay(script, exercise);

        // Check if parameters page was submitted.
        String submitParsButton = request.getParameter(WebLink.SUBMIT_PARS_BUTTON_NAME);
        if (submitParsButton != null) {
            source = SessionMetaInf.SOURCE_CONVERSION_BUTTON;
            
            String params = "[";
            
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                String nameBase = "paramArea";
                String parAreaName = nameBase + i;
                String parName = request.getParameter(parAreaName);
                
                if (parName == null) {
                    break;
                }
                
                String parValue = request.getParameter(parName);
                
                params += "\"" + parValue + "\"";
                if (request.getParameter(nameBase + (i + 1)) != null) {
                    params += ",";
                }
            }
            
            params += "]";
            
            if (exercise.isEncrypted()) {
                script = ScriptConversionMethods.decryptScript(script) + "\n**>" 
                            + request.getParameter("methodNameArea")
                            + params 
                            + "<**";
                script = ScriptConversionMethods.encryptScript(script);
            } else {
                script = script + "\n**>" 
                        + request.getParameter("methodNameArea")
                        + params 
                        + "<**";
            }
        }
        
        RepresentableAsPDF currentRep;
        try {
            currentRep = WebLink.getApplicablePDFType(
                    script, 
                    true,
                    null,
                    this.realPath());
        } catch (Exception e) {
            e.printStackTrace();
            currentRep = null;
        }
        
        // Check if task solution requested.
        String exerciseSolveButton = request.getParameter("SolveButton");
        String solution = null;
        Boolean solvedValue = null;
        if (exerciseSolveButton != null) {
            solution = request.getParameter("ExerciseSolution");
            
            if (solution != null && exercise != null && solution.equals(exercise.getSolution())) {
                solvedValue = true;
            } else {
                solvedValue = false;
            }
        }
        
        // Check if one of the dynamic method buttons was clicked.
        String lastMethodClickedName = null;
        if (currentRep != null) {
            source = SessionMetaInf.SOURCE_CONVERSION_BUTTON;
            String[] filters = getFilters(exercise);
            HashMap<String, MethodWrapper> methods = currentRep.getFilteredDynamicMethods(
                    filters[0],
                    filters[1],
                    filters[2]);
            for (String methodName : methods.keySet()) {
                String buttonName = WebLink.getDynMethodButtonName(methodName);
                String button = request.getParameter(buttonName);
                if (button != null) {
                    SQLQueries.addConversionMethodClicked(currentRep, methodName);

//                  MethodWrapper mw = methods.get(methodName);
//                  Parameter[] pars = mw.getMethodToWrap().getParameters();
//                  if (pars.length > 0) {
////                        currentMethodNames.put(cookieUserName, methodName);
//                      String html = WebLink.buildParametersHTMLPage(
//                              mw,
//                              methodName,
//                              isLanguageEnglish(cookieUserName));
//
//                      PrintWriter out = response.getWriter();
//                      out.println(subsequentFormDIV(html));
//
//                      return; // Wait for button click of parameters page.
//                  }

                    if (exercise.isEncrypted()) {
                        script = ScriptConversionMethods.decryptScript(script) + "\n" + buttonName + "<**";
                        script = ScriptConversionMethods.encryptScript(script);
                    } else {
                        script = script + "\n" + buttonName + "<**";
                    }

                    lastMethodClickedName = methodName;
                    
                    break;
                }
            }
        }
        
        // Check if one of the example buttons was clicked.
        int i = 0;
        for (Class<? extends RepresentableAsPDF> repClass : WebLink.availablePDFTypes) {
            RepresentableAsPDF r = RepresentableFactory.getRepByClass(repClass);
            String button = request.getParameter(getExampleButtonName(r, cookieUserName));
            if (button != null && r.getExampleScripts().length > 0) {
                source = SessionMetaInf.SOURCE_EXAMPLE_BUTTON;
                script = r.getExampleScripts()[WebLink.getExampleCounters()[i] 
                                % r.getExampleScripts().length];
                WebLink.getExampleCounters()[i]++;
                break;
            }
            i++;
        }

        // Check if Draw! button was clicked.
        if (script != null) {
            source = SessionMetaInf.SOURCE_DRAW_BUTTON; // TODO - this might also be a dynamic method.
        }

        // Check if the "return" button of a subsequent page was clicked.
        String returnToMainPage = request.getParameter("returnToMainPage");
        if (returnToMainPage != null) {
            source = SessionMetaInf.SOURCE_RETURN_FROM_SUBSEQUENT_PAGE;
//          script = PDFGeneratorWindow.removeConversionTagsFrom(
//                          originalScripts.get(cookieUserName));
        }

        // Make script web-free.
        String webFreeButton = request.getParameter(WebLink.SET_WEB_FREE_NAME);
        if (webFreeButton != null) {
            boolean english = this.isLanguageEnglish(cookieUserName);
            int scriptID = SQLQueries.makeWebFree(
                    ScriptConversionMethods.decryptScript(script));
            
            String messageUnsuccessful = english 
                    ? "Script could not be made web-free. "
                            + "This happens, for example, when you try to make the welcome "
                            + "script web-free which is not possible for technical reasons. "
                            + "\n\nKnown bug: If you just used a conversion method, click the button again, "
                            + "then it should work."
                    : "Skript konnte nicht freigegeben werden. "
                            + "Das kann beispielsweise beim Willkommens-Skript passieren, "
                            + "weil dieses nicht freigegeben werden kann.\n\nBekannter Fehler: Wenn das aktuelle Skript "
                            + "durch eine Konversionsmethode entstanden ist, sollte ein wiederholtes "
                            + "Anklicken des Buttons das Problem beheben.";
            
            String plainID = "ID-" + scriptID;
            String completeURL = VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER 
                    + WebLink.encodeScriptAsURLPar(plainID, true);
            String idMessage = scriptID >= 0 ? completeURL : messageUnsuccessful;
            
            String titleSuccessful = english 
                    ? "Script ID released for the web, use the short URL as an alternative to the long one."
                    : "Skript ID freigegeben, kurze URL kann als Alternative zur langen verwendet werden.";
            String titleUnsuccessful = english 
                    ? "Script not released for the web"
                    : "Skript nicht freigegeben";

            String title = scriptID >= 0 ? titleSuccessful : titleUnsuccessful;
            
            PrintWriter out = response.getWriter();
            out.println(this.buildPlainTextOutputPage(
                    idMessage, 
                    cookieUserName,
                    title,
                    script));

            return;
        }

        // Show all web-free scripts.
        String showWebFree = request.getParameter(SHOW_ALL_WEB_FREE_IDS_NAME);
        String showWebFreeWithID = null;
        Map<String, String[]> map = request.getParameterMap();
        
        for (String s : map.keySet()) {
            if (s.startsWith("ID-")) {
                String id = map.get(s)[0];
                showWebFreeWithID = id.contains(" ") ? id.split(" ")[0] : id;
            }
        }
        
        if (showWebFree != null || showWebFreeWithID != null) {
            boolean english = this.isLanguageEnglish(cookieUserName);
            
            PrintWriter out = response.getWriter();
            out.println(this.buildAllScriptIDsHTMLPage(
                    english, 
                    script,
                    showWebFreeWithID));

            return;
        }

        SessionMetaInf info = getMetaInf(
                request,
                cookieUserName, 
                SessionMetaInf.SOURCE_METHOD_DO_POST, 
                source,
                null,
                script,
                exerciseAvailable,
                solvedValue,
                solution,
                WebLink.isEncrypted(exercise),
                scrID);
        
        response.reset();
        
        // Calculate output according to script.
        buildStandardHTMLPage(
                request, 
                response, 
                0, 
                info, 
                cookieUserName, 
                false, 
                script, 
                solvedValue, 
                solution,
                lastMethodClickedName,
                isInLockedMode,
                getLBExplanation(script));
    }

    public static String inferCachedMode(String script) {
        if (script == null) {
            throw new RuntimeException("Cannot infer cached attribute to null.");
        } else {
            if (script.startsWith("C")) { // Script is already in cached mode.
                return script;
            } else if (scriptIsID(script) != null) { // Script is an ID.
                if (script.contains("C")) { // ID is already in cached mode.
                    return script;
                } else { // ID is not in cached mode.
                    return "C" + script.toUpperCase().replace("ID-", "");
                }
            } else { // Regular script is not in cached mode.
                return "C" + script;
            }
        }
    }
    
    public static String resolvePotentialChachedMode(String script, String cookieUser) {
        String script2 = script;
        
        if (script != null && script.startsWith("C")) { // Enter chached mode for regular script.
            cachedMode.put(cookieUser, true);
            script2 = script.substring(1);
        } else if (script != null && scriptIsID(script) != null && script.contains("C")) { // Enter chached mode for ID.
            cachedMode.put(cookieUser, true);
            script2 = script.replace("C", "");
        } else {
            cachedMode.put(cookieUser, false);
        }

        return script2;
    }

    private void loadJavaScript(HttpServletRequest request) {
        if (JAVA_SCRIPT_CODE == null) {
            JAVA_SCRIPT_CODE = StaticMethods.readTextFromFile(new File(request.getServletContext().getRealPath("/XWizard.js")), null);
            JAVA_SCRIPT_HEADER = StaticMethods.readTextFromFile(new File(request.getServletContext().getRealPath("/XWizard_JS_Header.txt")), null);
            JAVA_SCRIPT_REFERENCES = StaticMethods.readTextFromFile(new File(request.getServletContext().getRealPath("/XWizard_JS_References.txt")), null);
            
            HTML_HEADER_BASE = HTML_HEADER_BASE_NO_JS + JAVA_SCRIPT_HEADER;
            HTML_HEADER_PLAIN = HTML_HEADER_BASE + "<body><form action=\"Wizz\" method=\"post\" name=\"mainForm\">";
            HTML_HEADER_JUMP_TO_OUTPUT = HTML_HEADER_BASE + BODY_ONLOAD_SCROLL_TO_OUTPUT + "<form action=\"Wizz\" method=\"post\">";
            
            String docs = "<HR/><BR/><div style=\"float: left; display: inline-block; width: 75%; margin: 0 auto; background-color: lightblue; padding: 10px 10px 10px 10px; border-width: 2px; border-radius: 1em; border-style: solid;\">"
                    + HelpTexts.XWIZZ_HTML + " documentation" + " ("+ "see also " + HelpTexts.link("http://www.xwizard.de:8080/Wizz?help", "help pages", false, "Detailed information for all available script types") + "):" + "<BR/>"
                    + "<UL>"
                    + "<LI>" + HelpTexts.link("http://www.dasinfobuch.de/docs/documentation_users.pdf", "XWizard for regular users", true, "XWizard documentation for regular users.") + " (pdf, English)</LI>"
                    + "<LI>" + HelpTexts.link("http://www.dasinfobuch.de/docs/documentation_teachers.pdf", "XWizard for teachers", true, "XWizard documentation for people who want to go beyond regular usage.") + " (pdf, English)</LI>"
                    + "<LI>" + HelpTexts.link("http://www.dasinfobuch.de/docs/documentation_users_G.pdf", "XWizard f&uuml;r normale Benutzer", true, "XWizard documentation for regular users.") + " (pdf, Deutsch)</LI>"
                    + "<LI>" + "<i>XWizard f&uuml;r Lehrer" + " (pdf, Deutsch; in Arbeit)</i></LI>"
                    + "<BR/>"
                    + "<LI>" + "<i>XWizard for Developers" + " (pdf, English; work in progress)</i></LI>"
                    + "<LI>" + "<i>XWizard f&uuml;r Entwickler" + " (pdf, Deutsch; in Arbeit)</i></LI>"
                    + "</UL>";
            
            String beforePart = docs + "<div class=\"centered\">\r\n" + "<SPAN style=\"font-size:smaller;\">" 
                    + VFPVariables.HTML_COPYRIGHT_PARAGRAPH_XWIZZ 
                    + "</SPAN>" + "\r\n"
                    + "</div></div>"
                    + "<center><div style=\"width: 18%; float: right;\">"
                    + HelpTexts.link("http://www.dasinfobuch.de/", 
                                     "Empfohlen:<BR/>"
                                             + "<img width=\"85%\" src=\"http://dasinfobuch.de/bilder/Cover-small.png\"></img>"
                                             + "<BR/>Neu und sehr gut!",
                                     true,
                                     "Neues Lehrbuch zum Grundlagenstudium theoretische Informatik (ab Herbst 2016)")
                    + "</div></center>"
                    + "<div style=\"clear: both;\">&nbsp;</div>"
                    ;
            
            String afterPart = "</form>" + JAVA_SCRIPT_REFERENCES + "</body>\r\n" + "</html>";
            HTML_FOOTER_WITH_CLOSING_WRAPPER_DIV = beforePart + "</div>" + afterPart;
            HTML_FOOTER_WITHOUT_CLOSING_WRAPPER_DIV = beforePart + afterPart;
        }
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        WebLink.setDebugMode(false); // Sets debug mode according to debug file existing or not.
        resetTooltipDIVs();

        loadJavaScript(request);

        Cookie userNameCookie = getOrSetUserNameCookie(request, response);
        String cookieUserName = userNameCookie.getValue();

        String urlScript = request.getParameter(WebLink.URL_PAR_SCRIPT_NAME);
        String urlLanguage = request.getParameter(VFPVariables.URL_PAR_LANGUAGE);
        String urlHelpText = request.getParameter(WebLink.URL_PAR_HELP);
        String urlHiddenExamples = request.getParameter(WebLink.URL_PAR_HIDE_EXAMPLES);
        String urlImpressum = request.getParameter("impressum");
        String source;

        urlScript = resolvePotentialChachedMode(urlScript, cookieUserName);
        
        // Set URL language.
        if (urlLanguage != null) {
            this.languages.put(cookieUserName, urlLanguage);
        }

        String script = urlScript == null 
                ? resolvePotentialChachedMode(firstScriptToShow(isLanguageEnglish(cookieUserName)), cookieUserName) 
                : firstScriptToShow(isLanguageEnglish(cookieUserName));
        
        Integer scrID = null;
        
        if (urlScript == null) {
            source = SessionMetaInf.SOURCE_WELCOME;
        } else {
            scrID = scriptIsID(urlScript);
            
            if (scrID != null) {
                try {
                    String plainScript = SQLQueries.getWebFreeScript(scrID);

                    script = this.isEncryptedScript(plainScript)
                                ? ScriptConversionMethods.encryptScript(plainScript)
                                : plainScript;
                } catch (Exception e) {
                    boolean english = this.isLanguageEnglish(cookieUserName);
                    
                    String urlMalformed = english ? "Malformed URL template, sorry!" : "URL-Template nicht korrekt formatiert.";
                    script = LaTeXPDF.message
                            ("\\begin{center}" + urlMalformed
                                    + "\\par"
                                    + "'" + urlScript + "'"
                                    + "\\end{center}");
                }
                
                source = SessionMetaInf.SOURCE_URL_PAR_FROM_ID;
            } else {
                script = formatScriptFromURL(urlScript);
                source = SessionMetaInf.SOURCE_URL_PAR;
            }
        }
        
        Exercise exercise = this.getExercise(script);
        boolean exerciseAvailable = RepresentableDefault.exerciseAvailable(exercise);
        if (exercise.isEncrypted()) {
            script = ScriptConversionMethods.encryptScript(script);
        }

        // Set hidden examples.
        if (Arrays.equals(this.hiddenPersonalizedMessage.get(cookieUserName), new String[] {"", ""})) {
            resetModeToAllExamples(cookieUserName);
        }

        if (urlHiddenExamples != null) {
            resetModeToAllExamples(cookieUserName);
            this.currentMode.put(cookieUserName, urlHiddenExamples);
            this.examplesToHide.put(cookieUserName, new HashSet<>());
        }
        
        if (urlHiddenExamples != null && !urlHiddenExamples.isEmpty()) {
            if (RepresentableFactory.ABBREVIATIONS.keySet().contains(urlHiddenExamples)) {
                this.hiddenPersonalizedMessage.put(
                        cookieUserName, 
                        new String[] {RepresentableFactory.ABBREVIATIONS.get(urlHiddenExamples), 
                                RepresentableFactory.ABBREVIATIONS_G.get(urlHiddenExamples)});

                boolean logo = false;
                String courseHomepageLink = null;
                String courseHomepageName = null;
                String courseHomepageName_G = null;
                
                if (urlHiddenExamples.equals(ConvenienceMethods.INFO_II_MODE_NAME)) {
                    courseHomepageLink = HelpTexts.URL_TO_INFO2;
                    courseHomepageName = "Course homepage";
                    courseHomepageName_G = "Vorlesungsseite";
//                    logo = true;
                }
                
                if (urlHiddenExamples.equals(ConvenienceMethods.EFFALG_MODE)) {
                    courseHomepageLink = "http://www.toterlink.de";
                    courseHomepageName = "Course homepage";
                    courseHomepageName_G = "Vorlesungsseite";
//                    logo = true;
                }
                
                setHiddenMessage(
                        cookieUserName,
                        urlHiddenExamples,
                        courseHomepageLink,
                        courseHomepageName,
                        courseHomepageName_G,
                        logo);
                
                Collection<Class<? extends RepresentableAsPDF>> hidden = examplesToHide.get(cookieUserName); // Kept empty by commended parts below.
                hidden.clear();

                List<Class<? extends RepresentableAsPDF>> show 
                    = RepresentableFactory.CLASS_COLLECTIONS.get(urlHiddenExamples);
                
                for (Class<? extends RepresentableAsPDF> c : WebLink.availablePDFTypes) {
                    if (!show.contains(c)) {
//                        hidden.add(c);
                    }
                }
                
                // Catch unstable reps, too.
                for (Class<? extends RepresentableAsPDF> c : RepresentableFactory.getAvailableWebRepNamesWhichMightBeUnstable()) {
                    if (!show.contains(c)) {
//                        hidden.add(c);
                    }
                }
            } else {
                this.hiddenPersonalizedMessage.put(
                        cookieUserName, 
                        new String[] {
                                showAllModeLinks(true, null), 
                                showAllModeLinks(false, null), 
                                });

                String[] toHide = urlHiddenExamples.split(",");
                Collection<Class<? extends RepresentableAsPDF>> hidden = examplesToHide.get(cookieUserName);
                hidden.clear();
                for (@SuppressWarnings("unused") String exp : toHide) {
//                    hidden.add(RepresentableFactory.repClassBySimpleName(exp));
                }
            }
        }
        
        // Switch to help mode or show legal notice.
        RepresentableAsPDF repForHelpAndImpressum = WebLink.getRepByClassName(urlHelpText);
        if (repForHelpAndImpressum == null) {
            repForHelpAndImpressum = RepresentableDefault.getStaticInstance();
        }

        if (urlImpressum != null) {
            response.reset();
            source = SessionMetaInf.SOURCE_IMPRESSUM_BUTTON;

            // Store meta information in database.
            SessionMetaInf sessionInfoImpressumPage = getMetaInf(
                    request,
                    cookieUserName, 
                    SessionMetaInf.SOURCE_METHOD_DO_GET, 
                    source,
                    repForHelpAndImpressum,
                    script,
                    exerciseAvailable,
                    null,
                    null,
                    WebLink.isEncrypted(exercise),
                    scrID);

            SQLQueries.accessDatabase(
                    null, 
                    -1, 
                    -1, 
                    0, 
                    sessionInfoImpressumPage, 
                    null, 
                    null, 
                    0,
                    null); // No watchdog.

            buildImpressumHTMLPage(request, response, sessionInfoImpressumPage, cookieUserName, script);

            return;
        }
        
        if (urlHelpText != null) {
            response.reset();
            source = SessionMetaInf.SOURCE_HELP_BUTTON;
            
            // Store meta information in database.
            SessionMetaInf sessionInfoHelpPage = getMetaInf(
                    request,
                    cookieUserName, 
                    SessionMetaInf.SOURCE_METHOD_DO_GET, 
                    source,
                    repForHelpAndImpressum,
                    script,
                    exerciseAvailable,
                    null,
                    null,
                    WebLink.isEncrypted(exercise),
                    scrID);
            
            SQLQueries.accessDatabase(
                    null, 
                    -1, 
                    -1, 
                    0, 
                    sessionInfoHelpPage, 
                    null, 
                    null, 
                    0,
                    null); // No watchdog.
                
            buildHelpHTMLPage(request, response, cookieUserName, repForHelpAndImpressum, script);

            return;
        }
        
        SessionMetaInf info = getMetaInf(
                request,
                userNameCookie.getValue(), 
                SessionMetaInf.SOURCE_METHOD_DO_GET, 
                source,
                null,
                script,
                exerciseAvailable,
                null,
                null,
                WebLink.isEncrypted(exercise),
                scrID);
        
        try {
            response.reset();
            buildStandardHTMLPage(
                    request, 
                    response, 
                    0, 
                    info, 
                    cookieUserName, 
                    true, 
                    script,
                    null,
                    null,
                    null,
                    false,
                    getLBExplanation(script));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getLBExplanation(String script) {
        String explanation = "";
        if (TextbookScripts.containsScript(script)) {
            int id = TextbookScripts.getIDbyScript(script);
            explanation = "<DIV style=\"border-radius: 33px 33px 33px 33px;-moz-border-radius: 33px 33px 33px 33px;" + 
                    "-webkit-border-radius: 33px 33px 33px 33px;border: 8px double #000000;text-align:center;background-color: lightblue;\"><BR/>"
                    + ConvenienceMethods.replaceSpecialCharsHTML_G(
                            HelpTexts.bold("<div style=\"color:black;font-size:1.4em\">Willkommen, Lehrbuch-Leserinnen und -Leser! "
//                                  + "<span style=\"color:#000099\">" + VFPVariables.LB_NAME + ".</span></div>")
                                    + HelpTexts.link("http://www.dasinfobuch.de", VFPVariables.LB_NAME, true, "Zur Buchwebsite...") + "</div>")
                    + " Skript ID-" + id + ": " + TextbookScripts.getScriptName(id))
                    + "<P></P>";
            explanation += "<a href=\"#OOutput\"><img class=\"simpleLink cccenter fffit\" src=\"http://dasinfobuch.de/bilder/" 
                    + TextbookScripts.getDARString(id) + ".gif\"></a>";
            explanation += ConvenienceMethods.replaceSpecialCharsHTML_G(HelpTexts.par(TextbookScripts.getScriptExplanation(id))) + "<BR/>";
            
            for (int i = 1; i <= TextbookScripts.getScriptNum(); i++) {
                String dar = TextbookScripts.createDarStringFromNumber(i);
                if (TextbookScripts.getDARNum(id) == i) {
                    explanation += "<span style=\"white-space: nowrap;\">" + dar + "</span>";
                } else {
                    explanation += HelpTexts.link(
                            "http://www.xwizard.de:8080/Wizz?template=ID-" + TextbookScripts.getIDbyDarNum(i) + "&lang=ger&hide=.lb#Output", 
                            dar + " ", 
                            false, 
                            "Lade Darstellung " + dar + " aus dem Lehrbuch " + VFPVariables.LB_NAME);
                }
                
                explanation += " &nbsp; ";
            }
            
            explanation += "<BR/></DIV><DIV id=\"OOutput\">";
            explanation += "</DIV><BR/><BR/>";
        }
        
        return explanation;
    }

    public void resetModeToAllExamples(String cookieUserName) {
        this.hiddenPersonalizedMessage.put(
                cookieUserName, 
                new String[] {
                        showAllModeLinks(true, new HashSet<>()), 
                        showAllModeLinks(false, new HashSet<>()), 
                        });
    }

    public String encryptForDisplay(String script, Exercise exercise) {
        if (exercise.isExEncrypted()) {
            script = ScriptConversionMethods.encryptExScript(script, WebLink.availablePDFTypes);
        }
        
        if (exercise.isEncrypted()) {
            script = ScriptConversionMethods.encryptScript(script);
        }
        return script;
    }

    public static Integer scriptIsID(String script) {
        try {
            String scr = script.trim();
            if (scr.length() > 0 && Character.isDigit(scr.charAt(0)) 
                    || scr.length() > 3 && scr.substring(0, 3).toUpperCase().equals("ID-")) {
                    return Math.abs(Integer.parseInt(scr.replaceAll("[^0-9]","")));
            }
        } catch (Exception e) {
            
        }
        
        return null;
    }
    
    private void setHiddenMessage(
            String cookieUserName, 
            String abbreviation, 
            String link, 
            String linkName,
            String linkName_G,
            boolean aifbLogo) {
        HashSet<String> abbs = new HashSet<>();
        abbs.add(abbreviation);
        
        String greeting = RepresentableFactory.ABBREVIATIONS.get(abbreviation) 
                + "&nbsp;&nbsp;"
                + (link == null ? "" : HelpTexts.link(link, linkName, false) + " | ")
                + showAllModeLinks(true, abbs);
        
        String greeting_G = RepresentableFactory.ABBREVIATIONS_G.get(abbreviation) 
                + "&nbsp;&nbsp;"
                + (link == null ? "" : HelpTexts.link(link, linkName_G, false) + " | ")
                + showAllModeLinks(false, abbs);
        
        String logo = aifbLogo
                ? ("<span style=\"float: right;\">"
                    + "<a target=\"_blank\" href=\"http://www.aifb.kit.edu\">"
                    + "<img width=\"150\" src=\"http://www.xwizard.de:8080/AIFB_pos_c_rgb.png\"/> "
                    + "</a>"
                    + "</span>"
                    + "<span style=\"clear:left;\"></span>")
                : "";
        
        this.hiddenPersonalizedMessage.put(
                cookieUserName, 
                new String[] {
                        greeting + logo, 
                        greeting_G + logo});
    }

    private String showAllModeLinks(boolean english, HashSet<String> omit) {
        HashSet<String> newOmit = new HashSet<>();
        String allExamples = "";
        
        if (omit != null) {
            newOmit = omit;
        }
        
        if (omit == null || !newOmit.isEmpty()) {
            allExamples = english 
                    ? HelpTexts.link(VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER + "?hide#Examples", "Show all examples")
                    : HelpTexts.link(VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER + "?hide#Examples", "Zeige alle Beispiele");
        }
                
        for (String abb : RepresentableFactory.ABBREVIATIONS.keySet()) {
            if (!newOmit.contains(abb)) {
                allExamples += 
                        (allExamples.isEmpty() ? "" : " | ") + 
                        (english
                        ? HelpTexts.link(VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER + "?hide="
                                + abb
                                + "#Examples", RepresentableFactory.ABBREVIATIONS.get(abb))
                        : HelpTexts.link(VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER + "?hide="
                                + abb
                                + "#Examples", RepresentableFactory.ABBREVIATIONS_G.get(abb)));
//            } else {
//                allExamples += 
//                        (allExamples.isEmpty() ? "" : " | ") + 
//                        (english
//                        ? RepresentableFactory.ABBREVIATIONS.get(abb)
//                        : RepresentableFactory.ABBREVIATIONS_G.get(abb));
            }
        }
        
        return "";
//        return allExamples;
    }

    private boolean isLanguageEnglish(String cookieUserName) {
        return this.languages.get(cookieUserName).equals(VFPVariables.LANGUAGE_ENGLISH);
    }

    private Cookie getOrSetUserNameCookie(HttpServletRequest request, HttpServletResponse response) {
        String userNameName = "userName";
        Cookie cookies[] = request.getCookies();  
        
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(userNameName) && ConvenienceMethods.isNonNegativeInteger(cookies[i].getValue())) {
                    cookies[i].setMaxAge(60 * 60 * 24 * 365);
                    return cookies[i]; // Cookie already existed.
                }
            }
        }
        
        // Create new cookie.
        Cookie cookie = new Cookie(userNameName, generateCookieUserID(request));
        cookie.setMaxAge(60 * 60 * 24 * 365);
        response.addCookie(cookie);
        
        return cookie;
    }
    
    private String generateCookieUserID(HttpServletRequest request) {
        long idCounter = getUserID();
        return idCounter + "";
    }

    /**
     * Stores all ids used so far in the current program run. As IDs are
     * given according to the current system time, older IDs from
     * former runs should not matter (if the server's system clock runs
     * normally; if not, an ID collision is still negligibly unlikely).
     */
    private static HashSet<Long> alreadyUsedIDs = new HashSet<>();
    
    protected static long getUserID() {
    /*     
     * The commented part returns the next free ID from the table.
     * To keep the database interface slim, I'd rather like to generate a random id.
     * Also to avoid "Gitte's attack" by changing the personal cookie to someone else's
     * ID.
     */

//      updateTablesInDebugMode(); // Do nothing if not in DEBUG MODE.
//      
//      Connection connect = establishConnection();
//        try {
//            String queryCount = "select count(*) from " + TABLE_NAME_SESSION_DATA;
//            String queryMax = "select MAX(" + SessionMetaInf.COLUMN_NAME_COOKIE_USER_NAME + ") as maxID from " + TABLE_NAME_SESSION_DATA;
//            
//            ResultSet rsCount = executeQuery(queryCount, connect, false);
//            rsCount.next();
//            if (rsCount.getInt(1) == 0) {
//                return 0; // Return minimal valid ID if table empty.
//            }
//            
//            ResultSet rsMaxID = executeQuery(queryMax, connect, false);
//            
//            if (rsMaxID.next()) {
//                return rsMaxID.getInt("maxID");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        
//        return -1;
        
        Long id = System.currentTimeMillis();
        
        while (alreadyUsedIDs.contains(id)) {
            id++;
        }
        
        alreadyUsedIDs.add(id);
        
        return id;
    }

    /* ******** Meta-Inf building building follows ******** */

    private SessionMetaInf getMetaInf(
            HttpServletRequest request,
            String cookieUserName,
            String sourceMethod,
            String sourceType,
            RepresentableAsPDF r, // Only for help mode.
            String script,
            boolean exercise,
            Boolean exerciseSolved,
            String solution,
            boolean encrypted,
            Integer fromID
            ) {
        
        // From: http://stackoverflow.com/questions/8515161/detecting-device-type-in-a-web-application
        String completeInformation = request.getHeader("User-Agent");
        
        boolean mobileAccess = false;
        String browserName = "N.A.";
        if (completeInformation == null) {
            completeInformation = "N.A.";
        } else {
            mobileAccess = completeInformation.indexOf("Mobile") != -1;
            browserName = getBrowserInfo(completeInformation);
        }
        
        Class<? extends RepresentableAsPDF> repClass = null;
        if (r == null) {
            repClass = WebLink.getScriptClass(script); 
        } else {
            if (r != null) {
                repClass = r.getClass();
            }
        }
        
        String simpleClassName = repClass == null ? "null" : repClass.getSimpleName();
        
        SessionMetaInf info = new SessionMetaInf(
                sourceType, 
                sourceMethod, 
                mobileAccess, 
                completeInformation, 
                browserName,
                cookieUserName,
                this.languages.get(cookieUserName),
                simpleClassName,
                exercise,
                exerciseSolved,
                solution,
                encrypted,
                fromID == null ? null : fromID.toString());
        
        return info;
    }
    
    public String getBrowserInfo(String information) {
        String browsername = "";
        String browserversion = "";
        String browser = information;
        if (browser.contains("MSIE")) {
            String subsString = browser.substring(browser.indexOf("MSIE"));
            String info[] = (subsString.split(";")[0]).split(" ");
            browsername = info[0];
            browserversion = info[1];
        } else if (browser.contains("Firefox")) {
            String subsString = browser.substring(browser.indexOf("Firefox"));
            String info[] = (subsString.split(" ")[0]).split("/");
            browsername = info[0];
            browserversion = info[1];
        } else if (browser.contains("Chrome")) {
            String subsString = browser.substring(browser.indexOf("Chrome"));
            String info[] = (subsString.split(" ")[0]).split("/");
            browsername = info[0];
            browserversion = info[1];
        } else if (browser.contains("Opera")) {
            String subsString = browser.substring(browser.indexOf("Opera"));
            String info[] = (subsString.split(" ")[0]).split("/");
            browsername = info[0];
            browserversion = info[1];
        } else if (browser.contains("Safari")) {
            String subsString = browser.substring(browser.indexOf("Safari"));
            String info[] = (subsString.split(" ")[0]).split("/");
            browsername = info[0];
            browserversion = info[1];
        }
        
        return browsername + "-" + browserversion;
    }
    
    private String formatScriptFromURL(String scriptFromURL) {
        return scriptFromURL.replace(";", ";\n").replace(":", ":\n").replace("\n ", "\n").replace("  ", " ").replace(" --", "\n--").replace("-- ", "--\n").replace("\n\n", "\n").trim();
    }

    /* ******** HTML building follows ******** */
    
    public static final String SHOW_ALL_WEB_FREE_IDS_NAME = "showAllIDs";
    public static final String SHOW_ALL_WEB_FREE_IDS_VALUE = "Show all web-free script IDs";
    public static final String SHOW_ALL_WEB_FREE_IDS_VALUE_G = "Zeige alle freigegebenen Skript-IDs";
   
//    private static final String BUTTON_DISABLE_SCRIPT = "" +
//            "<script>\r\n" +
//            " $(\":submit\").closest(\"mainForm\").submit(function(){\r\n" + 
//            "        $(':submit').attr('disabled', 'disabled');\r\n" + 
//            " });" +
////            "$('mainForm').submit(function(){\r\n" + 
////            "    $(this).find('input[type=submit]').prop('disabled', true);\r\n" + 
////            "});" +
//            "</script>\r\n";
//            ;
    
    @SuppressWarnings("unused")
    private static final String AJAX_WEB_SERVICE_CALL = "<html>\r\n" + 
            "<head>\r\n" + 
            "    <title>SOAP JavaScript Client Test</title>\r\n" + 
            "</head>\r\n" + 
            "<body>\r\n" + 
            "    <script type=\"text/javascript\">\r\n" + 
            "        function soap() {\r\n" + 
            "var hallowelt = \"bdd:0111001\";\r\n" + 
            "var params = JSON.stringify({script: hallowelt});\r\n" + 
            "\r\n" + 
            "$.ajax({\r\n" + 
            "    type: \"POST\",\r\n" + 
            "    url: \"http://www.xwizard.de:8080/services/DeScriptor/retrieveSVGFromScript\",\r\n" +
            "    data: params,\r\n" + 
            "    dataType: \"json\",\r\n" + 
            "    contentType: \"application/json; charset=utf-8\",\r\n" +
            "    crossDomain: true,\r\n" +
            "    success: function (msg) {\r\n" + 
            "        console.log(msg.d); //Ausgabe auf der Konsole (öffnet sich mit F12)\r\n" + 
            "    },\r\n" + 
            "    error: function (xhr, status, error) {\r\n" + 
            "        // Falls der HttpRequest nicht erfolgreich war, geht's hier weiter\r\n" + 
            "    }\r\n" + 
            "});\r\n" +
            "}" +
            "    </script>\r\n" + 
            "    <form name=\"Demo\" action=\"\" method=\"post\">\r\n" + 
            "        <div>\r\n" + 
            "            <input type=\"button\" value=\"Soap\" onclick=\"soap();\" />\r\n" + 
            "        </div>\r\n" + 
            "    </form>\r\n" + 
            "</body>\r\n" + 
            "</html>";

//    private static final String JAVA_SCRIPT = 
//      "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js\"></script>\n" +
//      "<script src=\"tipped-4.5.4-light/js/tipped/tipped.js\"></script>" +
//      "<script src=\"codemirror-5.11/lib/codemirror.js\"></script>\r\n" + 
//      "<script src=\"codemirror-5.11/mode/stex/stex.js\"></script>\r\n" + 
//      "<script>\r\n" +
//      "$('.button').bind('click', function(){\r\n" + 
//      "    if($(this).hasClass('mLDisabled')){\r\n" + 
//      "        return false;\r\n" + 
//      "    }\r\n" + 
//      "});\r\n" + 
//      "$('.modalLink').bind('click', function(){\r\n" + /*Checkt ob Elemente der Klasse "modalLink" außerdem die Klasse "mLDisabled" besitzen. Wenn ja, wird Event abgebrochen*/
//      "    if($(this).hasClass('mLDisabled')){\r\n" + 
//      "        return false;\r\n" + 
//      "    }\r\n" + 
//      "});\r\n" + 
//      "        $(function() {\r\n" +
//      "Tipped.create('.modalLink', {\r\n" +
//      "        position: 'topleft',\r\n" +
//      "        skin: 'light'});\r\n" +
//      "Tipped.create('.button', {\r\n" + 
//      "        position: 'topleft',\r\n" + 
//      "        skin: 'light'\r\n" + 
//      "});\r\n" + 
////      AJAX_WEB_SERVICE_CALL +
//      "var codetextarea = $(\"#txtarea\");\r\n" + 
//      "var config = {\r\n" + 
//      "    mode: \"stex\",\r\n" + 
//      "    lineNumbers: true,\r\n" +
//      "    viewportMargin: Infinity,\r\n" +
////      "    value: codetextarea.val(),\r\n" + 
//      "    lineWrapping: true\r\n" +
//      "};\r\n" + 
//      "var editor = CodeMirror.fromTextArea(codetextarea.get(0), config);\r\n" + 
//      "            $('#nav1').on('click', '.nav-item', function(event) {\r\n" + 
//      "                var className = $(event.target).attr('class');\r\n" + 
//      "                console.log(className);    if(className.indexOf(\"external\") > -1) {\r\n" + 
//      "                window.open(event.target.href, \"_self\");\r\n" + 
//      "            }\r\n" + 
//      "            event.preventDefault();\r\n" + 
//      "            var hash = this.hash;\r\n" + 
//      "            var display = $('#menubutton').css('display');\r\n" + 
//      "            if(display != 'none'){\r\n" + 
//      "                $(\"#nav1\").hide(); //slideUp(\"slow\");\r\n" +
//      "            }\r\n" +
//      "            $('html, body').animate({\r\n" + 
//      "                scrollTop: $(hash).offset().top\r\n" + 
//      "                }, 800, function() {\r\n" + 
//      "                    window.location.hash = hash;\r\n" + 
//      "                });\r\n" + 
//      "            });\r\n" + 
//      "            \r\n" + 
//      "            $('#menubutton').click(function() {\r\n" + 
//      "                $(\"#nav1\").slideToggle(\"slow\");\r\n" + 
//      "            });\r\n" + 
//      "            $('#wrapper').click(function() {\r\n" + 
//      "                var display = $('#menubutton').css('display');\r\n" + 
//      "                if(display != 'none'){\r\n" + 
//      "                    $(\"#nav1\").slideUp(\"fast\");\r\n" +
//      "                }\r\n" +
//      "            });\r\n" + 
//      "            $('.disabled').click(function(e){ \r\n" +  
//      "               e.preventDefault();\r\n" +
//      "            });\r\n" +
//      "$('.button').click(function (e) {\r\n" + 
//      "    if(!$(this).hasClass('mLDisabled')){ //Nur diese if-Bedingung ist neu, alles andere bleibt gleich\r\n" + 
//      "        $('.button').addClass(\"disabled\");\r\n" + 
//      "        setTimeout(\"$('.button').removeClass('disabled')\", 2000);\r\n" + 
//      "    }\r\n" + 
//      "});\r\n" + 
//      "                 " +    
//      "        }" +
//      ");\r\n" +
////      "$('.simpleLink').click(function() {\n" + 
////      "    var className = $(event.target).attr('class');\n" + 
////      "    console.log(className);\n" + 
////      "    //Wenn externer Link -> normal �ffnen\n" + 
////      "    if (this.prev().is(\"a\")) {\n" + 
////      "\n" + 
////      "    if (className.indexOf(\"external\") > -1) {\n" + 
////      "        window.open(event.target.href, \"_self\");\n" + 
////      "    } //sonst -> smooth scrollen\n" + 
////      "    else {\n" + 
////      "        event.preventDefault();\n" + 
////      "        var hash = this.hash;\n" + 
////      "        var display = $('#menubutton').css('display'); //kann evtl noch raus, ich wei� gerade nicht wof�r die Zeile drin ist\n" + 
////      "        if (display != 'none') {\n" + 
////      "            $(\"#nav1\").hide();\n" + 
////      "        }\n" + 
////      "        $('html, body').animate({\n" + 
////      "            scrollTop: $(hash).offset().top\n" + 
////      "        }, 800, function () {\n" + 
////      "            window.location.hash = hash;\n" + 
////      "        });\n" + 
////      "    }\n" + 
////      "}\n" + 
////      "    else if(this.prev().is(\"img\")){\n" + 
////      "        var hash = \"#Output\";\n" + 
////      "        $('html, body').animate({\n" + 
////      "            scrollTop: $(hash).offset().top\n" + 
////      "        }, 800, function () {\n" + 
////      "            window.location.hash = hash;\n" + 
////      "        });\n" + 
////      "    }\n" + 
////      "});\n" + 
//      "$('.simpleLink').click(function(event) {\n" + 
//      "    var className = $(event.target).attr('class');\n" + 
//      "    var tagname = $(event.target).get(0).tagName;\n" + 
//      "    console.log(className + \", \"+tagname);\n" + 
//      "    //Wenn externer Link -> normal �ffnen\n" + 
//      "    if (tagname === \"A\") {\n" + 
//      "\n" + 
//      "    if (className.indexOf(\"external\") > -1) {\n" + 
//      "        console.log(\"inside first if\");\n" + 
//      "        window.open(event.target.href, \"_self\");\n" + 
//      "    } //sonst -> smooth scrollen\n" + 
//      "    else {\n" + 
//      "        event.preventDefault();\n" + 
//      "        var hash = this.hash;\n" + 
//      "        var display = $('#menubutton').css('display'); //kann evtl noch raus, ich wei� gerade nicht wof�r die Zeile drin ist\n" + 
//      "        if (display != 'none') {\n" + 
//      "            $(\"#nav1\").hide();\n" + 
//      "        }\n" + 
//      "        $('html, body').animate({\n" + 
//      "            scrollTop: $(hash).offset().top\n" + 
//      "        }, 800, function () {\n" + 
//      "            window.location.hash = hash;\n" + 
//      "        });\n" + 
//      "    }\n" + 
//      "}\n" + 
//      "    else if(tagname === \"IMG\"){\n" + 
//      "        var hash = \"#OOutput\";\n" + 
//      "        $('html, body').animate({\n" + 
//      "            scrollTop: $(hash).offset().top\n" + 
//      "        }, 800, function () {\n" + 
//      "            window.location.hash = hash;\n" + 
//      "        });\n" + 
//      "    }\n" + 
//      "});\n" + 
//      "</script>\r\n"
//      + "<script src=\"http://code.jquery.com/jquery-latest.js\"></script>\n" + 
//      "<script type=\"text/javascript\" language=\"JavaScript\">\n" + 
//      "  function set_body_height() { // set body height = window height\n" + 
//      "    $('body').height($(window).height());\n" + 
//      "  }\n" + 
//      "  $(document).ready(function() {\n" + 
//      "    $(window).bind('resize', set_body_height);\n" + 
//      "    set_body_height();\n" + 
//      "  });\n" + 
//      "</script>";
    
//    private static final String AUTO_SCROLLING_SCRIPT = ""
//          + "<script>"
//          + "  function scrollTo(hash) {location.hash = \"#\" + hash;}"
//          + "</script>";

    private static String HTML_HEADER_BASE = null;
    
    private static final String HTML_HEADER_BASE_NO_JS =
              "<!doctype html>\r\n" 
            + "<html>\r\n"
            + "  <head>\r\n" 
            + "    <meta name=viewport content=\"width=device-width, initial-scale=1\">"
            + "    <meta charset=\"utf-8\">\r\n" 
            + "    <title>How wizarre!</title>\r\n"
            + "    <link rel=\"icon\" href=\"http://www.xwizard.de:8080/favicon.png\" type=\"image/png\">"
            + "    <link rel=\"stylesheet\" type=\"text/css\" href=\"layout.css\">\r\n"
            + "    <link rel=\"stylesheet\" type=\"text/css\" href=\"codemirror-5.11/lib/codemirror.css\">"
            + "    <link rel=\"stylesheet\" type=\"text/css\" href=\"tipped-4.5.4-light/css/tipped/tipped.css\">"; 
    
    private static String HTML_HEADER_PLAIN = null;

    private static String HTML_HEADER_JUMP_TO_OUTPUT = null;
    
    private static String HTML_FOOTER_WITH_CLOSING_WRAPPER_DIV = null;

    private static String HTML_FOOTER_WITHOUT_CLOSING_WRAPPER_DIV = null;

    private String buildAllScriptIDsHTMLPage(boolean english, String script, String scriptFromID) {
//        WebLink.sendEmail(
//                new String[] {"lukas.koenig@kit.edu"}, 
//                "Test", 
//                test);
        
        String html = "";
        String titleParInput = english
                ? "Input parameters for conversion" 
                : "Konversions-Parameter eingeben f&uuml;r";
        
        html += WebLink.collectionPreambleHTMLString(
                titleParInput,
                english ? SHOW_ALL_WEB_FREE_IDS_VALUE : SHOW_ALL_WEB_FREE_IDS_VALUE_G,
                null,
                null,
                null);

        String realScriptFromID = null;
        String shortTit = "";
        
        if (scriptFromID != null) {
            String webFreeScript = SQLQueries.getWebFreeScript(Integer.parseInt(scriptFromID)).trim();
            
            Exercise exercise = this.getExercise(webFreeScript);
            
            if (exercise.isExEncrypted()) {
                webFreeScript = ScriptConversionMethods.encryptExScript(webFreeScript, WebLink.availablePDFTypes);
            }

            if (exercise.isEncrypted()) {
                shortTit = webFreeScript;
                    
                if (shortTit.length() > 10) {
                    shortTit = shortTit.substring(0, 10);
                }
                
                shortTit = ""
                        + "<SPAN style=\"border-width: 2px; border-radius: 1em; border-style: solid;\">&nbsp;" 
                        + (english ? "Glimpse into script: " : "Anfang des Skripts: ")
                        + "<B>'" + shortTit + "'</B>"
                        + "&nbsp;</SPAN>";
                
                webFreeScript = ScriptConversionMethods.encryptScript(webFreeScript);
            }
            
            realScriptFromID = "<DIV id=\"Codebox\">"
                + WebLink.plainTextArea(
                    null, 
                    english, 
                    true, 
                    escapeHtml4(webFreeScript))
                + "</DIV>";
        }
        
        
        String submitButtonDisplayName = realScriptFromID == null
                ? "Cancel"
                : "Draw!";
        
        String drawButton = "<div><input class=\"button\" type=\"submit\" name=\""
                                + "dummy"
                                + "\" value=\"" 
                                + submitButtonDisplayName 
                                + "\""
                                + "></div>";
        
        html += HelpTexts.par("" + (realScriptFromID == null ? "" : realScriptFromID) + shortTit + drawButton + "");

        String tempHTML = "";
        for (int id : SQLQueries.getAllWebFreeIDs()) {
            String tempScript = SQLQueries.getWebFreeScript(id);
            String type = " (" + "type" + /*WebLink.getApplicablePDFType(tempScript, true).getClass().getSimpleName()*/ ")";

            if (("" + id).toString().equals(scriptFromID)) {
                tempHTML += buttonHTML("active", null, "ID-" + id, id + type);
            } else {
                if (RepresentableDefault.exerciseAvailable(this.getExercise(tempScript))) {
                    tempHTML += buttonHTML("exercise", null, "ID-" + id, id + type);
                } else {
                    tempHTML += buttonHTML("ID-" + id, id + type);
                }
            }
        }
        
        html += HelpTexts.par(tempHTML);
        
        html += WebLink.COLLECTION_POSTAMBLE_HTML_STRING;
        html += WebLink.plainTextArea(null, english, false, escapeHtml4((realScriptFromID == null ? script : realScriptFromID)));
        html += HTML_FOOTER_WITHOUT_CLOSING_WRAPPER_DIV;

        return HTML_HEADER_PLAIN + subsequentFormDIV(html);
    }

    /**
     * Creates a HTML part for the navigation bar based on a set of anchors and
     * corresponding names.
     * 
     * @param anchorNamePairs  The anchors and names.
     * @param english          If the language is English.
     * @param sorting          The sorting of the bar entries in terms of anchors.
     * @param align            "ABC" - A: (L)eft, (R)ight or (C)enter; 
     *                                 B: (T)op, (B)ottom or (M)iddle; 
     *                                 C: (E)xternal link (optional).
     * @return  The HTML navigation part.
     */
    private String buildNavigationFrame(
            HashMap<String, String> anchorNamePairs, 
            boolean english, 
            LinkedList<String> sorting,
            LinkedList<String> align,
            String titleOfMainBlock,
            LinkedList<String> tooltips) {
        String navForMobileAddOn = 
                "        <a href=\"#nav1\" id=\"menubutton\">\r\n" + 
                "            Menu\r\n" + 
                "        </a>\r\n";
        String html = navForMobileAddOn + "<nav id=\"nav1\" class=\"nav\">\n";
        int currentDivNum = 1;
        int i = 0;
        String lastAlign = null;
        
        html += "<center>" + VFPVariables.xwizzLogo(62) + "</center>";
        
        for (String anchor : sorting) {
            String alignCurr = align.get(i);
            
            if (alignCurr.length() < 2) {
                throw new RuntimeException();
            }
            
            if (!alignCurr.equals(lastAlign)) {
                if (currentDivNum > 1) {
                    html += "</div></p>";
                }
                
                if (alignCurr.contains("M") && alignCurr.contains("R")) {
                    html += "<h3 style=\"padding: 2px 32px 10px 0;\">" 
                            + titleOfMainBlock
                            + "</h3>";
                }
                
                html += "<p><div class=\"navWrapper" + alignCurr.charAt(0) + alignCurr.charAt(1) + "\">\n";
                currentDivNum++;
            }
            
            String name = anchorNamePairs.get(anchor);
            
            if (name != null) {
                String navClass = alignCurr.contains("E") ? "nav-item-external" : "nav-item";
                
                String tooltip = english 
                        ? tooltips.get(i) 
                        : ConvenienceMethods.replaceSpecialCharsHTML_G(tooltips.get(i));

                html += "<a title=\"" + tooltip + "\" href=\"" + anchor + "\" class=\"" + navClass + " simpleLink\">" 
                        + ConvenienceMethods.replaceSpecialCharsHTML_G(name) 
                        + "</a>\n";
            }
            
            lastAlign = alignCurr;
            i++;
        }
        
        html += "</div></p>\n";
        html += "</nav>\n";
        html += "<div id=\"wrapper\">"; // Wrapped DIV follows - has to be closed in the end!
        
        String welcome = english
                ? "Welcome to " + HelpTexts.XWIZZ_HTML + ": The Online Informatics Toolbox" 
                : "Willkommen bei " + HelpTexts.XWIZZ_HTML_G + ": Das Online-Informatik-Tool";
        
        html += "<div style=\"background-color: white; color:black; text-align: center; font-size: 1.4em; font-weight: 900;\">" + welcome + "</div>";
        
        return html;
    }
    
    private boolean isInfoIIMode(String cookieUserName) {
        return ConvenienceMethods.INFO_II_MODE_NAME.equals(this.currentMode.get(cookieUserName));
//      String[] message = this.hiddenPersonalizedMessage.get(cookieUserName);
//        return (message != null) && message.length > 0 && message[0].toUpperCase().contains("INFO");
    }

    private void buildImpressumHTMLPage(
            HttpServletRequest request, 
            HttpServletResponse response,
            SessionMetaInf sessionInfoImpressumPage, 
            String cookieUserName,
            String script) throws IOException {
        boolean english = this.isLanguageEnglish(cookieUserName);
        LinkedList<String> sorted = new LinkedList<>();
        LinkedList<String> align = new LinkedList<>();
        LinkedList<String> tooltips = new LinkedList<>();
        HashMap<String, String> legalTopics = new HashMap<>();
        String impressumName = english ? "General notice" : "Allgemeine Hinweise";
        String copyrightName = "Copyright";

        String linkToOtherLanguage = VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER + "?impressum&lang=" + (english ? "ger" : "eng");
        createLanguageNavEntry(english, legalTopics, sorted, align, linkToOtherLanguage, tooltips);
        
        createBackLink(english, sorted, align, legalTopics, tooltips);
        
        tooltips.add(english ? "Copyright remarks" : "Copyright-Hinweise");
        sorted.add("#copyright");
        legalTopics.put("#copyright", copyrightName);
        align.add("RM");

        tooltips.add(english ? "General legal remarks" : "Allgemeine rechtliche Hinweise");
        sorted.add("#impressum");
        legalTopics.put("#impressum", impressumName);
        align.add("RM");
        
        String mainTitle = english ? "Legal notice" : "Impressum";
        String navigation = buildNavigationFrame(
                legalTopics, 
                english, 
                sorted, 
                align, 
                mainTitle,
                tooltips);
        
        String body = WebLink.sectionHTMLString(
                "", 
                "main", 
                mainTitle, 
                1,
                null);
        
        body += WebLink.sectionHTMLString(
                ConvenienceMethods.replaceSpecialCharsHTML_G(
                        english ? VFPVariables.XWIZZ_COPYRIGHT_HTML : VFPVariables.XWIZZ_COPYRIGHT_HTML_G), 
                "copyright",
                copyrightName,
                2,
                null);
        
        body += WebLink.sectionHTMLString(
                ConvenienceMethods.replaceSpecialCharsHTML_G(
                        english ? VFPVariables.XWIZZ_IMPRESSUM_HTML : VFPVariables.XWIZZ_IMPRESSUM_HTML_G), 
                "impressum", 
                impressumName, 
                2,
                null);

        String html = HTML_HEADER_PLAIN  
                + navigation 
                + body 
                + WebLink.plainTextArea(null, english, false, escapeHtml4(script))
                + HTML_FOOTER_WITH_CLOSING_WRAPPER_DIV;

        PrintWriter out = response.getWriter();
        out.println(html);
    }

    private void createBackLink(boolean english, LinkedList<String> sorted, LinkedList<String> align,
            HashMap<String, String> contentTopics, LinkedList<String> tooltips) {
        tooltips.add(english 
                ? "Go back to main page" 
                : ConvenienceMethods.replaceSpecialCharsHTML_G("Zurück zur Hauptseite"));
        sorted.add(VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER);
        align.add("LME");
        contentTopics.put(VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER, "&laquo; " 
                + (english ? "back" : ConvenienceMethods.replaceSpecialCharsHTML_G("zurück")));
    }

    private void buildHelpHTMLPage(
            HttpServletRequest request,
            HttpServletResponse response,
            String cookieUserName,
            RepresentableAsPDF r,
            String script) throws IOException {
        boolean english = this.isLanguageEnglish(cookieUserName);
        LinkedList<String> tooltips = new LinkedList<>();
        LinkedList<String> sorted = new LinkedList<>();
        LinkedList<String> align = new LinkedList<>();
        HashMap<String, String> helpingReps = new HashMap<>();
        
        String info2ModeAddOn = this.isInfoIIMode(cookieUserName)
                ? (english ? HelpTexts.INFO_II_MODE : ConvenienceMethods.replaceSpecialCharsHTML_G(HelpTexts.INFO_II_MODE_G))
                : "";
        
        String generalHelpText = "<div style=\"line-height: 140%\">" 
                + (english 
                        ? HelpTexts.XWIZARD_HELP 
                        : ConvenienceMethods.replaceSpecialCharsHTML_G(HelpTexts.XWIZARD_HELP_G)) 
                + info2ModeAddOn
                + "</div>";
        
        String navName = VFPVariables.PROG_NAME_XWIZZ;
        String title = english ? HelpTexts.XWIZZ_HTML + " usage guidelines" : "Anleitung zum " + HelpTexts.XWIZZ_HTML;
        
        String body = WebLink.sectionHTMLString(
                generalHelpText, 
                "main",
                title,
                1,
                null);
        
        String linkToOtherLanguage = VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER + "?help&lang=" + (english ? "ger" : "eng");
        createLanguageNavEntry(english, helpingReps, sorted, align, linkToOtherLanguage, tooltips);

        createBackLink(english, sorted, align, helpingReps, tooltips);
        
        tooltips.add((english ? "General remarks on " : "Allgemeine Hinweise zum ") + VFPVariables.PROG_NAME_XWIZZ);
        sorted.add("#main");
        align.add("RM");
        helpingReps.put("#main", navName);
        
        for (Class<? extends RepresentableAsPDF> repClass : WebLink.availablePDFTypes) {
            RepresentableAsPDF r2 = RepresentableFactory.getRepByClass(repClass);
            
            String helpText = english 
                    ? r2.helpText()
                    : r2.helpText_G();
            
            if (helpText != null) {
                tooltips.add(english 
                        ? "Help for script type '" + r2.getEnglishName() + "'" 
                        : "Hilfe zum Skripttyp '" + r2.getGermanName() + "'");
                sorted.add("#" + r2.getEnglishName());
                align.add("RM");
                helpingReps.put("#" + r2.getEnglishName(), english ? r2.getEnglishName() : r2.getGermanName());
                body += buildSingleHelpHTMLpart(english, r2);
            }
        }
        
        String navigation = buildNavigationFrame(
                helpingReps, 
                english, 
                sorted, 
                align, 
                english ? "Help topics" : "Hilfethemen",
                tooltips);
        
        String html = 
                HTML_HEADER_PLAIN  
                + navigation 
                + body 
                + WebLink.plainTextArea(null, english, false, escapeHtml4(script))
                + HTML_FOOTER_WITH_CLOSING_WRAPPER_DIV;

        PrintWriter out = response.getWriter();
        out.println(html);
    }
    
    private String buildSingleHelpHTMLpart(boolean english, RepresentableAsPDF r) {
        String helpText = english 
                ? r.helpText()
                : r.helpText_G();
                
        String rName = english 
                ? r.getEnglishName() 
                : ConvenienceMethods.replaceSpecialCharsHTML_G(r.getGermanName());

        String titleName = (english ? "Help for '" : "Hilfe zu '") + rName + "'";
        String mainPart = "<div class=\"wrapper\" style=\"line-height: 140%;\">" + helpText + "</div>";
        mainPart += "\n"
//              + "<form action=\"Wizz\" method=\"post\">\r\n" 
                + (english ? BUTTON_RETURN_TO_MAIN_PAGE : BUTTON_RETURN_TO_MAIN_PAGE_G)
//              + "</form>"
                ;

        String html = "\n" + WebLink.sectionHTMLString(mainPart, r.getEnglishName(), titleName, 2, null) + "\n";
        
        return html;
    }

    private String subsequentFormDIV(String html) {
        return "<DIV style=\"padding-top: 5%; padding-left: 15%; padding-right: 15%;\">" + html + "</DIV>";
    }
    
    public String realPath() {
        return getServletContext().getRealPath("/" + WebLink.WORKING_DIRECTORY_NAME);
    }

    private void buildStandardHTMLPage(
            HttpServletRequest request,
            HttpServletResponse response,
            int invokerNum,
            SessionMetaInf metaInf,
            String cookieUserName, 
            boolean comingFromDoGet,
            String script, 
            Boolean solvedValue, 
            String solution,
            String lastMethodClickedName,
            boolean isInLockedMode, 
            String explanationLB) throws IOException {
        String currScr = script;
        String originalScript = null;
        Exception caught = null;
        PrintWriter out = response.getWriter();
        
        addToTooltips(999, 
                isLanguageEnglish(cookieUserName) 
                ? "<h3>Short introduction</h3>" + VFPVariables.HTML_WELCOME_TEXT_XWIZZ_ENG 
                : "<h3>Kurzeinf&uuml;hrung</h3>" + VFPVariables.HTML_WELCOME_TEXT_XWIZZ_GER);
        
        if (invokerNum == 0) { // Store original script in case an error occurrs.
            originalScript = currScr;
        }
        
        String convertedString = currScr;
        retrieveCachedSVG = SQLQueries.retrieveCachedSVG(script);
        
        if (isCachedMode(cookieUserName) && retrieveCachedSVG != null) {
            convertedString = retrieveCachedSVG;
            retrievedFromCacheString = "CACHED: The current script has been retrieved from cache.";
            RepresentableDefault.removeKnownStuff();
        } else {
            retrievedFromCacheString = "(RE-)CALCULATED: The current script has not been retrieved from cache.";
            
            try {
                convertedString = WebLink.invokeVFPScriptConversion(
                        currScr, 
                        metaInf,
                        isLanguageEnglish(cookieUserName),
                        cookieUserName,
                        realPath(),
                        false,
                        0,
                        this);
            } catch (Exception e1) {
                caught = e1;
            }
            
            if (caught == null) {
                if (convertedString == null) {
                    convertedString = currScr;
                }
                
                if (isSVGcode(convertedString)) { // SVG image text received.
                    // Shavasana.
                } else if (convertedString.startsWith(RepresentableDefault.PREAMBLE_FOR_NON_SCRIPT_METHODS)) { // Script is plain text output.
                    out.println(buildPlainTextOutputPage(
                            MethodWrapper.removePreambleFrom(convertedString), 
                            cookieUserName, 
                            null,
                            ScriptConversionMethods.removeConversionTagsFrom(originalScript)));
                    return;
                } else if (invokerNum == 0){ // Recursive call maximally once (to rule out bad scripts).
        //          Exercise ex = new Exercise(RepresentableDefault.getExerciseString(convertedString));
                    if (isEncryptedScript(convertedString)) {
                        convertedString = ScriptConversionMethods.encryptScript(convertedString);
                    }
                    
                    buildStandardHTMLPage(
                            request, 
                            response, 
                            invokerNum + 1, 
                            metaInf, 
                            cookieUserName, 
                            comingFromDoGet, 
                            convertedString,
                            solvedValue,
                            solution,
                            lastMethodClickedName,
                            isInLockedMode,
                            explanationLB);
                    return;
                } else { // Script not an image and more than one recursive call.
                    currScr = originalScript; // Restore original script;
                    GlobalVariables.getParameters().logError("Bad script tried to be invoked recursively more than once in buildHTMPage.");
                }
            }
        }
        
        if (currScr == null) {
            currScr = script;
        }
        
        RepresentableAsPDF r = null;
        boolean errorOccurred = false;
        
        try {
            r = WebLink.getApplicablePDFType(currScr, true, null, this.realPath());
        } catch (Exception e) {
            e.printStackTrace();
            convertedString = errorMessage(cookieUserName, e);
            errorOccurred = true;
        }

        if (caught != null) {
            convertedString = errorMessage(cookieUserName, caught);
            errorOccurred = true;
        }
        
        if (isCachedMode(cookieUserName) && retrieveCachedSVG == null            // In cache mode store script in empty slot.
                || !isCachedMode(cookieUserName) && retrieveCachedSVG != null) { // If not in cache mode, store anyway if slot not empty.
            SQLQueries.storeCache(script, convertedString, "null");
        }
        
        out.println(buildMainPageHTML(
                convertedString, 
                r, 
                cookieUserName, 
                comingFromDoGet, 
                currScr,
                solvedValue,
                solution,
                lastMethodClickedName,
                errorOccurred, 
                explanationLB));
    }

    private String errorMessage(String cookieUserName, Throwable caughtEx) {
        String completeErrorString;
        String error = "";

        while (caughtEx != null) {
            StringWriter errors = new StringWriter();
            caughtEx.printStackTrace(new PrintWriter(errors));
            error = errors.toString().replace("at ", "</BR>at ") + "</BR></BR>Caused by:</BR>";
            caughtEx = caughtEx.getCause();
        }
        
        error += "* YOU *";
        
        completeErrorString = "</BR></BR>"
                + (this.isLanguageEnglish(cookieUserName)
                ? VFPVariables.GENERAL_ERROR_MESSAGE_XWIZZ
                : VFPVariables.GENERAL_ERROR_MESSAGE_XWIZZ_G)
                + "</BR></BR></BR></BR></BR></BR></BR></BR></BR></BR></BR></BR></BR></BR></BR></BR>"
                + "Error trace:</BR></BR>"
                + error
                + "</BR></BR></BR>";
        
        return completeErrorString;
    }
    
    private Exercise getExercise(String rawScript) {
        return new Exercise(RepresentableDefault.getExerciseString(rawScript));
    }
    
    private boolean isEncryptedScript(String rawScript) {
        return getExercise(rawScript).isEncrypted();
    }

    public static boolean isSVGcode(String convertedString) {
        return convertedString.startsWith("<");
    }

    private String buildPlainTextOutputPage(
            String textToShow, 
            String cookieUserName,
            String title, 
            String script) {
        String html = "";
        
        boolean english = this.isLanguageEnglish(cookieUserName);
        String genericTitle = english ? "Plain text output" : "Textausgabe";
        
        html += WebLink.collectionPreambleHTMLString(
                "Plain", 
                title == null ? genericTitle : title, 
                null,
                null,
                null);
        
        String areaCode = ""
            + "<textarea name=\"plainTextArea\" rows=\"15\" cols=\"110\">\r\n" 
            + textToShow
            + "</textarea><BR/>"
            + (english ? BUTTON_RETURN_TO_MAIN_PAGE : BUTTON_RETURN_TO_MAIN_PAGE_G)
            ;
        
        html += areaCode;
        html += WebLink.COLLECTION_POSTAMBLE_HTML_STRING;
        html += WebLink.plainTextArea(null, english, false, ScriptConversionMethods.removeConversionTagsFrom(escapeHtml4(script)));
        
        html = subsequentFormDIV(html);

        html = HTML_HEADER_PLAIN + html + HTML_FOOTER_WITHOUT_CLOSING_WRAPPER_DIV;
        return html;
    }

    private static String exampleToolTipDIVs = "";

    private static void resetTooltipDIVs() {
        exampleToolTipDIVs = "";
    }
    
    private static void addToTooltips(int tooltipID, String tooltipHTML) {
        exampleToolTipDIVs += "<DIV class=\"tooltip-div\" id=\"tooltip" + tooltipID + "\">"
            + tooltipHTML
            + "</DIV>";
    }

/*    
 * - Gib den 4 "Tabs" 
 *      - jeweils eine Klasse "tab", 
 *      - eine jeweils einzigartige Klasse "but1-4" (je einmal) und 
 *      - packe die 4 in ein DIV mit der ID"tabs" und 
 *      - dem ersten Tab zus�tzlich die Klasse "activeTab".
 * - Erstelle ein div mit der ID "tabcontent". 
 * - Darin erstelle 4 Divs die jeweils den Inhalt beinhalten. 
 * - Diese Divs bekommen die IDs "tab1-4". 
 * - Darin kannst du dann die Buttons werfen, die bei den jeweiligen Tabs angezeigt werden sollen (but1 zeigt tab1 usw.)
 */
    private String getExampleButtonsHTMLString(RepresentableAsPDF currentR, String cookieUserName, String exampleTitle) {
        String exampleButtonsHTML = "<p></p>";
        boolean english = this.isLanguageEnglish(cookieUserName);
        HashMap<String, List<Class<? extends RepresentableAsPDF>>> grouped = WebLink.availablePDFTypesGrouped();
        LinkedList<String> sorted = new LinkedList<>(grouped.keySet());
        int tooltipNum = 0;
        int buttNum = 0;
        ArrayList<String> singleTabs = new ArrayList<>(sorted.size());
        
        Collections.sort(sorted, RepresentableFactory.COMPARATOR_CATEGORIES);
        
        exampleButtonsHTML += "<DIV id=\"tabs\">";
        for (String name : sorted) {
            List<Class<? extends RepresentableAsPDF>> currentClasses = grouped.get(name);
            List<Class<? extends RepresentableAsPDF>> toShow = new LinkedList<>(currentClasses);
            toShow.removeAll(examplesToHide.get(cookieUserName));
            String singleTab = "";
            
            if (!toShow.isEmpty()) {
                buttNum++;
                Class<? extends RepresentableAsPDF> repCl = currentClasses.get(0);
                Class<? extends PDFProcessor> pdfType = RepresentableFactory.getRepByClass(repCl).getPDFProcessorClass();
                
                String activeTab = containsActiveRep(currentR, grouped, name);
                
                exampleButtonsHTML += 
                        "<a class="
                                + "\"" + "tab but" + buttNum + activeTab + "\""
//                                + " href=\"#Examples\""
                                + ">" 
                                + (english ? name : ConvenienceMethods.replaceSpecialCharsHTML_G(RepresentableFactory.NAMES_G.get(name)))
                                + "</a>";
//                        WebLink.collectionPreambleHTMLString(
//                        null, 
//                        english ? name : ConvenienceMethods.replaceSpecialCharsHTML_G(RepresentableFactory.NAMES_G.get(name)), 
//                        "tab but" + buttNum + activeTab,
//                        null,
//                        "invisibleBorder");
                
                for (Class<? extends RepresentableAsPDF> repClass : grouped.get(name)) {
                    RepresentableAsPDF r = RepresentableFactory.getRepByClass(repClass);
                    
                    if (!examplesToHide.get(cookieUserName).contains(repClass)) {
                        Class<? extends PDFProcessor> newType = r.getPDFProcessorClass();
                        String additionalPars = "";
                        
                        String[] exampleCodes = r.getExampleScripts();
                        
                        exampleToolTipDIVs += 
                                "<DIV class=\"tooltip-div\" id=\"tooltip" + tooltipNum + "\">"
                                + getVeryQuickHelpBoxHTMLString(r, cookieUserName)
                                + "</DIV>";
                        
                        String disabled = "";
                        String exampleButtonName = getExampleButtonName(r, cookieUserName);
                        String exampleButtonDisplayName = getExampleButtonDisplayName(r, cookieUserName);
                        
                        if (exampleCodes.length == 0) {
                            disabled = " disabled";
                        }
        
                        if (WebLink.getPossiblyUnstableReps().contains(exampleButtonName)) {
                            Color bckgnd = Color.red;
                            additionalPars += " style=\"background-color:rgb(" 
                                    + bckgnd.getRed() + "," 
                                    + bckgnd.getGreen() + "," 
                                    + bckgnd.getBlue() 
                                    + ");\"";
                        }
                        
                        String additionalClass = "";
                        
                        if (currentR != null && r.getClass().equals(currentR.getClass())) {
                            additionalClass += " active";
                        }
                        
                        if (!pdfType.equals(newType)) {
                            pdfType = newType;
                        }
                        
                        String mode = this.currentMode.get(cookieUserName);
                        String modeInfo = r.getModeDependentInfo(mode, english);
                        
                        if (!english) {
                            modeInfo = ConvenienceMethods.replaceSpecialCharsHTML_G(modeInfo);
                        }
                        
                        modeInfo = ""; // TODO: Do we want this?
                        
                        singleTab += " " + HelpTexts.noLineBreak(
                                        "<span><input class=\"button" 
                                        + additionalClass + "\" "
                                        + "data-tipped-options=\"inline: 'tooltip" + tooltipNum + "'\" "
                                        + "type=\"submit\" name=\"" 
                                        + exampleButtonName 
                                        + "\" value=\"" 
                                        + exampleButtonDisplayName
                                        + "\""
                                        + additionalPars
                                        + disabled 
                                        + ">" + modeInfo + "</span>\r\n");
                        
        
                        tooltipNum++;
                    }
                }
                
                singleTabs.add(singleTab);
//                exampleButtonsHTML += WebLink.COLLECTION_POSTAMBLE_HTML_STRING;
            }
        }
        
        if (!WebLink.isDebugMode()) { // Disable unstable reps in productive mode.
            HashSet<String> visibleUnstableReps = new HashSet<>();
            
            for (Class<? extends RepresentableAsPDF> r : WebLink.getPossiblyUnstableReps()) {
                if (!examplesToHide.get(cookieUserName).contains(r)) {
                    visibleUnstableReps.add(r.getSimpleName());
                }
            }
            
            if (!visibleUnstableReps.isEmpty()) {
                exampleButtonsHTML += "</p><p>";
            }
            
            for (String r : visibleUnstableReps) {
                String disabled = " disabled";
                String unstableTooltip = english
                        ? "is currently under construction (unstable flag has been set)"
                        : ConvenienceMethods.replaceSpecialCharsHTML_G(
                                "ist derzeit als potentiell instabil gekennzeichnet und nur im Debug-Modus verfügbar");
                
                String tooltip = "'" + r + "' " + unstableTooltip + "";
                exampleButtonsHTML += "<input class=\"button\"  title=\"" + tooltip + "\" type=\"submit\" name=\"" 
                        + r 
                        + "\" value=\"" 
                        + r 
                        + "\""
                        + disabled 
                        + ">\r\n";
            }
        }

        exampleButtonsHTML += ""
                + "</p>";

        exampleButtonsHTML += "</DIV>";

//        * - Erstelle ein div mit der ID "tabcontent". 
//        * - Darin erstelle 4 Divs die jeweils den Inhalt beinhalten. 
//        * - Diese Divs bekommen die IDs "tab1-4". 
//        * - Darin kannst du dann die Buttons werfen, die bei den jeweiligen Tabs angezeigt werden sollen (but1 zeigt tab1 usw.)

        exampleButtonsHTML += "<DIV id=\"tabcontent\">";
        int tabNum = 0;
        
        for (String tab : singleTabs) {
            tabNum++;
            exampleButtonsHTML += "<DIV id=\"tab" + tabNum + "\">";
            exampleButtonsHTML += tab;
            exampleButtonsHTML += "</DIV>";
        }
        
        exampleButtonsHTML += "</DIV>";
        
        String tempExpHTML = "";
        // Show all script ids.
        
        String additionalPars = "style=\"background-color:rgb(" 
                + 100 + "," 
                + 200 + "," 
                + 100 
                + ");\"";

        tempExpHTML += regularButtonHTML(
                (english ? "Shows a list of all script ids that have been set free for the web" 
                        : "Zeigt eine Liste aller Skript-IDs, die im Web freigegeben sind"), 
                SHOW_ALL_WEB_FREE_IDS_NAME, 
                (english ? SHOW_ALL_WEB_FREE_IDS_VALUE : SHOW_ALL_WEB_FREE_IDS_VALUE_G) + "...", 
                additionalPars,
                null,
                null,
                null,
                null);
        
        exampleButtonsHTML += HelpTexts.par(tempExpHTML);
        
        String i2TitleAddon = "<DIV style=\"font-size:14pt;color:#009D82\">" 
                    + this.hiddenPersonalizedMessage.get(cookieUserName)[english ? 0 : 1]
                    + "</DIV>";

        return WebLink.sectionHTMLString(
                i2TitleAddon + exampleButtonsHTML + exampleToolTipDIVs, 
                "Examples", 
                exampleTitle, 
                2, 
                null);
    }

    private String containsActiveRep(RepresentableAsPDF currentR,
            HashMap<String, List<Class<? extends RepresentableAsPDF>>> grouped, String name) {
        String activeTab = "";

        for (Class<? extends RepresentableAsPDF> repClass : grouped.get(name)) {
            RepresentableAsPDF r = RepresentableFactory.getRepByClass(repClass);
            if (currentR != null && r.getClass().equals(currentR.getClass())) {
                activeTab = " activeTab";
                break;
            }
        }
        return activeTab;
    }
    
    private static String regularButtonHTML(
            String title, 
            String name, 
            String value, 
            String additionalPars,
            String plainLink,
            String imageLocation,
            String tooltipID,
            String additionalClasses) {
        String butt = "";

//        <input class="button" data-tipped-options="inline:'tooltip999'" src="http://dasinfobuch.de/bilder/help.png" 
//                onClick="parent.location='http://www.xwizard.de:8080/Wizz?help'"
//                type="image">

        butt += "<input class=\"button"
                + (additionalClasses == null ? "" : " " + additionalClasses.trim())
                + "\""
                + (tooltipID == null ? "" : " data-tipped-options=\"inline: '" + tooltipID + "'\"")
                + " title=\"" + title + "\""
                + " type=\"" + (imageLocation == null ? "submit" : "image") + "\""
                + (imageLocation == null ? "" : " src=\"" + imageLocation + "\"")
                + " name=\"" + name + "\""
                + " value=\"" + value + "\""
                + (plainLink == null ? "" : " onClick=\"parent.location='" + plainLink + "'\"")
                + (additionalPars == null ? "" : " " + additionalPars)
                + ">";

        return butt;
    }
    
    private String getVeryQuickHelpBoxHTMLString(RepresentableAsPDF r, String cookieUserName) {
        String rName = isLanguageEnglish(cookieUserName)
                ? WebLink.SCRIPT_TYPE_NOT_DETECTED_STRING
                : WebLink.SCRIPT_TYPE_NOT_DETECTED_STRING_G;
        String helpText = isLanguageEnglish(cookieUserName) 
                ? "Error: Invalid script, no help provided." 
                : ConvenienceMethods.replaceSpecialCharsHTML_G("Fehler: Skript ungültig, keine Hilfe verfügbar.");
                
        if (r != null) {
            rName = this.isLanguageEnglish(cookieUserName) ? r.getEnglishName() : ConvenienceMethods.replaceSpecialCharsHTML_G(r.getGermanName());
            helpText = this.isLanguageEnglish(cookieUserName)
                    ? r.veryQuickHelpText()
                    : r.veryQuickHelpText_G();
        }
        
        String helpHTML = "";
//              WebLink.collectionPreambleHTMLString(
//              "Very",
//              (this.isLanguageEnglish(cookieUserName)
//              ? "Very quick help for" : "Schnell-Hilfe zu") + " '" + rName + "'",
//              null,
//              null);

        helpHTML += "\n<div style=\"line-height: 140%; font-size:17px;\"><font color=\"lightgray\">"
                + (helpText + "").replace("\r\n", "<BR/>")
                + "</font></div>\n";
        
        if (r != null && r.helpText() != null) {
            helpHTML += "\n"
                    + HelpTexts.par(HelpTexts.link(
                            VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER + "?help=" + r.getEnglishName() + "#" + r.getEnglishName(), 
                            (this.isLanguageEnglish(cookieUserName) ? "More help for" : "Mehr Hilfe zu") + " '" + rName + "'...", 
                            true))
                    + "\n";
        }
        
//      helpHTML = WebLink.sectionHTMLString(helpHTML, "Help", helpTitle, 2, "tooltip");
        
        return helpHTML;
    }

    private String getExampleButtonName(RepresentableAsPDF r, String cookieUserName) {
        if (r == null) {
            return isLanguageEnglish(cookieUserName)
                    ? WebLink.SCRIPT_TYPE_NOT_DETECTED_STRING
                    : WebLink.SCRIPT_TYPE_NOT_DETECTED_STRING_G;
        }
        
        return r.getClass().getSimpleName();
    }

    private String getExampleButtonDisplayName(RepresentableAsPDF r, String cookieUserName) {
        if (r == null) {
            return isLanguageEnglish(cookieUserName)
                    ? WebLink.SCRIPT_TYPE_NOT_DETECTED_STRING
                    : WebLink.SCRIPT_TYPE_NOT_DETECTED_STRING_G;
        }
        
        return this.isLanguageEnglish(cookieUserName)
            ? r.getEnglishName() : ConvenienceMethods.replaceSpecialCharsHTML_G(r.getGermanName());
    }

    private static final String HELP_BUTTON = "<SPAN>" 
            + regularButtonHTML(
                    "TEST-TITLE", 
                    "NAME", 
                    "VALUE", 
                    "", 
                    "http://www.xwizard.de:8080/Wizz?help", 
                    "http://dasinfobuch.de/bilder/help.png",
                    "tooltip999",
                    "question") 
            + "</SPAN>";
    
    private String buildMainPageHTML(
            String convertedString,
            RepresentableAsPDF r,
            String cookieUserName,
            boolean comingFromDoGet, 
            String script, 
            Boolean solvedValue, 
            String solution,
            String lastMethodClickedName,
            boolean errorOccurred, 
            String explanationLB) {
        boolean english = isLanguageEnglish(cookieUserName);
        String exampleButtonDisplayName = getExampleButtonDisplayName(r, cookieUserName);
        
        String lastClickedHTML = this.getLastClickedButtonHTML(r, lastMethodClickedName, english, script);
        
        String outputTitle = (english ? "Output" : "Ausgabe");
        String scriptTitle = (english ? "Script" : "Skript");
        String conversionMethodsTitle = (english ? "Conversion methods" : "Konversionsmethoden");
//        String helpTitle = (english ? "Very quick help" : "Schnellhilfe");
        String exampleTitle = (english ? "Examples" : "Beispiele");
//        String exerciseTitle = (english ? "Exercise" : ConvenienceMethods.replaceSpecialCharsHTML_G("Übungsaufgabe"));
        LinkedList<String> tooltips = new LinkedList<>();
        
        String outputAnchor = "Output";
        String scriptAnchor = "Codebox";
        String conversionMethodsAnchor = "ConversionMethods";
        
        // Navbar.
        HashMap<String, String> anchorNamePairs = new HashMap<>();
        LinkedList<String> sorting = new LinkedList<>();
        LinkedList<String> align = new LinkedList<>();
        
        String linkToOtherLanguage = VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER + "?lang=" + (english ? "ger" : "eng");
        createLanguageNavEntry(english, anchorNamePairs, sorting, align, linkToOtherLanguage, tooltips);

        /* ***** Exercise, if any. ****** */
        String exerciseSection = "";
        Exercise exercise = r != null ? r.getExercise() : null;

        if (RepresentableDefault.exerciseAvailable(exercise)) {
            boolean solvedCorrectly = false;
            boolean solvedIncorrectly = false;
            
            if (solvedValue != null) {
                solvedCorrectly = solvedValue;
                solvedIncorrectly = !solvedValue;
            }
            
            outputTitle = english 
                    ? "Exercise" 
                    : ConvenienceMethods.replaceSpecialCharsHTML_G("�bungsaufgabe");
            
            exerciseSection = "<P><div style=\"border-radius: 25px; background-color: #D6FFDF; padding: 10px 25px 10px 25px; border: 2px solid #009D82;\"><H3>" 
                    + (english ? "" : "") + "" 
                    + exercise.getTitle()
//                    + "<BR/><SPAN style=\"font-weight:normal;\">(" + (english 
//                            ? "Don't use the example buttons, the ." 
//                            : ConvenienceMethods.replaceSpecialCharsHTML_G("Die Konversionsmethoden dürfen genutzt werden")) + ")"
//                    + "</SPAN>
                    + "</H3>"
                    ;
            
            if (exercise.getExplanation() != null) {
                exerciseSection += exercise.getExplanation();
            }
            
            exerciseSection += "<P><Center>";
            String space = HelpTexts.SPACE;
            if (exercise.getSolution() != null) {
                if (!solvedCorrectly) {
                    exerciseSection += 
                        HelpTexts.par((english ? "Your solution" : ConvenienceMethods.replaceSpecialCharsHTML_G("Deine Lösung")) + ":" 
                            + space + space + space
                            + "<input type=\"text\" name=\"" + "ExerciseSolution" + "\">"
                            + space + space + space + buttonHTML("", "solve", "SolveButton", english 
                            ? "Solve task" 
                            : ConvenienceMethods.replaceSpecialCharsHTML_G("Löse Aufgabe")));
                }
            } else {
                exerciseSection += HelpTexts.par((english 
                                ? "No solution available for this task. Consider " 
                                    + "<B>discussing your own solution (use link below)</B>." 
                                : ConvenienceMethods.replaceSpecialCharsHTML_G("Diese Aufgabe enthält keine Lösung. "
                                    + "<B>Diskutiere Deine eigenen Lösungsansätze (nutze den u.a. Link zum Forum).</B>")));
            }
            
            if (solvedCorrectly) {
                String taskSolvedString = english ? "Task solved!" : ConvenienceMethods.replaceSpecialCharsHTML_G("Aufgabe gelöst!");
                String solCode = exercise.getSolCode();
                
                exerciseSection += "<H4>" + taskSolvedString  +  "</H4>"
                        + "<DIV style=\"font-weight: bold; color: darkgreen\">"
                        + (english
                                ? "Congratulations, your solution '" + solution + "' is correct!"
                                        + (solCode == null ? "" : "<BR/>You earned the solution code word: " + HelpTexts.button(solCode))
                                : ConvenienceMethods.replaceSpecialCharsHTML_G(
                                        "Super, die Lösung '" + solution + "' ist korrekt!"
                                         + (solCode == null ? "" : "<BR/>Du hast das folgende Codewort verdient: " + HelpTexts.button(solCode))))
                        + "</DIV>";
                
                if (exercise.getSolExp() != null) {
                    exerciseSection += HelpTexts.par("<DIV style=\"text-align: left\"><SPAN style=\"font-weight: bold;\">"
                            + (english ? "Explanation: " : "Erkl&auml;rung: ")
                            + "</SPAN>"
                            + exercise.getSolExp()
                            + "</DIV>");
                }
            }

            if (solvedIncorrectly) {
                exerciseSection += "<DIV style=\"font-weight: bold; color: red\">"
                        + (english
                                ? "Sorry, your suggestion '" + solution + "' is incorrect. Try again!"
                                : ConvenienceMethods.replaceSpecialCharsHTML_G("Die Lösung '" + solution + "' ist leider falsch. Versuche es erneut!"))
                        + "</DIV>";
            }

            String closeExercise = english ? "Close exercise" : "Aufgabe beenden";
            String closeExerciseTooltip = english 
                    ? "Closes the exercise, i.e., goes back to a regular script" 
                    : ConvenienceMethods.replaceSpecialCharsHTML_G("Schließt die Aufgabe, das heißt, dass ein normales Skript geladen wird");
            
            exerciseSection += "</Center></P>";
            exerciseSection += HelpTexts.par(
                    HelpTexts.link(
                            VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER + "", 
                            closeExercise, 
                            false, 
                            closeExerciseTooltip)
                    + " | "
                    + HelpTexts.link(
                            VFPVariables.URL_TO_ASK_QUESTION + createURLParToQA(script), 
                            discussScriptString(english, true), 
                            true, 
                            discussScriptTooltip(english)))
                    + HELP_BUTTON;
            
            exerciseSection += "</div></P><BR/>";
        }
        
        /* ***** EO Exercise, if any. ****** */

        tooltips.add(english
                ? "Main view of the PDF output"
                : "Hauptansicht der PDF-Grafikausgabe");
        anchorNamePairs.put("#" + outputAnchor, outputTitle);
        sorting.add("#" + outputAnchor);
        align.add("RM");

        tooltips.add(english
                ? "Script view: type a script or an ID and click Draw!"
                : "Skripteingabe: gib ein Skript oder eine ID ein und klicke auf Draw!");
        anchorNamePairs.put("#" + scriptAnchor, scriptTitle);
        sorting.add("#" + scriptAnchor);
        align.add("RM");

        tooltips.add(english
                ? "Conversion methods to transform a script into another script"
                : "Konversionsmethoden zum Umformen eines Skripts in ein anderes Skript");
        anchorNamePairs.put("#" + conversionMethodsAnchor, conversionMethodsTitle);
        sorting.add("#" + conversionMethodsAnchor);
        align.add("RM");

        tooltips.add(english
                ? "Lists of examples for each available script type"
                : "Sammlung von Beispielen für jeden der verfügbaren Skripttypen");
        anchorNamePairs.put("#Examples", exampleTitle);
        sorting.add("#Examples");
        align.add("RM");
        
        tooltips.add(english
                ? VFPVariables.BLITZUMFRAGE_NOTICE
                : VFPVariables.BLITZUMFRAGE_NOTICE_G);
        anchorNamePairs.put(
                VFPVariables.SURVEY_LINK, 
                english ? VFPVariables.BLITZUMFRAGE_NAME : VFPVariables.BLITZUMFRAGE_NAME_G);
        sorting.add(VFPVariables.SURVEY_LINK);
        align.add("RBE");

        tooltips.add(english
                ? VFPVariables.PROG_NAME_XWIZZ + " help page with explanations for each script type"
                : "Hilfeseite zum " + VFPVariables.PROG_NAME_XWIZZ + " mit Erkl�rungen zu jedem Skripttyp");
        anchorNamePairs.put(VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER + "?help", english ? "Help" : "Hilfe");
        sorting.add(VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER + "?help");
        align.add("RBE");
        
        tooltips.add(english
                ? "Legal notice regarding the " + VFPVariables.PROG_NAME_XWIZZ + " web pages and sources"
                : "Rechtliche Hinweise zu den " + VFPVariables.PROG_NAME_XWIZZ + "-Webseiten und -Quellen");
        anchorNamePairs.put(VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER + "?impressum", english ? "Legal notice" : "Impressum");
        sorting.add(VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER + "?impressum");
        align.add("RBE");

        String navBar = this.buildNavigationFrame(
                anchorNamePairs, 
                english, 
                sorting, 
                align,
                english ? "Contents" : "Inhalt",
                tooltips);
        
        // Debug mode.
        String unstableRepsNote = WebLink.getPossiblyUnstableReps().isEmpty() ? "" : "<BR/>"
                + "The following representables might be unstable: " 
                + WebLink.getPossiblyUnstableReps();
        
        String debugMode = WebLink.isDebugMode() 
                ?     "<center>"
                    + HelpTexts.par(
                        "<font color=\"red\">"
                        + "** DEBUG MODE **"
                        + unstableRepsNote
                        + "</font>")
                    + "</center>" 
                : "";
        
        String[] filters = getFilters(exercise);

        // Combine it all.
        HashMap<String, Integer> conversionMethodClickCounts = null;
        
        // CLick counts temporarily deactivated:
//        try {
//            conversionMethodClickCounts = SQLQueries.getConversionMethodClickCounts(r);            
//        } catch (Exception e) {
//        }
        
        String linkToWorkingDir = "http://www.xwizard.de:8080/workingDir/";
        String linkToFile = linkToWorkingDir + WebLink.fileName(RepresentableDefault.THIS_NAME) + ".pdf";
        String downloadLinks = (english ? "PDF download(s): " : "PDF-Download(s): ")
                + HelpTexts.link(
                        linkToFile, 
                        english ? "Main document" : "Hauptdokument", 
                        true, 
                        english ? "Download the main PDF document" : "Lade Hauptobjekt als PDF herunter");
        
        HashMap<String, String> preprocs = new HashMap<>();
        RepresentableDefault.getAlltimePreprocessors().values().forEach(m -> preprocs.putAll(m));
        
        String tempString = "</BR>" + " " + (english ? "Objects" : "Objekte") + " [";

        boolean containsAny = false;
        LinkedList<String> keySet = new LinkedList<>(preprocs.keySet());
        Collections.sort(keySet);
        
        for (String varname : keySet) {
            String filename = WebLink.fileName(varname) + ".pdf";
            
            if (!RepresentableDefault.HIDDEN_PREPROCESSORS.contains(varname)
                    && new File(WebLink.getWORKING_DIRECTORY() + "/" + filename).exists()) {
                containsAny = true;
                linkToFile = linkToWorkingDir + filename;
                tempString += " " + HelpTexts.link(
                        linkToFile, 
                        varname, 
                        true, 
                        (english ? "Download sub-pdf given by variable " : "Lade untergeordnete PDF herunter aus Variable ") + varname);
            }
        }
        tempString += "]";
        
        if (containsAny) {
            downloadLinks += tempString;
        }

        tempString = "</BR>" + " " + (english ? "Pages" : "Seiten") + " [";
        containsAny = false;
        boolean moreThanOne = false;
        LinkedList<String> keySet2 = new LinkedList<>(LaTeXPDF.getNamesOfPDFPages());
        Collections.sort(keySet2);
        for (String filename : keySet2) {
            if (containsAny) {
                moreThanOne = true;
            }
            containsAny = true;
            linkToFile = linkToWorkingDir + WebLink.fileName(filename) + ".pdf";
            tempString += " " + HelpTexts.link(
                    linkToFile, 
                    filename, 
                    true, 
                    (english ? "Download sub-pdf given by page " : "Lade untergeordnete PDF herunter als Seite ") + filename);
        }
        tempString += "]";
        
        if (moreThanOne) {
            downloadLinks += tempString;
        }

        if (errorOccurred) {
            downloadLinks = "";
        }

        if (isScriptRetrievedFromCache(cookieUserName)) {
            downloadLinks = english ? "No PDF files created due to cached mode. Click DRAW! to create PDFs." : "Keine PDFs im Cache-Modus erzeugt. Klicke auf DRAW! um PDFs zu erstellen.";
            downloadLinks = "<div style=\"color: red\">" + downloadLinks + "</div>";
        }
        
        String codeAndDraw = ""
            + explanationLB
            + "\r\n<DIV  id=\"codeanddraw\" class=\"wrapper\">" 
            + "<DIV id=\"drawbox\">\r\n"
            + "<DIV class=\"background\">\r\n"
            + convertedString
            + "<DIV style=\"text-align: center;\">" + downloadLinks + "</DIV>\r\n"
            + "</DIV>\r\n"
            + "</DIV>\r\n"
            + lastClickedHTML
            + mainScriptArea(english, script, exercise, true, scriptAnchor)
            + WebLink.getDynamicMethodButtonsHTMLString(
                    r, 
                    escapeHtml4(script),
                    english, 
                    WebLink.availablePDFTypes,
                    filters[0],
                    filters[1],
                    filters[2],
                    conversionMethodClickCounts)
            + "</DIV>";
        
        codeAndDraw = WebLink.sectionHTMLString(
                exerciseSection + codeAndDraw, 
                outputAnchor, 
                outputTitle + " '" + exampleButtonDisplayName + "'", 
                2,
                "outputwrap",
                true);
        
        return (comingFromDoGet ? HTML_HEADER_PLAIN : HTML_HEADER_JUMP_TO_OUTPUT)
                + navBar
                + debugMode
                + codeAndDraw 
                + "<DIV>"
                + getExampleButtonsHTMLString(r, cookieUserName, exampleTitle 
                        + (english 
                                ? "" 
                                : ""))
//                + WebLink.sectionHTMLString(
//                "\n<DIV style=\"line-height: 140%;\">" + welcomeMessage + "</DIV>", 
//                  "main", 
//                  introTitle,  // + (english ? " to " : " zum ") + HelpTexts.XWIZZ_HTML + "", 
//                  2,
//                  null)
//              + getVeryQuickHelpBoxHTMLString(r, cookieUserName, helpTitle + " '" + exampleButtonDisplayName + "'")
//              + "\n<BR/>\n"
                + "\n</DIV>"
                + (WebLink.isDebugMode() 
                        ? "<DIV style=\"white-space: pre-wrap; border-radius: 25px; background: lightgray; padding: 20px;\">" 
                            + "<strong>javascript part</strong> (shown in Debug mode only):\n\n"
                            + "<p>JS-HEADER</p>"
                            + "<div style=\"border-color: black; border-radius: 25px; background: white; padding: 20px;\">"
                            + escapeHtml4(ConvenienceMethods.replaceSpecialCharsHTML_G(JAVA_SCRIPT_HEADER))
                            + "</div>"
                            + "<p>JS-REFERENCES</p>"
                            + "<div style=\"border-color: black; border-radius: 25px; background: white; padding: 20px;\">"
                            + escapeHtml4(ConvenienceMethods.replaceSpecialCharsHTML_G(JAVA_SCRIPT_REFERENCES))
                            + "</div>"
                            + "<p>Contents of XWizard.js</p>"
                            + "<div style=\"border-color: black; border-radius: 25px; background: white; padding: 20px;\">"
                            + escapeHtml4(ConvenienceMethods.replaceSpecialCharsHTML_G(JAVA_SCRIPT_CODE))  
                            + "</div>"
                            + "<p><center>** " + retrievedFromCacheString + " **</center></p>" 
                            + "</DIV>"
                        : "")
                + HTML_FOOTER_WITH_CLOSING_WRAPPER_DIV;
    }

    public String discussScriptTooltip(boolean english) {
        return english 
                ? "Switches to the discussion forum and copies this exercise's script into the forum's question editor (if it's not too large)" 
                : ConvenienceMethods.replaceSpecialCharsHTML_G(
                        "Wechselt zum Diskussionsforum und kopiert das Aufgabenskript ins Fragen-Eingabefeld (wenn es nicht zu lang ist)");
    }

    private String getLastClickedButtonHTML(
            RepresentableAsPDF r, 
            String lastMethodClickedName,
            boolean isEnglish,
            String script) {
        if (r != null) {
            HashMap<String, MethodWrapper> dynamicMethods = r.getDynamicMethods();
            for (String methodName : dynamicMethods.keySet()) {
                if (StaticMethods.similarity(methodName, lastMethodClickedName) > 0.9) {
                    MethodWrapper mw = dynamicMethods.get(methodName);
                    if (mw.isMethodButtonVisible() 
//                            && mw.isMethodButtonEnabled() 
                            && mw.isReturnValueScript()
                            && mw.isUseInWebProductiveMode()) {
                        return "<center>"
                                + WebLink.singleButtonHTML(
                                        isEnglish,
                                        mw, 
                                        false, 
                                        mw.getBckgnd(), 
                                        "&#8635;&nbsp;&nbsp;",
                                        escapeHtml4(script))
                                + "</center>";
                    }
                }
            }
        }
        
        return "";
    }

    private String[] getFilters(Exercise exercise) {
        return exercise == null ? new String[] {".*", ".*", ".*"} : exercise.getFilters();
    }

    private void createLanguageNavEntry(boolean english, HashMap<String, String> anchorNamePairs,
            LinkedList<String> sorting, LinkedList<String> align, String linkToOtherLanguage,
            LinkedList<String> tooltips) {
        tooltips.add(english 
                ? "Schalte die " + VFPVariables.PROG_NAME_XWIZZ + "-Sprache um auf Deutsch"
                : "Switch " + VFPVariables.PROG_NAME_XWIZZ + "'s language to English");
        anchorNamePairs.put(linkToOtherLanguage, english ? "Deutsch" : "English");
        sorting.add(linkToOtherLanguage);
        align.add("RTE");
    }

    private String createURLParToQA(String script) {
        String parameter;
        String scriptCode = script;
        
        if (script != null) {
            if (script.length() > 400 || WebLink.isEncrypted(this.getExercise(script))) {
                int id = SQLQueries.webFreeIDIfAny(ScriptConversionMethods.decryptScript(script));
                
                if (id >= 0) {
                    String plainID = "ID-" + id;
                    scriptCode = VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER 
                        + WebLink.encodeScriptAsURLPar(plainID, true);
                }
            }
        }
        
        parameter = WebLink.encodeScriptAsURLPar(
                WebLink.INTRO
                + scriptCode
                + WebLink.EXTRO,
                false);
        
        return parameter;
    }

    private String mainScriptArea(
            boolean english, 
            String script2, 
            Exercise exercise,
            boolean show,
            String anchor) {
        String script = script2;
        
        if (exercise == null) {
            exercise = this.getExercise(script);
        }
        
        String discussScript = discussScriptString(english, false);
        
        String encryptedMessage = "" 
                + (english ? "Encrypted script" : ConvenienceMethods.replaceSpecialCharsHTML_G("Skript verschl&uuml;sselt"))
                ;
        
        String parameter = this.createURLParToQA(script);
        

        String areaCode = ""
//                + "<span style=\"display: none;\"><section id=\"" + anchor + "\"></section></span>" // Achtung, Hack!
                + "\r\n<DIV id=\"Codebox\">" 
                + WebLink.plainTextArea(exercise, english, show, escapeHtml4(script))
                + "</DIV>"
                + "<DIV class=\"wrapper\">"
                + "<DIV class=\"firsttwo\">"
//                + (exercise.isEncrypted() ? encryptedMessage : "")
                + buttonHTML("", "draw", "DrawButton", "Draw!")
                + (exercise.isEncrypted() 
                        ? encryptedMessage 
                        : HelpTexts.link(VFPVariables.URL_TO_ASK_QUESTION + parameter, 
                                discussScript, true, discussScriptTooltip(english))
                )
//                + (exercise.isEncrypted() ? "</center>" : "")
                + HELP_BUTTON
                + "</DIV>"
//                + "<div class=\"second\">"
//                + "</div>"
                + "</DIV>\r\n"
                + "";

        return areaCode;
    }

    private String discussScriptString(boolean english, boolean exercise) {
        String discussScript;
        
        if (english) {
            discussScript = "Discuss this " + (exercise ? "exercise" : "script");
        } else {
            discussScript = (exercise ? "Aufgabe" : "Skript") + " diskutieren";
        }
        
        return discussScript;
    }
    
    private static String buttonHTML(String name, String value) {
        return buttonHTML("", null, name, value);
    }
    
    private static String buttonHTML(
            String className, 
            String idName, 
            String name, 
            String value) {
        return "<input class=\"" + className + " button" + "\" "
                + (idName == null ? "" : "ID=\""+ idName + "\" ")
                + "type=\"submit\" name=\"" + name + "\" "
                + "value=\"" + value + "\">";
    }
    
    public static boolean isCachedMode(String cookieUserName) {
        Boolean value = cachedMode.get(cookieUserName);
        return value != null && value;
    }
    
    public boolean isScriptRetrievedFromCache(String cookieUserName) {
        return isCachedMode(cookieUserName) && retrieveCachedSVG != null;
    }
}
