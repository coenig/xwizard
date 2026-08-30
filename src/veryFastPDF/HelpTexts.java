/*
 * File name:        HelpTexts.java (package veryFastPDF)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  22.07.2015 (21:26:20)
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

import java.util.HashMap;

import eas.miscellaneous.convenience.GeneralDialog;
import mainServlet.WebLink;
import veryFastPDF.algorithms.bdd.BDD;
import veryFastPDF.algorithms.circuits.LogicCircuit;
import veryFastPDF.algorithms.fsm.FSM;
import veryFastPDF.algorithms.grammars.Grammar;
import veryFastPDF.algorithms.huffman.Huffman;
import veryFastPDF.algorithms.latex.LaTeX;
import veryFastPDF.algorithms.metaProperties.MetaProperties;
import veryFastPDF.algorithms.numberRep.Numbers;
import veryFastPDF.algorithms.patTree.PatTree;
import veryFastPDF.algorithms.pda.PDA;
import veryFastPDF.algorithms.plainDOT.Graphviz;
import veryFastPDF.algorithms.regEx.RegularExpression;
import veryFastPDF.algorithms.searchTree.AbstractTreeRepresentable;
import veryFastPDF.algorithms.searchTree.redblacktree.RedBlackTree;
import veryFastPDF.algorithms.searchTree.tree234.Tree234;
import veryFastPDF.algorithms.turing.Turing;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.web.ConvenienceMethods;

/**
 * All help texts for XWizard.
 * 
 * @author Lukas König
 */
public class HelpTexts {

    /*
     * Contents:
     * 1) General texts
     * 2) Help texts QUICK/ENG
     * 3) Help texts QUICK/GER
     * 4) Help texts LONG/ENG
     * 5) Help texts LONG/GER
     * 6) Help texts introduction - ENG+GER
     */
    
    /* 
     * TODO: Grammar/Turing/PDA/EA - new display modes.
     * TODO: PDA - new stepwise simulation mode.
     * TODO: Example script for logic circuits.
     * TODO: Long Help texts for Numbers.
     * TODO: RA - show some words.
     * TODO: Help for method parameters: Numbers.
     * TODO: Tree-234 (short+long).                 MARLON
     * TODO: Red-black tree (short+long).           MARLON
     * TODO: Help for method parameters: Tree234.   MARLON
     * TODO: Help for method parameters: Red-Black. MARLON
     */

    public static final HashMap<Class<?>, String> VERY_QUICK_HELP_TEXTS = new HashMap<>();
    public static final HashMap<Class<?>, String> VERY_QUICK_HELP_TEXTS_G = new HashMap<>();
    public static final HashMap<Class<?>, String> LONG_HELP_TEXTS = new HashMap<>();
    public static final HashMap<Class<?>, String> LONG_HELP_TEXTS_G = new HashMap<>();

    /* ****************** General texts / convenience stuff. ****************** */

    public static final String URL_TO_INFO2 = "https://ilias.studium.kit.edu/goto.php?target=crs_733850";
    public static final String URL_TO_KIT = "http://www.kit.edu";
    public static final String XWIZZ_HTML = span(VFPVariables.PROG_NAME_XWIZZ, "style=\"font-size:1.1em\"");
    public static final String URL_TO_INFO2_G = URL_TO_INFO2;
    public static final String URL_TO_KIT_G = URL_TO_KIT;
    public static final String XWIZZ_HTML_G = XWIZZ_HTML;
    
    public static final String HELP_ERROR_NONE_PROVIDED = "Dear programmer, you have not provided a help text so far. "
            + "Please consider adding one to the HashMap in class 'HelpTexts' or implementing the 'requestHelpText' method of your 'RepresentableAsPDF'.";
    
    public static final String HELP_ERROR_NONE_PROVIDED_G = "Lieber Programmierer, du hast keinen deutschen Hilfetext zu deiner tollen Klasse geschrieben. "
            + "Bitte füge einen zur HashMap in der Klasse 'HelpTexts' hinzu oder implementiere die 'requestHelpText' Methode deiner 'RepresentableAsPDF'-Klasse.";
    
    private static final String USAGE = bold("Usage in " + XWIZZ_HTML + ":");
    private static final String USAGE_G = bold("Vorgehensweise im " + XWIZZ_HTML_G + ":");
    
    private static final String HOW_TO_ADD_DECLARATIONS = "(click gray conversion button " + button("FORMAT SCRIPT") + " or " + button("Add declarations to script") + " to add declarations to a script)";
    private static final String HOW_TO_ADD_DECLARATIONS_G = "(klicke auf den grauen Konversions-Button " + button("Skript formatieren") + " oder " + button("Deklarationen hinzufügen") + ", um den Deklarations-Bereich zum Skript hinzuzufügen)";

    private static class CellHTML {
        String content = "";
        String pars = "";

        public CellHTML(String content, String pars) {
            this.content = content;
            this.pars = pars;
        }
    }

    public static final String SPACE = "&nbsp;";
    private static final String ARROW_HTML = SPACE + "=>" + SPACE;
    
    private static final String COLLAPSE_RULES_EXAMPLE = script(table("",
            new CellHTML[] {new CellHTML("A", "align=\"right\""), new CellHTML(ARROW_HTML, "align=\"center\""), new CellHTML("B | C;", "align=\"left\"")},
            new CellHTML[] {new CellHTML("A | B", "align=\"right\""), new CellHTML(ARROW_HTML, "align=\"center\""), new CellHTML("C;", "align=\"left\"")},
            new CellHTML[] {new CellHTML("A | B", "align=\"right\""), new CellHTML(ARROW_HTML, "align=\"center\""), new CellHTML("C | D;", "align=\"left\"")}
            ));

    public static final String HOW_TO_COLLAPSE_RULES_QUICK = par(bold("Hint:") 
            + " Combine rules on left side, right side or both:\n"
            + COLLAPSE_RULES_EXAMPLE);

    public static final String HOW_TO_COLLAPSE_RULES_QUICK_G = par(bold("Tipp:") 
            + " Regeln können zusammengefasst werden, auf der rechten Seite, der linken oder beiden:\n"
            + COLLAPSE_RULES_EXAMPLE);

    public static final String HOW_TO_COLLAPSE_RULES = "<BR/><BR/><BR/>" + par(def("Collapsing/decollapsing rules:") + " This script type is based on " + def("rules") + " such as " + inlineScript("X => Y;") + " "
            + "Use button " + button("Format script") + " to collapse like this:" 
            + par(script("A => B;\n"
                   + "A => C;\n"
                   + "A => D;\n") 
            + " to: " + script("A => B | C | D;"))
            + par("or this:" 
            + script("A => D;\n"
                   + "B => D;\n"
                   + "C => D;\n") 
            + " to: " + script("A | B | C => D;")) 
            + par("...or even this:" 
            + script("A => C;\n"
                   + "A => D;\n"
                   + "B => C;\n"
                   + "B => D;") 
            + " to: " + script("A | B => C | D;")) 
            + par("Collapsed rules provide the same information as decollapsed ones, i.e., they are equivalent in the above sense."));

    public static final String HOW_TO_COLLAPSE_RULES_G =
            "<BR/><BR/><BR/>" + par(def("Zusammenfassen/Auseinanderziehen von Regeln:") + " Skripte dieses Typs basieren auf sogenannten " + def("Regeln") + ", etwa: " + inlineScript("X => Y;") + " "
            + "Regeln können über den Button " + button("Skript formatieren") + " (oder von Hand) folgendermaßen zusammengefasst werden:" 
            + par(script("A => B;\n"
                   + "A => C;\n"
                   + "A => D;") 
            + " wird zu: " + script("A => B | C | D;"))
            + par("...und ebenso:" 
            + script("A => D;\n"
                   + "B => D;\n"
                   + "C => D;") 
            + " wird zu: " + script("A | B | C => D;")) 
            + par("...und sogar:" 
            + script("A => C;\n"
                   + "A => D;\n"
                   + "B => C;\n"
                   + "B => D;") 
            + " wird zu: " + script("A | B => C | D;")) 
            + par("Zusammengefasste und auseinandergezogene Regeln haben denselben Informationsgehalt, sie sind also in der oben gezeigten Weise austauschbar."));
    
    public static String itemize(String... elements) {
        String s = "<UL>\n";
        
        for (String el : elements) {
            s += "<LI>" + el + "</LI>";
        }
        
        s += "</UL>";
        
        return s;
        
    }
    
    public static String title(String html) {
        return par(center(bold("" + html + "")));
    }
    
    public static String center(String bold) {
        return "<center>" + bold + "</center>";
    }

    public static String bold(String html) {
        return "<B>" + html + "</B>";
    }

    private static String italic(String html) {
        return "<I>" + html + "</I>";
    }

    public static String par(String html) {
        return "\n<P>" + html + "</P>\n";
    }
    
    public static String div(String html, String pars) {
        return "<DIV " + pars + ">" + html + "</DIV>";
    }

    public static String textArea(String html, String pars) {
        return "<TEXTAREA " + pars + ">" + html + "</TEXTAREA>";
    }

    public static String span(String html, String pars) {
        return "<SPAN " + pars + ">" + html + "</SPAN>";
    }

    public static final String CONVERSION_BUTTONS_PLACEHOLDER = "$$$---XYZ_CONV_ZYX---$$$";
    
    private static final String EXAMPLE_STRING = "Example";
    private static final String EXAMPLE_STRING_G = "Beispiel";
    
    private static String example(String exampleText) {
        return exp(exampleText, true);
    }
    
    private static String example_G(String exampleText) {
        return exp(exampleText, false);
    }
    
    private static String exp(String exampleText, boolean english) {
        int count = exampleText.length() - exampleText.replace("\n", "").length();
        String urlPar = WebLink.encodeScriptAsURLPar(exampleText, true) + "#Output";
        return CONVERSION_BUTTONS_PLACEHOLDER + par((english ? EXAMPLE_STRING : EXAMPLE_STRING_G) + ": " 
                + div(textArea(exampleText, "rows=\"" + (count + 1) + "\" cols=\"60\" disabled"), "")
                + link(VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER_RELATIVE + urlPar, "Draw!"));
    }
    
    private static String script(String script) {
        return div(script.replace("\n", "<BR/>"), 
                "style=\"display:inline-block; font-family: Courier New; border: 1px solid green; padding:15px;\"");
    }
    
    private static String inlineScript(String script) {
        return "<U>" 
                + span(script.replace(">", "&gt;").replace("<", "&lt;"), 
                        "style=\"font-family: Courier New;white-space: nowrap;\"") // border: 1px solid green;\"")
                + "</U>";
    }
    
    private static String newlineScript(String script) {
        return center(inlineScript(script));
    }
    
    public static String button(String buttName) {
        return SPACE + span(bold(SPACE + buttName.toLowerCase() + SPACE), 
                "style=\"font-style: normal; white-space: nowrap; border: 2px solid green; border-radius: 5px;\"")
                + SPACE;
    }
    
    private static String def(String defText) {
        return bold(defText);
    }
    
    public static String link(String url, String linkText) {
        return link(url, linkText, false);
    }
    
    public static String link(String urlAndText) {
        return link(urlAndText, urlAndText, false);
    }
    
    public static String link(String urlAndText, boolean openNewWindow) {
        return link(urlAndText, urlAndText, openNewWindow);
    }

    public static String link(String url, String linkText, boolean openNewWindow) {
        return link(url, linkText, openNewWindow, "");
    }

    public static String link(String url, String linkText, boolean openNewWindow, String tooltip) {
        String newWindow1 = openNewWindow ? " external" : " internal";
        String newWindow2 = openNewWindow ? " target=\"_blank\"" : "";
        return "<a style=\"white-space: nowrap;\" title=\"" + tooltip
                + "\" class=\"simpleLink" 
                + newWindow1 
                + "\" href=\"" + url + "\""
                + newWindow2 + ">&rarr;&nbsp;" + linkText + "</a>";
    }

    @SuppressWarnings("unused")
    private static String table(String pars, String[]... rows) {
        CellHTML[][] newRows = new CellHTML[rows.length][rows[0].length];
        
        for (int i = 0; i < rows.length; i++) {
            String[] row = rows[i];
            CellHTML[] newRow = new CellHTML[row.length];
            for (int j = 0; j < row.length; j++) {
                newRow[j] = new CellHTML(row[j], "");
            }
            newRows[i] = newRow;
        }
        
        return table(pars, newRows);
    }

    private static String table(String pars, CellHTML[]... rows) {
        String s = "<TABLE " + pars + ">";
        
        for (CellHTML[] row : rows) {
            s += "<TR>";
            for (CellHTML cell : row) {
                s += "<TD " + cell.pars + ">" + cell.content + "</TD>";
            }
            s += "</TR>";
        }
        
        s += "</TABLE>";
        return s;
    }
    
    private static String font(String html, String fontName) {
        return "<font face=\"" + fontName + "\">" + html + "</font>";
    }
    
    public static String pow(String string, String string2) {
        return string + "<SUP>" + string2 + "</SUP>";
    }
    
    public static String index(String string, String string2) {
        return string + "<SUB>" + string2 + "</SUB>";
    }

    public static String math(String num) {
        return noLineBreak(font(italic(ConvenienceMethods.replaceMathHTML(num)), "Palatino Linotype"));
    }
    
    public static String noLineBreak(String html) {
        return span(html, "style=\"white-space: nowrap\"");
    }
    
    public static final String INFO_II_MODE = "<H3>Info II mode</H3>" + par("You are using " + XWIZZ_HTML + " in Info II mode. This means that "
            + "non-Info II script types are hidden. If you want to switch to standard mode, click here: "
            + link(VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER_RELATIVE + "?hide&help", "standard mode") + ". "
            + "To reactivate Info II mode, add the URL parameter " + inlineScript("hide=" 
            + ConvenienceMethods.INFO_II_MODE_NAME + "") + " to your " + XWIZZ_HTML + " URL, "
            + "like " + link(VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER_RELATIVE + "?hide=" 
            + ConvenienceMethods.INFO_II_MODE_NAME + "", "this") + ", "
            + "or navigate to " + XWIZZ_HTML + " from any of the Info II pages or links.");

    public static final String INFO_II_MODE_G = "<H3>Info-II-Modus</H3>" + par(XWIZZ_HTML_G + " läuft derzeit im Info-II-Modus. Das heißt, "
            + "dass nur Skript-Typen, die zu Info II gehören, angezeigt werden. Um in den allgemeinen Modus zu gelangen, klicke hier: "
            + link(VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER_RELATIVE + "?hide&help", "Allgemeiner Modus") + ". "
            + "Um zurück in den Info-II-Modus zu gelangen, füge den URL-Parameter " + inlineScript("hide=" 
            + ConvenienceMethods.INFO_II_MODE_NAME + "") + " zur " + XWIZZ_HTML + "-URL "
            + "hinzu, " + link(VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER_RELATIVE + "?hide=" 
            + ConvenienceMethods.INFO_II_MODE_NAME + "", "etwa so") + ", oder klicke auf irgendeinen der " + XWIZZ_HTML + "-Links, der über die Info-II-Seiten und Aufgabenblätter "
            + "angeboten wird.");

    /* ****************** Help texts QUICK/ENG. ****************** */
    
    public static final String QUICK_HELP_FSM = ""
            + par(title("Finite-State Machine")
                    + "Define transition 'from " + inlineScript("s0") + " to " + inlineScript("s1") + " reading input symbol " + inlineScript("a") + "' as: "
                    + inlineScript("(s0, a) => s1;"))
            + par("Define initial state " + inlineScript("s0") + ", final states " + inlineScript("F") + " and " + inlineScript("input") + " word in declarations.");
    
    public static final String QUICK_HELP_PDA = ""
            + par(title("Pushdown Automaton")
                    + "Define transition 'from " + inlineScript("s0") + " reading input symbol " 
                    + inlineScript("a") + " and stack symbol " + inlineScript("k") + " to " + inlineScript("s1") 
                    + " putting " + inlineScript("ak") + " onto the stack' as: " 
                    + inlineScript("(s0, a, k) => (s1, ak);"))
            + par("or 'from " + inlineScript("s0") + " reading nothing...' as: "
                    + inlineScript("(s0, lambda, k) => (s1, abck);"))  
            + par("Define initial state " + inlineScript("s0") + ", final states " + inlineScript("F") 
                    + ", stack symbol " + inlineScript("kSymb") + " and " +  inlineScript("input") + " word(s) in declarations.");
    
    public static final String QUICK_HELP_TURING = ""
            + par(title("Turing machine")
                    + "Define transitions 'from " + inlineScript("s0") + " reading input symbol " 
                    + inlineScript("*") + " to " + inlineScript("s1") + ", writing " + inlineScript("a") 
                    + " and shifting to the left' as: " 
            + inlineScript("(s0, *) => (s1, a, L);"))
            + par("Define initial state " + inlineScript("s0") + ", final states " + inlineScript("F") 
                    + ", " + inlineScript("blank") + " tape symbol and " + inlineScript("input") + " word(s) in declarations.");
    
    public static final String QUICK_HELP_GRAMMAR = ""
            + par("Grammars can have two modes (grammar tree or parse tree).")
            + par(title("Grammar tree")
                + "Define a production rule as:\n"
                + inlineScript("S => a, S, b;")
                + " or "
                + inlineScript("S => epsilon;")
                + "\n"
                + "Define " + inlineScript("N") + " (nonterminals), " + inlineScript("T") 
                + " (terminals) and " + inlineScript("S") + " (start symbol) in declarations. "
                + "Each branch of the grammar tree shows the derivation sequence of a terminal word by the grammar.")
            + par(title("Parse tree (context-free Grammars)")
                + "You can create a parse tree by clicking " 
                    + button("Parse single word")
                    + " or adding " + inlineScript("parse(w, o, r, d)--0") + " before the colon in the beginning. "
                    + "A parse tree shows for a single word how it can be derived by the grammar. "
                    + "Toggle parse trees by clicking the " + button("Toggle...") + " button.\n")
            + par("Note that the empty word is called " + inlineScript("epsilon") + ", not lambda, in grammars.");
    
    public static final String BDD_QUICK_HELP_PLAIN 
        = "Just write a series of " + inlineScript("0") + "s and " + inlineScript("1") 
            + "s in the order as given in a standard truth table of the desired function to define the BDD.\r\n"
            + "If the length is not " + math(pow("2", "n")) + " for some " + math("n") + ", " + inlineScript("0") 
            + "s are filled in at the end.";
    
