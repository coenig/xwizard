/*
 * File name:        Grammar.java (package veryFastPDF.algorithms.grammars)
 * Author(s):        Lukas König
 * Java version:     6.0
 * Generation date:  19.01.2012 (20:05:06)
 *
 * (c) This file and the EAS (Easy Agent Simulation) framework containing it
 * is protected by Creative Commons by-nc-sa license. Any altered or
 * further developed versions of this file have to meet the agreements
 * stated by the license conditions. 
 * 
 * In a nutshell
 * -------------
 * You are free:
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

package veryFastPDF.algorithms.grammars;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.JComponent;
import javax.swing.JPanel;

import eas.GlobalVariables;
import eas.miscellaneous.StaticMethods;
import eas.miscellaneous.convenience.GeneralDialog;
import eas.veryFastPDF.MainLink;
import net.miginfocom.swing.MigLayout;
import veryFastPDF.algorithms.grammars.type2grammars.charty.ChartParser;
import veryFastPDF.algorithms.grammars.type2grammars.charty.CtxtFreeGrammar;
import veryFastPDF.algorithms.grammars.type2grammars.charty.ParseTree;
import veryFastPDF.algorithms.latex.LaTeXCommands;
import veryFastPDF.algorithms.pda.PDA;
import veryFastPDF.algorithms.pda.StateKellersymbols;
import veryFastPDF.algorithms.pda.StateTapesymbolKellersymbol;
import veryFastPDF.algorithms.pda.Transition;
import veryFastPDF.algorithms.regEx.RegularExpression;
import veryFastPDF.pdfProcessors.GraphViz;
import veryFastPDF.pdfProcessors.LaTeXPDF;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.pdfProcessors.PDFProcessorFactory;
import veryFastPDF.plugin.FancyJLabel;
import veryFastPDF.script.ConversionMethod;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.web.ConvenienceMethods;
import veryFastPDF.web.Webproof;

/**
 * @author Lukas König
 */
@Webproof(useInProductiveMode = true)
public class Grammar extends RepresentableDefault {

    private static final long serialVersionUID = 4852826272144571188L;
    
    private static final RuntimeException ABORT_LONG_OPERATION_EXCEPTION = new RuntimeException("Abort long-time operation.");
    public static final long ASKING_TIME = 3000;
    public static final String ABORTED_STRING = "Long-time operation aborted";
    private static final GrammTree LONG_OPERATION_ABORTED 
        = new GrammTree(new Word(new Symbol[] {new Nonterminal(" " + ABORTED_STRING)}), null, null);

    private String currentStepInGNFCreation;
    private boolean isRemoved;

    /*
     * Declaration variables received from script.
     */
    private String N = "S";
    private String T = "a";
    private String S = "S";
    private int displayMode = 2;
    private int maxdepth = 5;
    private boolean cutNonTerminalBranches = true;
    private boolean cutTerminalDoubleBranches = true;
    private int maxLengthWords = 6;
    private boolean multiLetterSymbolsHaveIndex = false;
    /*
     * EO declaration variables.
     */

    /**
     * In contrast to S, this may change during calculations.
     */
    private Nonterminal internalStartSymbol;
    private String lastCode;
    private String[] wordToParse = null;
    private int parseTreeNum = 0;
    private int numOfParseTrees = -1;
    private HashSet<Rule> rules = new HashSet<Rule>();
    private ArrayList<Nonterminal> nonTerminals;

    private int nodeNum = 0;
    private ArrayList<ParseTree> parseTreesFromLastCalculation;
    private int nontermCountCNF = 0;
    private int nontermCountKNF = 0;
    private boolean abortRequested = false;
    private Integer dontRecalculateParseTrees;
    private boolean senseLongTimeOperations = true; // Keep this true where possible.
    
    public boolean isMultiLetterSymbolsHaveIndex() {
        return this.multiLetterSymbolsHaveIndex;
    }
    
    public Nonterminal getStartSymbol() {
        return this.internalStartSymbol;
    }
    
    /**
     * Seriously use for type 2 grammars only, it won't work for type 1 or 0!
     * Should be deleted eventually.
     * 
     * @param s  The symbol to check.
     * @return  Iff the symbol is a terminal.
     */
    public boolean isTerminalType2(Symbol s) {
        for (Rule r : this.getRules()) {
            if (r.getImmutableLeftSide().getSymbols().get(0).toString().equals(s.toString())) {
                return false;
            }
        }
        
        return true;
    }
    
    public HashSet<String> getTerminals() {
        HashSet<String> list = new HashSet<>();
        for (Rule r : this.getRules()) {
            for (Symbol s : r.getImmutableLeftSide().getSymbols()) {
                if (this.isTerminal(s)) {
                    list.add(s.toString());
                }
            }
            for (Symbol s : r.getImmutableRightSide().getSymbols()) {
                if (this.isTerminal(s)) {
                    list.add(s.toString());
                }
            }
        }
        
        return list;
    }

    private boolean isTerminal(Symbol s2) {
        return !this.isNonterminal(s2);
    }

    public HashSet<String> getNonTerminals() {
        HashSet<String> list = new HashSet<>();
        for (Rule r : this.getRules()) {
            for (Symbol s : r.getImmutableLeftSide().getSymbols()) {
                if (Nonterminal.class.isInstance(s)) {
                    list.add(s.toString());
                }
            }
            for (Symbol s : r.getImmutableRightSide().getSymbols()) {
                if (Nonterminal.class.isInstance(s)) {
                    list.add(s.toString());
                }
            }
        }
        
        return list;
    }

    /**
     * Constructor to create a grammar identical to another grammar (including
     * all side effects and field variables. Note that every new field has to
     * be registered here.
     * 
     * @param other  The other grammar.
     */
    public Grammar(Grammar other) {
        this(other.internalStartSymbol, other.getExercise());
        this.multiLetterSymbolsHaveIndex = other.multiLetterSymbolsHaveIndex;
        this.nontermCountCNF = other.nontermCountCNF;
        this.nontermCountKNF = other.nontermCountKNF;
        this.newStartSymbolToBeCreated = other.newStartSymbolToBeCreated;
        this.abortRequested = other.abortRequested;
        this.cutNonTerminalBranches = other.cutNonTerminalBranches;
        this.cutTerminalDoubleBranches = other.cutTerminalDoubleBranches;
        this.parseTreeNum = other.parseTreeNum;
        this.dontRecalculateParseTrees = other.dontRecalculateParseTrees;
        this.lastCode = other.lastCode;
        this.maxdepth = other.maxdepth;
        this.maxLengthWords = other.maxLengthWords;
        this.nodeNum = other.nodeNum;
        this.nonTerminals = new ArrayList<>(other.nonTerminals.size());
        this.displayMode = other.displayMode;
        for (Nonterminal n : other.nonTerminals) {
            this.nonTerminals.add(new Nonterminal(n));
        }
        this.numOfParseTrees = other.numOfParseTrees;
        
        if (other.parseTreesFromLastCalculation != null) {
            this.parseTreesFromLastCalculation = new ArrayList<>(other.parseTreesFromLastCalculation);
//            for (int i = 0; i < other.parseTreesFromLastCalculation.size(); i++) {
//                this.parseTreesFromLastCalculation[i] = other.parseTreesFromLastCalculation[i];
//            }
        }
        
        this.rules = new HashSet<>(other.rules.size());
        
        for (Rule r : other.rules) {
            this.rules.add(new Rule(new Word(r.getImmutableLeftSide()), new Word(r.getImmutableRightSide())));
        }
        
//        this.terminals = new ArrayList<>(other.terminals.size());
//        for (Terminal t : other.terminals) {
//            this.terminals.add(new Terminal(t));
//        }
        
        if (other.wordToParse != null) {
            this.wordToParse = new String[other.wordToParse.length];
            for (int i = 0; i < other.wordToParse.length; i++) {
                this.wordToParse[i] = other.wordToParse[i];
            }
        }
        
        this.N = other.N;
        this.T = other.T;
        this.S = other.S;
    }
    
    public Grammar(Exercise exercise) {
        this(new Nonterminal("S"), exercise);
    }
    
    public Grammar(Nonterminal start, Exercise exercise) {
        this(null, start, exercise);
    }
    
    public Grammar(File readGramm, Nonterminal start, Exercise exercise) {
        super(exercise);
        this.internalStartSymbol = new Nonterminal(start);
        this.nonTerminals = new ArrayList<>();
//        this.terminals = new ArrayList<>();
        this.nonTerminals.add(this.internalStartSymbol);
        
        if (readGramm != null) {
            List<String> rules = liesTextArray(readGramm);
            
            for (String s : rules) {
                if (s.length() > 0 && s.charAt(0) != '%') {
                    String[] sides = s.replace(" ", "").split("-->");
                    Rule rule;
                    
                    ArrayList<Symbol> leftSideSymb = new ArrayList<>();
                    ArrayList<Symbol> rightSideSymb = new ArrayList<>();
                    
                    // Left side.
                    for (String s2 : sides[0].split(",")) {
                        if (Character.isLetter(s2.charAt(0)) && Character.isUpperCase(s2.charAt(0))) { // Nonterminal
                            leftSideSymb.add(new Nonterminal(s2));
                        } else { // Terminal
                            leftSideSymb.add(new Terminal(s2));
                        }
                    }
                    
                    // Right side.
                    if (sides.length > 1) {
                        for (String s2 : sides[1].split(",")) {
                            if (Character.isLetter(s2.charAt(0)) && Character.isUpperCase(s2.charAt(0))) { // Nonterminal
                                rightSideSymb.add(new Nonterminal(s2));
                            } else { // Terminal
                                rightSideSymb.add(new Terminal(s2));
                            }
                        }
                    }
                    
                    rule = new Rule(new Word(leftSideSymb), new Word(rightSideSymb));
                    
                    this.addRule(rule);
                }
            }
        }
        
        this.addIgnoredField("senseLongTimeOperations");
        this.addIgnoredField("abortRequested");
        this.addIgnoredField("lastCode");
        this.addIgnoredField("numOfParseTrees");
        this.addIgnoredField("dontRecalculateParseTrees");
        this.addIgnoredField("isRemoved");
        this.addIgnoredField("nodeNum");
        this.addIgnoredField("nontermCountCNF");
        this.addIgnoredField("nontermCountKNF");
        this.addIgnoredField("newStartSymbolToBeCreated");
        this.addIgnoredField("currentStepInGNFCreation");
    }
    
    public void addRule(Rule rule) {
        rules.add(rule);
    }
    
    public void addAllRules(Collection<Rule> rules) {
        this.rules.addAll(rules);
    }
    
    public void removeRule(Rule rule) {
        rules.remove(rule);
    }
    
    public void removeAllRules(Collection<Rule> rules) {
        this.rules.removeAll(rules);
    }
    
    public HashSet<Rule> getRules() {
        return this.rules;
    }
    
    /**
     * Creates a LaTeXCode output for all rules that have a specific single 
     * nonterminal on the left-hand side.
     * 
     * @param n  The nonterminal symbol on the left-hand side.
     * 
     * @return  A LaTeXCode output of every rule with this left-hand side.
     */
    private String toStringAllRulesFromLatex(Nonterminal n) {
        String s = "";
        
        s += "&" + LaTeXPDF.replaceSpecialChars(n.toString());
        s += " \\rightarrow ";

        List<Rule> relevantRules = this.getSortedRuleList(this.getAllRulesWithLeftSideSingleElement(n.toString(), this.getRules()));

        for (int i = 0; i < relevantRules.size(); i++) {
            Rule r = relevantRules.get(i);
            Word rightSide = r.getImmutableRightSide();
            
            if (rightSide.getWordLength() > 0) {
                s += LaTeXPDF.replaceSpecialChars(rightSide.toString()).replace("[", "").replace("]", "").replace(": ", "");
            } else {
                s += "\\epsilon";
            }
            
            if (i < relevantRules.size() - 1) {
                s += " \\ | \\ ";
            }
        }
        
        return s;
    }
    
    private String toStringRulesLatex(String baseDef) {
        String s = "";
        HashSet<Nonterminal> finished = new HashSet<>();
        
        List<Rule> rules = this.getSortedRuleList(this.rules);
        
        final String endOfNonfinalRule = ",\\\\\n";
        final String endOfFinalRule = "\\}\n";

        for (int i = 0; i < rules.size(); i++) {
            Rule r = rules.get(i);
            if (r.getImmutableLeftSide().getWordLength() == 1) {
                Nonterminal toStringN = (Nonterminal) r.getImmutableLeftSide().getSymbols().get(0);
                if (!finished.contains(toStringN)) {
                    if (i < this.rules.size() - 1) {
                        s += this.toStringAllRulesFromLatex(toStringN) + endOfNonfinalRule;
                    } else {
                        s += this.toStringAllRulesFromLatex(toStringN) + endOfFinalRule;
                    }
                    
                    finished.add(toStringN);
                }
            } else {
                if (i < this.rules.size() - 1) {
                    s += r.toStringLatex() + endOfNonfinalRule;
                } else {
                    s += r.toStringLatex() + endOfFinalRule;
                }
            }
        }

        if (s.endsWith(endOfNonfinalRule)) {
            s = s.substring(0, s.length() - endOfNonfinalRule.length()) + endOfFinalRule;
        }
        
        s = "\\begin{align*}" + baseDef + "P=\\{\n" + s + "\\end{align*}";

        return s;
    }
    
    private List<Rule> getSortedRuleList(Collection<Rule> coll) {
        LinkedList<Rule> list = new LinkedList<>(coll);
        Collections.sort(list, (r1, r2) -> {
            if (r1.getImmutableLeftSide().getSymbols().get(0).equals(this.getStartSymbol())
                    && !r2.getImmutableLeftSide().getSymbols().get(0).equals(this.getStartSymbol())) {
                return -1;
            } else if (r2.getImmutableLeftSide().getSymbols().get(0).equals(this.getStartSymbol())
                    && !r1.getImmutableLeftSide().getSymbols().get(0).equals(this.getStartSymbol())) {
                return 1;
            } else {
                if (r1.getImmutableLeftSide().getSymbols().toString().length() > r2.getImmutableLeftSide().getSymbols().toString().length()) {
                    return 1;
                } else if (r1.getImmutableLeftSide().getSymbols().toString().length() < r2.getImmutableLeftSide().getSymbols().toString().length()) {
                    return -1;
                } else {
                    int comparedAlphaLeft = r1.getImmutableLeftSide().getSymbols().get(0).toString()
                            .compareTo(r2.getImmutableLeftSide().getSymbols().get(0).toString());
                    
                    if (comparedAlphaLeft != 0) {
                        return comparedAlphaLeft;
                    } else {
                        List<Symbol> symbolsR1 = r1.getImmutableRightSide().getSymbols();
                        List<Symbol> symbolsR2 = r2.getImmutableRightSide().getSymbols();
                        if (symbolsR1.size() != symbolsR2.size()) {
                            int i = symbolsR1.size() - symbolsR2.size();
                            return i;
                        } else {
                            int comparedAlphaRight = symbolsR1.get(0).toString()
                                    .compareTo(symbolsR2.get(0).toString());
                            return comparedAlphaRight;
                        }
                    }
                }
            }
        });
        
        return list;
    }

