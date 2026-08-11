/*
 * File name:        SessionMetaInf.java (package mainServlet)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  31.07.2015 (07:22:46)
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

package veryFastPDF.web;

/**
 * @author Lukas König
 *
 */
public class SessionMetaInf {
    public static final String SOURCE_DRAW_BUTTON = "draw-button";
    public static final String SOURCE_WELCOME = "welcome-no-source";
    public static final String SOURCE_RETURN_FROM_SUBSEQUENT_PAGE = "returned-from-subsequent-page";
    public static final String SOURCE_EXAMPLE_BUTTON = "example-button";
    public static final String SOURCE_CONVERSION_BUTTON = "conversion-button";
    public static final String SOURCE_HELP_BUTTON = "help-button";
    public static final String SOURCE_IMPRESSUM_BUTTON = "impressum-button";
    public static final String SOURCE_URL_PAR = "url";
    public static final String SOURCE_URL_PAR_FROM_ID = "url-from-id";
    public static final String SOURCE_WEB_SERVICE = "webservice";
    
    public static final String SOURCE_METHOD_DO_GET = "doGet";
    public static final String SOURCE_METHOD_DO_POST = "doPost";
    public static final String SOURCE_METHOD_NONE = "none"; // For WebService.
    
    public static final String COLUMN_NAME_SOURCE_TYPE = "sourceType";
    public static final String COLUMN_NAME_SOURCE_METHOD = "sourceHTMLMethod";
    public static final String COLUMN_NAME_SOURCE_COMPLETE_USER = "userInformation";
    public static final String COLUMN_NAME_SOURCE_BROWSER_NAME = "browserName";
    public static final String COLUMN_NAME_SOURCE_MOBILE_ACCESS = "mobileAccess";
    public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    public static final String COLUMN_NAME_DEBUG_MODE = "inDebugMode";
    public static final String COLUMN_NAME_DURATION = "calcDurationMS";
    public static final String COLUMN_NAME_COOKIE_USER_NAME = "cookieUserName";
    public static final String COLUMN_NAME_LANGUAGE = "language";
    public static final String COLUMN_NAME_SCRIPT_TYPE = "scriptType";
    public static final String COLUMN_NAME_EXERCISE = "exercise";
    public static final String COLUMN_NAME_SOLUTION = "sulution";
    public static final String COLUMN_NAME_EXERCISE_SOLVED = "exerciseSolved";
    public static final String COLUMN_NAME_ENCRYPTED = "encrypted";
    public static final String COLUMN_NAME_FROM_ID = "fromIDretrieved";
    
    public static final String STRING_IF_NOT_FROM_ID = "Regular script";
    
    private String sourceType;
    private String sourceMethod;
    
    private boolean mobileAccess;
    private String completeUserInformation;
    private String browserName;
    
    private String cookieUserName;
    
    private String language;
    private String scriptType;
    
    private boolean exercise;
    private String solution;
    private Boolean exerciseSolved;
    private boolean encrypted;
    
    private String scriptID;
    
    public String getScriptID() {
        return this.scriptID;
    }
    
    public String getSolution() {
        return this.solution;
    }
    
    public boolean isEncrypted() {
        return this.encrypted;
    }
    
    public String getScriptType() {
        return scriptType;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public String getCookieUserName() {
        return cookieUserName;
    }
    
    public String getSourceType() {
        return sourceType;
    }

    public String getSourceMethod() {
        return sourceMethod;
    }

    public int isMobileAccess() {
        return mobileAccess ? 1 : 0;
    }

    public String getCompleteUserInformation() {
        return completeUserInformation;
    }

    public String getBrowserName() {
        return browserName;
    }

    public Boolean getExerciseSolved() {
        return this.exerciseSolved;
    }

    public boolean isExercise() {
        return exercise;
    }
    
    public SessionMetaInf(
            String sourceType, 
            String sourceMethod, 
            boolean mobileAccess, 
            String completeUserInformation,
            String browserName,
            String cookieUserName,
            String language,
            String scriptType,
            boolean exercise,
            Boolean solved,
            String solution,
            boolean encrypted,
            String scriptID) {
        this.sourceType = sourceType;
        this.sourceMethod = sourceMethod;
        this.mobileAccess = mobileAccess;
        this.completeUserInformation = completeUserInformation;
        this.browserName = browserName;
        this.cookieUserName = cookieUserName;
        this.language = language;
        this.scriptType = scriptType;
        this.exercise = exercise;
        this.exerciseSolved = solved;
        this.solution = solution;
        this.encrypted = encrypted;
        this.scriptID = scriptID == null ? STRING_IF_NOT_FROM_ID : scriptID;
    }

    @Override
    public String toString() {
        return "SessionMetaInf [sourceType=" + this.sourceType
                + ", sourceMethod=" + this.sourceMethod + ", mobileAccess="
                + this.mobileAccess + ", completeUserInformation="
                + this.completeUserInformation + ", browserName="
                + this.browserName + ", cookieUserName=" + this.cookieUserName
                + ", language=" + this.language + ", scriptType="
                + this.scriptType + "]";
    }
}