    public static final String QUICK_HELP_BDD = ""
            + par(title("Binary decision diagram")
                    + BDD_QUICK_HELP_PLAIN.replace("\r\n", " "))
            + par("Give the variable names in the beginning as " + inlineScript("a,b,c,...") + "");
    
    public static final String HUFFMAN_QUICK_HELP_PLAIN = "Write an arbitrary text as a base for the huffman tree. "
            + "Note that white spaces count as symbols, too.";
    
    public static final String HUFFMAN_QUICK_HELP_PLAIN_G = "Gib einen beliebigen Text ein, der als Basis für die Berechnung des Huffman-Baums genommen werden soll. "
            + "Zu beachten ist, dass Leerzeichen und Zeilenumbrüche auch gezählt werden.";
    
    public static final String QUICK_HELP_HUFFMAN = ""
            + par(title("Huffman tree")
                    + HUFFMAN_QUICK_HELP_PLAIN);
    
    public static final String QUICK_HELP_PLAINDOT = ""
            + par(title("Plain Graphviz DOT code")
                    + "This is raw graphviz code. It can be generated from all other graphviz-based scripts by clicking on " + button("Plain Generator Code") + ". "
                    + "It can be modified as desired according to the documentation given in "
                    + link("http://www.graphviz.org/Documentation.php"))
            + par("Note that, so far, only the 'dot' processor can be used.");

    public static final String QUICK_HELP_CALC = ""
            + par(title("Calculator")
                    + "Type an arithmetic expression using the usual syntax, such as: "
                    + inlineScript("(3+4)^2")
                    + ", meaning: " + math(pow("(3+4)", "2"))
                    + ". The output is the result of the expression. Using " + inlineScript("x") 
                    + " and/or " + inlineScript("y") 
                    + ", such as: " + inlineScript("x^2+2^y") + ", meaning " + math(pow("x", "2") + "+" + pow("2", "y")) 
                    + ", will produce a table of all results in a certain range of these variables. "
                    + "Define this range in the declarations area.");

    public static final String QUICK_HELP_LATEX = ""
            + par(title("Latex processor")
                    + "Type an arbitrary (complete) Latex document and hit " + button("Draw!") + " to compile it. "
                    + "Cf. " + link("https://en.wikipedia.org/wiki/LaTeXCode", "Wikipedia") + " to get started with Latex.")
            + par("If " + inlineScript("formulaMode=true") + " is set in the declarations, assume being in "
                    + "Latex math mode, and avoid all the stuff around it."
            + par("Latex code can be generated from all Latex-based scripts by clicking the "
                    + button("Plain Generator Code") + " button."));

    public static final String SEARCHTREE_BASE = "Type a sequence of integers separated by spaces."
            + "The order of these elements written from left to right defines their insertion order in the tree.";

    public static final String QUICK_HELP_REDBLACKTREE = ""
            + par(title("Red-black tree")
                    + SEARCHTREE_BASE);

    public static final String QUICK_HELP_TREE234 = ""
            + par(title("2-3-4 tree")
                    + SEARCHTREE_BASE);

    public static final String QUICK_HELP_PATTREE = ""
            + par(title("Pat tree")
                    + "Type a text as a basis for a pat tree. White spaces count as symbols, however, line breaks are not permitted.");

    private static final String REG_EX_BASIC_HELP = "Type a regular expression which may be abbreviated in the usual ways. "
            + "All lowercase letters (" + inlineScript("a, b, c, ...") + ") and numbers (" + inlineScript("0, 1, 2, ...") + ") "
                    + "are in the terminal alphabet. Union is given by "
            + inlineScript("+") + ", concatenation by "
            + inlineScript(".") + " (but omissible), Kleene's star by "
            + inlineScript("*") + ". "
            + inlineScript("O") + " (the uppercase letter, not the number) is the empty set " + math("\\emptyset") + ", "
            + inlineScript("O*") + " is (accordingly) the empty word " + math("\\lambda") + ".";

    private static final String REG_EX_BASIC_HELP_G = "Die Skripteingabe ist ein regulärer Ausdruck, der in der üblichen Weise abgekürzt werden darf. "
            + "Alle Kleinbuchstaben (" + inlineScript("a, b, c, ...") + ") und Ziffern (" + inlineScript("0, 1, 2, ...") 
                + ") sind Teil des Terminalalphabets. Vereinigung ist kodiert als "
            + inlineScript("+") + ", Konkatenation als "
            + inlineScript(".") + " (kann weggelassen werden), die Kleene'sche Stern-Operation als "
            + inlineScript("*") + ". "
            + inlineScript("O") + " (der Großbuchstabe, nicht die Zahl) ist die leere Menge " + math("\\emptyset") + ", "
            + inlineScript("O*") + " (entsprechend) das leere Wort " + math("\\lambda") + ".";

    private static final String REG_EX_HELP_SIMPLIFY_G = "Die Ausgabe wird automatisch durch einige Standard-Operationen vereinfacht. "
    + "Um auch das Skript zu vereinfachen, klicke auf " + button("Vereinfache (ein bisschen)") + ".";

    private static final String REG_EX_HELP_SIMPLIFY = "The output is automatically simplified in some basic ways. "
    + "To simplify the script, click " + button("Simplify (a little)") + ".";

    private static final String REG_EX_EXAMPLE_1 = "(((((a+b).b)*.c).d)+O)";
    private static final String REG_EX_EXAMPLE_2 = "((a+b)b)*cd";

    public static final String QUICK_HELP_RegEx = ""
            + par(title("Regular expression")
                    + REG_EX_BASIC_HELP
                    + par("Example: " + inlineScript(REG_EX_EXAMPLE_1) + "<BR/>which is equivalent "
                    + "to: " + inlineScript(REG_EX_EXAMPLE_2))
                    + par(REG_EX_HELP_SIMPLIFY)
                    );
    
    public static final String QUICK_HELP_NUMBERS = ""
            + par(title("Number representations")
                    + "A number representation is given by a representation name followed by a list of parameters, e.g.:\r\n"
                    + center(inlineScript("excessq[value=230, q=128, radix=2, *length=10];") + "\r\n")
                    + center(inlineScript("complement[code=111100, complementType=2];") + "\r\n")
                    + "Give a decimal " + inlineScript("value") + " to calculate the according code.\r\n"
                    + "Give a " + inlineScript("code") + " in the syntax of the representation to calculate the according value.\r\n"
                    + "Where available, a " + inlineScript("radix") + " between " + inlineScript("2") + " and " + inlineScript("36") + " determines the base of the representation.\r\n"
                    + "Currently available representations: " + bold(new Numbers(null).getAvailableTypesFormatted()) + "."
                    );
    
    private static final String QUICK_HELP_CIRCUIT_PLAIN = "A logic circuit is given by a list of connections, each either "
            + "<UL>"
            + "<LI>from a logic gate " + inlineScript("X") + " to an input " + inlineScript("n") + " of another logic gate " + inlineScript("Y") + " as: " + newlineScript("X => Y.n;") + "</LI>"
            + "<LI>or from an input " + inlineScript("I") + " to an input " + inlineScript("n") + " of a logic gate " + inlineScript("Y") + " as: " + newlineScript("I => Y.n;") + "</LI>"
            + "<LI>or from a logic gate " + inlineScript("X") + " to an output " + inlineScript("O") + " as: " + newlineScript("X => O;") + "</LI>"
            + "</UL>"
            + "Undefined elements " + inlineScript("I,O") + " are treated as inputs or outputs, respectively. "
            + "The remaining elements, i.e., logic gates, are defined in the declarations, e.g.:"
            + center(inlineScript("components = A:NOT, B:XOR-nnin, C:XOR, D:AND, E:AND, F:OR;"))
            + "More than two inputs are defined by a series of " + inlineScript("n") + " and " + inlineScript("i") + " "
            + "(cf. " + inlineScript("B") + " in the example) for "
            + "'normal' and 'inverted' inputs, respectively.";

    public static final String QUICK_HELP_CIRCUIT = ""
            + par(title("Logic circuit")
                    + QUICK_HELP_CIRCUIT_PLAIN
                    );
    
    private static final String QUICK_HELP_META_PROPERTIES = "This object type can be used to retrieve certain kinds of "
            + "meta properties about " + XWIZZ_HTML + ". So far it lists all the objects available and shows how they can be transformed into one another.";

    private static final String COMPL_BEH_IN_EVOROB_LINK = "http://www.degruyter.com/view/product/449355";
    private static final String COMPL_BEH_IN_EVOROB_REFERENCE = "[Lukas K&ouml;nig: Complex Behavior in Evolutionary Robotics, DeGruyter (Munich), 2015]";

    private static final String QUICK_HELP_MARB = "A Moore Automaton for Robot Behavior (MARB) is represented by this script type. "
            + "See " + link(COMPL_BEH_IN_EVOROB_LINK, COMPL_BEH_IN_EVOROB_REFERENCE, true) + " for details.\"";

    /* ****************** Help texts QUICK/GER. ****************** */
    
    private static final String QUICK_HELP_MARB_G = "Dieser Skripttyp representiert einen \"Moore Automaton for Robot Behavior (MARB)\". "
            + "Siehe " + link(COMPL_BEH_IN_EVOROB_LINK, COMPL_BEH_IN_EVOROB_REFERENCE, true) + ".\"";

    private static final String QUICK_HELP_META_PROPERTIES_G = "Dieser Objekttyp ist dazu da, einige Meta-Eigenschaften über den "
            + XWIZZ_HTML_G + " anzuzeigen. Bisher werden alle verfügbaren Objekte aufgelistet und ihre Beziehung untereinander durch Pfeile verdeutlicht. "
            + "Ein Pfeil von Objekt X zu Objekt Y bedeutet dabei, dass es Konversionsmethoden gibt, die X in Y transformieren.";
    
    private static final String SEARCHTREE_BASE_G = "Schreibe eine Sequenz von ganzen Zahlen, die durch Leerzeichen getrennt sind. +"
            + "Die Reihenfolge der Zahlen von links nach rechts gibt deren Einfügereihenfolge im Baum ein.";
    
    private static final String QUICK_HELP_TREE234_G = ""
            + par(title("2-3-4-Baum")
            + SEARCHTREE_BASE_G);
    private static final String QUICK_HELP_REDBLACKTREE_G = ""
            + par(title("Rot-Schwarz-Baum")
            + SEARCHTREE_BASE_G);

    private static final String QUICK_HELP_CIRCUIT_PLAIN_G = "Ein logischer Schaltkreis wird definiert durch eine Liste von Verbindungen:"
            + "<UL>"
            + "<LI>von einem Gatter " + inlineScript("X") + " zum Eingang " + inlineScript("n") + " eines anderen Gatters " + inlineScript("Y") + " als: " + newlineScript("X => Y.n;") + "</LI>"
            + "<LI>oder von einem Schaltungseingang " + inlineScript("I") + " zum Eingang " + inlineScript("n") + " eines Gatters " + inlineScript("Y") + " als: " + newlineScript("I => Y.n;") + "</LI>"
            + "<LI>oder von einem Gatter " + inlineScript("X") + " zu einem Schaltungsausgang " + inlineScript("O") + " als: " + newlineScript("X => O;") + "</LI>"
            + "</UL>"
            + "Undefinierte Elemente " + inlineScript("I,O") + " werden jeweils als Schaltungseingang oder Schaltungsausgang behandelt. "
            + "Die übrigen Elemente, d.h. Gatter, werden im Deklarationsbereich definiert, bspw.:"
            + center(inlineScript("components = A:NOT, B:XOR-nnin, C:XOR, D:AND, E:AND, F:OR;"))
            + "Soll ein Gatter mehr als zwei Eingänge haben, kann eine Sequenz aus " + inlineScript("n") + " und " + inlineScript("i") + " "
            + "(vgl. " + inlineScript("B") + " im Beispiel) für "
            + "'normale' bzw. 'invertierte' Eingänge angegeben werden.";

    public static final String QUICK_HELP_CIRCUIT_G = ""
            + par(title("Logischer Schaltkreis")
                    + QUICK_HELP_CIRCUIT_PLAIN_G
                    );
    
    private static final String QUICK_HELP_NUMBERS_G = ""
            + par(title("Zahlendarstellung")
                    + "Eine Zahlendarstellung wird angegeben durch den Namen der Darstellung, gefolgt von einer Parameterliste, bspw.:\r\n"
                    + center(inlineScript("excessq[value=230, q=128, radix=2, *length=10];") + "\r\n")
                    + center(inlineScript("complement[code=111100, complementType=2];") + "\r\n")
                    + "Gib eine Dezimalzahl (Parameter " + inlineScript("value") + ") an, um den zugehörigen Code zu berechnen.\r\n"
                    + "Gib einen " + inlineScript("code") + " in der Syntax der entsprechenden Darstellung an, um die zugehörige Dezimalzahl zu berechnen.\r\n"
                    + "Wenn verfügbar, kann durch den Parameter " + inlineScript("radix") + " eine Zahlenbasis zwischen " + inlineScript("2") + " und " + inlineScript("36") + " angegeben werden.\r\n"
                    + "Derzeit verfügbare Darstellungen: " + bold(new Numbers(null).getAvailableTypesFormatted()) + "."
                    );

    private static final String QUICK_HELP_RegEx_G = ""
            + par(title("Regulärer Ausdruck")
                    + REG_EX_BASIC_HELP_G
                    + par("Beispiel: " + inlineScript(REG_EX_EXAMPLE_1) + "<BR/>oder äquivalent: "
                    + inlineScript(REG_EX_EXAMPLE_2))
                    + par(REG_EX_HELP_SIMPLIFY_G)
                    );

    public static final String BDD_QUICK_HELP_PLAIN_G 
        = "Schreibe eine Reihe von " + inlineScript("0") + "en und " + inlineScript("1") 
            + "en in der Reihenfolge wie sie in einer Standard-Wahrheitstabelle der gewünschten Funktion "
            + "auftauchen würden, um ein BDD zu definieren.\r\n"
            + "Wenn die Länge nicht " + math(pow("2", "n")) + " für irgendein " + math("n") + " ist, werden " 
            + inlineScript("0") + "en am Ende aufgefüllt.";

    public static final String QUICK_HELP_BDD_G = ""
        + par(title("Binary-Decision-Diagram")
                + BDD_QUICK_HELP_PLAIN_G.replace("\r\n", " "))
        + par("Die Benennung der Variablen kann angepasst werden, indem vorne " 
                + inlineScript("a,b,c,...") + " eingefügt wird.");
    
    private static final String QUICK_HELP_HUFFMAN_G = ""
            + par(title("Huffman-Baum")
                    + HUFFMAN_QUICK_HELP_PLAIN_G);
    
    private static final String QUICK_HELP_FSM_G = ""
            + par(title("Endlicher Automat")
                    + "Eine Transition 'von " + inlineScript("s0") + " nach " 
                    + inlineScript("s1") + ", beim Lesen des Eingabesymbols " + inlineScript("a") + "' wird definiert als  "
                    + inlineScript("(s0, a) => s1;"))
            + par("Definiere den Startzustand " + inlineScript("s0") + ", Endzustände " + inlineScript("F") + " und das " + inlineScript("input") + "-Wort im Deklarationsbereich.");

    private static final String QUICK_HELP_GRAMMAR_G = ""
            + par("Grammatiken können in zwei Modi betrieben werden (Gesamtbaum oder Parsebaum).")
            + par(title("Gesamtbaum")
                + "Definiere eine Produktionsregel als:\n"
                + inlineScript("S => a, S, b;")
                + " oder "
                + inlineScript("S => epsilon;")
                + "\n"
                + "Definiere " + inlineScript("N") + " (Nonterminalsymbole), " + inlineScript("T") 
                + " (Terminalsymbole) und " + inlineScript("S") + " (das Startsymbol) im Deklarationsbereich. "
                + "Jeder Ast des Gesamtbaums zeigt für ein terminales Wort, wie es durch die Grammatik abgeleitet werden kann.")
            + par(title("Parsebaum (kontextfreie Grammatiken)")
                + "Es kann ein Parsebaum durch Klicken auf " 
                    + button("Parse einzelnes Wort") 
                    + " oder Hinzufügen von " + inlineScript("parse(w, o, r, d)--0") + " (vor dem Doppelpunkt am Beginn des Skripts) erzeugt werden. "
                    + "Ein Parsebaum zeigt für ein einzelnes terminales Wort an, wie es durch die Grammatik abgeleitet werden kann. "
                    + "Durchlaufe die Parsebäume durch Klicken auf den " + button("Durchlaufe...") + "-Button.\n")
            + par("Beachte: Das leere Wort heißt " + inlineScript("epsilon") + ", nicht lambda, in Grammatik-Skripten.");
    
    private static final String QUICK_HELP_PDA_G = ""
            + par(title("Kellerautomat")
                    + "Eine Transition 'von " + inlineScript("s0") + ", wobei das Eingabesymbol " 
                    + inlineScript("a") + " und das Kellersymbol " + inlineScript("k") + " eingelesen werden, nach " + inlineScript("s1") 
                    + ", wobei " + inlineScript("ak") + " auf den Keller geschrieben wird' wird definiert als: " 
                    + inlineScript("(s0, a, k) => (s1, ak);"))
            + par("oder 'von " + inlineScript("s0") + " ohne die Eingabe zu lesen...' als: "
                    + inlineScript("(s0, lambda, k) => (s1, abck);"))  
            + par("Definiere den Startzustand " + inlineScript("s0") + ", Endzustände " + inlineScript("F") 
                    + ", das Kellersymbol " + inlineScript("kSymb") + " und " +  inlineScript("input") + "-Wörter im Deklarationsbereich.");
    
    private static final String QUICK_HELP_TURING_G = ""
            + par(title("Turingmaschine")
                    + "Eine Transition 'von " + inlineScript("s0") + ", wobei das Eingabesymbol " 
                    + inlineScript("*") + " gelesen wird, nach " + inlineScript("s1") + ", wobei " + inlineScript("a") 
                    + " auf das Band geschrieben wird und der Kopf nach links läuft,' wird definiert als: " 
            + inlineScript("(s0, *) => (s1, a, L);"))
            + par("Definiere den Startzustand " + inlineScript("s0") + ", Endzustände " + inlineScript("F") 
                    + ", das " + inlineScript("blank") + "-Symbol und " + inlineScript("input") + "-Wörter im Deklarationsbereich.");