    private List<String> getSortedStringList(Collection<String> coll) {
        LinkedList<String> list = new LinkedList<>(coll);
        Collections.sort(list, (c1, c2) -> c1.compareTo(c2));
        return list;
    }
    
    @Override
    public String toString() {
        String latex = toStringLatex(false);
        
        return "Script\n-----\n" + this.getRawScript()
                + "\n\n/* This is a type-" + this.retrieveHighestGrammarType() + " grammar (" + this.rules.size() + " rules). */"
                + "\n-----"
                + latex;
    }

    public String toStringLatex(boolean defOnly) {
        List<String> terminals = this.getSortedStringList(this.getTerminals());
        List<String> nonterminals = this.getSortedStringList(this.getNonTerminals());

        String latex;

        if (defOnly) {
            latex = this.toStringRulesLatex("G=(&\\{" 
                    + LaTeXPDF.replaceSpecialChars(nonterminals.toString().replace("[", "").replace("]", "")) 
                    + "\\}, \\{" 
                    + LaTeXPDF.replaceSpecialChars(terminals.toString().replace("[", "").replace("]", "")) + "\\}, P, " 
                    + this.internalStartSymbol + ")\\\\");
        } else {
            latex = "\n\n$$G=\\left(\\{" 
                    + LaTeXPDF.replaceSpecialChars(nonterminals.toString().replace("[", "").replace("]", "")) 
                    + "\\}, \\{" 
                    + LaTeXPDF.replaceSpecialChars(terminals.toString().replace("[", "").replace("]", "")) + "\\}, P, " 
                    + this.internalStartSymbol + "\\right)$$\n\n" + this.toStringRulesLatex("");
        }
        
        String bracketLeft = "-$$SPECIAL-LLL$$$";
        String bracketLeftPlain = "-$$SPECIAL-LLL-safe$$$";
        String bracketRight = "-$$SPECIAL-RRR$$$";
        String bracketRightPlain = "-$$SPECIAL-RRR-safe$$$";
        String align = "-$$ALIGN$$$";
        
        // A1 => A_{1} or Satz => \mbox{Satz}
        for (String n : nonterminals) {
            if (n.length() > 1 && !n.toString().contains("'")) {
                if (this.multiLetterSymbolsHaveIndex) {
                    latex = latex.replace(n, n.charAt(0) + "_" + bracketLeftPlain + n.substring(1) + bracketRightPlain);
                } else {
                    latex = latex.replace(n, "\\mbox" + bracketLeftPlain + "" + n + "" + bracketRightPlain);
                }
            }
        }
        
        // Derive single word.
        if (!defOnly) {
            String temp = "";
            if (this.parseTreesFromLastCalculation != null) {
                temp += "\n\n" + "\\begin{align*}\n";
                int strLength = 0;
                int count = this.parseTreesFromLastCalculation.size();
                
                for (ParseTree p : this.parseTreesFromLastCalculation) {
                    String newString = p.getDerivation().toString() + "\\\\\n";
                    strLength += newString.length();
                    temp += newString;
                    count--;
                    
                    if (strLength > 4000 && count > 0) {
                        temp += "&\\mbox{\\ldots " + count + " more derivations cut off\\ldots}";
                        break;
                    }
                    
                    strLength++;
                }
                
                temp += "\n\\end{align*}";
                
                temp = temp.replace("\n\n", "\n").replace("\n\n", "\n").replace("\n\n", "\n").replace("\n\n", "\n");
            }
            temp = temp.replace("{", bracketLeftPlain).replace("}", bracketRightPlain);
            
            latex += temp;
        }
        
        
        return latex
                .replace("\\{", bracketLeft).replace("\\}", bracketRight)
                .replace("{align*}", align)
                .replace("{", "\\{").replace("}", "\\}")
                .replace(align, "{align*}")
                .replace(bracketLeft, "\\{").replace(bracketRight, "\\}")
                .replace(bracketLeftPlain, "{").replace(bracketRightPlain, "}")
                .replace("<>", "\\stackrel{\\diamond}{\\epsilon}");
    }
    
