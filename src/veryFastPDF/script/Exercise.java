/*
 * File name:        Exercise.java (package veryFastPDF.script)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  04.10.2015 (15:06:23)
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

package veryFastPDF.script;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author Lukas König
 */
public class Exercise {

    public static final char SEPARATOR = ',';
    public static final char END_LITERAL = '~';
    public static final char BEGIN_LITERAL = '~';

    public static final String ENCRYPTED_PREFIX = "scrypt:";

    public static final String TITLE_NAME = "tit";
    public static final String EXPLANATION_NAME = "exp";
    public static final String SOLUTION_NAME = "sol";
    public static final String METHOD_NAME_REGEX_NAME = "met";
    public static final String CURRENT_CLASS_REGEX_NAME = "cur";
    public static final String TARGET_CLASS_REGEX_NAME = "tar";
    public static final String SOLUTION_CODE_NAME = "cod";
    public static final String SOLUTION_EXPLANATION_NAME = "sexp";
    public static final String SCRIPT_ENCRYPTED_NAME = "crypt";
    public static final String EXERCISE_ENCRYPTED_NAME = "excrypt";
    
    private String rawExerciseString;

    private String title;
    private String explanation;
    private String solution;
    private String aMethodNameRegex;
    private String bCurrentClassRegex;
    private String cTargetClassRegex;
    private String solCode;
    private String solExp;
    private boolean encrypted;
    private boolean exEncrypted;
    
    public Exercise(String rawExCode) {
        this.rawExerciseString = ScriptConversionMethods.decryptScript(rawExCode); // In case of encrypted exercise.
        
        this.aMethodNameRegex = ".*";
        this.bCurrentClassRegex = ".*";
        this.cTargetClassRegex = ".*";
        this.encrypted = false;
        this.exEncrypted = false;
        
        LinkedList<ArrayList<String>> variables = RepresentableDefault.extractNVPairs(
                this.rawExerciseString, BEGIN_LITERAL, END_LITERAL, SEPARATOR, null, null);

        variables.forEach(v -> {
            if (v.get(0).equals(TITLE_NAME)) {
                if (!v.get(1).isEmpty()) {
                    title = v.get(1);
                }
            } else if (v.get(0).equals(EXPLANATION_NAME)) {
                if (!v.get(1).isEmpty()) {
                    explanation = v.get(1);
                }
            } else if (v.get(0).equals(SOLUTION_NAME)) {
                if (!v.get(1).isEmpty()) {
                    solution = v.get(1);
                }
            } else if (v.get(0).equals(METHOD_NAME_REGEX_NAME)) {
                if (!v.get(1).isEmpty()) {
                    aMethodNameRegex = v.get(1);
                }
            } else if (v.get(0).equals(CURRENT_CLASS_REGEX_NAME)) {
                if (!v.get(1).isEmpty()) {
                    bCurrentClassRegex = v.get(1);
                }
            } else if (v.get(0).equals(TARGET_CLASS_REGEX_NAME)) {
                if (!v.get(1).isEmpty()) {
                    cTargetClassRegex = v.get(1);
                }
            } else if (v.get(0).equals(SOLUTION_CODE_NAME)) {
                if (!v.get(1).isEmpty()) {
                    solCode = v.get(1);
                }
            } else if (v.get(0).equals(SOLUTION_EXPLANATION_NAME)) {
                if (!v.get(1).isEmpty()) {
                    solExp = v.get(1);
                }
            } else if (v.get(0).equals(SCRIPT_ENCRYPTED_NAME)) {
                if (!v.get(1).isEmpty()) {
                    try {
                        encrypted = Boolean.parseBoolean(v.get(1));
                    } catch (Exception e) {
                    }
                }
            } else if (v.get(0).equals(EXERCISE_ENCRYPTED_NAME)) {
                if (!v.get(1).isEmpty()) {
                    try {
                        exEncrypted = Boolean.parseBoolean(v.get(1));
                    } catch (Exception e) {
                    }
                }
            }
        });
    }

    public boolean isExEncrypted() {
        return this.exEncrypted;
    }
    
    public boolean isEncrypted() {
        return this.encrypted;
    }

    public String getRawExerciseString() {
        return this.rawExerciseString;
    }

    public String getTitle() {
        return this.title;
    }

    public String getExplanation() {
        return this.explanation;
    }

    public String getSolution() {
        return this.solution;
    }

    public String getaMethodNameRegex() {
        return this.aMethodNameRegex;
    }

    public String getbCurrentClassRegex() {
        return this.bCurrentClassRegex;
    }

    public String getcTargetClassRegex() {
        return this.cTargetClassRegex;
    }
    
    public String getSolCode() {
        return this.solCode;
    }

    public String getSolExp() {
        return this.solExp;
    }
    
    @Override
    public String toString() {
        return "Exercise [title=" + this.title + ", explanation="
                + this.explanation + ", solution=" + this.solution
                + ", aMethodNameRegex=" + this.aMethodNameRegex
                + ", bCurrentClassRegex=" + this.bCurrentClassRegex
                + ", cTargetClassRegex=" + this.cTargetClassRegex + ", solCode="
                + this.solCode + ", solExp="
                + this.solExp + ", encrypted=" + this.encrypted + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.rawExerciseString == null) ? 0
                : this.rawExerciseString.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Exercise other = (Exercise) obj;
        if (this.rawExerciseString == null) {
            if (other.rawExerciseString != null)
                return false;
        } else if (!this.rawExerciseString.equals(other.rawExerciseString))
            return false;
        return true;
    }
    
    public String[] getFilters() {
        return new String[] {aMethodNameRegex, bCurrentClassRegex, cTargetClassRegex};
    }

    /**
     * Don't use this method unless you... although: just don't use it!
     * 
     * @param exEncrypted  Don't care about this!
     */
    protected void setExEncrypted(boolean exEncrypted) {
        this.exEncrypted = exEncrypted;
    }
}