    private static final String QUICK_HELP_LATEX_G = ""
            + par(title("Latex-Prozessor")
                + "Gib ein beliebiges vollständiges Latex-Dokument ein und klicke auf " 
                + button("Draw!") + ", um es zu kompilieren. Latex ist ein komplexes "
                + "und sehr mächtiges Textsatzsystem. Siehe " 
                + link("https://de.wikipedia.org/wiki/LaTeXCode", "Wikipedia") + " zum Einstieg.")
            + par("Wenn " + inlineScript("formulaMode=true")
                + " eingestellt ist (im Deklarations-Bereich), dann ist der Mathe-Modus eingeschaltet. "
                + "Alles außen herum (Package-Deklarationen, \\documentclass, "
                + "\\begin{document} usw.) fallen in diesem Fall weg."
            + par("Latex-Code kann aus allen auf Latex basierenden Skripten generiert werden, "
                + "indem auf den Button " + button("Zeige Generator-Code") + " geklickt wird."));
    
    private static final String QUICK_HELP_PATTREE_G = ""
            + par(title("Pat tree")
                    + "Gib einen beliebigen Text ein, der als Basis für die Berechnung des PatTrees genommen werden soll. "
                    + "Leerzeichen sind erlaubt, allerdings keine Zeilenumbrüche.");
    
    private static final String QUICK_HELP_PLAINDOT_G = ""
            + par(title("Einfacher Graphviz-DOT-Code")
                    + "Dieser Skript-Typ akzeptiert reinen Graphviz-Code und generiert den entsprechenden "
                    + "Graphen. Der Code kann eingetippt oder aus allen auf Graphviz basierenden Skripten "
                    + "generiert werden, indem auf den Button " + button("Zeige Generator-Code") + " "
                    + "geklickt wird. "
                    + "Der Code muss der Syntax entsprechen, die in der Graphviz-Dokumentation beschrieben "
                    + "wird, siehe: "
                    + link("http://www.graphviz.org/Documentation.php"))
            + par("Beachte: Im Augenblick kann nur der Graphviz-'dot'-Prozessor genutzt werden.");
    
    private static final String QUICK_HELP_CALC_G = ""
            + par(title("Taschenrechner")
                    + "Gib einen arithmetischen Term ein, etwa: "
                    + inlineScript("(3+4)^2")
                    + ", mit der Bedeutung: " + math(pow("(3+4)", "2"))
                    + ", und das Ergebnis wird berechnet. Wenn " + inlineScript("x") 
                    + " und/oder " + inlineScript("y") + " verwendet wird" 
                    + ", etwa: " + inlineScript("x^2+2^y") + ", mit der Bedeutung " + math(pow("x", "2") + "+" + pow("2", "y")) 
                    + ", wird eine Tabelle aller Ergebnisse in einem bestimmten Intervall ausgegeben. "
                    + "Die Intervalle können im Deklarations-Bereich festgelegt werden.");

    /* ****************** Help texts LONG/ENG. ****************** */
    
    private static final String LONG_HELP_BDD = ""
            + par(def("Binary decision diagrams (BDDs)") + " are used to represent Boolean functions "
                    + "in a compressed way. They are usually more efficient than other representations such as truth tables or normal forms of boolean expressions "
                    + "(in terms of space as well as time to calculate a value from a given tuple of Boolean input values). "
                    + "Unlike other compressed representations, operations can be performed directly on the "
                    + "compressed representation, i.e., without decompression. "
                    + "BDDs are constructed in a bottom-up procedure from a truth tree as shown " + link("https://en.wikipedia.org/wiki/Binary_decision_diagram", "here") + ".")
            + par(USAGE
                    + itemize(BDD_QUICK_HELP_PLAIN.replace("\r\n", "</li><li>"),
                          "If you add declarations to the script " + HOW_TO_ADD_DECLARATIONS 
                              + ", you can specify how many construction steps to perform by changing the variable " 
                              + inlineScript("simplifySteps") + " (" + inlineScript("-1") + " to perform all steps).",
                          "To simplify the truth tree stepwise, click " + button("Simplify stepwise") + ". "
                              + "This will actually just increase the " + inlineScript("simplifySteps")
                              + " value in the declarations as described above.",
                          "By clicking on " + button("TRUTH TABLE...") 
                          + ", you can generate the truth table which the BDD relies on."))
            + example("bdd: a,b,c: 01101010\n" + 
                    "--declarations--\n" + 
                    "simplifySteps=-1\n" + 
                    "--declarations-end--");
    
    private static final String LONG_HELP_FSM = ""
          + par("A " + def("finite-state machine") + " (" + def("FSM") + "; also just " + def("state machine") + " or " + def("finite-state automaton") + "), "
                + "is a mathematical model of computation. Apart from its applications in theoretical computer science, it is practically used to design "
                + "both computer programs and sequential logic circuits. "
                + "An FSM is conceived as an abstract machine that is in one state (called the " + def("current state") + ") out of a finite number of possible states at a time. "
                + "It can change from one state to another when initiated by a triggering event or condition; this is called a " + def("transition") + ". "
                + "A particular FSM is defined by a list of its states and the triggering condition for each transition.")
          + par("Note that the " + italic("overall current state") + " of a " + def("non-deterministic") + " FSM can consist of several "
                + "states or no state at all. "
                + "However, in essence the above description still applies in this case, "
                + "as any subset of the set of states of a non-deterministic FSM can be seen as a single state in an equivalent deterministic FSM. "
                + "(Cf. the " + link("https://en.wikipedia.org/wiki/Powerset_construction", "power set construction procedure") 
                + " to make a non-deterministic FSM deterministic.)")
          + par("An FSM-like behavior is  typical to many real-world appliances which perform a predetermined "
                + "sequence of actions depending on a sequence of events with which they are presented. "
                + "Simple examples are vending machines which dispense products when the proper combination of coins is deposited, "
                + "elevators which drop riders off at upper floors before going down, traffic lights which change sequence when cars are waiting, "
                + "and combination locks which require the input of combination numbers in the proper order. "
                + "Find more details regarding FSMs " + link("https://en.wikipedia.org/wiki/Finite-state_machine", "here") + ".")
          + par(USAGE
                + itemize("Start a script with " + inlineScript("fsm:") + " to indicate an FSM script.",
                      "Provide a list of transitions, each having the form: " + inlineScript("(s0, a) => s1;") + " (meaning: from state " + inlineScript("s0") + ", if reading " + inlineScript("a") + ", go to state " + inlineScript("s1") + ").",
                      "The list of states as well as the symbols of the input alphabet are not given explicitly. "
                              + "When reading a transition, the given states (" + inlineScript("s0, s1") + " in the above example) "
                              + "as well as the input symbol (" + inlineScript("a") + " in the above example) are assumed to be part of the FSM.",
                      "The initial state (" + inlineScript("s0") + ") and the list of final states (" + inlineScript("F") + ") have to be defined in the declarations area " + HOW_TO_ADD_DECLARATIONS + ".",
                      "By clicking on " + button("SIMULATE ONE STEP") + ", you can provide an input word which the FSM is simulated on by repeatedly clicking the same button. "
                              + "(You can accomplish the same by entering an " + inlineScript("input") + " word and a " 
                              + inlineScript("simulateToStep") + " value into the declarations area.)",
                      "If you define a deterministic FSM, you can click " + button("MINIMIZE") + " to create an equivalent minimal FSM.",
                      "In this case, you can also show the minimization table by clicking " + button("Show minimization table") + ".",
                      "If you define a non-deterministic FSM, you can create an equivalent deterministic FSM by clicking " + button("DETERMINIZE") + ".",
                      "For any FSM you can generate the " + button("MINIMIZATION CHAIN") + " which gives information about the determinization and minimization process.",
                      "You can convert the FSM into an equivalent pushdown automaton (" + button("PDA") + "), "
                              + "a turing machine (" + button("TM") + "), a right-linear grammar (" + button("RL Grammar") + ") or a regular expression (" + button("RegEx") + ") "
                              + "by clicking on the according buttons."))
          + example("fsm:\n" + 
                "(s0, a) | (s3, a) | (s4, a) => s2;\n" + 
                "(s0, b) | (s3, b) => s1;\n" + 
                "(s1, a) => s0;\n" + 
                "(s1, b) | (s2, a) => s4;\n" + 
                "(s2, b) | (s4, b) => s3;\n" + 
                "--declarations--\n" + 
                "simulateToStep=0;\n" + 
                "input=bbbbbaabba;\n" + 
                "s0=s0;\n" + 
                "F=s0\n" + 
                "--declarations-end--");
    
    private static final String LONG_HELP_TURING = ""
              + par("A " + def("Turing machine (TM)") + " is a computational model (" + def("automaton") + ") from theoretical computer science. "
                      + "It is a hypothetical device that can manipulate symbols on a one-dimensional tape according to a table of rules (" + def("transitions") + "). "
                      + "In essence, it can be imagined as a " + def("finite-state machine") + " with the following alterations:"
                      + itemize("The tape is infinite to both the left and the right, containing at the beginning the input word surrounded by infinite sequences of a " + def("blank") + " symbol " 
                              + inlineScript("*") + ". For example: " + inlineScript("...****abaabaabb****..."),
                              "The head can both read and write symbols, i.e., replace the symbol it reads by another one.",
                              "The head can move in both directions or stand still.",
                              "The transition function is not total, meaning that there may be states which for certain read symbols do not have a following state. "
                                      + "In this case (and only in this case), the TM calculation is terminated (the TM " + def("halts") + ").")
                      + "A TM is said to " + def("compute a function") + " which is defined by mapping the input word (for all possible input words) "
                      + "onto the corresponding word written on the tape after halting, i.e., the word generated during the computation process. "
                      + "Furthermore, a TM is said to " + def("accept") + " all input words that cause the machine "
                      + "to halt in a " + def("final state") + ". The set of these words is called the "
                      + def("language") + " of the TM. "
                      + "As not all TMs halt for all input words, their computed functions do not have to be total. "
                      + "Despite its simplicity, a TM can be adapted to simulate the logic of any computer algorithm "
                      + "in terms of computing the same function as the corresponding program does. "
                      + "For further information look " + link("https://en.wikipedia.org/wiki/Turing_machine", "here") + "."
              + par("A TM is " + def("non-deterministic") + " if more than one transition exists for a state and a corresponding input symbol. "
                      + "In this case the TM considers all possibilities simultaneously leading to a computation tree (rather than a computation sequence). "
                      + "A non-deterministic TM accepts all input words that lead to a computation tree with at least one branch ending in a final state."))
              + par(USAGE
                    + itemize("Start a script with " + inlineScript("turing:") + " to indicate a TM script.",
                          "Rather than using a table, " + XWIZZ_HTML + " excepts a list of transitions of the form: " 
                                  + inlineScript("(s0, a) => (s1, b, R);")
                                  + " (meaning: from state " + inlineScript("s0") + ", if reading " + inlineScript("a") + ", go to state " + inlineScript("s1") + ", "
                                  + "write symbol " + inlineScript("b") + " on the tape field where the head resides, "
                                  + "and position the head one field to the right; also allowed: " + inlineScript("L") + " for left "
                                  + "and " + inlineScript("N") + " for neutral, i.e., no movement).",
                          "The list of states as well as the symbols of the input and tape alphabets are not given explicitly. "
                                  + "When reading a transition, the given states (" + inlineScript("s0, s1") + " in the above example) "
                                  + "as well as the tape symbols (" + inlineScript("a, b") + " in the above example) are assumed to be part of the TM.",
                          "The initial state (" + inlineScript("s0") + "), the list of final states (" 
                                  + inlineScript("F") + ") and the blank symbol (" + inlineScript("*") + ") have to be defined in the declarations area " 
                                  + HOW_TO_ADD_DECLARATIONS + ".",
                          "The default ouput graph of a TM script is a sequence of tapes denoting "
                                  + "the trace of a computation beginning with a certain input word. "
                                  + "The input word also has to be defined in the declarations area. For this, the variable " + inlineScript("inputs") 
                                  + " can be set to one or several strings denoting one or several input words. In the latter case, several computation traces "
                                  + "are depicted at once. "
                                  + "Note that for non-deterministic TMs only one input word is allowed. The output is a computation tree rather than a sequence.",
                          "As TMs can run indefinitely, the variable " + inlineScript("runStepsScript") + " defines an upper bound for computation steps to simulate.",
                          "Setting the variable " + inlineScript("shortTrace") + " to true lets " + XWIZZ_HTML + " hide computation steps which "
                                  + "represent the same action as the preceding step.",
                          "Other than the default output, a TM script provides conversion methods into Latex scripts "
                                  + "to show the transition table and the (visually nicer) computation trace, respectively.",
                          "Furthermore, a random TM can be generated by clicking " + button("Random Busy Beaver") + ". "
                                  + "According to a user-defined number of states " + math("|S|") 
                                  + " and a number of trials " + math("t") + ", the algorithm generates "
                                  + "" + math("t") + " random TMs with " + math("|S|") + " states and input word " 
                                  + inlineScript("*") + ". The ouput script is "
                                  + "the one of the " + math("t") + " TMs which has the most '" + inlineScript("1") + "' symbols in the computed word, "
                                  + "i.e., the 'busiest' of all the generated machines. Cf. the definition of " 
                                  + link("https://en.wikipedia.org/wiki/Busy_beaver", "Busy Beavers") + "."))
              + example("turing:\n" + 
                      "(s4, 1) => (s1, 1, L);\n" + 
                      "(s2, 1) => (s4, *, L);\n" + 
                      "(s3, *) => (s2, *, R);\n" + 
                      "(s4, *) => (s3, *, L);\n" + 
                      "(s1, *) => (s4, 1, L);\n" + 
                      "(s2, *) => (s0, 1, L);\n" + 
                      "(s0, *) => (s1, 1, R);\n" + 
                      "--declarations--\n" + 
                      "s0=s0;\n" + 
                      "F=s3,s4,s0,s1,s2,;\n" + 
                      "blank=*;\n" + 
                      "inputs=*;\n" + 
                      "runStepsScript=100;\n" + 
                      "shortTrace=false\n" + 
                      "--declarations-end--");
    
    private static final String LONG_HELP_GRAMMAR = ""
            + par("In formal language theory (studied both in theoretical computer science and linguistics), "
                    + "a " + def("Chomsky grammar") + ", " + def("formal grammar") + " or simply " + def("grammar") 
                    +  " is essentially a set of production rules for strings, i.e., sequences of symbols. "
                    + "The rules describe how to construct strings over a given alphabet that are " + def("valid") + ", "
                    + "as opposed to unconstractable, i.e., invalid strings. The set of valid strings "
                    + "is called the " + def("(formal) language") + " of the grammar."
                    + "Therefore, grammars are usually thought of as language " + def("generators") + ". ")
            + par("However, grammars can also be used as the basis for " + def("recognizers") + ", i.e., programs "
                    + "for determining whether a given string belongs to a grammar's language or not. "
                    + "For this purpose, the grammar is processed by the classic recognizing formalism: an " + def("automaton") + " "
                    + "(which is basically a computer program). "
                    + "In addition to plain recognizing, a grammar can be used to " + def("parse") + " a given string "
                    + "if it is in the grammar's language. Parsing provides information about how the string "
                    + "is constructed by the grammar. For grammars of Chomsky type 2, parsing is efficiently "
                    + "possible (for example by using an " + link("https://en.wikipedia.org/wiki/Earley_parser", "Earley parser") 
                    + ") and the result is a " + link("https://en.wikipedia.org/wiki/Parse_tree", "parse tree") + " "
                    + "or " + def("syntax tree") + ". "
                    + "Due to these properties, grammars are particularly useful as a basis for "
                    + "programming languages.")
            + par(bold("Word generation:") + " A grammar's rules define a set of rewriting operations on strings, such as " 
                    + inlineScript("string => another-string") + ", "
                    + "along with a " + def("start symbol S") + ". Beginning with the set {S} (containing "
                    + "only the start symbol), all strings are added to the set that can be generated by taking a "
                    + "string from the set and replacing a substring that matches the left side of a rule with its "
                    + "right side. The language of the grammar is defined to be a subset of the resulting "
                    + "set which contains only " + def("terminal") + " words, i.e., words containing only symbols "
                    + "from a subset of the underlying alphabet called " + def("the set of terminal symbols T") + ". "
                    + "The remaining symbols are called " + def("the set of non-terminal symbols N") + ". "
                    + "More information on grammars can be found "
                    + link("https://en.wikipedia.org/wiki/Formal_grammar", "here") + ".")
            + par(USAGE
                  + itemize("Start a script with the preamble " + inlineScript("grammar:") + " to indicate a regular grammar script. "
                              + "This will show a " + def("complete generation tree") + ", i.e., all words that can be generated "
                              + "from " + inlineScript("S") + " up to a certain depth. By adding " 
                              + inlineScript("parse(a, a, b, b, a, b)--0") + " to the preamble (right before the colon), "
                              + "parse mode is activated showing the " + def("parse tree") + " for the word "
                              + inlineScript("a, a, b, b, a, b") + ".",
                          "To switch between the two modes, the buttons " + button("Complete tree") + " and "
                              + button("Parse single word") + " can be used as well.",
                          "If a word can be parsed in different ways, the trees can be toggled "
                              + "by clicking the " + button("Toggle...") + " button or changing the number in " 
                              + inlineScript("--0") + ".",
                          "Note that " + bold("all") + " parse trees are shown, not only those "
                              + "with the start symbol as root.",
                          "The rules are given by a list of elements such as " + inlineScript("A, S => b, A, A, c") + ".",
                          "In grammar scripts a symbol can consist of "
                              + "several characters, therefore, symbols are spearated by commas.",
                          "Both the left side and the right side of a rule can consist "
                              + "of more than one symbol (cf. " + def("Chomsky hierarchy") + ").",
                          "By convention, uppercase letters are usually used for nonterminals, "
                              + "lowercase letters for terminals. "
                              + "Nevertheless, nonterminals, terminals and the start symbol can be given explicitly (and by "
                              + "disobeying the convention if desired) "
                                  + "in the declarations area as "
                                  + inlineScript("N, T, S") + " " + HOW_TO_ADD_DECLARATIONS + ".",
                          "In regular mode, the variables " + inlineScript("maxdepth, maxLengthWords, cutNonTerminalBranches, cutTerminalDoubleBranches") + " can be "
                                  + "used to restrict the size of the generated tree. The first two do what their name tells. "
                                  + "The last two hide all nonterminal branches and all terminal branches if they lead to an already generated word, respectively.",
                          "Using conversion methods, the grammar can be made " + button("Epsilon-free") 
                                  + ", transformed into " + button("Chomsky NF") + ", " + button("Greibach NF") + " or " + button("Kuroda NF") + " (for type-1 grammars), "
                                  + "or " + button("Randomize") + "-d.",
                          "An equivalent push-down automaton can be generated by clicking " + button("PDA") + ".",
                          "By clicking " + button("Display mode") + " different views of the grammar can be shown. "
                                  + "This includes the grammar definition, a parsing tree and derivation chains.",
                          "Setting " + inlineScript("multiLetterSymbolsHaveIndex=true") + " leads to displaying symbols with more than "
                                  + "one character by subscripting all characters following the first."
                                  )
                  + bold("Note that parsing and most conversion methods require type-2 grammars.") 
                      + " Parse trees do not exist for non-type-2 grammars, and deciding if a word is in such a grammar's language is at least " + math("NP") + "-hard; "
                      + "Chomsky and Greibach normal forms do not exist for non-type-2 grammars as well, and making such a grammar epsilon-free is harder. Possibly, some "
                      + "conversion methods for non-type-2 grammars will be added in future. Kuroda NF has been added as a first start.")
            + example("grammar parse(a, a, b, b, a, b)--0:\n" + 
                    "A => D, D, a, A | A, B, b, B | A, b, D | B, a;\n" + 
                    "B => S, a | A, a, b;\n" + 
                    "D => b;\n" + 
                    "S => A, B, b | S, b | a, S | b | S, a;\n" + 
                    "--declarations--\n" + 
                    "N=S,A,B,C,D;\n" + 
                    "T=a,b;\n" + 
                    "S=S;\n" + 
                    "multiLetterSymbolsHaveIndex=true;\n" + 
                    "maxdepth=5;\n" + 
                    "cutNonTerminalBranches=true;\n" + 
                    "cutTerminalDoubleBranches=true;\n" + 
                    "maxLengthWords=10\n" + 
                    "--declarations-end--");
    
