/*
 * File name:        TextbookScripts.java (package veryFastPDF)
 * Author(s):        hq0976
 * Java version:     8.0 (at generation time)
 * Generation date:  05.08.2016 (18:54:03)
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

package veryFastPDF;

import java.util.ArrayList;
import java.util.HashMap;

import eas.miscellaneous.StaticMethods;

/**
 * @author hq0976
 */
public class TextbookScripts {

    private static final HashMap<Integer, ScriptString> TEXTBOOK_SCRIPTS = new HashMap<>();
    private static final HashMap<ScriptString, Integer> TEXTBOOK_IDS = new HashMap<>();
    private static final ArrayList<ScriptString> TEXTBOOK_ORDERED_SCRIPTS = new ArrayList<>(34);
    private static final ArrayList<Integer> TEXTBOOK_ORDERED_IDS = new ArrayList<>(34);
    private static final HashMap<Integer, String> TEXTBOOK_NAMES = new HashMap<>();
    private static final HashMap<Integer, String> TEXTBOOK_EXPLANATIONS = new HashMap<>();

    private static TextbookScripts jao = new TextbookScripts(); // Just an object.
    
    private class ScriptString {
        private String scriptString = "";

        public ScriptString(String scr) {
            scriptString = scr;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result
                    + ((this.scriptString == null) ? 0 : StaticMethods.removeWhitespaces(this.scriptString).hashCode());
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
            ScriptString other = (ScriptString) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (this.scriptString == null) {
                if (other.scriptString != null)
                    return false;
            } else if (!StaticMethods.removeWhitespaces(this.scriptString).equals(StaticMethods.removeWhitespaces(other.scriptString)))
                return false;
            return true;
        }

        private TextbookScripts getOuterType() {
            return TextbookScripts.this;
        }
        
        @Override
        public String toString() {
            return this.scriptString;
        }
    }
    
    public static int getDARNum(int id) {
        return TEXTBOOK_ORDERED_IDS.indexOf(id) + 1;
    }
    
    public static int getDARNum(String script) {
        return getDARNum(getIDbyScript(script));
    }
    
    public static boolean equals(String script1, String script2) {
        return jao.new ScriptString(script1).equals(jao.new ScriptString(script2));
    }
    
    public static String getScriptById(int id) {
        return TEXTBOOK_SCRIPTS.get(id).toString();
    }
    
    public static Integer getIDbyScript(String script) {
        return TEXTBOOK_IDS.get(jao.new ScriptString(script));
    }
    
    public static void putIntoMap(int id, String script, String name, String explanation) {
        ScriptString scriptObject = jao.new ScriptString(script);
        
        if (TEXTBOOK_SCRIPTS.containsKey(id)
                || TEXTBOOK_SCRIPTS.containsValue(scriptObject)) {
            throw new RuntimeException("Textbook script already stored.");
        }
        
        TEXTBOOK_SCRIPTS.put(id, scriptObject);
        TEXTBOOK_IDS.put(scriptObject, id);
        TEXTBOOK_ORDERED_SCRIPTS.add(scriptObject);
        TEXTBOOK_ORDERED_IDS.add(id);
        TEXTBOOK_NAMES.put(id, name);
        TEXTBOOK_EXPLANATIONS.put(id, explanation);
    }

    public static boolean containsScript(String script) {
        return getIDbyScript(script) != null;
    }
    
    public static boolean containsID(int id) {
        return TEXTBOOK_IDS.containsKey(id);
    }
    
    public static String getScriptName(int id) {
        return getDARString(id) + " (" + TEXTBOOK_NAMES.get(id) + ")";
    }

    public static String getScriptNameByDarNum(int num) {
        return getScriptName(getIDbyDarNum(num));
    }

    public static Integer getIDbyDarNum(int num) {
        return TEXTBOOK_ORDERED_IDS.get(num - 1);
    }
    
    public static String getDARString(int id) {
        int num = getDARNum(id);
        return createDarStringFromNumber(num);
    }

    /**
     * 4 ==> DAR-04.
     */
    public static String createDarStringFromNumber(int num) {
        return "DAR-" + String.format("%02d", num);
    }
    
    public static String getScriptName(String script) {
        int id = getIDbyScript(script);
        return TEXTBOOK_NAMES.get(id);
    }
    
    public static String getScriptExplanation(int id) {
        return TEXTBOOK_EXPLANATIONS.get(id);
    }
    
    public static String getScriptExplanation(String script) {
        int id = getIDbyScript(script);
        return TEXTBOOK_EXPLANATIONS.get(id);
    }
    
    public static int getScriptNum() {
        return TEXTBOOK_SCRIPTS.size();
    }
    