    private boolean match(Word w, Rule r, int i) {
        try {
            for (int j = i; j < i + r.getImmutableLeftSide().getSymbols().size(); j++) {
                if (!r.getImmutableLeftSide().getSymbols().get(j - i).equals(w.getSymbols().get(j))) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }
    
    private GrammTree generateWords(int depth, GrammTree tree, int maxLengthWords, boolean quiet) {
        if (!quiet && !GeneralDialog.continueLongOperation(
                "Long-time calculation", 
                "This seems to be a long-time calculation - proceed?", 
                Grammar.ASKING_TIME, 
                "Grammar-long-time-ID")) {
                this.abortRequested = true;
                return tree;
            }

        if (this.maxdepth <= depth) {
            return tree;
        }
    
        for (int i = 0; i < tree.getWord().getSymbols().size(); i++) {
            for (Rule r : this.getRules()) {
                if (!this.abortRequested && this.match(tree.getWord(), r, i)) {
                    Word newWord = new Word(tree.getWord().replace(i, r.getImmutableLeftSide().getSymbols().size(), r.getImmutableRightSide()));
                    if (newWord.getWordLength() <= maxLengthWords) {
                        tree.addChild(new GrammTree(newWord, tree, this));
                    }
                }
            }
        }
        
        for (GrammTree c : tree.getChildren()) {
            this.generateWords(depth + 1, c, maxLengthWords, quiet);
        }
        
        return tree;
    }

    /**
     * KOPIE aus StaticMethods.
     * Liest zeilenweise Text aus einer Textdatei und gibt einen String-Array
     * der gesamten Datei zurück.
     * 
     * @param verz     Das Verzeichnis.
     * @param datName  Die Datei.
     * @param params   Die Parameter.
     * 
     * @return  Der zeilenweise gelesene Text aus der Datei.
     */
    public static LinkedList<String> liesTextArray(
            final String verz,
            final String datName) {
        String zwisch;
        LinkedList<String> textArray = new LinkedList<String>();
        
        try {
            BufferedReader f1 =
                new BufferedReader(
                        new FileReader(verz + File.separator + datName));
            
            zwisch = f1.readLine();
            while (zwisch != null) {
                textArray.add(zwisch);
                zwisch = f1.readLine();
            }
            f1.close();
        } catch (IOException e) {
            GlobalVariables.getParameters().logWarning(
                    "Fehler beim Lesen aus der Text-Datei: " + verz + File.separator + datName);
            throw new RuntimeException(
                    "Aus Datei konnte nicht gelesen werden.");
        }
        
        return textArray;
    }

    /**
     * KOPIE aus StaticMethods.
     * @param datei
     * @return
     */
    public static LinkedList<String> liesTextArray(final File datei) {
        String pfad = datei.getParent();
        String dateiname = datei.getName();
        LinkedList<String> textArray = liesTextArray(pfad, dateiname);
        return textArray;
    }

    /**
     * Retreives the highest Chomsky type this grammar (not its language) is of.
     * Note that type 1 is considered as monotone, not as context-sensitive. Type 3
     * may be left or right linear.
     * 
     * @return The Chomsky type of this grammar.
     */
    public int retrieveHighestGrammarType() {
        int type = 3;
        boolean leftLinear = true;
        boolean firstTest = true;
        
        for (Rule r : this.rules) {

            // Type0
            if (r.getImmutableLeftSide().getSymbols().size() > r.getImmutableRightSide().getSymbols().size()) {
                // XY... -> z...
                if (r.getImmutableLeftSide().getSymbols().size() > 1) {
                    return 0;
                } else { // X -> lambda
                    boolean sOnLeftSide = r.getImmutableLeftSide().getWordLength() == 1 && r.getImmutableLeftSide().getSymbols().get(0).equals(this.internalStartSymbol);
                    boolean moreThanOneLeft = false;
                    boolean sOnRightSide = false;
                    
                    for (Rule r1 : this.rules) {
                        if (r1.getImmutableLeftSide().getSymbols().size() > 1) {
                            moreThanOneLeft = true;
                        }
                        
                        if (r1.getImmutableRightSide().getSymbols().contains(this.internalStartSymbol)) {
                            sOnRightSide = true;
                        }
                        
                        if (moreThanOneLeft && !sOnLeftSide
                                || moreThanOneLeft && sOnLeftSide && sOnRightSide) {
                            return 0;
                        }
                    }
                }
            }

            // Type1/0.
            if (r.getImmutableLeftSide().getSymbols().size() > 1) {
                type = 1;
            }
            
            // Type2/3
            if (type == 3) {
                if (r.getImmutableRightSide().getSymbols().size() > 2) {
                    type = 2;
                } else {
                    // Size == 1 ==> Type3
                    if (r.getImmutableRightSide().getSymbols().size() == 2) {
                        if (firstTest) {
                            firstTest = false;
                            if (r.getImmutableRightSide().getSymbols().get(0).isTerminal()) {
                                leftLinear = false;
                            }
                        }
                        
                        if (leftLinear) {
                            if (!(!r.getImmutableRightSide().getSymbols().get(0).isTerminal()
                                    && r.getImmutableRightSide().getSymbols().get(1).isTerminal())) {
                                type = 2;
                            }
                        } else {
                            if (!(r.getImmutableRightSide().getSymbols().get(0).isTerminal()
                                    && !r.getImmutableRightSide().getSymbols().get(1).isTerminal())) {
                                type = 2;
                            }
                        }
                    }
                }
            }
        }
        
        return type;
    }

    @Override
    public String[] getExampleScripts() {
        String s0 = "grammar parse(a, a, <>, b, b, <>, a, a, <>, b, b)--48:\n" + 
                "S => a, S, b | <> | S, <>, S | a | b;\n" + 
                "--declarations--\n" + 
                "N=S,A;\n" + 
                "T=a,b,c;\n" + 
                "S=S;\n" + 
                "--declarations-end--";
        
        String sExpMon = "grammar:\n" + 
                "S => Y, T | b | b, b;\n" + 
                "Y => X, Y | b, b;\n" + 
                "X, b => b, b, X;\n" + 
                "X, b, T => b, b, T;\n" + 
                "X, b, b, T => b, b, b, b;\n" + 
                "--declarations--\n" + 
                "N=S,Y,X,T;\n" + 
                "T=b;\n" + 
                "S=S;\n" + 
                "maxdepth=900;\n" + 
                "cutNonTerminalBranches=true;\n" + 
                "cutTerminalDoubleBranches=true;\n" + 
                "maxLengthWords=16\n" + 
                "--declarations-end--";
        
        String sExp = "grammar:\n" + 
                "A => a;\n" + 
                "H => epsilon;\n" + 
                "S => V, A, H | a;\n" + 
                "A, B => B, A, A;\n" + 
                "A, H => A, B, H;\n" + 
                "V, B => epsilon | V;\n" + 
                "--declarations--\n" + 
                "N=S,V,A,H,B;\n" + 
                "T=b;\n" + 
                "S=S;\n" + 
                "maxdepth=9;\n" + 
                "cutNonTerminalBranches=true;\n" + 
                "cutTerminalDoubleBranches=true;\n" + 
                "maxLengthWords=16\n" + 
                "--declarations-end--";
        
        String s2 = "grammar:\n" + 
                "A => a;\n" + 
                "S => epsilon | a | S', A, A, X;\n" + 
                "S' => S', A | Z, B;\n" + 
                "Y => a;\n" + 
                "Z => epsilon;\n" + 
                "A, C => C, A;\n" + 
                "A, Y => Y, A;\n" + 
                "B, A => Y, A, B;\n" + 
                "B, X => epsilon | C, X;\n" + 
                "Z, Y => Y, Z;\n" + 
                "Z, C, A => Z, B, A;\n" + 
                "--declarations--\n" + 
                "N=S,S',A,B,C,X,Y,Z;\n" + 
                "T=b;\n" + 
                "S=S;\n" + 
                "multiLetterSymbolsHaveIndex=true;\n" + 
                "maxdepth=6;\n" + 
                "cutNonTerminalBranches=true;\n" + 
                "cutTerminalDoubleBranches=true;\n" + 
                "maxLengthWords=16\n" + 
                "--declarations-end--";
        
        String s3 = "grammar parse(a, a, c, c, b, b)--0:\n" + 
                "A => c | A, A;\n" + 
                "S => a, S, b | A | S, S;\n" + 
                "--declarations--\n" + 
                "N=S,A;\n" + 
                "T=a,b,c;\n" + 
                "S=S;\n" + 
                "--declarations-end--";
        
        String s4 = "grammar:\n" + 
                "A => c;\n" + 
                "S => a, S, b | A | epsilon;\n" + 
                "--declarations--\n" + 
                "N=S,A;\n" + 
                "T=a,b,c;\n" + 
                "S=S;\n" + 
                "maxdepth=8;\n" + 
                "cutNonTerminalBranches=true;\n" + 
                "cutTerminalDoubleBranches=true;\n" + 
                "maxLengthWords=100;\n" + 
                "--declarations-end--";
        
        return new String[] {
                s0, 
                RegularExpression.REG_EX_GRAMM_SCRIPT, 
                s4, 
                s3, 
                s2, 
                sExpMon, 
                sExp};
    }
    
    private Integer isScriptEqualExceptForTreeNum(String script) {
        try {
            int treeNum = Integer.parseInt(StaticMethods.removeWhitespaces(script).split(":")[0].split("--")[1]);
            String[] scriptTokens = StaticMethods.removeWhitespaces(script).split(":");
            String scriptWithoutNum = 
                    scriptTokens[0].split("--")[0]
                            + scriptTokens[1];
            String[] oldScriptTokens = StaticMethods.removeWhitespaces(this.lastCode).split(":");
            String oldScriptWithoutNum = 
                    oldScriptTokens[0].split("--")[0]
                            + oldScriptTokens[1];

            if (scriptWithoutNum.equals(oldScriptWithoutNum)) {
                return treeNum;
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isAcceptableScript(String code) {
        return (code + "").toLowerCase().startsWith("grammar");
    }

    @Override
    public void createInstanceFromScript(String originalCode, RepresentableAsPDF father) {
        this.applyDeclarationsAndPreprocessors(originalCode, father, 0);
        String code = this.getScriptWithoutPrepAndDecl();

        code = this.decollapseRules(code);
        
        this.dontRecalculateParseTrees = this.isScriptEqualExceptForTreeNum(code);
        if (this.dontRecalculateParseTrees == null) {
            this.parseTreesFromLastCalculation = null;
        }
        
        this.lastCode = code;
        String[] segments = StaticMethods.removeWhitespaces(code).split(":");
        String[] nonTerminals = this.N.split(",");
        @SuppressWarnings("unused") String[] terminals = this.T.split(",");
        this.internalStartSymbol = new Nonterminal(this.S);
        
        String[] rules = segments[1].split(";");
        
        this.nonTerminals = new ArrayList<Nonterminal>(nonTerminals.length);
        for (int i = 0; i < nonTerminals.length; i++) {
            this.nonTerminals.add(new Nonterminal(nonTerminals[i]));
        }

        ArrayList<String> nonTermStrings = new ArrayList<>(this.nonTerminals.size());
        for (Nonterminal nt : this.nonTerminals) {
            nonTermStrings.add(nt + "");
        }
        
        this.rules = new HashSet<>();
        for (int i = 0; i < rules.length; i++) {
            this.addRule(new Rule(rules[i], nonTermStrings));
        }

        if (this.retrieveHighestGrammarType() > 1 
                && segments[0].contains("(" ) && segments[0].contains(")")) {
            String between = segments[0].split("\\(")[1].split("\\)")[0];
            
            // The following is in case the + sign has been swallowed by the browser.
            between = between.replace(",,", ",+,");
            
            this.wordToParse = between.split(",");
            this.parseTreeNum = Integer.parseInt(segments[0].split("--")[1]);
        } else {
            this.wordToParse = null;
        }
        
        // Search for invalid rules.
        for (Rule r : this.getRules()) {
            boolean ok = false;
            for (Symbol s : r.getImmutableLeftSide().getSymbols()) {
                if (this.getNonTerminals().contains(s.toString())) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                throw new RuntimeException("Cannot insert rule with complete left side terminal: '" 
                   + r 
                   + "'. Check if all nonterminals in '" + r.getImmutableLeftSide() + "' are defined correctly.");
            }
        }
    }
    
    public GrammTree generateWords(boolean cutNonterminalBranches, boolean cutDblBranches, boolean quiet) {
        this.abortRequested = false;
        GeneralDialog.resetLongTimeOperationID("Grammar-long-time-ID");
        GrammTree tree = this.generateWords(
                0, 
                new GrammTree(new Word(new Symbol[] {this.internalStartSymbol}), null, this), 
                this.maxLengthWords, 
                quiet);

        if (abortRequested) {
            this.abortRequested = false;
            return LONG_OPERATION_ABORTED;
        }
        
        if (cutDblBranches) {
            setRemoved(true);
            while (isRemoved()) {
                setRemoved(false);
                tree.killDoubleTerminals(new HashSet<Word>());
            }
        }
        
        if (cutNonterminalBranches || cutDblBranches) {
            setRemoved(true);
            while (isRemoved()) {
                setRemoved(false);
                tree.killNonTerminalBranches();
            }
        }
        
        return tree;
    }
    
    private GraphViz generateGraphvizPreprocessor(String pdfPath) {
        GraphViz gv = new GraphViz(pdfPath, this);

        this.numOfParseTrees = -1;
        
        GlobalVariables.getParameters().logDebug("I'm starting the grammar tree creation.");
        if (this.wordToParse == null) {
            try {
                GrammTree tree = this.generateWords(this.cutNonTerminalBranches, this.cutTerminalDoubleBranches, false);
                GlobalVariables.getParameters().logDebug("Nodes in tree: " + tree.getSize());
                GlobalVariables.getParameters().logDebug("Terminal nodes in tree: " + tree.getTermNum());
                
                nodeNum = 0;
                gv.addln("digraph G {");
                traverseTree(gv, tree);
                gv.addln("};");
            } catch (Exception e) {}
        } else { // Parser type 2.
            gv.addln("digraph G {");
            if (this.isEpsilonFree()) {
                CtxtFreeGrammar gramm = new CtxtFreeGrammar(this);
                try {
                    if (this.parseTreesFromLastCalculation == null || this.dontRecalculateParseTrees == null) {
                        ParseTree[] trees = ChartParser.parse(gramm, this.wordToParse, null, this);
                        this.parseTreesFromLastCalculation = new ArrayList<>(trees.length);
                        for (ParseTree tree : trees) {
                            this.parseTreesFromLastCalculation.add(tree);
                        }
                        
                        Collections.sort(
                                this.parseTreesFromLastCalculation,
                                (c1, c2) -> {
                                    if (c1.getEdge().getLhs().equals(this.internalStartSymbol.getSymbolAsString())
                                            && !c2.getEdge().getLhs().equals(this.internalStartSymbol.getSymbolAsString())) {
                                        return -1;
                                    } else if (c2.getEdge().getLhs().equals(this.internalStartSymbol.getSymbolAsString())
                                            && !c1.getEdge().getLhs().equals(this.internalStartSymbol.getSymbolAsString())) {
                                        return 1;
                                    }
                                    return 0;
                                });
                    }
                    ChartParser.currentStartSymbol = this.internalStartSymbol.getSymbolAsString();
                    gv.addln(parseTreesFromLastCalculation.get(
                            this.parseTreeNum % parseTreesFromLastCalculation.size()).toString(
                                    this.multiLetterSymbolsHaveIndex));
                    this.numOfParseTrees = parseTreesFromLastCalculation.size();
                } catch (Exception e) {
                    if (e.getMessage().toLowerCase().contains("user")) {
                        gv.addln("b [label=\"" + ABORTED_STRING + "\"];");
                        gv.addln("b -> a;");
                    }
                    gv.addln("a [label=\"CANNOT PARSE\"];\n c [label=\""
                            + wordToParseFormatted() 
                                    + "\"];");
                    gv.addln("a -> c;");
                }
            } else {
                gv.addln("rankdir = LR");
                gv.addln("\"Note: S -> epsilon may remain after converting to epsilon-free\" [shape=box];");
                gv.addln("\"SORRY EPSILON NOT ALLOWED MAKE EPSILON-FREE\";");
            }
            gv.addln("};");
        }
        
        return gv;
    }
    
    @Override
    public PDFProcessor generatePDFscript(String pdfPath) {
        super.generatePDFscript(pdfPath);

        
        // In simplest case return just plain GraphViz.
        if (displayMode == 0) {
            // This preprocessor was originally returned directly.
            return this.generateGraphvizPreprocessor(pdfPath);
        }
        
        // This is the additional definition string to be displayed above the grammar graph.
        String stringLatex = this.toStringLatex(displayMode != 1);

        // The main processor is LaTeXPDF now, it includes the previously main grammar graph as image "actualGraph".
        LaTeXPDF mainProcessor = new LaTeXPDF(
                LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW
                    + (displayMode != 0 ? stringLatex : "")
                    + (displayMode != 1 ? "\n" + RepresentableDefault.INSCR_BEG_TAG_FOR_INTERNAL_USAGE
                            + "-1|dot:\n" + this.generateGraphvizPreprocessor(pdfPath).getSourceString() 
                            + RepresentableDefault.INSCR_END_TAG_FOR_INTERNAL_USAGE
                            : "")
                    + LaTeXCommands.POSTAMBLE_STANDARD, 
                pdfPath,
                this);
        
        return mainProcessor;
    }

    private String wordToParseFormatted() {
        String s = "";
        
        for (String c : this.wordToParse) {
            s += GraphViz.replaceSpecialChars(c);
        }
        
        return s;
    }

    private String traverseTree(GraphViz gv, GrammTree tree) {
        String prop = "shape=\"\"";
        
        Word word = tree.getWord();
        if (word.isTerminal()) {
            prop = "shape=\"rectangle\"";
        }
        
        String nodeID = "a" + nodeNum;
        nodeNum++;
        
        String nodeName = "<-" + word.toStringHTML(this.multiLetterSymbolsHaveIndex).replace(": ", " ") + "->";
        gv.addln(nodeID + "[label=" + nodeName + "," + prop + "];");
        for (GrammTree c : tree.getChildren()) {
            gv.addln(nodeID + "->" + traverseTree(gv, c) + ";");
        }
        return nodeID;
    }

    /**
     * Creates a random grammar script of type 2 and with several
     * parse trees.
     * 
     * @return  The randomized script.
     */
    public Grammar randomizeScript() {
        Random rand = new Random();
        ArrayList<String> nonterm = new ArrayList<>();
        ArrayList<String> term = new ArrayList<>();
        ArrayList<String> symbs;
        
        nonterm.add("S");
        nonterm.add("A");
        nonterm.add("B");
        nonterm.add("C");
        nonterm.add("D");
        
        term.add("a");
        term.add("b");
        
        symbs = new ArrayList<>(nonterm);
        symbs.addAll(term);
        
        String s = "";
        int numRulesMin = rand.nextInt(12);
        
        s += "S";
        for (int i = 0; i < numRulesMin + 10; i++) {
            s += "=>";
            
            int numTerm = 0;
            for (int j = 0; j < rand.nextInt(5); j++) {
                String symbol = symbs.get(rand.nextInt(symbs.size()));
                if (term.contains(symbol)) {
                    numTerm++;
                }
                s += symbol + ",";
            }
            if (numTerm == 0) {
                s += term.get(rand.nextInt(term.size())) + "";
            } else {
                s += symbs.get(rand.nextInt(symbs.size())) + "";
            }
            
            s += ";\n";
            
            if (i < numRulesMin + 9) {
                s += nonterm.get(rand.nextInt(nonterm.size()));
            }
        }

        s = "grammar parse(a, a, b, b, a, b)--0:\n"
                + s
                + RepresentableDefault.DECL_BEG_TAG + "\n"
                + "N=" + nonterm.toString().replace("[", "").replace("]", "") + ";\n"
                + "T=" + term.toString().replace("[", "").replace("]", "") + ";\n"
                + "S=S;\n"
                + "maxdepth=5;\n"
                + "maxLengthWords=10;\n"
                + "cutNonTerminalBranches=true;\n"
                + "cutTerminalDoubleBranches=true\n"
                + RepresentableDefault.DECL_END_TAG;
        
        Grammar g = new Grammar(this.getExercise());
        g.createInstanceFromScript(s, null);
        g.generatePDFscript("."); // PDF file path is not required in this context.
        
        if (g.parseTreesFromLastCalculation.size() <= 3) {
            return this.randomizeScript();
        }
        
        return g;
    }
    
    private Grammar replaceEpsilonWithPseudoEpsilon() {
        Grammar g = new Grammar(this);
        HashSet<Rule> rules = new HashSet<>(g.getRules());
        g.rules.clear();
                
        for (Rule r : rules) {
            if (r.getImmutableRightSide().getWordLength() == 0) {
                g.addRule(new Rule(new Word(r.getImmutableLeftSide()), new Word(new Symbol[] {new Terminal("<>")})));
            } else {
                g.addRule(r);
            }
        }
        
        return g;
    }

    /**
     * Creates a script of this grammar.
     * 
     * @return  The script representing this grammar.
     */
    @Override
    public String createScriptFromInstance() {
        List<String> terminals = this.getSortedStringList(this.getTerminals());
        List<String> nonterminals = this.getSortedStringList(this.getNonTerminals());
        List<Rule> rules = this.getSortedRuleList(this.rules);
        String termString = terminals.toString().replace("[", "").replace("]", "");
        String nonTString = nonterminals.toString().replace("[", "").replace("]", "");
        
        this.T = termString;
        this.N = nonTString;
        this.S = this.internalStartSymbol.toString();
        
        String parseWord = " /* parse(*word-to-parse*)--0 */";
        if (wordToParse != null) {
            parseWord = " parse";
            parseWord += Arrays.toString(wordToParse).replace("[", "(").replace("]", ")");
            parseWord += "--" + this.parseTreeNum;
        }
        
        String s = "grammar" + parseWord + ":\n" 
                    + this.generateCompleteDeclarationsBlock() + "\n";
        
        for (Rule r : rules) {
            if (r.getImmutableRightSide().getSymbols().size() == 0) {
                s += r.getImmutableLeftSide().getSymbols().toString().replace("[", "").replace("]", "") 
                        + " => epsilon" 
                        + ";\n";
            } else {
                s += r.getImmutableLeftSide().getSymbols().toString().replace("[", "").replace("]", "") 
                        + " => " 
                        + r.getImmutableRightSide().getSymbols().toString().replace("[", "").replace("]", "") + ";\n";
            }
        }
        
        return this.collapseRulesRtoL(s) + this.generateCompleteDeclarationsBlock();
    }

    public String formatWordToParse() {
        if (wordToParse == null || wordToParse.length == 0) {
            return "null";
        }
        
        return Arrays.toString(wordToParse).replace("[", "").replace("]", "").replace(",", "").replace(" ", "");
    }
    
    /**
     * Replaces all >2 symbol-rules with 2 rules having 2 symbols only.
     * Note that <code>this</code> is not changed and a new grammar is
     * returned.</BR>
     * </BR>
     * If the grammar has rules with multiple symbols on the left side</BR>
     *                  ABC... -> phi,</BR>
     * these rules are ignored.</BR>
     * </BR>
     * <code>this</code> is not changed in the process.
     * 
     * @return  A Chomsky NF Grammar equivalent to <code>this</code>
     *          (which has to have passed the other three private
     *          CNF creation methods before).
     */
    private Grammar getEqTwoSymbolGrammarCNF3_KNF1() {
        Grammar twoSymbols = new Grammar(this);
        
        if (twoSymbols.retrieveHighestGrammarType() < 1) {
            throw new RuntimeException("Grammars of type " + twoSymbols.retrieveHighestGrammarType() + " cannot be converted into CNF.");
        }
        
        for (Rule r : new HashSet<>(twoSymbols.getRules())) {
            this.continueNFLongtimeOperation();

            List<Symbol> rightSide = r.getImmutableRightSide().getSymbols();
            int leftSideLength = r.getImmutableLeftSide().getWordLength();
            if (leftSideLength == 1 && rightSide.size() > 2) {
                Nonterminal a = (Nonterminal) rightSide.get(0);
                Nonterminal b = (Nonterminal) rightSide.get(1);
                List<Symbol> rest = rightSide.subList(2, rightSide.size());
                
                // Completely new rule.
                List<Symbol> newLeftSide1 = new LinkedList<>();
                List<Symbol> newRightSide1 = new LinkedList<>();
                String newName = getCanonicalName(this.nontermCountCNF, "D");
                
                newLeftSide1.add(new Nonterminal(newName));
                newRightSide1.add(new Nonterminal(a));
                newRightSide1.add(new Nonterminal(b));
                
                List<Symbol> newLeftSide2 = new LinkedList<>(r.getImmutableLeftSide().getSymbols());
                List<Symbol> newRightSide2 = new LinkedList<>();
                newRightSide2.add(new Nonterminal(newName));
                newRightSide2.addAll(rest);
                
                twoSymbols.rules.remove(r);
                twoSymbols.addRule(new Rule(new Word(newLeftSide1), new Word(newRightSide1)));
                twoSymbols.addRule(new Rule(new Word(newLeftSide2), new Word(newRightSide2)));
                
                twoSymbols.nontermCountCNF++;
                
                return twoSymbols.getEqTwoSymbolGrammarCNF3_KNF1();
            }
        }
        
        return twoSymbols;
    }

    /**
     * Creates a terminal-free grammar out of a chain-free grammar.
     * <code>this</code> is not changed in the process.
     * 
     * @return  A terminal-free grammar.
     */
    private Grammar getEqTerminalFreeGrammarCNF2_KNF0() {
        Grammar noTerminals = new Grammar(this);
        HashSet<Rule> newRules = new HashSet<>();
        HashSet<Rule> rulesToRemove = new HashSet<>();

        for (Rule r : noTerminals.getRules()) {
            this.continueNFLongtimeOperation();

            LinkedList<Symbol> newLeftSideSymb = new LinkedList<>();
            LinkedList<Symbol> newRightSideSymb = new LinkedList<>();
            boolean remove = false;
            
            if (r.getImmutableRightSide().getWordLength() > 1 
                    || !r.getImmutableRightSide().isTerminal()
                    || r.getImmutableLeftSide().getWordLength() > 1) {
                // Right side.
                for (int i = 0; i < r.getImmutableRightSide().getWordLength(); i++) {
                    Symbol termSymb = r.getImmutableRightSide().getSymbols().get(i);
                    
                    if (noTerminals.getTerminals().contains(termSymb.toString())) { // It's a terminal.
                        String termNewName = getCanonicalName(termSymb.toString(), "C");
                        
                        newRightSideSymb.add(new Nonterminal(termNewName));
                        newRules.add(new Rule(
                                new Word(new Symbol[] {new Nonterminal(termNewName)}), 
                                new Word(new Symbol[] {termSymb})));
                        
                        this.nonTerminals.add(new Nonterminal(termNewName));

                        remove = true;
                    } else {
                        newRightSideSymb.add(new Nonterminal(termSymb.toString()));
                    }
                }

                // Left side.
                for (int i = 0; i < r.getImmutableLeftSide().getWordLength(); i++) {
                    Symbol termSymb = r.getImmutableLeftSide().getSymbols().get(i);
                    
                    if (noTerminals.getTerminals().contains(termSymb.toString())) { // It's a terminal.
                        String termNewName = getCanonicalName(termSymb.toString(), "C");
                        
                        newLeftSideSymb.add(new Nonterminal(termNewName));
                        newRules.add(new Rule(
                                new Word(new Symbol[] {new Nonterminal(termNewName)}), 
                                new Word(new Symbol[] {termSymb})));
                        
                        this.nonTerminals.add(new Nonterminal(termNewName));

                        remove = true;
                    } else {
                        newLeftSideSymb.add(new Nonterminal(termSymb.toString()));
                    }
                }
            }
        
            if (remove) {
                newRules.add(new Rule(new Word(newLeftSideSymb), new Word(newRightSideSymb)));
                rulesToRemove.add(r);
            }
        }

        noTerminals.getRules().removeAll(rulesToRemove);
        noTerminals.getRules().addAll(newRules);
        
        return noTerminals;
    }

    /**
     * Creates a chain-free grammar out of an epsilon-free grammar.
     * <code>this</code> is not changed in the process.
     * 
     * @return  A chain-free grammar.
     */
    private Grammar getEqChainFreeGrammarCNF1() {
        Grammar chainFree = new Grammar(this);
        HashSet<Rule> newRules = new HashSet<>();
        
        for (String n : chainFree.getNonTerminals()) {
            this.continueNFLongtimeOperation();

            HashSet<String> chain = chainFree.getChainSetFor(n);
            // Remove all chain rules for n.
            chainFree.removeChainRulesFor(n);
            
            // Add rules from the variables in CHAIN(T).
            for (String q : chain) {
                for (Rule r : chainFree.getRules()) {
                    if (r.getImmutableLeftSide().getWordLength() == 1
                            && r.getImmutableLeftSide().getSymbols().get(0).toString().equals(q)
                            && !chainFree.isChainRule(r)) {
                        Word leftSide = new Word(new Symbol[] {new Nonterminal(n)});
                        LinkedList<Symbol> rightSideSymb = new LinkedList<>(r.getImmutableRightSide().getSymbols());
                        Word rightSide = new Word(rightSideSymb);
                        newRules.add(new Rule(leftSide, rightSide));
                    }
                }
            }

            newRules.forEach(r -> chainFree.addRule(r));
        }
        
        return chainFree;
    }

    /**
     * Checks if the grammar is completely epsilon-free, i.e., even without
     * S => epsilon. Other than that, this method is the same as
     * <code>isEpsilonFree</code>.
     * 
     * @return  If the grammar's rules are all epsilon-free without any
     *          exception.
     */
    public boolean isCompletelyEpsilonFree() {
        for (Rule r : this.getRules()) {
            if (r.getImmutableRightSide().getWordLength() == 0) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Checks if the grammar is epsilon-free. This check works for all
     * grammar types (in contrast to the conversion-to-epsilon-free
     * algorithm which works for type-2 grammars only)s.
     * 
     * @return  If the grammar is epsilon-free except for S => epsilon, if
     *          S is not part of any right side.
     */
    public boolean isEpsilonFree() {
        boolean isEpsilonRuleS = false;
        
        for (Rule r : this.getRules()) {
            if (r.getImmutableRightSide().getWordLength() == 0) {
                // Is S epsilon rule?
                if (r.getImmutableLeftSide().getWordLength() == 1 
                        && r.getImmutableLeftSide().getSymbols().get(0).equals(this.internalStartSymbol)) {
                    isEpsilonRuleS = true;
                } else { // Is any other epsilon rule?
                    return false;
                }
            }
        }
        
        // Look for S on right side if S is epsilon rule.
        if (isEpsilonRuleS) {
            this.newStartSymbolToBeCreated = true;
            
            for (Rule r : this.getRules()) {
                if (r.getImmutableRightSide().getSymbols().contains(this.internalStartSymbol)) {
                    return false;
                }
            }
        }
        
        return true;
    }

    /**
     * Checks if a symbol is nonterminal (without 
     * checking the symbol themself as I don't fully trust this flag).
     * 
     * @param coll  The collection to check.
     * @return  If all the symbols in the collection are nonterminal.
     */
    private boolean isNonterminal(Symbol symb) {
        return this.getNonTerminals().contains(symb.toString());
    }
    
    /**
     * Checks if a collection of symbols is altogether nonterminal (without 
     * checking the symbols themselves as I don't fully trust this flag).
     * 
     * @param coll  The collection to check.
     * @return  If all the symbols in the collection are nonterminal.
     */
    private boolean isNonterminal(Collection<Symbol> coll) {
        for (Symbol symb : coll) {
            if (!this.isNonterminal(symb)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Removes a single specific epsilon rule. The method is called recursively
     * until all possible matches of the left side of the rule to remove in 
     * all rules are processed.
     * 
     * @param epsilonRule      The rule to remove.
     */
    private void removeEpsilonRule(Rule epsilonRule) {
        replaceSimpleRule(epsilonRule, new HashSet<>());
        Nonterminal leftSide = (Nonterminal) epsilonRule.getImmutableLeftSide().getSymbols().get(0);
        
        // Epsilon element of L(G).
        if (this.internalStartSymbol.equals(leftSide)) {
            this.newStartSymbolToBeCreated = true;
        }
    }

    private boolean newStartSymbolToBeCreated = false;
    
    /**
     * Removes a single specific simple rule, i.e., a rule with a single nonterminal
     * symbol on the left side and at most a single nonterminal symbol on the
     * right side. The method is called recursively
     * until all possible matches of the left side of the rule to remove in 
     * all rules are processed.
     * 
     * @param simpleRule      The rule to remove.
     * @param alreadyFinished  A set of rules that already have been processed.
     *                         Required for the recursive call only, so initially
     *                         invoke with <code>new HashSet<>()</code>.
     */
    private void replaceSimpleRule(
            Rule simpleRule,
            HashSet<Rule> alreadyFinished) {
        HashSet<Rule> newRules = new HashSet<>();
        Nonterminal leftSide = (Nonterminal) simpleRule.getImmutableLeftSide().getSymbols().get(0);
        Nonterminal rightSide = null;
        
        if (simpleRule.getImmutableRightSide().getWordLength() > 0) {
            rightSide = (Nonterminal) simpleRule.getImmutableRightSide().getSymbols().get(0);
        } 
        
        if (simpleRule.getImmutableRightSide().getWordLength() > 1
                || simpleRule.getImmutableLeftSide().getWordLength() > 1) {
            throw new RuntimeException("Rule is not \"simple\" and cannot be replaced.");
        }
        
        for (Rule r : this.getRules()) {
            if (!alreadyFinished.contains(r)) {
                List<Symbol> wordRightSide = new LinkedList<>(r.getImmutableRightSide().getSymbols());
                for (int i = 0; i < wordRightSide.size(); i++) {
                    if (wordRightSide.get(i).equals(leftSide)) {
                        LinkedList<Symbol> newRightSide = new LinkedList<>();
                        LinkedList<Symbol> newLeftSide = new LinkedList<>();
                        newLeftSide.addAll(r.getImmutableLeftSide().getSymbols());
                        for (int j = 0; j < wordRightSide.size(); j++) {
                            if (j == i) {
                                if (rightSide != null) {
                                    newRightSide.add(rightSide);
                                }
                            } else {
                                newRightSide.add(wordRightSide.get(j));
                            }
                        }
                        
                        if (!newLeftSide.equals(newRightSide)) {
                            newRules.add(new Rule(
                                    new Word(newLeftSide),
                                    new Word(newRightSide)));
                        }
                    }
                }
                
                alreadyFinished.add(r);
            }
        }
        
        for (Rule newRule : newRules) {
            this.addRule(newRule);
        }

        if (newRules.size() > 0) {
            replaceSimpleRule(simpleRule, alreadyFinished); // Recursively remove rule.
        } else {
            this.rules.remove(simpleRule);
        }
    }

    /**
     * Creates a new start symbol including a rule from it to the old start
     * symbol as well as a rule from it to epsilon. Note that <code>this</code>
     * is changed in the process. (If the flag <code>newStartSymbolToBeCreated</code>
     * is not set, no changes will occur.)
     */
    private Grammar possiblyCreateNewEpsilonStartsymbol() {
        if (this.newStartSymbolToBeCreated) {
            String newStartSymbolName = "S";
            while (this.getNonTerminals().contains(newStartSymbolName)) {
                newStartSymbolName += "'";
            }

            LinkedList<Symbol> newLeftSide = new LinkedList<>();
            LinkedList<Symbol> newRightSide = new LinkedList<>();
            newLeftSide.add(new Nonterminal(newStartSymbolName));
            newRightSide.add(new Nonterminal(this.internalStartSymbol));
            
            this.addRule(new Rule(
                    new Word(newLeftSide),
                    new Word(newRightSide)));
    
            this.addRule(new Rule(
                    new Word(newLeftSide),
                    new Word(new LinkedList<Symbol>())));

            this.removeSToEpsilonIfAny();
            
            this.internalStartSymbol = (Nonterminal) newLeftSide.get(0);
        }
        
        this.newStartSymbolToBeCreated = false;
        
        return this;
    }
    
    private void removeSToEpsilonIfAny() {
        for (Rule r : this.getRules()) {
            if (r.getImmutableRightSide().getSymbols().size() == 0
                    && r.getImmutableLeftSide().getSymbols().get(0).equals(this.internalStartSymbol)) {
                this.getRules().remove(r);
                return;
            }
        }
    }
    
    /**
     * Creates an epsilon-free grammar equivalent to the given grammar. The 
     * given grammar is not changed in the process. Works for type-2 grammars
     * only. (The empty word might slip out of the language - or else,
     * the method possiblyCreateNewEpsilonStartsymbol has to be invoked
     * afterward.)
     * 
     * @param grammar  The original grammar which is the template of the
     *                 new epsilon-free grammar.
     * @return  The new epsilon-free grammar.
     */
    private Grammar getEqEpsilonfreeGrammarCNF0() {
        if (this.retrieveHighestGrammarType() < 2) {
            throw new RuntimeException("Grammars of type " + this.retrieveHighestGrammarType() + " cannot be made epsilon-free.");
        }


        if (this.isEpsilonFree()) {
            return this.removeAdditionalStartRulesForEpsilon();
        }

        Grammar grammepsilonFree = new Grammar(this);

        List<Rule> allRules = new LinkedList<>(grammepsilonFree.getRules());
        
        for (Rule r : allRules) {
            this.continueNFLongtimeOperation();

            if (r.getImmutableRightSide().getWordLength() == 0) {
                grammepsilonFree.removeEpsilonRule(r);
            }
        }

        return grammepsilonFree.getEqEpsilonfreeGrammarCNF0().setFieldsToAvoidLongOperations();
    }

    /**
     * Avoid long-time operations.
     * 
     * @return  The grammar itself.
     */
    private Grammar setFieldsToAvoidLongOperations() {
        this.maxdepth = 3;
        this.maxLengthWords = 6;
        return this;
    }

    /**
     * Creates a chain set of this grammar (A => B => C) for a specific nonterminal.
     * 
     * @param n           The nonterminal symbol to process.
     * @return  The chain of this grammar for the given nonterminal.
     */
    public HashSet<String> getChainSetFor(String nonTerminal) {
        return getChainSetFor(nonTerminal, new HashSet<>(), new HashSet<>());
    }
    
    /**
     * Creates a chain of this grammar (A => B => C) for a specific nonterminal.
     * 
     * @param n           The nonterminal symbol to process.
     * @param soFarChain  So far found symbols (initially empty).
     * @param finished    So far processed symbols (initially empty).
     * @return  The chain of this grammar for the given nonterminal.
     */
    private HashSet<String> getChainSetFor(String n, HashSet<String> soFarChain, HashSet<String> finished) {
        finished.add(n);
        
        for (Rule r : this.getRules()) {
            if (r.getImmutableLeftSide().getSymbols().get(0).equals(new Nonterminal(n))) {
                if (r.getImmutableRightSide().getWordLength() == 1
                        && this.getNonTerminals().contains(r.getImmutableRightSide().getSymbols().get(0).toString())) {
                    soFarChain.add(r.getImmutableRightSide().getSymbols().get(0).toString());
                }
            }
        }
        
        HashSet<String> stillOpen = new HashSet<>();
        
        for (String nonTerm : soFarChain) {
            if (!finished.contains(nonTerm)) {
                stillOpen.add(nonTerm);
            }
        }

        for (String nonTerm : stillOpen) {
            soFarChain.addAll(getChainSetFor(nonTerm, soFarChain, finished));
        }
        
        return soFarChain;
    }
    
    /**
     * Creates a set of all chains for all nonterminal symbols of this grammar.
     * 
     * @return  The set of chains for all nonterminal symbols.
     */
    @SuppressWarnings("unused")
    private HashMap<Nonterminal, HashSet<String>> getAllChainSetsExceptForNewStartSymbol() {
        HashMap<Nonterminal, HashSet<String>> chainSets = new HashMap<>();
        
        for (String n : this.getNonTerminals()) {
            chainSets.put(new Nonterminal(n), getChainSetFor(n, new HashSet<>(), new HashSet<>()));
        }
        
        return chainSets;
    }
    
//    /**
//     * Checks if a rule is a chain rule, i.e., a rule of the form A => B.
//     * 
//     * @param r  The rule to check.
//     * 
//     * @return  It the rule is a chain rule.
//     */
//    private boolean isChainRule(Rule r) {
//        return r.getImmutableLeftSide().getWordLength() == 1
//                && r.getImmutableRightSide().getWordLength() == 1
//                && this.getNonTerminals().contains(r.getImmutableRightSide().getSymbols().get(0).toString());
//    }
    
    /**
     * Removes all chain rules with a specified nonterminal symbol on the
     * left-hand side. Works with type-2+ grammars only. (Note that the rules
     * are simply removed without providing new rules to compensate for their
     * functioning.
     * 
     * @param n  The nonterminal symbol on the left-hand side.
     */
    private void removeChainRulesFor(String n) {
        HashSet<Rule> toRemove = new HashSet<>();
        
        for (Rule r : this.getRules()) {
            if (isChainRule(r) && r.getImmutableLeftSide().getWordLength() == 1
                    && r.getImmutableLeftSide().getSymbols().get(0).toString().equals(n)) {
                toRemove.add(r);
            }
        }
        
        toRemove.forEach(r -> this.getRules().remove(r));
    }
    
    /**
     * Checks if a nonterminal symbol is reachable, i.e., if it occurs on the
     * right-hand side of any rule.
     * 
     * @param n  The nonterminal symbol to check.
     * @return  If it is reachable by the above definition.
     */
    private boolean isNonterminalReachable(String n) {
        if (this.internalStartSymbol.toString().equals(n)) {
            return true;
        }
        
        for (Rule r : this.getRules()) {
            if (r.getImmutableRightSide().getSymbols().contains(new Nonterminal(n))) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Removes all rules with a single unreachable nonterminal symbol on
     * the left side. Note that <code>this</code> WILL be changed!
     * 
     * @return  <code>this</code>, with the according rules removed.
     */
    public Grammar removeUnreachableRules() {
        if (isSenseLongTimeOperations()) {
            currentStepInGNFCreation += " (postprocessing: removing unreachable rules)";
        }
        
        if (this.retrieveHighestGrammarType() >= 2) {
            this.remUnreach1(new ArrayList<>(this.getRules()));
            this.remUnreach2(new ArrayList<>(this.getRules()));
        }
        
        return this;
    }

    private static String CONTINUE_LONG_OPERATION_NF_ID = "$$GNF$$";
    
    private void remUnreach1(ArrayList<Rule> rules) {
        for (int i = 0; i < rules.size(); i++) {
            this.continueNFLongtimeOperation();
            
            GlobalVariables.getParameters().logDebug("Removing unreachable rules, STEP 1: " + i + " / max: " + this.rules.size());

            Rule r = rules.get(i);
            
            if (r.getImmutableLeftSide().getWordLength() == 1
                    && !this.isNonterminalReachable(r.getImmutableLeftSide().getSymbols().get(0).toString())) {
                this.rules.remove(r);
                GlobalVariables.getParameters().logDebug("Rule removed: " + r);
                remUnreach1(new ArrayList<>(this.rules));
                return;
            }
        }
    }

    private void remUnreach2(ArrayList<Rule> rules) {
        for (int i = 0; i < rules.size(); i++) {
            this.continueNFLongtimeOperation();
            
            GlobalVariables.getParameters().logDebug("Removing unreachable rules, STEP 2: " + i + " / max: " + this.rules.size());
            
            Rule r = rules.get(i);
            
            if (r.getImmutableLeftSide().getWordLength() == 1) {
                for (Symbol s : r.getImmutableRightSide().getSymbols()) {
                    if (this.getNonTerminals().contains(s.toString())) {
                        if (this.getAllRulesWithLeftSideSingleElement(s.toString(), this.getRules()).isEmpty()) {
                            this.rules.remove(r);
                            GlobalVariables.getParameters().logDebug("Rule removed: " + r);
                        }
                    }
                }
            }
        }
    }
    
    public boolean isSenseLongTimeOperations() {
        return this.senseLongTimeOperations;
    }

    public void setSenseLongTimeOperations(boolean senseLongTimeOperations) {
        this.senseLongTimeOperations = senseLongTimeOperations;
    }

    private void continueNFLongtimeOperation() {
        if (isSenseLongTimeOperations() && !GeneralDialog.continueLongOperation(
                "Continue long-time GNF construction?", 
                "Creation of normal forms can be a long-time operation. Do you want to continue?\n \n"
                + "Currently, I am working on step " + currentStepInGNFCreation + " of 3.", 
                5000, 
                CONTINUE_LONG_OPERATION_NF_ID)) {
            throw ABORT_LONG_OPERATION_EXCEPTION;
        }
    }

    /**
     * Call this method before calling the four private methods for creating
     * the Chomsky Normal Form.
     */
    private void initializeCNF_KNFCreation() {
        this.newStartSymbolToBeCreated = false;
        this.nontermCountCNF = 0;
        this.nontermCountKNF = 0;
    }

    /**
     * Checks if the grammar is in Chomsky Normal Form.
     * 
     * @return  If the grammar is in Chomsky Normal Form.
     */
    public boolean isCNF() {
        if (!this.isEpsilonFree() || this.retrieveHighestGrammarType() < 2) {
            return false;
        }
        
        for (Rule r : this.getRules()) {
            if (!isCNFRuleIgnoringChains(r) || isChainRule(r)) {
                return false;
            }
        }
        
        return true;
    }

    private boolean isChainRule(Rule r) {
        return r.getImmutableLeftSide().getWordLength() == 1
                && r.getImmutableRightSide().getWordLength() == 1
                && isNonterminal(r.getImmutableLeftSide().getSymbols())
                && isNonterminal(r.getImmutableRightSide().getSymbols());
    }
    
    private boolean isCNFRuleIgnoringChains(Rule r) {
        return r.getImmutableLeftSide().getWordLength() == 1 
                && !(r.getImmutableRightSide().getWordLength() > 2
                    || (r.getImmutableRightSide().getWordLength() == 1 
                            && !r.getImmutableRightSide().isTerminal()
                            && !r.getImmutableRightSide().getSymbols().get(0).equals(this.internalStartSymbol))
                    || (r.getImmutableRightSide().getWordLength() == 2 && !isNonterminal(r.getImmutableRightSide().getSymbols())));
    }
    
    /**
     * @param r  A rule.
     * 
     * @return Iff the rule is of the type: "AB -> CD".
     */
    private boolean isRealKNFRule(Rule r) {
        return r.getImmutableLeftSide().getWordLength() == 2
                && r.getImmutableRightSide().getWordLength() == 2
                && isNonterminal(r.getImmutableLeftSide().getSymbols())
                && isNonterminal(r.getImmutableRightSide().getSymbols());
    }
    
    /**
     * Checks if the grammar is in Greibach Normal Form.
     * 
     * @return  If the grammar is in Greibach Normal Form.
     */
    public boolean isGNF() {
        if (!this.isEpsilonFree()) {
            return false;
        }
        
        for (Rule r : this.getRules()) {

            // Epsilon-free means that there is at most one epsilon rule, namely S' => epsilon.
            if (r.getImmutableRightSide().getWordLength() > 0 && !isSpecialStartRuleSToA(r)) {
                
                // Right side begins with non-terminal...
                if (this.getNonTerminals().contains(r.getImmutableRightSide().getSymbols().get(0).toString())) {
                    return false;
                }

                // So first symbol right is terminal.
                for (Symbol s : r.getImmutableRightSide().getSymbols().subList(1, r.getImmutableRightSide().getWordLength())) {
                    if (!this.getNonTerminals().contains(s.toString())) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * Specific test for special rule S => A (in addition to S => epsilon).
     * 
     * @param r  The rule to test.
     * @return  True iff it is a rule of type S => A where S is nowhere on any
     *          right side.
     */
    private boolean isSpecialStartRuleSToA(Rule r) {
        return r.getImmutableLeftSide().getSymbols().size() == 1
                && r.getImmutableRightSide().getSymbols().size() == 1
                && this.getStartSymbol().equals(r.getImmutableLeftSide().getSymbols().get(0))
                && this.getNonTerminals().contains(r.getImmutableRightSide().getSymbols().get(0).toString())
                && !isStartSymbolOnAnyRightSide();
    }
    
    private boolean isStartSymbolOnAnyRightSide() {
        for (Rule r : this.getRules()) {
            for (Symbol s : r.getImmutableRightSide().getSymbols()) {
                if (this.getStartSymbol().equals(s)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    public Grammar removeAdditionalStartRulesForEpsilon() {
        Grammar g = new Grammar(this);
        
        // Check if start symbol is an additional epsilon rule.
        int countSRuleEpsilon = 0;
        int countSRuleOther = 0;
        for (Rule r : g.getRules()) {
            if (r.getImmutableLeftSide().getWordLength() > 1 
                    || r.getImmutableRightSide().getSymbols().contains(g.internalStartSymbol)) {
                return g;
            }
            
            if (r.getImmutableLeftSide().getSymbols().contains(g.internalStartSymbol)) {
                if (r.getImmutableRightSide().getWordLength() == 0) {
                    countSRuleEpsilon++;
                } else if (r.getImmutableRightSide().getWordLength() == 1) {
                    countSRuleOther++;
                } else {
                    return g;
                }
            }
        }
        
        if (countSRuleEpsilon != 1 || countSRuleOther != 1) {
            return g;
        }
        
        // Remove the according rules.
        for (Rule r : g.getAllRulesWithLeftSideSingleElement(g.getStartSymbol().toString(), g.getRules())) {
            if (r.getImmutableRightSide().getWordLength() == 1) {
                try {
                    g.internalStartSymbol = new Nonterminal(r.getImmutableRightSide().getSymbols().get(0));
                } catch (Exception e) {
                }
            }
            
            g.rules.remove(r);
            g.newStartSymbolToBeCreated = true;
        }
        
        return g;
    }
    
    /**
     * Creates a grammar in Chomsky Normal Form by calling all three (private)
     * methods of the GNF algorithm. A new start symbol is NOT inserted in case 
     * of epsilon is element of the grammar's language (has to be done afterward;
     * epsilon is even removed if existing in an already CNF grammar). 
     * Note that <code>this</code> is NOT changed in the process. 
     * 
     * @return  The grammar in Chomsky Normal Form.
     */
    public Grammar createCNF() {
        if (this.isCNF()) {
            Grammar g = this.removeAdditionalStartRulesForEpsilon();
            return g;
        }

        this.initializeCNF_KNFCreation();
        
        return this.getEqEpsilonfreeGrammarCNF0()
                .getEqChainFreeGrammarCNF1()
                .getEqTerminalFreeGrammarCNF2_KNF0()
                .getEqTwoSymbolGrammarCNF3_KNF1()
                .removeUnreachableRules()
                .setFieldsToAvoidLongOperations();
    }

    /**
     * Creates a script of a grammar equivalent to <code>this</code> in
     * Chomsky Normal Form (CNF). This grammar is not changed in the process.
     * This works for type-2 grammars only, and an exception is thrown for
     * type-n < 2 when the epsilon-freeing procedure is invoked.
     * 
     * @return  The script in CNF.
     */
    @ConversionMethod(plainText = false)
    public String createScriptInCNF() {
        GeneralDialog.resetLongTimeOperationID(CONTINUE_LONG_OPERATION_NF_ID);
        
        if (this.isCNF()) {
            GeneralDialog.message("Grammar is already in Chomsky Normal Form.", "Nothing left to do...", false);
            return null;
        }
        
        this.initializeCNF_KNFCreation();
        Grammar grammEpsilonFree = new Grammar(this).removeUnreachableRules().getEqEpsilonfreeGrammarCNF0().removeUnreachableRules();
        Grammar grammNoChains = grammEpsilonFree.getEqChainFreeGrammarCNF1().removeUnreachableRules();
        Grammar grammNoTerminalRightSides = grammNoChains.getEqTerminalFreeGrammarCNF2_KNF0().removeUnreachableRules();
        Grammar grammCNF = grammNoTerminalRightSides.getEqTwoSymbolGrammarCNF3_KNF1().removeUnreachableRules();
        
        grammCNF = grammCNF.possiblyCreateNewEpsilonStartsymbol().removeUnreachableRules().setFieldsToAvoidLongOperations();
        grammCNF.multiLetterSymbolsHaveIndex = true;
        
        String message = "Generation of the Chomsky Normal Form (CNF):\n"
                       + "------------------------------------\n\n"
                       + "*** Original grammar ***\n\n"
                       + this.toString() + "\n\n\n"
                       + "*** Epsilon-free grammar ***\n\n"
                       + grammEpsilonFree.toString() + "\n\n\n"
                       + "*** Grammar without chains ***\n\n"
                       + grammNoChains.toString() + "\n\n\n"
                       + "*** Grammar with all terminals isolated ***\n\n"
                       + grammNoTerminalRightSides.toString() + "\n\n\n"
                       + "*** CNF grammar ***\n\n"
                       + grammCNF.toString()
                       ;
        
        GeneralDialog.message(message, "Information of CNF generation.", true);
        
        return grammCNF.createScriptFromInstance();
    }

    /**
     * Creates a script of a grammar equivalent to <code>this</code> in
     * Kuroda Normal Form (KNF). This grammar is not changed in the process.
     * This works for all types of grammars, for type >= 2, the CNF procedure
     * is invoked.
     * 
     * @return  The script in KNF.
     */
    @ConversionMethod(plainText = false)
    public String createScriptInKNF() {
        if (this.retrieveHighestGrammarType() >= 2) {
            return this.createScriptInCNF();
        }
        
        GeneralDialog.resetLongTimeOperationID(CONTINUE_LONG_OPERATION_NF_ID);
        
        if (this.isKNF_Type1()) {
            GeneralDialog.message("Grammar is already in Kuroda Normal Form.", "Nothing left to do...", false);
            return null;
        }
        
        this.initializeCNF_KNFCreation();

        Grammar grammTerminalFree = 
                new Grammar(this)
                .removeUnreachableRules()
                .getEqTerminalFreeGrammarCNF2_KNF0()
                .removeUnreachableRules();
        
        Grammar grammTwoSymbols = 
                grammTerminalFree
                .getEqTwoSymbolGrammarCNF3_KNF1()
                .removeUnreachableRules();

        Grammar grammKNF = 
                grammTwoSymbols
                .getEqTwoSymbolBothSidesGrammar_KNF2()
                .removeUnreachableRules();

        grammKNF = 
                grammKNF
                .possiblyCreateNewEpsilonStartsymbol()
                .removeUnreachableRules()
                .setFieldsToAvoidLongOperations();
        
//        String message = "Generation of the Chomsky Normal Form (CNF):\n"
//                       + "------------------------------------\n\n"
//                       + "*** Original grammar ***\n\n"
//                       + this.toString() + "\n\n\n"
//                       + "*** Terminal-free grammar ***\n\n"
//                       + grammTerminalFree.toString() + "\n\n\n"
//                       + "*** Grammar in CNF, for type 2 rules ***\n\n"
//                       + grammTwoSymbols.toString() + "\n\n\n"
//                       + "*** KNF grammar ***\n\n"
//                       + grammKNF.toString()
//                       ;
//        
//        GeneralDialog.message(message, "Information of CNF generation.", true);
        
        grammKNF.multiLetterSymbolsHaveIndex = true;
        
        return grammKNF.createScriptFromInstance();
    }

    private boolean isKNF_Type1() {
        int type = this.retrieveHighestGrammarType();
        
        if (type >= 2) {
            return this.isCNF();
        }
        
        if (type == 0) {
            return false;
        }
        
        for (Rule r : this.getRules()) {
            if (!isCNFRuleIgnoringChains(r) && !isRealKNFRule(r)) {
                return false;
            }
        }
        
        return true;
    }

    private Grammar getEqTwoSymbolBothSidesGrammar_KNF2() {
        Grammar grammar = new Grammar(this);
        
        HashSet<Rule> rulesToAdd = new HashSet<>();
        HashSet<Rule> rulesToRemove = new HashSet<>();
        
        for (Rule r : grammar.getRules()) {
            if (r.getImmutableLeftSide().getWordLength() >= 2 
                    && r.getImmutableRightSide().getWordLength() > 2) {
                HashSet<Rule> newRules = generateTwoSymbolBothSidesRules(r);

                rulesToRemove.add(r);
                rulesToAdd.addAll(newRules);
            }
        }

        grammar.removeAllRules(rulesToRemove);
        grammar.addAllRules(rulesToAdd);
        
        return grammar;
    }
    
    /**
     * Algorithm from https://de.wikipedia.org/wiki/Kuroda-Normalform.
     * 
     * @param r  A rule 
     *                  A0 A1 ... Al-1 -> B0 B1 ... Bk-1.
     *                  
     * @return   An equivalent set of rules in KNF.
     */
    private HashSet<Rule> generateTwoSymbolBothSidesRules(Rule r) {
        HashSet<Rule> newRules = new HashSet<>();
        ArrayList<Symbol> A = new ArrayList<>(r.getImmutableLeftSide().getSymbols());
        ArrayList<Symbol> B = new ArrayList<>(r.getImmutableRightSide().getSymbols());
        int l = A.size();  // Left side length.
        int k = B.size();  // Right side length.
        String C = "E";
        int count = this.nontermCountKNF;
        
        Word left;
        Word right;
        
        // A0 A1 -> B0 C0
        left = new Word(A.get(0), A.get(1));
        right = new Word(B.get(0), new Nonterminal(Grammar.getCanonicalName(count + 0, C)));
        newRules.add(new Rule(left, right));
        
        // i = 0, ..., l-3: Ci Ai+2 -> Bi+1 Ci+1
        for (int i = 0; i <= l - 3; i++) {
            left = new Word(new Nonterminal(Grammar.getCanonicalName(count + i, C)), A.get(i + 2));
            right = new Word(B.get(i + 1), new Nonterminal(Grammar.getCanonicalName(count + i + 1, C)));
            newRules.add(new Rule(left, right));
        }
        
        // i = l-2, ..., k-4: Ci -> Bi+1 Ci+1
        for (int i = l - 2; i <= k - 4; i++) {
            left = new Word(new Nonterminal(Grammar.getCanonicalName(count + i, C)));
            right = new Word(B.get(i + 1), new Nonterminal(Grammar.getCanonicalName(count + i + 1, C)));
            newRules.add(new Rule(left, right));
        }
        
        // Ck-3 -> Bk-2 Bk-1
        left = new Word(new Nonterminal(Grammar.getCanonicalName(count + k - 3, C)));
        right = new Word(B.get(k - 2), B.get(k - 1));
        newRules.add(new Rule(left, right));
        
        this.nontermCountKNF += k - 1;
        
        return newRules;
    }

    private void renameNonterminal(String n, String nNew) {
        if (this.internalStartSymbol.getSymbolAsString().equals(n)) {
            this.internalStartSymbol = new Nonterminal(nNew);
        }
        
        for (int i = 0; i < this.nonTerminals.size(); i++) {
            Symbol nOld = this.nonTerminals.get(i);
            
            if (nOld.getSymbolAsString().equals(n)) {
                this.nonTerminals.set(i, new Nonterminal(nNew));
            }
        }

        HashSet<Rule> newRules = new HashSet<>();
        
        // Immutable.
        for (Rule r : this.getRules()) {
            ArrayList<Symbol> leftSide = new ArrayList<>();
            ArrayList<Symbol> rightSide = new ArrayList<>();
            
            for (int i = 0; i < r.getImmutableLeftSide().getWordLength(); i++) {
                Symbol s = r.getImmutableLeftSide().getSymbols().get(i);
                
                if (this.isNonterminal(s) && s.toString().equals(n)) {
                    leftSide.add(new Nonterminal(nNew));
                } else {
                    leftSide.add(s);
                }
            }
            
            for (int i = 0; i < r.getImmutableRightSide().getWordLength(); i++) {
                Symbol s = r.getImmutableRightSide().getSymbols().get(i);
                
                if (this.isNonterminal(s) && s.toString().equals(n)) {
                    rightSide.add(new Nonterminal(nNew));
                } else {
                    rightSide.add(s);
                }
            }
            
            newRules.add(new Rule(new Word(leftSide), new Word(rightSide)));
        }
        
        this.getRules().clear();
        this.getRules().addAll(newRules);
    }

    /**
     * Renames all nonterminals to form an ordered sequence such as:
     * A(1), A(2), ...
     * 
     * @return  The renamed grammar or <code>null</code> if the rename was
     *          not successful.
     */
    public Grammar renameAllNonterminalsCanonically(String baseName) {
        Grammar renamed = new Grammar(this);
        
        int i = 1;
        
        for (String n : renamed.getNonTerminals()) {
            String nNew = getCanonicalName(i, baseName);
            if (renamed.getNonTerminals().contains(nNew)) {
                GlobalVariables.getParameters().logDebug(
                        "Cannot rename nonterminal to '" + nNew + "' as this name is already used.");
                return null;
            }
            
            renamed.renameNonterminal(n, nNew);
            i++;
        }
        
        return renamed;
    }
    
    private List<Rule> getAllRulesWithLeftSideSingleElement(String nTerm, Collection<Rule> fromRules) {
        return fromRules.stream().filter(
                r -> r.getImmutableLeftSide().getWordLength() == 1 
                        && r.getImmutableLeftSide().getSymbols().get(0).toString().equals(nTerm)).collect(Collectors.toList());
    }
    
    private List<Rule> getAllRulesWithFirstRightSideElement(String nTerm, Collection<Rule> fromRules) {
        return fromRules.stream().filter(
                r -> r.getImmutableRightSide().getWordLength() > 0
                        && r.getImmutableRightSide().getSymbols().get(0).toString().equals(nTerm)).collect(Collectors.toList());
    }
    
    private List<Rule> getAllRulesWithRightSideNotStartingWith(String nTerm, Collection<Rule> fromRules) {
        return fromRules.stream().filter(
                r -> r.getImmutableRightSide().getWordLength() > 0
                        && !r.getImmutableRightSide().getSymbols().get(0).toString().equals(nTerm)).collect(Collectors.toList());
    }
    
    private static String getCanonicalName(int index, String type) {
        return getCanonicalName("" + index, type);
    }
    
    private static String removeSpecialChars(String string) {
        return StaticMethods.removeWhitespaces(string).replace("<>", "eps").replace("<", "-").replace(">", "-");
    }
    
    private static String getCanonicalName(String index, String type) {
        return type + "(" + removeSpecialChars(index) + ")";
    }

    /**
     * Performs the first step in GNF creation (assuming the grammar is already
     * in CNF and the nonterminals are renamed to A(1), ..., A(m).
     * 
     * @return  A new grammar with the according step performed. Note that
     *          <code>this</code> is not changed in the process.
     */
    private Grammar step1GNF() {
        Grammar step1 = new Grammar(this);
        int m = step1.getNonTerminals().size();
        if (this.getMaxVarnum("A") != m) {
            throw new RuntimeException("The variables have to be named in subsequent order for GNF step 1.");
        }
        
        /*
         * Assuming all nonterminals in the set {A(1), ..., A(m)}.
         * Algorithm according to Hopcroft/Ullman, 1994 (German, 3rd edition), 
         * p. 103.
         */
        currentStepInGNFCreation = "1";
        for (int k = 1; k <= m; k++) {
            for (int j = 1; j <= k - 1; j++) {
                List<Rule> rulesAkAjAlpha = step1.getAllRulesWithLeftSideSingleElement(
                        getCanonicalName(k, "A"), 
                        step1.getAllRulesWithFirstRightSideElement(
                                getCanonicalName(j, "A"), 
                                step1.getRules()));
                
                for (Rule r : rulesAkAjAlpha) { // For all rules A(k) => A(j) alpha.
                    this.continueNFLongtimeOperation();
                    List<Rule> rulesAjBeta = step1.getAllRulesWithLeftSideSingleElement(
                            getCanonicalName(j, "A"), step1.getRules());
                    List<Symbol> alpha = new ArrayList<>(r.getImmutableRightSide().getSymbols().subList(1, r.getImmutableRightSide().getWordLength()));
                    
                    for (Rule s : rulesAjBeta) { // (1) For all rules A(j) => beta.
//                        GlobalVariables.getPrematureParameters().logDebug(r + " ---- " + s);
                        List<Symbol> beta = new ArrayList<>(s.getImmutableRightSide().getSymbols());
                        LinkedList<Symbol> newLeftSide = new LinkedList<>();
                        LinkedList<Symbol> newRightSide = new LinkedList<>();
                        newLeftSide.add(new Nonterminal(getCanonicalName(k, "A")));
                        newRightSide.addAll(beta);
                        newRightSide.addAll(alpha);
                        
                        GlobalVariables.getParameters().logDebug("GNF Step1 (1) add: " + new Rule(new Word(newLeftSide), new Word(newRightSide)));
                        
                        step1.addRule(new Rule(new Word(newLeftSide), new Word(newRightSide)));
                    }

                    GlobalVariables.getParameters().logDebug("GNF Step1 (1) remove: " + r);
                    step1.rules.remove(r);
                }
            }

            List<Rule> rulesAkAkAlpha = step1.getAllRulesWithLeftSideSingleElement(
                    getCanonicalName(k, "A"), 
                    step1.getAllRulesWithFirstRightSideElement(
                            getCanonicalName(k, "A"), 
                            step1.getRules()));
            
            for (Rule r : rulesAkAkAlpha) { // (2) For all rules A(k) => A(k) alpha.
                this.continueNFLongtimeOperation();
                Nonterminal bk = new Nonterminal(getCanonicalName(k, "B"));
                List<Symbol> alpha = new ArrayList<>(r.getImmutableRightSide().getSymbols().subList(1, r.getImmutableRightSide().getWordLength()));
                LinkedList<Symbol> newLeftSide1 = new LinkedList<>();
                newLeftSide1.add(bk);
                LinkedList<Symbol> newLeftSide2 = new LinkedList<>();
                LinkedList<Symbol> newRightSide2 = new LinkedList<>();
                newLeftSide2.add(bk);
                newRightSide2.addAll(alpha);
                newRightSide2.add(bk);
                
                GlobalVariables.getParameters().logDebug("GNF Step1 (2) add: " + new Rule(new Word(newLeftSide1), new Word(alpha)));
                GlobalVariables.getParameters().logDebug("GNF Step1 (2) add: " + new Rule(new Word(newLeftSide2), new Word(newRightSide2)));
                GlobalVariables.getParameters().logDebug("GNF Step1 (2) remove: " + r);
                
                step1.addRule(new Rule(new Word(newLeftSide1), new Word(alpha)));
                step1.addRule(new Rule(new Word(newLeftSide2), new Word(newRightSide2)));
                step1.rules.remove(r);
            }
            
            List<Rule> rulesAkBeta = step1.getAllRulesWithRightSideNotStartingWith(
                    getCanonicalName(k, "A"), 
                    step1.getAllRulesWithLeftSideSingleElement(
                            getCanonicalName(k, "A"), 
                            step1.getRules()));
            
            for (Rule r : rulesAkBeta) { // (3) For all rules A(k) => beta, with beta != A(k) ...
               this.continueNFLongtimeOperation();
               LinkedList<Symbol> newLeftSide = new LinkedList<>();
                LinkedList<Symbol> newRightSide = new LinkedList<>();
                
                newLeftSide.add(new Nonterminal(getCanonicalName(k, "A")));
                newRightSide.addAll(r.getImmutableRightSide().getSymbols());
                newRightSide.add(new Nonterminal(getCanonicalName(k, "B")));

                GlobalVariables.getParameters().logDebug("GNF Step1 (3) add: " + new Rule(new Word(newLeftSide), new Word(newRightSide)));
                step1.addRule(new Rule(new Word(newLeftSide), new Word(newRightSide)));
            }
        }

        return step1;
    }
    
    /**
     * Finds the maximum variable number used for some base name X. For example,
     * X=A returns the maximum i for some used nonterminal variable A(i).
     * 
     * @param varBaseName  The base name X to look for.
     * 
     * @return  The maximum number used for this base name.
     */
    private int getMaxVarnum(String varBaseName) {
        int max = Integer.MIN_VALUE;
        
        for (Rule r : this.getRules()) {
            if (r.getImmutableLeftSide().getSymbols().get(0).toString().startsWith(varBaseName)) {
                int value = Integer.parseInt(r.getImmutableLeftSide().getSymbols().get(0).toString().replace("(", "").replace(")", "").replace(varBaseName, ""));
                
                if (value > max) {
                    max = value;
                }
            }
        }
        
        return max;
    }
    
    /**
     * Replaces in a given rule a given (noterminal) position on the right side
     * with all its possible next derivation steps. E.g.:
     * replace...(A => abcDe, 3 ====>> A => abdXYZe, if D => XYZ is a rule.)
     * More precisely, the rule r is deleted, and a set of new rules is
     * introduced implementing the next derivation step.
     * 
     * @param r                  The rule to replace by derivations.
     * @param positionRightSide  The position of the nonterminal on the right
     *                           side (start conting at 0).
     */
    private void replaceSymbolByNextDerivation(Rule r, int positionRightSide) {
        Symbol symbOnRight = r.getImmutableRightSide().getSymbols().get(positionRightSide);
        
        // If the symbol already is terminal, do nothing.
        if (!this.getNonTerminals().contains(symbOnRight.toString())) {
            return;
        }
        
        List<Rule> rulesDeriv = this.getAllRulesWithLeftSideSingleElement(
                symbOnRight.toString(), 
                this.getRules());
        
        for (Rule ruleDeriv : rulesDeriv) {
            List<Symbol> rightSide1st = r.getImmutableRightSide().getSymbols().subList(0, positionRightSide);
            List<Symbol> rightSide2nd = ruleDeriv.getImmutableRightSide().getSymbols();
            List<Symbol> rightSide3rd = r.getImmutableRightSide().getSymbols().subList(positionRightSide + 1, r.getImmutableRightSide().getWordLength());
            List<Symbol> newLeftSide = new LinkedList<>();
            newLeftSide.addAll(r.getImmutableLeftSide().getSymbols());
            List<Symbol> newRightSide = new LinkedList<>();
            newRightSide.addAll(rightSide1st);
            newRightSide.addAll(rightSide2nd);
            newRightSide.addAll(rightSide3rd);
            
            Rule newRule = new Rule(new Word(newLeftSide), new Word(newRightSide));
            this.addRule(newRule);
            GlobalVariables.getParameters().logDebug("GNF Step2/3 add: " + newRule + " (by " + ruleDeriv + ")");
        }
        
        this.rules.remove(r);
        GlobalVariables.getParameters().logDebug("GNF Step2/3 remove: " + r);
    }
    
    /**
     * Basically goes backward from rule A(m-1) => X to rule A(1) => Y, 
     * and, if the first symbol of X and Y is NOT terminal, this symbol with
     * its direct derivations. According to the basic properties of the
     * algorithm performed in step 1, these direct derivations always have
     * to start with a terminal symbol, being overall in GNF.
     * 
     * @return  A new grammar with the according step performed. Note that
     *          <code>this</code> is not changed in the process.
     */
    private Grammar step2GNF() {
        currentStepInGNFCreation = "2";

        Grammar step2 = new Grammar(this);
        int m = step2.getMaxVarnum("A");

        for (int k = m - 1; k >= 1; k--) {
            this.continueNFLongtimeOperation();
            List<Rule> rulesAk = step2.getAllRulesWithLeftSideSingleElement(
                    getCanonicalName(k, "A"), step2.getRules());
            
            for (Rule r : rulesAk) {
                step2.replaceSymbolByNextDerivation(r, 0);
            }
        }
        
        return step2;
    }
    
    /**
     * Performs the third step in GNF creation which is in essence the same
     * as step 1, only for the B(.) variables. (Going backward through the
     * B(i) is not necessary, but doesn't hurt, either.)
     * 
     * @return  A new grammar with the according step performed. Note that
     *          <code>this</code> is not changed in the process.
     */
    private Grammar step3GNF() {
        currentStepInGNFCreation = "3";

        Grammar step3 = new Grammar(this);
        
        int m = step3.getMaxVarnum("B");

        for (int k = m; k >= 1; k--) {
            this.continueNFLongtimeOperation();
            List<Rule> rulesAk = step3.getAllRulesWithLeftSideSingleElement(
                    getCanonicalName(k, "B"), step3.getRules());
            
            for (Rule r : rulesAk) {
                step3.replaceSymbolByNextDerivation(r, 0);
            }
        }
        
        return step3;
    }
    
    /**
     * Creates a script of a grammar equivalent to <code>this</code> in
     * Greibach Normal Form (GNF). This grammar is not changed in the process.
     * This works for type-2 grammars only, and an exception is thrown for
     * type-n < 2 when the epsilon-freeing procedure (of CNF creation which
     * is part of the GNF creation) is invoked.
     * 
     * @return  The script in GNF.
     */
    @ConversionMethod(plainText = false)
    public String createScriptInGNF() {
        GeneralDialog.resetLongTimeOperationID(CONTINUE_LONG_OPERATION_NF_ID);

        try {
            currentStepInGNFCreation = "0 (creating CNF)";
            
            if (this.isGNF()) {
                GeneralDialog.message("Grammar is already in Greibach Normal Form.", "Nothing left to do...", false);
                return null;
            }
    
            Grammar grammCNF = this.createCNF().setFieldsToAvoidLongOperations();
            grammCNF.multiLetterSymbolsHaveIndex = true;
            
            Grammar grammRenamed = grammCNF.renameAllNonterminalsCanonically("TEMPSPECIAL"); // In case of A(.) being already in use.
            grammRenamed = grammRenamed.renameAllNonterminalsCanonically("A");

            /*
             * grammRenamed.removeUnreachableRules():
             * It's important NOT to do this here in order to prepare correctly
             * for step 1 of GNF creation.
             */
            
            Grammar grammGNF1 = grammRenamed.step1GNF().removeUnreachableRules();
            Grammar grammGNF2 = grammGNF1.step2GNF().removeUnreachableRules();
            Grammar grammGNF = grammGNF2.step3GNF().possiblyCreateNewEpsilonStartsymbol().removeUnreachableRules();
            
            String message = "Generation of the Greibach Normal Form (GNF):\n"
                           + "------------------------------------\n\n"
                           + "*** Original grammar ***\n\n"
                           + this.toString() + "\n\n\n"
                           + "*** Chomsky Normal Form ***\n\n"
                           + grammCNF.toString() + "\n\n\n"
                           + "*** Nonterminals canonically renamed ***\n\n"
                           + grammRenamed.toString() + "\n\n\n"
                           + "*** GNF step 1 ***\n\n"
                           + grammGNF1.toString() + "\n\n\n"
                           + "*** GNF step 2 ***\n\n"
                           + grammGNF2.toString() + "\n\n\n"
                           + "*** GNF (step 3) ***\n\n"
                           + grammGNF.toString()
                           ;
            
            GeneralDialog.message(message, "Information of GNF generation.", true);
            return grammGNF.createScriptFromInstance();
        } catch (Exception e) { // Catching user aborting long-time operation.
            if (e.equals(ABORT_LONG_OPERATION_EXCEPTION)) {
                return null;
            }
            
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Creates a grammar in Greibach Normal Form by calling all three (private)
     * methods of the GNF algorithm. A new start symbol is NOT inserted in case 
     * of epsilon is element of the grammar's language (has to be done afterward;
     * epsilon is even removed if existing in an already GNF grammar). 
     * Note that <code>this</code> is NOT changed in the process. 
     * 
     * @return  The grammar in Chomsky Normal Form.
     */
    public Grammar createGNF() {
        if (this.isGNF()) {
            Grammar g = this.removeAdditionalStartRulesForEpsilon();
            return g;
        }

        Grammar grammCNF = this.createCNF(); 
        Grammar grammRenamed = grammCNF.renameAllNonterminalsCanonically("TEMPSPECIAL"); // In case of A(.) being already in use.
        grammRenamed = grammRenamed.renameAllNonterminalsCanonically("A");
        Grammar grammGNF1 = grammRenamed.step1GNF();
        Grammar grammGNF2 = grammGNF1.step2GNF();
        Grammar grammGNF = grammGNF2.step3GNF().possiblyCreateNewEpsilonStartsymbol().removeUnreachableRules();
        return grammGNF.setFieldsToAvoidLongOperations();
    }

    public static String removeParseStringFromScript(String script) {
        return replaceParseStringInScriptWith(script, null);
    }
    
    public static String replaceParseStringInScriptWith(String script, String newParseString) {
        String parsePart = "";
        
        if (newParseString != null) {
            parsePart += " parse(";
            parsePart += newParseString;
            parsePart += ")--0";
        }
        
        try {
            String[] scriptDivided = script.split(":");
            String newScript = "grammar" + parsePart;
            
            for (int i = 1; i < scriptDivided.length; i++) {
                newScript += ":" + scriptDivided[i];
            }
            
            return newScript;
        } catch (Exception e) {
            return script;
        }
    }

    @ConversionMethod(plainText = false)
    public String removeParseStringFromScript() {
        return Grammar.removeParseStringFromScript(this.getRawScript());
    }
    
    @ConversionMethod(plainText = false)
    public String insertWordToParse(String wordToParse) {
        String parse = wordToParse;

        if (this.displayMode == 1) {
            this.displayMode = 2;
        }
        
        if (!wordToParse.contains(",") && wordToParse.length() > 0) {
            parse = wordToParse.charAt(0) == '('
                    ? "XkaX"
                    : wordToParse.charAt(0) == ')'
                        ? "XkzX"
                        : wordToParse.charAt(0) + "";
            for (int i = 1; i < wordToParse.length(); i++) {
                if (wordToParse.charAt(i) == '(') {
                    parse += ",XkaX";
                } else if (wordToParse.charAt(i) == ')') {
                    parse += ",XkzX";
                } else {
                    parse += "," + wordToParse.charAt(i);
                }
            }
        }
        
        Grammar epsilonFree = null;
        
        if (MainLink.isApplicationOriginDesktop() && !this.isEpsilonFree()) {
            GeneralDialog dia = new GeneralDialog(
                    this.getFather(),
                    "Only completely epsilon-free grammars can be parsed which the current is not.\n \n"
                        + "The grammar can be fixed automatically by either\n"
                        + " * Making it epsilon-free (possibly loosing the one word epsilon from the language) or\n"
                        + " * Replacing epsilon by the \"pseudo-epsilon\" terminal '<>' (guaranteeing exactly equivalent grammar)\n", 
                    "Fix epsilon in grammar?", 
                    new String[] {"Make epsilon-free", "Introduce pseudo-epsilon", "Do nothing"}, 
                    null);
            dia.setVisible(true);
            if (dia.getResult().equals("Make epsilon-free")) {
                epsilonFree = this.getEqEpsilonfreeGrammarCNF0();
            } else if (dia.getResult().equals("Introduce pseudo-epsilon")) {
                epsilonFree = this.replaceEpsilonWithPseudoEpsilon();
            }
        }
        
        if ("".equals(parse)) {
            parse = "<>";
        }

        String script = this.createScriptFromInstance();
        if (epsilonFree != null) {
            script = epsilonFree.createScriptFromInstance();
        }
        
        if (parse != null) {
            return Grammar.replaceParseStringInScriptWith(script, parse);
        } else {
            return null;
        }
    }

//    private JButton switchThroughParseTreesButton() {
//        String typeString = "Toggle " + (this.parseTreeNum % this.numOfParseTrees + 1) + " of " + this.numOfParseTrees + " syntax trees";
//        JButton labelShowGrammType;
//        labelShowGrammType = new JButton(typeString);
//        labelShowGrammType.addMouseListener(new MouseListener() {
//            @Override public void mouseReleased(MouseEvent e) {}
//            @Override public void mousePressed(MouseEvent e) {}
//            @Override public void mouseExited(MouseEvent e) {}
//            @Override public void mouseEntered(MouseEvent e) {}
//            
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                switchToNextSyntaxTree(e);
//            }
//            
//        });
//        
//        return labelShowGrammType;
//    }

    @ConversionMethod(plainText = false)
    public String switchToNextSyntaxTree() {
        boolean rightClick = false;
        
        int num = Integer.parseInt(StaticMethods.removeWhitespaces(this.lastCode).split(":")[0].split("--")[1]);
        int nextNum = num + 1 % this.numOfParseTrees;
        
        if (rightClick) {
            nextNum = num - 1;
            if (nextNum < 0) {
                nextNum = this.numOfParseTrees - 1;
            }
        }
        
        String[] tokens = this.lastCode.split(":");
        String s = tokens[0].split("--")[0]
                + "--" + ((nextNum) % this.numOfParseTrees) + ":"
                + tokens[1];
        s = this.collapseRulesRtoL(s) + this.generateCompleteDeclarationsBlock();
        
        return s;
    }

    @ConversionMethod(plainText = false)
    public String toggleDisplayMode() {
        Grammar g = new Grammar(this);
        g.displayMode = (g.displayMode + 1) % 3;
        return g.createScriptFromInstance();
    }

    @Override
    public JComponent getAdditionalInfo() {
        String add = "";
        
        if (this.isCNF()) {
            add += " | Chomsky NF";
        }
        if (this.isGNF()) {
            add += " | Greibach NF";
        }
        if (this.isKNF_Type1()) {
            add += " | Kuroda NF";
        }
        
        int type = this.retrieveHighestGrammarType();
        JComponent labelShowGrammType = new FancyJLabel("  TYPE-" + type + " Gr." + add);
        JPanel panel = new JPanel(new MigLayout("wrap 1"));
        
        panel.add(labelShowGrammType);
        panel.add(super.getAdditionalInfo());
        
        return panel;
    }

    @ConversionMethod(plainText = false)
    public String createRandomGrammarScript() {
        Grammar g = this.randomizeScript();
        return g.createScriptFromInstance();
    }

    @ConversionMethod(plainText = false)
    public String convertToEpsilonFreeScript() {
        GeneralDialog.resetLongTimeOperationID(CONTINUE_LONG_OPERATION_NF_ID);
        
        if (this.isEpsilonFree()) {
            GeneralDialog.message("Grammar is already (essentially) epsilon-free.", "Nothing left to do...", false);
            return null;
        }
        this.initializeCNF_KNFCreation();
        Grammar gEpsFree = this.getEqEpsilonfreeGrammarCNF0();
        gEpsFree = gEpsFree.possiblyCreateNewEpsilonStartsymbol();
        return gEpsFree.createScriptFromInstance();
    }

    public boolean existsMultiLetterSymbol() {
        for (String t : this.getTerminals()) {
            if (t.length() > 1) {
                return true;
            }
        }
        
        for (String n : this.getNonTerminals()) {
            if (n.length() > 1) {
                return true;
            }
        }
        
        return false;
    }
    
    @ConversionMethod(plainText = false)
    public String createEquivalentPDAScript() {
        PDA pda = new PDA(this.getExercise());
        String k = pda.getKellerZeichen();
        
        pda.setInitialState("s0");
        pda.setInput(this.formatWordToParse());
        pda.addFinalState("s2");
        pda.addTransition(new Transition(
                new StateTapesymbolKellersymbol("s0", "lambda", k), 
                new StateKellersymbols("s1", this.getStartSymbol() + k)));

        for (Rule r : this.getRules()) {
            pda.addTransition(new Transition(
                    new StateTapesymbolKellersymbol("s1", "lambda", r.getImmutableLeftSide().toString()), 
                    new StateKellersymbols("s1", StaticMethods.removeWhitespaces(r.getImmutableRightSide().toString().replace(":", "")))));
        }
        
        for (String t : this.getTerminals()) {
            pda.addTransition(new Transition(
                    new StateTapesymbolKellersymbol("s1", t, t), 
                    new StateKellersymbols("s1", "lambda")));
        }
        
        pda.addTransition(new Transition(
                new StateTapesymbolKellersymbol("s1", "lambda", k), 
                new StateKellersymbols("s2", k)));
        
        return pda.createScriptFromInstance();
    }
    
    @Override
    public HashMap<String, MethodWrapper> getDynamicMethods() {
        this.generateGraphvizPreprocessor(null);
        
        HashMap<String, MethodWrapper> methods = super.getDynamicMethods();
        int type = this.retrieveHighestGrammarType();

        String epsFreeName = "Epsilon-free";
        String chomskyName = "Chomsky NF";
        String kurodaName = "Kuroda NF";
        String greibachName = "Greibach NF";
        String toggleDisplayName = "Display mode";
        String randName = "Randomize";
        String parseName = "Parse single word";
        String completeTreeName = "Complete tree";
        String createPDA = "PDA";
        String epsFreeName_G = "Epsilon-frei";
        String chomskyName_G = "Chomsky NF";
        String kurodaName_G = "Kuroda NF";
        String greibachName_G = "Greibach NF";
        String toggleDisplayName_G = "Anzeigemodus";
        String randName_G = "Zufällige Grammatik";
        String parseName_G = "Parse einzelnes Wort";
        String completeTreeName_G = "Gesamtbaum";
        String createPDA_G = "Kellerautomat";

        String currentDisplayMode = " (" + this.displayMode + ")";
        
        try {
            MethodWrapper mw1 = new MethodWrapper(
                    this.getClass().getMethod("convertToEpsilonFreeScript"),
                    Grammar.class, // Target script class. Important to set correctly!
                    this,
                    "Remove the empty word 'epsilon' from all right sides (except for the start symbol if it is not occurring on any right side)",
                    "Entferne das leere Wort von allen rechten Seiten (außer beim Startsymbol, falls es auf keiner rechten Seite auftritt)",
                    epsFreeName,
                    epsFreeName_G);
            MethodWrapper mw2 = new MethodWrapper(
                    this.getClass().getMethod("createScriptInCNF"),
                    Grammar.class, // Target script class. Important to set correctly!
                    this,
                    "Convert this Grammar into Chomsky Normal Form",
                    "Konvertiere Grammatik in Chomsky-Normalform",
                    chomskyName,
                    chomskyName_G);
            MethodWrapper mw4 = new MethodWrapper(
                    this.getClass().getMethod("createScriptInKNF"),
                    Grammar.class, // Target script class. Important to set correctly!
                    this,
                    "Convert this Grammar into Kuroda Normal Form",
                    "Konvertiere Grammatik in Kuroda-Normalform",
                    kurodaName,
                    kurodaName_G);
            MethodWrapper mw3 = new MethodWrapper(
                    this.getClass().getMethod("createScriptInGNF"),
                    Grammar.class, // Target script class. Important to set correctly!
                    this,
                    "Convert this Grammar into Greibach Normal Form",
                    "Konvertiere Grammatik in Greibach-Normalform",
                    greibachName,
                    greibachName_G);
            MethodWrapper mw5 = new MethodWrapper(
                    this.getClass().getMethod("toggleDisplayMode"),
                    Grammar.class, // Target script class. Important to set correctly!
                    this,
                    "Toggle display modes: (0) Graph; (1) Definition; (2) Both - compact",
                    "Wechsle Anzeigemodi zwischen: (0) Graph; (1) Definition; (2) Beide - kompakt",
                    toggleDisplayName + currentDisplayMode,
                    toggleDisplayName_G + currentDisplayMode);
            MethodWrapper mw6 = new MethodWrapper(
                    this.getClass().getMethod("createRandomGrammarScript"),
                    Grammar.class, // Target script class. Important to set correctly!
                    this,
                    "Create a new random (type 2) Grammar",
                    "Erzeuge zufällige Grammatik (Typ 2)",
                    randName,
                    randName_G);
            MethodWrapper mw7 = new MethodWrapper(
                    this.getClass().getMethod("insertWordToParse", String.class),
                    Grammar.class, // Target script class. Important to set correctly!
                    this,
                    "Parse single word and toggle to syntax tree view",
                    "Füge zu parsendes Wort ins Skript ein und wechsle zur Parse-Baum-Sicht",
                    parseName,
                    parseName_G);
            mw7.setMethodDescription("Enter word to parse (separate terminals with commas)");
            mw7.setParameterExplanation(0, "Enter a word to create a parse tree from. If it includes multi-character symbols, use commas to separate them.");
            mw7.setParameterExplanation_G(0, "Gib ein Wort ein, für das der Parsebaum erzeugt werden soll. Falls es Symbole enthalten soll, die aus mehreren Buchstaben bestehen (etwa S'), müssen alle Symbole durch Kommas getrennt werden.");
            
            MethodWrapper mw8 = new MethodWrapper(
                    this.getClass().getMethod("removeParseStringFromScript"),
                    Grammar.class, // Target script class. Important to set correctly!
                    this,
                    "Remove word to parse and show complete grammar tree instead",
                    "Zeige den kompletten Grammatik-Baum (zu parsendes Wort wird aus Skript entfernt)",
                    completeTreeName,
                    completeTreeName_G);
            
            if (this.wordToParse == null) {
                mw8.setMethodButtonEnabled(false);
                mw8.setTooltip("Complete tree is already shown");
                mw8.setTooltip_G("Der gesamte Grammatikbaum wird bereits angezeigt");
            }

//TODO
            String typeString = "Toggle " + (this.parseTreeNum % this.numOfParseTrees + 1) + " of " + this.numOfParseTrees + " syntax trees";
            String typeString_G = "Durchlaufe " + (this.parseTreeNum % this.numOfParseTrees + 1) + " von " + this.numOfParseTrees + " Syntaxbäumen";
            MethodWrapper mw9 = new MethodWrapper(
                    this.getClass().getMethod("switchToNextSyntaxTree"),
                    Grammar.class, // Target script class. Important to set correctly!
                    this,
                    "Toggle all calculated parse trees.",
                    "Durchlaufe alle berechneten Parse-Bäume",
                    typeString,
                    typeString_G);
            
            if (this.wordToParse == null || this.wordToParse.length == 0 || this.numOfParseTrees < 2) {
                mw9.setMethodButtonEnabled(false);
            }
            
            MethodWrapper mw10 = new MethodWrapper(
                    this.getClass().getMethod("createEquivalentPDAScript"),
                    PDA.class, // Target script class. Important to set correctly!
                    this,
                    "Create PDA equivalent to this grammar.",
                    "Erzeuge einen zu dieser Grammatik äquivalenten Kellerautomaten",
                    createPDA,
                    createPDA_G);

            if (type < 2) {
                mw1.setMethodButtonVisible(false);
                mw2.setMethodButtonVisible(false);
                mw3.setMethodButtonVisible(false);
                mw7.setMethodButtonVisible(false);
                mw8.setMethodButtonVisible(false);
                mw9.setMethodButtonVisible(false);
                mw10.setMethodButtonVisible(false);
            }

            if (type >= 2) {
                if (this.existsMultiLetterSymbol()) {
                    mw10.setMethodButtonEnabled(false);
                    mw10.setTooltip(mw10.getTooltip() + " (not available since there are multi-letter symbols in the grammar which are not supported by PDAs)");
                    mw10.setTooltip_G(mw10.getTooltip_G() + " (nicht verfügbar, weil es Symbole mit mehr als einem Zeichen gibt, was bei Kellerautomaten nicht erlaubt ist)");
                }
                
                mw4.setMethodButtonEnabled(false);
                mw4.setTooltip(mw4.getTooltip() + " (For type " + type + " grammars use Chomsky NF, it's the same!)");
                mw4.setTooltip_G(mw4.getTooltip_G() + " (Für Typ-" + type + "-Grammatiken kann die Chomsky NF verwendet werden - sie ist äquivalent!)");
            }

            if (type == 0) {
                mw4.setMethodButtonEnabled(false);
                mw4.setTooltip(mw4.getTooltip() + " (Kuroda NF not available for type 0.)");
                mw4.setTooltip_G(mw4.getTooltip_G() + " (Für Typ-0-Grammatiken existiert keine Kuroda-NF.)");
            }
            
            if (this.isKNF_Type1()) {
                mw4.setMethodButtonEnabled(false);
                mw4.setTooltip(mw4.getTooltip() + " (Grammar is already in Kuroda NF.)");
                mw4.setTooltip_G(mw4.getTooltip_G() + " (Die Grammatik ist bereits in Kuroda-NF.)");
            }
            
            if (this.isEpsilonFree()) {
                mw1.setMethodButtonEnabled(false);
                mw1.setTooltip(mw1.getTooltip() + " (grammar is already esentially epsilon-free)");
                mw1.setTooltip_G(mw1.getTooltip_G() + " (Grammatik ist bereits epsilon-frei)");
                if (this.isCNF()) {
                    mw2.setMethodButtonEnabled(false);
                    mw2.setTooltip(mw2.getTooltip() + " (grammar is already in CNF)");
                    mw2.setTooltip_G(mw2.getTooltip_G() + " (Grammatik ist bereits in CNF)");
                }
                if (this.isGNF()) {
                    mw3.setMethodButtonEnabled(false);
                    mw3.setTooltip(mw3.getTooltip() + " (grammar is already in GNF)");
                    mw3.setTooltip_G(mw3.getTooltip_G() + " (Grammatik ist bereits in GNF)");
                }
            }
            
            methods.put(epsFreeName, mw1);
            methods.put(chomskyName, mw2);
            methods.put(kurodaName, mw4);
            methods.put(greibachName, mw3);
            methods.put(toggleDisplayName, mw5);
            methods.put(randName, mw6);
            methods.put(parseName, mw7);
            methods.put(completeTreeName, mw8);
            methods.put(typeString, mw9);
            methods.put(createPDA, mw10);
        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        
        return methods;
    }

    @Override
    public String getGermanName() {
        return "Grammatik";
    }
    
    @Override
    public String getModeDependentInfo(String mode, boolean english) {
        if (mode.equals(ConvenienceMethods.INFO_II_MODE_NAME)) {
            return ConvenienceMethods.createInfo2ModeString(
                    3, 
                    6, 
                    1, 
                    "http://www.dasinfobuch.de/links/Kontextfreie-Grammatiken.html",
                    "http://info2.aifb.kit.edu/qa/index.php?qa=351&qa_1=band-i-kapitel-6",
                    english
                    );
        }

        return "";
    }

    @Override
    public HashMap<String, String> getMetaProperties() {
        String className = Grammar.class.getSimpleName();
        HashMap<String, String> metaProperties = super.getMetaProperties();
        boolean gnf = this.isGNF();
        boolean cnf = this.isCNF();
        
        metaProperties.put(className + "_type", this.retrieveHighestGrammarType() + "");
        metaProperties.put(className + "_gnf", gnf + "");
        metaProperties.put(className + "_cnf", cnf + "");
        metaProperties.put(className + "_epsilonFree", (cnf || gnf || this.isEpsilonFree()) + "");
        metaProperties.put(className + "_startSymbol", this.getStartSymbol() + "");
        metaProperties.put(className + "_nonterminals", this.getNonTerminals() + "");
        metaProperties.put(className + "_numRules", this.getRules().size() + "");
        metaProperties.put(className + "_numParseTrees", this.numOfParseTrees + "");
        metaProperties.put(className + "_displayMode", this.displayMode + "");

        return metaProperties;
    }

    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        if (displayMode == 0) {
            return GraphViz.class;
        }
        
        return LaTeXPDF.class;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(boolean isRemoved) {
        this.isRemoved = isRemoved;
    }
    
    @Override
    public Collection<PDFProcessor> getPossiblePDFProcessorClasses() {
        return PDFProcessorFactory.allWebPDFProcessors();
    }
}