    private static final String LONG_HELP_HUFFMAN = ""
            + par("In computer science and information theory, a " + def("Huffman code") + " is a particular "
                    + "type of " + def("optimal prefix code") + " that is commonly used for lossless data compression. "
                    + "A Huffman code maps each symbol to encode onto a bit word such that the "
                    + link("https://de.wikipedia.org/wiki/Fano-Bedingung", "Fano property")
                    + " is satisfied (no code word is prefix of another one) and the resulting code has minimal size when encoding "
                    + "a text with a specified probability distribution of symbols. "
                    + "The process of finding and using such a code proceeds by an algorithm developed by David A. Huffman "
                    + "while he was a Ph.D. student at MIT. Look " 
                    + link("https://en.wikipedia.org/wiki/Huffman_coding", "here") 
                    + " for more information.")
            + par(USAGE
                  + itemize(HUFFMAN_QUICK_HELP_PLAIN.replace(". ", ".</li><li>"),
                          "The output depicts a " + bold("Huffman tree") + ". Running from its root to the leaves, the "
                                  + "sequence of labels of the visited edges is the code of the encoded character "
                                  + "(the upper-left corner of the according leaf label shows this character; its "
                                  + "probability of occurrence in the corresponding text is given in the upper-right corner, "
                                  + "the encoding in the bottom).",
                          "Note that the Huffman tree is usually not unique; particularly, labelling edges with " + math("1") 
                                  + " if they are drawn from right to left, and with " + math("0") + " otherwise "
                                  + "(as " + XWIZZ_HTML + " does it) is completely arbitrary. "
                                  + "As long as each (non-leaf) father node has a " + math("0") + " and a " + math("1") + " edge "
                                  + "leading to the according child node, all combinations are allowed.",
                          "Use the boolean variable " + inlineScript("classicView") + " " + HOW_TO_ADD_DECLARATIONS + " to switch between different views."))
            + example("huff:huffman-example\n" + 
                    "--declarations--\n" + 
                    "classicView=false\n" + 
                    "--declarations-end--");
    
    private static final String LONG_HELP_PDA = ""
            + par("In computer science, a " + def("pushdown automaton (PDA)") + " is a computational model "
                    + "that employs a tape accessible in 'Last-In-First-Out' manner, i.e., a "
                    + link("https://en.wikipedia.org/wiki/Stack_(data_structure)", "stack") + ". "
                    + "The term " + italic("pushdown") + " refers to the image that the stack is being 'pushed down' "
                    + "like a tray dispenser at a cafeteria, since the "
                    + "operations work on the top element only, and the other elements remain untouched until reaching the top.")
            + par("In terms of computational power, PDAs are more capable "
                    + "than " + def("finite-state machines") + " but less capable than "
                    + def("Turing machines") + ". "
                    + "More precisely, deterministic PDAs can recognize all deterministic "
                    + "context-free languages while nondeterministic PDAs can recognize "
                    + "all context-free languages. Mainly the former are frequently applied in the area of parser generation. "
                    + "Continue reading " + link("https://en.wikipedia.org/wiki/Pushdown_automaton", "here") 
                    + " to get more general information.")
            + par("A PDA scans an input word from left to right while switching between a finite number of "
                    + def("states") + ". Additionally, the PDA can store in each step an arbitrary number of symbols "
                    + "on the stack (" + def("push") + "); of these symbols, only the top-most one is read and removed in each step (" 
                    + def("pop") + "). Accordingly, a PDA " + def("transition") + " maps a tuple containing a state, "
                    + "an input symbol (read on the input tape) and a stack symbol (popped from the stack) "
                    + "onto a target state and a sequence of symbols to be pushed to the stack. "
                    + "A PDA accepts an input if it reaches a final state after processing the complete input.")
            + par(USAGE
                  + itemize(
                        "Start a script with " + inlineScript("pda:") + " to indicate a PDA script.",
                        "Provide a list of transitions like this: "
                          + inlineScript("(s0, a, b) => (s1, ab);")
                          + " (meaning: from state " + inlineScript("s0") + ", if reading " + inlineScript("a") 
                          + " on the tape and popping " + inlineScript("b") + " from the stack, go to state " 
                          + inlineScript("s1") + " and push " + inlineScript("ab") + " to the stack; as " 
                          + inlineScript("b") + " has been popped before, this means that, in effect, " 
                          + inlineScript("b") + " remains on the stack, and " + inlineScript("a") + " is "
                          + " pushed on top of it).",
                        "The output of a PDA script is a sequence (or tree in the non-deterministic case) "
                          + "of computation steps performed on a given word.",
                        "The input word (several allowed in deterministic case) to process is given as "
                          + inlineScript("inputs") + " in the declarations area " + HOW_TO_ADD_DECLARATIONS + ".",
                        "The initial state " + inlineScript("s0") + ", "
                          + "the list of final states " + inlineScript("F") + " and "
                          + "the special stack symbol (denoting the bottom of the stack) " + inlineScript("kSymb") + " "
                          + "are also defined in the declarations area.",
                        "Clicking button " + button("PDA definition") + " switches to Latex mode and shows "
                          + "the definition of the PDA.",
                        "For a deterministic PDA, button " + button("Calculation steps") + " switches to a nicer "
                          + "depiction of the computation trace in Latex mode."))
            + example("pda:\n" + 
                    "(s0, a, k) => (s4, k);\n" + 
                    "(s0, b, k) => (s3, k) | (s1, k) | (s0, k);\n" + 
                    "(s2, a, k) => (s1, k);\n" + 
                    "(s2, b, k) => (s1, k) | (s0, k);\n" + 
                    "(s3, a, k) => (s3, k) | (s1, k);\n" + 
                    "(s3, b, k) => (s4, k) | (s1, k) | (s2, k) | (s0, k) | (s3, k);\n" + 
                    "(s4, b, k) => (s0, k);\n" + 
                    "--declarations--\n" + 
                    "s0=s0;\n" + 
                    "F=s0,s2;\n" + 
                    "kSymb=k;\n" + 
                    "inputs=abbab\n" + 
                    "--declarations-end--");

    private static final String LONG_HELP_REGEX = ""
            + par(" In theoretical computer science and "
                    + "formal language theory, a " + def("Regular expression (RE or regex)") + " "
                    + "is an operational method to define a " + def("formal language") + ". "
                    + "For an alphabet " + math("E") + ", an RE " + math("\\alpha") 
                    + " defines (by using basic operations on sets) how characters "
                    + "from " + math("E") + " can be combined to sequences of characters, i.e., " 
                    + def("words") + " in the language " + math("L(\\alpha)") + ". "
                    + "There, " + math("\\alpha") + " is a valid RE if"
                    + itemize(
                            math("\\alpha = \\emptyset") + " or " + math("\\alpha = e") + " for " + math("e \\in E") + " or",
                            math("\\alpha = (" + index("\\alpha", "1") + " + " + index("\\alpha", "2") + ")") 
                                + " (union) or",
                            math("\\alpha = (" + index("\\alpha", "1") + " \\cdot " + index("\\alpha", "2") + ")") 
                                + " (concatenation) or",
                            math("\\alpha = (" + index("\\alpha", "1") + ")*") 
                                + " (iteration or 'Kleene's operation')"
                                )
                    + "for valid REs " + math(index("\\alpha", "1") + ", " + index("\\alpha", "2")) + ". "
                    + "The language of an RE is given by recursively evaluating the above-defined patterns as follows:"
                    + itemize(
                            math("L(\\emptyset) = \\emptyset, L(e) = {e}") + ",",
                            math("L((" + index("\\alpha", "1") + " + " + index("\\alpha", "2") + "))"
                                    + " = L(" + index("\\alpha", "1") + ") \\cup L(" + index("\\alpha", "2") + ")"),
                            math("L((" + index("\\alpha", "1") + " \\cdot " + index("\\alpha", "2") + "))"
                                    + " = L(" + index("\\alpha", "1") + ") \\circ L(" + index("\\alpha", "2") + ")")
                                + " where " + math(index("L", "1") + " \\circ " + index("L", "2") 
                                + " = {" + index("w", "1") + index("w", "2") + " | " 
                                        + index("w", "1") + " \\in " + index("L", "1") + ", "
                                        + index("w", "2") + " \\in " + index("L", "2")
                                + "}")
                                + ", and",
                            math("L((" + index("\\alpha", "1") + ")*) = " + index("\\bigcup", "i \\in " + 
                                    index("\\N", "0"))
                                    + " " + pow("L(" + index("\\alpha", "1") + ")", "i"))
                                + "."
                                )
                    + "For example, " + math("((a \\cdot b))*") + " is a regular expression over the alphabet "
                    + math("E = {a, b}") + " which defines the language " + math("{\\lambda, ab, abab, ababab, ...}") + ". "
                    + "Note, however, that parentheses can be omitted if the RE can be interpreted "
                    + "correctly by evaluating " + math("*") + " before " + math("\\cdot") + " before " + math("+") 
                    + ", and that " + math("\\cdot") + " is usually not written" + ". Thus, the above "
                    + "RE can be written as " + math("(ab)*") + " as well. Note furthermore, that by "
                    + "definition the empty word " + math("\\lambda") + " can be expressed by the RE: " 
                    + math("\\emptyset*") + "")
            + par("The set of languages which can be defined by REs is the same that can "
                    + "be recognized by a " + def("finite state machine") + " or constructed by a " 
                    + def("right-linear grammar") + ". Therefore, there are algorithms to construct "
                    + "for any RE an equivalent finite state machine and vice versa (the same is true "
                    + "for right-linear grammars).")
            + par("In practice, REs are mainly used in pattern matching with strings, "
                    + "or string matching, i.e. 'find and replace'-like operations. "
                    + "The concept arose in the 1950s, when the American mathematician "
                    + "Stephen Kleene (cf. 'Kleene's operation' above) formalized the description of a regular language. "
                    + "REs came into common use with the Unix text processing utilities " + def("ed") + ", "
                    + "an editor, and " + def("grep") + " (global regular expression print), a filtering program. "
                    + "REs are so useful in computing that the various "
                    + "systems have evolved to provide "
                    + "both a basic and extended standard for the grammar and syntax; "
                    + "modern regular expressions heavily augment the above-defined minimal set of operations. "
                    + XWIZZ_HTML + ", however, doesn't.") 
//            + par("Many programming languages provide regular expression capabilities, "
//                    + "some built-in, for example Perl, JavaScript, Ruby, AWK, and Tcl, "
//                    + "and others via a standard library, for example .NET languages, "
//                    + "Java, Python and C++ (since C++11). Most other languages offer "
//                    + "regular expressions via a library.")
            + par("More information regarding REs, particularly practical usage guidelines, can be found " 
                    + link("https://en.wikipedia.org/wiki/Regular_expression", "here") + ".")
            + par(USAGE
                    + itemize(
                            "Start an RE script with " + inlineScript(RegularExpression.SCRIPT_PREAMBLE),
                            REG_EX_BASIC_HELP.replace(". ", ".</LI><LI>"),
                            REG_EX_HELP_SIMPLIFY,
                            "Note that minimizing an RE is PSPACE-complete, "
                            + "so all simplifications are merely straight-forward 'common sense' operations "
                            + "which will simplify the RE only to a basic extent."))
            + example(RegularExpression.SCRIPT_PREAMBLE + " " + REG_EX_EXAMPLE_2);

    private static final String LONG_HELP_NUMBERS = "LONG_TEST"; // TODO

    private static final String LONG_HELP_CIRCUIT = "Note that logic circuits are currently under construction and don't provide much functionality. "
            + "Furthermore, their depiction can get pretty ugly for complex circuits - we are working on that.\r\n\r\n"
            + QUICK_HELP_CIRCUIT_PLAIN;       
    
    private static final String LONG_HELP_PATTREE = null;         // Wird in Info II nicht verwendet. Erstmal keine Hilfe erforderlich.
    private static final String LONG_HELP_PLAINDOT = null;        // Wird in Info II nicht verwendet. Erstmal keine Hilfe erforderlich.
    private static final String LONG_HELP_LATEX = null;           // Wird in Info II nicht verwendet. Erstmal keine Hilfe erforderlich.
    private static final String LONG_HELP_CALC = null;            // Wird in Info II nicht verwendet. Erstmal keine Hilfe erforderlich.
    private static final String LONG_HELP_META_PROPERTIES = null; // Eigentlich keine Hilfe erforderlich.
    
    /**
     * A placeholder to be used with the generic description of 2-3-4 and red-black trees.
     */
    private static final String TREE_PLACEHOLDER = "XXXTREEXXX";
    
    /**
     * Describes how search trees work in simple-mode.
     */
    private static final String SEARCHTREE_SIMPLE_LONG = ""
            + " The tree implementation possesses two operation modes."
            + " In " + italic("simple") + " mode you simply type in a sequence of elements that are separated by spaces."
            + " These elements are inserted one by one into an empty tree, so their order from left to right defines their insertion order into the tree."
            + " Simple mode is activated by prepending " + italic(AbstractTreeRepresentable.SIMPLE) + " to the prefix " + italic(TREE_PLACEHOLDER) + ".";

    /**
     * Disclaimer stating that the script language allows the definition of invalid search trees.
     */
    private static final String SEARCHTREE_SCRIPT_DISCLAIMER = "The tree can also be defined using a script language. Please note that although some basic sanity checks are performed,"
            + " the implementation does not check, whether the script defines a valid tree. (The implementation does, for example,"
            + " not yet check, whether the tree is balanced or if the order of elements is correct.)";
    
    /**
     * Describes how declarations of the search tree work.
     */
    private static final String SEARCHTREE_DECLARATIONS = ""
            + "You can either store integers, real numbers or strings in this tree. You can change the type of values in the declarations section"
            + "by setting the variable " + inlineScript("type") + " to a different value. Choose either " + inlineScript("integer") + " for integers, "
            + inlineScript("real") + " for real numbers, or " + inlineScript("string") + " for characters or strings.";
    
    
    private static final String LONG_HELP_TREE234 = ""
            + par("A " + def("2-3-4 tree") + " is a self-balancing data structure. Each node possesses either two, three or four children."
                    + " A two node has one data element, a three node two data elements and a four node three data elements."
                    + " All leaves possess the same depth. For further information see " + link("https://en.wikipedia.org/wiki/2%E2%80%933%E2%80%934_tree", "Wikipedia") + ".")
            + par(USAGE +
                    SEARCHTREE_SIMPLE_LONG.replaceAll(TREE_PLACEHOLDER, Tree234.TREE_PREFIX))
            + par(SEARCHTREE_SCRIPT_DISCLAIMER.replaceAll(TREE_PLACEHOLDER, Tree234.TREE_PREFIX) +
                    " A node is defined by writing all the values it contains separated by commas into brackets. A parent-child relationship between"
                    + " two brackets can then be established using the following mapping syntax: " + inlineScript("[p1] => [c1,c2];") + "."
                    + " It is possible to define multiple children in the same rule by separating children with pipes, i.e. "
                    + inlineScript("[p1] => [c1,c2]|[c3];"))
            + par(SEARCHTREE_DECLARATIONS.replaceAll(TREE_PLACEHOLDER, Tree234.TREE_PREFIX));
    
    private static final String LONG_HELP_REDBLACKTREE = ""
            + par("A " + def("red-black tree") + " is a self-balancing binary search tree. The self-balancing property of this tree type guarantees"
                    + " that searching, insertion, deletion (and rebalancing) is always performed in " + italic("O(log n)") + ", where " + italic("n")
                    + " is the size of the tree. A red-black tree distinguishes between red and black nodes (indicated by red dotted and black solid lines"
                    + " in the PDF). A red parent node may never have red child nodes."
                    + " For further information see " + link("https://en.wikipedia.org/wiki/Red%E2%80%93black_tree", "Wikipedia") + ".")
            + par(USAGE +
                    SEARCHTREE_SIMPLE_LONG.replaceAll(TREE_PLACEHOLDER, RedBlackTree.TREE_PREFIX))
            + par(SEARCHTREE_SCRIPT_DISCLAIMER.replaceAll(TREE_PLACEHOLDER, RedBlackTree.TREE_PREFIX) +
                    " Use the syntax " 
                    + inlineScript("p => c") + " to specify that 'p' is the parent of 'c'. p and c are placeholders for values that you want to store"
                    + " in the tree. To declare a node as red, prepend " + inlineScript("r:") + " to all of its occurrences in the script."
                    + "You can specify the left and the right children in one line by separating the values of the two children by a pipe, i.e."
                    + inlineScript("p => c1|c2"))
             + par(SEARCHTREE_DECLARATIONS.replaceAll(TREE_PLACEHOLDER, RedBlackTree.TREE_PREFIX));
    