    static {
        putIntoMap( // 01
                16356, "grammar parse(dies, ist, ein, Beispiel, zur, Bedienung, des, XWizards)--0:\r\n" + 
                        "X => Bedienung | Beispiel | XWizards | des | dies | ein | ist | zur | X, X;\r\n" + 
                        "--declarations--\r\n" + 
                        "e=#n#;\r\n" + 
                        "N=X;\r\n" + 
                        "T=Bedienung,Beispiel,XWizards,des,dies,ein,ist,zur;\r\n" + 
                        "S=X;\r\n" + 
                        "displayMode=2;\r\n" + 
                        "maxdepth=3;\r\n" + 
                        "cutNonTerminalBranches=true;\r\n" + 
                        "cutTerminalDoubleBranches=true;\r\n" + 
                        "maxLengthWords=6;\r\n" + 
                        "multiLetterSymbolsHaveIndex=false;\r\n" + 
                        "parseTreeNum=0\r\n" + 
                        "--declarations-end--", 
                            "" + VFPVariables.PROG_NAME_XWIZZ + "-Demoskript", 
                            "Das Demoskript zeigt die Funktionsweise des " + HelpTexts.XWIZZ_HTML + "s. "
                                + "Zu jedem Skript aus dem Buch gibt es eine kleine Anleitung und meist auch eine Miniaufgabe, die in wenigen Minuten gelöst werden kann. "
                                + "<BR/><b>Einfache Miniaufgaben zum Einstieg</b>: Klicke verschiedene Konversionsmethoden an und schaue, was passiert."
                                + " Lade mindestens eine PDF herunter.");
        
        putIntoMap( // 02
                3761, "turing:\r\n" + 
                        "(s0, *) => (sab, *, L);\r\n" + 
                        "(s0, a) => (s1, a, R);\r\n" + 
                        "(s0, b) => (s2, b, R);\r\n" + 
                        "(s1, *) => (sb, *, L);\r\n" + 
                        "(s1, a) => (s0, a, R);\r\n" + 
                        "(s1, b) => (s3, b, R);\r\n" + 
                        "(s2, *) => (sa, *, L);\r\n" + 
                        "(s2, a) => (s3, a, R);\r\n" + 
                        "(s2, b) => (s0, b, R);\r\n" + 
                        "(s3, a) => (s2, a, R);\r\n" + 
                        "(s3, b) => (s1, b, R);\r\n" + 
                        "(sa, a) | (sab, a) => (sf, a, N);\r\n" + 
                        "(sab, b) | (sb, b) => (sf, b, N);\r\n" + 
                        "(sab, *) => (sf, *, N);\r\n" +
                        "--declarations--\r\n" + 
                        "s0=s0;\r\n" + 
                        "F=sf;\r\n" + 
                        "blank=*;\r\n" + 
                        "inputs=aabab;\r\n" + 
                        "runStepsScript=100;\r\n" + 
                        "shortTrace=false\r\n" + 
                        "--declarations-end--",
                        "Turingmaschine für " + HelpTexts.math(HelpTexts.index("L", "mod")), 
                            "<b>Miniaufgaben</b>: Teste die verschiedenen Anzeigemöglichkeiten für Turingmaschinen. "
                            + "Der zuletzt benutzte Button erscheint immer direkt unterhalb der Darstellung. "
                            + "Versuche, den Programmablauf für das Wort " 
                            + HelpTexts.math("abaaaabbabb") 
                            + " durch Verändern der Variable " + HelpTexts.button("inputs") + " im Skript anzeigen zu lassen.");
        
        putIntoMap( // 03
                16364, "pda:\r\n" + 
                        "(s0, a, k) => (s1, k);\r\n" + 
                        "(s0, b, k) => (s2, k);\r\n" + 
                        "(s1, a, k) | (s2, b, k) | (sf1, a, k) | (sf2, b, k) => (s0, k);\r\n" + 
                        "(s1, b, k) | (s2, a, k) | (sf1, b, k) | (sf2, a, k) => (s3, k);\r\n" + 
                        "(s3, a, k) => (sf2, k);\r\n" + 
                        "(s3, b, k) => (sf1, k);\r\n" + 
                        "--declarations--\r\n" + 
                        "s0=s0;\r\n" + 
                        "F=s0,sf1,sf2;\r\n" + 
                        "kSymb=k;\r\n" + 
                        "inputs=bbbabba;\r\n" + 
                        "simSteps=0;\r\n" + 
                        "displayMode=0\r\n" + 
                        "--declarations-end--", 
                        "Erster Kellerautomat für " + HelpTexts.math(HelpTexts.index("L", "mod")), 
                        "<b>Miniaufgaben</b>: Teste die verschiedenen Anzeigemöglichkeiten für Kellerautomaten. "
                        + "Simuliere den Kellerautomaten schrittweise. "
                        + "Lasse den Programmablauf für das Wort "
                        + HelpTexts.math("abaaaabbabb") 
                        + " durch Anpassen der Variable " + HelpTexts.button("inputs") 
                        + " anzeigen.");
        
        putIntoMap( // 04
                16380, "pda:\r\n" + 
                        "(s0, a, A) => (s1, B);\r\n" + 
                        "(s0, a, E) | (s0, b, F) | (s1, a, B) | (s1, b, C)\r\n" + 
                        "=> (s0, A);\r\n" + 
                        "(s0, a, F) | (s0, b, E) | (s1, a, C) | (s1, b, B)\r\n" + 
                        "=> (s1, D);\r\n" + 
                        "(s0, b, A) => (s1, C);\r\n" + 
                        "(s0, lambda, k) => (s0, Ak);\r\n" + 
                        "(s1, a, D) => (s0, F);\r\n" + 
                        "(s1, b, D) => (s0, E);\r\n" + 
                        "--declarations--\r\n" + 
                        "s0=s0;\r\n" + 
                        "F=s0;\r\n" + 
                        "kSymb=k;\r\n" + 
                        "inputs=bbbabba;\r\n" + 
                        "simSteps=0;\r\n" + 
                        "displayMode=0\r\n" + 
                        "--declarations-end--", 
                        "Zweiter Kellerautomat für " + HelpTexts.math(HelpTexts.index("L", "mod")), 
                        "<b>Miniaufgabe</b>: Simuliere den Automaten einmal komplett durch. Lasse den Programmablauf für das Wort "
                        + HelpTexts.math("abaaaabbabb") 
                        + " durch Anpassen der Variable " + HelpTexts.button("inputs") 
                        + " anzeigen.");
        
        putIntoMap( // 05
                16377, "pda:\r\n" + 
                        "(s1, [, [) => (s1, [[);\r\n" + 
                        "(s1, lambda, k) => (s0, k);\r\n" + 
                        "(s0, [, k) => (s1, [k);\r\n" + 
                        "(s1, ], [) => (s1, lambda);\r\n" + 
                        "--declarations--\r\n" + 
                        "e=#n#;\r\n" + 
                        "s0=s0;\r\n" + 
                        "F=s0;\r\n" + 
                        "kSymb=k;\r\n" + 
                        "inputs=[][[]];\r\n" + 
                        "simSteps=0\r\n" + 
                        "--declarations-end--", 
                        "Kellerautomat für die Sprache " + HelpTexts.math(HelpTexts.index("L", "kK")) + " der korrekten Klammerausdrücke", 
                        "<b>Miniaufgabe</b>: Ändere die Variable " + HelpTexts.button("inputs") + " so, dass "
                                + "während der Rechnung mindestens vier Zeichen auf einmal "
                                + "im Keller stehen und das Wort am Ende akzeptiert wird.");
        
        putIntoMap( // 06
                16379, "pda:\r\n" + 
                        "(s0, 0, 0) => (s0, 00);\r\n" + 
                        "(s0, 0, 1) => (s0, 01);\r\n" + 
                        "(s0, 0, k) => (s0, 0k);\r\n" + 
                        "(s0, 1, 0) => (s0, 10);\r\n" + 
                        "(s0, 1, 1) => (s0, 11);\r\n" + 
                        "(s0, 1, k) => (s0, 1k);\r\n" + 
                        "(s0, M, 0) => (s1, 0);\r\n" + 
                        "(s0, M, 1) => (s1, 1);\r\n" + 
                        "(s0, M, k) | (s1, lambda, k) => (se, k);\r\n" + 
                        "(s1, 0, 0) | (s1, 1, 1) => (s1, lambda);\r\n" + 
                        "--declarations--\r\n" + 
                        "s0=s0;\r\n" + 
                        "F=se;\r\n" + 
                        "kSymb=k;\r\n" + 
                        "inputs=101101M101101;\r\n" + 
                        "simSteps=0;\r\n" + 
                        "displayMode=0\r\n" + 
                        "--declarations-end--", 
                        "Kellerautomat für die Sprache " + HelpTexts.math(HelpTexts.index("L", "pal-M")) + " der Palindrome mit Kennzeichnung der Mitte", 
                        "<b>Miniaufgabe</b>: Was passiert, wenn ein Wort ohne " + HelpTexts.math("M") + " eingegeben wird?");
        
        putIntoMap( // 07
                16416, "fsm:\r\n" + 
                        "(s0, a) | (s1b, a) | (s2b, a) => s1a;\r\n" + 
                        "(s0, b) | (s1a, b) | (s2a, b) => s1b;\r\n" + 
                        "(s1a, a) => s2a;\r\n" + 
                        "(s1b, b) => s2b;\r\n" + 
                        "(s2a, a) | (s2b, b) | (s3, a) | (s3, b) => s3;\r\n" + 
                        "--declarations--\r\n" + 
                        "e=#n#;\r\n" + 
                        "simulateToStep=-1;\r\n" + 
                        "input=null;\r\n" + 
                        "s0=s0;\r\n" + 
                        "F=s3;\r\n" + 
                        "displayMode=0;\r\n" + 
                        "showMinimizedFSM=false;\r\n" + 
                        "showDeterministicFSM=false\r\n" + 
                        "--declarations-end--", 
                        "Endlicher Automat für die Sprache " + HelpTexts.math(HelpTexts.index("L", "3e")) + " der Wörter mit drei aufeinanderfolgenden gleichen Zeichen", 
                        "<b>Miniaufgabe</b>: Klicke auf " + HelpTexts.button("Simuliere einen Schritt...")
                        + " und gib das Wort " + HelpTexts.math("abaababbb") + " ein. "
                        + "Klicke danach wiederholt auf " + HelpTexts.button("Simuliere einen Schritt") 
                        + " bis das Wort akzeptiert worden ist.");
        
        putIntoMap( // 08
                16373, "fsm:\r\n" + 
                        "(s0, a) | (s1, a) | (s4, b) => s1;\r\n" + 
                        "(s0, b) => s4;\r\n" + 
                        "(s1, b) | (s2, b) | (s4, a) => s3;\r\n" + 
                        "(s2, a) | (s3, a) | (s3, b) => s2;\r\n" + 
                        "--declarations--\r\n" + 
                        "simulateToStep=-1;\r\n" + 
                        "input=null;\r\n" + 
                        "s0=s0;\r\n" + 
                        "F=s3,s0,s1,s2;\r\n" + 
                        "displayMode=1;\r\n" + 
                        "showMinimizedFSM=true;\r\n" + 
                        "showDeterministicFSM=true\r\n" + 
                        "--declarations-end--", 
                        "Minimierung des endlichen Automaten " + HelpTexts.math(HelpTexts.index("A", "einfach")) + "", 
                        "<b>Miniaufgabe</b>: Simuliere beide dargestellte Automaten (" + HelpTexts.math(HelpTexts.index("A", "einfach")) 
                        + " und die minimierte Version gleichzeitig) auf dem Wort " + HelpTexts.math("bbaa") + ".");
        
        putIntoMap( // 09
                3762, "turing:\r\n" + 
                        "(s0, a) => (s2, a, R) | (s3, a, R);\r\n" + 
                        "(s0, b) => (s1, b, R) | (s4, b, R);\r\n" + 
                        "(s1, a) => (s2, a, R);\r\n" + 
                        "(s1, b) => (s1, b, R);\r\n" + 
                        "(s2, a) => (s1, a, R) | (s5, a, R);\r\n" + 
                        "(s2, b) => (s2, b, R);\r\n" + 
                        "(s3, a) => (s3, a, R);\r\n" + 
                        "(s3, b) => (s4, b, R);\r\n" + 
                        "(s4, a) => (s4, a, R);\r\n" + 
                        "(s4, b) => (s3, b, R) | (s5, b, R);\r\n" + 
                        "(s5, *) => (sf, *, N);\r\n" + 
                        "--declarations--\r\n" + 
                        "s0=s0;\r\n" + 
                        "F=sf,s0;\r\n" + 
                        "blank=*;\r\n" + 
                        "inputs=aabab\r\n" + 
                        "--declarations-end--", 
                        "Nichtdeterministische Turingmaschine für die Sprache " + HelpTexts.math(HelpTexts.index("L", "mod")) + "", 
                         "<b>Miniaufgabe</b>: Lasse die Turingmaschine auf dem leeren Wort " + HelpTexts.math("\\lambda") + " laufen. "
                         + "Dafür muss die Variable " + HelpTexts.button("inputs") + " auf * gesetzt werden.");
        
        putIntoMap( // 10
                16456, "pda:\r\n" + 
                        "(s0, 0, 0) => (s0, 00);\r\n" + 
                        "(s0, 0, 1) => (s0, 01);\r\n" + 
                        "(s0, 0, k) => (s0, 0k);\r\n" + 
                        "(s0, 1, 0) => (s0, 10);\r\n" + 
                        "(s0, 1, 1) => (s0, 11);\r\n" + 
                        "(s0, 1, k) => (s0, 1k);\r\n" + 
                        "(s0, lambda, 0) => (s1, lambda) | (s1, 0);\r\n" + 
                        "(s0, lambda, 1) => (s1, lambda) | (s1, 1);\r\n" + 
                        "(s0, lambda, k) => (s1, k);\r\n" + 
                        "(s1, 0, 0) | (s1, 1, 1) => (s1, lambda);\r\n" + 
                        "(s1, lambda, k) => (se, k);\r\n" + 
                        "--declarations--\r\n" + 
                        "s0=s0;\r\n" + 
                        "F=se;\r\n" + 
                        "kSymb=k;\r\n" + 
                        "inputs=1001;\r\n" + 
                        "simSteps=-1;\r\n" + 
                        "maxNondetCalcDepth=18;\r\n" + 
                        "displayMode=0\r\n" + 
                        "--declarations-end--", 
                        "Nichtdeterministischer Kellerautomat für die Sprache " + HelpTexts.math(HelpTexts.index("L", "pal")) + " der Palindrome ohne Kennzeichnung der Mitte", 
                        "<b>Miniaufgabe</b>: Was passiert, wenn das leere Wort " + HelpTexts.math("\\lambda") + " eingegeben wird? "
                        + "(Dafür muss der Wert der Variable " + HelpTexts.button("inputs") + " einfach leergelassen werden: 'inputs=;')");
        
        putIntoMap( // 11
                16463, "fsm:\r\n" + 
                        "(s0, a) => s3;\r\n" + 
                        "(s0, b) => s0 | s6;\r\n" + 
                        "(s1, a) | (s4, a) => s5;\r\n" + 
                        "(s1, b) => s5 | s2 | s0;\r\n" + 
                        "(s2, a) => s1;\r\n" + 
                        "(s2, b) => s2 | s4;\r\n" + 
                        "(s3, a) => s4 | s1 | s6;\r\n" + 
                        "(s4, b) => s4 | s1;\r\n" + 
                        "(s5, a) => s6 | s4;\r\n" + 
                        "(s5, b) => s6;\r\n" + 
                        "(s6, a) | (s6, b) => s4;\r\n" + 
                        "--declarations--\r\n" + 
                        "simulateToStep=0;\r\n" + 
                        "input=babaabbb;\r\n" + 
                        "s0=s0;\r\n" + 
                        "F=s0,s2;\r\n" + 
                        "displayMode=0;\r\n" + 
                        "showMinimizedFSM=false;\r\n" + 
                        "showDeterministicFSM=false\r\n" + 
                        "--declarations-end--", 
                        "Irgendein nichtdeterministischer endlicher Automat", 
                        "Simuliere den endlichen Automaten einmal komplett durch und "
                        + "beobachte, wie sich der Nichtdeterminismus deterministisch "
                        + "ausprägt.");
        
        putIntoMap( // 12
                3763, "fsm:\r\n" + 
                        "(s0, a) => s2 | s3;\r\n" + 
                        "(s0, b) => s1 | s4;\r\n" + 
                        "(s1, a) | (s2, b) => s2;\r\n" + 
                        "(s1, b) => s1;\r\n" + 
                        "(s2, a) => s1 | s5;\r\n" + 
                        "(s3, a) => s3;\r\n" + 
                        "(s3, b) | (s4, a) => s4;\r\n" + 
                        "(s4, b) => s3 | s5;\r\n" + 
                        "--declarations--\r\n" + 
                        "s0=s0;\r\n" + 
                        "F=s5\r\n" + 
                        "--declarations-end--", 
                        "Nichtdeterministischer endlicher Automat für die Sprache " + HelpTexts.math(HelpTexts.index("L", "mod")), 
                        "<b>Miniaufgabe</b>: Lasse die nichtdeterministische und eine deterministische "
                        + "Version des endlichen Automaten gleichzeitig anzeigen; simuliere beide "
                        + "gleichzeitig auf dem Wort " + HelpTexts.math("abaabbaaa"));
        
        putIntoMap( // 13
                3764, "fsm:\r\n" + 
                        "(s0, a) | (s4, a) => s2;\r\n" + 
                        "(s0, b) | (s4, b) => s1;\r\n" + 
                        "(s1, a) | (s2, b) | (s5, a) | (s6, b) => s3;\r\n" + 
                        "(s1, b) | (s2, a) | (s5, b) | (s6, a) => s4;\r\n" + 
                        "(s3, a) => s5;\r\n" + 
                        "(s3, b) => s6;\r\n" + 
                        "--declarations--\r\n" + 
                        "s0=s0;\r\n" + 
                        "F=s4,s5,s6\r\n" + 
                        "--declarations-end--", 
                        "Deterministischer endlicher Automat für die Sprache " + HelpTexts.math(HelpTexts.index("L", "mod")), 
                        "<b>Miniaufgabe</b>: Simuliere den endlichen Automaten auf dem Wort " 
                                + HelpTexts.math("aabab") + ".");
        
        putIntoMap( // 14
                3765, "grammar:\r\n" + 
                        "Satz => NP, VP;\r\n" + 
                        "NP => Aachen;\r\n" + 
                        "RV => aalt | aalte;\r\n" + 
                        "VP => RV, sich;\r\n" + 
                        "--declarations--\r\n" + 
                        "N=Satz,NP,VP,RV;\r\n" + 
                        "T=Aachen,aalt,aalte,sich;\r\n" + 
                        "S=Satz;\r\n" + 
                        "multiLetterSymbolsHaveIndex=false;\r\n" + 
                        "maxdepth=5;\r\n" + 
                        "cutNonTerminalBranches=false;\r\n" + 
                        "cutTerminalDoubleBranches=false;\r\n" + 
                        "maxLengthWords=100\r\n" + 
                        "--declarations-end--", 
                        "Grammatik für einfache deutsche Sätze",
                        "Die Darstellung zeigt einen vollständigen Verzweigungsbaum der ableitbaren Wörter. "
                        + "<b>Miniaufgabe</b>: Entferne die mehrfach abgeleiteten terminalen Wörter aus dem Baum.");
        
        putIntoMap( // 15
                3766, "grammar parse(er, glaubt, dass, Fritz, denkt, dass, Peter, luegt)--0:\r\n" + 
                        "Satz => NP, VP | NP, VPD;\r\n" + 
                        "DS => dass, Satz;\r\n" + 
                        "NP => er | Fritz | Peter;\r\n" + 
                        "V => luegt;\r\n" + 
                        "VD => denkt | glaubt;\r\n" + 
                        "VP => VD | V;\r\n" + 
                        "VPD => VD, DS;\r\n" + 
                        "--declarations--\r\n" + 
                        "N=Satz, VPD, VP, DS, NP, VD, V;\r\n" + 
                        "T=er, glaubt, dass, Fritz, denkt, Peter, luegt;\r\n" + 
                        "S=Satz;\r\n" + 
                        "multiLetterSymbolsHaveIndex=false\r\n" + 
                        "--declarations-end--", 
                        "Grammatik für etwas kompliziertere deutsche Sätze", 
                        "Die Darstellung zeigt einen Ableitungsbaum für das Wort " + HelpTexts.math("er glaubt dass Fritz denkt dass Peter luegt") + "."
                                + " <b>Miniaufgabe</b>: Erweitere die Grammatik, sodass Verneinungen möglich werden. Leite ab: "
                                + HelpTexts.math("er glaubt dass Fritz nicht luegt") + ".");
        
        putIntoMap( // 16
                16496, "grammar:\r\n" + 
                        "S => T, a | U, b | epsilon;\r\n" + 
                        "A => a;\r\n" + 
                        "B => b;\r\n" + 
                        "T => A, A, T | B, T | A;\r\n" + 
                        "U => B, B, U | A, U | B;\r\n" + 
                        "A, B => B, A;\r\n" + 
                        "B, A => A, B;\r\n" + 
                        "--declarations--\r\n" + 
                        "N=A,B,C,D,E,S,T,U;\r\n" + 
                        "T=a,b,c,d;\r\n" + 
                        "S=S;\r\n" + 
                        "maxdepth=7;\r\n" + 
                        "cutNonTerminalBranches=true;\r\n" + 
                        "cutTerminalDoubleBranches=true;\r\n" + 
                        "maxLengthWords=100\r\n" + 
                        "--declarations-end--", 
                        "Monotone Grammatik für die Sprache " + HelpTexts.math(HelpTexts.index("L", "mod")), 
                        "<b>Miniaufgabe</b>: Mache " + HelpTexts.math("T") + " zum Startsymbol. Welche Wörter sind jetzt noch ableitbar?");
        
        putIntoMap( // 17
                16562, "grammar:\r\n" + 
                        "A, B => C, B;\r\n" + 
                        "A, F => A, B;\r\n" + 
                        "A => a;\r\n" + 
                        "B, A => E, A;\r\n" + 
                        "B, D => B, A;\r\n" + 
                        "B => b;\r\n" + 
                        "C, B => C, D;\r\n" + 
                        "C, D => B, D;\r\n" + 
                        "E, A => E, F;\r\n" + 
                        "E, F => A, F;\r\n" + 
                        "S => T, a /*| U, b*/ | epsilon;\r\n" + 
                        "T => A, A, T | B, T | A;\r\n" + 
                        "U => B, B, U | A, U | B;\r\n" + 
                        "--declarations--\r\n" + 
                        "e=#n#;\r\n" + 
                        "N=A,B,C,D,E,F,S,T,U;\r\n" + 
                        "T=a,b,c,d;\r\n" + 
                        "S=S;\r\n" + 
                        "displayMode=0;\r\n" + 
                        "maxdepth=9;\r\n" + 
                        "cutNonTerminalBranches=true;\r\n" + 
                        "cutTerminalDoubleBranches=true;\r\n" + 
                        "maxLengthWords=100;\r\n" + 
                        "multiLetterSymbolsHaveIndex=false;\r\n" + 
                        "parseTreeNum=0\r\n" + 
                        "--declarations-end--", 
                        "Kontextsensitive Grammatik für die Sprache " + HelpTexts.math(HelpTexts.index("L", "mod")), 
                        "<b>Miniaufgaben</b>: Wie viele (terminale und nichtterminale) Wörter sind in genau vier Schritten ableitbar? "
                        + "Die Produktion " + HelpTexts.math("S => U, b") + " ist auskommentiert. "
                        + "Entferne die Kommentartags und betrachte die vollständige Grammatik.");
        
        putIntoMap( // 18
                16563, "grammar:\r\n" + 
                        "S => T, a /*| U, b*/ | epsilon;\r\n" + 
                        "T => a, T, a | b, T | T, b | a;\r\n" + 
                        "U => b, U, b | a, U | U, a | b;\r\n" + 
                        "--declarations--\r\n" + 
                        "N=S,T,U;\r\n" + 
                        "T=a,b;\r\n" + 
                        "S=S;\r\n" + 
                        "multiLetterSymbolsHaveIndex=true;\r\n" + 
                        "maxdepth=10;\r\n" + 
                        "cutNonTerminalBranches=true;\r\n" + 
                        "cutTerminalDoubleBranches=true;\r\n" + 
                        "maxLengthWords=5\r\n" + 
                        "--declarations-end--", 
                        "Kontextfreie Grammatik für die Sprache " + HelpTexts.math(HelpTexts.index("L", "mod")), 
                        "<b>Miniaufgabe</b>: Wie viele (terminale und nichtterminale) Wörter sind diesmal in genau vier Schritten ableitbar?");
        
        putIntoMap( // 19
                16727, "grammar parse(a,b,b,b,a,a,b,a,b,b)--0:\r\n" + 
                        "S => epsilon | T, a | U, b;\r\n" + 
                        "T => a | T, b | b, T | a, T, a;\r\n" + 
                        "U => b | U, a | a, U | b, U, b;\r\n" + 
                        "--declarations--\r\n" + 
                        "N=S,T,U;\r\n" + 
                        "T=a,b;\r\n" + 
                        "S=S;\r\n" + 
                        "displayMode=0;\r\n" + 
                        "maxdepth=10;\r\n" + 
                        "cutNonTerminalBranches=true;\r\n" + 
                        "cutTerminalDoubleBranches=true;\r\n" + 
                        "maxLengthWords=5;\r\n" + 
                        "multiLetterSymbolsHaveIndex=true;\r\n" + 
                        "parseTreeNum=0\r\n" + 
                        "--declarations-end--", 
                        "Ableitung eines Wortes mit der kontextfreien Grammatik für die Sprache " + HelpTexts.math(HelpTexts.index("L", "mod")), 
                        "<b>Miniaufgabe</b>: Erstellen Sie einen Ableitungsbaum für das Wort " + HelpTexts.math("aaabbbb") + ".");
        
        putIntoMap( // 20
                16778, "grammar:\r\n" + 
                        "C, B => B, C;\r\n" + 
                        "S' => S | epsilon;\r\n" + 
                        "S => a, S, B, C | a, B, C;\r\n" + 
                        "a, B => a, b;\r\n" + 
                        "b, B => b, b;\r\n" + 
                        "b, C => b, c;\r\n" + 
                        "c, C => c, c;\r\n" + 
                        "--declarations--\r\n" + 
                        "N=S,S',B,C,A;\r\n" + 
                        "T=a,b,c;\r\n" + 
                        "S=S';\r\n" + 
                        "displayMode=0;\r\n" + 
                        "maxdepth=90;\r\n" + 
                        "cutNonTerminalBranches=true;\r\n" + 
                        "cutTerminalDoubleBranches=true;\r\n" + 
                        "maxLengthWords=9;\r\n" + 
                        "multiLetterSymbolsHaveIndex=false;\r\n" + 
                        "parseTreeNum=0\r\n" + 
                        "--declarations-end--", 
                        "Verzweigungsbaum für die Sprache " + HelpTexts.math(HelpTexts.pow("a", "n") + HelpTexts.pow("b", "n") + HelpTexts.pow("c", "n")), 
                        "<b>Miniaufgabe</b>: Das Wort " + HelpTexts.math("\\lambda") + " wird in einem Schritt abgeleitet. "
                                + "Das Wort " + HelpTexts.math("abc") + " benötigt vier Schritte. Für " 
                                + HelpTexts.math("aabbcc") + " sind es schon acht und " + HelpTexts.math("aaabbbccc") + " braucht dreizehn Schritte. "
                                + "Da steckt eine Regelmäßigkeit dahinter. Teste, ob diese These stimmt, durch Ableiten bis " + HelpTexts.math("aaaabbbbcccc") + ".");
        
        putIntoMap( // 21
                16824, "fsm:\r\n" + 
                        "(S, a) => A0 | B1;\r\n" + 
                        "(S, b) => A1 | B0;\r\n" + 
                        "(A1, a) | (A0, b) => A0;\r\n" + 
                        "(A1, b) => A1;\r\n" + 
                        "(A0, a) => A1 | s5;\r\n" + 
                        "(B1, a) => B1;\r\n" + 
                        "(B1, b) | (B0, a) => B0;\r\n" + 
                        "(B0, b) => B1 | s5;\r\n" + 
                        "--declarations--\r\n" + 
                        "s0=S;\r\n" + 
                        "F=s5\r\n" + 
                        "--declarations-end--", 
                        "Nochmal ein nichtdeterministischer endlicher Automat für die Sprache " + HelpTexts.math(HelpTexts.index("L", "mod")), 
                        "<b>Miniaufgabe</b>: Lasse den endlichen Automaten in eine rechtslineare Grammatik umwandeln.");
        
        putIntoMap( // 22
                16836, "grammar:\r\n" + 
                        "A0 => a, A1 | b, A0;\r\n" + 
                        "A1 => a | a, A0 | b, A1;\r\n" + 
                        "B0 => b, B1 | a, B0;\r\n" + 
                        "B1 => b | b, B0 | a, B1;\r\n" + 
                        "S => a, A1 | b, A0 | b, B1 | a, B0 | epsilon;\r\n" + 
                        "--declarations--\r\n" + 
                        "N=S,A0,A1,B0,B1;\r\n" + 
                        "T=a,b,c;\r\n" + 
                        "S=S;\r\n" + 
                        "displayMode=0;\r\n" + 
                        "maxdepth=100;\r\n" + 
                        "cutNonTerminalBranches=true;\r\n" + 
                        "cutTerminalDoubleBranches=true;\r\n" + 
                        "maxLengthWords=4;\r\n" + 
                        "multiLetterSymbolsHaveIndex=true;\r\n" + 
                        "--declarations-end--", 
                        "Rechtslineare Grammatik für die Sprache " + HelpTexts.math(HelpTexts.index("L", "mod")), 
                        "<b>Miniaufgabe</b>: Lasse eine Ableitungs<b>folge</b> (keinen Ableitungs<B>baum</b>) für das Wort " 
                                + HelpTexts.math("abbaaba") + " anzeigen. Dafür muss der " + HelpTexts.button("displayMode") + " auf 1 gesetzt werden.");
        
        putIntoMap( // 23
                16837, "grammar parse(a, b, b, a, a, a)--0:\r\n" + 
                        "A0 => a, A1 | b, A0;\r\n" + 
                        "A1 => a | a, A0 | b, A1;\r\n" + 
                        "B0 => b, B1 | a, B0;\r\n" + 
                        "B1 => b | b, B0 | a, B1;\r\n" + 
                        "S => a, A1 | b, A0 | b, B1 | a, B0 | epsilon;\r\n" + 
                        "--declarations--\r\n" + 
                        "N=S,A0,A1,B0,B1;\r\n" + 
                        "T=a,b,c;\r\n" + 
                        "S=S;\r\n" + 
                        "displayMode=0;\r\n" + 
                        "maxdepth=100;\r\n" + 
                        "cutNonTerminalBranches=true;\r\n" + 
                        "cutTerminalDoubleBranches=true;\r\n" + 
                        "maxLengthWords=4;\r\n" + 
                        "multiLetterSymbolsHaveIndex=true;\r\n" + 
                        "--declarations-end--", 
                        "Rechtslineare Grammatik für die Sprache " + HelpTexts.math(HelpTexts.index("L", "mod")), 
                        "<b>Miniaufgabe</b> (für LeserInnen, denen die Chomsky- bzw. Greibach-Normalformen "
                                + "aus Kapitel 5 bereits bekannt sind): Warum kann die Grammatik nicht in die GNF "
                                + "umgewandelt werden? Wandle die Grammatik in die CNF um - was ändert sich am "
                                + "Ableitungsbaum?");
        
        putIntoMap( // 24
                3772, "grammar:\r\n" + 
                        "S => a,b,b,a;\r\n" + 
                        "S => a,a,a;\r\n" + 
                        "--declarations--\r\n" + 
                        "N=S;\r\n" + 
                        "T=a,b;\r\n" + 
                        "S=S;\r\n" + 
                        "multiLetterSymbolsHaveIndex=true;\r\n" + 
                        "maxdepth=5;\r\n" + 
                        "cutNonTerminalBranches=true;\r\n" + 
                        "cutTerminalDoubleBranches=true;\r\n" + 
                        "maxLengthWords=6\r\n" + 
                        "--declarations-end--", 
                        "Grammatik mit endlicher Auswahl für eine einfache Zweiwort-Sprache", 
                        "<b>Miniaufgabe</b>: Wandle die Grammatik in einen Kellerautomaten um "
                        + "und simulieren ihn auf dem Wort " + HelpTexts.math("abba") + ".");
        
        putIntoMap( // 25
                3773, "fsm:\r\n" + 
                        "(s0, a) => s11 | s21;\r\n" + 
                        "(s11, b) => s12;\r\n" + 
                        "(s12, b) => s13;\r\n" + 
                        "(s13, a) => s14;\r\n" + 
                        "(s21, a) => s22;\r\n" + 
                        "(s22, a) => s23;\r\n" + 
                        "--declarations--\r\n" + 
                        "s0=s0;\r\n" + 
                        "F=s23,s14\r\n" + 
                        "--declarations-end--", 
                        "Endlicher Automat ohne Schleifen für eine Zweiwort-Sprache", 
                        "<b>Miniaufgabe</b>: Lasse die deterministische Variante des "
                        + "Automaten anzeigen und betrachte die Unterschiede. Wie könnte man die Sprachen, die durch "
                        + "schleifenlose nichtdeterministische endliche Automaten "
                        + "bzw. durch Grammatiken mit endlicher Auswahl definiert werden können, "
                        + "durch ein deterministisches Automatenmodell charakterisieren?");
        
        putIntoMap( // 26
                7439, "fsm:\r\n" + 
                        "(s0, a) => s1;\r\n" + 
                        "(s1, a) => s2;\r\n" + 
                        "(s1, b) | (s9, b) => s10;\r\n" + 
                        "(s4, a) => s5;\r\n" + 
                        "(s4, b) | (s6, b) => s7;\r\n" + 
                        "(s7, b) | (s3, b) => s8;\r\n" + 
                        "(s8, b) | (s2, b) => s9;\r\n" + 
                        "(s5, b) => s6;\r\n" + 
                        "(s2, a) => s3;\r\n" + 
                        "(s3, a) => s4;\r\n" + 
                        "--declarations--\r\n" + 
                        "e=#n#;\r\n" + 
                        "simulateToStep=-1;\r\n" + 
                        "input=null;\r\n" + 
                        "s0=s0;\r\n" + 
                        "F=s10,s0\r\n" + 
                        "--declarations-end--", 
                        "Endlicher Automat für Wörter der Form " 
                                + HelpTexts.math(HelpTexts.pow("a", "n")) + HelpTexts.math(HelpTexts.pow("b", "n"))
                                + " mit " + HelpTexts.math("n \\in {1, 2, 3, 4, 5}"), 
                        "<b>Miniaufgabe</b>: Erweitere den Automaten, sodass zusätzlich auch "
                                + HelpTexts.math(HelpTexts.pow("a", "6")) + HelpTexts.math(HelpTexts.pow("b", "6"))
                                + " akzeptiert wird. " + HelpTexts.bold("Tipp:") + " Sie werden einen neuen Endzustand hinter "
                                + HelpTexts.math(HelpTexts.index("s", "10")) + " benötigen "
                                + "und einen weiteren Zustand zwischen "
                                + HelpTexts.math(HelpTexts.index("s", "0")) + " und "
                                + HelpTexts.math(HelpTexts.index("s", "1")) + ".");
        
        putIntoMap( // 27
                17493, "grammar parse(a,b,a,b,b,a,b)--0:\r\n" + 
                        "S => T, Ca | U, Cb;\r\n" + 
                        "T => a | Cb, T | Ca,D1 | T, Cb;\r\n" + 
                        "U => b | Ca, U | Cb,D2 | U, Ca;\r\n" + 
                        "Ca => a;\r\n" + 
                        "Cb => b;\r\n" + 
                        "D1 => T,Ca;\r\n" + 
                        "D2 => U,Cb;\r\n" + 
                        "--declarations--\r\n" + 
                        "N=Ca,Cb,D1,D2,S,T,U;\r\n" + 
                        "T=a,b;\r\n" + 
                        "S=S;\r\n" + 
                        "multiLetterSymbolsHaveIndex=true;\r\n" + 
                        "--declarations-end--", 
                        "Chomsky-Normalform", 
                        "<b>Miniaufgabe</b>: Auf wie viele verschiedene Arten kann "
                        + "das Wort " + HelpTexts.math("ababbab") + " von " + HelpTexts.math("S") + " aus abgeleitet werden?");
        
        putIntoMap( // 28
                15728, "grammar:\r\n" + 
                        "S => T, a | U, b;\r\n" + 
                        "T => a, T, a | b, T | T, b | a;\r\n" + 
                        "U => b, U, b | a, U | U, a | b;\r\n" + 
                        "--declarations--\r\n" + 
                        "N=S,T,U;\r\n" + 
                        "T=a,b;\r\n" + 
                        "S=S;\r\n" + 
                        "multiLetterSymbolsHaveIndex=true;\r\n" + 
                        "maxdepth=10;\r\n" + 
                        "cutNonTerminalBranches=true;\r\n" + 
                        "cutTerminalDoubleBranches=true;\r\n" + 
                        "maxLengthWords=5\r\n" + 
                        "--declarations-end--", "", "");
        
        putIntoMap( // 29
                15731, "grammar :\r\n" + 
                        "A(1) => a;\r\n" + 
                        "A(2) => A(4), A(7);\r\n" + 
                        "A(3) => A(1), A(6);\r\n" + 
                        "A(4) => b;\r\n" + 
                        "A(5) => A(6), A(1) | A(7), A(4);\r\n" + 
                        "A(6) => a | A(3), A(1) | A(4), A(6) | A(6), A(4);\r\n" + 
                        "A(7) => b | A(1), A(7) | A(2), A(4) | A(7), A(1);\r\n" + 
                        "--declarations--\r\n" + 
                        "e=#n#;\r\n" + 
                        "N=A(1),A(2),A(3),A(4),A(5),A(6),A(7),B(6),B(7);\r\n" + 
                        "T=a,b;\r\n" + 
                        "S=A(5);\r\n" + 
                        "displayMode=1;\r\n" + 
                        "maxdepth=3;\r\n" + 
                        "cutNonTerminalBranches=true;\r\n" + 
                        "cutTerminalDoubleBranches=true;\r\n" + 
                        "maxLengthWords=6;\r\n" + 
                        "multiLetterSymbolsHaveIndex=true;\r\n" + 
                        "parseTreeNum=0\r\n" + 
                        "--declarations-end--", "", "");
        
        putIntoMap( // 30
                15733, "grammar:\r\n" + 
                        "A(1) => a;\r\n" + 
                        "A(4) => b;\r\n" + 
                        "A(5) => A(6), A(1) | A(7), A(4);\r\n" + 
                        "A(6) => a | a, B(6) | b, A(6) | a, A(6), A(1) | b, A(6), B(6) | a, A(6), A(1), B(6);\r\n" + 
                        "A(7) => b | a, A(7) | b, B(7) | a, A(7), B(7) | b, A(7), A(4) | b, A(7), A(4), B(7);\r\n" + 
                        "B(6) => A(4) | A(4), B(6);\r\n" + 
                        "B(7) => A(1) | A(1), B(7);\r\n" + 
                        "--declarations--\r\n" + 
                        "e=#n#;\r\n" + 
                        "N=A(1),A(2),A(3),A(4),A(5),A(6),A(7),B(6),B(7);\r\n" + 
                        "T=a,b;\r\n" + 
                        "S=A(5);\r\n" + 
                        "displayMode=1;\r\n" + 
                        "maxdepth=3;\r\n" + 
                        "cutNonTerminalBranches=true;\r\n" + 
                        "cutTerminalDoubleBranches=true;\r\n" + 
                        "maxLengthWords=6;\r\n" + 
                        "multiLetterSymbolsHaveIndex=true;\r\n" + 
                        "parseTreeNum=0\r\n" + 
                        "--declarations-end--", "", "");
        
        putIntoMap( // 31
                15741, "grammar:\r\n" + 
                        "A(1) => a;\r\n" + 
                        "A(4) => b;\r\n" + 
                        "A(5) => a, A(1) | b, A(4) | a, A(7), A(4) | a, B(6), A(1) | b, B(7), A(4) | b, A(6), A(1) | a, A(6), A(1), A(1) | a, A(7), B(7), A(4) | b, A(7), A(4), A(4) | b, A(6), B(6), A(1) | a, A(6), A(1), B(6), A(1) | b, A(7), A(4), B(7), A(4);\r\n" + 
                        "A(6) => a | a, B(6) | b, A(6) | a, A(6), A(1) | b, A(6), B(6) | a, A(6), A(1), B(6);\r\n" + 
                        "A(7) => b | a, A(7) | b, B(7) | a, A(7), B(7) | b, A(7), A(4) | b, A(7), A(4), B(7);\r\n" + 
                        "B(6) => A(4) | A(4), B(6);\r\n" + 
                        "B(7) => A(1) | A(1), B(7);\r\n" + 
                        "--declarations--\r\n" + 
                        "e=#n#;\r\n" + 
                        "N=A(1),A(2),A(3),A(4),A(5),A(6),A(7),B(6),B(7);\r\n" + 
                        "T=a,b;\r\n" + 
                        "S=A(5);\r\n" + 
                        "displayMode=1;\r\n" + 
                        "maxdepth=3;\r\n" + 
                        "cutNonTerminalBranches=true;\r\n" + 
                        "cutTerminalDoubleBranches=true;\r\n" + 
                        "maxLengthWords=6;\r\n" + 
                        "multiLetterSymbolsHaveIndex=true;\r\n" + 
                        "parseTreeNum=0\r\n" + 
                        "--declarations-end--", "", "");
        
        putIntoMap( // 32
                15742, "grammar:\r\n" + 
                        "A(1) => a;\r\n" + 
                        "A(4) => b;\r\n" + 
                        "A(5) => a, A(1) | b, A(4) | a, A(7), A(4) | a, B(6), A(1) | b, B(7), A(4) | b, A(6), A(1) | a, A(6), A(1), A(1) | a, A(7), B(7), A(4) | b, A(7), A(4), A(4) | b, A(6), B(6), A(1) | a, A(6), A(1), B(6), A(1) | b, A(7), A(4), B(7), A(4);\r\n" + 
                        "A(6) => a | a, B(6) | b, A(6) | a, A(6), A(1) | b, A(6), B(6) | a, A(6), A(1), B(6);\r\n" + 
                        "A(7) => b | a, A(7) | b, B(7) | a, A(7), B(7) | b, A(7), A(4) | b, A(7), A(4), B(7);\r\n" + 
                        "B(6) => b | b, B(6);\r\n" + 
                        "B(7) => a | a, B(7);\r\n" + 
                        "--declarations--\r\n" + 
                        "e=#n#;\r\n" + 
                        "N=A(1),A(2),A(3),A(4),A(5),A(6),A(7),B(6),B(7);\r\n" + 
                        "T=a,b;\r\n" + 
                        "S=A(5);\r\n" + 
                        "displayMode=1;\r\n" + 
                        "maxdepth=3;\r\n" + 
                        "cutNonTerminalBranches=true;\r\n" + 
                        "cutTerminalDoubleBranches=true;\r\n" + 
                        "maxLengthWords=6;\r\n" + 
                        "multiLetterSymbolsHaveIndex=true;\r\n" + 
                        "parseTreeNum=0\r\n" + 
                        "--declarations-end--", "", "");
        
        putIntoMap( // 33
                17177, "grammar parse(a,b,a,b,b,a,b)--0:\r\n" + 
                        "A(1) => a;\r\n" + 
                        "A(4) => b;\r\n" + 
                        "A(5) => a, A(1) | b, A(4) | a, A(7), A(4) | a, B(6), A(1) | b, B(7), A(4) | b, A(6), A(1) | a, A(6), A(1), A(1) | a, A(7), B(7), A(4) | b, A(7), A(4), A(4) | b, A(6), B(6), A(1) | a, A(6), A(1), B(6), A(1) | b, A(7), A(4), B(7), A(4);\r\n" + 
                        "A(6) => a | a, B(6) | b, A(6) | a, A(6), A(1) | b, A(6), B(6) | a, A(6), A(1), B(6);\r\n" + 
                        "A(7) => b | a, A(7) | b, B(7) | a, A(7), B(7) | b, A(7), A(4) | b, A(7), A(4), B(7);\r\n" + 
                        "B(6) => b | b, B(6);\r\n" + 
                        "B(7) => a | a, B(7);\r\n" + 
                        "--declarations--\r\n" + 
                        "e=#n#;\r\n" + 
                        "N=A(1),A(4),A(5),A(6),A(7),B(6),B(7);\r\n" + 
                        "T=a,b;\r\n" + 
                        "S=A(5);\r\n" + 
                        "displayMode=0;\r\n" + 
                        "maxdepth=3;\r\n" + 
                        "cutNonTerminalBranches=true;\r\n" + 
                        "cutTerminalDoubleBranches=true;\r\n" + 
                        "maxLengthWords=6;\r\n" + 
                        "multiLetterSymbolsHaveIndex=true;\r\n" + 
                        "parseTreeNum=0\r\n" + 
                        "--declarations-end--", "", "");
        
        putIntoMap( // 34
                16336, "grammar:\r\n" + 
                        "S => X | XkaX, S, u, S, XkzX | XkaX, S, o, S, XkzX | S, u, S | S, o, S | n, S;\r\n" + 
                        "X => x1 | x2 | x3;\r\n" + 
                        "--declarations--\r\n" + 
                        "e=#n#;\r\n" + 
                        "N=S,X;\r\n" + 
                        "T=x1,x2,x3,u,o,n,XkaX,XkzX;\r\n" + 
                        "S=S;\r\n" + 
                        "displayMode=1;\r\n" + 
                        "multiLetterSymbolsHaveIndex=true;\r\n" + 
                        "--declarations-end--", 
                        "Grammatik für aussagenlogische Formeln", 
                        "<b>Miniaufgabe</b>: Die Formel " + HelpTexts.math(HelpTexts.index("x", "1") + " AND " + HelpTexts.index("x", "2")) + " kann bezüglich dieser Grammatik als " 
                            + HelpTexts.math("XkaX,x1,u,x2,XkzX") + " dargestell werden. Wie kann diese Formel geparst werden?");
    }
}