    /* ****************** Help texts LONG/GER. ****************** */
    
    private static final String LONG_HELP_PATTREE_G = null;         // Wird in Info II nicht verwendet. Erstmal keine Hilfe erforderlich.
    private static final String LONG_HELP_PLAINDOT_G = null;        // Wird in Info II nicht verwendet. Erstmal keine Hilfe erforderlich.
    private static final String LONG_HELP_LATEX_G = null;           // Wird in Info II nicht verwendet. Erstmal keine Hilfe erforderlich.
    private static final String LONG_HELP_CALC_G = null;            // Wird in Info II nicht verwendet. Erstmal keine Hilfe erforderlich.
    private static final String LONG_HELP_META_PROPERTIES_G = null; // Eigentlich keine Hilfe erforderlich.
    
    private static final String SEARCHTREE_SIMPLE_LONG_G = ""
            + " Der Baum kann über zwei verschiedene Wege definiert werden."
            + " In der " + italic("simplen") + " Variante kannst du einfach ein Folge an Elementen eingeben, die durch Leerzeichen getrennt sind."
            + " Diese Elemente werden nacheinander in einen leeren Baum eingefügt, so dass er Anordnung von links nach rechts ihre Einfügereihenfolge in den Baum angibt."
            + " Der simple Modus wird aktiviert in dem " + italic(AbstractTreeRepresentable.SIMPLE) + " dem Präfix " + italic(TREE_PLACEHOLDER) + " vorangestellt wird.";
    
    private static final String SEARCHTREE_SCRIPT_DISCLAIMER_G = "Der Baum kann auch über eine Skriptsprache definiert werden. "
            + " Bitte berücksichtigte, dass die Implementierung nicht überprüft ob dein Skript einen korrekten Baum definiert,"
            + " obwohl eine rudimentäre Prüfung auf Korrektheit stattfindet. (Das Programm überprüft bspw. bisher noch nicht,"
            + " ob der Baum balanciert ist oder ob die Ordnung der Knoten korrekt ist.";
    
    private static final String SEARCHTREE_DECLARATIONS_G = ""
            + " Du kannst entweder ganze Zahlen, reelle Zahlen oder Zeichenketten in diesem Baum speichern."
            + " Du kannst den Typ der Werte, die gespeichert werden, über die Variable " + inlineScript("type") + " ändern."
            + " Wähle entweder " + inlineScript("integer") + " für ganze Zahlen, " + inlineScript("real") + " für reelle Zahlen, "
            + " oder " + inlineScript("string") + " für Buchstaben oder Zeichenketten.";

    private static final String LONG_HELP_TREE234_G = ""
            + par("Ein " + def("2-3-4-Baum") + " ist eine sich selbst balancierende Datenstruktur. Jeder Knoten besitzt entweder zwei, drei oder vier Kindknoten."
                    + " Ein Zwei-Knoten besitzt ein Datenelement, ein drei Knoten zwei Elemente und ein Vier-Knoten drei Elemente."
                    + " Alle Blätter besitzen die gleiche Tiefe. Für weitere Informationen siehe "
                    + link("https://de.wikipedia.org/wiki/2-3-4-Baum", "Wikipedia"))
                    + par(USAGE_G + SEARCHTREE_SIMPLE_LONG_G.replaceAll(TREE_PLACEHOLDER, Tree234.TREE_PREFIX))
                    + par(SEARCHTREE_SCRIPT_DISCLAIMER_G.replaceAll(TREE_PLACEHOLDER, Tree234.TREE_PREFIX)
                            + " Ein Knoten wir definiert, in dem all seine Werte in eckige Klammern geschrieben werden und durch Kommas getrennt werden."
                            + " Eine Eltern-Kind Beziehung zwischen zwei Knoten kann durch die folgende Syntax hergestellt werden: " + inlineScript("[p1] => [c1,c2];") + "."
                            + " Es ist möglich mehrere Kinder in der gleichen Regel zu definieren, in dem die Kinder durch einen senkrechten Strich getrennt werden, also "
                    + inlineScript("[p1] => [c1,c2]|[c3];"))
                    + par(SEARCHTREE_DECLARATIONS_G.replaceAll(TREE_PLACEHOLDER, Tree234.TREE_PREFIX));
    
    private static final String LONG_HELP_REDBLACKTREE_G = ""
            + par("Ein " + def("Rot-Schwarz-Baum") + " ist ein sich selbst balancierender binärer Suchbaum."
                    + " Die selbst-balancierende Eigenschaft dieses Baums garantiert, dass Such-, Einfüge-, Lösch- (und Rebalancierungsoperationen) immer"
                    + " in der Zeit " + italic("O(log n)") + " ausgeführt werden, wobei " + italic("n") + " der Größe des Baums entspricht."
                    + " Ein Rot-Schwarz-Baum unterscheidet zwischen roten und schwarzen Knoten (durch rote gepunktete und schwarze durchgezogene Linien in der PDF)."
                    + " Für weitere Informationen, siehe " + link("https://de.wikipedia.org/wiki/Rot-Schwarz-Baum", "Wikipedia") + ".")
                    + par(USAGE_G + SEARCHTREE_SIMPLE_LONG_G.replaceAll(TREE_PLACEHOLDER, RedBlackTree.TREE_PREFIX))
                    + par(SEARCHTREE_SCRIPT_DISCLAIMER_G.replaceAll(TREE_PLACEHOLDER, RedBlackTree.TREE_PREFIX) 
                            + " Nutze die die Syntax " + inlineScript("p => c") + " um festzulegen, dass 'p' Elternknoten von 'c' ist."
                            + " p und c sind Platzhalter für Werte, die du im Baum speichern möchtest."
                            + " Um einen Knoten als rot zu deklarieren, füge den Präfix " + inlineScript("r:") + " zu all seinen Vorkommnissen im Skript hinzu."
                            + " Du kann den linken und rechten Kindknoten in einer Zeile definieren, in dem du die Werte der beiden Kinder mit einem senkrechten"
                            + " Strich trennst, also " + inlineScript("p => c1|c2"))
                            + par(SEARCHTREE_DECLARATIONS_G.replaceAll(TREE_PLACEHOLDER, RedBlackTree.TREE_PREFIX));

    private static final String LONG_HELP_NUMBERS_G = "LONG_TEST_G"; // TODO

    private static final String LONG_HELP_CIRCUIT_G = "Logische Schaltkreise sind bisher nur in einer rudimentären Version implementiert und haben wenig Funktionalität. "
            + "Auch ist die Darstellung für komplexe Schaltkreise nicht schön - wir arbeiten daran.\r\n\r\n"
            + QUICK_HELP_CIRCUIT_PLAIN_G;

    private static final String LONG_HELP_BDD_G = ""
            + par(def("Binary decision diagrams (BDDs)") + " werden verwendet, um boolesche Funktionen in komprimierter Form darzustellen. "
                    + "Sie sind normalerweise effizienter als andere Darstellungsformen, wie z.B. Wahrheitstabellen oder Normalformen boolescher Ausdrücke "
                    + "(sowohl bezüglich des benötigten Speicherplatzes, als auch bezüglich der Berechnungszeit für ein gegebenes Tupel boolescher Eingabewerte). "
                    + "Im Gegensatz zu anderen Darstellungsformen können Operationen direkt mit der komprimierten Darstellung ausgeführt werden, "
                    + "also ohne sie vorher umwandeln zu müssen. "
                    + "BDDs werden aufsteigend von den Blättern zur Wurzel hin konstruiert, wie " + link("https://de.wikipedia.org/wiki/Bin%C3%A4res_Entscheidungsdiagramm", "hier") + " beschrieben.")
            + par(USAGE_G
                    + itemize(BDD_QUICK_HELP_PLAIN_G.replace("\r\n", "</li><li>"),
                          "Wenn zu einem Skript Deklarationen hinzugefügt werden " + HOW_TO_ADD_DECLARATIONS_G 
                              + ", kann die Anzahl der Konstruktionsschritte mittels der Variablen " 
                              + inlineScript("simplifySteps") + " festgelegt werden (" + inlineScript("-1") + " um alle Schritte auszuführen).",
                          "Klicke auf " + button("Vereinfache schrittweise") + ", um den Wahrheitsbaum schrittweise zu vereinfachen. "
                              + "Dadurch wird der Wert der Variablen " + inlineScript("simplifySteps")
                              + ", wie oben beschrieben, um eins erhöht (alternativ kann das also auch von Hand gemacht werden).",
                          "Durch Klicken auf " + button("Wahrheitstabelle...") 
                          + " wird die Wahrheitstabelle generiert, die dem BDD zugrunde liegt."))
            + example_G("bdd: a,b,c: 01101010\n" + 
                    "--declarations--\n" + 
                    "simplifySteps=-1\n" + 
                    "--declarations-end--");

    private static final String LONG_HELP_FSM_G = ""
            + par("Ein " + def("Endlicher Automat") + " (" + def("EA") + "; auch " + def("Zustandsmaschine") + " oder " + def("Zustandsautomat") + ", engl. " + def("Finite State Machine") + "), "
                    + "ist ein mathematisches Berechnungsmodell. Neben seiner Anwendung in der Theoretischen Informatik wird er in der Praxis beim Entwurf "
                    + "sowohl von Computerprogrammen als auch von logischen Schaltkreisen verwendet. "
                    + "Ein EA kann als abstrakte Maschine begriffen werden, die sich zu jedem gegebenen Zeitpunkt in einem bestimmten "
                    + "Zustand befindet (bezeichnet als " + def("momentaner Zustand") + "), der zu einer endlichen Menge " + def("möglicher Zustände") + " gehört. "
                    + "Der Übergang von einem Zustand in einen anderen wird " + def("Transition") + " genannt und "
                    + "hängt von einer auslösenden Bedingung ab. Wir geben diese Bedingung durch ein Zeichen "
                    + "aus einem Eingabealphabet an. "
                    + "Ein bestimmter EA wird definiert durch eine Liste seiner Zustände und die auslösende Bedingung für jede Transition.")
              + par("Es ist zu beachten, dass der " + italic("momentane Gesamtzustand") + " eines " + def("nichtdeterministischen") + " EA auch aus mehreren "
                    + "Zuständen oder aus überhaupt keinem bestehen kann. "
                    + "Grundsätzlich bleibt jedoch die obige Beschreibung gültig, da jede Teilmenge von Zuständen eines nichtdeterministischen EA "
                    + "als einzelner Zustand eines äquivalenten deterministischen EA betrachtet werden kann. "
                    + "(Vgl. die " + link("https://de.wikipedia.org/wiki/Potenzmengenkonstruktion", "Potenzmengenkonstruktion") 
                    + ", um einen nichtdeterministischen EA in einen deterministischen umzuwandeln.)")
              + par("Das Verhalten von EAs bildet viele reale Anwendungsfälle ab, in denen Maschinen vordefinierte Sequenzen "
                    + "von Aktionen ausführen, die von einer Sequenz von zugehörigen Ereignissen abhängen. "
                    + "Einfache Beispiele hierfür sind Getränkeautomaten, die Waren ausgeben, nachdem eine ausreichende Zahl an Münzen eingeworfen wurde, "
                    + "Aufzüge, die Passagiere in höheren Stockwerken absetzen, bevor sie nach unten fahren, "
                    + "Ampeln, die ihre Schaltung ändern, wenn Autos warten, "
                    + "und Zahlenschlösser, bei denen die richtige Kombination in der richtigen Reihenfolge eingegeben werden muss. "
                    + "Weitere Einzelheiten bezüglich Endlicher Automaten können " + link("https://de.wikipedia.org/wiki/Endlicher_Automat", "hier") + " nachgelesen werden.")
              + par(USAGE_G
                    + itemize("Ein Skript wird mit " + inlineScript("fsm:") + "  (für Finite State Machine) begonnen, um anzuzeigen, dass es sich um einen EA handelt.",
                          "Eine Liste aller Transitionen definiert das Verhalten des Automaten, "
                                  + "jede einzelne von der Form: " + inlineScript("(s0, a) => s1;") 
                                  + " (Bedeutung: Übergang von Zustand " + inlineScript("s0") + " in Zustand " + inlineScript("s1") 
                                  + ", unter der Bedingung, dass " + inlineScript("a") + " gelesen wird).",
                          "Die Liste aller Zustände, sowie die Symbole des Eingabealphabets werden nicht explizit angegeben. "
                                  + "Wird eine Transition gelesen, wird automatisch angenommen, dass die gegebenen Zustände (" 
                                  + inlineScript("s0, s1") + " im obigen Beispiel) "
                                  + "sowie die Eingabesymbole (" + inlineScript("a") + " im obigen Beispiel) Teil des EA sind.",
                          "Der Anfangszustand (" + inlineScript("s0") + ") und die Liste der Endzustände (" + inlineScript("F") + ") "
                                  + "müssen im Deklarations-Bereich definiert werden " + HOW_TO_ADD_DECLARATIONS_G + ".",
                          "Durch klicken auf " + button("Simuliere einen Schritt") + ", kann ein Eingabewort angegeben werden, für das das Verhalten des EA schrittweise durch wiederholtes Klicken auf denselben Button simuliert wird "
                                  + "(dasselbe kann man erreichen, indem man ein " + inlineScript("input") + "-Wort und einen " 
                                  + inlineScript("simulateToStep") + "-Wert im Deklarations-Bereich angibt).",
                          "Wird ein deterministischer EA definiert, erzeugt ein Klick auf " + button("Minimiere") + " einen äquivalenten minimalen EA.",
                          "Ein Klick auf " + button("Zeige Minimierungstabelle") + " zeigt die entsprechende Minimierungstabelle an (auch nur im deterministischen Fall).",
                          "Wird ein nichtdeterministischer EA definiert, erzeugt ein Klick auf " + button("Mache deterministisch") + " einen äquivalenten deterministischen EA.",
                          "Für jeden EA können durch einen Klick auf " + button("Zeige Minimierungs-Ablauf") + " Informationen zum Determinierungs- und Minimierungsprozess angezeigt werden.",
                          "Der EA kann durch Klick auf den entsprechenden Button in einen äquivalenten Kellerautomaten konvertiert werden (" + button("Kellerautomat") + "), "
                                  + "in eine Turingmaschine (" + button("Turingmaschine") + "), in eine rechtslineare Grammatik (" + button("Rechtslineare Grammatik") + ") oder in einen Regulären Ausdruck (" + button("Regulärer Ausdruck") + ")."))
              + example_G("fsm:\n" + 
                    "(s0, a) | (s3, a) | (s4, a) => s2;\n" + 
                    "(s0, b) | (s3, b) => s1;\n" + 
                    "(s1, a) => s0;\n" + 
                    "(s1, b) | (s2, a) => s4;\n" + 
                    "(s2, b) | (s4, b) => s3;\n" + 
                    "--declarations--\n" + 
                    "simulateToStep=0;\n" + 
                    "input=bbbbbaabba;\n" + 
                    "s0=s0;\n" + 
                    "F=s0\n" + 
                    "--declarations-end--");
    
    private static final String LONG_HELP_GRAMMAR_G = ""
            + par("In der Theorie der formalen Sprachen (die sowohl in der theoretischen Informatik als auch in der Linguistik betrachtet wird), "
                    + "versteht man unter einer " + def("Chomsky Grammatik") + ", " + def("formalen Grammatik") + " oder einfach " + def("Grammatik") 
                    + " im Grunde eine Menge von Produktionsregeln für Zeichenketten, d.h., Symbolsequenzen. "
                    + "Die Regeln beschreiben, wie über einem gegebenen Alphabet " + def("gültige") + " Zeichenketten abgeleitet werden können, "
                    + "in Unterscheidung zur Menge der übrigen, nicht ableitbaren Zeichenketten, die als " + def("ungültig") + " bezeichnet werden. "
                    + "Die Menge gültiger Zeichenketten heißt " + def("(formale) Sprache") + " der Grammatik. "
                    + "Grammatiken werden in der Regel als " + def("erzeugende Systeme für Sprachen") + " bezeichnet, weil sie "
                    + "definieren, wie aus einer Anfangszeichenkette die gültigen Zeichenketten abgeleitet bzw. bildhaft 'produziert' werden. ")
            + par(bold("Wort-Erzeugung:") + " Die Produktionen einer Grammatik definieren eine Menge von modifizierenden Operationen auf Zeichenketten, wie etwa "
                    + inlineScript("Zeichenkette => andere-Zeichenkette") + ", "
                    + "in Verbindung mit einem " + def("Startsymbol S") + ". Beginnend mit der Menge {S} "
                    + "(die nur das Startsymbol enthält), werden zu dieser Menge alle Zeichenketten hinzugefügt, die erzeugt werden können, "
                    + "indem bei einer bereits enthaltenen Zeichenkette ein Teil, der der linken Seite einer "
                    + "Produktion entspricht, durch deren rechten Teil ersetzt wird. Die Sprache der Grammatik ist definiert als diejenige Teilmenge "
                    + "dieser konstruierten Menge, deren Zeichenketten nur " + def("terminale") + " Wörter darstellen, d.h. Wörter, "
                    + "die nur Zeichen des zugrundeliegenden Alphabets enthalten. Dieses wird als " 
                    + def("Menge T der Terminalsymbole") + " bezeichnet. "
                    + "Die übrigen Symbole werden als " + def("Menge N der Nichtterminalsymbole") + " bezeichnet. "
            + par("Grammatiken können auch als Basis für " + def("Erkenner") + " verwendet werden, d.h. Programme, die feststellen, "
                    + "ob eine gegebene Zeichenkette zur Sprache einer Grammatik gehört oder nicht. "
                    + "Zu diesem Zweck wird die Grammatik von einem " + def("Automaten") + " verarbeitet, der eigentlich "
                    + "zu den " + def("erkennenden") + " Formalismen in Bezug auf formale Sprachen gehört (in der Praxis handelt es sich um ein Computerprogramm, "
                    + "was wir in der Theorie aber durch einen Automaten modellieren). Wichtig ist hier zu verstehen, dass der Automat nicht "
                    + "zum Erkennen der jeweiligen Sprache einer bestimmten Grammatik konstruiert wird. Vielmehr handelt es sich um einen allgemeinen Automaten, der "
                    + "sowohl die Grammatik als auch das zu prüfende Wort einliest und daraus die Antwort auf die Frage berechnet, ob "
                    + "das Wort in der Sprache der Grammatik ist oder nicht. "
                    + "Zusätzlich zu diesem bloßen Erkennungsprozess kann eine Grammatik auch dazu verwendet werden, eine gegebene Zeichenkette "
                    + "zu " + def("parsen") + ", "
                    + "falls sie zur Sprache der Grammatik gehört. Das Parsen liefert Informationen dazu, wie die Zeichenkette "
                    + "in Bezug zur Grammatik strukturiert ist, wie sie also durch die Grammatik abgeleitet wird. "
                    + "Grammatiken vom Chomsky-Typ 2 können effizienet geparst werden "
                    + "(z.B. mittels eines " + link("https://de.wikipedia.org/wiki/Earley-Algorithmus", "Earley-Parsers") 
                    + ", der auch vom " + XWIZZ_HTML_G + " genutzt wird). Als Ergebnis ergibt sich ein " + link("https://de.wikipedia.org/wiki/Syntaxbaum", "Syntaxbaum") + " ("
                    + "auch " + def("Parsebaum") + " "
                    + "oder " + def("Ableitungsbaum") + "). "
                    + "Aufgrund dieser Eigenschaften sind Grammatiken des Typs 2 besonders als Basis für Programmiersprachen geeignet. Ein 'Wort' ist in diesem "
                    + "Zusammenhang ein zu kompilierendes Programm in der jeweiligen Sprache (z.B. Java). Da zu Java (und praktisch jeder anderen modernen Programmiersprache) "
                    + "eine Grammatik gehört, kann durch einen Parser "
                    + "zunächst geprüft werden, ob das Wort ein gültiges Programm darstellt (Erkennung). Sowohl im positiven Fall als auch im negativen können dann durch die "
                    + "Parsing-Informationen weitergehende Informationen über die Struktur des Programms abgeleitet werden. (Zum gesamten Kompilierungsprozess gehören allerdings "
                    + "noch weitere Komponenten, z.B. Lexer, Tokenizer usw.) "
                    + "Weitere Einzelheiten bezüglich Grammatiken können "
                    + link("https://de.wikipedia.org/wiki/Formale_Grammatik", "hier") + " nachgelesen werden.")
                    )
            + par(USAGE_G
                  + itemize("Ein Skript wird mit " + inlineScript("grammar:") + " begonnen, um anzuzeigen, dass es sich um eine formale Grammatik handelt. "
                              + "Es wird zunächst ein " + def("Gesamtbaum") + " dargestellt, d.h. alle Wörter, die von "
                              + inlineScript("S") + " ausgehend bis zu einer bestimmten Tiefe erzeugt werden können. Durch Hinzufügen von " 
                              + inlineScript("parse(a, a, b, b, a, b)--0") + " zur Preambel (direkt vor dem Doppelpunkt), "
                              + "wird der Parse-Modus aktiviert, der den (bzw. einen) " + def("Syntaxbaum") + " für das angegebene Wort "
                              + inlineScript("a, a, b, b, a, b") + " zeigt.",
                          "Um zwischen den beiden Modi zu wechseln, können auch die Buttons " + button("Gesamtbaum") + " und "
                              + button("Parse einzelnes Wort") + " verwendet werden.",
                          "Falls ein Wort auf verschiedene Arten geparst werden kann, kann zwischen den jeweiligen Syntaxbäumen durch "
                              + "Klicken auf den Button " + button("Durchlaufe...") + " gewechselt werden oder indem man die 0 in " 
                              + inlineScript("--0") + " durch eine andere Zahl ersetzt.",
                          "Es ist zu beachten, dass " + bold("alle") + " Syntaxbäume berechnet werden, nicht nur die, deren Wurzel das Startsymbol ist. "
                              + "Nur die letzteren stellen allerdings gültige Ableitungen des Wortes durch die Grammatik dar.",
                          "Die Produktionen der Grammatik werden in der Form " + inlineScript("A, S => b, A, A, c") + " angegeben.",
                          "In Grammatik-Skripten kann ein Symbol aus mehreren Zeichen bestehen, "
                              + "daher werden Symbole durch Kommas voneinander getrennt. ",
                          "Sowohl die linke als auch die rechte Seite einer Produktionsregel kann "
                              + "aus mehr als einem Symbol bestehen (vgl. " + def("Chomsky-Hierarchie") + ").",
                          "Nach Konvention werden Großbuchstaben in der Regel für Nonterminalsymbole verwendet, Kleinbuchstaben für Terminalsymbole. "
                              + "Trotzdem können sowohl Nonterminal- als auch Terminalsymbole und das Startsymbol explizit im Deklarations-Bereich als "
                              + inlineScript("N, T, S") + " " + HOW_TO_ADD_DECLARATIONS_G 
                              + " angegeben werden (auch entgegen der beschriebenen Konvention, falls gewünscht). ",
                          "Im regulären Modus können die Variablen " + inlineScript("maxdepth, maxLengthWords, cutNonTerminalBranches, cutTerminalDoubleBranches")
                                  + " verwendet werden, um die Größe des erzeugten Baumes zu beschränken. Die beiden ersten legen eine maximale Baum-Tiefe und Wortlänge fest. "
                                  + "Die beiden letzten verbergen jeweils alle Zweige zu nichtterminalen Wörtern und zu terminalen Wörtern, die bereits durch einen anderen Zweig erzeugt wurden.",
                          "Durch Konversionsmethoden kann die Grammatik  " + button("Epsilon-frei") 
                                  + "gemacht, in " + button("Chomsky NF") + ", " + button("Greibach NF") + " oder (bei Typ-1-Grammatiken) " + button("Kuroda NF") + " überführt, "
                                  + "oder durch den Button " + button("Randomize") + " durch eine neue, zufällig generierte (Typ-2-) Grammatik ersetzt werden.",
                          "Ein äquivalenter Kellerautomat kann erstellt werden, indem auf " + button("Kellerautomat") + " geklickt wird.",
                          "Durch Klicken auf " + button("Anzeigemodus") + " können verschiedene Sichten auf die Grammatik angezeigt werden. "
                                  + "Dazu gehört die Grammatikdefinition, ein Ableitungsbaum und die Ableitungen eines zu parsenden Wortes.",
                          "Wird " + inlineScript("multiLetterSymbolsHaveIndex=true") + " gesetzt, werden Symbole mit mehr als einem Zeichen so angezeigt, "
                                  + "dass alle Zeichen nach dem Ersten tiefergestellt sind."
                                  )
                  + bold("Es ist zu beachten, dass der Parse-Modus und die meisten Konversionsmethoden eine Grammatik vom Typ 2 erfordern. ") 
                      + "Es existieren keine Syntaxbäume für Grammatiken, die nicht vom Typ 2 sind, und die Entscheidung, ob ein Wort zur Sprache einer solchen Grammatik gehört, "
                      + "ist mindestens " + math("NP") + "-schwer; "
                      + "Auch die Chomsky- und Greibach-Normalformen gibt es nicht bei Nicht-Typ-2-Grammatiken, und sie epsilon-frei zu machen ist schwieriger. In der Zukunft werden "
                      + "vielleicht weitere Konversionsmethoden für Nicht-Typ-2-Grammatiken implementiert. Die Kuroda-Normalform ist ein erster Anfang.")
            + example_G("grammar parse(a, a, b, b, a, b)--0:\n" + 
                    "A => D, D, a, A | A, B, b, B | A, b, D | B, a;\n" + 
                    "B => S, a | A, a, b;\n" + 
                    "D => b;\n" + 
                    "S => A, B, b | S, b | a, S | b | S, a;\n" + 
                    "--declarations--\n" + 
                    "N=S,A,B,C,D;\n" + 
                    "T=a,b;\n" + 
                    "S=S;\n" + 
                    "multiLetterSymbolsHaveIndex=true;\n" + 
                    "maxdepth=5;\n" + 
                    "cutNonTerminalBranches=true;\n" + 
                    "cutTerminalDoubleBranches=true;\n" + 
                    "maxLengthWords=10\n" + 
                    "--declarations-end--");
    
    private static final String LONG_HELP_HUFFMAN_G = ""
            + par("In der Informatik und der Informationstheorie ist eine " + def("Huffman-Kodierung") + " ein bestimmter optimaler " 
                    + def("Präfix-Code") + ", der häufig für verlustfreie Datenkompression verwendet wird. "
                    + "Die Huffman-Kodierung ordnet jedem zu kodierenden Zeichen ein Bit-Wort zu, so dass der resultierende Code die "
                    + link("https://de.wikipedia.org/wiki/Fano-Bedingung", "Fano-Bedingung")
                    + " erfüllt (kein Codewort ist das Präfix eines anderen) und für eine gegebene Wahrscheinlichkeitsverteilung der Zeichen eine minimale Größe aufweist. "
                    + "Die Vorgehensweise, um einen solchen Code zu erstellen und zu verwenden, geht auf einen Algorithmus zurück, den David A. Huffman "
                    + "während seiner Zeit als Doktorand am MIT entwickelt hat. "
                    + "Weitere Einzelheiten über die Huffman-Kodierung können "
                    + link("https://de.wikipedia.org/wiki/Huffman-Kodierung", "hier") 
                    + " nachgelesen werden.")
            + par(USAGE_G
                  + itemize(HUFFMAN_QUICK_HELP_PLAIN_G.replace(". ", ".</li><li>"),
                          "Die Ausgabe besteht aus einem " + bold("Huffman-Baum") + ". Ausgehend von der Wurzel bis zu den einzelnen Blättern stellt die "
                                  + "Folge der Markierungen der besuchten Kanten das Codewort für das kodierte Zeichen (im jeweiligen Blatt) dar "
                                  + "(die obere linke Ecke des jeweiligen Blattes enthält dieses Zeichen; "
                                  + "die Auftrittswahrscheinlichkeit für einen gegebenen Text wird in der oberen rechten Ecke angezeigt, "
                                  + "die Kodierung des Zeichens unten).",
                          "Es ist zu beachten, dass der Huffman-Baum in der Regel nicht eindeutig ist; inbesondere die Bezeichnung der Kanten mit " + math("1") 
                                  + " wenn sie von rechts nach links gezeichnet werden und mit " + math("0") + " sonst "
                                  + "(so wie es der " + XWIZZ_HTML_G + " macht) ist völlig willkürlich. "
                                  + "Solange jeder Vater-Knoten (der kein Blatt ist) eine " + math("0") + " und eine " + math("1") + " Kante "
                                  + "zu einem entsprechenden Kind-Knoten hat, sind alle Kombinationen erlaubt.",
                          "Mit der booleschen Variablen " + inlineScript("classicView") + " " + HOW_TO_ADD_DECLARATIONS_G + " kann zwischen verschiedenen Ansichten gewechselt werden."))
            + example_G("huff:huffman-example\n" + 
                    "--declarations--\n" + 
                    "classicView=false\n" + 
                    "--declarations-end--");
    
    private static final String LONG_HELP_PDA_G = ""
            + par("In der Informatik versteht man unter einem " + def("Kellerautomaten (KA)") + " ein Berechnungsmodell, "
                    + "das ein Band verwendet (vgl. Turingmaschine), auf das nur im 'Last-In-First-Out' Verfahren zugegriffen werden kann. Dieses wird als "
                    + link("https://de.wikipedia.org/wiki/Stapelspeicher", "Stapelspeicher") + " oder " + def("Keller") + " bezeichnet. "
                    //+ "The term " + italic("pushdown") + " refers to the image that the stack is being 'pushed down' "
                    //+ "like a tray dispenser at a cafeteria, since the "
                    //+ "operations work on the top element only, and the other elements remain untouched until reaching the top.")
                    + "Die Bezeichnung " + italic("Stapelspeicher") + " (statt Keller) illustriert, wie neue Elemente im Speicher immer oben auf die "
                    + "bereits enthaltenen 'gestapelt' werden. Für jede Operation auf dem Speicher ist entsprechend auch immer nur das oberste Element verfügbar, es kann "
                    + "keines von unten 'hervorgezogen' werden, man muss sie Stück für Stück wieder freilegen.")
            + par("Was die Berechnungsmächtigkeit angeht, sind KA leistungsfähiger als " + def("endliche Automaten") + ", aber weniger leistungsfähig als "
                    + def("Turingmaschinen") + ". "
                    + "Genauer gesagt sind deterministische KA in der Lage, alle deterministischen kontextfreien Sprachen zu erkennen, "
                    + "während nichtdeterministische KA alle kontextfreien Sprachen erkennen können. "
                    + "Hauptsächlich erstere werden häufig beim Entwurf von " + link("https://de.wikipedia.org/wiki/Parser", "Parsern") + " verwendet. "
                    + "Weitere Einzelheiten über Kellerautomaten können " + link("https://de.wikipedia.org/wiki/Kellerautomat", "hier") 
                    + " nachgelesen werden.")
            + par("Ein KA arbeitet ein Eingabewort von links nach rechts ab und wechselt dabei zwischen einer endlichen Anzahl von "
                    + def("Zuständen") + ". Darüber hinaus kann der KA in jedem Schritt eine beliebige Menge von Symbolen "
                    + "im Keller ablegen (" + def("push") + "); von diesen Symbolen kann in jedem Schritt nur das oberste gelesen und entfernt werden (" 
                    + def("pop") + "). Entsprechend wird durch eine KA-" + def("Transition") + " ein Tupel aus Zustand, "
                    + "Eingabesymbol (gelesen vom Eingabeband) und Kellersymbol (oberstes Symbol des Kellerspeichers, 'pop') "
                    + "abgebildet auf einen Folgezustand und eine Sequenz von Symbolen, die in den Keller geschrieben werden ('push'). "
                    + "Eine Eingabe wird vom KA " + def("akzeptiert") + ", falls er sich in einem Endzustand befindet, nachdem die komplette Eingabe verarbeitet wurde.")
            + par(USAGE_G
                  + itemize(
                        "Ein Skript wird mit " + inlineScript("pda:") + " begonnen (für engl. " + def("push-down automaton") + "), um anzuzeigen, dass es sich um einen KA handelt.",
                        "Das Verhalten des KA wird definiert durch eine Liste von Transitionen, von denen jede einzelne definiert ist als: "
                          + inlineScript("(s0, a, b) => (s1, ab);")
                          + " (mit der Bedeutung: Wenn in Zustand " + inlineScript("s0") + " " + inlineScript("a") 
                          + " vom Eingabeband gelesen wird und " + inlineScript("b") + " vom Kellerspeicher, dann wechsle in Zustand " 
                          + inlineScript("s1") + " und schreibe " + inlineScript("ab") + " in den Keller; da " 
                          + inlineScript("b") + " vorher aus dem Keller entnommen wurde ('pop'), bedeutet das in diesem Fall, dass " 
                          + inlineScript("b") + " wieder zurück gelegt wird und " + inlineScript("a") + " darauf, sodass nun ein Zeichen mehr im Keller liegt, nämlich " + inlineScript("a") 
                          + ", und zwar ganz oben).",
                        "Die Ausgabe zu einem KA-Skript besteht aus einer Sequenz (bzw. im nicht-deterministischen Fall aus einem Baum), worin "
                          + "die Berechnungsschritte für ein gegebenes Eingabewort dargestellt werden.",
                        "Das Eingabewort (im deterministischen Fall sind auch mehrere durch Komma getrennte erlaubt) wird im Deklarations-Bereich als "
                          + inlineScript("inputs") + " spezifiziert " + HOW_TO_ADD_DECLARATIONS_G + ".",
                        "Der Anfangszustand " + inlineScript("s0") + ", "
                          + "die Menge der Endzustände " + inlineScript("F") + " und "
                          + "das spezielle Kellersymbol (das den 'Boden' des Kellers markiert) " + inlineScript("kSymb") + " "
                          + "werden ebenfalls im Deklarations-Bereich festgelegt.",
                        "Durch klicken auf " + button("Zeige Definition (Latex)") + " wird der Latex-Modus aktiviert, der die Definition des KA anzeigt.",
                        "Für einen deterministischen KA kann mit dem Button " + button("Zeige Berechnungsschritte (Latex)") + " eine übersichtlichere Darstellung "
                          + "der Berechnungsfolge im Latex-Modus angezeigt werden."))
            + example_G("pda:\n" + 
                    "(s0, a, k) => (s4, k);\n" + 
                    "(s0, b, k) => (s3, k) | (s1, k) | (s0, k);\n" + 
                    "(s2, a, k) => (s1, k);\n" + 
                    "(s2, b, k) => (s1, k) | (s0, k);\n" + 
                    "(s3, a, k) => (s3, k) | (s1, k);\n" + 
                    "(s3, b, k) => (s4, k) | (s1, k) | (s2, k) | (s0, k) | (s3, k);\n" + 
                    "(s4, b, k) => (s0, k);\n" + 
                    "--declarations--\n" + 
                    "s0=s0;\n" + 
                    "F=s0,s2;\n" + 
                    "kSymb=k;\n" + 
                    "inputs=abbab\n" + 
                    "--declarations-end--");
    
    private static final String LONG_HELP_TURING_G = ""
            + par("Der Begriff " + def("Turingmaschine (TM)") + " bezeichnet ein wesentliches Automatenmodell der theoretischen Informatik. "
                    + "Es handelt sich um eine hypothetische Maschine, die, nach Regeln aus einer vorgegebenen " + def("Menge von Transitionen") + ", "
                    + "Symbole auf einem eindimensionalen Band lesen und editieren kann. "
                    + "Von der Idee her stellt sie einen erweiterten " + def("endlichen Automaten") + " dar, mit den folgenden Unterschieden:"
                    + itemize("Das Band ist sowohl nach links als auch nach rechts unbegrenzt (Ausnahmen stellen "
                            + "beschränkte Turingmaschinen dar, von denen die wichtigsten " + def("einseitig") 
                            + " und " + def("linear") + " beschränkte Turingmaschinen sind). "
                            + "Es enthält zu Beginn das Eingabewort, das von einer unendlichen Folge von " + def("Leerzeichen") + " " + inlineScript("*") 
                            + " auf jeder Seite umgeben ist. Beispiel: " + inlineScript("...****abaabaabb****..."),
                            "Der Kopf kann die Symbole auf dem Band einlesen und verändern, d.h. ein gelesenes Symbol durch ein anderes ersetzen. Er wird daher als "
                            + def("Lese-/Schreibkopf" + " bezeichnet."),
                            "Der Lese-/Schreibkopf kann sich in beide Richtungen bewegen (jew. ein Feld pro Berechnungsschritt) oder seine Position beibehalten.",
                            "Die Transitionsfunktion ist nicht total, sondern partiell definiert, d.h. dass es Zustände geben kann, bei denen für bestimmte Bandsymbole kein Folgezustand definiert ist. "
                            + "In diesem Fall (und nur dann) " + def("hält") + " die TM.")
                    + "Die TM wird als Formalismus zur Definition von " + def("berechenbaren Funktionen") + " genutzt. "
                    + "Dabei nennt man den Prozess, den eine TM durch Transitionswechsel bis zum (potentiellen) Halten "
                    + "durchläuft, " + def("Rechnung") + ". Die von der TM " + def("berechnete Funktion") 
                    + " ist definiert als die Abbildung von dem zu Beginn der Rechnung auf dem Band stehenden Eingabewort"
                    + " auf das Wort, das nach dem Anhalten auf dem Band steht, d.h. das Wort, das während des Berechnungsprozesses generiert wurde. "
                    + "Eine Funktion wird genau dann als " + def("(Turing-) berechenbar") + " bezeichnet, "
                    + "wenn es eine TM gibt, die sie berechnet. "
                    + "Darüber hinaus bezeichnet man all jene Eingabewörter als von einer TM " + def("akzeptiert") + ", bei deren Eingabe sie in einem " + def("Endzustand") + " anhält. "
                    + "Die Menge dieser Wörter wird als " + def("Sprache") + " der TM bezeichnet. "
                    + "Da nicht alle Turingmaschinen für alle möglichen Eingabewörter anhalten, "
                    + "können die von ihnen definierten Funktionen partiell sein. "
                    + "Trotz ihres einfachen Aufbaus können Turingmaschinen jedes Programm, das auf irgendeinem üblichen "
                    + "Computer läuft, simulieren (also dieselbe Funktion berechnen wie das Programm). "
            + par("Eine TM ist " + def("nichtdeterministisch") + ", wenn für einen Zustand und ein bestimmtes Eingabesymbol mehr als eine mögliche Transition definiert ist. "
                    + "In diesem Fall führt die TM alle Möglichkeiten parallel aus, was zu einem Berechnungsbaum führt "
                    + "(statt einer Berechnungsfolge, wie im deterministischen Fall). "
                    + "Eine nichtdeterministische TM akzeptiert alle Eingabewörter, die zu einem Berechnungsbaum führen, bei dem " 
                    + def("mindestens ein Ast") + " in einen Endzustand mündet. Zu beachten ist, dass nichtdeterministische "
                    + "Turingmaschinen ein rein theoretisches Konzept sind, das in der realen Welt nicht realisiert werden kann. "
                    + "Weitere Einzelheiten bezüglich Turingmaschinen können " + link("https://de.wikipedia.org/wiki/Turingmaschine", "hier") + " nachgelesen werden."))
            + par(USAGE_G
                  + itemize("Ein Skript wird mit " + inlineScript("turing:") + " begonnen, um anzuzeigen, dass es sich um eine TM handelt.",
                        "Anstatt eine Tabelle zu verwenden, erwartet der " + XWIZZ_HTML_G + " eine Liste von Transitionen der Form: " 
                                + inlineScript("(s0, a) => (s1, b, R);")
                                + " (Bedeutung: Wenn in Zustand " + inlineScript("s0") + " das Zeichen " + inlineScript("a") + " vom Band gelesen wird, wechsle in Zustand " + inlineScript("s1") + ", "
                                + "schreibe das Zeichen " + inlineScript("b") + " an die momentane Position des Lese-/Schreibkopfes auf dem Band "
                                + "und bewege den Lese-/Schreibkopf einen Schritt nach rechts; auch erlaubt: " + inlineScript("L") + " für links "
                                + "und " + inlineScript("N") + " für neutral, d.h. keine Änderung der Position).",
                        "Die Liste der Zustände sowie die Symbole des Eingabe- und des Bandalphabets werden nicht explizit angegeben. "
                                + "Beim Einlesen einer Transition wird angenommen, dass die angegebenen Zustände (" + inlineScript("s0, s1") + " im obigen Beispiel) "
                                + "sowie die Bandsymbole (" + inlineScript("a, b") + " im obigen Beispiel) zur TM gehören.",
                        "Der Anfangszustand (" + inlineScript("s0") + "), die Liste der Endzustände (" + inlineScript("F") + ") und das Leerzeichen (" + inlineScript("*") 
                                + ") müssen im Deklarations-Bereich definiert werden " + HOW_TO_ADD_DECLARATIONS_G + ".",
                        "Die Ausgabe einer TM besteht aus einer Folge von Bandzuständen, die den Verlauf der Berechnung für ein gegebenes Eingabewort repräsentieren. "
                                + "Dieses Eingabewort muss ebenfalls im Deklarations-Bereich angegeben werden. Zu diesem Zweck kann die Variable " + inlineScript("inputs") 
                                + " mit einer (oder mehreren durch Kommas getrennten) Zeichenkette(n) belegt werden. Im Falle mehrerer Eingabewörter werden mehrere Berechnungsfolgen "
                                + "nebeneinander angezeigt. "
                                + "Es ist zu beachten, dass für nichtdeterministische TM nur ein Eingabewort erlaubt ist "
                                + "(der Übersichtlichkeit halber, da die Ausgabe dann ein Berechnungsbaum ist).",
                        "Da Turingmaschinen theoretisch unendlich lange laufen können, legt die Variable " + inlineScript("runStepsScript") + " eine Obergrenze dafür fest, "
                                + "wie viele Berechnungsschritte simuliert werden sollen.",
                        "Wird die Variable " + inlineScript("shortTrace") + " auf 'true' gesetzt, verbirgt der " + XWIZZ_HTML_G + " Berechnungsschritte, "
                                + "die keine Bandänderung zur Folge haben. ",
                        "Neben der Standardausgabe stehen für ein TM-Skript Methoden zur Konversion in Latex Skripte zur Verfügung. "
                                + "So kann die Transitionstafel oder eine (anschaulichere) Darstellung der Berechnungsfolge angezeigt werden.",
                        "Außerdem kann durch Klicken auf " + button("Zufälliger Busy-Beaver") + " eine zufällige TM erstellt werden. "
                                + "In Abhängigkeit von einer anzugebenden Anzahl " + math("|S|") 
                                + " an Zuständen und einer ebenfalls anzugebenden Anzahl " + math("t") + " an Wiederholungen erstellt der Algorithmus "
                                + "" + math("t") + " zufällige Turingmaschinen mit jew. " + math("|S|") + " Zuständen bei zugehörigem Eingabewort " 
                                + inlineScript("*") + ". Das ausgegebene Skript ist das für diejenige der " + math("t") + " Turingmaschinen, "
                                + "deren berechnetes Ausgabe-Wort die meisten '" + inlineScript("1") + "en' enthält, "
                                + "d.h. die 'fleißigste' aller erstellen Maschinen. Vgl. die Definition des " 
                                + link("https://de.wikipedia.org/wiki/Flei%C3%9Figer_Biber", "Fleißigen Bibers") + "."))
            + example_G("turing:\n" + 
                    "(s4, 1) => (s1, 1, L);\n" + 
                    "(s2, 1) => (s4, *, L);\n" + 
                    "(s3, *) => (s2, *, R);\n" + 
                    "(s4, *) => (s3, *, L);\n" + 
                    "(s1, *) => (s4, 1, L);\n" + 
                    "(s2, *) => (s0, 1, L);\n" + 
                    "(s0, *) => (s1, 1, R);\n" + 
                    "--declarations--\n" + 
                    "s0=s0;\n" + 
                    "F=s3,s4,s0,s1,s2,;\n" + 
                    "blank=*;\n" + 
                    "inputs=*;\n" + 
                    "runStepsScript=100;\n" + 
                    "shortTrace=false\n" + 
                    "--declarations-end--");
    
    private static final String LONG_HELP_REGEX_G = ""
            + par("In der theoretischen Informatik und der Theorie formaler Sprachen bezeichnet der Begriff " 
                    + def("Regulärer Ausdruck (RA; engl. regular expression, regex)") + " "
                    + "eine operationelle Methode um eine " + def("formale Sprache") + " zu definieren. "
                    + "Für ein Alphabet " + math("E") + " definiert ein RA " + math("\\alpha") 
                    + " (mit Hilfe einer Menge von Basisoperationen auf Mengen), wie Zeichen "
                    + "aus " + math("E") + " zu Zeichenketten, d.h. zu " 
                    + def("Wörtern") + " der Sprache " + math("L(\\alpha)") + ", kombiniert werden können. "
                    + "In diesem Sinne ist " + math("\\alpha") + " ein gültiger RA, wenn"
                    + itemize(
                            math("\\alpha = \\emptyset") + " oder " + math("\\alpha = e") + " für " + math("e \\in E") + " oder",
                            math("\\alpha = (" + index("\\alpha", "1") + " + " + index("\\alpha", "2") + ")") 
                                + " (Vereinigung) oder",
                            math("\\alpha = (" + index("\\alpha", "1") + " \\cdot " + index("\\alpha", "2") + ")") 
                                + " (Konkatenation) oder",
                            math("\\alpha = (" + index("\\alpha", "1") + ")*") 
                                + " (Iteration or 'Kleenesche Hülle')"
                                )
                    + "für gültige RA " + math(index("\\alpha", "1") + ", " + index("\\alpha", "2")) + ". "
                    + "Die Sprache eines RA wird durch rekursive Auswertung des oben definierten Schemas wie folgt konstruiert:"
                    + itemize(
                            math("L(\\emptyset) = \\emptyset, L(e) = {e}") + ",",
                            math("L((" + index("\\alpha", "1") + " + " + index("\\alpha", "2") + "))"
                                    + " = L(" + index("\\alpha", "1") + ") \\cup L(" + index("\\alpha", "2") + ")"),
                            math("L((" + index("\\alpha", "1") + " \\cdot " + index("\\alpha", "2") + "))"
                                    + " = L(" + index("\\alpha", "1") + ") \\circ L(" + index("\\alpha", "2") + ")")
                                + " wobei " + math(index("L", "1") + " \\circ " + index("L", "2") 
                                + " = {" + index("w", "1") + index("w", "2") + " | " 
                                        + index("w", "1") + " \\in " + index("L", "1") + ", "
                                        + index("w", "2") + " \\in " + index("L", "2")
                                + "}")
                                + ", und",
                            math("L((" + index("\\alpha", "1") + ")*) = " + index("\\bigcup", "i \\in " + 
                                    index("\\N", "0"))
                                    + " " + pow("L(" + index("\\alpha", "1") + ")", "i"))
                                + "."
                                )
                    + "Beispielsweise ist " + math("((a \\cdot b))*") + " ein regulärer Ausdruck über dem Alphabet "
                    + math("E = {a, b}") + ", der die Sprache " + math("{\\lambda, ab, abab, ababab, ...}") + " definiert. "
                    + "Klammern können wie üblich weggelassen werden, solange ein RA eindeutig über die Operatorengewichtung "
                    + math("*") + " vor " + math("\\cdot") + " vor " + math("+") 
                    + " ausgewertet werden kann. Außerdem wird " + math("\\cdot") + " in der Regel nicht explizit hingeschrieben. Damit kann der oben genannte RA "
                    + "auch umgeschrieben werden in " + math("(ab)*") + ". Beachten Sie, dass der Spezialfall des leeren Wortes " + math("\\lambda") 
                    + " per Definition schon durch den RA " + math("\\emptyset*") + " abgedeckt ist (trotzdem erlauben manche "
                    + " Autoren in der Literatur auch " + math("\\lambda") + " explizit als RA mit der Bedeutung " + math("L(\\lambda) = {\\lambda}") + ").")
            + par("Die Sprachen, die durch RA definiert werden können, sind genau dieselben, die von "
                    + def("endlichen Automaten") + " erkannt bzw. von " + def("rechtslinearen Grammatiken") + " generiert werden können. "
                    + "Es gibt auch Algorithmen, mittels derer für jeden RA ein äquivalenter endlicher Automat konstruiert werden kann und umgekehrt "
                    + "(dasselbe gilt für rechtslineare Grammatiken).")
            + par("In der Praxis werden RA hauptsächlich zur Mustererkennung bei Aufgaben vom Typ "
                    + "'Finden und Ersetzen' verwendet. "
                    + "Sie finden bspw. breite Anwendung in den UNIX-Werkzeugen " + def("ed") + ", "
                    + "einem Editor, und " + def("grep") + " (global regular expression print), einem Filterwerkzeug, aber selbstverständlich auch "
                    + "in den meisten modernen Programmiersprachen, wie Java, C# etc. "
                    + "RA haben sich als höchst nützlich für Anwendungen dieser Art erwiesen, und im Lauf der Zeit sind die oben definierten Basisoperationen "
                    + "zur Vereinfachung der Arbeit noch erheblich erweitert worden. Der " + XWIZZ_HTML_G + " unterstützt jedoch nur die Basisoperationen.") 
//            + par("Many programming languages provide regular expression capabilities, "
//                    + "some built-in, for example Perl, JavaScript, Ruby, AWK, and Tcl, "
//                    + "and others via a standard library, for example .NET languages, "
//                    + "Java, Python and C++ (since C++11). Most other languages offer "
//                    + "regular expressions via a library.")
            + par("Weitere Einzelheiten bezüglich regulärer Ausdrücke, insbesondere auch Hinweise zum praktischen Gebrauch, können " 
                    + link("https://de.wikipedia.org/wiki/Regul%C3%A4rer_Ausdruck", "hier") + " nachgelesen werden.")
            + par(USAGE_G
                    + itemize(
                            "Ein Skript für einen RA beginnt mit " + inlineScript(RegularExpression.SCRIPT_PREAMBLE),
                            REG_EX_BASIC_HELP_G.replace(". ", ".</LI><LI>"),
                            REG_EX_HELP_SIMPLIFY_G,
                            "Es ist zu beachten, dass die Minimierung eines RA PSPACE-vollständig ist, "
                            + "alle Vereinfachungen sind also nur Operationen, die mit 'gesundem Menschenverstand' ersichtlich sind "
                            + "und den RA oft nur rudimentär vereinfachen."))
            + example_G(RegularExpression.SCRIPT_PREAMBLE + " " + REG_EX_EXAMPLE_2);
    
    /* ****************** Help texts introduction - ENG+GER. ****************** */

    public static final String BUGS_N_SUCH = ""
            + par(XWIZZ_HTML + " works with complex objects which may have uncomputable properties "
                + "(particularly Grammars and Turing machines have many such properties). "
                + "Therefore, some requests that might run into very long-lasting or even "
                + "infinite loops cannot be rejected before-hand as it is not possible to recognize them. "
                + XWIZZ_HTML + " will therefore run the according algorithms anyway, "
                + "and break up the loops first when they consume too much time or space. "
                + "In many of these cases, an error message will show you that something went wrong, in some rare cases "
                + "the requested operation will just not be performed. In any case (at least as far as we know), "
                + XWIZZ_HTML + " will keep running correctly within its designated target space, i.e., it will remain stable. "
                + "The error messages might be made a little nicer in future, "
                + "but they are in any case not a sign of instability as the problem root itself " + bold("cannot") + " be solved. "
                + "(If you do experience more serious problems, however, such as the site not reacting anymore or "
                + "showing an HTML error or Java exception, please let us know " 
                + link(VFPVariables.URL_TO_ASK_QUESTION, "here", true) + ".)")
            + par("<B>Cookies:</B> " + XWIZZ_HTML + " uses a single cookie, which is just a random integer number, "
                + "to recognize you when you come back the next time. If you disallow cookies, personal settings "
                + "such as language, mode etc. might not work correctly.")
            + par("<B>Known bug 1:</B> You may use the " + button("BACK") + " button of your browser, but "
                    + "if you immediately afterwards try downloading the PDF, it will not be available. "
                    + "Click " + button("Draw!") + " to recreate the PDF, then click the " + button("Download PDF") + " link. "
                    + "Note that your browser should allow cookies for the PDF download to work correctly.")
            + par("<B>Known bug 2:</B> If the current script has been generated using a conversion method "
                    + "the short URL cannot be generated directly. Click the " + button("Draw!") + " button "
                    + "before creating the short URL.")
//            + par("<B>Known bug 2:</B> Bad timing (e.g. clicking too fast) can lead to corrupted computation results. "
//                    + "In these cases an error message will tell you to try the same procedure again. "
//                    + "This bug is a little annoying, but we are working"
//                    + "on fixing it, and already succeeded in letting it occur only very rarely. "
//                    + "We appologize and appreciate any suggestions or bug descriptions sent to " 
//                    + link("mailto:lukas.koenig@kit.edu", "lukas.koenig@kit.edu") + ".")
            ;
    
    private static final String CLICK_DRAW_SUCKA_G = "Durch "
            + "Anklicken des Buttons " + button("Draw!") + " kann die Funktionalität " 
            + "wiederhergestellt werden.";
    
    public static final String BUGS_N_SUCH_G = ""
            + par(XWIZZ_HTML_G + " bearbeitet komplexe Objekte, die zum Teil unberechenbare (im Turing-Sinn) Eigenschaften haben können "
                + "(insbesondere Grammatiken und Turingmaschinen haben viele solche Eigenschaften). "
                + "Daher können manche Algorithmen in sehr lange oder sogar endlos laufende Schleifen laufen, "
                + "die nicht im Vorfeld erkannt und ausselektiert werden können. "
                + XWIZZ_HTML_G + " wird die entsprechenden Algorithmen also trotzdem starten und erst nach einer gewissen Laufzeit "
                + "unterbrechen, wenn sie zu lange gelaufen sind (oder zuviel Speicher benötigt haben). "
                + "In vielen dieser Fälle wird eine Fehlermeldung ausgegeben, in einigen seltenen Fällen "
                + "wird die ausgewählte Operation einfach nicht beendet und der Vorzustand wiederhergestellt. "
                + "In jedem Fall aber (zumindest soweit wir wissen), wird "
                + XWIZZ_HTML_G + " innerhalb seines vorgesehen Zustandsraums bleiben, d.h., er wird stabil laufen. "
                + "Wir werden in Zukunft daran arbeiten, noch etwas schönere Fehlermeldungen auszugeben, "
                + "aber da die Wurzel des Problems nicht gelöst werden " + bold("kann") + ", sind weder schöne noch hässliche "
                + "Fehlermeldungen als Zeichen der Instabilität zu werten. "
                + "(Wenn allerdings die Seite nicht mehr reagiert oder HTML- oder Java-Fehler ausgegeben werden, "
                + "können Sie uns " 
                + link(VFPVariables.URL_TO_ASK_QUESTION, "hier", true) + " Bescheid sagen.)")
            + par("<B>Cookies:</B> " + XWIZZ_HTML_G + " speichert eine Zufallszahl als Cookie auf dem Rechner des Benutzers. Sie "
                + "wird ausschließlich dazu verwendet, diesen beim nächsten Mal wiederzuerkennen und dessen Einstellungen "
                + "zu reaktivieren. Wenn Cookies ausgeschaltet sind, können persönlichen Einstellungen "
                + "(wie Sprache, Modus etc.) nicht korrekt wiederhergestellt werden.")
            + par("<B>Bekannter Fehler 1:</B> Wenn der " + button("Zurück") + "-Button des Browsers verwendet wird, "
                    + "funktioniert direkt danach der Button " + button("PDF herunterladen") + " nicht richtig. "
                    + CLICK_DRAW_SUCKA_G)
            + par("<B>Bekannter Fehler 2:</B> Wenn das aktuelle Skript durch eine Konversionsmethode entstanden ist, "
                    + "dann kann die Kurz-URL nicht erzeugt werden. " + CLICK_DRAW_SUCKA_G)
//            + par("<B>Bekannter Fehler 2:</B> Schlechtes Timing (bspw. wenn zu schnell geklickt wird) kann zu "
//                    + "'halbfertigen' Rechenergebnisse führen. "
//                    + "In solchen Fällen wird eine Nachricht ausgegeben mit dem Hinweis, die letzte Aktion "
//                    + "erneut auszuführen. "
//                    + "Dieser Fehler ist etwas lästig, aber wir arbeiten daran, und er tritt nur noch "
//                    + "sehr selten auf. Für Hinweise oder Beschreibungen der Umstände, "
//                    + "unter welchen er auftrat, sind wir dankbar: "
//                    + link("mailto:lukas.koenig@kit.edu", "lukas.koenig@kit.edu"))
            ;
    
    public static final String XWIZARD_HELP = "" 
            + par(XWIZZ_HTML + " (eXercise Wizard) is a tool for creating "
                + "and visualizing mathematical objects of various types for the purpose "
                + "of both learning and teaching. Among the supported object types are "
                + bold("Turing machines, finite state machines, pushdown automata, Chomsky grammars, "
                + "several types of search trees and others") + ". In order to define a certain object, a "
                + "script with an individual syntax (as defined below) has to be input into the "
                + "script field on the main page. Once created, algorithms such as "
                + bold("automaton minimization, syntax parsing and many more") + " can be applied to the objects "
                + "by using conversion methods given as buttons. "
                + "More precisely, each of these algorithms transforms the script that underlies the current object "
                + "into another script representing a new object which, in turn, changes the output into "
                + "the new object's graph. Strictly speaking, "
                + "scripts are the sole underlying control mechanism, so providing an appropriate script "
                + "can always substitute for clicking any of the buttons. "
                + "On the other hand, many operations can be performed by just "
                + "using the graphical interface (buttons etc.) and not caring "
                + "about the script.")
            + par("You can use the examples on the main page as a starting point for creating "
                + "a script of a specific type. If you have trouble with a script, "
                + bold("read the help pages below") + " or click the " + button("discuss this script") + " link "
                + "below the script area to " + bold("get help in the community") + ".")
            + par(XWIZZ_HTML + " was originally developed for the " + link(URL_TO_KIT, "KIT") 
                + " course " + link(URL_TO_INFO2, "Info II") + ", but by now it includes "
                + "non-Info II script types as well. Beyond this web version, "
                + "a desktop version called " + def("PDF XWizard") + " with more functions and no restrictions regarding script size "
                + "or calculation time is " 
                + link(VFPVariables.URL_TO_VFP_DOWNLOAD, "available for free from sourceforge") + ". "
            + par("If you're interested in joining the " + XWIZZ_HTML + " development team, for example if you want to "
                + "implement your own script types, please send an email to " 
                + link("mailto:lukas.koenig@kit.edu", "lukas.koenig@kit.edu")
                + ".")
            + "<H3>Exercise mode</H3>"
            + par("Besides the regular mode where all conversion methods are available and you are "
                    + "free to work with whatever types of scripts you desire, " + XWIZZ_HTML + " can be run in "
                    + "another mode called " + def("exercise mode") + ". "
                    + "There, a question is posed that you are supposed to answer. In this mode, "
                    + "some of the conversion methods might be hidden, and you can only use the "
                    + "remaining ones to get to the solution. Furthermore, an exercise script can be "
                    + "encrypted (completely or partially) to blur its content and preserve for you the fun of solving it yourself"
                    + " (as the solution is encoded in the script). Once an exercise is answered correctly, you "
                    + "receive a bonus badge (this is a secret word), and " + XWIZZ_HTML + " returns to regular mode.")
            + par("Note that you are allowed to generate your own individual exercises. "
                    + "For this, use the button " + button("Create exercise from this script") + ". "
                    + "To archive and share exercises (or any other scripts), you can "
                    + "convert them into a URL by using the button " + button("URL to this script" + ". ")
                    + "To avoid very long URLs, exercises and (nearly) all other scripts can be encoded as a short URL by "
                    + "using the button " + button("Short URL to this script...") + ".")
            + par(center(div("<H3>Technicalities</H3>" + BUGS_N_SUCH, "style=\"border-radius: 5px; border: 2px solid #009D82;\"")))
                    );
    
    public static final String XWIZARD_HELP_G = "" 
            + par(XWIZZ_HTML_G + " (eXercise Wizard) ist ein Werkzeug zur Erstellung und Visualisierung mathematischer Objekte verschiedenster Art. "
                    + "Er wurde entwickelt, um sowohl Lehrende als auch Lernende bei der Veranschaulichung theoretischer Konzepte zu unterstützen. "
                    + "Zu den derzeit implementierten Objekttypen gehören unter anderem "
                    + bold("Turingmaschinen, endliche Automaten, Kellerautomaten, Chomsky Grammatiken und verschiedene Arten von Suchbäumen") 
                    + ". Um ein bestimmtes Objekt zu definieren, muss ein Skript mit entsprechender Syntax (wie unten beschrieben) in das Skript-Feld "
                    + "auf der Startseite eingegeben werden. "
                    + "Sobald das Objekt generiert wurde, können mittels der dafür zur Verfügung stehenden 'Konversions-Buttons' Algorithmen wie z.B. "
                    + bold("Automatenminimierung, Syntax-Analyse und viele andere") + " darauf angewendet werden. "
                    + "Genauer gesagt transformieren diese Algorithmen das Skript, das dem aktuellen Objekt zugrundeliegt, in ein anderes Skript, das "
                    + "einen anderen Objekttypen beschreibt. Dadurch ändert sich auch die Darstellung dieses neuen Objektes. "
                    + "Strenggenommen stellen Skripte den ausschließlichen Kontrollmechanismus im " + XWIZZ_HTML_G + " dar, daher kann ein Klick auf einen beliebigen Button immer "
                    + "durch das Einfügen eines entsprechende Skriptes ersetzt werden. "
                    + "Andererseits können viele Operationen auch einfach mittels der graphischen Oberfläche (Buttons usw.) ausgeführt werden, "
                    + "ohne dass man sich Gedanken um das passende Skript machen muss.")
                + par("Die Beispiele, die auf der Startseite zur Verfügung stehen, können als Ausgangspunkt zur Erstellung eines Skriptes des jeweiligen "
                    + "Typs verwendet werden. Bei Problemen mit einem Skript "
                    + bold("können die Hilfetexte unten weiterhelfen") + ", oder ein Klick auf den Link " + button("discuss this script")
                    + "unterhalb des Skript-Feldes, um " + bold("Hilfe in der Community") + " zu bekommen.")
                + par("Der " + XWIZZ_HTML_G + " wurde ursprünglich zur Unterstützung der Vorlesung "  
                    + link(URL_TO_INFO2_G, "Info II") + " am "
                    + link(URL_TO_KIT_G, "KIT") + " entwickelt, beinhaltet inzwischen aber auch Skript-Typen, die darüber hinausgehen. "
                    + "Zusätzlich zu dieser web-basierten Version ist auch die Desktopversion " + def("PDF-XWizard") + " "
                    + link(VFPVariables.URL_TO_VFP_DOWNLOAD, "auf Sourceforge als kostenloser Download")
                    + " erhältlich. Sie stellt zusätzliche Funktionen zur Verfügung "
                    + "und verzichtet auf jegliche Einschränkungen bezüglich Skriptgröße oder Rechenzeit. "
                + par("Falls Sie Interesse haben, sich dem " + XWIZZ_HTML_G + "-Entwicklungs-Team anzuschließen, "
                    + "etwa um eigene Skript-Typen zu implementieren, wenden Sie sich bitte per Email an " 
                    + link("mailto:lukas.koenig@kit.edu", "lukas.koenig@kit.edu")
                    + ".")
                + "<H3>Übungsmodus</H3>"
                + par("Außer dem normalen Modus, in dem alle Konversionsmethoden zur Verfügung stehen und mit allen Skript-Typen gearbeitet werden kann, "
                        + "kann der " + XWIZZ_HTML_G + " auch im sogenannten " + def("Übungsmodus") + " betrieben werden. "
                        + "In diesem Fall wird eine Aufgabe gestellt, die gelöst werden soll. Dabei können einige Konversionsbuttons verborgen sein, "
                        + "damit die Lösung der Aufgabe mit den verbleibenden Methoden erfolgen muss. Außerdem kann das Skript der Aufgabe "
                        + "ganz oder teilweise verschlüsselt sein, damit der Inhalt nicht zum Schummeln genutzt werden kann. "
                        + "Wenn die Aufgabe richtig gelöst wurde, wird ein Bonus (ein geheimes Wort) angezeigt, und der "
                        + XWIZZ_HTML_G + " kehrt in den normalen Modus zurück.")
                + par("Es können auch eigene, individuelle Aufgaben erstellt werden, "
                        + "bspw. durch Anklicken des Buttons " + button("Erzeuge Aufgabe aus Skript") + ". "
                        + "Um eine Aufgabe (oder beliebige andere Skripte) zu archivieren und zu teilen, "
                        + "kann daraus mit Hilfe des Buttons " + button("Erstelle URL zu diesem Skript") + " eine URL erstellt werden. "
                        + "Um sehr lange URLs zu vermeiden, können Aufgaben und (fast) alle anderen Skript-Typen auch mit Hilfe des Buttons "
                        + button("Erstelle kurze URL zu diesem Skript") + " in eine Kurz-URL konvertiert werden.")
                + par(center(div("<H3>Technisches</H3>" + BUGS_N_SUCH_G, "style=\"border-radius: 5px; border: 2px solid #009D82;\"")))
                        );
    
    static {
        LONG_HELP_TEXTS.put(BDD.class, LONG_HELP_BDD);
        LONG_HELP_TEXTS.put(FSM.class, LONG_HELP_FSM);
        LONG_HELP_TEXTS.put(Grammar.class, LONG_HELP_GRAMMAR);
        LONG_HELP_TEXTS.put(Huffman.class, LONG_HELP_HUFFMAN);
        LONG_HELP_TEXTS.put(LaTeX.class, LONG_HELP_LATEX);
        LONG_HELP_TEXTS.put(Numbers.class, LONG_HELP_NUMBERS);
        LONG_HELP_TEXTS.put(LogicCircuit.class, LONG_HELP_CIRCUIT);
        LONG_HELP_TEXTS.put(PatTree.class, LONG_HELP_PATTREE);
        LONG_HELP_TEXTS.put(PDA.class, LONG_HELP_PDA);
        LONG_HELP_TEXTS.put(RegularExpression.class, LONG_HELP_REGEX);
        LONG_HELP_TEXTS.put(Tree234.class, LONG_HELP_TREE234);
        LONG_HELP_TEXTS.put(RedBlackTree.class, LONG_HELP_REDBLACKTREE);
        LONG_HELP_TEXTS.put(Turing.class, LONG_HELP_TURING);
        LONG_HELP_TEXTS.put(Graphviz.class, LONG_HELP_PLAINDOT);
        LONG_HELP_TEXTS.put(MetaProperties.class, LONG_HELP_META_PROPERTIES);

        VERY_QUICK_HELP_TEXTS.put(BDD.class, QUICK_HELP_BDD);
        VERY_QUICK_HELP_TEXTS.put(FSM.class, QUICK_HELP_FSM);
        VERY_QUICK_HELP_TEXTS.put(Grammar.class, QUICK_HELP_GRAMMAR);
        VERY_QUICK_HELP_TEXTS.put(Huffman.class, QUICK_HELP_HUFFMAN);
        VERY_QUICK_HELP_TEXTS.put(LaTeX.class, QUICK_HELP_LATEX);
        VERY_QUICK_HELP_TEXTS.put(Numbers.class, QUICK_HELP_NUMBERS);
        VERY_QUICK_HELP_TEXTS.put(LogicCircuit.class, QUICK_HELP_CIRCUIT);
        VERY_QUICK_HELP_TEXTS.put(PatTree.class, QUICK_HELP_PATTREE);
        VERY_QUICK_HELP_TEXTS.put(PDA.class, QUICK_HELP_PDA);
        VERY_QUICK_HELP_TEXTS.put(RegularExpression.class, QUICK_HELP_RegEx);
        VERY_QUICK_HELP_TEXTS.put(Tree234.class, QUICK_HELP_TREE234);
        VERY_QUICK_HELP_TEXTS.put(RedBlackTree.class, QUICK_HELP_REDBLACKTREE);
        VERY_QUICK_HELP_TEXTS.put(Turing.class, QUICK_HELP_TURING);
        VERY_QUICK_HELP_TEXTS.put(Graphviz.class, QUICK_HELP_PLAINDOT);
        VERY_QUICK_HELP_TEXTS.put(MetaProperties.class, QUICK_HELP_META_PROPERTIES);

        LONG_HELP_TEXTS_G.put(BDD.class, LONG_HELP_BDD_G);
        LONG_HELP_TEXTS_G.put(FSM.class, LONG_HELP_FSM_G);
        LONG_HELP_TEXTS_G.put(Grammar.class, LONG_HELP_GRAMMAR_G);
        LONG_HELP_TEXTS_G.put(Huffman.class, LONG_HELP_HUFFMAN_G);
        LONG_HELP_TEXTS_G.put(LaTeX.class, LONG_HELP_LATEX_G);
        LONG_HELP_TEXTS_G.put(Numbers.class, LONG_HELP_NUMBERS_G);
        LONG_HELP_TEXTS_G.put(LogicCircuit.class, LONG_HELP_CIRCUIT_G);
        LONG_HELP_TEXTS_G.put(PatTree.class, LONG_HELP_PATTREE_G);
        LONG_HELP_TEXTS_G.put(PDA.class, LONG_HELP_PDA_G);
        LONG_HELP_TEXTS_G.put(RegularExpression.class, LONG_HELP_REGEX_G);
        LONG_HELP_TEXTS_G.put(Tree234.class, LONG_HELP_TREE234_G);
        LONG_HELP_TEXTS_G.put(RedBlackTree.class, LONG_HELP_REDBLACKTREE_G);
        LONG_HELP_TEXTS_G.put(Turing.class, LONG_HELP_TURING_G);
        LONG_HELP_TEXTS_G.put(Graphviz.class, LONG_HELP_PLAINDOT_G);
        LONG_HELP_TEXTS_G.put(MetaProperties.class, LONG_HELP_META_PROPERTIES_G);

        VERY_QUICK_HELP_TEXTS_G.put(BDD.class, QUICK_HELP_BDD_G);
        VERY_QUICK_HELP_TEXTS_G.put(FSM.class, QUICK_HELP_FSM_G);
        VERY_QUICK_HELP_TEXTS_G.put(Grammar.class, QUICK_HELP_GRAMMAR_G);
        VERY_QUICK_HELP_TEXTS_G.put(Huffman.class, QUICK_HELP_HUFFMAN_G);
        VERY_QUICK_HELP_TEXTS_G.put(LaTeX.class, QUICK_HELP_LATEX_G);
        VERY_QUICK_HELP_TEXTS_G.put(Numbers.class, QUICK_HELP_NUMBERS_G);
        VERY_QUICK_HELP_TEXTS_G.put(LogicCircuit.class, QUICK_HELP_CIRCUIT_G);
        VERY_QUICK_HELP_TEXTS_G.put(PatTree.class, QUICK_HELP_PATTREE_G);
        VERY_QUICK_HELP_TEXTS_G.put(PDA.class, QUICK_HELP_PDA_G);
        VERY_QUICK_HELP_TEXTS_G.put(RegularExpression.class, QUICK_HELP_RegEx_G);
        VERY_QUICK_HELP_TEXTS_G.put(Tree234.class, QUICK_HELP_TREE234_G);
        VERY_QUICK_HELP_TEXTS_G.put(RedBlackTree.class, QUICK_HELP_REDBLACKTREE_G);
        VERY_QUICK_HELP_TEXTS_G.put(Turing.class, QUICK_HELP_TURING_G);
        VERY_QUICK_HELP_TEXTS_G.put(Graphviz.class, QUICK_HELP_PLAINDOT_G);
        VERY_QUICK_HELP_TEXTS_G.put(MetaProperties.class, QUICK_HELP_META_PROPERTIES_G);
    }

    public static void showLongHelpWindow(RepresentableAsPDF rep) {
        String text = rep.helpText();
        String title = "Long help for '" + rep.getEnglishName() + "'";
        
        if (text == null) {
            showQuickHelpWindow(rep, true); // Show quick help if no long help provided.
        } else {
            text = text.replace("<fieldset", "<B")
                    .replace("</fieldset", "</B")
                    .replace("<legend", "<center")
                    .replace("</legend", "</center");
            
            showHelpWindow(
                    text, 
                    title,
                    true);
        }
    }

    public static void showQuickHelpWindow(RepresentableAsPDF rep, boolean always) {
        String text = rep.veryQuickHelpText();
        String title = "Quick help for '" + rep.getEnglishName() + "'";
        
        if (text != null) {
            showHelpWindow(
                    "<CENTER>" + text + "</CENTER>", 
                    title,
                    always);
        }
    }
    
    private static void showHelpWindow(String html, String title, boolean showAlways) {
        GeneralDialog.showHTML(
                html, 
                title, 
                showAlways ? null : title.hashCode() + "_ID");
    }
}
